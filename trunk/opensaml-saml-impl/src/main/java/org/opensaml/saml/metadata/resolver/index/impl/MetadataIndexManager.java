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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotLive;
import net.shibboleth.utilities.java.support.annotation.constraint.Unmodifiable;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.saml.metadata.resolver.index.MetadataIndex;
import org.opensaml.saml.metadata.resolver.index.MetadataIndexKey;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

/**
 * High-level component which handles index and lookup of {@link EntityDescriptor} instances,
 * based on a set of {@link MetadataIndex} instances currently held.
 */
public class MetadataIndexManager {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(MetadataIndexManager.class);
    
    /** Storage for secondary indexes. */
    private Map<MetadataIndex, MetadataIndexStore> indexes;
    
    /**
     * Constructor.
     *
     * @param initIndexes indexes for which to initialize storage
     */
    public MetadataIndexManager(
            @Nullable @NonnullElements @Unmodifiable @NotLive Set<MetadataIndex> initIndexes) {
        indexes = new ConcurrentHashMap<>();
        if (initIndexes != null) {
            for (MetadataIndex index : initIndexes) {
                log.trace("Initializing manager for index: {}", index);
                indexes.put(index, new MetadataIndexStore());
            }
        }
    }
    
    /**
     * Get the set of all {@link MetadataIndex} instances currently initialized.
     * 
     * @return the set of all current indexes
     */
    @Nonnull @NonnullElements @Unmodifiable @NotLive 
    public Set<MetadataIndex> getIndexes() {
        return ImmutableSet.copyOf(indexes.keySet());
    }
    
    /**
     * Get the {@link MetadataIndexStore} for the specified {@link MetadataIndex}.
     * 
     * @param index the index for which the store is desired
     * @return the index store for the index, may be null if index was not initialized 
     *         for this manager instance
     */
    @Nullable public MetadataIndexStore getStore(@Nonnull MetadataIndex index) {
        Constraint.isNotNull(index, "MetadataIndex was null");
        return indexes.get(index);
    }
    
    /**
     * Resolve the set of descriptors based on the indexes currently held.
     * 
     * @param criteria the criteria set to process
     * 
     * @return descriptors resolved via indexes, and based on the input criteria set. May be empty.
     */
    @Nonnull @NonnullElements
    public Set<EntityDescriptor> lookupEntityDescriptors(@Nonnull final CriteriaSet criteria) {
        HashSet<EntityDescriptor> descriptors = new HashSet<>();
        for (MetadataIndex index : indexes.keySet()) {
            Set<MetadataIndexKey> keys = index.generateKeys(criteria);
            if (keys != null) {
                MetadataIndexStore indexStore = indexes.get(index);
                for (MetadataIndexKey key : keys) {
                    descriptors.addAll(indexStore.lookup(key));
                }
            }
        }
        return descriptors;
    }
    
    /**
     * Index the specified descriptor based on the indexes currently held.
     * 
     * @param descriptor the entity descriptor to index
     */
    public void indexEntityDescriptor(@Nonnull final EntityDescriptor descriptor) {
        for (MetadataIndex index : indexes.keySet()) {
            Set<MetadataIndexKey> keys = index.generateKeys(descriptor);
            if (keys != null) {
                MetadataIndexStore store = indexes.get(index);
                for (MetadataIndexKey key : keys) {
                    store.add(key, descriptor);
                }
            }
        }
    }

}
