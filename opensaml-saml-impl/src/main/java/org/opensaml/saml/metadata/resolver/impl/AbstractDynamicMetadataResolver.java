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
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotLive;
import net.shibboleth.utilities.java.support.annotation.constraint.Unmodifiable;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.metadata.resolver.DynamicMetadataResolver;
import org.opensaml.saml.metadata.resolver.filter.FilterException;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.collections.Lists;

import com.google.common.base.Strings;

/**
 * Abstract subclass for metadata resolvers that resolve metadata dynamically, as needed and on demand.
 */
public abstract class AbstractDynamicMetadataResolver extends AbstractMetadataResolver 
        implements DynamicMetadataResolver {
    
    /** Default list of supported content MIME types. */
    public static final String[] DEFAULT_CONTENT_TYPES = 
            new String[] {"application/samlmetadata+xml", "application/xml", "text/xml"};
    
    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(AbstractDynamicMetadataResolver.class);
    
    /** HTTP Client used to pull the metadata. */
    private HttpClient httpClient;
    
    /** Timer used to schedule background metadata update tasks. */
    private Timer taskTimer;
    
    /** Whether we created our own task timer during object construction. */
    private boolean createdOwnTaskTimer;
    
    /** List of supported MIME types for use in Accept request header and validation of 
     * response Content-Type header.*/
    private List<String> supportedContentTypes;
    
    /** Generated Accept request header value. */
    private String supportedContentTypesValue;
    
    /** The maximum idle time for which the resolver will keep data for a given entityID, before it is removed. */
    private Long maxIdleEntityData;
    
    /** The interval in milliseconds at which the cleanup task should run. */
    private Long cleanupTaskInterval;
    
    /** The backing store cleanup sweeper background task. */
    private BackingStoreCleanupSweeper cleanupTask;
    
    /**
     * Constructor.
     *
     * @param client the instance of {@link HttpClient} used to fetch remote metadata
     */
    public AbstractDynamicMetadataResolver(@Nonnull final HttpClient client) {
        this(null, client);
    }
    
    /**
     * Constructor.
     *
     * @param backgroundTaskTimer the {@link Timer} instance used to run resolver background managment tasks
     * @param client the instance of {@link HttpClient} used to fetch remote metadata
     */
    public AbstractDynamicMetadataResolver(@Nullable final Timer backgroundTaskTimer, 
            @Nonnull final HttpClient client) {
        super();
        
        httpClient = Constraint.isNotNull(client, "HttpClient may not be null");
        
        if (backgroundTaskTimer == null) {
            taskTimer = new Timer(true);
            createdOwnTaskTimer = true;
        } else {
            taskTimer = backgroundTaskTimer;
        }
        
        // Default to 30 minutes.
        cleanupTaskInterval = 30*60*1000L;
    }
    
    /**
     * Get the maximum idle time for which the resolver will keep data for a given entityID, before it is removed.
     * 
     * @return return the maximum idle time in milliseconds, or null if not value
     */
    @Nullable public Long getMaxIdleEntityData() {
        return maxIdleEntityData;
    }

    /**
     * Set the maximum idle time for which the resolver will keep data for a given entityID, before it is removed.
     * 
     * @param max the maximum entity data idle time, in milliseconds
     */
    public void setMaxIdleEntityData(@Nullable final Long max) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        maxIdleEntityData = max;
    }

    /**
     * Get the interval in milliseconds at which the cleanup task should run.
     * 
     * <p>The default is 30 minutes</p>
     * 
     * @return return the interval, in milliseconds
     */
    @Nonnull public Long getCleanupTaskInterval() {
        return cleanupTaskInterval;
    }

    /**
     * Set the interval in milliseconds at which the cleanup task should run.
     * 
     * <p>The default is 30 minutes</p>
     * 
     * @param interval the interval to set, in milliseconds
     */
    public void setCleanupTaskInterval(@Nonnull final Long interval) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        cleanupTaskInterval = Constraint.isNotNull(interval, "Cleanup task interval may not be null");
    }

    /**
     * Get the list of supported MIME types for use in Accept request header and validation of 
     * response Content-Type header.
     * 
     * @return the supported content types
     */
    @NonnullAfterInit @NotLive @Unmodifiable public List<String> getSupportedContentTypes() {
        return supportedContentTypes;
    }

    /**
     * Set the list of supported MIME types for use in Accept request header and validation of 
     * response Content-Type header.
     * 
     * @param types the new supported content types to set
     */
    public void setSupportedContentTypes(@Nullable final List<String> types) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        if (types == null) {
            supportedContentTypes = Collections.emptyList();
        } else {
            supportedContentTypes = Lists.newArrayList(StringSupport.normalizeStringCollection(types));
        }
    }

    /** {@inheritDoc} */
    @Nonnull public Iterable<EntityDescriptor> resolve(@Nonnull final CriteriaSet criteria) throws ResolverException {
        ComponentSupport.ifNotInitializedThrowUninitializedComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        
        EntityIdCriterion entityIdCriterion = criteria.get(EntityIdCriterion.class);
        if (entityIdCriterion == null || Strings.isNullOrEmpty(entityIdCriterion.getEntityId())) {
            //TODO throw or just log?
            throw new ResolverException("Entity Id was not supplied in criteria set");
        }
        
        String entityID = StringSupport.trimOrNull(criteria.get(EntityIdCriterion.class).getEntityId());
        Lock readLock = getBackingStore().getManagementData(entityID).getReadWriteLock().readLock();
        try {
            readLock.lock();
            
            List<EntityDescriptor> descriptors = lookupEntityID(entityID);
            if (!descriptors.isEmpty()) {
                return descriptors;
            }
        } finally {
            readLock.unlock();
        }
        
        return fetchByCriteria(criteria);
        
    }
    
    /**
     * Fetch metadata from an origin source based on the input criteria, store it in the backing store 
     * and then return it.
     * 
     * @param criteria the input criteria set
     * @return the resolved metadata
     * @throws ResolverException  if there is a fatal error attempting to resolve the metadata
     */
    @Nonnull @NonnullElements protected Iterable<EntityDescriptor> fetchByCriteria(@Nonnull final CriteriaSet criteria) 
            throws ResolverException {
        String entityID = StringSupport.trimOrNull(criteria.get(EntityIdCriterion.class).getEntityId());
        Lock writeLock = getBackingStore().getManagementData(entityID).getReadWriteLock().writeLock(); 
        
        try {
            writeLock.lock();
            
            List<EntityDescriptor> descriptors = lookupEntityID(entityID);
            if (!descriptors.isEmpty()) {
                log.debug("Metadata was resolved and stored by another thread " 
                        + "while this thread was waiting on the write lock");
                return descriptors;
            }
            
            HttpUriRequest request = buildHttpRequest(criteria);
            if (request == null) {
                log.debug("Could not build request based on input criteria, unable to query");
                return Collections.emptyList();
            }
        
            HttpResponse response = httpClient.execute(request);
            
            processResponse(response, request.getURI());
            
            return lookupEntityID(entityID);
            
        } catch (IOException e) {
            log.error("Error executing HTTP request", e);
            return Collections.emptyList();
        } finally {
            writeLock.unlock();
        }
        
    }

    /** {@inheritDoc} */
    @Nonnull @NonnullElements protected List<EntityDescriptor> lookupEntityID(@Nonnull String entityID) 
            throws ResolverException {
        getBackingStore().getManagementData(entityID).recordEntityAccess();
        return super.lookupEntityID(entityID);
    }

    /**
     * Build an appropriate instance of {@link HttpUriRequest} based on the input criteria set.
     * 
     * @param criteria the input criteria set
     * @return the newly constructed request, or null if it can not be built from the supplied criteria
     */
    @Nullable protected HttpUriRequest buildHttpRequest(@Nonnull final CriteriaSet criteria) {
        String url = buildRequestURL(criteria);
        log.debug("Built request URL of: {}", url);
        
        if (url == null) {
            log.debug("Could not construct request URL from input criteria, unable to query");
            return null;
        }
            
        HttpGet getMethod = new HttpGet(url);
        
        if (!Strings.isNullOrEmpty(supportedContentTypesValue)) {
            getMethod.addHeader("Accept", supportedContentTypesValue);
        }
        
        // TODO other headers ?
        
        return getMethod;
    }

    /**
     * Build the request URL based on the input criteria set.
     * 
     * @param criteria the input criteria set
     * @return the request URL, or null if it can not be built based on the supplied criteria
     */
    @Nullable protected abstract String buildRequestURL(@Nonnull final CriteriaSet criteria);
    
    /**
     * Process the received HTTP response, including validating the response, unmarshalling the received metadata,
     * and storing the metadata in the backing store.  
     * 
     * @param response the received response
     * @param requestURI the original request URI
     * @throws ResolverException if there is a fatal error processing the response
     */
    protected void processResponse(@Nonnull final HttpResponse response, @Nonnull final URI requestURI) 
            throws ResolverException {
        
        int httpStatusCode = response.getStatusLine().getStatusCode();
        
        // TODO should we be seeing/doing this? Probably not if we don't do conditional GET.
        // But we will if we do pre-emptive refreshing of metadata in background thread.
        if (httpStatusCode == HttpStatus.SC_NOT_MODIFIED) {
            log.debug("Metadata document from '{}' has not changed since last retrieval", requestURI);
            return;
        }

        if (httpStatusCode != HttpStatus.SC_OK) {
            log.warn("Non-ok status code '{}' returned from remote metadata source: {}", httpStatusCode, requestURI);
            return;
        }
        
        
        XMLObject root = null;
        try {
            try {
                validateResponse(response, requestURI);
            } catch (ResolverException e) {
                log.error("Problem validating dynamic metadata HTTP response", e);
                return;
            }
            
            try {
                InputStream ins = response.getEntity().getContent();
                root = unmarshallMetadata(ins);
            } catch (IOException | UnmarshallingException e) {
                log.error("Error unmarshalling HTTP response stream", e);
                return;
            }
        } finally {
            closeResponse(response, requestURI);
        }
            
        try {
            processNewMetadata(root);
        } catch (FilterException e) {
            log.error("Metadata filtering problem processing new metadata", e);
            return;
        }
        
    }

    /**
     * Close the HTTP response instance.
     * 
     * @param response the received response
     * @param requestURI the original request URI
     */
    protected void closeResponse(@Nonnull final HttpResponse response, @Nonnull final URI requestURI) {
        if (response instanceof CloseableHttpResponse) {
            try {
                ((CloseableHttpResponse)response).close();
            } catch (final IOException e) {
                log.error("Error closing HTTP response from {}", requestURI, e);
            }
        }
    }
    
    /**
     * Validate the received HTTP response instance, such as checking for supported content types.
     * 
     * @param response the received response
     * @param requestURI the original request URI
     * @throws ResolverException if the response was not valid, or if there is a fatal error validating the response
     */
    public void validateResponse(@Nonnull final HttpResponse response, @Nonnull final URI requestURI) 
            throws ResolverException {
        if (!getSupportedContentTypes().isEmpty()) {
            Header contentType = response.getEntity().getContentType();
            if (contentType != null && contentType.getValue() != null) {
                if (!getSupportedContentTypes().contains(contentType.getValue())) {
                    throw new ResolverException("HTTP response specified an unsupported Content-Type: " 
                            + contentType.getValue());
                }
            }
        }
        
        // TODO other validation
        
    }

    /**
     * Process the specified new metadata document, including metadata filtering, and store the 
     * processed metadata in the backing store.
     * 
     * @param root the root of the new metadata document being processed
     * 
     * @throws FilterException if there is a problem filtering the metadata
     */
    @Nonnull protected void processNewMetadata(@Nonnull final XMLObject root) throws FilterException {
        
        XMLObject filteredMetadata = filterMetadata(root);
        
        if (filteredMetadata == null) {
            log.info("Metadata filtering process produced a null document, resulting in an empty data set");
            return;
        }
        
        // TODO if is EntitiesDescriptor with multiple entityID's, then our per-entityID lock is not right
        // - support EntitiesDescriptors case? It complicates things a lot.
        
        // TODO handling case where exists already one (or more) entries for a given entityID when this is called
        // - either have to overwrite, or somehow pick one or the otheri, or (?) merge.
        
        if (filteredMetadata instanceof EntityDescriptor) {
            preProcessEntityDescriptor((EntityDescriptor)filteredMetadata, getBackingStore());
        } else if (filteredMetadata instanceof EntitiesDescriptor) {
            preProcessEntitiesDescriptor((EntitiesDescriptor)filteredMetadata, getBackingStore());
        } else {
            log.warn("Document root was neither an EntityDescriptor nor an EntitiesDescriptor: {}", 
                    root.getClass().getName());
        }
    
    }
    
    /** {@inheritDoc} */
    @Nonnull protected DynamicEntityBackingStore createNewBackingStore() {
        return new DynamicEntityBackingStore();
    }
    
    /** {@inheritDoc} */
    @NonnullAfterInit protected DynamicEntityBackingStore getBackingStore() {
        return (DynamicEntityBackingStore) super.getBackingStore();
    }
    
    /** {@inheritDoc} */
    protected void initMetadataResolver() throws ComponentInitializationException {
        super.initMetadataResolver();
        setBackingStore(createNewBackingStore());
        
        if (getSupportedContentTypes() == null) {
            setSupportedContentTypes(Arrays.asList(DEFAULT_CONTENT_TYPES));
        }
        
        if (! getSupportedContentTypes().isEmpty()) {
            supportedContentTypesValue = StringSupport.listToStringValue(getSupportedContentTypes(), ", ");
        } 
        
        cleanupTask = new BackingStoreCleanupSweeper();
        // Start with a delay of 1 minute, run at the user-specified interval
        taskTimer.schedule(cleanupTask, 1*60*1000, getCleanupTaskInterval());
    }
    
   /** {@inheritDoc} */
    protected void doDestroy() {
        cleanupTask.cancel();
        if (createdOwnTaskTimer) {
            taskTimer.cancel();
        }
        cleanupTask = null;
        taskTimer = null;
        
        httpClient = null;
        
        supportedContentTypes = null;
        supportedContentTypesValue = null;
        
        super.doDestroy();
    }
    
    /**
     * Specialized entity backing store implementation for dynamic metadata resolvers.
     */
    protected class DynamicEntityBackingStore extends EntityBackingStore {
        
        /** Map holding management data for each entityID. */
        private Map<String, EntityManagementData> mgmtDataMap;
        
        /** Constructor. */
        protected DynamicEntityBackingStore() {
            super();
            mgmtDataMap = new ConcurrentHashMap<>();
        }
        
        /**
         * Get the management data for the specified entityID.
         * 
         * @param entityID the input entityID
         * @return the corresponding management data
         */
        @Nonnull public EntityManagementData getManagementData(@Nonnull final String entityID) {
            Constraint.isNotNull(entityID, "EntityID may not be null");
            EntityManagementData entityData = mgmtDataMap.get(entityID);
            if (entityData != null) {
                return entityData;
            }
            
            // TODO use intern-ed String here for monitor target?
            synchronized (this) {
                // Check again in case another thread beat us into the monitor
                entityData = mgmtDataMap.get(entityID);
                if (entityData != null) {
                    return entityData;
                } else {
                    entityData = new EntityManagementData();
                    mgmtDataMap.put(entityID, entityData);
                    return entityData;
                }
            }
        }
        
        /**
         * Remove the management data for the specified entityID.
         * 
         * @param entityID the input entityID
         */
        public void removeManagementData(@Nonnull final String entityID) {
            Constraint.isNotNull(entityID, "EntityID may not be null");
            // TODO use intern-ed String here for monitor target?
            synchronized (this) {
                mgmtDataMap.remove(entityID);
            }
        }
        
        /**
         * Remove management data instances which have been orphaned, meaning there is
         * no metadata for the corresponding entityID in the backing store.
         */
        public void cleanupOrphanedManagementData() {
            // TODO think have a race condition here 
            for (String entityID : mgmtDataMap.keySet()) {
                Lock writeLock = mgmtDataMap.get(entityID).getReadWriteLock().writeLock();
                try {
                    writeLock.lock();
                    
                    if (!getIndexedDescriptors().containsKey(entityID)) {
                        removeManagementData(entityID);
                    }
                    
                } finally {
                    writeLock.unlock();
                }
            }
        }
        
    }
    
    /**
     * Class holding per-entity management data.
     */
    protected class EntityManagementData {
        
        /** The last time in milliseconds at which the entity's backing store data was accessed. */
        private long lastAccessedTime;
        
        /** Read-write lock instance which governs access to the entity's backing store data. */
        private ReadWriteLock readWriteLock;
        
        /** Constructor. */
        protected EntityManagementData() {
            lastAccessedTime = System.currentTimeMillis();
            readWriteLock = new ReentrantReadWriteLock(true);
        }
        
        /**
         * Get the last time in milliseconds at which the entity's backing store data was accessed.
         * 
         * @return the time in milliseconds since the epoch
         */
        public long getLastAccessedTime() {
            return lastAccessedTime;
        }
        
        /**
         * Record access of the entity's backing store data.
         */
        public void recordEntityAccess() {
            long current = System.currentTimeMillis();
            if (current > lastAccessedTime) {
                lastAccessedTime = System.currentTimeMillis();
            }
        }

        /**
         * Get the read-write lock instance which governs access to the entity's backing store data. 
         * 
         * @return the lock instance
         */
        public ReadWriteLock getReadWriteLock() {
            return readWriteLock;
        }
        
    }
    
    /**
     * Background maintenance task which cleans idle metadata from the backing store, and removes
     * orphaned entity management data.
     */
    protected class BackingStoreCleanupSweeper extends TimerTask {

        /** {@inheritDoc} */
        public void run() {
            if (isDestroyed()) {
                // just in case the metadata provider was destroyed before this task runs
                return;
            }
            
            // Purge entries that haven't been accessed in the specified interval
            long now = System.currentTimeMillis();
            long latestValid = now - getMaxIdleEntityData();
            
            DynamicEntityBackingStore backingStore = getBackingStore();
            Map<String, List<EntityDescriptor>> indexedDescriptors = backingStore.getIndexedDescriptors();
            
            for (String entityID : indexedDescriptors.keySet()) {
                Lock writeLock = backingStore.getManagementData(entityID).getReadWriteLock().writeLock();
                try {
                    writeLock.lock();
                    
                    if (backingStore.getManagementData(entityID).getLastAccessedTime() < latestValid) {
                        indexedDescriptors.remove(entityID);
                        backingStore.removeManagementData(entityID);
                    }
                    
                } finally {
                    writeLock.unlock();
                }
            }
            
            // Cleanup mgmt data entries that don't have any indexed descriptors associated with them
            backingStore.cleanupOrphanedManagementData();
        }
        
    }

}
