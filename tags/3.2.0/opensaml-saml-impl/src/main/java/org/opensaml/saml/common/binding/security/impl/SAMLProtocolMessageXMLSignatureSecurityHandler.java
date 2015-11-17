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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.component.ComponentSupport;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.security.impl.SAMLSignatureProfileValidator;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignaturePrevalidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAML security message handler which validates the signature (if present) on the {@link SAMLObject} which represents
 * the SAML protocol message being processed.
 * 
 * <p>
 * If the message is not an instance of {@link SignableSAMLObject}, then no processing is performed. If signature
 * validation is successful, and the SAML message context issuer was not previously authenticated, then the context's
 * authentication state will be set to <code>true</code>.
 * </p>
 * 
 * <p>
 * If an optional {@link SAMLSignatureProfileValidator} or subclass is supplied, this validator will be used to validate
 * the XML Signature element prior to the actual cryptographic validation of the signature. This might for example be
 * used to enforce certain signature profile requirements or to detect signatures upon which it would be unsafe to
 * attempt cryptographic processing. The validator will default to {@link SAMLSignatureProfileValidator}.
 * </p>
 */
public class SAMLProtocolMessageXMLSignatureSecurityHandler extends BaseSAMLXMLSignatureSecurityHandler {

    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(SAMLProtocolMessageXMLSignatureSecurityHandler.class);

    /** Validator for XML Signature instances. */
    @Nullable private SignaturePrevalidator signaturePrevalidator;

    /**
     * Constructor.
     * 
     * Signature prevalidator defaults to {@link SAMLSignatureProfileValidator}.
     * 
     */
    public SAMLProtocolMessageXMLSignatureSecurityHandler() {
        setSignaturePrevalidator(new SAMLSignatureProfileValidator());
    }

    /**
     * Get the prevalidator for XML Signature instances.
     * 
     * @return Returns the prevalidator.
     */
    @Nullable public SignaturePrevalidator getSignaturePrevalidator() {
        return signaturePrevalidator;
    }

    /**
     * Set the prevalidator for XML Signature instances.
     * 
     * @param validator The prevalidator to set.
     */
    public void setSignaturePrevalidator(@Nullable final SignaturePrevalidator validator) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        signaturePrevalidator = validator;
    }

    /** {@inheritDoc} */
    @Override
    public void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {

        final Object samlMsg = messageContext.getMessage();
        if (!(samlMsg instanceof SignableSAMLObject)) {
            log.debug("{} Extracted SAML message was not a SignableSAMLObject, cannot process signature",
                    getLogPrefix());
            return;
        }
        final SignableSAMLObject signableObject = (SignableSAMLObject) samlMsg;
        if (!signableObject.isSigned()) {
            log.debug("{} SAML protocol message was not signed, skipping XML signature processing", getLogPrefix());
            return;
        }
        final Signature signature = signableObject.getSignature();

        performPrevalidation(signature);

        doEvaluate(signature, signableObject, messageContext);
    }

    /**
     * Perform cryptographic validation and trust evaluation on the Signature token using the configured Signature
     * trust engine.
     * 
     * @param signature the signature which is being evaluated
     * @param signableObject the signable object which contained the signature
     * @param messageContext the SAML message context being processed
     * @throws MessageHandlerException thrown if the signature fails validation
     */
    protected void doEvaluate(@Nonnull final Signature signature, @Nonnull final SignableSAMLObject signableObject, 
            @Nonnull final MessageContext messageContext) throws MessageHandlerException {

        //TODO authentication flags - on peer or on message?
        
        final SAMLPeerEntityContext peerContext = getSAMLPeerEntityContext();
        if (peerContext.getEntityId() != null) {
            final String contextEntityID = peerContext.getEntityId();
            final String msgType = signableObject.getElementQName().toString();
            log.debug("{} Attempting to verify signature on signed SAML protocol message type: {}",
                    getLogPrefix(), msgType);
            
            if (evaluate(signature, contextEntityID, messageContext)) {
                log.info("{} Validation of protocol message signature succeeded, message type: {}",
                        getLogPrefix(), msgType);
                if (!peerContext.isAuthenticated()) {
                    log.debug("{} Authentication via protocol message signature succeeded for "
                            + "context issuer entity ID {}", getLogPrefix(), contextEntityID);
                    peerContext.setAuthenticated(true);
                }
            } else {
                log.debug(
                        "{} Validation of protocol message signature failed for context issuer '{}', message type: {}",
                        getLogPrefix(), contextEntityID, msgType);
                throw new MessageHandlerException("Validation of protocol message signature failed");
            }
        } else {
            log.debug("{} Context issuer unavailable, cannot attempt SAML protocol message signature validation",
                    getLogPrefix());
            throw new MessageHandlerException("Context issuer unavailable, cannot validate signature");
        }
    }

    /**
     * Perform pre-validation on the Signature token.
     * 
     * @param signature the signature to evaluate
     * @throws MessageHandlerException thrown if the signature element fails pre-validation
     */
    protected void performPrevalidation(@Nonnull final Signature signature) throws MessageHandlerException {
        if (getSignaturePrevalidator() != null) {
            try {
                getSignaturePrevalidator().validate(signature);
            } catch (final SignatureException e) {
                log.debug("{} Protocol message signature failed signature pre-validation", getLogPrefix(), e);
                throw new MessageHandlerException("Protocol message signature failed signature pre-validation", e);
            }
        }
    }
    
}