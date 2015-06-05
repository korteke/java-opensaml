/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.saml.saml2.assertion;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.collection.LazyMap;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Condition;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.Statement;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignaturePrevalidator;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/** 
 * A component capable of performing core validation of SAML version 2.0 {@link Assertion} instances.
 * 
 * <p>
 * Supports the following {@link ValidationContext} static parameters:
 * <ul>
 * <li>
 * {@link SAML2AssertionValidationParameters#SIGNATURE_REQUIRED}:
 * Optional.
 * If not supplied, defaults to 'true'. If an Assertion is signed, the signature is always evaluated 
 * and the result factored into the overall validation result, regardless of the value of this setting.
 * </li>
 * <li>
 * {@link SAML2AssertionValidationParameters#SIGNATURE_VALIDATION_CRITERIA_SET}:
 * Optional.
 * If not supplied, a minimal criteria set will be constructed which contains an {@link EntityIDCriteria} 
 * containing the Assertion Issuer entityID, and a {@link UsageCriteria} of {@link UsageType#SIGNING}.
 * If it is supplied, but either of those criteria are absent from the criteria set, they will be added 
 * with the above values.
 * </li>
 * <li>
 * {@link SAML2AssertionValidationParameters#CLOCK_SKEW}:
 * Optional.
 * If not present the default clock skew of {@link SAML20AssertionValidator#DEFAULT_CLOCK_SKEW} milliseconds 
 * will be used.
 * </li>
 * </ul>
 * </p>
 * 
 * <p>
 * Supports the following {@link ValidationContext} dynamic parameters:
 * <ul>
 * <li>
 * {@link SAML2AssertionValidationParameters#CONFIRMED_SUBJECT_CONFIRMATION}:
 * Optional.
 * Will be present after validation iff subject confirmation was successfully performed.
 * </li>
 * </ul>
 * </p>
 * 
 * */
public class SAML20AssertionValidator {

    /** Default clock skew; {@value} milliseconds. */
    public static final long DEFAULT_CLOCK_SKEW = 5 * 60 * 1000;

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(SAML20AssertionValidator.class);

    /** Registered {@link Condition} validators. */
    private LazyMap<QName, ConditionValidator> conditionValidators;

    /** Registered {@link SubjectConfirmation} validators. */
    private LazyMap<String, SubjectConfirmationValidator> subjectConfirmationValidators;

    /** Registered {@link Statement} validators. */
    private LazyMap<QName, StatementValidator> statementValidators;
    
    /** Trust engine for signature evaluation. */
    private SignatureTrustEngine trustEngine;
    
    /** SAML signature profile validator.*/
    private SignaturePrevalidator signaturePrevalidator;

    /**
     * Constructor.
     * 
     * @param newConditionValidators validators used to validate the {@link Condition}s within the assertion
     * @param newConfirmationValidators validators used to validate {@link SubjectConfirmation} methods within the
     *            assertion
     * @param newStatementValidators validators used to validate {@link Statement}s within the assertion
     * @param newTrustEngine the trust used to validate the Assertion signature
     * @param newSignaturePrevalidator the signature pre-validator used to pre-validate the Assertion signature
     */
    public SAML20AssertionValidator(@Nullable final Collection<ConditionValidator> newConditionValidators,
            @Nullable final Collection<SubjectConfirmationValidator> newConfirmationValidators,
            @Nullable Collection<StatementValidator> newStatementValidators, 
            @Nullable final SignatureTrustEngine newTrustEngine,
            @Nullable final SignaturePrevalidator newSignaturePrevalidator) {
        
        conditionValidators = new LazyMap<QName, ConditionValidator>();
        if (newConditionValidators != null) {
            for (ConditionValidator validator : newConditionValidators) {
                if (validator != null) {
                    conditionValidators.put(validator.getServicedCondition(), validator);
                }
            }
        }

        subjectConfirmationValidators = new LazyMap<String, SubjectConfirmationValidator>();
        if (newConfirmationValidators != null) {
            for (SubjectConfirmationValidator validator : newConfirmationValidators) {
                if (validator != null) {
                    subjectConfirmationValidators.put(validator.getServicedMethod(), validator);
                }
            }
        }

        statementValidators = new LazyMap<QName, StatementValidator>();
        if (newStatementValidators != null) {
            for (StatementValidator validator : newStatementValidators) {
                if (validator != null) {
                    statementValidators.put(validator.getServicedStatement(), validator);
                }
            }
        }
        
        trustEngine = newTrustEngine;
        signaturePrevalidator = newSignaturePrevalidator;
    }

