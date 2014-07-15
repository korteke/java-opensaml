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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.InboundMessageContextLookup;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;
import org.opensaml.saml.common.messaging.context.ChannelBindingsContext;
import org.opensaml.saml.common.profile.SAMLEventIds;
import org.opensaml.saml.ext.saml2cb.ChannelBindings;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Objects;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 * Action that verifies two sets of {@link ChannelBindings} from two different {@link ChannelBindingsContext}
 * objects obtained via lookup functions, by default from below the inbound message context and from below
 * a {@link SOAP11Context} below the inbound message context.
 * 
 * <p>If neither function supplies a non-empty {@link ChannelBindingsContext}, then there is no verification
 * required, but if either one supplies a non-empty context, then a match must be achieved or an error event
 * is signaled.</p>
 * 
 * <p>If verification is successful, then the resulting match is stored in new {@link ChannelBindingsContext}
 * objects created from one or more lookup/creation functions. By default these are created below the profile
 * request context and below a {@link SOAP11Context} below the outbound message context.</p>
 * 
 * @event {@link org.opensaml.profile.action.EventIds#PROCEED_EVENT_ID}
 * @event {@link SAMLEventIds#CHANNEL_BINDINGS_ERROR}
 * 
 * @pre {@link ChannelBindingContext} objects to be returned from lookup functions must be populated.
 * @post Upon successful verification, one or more {@link ChannelBindingContext} objects will be created as described.
 */
public class VerifyChannelBindings extends AbstractProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(VerifyChannelBindings.class);

    /** Strategy used to locate the first set of bindings to operate on. */
    @Nonnull private Function<ProfileRequestContext,ChannelBindingsContext> channelBindingsLookupStrategy1;

    /** Strategy used to locate the second set of bindings to operate on. */
    @Nonnull private Function<ProfileRequestContext,ChannelBindingsContext> channelBindingsLookupStrategy2;
    
    /** Strategy used to locate the first set of bindings to operate on. */
    @Nonnull @NonnullElements
    private Collection<Function<ProfileRequestContext,ChannelBindingsContext>> channelBindingsCreationStrategies;
    
    /** The first set of bindings. */
    @Nullable private ChannelBindingsContext channelBindingsContext1;

    /** The second set of bindings. */
    @Nullable private ChannelBindingsContext channelBindingsContext2;

    /** Constructor. */
    public VerifyChannelBindings() {
        channelBindingsLookupStrategy1 = Functions.compose(new ChildContextLookup<>(ChannelBindingsContext.class),
                new InboundMessageContextLookup());
        channelBindingsLookupStrategy2 = Functions.compose(new ChildContextLookup<>(ChannelBindingsContext.class),
                Functions.compose(new ChildContextLookup<>(SOAP11Context.class), new InboundMessageContextLookup()));
                
        // Default locations to create are ECP-centric, beneath PRC, and outbound MC -> SOAP
        channelBindingsCreationStrategies = Lists.newArrayListWithExpectedSize(2);
        channelBindingsCreationStrategies.add(new ChildContextLookup<ProfileRequestContext,ChannelBindingsContext>(
                ChannelBindingsContext.class, true));
        channelBindingsCreationStrategies.add(
                Functions.compose(new ChildContextLookup<>(ChannelBindingsContext.class, true),
                        Functions.compose(
                                new ChildContextLookup<MessageContext,SOAP11Context>(SOAP11Context.class, true),
                                new OutboundMessageContextLookup())));
    }
    
    /**
     * Set the strategy used to locate the first {@link ChannelBindingsContext} to operate on.
     * 
     * @param strategy lookup strategy
     */
    public void setChannelBindingsLookupStrategy1(
            @Nonnull final Function<ProfileRequestContext,ChannelBindingsContext> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        channelBindingsLookupStrategy1 = Constraint.isNotNull(strategy,
                "First ChannelBindingsContext lookup strategy cannot be null");
    }

    /**
     * Set the strategy used to locate the second {@link ChannelBindingsContext} to operate on.
     * 
     * @param strategy lookup strategy
     */
    public void setChannelBindingsLookupStrategy2(
            @Nonnull final Function<ProfileRequestContext,ChannelBindingsContext> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        channelBindingsLookupStrategy2 = Constraint.isNotNull(strategy,
                "Second ChannelBindingsContext lookup strategy cannot be null");
    }

    /**
     * Set the strategy used to creare or locate the {@link ChannelBindingsContext}s to save matching results in.
     * 
     * @param strategies lookup/creation strategies
     */
    public void setChannelBindingsCreationStrategies(@Nonnull @NonnullElements
            final Collection<Function<ProfileRequestContext,ChannelBindingsContext>> strategies) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        Constraint.isNotNull(strategies, "ChannelBindingsContext creation strategies cannot be null");

        channelBindingsCreationStrategies = Lists.newArrayList(Collections2.filter(strategies, Predicates.notNull()));
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        if (!super.doPreExecute(profileRequestContext)) {
            return false;
        }

        channelBindingsContext1 = channelBindingsLookupStrategy1.apply(profileRequestContext);
        channelBindingsContext2 = channelBindingsLookupStrategy2.apply(profileRequestContext);
        
        if (channelBindingsContext1 != null && channelBindingsContext1.getChannelBindings().isEmpty()) {
            channelBindingsContext1 = null;
        }
        
        if (channelBindingsContext2 != null && channelBindingsContext2.getChannelBindings().isEmpty()) {
            channelBindingsContext2 = null;
        }
        
        if (channelBindingsContext1 == null && channelBindingsContext2 == null) {
            log.debug("{} No channel bindings found to verify, nothing to do", getLogPrefix());
            return false;
        }
     
        return true;
    }

    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {

        if (channelBindingsContext1 == null || channelBindingsContext2 == null) {
            log.warn("{} Unable to verify channel bindings sent for comparison", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, SAMLEventIds.CHANNEL_BINDINGS_ERROR);
            return;
        }
        
        ChannelBindings matched = null;
        
        for (final ChannelBindings cb1 : channelBindingsContext1.getChannelBindings()) {
            for (final ChannelBindings cb2 : channelBindingsContext2.getChannelBindings()) {
                if (Objects.equal(cb1.getType(), cb2.getType())) {
                    final String cb1Data = StringSupport.trimOrNull(cb1.getValue());
                    final String cb2Data = StringSupport.trimOrNull(cb2.getValue());
                    if (Objects.equal(cb1Data, cb2Data)) {
                        matched = cb1;
                    }
                }
            }
        }
        
        if (matched == null) {
            log.warn("{} Unable to verify channel bindings sent for comparison", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, SAMLEventIds.CHANNEL_BINDINGS_ERROR);
            return;
        }
        
        log.debug("{} Saving matched channel bindings for later use", getLogPrefix());
        
        for (final Function<ProfileRequestContext,ChannelBindingsContext> fn : channelBindingsCreationStrategies) {
            final ChannelBindingsContext cbCtx = fn.apply(profileRequestContext);
            if (cbCtx != null) {
                cbCtx.getChannelBindings().add(matched);
            }
        }
    }

}