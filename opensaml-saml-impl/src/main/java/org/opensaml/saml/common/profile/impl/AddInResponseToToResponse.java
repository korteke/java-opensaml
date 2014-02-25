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

package org.opensaml.saml.common.profile.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.profile.ProfileException;
import org.opensaml.profile.action.AbstractConditionalProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;

import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.messaging.context.BasicMessageMetadataContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.MessageLookup;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml1.core.Response;
import org.opensaml.saml.saml1.core.ResponseAbstractType;
import org.opensaml.saml.saml2.core.StatusResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Functions;

/**
 * Action that adds the <code>InResponseTo</code> attribute to a response message if a message ID is set on
 * the inbound message context.
 * 
 * <p>Supports all of the abstract types in SAML that carry this attribute.</p>
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_MSG_CTX}
 */
public class AddInResponseToToResponse extends AbstractConditionalProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AddInResponseToToResponse.class);

    /** Strategy used to locate the message to operate on. */
    @Nonnull private Function<ProfileRequestContext, SAMLObject> responseLookupStrategy;
    
    /** Message to modify. */
    @Nullable private SAMLObject response;
    
    /** Request ID to populate from. */
    @Nullable private String requestId;
    
    /** Constructor. */
    public AddInResponseToToResponse() {
        responseLookupStrategy =
                Functions.compose(new MessageLookup<>(SAMLObject.class), new OutboundMessageContextLookup());
    }
    
    /**
     * Set the strategy used to locate the message to operate on.
     * 
     * @param strategy strategy used to locate the message to operate on
     */
    public synchronized void setResponseLookupStrategy(
            @Nonnull final Function<ProfileRequestContext, SAMLObject> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        responseLookupStrategy = Constraint.isNotNull(strategy, "Response lookup strategy cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) throws ProfileException {
        log.debug("{} Attempting to add InResponseTo to outgoing Response", getLogPrefix());

        requestId = getInboundMessageId(profileRequestContext);
        if (requestId == null) {
            log.debug("{} Inbound message did not have an ID, nothing to do", getLogPrefix());
            return false;
        }
        
        response = responseLookupStrategy.apply(profileRequestContext);
        if (response == null) {
            log.debug("{} No SAML message located in current profile request context", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        }
        
        return super.doPreExecute(profileRequestContext);
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext)
            throws ProfileException {

        if (response instanceof ResponseAbstractType) {
            ((ResponseAbstractType) response).setInResponseTo(requestId);
        } else if (response instanceof StatusResponseType) {
            ((StatusResponseType) response).setInResponseTo(requestId);
        } else {
            log.debug("{} Message type {} is not supported", getLogPrefix(), response.getElementQName());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
        }
    }

    /**
     * Get the ID of the inbound message.
     * 
     * @param profileRequestContext current profile request context
     * 
     * @return the inbound message ID or null if the was no ID
     */
    @Nullable private String getInboundMessageId(final ProfileRequestContext<Object, Response> profileRequestContext) {
        final MessageContext inMsgCtx = profileRequestContext.getInboundMessageContext();
        if (inMsgCtx == null) {
            log.debug("{} No inbound message context available", getLogPrefix());
            return null;
        }

        final BasicMessageMetadataContext inMsgMetadataCtx = inMsgCtx.getSubcontext(BasicMessageMetadataContext.class);
        if (inMsgMetadataCtx == null) {
            log.debug("{} No inbound message metadata context available", getLogPrefix());
            return null;
        }

        return inMsgMetadataCtx.getMessageId();
    }
    
}