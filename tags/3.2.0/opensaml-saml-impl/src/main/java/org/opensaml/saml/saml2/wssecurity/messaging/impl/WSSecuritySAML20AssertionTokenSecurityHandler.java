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

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.collection.LazyList;
import net.shibboleth.utilities.java.support.collection.Pair;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;

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
import org.opensaml.soap.messaging.SOAPMessagingSupport;
import org.opensaml.soap.soap11.FaultCode;
import org.opensaml.soap.wssecurity.Security;
import org.opensaml.soap.wssecurity.WSSecurityConstants;
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
    
    /** The SAML 2.0 Assertion validator, may be null.*/
    @Nullable private SAML20AssertionValidator assertionValidator;
    
    /** The SAML 2.0 Assertion validator lookup function, may be null.*/
    @Nullable private Function<Pair<MessageContext, Assertion>, SAML20AssertionValidator> assertionValidatorLookup;
    
    /** Function that builds a {@link ValidationContext} instance based on a 
     * {@link SAML20AssertionTokenValidationInput} instance. */
    @NonnullAfterInit private Function<SAML20AssertionTokenValidationInput, ValidationContext> validationContextBuilder;
    
    
    /** Constructor. */
    public WSSecuritySAML20AssertionTokenSecurityHandler() {
        super();
        setInvalidFatal(true);
        setValidationContextBuilder(new DefaultSAML20AssertionValidationContextBuilder());
    }

    /**
     * Get the function that builds a {@link ValidationContext} instance based on a 
     * {@link SAML20AssertionTokenValidationInput} instance.
     * 
     * <p>
     * Defaults to an instance of {@link DefaultSAML20AssertionValidationContextBuilder}.
     * </p>
     * 
     * @return the builder function
     */
    @NonnullAfterInit
    public Function<SAML20AssertionTokenValidationInput, ValidationContext> getValidationContextBuilder() {
        return validationContextBuilder;
    }

    /**
     * Set the function that builds a {@link ValidationContext} instance based on a 
     * {@link SAML20AssertionTokenValidationInput} instance.
     * 
     * <p>
     * Defaults to an instance of {@link DefaultSAML20AssertionValidationContextBuilder}.
     * </p>
     * 
     * @param builder the builder function
     */
    public void setValidationContextBuilder(
            @Nonnull final Function<SAML20AssertionTokenValidationInput, ValidationContext> builder) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        validationContextBuilder = Constraint.isNotNull(builder, "Validation context builder may not be null");
    }

    /**
     * Get the HTTP servlet request being processed.
     * 
     * @return the HTTP servlet request
     */
    @NonnullAfterInit public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    /**
     * Set the HTTP servlet request being processed.
     * 
     * @param request The HTTP servlet request
     */
    public void setHttpServletRequest(@Nonnull final HttpServletRequest request) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        httpServletRequest = Constraint.isNotNull(request, "HttpServletRequest cannot be null");
    }
    
    /**
     * Get flag which indicates whether a failure of Assertion validation should be considered a fatal processing error.
     * 
     * <p>
     * Defaults to: {@code true}.
     * </p>
     * 
     * @return Returns the invalidFatal.
     */
    public boolean isInvalidFatal() {
        return invalidFatal;
    }

    /**
     * Set flag which indicates whether a failure of Assertion validation should be considered a fatal processing error.
     * 
     * <p>
     * Defaults to: {@code true}.
     * </p>
     * 
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
    
    /**
     * Get the Assertion validator lookup function.
     * 
     * @return the Assertion validator lookup function, or null
     */
    @Nullable public Function<Pair<MessageContext, Assertion>, SAML20AssertionValidator> getAssertionValidatorLookup() {
        return assertionValidatorLookup;
    }

    /**
     * Set the Assertion validator lookup function.
     * 
     * @param function the Assertion validator lookup function, may be null
     */
    public void setAssertionValidatorLookup(
            @Nullable final Function<Pair<MessageContext, Assertion>, SAML20AssertionValidator> function) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        assertionValidatorLookup = function;
    }

    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (getValidationContextBuilder() == null) {
            throw new ComponentInitializationException("ValidationContext builder cannot be null");
        }
        
        if (getHttpServletRequest() == null) {
            throw new ComponentInitializationException("HttpServletRequest cannot be null");
        }
        
        if (getAssertionValidator() == null) {
            if (getAssertionValidatorLookup() == null) {
                throw new ComponentInitializationException("Both Assertion validator and lookup function were null");
            } else {
                log.info("Assertion validator is null, must be resovleable via the lookup function");
            }
        }
    }

    /** {@inheritDoc} */
    protected void doDestroy() {
        httpServletRequest = null;
        
        super.doDestroy();
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
        
        for (Assertion assertion : assertions) {
            SAML20AssertionValidator validator = resolveValidator(messageContext, assertion);
            if (validator == null) {
                log.warn("No SAML20AssertionValidator was available, terminating");
                SOAPMessagingSupport.registerSOAP11Fault(messageContext, FaultCode.SERVER, 
                        "Internal processing error", null, null, null);
                throw new MessageHandlerException("No SAML20AssertionValidator was available");
            }
        
            ValidationContext validationContext = buildValidationContext(messageContext, assertion);
            
            try { 
                ValidationResult validationResult = validator.validate(assertion, validationContext);
                SAML20AssertionToken token = new SAML20AssertionToken(assertion);
                processResult(validationContext, validationResult, token, messageContext);
                wsContext.getTokens().add(token);
            } catch (AssertionValidationException e) {
                log.warn("There was a problem determining Assertion validity: {}", e.getMessage());
                SOAPMessagingSupport.registerSOAP11Fault(messageContext, FaultCode.SERVER, 
                        "Internal security token processing error", null, null, null);
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
                    
        switch (validationResult) {
            case VALID:
                token.setValidationStatus(ValidationStatus.VALID);
                token.setSubjectConfirmation((SubjectConfirmation) validationContext.getDynamicParameters()
                        .get(SAML2AssertionValidationParameters.CONFIRMED_SUBJECT_CONFIRMATION));
                break;
            case INVALID:
                log.warn("Assertion token validation was INVALID.  Reason: {}", validationMsg);
                if (isInvalidFatal()) {
                    SOAPMessagingSupport.registerSOAP11Fault(messageContext,
                            WSSecurityConstants.SOAP_FAULT_INVALID_SECURITY_TOKEN, 
                            "The SAML 2.0 Assertion token was invalid", null, null, null);
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
                    SOAPMessagingSupport.registerSOAP11Fault(messageContext,
                            WSSecurityConstants.SOAP_FAULT_INVALID_SECURITY_TOKEN, 
                            "The SAML 2.0 Assertion token's validity could not be determined", null, null, null);
                    throw new MessageHandlerException("Assertion token validation result was INDETERMINATE"); 
                } else {
                    token.setValidationStatus(ValidationStatus.INDETERMINATE);
                    token.setSubjectConfirmation((SubjectConfirmation) validationContext.getDynamicParameters()
                            .get(SAML2AssertionValidationParameters.CONFIRMED_SUBJECT_CONFIRMATION));
                }
                break;
            default:
                log.warn("Assertion validation result indicated an unknown value: {}", validationResult);
                SOAPMessagingSupport.registerSOAP11Fault(messageContext, FaultCode.SERVER, 
                        "Internal processing error", null, null, null);
                throw new IllegalArgumentException("Assertion validation result indicated an unknown value: "
                        + validationResult);
        }
        
    }

    /**
     * Resolve the Assertion token validator to use with the specified Assertion.
     * 
     * @param messageContext the current message context
     * @param assertion the assertion being evaluated
     * 
     * @return the token validator
     */
    @Nullable protected SAML20AssertionValidator resolveValidator(@Nonnull final MessageContext messageContext, 
            @Nonnull final Assertion assertion) {
        
        if (getAssertionValidatorLookup() != null) {
            log.debug("Attempting to resolve SAML 2 Assertion validator via lookup function");
            SAML20AssertionValidator validator = getAssertionValidatorLookup().apply(
                    new Pair<>(messageContext, assertion));
            if (validator != null) {
                log.debug("Resolved SAML 2 Assertion validator via lookup function");
                return validator;
            }
        }
        
        if (getAssertionValidator() != null) {
            log.debug("Resolved locally configured SAML 2 Assertion validator");
            return getAssertionValidator();
        }
        
        log.debug("No SAML 2 Assertion validator could be resolved");
        return null;
    }

    /**
     * Build the Assertion ValidationContext.
     * 
     * @param messageContext the current message context
     * @param assertion the assertion which is to be validated
     * 
     * @return the new Assertion validation context to use
     * 
     * @throws MessageHandlerException if no validation context instance could be built
     */
    @Nonnull protected ValidationContext buildValidationContext(@Nonnull final MessageContext messageContext, 
            @Nonnull final Assertion assertion) throws MessageHandlerException {
        
        ValidationContext validationContext = getValidationContextBuilder().apply(
                new SAML20AssertionTokenValidationInput(messageContext, getHttpServletRequest(), assertion));
        
        if (validationContext == null) {
            log.warn("ValidationContext produced was null");
            SOAPMessagingSupport.registerSOAP11Fault(messageContext, FaultCode.SERVER, 
                    "Internal processing error", null, null, null);
            throw new MessageHandlerException("No ValidationContext was produced");
        }
        
        return validationContext;
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
            log.debug("No WS-Security Security header found in inbound SOAP message. Skipping further processing.");
            return Collections.emptyList();
        }
        
        LazyList<Assertion> assertions = new LazyList<>();
        
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
