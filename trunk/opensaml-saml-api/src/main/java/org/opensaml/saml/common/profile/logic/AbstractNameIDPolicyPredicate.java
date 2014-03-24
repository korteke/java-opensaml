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

package org.opensaml.saml.common.profile.logic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.component.AbstractInitializableComponent;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.messaging.context.navigate.MessageLookup;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.InboundMessageContextLookup;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.RequestAbstractType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Functions;

/**
 * Base class for implementations of {@link NameIDPolicyPredicate} that handles all the basic lookup
 * functions and calls the {@link #doApply(String, String, String, String)} method to do actual work.
 */
public abstract class AbstractNameIDPolicyPredicate extends AbstractInitializableComponent
        implements NameIDPolicyPredicate {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AbstractNameIDPolicyPredicate.class);

    /** Requester ID lookup function. */
    @Nullable private Function<ProfileRequestContext,String> requesterIdLookupStrategy;

    /** Responder ID lookup function. */
    @Nullable private Function<ProfileRequestContext,String> responderIdLookupStrategy;

    /** NameQualifier lookup function. */
    @Nullable private Function<ProfileRequestContext,String> nameQualifierLookupStrategy;

    /** SPNameQualifier lookup function. */
    @Nullable private Function<ProfileRequestContext,String> spNameQualifierLookupStrategy;
    
    /** Constructor. */
    public AbstractNameIDPolicyPredicate() {
        requesterIdLookupStrategy = new RequesterIdFromIssuerFunction();
        spNameQualifierLookupStrategy = new SPNameQualifierFromNameIDPolicyFunction();
    }

    /**
     * Set the strategy used to locate the requester ID.
     * 
     * @param strategy lookup strategy
     */
    public synchronized void setRequesterIdLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,String> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        requesterIdLookupStrategy = Constraint.isNotNull(strategy, "Requester ID lookup strategy cannot be null");
    }

    /**
     * Set the strategy used to locate the responder ID.
     * 
     * @param strategy lookup strategy
     */
    public synchronized void setResponderIdLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,String> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        responderIdLookupStrategy = Constraint.isNotNull(strategy, "Responder ID lookup strategy cannot be null");
    }
    
    /**
     * Set the strategy used to locate the NameQualifier.
     * 
     * @param strategy lookup strategy
     */
    public synchronized void setNameQualifierLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,String> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        nameQualifierLookupStrategy = Constraint.isNotNull(strategy, "NameQualifier lookup strategy cannot be null");
    }

    /**
     * Set the strategy used to locate the SPNameQualifier.
     * 
     * @param strategy lookup strategy
     */
    public synchronized void setSPNameQualifierLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,String> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        spNameQualifierLookupStrategy = Constraint.isNotNull(strategy,
                "SPNameQualifier lookup strategy cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean apply(@Nullable final ProfileRequestContext input) {
        
        final String requesterId;
        final String responderId;
        final String nameQualifier;
        final String spNameQualifier;
        
        if (requesterIdLookupStrategy != null) {
            requesterId = requesterIdLookupStrategy.apply(input);
        } else {
            requesterId = null;
        }

        if (responderIdLookupStrategy != null) {
            responderId = responderIdLookupStrategy.apply(input);
        } else {
            responderId = null;
        }
        
        if (nameQualifierLookupStrategy != null) {
            nameQualifier = nameQualifierLookupStrategy.apply(input);
        } else {
            nameQualifier = null;
        }

        if (spNameQualifierLookupStrategy != null) {
            spNameQualifier = spNameQualifierLookupStrategy.apply(input);
        } else {
            spNameQualifier = null;
        }
        
        return doApply(requesterId, responderId, nameQualifier, spNameQualifier);
    }
    
    /**
     * Apply the predicate to the request.
     * 
     * @param requesterId the requester
     * @param responderId the responder
     * @param nameQualifier the NameQualifier
     * @param spNameQualifier the SPNameQualifier
     * 
     * @return  true iff the combination of inputs satisfies a policy
     */
    protected abstract boolean doApply(@Nullable final String requesterId, @Nullable final String responderId,
            @Nullable final String nameQualifier, @Nullable final String spNameQualifier);
    
    /**
     * Lookup function that returns {@link org.opensaml.saml.saml2.core.RequestAbstractType#getIssuer()}
     * from a request message returned from a lookup function, by default the inbound message.
     */
    public static class RequesterIdFromIssuerFunction implements Function<ProfileRequestContext,String> {

        /** Strategy used to locate the {@link AuthnRequest} to operate on. */
        @Nonnull private Function<ProfileRequestContext,RequestAbstractType> requestLookupStrategy;
        
        /** Constructor. */
        public RequesterIdFromIssuerFunction() {
            requestLookupStrategy = Functions.compose(new MessageLookup<>(RequestAbstractType.class),
                    new InboundMessageContextLookup());
        }

        /**
         * Set the strategy used to locate the {@link RequestAbstractType} to examine.
         * 
         * @param strategy strategy used to locate the {@link RequestAbstractType}
         */
        public void setRequestLookupStrategy(
                @Nonnull final Function<ProfileRequestContext,RequestAbstractType> strategy) {
            requestLookupStrategy = Constraint.isNotNull(strategy, "Request lookup strategy cannot be null");
        }
        
        /** {@inheritDoc} */
        @Override
        @Nullable public String apply(@Nullable final ProfileRequestContext profileRequestContext) {
            
            final RequestAbstractType request = requestLookupStrategy.apply(profileRequestContext);
            if (request != null && request.getIssuer() != null) {
                final Issuer issuer = request.getIssuer();
                if (issuer.getFormat() == null || NameID.ENTITY.equals(issuer.getFormat())) {
                    return issuer.getValue();
                }
            }
            
            return null;
        }
        
    }
    
    /**
     * Lookup function that returns {@link org.opensaml.saml.saml2.core.NameIDPolicy#getSPNameQualifier()}
     * from an {@link AuthnRequest} message returned from a lookup function, by default the inbound message.
     */
    public static class SPNameQualifierFromNameIDPolicyFunction implements Function<ProfileRequestContext,String> {

        /** Strategy used to locate the {@link AuthnRequest} to operate on. */
        @Nonnull private Function<ProfileRequestContext,AuthnRequest> requestLookupStrategy;
        
        /** Constructor. */
        public SPNameQualifierFromNameIDPolicyFunction() {
            requestLookupStrategy =
                    Functions.compose(new MessageLookup<>(AuthnRequest.class), new InboundMessageContextLookup());
        }

        /**
         * Set the strategy used to locate the {@link AuthnRequest} to examine.
         * 
         * @param strategy strategy used to locate the {@link AuthnRequest}
         */
        public void setRequestLookupStrategy(@Nonnull final Function<ProfileRequestContext,AuthnRequest> strategy) {
            requestLookupStrategy = Constraint.isNotNull(strategy, "AuthnRequest lookup strategy cannot be null");
        }
        
        /** {@inheritDoc} */
        @Override
        @Nullable public String apply(@Nullable final ProfileRequestContext profileRequestContext) {
            
            final AuthnRequest request = requestLookupStrategy.apply(profileRequestContext);

            if (request != null && request.getNameIDPolicy() != null) {
                return request.getNameIDPolicy().getSPNameQualifier();
            }
            
            return null;
        }
        
    }
}