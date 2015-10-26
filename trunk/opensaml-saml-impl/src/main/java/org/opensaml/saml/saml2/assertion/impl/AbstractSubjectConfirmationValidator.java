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

package org.opensaml.saml.saml2.assertion.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.assertion.SAML20AssertionValidator;
import org.opensaml.saml.saml2.assertion.SAML2AssertionValidationParameters;
import org.opensaml.saml.saml2.assertion.SubjectConfirmationValidator;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A base class for {@link SubjectConfirmationValidator} implementations. 
 * 
 * <p>
 * This class takes care of processing the <code>NotBefore</code>, <code>NotOnOrAfter</code>, 
 * <code>Recipient</code>, and <code>Address</code> checks.
 * </p>
 * 
 * <p>
 * Supports the following {@link ValidationContext} static parameters:
 * <ul>
 * <li>
 * {@link SAML2AssertionValidationParameters#SC_VALID_ADDRESSES}:
 * Required.
 * </li>
 * <li>
 * {@link SAML2AssertionValidationParameters#SC_VALID_RECIPIENTS}:
 * Required.
 * </li>
 * </ul>
 * </p>
 * 
 * <p>
 * Supports the following {@link ValidationContext} dynamic parameters:
 * <ul>
 * None.
 * </ul>
 * </p>
 */
@ThreadSafe
public abstract class AbstractSubjectConfirmationValidator implements SubjectConfirmationValidator {

    /** Class logger. */
    private Logger log = LoggerFactory.getLogger(AbstractSubjectConfirmationValidator.class);

    /** Constructor. */
    public AbstractSubjectConfirmationValidator() {
    }

    /** {@inheritDoc} */
    @Nonnull public ValidationResult validate(@Nonnull final SubjectConfirmation confirmation, 
            @Nonnull final Assertion assertion, @Nonnull final ValidationContext context)
            throws AssertionValidationException {

        if (confirmation.getSubjectConfirmationData() != null) {
            ValidationResult result = validateNotBefore(confirmation, assertion, context);
            if (result != ValidationResult.VALID) {
                return result;
            }

            result = validateNotOnOrAfter(confirmation, assertion, context);
            if (result != ValidationResult.VALID) {
                return result;
            }

            result = validateRecipient(confirmation, assertion, context);
            if (result != ValidationResult.VALID) {
                return result;
            }

            result = validateAddress(confirmation, assertion, context);
            if (result != ValidationResult.VALID) {
                return result;
            }
        }

        return doValidate(confirmation, assertion, context);
    }

    /**
     * Validates the <code>NotBefore</code> condition of the {@link SubjectConfirmationData}, if any is present.
     * 
     * @param confirmation confirmation method, with {@link SubjectConfirmationData}, being validated
     * @param assertion assertion bearing the confirmation method
     * @param context current validation context
     * 
     * @return the result of the validation evaluation
     * 
     * @throws AssertionValidationException thrown if there is a problem determining the validity of the NotBefore
     */
    @Nonnull protected ValidationResult validateNotBefore(@Nonnull final SubjectConfirmation confirmation, 
            @Nonnull final Assertion assertion, @Nonnull final ValidationContext context) 
                    throws AssertionValidationException {
        DateTime skewedNow = new DateTime(ISOChronology.getInstanceUTC()).plus(SAML20AssertionValidator
                .getClockSkew(context));
        DateTime notBefore = confirmation.getSubjectConfirmationData().getNotBefore();
        
        log.debug("Evaluating SubjectConfirmationData NotBefore '{}' against 'skewed now' time '{}'",
                notBefore, skewedNow);
        if (notBefore != null && notBefore.isAfter(skewedNow)) {
            context.setValidationFailureMessage(String.format(
                    "Subject confirmation, in assertion '%s', with NotBefore condition of '%s' is not yet valid",
                    assertion.getID(), notBefore));
            return ValidationResult.INVALID;
        }

        return ValidationResult.VALID;
    }

    /**
     * Validates the <code>NotOnOrAfter</code> condition of the {@link SubjectConfirmationData}, if any is present.
     * 
     * @param confirmation confirmation method, with {@link SubjectConfirmationData}, being validated
     * @param assertion assertion bearing the confirmation method
     * @param context current validation context
     * 
     * @return the result of the validation evaluation
     * 
     * @throws AssertionValidationException thrown if there is a problem determining the validity of the NotOnOrAFter
     */
    @Nonnull protected ValidationResult validateNotOnOrAfter(@Nonnull final SubjectConfirmation confirmation, 
            @Nonnull final Assertion assertion, @Nonnull final ValidationContext context) 
                    throws AssertionValidationException {
        DateTime skewedNow = new DateTime(ISOChronology.getInstanceUTC()).minus(SAML20AssertionValidator
                .getClockSkew(context));
        DateTime notOnOrAfter = confirmation.getSubjectConfirmationData().getNotOnOrAfter();
        
        log.debug("Evaluating SubjectConfirmationData NotOnOrAfter '{}' against 'skewed now' time '{}'",
                notOnOrAfter, skewedNow);
        if (notOnOrAfter != null && notOnOrAfter.isBefore(skewedNow)) {
            context.setValidationFailureMessage(String.format(
                    "Subject confirmation, in assertion '%s', with NotOnOrAfter condition of '%s' is no longer valid",
                    assertion.getID(), notOnOrAfter));
            return ValidationResult.INVALID;
        }

        return ValidationResult.VALID;
    }

    /**
     * Validates the <code>Recipient</code> condition of the {@link SubjectConfirmationData}, if any is present.
     * 
     * @param confirmation confirmation method being validated
     * @param assertion assertion bearing the confirmation method
     * @param context current validation context
     * 
     * @return the result of the validation evaluation
     * 
     * @throws AssertionValidationException thrown if there is a problem determining the validity of the recipient
     */
    @Nonnull protected ValidationResult validateRecipient(@Nonnull final SubjectConfirmation confirmation, 
            @Nonnull final Assertion assertion, @Nonnull final ValidationContext context) 
                    throws AssertionValidationException {
        String recipient = StringSupport.trimOrNull(confirmation.getSubjectConfirmationData().getRecipient());
        if (recipient == null) {
            return ValidationResult.VALID;
        }
        
        log.debug("Evaluating SubjectConfirmationData@Recipient of : {}", recipient);

        Set<String> validRecipients;
        try {
            validRecipients = (Set<String>) context.getStaticParameters().get(
                    SAML2AssertionValidationParameters.SC_VALID_RECIPIENTS);
        } catch (ClassCastException e) {
            log.warn("The value of the static validation parameter '{}' was not java.util.Set<String>",
                    SAML2AssertionValidationParameters.SC_VALID_RECIPIENTS);
            context.setValidationFailureMessage(
                    "Unable to determine list of valid subject confirmation recipient endpoints");
            return ValidationResult.INDETERMINATE;
        }
        if (validRecipients == null || validRecipients.isEmpty()) {
            log.warn("Set of valid recipient URI's was not available from the validation context, " 
                    + "unable to evaluate SubjectConfirmationData@Recipient");
            context.setValidationFailureMessage(
                    "Unable to determine list of valid subject confirmation recipient endpoints");
            return ValidationResult.INDETERMINATE;
        }
        
        

        if (validRecipients.contains(recipient)) {
            log.debug("Matched valid recipient: {}", recipient);
            return ValidationResult.VALID;
        }
        
        log.debug("Failed to match SubjectConfirmationData@Recipient to any supplied valid recipients: {}",
                validRecipients);

        context.setValidationFailureMessage(String.format(
                "Subject confirmation recipient for asertion '%s' did not match any valid recipients", assertion
                        .getID()));
        return ValidationResult.INVALID;
    }

    /**
     * Validates the <code>Address</code> condition of the {@link SubjectConfirmationData}, if any is present.
     * 
     * @param confirmation confirmation method being validated
     * @param assertion assertion bearing the confirmation method
     * @param context current validation context
     * 
     * @return the result of the validation evaluation
     * 
     * @throws AssertionValidationException thrown if there is a problem determining the validity of the address
     */
    @Nonnull protected ValidationResult validateAddress(@Nonnull final SubjectConfirmation confirmation, 
            @Nonnull final Assertion assertion, @Nonnull final ValidationContext context) 
                    throws AssertionValidationException {
        String address = StringSupport.trimOrNull(confirmation.getSubjectConfirmationData().getAddress());
        if (address == null) {
            return ValidationResult.VALID;
        }
        
        log.debug("Evaluating SubjectConfirmationData@Address of : {}", address);

        InetAddress[] confirmingAddresses;
        try {
            confirmingAddresses = InetAddress.getAllByName(address);
        } catch (UnknownHostException e) {
            log.warn("The subject confirmation address '{}' in assetion '{}' can not be resolved " 
                    + "to a valid set of IP address(s)", address, assertion.getID());
            context.setValidationFailureMessage(String.format(
                    "Subject confirmation address '%s' is not resolvable hostname or IP address", address));
            return ValidationResult.INDETERMINATE;
        }
        
        if (log.isDebugEnabled()) {
            log.debug("SubjectConfirmationData/@Address was resolved to addresses: {}",
                    Arrays.asList(confirmingAddresses));
        }

        Set<InetAddress> validAddresses;
        try {
            validAddresses = (Set<InetAddress>) context.getStaticParameters().get(
                    SAML2AssertionValidationParameters.SC_VALID_ADDRESSES);
        } catch (ClassCastException e) {
            log.warn("The value of the static validation parameter '{}' was not java.util.Set<InetAddress>",
                    SAML2AssertionValidationParameters.SC_VALID_ADDRESSES);
            context.setValidationFailureMessage("Unable to determine list of valid subject confirmation addresses");
            return ValidationResult.INDETERMINATE;
        }
        if (validAddresses == null || validAddresses.isEmpty()) {
            log.warn("Set of valid addresses was not available from the validation context, " 
                    + "unable to evaluate SubjectConfirmationData@Address");
            context.setValidationFailureMessage("Unable to determine list of valid subject confirmation addresses");
            return ValidationResult.INDETERMINATE;
        }

        for (InetAddress confirmingAddress : confirmingAddresses) {
            if (validAddresses.contains(confirmingAddress)) {
                log.debug("Matched SubjectConfirmationData address '{}' to valid address",
                        confirmingAddress.getHostAddress());
                return ValidationResult.VALID;
            }
        }
        
        log.debug("Failed to match SubjectConfirmationData@Address to any supplied valid addresses", validAddresses);

        context.setValidationFailureMessage(String.format(
                "Subject confirmation address for asertion '%s' did not match any valid addresses", assertion
                        .getID()));
        return ValidationResult.INVALID;
    }

    /**
     * Performs any further validation required for the specific confirmation method implementation.
     * 
     * @param confirmation confirmation method being validated
     * @param assertion assertion bearing the confirmation method
     * @param context current validation context
     * 
     * @return the result of the validation evaluation
     * 
     * @throws AssertionValidationException thrown if further validation finds the confirmation method to be invalid
     */
    @Nonnull protected abstract ValidationResult doValidate(@Nonnull final SubjectConfirmation confirmation, 
            @Nonnull final Assertion assertion, @Nonnull final ValidationContext context) 
                    throws AssertionValidationException;
    
}