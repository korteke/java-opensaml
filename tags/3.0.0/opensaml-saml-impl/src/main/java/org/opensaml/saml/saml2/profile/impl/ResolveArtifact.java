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
import org.opensaml.saml.saml2.core.ArtifactResolve;
import org.opensaml.saml.saml2.core.ArtifactResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Functions;

/**
 * Action that resolves a SAML 2.0 artifact inside an {@link ArtifactResolve} request located
 * via a lookup strategy, by default from the inbound message context, and maps it to the
 * corresponding message.
 * 
 * <p>The message is added to an {@link ArtifactResponse} located via a lookup strategy, by default
 * from the outbound message context.</p>
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_MSG_CTX}
 * @event {@link EventIds#INVALID_PROFILE_CTX}
 * @event {@link SAMLEventIds#UNABLE_RESOLVE_ARTIFACT}
 */
public class ResolveArtifact extends AbstractProfileAction {

    /** Class logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(ResolveArtifact.class);

    /** Strategy used to locate the {@link ArtifactResolve} to operate on. */
    @Nonnull private Function<ProfileRequestContext,ArtifactResolve> requestLookupStrategy;
    
    /** Strategy used to locate the {@link ArtifactResponse} to operate on. */
    @Nonnull private Function<ProfileRequestContext,ArtifactResponse> responseLookupStrategy;
    
    /** Strategy used to locate the issuer identity to validate against artifact entries. */
    @NonnullAfterInit private Function<ProfileRequestContext,String> issuerLookupStrategy;
    
    /** Strategy used to locate the requester identity to validate against artifact entries. */
    @Nonnull private Function<ProfileRequestContext,String> requesterLookupStrategy;
    
    /** Artifact mapper. */
    @NonnullAfterInit private SAMLArtifactMap artifactMap;

    /** Request to process. */
    @Nullable private ArtifactResolve request;
    
    /** Response to populate. */
    @Nullable private ArtifactResponse response;
    
    /** Identity of issuer. */
    @Nullable private String issuerId;

    /** Identity of requester. */
    @Nullable private String requesterId;
    
    /** Constructor. */
    public ResolveArtifact() {
        requestLookupStrategy =
                Functions.compose(new MessageLookup<>(ArtifactResolve.class), new InboundMessageContextLookup());
        responseLookupStrategy =
                Functions.compose(new MessageLookup<>(ArtifactResponse.class), new OutboundMessageContextLookup());
        requesterLookupStrategy =
                Functions.compose(new SAMLMessageContextIssuerFunction(), new InboundMessageContextLookup());
    }
    
    /**
     * Set the strategy used to locate the {@link ArtifactResolve} to operate on.
     * 
     * @param strategy lookup strategy
     */
    public void setRequestLookupStrategy(@Nonnull final Function<ProfileRequestContext,ArtifactResolve> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        requestLookupStrategy = Constraint.isNotNull(strategy, "Request lookup strategy cannot be null");
    }

    /**
     * Set the strategy used to locate the {@link ArtifactResponse} to operate on.
     * 
     * @param strategy lookup strategy
     */
    public void setResponseLookupStrategy(@Nonnull final Function<ProfileRequestContext,ArtifactResponse> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        responseLookupStrategy = Constraint.isNotNull(strategy, "Response lookup strategy cannot be null");
    }
    
    /**
     * Set the strategy used to locate the issuer's identity.
     * 
     * @param strategy lookup strategy
     */
    public void setIssuerLookupStrategy(@Nonnull final Function<ProfileRequestContext,String> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
    
        issuerLookupStrategy = Constraint.isNotNull(strategy, "Issuer lookup strategy cannot be null");
    }

    /**
     * Set the strategy used to locate the requester's identity.
     * 
     * @param strategy lookup strategy
     */
    public void setRequesterLookupStrategy(@Nonnull final Function<ProfileRequestContext,String> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        requesterLookupStrategy = Constraint.isNotNull(strategy, "Requester lookup strategy cannot be null");
    }

    /**
     * Set the artifact map to use.
     * 
     * @param map   artifact map
     */
    public void setArtifactMap(@Nonnull final SAMLArtifactMap map) {
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
        } else if (request.getArtifact() == null || request.getArtifact().getArtifact() == null) {
            log.debug("{} No Artifact element found in request, nothing to do", getLogPrefix());
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
        
        final String artifact = request.getArtifact().getArtifact();

        SAMLArtifactMapEntry entry = null;
        
        try {
            entry = artifactMap.get(artifact);
        } catch (final IOException e) {
            log.error("{} Error resolving artifact", getLogPrefix(), e);
            ActionSupport.buildEvent(profileRequestContext, SAMLEventIds.UNABLE_RESOLVE_ARTIFACT);
            return;
        }
        
        if (entry == null) {
            log.warn("{} Unresolvable Artifact '{}' from relying party '{}'", getLogPrefix(), artifact,
                    requesterId);
            ActionSupport.buildEvent(profileRequestContext, SAMLEventIds.UNABLE_RESOLVE_ARTIFACT);
            return;
        }
        
        try {
            artifactMap.remove(artifact);
        } catch (final IOException e) {
            log.error("{} Error removing artifact from map", getLogPrefix(), e);
            ActionSupport.buildEvent(profileRequestContext, SAMLEventIds.UNABLE_RESOLVE_ARTIFACT);
            return;
        }
        
        if (!entry.getIssuerId().equals(issuerId)) {
            log.warn("{} Artifact issuer mismatch, issued by '{}' but IdP has entityID of '{}'",
                    getLogPrefix(), entry.getIssuerId(), issuerId);
            ActionSupport.buildEvent(profileRequestContext, SAMLEventIds.UNABLE_RESOLVE_ARTIFACT);
        } else if (!entry.getRelyingPartyId().equals(requesterId)) {
            log.warn("{} Artifact relying party mismatch, issued to '{}' but requested by '{}'",
                    getLogPrefix(), entry.getRelyingPartyId(), requesterId);
            ActionSupport.buildEvent(profileRequestContext, SAMLEventIds.UNABLE_RESOLVE_ARTIFACT);
        } else {
            response.setMessage(entry.getSamlMessage());
        }
    }

}