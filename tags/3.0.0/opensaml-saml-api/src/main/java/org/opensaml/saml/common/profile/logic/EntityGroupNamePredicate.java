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

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotLive;
import net.shibboleth.utilities.java.support.annotation.constraint.Unmodifiable;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.saml.metadata.EntityGroupName;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Predicate to determine whether one of a set of names matches any of an entity's containing
 * {@link org.opensaml.saml.saml2.metadata.EntitiesDescriptor} groups. 
 */
public class EntityGroupNamePredicate implements Predicate<EntityDescriptor> {
    
    /** Groups to match on. */
    @Nonnull @NonnullElements private final Set<String> groupNames;
    
    /**
     * Constructor.
     * 
     * @param names the group names to test for
     */
    public EntityGroupNamePredicate(@Nonnull @NonnullElements final Collection<String> names) {
        
        Constraint.isNotNull(names, "Group name collection cannot be null");
        groupNames = Sets.newHashSetWithExpectedSize(names.size());
        for (final String name : names) {
            final String trimmed = StringSupport.trimOrNull(name);
            if (trimmed != null) {
                groupNames.add(trimmed);
            }
        }
    }

    /**
     * Get the group name criteria.
     * 
     * @return  the group name criteria
     */
    @Nonnull @NonnullElements @Unmodifiable @NotLive public Set<String> getGroupNames() {
        return ImmutableSet.copyOf(groupNames);
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean apply(@Nullable final EntityDescriptor input) {
        if (input != null) {
            for (final EntityGroupName group : input.getObjectMetadata().get(EntityGroupName.class)) {
                if (groupNames.contains(group.getName())) {
                    return true;
                }
            }
        }
        
        return false;
    }

}