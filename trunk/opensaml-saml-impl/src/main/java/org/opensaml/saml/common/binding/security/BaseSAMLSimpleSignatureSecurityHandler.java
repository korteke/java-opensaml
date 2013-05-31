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

package org.opensaml.saml.common.binding.security;

import java.util.List;


import javax.servlet.http.HttpServletRequest;

import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.messaging.context.SamlPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SamlProtocolContext;
import org.opensaml.saml.security.MetadataCriterion;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.EntityIDCriterion;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * Base class for security-oriented message handlers which verify simple "blob" signatures computed 
 * over some components of a request.
 */
public abstract class BaseSAMLSimpleSignatureSecurityHandler extends AbstractMessageHandler<SAMLObject>{

    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(BaseSAMLSimpleSignatureSecurityHandler.class);

    /** Signature trust engine used to validate raw signatures. */
    private SignatureTrustEngine trustEngine;
    
    /** The HttpServletRequest being processed. */
    private HttpServletRequest httpServletRequest;
    
    /** The context representing the SAML peer entity. */
    private SamlPeerEntityContext peerContext;
    
    /** The SAML protocol context in operation. */
    private SamlProtocolContext samlProtocolContext;

    /**
     * Gets the engine used to validate the signature.
     * 
     * @return engine engine used to validate the signature
     */
    public SignatureTrustEngine getTrustEngine() {
        return trustEngine;
    }
    