    /**
     * Gets the clock skew from the {@link ValidationContext#getStaticParameters()} parameters. If the parameter is not
     * set or is not a positive {@link Long} then the {@link #DEFAULT_CLOCK_SKEW} is used.
     * 
     * @param context current validation context
     * 
     * @return the clock skew
     */
    public static long getClockSkew(@Nonnull final ValidationContext context) {
        long clockSkew = DEFAULT_CLOCK_SKEW;

        if (context.getStaticParameters().containsKey(SAML2AssertionValidationParameters.CLOCK_SKEW)) {
            try {
                clockSkew = (Long) context.getStaticParameters().get(SAML2AssertionValidationParameters.CLOCK_SKEW);
                if (clockSkew < 1) {
                    clockSkew = DEFAULT_CLOCK_SKEW;
                }
            } catch (ClassCastException e) {
                clockSkew = DEFAULT_CLOCK_SKEW;
            }
        }

        return clockSkew;
    }

    /**
     * Validate the supplied SAML 2 {@link Assertion}, using the parameters from the supplied {@link ValidationContext}.
     * 
     * @param assertion the assertion being evaluated
     * @param context the current validation context
     * 
     * @return the validation result
     * 
     * @throws AssertionValidationException if there is a fatal error evaluating the validity of the assertion
     */
    @Nonnull public ValidationResult validate(@Nonnull final Assertion assertion, 
            @Nonnull final ValidationContext context) throws AssertionValidationException {
        
        log(assertion, context);
        
        ValidationResult result = validateVersion(assertion, context);
        if (result != ValidationResult.VALID) {
            return result;
        }

        result = validateSignature(assertion, context);
        if (result != ValidationResult.VALID) {
            return result;
        }

        result = validateConditions(assertion, context);
        if (result != ValidationResult.VALID) {
            return result;
        }

        result = validateSubjectConfirmation(assertion, context);
        if (result != ValidationResult.VALID) {
            return result;
        }

        return validateStatements(assertion, context);
    }

    /**
     * Log the Assertion which is being validated, along with the supplied validation context parameters.
     * 
     * @param assertion the SAML 2 Assertion being validated
     * @param context 
     */
    protected void log(@Nonnull final Assertion assertion, @Nonnull final ValidationContext context) {
        if (log.isDebugEnabled()) {
            try {
                final Element dom = XMLObjectSupport.marshall(assertion);
                log.debug("SAML 2 Assertion being validated:\n{}", SerializeSupport.prettyPrintXML(dom));
            } catch (final MarshallingException e) {
                log.error("Unable to marshall SAML 2 Assertion for logging purposes", e);
            }
            log.debug("SAML 2 Assertion ValidationContext - static parameters: {}", context.getStaticParameters());
            log.debug("SAML 2 Assertion ValidationContext - dynamic parameters: {}", context.getDynamicParameters());
        }
    }

    /**
     * Validates that the assertion is a {@link SAMLVersion#VERSION_20} assertion.
     * 
     * @param assertion the assertion to validate
     * @param context current validation context
     * 
     * @return result of the validation evaluation
     * 
     * @throws AssertionValidationException thrown if there is a problem validating the version
     */
    @Nonnull protected ValidationResult validateVersion(@Nonnull final Assertion assertion, 
            @Nonnull final ValidationContext context) throws AssertionValidationException {
        
        if (assertion.getVersion() != SAMLVersion.VERSION_20) {
            context.setValidationFailureMessage(String.format(
                    "Assertion '%s' is not a SAML 2.0 version Assertion", assertion.getID()));
            return ValidationResult.INVALID;
        }
        return ValidationResult.VALID;
    }

