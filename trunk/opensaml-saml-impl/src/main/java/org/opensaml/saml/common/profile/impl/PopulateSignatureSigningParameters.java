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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.profile.ProfileException;
import org.opensaml.profile.action.AbstractConditionalProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.criterion.RoleDescriptorCriterion;
import org.opensaml.xmlsec.SecurityConfigurationSupport;
import org.opensaml.xmlsec.SignatureSigningConfiguration;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.SignatureSigningParametersResolver;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.opensaml.xmlsec.criterion.SignatureSigningConfigurationCriterion;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;

/**
 * Action that resolves and populates {@link SignatureSigningParameters} on a {@link SecurityParametersContext}
 * created/accessed via a lookup function, by default on the outbound message context.
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_PROFILE_CTX}
 * @event {@link EventIds#INVALID_SEC_CFG}
 */
public class PopulateSignatureSigningParameters extends AbstractConditionalProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(PopulateSignatureSigningParameters.class);
    
    /** Strategy used to look up the {@link SecurityParametersContext} to set the parameters for. */
    @Nonnull private Function<ProfileRequestContext,SecurityParametersContext> securityParametersContextLookupStrategy;
    
    /** Strategy used to look up a per-request {@link SignatureSigningConfiguration}. */
    @Nullable private Function<ProfileRequestContext,SignatureSigningConfiguration> configurationLookupStrategy;

    /** Strategy used to look up a SAML metadata context. */
    @Nullable private Function<ProfileRequestContext,SAMLMetadataContext> metadataContextLookupStrategy;
    
    /** Resolver for parameters to store into context. */
    @NonnullAfterInit private SignatureSigningParametersResolver resolver;
    
    /**
     * Constructor.
     * 
     * Initializes {@link #messageMetadataContextLookupStrategy} to {@link ChildContextLookup}.
     */
    public PopulateSignatureSigningParameters() {
        // Create context by default.
        securityParametersContextLookupStrategy = Functions.compose(
                new ChildContextLookup<>(SecurityParametersContext.class, true), new OutboundMessageContextLookup());
        
        // Default: outbound msg context -> SAMLPeerEntityContext -> SAMLMetadataContext
        metadataContextLookupStrategy = Functions.compose(
                new ChildContextLookup<>(SAMLMetadataContext.class),
                Functions.compose(new ChildContextLookup<>(SAMLPeerEntityContext.class),
                        new OutboundMessageContextLookup()));
    }

    /**
     * Set the strategy used to look up the {@link SecurityParametersContext} to set the parameters for.
     * 
     * @param strategy lookup strategy
     */
    public void setSecurityParametersContextLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,SecurityParametersContext> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        securityParametersContextLookupStrategy = Constraint.isNotNull(strategy,
                "SecurityParametersContext lookup strategy cannot be null");
    }
    
    /**
     * Set the strategy used to look up a per-request {@link SignatureSigningConfiguration}.
     * 
     * @param strategy lookup strategy
     */
    public void setConfigurationLookupStrategy(
            @Nullable final Function<ProfileRequestContext,SignatureSigningConfiguration> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        configurationLookupStrategy = strategy;
    }
    
    /**
     * Set lookup strategy for {@link SAMLMetadataContext} for input to resolution.
     * 
     * @param strategy  lookup strategy
     */
    public void setMetadataContextLookupStrategy(
            @Nullable final Function<ProfileRequestContext,SAMLMetadataContext> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        metadataContextLookupStrategy = strategy;
    }
    
    /**
     * Set the resolver to use for the parameters to store into the context.
     * 
     * @param newResolver   resolver to use
     */
    public void setSignatureSigningParametersResolver(
            @Nonnull final SignatureSigningParametersResolver newResolver) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        resolver = Constraint.isNotNull(newResolver, "SignatureSigningParametersResolver cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (resolver == null) {
            throw new ComponentInitializationException("SignatureSigningParametersResolver cannot be null");
        }
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) throws ProfileException {
        
        if (super.doPreExecute(profileRequestContext)) {
            log.debug("{} Signing enabled", getLogPrefix());
            return true;
        } else {
            log.debug("{} Signing not enabled", getLogPrefix());
            return false;
        }
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) throws ProfileException {

        log.debug("{} Resolving SignatureSigningParameters for request", getLogPrefix());
        
        final SecurityParametersContext paramsCtx =
                securityParametersContextLookupStrategy.apply(profileRequestContext);
        if (paramsCtx == null) {
            log.debug("{} No SecurityParametersContext returned by lookup strategy", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_PROFILE_CTX);
            return;
        }
        
        // TODO: do we include anything but the global default and the per-profile configs?
        // Maybe a global IdP config in addition or instead of the OpenSAML one?
        
        final List<SignatureSigningConfiguration> configs = Lists.newArrayList();
        configs.add(SecurityConfigurationSupport.getGlobalSignatureSigningConfiguration());
        
        if (configurationLookupStrategy != null) {
            log.debug("{} Looking up per-request SignatureSigningConfiguration", getLogPrefix());
            final SignatureSigningConfiguration perRequestConfig =
                    configurationLookupStrategy.apply(profileRequestContext);
            if (perRequestConfig != null) {
                configs.add(perRequestConfig);
            }
        }
        
        final CriteriaSet criteria = new CriteriaSet(new SignatureSigningConfigurationCriterion(configs));
        
        if (metadataContextLookupStrategy != null) {
            final SAMLMetadataContext metadataCtx = metadataContextLookupStrategy.apply(profileRequestContext);
            if (metadataCtx != null && metadataCtx.getRoleDescriptor() != null) {
                log.debug("{} Adding metadata to resolution criteria for signing/digest algorithms", getLogPrefix());
                criteria.add(new RoleDescriptorCriterion(metadataCtx.getRoleDescriptor()));
            }
        }
        
        try {
            final SignatureSigningParameters params = resolver.resolveSingle(criteria);
            paramsCtx.setSignatureSigningParameters(params);
            log.debug("{} {} SignatureSigningParameters", getLogPrefix(),
                    params != null ? "Resolved" : "Failed to resolve");
        } catch (final ResolverException e) {
            log.error(getLogPrefix() + " Error resolving SignatureSigningParameters", e);
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_SEC_CFG);
        }
    }
    
}