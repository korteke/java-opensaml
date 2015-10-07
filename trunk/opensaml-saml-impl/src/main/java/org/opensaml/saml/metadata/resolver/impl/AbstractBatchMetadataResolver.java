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

package org.opensaml.saml.metadata.resolver.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotLive;
import net.shibboleth.utilities.java.support.annotation.constraint.Unmodifiable;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.metadata.IterableMetadataSource;
import org.opensaml.saml.metadata.resolver.BatchMetadataResolver;
import org.opensaml.saml.metadata.resolver.filter.FilterException;
import org.opensaml.saml.metadata.resolver.index.MetadataIndex;
import org.opensaml.saml.metadata.resolver.index.impl.MetadataIndexManager;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;

/**
 * Abstract subclass for metadata resolvers that process and resolve metadata at a given point 
 * in time from a single metadata source document.
 */
public abstract class AbstractBatchMetadataResolver extends AbstractMetadataResolver 
        implements BatchMetadataResolver, IterableMetadataSource {
    
    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(AbstractBatchMetadataResolver.class);
    
    /** Flag indicating whether to cache the original source metadata document. */
    private boolean cacheSourceMetadata;
    
    /** The set of indexes configured. */
    private Set<MetadataIndex> indexes;
    
    /** Constructor. */
    public AbstractBatchMetadataResolver() {
        super();
        
        indexes = Collections.emptySet();
        
        setCacheSourceMetadata(true);
    }
    
    /** {@inheritDoc} */
    @Override
    public Iterator<EntityDescriptor> iterator() {
        ComponentSupport.ifNotInitializedThrowUninitializedComponentException(this);
        return Collections.unmodifiableList(getBackingStore().getOrderedDescriptors()).iterator();
    }

    /**
     * Get whether to cache the original source metadata document.
     * 
     * @return true if source should be cached, false otherwise
     */
    protected boolean isCacheSourceMetadata() {
        return cacheSourceMetadata;
    }
    
    /**
     * Set whether to cache the original source metadata document.
     * 
     * @param flag true if source should be cached, false otherwise
     */
    protected void setCacheSourceMetadata(boolean flag) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        cacheSourceMetadata = flag; 
    }
    
    /**
     * Get the configured indexes.
     * 
     * @return the set of configured indexes
     */
    @Nonnull @NonnullElements @Unmodifiable @NotLive public Set<MetadataIndex> getIndexes() {
        return ImmutableSet.copyOf(indexes);
    }

    /**
     * Set the configured indexes.
     * 
     * @param newIndexes the new indexes to set
     */
    public void setIndexes(@Nullable final Set<MetadataIndex> newIndexes) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        if (newIndexes == null) {
            indexes = Collections.emptySet();
        } else {
            indexes = new HashSet<>(Collections2.filter(newIndexes, Predicates.notNull()));
        }
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull public Iterable<EntityDescriptor> resolve(CriteriaSet criteria) throws ResolverException {
        ComponentSupport.ifNotInitializedThrowUninitializedComponentException(this);
        
        //TODO add filtering for entity role, protocol? maybe
        //TODO add filtering for binding? probably not, belongs better in RoleDescriptorResolver
        
        EntityIdCriterion entityIdCriterion = criteria.get(EntityIdCriterion.class);
        if (entityIdCriterion != null) {
            return lookupEntityID(entityIdCriterion.getEntityId());
        } else {
            return lookupByIndexes(criteria);
        }
        
    }
    
    /**
     * Resolve the set up descriptors based on the indexes currently held.
     * 
     * @param criteria the criteria set to process
     * 
     * @return descriptors resolved via indexes, and based on the input criteria set. May be empty.
     */
    @Nonnull @NonnullElements 
    protected Set<EntityDescriptor> lookupByIndexes(@Nonnull final CriteriaSet criteria) {
        return getBackingStore().getSecondaryIndexManager().lookupEntityDescriptors(criteria);
    }
    
    /** {@inheritDoc} */
    @Override
    protected void indexEntityDescriptor(@Nonnull final EntityDescriptor entityDescriptor, 
            @Nonnull final EntityBackingStore backingStore) {
        super.indexEntityDescriptor(entityDescriptor, backingStore);
        
        ((BatchEntityBackingStore)backingStore).getSecondaryIndexManager().indexEntityDescriptor(entityDescriptor);
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull protected BatchEntityBackingStore createNewBackingStore() {
        return new BatchEntityBackingStore(getIndexes());
    }
    
    /** {@inheritDoc} */
    @Override
    @Nonnull protected BatchEntityBackingStore getBackingStore() {
        return (BatchEntityBackingStore) super.getBackingStore();
    }
    
    /** {@inheritDoc} */
    @Override
    protected void initMetadataResolver() throws ComponentInitializationException {
        super.initMetadataResolver();
        // Init this to an empty instance to ensure we always have a non-null instance,
        // even if initialization in the subclass fails for whatever reason.
        // Most subclasses will replace this with a new populated instance.
        setBackingStore(createNewBackingStore());
    }

    /**
     * Convenience method for getting the current effective cached original metadata.
     * 
     * <p>
     * Note: may or may not be the same as that obtained from {@link #getCachedFilteredMetadata()},
     * depending on what metadata filtering produced from the original metadata document.
     * </p>
     * 
     * @return the current effective cached metadata document
     */
    @Nullable protected XMLObject getCachedOriginalMetadata() {
       return getBackingStore().getCachedOriginalMetadata(); 
    }
    
    /**
     * Convenience method for getting the current effective cached filtered metadata.
     * 
     * <p>
     * Note: may or may not be the same as that obtained from {@link #getCachedOriginalMetadata()},
     * depending on what metadata filtering produced from the original metadata document.
     * </p>
     * 
     * @return the current effective cached metadata document
     */
    @Nullable protected XMLObject getCachedFilteredMetadata() {
       return getBackingStore().getCachedFilteredMetadata(); 
    }

    /**
     * Process the specified new metadata document, including metadata filtering 
     * and return its data in a new entity backing store instance.
     * 
     * @param root the root of the new metadata document being processed
     * 
     * @return the new backing store instance
     * 
     * @throws FilterException if there is a problem filtering the metadata
     */
    @Nonnull protected BatchEntityBackingStore preProcessNewMetadata(@Nonnull final XMLObject root) 
            throws FilterException {
        
        BatchEntityBackingStore newBackingStore = createNewBackingStore();
        
        XMLObject filteredMetadata = filterMetadata(root);
        
        if (isCacheSourceMetadata()) {
            newBackingStore.setCachedOriginalMetadata(root);
            newBackingStore.setCachedFilteredMetadata(filteredMetadata);
        } 
        
        if (filteredMetadata == null) {
            log.info("Metadata filtering process produced a null document, resulting in an empty data set");
            return newBackingStore;
        }
        
        if (filteredMetadata instanceof EntityDescriptor) {
            preProcessEntityDescriptor((EntityDescriptor)filteredMetadata, newBackingStore);
        } else if (filteredMetadata instanceof EntitiesDescriptor) {
            preProcessEntitiesDescriptor((EntitiesDescriptor)filteredMetadata, newBackingStore);
        } else {
            log.warn("Document root was neither an EntityDescriptor nor an EntitiesDescriptor: {}", 
                    root.getClass().getName());
        }
        
        return newBackingStore;
    }

    /**
     * Specialized entity backing store implementation for batch metadata resolvers.
     * 
     * <p>
     * Adds the following to parent impl:
     * <ol>
     * <li>capable of storing the original metadata document on which the backing store is based</li>
     * <li>stores data for any secondary indexes defined</li>
     * </ol>
     * </p>
     */
    protected class BatchEntityBackingStore extends EntityBackingStore {
        
        /** The cached original source metadata document. */
        private XMLObject cachedOriginalMetadata;
        
        /** The cached original source metadata document. */
        private XMLObject cachedFilteredMetadata;
        
        /** Manager for secondary indexes. */
        private MetadataIndexManager secondaryIndexManager;
        
        /**
         * Constructor.
         *
         * @param initIndexes secondary indexes for which to initialize storage
         */
        protected BatchEntityBackingStore(
                @Nullable @NonnullElements @Unmodifiable @NotLive Set<MetadataIndex> initIndexes) {
            super();
            secondaryIndexManager = new MetadataIndexManager(initIndexes);
        }

        /**
         * Get the cached original source metadata.
         * 
         * @return the cached metadata
         */
        public XMLObject getCachedOriginalMetadata() {
            return cachedOriginalMetadata;
        }

        /**
         * Set the cached original source metadata.
         * 
         * @param metadata The new cached metadata
         */
        public void setCachedOriginalMetadata(XMLObject metadata) {
            cachedOriginalMetadata = metadata;
        }
        
        /**
         * Get the cached filtered source metadata.
         * 
         * @return the cached metadata
         */
        public XMLObject getCachedFilteredMetadata() {
            return cachedFilteredMetadata;
        }

        /**
         * Set the cached filtered source metadata.
         * 
         * @param metadata The new cached metadata
         */
        public void setCachedFilteredMetadata(XMLObject metadata) {
            cachedFilteredMetadata = metadata;
        }
        
        /**
         * Get the secondary index manager.
         * 
         * @return the manager for secondary indexes
         */
        public MetadataIndexManager getSecondaryIndexManager() {
            return secondaryIndexManager;
        }
        
    }

}
