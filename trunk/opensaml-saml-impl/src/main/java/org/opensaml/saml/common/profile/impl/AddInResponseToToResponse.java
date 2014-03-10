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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.profile.ProfileException;
import org.opensaml.profile.action.AbstractConditionalProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.MessageLookup;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.messaging.context.SAMLMessageInfoContext;
import org.opensaml.saml.saml1.core.ResponseAbstractType;
import org.opensaml.saml.saml2.core.StatusResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Sets;

/**
 * Action that adds the <code>InResponseTo</code> attribute to a response message if a SAML message ID is set on
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
    @Nonnull private Function<ProfileRequestContext,SAMLObject> responseLookupStrategy;
    
    /** Strategy used to locate request ID to correlate. */
    @Nonnull private Function<ProfileRequestContext,String> requestIdLookupStrategy;
    
    /** Message to modify. */
    @Nullable private SAMLObject response;
    
    /** Request ID to populate from. */
    @Nullable private String requestId;
    
    /** Constructor. */
    public AddInResponseToToResponse() {
        responseLookupStrategy =
                Functions.compose(new MessageLookup<>(SAMLObject.class), new OutboundMessageContextLookup());
        requestIdLookupStrategy = new DefaultRequestIdLookupStrategy();
    }
    
    /**
     * Set the strategy used to locate the message to operate on.
     * 
     * @param strategy strategy used to locate the message to operate on
     */
    public synchronized void setResponseLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,SAMLObject> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        responseLookupStrategy = Constraint.isNotNull(strategy, "Response lookup strategy cannot be null");
    }
    
    /**
     * Set the strategy used to locate the request ID.
     * 
     * @param strategy lookup strategy
     */
    public synchronized void setRequestIdLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,String> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        requestIdLookupStrategy = Constraint.isNotNull(strategy, "Request ID lookup strategy cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) throws ProfileException {
        log.debug("{} Attempting to add InResponseTo to outgoing Response", getLogPrefix());

        response = responseLookupStrategy.apply(profileRequestContext);
        if (response == null) {
            log.debug("{} No SAML message located in current profile request context", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        }

        requestId = requestIdLookupStrategy.apply(profileRequestContext);
        if (requestId == null) {
            log.debug("{} No request ID, nothing to do", getLogPrefix());
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
     * Default lookup of request ID from inbound message context, suppressing lookup for bindings
     * known to be supplying artificial IDs.
     */
    public static class DefaultRequestIdLookupStrategy implements Function<ProfileRequestContext,String> {

        /** Class logger. */
        @Nonnull private final Logger log = LoggerFactory.getLogger(AddInResponseToToResponse.class);
        
        /** Set of bindings to ignore request ID for. */
        @Nonnull @NonnullElements private Set<String> suppressForBindings;
        
        /** Constructor. */
        public DefaultRequestIdLookupStrategy() {
            suppressForBindings = Collections.emptySet();
        }
        
        /**
         * Set the collection of bindings to suppress the lookup of a request ID for.
         * 
         * @param bindings collection of bindings
         */
        public void setSuppressForBindings(@Nonnull @NonnullElements Collection<String> bindings) {
            Constraint.isNotNull(bindings, "Bindings collection cannot be null");
            
            suppressForBindings = Sets.newHashSet();
            for (final String b : bindings) {
                final String trimmed = StringSupport.trimOrNull(b);
                if (trimmed != null) {
                    suppressForBindings.add(trimmed);
                }
            }
        }
        
        /** {@inheritDoc} */
        @Override
        @Nullable public String apply(@Nullable final ProfileRequestContext input) {
            final MessageContext inMsgCtx = input.getInboundMessageContext();
            if (inMsgCtx == null) {
                log.debug("No inbound message context available");
                return null;
            }

            if (!suppressForBindings.isEmpty()) {
                final SAMLBindingContext bindingCtx = inMsgCtx.getSubcontext(SAMLBindingContext.class);
                if (bindingCtx != null && bindingCtx.getBindingUri() != null
                        && suppressForBindings.contains(bindingCtx.getBindingUri())) {
                    log.debug("Inbound binding {} is suppressed, ignoring request ID",
                            bindingCtx.getBindingUri());
                    return null;
                }
            }
            
            final SAMLMessageInfoContext infoCtx = inMsgCtx.getSubcontext(SAMLMessageInfoContext.class);
            if (infoCtx == null) {
                log.debug("No inbound SAMLMessageInfoContext available");
                return null;
            }

            return infoCtx.getMessageId();
        }
    }
    
}