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

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.component.AbstractIdentifiableInitializableComponent;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.messaging.context.navigate.MessageLookup;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.InboundMessageContextLookup;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;

/**
 * Evaluates a {@link NameIDPolicy} inside an {@link AuthnRequest} located via a lookup strategy,
 * by default the incoming message context, and enforces a default policy over its content.
 * 
 * <p>If {@link NameIDPolicy#getSPNameQualifier()} is non-null, the value must match the
 * request issuer.</p>
 */
public class DefaultNameIDPolicyPredicate extends AbstractIdentifiableInitializableComponent
        implements Predicate<ProfileRequestContext> {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(DefaultNameIDPolicyPredicate.class);
    
    /** Strategy used to locate the {@link AuthnRequest} to operate on, if any. */
    @Nonnull private Function<ProfileRequestContext,AuthnRequest> requestLookupStrategy;
    
    /** Constructor. */
    public DefaultNameIDPolicyPredicate() {
        requestLookupStrategy =
                Functions.compose(new MessageLookup<>(AuthnRequest.class), new InboundMessageContextLookup());
    }

    /**
     * Set the strategy used to locate the {@link AuthnRequest} to examine, if any.
     * 
     * @param strategy strategy used to locate the {@link AuthnRequest}
     */
    public synchronized void setRequestLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,AuthnRequest> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        requestLookupStrategy = Constraint.isNotNull(strategy, "AuthnRequest lookup strategy cannot be null");
    }

    /** {@inheritDoc} */
    @Override
    public boolean apply(@Nullable final ProfileRequestContext input) {
        final AuthnRequest request = requestLookupStrategy.apply(input);
        if (request == null) {
            log.debug("No AuthnRequest returned by lookup strategy");
            return true;
        }
        
        final NameIDPolicy policy = request.getNameIDPolicy();
        if (policy == null) {
            log.debug("AuthnRequest did contain a NameIDPolicy element");
            return true;
        }
        
        final String qualifier = policy.getSPNameQualifier();
        if (qualifier == null) {
            log.debug("NameIDPolicy did not contain an SPNameQualifier");
            return true;
        }
        
        final Issuer issuer = request.getIssuer();
        if (issuer != null && issuer.getValue() != null &&
                (issuer.getFormat() == null || NameID.ENTITY.equals(issuer.getFormat()))) {
            return doApply(request, issuer.getValue(), qualifier);
        } else {
            log.debug("Request did not contain an Issuer");
        }
        
        return false;
    }
    
    /**
     * Apply a predicate to the request.
     * 
     * @param request   the request
     * @param issuer    request issuer
     * @param qualifier the qualifier
     * 
     * @return  true iff the combination of inputs satisfies a policy
     */
    protected boolean doApply(@Nonnull final AuthnRequest request, @Nonnull @NotEmpty final String issuer,
            @Nonnull @NotEmpty final String qualifier) {
        
        if (qualifier.equals(issuer)) {
            log.debug("Requested SPNqmeQualifier {} matches request issuer", qualifier);
            return true;
        } else {
            log.debug("Requested SPNameQualifier {} did not match request issuer", qualifier);
            return false;
        }
    }
    
}