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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import net.shibboleth.utilities.java.support.xml.ParserPool;
import net.shibboleth.utilities.java.support.xml.QNameSupport;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.criterion.EntityIdCriterion;
import org.opensaml.saml.metadata.resolver.filter.FilterException;
import org.opensaml.saml.saml2.common.SAML2Helper;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.common.base.Strings;

/** An abstract, base, implementation of a metadata provider. */
public abstract class AbstractMetadataResolver extends BaseMetadataResolver {
    
    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(AbstractMetadataResolver.class);

    /** Whether the metadata provider has been initialized. */
    private boolean initialized;

    /**
     * Whether problems during initialization should cause the provider to fail or go on without metadata. The
     * assumption being that in most cases a provider will recover at some point in the future. Default: true.
     */
    private boolean failFastInitialization;
    
    /** Backing store for runtime EntityDescriptor data. */
    private EntityBackingStore entityBackingStore;

    /** Pool of parsers used to process XML. */
    private ParserPool parser;

    /** Constructor. */
    public AbstractMetadataResolver() {
        super();
        failFastInitialization = true;
        initialized = false;
    }

    /** {@inheritDoc} */
    @Nonnull public Iterable<EntityDescriptor> resolve(CriteriaSet criteria) throws ResolverException {
        //TODO add filtering for entity role, protocol? maybe
        //TODO add filtering for binding? probably not, belongs better in RoleDescriptorResolver
        //TODO this needs to change substantially if we support queries *without* an EntityIdCriterion
        
        EntityIdCriterion entityIdCriterion = criteria.get(EntityIdCriterion.class);
        if (entityIdCriterion == null || Strings.isNullOrEmpty(entityIdCriterion.getEntityId())) {
            //TODO throw or just log?
            throw new ResolverException("Entity Id was not supplied in criteria set");
        }
        
        return lookupEntityID(entityIdCriterion.getEntityId());
    }
    
    /**
     * Gets whether this provider is initialized.
     * 
     * @return whether this provider is initialized
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Sets whether this provider is initialized.
     * 
     * @param isInitialized whether this provider is initialized
     */
    protected void setInitialized(boolean isInitialized) {
        initialized = isInitialized;
    }

    /**
     * Gets whether problems during initialization should cause the provider to fail or go on without metadata. The
     * assumption being that in most cases a provider will recover at some point in the future.
     * 
     * @return whether problems during initialization should cause the provider to fail
     */
    public boolean isFailFastInitialization() {
        return failFastInitialization;
    }

    /**
     * Sets whether problems during initialization should cause the provider to fail or go on without metadata. The
     * assumption being that in most cases a provider will recover at some point in the future.
     * 
     * @param failFast whether problems during initialization should cause the provider to fail
     */
    public void setFailFastInitialization(boolean failFast) {
        if (isInitialized()) {
            return;
        }

        failFastInitialization = failFast;
    }

    /**
     * Gets the pool of parsers to use to parse XML.
     * 
     * @return pool of parsers to use to parse XML
     */
    @Nonnull public ParserPool getParserPool() {
        return parser;
    }

    /**
     * Sets the pool of parsers to use to parse XML.
     * 
     * @param pool pool of parsers to use to parse XML
     */
    public void setParserPool(@Nonnull final ParserPool pool) {
        parser = Constraint.isNotNull(pool, "ParserPool may not be null");
    }

    /**
     * Initializes this metadata provider. If called after the metadata provider has already been initialized this
     * method simply returns.
     * 
     * @throws ResolverException thrown if there is a problem initializing the problem and fail fast
     *             Initialization is enabled
     */
    public synchronized void initialize() throws ResolverException {
        if (initialized) {
            return;
        }

        try {
            doInitialization();
            initialized = true;
        } catch (ResolverException e) {
            if (failFastInitialization) {
                log.error("Metadata provider failed to properly initialize, fail-fast=true, halting", e);
                throw e;
            } else {
                log.error("Metadata provider failed to properly initialize, fail-fast=false, " 
                        + "continuing on in a degraded state", e);
                initialized = true;
            }
        }
    }

    /** {@inheritDoc} */
    public synchronized void destroy() {
        initialized = false;
        entityBackingStore = null;
        parser = null;

        super.destroy();        
    }
    
