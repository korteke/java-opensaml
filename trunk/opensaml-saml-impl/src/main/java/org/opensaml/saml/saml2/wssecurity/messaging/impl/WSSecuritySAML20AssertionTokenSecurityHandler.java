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

package org.opensaml.saml.saml2.wssecurity.messaging.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.collection.LazyList;
import net.shibboleth.utilities.java.support.collection.LazySet;
import net.shibboleth.utilities.java.support.collection.Pair;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.assertion.SAML20AssertionValidator;
import org.opensaml.saml.saml2.assertion.SAML2AssertionValidationParameters;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.wssecurity.SAML20AssertionToken;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.security.messaging.ServletRequestX509CredentialAdapter;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.soap.messaging.SOAPMessagingSupport;
import org.opensaml.soap.wssecurity.Security;
import org.opensaml.soap.wssecurity.messaging.Token.ValidationStatus;
import org.opensaml.soap.wssecurity.messaging.WSSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Strings;

/**
 * A security handler which resolves SAML 2.0 Assertion tokens from a SOAP envelope's
 * wsse:Security header, validates them, and makes them available via via the
 * {@link WSSecurityContext}.
 */
public class WSSecuritySAML20AssertionTokenSecurityHandler extends AbstractMessageHandler {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(WSSecuritySAML20AssertionTokenSecurityHandler.class);
    
    /** The HttpServletRequest being processed. */
    @NonnullAfterInit private HttpServletRequest httpServletRequest;
    
    /** Flag which indicates whether a failure of Assertion validation should be considered fatal. */
    private boolean invalidFatal;
    
    /** The SAML 2.0 Assertion validator.*/
    private SAML20AssertionValidator assertionValidator;
    
    /** A function for resolving the signature validation CriteriaSet for a particular function. */
    private Function<Pair<MessageContext, Assertion>, CriteriaSet> signatureCriteriaSetFunction;
    
    
    /** Constructor. */
    public WSSecuritySAML20AssertionTokenSecurityHandler() {
        super();
        setInvalidFatal(true);
    }
    
    /**
     * Get the function for resolving the signature validation CriteriaSet for a particular function.
     * 
     * @return a criteria set instance, or null
     */
    @Nullable public Function<Pair<MessageContext, Assertion>, CriteriaSet> getSignatureCriteriaSetFunction() {
        return signatureCriteriaSetFunction;
    }

