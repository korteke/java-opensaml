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
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;

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
    
    // TODO constructor defaults and getter/setter for this
    // TODO right name?
    private long maxLastAccessedInterval;
    
    /**
     * Constructor.
     *
     * @param client
     */
    public AbstractDynamicMetadataResolver(HttpClient client) {
        this(null, client);
    }
    
    /**
     * Constructor.
     *
     * @param backgroundTaskTimer
     * @param client
     */
    public AbstractDynamicMetadataResolver(Timer backgroundTaskTimer, HttpClient client) {
        super();
        
        httpClient = Constraint.isNotNull(client, "HttpClient may not be null");
        
        if (backgroundTaskTimer == null) {
            taskTimer = new Timer(true);
            createdOwnTaskTimer = true;
        } else {
            taskTimer = backgroundTaskTimer;
        }
    }
    
    /**
     * Get the list of supported MIME types for use in Accept request header and validation of 
     * response Content-Type header.
     * 
     * @return the supported content types
     */
    public List<String> getSupportedContentTypes() {
        return supportedContentTypes;
    }

    /**
     * Set the list of supported MIME types for use in Accept request header and validation of 
     * response Content-Type header.
     * 
     * @param types the new supported content types to set
     */
    public void setSupportedContentTypes(List<String> types) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        supportedContentTypes = types;
    }

    /** {@inheritDoc} */
    @Nonnull public Iterable<EntityDescriptor> resolve(CriteriaSet criteria) throws ResolverException {
        ComponentSupport.ifNotInitializedThrowUninitializedComponentException(this);
        
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
     * @param criteria
     * @return
     * @throws ResolverException 
     */
    protected Iterable<EntityDescriptor> fetchByCriteria(CriteriaSet criteria) throws ResolverException {
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
        
            HttpResponse response = httpClient.execute(request);
            
            processResponse(response, request.getURI());
            
            return lookupEntityID(entityID);
            
        } catch (IOException e) {
            throw new ResolverException("Error executing HTTP request", e);
        } finally {
            writeLock.unlock();
        }
        
    }

    /** {@inheritDoc} */
    @Nonnull protected List<EntityDescriptor> lookupEntityID(@Nonnull String entityID) throws ResolverException {
        getBackingStore().getManagementData(entityID).recordEntityAccess();
        return super.lookupEntityID(entityID);
    }

    /**
     * @param url
     * @return
     */
    protected HttpUriRequest buildHttpRequest(CriteriaSet criteria) {
        String url = buildRequestURL(criteria);
        log.debug("Built request URL of: {}", url);
            
        HttpGet getMethod = new HttpGet(url);
        
        if (!Strings.isNullOrEmpty(supportedContentTypesValue)) {
            getMethod.addHeader("Accept", supportedContentTypesValue);
        }
        
        // TODO other headers ?
        
        return getMethod;
    }

    /**
     * @param criteria
     * @return
     */
    protected abstract String buildRequestURL(CriteriaSet criteria);
    
    /**
     * @param response
     * @param requestURI
     * @throws ResolverException
     */
    protected void processResponse(HttpResponse response, URI requestURI) throws ResolverException {
        int httpStatusCode = response.getStatusLine().getStatusCode();
        
        // TODO should we be seeing/doing this? Probably not if we don't do conditional GET.
        // But we will if we do pre-emptive refreshing of metadata in background thread.
        if (httpStatusCode == HttpStatus.SC_NOT_MODIFIED) {
            log.debug("Metadata document from '{}' has not changed since last retrieval", requestURI);
            return;
        }

        if (httpStatusCode != HttpStatus.SC_OK) {
            String errMsg = "Non-ok status code " + httpStatusCode + " returned from remote metadata source: " 
                    + requestURI;
            log.error(errMsg);
            throw new ResolverException(errMsg);
        }
        
        
        XMLObject root = null;
        try {
            try {
                validateResponse(response, requestURI);
            } catch (ResolverException e) {
                // TODO for now don't treat this as fatal, just return. Maybe re-evaluate.
                log.error("Problem validating dynamic metadata HTTP response", e);
                return;
            }
            
            try {
                InputStream ins = response.getEntity().getContent();
                root = unmarshallMetadata(ins);
            } catch (IOException | UnmarshallingException e) {
                // TODO for now don't treat this as fatal, just return. Maybe re-evaluate.
                log.error("Error unmarshalling HTTP response stream", e);
                return;
            }
        } finally {
            closeResponse(response, requestURI);
        }
            
        try {
            processNewMetadata(root);
        } catch (FilterException e) {
            // TODO for now don't treat this as fatal, just return. Maybe re-evaluate.
            log.error("Metadata filtering problem processing new metadata", e);
            return;
        }
        
    }

    /**
     * @param response
     * @param requestURI
     */
    protected void closeResponse(HttpResponse response, URI requestURI) {
        if (response instanceof CloseableHttpResponse) {
            try {
                ((CloseableHttpResponse)response).close();
            } catch (final IOException e) {
                log.error("Error closing HTTP response from {}", requestURI, e);
            }
        }
    }
    
    /**
     * @param response
     * @param requestURI
     * @throws ResolverException
     */
    public void validateResponse(HttpResponse response, URI requestURI) throws ResolverException {
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
     * Process the specified new metadata document, including metadata filtering.
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
    @Nonnull protected DynamicEntityBackingStore getBackingStore() {
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
        
        // TODO create and init backing store cleanup sweeper
    }
    
   /** {@inheritDoc} */
    protected void doDestroy() {
        httpClient = null;
        
        supportedContentTypes = null;
        supportedContentTypesValue = null;
        
        //TODO cancel all timer tasks
        
        if (createdOwnTaskTimer) {
            taskTimer.cancel();
        }
        
        super.doDestroy();
    }
    
    /**
     * Specialized entity backing store implementation for dynamic metadata resolvers.
     */
    protected class DynamicEntityBackingStore extends EntityBackingStore {
        
        private Map<String, EntityManagementData> mgmtDataMap;
        
        /** Constructor. */
        protected DynamicEntityBackingStore() {
            super();
            mgmtDataMap = new ConcurrentHashMap<>();
        }
        
        public EntityManagementData getManagementData(String entityID) {
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
        
        public void removeManagementData(String entityID) {
            Constraint.isNotNull(entityID, "EntityID may not be null");
            // TODO use intern-ed String here for monitor target?
            synchronized (this) {
                mgmtDataMap.remove(entityID);
            }
        }
        
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
    
    protected class EntityManagementData {
        
        private long lastAccessedTime;
        
        private ReadWriteLock readWriteLock;
        
        protected EntityManagementData() {
            lastAccessedTime = System.currentTimeMillis();
            readWriteLock = new ReentrantReadWriteLock(true);
        }
        
        public long getLastAccessedTime() {
            return lastAccessedTime;
        }
        
        public void recordEntityAccess() {
            long current = System.currentTimeMillis();
            if (current > lastAccessedTime) {
                lastAccessedTime = System.currentTimeMillis();
            }
        }

        public ReadWriteLock getReadWriteLock() {
            return readWriteLock;
        }
        
    }
    
    protected class BackingStoreCleanupSweeper extends TimerTask {

        /** {@inheritDoc} */
        public void run() {
            if (!isInitialized()) {
                // just in case the metadata provider was destroyed before this task runs
                return;
            }
            
            // Purge entries that haven't been accessed in the specified interval
            long now = System.currentTimeMillis();
            long latestValid = now - maxLastAccessedInterval;
            
            DynamicEntityBackingStore backingStore = getBackingStore();
            Map<String, List<EntityDescriptor>> indexedDescriptors = backingStore.getIndexedDescriptors();
            
            for (String entityID : indexedDescriptors.keySet()) {
                Lock writeLock = backingStore.getManagementData(entityID).getReadWriteLock().writeLock();
                try {
                    writeLock.lock();
                    
                    if (backingStore.getManagementData(entityID).getLastAccessedTime() < latestValid) {
                        indexedDescriptors.remove(entityID);
                        // TODO do this here, or in later cleanup code
                        //backingStore.removeManagementData(entityID);
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
