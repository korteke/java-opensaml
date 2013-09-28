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

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.apache.http.client.HttpClient;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.metadata.resolver.filter.FilterException;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * Abstract subclass for metadata resolvers that resolve metadata dynamically, as needed and on demand.
 */
public class AbstractDynamicMetadataResolver extends AbstractMetadataResolver {
    
    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(AbstractDynamicMetadataResolver.class);
    
    /** HTTP Client used to pull the metadata. */
    private HttpClient httpClient;
    
    /** Timer used to schedule background metadata update tasks. */
    private Timer taskTimer;
    
    /** Whether we created our own task timer during object construction. */
    private boolean createdOwnTaskTimer;
    
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
    
    /** {@inheritDoc} */
    @Nonnull public Iterable<EntityDescriptor> resolve(CriteriaSet criteria) throws ResolverException {
        ComponentSupport.ifNotInitializedThrowUninitializedComponentException(this);
        
        EntityIdCriterion entityIdCriterion = criteria.get(EntityIdCriterion.class);
        if (entityIdCriterion == null || Strings.isNullOrEmpty(entityIdCriterion.getEntityId())) {
            //TODO throw or just log?
            throw new ResolverException("Entity Id was not supplied in criteria set");
        }
        
        // TODO lock for reading, or otherwise deal with concurrency
        List<EntityDescriptor> descriptors = lookupEntityID(entityIdCriterion.getEntityId());
        
        if (!descriptors.isEmpty()) {
            return descriptors;
        } else {
            return fetchByCriteria(criteria);
        }
        
    }
    
    /**
     * @param criteria
     * @return
     */
    protected Iterable<EntityDescriptor> fetchByCriteria(CriteriaSet criteria) {
        // TODO Auto-generated method stub
        
        // TODO lock for writing, or otherwise deal with concurrency
        
        return null;
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
    }
    
    /** {@inheritDoc} */
    protected void doDestroy() {
        httpClient = null;
        
        //TODO cancel all timer tasks
        
        if (createdOwnTaskTimer) {
            taskTimer.cancel();
        }
        
        super.doDestroy();
    }

    /**
     * Process the specified new metadata document, including metadata filtering.
     * 
     * @param root the root of the new metadata document being processed
     * 
     * @throws FilterException if there is a problem filtering the metadata
     */
    @Nonnull protected void processNewMetadata(@Nonnull final XMLObject root) throws FilterException {
        
        // TODO NOT DONE! this is just some working idea
        
        XMLObject filteredMetadata = filterMetadata(root);
        
        if (filteredMetadata == null) {
            log.info("Metadata filtering process produced a null document, resulting in an empty data set");
            return;
        }
        
        // TODO concurrency
        
        if (filteredMetadata instanceof EntityDescriptor) {
            preProcessEntityDescriptor((EntityDescriptor)filteredMetadata, getBackingStore());
        } else if (filteredMetadata instanceof EntitiesDescriptor) {
            preProcessEntitiesDescriptor((EntitiesDescriptor)filteredMetadata, getBackingStore());
        } else {
            log.warn("Document root was neither an EntityDescriptor nor an EntitiesDescriptor: {}", 
                    root.getClass().getName());
        }
    
    }
    
    /**
     * Specialized entity backing store implementation for dynamic metadata resolvers.
     */
    protected class DynamicEntityBackingStore extends EntityBackingStore {
        
        private Map<String, EntityManagementData> mgmtData;
        
        /** Constructor. */
        protected DynamicEntityBackingStore() {
            super();
            mgmtData = new ConcurrentHashMap<>();
        }
        
        public Map<String, EntityManagementData> getManagementData() {
            return mgmtData;
        }
        
    }
    
    protected class EntityManagementData {
        
        private long lastAccessedTime;
        
        protected EntityManagementData() {
            lastAccessedTime = System.currentTimeMillis();
        }
        
        public long getLastAccessedTime() {
            return lastAccessedTime;
        }
        
        public void recordEntityAccess() {
            lastAccessedTime = System.currentTimeMillis();
        }
        
    }

}
