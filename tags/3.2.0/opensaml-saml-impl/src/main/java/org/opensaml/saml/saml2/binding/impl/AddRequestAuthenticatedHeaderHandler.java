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

package org.opensaml.saml.saml2.binding.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.messaging.context.ECPContext;
import org.opensaml.saml.saml2.ecp.RequestAuthenticated;
import org.opensaml.soap.messaging.SOAPMessagingSupport;
import org.opensaml.soap.soap11.ActorBearing;
import org.opensaml.soap.util.SOAPSupport;

/**
 * MessageHandler to add the ECP {@link RequestAuthenticated} header to an outgoing SOAP envelope.
 */
public class AddRequestAuthenticatedHeaderHandler extends AbstractMessageHandler {

    /** {@inheritDoc} */
    @Override
    protected boolean doPreInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        
        if (!super.doPreInvoke(messageContext)) {
            return false;
        }
        
        final ECPContext ctx = messageContext.getSubcontext(ECPContext.class);
        if (ctx == null || !ctx.isRequestAuthenticated()) {
            return false;
        }
        
        return true;
    }

    /** {@inheritDoc} */
    @Override
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        
        final SAMLObjectBuilder<RequestAuthenticated> builder = (SAMLObjectBuilder<RequestAuthenticated>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<RequestAuthenticated>getBuilderOrThrow(
                        RequestAuthenticated.DEFAULT_ELEMENT_NAME);
        
        final RequestAuthenticated header = builder.buildObject();
        
        SOAPSupport.addSOAP11ActorAttribute(header, ActorBearing.SOAP11_ACTOR_NEXT);
        
        try {
            SOAPMessagingSupport.addHeaderBlock(messageContext, header);
        } catch (final Exception e) {
            throw new MessageHandlerException(e);
        }
    }
    
}