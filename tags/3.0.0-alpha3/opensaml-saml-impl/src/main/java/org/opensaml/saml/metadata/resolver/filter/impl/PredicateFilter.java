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

package org.opensaml.saml.metadata.resolver.filter.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.metadata.resolver.filter.FilterException;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

/**
 * A filter that removes any {@link EntityDescriptor} that does or does not match a {@link Predicate}, thus
 * a whitelist or blacklist. 
 * 
 * <p>If an {@link EntitiesDescriptor} does not contain any children after filtering it may, optionally, be removed as
 * well. If the root element of the metadata document is an @link EntitiesDescriptor}, it will never be removed,
 * regardless of of whether it still has children.</p>
 */
public class PredicateFilter implements MetadataFilter {

    /** Whether matching means to include or exclude an entity. */
    public enum Direction { INCLUDE, EXCLUDE, };
    
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(PredicateFilter.class);
    
    /** Whether matching means to include or exclude an entity. */
    @Nonnull private final Direction direction;

    /** Matching predicate. */
    @Nonnull private final Predicate<EntityDescriptor> condition;
    
    /** Whether to keep entities descriptors that contain no entity descriptors; default value: true. */
    private boolean removeEmptyEntitiesDescriptors;

    /**
     * Constructor.
     * 
     * @param dir whether to whitelist or blacklist
     * @param theCondition the predicate to apply to determine inclusion or exclusion
     */
    public PredicateFilter(@Nonnull final Direction dir, @Nonnull final Predicate<EntityDescriptor> theCondition) {

        condition = Constraint.isNotNull(theCondition, "Matching condition cannot be null");
        direction = Constraint.isNotNull(dir, "Direction cannot be null");
        removeEmptyEntitiesDescriptors = true;
    }
    
    /**
     * Get the direction of filtering.
     * 
     * @return filtering direction
     */
    @Nonnull public Direction getDirection() {
        return direction;
    }
    
    /**
     * Get the predicate to be applied.
     * 
     * @return the predicate to be applied
     */
    @Nonnull public Predicate<EntityDescriptor> getCondition() {
        return condition;
    }

    /**
     * Get whether to remove an entities descriptor if it does not contain any entity descriptor or entities
     * descriptors.
     * 
     * @return whether to remove an entities descriptor if it does not contain any entity descriptor or entities
     *         descriptors
     */
    public boolean getRemoveEmptyEntitiesDescriptors() {
        return removeEmptyEntitiesDescriptors;
    }

    /**
     * Set whether to remove an entities descriptor if it does not contain any entity descriptor or entities
     * descriptors.
     * 
     * @param remove whether to remove an entities descriptor if it does not contain any entity descriptor or entities
     *            descriptors
     */
    public void setRemoveEmptyEntitiesDescriptors(final boolean remove) {
        removeEmptyEntitiesDescriptors = remove;
    }

    /** {@inheritDoc} */
    @Override
    public XMLObject filter(@Nullable final XMLObject metadata) throws FilterException {
        
        if (metadata == null) {
            return null;
        }

        if (metadata instanceof EntitiesDescriptor) {
            filterEntitiesDescriptor((EntitiesDescriptor) metadata);
            return metadata;
        } else if (condition.apply((EntityDescriptor) metadata)) {
            return Direction.INCLUDE.equals(direction) ?  metadata : null;
        } else {
            return null;
        }
    }

    /**
     * Filters entities descriptor.
     * 
     * @param descriptor entities descriptor to filter
     */
    protected void filterEntitiesDescriptor(@Nonnull final EntitiesDescriptor descriptor) {
        
        // First we check any contained EntityDescriptors.
        final List<EntityDescriptor> entityDescriptors = descriptor.getEntityDescriptors();
        if (!entityDescriptors.isEmpty()) {
            final List<EntityDescriptor> emptyEntityDescriptors = new ArrayList<>();
            final Iterator<EntityDescriptor> entityDescriptorsItr = entityDescriptors.iterator();
            while (entityDescriptorsItr.hasNext()) {
                final EntityDescriptor entityDescriptor = entityDescriptorsItr.next();
                if (Direction.EXCLUDE.equals(direction) == condition.apply(entityDescriptor)) {
                    log.trace("Filtering out entity {} from group {}", entityDescriptor.getEntityID(),
                            descriptor.getName());
                    emptyEntityDescriptors.add(entityDescriptor);
                }
            }
            entityDescriptors.removeAll(emptyEntityDescriptors);
        }

        // Next, check contained EntityDescriptors.
        final List<EntitiesDescriptor> entitiesDescriptors = descriptor.getEntitiesDescriptors();
        if (!entitiesDescriptors.isEmpty()) {
            final List<EntitiesDescriptor> emptyEntitiesDescriptors = new ArrayList<>();
            final Iterator<EntitiesDescriptor> entitiesDescriptorsItr = entitiesDescriptors.iterator();
            while (entitiesDescriptorsItr.hasNext()) {
                final EntitiesDescriptor entitiesDescriptor = entitiesDescriptorsItr.next();
                filterEntitiesDescriptor(entitiesDescriptor);
                if (getRemoveEmptyEntitiesDescriptors()) {
                    // Remove the EntitiesDescriptor if empty.
                    if (entitiesDescriptor.getEntityDescriptors().isEmpty()
                            && entitiesDescriptor.getEntitiesDescriptors().isEmpty()) {
                        log.trace("Filtering out empty group {} from group {}", entitiesDescriptor.getName(),
                                descriptor.getName());
                        emptyEntitiesDescriptors.add(entitiesDescriptor);
                    }
                }
            }
            entitiesDescriptors.removeAll(emptyEntitiesDescriptors);
        }
    }
    
}