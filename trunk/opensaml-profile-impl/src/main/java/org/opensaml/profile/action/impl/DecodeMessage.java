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

package org.opensaml.profile.action.impl;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecoder;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.profile.ProfileException;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action that decodes an incoming request into a {@link MessageContext}.
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#UNABLE_TO_DECODE}
 * 
 * @post If decode succeeds, ProfileRequestContext.getInboundMessageContext() != null
 * @post The injected {@link MessageDecoder} is destroyed.
 */
public class DecodeMessage extends AbstractProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(DecodeMessage.class);

    /** The {@link MessageDecoder} instance used to decode the incoming message. */
    @Nonnull private final MessageDecoder decoder;

    /**
     * Constructor.
     * 
     * @param messageDecoder the {@link MessageDecoder} used for the incoming request
     */
    public DecodeMessage(@Nonnull final MessageDecoder messageDecoder) {
        decoder = Constraint.isNotNull(messageDecoder, "MessageDecoder cannot be null");
    }

    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) throws ProfileException {
        try {
            log.debug("{} Decoding message using message decoder of type {} for this request", getLogPrefix(),
                    decoder.getClass().getName());
            decoder.decode();
            final MessageContext msgContext = decoder.getMessageContext();
            log.debug("{} Incoming request decoded into a message of type {}", getLogPrefix(), 
                    msgContext.getMessage().getClass().getName());

            profileRequestContext.setInboundMessageContext(msgContext);
        } catch (MessageDecodingException e) {
            log.error(getLogPrefix() + " Unable to decode incoming request", e);
            ActionSupport.buildEvent(profileRequestContext, EventIds.UNABLE_TO_DECODE);
        } finally {
            // TODO: should we actually destroy the MessageDecoder here?
            decoder.destroy();
        }
    }
    
}