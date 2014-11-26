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

import java.util.Collection;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.component.AbstractInitializableComponent;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml1.core.NameIdentifier;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

/**
 * Base class for implementations of {@link Predicate} that handle evaluation of name identifier content in various
 * scenarios.
 */
public abstract class AbstractNameIDPolicyPredicate extends AbstractInitializableComponent
        implements Predicate<ProfileRequestContext> {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AbstractNameIDPolicyPredicate.class);

    /** Requester ID lookup function. */
    @Nullable private Function<ProfileRequestContext,String> requesterIdLookupStrategy;

    /** Responder ID lookup function. */
    @Nullable private Function<ProfileRequestContext,String> responderIdLookupStrategy;

    /** Object lookup function. */
    @NonnullAfterInit private Function<ProfileRequestContext,SAMLObject> objectLookupStrategy;

    /** Formats to apply policy to. */
    @Nonnull @NonnullElements private Set<String> formats;
    
    /** Constructor. */
    public AbstractNameIDPolicyPredicate() {
        formats = Sets.newHashSet(NameID.TRANSIENT, NameID.PERSISTENT);
    }
    
    /**
     * Set the strategy used to locate the requester ID.
     * 
     * @param strategy lookup strategy
     */
    public void setRequesterIdLookupStrategy(@Nullable final Function<ProfileRequestContext,String> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        requesterIdLookupStrategy = strategy;
    }

    /**
     * Set the strategy used to locate the responder ID.
     * 
     * @param strategy lookup strategy
     */
    public void setResponderIdLookupStrategy(@Nullable final Function<ProfileRequestContext,String> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        responderIdLookupStrategy = strategy;
    }
    
    /**
     * Set the lookup strategy used to locate the object to evaluate.
     * 
     * @param strategy lookup function
     */
    public void setObjectLookupStrategy(@Nullable final Function<ProfileRequestContext,SAMLObject> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        objectLookupStrategy = Constraint.isNotNull(strategy, "Object lookup strategy cannot be null");
    }
    
    /**
     * Set the formats to apply the predicate to.
     * 
     * @param newFormats    formats to apply predicate to
     */
    public void setFormats(@Nonnull @NonnullElements final Collection<String> newFormats) {
        Constraint.isNotNull(formats, "Format collection cannot be null");
        
        for (final String s : newFormats) {
            final String trimmed = StringSupport.trimOrNull(s);
            if (trimmed != null) {
                formats.add(trimmed);
            }
        }
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (objectLookupStrategy == null) {
            throw new ComponentInitializationException("Object lookup strategy cannot be null");
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean apply(@Nullable final ProfileRequestContext input) {

        final SAMLObject target = objectLookupStrategy.apply(input);
        if (target == null) {
            log.debug("No object to operate on, returning true");
            return true;
        }
        
        if (target instanceof NameIdentifier) {
            return doApply(input, (NameIdentifier) target);
        } else if (target instanceof NameID) {
            return doApply(input, (NameID) target);
        } else if (target instanceof NameIDPolicy) {
            return doApply(input, (NameIDPolicy) target);
        } else {
            log.error("Lookup function returned an object of an unsupported type: {}", target.getElementQName());
            return false;
        }
    }

    /**
     * Apply policy to the target object.
     * 
     * @param input current profile request context
     * @param target target object
     * 
     * @return result of policy
     */
    private boolean doApply(@Nullable final ProfileRequestContext input, @Nonnull final NameIdentifier target) {
        final String requesterId = requesterIdLookupStrategy != null ? requesterIdLookupStrategy.apply(input) : null;
        final String responderId = responderIdLookupStrategy != null ? responderIdLookupStrategy.apply(input) : null;

        final String format = target.getFormat();
        if (formats.contains(format != null ? format : NameIdentifier.UNSPECIFIED)) {
            log.debug("Applying policy to NameIdentifier with Format {}",
                    format != null ? format : NameIdentifier.UNSPECIFIED);
            return doApply(requesterId, responderId, format, target.getNameQualifier(), null);
        } else {
            log.debug("Policy checking disabled for NameIdentifier Format {}",
                    format != null ? format : NameIdentifier.UNSPECIFIED);
            return true;
        }
    }

    /**
     * Apply policy to the target object.
     * 
     * @param input current profile request context
     * @param target target object
     * 
     * @return result of policy
     */
    private boolean doApply(@Nullable final ProfileRequestContext input, @Nonnull final NameID target) {
        final String requesterId = requesterIdLookupStrategy != null ? requesterIdLookupStrategy.apply(input) : null;
        final String responderId = responderIdLookupStrategy != null ? responderIdLookupStrategy.apply(input) : null;

        final String format = target.getFormat();
        if (formats.contains(format != null ? format : NameID.UNSPECIFIED)) {
            log.debug("Applying policy to NameID with Format {}", format != null ? format : NameID.UNSPECIFIED);
            return doApply(requesterId, responderId, format, target.getNameQualifier(), target.getSPNameQualifier());
        } else {
            log.debug("Policy checking disabled for NameID Format {}", format != null ? format : NameID.UNSPECIFIED);
            return true;
        }
    }
    
    /**
     * Apply policy to the target object.
     * 
     * @param input current profile request context
     * @param target target object
     * 
     * @return result of policy
     */
    private boolean doApply(@Nullable final ProfileRequestContext input, @Nonnull final NameIDPolicy target) {
        final String requesterId = requesterIdLookupStrategy != null ? requesterIdLookupStrategy.apply(input) : null;
        final String responderId = responderIdLookupStrategy != null ? responderIdLookupStrategy.apply(input) : null;

        final String format = target.getFormat();
        if (formats.contains(format != null ? format : NameID.UNSPECIFIED)) {
            log.debug("Applying policy to NameIDPolicy with Format {}", format != null ? format : NameID.UNSPECIFIED);
            return doApply(requesterId, responderId, format, null, target.getSPNameQualifier());
        } else {
            log.debug("Policy checking disabled for NameIDPolicy with Format {}",
                    format != null ? format : NameID.UNSPECIFIED);
            return true;
        }
    }
    
    /**
     * Apply the predicate to the request.
     * 
     * @param requesterId the requester
     * @param responderId the responder
     * @param format format of identifier
     * @param nameQualifier the NameQualifier
     * @param spNameQualifier the SPNameQualifier
     * 
     * @return  true iff the combination of inputs satisfies a policy
     */
    protected abstract boolean doApply(@Nullable final String requesterId, @Nullable final String responderId,
            @Nullable final String format, @Nullable final String nameQualifier,
            @Nullable final String spNameQualifier);
    
}