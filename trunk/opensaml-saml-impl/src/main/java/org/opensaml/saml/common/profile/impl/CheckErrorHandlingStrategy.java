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

import org.opensaml.profile.action.AbstractConditionalProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ErrorEventContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Sets;

/**
 * Action that decides whether to handle an error by returning a SAML response to a requester
 * or fail locally.
 * 
 * <p>This is principally determined based on whether or not the necessary message context children
 * are present so that a response can be delivered, but is also tunable based on the error event
 * being handled.</p>
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#TRAP_ERROR}
 */
public class CheckErrorHandlingStrategy extends AbstractConditionalProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(CheckErrorHandlingStrategy.class);
    
    /** Strategy function for access to {@link SAMLBindingContext} to check. */
    @Nonnull private Function<ProfileRequestContext,SAMLBindingContext> bindingContextLookupStrategy;

    /** Strategy function for access to {@link SAMLEndpointContext} to check. */
    @Nonnull private Function<ProfileRequestContext,SAMLEndpointContext> endpointContextLookupStrategy;

    /** Strategy function for access to {@link ErrorEventContext} to check. */
    @Nonnull private Function<ProfileRequestContext,ErrorEventContext> errorEventContextLookupStrategy;
    
    /** Error events to handle locally, even if possible to do so with a response. */
    @Nonnull @NonnullElements private Set<String> localEvents;
    
    /**
     * Constructor.
     * 
     * Initializes {@link #messageMetadataContextLookupStrategy} to {@link ChildContextLookup}.
     */
    public CheckErrorHandlingStrategy() {
        // Default: outbound msg context -> SAMLBindingContext
        bindingContextLookupStrategy = Functions.compose(
                new ChildContextLookup<>(SAMLBindingContext.class), new OutboundMessageContextLookup());
        
        // Default: outbound msg context -> SAMLPeerEntityContext -> SAMLEndpointContext
        endpointContextLookupStrategy = Functions.compose(
                new ChildContextLookup<>(SAMLEndpointContext.class),
                Functions.compose(new ChildContextLookup<>(SAMLPeerEntityContext.class),
                        new OutboundMessageContextLookup()));
        
        errorEventContextLookupStrategy = new ChildContextLookup<>(ErrorEventContext.class);
        
        localEvents = Collections.emptySet();
    }
    
    /**
     * Set lookup strategy for {@link SAMLBindingContext} to check.
     * 
     * @param strategy  lookup strategy
     */
    public void setBindingContextLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,SAMLBindingContext> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        bindingContextLookupStrategy = Constraint.isNotNull(strategy,
                "SAMLBindingContext lookup strategy cannot be null");
    }

    /**
     * Set lookup strategy for {@link SAMLEndpointContext} to check.
     * 
     * @param strategy  lookup strategy
     */
    public void setEndpointContextLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,SAMLEndpointContext> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        endpointContextLookupStrategy = Constraint.isNotNull(strategy,
                "SAMLEndpointContext lookup strategy cannot be null");
    }

    /**
     * Set lookup strategy for {@link ErrorEventContext} to check.
     * 
     * @param strategy  lookup strategy
     */
    public void setErrorEventContextLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,ErrorEventContext> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        errorEventContextLookupStrategy = Constraint.isNotNull(strategy,
                "ErrorEventContext lookup strategy cannot be null");
    }
    
    /**
     * Set the events to handle locally.
     * 
     * @param events locally handled events
     */
    public void setLocalEvents(@Nonnull @NonnullElements Collection<String> events) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        Constraint.isNotNull(events, "Event collection cannot be null");
        
        localEvents = Sets.newHashSetWithExpectedSize(events.size());
        for (final String e : events) {
            final String trimmed = StringSupport.trimOrNull(e);
            if (trimmed != null) {
                localEvents.add(trimmed);
            }
        }
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        final SAMLBindingContext bindingCtx = bindingContextLookupStrategy.apply(profileRequestContext);
        if (bindingCtx == null || bindingCtx.getBindingUri() == null) {
            log.debug("{} No SAMLBindingContext or binding URI available, error must be handled locally",
                    getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.TRAP_ERROR);
            return false;
        }
        
        final SAMLEndpointContext endpointCtx = endpointContextLookupStrategy.apply(profileRequestContext);
        if (endpointCtx == null || endpointCtx.getEndpoint() == null ||
                (endpointCtx.getEndpoint().getLocation() == null
                    && endpointCtx.getEndpoint().getResponseLocation() == null)) {
            log.debug("{} No SAMLEndpointContext or endpoint location available, error must be handled locally",
                    getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.TRAP_ERROR);
            return false;
        }
        
        return super.doPreExecute(profileRequestContext);
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {

        final ErrorEventContext errorEventCtx = errorEventContextLookupStrategy.apply(profileRequestContext);
        if (errorEventCtx == null || errorEventCtx.getEvent() == null) {
            log.debug("{} No event found, assuming error handled with response", getLogPrefix());
            return;
        }
        
        final String event = errorEventCtx.getEvent().toString();
        if (localEvents.contains(event)) {
            log.debug("{} Error event {} will be handled locally", getLogPrefix(), event);
            ActionSupport.buildEvent(profileRequestContext, EventIds.TRAP_ERROR);
        } else {
            log.debug("{} Error event {} will be handled with response", getLogPrefix(), event);
        }
    }
    
}