    /**
     * Set the function for resolving the signature validation CriteriaSet for a particular function.
     * 
     * @param function the resolving function, may be null
     */
    public void setSignatureCriteriaSetFunction(
            @Nullable final Function<Pair<MessageContext, Assertion>, CriteriaSet> function) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        signatureCriteriaSetFunction = function;
    }

    /**
     * Get the HTTP servlet request being processed.
     * 
     * @return Returns the request.
     */
    @NonnullAfterInit public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    /**
     * Set the HTTP servlet request being processed.
     * 
     * @param request The to set.
     */
    public void setHttpServletRequest(@Nonnull final HttpServletRequest request) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        
        httpServletRequest = Constraint.isNotNull(request, "HttpServletRequest cannot be null");
    }
    
    /**
     * Get flag which indicates whether a failure of Assertion validation should be considered a fatal processing error.
     * @return Returns the invalidFatal.
     */
    public boolean isInvalidFatal() {
        return invalidFatal;
    }

    /**
     * Set flag which indicates whether a failure of Assertion validation should be considered a fatal processing error.
     * @param flag The invalidFatal to set.
     */
    public void setInvalidFatal(boolean flag) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        invalidFatal = flag;
    }
    
    /**
     * Get the locally-configured Assertion validator.
     * 
     * @return the local Assertion validator, or null
     */
    @Nullable public SAML20AssertionValidator getAssertionValidator() {
        return assertionValidator;
    }

    /**
     * Set the locally-configured Assertion validator.
     * 
     * @param validator the local Assertion validator, may be null
     */
    public void setAssertionValidator(@Nullable final SAML20AssertionValidator validator) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        assertionValidator = validator;
    }

    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (getHttpServletRequest() == null) {
            throw new ComponentInitializationException("HttpServletRequest cannot be null");
        }
        
        if (getAssertionValidator() == null) {
            log.info("Assertion validator is null, must be resovleable from the MessageContext");
        }
    }

    /** {@inheritDoc} */
    protected void doDestroy() {
        httpServletRequest = null;
        
        super.doDestroy();
    }
    
    /** {@inheritDoc} */
    protected boolean doPreInvoke(MessageContext messageContext) throws MessageHandlerException {
        // TODO Auto-generated method stub
        return super.doPreInvoke(messageContext);
    }

    /** {@inheritDoc} */
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        if (!SOAPMessagingSupport.isSOAPMessage(messageContext)) {
            log.info("Message context does not contain a SOAP envelope. Skipping rule...");
            return;
        }
        
        List<Assertion> assertions = resolveAssertions(messageContext);
        if (assertions == null || assertions.isEmpty()) {
            log.info("Inbound SOAP envelope contained no Assertion tokens. Skipping further processing");
            return;
        }
        
        WSSecurityContext wsContext = messageContext.getSubcontext(WSSecurityContext.class, true);
        
        SAML20AssertionValidator tokenValidator = getTokenValidator(messageContext);
        if (tokenValidator == null) {
            log.warn("No SAML20AssertionValidator was available, terminating");
            throw new MessageHandlerException("No SAML20AssertionValidator was available");
        }
        
        for (Assertion assertion : assertions) {
            ValidationContext validationContext = buildValidationContext(messageContext, assertion);
            
            SAML20AssertionToken token = new SAML20AssertionToken(assertion);
            
            try { 
                ValidationResult validationResult = tokenValidator.validate(assertion, validationContext);
                processResult(validationContext, validationResult, token, messageContext);
                wsContext.getTokens().add(token);
            } catch (AssertionValidationException e) {
                log.warn("There was a problem determining Assertion validity: {}", e.getMessage());
                //TODO how are we handling SOAP faults in v3?
                //SOAPProcessingHelper.registerFault(messageContext, FaultCode.SERVER, 
                //        "Internal security token processing error", null, null, null);
                throw new MessageHandlerException("Error determining SAML 2.0 Assertion validity", e);
            }
        }
        
    }

    /**
     * Process the result of the token validation.
     * 
     * @param validationContext the Assertion validation context
     * @param validationResult the Assertion validation result
     * @param token the token being produced
     * @param messageContext the current message context
     * 
     * @throws MessageHandlerException if the Assertion was invalid or indeterminate and idInvalidFatal is true
     */
    protected void processResult(@Nonnull final ValidationContext validationContext, 
            @Nonnull final ValidationResult validationResult, @Nonnull final SAML20AssertionToken token, 
            @Nonnull final MessageContext messageContext) throws MessageHandlerException {
        
        log.debug("Assertion token validation result was: {}", validationResult);
        
        String validationMsg = validationContext.getValidationFailureMessage();
        if (Strings.isNullOrEmpty(validationMsg)) {
            validationMsg  = "unspecified";
        }
                    
        // TODO re fault reporting: per WS-S spec there are various codes for more specific processing failures.
        // We would need the Assertion validator to report that more specific error in some fashion
        switch (validationResult) {
            case VALID:
                token.setValidationStatus(ValidationStatus.VALID);
                token.setSubjectConfirmation((SubjectConfirmation) validationContext.getDynamicParameters()
                        .get(SAML2AssertionValidationParameters.CONFIRMED_SUBJECT_CONFIRMATION));
                break;
            case INVALID:
                log.warn("Assertion token validation was INVALID.  Reason: {}", validationMsg);
                if (isInvalidFatal()) {
                    //TODO how are we handling SOAP faults in v3?
                    /*
                    SOAPProcessingHelper.registerFault(messageContext,
                            WSSecurityConstants.SOAP_FAULT_INVALID_SECURITY_TOKEN, 
                            "The SAML 2.0 Assertion token was invalid", null, null, null);
                     */
                    throw new MessageHandlerException("Assertion token validation result was INVALID"); 
                } else {
                    token.setValidationStatus(ValidationStatus.INVALID);
                    token.setSubjectConfirmation((SubjectConfirmation) validationContext.getDynamicParameters()
                            .get(SAML2AssertionValidationParameters.CONFIRMED_SUBJECT_CONFIRMATION));
                }
                break;
            case INDETERMINATE:
                log.warn("Assertion token validation was INDETERMINATE. Reason: {}", validationMsg);
                if (isInvalidFatal()) {
                    //TODO how are we handling SOAP faults in v3?
                    /*
                    SOAPProcessingHelper.registerFault(messageContext,
                            WSSecurityConstants.SOAP_FAULT_INVALID_SECURITY_TOKEN, 
                            "The SAML 2.0 Assertion token's validity could not be determined", null, null, null);
                     */
                    throw new MessageHandlerException("Assertion token validation result was INDETERMINATE"); 
                } else {
                    token.setValidationStatus(ValidationStatus.INDETERMINATE);
                    token.setSubjectConfirmation((SubjectConfirmation) validationContext.getDynamicParameters()
                            .get(SAML2AssertionValidationParameters.CONFIRMED_SUBJECT_CONFIRMATION));
                }
                break;
            default:
                log.warn("Assertion validation result indicated an unknown value: {}", validationResult);
        }
        
    }

    /**
     * Get the Assertion token validator.
     * 
     * @param messageContext the current message context
     * 
     * @return the token validator
     */
    @Nullable protected SAML20AssertionValidator getTokenValidator(@Nonnull final MessageContext messageContext) {
        //TODO support resolution from context, etc.
        return assertionValidator;
    }

    /**
     * Build the Assertion ValidationContext.
     * 
     * @param messageContext the current message context
     * @param assertion the assertion which is to be validated
     * 
     * @return the new Assertion validation context to use
     */
    @Nonnull protected ValidationContext buildValidationContext(@Nonnull final MessageContext messageContext, 
            @Nonnull final Assertion assertion) {
        HashMap<String, Object> staticParams = new HashMap<String, Object>();
        
        //For signature validation
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, Boolean.TRUE);
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_VALIDATION_CRITERIA_SET, 
                getSignatureCriteriaSet(messageContext, assertion));
        
        // For HoK subject confirmation
        staticParams.put(SAML2AssertionValidationParameters.SC_HOK_PRESENTER_CERT, 
                getAttesterCertificate(messageContext));
        
        // For SubjectConfirmationData
        HashSet<String> validRecipients = new HashSet<String>();
        validRecipients.addAll(getValidRecipients(messageContext));
        staticParams.put(SAML2AssertionValidationParameters.SC_VALID_RECIPIENTS, validRecipients);
        
        try {
            InetAddress[] addresses = null;
            String attesterIPAddress = getAttesterIPAddress(messageContext);
            if (attesterIPAddress != null) {
                addresses = InetAddress.getAllByName(getAttesterIPAddress(messageContext));
                HashSet<InetAddress> validAddresses = new HashSet<InetAddress>();
                validAddresses.addAll(Arrays.asList(addresses));
                staticParams.put(SAML2AssertionValidationParameters.SC_VALID_ADDRESSES, validAddresses);
            } else {
                log.warn("Could not determine attester IP address. Validation of Assertion may or may not succeed");
            }
        } catch (UnknownHostException e) {
            log.warn("Processing of attester IP address failed. Validation of Assertion may or may not succeed", e);
        }
        
        // For Audience Condition
        HashSet<String> validAudiences = new HashSet<String>();
        validAudiences.addAll(getValidAudiences(messageContext));
        staticParams.put(SAML2AssertionValidationParameters.COND_VALID_AUDIENCES, validAudiences);
        
        return new ValidationContext(staticParams);
    }

    /**
     * Get the attesting entity's certificate.
     * 
     * @param messageContext the current message context.
     * 
     * @return the entity certificate
     */
    @Nullable protected X509Certificate getAttesterCertificate(@Nonnull final MessageContext messageContext) {
        try {
            X509Credential credential = new ServletRequestX509CredentialAdapter(getHttpServletRequest());
            return ((X509Credential)credential).getEntityCertificate();
        } catch (SecurityException e) {
            log.warn("Peer TLS X.509 certificate was not present. " 
                    + "Holder-of-key proof-of-possession via client TLS cert will not be possible");
            return null;
        }
    }

    /**
     * Get the attester's IP address.
     * 
     * @param messageContext the current message context.
     * 
     * @return the IP address of the attester
     */
    @Nonnull protected String getAttesterIPAddress(@Nonnull final MessageContext messageContext) {
        return getHttpServletRequest().getRemoteAddr();
    }
    
    /**
     * Get the signature validation criteria set.
     * 
     * @param messageContext the current message context.
     * @param assertion the assertion whose signature is to be verified
     * 
     * @return the criteria set based on the message context data
     */
    @Nonnull protected CriteriaSet getSignatureCriteriaSet(@Nonnull final MessageContext messageContext, 
            @Nonnull final Assertion assertion) {
        CriteriaSet criteriaSet = new CriteriaSet();
        
        if (getSignatureCriteriaSetFunction() != null) {
            CriteriaSet dynamicCriteria = getSignatureCriteriaSetFunction().apply(
                    new Pair<MessageContext, Assertion>());
            if (dynamicCriteria != null) {
                criteriaSet.addAll(dynamicCriteria);
            }
        }
        
        if (!criteriaSet.contains(EntityIdCriterion.class)) {
            String issuer = null;
            if (assertion.getIssuer() != null) {
                issuer = StringSupport.trimOrNull(assertion.getIssuer().getValue());
            }
            if (issuer != null) {
                log.debug("Adding internally-generated EntityIdCriterion with value of: {}", issuer);
                criteriaSet.add(new EntityIdCriterion(issuer));
            }
        }
        
        if (!criteriaSet.contains(UsageCriterion.class)) {
            log.debug("Adding internally-generated UsageCriterion with value of: {}", UsageType.SIGNING);
            criteriaSet.add(new UsageCriterion(UsageType.SIGNING));
        }
        
        return criteriaSet;
    }

    /**
     * Get the valid recipient endpoints for attestation.
     * 
     * @param messageContext the current message context.
     * 
     * @return set of recipient endpoint URI's
     */
    @Nonnull protected Set<String> getValidRecipients(@Nonnull final MessageContext messageContext) {
        LazySet<String> validRecipients = new LazySet<String>();
        String endpoint = getHttpServletRequest().getRequestURL().toString();
        validRecipients.add(endpoint);
        return validRecipients;
    }

    /**
     * Get the valid audiences for attestation.
     * 
     * @param messageContext the current message context.
     * 
     * @return set of audience URI's
     */
    @Nonnull protected Set<String> getValidAudiences(@Nonnull final MessageContext messageContext) {
        LazySet<String> validAudiences = new LazySet<String>();
        
        //TODO for future library inclusion this probably needs to be more generalized and/or pluggable
        
        //TODO
        /*
        ShibbolethAssertionValidationContext shibAssertionValidationContext = null;
        //TODO
        if (shibAssertionValidationContext == null) {
            log.warn("Context container did not contain a Shibboleth assertion validation sub context." 
                    + "Unable to resolve set of valid audiences. Validation of Assertion may or may not succeed");
            return validAudiences;
        }
        
        log.debug("Obtained set of valid audiences from Shibboleth assertion validation context: {}",
                shibAssertionValidationContext.getValidAudiences());
        
        validAudiences.addAll(shibAssertionValidationContext.getValidAudiences());
        */
        
        return validAudiences;
    }

    /**
     * Resolve the SAML 2.0 Assertions token from the SOAP envelope.
     * 
     * @param messageContext the current message context
     * 
     * @return the list of resolved Assertions, or an empty list
     */
    @Nonnull protected List<Assertion> resolveAssertions(@Nonnull final MessageContext messageContext) {
        List<XMLObject> securityHeaders = SOAPMessagingSupport.getInboundHeaderBlock(messageContext,
                Security.ELEMENT_NAME);
        if (securityHeaders == null || securityHeaders.isEmpty()) {
            log.debug("No WS-Security found in inbound SOAP message. Skipping further processing.");
            return Collections.emptyList();
        }
        
        LazyList<Assertion> assertions = new LazyList<Assertion>();
        
        // There could be multiple Security headers targeted to this node, so process all of them
        for (XMLObject header : securityHeaders) {
            Security securityHeader = (Security) header;
            List<XMLObject> xmlObjects = securityHeader.getUnknownXMLObjects(Assertion.DEFAULT_ELEMENT_NAME);
            if (xmlObjects != null && !xmlObjects.isEmpty()) {
                for (XMLObject xmlObject : xmlObjects) {
                    assertions.add((Assertion) xmlObject);
                }
            }
        }
        
        return assertions;
    }

}
