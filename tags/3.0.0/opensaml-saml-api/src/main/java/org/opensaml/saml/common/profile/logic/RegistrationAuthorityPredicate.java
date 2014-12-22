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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Predicate to determine whether one of a set of names matches an entity's
 * {@link RegistrationInfo#getRegistrationAuthority()}.
 */
public class RegistrationAuthorityPredicate  extends AbstractRegistrationInfoPredicate {

    /** Authorities to match on. */
    @Nonnull @NonnullElements private final Set<String> authorities;
    
    /**
     * Constructor.
     * 
     * @param names the authority names to test for
     */
    public RegistrationAuthorityPredicate(@Nonnull @NonnullElements final Collection<String> names) {
        
        Constraint.isNotNull(names, "Authority name collection cannot be null");
        authorities = Sets.newHashSetWithExpectedSize(names.size());
        for (final String name : names) {
            final String trimmed = StringSupport.trimOrNull(name);
            if (trimmed != null) {
                authorities.add(trimmed);
            }
        }
    }

    /**
     * Get the authority name criteria.
     * 
     * @return  the authority name criteria
     */
    @Nonnull @NonnullElements @Unmodifiable @NotLive public Set<String> getAuthorities() {
        return ImmutableSet.copyOf(authorities);
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doApply(@Nonnull final RegistrationInfo info) {
        return authorities.contains(info.getRegistrationAuthority());
    }
    
}