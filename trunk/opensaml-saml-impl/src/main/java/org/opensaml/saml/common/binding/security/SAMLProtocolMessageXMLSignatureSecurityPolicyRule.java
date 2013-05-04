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

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.saml.common.messaging.context.SamlPeerEntityContext;
import org.opensaml.saml.security.SAMLSignatureProfileValidator;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAML security policy rule which validates the signature (if present) on the {@link SAMLObject} which represents the
 * SAML protocol message being processed.
 * 
 * <p>
 * If the message is not an instance of {@link SignableSAMLObject}, then no processing is performed. If signature
 * validation is successful, and the SAML message context issuer was not previously authenticated, then the context's
 * issuer authentication state will be set to <code>true</code>.
 * </p>
 * 
 * <p>
 * If an optional {@link Validator} for {@link Signature} objects is supplied, this validator will be used to validate
 * the XML Signature element prior to the actual cryptographic validation of the signature. This might for example be
 * used to enforce certain signature profile requirements or to detect signatures upon which it would be unsafe to
 * attempt cryptographic processing. When using the single argument constructuor form, the validator will default to
 * {@link SAMLSignatureProfileValidator}.
 * </p>
 */
public class SAMLProtocolMessageXMLSignatureSecurityPolicyRule extends BaseSAMLXMLSignatureSecurityPolicyRule {

    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(SAMLProtocolMessageXMLSignatureSecurityPolicyRule.class);

    //TODO decide whether this should be an interface with an impl
    /** Validator for XML Signature instances. */
    private SAMLSignatureProfileValidator sigValidator;

    /**
     * Constructor.
     * 
     * Signature pre-validator defaults to {@link SAMLSignatureProfileValidator}.
     * 
     */
    public SAMLProtocolMessageXMLSignatureSecurityPolicyRule() {
        setSigValidator(new SAMLSignatureProfileValidator());
    }

    /**
     * Get the validator for XML Signature instances.
     * 
     * @return Returns the sigValidator.
     */
    public SAMLSignatureProfileValidator getSigValidator() {
        return sigValidator;
    }

    /**
     * Set the validator for XML Signature instances.
     * 
     * @param validator The sigValidator to set.
     */
    public void setSigValidator(SAMLSignatureProfileValidator validator) {
        sigValidator = validator;
    }

    /** {@inheritDoc} */
    public void doInvoke(MessageContext<SAMLObject> messageContext) throws MessageHandlerException {

        SAMLObject samlMsg = messageContext.getMessage();
        if (!(samlMsg instanceof SignableSAMLObject)) {
            log.debug("Extracted SAML message was not a SignableSAMLObject, can not process signature");
            return;
        }
        SignableSAMLObject signableObject = (SignableSAMLObject) samlMsg;
        if (!signableObject.isSigned()) {
            log.info("SAML protocol message was not signed, skipping XML signature processing");
            return;
        }
        Signature signature = signableObject.getSignature();

        performPreValidation(signature);

        doEvaluate(signature, signableObject, messageContext);
    }

    /**
     * Perform cryptographic validation and trust evaluation on the Signature token using the configured Signature trust
     * engine.
     * 
     * @param signature the signature which is being evaluated
     * @param signableObject the signable object which contained the signature
     * @param messageContext the SAML message context being processed
     * @throws MessageHandlerException thrown if the signature fails validation
     */
    protected void doEvaluate(Signature signature, SignableSAMLObject signableObject, 
            MessageContext<SAMLObject> messageContext) throws MessageHandlerException {

        //TODO authentication flags - on peer or on message?
        
        SamlPeerEntityContext peerContext = messageContext.getSubcontext(SamlPeerEntityContext.class, false);
        if (peerContext != null && peerContext.getEntityId() != null) {
            String contextEntityID = peerContext.getEntityId();
            String msgType = signableObject.getElementQName().toString();
            log.debug("Attempting to verify signature on signed SAML protocol message type: {}", msgType);
            
            if (evaluate(signature, contextEntityID, messageContext)) {
                log.info("Validation of protocol message signature succeeded, message type: {}", msgType);
                if (!peerContext.isAuthenticated()) {
                    log.debug("Authentication via protocol message signature succeeded for context issuer entity ID {}",
                            contextEntityID);
                    peerContext.setAuthenticated(true);
                }
            } else {
                log.debug("Validation of protocol message signature failed for context issuer '" + contextEntityID
                        + "', message type: " + msgType);
                throw new MessageHandlerException("Validation of protocol message signature failed");
            }
        } else {
            log.debug("Context issuer unavailable, can not attempt SAML protocol message signature validation");
            throw new MessageHandlerException("Context issuer unavailable, can not validate signature");
        }
    }

    /**
     * Get the validator used to perform pre-validation on Signature tokens.
     * 
     * @return the configured Signature validator, or null
     */
    protected SAMLSignatureProfileValidator getSignaturePrevalidator() {
        return getSigValidator();
    }

    /**
     * Perform pre-validation on the Signature token.
     * 
     * @param signature the signature to evaluate
     * @throws MessageHandlerException thrown if the signature element fails pre-validation
     */
    protected void performPreValidation(Signature signature) throws MessageHandlerException {
        if (getSignaturePrevalidator() != null) {
            try {
                getSignaturePrevalidator().validate(signature);
            } catch (SignatureException e) {
                log.debug("Protocol message signature failed signature pre-validation", e);
                throw new MessageHandlerException("Protocol message signature failed signature pre-validation", e);
            }
        }
    }
}