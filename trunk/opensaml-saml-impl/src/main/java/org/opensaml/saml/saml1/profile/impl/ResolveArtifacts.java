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

package org.opensaml.saml.saml1.profile.impl;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.messaging.context.navigate.MessageLookup;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.InboundMessageContextLookup;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;
import org.opensaml.saml.common.binding.artifact.SAMLArtifactMap;
import org.opensaml.saml.common.binding.artifact.SAMLArtifactMap.SAMLArtifactMapEntry;
import org.opensaml.saml.common.messaging.context.navigate.SAMLMessageContextIssuerFunction;
import org.opensaml.saml.common.profile.SAMLEventIds;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.AssertionArtifact;
import org.opensaml.saml.saml1.core.Request;
import org.opensaml.saml.saml1.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Functions;

/**
 * Action that resolves SAML 1.x artifacts inside a {@link Request} located via a lookup strategy,
 * by default from the inbound message context, and maps them to the corresponding assertions.
 * 
 * <p>The assertions are added to a {@link Response} located via a lookup strategy, by default
 * from the outbound message context.</p>
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_MSG_CTX}
 * @event {@link EventIds#INVALID_PROFILE_CTX}
 * @event {@link SAMLEventIds#UNABLE_RESOLVE_ARTIFACT}
 */
public class ResolveArtifacts extends AbstractProfileAction {

    /** Class logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(ResolveArtifacts.class);

    /** Strategy used to locate the {@link Request} to operate on. */
    @Nonnull private Function<ProfileRequestContext,Request> requestLookupStrategy;
    
    /** Strategy used to locate the {@link Response} to operate on. */
    @Nonnull private Function<ProfileRequestContext,Response> responseLookupStrategy;
    
    /** Strategy used to locate the issuer identity to validate against artifact entries. */
    @NonnullAfterInit private Function<ProfileRequestContext,String> issuerLookupStrategy;
    
    /** Strategy used to locate the requester identity to validate against artifact entries. */
    @Nonnull private Function<ProfileRequestContext,String> requesterLookupStrategy;
    
    /** Artifact mapper. */
    @NonnullAfterInit private SAMLArtifactMap artifactMap;

    /** Request to process. */
    @Nullable private Request request;
    
    /** Response to populate. */
    @Nullable private Response response;
    
    /** Identity of issuer. */
    @Nullable private String issuerId;

    /** Identity of requester. */
    @Nullable private String requesterId;
    
    /** Constructor. */
    public ResolveArtifacts() {
        requestLookupStrategy =
                Functions.compose(new MessageLookup<>(Request.class), new InboundMessageContextLookup());
        responseLookupStrategy =
                Functions.compose(new MessageLookup<>(Response.class), new OutboundMessageContextLookup());
        requesterLookupStrategy =
                Functions.compose(new SAMLMessageContextIssuerFunction(), new InboundMessageContextLookup());
    }
    
    /**
     * Set the strategy used to locate the {@link Request} to operate on.
     * 
     * @param strategy lookup strategy
     */
    public synchronized void setRequestLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,Request> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        requestLookupStrategy = Constraint.isNotNull(strategy, "Request lookup strategy cannot be null");
    }

    /**
     * Set the strategy used to locate the {@link Response} to operate on.
     * 
     * @param strategy lookup strategy
     */
    public synchronized void setResponseLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,Response> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        responseLookupStrategy = Constraint.isNotNull(strategy, "Response lookup strategy cannot be null");
    }
    
    /**
     * Set the strategy used to locate the issuer's identity.
     * 
     * @param strategy lookup strategy
     */
    public synchronized void setIssuerLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,String> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
    
        issuerLookupStrategy = Constraint.isNotNull(strategy, "Issuer lookup strategy cannot be null");
    }

    /**
     * Set the strategy used to locate the requester's identity.
     * 
     * @param strategy lookup strategy
     */
    public synchronized void setRequesterLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,String> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        requesterLookupStrategy = Constraint.isNotNull(strategy, "Requester lookup strategy cannot be null");
    }

    /**
     * Set the artifact map to use.
     * 
     * @param map   artifact map
     */
    public synchronized void setArtifactMap(@Nonnull final SAMLArtifactMap map) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        artifactMap = Constraint.isNotNull(map, "SAMLArtifactMap cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (artifactMap == null) {
            throw new ComponentInitializationException("SAMLArtifactMap cannot be null");
        } else if (issuerLookupStrategy == null) {
            throw new ComponentInitializationException("Issuer lookup strategy cannot be null");
        }
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        request = requestLookupStrategy.apply(profileRequestContext);
        if (request == null) {
            log.debug("{} No request located", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        } else if (request.getAssertionArtifacts().isEmpty()) {
            log.debug("{} No AssertionArtifact elements found in request, nothing to do", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        }

        response = responseLookupStrategy.apply(profileRequestContext);
        if (response == null) {
            log.debug("{} No response located", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        }

        issuerId = issuerLookupStrategy.apply(profileRequestContext);
        if (issuerId == null) {
            log.debug("{} No issuer identity located", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_PROFILE_CTX);
            return false;
        }
        
        requesterId = requesterLookupStrategy.apply(profileRequestContext);
        if (requesterId == null) {
            log.debug("{} No requester identity located", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        }
        
        return super.doPreExecute(profileRequestContext);
    }

    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {

        boolean success = true;
        
        try {
            for (final AssertionArtifact artifact : request.getAssertionArtifacts()) {
                final SAMLArtifactMapEntry entry = artifactMap.get(artifact.getAssertionArtifact());
                if (entry == null) {
                    log.warn("{} Unresolvable AssertionArtifact '{}' from relying party '{}'", getLogPrefix(),
                            artifact.getAssertionArtifact(), requesterId);
                    success = false;
                    break;
                }
                
                artifactMap.remove(artifact.getAssertionArtifact());
                
                if (!entry.getIssuerId().equals(issuerId)) {
                    log.warn("{} Artifact issuer mismatch, issued by '{}' but IdP has entityID of '{}'",
                            getLogPrefix(), entry.getIssuerId(), issuerId);
                    success = false;
                    break;
                } else if (!entry.getRelyingPartyId().equals(requesterId)) {
                    log.warn("{} Artifact relying party mismatch, issued to '{}' but requested by '{}'",
                            getLogPrefix(), entry.getRelyingPartyId(), requesterId);
                    success = false;
                    break;
                } else if (!(entry.getSamlMessage() instanceof Assertion)) {
                    log.warn("{} Artifact '{}' resolved to a non-Assertion object", getLogPrefix(),
                            artifact.getAssertionArtifact());
                    success = false;
                    break;
                }
                
                response.getAssertions().add((Assertion) entry.getSamlMessage());
            }
        } catch (final IOException e) {
            log.error(getLogPrefix() + " Error resolving artifact", e);
            success = false;
        }

        if (!success) {
            response.getAssertions().clear();
            
            // Make sure we remove everything requested.
            for (final AssertionArtifact artifact : request.getAssertionArtifacts()) {
                try {
                    artifactMap.remove(artifact.getAssertionArtifact());
                } catch (final IOException e) {
                    log.error("{} Error removing mapping for artifact '{}'", getLogPrefix(),
                            artifact.getAssertionArtifact());
                }
            }
            
            ActionSupport.buildEvent(profileRequestContext, SAMLEventIds.UNABLE_RESOLVE_ARTIFACT);
        }
    }

}