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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.component.ComponentSupport;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.metadata.resolver.filter.FilterException;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract subclass for metadata resolvers that process and resolve metadata at a given point 
 * in time from a single metadata source document.
 */
public abstract class AbstractBatchMetadataResolver extends AbstractMetadataResolver {
    
    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(AbstractBatchMetadataResolver.class);
    
    /** Flag indicating whether to cache the original source metadata document. */
    private boolean cacheSourceMetadata;
    
    /** Constructor. */
    public AbstractBatchMetadataResolver() {
        super();
        setCacheSourceMetadata(true);
    }

    /**
     * Get whether to cache the original source metadata document.
     * 
     * @return true if source should be cached, false otherwise
     */
    public boolean isCacheSourceMetadata() {
        return cacheSourceMetadata;
    }
    
    /**
     * Set whether to cache the original source metadata document.
     * 
     * @param flag true if source should be cached, false otherwise
     */
    public void setCacheSourceMetadata(boolean flag) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        cacheSourceMetadata = flag; 
    }
    
    /** {@inheritDoc} */
    @Nonnull protected BatchEntityBackingStore createNewBackingStore() {
        return new BatchEntityBackingStore();
    }
    
    /** {@inheritDoc} */
    @Nonnull protected BatchEntityBackingStore getBackingStore() {
        return (BatchEntityBackingStore) super.getBackingStore();
    }

    /**
     * Convenience method for getting the current effective cached metadata.
     * 
     * <p>
     * Will only return non null if 
     * </p>
     * 
     * @return the current effective cached metadata document
     */
    @Nullable protected XMLObject getCachedMetadata() {
       return getBackingStore().getCachedMetadata(); 
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
        
        if (isCacheSourceMetadata()) {
            newBackingStore.setCachedMetadata(root);
        } 
        
        filterMetadata(root);
        
        if (root instanceof EntityDescriptor) {
            preProcessEntityDescriptor((EntityDescriptor)root, newBackingStore);
        } else if (root instanceof EntitiesDescriptor) {
            preProcessEntitiesDescriptor((EntitiesDescriptor)root, newBackingStore);
        } else {
            log.warn("Document root was neither an EntityDescriptor nor an EntitiesDescriptor: {}", 
                    root.getClass().getName());
        }
        
        return newBackingStore;
    }

    /**
     * Specialized entity backing store implementation which is capable of storing the original metadata 
     * document on which the backing store is based.
     */
    protected class BatchEntityBackingStore extends EntityBackingStore {
        
        /** The cached metadata document. */
        private XMLObject cachedMetadata;
        
        /** Constructor. */
        protected BatchEntityBackingStore() {
            super();
        }

        /**
         * Get the cached metadata.
         * 
         * @return the cached metadata
         */
        public XMLObject getCachedMetadata() {
            return cachedMetadata;
        }

        /**
         * Set the cached metadata.
         * 
         * @param metadata The new cached metadata
         */
        public void setCachedMetadata(XMLObject metadata) {
            cachedMetadata = metadata;
        }
        
    }

}
