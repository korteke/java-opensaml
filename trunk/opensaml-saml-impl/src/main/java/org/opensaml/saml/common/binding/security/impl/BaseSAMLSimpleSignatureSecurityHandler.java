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

package org.opensaml.saml.common.binding.security.impl;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLProtocolContext;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.opensaml.xmlsec.signature.support.SignatureValidationParametersCriterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * Base class for security-oriented message handlers which verify simple "blob" signatures computed 
 * over some components of a request.
 */
public abstract class BaseSAMLSimpleSignatureSecurityHandler extends AbstractMessageHandler<SAMLObject>{

    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(BaseSAMLSimpleSignatureSecurityHandler.class);

    /** Signature trust engine used to validate raw signatures. */
    @NonnullAfterInit private SignatureTrustEngine trustEngine;
    
    /** The HttpServletRequest being processed. */
    @NonnullAfterInit private HttpServletRequest httpServletRequest;
    
    /** The context representing the SAML peer entity. */
    @Nullable private SAMLPeerEntityContext peerContext;
    
    /** The SAML protocol context in operation. */
    @Nullable private SAMLProtocolContext samlProtocolContext;

    /**
     * Gets the engine used to validate the signature.
     * 
     * @return engine engine used to validate the signature
     */
    @NonnullAfterInit public SignatureTrustEngine getTrustEngine() {
        return trustEngine;
    }
    
    /**
     * Sets the engine used to validate the signature.
     * 
     * @param engine engine used to validate the signature
     */
    public void setTrustEngine(@Nonnull final SignatureTrustEngine engine) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        trustEngine = Constraint.isNotNull(engine, "SignatureTrustEngine cannot be null");
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
        
