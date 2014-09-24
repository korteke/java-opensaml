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

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotLive;
import net.shibboleth.utilities.java.support.annotation.constraint.Unmodifiable;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.saml.ext.saml2mdrpi.RegistrationInfo;
import org.opensaml.saml.ext.saml2mdrpi.RegistrationPolicy;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Predicate to determine whether one of a set of names matches an entity's
 * {@link RegistrationPolicy}.
 */
public class RegistrationPolicyPredicate  extends AbstractRegistrationInfoPredicate {

    /** Policies to match on. */
    @Nonnull @NonnullElements private final Set<String> policySet;
    
    /**
     * Constructor.
     * 
     * @param policies the policies to test for
     */
    public RegistrationPolicyPredicate(@Nonnull @NonnullElements final Collection<String> policies) {
        
        Constraint.isNotNull(policies, "Authority name collection cannot be null");
        policySet = Sets.newHashSetWithExpectedSize(policies.size());
        for (final String policy : policies) {
            final String trimmed = StringSupport.trimOrNull(policy);
            if (trimmed != null) {
                policies.add(trimmed);
            }
        }
    }

    /**
     * Get the policy criteria.
     * 
     * @return  the policy criteria
     */
    @Nonnull @NonnullElements @Unmodifiable @NotLive public Set<String> getPolicies() {
        return ImmutableSet.copyOf(policySet);
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doApply(@Nonnull final RegistrationInfo info) {
        for (final RegistrationPolicy policy : info.getRegistrationPolicies()) {
            if (policySet.contains(policy.getValue())) {
                return true;
            }
        }
        
        return false;
    }

}