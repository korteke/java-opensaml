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

package org.opensaml.saml.metadata.resolver.index.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotLive;
import net.shibboleth.utilities.java.support.annotation.constraint.Unmodifiable;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.saml.metadata.resolver.index.MetadataIndexKey;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;

import com.google.common.collect.ImmutableSet;

/**
 * Component which stores indexed instances of {@link EntityDescriptor} under one or more instances
 * of {@link MetadataIndexKey}.
 */
public class MetadataIndexStore {
    
    /** The indexed storage of entity descriptors. */
    @Nonnull private Map<MetadataIndexKey, Set<EntityDescriptor>> index;
    
    /**
     * Constructor.
     */
    public MetadataIndexStore() {
        index = new ConcurrentHashMap<>();
    }
    
    /**
     * Get the set of all {@link MetadataIndexKey} instances currently indexed.
     * 
     * @return the set of all currently indexed keys
     */
    @Nonnull @NonnullElements @Unmodifiable @NotLive 
    public Set<MetadataIndexKey> getKeys() {
        return ImmutableSet.copyOf(index.keySet());
    }
    
    /**
     * Lookup the instances of {@link EntityDescriptor} indexed under the supplied {@link MetadataIndexKey}.
     * 
     * @param key the index key to lookup
     * @return the set of descriptors indexed under that key
     */
    @Nonnull @NonnullElements @Unmodifiable @NotLive 
    public Set<EntityDescriptor> lookup(@Nonnull final MetadataIndexKey key) {
        Constraint.isNotNull(key, "IndexKey was null");
        Set<EntityDescriptor> entities = index.get(key);
        if (entities == null) {
            return Collections.emptySet();
        } else {
            return ImmutableSet.copyOf(entities);
        }
    }
    
    /**
     * Add the supplied {@link EntityDescriptor} to the index under the supplied {@link MetadataIndexKey}.
     * 
     * @param key the index key
     * @param descriptor the descriptor to index
     */
    public void add(MetadataIndexKey key, EntityDescriptor descriptor) {
        Constraint.isNotNull(key, "IndexKey was null");
        Constraint.isNotNull(descriptor, "EntityDescriptor was null");
        Set<EntityDescriptor> entities = index.get(key);
        if (entities == null) {
            entities = new HashSet<>();
            index.put(key, entities);
        }
        entities.add(descriptor);
    }
    
    /**
     * Remove the supplied {@link EntityDescriptor} from the index under the supplied {@link MetadataIndexKey}.
     * 
     * @param key the index key
     * @param descriptor the descriptor to index
     */
    public void remove(MetadataIndexKey key, EntityDescriptor descriptor) {
        Constraint.isNotNull(key, "IndexKey was null");
        Constraint.isNotNull(descriptor, "EntityDescriptor was null");
        Set<EntityDescriptor> entities = index.get(key);
        if (entities == null) {
            return;
        }
        entities.remove(descriptor);
    }
    
    /**
     * Clear all indexed descriptors under the supplied {@link MetadataIndexKey}.
     * 
     * @param key the index key
     */
    public void clear(MetadataIndexKey key) {
        Constraint.isNotNull(key, "IndexKey was null");
        index.remove(key);
    }
    
    /**
     * Clear all indexed descriptors from the store.
     */
    public void clear() {
        index.clear();
    }

}