        httpServletRequest = Constraint.isNotNull(request, "HttpServletRequest cannot be null");
    }
    
    /**
     * Get the current SAML Protocol context.
     * 
     * @param messageContext the current message context
     * @return the current SAML protocol context
     */
    @Nullable protected SAMLProtocolContext getSAMLProtocolContext(
            @Nonnull final MessageContext<SAMLObject> messageContext) {
        //TODO is this the final resting place?
        return messageContext.getSubcontext(SAMLProtocolContext.class, false);
    }

    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (trustEngine == null) {
            throw new ComponentInitializationException("SignatureTrustEngine cannot be null");
        } else if (httpServletRequest == null) {
            throw new ComponentInitializationException("HttpServletRequest cannot be null");
        }
    }

    /** {@inheritDoc} */
    @Override
    protected boolean doPreInvoke(@Nonnull final MessageContext<SAMLObject> messageContext)
            throws MessageHandlerException {
        peerContext = messageContext.getSubcontext(SAMLPeerEntityContext.class, true);
        samlProtocolContext = getSAMLProtocolContext(messageContext);
        if (samlProtocolContext == null || samlProtocolContext.getProtocol() == null) {
            throw new MessageHandlerException("SAMLProtocolContext was missing or unpopulated");
        }
        return super.doPreInvoke(messageContext);
    }

    /** {@inheritDoc} */
    @Override
    protected void doInvoke(@Nonnull final MessageContext<SAMLObject> messageContext) throws MessageHandlerException {
        log.debug("{} Evaluating simple signature rule of type: {}", getLogPrefix(), getClass().getName());

        if (!ruleHandles(messageContext)) {
            log.debug("{} Handler can not handle this request, skipping processing", getLogPrefix());
            return;
        }

        final byte[] signature = getSignature();
        if (signature == null || signature.length == 0) {
            log.debug("{} HTTP request was not signed via simple signature mechanism, skipping", getLogPrefix());
            return;
        }

        final String sigAlg = getSignatureAlgorithm();
        if (Strings.isNullOrEmpty(sigAlg)) {
            log.warn("{} Signature algorithm could not be extracted from request, cannot validate simple signature",
                    getLogPrefix());
            return;
        }

        final byte[] signedContent = getSignedContent();
        if (signedContent == null || signedContent.length == 0) {
            log.warn("{} Signed content could not be extracted from HTTP request, cannot validate", getLogPrefix());
            return;
        }

        doEvaluate(signature, signedContent, sigAlg, messageContext);
    }

    /**
     * Evaluate the simple signature based on information in the request and/or message context.
     * 
     * @param signature the signature value
     * @param signedContent the content that was signed
     * @param algorithmURI the signature algorithm URI which was used to sign the content
     * @param messageContext the SAML message context being processed
     * @throws MessageHandlerException thrown if there are errors during the signature validation process
     * 
     */
    private void doEvaluate(@Nonnull @NotEmpty final byte[] signature, @Nonnull @NotEmpty byte[] signedContent,
            @Nonnull @NotEmpty final String algorithmURI, @Nonnull final MessageContext<SAMLObject> messageContext)
                    throws MessageHandlerException {

        final List<Credential> candidateCredentials = getRequestCredentials(messageContext);

        final String contextEntityID = peerContext.getEntityId();

        
        //TODO authentication flags - on peer or on message?
        
        if (contextEntityID != null) {
            log.debug("{} Attempting to validate SAML protocol message simple signature using context entityID: {}",
                    getLogPrefix(), contextEntityID);
            final CriteriaSet criteriaSet = buildCriteriaSet(contextEntityID, messageContext);
            if (validateSignature(signature, signedContent, algorithmURI, criteriaSet, candidateCredentials)) {
                log.debug("{} Validation of request simple signature succeeded", getLogPrefix());
                if (!peerContext.isAuthenticated()) {
                    log.debug(
                            "{} Authentication via request simple signature succeeded for context issuer entity ID {}",
                            getLogPrefix(), contextEntityID);
                    peerContext.setAuthenticated(true);
                }
                return;
            } else {
                log.warn("{} Validation of request simple signature failed for context issuer: {}", getLogPrefix(),
                        contextEntityID);
                throw new MessageHandlerException("Validation of request simple signature failed for context issuer");
            }
        }
            
        final String derivedEntityID = deriveSignerEntityID(messageContext);
        if (derivedEntityID != null) {
            log.debug("{} Attempting to validate SAML protocol message simple signature using derived entityID: {}",
                    getLogPrefix(), derivedEntityID);
            final CriteriaSet criteriaSet = buildCriteriaSet(derivedEntityID, messageContext);
            if (validateSignature(signature, signedContent, algorithmURI, criteriaSet, candidateCredentials)) {
                log.debug("{} Validation of request simple signature succeeded", getLogPrefix());
                if (!peerContext.isAuthenticated()) {
                    log.debug("{} Authentication via request simple signature succeeded for derived issuer {}",
                            getLogPrefix(), derivedEntityID);
                    peerContext.setEntityId(derivedEntityID);
                    peerContext.setAuthenticated(true);
                }
                return;
            } else {
                log.warn("{} Validation of request simple signature failed for derived issuer: {}", getLogPrefix(),
                        derivedEntityID);
                throw new MessageHandlerException("Validation of request simple signature failed for derived issuer");
            }
        }
        
        log.warn("{} Neither context nor derived issuer available, cannot attempt SAML simple signature validation",
                getLogPrefix());
        throw new MessageHandlerException("No message issuer available, cannot attempt simple signature validation");
    }

    /**
     * Validate the simple signature.
     * 
     * @param signature the signature value
     * @param signedContent the content that was signed
     * @param algorithmURI the signature algorithm URI which was used to sign the content
     * @param criteriaSet criteria used to describe and/or resolve the information which serves as the basis for trust
     *            evaluation
     * @param candidateCredentials the request-derived candidate credential(s) containing the validation key for the
     *            signature (optional)
     * @return true if signature can be verified successfully, false otherwise
     * 
     * @throws MessageHandlerException thrown if there are errors during the signature validation process
     * 
     */
    protected boolean validateSignature(@Nonnull @NotEmpty final byte[] signature,
            @Nonnull @NotEmpty final byte[] signedContent, @Nonnull @NotEmpty final String algorithmURI,
            @Nonnull final CriteriaSet criteriaSet,
            @Nonnull @NonnullElements final List<Credential> candidateCredentials) throws MessageHandlerException {

        final SignatureTrustEngine engine = getTrustEngine();

        // Some bindings allow candidate signing credentials to be supplied (e.g. via ds:KeyInfo), some do not.
        // So have 2 slightly different cases.
        try {
            if (candidateCredentials == null || candidateCredentials.isEmpty()) {
                if (engine.validate(signature, signedContent, algorithmURI, criteriaSet, null)) {
                    log.debug("{} Simple signature validation (with no request-derived credentials) was successful",
                            getLogPrefix());
                    return true;
                } else {
                    log.warn("{} Simple signature validation (with no request-derived credentials) failed",
                            getLogPrefix());
                    return false;
                }
            } else {
                for (Credential cred : candidateCredentials) {
                    if (engine.validate(signature, signedContent, algorithmURI, criteriaSet, cred)) {
                        log.debug("{} Simple signature validation succeeded with a request-derived credential",
                                getLogPrefix());
                        return true;
                    }
                }
                log.warn("{} Signature validation using request-derived credentials failed", getLogPrefix());
                return false;
            }
        } catch (SecurityException e) {
            log.warn(getLogPrefix() + " Error evaluating the request's simple signature using the trust engine", e);
            throw new MessageHandlerException("Error during trust engine evaluation of the simple signature", e);
        }
    }

    /**
     * Extract any candidate validation credentials from the request and/or message context.
     * 
     * Some bindings allow validataion keys for the simple signature to be supplied, and others do not.
     * @param messageContext the SAML message context being processed
     * 
     * @return a list of candidate validation credentials in the request, or null if none were present
     * @throws MessageHandlerException thrown if there is an error during request processing
     */
    @Nonnull @NonnullElements protected List<Credential> getRequestCredentials(
            @Nonnull final MessageContext<SAMLObject> messageContext) throws MessageHandlerException {
        // This will be specific to the binding and message types, so no default.
        return Collections.emptyList();
    }

    /**
     * Extract the signature value from the request, in the form suitable for input into
     * {@link SignatureTrustEngine#validate(byte[], byte[], String, CriteriaSet, Credential)}.
     * 
     * Defaults to the Base64-decoded value of the HTTP request parameter named <code>Signature</code>.
     * 
     * @return the signature value
     * @throws MessageHandlerException thrown if there is an error during request processing
     */
    @Nullable protected byte[] getSignature() throws MessageHandlerException {
        String signature = getHttpServletRequest().getParameter("Signature");
        if (Strings.isNullOrEmpty(signature)) {
            return null;
        }
        return Base64Support.decode(signature);
    }

    /**
     * Extract the signature algorithm URI value from the request.
     * 
     * Defaults to the HTTP request parameter named <code>SigAlg</code>.
     * 
     * @return the signature algorithm URI value
     * @throws MessageHandlerException thrown if there is an error during request processing
     */
    @Nullable protected String getSignatureAlgorithm()
            throws MessageHandlerException {
        return getHttpServletRequest().getParameter("SigAlg");
    }

    /**
     * Derive the signer's entity ID from the message context.
     * 
     * This is implementation-specific and there is no default. This is primarily an extension point for subclasses.
     * 
     * @param messageContext the SAML message context being processed
     * @return the signer's derived entity ID
     * @throws MessageHandlerException thrown if there is an error during request processing
     */
    @Nullable protected String deriveSignerEntityID(@Nonnull final MessageContext<SAMLObject> messageContext)
            throws MessageHandlerException {
        // No default
        return null;
    }

    /**
     * Build a criteria set suitable for input to the trust engine.
     * 
     * @param entityID the candidate issuer entity ID which is being evaluated
     * @param messageContext the message context which is being evaluated
     * @return a newly constructly set of criteria suitable for the configured trust engine
     * @throws MessageHandlerException thrown if criteria set can not be constructed
     */
    @Nonnull protected CriteriaSet buildCriteriaSet(@Nullable final String entityID,
            @Nonnull final MessageContext<SAMLObject> messageContext) throws MessageHandlerException {

        final CriteriaSet criteriaSet = new CriteriaSet();
        if (!Strings.isNullOrEmpty(entityID)) {
            criteriaSet.add(new EntityIdCriterion(entityID));
        }
        
        try {
            SAMLPeerEntityContext peerEntityContext = messageContext.getSubcontext(SAMLPeerEntityContext.class);
            Constraint.isNotNull(peerEntityContext, "SAMLPeerEntityContext was null");
            Constraint.isNotNull(peerEntityContext.getRole(), "SAML peer role was null");
            criteriaSet.add(new EntityRoleCriterion(peerEntityContext.getRole()));

            SAMLProtocolContext protocolContext = getSAMLProtocolContext(messageContext);
            Constraint.isNotNull(protocolContext, "SAMLProtocolContext was null");
            Constraint.isNotNull(protocolContext.getProtocol(), "SAML protocol was null");
            criteriaSet.add(new ProtocolCriterion(protocolContext.getProtocol()));
        } catch (ConstraintViolationException e) {
            throw new MessageHandlerException(e);
        }
        
        criteriaSet.add(new UsageCriterion(UsageType.SIGNING));
        
        SecurityParametersContext secParamsContext = messageContext.getSubcontext(SecurityParametersContext.class);
        if (secParamsContext != null && secParamsContext.getSignatureValidationParameters() != null) {
            criteriaSet.add(new SignatureValidationParametersCriterion(
                    secParamsContext.getSignatureValidationParameters()));
        }

        return criteriaSet;
    }

    /**
     * Get the content over which to validate the signature, in the form suitable for input into
     * {@link SignatureTrustEngine#validate(byte[], byte[], String, CriteriaSet, Credential)}.
     * 
     * @return the signed content extracted from the request, in the format suitable for input to the trust engine.
     * @throws MessageHandlerException thrown if there is an error during request processing
     */
    @Nullable protected abstract byte[] getSignedContent()
            throws MessageHandlerException;

    /**
     * Determine whether the rule should handle the request, based on the unwrapped HTTP servlet request and/or message
     * context.
     * @param messageContext the SAML message context being processed
     * 
     * @return true if the rule should attempt to process the request, otherwise false
     * @throws MessageHandlerException thrown if there is an error during request processing
     */
    protected abstract boolean ruleHandles(@Nonnull final MessageContext<SAMLObject> messageContext)
            throws MessageHandlerException;

}
