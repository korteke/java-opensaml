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

package org.opensaml.saml.common.binding;

import java.net.URI;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for outbound SAML protocol messages which adds the destination endpoint URL as the 'recipient'
 * attribute to SAML 1 {@link org.opensaml.saml.saml1.core.ResponseAbstractType} messages, or as the
 * 'destination' attribute to {@link org.opensaml.saml.saml2.core.RequestAbstractType} and 
 * {@link org.opensaml.saml.saml2.core.StatusResponseType} messages.
 */
public class SAMLOutboundDestinationHandler extends AbstractMessageHandler<SAMLObject> {
    
    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(SAMLOutboundDestinationHandler.class);

    /** {@inheritDoc} */
    @Override
    protected void doInvoke(@Nonnull final MessageContext<SAMLObject> messageContext) throws MessageHandlerException {
        SAMLObject samlMessage = Constraint.isNotNull(messageContext.getMessage(), 
                "SAML message was not present in message context");
        
        try {
            URI endpointURI = SAMLBindingSupport.getEndpointURL(messageContext);
            String endpointURL = endpointURI.toString();
            
            if (samlMessage instanceof org.opensaml.saml.saml1.core.ResponseAbstractType) {
                log.debug("Adding recipient to outbound SAML 1 protocol message: {}", endpointURL);
                SAMLBindingSupport.setSAML1ResponseRecipient(samlMessage, endpointURL);
            } else if (samlMessage instanceof org.opensaml.saml.saml2.core.RequestAbstractType
                    || samlMessage instanceof org.opensaml.saml.saml2.core.StatusResponseType) {
                log.debug("Adding destination to outbound SAML 2 protocol message: {}", endpointURL);
                SAMLBindingSupport.setSAML2Destination(samlMessage, endpointURL);
            }
            
        } catch (BindingException e) {
            throw new MessageHandlerException("Could not obtain SAML destination endpoint URL from message context", e);
        }
        
    }

}