    /**
     * Validates the signature of the assertion, if it is signed.
     * 
     * @param token assertion whose signature will be validated
     * @param context current validation context
     * 
     * @return the result of the signature validation
     * 
     * @throws AssertionValidationException thrown if there is a problem determining the validity of the signature
     */
    @Nonnull protected ValidationResult validateSignature(@Nonnull final Assertion token, 
            @Nonnull final ValidationContext context) throws AssertionValidationException {
        
        Boolean signatureRequired = (Boolean) context.getStaticParameters().get(
                SAML2AssertionValidationParameters.SIGNATURE_REQUIRED);
        if (signatureRequired == null) {
            signatureRequired = Boolean.TRUE;
        }
        
        // Validate params and requirements
        if (!token.isSigned()) {
            if (signatureRequired) {
                context.setValidationFailureMessage("Assertion was required to be signed, but was not");
                return ValidationResult.INVALID;
            } else {
                log.debug("Assertion was not required to be signed, and was not signed.  " 
                        + "Skipping further signature evaluation");
                return ValidationResult.VALID;
            }
        }
        
        if (trustEngine == null) {
            log.warn("Signature validation was necessary, but no signature trust engine was available");
            context.setValidationFailureMessage("Assertion signature could not be evaluated due to internal error");
            return ValidationResult.INDETERMINATE;
        }
        
        return performSignatureValidation(token, context);
    }
    
    /**
     * Handle the actual signature validation.
     * 
     * @param token assertion whose signature will be validated
     * @param context current validation context
     * 
     * @return the validation result
     * 
     * @throws AssertionValidationException thrown if there is a problem determining the validity of the signature
     */
    @Nonnull protected ValidationResult performSignatureValidation(@Nonnull final Assertion token, 
            @Nonnull final ValidationContext context) throws AssertionValidationException {
        Signature signature = token.getSignature();
        
        String tokenIssuer = null;
        if (token.getIssuer() != null) {
            tokenIssuer = token.getIssuer().getValue();
        }
        
        log.debug("Attempting signature validation on Assertion '{}' from Issuer '{}'",
                token.getID(), tokenIssuer);
        
        try {
            signaturePrevalidator.validate(signature);
        } catch (SignatureException e) {
            String msg = String.format("Assertion Signature failed pre-validation: %s", e.getMessage());
            log.warn(msg);
            context.setValidationFailureMessage(msg);
            return ValidationResult.INVALID;
        }
        
        CriteriaSet criteriaSet = getSignatureValidationCriteriaSet(token, context);
        
        try {
            if (trustEngine.validate(signature, criteriaSet)) {
                log.debug("Validation of signature of Assertion '{}' from Issuer '{}' was successful",
                        token.getID(), tokenIssuer);
                return ValidationResult.VALID;
            } else {
                String msg = String.format(
                        "Signature of Assertion '%s' from Issuer '%s' was not valid", token.getID(), tokenIssuer);
                log.warn(msg);
                context.setValidationFailureMessage(msg);
                return ValidationResult.INVALID;
            }
        } catch (SecurityException e) {
            String msg = String.format(
                    "A problem was encountered evaluating the signature over Assertion with ID '%s': %s",
                    token.getID(), e.getMessage());
            log.warn(msg);
            context.setValidationFailureMessage(msg);
            return ValidationResult.INDETERMINATE;
        }
        
    }

