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

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.profile.action.AbstractConditionalProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.navigate.MessageLookup;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Audience;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.ProxyRestriction;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.profile.SAML2ActionSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Functions;

/**
 * Action adds an {@link ProxyRestrictionCondition} to every {@link Assertion} contained in a SAML 2
 * response, with the audiences and count obtained from a lookup function. If the containing
 * {@link Conditions} is not present, it will be created.
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_MSG_CTX}
 */
public class AddProxyRestrictionToAssertions extends AbstractConditionalProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AddProxyRestrictionToAssertions.class);

    /** Strategy used to locate the Response to operate on. */
    @Nonnull private Function<ProfileRequestContext,Response> responseLookupStrategy;

    /** Strategy used to obtain the audiences to add. */
    @Nullable private Function<ProfileRequestContext,Collection<String>> proxyAudiencesLookupStrategy;
    
    /** Strategy used to obtain the proxy count to add. */
    @Nullable private Function<ProfileRequestContext,Long> proxyCountLookupStrategy;
    
    /** Response to modify. */
    @Nullable private Response response;

    /** Audiences to add. */
    @Nullable private Collection<String> audiences;
    
    /** Constructor. */
    public AddProxyRestrictionToAssertions() {
        responseLookupStrategy =
                Functions.compose(new MessageLookup<>(Response.class), new OutboundMessageContextLookup());
    }
    
    /**
     * Set the strategy used to locate the Response to operate on.
     * 
     * @param strategy lookup strategy
     */
    public void setResponseLookupStrategy(@Nonnull final Function<ProfileRequestContext,Response> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        responseLookupStrategy = Constraint.isNotNull(strategy, "Response lookup strategy cannot be null");
    }
    
    /**
     * Set the strategy used to obtain the proxy restrictions to apply.
     * 
     * @param strategy lookup strategy
     */
    public void setProxyAudiencesLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,Collection<String>> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        proxyAudiencesLookupStrategy =
                Constraint.isNotNull(strategy, "Proxy restriction audiences lookup strategy cannot be null");
    }

    /**
     * Set the strategy used to obtain the proxy count to apply.
     * 
     * @param strategy lookup strategy
     */
    public void setProxyCountLookupStrategy(@Nonnull final Function<ProfileRequestContext,Long> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        proxyCountLookupStrategy = Constraint.isNotNull(strategy, "Proxy count lookup strategy cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (proxyAudiencesLookupStrategy == null) {
            throw new ComponentInitializationException("Proxy restriction audience lookup strategy cannot be null");
        } else if (proxyCountLookupStrategy == null) {
            throw new ComponentInitializationException("Proxy count lookup strategy cannot be null");
        }
    }

    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        audiences = proxyAudiencesLookupStrategy.apply(profileRequestContext);
        if (audiences == null || audiences.isEmpty()) {
            log.debug("{} No audiences to add, nothing to do", getLogPrefix());
            return false;
        }

        log.debug("{} Attempting to add an ProxyRestriction to every Assertion in Response",
                getLogPrefix());

        response = responseLookupStrategy.apply(profileRequestContext);
        if (response == null) {
            log.debug("{} No response located", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        } else if (response.getAssertions().isEmpty()) {
            log.debug("{} No assertions found in response, nothing to do", getLogPrefix());
            return false;
        }
        
        return super.doPreExecute(profileRequestContext);
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        for (final Assertion assertion : response.getAssertions()) {
            addProxyRestriction(profileRequestContext, SAML2ActionSupport.addConditionsToAssertion(this, assertion));
            log.debug("{} Added ProxyRestriction to Assertion {}", getLogPrefix(), assertion.getID());
        }
    }

    /**
     * Add the audiences obtained from a lookup function to the {@link ProxyRestriction}. If no
     * {@link ProxyRestriction} exists on the given {@link Conditions} one is created and added.
     * 
     * @param profileRequestContext current profile request context
     * @param conditions condition that has, or will receive the created, {@link ProxyRestriction}
     */
    private void addProxyRestriction(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final Conditions conditions) {
        final ProxyRestriction condition = getProxyRestriction(conditions);

        final SAMLObjectBuilder<Audience> audienceBuilder = (SAMLObjectBuilder<Audience>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Audience>getBuilderOrThrow(
                        Audience.DEFAULT_ELEMENT_NAME);
        for (final String audienceId : audiences) {
            log.debug("{} Adding {} as an Audience of the ProxyRestriction", getLogPrefix(), audienceId);
            final Audience audience = audienceBuilder.buildObject();
            audience.setAudienceURI(audienceId);
            condition.getAudiences().add(audience);
        }
        
        final Long count = proxyCountLookupStrategy.apply(profileRequestContext);
        condition.setProxyCount(count != null ? count.intValue() : 0);
    }
        
    /**
     * Get the {@link ProxyRestriction} to which audiences will be added.
     * 
     * @param conditions existing set of conditions
     * 
     * @return the condition to which audiences will be added
     */
    @Nonnull private ProxyRestriction getProxyRestriction(@Nonnull final Conditions conditions) {
        
        final ProxyRestriction condition;

        if (conditions.getProxyRestriction() == null) {
            final SAMLObjectBuilder<ProxyRestriction> conditionBuilder = (SAMLObjectBuilder<ProxyRestriction>)
                    XMLObjectProviderRegistrySupport.getBuilderFactory().<ProxyRestriction>getBuilderOrThrow(
                            ProxyRestriction.DEFAULT_ELEMENT_NAME);
            log.debug("{} Adding new ProxyRestriction", getLogPrefix());
            condition = conditionBuilder.buildObject();
            conditions.getConditions().add(condition);
        } else {
            log.debug("{} Conditions already contained an ProxyRestriction, using it", getLogPrefix());
            condition = conditions.getProxyRestriction();
        }

        return condition;
    }

}