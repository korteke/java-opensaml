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

package org.opensaml.saml.saml2.profile.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.messaging.context.navigate.MessageLookup;
import org.opensaml.profile.action.AbstractConditionalProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.messaging.context.ChannelBindingsContext;
import org.opensaml.saml.ext.saml2cb.ChannelBindings;
import org.opensaml.saml.saml2.core.Advice;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.profile.SAML2ActionSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Functions;

/**
 * Action to add {@link ChannelBindings} extensions to every {@link Assertion} in a {@link Response} message.
 * If the containing {@link Advice} is not present, it will be created.
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_MSG_CTX}
 */
public class AddChannelBindingsToAssertions extends AbstractConditionalProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AddChannelBindingsToAssertions.class);

    /** Strategy used to locate the {@link ChannelBindingsContext} to operate on. */
    @Nonnull private Function<ProfileRequestContext,ChannelBindingsContext> channelBindingsContextLookupStrategy;
    
    /** Strategy used to locate the {@link Response} to operate on. */
    @Nonnull private Function<ProfileRequestContext,Response> responseLookupStrategy;

    /** ChannelBindingsContext to read from. */
    @Nullable private ChannelBindingsContext channelBindingsContext;
    
    /** Response to modify. */
    @Nullable private Response response;

    /** Constructor. */
    public AddChannelBindingsToAssertions() {
        channelBindingsContextLookupStrategy = new ChildContextLookup<>(ChannelBindingsContext.class);
        responseLookupStrategy =
                Functions.compose(new MessageLookup<>(Response.class), new OutboundMessageContextLookup());
    }

    /**
     * Set the strategy used to locate the {@link ChannelBindingsContext} to operate on.
     * 
     * @param strategy lookup strategy
     */
    public void setChannelBindingsContextLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,ChannelBindingsContext> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        channelBindingsContextLookupStrategy = Constraint.isNotNull(strategy,
                "ChannelBindingsContext lookup strategy cannot be null");
    }
    
    /**
     * Set the strategy used to locate the {@link Response} to operate on.
     * 
     * @param strategy lookup strategy
     */
    public void setResponseLookupStrategy(@Nonnull final Function<ProfileRequestContext,Response> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        responseLookupStrategy = Constraint.isNotNull(strategy, "Response lookup strategy cannot be null");
    }

    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        if (!super.doPreExecute(profileRequestContext)) {
            return false;
        }
        
        channelBindingsContext = channelBindingsContextLookupStrategy.apply(profileRequestContext);
        if (channelBindingsContext == null || channelBindingsContext.getChannelBindings().isEmpty()) {
            log.debug("{} No ChannelBindings to add, nothing to do", getLogPrefix());
            return false;
        }
        
        log.debug("{} Attempting to add ChannelBindings to every Assertion in Response", getLogPrefix());

        response = responseLookupStrategy.apply(profileRequestContext);
        if (response == null) {
            log.debug("{} No SAML response located in current profile request context", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        } else if (response.getAssertions().isEmpty()) {
            log.debug("{} No assertions in response message, nothing to do", getLogPrefix());
            return false;
        }
        
        return true;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        final SAMLObjectBuilder<ChannelBindings> cbBuilder = (SAMLObjectBuilder<ChannelBindings>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<ChannelBindings>getBuilderOrThrow(
                        ChannelBindings.DEFAULT_ELEMENT_NAME);

        for (final Assertion assertion : response.getAssertions()) {
            final Advice advice = SAML2ActionSupport.addAdviceToAssertion(this, assertion);
            for (final ChannelBindings cb : channelBindingsContext.getChannelBindings()) {
                final ChannelBindings newCB = cbBuilder.buildObject();
                newCB.setType(cb.getType());
                advice.getChildren().add(newCB);
            }
        }
        
        log.debug("{} Added ChannelBindings indicator(s) to Advice", getLogPrefix());
    }

}