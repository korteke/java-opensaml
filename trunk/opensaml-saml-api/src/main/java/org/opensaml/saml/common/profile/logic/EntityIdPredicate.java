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

import org.opensaml.saml.saml2.metadata.EntityDescriptor;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

/**
 * Predicate that matches an {@link EntityDescriptor} against a set of entityIDs.
 */
public class EntityIdPredicate implements Predicate<EntityDescriptor> {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(EntityIdPredicate.class);
    
    /** Set of entityIDs to check for. */
    @Nonnull @NonnullElements private final Set<String> entityIds;
    
    /**
     * Constructor.
     * 
     * @param ids the entityIDs to check for
     */
    public EntityIdPredicate(@Nonnull @NonnullElements final Collection<String> ids) {
        Constraint.isNotNull(ids, "EntityID collection cannot be null");
        
        entityIds = Sets.newHashSetWithExpectedSize(ids.size());
        for (final String id : ids) {
            final String trimmed = StringSupport.trimOrNull(id);
            if (trimmed != null) {
                entityIds.add(trimmed);
            }
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean apply(@Nullable final EntityDescriptor input) {
        
        if (input == null || input.getEntityID() == null) {
            return false;
        }
        
        return entityIds.contains(input.getEntityID());
    }

}