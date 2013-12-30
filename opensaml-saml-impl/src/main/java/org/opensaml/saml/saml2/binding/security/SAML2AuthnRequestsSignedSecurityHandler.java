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

package org.opensaml.saml.saml2.binding.security;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * Message handler implementation that enforces the AuthnRequestsSigned flag of 
 * SAML 2 metadata element @{link {@link SPSSODescriptor}.
 */
public class SAML2AuthnRequestsSignedSecurityHandler extends AbstractMessageHandler<SAMLObject>{
    
    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(SAML2AuthnRequestsSignedSecurityHandler.class);

    /** {@inheritDoc} */
    public void doInvoke(MessageContext<SAMLObject> messageContext) throws MessageHandlerException {
        SAMLObject samlMessage = messageContext.getMessage();
        if (! (samlMessage instanceof AuthnRequest) ) {
            log.debug("Inbound message is not an instance of AuthnRequest, skipping evaluation...");
            return;
        }
        
        SAMLPeerEntityContext peerContext = messageContext.getSubcontext(SAMLPeerEntityContext.class, true);
        if (peerContext == null || Strings.isNullOrEmpty(peerContext.getEntityId())) {
            log.warn("SAML peer entityID was not available, unable to evaluate rule");
            return;
        }
        String messageIssuer = peerContext.getEntityId();
        
        SAMLMetadataContext metadataContext = peerContext.getSubcontext(SAMLMetadataContext.class, false);
        if (metadataContext == null || metadataContext.getRoleDescriptor() == null) {
            log.warn("SAMLPeerContext did not contain either a SAMLMetadataContext or a RoleDescriptor, " 
                    + "unable to evaluate rule");
            return;
        }
        
        if (!(metadataContext.getRoleDescriptor() instanceof SPSSODescriptor)) {
            log.warn("RoleDescriptor was not an SPSSODescriptor, it was a {}. Unable to evaluate rule", 
                    metadataContext.getRoleDescriptor().getClass().getName());
            return;
        }
        
        SPSSODescriptor spssoRole = (SPSSODescriptor) metadataContext.getRoleDescriptor();
        
        if (spssoRole.isAuthnRequestsSigned() == Boolean.TRUE) {
            if (! isMessageSigned(messageContext)) {
                log.error("SPSSODescriptor for entity ID '{}' indicates AuthnRequests must be signed, "
                        + "but inbound message was not signed", messageIssuer);
                throw new MessageHandlerException("Inbound AuthnRequest was required to be signed but was not");
            }
        } else {
            log.debug("SPSSODescriptor for entity ID '{}' does not require AuthnRequests to be signed", messageIssuer);
        }

    }
    
    /**
     * Determine whether the inbound message is signed.
     * 
     * @param messageContext the message context being evaluated
     * @return true if the inbound message is signed, otherwise false
     */
    protected boolean isMessageSigned(MessageContext<SAMLObject> messageContext) {
        return SAMLBindingSupport.isMessageSigned(messageContext);
    }

}
