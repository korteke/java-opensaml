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

package org.opensaml.saml.saml2.binding.security.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.messaging.context.ChannelBindingsContext;
import org.opensaml.saml.ext.saml2cb.ChannelBindings;
import org.opensaml.saml.saml2.core.Extensions;
import org.opensaml.saml.saml2.core.RequestAbstractType;
import org.opensaml.saml.saml2.core.StatusResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MessageHandler to process {@link ChannelBindings} extensions in an incoming SAML message
 * and save them to a {@link ChannelBindingsContext} underneath the {@link MessageContext}.
 */
public class ExtractChannelBindingsExtensionsHandler extends AbstractMessageHandler {
   
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(ExtractChannelBindingsExtensionsHandler.class);

    /** {@inheritDoc} */
    @Override
    protected boolean doPreInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        if (!super.doPreInvoke(messageContext) || messageContext.getMessage() == null) {
            return false;
        } else if (!SAMLBindingSupport.isMessageSigned(messageContext)) {
            log.debug("Message was not signed, cannot extract ChannelBindings from it");
            return false;
        }
        
        return true;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {

        Extensions extensions = null;
        if (messageContext.getMessage() instanceof RequestAbstractType) {
            extensions = ((RequestAbstractType) messageContext.getMessage()).getExtensions();
        } else if (messageContext.getMessage() instanceof StatusResponseType) {
            extensions = ((StatusResponseType) messageContext.getMessage()).getExtensions();
        } else {
            log.debug("{} Message was not of a supported type", getLogPrefix());
            return;
        }
        
        final List<XMLObject> bindings = extensions != null
                ? extensions.getUnknownXMLObjects(ChannelBindings.DEFAULT_ELEMENT_NAME)
                        : Collections.<XMLObject>emptyList();
        if (bindings.isEmpty()) {
            log.debug("{} Message did not contain any ChannelBindings extensions", getLogPrefix());
            return;
        }

        final Collection<ChannelBindings> channelBindings =
                messageContext.getSubcontext(ChannelBindingsContext.class, true).getChannelBindings();
        for (final XMLObject cb : bindings) {
            if (cb instanceof ChannelBindings) {
                channelBindings.add((ChannelBindings) cb);
            }
        }
        
        log.debug("{} {} ChannelBindings extension(s) found", getLogPrefix(), channelBindings.size());
    }
    
}