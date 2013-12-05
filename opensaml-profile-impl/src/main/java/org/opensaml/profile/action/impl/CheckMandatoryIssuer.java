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
import javax.annotation.Nullable;

import org.opensaml.profile.ProfileException;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;

import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.messaging.context.BasicMessageMetadataContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.ChildContextLookup;

import com.google.common.base.Function;

/**
 * Action that checks that an incoming message has an issuer.
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_MSG_MD}
 */
public final class CheckMandatoryIssuer extends AbstractProfileAction {

    /**
     * Strategy used to look up the {@link BasicMessageMetadataContext} associated with the inbound message context.
     */
    @Nonnull private Function<MessageContext, BasicMessageMetadataContext> messageMetadataContextLookupStrategy;

    /** The context to check. */
    @Nullable private BasicMessageMetadataContext msgContext;
    
    /**
     * Constructor.
     * 
     * Initializes {@link #messageMetadataContextLookupStrategy} to {@link ChildContextLookup}.
     */
    public CheckMandatoryIssuer() {
        messageMetadataContextLookupStrategy = new ChildContextLookup<>(BasicMessageMetadataContext.class, false);
    }

    /**
     * Set the strategy used to look up the {@link BasicMessageMetadataContext} associated with the inbound message
     * context.
     * 
     * @param strategy strategy used to look up the {@link BasicMessageMetadataContext} associated with the inbound
     *            message context
     */
    public void setMessageMetadataContextLookupStrategy(
            @Nonnull final Function<MessageContext, BasicMessageMetadataContext> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        messageMetadataContextLookupStrategy = Constraint.isNotNull(strategy,
                "Message metadata context lookup strategy cannot be null");
    }

    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) throws ProfileException {

        msgContext = messageMetadataContextLookupStrategy.apply(profileRequestContext.getInboundMessageContext());
        return msgContext != null;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) throws ProfileException {

        if (msgContext.getMessageIssuer() == null) {
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_MD);
        }
    }
    
}