    /**
     * Get the criteria set that will be used in evaluating the Assertion signature via the supplied trust engine.
     * 
     * @param token assertion whose signature will be validated
     * @param context current validation context
     * @return the criteria set to use
     */
    @Nonnull protected CriteriaSet getSignatureValidationCriteriaSet(@Nonnull final Assertion token, 
            @Nonnull final ValidationContext context) {
        
        CriteriaSet criteriaSet = (CriteriaSet) context.getStaticParameters().get(
                SAML2AssertionValidationParameters.SIGNATURE_VALIDATION_CRITERIA_SET);
        if (criteriaSet == null)  {
            criteriaSet = new CriteriaSet();
        }
        
        if (!criteriaSet.contains(EntityIdCriterion.class)) {
            String issuer =  null;
            if (token.getIssuer() != null) {
                issuer = StringSupport.trimOrNull(token.getIssuer().getValue());
            }
            if (issuer != null) {
                criteriaSet.add(new EntityIdCriterion(issuer));
            }
        }
        
        if (!criteriaSet.contains(UsageCriterion.class)) {
            criteriaSet.add(new UsageCriterion(UsageType.SIGNING));
        }
        
        return criteriaSet;
    }

    /**
     * Validates the conditions on the assertion. Condition validators are looked up by the element QName and, if
     * present, the schema type of the condition. If no validator can be found for the Condition the validation process
     * fails.
     * 
     * @param assertion the assertion whose conditions will be validated
     * @param context current validation context
     * 
     * @return the result of the validation evaluation
     * 
     * @throws AssertionValidationException thrown if there is a problem determining the validity of the conditions
     */
    @Nonnull protected ValidationResult validateConditions(@Nonnull final Assertion assertion, 
            @Nonnull final ValidationContext context) throws AssertionValidationException {
        
        Conditions conditions = assertion.getConditions();
        if (conditions == null) {
            log.debug("Assertion contained no Conditions element");
            return ValidationResult.VALID;
        }
        
        ValidationResult timeboundsResult = validateConditionsTimeBounds(assertion, context);
        if (timeboundsResult != ValidationResult.VALID) {
            return timeboundsResult;
        }

        ConditionValidator validator;
        for (Condition condition : conditions.getConditions()) {
            validator = conditionValidators.get(condition.getElementQName());
            if (validator == null && condition.getSchemaType() != null) {
                validator = conditionValidators.get(condition.getSchemaType());
            }

            if (validator == null) {
                String msg = String.format(
                        "Unknown Condition '%s' of type '%s' in assertion '%s'", 
                                condition.getElementQName(), condition.getSchemaType(), assertion.getID());
                log.debug(msg);
                context.setValidationFailureMessage(msg);
                return ValidationResult.INDETERMINATE;
            }
            if (validator.validate(condition, assertion, context) != ValidationResult.VALID) {
                String msg = String.format(
                        "Condition '%s' of type '%s' in assertion '%s' was not valid.",
                                condition.getElementQName(), condition.getSchemaType(), assertion.getID());
                if (context.getValidationFailureMessage() != null) {
                    msg = msg + ": " + context.getValidationFailureMessage();
                }
                log.debug(msg);
                context.setValidationFailureMessage(msg);
                return ValidationResult.INVALID;
            }
        }

        return ValidationResult.VALID;
    }
    
    /**
     * Validates the NotBefore and NotOnOrAfter Conditions constraints on the assertion.
     * 
     * @param assertion the assertion whose conditions will be validated
     * @param context current validation context
     * 
     * @return the result of the validation evaluation
     * 
     * @throws AssertionValidationException thrown if there is a problem determining the validity of the conditions
     */
    @Nonnull protected ValidationResult validateConditionsTimeBounds(@Nonnull final Assertion assertion,
            @Nonnull final ValidationContext context) throws AssertionValidationException {
        
        Conditions conditions = assertion.getConditions();
        if (conditions == null) {
            return ValidationResult.VALID;
        }
        
        DateTime now = new DateTime(ISOChronology.getInstanceUTC());
        long clockSkew = getClockSkew(context);

        DateTime notBefore = conditions.getNotBefore();
        log.debug("Evaluating Conditions NotBefore '{}' against 'skewed now' time '{}'",
                notBefore, now.plus(clockSkew));
        if (notBefore != null && notBefore.isAfter(now.plus(clockSkew))) {
            context.setValidationFailureMessage(String.format(
                    "Assertion '%s' with NotBefore condition of '%s' is not yet valid", assertion.getID(), notBefore));
            return ValidationResult.INVALID;
        }

        DateTime notOnOrAfter = conditions.getNotOnOrAfter();
        log.debug("Evaluating Conditions NotOnOrAfter '{}' against 'skewed now' time '{}'",
                notOnOrAfter, now.minus(clockSkew));
        if (notOnOrAfter != null && notOnOrAfter.isBefore(now.minus(clockSkew))) {
            context.setValidationFailureMessage(String.format(
                    "Assertion '%s' with NotOnOrAfter condition of '%s' is no longer valid", assertion.getID(),
                    notOnOrAfter));
            return ValidationResult.INVALID;
        }
        
        return ValidationResult.VALID;
    }

