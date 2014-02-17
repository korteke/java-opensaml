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

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.profile.ProfileException;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.binding.BindingDescriptor;
import org.opensaml.saml.common.binding.DefaultEndpointResolver;
import org.opensaml.saml.common.binding.EndpointResolver;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.profile.SAMLEventIds;
import org.opensaml.saml.criterion.BindingCriterion;
import org.opensaml.saml.criterion.EndpointCriterion;
import org.opensaml.saml.criterion.RoleDescriptorCriterion;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.metadata.IndexedEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 * Action that populates the outbound {@link SAMLBindingContext} and {@link SAMLEndpointContext}
 * based on the inbound request.
 * 
 * <p>The endpoint context is populated by constructing a "template" endpoint, with content
 * based on the inbound request, and relying on an injected {@link EndpointResolver} and
 * an injected list of acceptable bindings.</p>
 * 
 * <p>The binding context is populated based on the computed endpoint's binding, and the
 * inbound {@link SAMLBindingContext}'s relay state.</p>
 * 
 * <p>The base action understands SAML 2 {@link AuthnRequest} messages. Subclasses may
 * override the {@link #supplementResolverCriteria(ProfileRequestContext, CriteriaSet)} method
 * to implement support for other message types or advanced criteria.</p>
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_MSG_CTX}
 * @event {@link SAMLEventIds#ENDPOINT_RESOLUTION_FAILED}
 */
public class PopulateBindingAndEndpointContexts extends AbstractProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(PopulateBindingAndEndpointContexts.class);
    
    /** The type of endpoint to resolve. */
    @Nonnull private QName endpointType;

    /** Endpoint resolver. */
    @Nonnull private EndpointResolver<?> endpointResolver;
    
    /** List of possible bindings, in preference order. */
    @Nonnull @NonnullElements private List<BindingDescriptor> bindingDescriptors;
    
    /** Builder for template endpoints. */
    @NonnullAfterInit private XMLObjectBuilder<?> endpointBuilder;
    
    /** Optional inbound message. */
    @Nullable private Object inboundMessage;
    
    /** Optional metadata for use in endpoint derivation/validation. */
    @Nullable private SAMLMetadataContext mdContext;
    
    /** Constructor. */
    public PopulateBindingAndEndpointContexts() {
        endpointType = AssertionConsumerService.DEFAULT_ELEMENT_NAME;
        endpointResolver = new DefaultEndpointResolver();
        bindingDescriptors = Collections.emptyList();
    }
    
    /**
     * Set the type of endpoint to resolve, defaults to {@link AssertionConsumerService}.
     * 
     * @param type  type of endpoint to resolve
     */
    public void setEndpointType(@Nonnull final QName type) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        endpointType = Constraint.isNotNull(type, "Endpoint type cannot be null");
    }
    
    /**
     * Set a custom {@link EndpointResolver} to use.
     * 
     * @param resolver endpoint resolver to use  
     */
    public void setEndpointResolver(@Nonnull final EndpointResolver<?> resolver) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        endpointResolver = Constraint.isNotNull(resolver, "EndpointResolver cannot be null");
    }
    
    /**
     * Set the bindings to evaluate for use, in preference order.
     * 
     * @param bindings bindings to consider
     */
    public void setBindings(@Nonnull @NonnullElements final List<BindingDescriptor> bindings) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        Constraint.isNotNull(bindings, "Binding descriptor list cannot be null");
        
        bindingDescriptors = Lists.newArrayList(Collections2.filter(bindings, Predicates.notNull()));
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        endpointBuilder = XMLObjectSupport.getBuilder(endpointType);
        if (endpointBuilder == null) {
            throw new ComponentInitializationException("Unable to obtain builder for endpoint type " + endpointType);
        } else if (!(endpointBuilder.buildObject(endpointType) instanceof Endpoint)) {
            throw new ComponentInitializationException("Builder for endpoint type " + endpointType
                    + " did not result in Endpoint object");
        }
        
        endpointResolver.initialize();
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) throws ProfileException {
        if (profileRequestContext.getInboundMessageContext() != null) {
            inboundMessage = profileRequestContext.getInboundMessageContext().getMessage();
        }
        
        if (profileRequestContext.getOutboundMessageContext() == null) {
            log.debug("{} No outbound message context", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        }
        
        final SAMLPeerEntityContext peerContext =
                profileRequestContext.getOutboundMessageContext().getSubcontext(SAMLPeerEntityContext.class, false);
        if (peerContext != null) {
            mdContext = peerContext.getSubcontext(SAMLMetadataContext.class, false);
        }

        return super.doPreExecute(profileRequestContext);
    }

    /** {@inheritDoc} */
    @Override protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext)
            throws ProfileException {
        
        final MessageContext msgCtx = profileRequestContext.getOutboundMessageContext();
        
        log.debug("{} Attempting to resolve endpoint of type {} for outbound message", getLogPrefix(), endpointType);

        // Compile binding list.
        final List<String> bindings = Lists.newArrayListWithExpectedSize(bindingDescriptors.size());
        for (final BindingDescriptor bindingDescriptor : bindingDescriptors) {
            if (bindingDescriptor.apply(profileRequestContext)) {
                bindings.add(bindingDescriptor.getId());
            }
        }
        if (bindings.isEmpty()) {
            log.warn("{} No outbound bindings are eligible for use");
            ActionSupport.buildEvent(profileRequestContext, SAMLEventIds.ENDPOINT_RESOLUTION_FAILED);
            return;
        } else {
            log.trace("{} Eligible outbound bindings: {}", getLogPrefix(), bindings);
        }
        
        // Build criteria for the resolver.
        CriteriaSet criteria = new CriteriaSet(new EndpointCriterion(buildTemplateEndpoint()),
                new BindingCriterion(bindings));
        if (mdContext != null || mdContext.getRoleDescriptor() != null) {
            criteria.add(new RoleDescriptorCriterion(mdContext.getRoleDescriptor()));
        } else {
            log.debug("{} No metadata available for endpoint resolution");
        }
        
        try {
            supplementResolverCriteria(profileRequestContext, criteria);
        } catch (final ProfileException e) {
            log.error(getLogPrefix() + " Error during criteria supplementation", e);
            ActionSupport.buildEvent(profileRequestContext, SAMLEventIds.ENDPOINT_RESOLUTION_FAILED);
            return;
        }
        
        // Attempt resolution.
        Endpoint resolvedEndpoint = null;
        try {
            resolvedEndpoint = endpointResolver.resolveSingle(criteria);
        } catch (final ResolverException e) {
            log.error(getLogPrefix() + " Error resolving outbound message endpoint", e);
        }
        
        if (resolvedEndpoint == null) {
            log.warn("{} Unable to resolve outbound message endpoint");
            ActionSupport.buildEvent(profileRequestContext, SAMLEventIds.ENDPOINT_RESOLUTION_FAILED);
            return;
        }
        
        // Transfer results to contexts.
        
        final SAMLEndpointContext endpointContext =
                msgCtx.getSubcontext(SAMLPeerEntityContext.class, true).getSubcontext(SAMLEndpointContext.class, true);
        endpointContext.setEndpoint(resolvedEndpoint);
        
        final SAMLBindingContext bindingCtx = msgCtx.getSubcontext(SAMLBindingContext.class, true);
        bindingCtx.setRelayState(SAMLBindingSupport.getRelayState(profileRequestContext.getInboundMessageContext()));
        bindingCtx.setBindingUri(resolvedEndpoint.getBinding());
    }
    
    /**
     * Allow a subclass to override or supplement criteria for endpoint resolution.
     * 
     * @param profileRequestContext current profile request context
     * @param criteria input criteria established by base class
     * 
     * @throws ProfileException if resolution should be aborted
     */
    protected void supplementResolverCriteria(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final CriteriaSet criteria) throws ProfileException {
        
    }

    /**
     * Build a template Endpoint object to use as input criteria to the resolution process.
     * 
     * @return a template for the eventual Endpoint to resolve
     */
    @Nonnull private Endpoint buildTemplateEndpoint() {
        final Endpoint endpoint = (Endpoint) endpointBuilder.buildObject(endpointType);
        
        if (inboundMessage instanceof AuthnRequest) {
            log.debug("{} Populating template endpoint for resolution from SAML AuthnRequest");
            
            endpoint.setLocation(((AuthnRequest) inboundMessage).getAssertionConsumerServiceURL());
            endpoint.setBinding(((AuthnRequest) inboundMessage).getProtocolBinding());
            if (endpoint instanceof IndexedEndpoint) {
                ((IndexedEndpoint) endpoint).setIndex(((AuthnRequest) endpoint).getAssertionConsumerServiceIndex());
            }
        }
        
        return endpoint;
    }

}