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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.component.ComponentSupport;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.messaging.context.ChannelBindingsContext;
import org.opensaml.saml.ext.saml2cb.ChannelBindings;
import org.opensaml.soap.messaging.SOAPMessagingSupport;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.ActorBearing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MessageHandler to process {@link ChannelBindings} SOAP header blocks in an incoming SOAP envelope
 * and save them to a {@link ChannelBindingsContext} underneath the {@link SOAP11Context}.
 */
public class ExtractChannelBindingsHeadersHandler extends AbstractMessageHandler {
   
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(ExtractChannelBindingsHeadersHandler.class);
    
    /** Include header blocks targeted at the final destination node? */
    private boolean finalDestination;
    
    /** Include header blocks targeted at the next destination node? */
    private boolean nextDestination;
    
    /** Constructor. */
    public ExtractChannelBindingsHeadersHandler() {
        nextDestination = true;
    }
    
    /**
     * Set whether to include header blocks targeted at the final destination node (defaults to false).
     * 
     * @param flag flag to set
     */
    public void setFinalDestination(final boolean flag) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        finalDestination = flag;
    }

    /**
     * Set whether to include header blocks targeted at the next destination node (defaults to true).
     * 
     * @param flag flag to set
     */
    public void setNextDestination(final boolean flag) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        nextDestination = flag;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        
        final Collection<ChannelBindings> channelBindings = new ArrayList<>();
        
        final List<XMLObject> headers = SOAPMessagingSupport.getHeaderBlock(messageContext,
                ChannelBindings.DEFAULT_ELEMENT_NAME, null, finalDestination);
        for (final XMLObject header : headers) {
            if (header instanceof ChannelBindings) {
                if (null == ((ActorBearing) header).getSOAP11Actor() || nextDestination) {
                    channelBindings.add((ChannelBindings) header);
                }
            }
        }
        
        if (channelBindings.isEmpty()) {
            log.debug("{} No ChannelBindings header blocks found", getLogPrefix());
        } else {
            log.debug("{} {} ChannelBindings header block(s) found", getLogPrefix(), channelBindings.size());
            messageContext.getSubcontext(SOAP11Context.class).getSubcontext(ChannelBindingsContext.class,
                    true).getChannelBindings().addAll(channelBindings);
        }
    }
    
}