    /**
     * Sets the engine used to validate the signature.
     * 
     * @param engine engine used to validate the signature
     */
    public void setTrustEngine(SignatureTrustEngine engine) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        trustEngine = Constraint.isNotNull(engine, "TrustEngine may not be null");
    }

    /**
     * Get the HTTP servlet request being processed.
     * 
     * @return Returns the request.
     */
    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    /**
     * Set the HTTP servlet request being processed.
     * 
     * @param request The to set.
     */
    public void setHttpServletRequest(HttpServletRequest request) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        httpServletRequest = Constraint.isNotNull(request, "HttpServletRequest may not be null");
    }
    
    /**
     * Get the current SAML Protocol context.
     * 
     * @param messageContext the current message context
     * @return the current SAML protocol context
     */
    protected SamlProtocolContext getSamlProtocolContext(MessageContext<SAMLObject> messageContext) {
        //TODO is this the final resting place?
        return messageContext.getSubcontext(SamlProtocolContext.class, false);
    }

    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        Constraint.isNotNull(trustEngine, "SignatureTrustEngine must be supplied");
        Constraint.isNotNull(httpServletRequest, "HttpServletRequest must be supplied");
    }

    /** {@inheritDoc} */
    protected boolean doPreInvoke(MessageContext<SAMLObject> messageContext) throws MessageHandlerException {
        peerContext = messageContext.getSubcontext(SamlPeerEntityContext.class, true);
        samlProtocolContext = Constraint.isNotNull(getSamlProtocolContext(messageContext), 
                "SamlProtocolContext was not found");
        Constraint.isNotNull(samlProtocolContext.getProtocol(), "SAML protocol value was null");
        return true;
    }

    /** {@inheritDoc} */
    protected void doInvoke(MessageContext<SAMLObject> messageContext) throws MessageHandlerException {
        log.debug("Evaluating simple signature rule of type: {}", getClass().getName());

        if (!ruleHandles(getHttpServletRequest(), messageContext)) {
            log.debug("Handler can not handle this request, skipping processing");
            return;
        }

        byte[] signature = getSignature(httpServletRequest);
        if (signature == null || signature.length == 0) {
            log.debug("HTTP request was not signed via simple signature mechanism, skipping");
            return;
        }

        String sigAlg = getSignatureAlgorithm(httpServletRequest);
        if (Strings.isNullOrEmpty(sigAlg)) {
            log.warn("Signature algorithm could not be extracted from request, can not validate simple signature");
            return;
        }

        byte[] signedContent = getSignedContent(httpServletRequest);
        if (signedContent == null || signedContent.length == 0) {
            log.warn("Signed content could not be extracted from HTTP request, can not validate");
            return;
        }

        doEvaluate(signature, signedContent, sigAlg, httpServletRequest, messageContext);
    }

    /**
     * Evaluate the simple signature based on information in the request and/or message context.
     * 
     * @param signature the signature value
     * @param signedContent the content that was signed
     * @param algorithmURI the signature algorithm URI which was used to sign the content
     * @param request the HTTP servlet request being processed
     * @param messageContext the SAML message context being processed
     * 
     * @throws MessageHandlerException thrown if there are errors during the signature validation process
     * 
     */
    private void doEvaluate(byte[] signature, byte[] signedContent, String algorithmURI, HttpServletRequest request,
            MessageContext<SAMLObject> messageContext) throws MessageHandlerException {

        List<Credential> candidateCredentials = getRequestCredentials(request, messageContext);

        String contextEntityID = peerContext.getEntityId();

        
        //TODO authentication flags - on peer or on message?
        
        if (contextEntityID != null) {
            log.debug("Attempting to validate SAML protocol message simple signature using context entityID: {}",
                    contextEntityID);
            CriteriaSet criteriaSet = buildCriteriaSet(contextEntityID, messageContext);
            if (validateSignature(signature, signedContent, algorithmURI, criteriaSet, candidateCredentials)) {
                log.info("Validation of request simple signature succeeded");
                if (!peerContext.isAuthenticated()) {
                    log.info("Authentication via request simple signature succeeded for context issuer entity ID {}",
                            contextEntityID);
                    peerContext.setAuthenticated(true);
                }
                return;
            } else {
                log.warn("Validation of request simple signature failed for context issuer: {}", contextEntityID);
                throw new MessageHandlerException("Validation of request simple signature failed for context issuer");
            }
        }
            
        String derivedEntityID = deriveSignerEntityID(messageContext);
        if (derivedEntityID != null) {
            log.debug("Attempting to validate SAML protocol message simple signature using derived entityID: {}",
                    derivedEntityID);
            CriteriaSet criteriaSet = buildCriteriaSet(derivedEntityID, messageContext);
            if (validateSignature(signature, signedContent, algorithmURI, criteriaSet, candidateCredentials)) {
                log.info("Validation of request simple signature succeeded");
                if (!peerContext.isAuthenticated()) {
                    log.info("Authentication via request simple signature succeeded for derived issuer {}",
                            derivedEntityID);
                    peerContext.setEntityId(derivedEntityID);
                    peerContext.setAuthenticated(true);
                }
                return;
            } else {
                log.warn("Validation of request simple signature failed for derived issuer: {}", derivedEntityID);
                throw new MessageHandlerException("Validation of request simple signature failed for derived issuer");
            }
        }
        
        log.warn("Neither context nor derived issuer available, can not attempt SAML simple signature validation");
        throw new MessageHandlerException("No message issuer available, can not attempt simple signature validation");
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
    protected boolean validateSignature(byte[] signature, byte[] signedContent, String algorithmURI,
            CriteriaSet criteriaSet, List<Credential> candidateCredentials) throws MessageHandlerException {

        SignatureTrustEngine engine = getTrustEngine();

        // Some bindings allow candidate signing credentials to be supplied (e.g. via ds:KeyInfo), some do not.
        // So have 2 slightly different cases.
        try {
            if (candidateCredentials == null || candidateCredentials.isEmpty()) {
                if (engine.validate(signature, signedContent, algorithmURI, criteriaSet, null)) {
                    log.debug("Simple signature validation (with no request-derived credentials) was successful");
                    return true;
                } else {
                    log.warn("Simple signature validation (with no request-derived credentials) failed");
                    return false;
                }
            } else {
                for (Credential cred : candidateCredentials) {
                    if (engine.validate(signature, signedContent, algorithmURI, criteriaSet, cred)) {
                        log.debug("Simple signature validation succeeded with a request-derived credential");
                        return true;
                    }
                }
                log.warn("Signature validation using request-derived credentials failed");
                return false;
            }
        } catch (SecurityException e) {
            log.warn("There was an error evaluating the request's simple signature using the trust engine", e);
            throw new MessageHandlerException("Error during trust engine evaluation of the simple signature", e);
        }
    }

    /**
     * Extract any candidate validation credentials from the request and/or message context.
     * 
     * Some bindings allow validataion keys for the simple signature to be supplied, and others do not.
     * 
     * @param request the HTTP servlet request being processed
     * @param messageContext the SAML message context being processed
     * @return a list of candidate validation credentials in the request, or null if none were present
     * @throws MessageHandlerException thrown if there is an error during request processing
     */
    protected List<Credential> getRequestCredentials(HttpServletRequest request, 
            MessageContext<SAMLObject> messageContext) throws MessageHandlerException {
        // This will be specific to the binding and message types, so no default.
        return null;
    }

    /**
     * Extract the signature value from the request, in the form suitable for input into
     * {@link SignatureTrustEngine#validate(byte[], byte[], String, CriteriaSet, Credential)}.
     * 
     * Defaults to the Base64-decoded value of the HTTP request parameter named <code>Signature</code>.
     * 
     * @param request the HTTP servlet request
     * @return the signature value
     * @throws MessageHandlerException thrown if there is an error during request processing
     */
    protected byte[] getSignature(HttpServletRequest request) throws MessageHandlerException {
        String signature = request.getParameter("Signature");
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
     * @param request the HTTP servlet request
     * @return the signature algorithm URI value
     * @throws MessageHandlerException thrown if there is an error during request processing
     */
    protected String getSignatureAlgorithm(HttpServletRequest request) throws MessageHandlerException {
        return request.getParameter("SigAlg");
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
    protected String deriveSignerEntityID(MessageContext<SAMLObject> messageContext) throws MessageHandlerException {
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
    protected CriteriaSet buildCriteriaSet(String entityID, MessageContext<SAMLObject> messageContext)
            throws MessageHandlerException {

        CriteriaSet criteriaSet = new CriteriaSet();
        if (!Strings.isNullOrEmpty(entityID)) {
            criteriaSet.add(new EntityIDCriterion(entityID));
        }
        
        SamlPeerEntityContext peerContext = messageContext.getSubcontext(SamlPeerEntityContext.class);
        Constraint.isNotNull(peerContext, "SamlPeerEntityContext was null");
        Constraint.isNotNull(peerContext.getRole(), "SAML peer role was null");
        
        SamlProtocolContext protocolContext = getSamlProtocolContext(messageContext);
        Constraint.isNotNull(protocolContext, "SamlProtocolContext was null");
        Constraint.isNotNull(protocolContext.getProtocol(), "SAML protocol was null");
        
        MetadataCriterion mdCriteria = new MetadataCriterion(peerContext.getRole(), protocolContext.getProtocol());

        criteriaSet.add(mdCriteria);

        criteriaSet.add(new UsageCriterion(UsageType.SIGNING));

        return criteriaSet;
    }

    /**
     * Get the content over which to validate the signature, in the form suitable for input into
     * {@link SignatureTrustEngine#validate(byte[], byte[], String, CriteriaSet, Credential)}.
     * 
     * @param request the HTTP servlet request being processed
     * @return the signed content extracted from the request, in the format suitable for input to the trust engine.
     * @throws MessageHandlerException thrown if there is an error during request processing
     */
    protected abstract byte[] getSignedContent(HttpServletRequest request) throws MessageHandlerException;

    /**
     * Determine whether the rule should handle the request, based on the unwrapped HTTP servlet request and/or message
     * context.
     * 
     * @param request the HTTP servlet request being processed
     * @param messageContext the SAML message context being processed
     * @return true if the rule should attempt to process the request, otherwise false
     * @throws MessageHandlerException thrown if there is an error during request processing
     */
    protected abstract boolean ruleHandles(HttpServletRequest request, MessageContext<SAMLObject> messageContext)
            throws MessageHandlerException;

}
