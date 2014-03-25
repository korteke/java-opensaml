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
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;

import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.saml1.core.NameIdentifier;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * Base class for implementations of {@link NameIDPolicyPredicate} that handles all the basic lookup
 * functions and calls the {@link #doApply(String, String, String, String)} method to do actual work.
 */
public abstract class AbstractNameIDPolicyPredicate extends AbstractInitializableComponent
        implements Predicate<ProfileRequestContext> {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AbstractNameIDPolicyPredicate.class);

    /** Requester ID lookup function. */
    @Nullable private Function<ProfileRequestContext,String> requesterIdLookupStrategy;

    /** Responder ID lookup function. */
    @Nullable private Function<ProfileRequestContext,String> responderIdLookupStrategy;

    /** NameIDPolicy lookup function. */
    @Nullable private Function<ProfileRequestContext,NameIDPolicy> nameIDPolicyLookupStrategy;

    /** NameID lookup function. */
    @Nullable private Function<ProfileRequestContext,NameID> nameIDLookupStrategy;

    /** NameIdentifier lookup function. */
    @Nullable private Function<ProfileRequestContext,NameIdentifier> nameIdentifierLookupStrategy;

    /**
     * Set the strategy used to locate the requester ID.
     * 
     * @param strategy lookup strategy
     */
    public synchronized void setRequesterIdLookupStrategy(
            @Nullable final Function<ProfileRequestContext,String> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        requesterIdLookupStrategy = strategy;
    }

    /**
     * Set the strategy used to locate the responder ID.
     * 
     * @param strategy lookup strategy
     */
    public synchronized void setResponderIdLookupStrategy(
            @Nullable final Function<ProfileRequestContext,String> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        responderIdLookupStrategy = strategy;
    }
    
    /**
     * Set a lookup strategy used to locate a {@link NameIDPolicy} to evaluate.
     * 
     * @param strategy lookup function
     */
    public synchronized void setNameIDPolicyLookupStrategy(
            @Nullable final Function<ProfileRequestContext,NameIDPolicy> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        nameIDPolicyLookupStrategy = strategy;
    }

    /**
     * Set a lookup strategy used to locate a {@link NameID} to evaluate.
     * 
     * @param strategy lookup function
     */
    public synchronized void setNameIDLookupStrategy(@Nullable final Function<ProfileRequestContext,NameID> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        nameIDLookupStrategy = strategy;
    }

    /**
     * Set a lookup strategy used to locate a {@link NameIdentifier} to evaluate.
     * 
     * @param strategy lookup function
     */
    public synchronized void setNameIdentifierLookupStrategy(
            @Nullable final Function<ProfileRequestContext,NameIdentifier> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        nameIdentifierLookupStrategy = strategy;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (nameIDPolicyLookupStrategy == null && nameIDLookupStrategy == null
                && nameIdentifierLookupStrategy == null) {
            throw new ComponentInitializationException(
                    "One of NameIDPolicy, NameID, or NameIdentifier lookup strategies must be non-null");
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean apply(@Nullable final ProfileRequestContext input) {
        
        final String requesterId;
        final String responderId;
        
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

        if (nameIdentifierLookupStrategy != null) {
            final NameIdentifier nameIdentifier = nameIdentifierLookupStrategy.apply(input);
            if (nameIdentifier != null) {
                return doApply(requesterId, responderId, nameIdentifier.getNameQualifier(), null);
            }
        }

        if (nameIDLookupStrategy != null) {
            final NameID nameID = nameIDLookupStrategy.apply(input);
            if (nameID != null) {
                return doApply(requesterId, responderId, nameID.getNameQualifier(), nameID.getSPNameQualifier());
            }
        }
        
        if (nameIDPolicyLookupStrategy != null) {
            final NameIDPolicy policy = nameIDPolicyLookupStrategy.apply(input);
            if (policy != null) {
                return doApply(requesterId, responderId, null, policy.getSPNameQualifier());
            }
        }
        
        // If no objects were found to evaluate, then there's no reason to fail.
        return true;
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
        
}