    /**
     * Subclasses should override this method to perform any initialization logic necessary. Default implementation is a
     * no-op.
     * 
     * @throws ResolverException thrown if there is a problem initializing the provider
     */
    protected void doInitialization() throws ResolverException {

    }

    /**
     * Unmarshalls the metadata from the given stream. The stream is closed by this method and the returned metadata
     * released its DOM representation.
     * 
     * @param metadataInput the input reader to the metadata.
     * 
     * @return the unmarshalled metadata
     * 
     * @throws UnmarshallingException thrown if the metadata can no be unmarshalled
     */
    @Nonnull protected XMLObject unmarshallMetadata(@Nonnull final InputStream metadataInput) 
            throws UnmarshallingException {
        
        try {
            log.trace("Parsing retrieved metadata into a DOM object");
            Document mdDocument = parser.parse(metadataInput);

            log.trace("Unmarshalling and caching metdata DOM");
            Unmarshaller unmarshaller = getUnmarshallerFactory().getUnmarshaller(mdDocument.getDocumentElement());
            if (unmarshaller == null) {
                String msg ="No unmarshaller registered for document element " + QNameSupport
                        .getNodeQName(mdDocument.getDocumentElement());
                log.error(msg);
                throw new UnmarshallingException(msg);
            }
            XMLObject metadata = unmarshaller.unmarshall(mdDocument.getDocumentElement());
            return metadata;
        } catch (Exception e) {
            throw new UnmarshallingException(e);
        } finally {
            try {
                metadataInput.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    /**
     * Filters the given metadata.
     * 
     * @param metadata the metadata to be filtered
     * 
     * @throws FilterException thrown if there is an error filtering the metadata
     */
    protected void filterMetadata(@Nullable final XMLObject metadata) throws FilterException {
        if (getMetadataFilter() != null) {
            log.debug("Applying metadata filter");
            getMetadataFilter().doFilter(metadata);
        }
    }

    /**
     * Releases the DOM representation from the metadata object.
     * 
     * @param metadata the metadata object
     */
    protected void releaseMetadataDOM(@Nullable final XMLObject metadata) {
        if (metadata != null) {
            metadata.releaseDOM();
            metadata.releaseChildrenDOM(true);
        }
    }

    /**
     * Returns whether the given descriptor is valid. If valid metadata is not required this method always returns true.
     * 
     * @param descriptor the descriptor to check
     * 
     * @return true if valid metadata is not required or the given descriptor is valid, false otherwise
     */
    protected boolean isValid(@Nullable final XMLObject descriptor) {
        if (descriptor == null) {
            return false;
        }

        if (!isRequireValidMetadata()) {
            return true;
        }

        return SAML2Helper.isValid(descriptor);
    }

    /** {@inheritDoc} */
    @Nonnull protected List<EntityDescriptor> lookupEntityID(@Nonnull final String entityID) throws ResolverException {
        if (!isInitialized()) {
            throw new ResolverException("Metadata provider has not been initialized");
        }

        if (Strings.isNullOrEmpty(entityID)) {
            log.debug("EntityDescriptor entityID was null or empty, skipping search for it");
            return null;
        }

        List<EntityDescriptor> descriptors = lookupIndexedEntityID(entityID);
        if (descriptors.isEmpty()) {
            log.debug("Metadata document does not contain any EntityDescriptors with the ID {}", entityID);
            return descriptors;
        }
        
        Iterator<EntityDescriptor> entitiesIter = descriptors.iterator();
        while (entitiesIter.hasNext()) {
            EntityDescriptor descriptor = entitiesIter.next();
            if (!isValid(descriptor)) {
                log.debug("Metadata document contained an EntityDescriptor with the ID {}, but it was no longer valid",
                        entityID);
                entitiesIter.remove();
            }
        }

        return descriptors;
    }
    
    /**
     * Lookup the specified entityID from the index. The returned list will be a copy of what is
     * stored in the backing index, and is safe to be manipulated by callers.
     * 
     * @param entityID the entityID to lookup
     * 
     * @return list copy of indexed entityID's, may be empty, will never be null
     */
    @Nonnull protected List<EntityDescriptor> lookupIndexedEntityID(@Nonnull final String entityID) {
        List<EntityDescriptor> descriptors = getBackingStore().getIndexedDescriptors().get(entityID);
        if (descriptors != null) {
            return new ArrayList<EntityDescriptor>(descriptors);
        } else {
            return Collections.emptyList();
        }
    }
    
    /**
     * Create a new backing store instance for EntityDescriptor data.  Subclasses may override to return
     * a more specialized subclass type. Note this method does not make the returned backing store
     * the effective one in use.  The caller is responsible for calling 
     * {@link #setBackingStore(EntityBackingStore)} to make it the effective instance in use.
     * 
     * @return the new backing store instance
     */
    @Nonnull protected EntityBackingStore createNewBackingStore() {
        return new EntityBackingStore();
    }
    
    /**
     * Get the EntityDescriptor backing store currently in use by the metadata resolver.
     * 
     * @return the current effective entity backing store
     */
    @Nonnull protected EntityBackingStore getBackingStore() {
       return entityBackingStore;
    }
    
    /**
     * Set the EntityDescriptor backing store currently in use by the metadata resolver.
     * 
     * @param newBackingStore the new entity backing store
     */
    protected void setBackingStore(@Nonnull EntityBackingStore newBackingStore) {
       entityBackingStore = Constraint.isNotNull(newBackingStore, "EntityBackingStore may not be null");
    }

    /**
     * Pre-process the specified entity descriptor, updating the specified entity backing
     * store instance as necessary.
     * 
     * @param entityDescriptor the target entity descriptor to process
     * @param backingStore the backing store instance to update
     */
    protected void preProcessEntityDescriptor(@Nonnull final EntityDescriptor entityDescriptor, 
            @Nonnull final EntityBackingStore backingStore) {
        
        backingStore.getOrderedDescriptors().add(entityDescriptor);
        indexEntityDescriptor(entityDescriptor, backingStore);
    }
    
    /**
     * Index the specified entity descriptor, updating the specified entity backing
     * store instance as necessary.
     * 
     * @param entityDescriptor the target entity descriptor to process
     * @param backingStore the backing store instance to update
     */
    protected void indexEntityDescriptor(@Nonnull final EntityDescriptor entityDescriptor, 
            @Nonnull final EntityBackingStore backingStore) {
        
        String entityID = StringSupport.trimOrNull(entityDescriptor.getEntityID());
        if (entityID != null) {
            List<EntityDescriptor> entities = backingStore.getIndexedDescriptors().get(entityID);
            if (entities == null) {
                entities = new ArrayList<EntityDescriptor>();
                backingStore.getIndexedDescriptors().put(entityID, entities);
            } else if (!entities.isEmpty()) {
                log.warn("Detected duplicate EntityDescriptor for entityID: {}", entityID);
            }
            entities.add(entityDescriptor);
        }
    }

    /**
     * Pre-process the specified entities descriptor, updating the specified entity backing
     * store instance as necessary.
     * 
     * @param entitiesDescriptor the target entities descriptor to process
     * @param backingStore the backing store instance to update
     */
    protected void preProcessEntitiesDescriptor(@Nonnull final EntitiesDescriptor entitiesDescriptor, 
            EntityBackingStore backingStore) {
        
        for (XMLObject child : entitiesDescriptor.getOrderedChildren()) {
            if (child instanceof EntityDescriptor) {
                preProcessEntityDescriptor((EntityDescriptor) child, backingStore);
            } else if (child instanceof EntitiesDescriptor) {
                preProcessEntitiesDescriptor((EntitiesDescriptor) child, backingStore);
            }
        }
    }
    
    /**
     * The collection of data which provides the backing store for the processed metadata.
     */
    protected class EntityBackingStore {
        
        /** Index of entity IDs to their descriptors. */
        private Map<String, List<EntityDescriptor>> indexedDescriptors;
        
        /** Ordered list of entity descriptors. */
        private List<EntityDescriptor> orderedDescriptors;
        
        /** Constructor. */
        protected EntityBackingStore() {
            indexedDescriptors = new ConcurrentHashMap<>();
            orderedDescriptors = new ArrayList<>();
        }

        /**
         * Get the entity descriptor index.
         * 
         * @return the entity descriptor index
         */
        @Nonnull public Map<String, List<EntityDescriptor>> getIndexedDescriptors() {
            return indexedDescriptors;
        }
        
        /**
         * Get the ordered entity descriptor list.
         * 
         * @return the entity descriptor list
         */
        @Nonnull public List<EntityDescriptor> getOrderedDescriptors() {
            return orderedDescriptors;
        }
        
    }

}