    /**
     * Validates the subject confirmations of the assertion. Validators are looked up by the subject confirmation
     * method. If any one subject confirmation is met the subject is considered confirmed per the SAML specification.
     * 
     * @param assertion assertion whose subject is being confirmed
     * @param context current validation context
     * 
     * @return the result of the validation
     * 
     * @throws AssertionValidationException thrown if there is a problem determining the validity the subject
     */
    @Nonnull protected ValidationResult validateSubjectConfirmation(@Nonnull final Assertion assertion, 
            @Nonnull final ValidationContext context) throws AssertionValidationException {
        
        Subject assertionSubject = assertion.getSubject();
        if (assertionSubject == null) {
            log.debug("Assertion contains no Subject, skipping subject confirmation");
            return ValidationResult.VALID;
        }

        List<SubjectConfirmation> confirmations = assertionSubject.getSubjectConfirmations();
        if (confirmations == null || confirmations.isEmpty()) {
            log.debug("Assertion contains no SubjectConfirmations, skipping subject confirmation");
            return ValidationResult.VALID;
        }
        
        log.debug("Assertion contains at least 1 SubjectConfirmation, proceeding with subject confirmation");

        for (SubjectConfirmation confirmation : confirmations) {
            SubjectConfirmationValidator validator = subjectConfirmationValidators.get(confirmation.getMethod());
            if (validator != null) {
                try {
                    if (validator.validate(confirmation, assertion, context) == ValidationResult.VALID) {
                        context.getDynamicParameters().put(
                                SAML2AssertionValidationParameters.CONFIRMED_SUBJECT_CONFIRMATION, confirmation);
                        return ValidationResult.VALID;
                    }
                } catch (AssertionValidationException e) {
                    log.warn("Error while executing subject confirmation validation " + validator.getClass().getName(),
                            e);
                }
            }
        }

        String msg = String.format(
                "No subject confirmation methods were met for assertion with ID '%s'", assertion.getID());
        log.debug(msg);
        context.setValidationFailureMessage(msg);
        return ValidationResult.INVALID;
    }

    /**
     * Validates the statements within the assertion. Validators are looked up by the Statement's element QName or, if
     * present, its schema type. Any statement for which a validator can not be found is simply ignored.
     * 
     * @param assertion assertion whose statements are being validated
     * @param context current validation context
     * 
     * @return result of the validation
     * 
     * @throws AssertionValidationException thrown if there is a problem determining the validity the statements
     */
    @Nonnull protected ValidationResult validateStatements(@Nonnull final Assertion assertion, 
            @Nonnull final ValidationContext context) throws AssertionValidationException {
        
        List<Statement> statements = assertion.getStatements();
        if (statements == null || statements.isEmpty()) {
            return ValidationResult.VALID;
        }

        ValidationResult result;
        StatementValidator validator;
        for (Statement statement : statements) {
            validator = statementValidators.get(statement.getElementQName());
            if (validator == null && statement.getSchemaType() != null) {
                validator = statementValidators.get(statement.getSchemaType());
            }

            if (validator != null) {
                result = validator.validate(statement, assertion, context);
                if (result != ValidationResult.VALID) {
                    return result;
                }
            }
        }

        return ValidationResult.VALID;
    }
}