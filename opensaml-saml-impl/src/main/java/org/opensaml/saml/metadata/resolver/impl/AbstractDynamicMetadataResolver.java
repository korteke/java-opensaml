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
import java.util.Objects;
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
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.metadata.resolver.DynamicMetadataResolver;
import org.opensaml.saml.metadata.resolver.filter.FilterException;
import org.opensaml.saml.saml2.common.SAML2Support;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * Abstract subclass for metadata resolvers that resolve metadata dynamically, as needed and on demand.
 */
public abstract class AbstractDynamicMetadataResolver extends AbstractMetadataResolver 
        implements DynamicMetadataResolver {
    
    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(AbstractDynamicMetadataResolver.class);
    
    /** Timer used to schedule background metadata update tasks. */
    private Timer taskTimer;
    
    /** Whether we created our own task timer during object construction. */
    private boolean createdOwnTaskTimer;
    
    /** Minimum cache duration. */
    private Long minCacheDuration;
    
    /** Maximum cache duration. */
    private Long maxCacheDuration;
    
    /** The maximum idle time in milliseconds for which the resolver will keep data for a given entityID, 
     * before it is removed. */
    private Long maxIdleEntityData;
    
    /** Flag indicating whether idle entity data should be removed. */
    private boolean removeIdleEntityData;
    
    /** The interval in milliseconds at which the cleanup task should run. */
    private Long cleanupTaskInterval;
    
    /** The backing store cleanup sweeper background task. */
    private BackingStoreCleanupSweeper cleanupTask;
    
    /**
     * Constructor.
     *
     * @param backgroundTaskTimer the {@link Timer} instance used to run resolver background managment tasks
     */
    public AbstractDynamicMetadataResolver(@Nullable final Timer backgroundTaskTimer) {
        super();
        
        if (backgroundTaskTimer == null) {
            taskTimer = new Timer(true);
            createdOwnTaskTimer = true;
        } else {
            taskTimer = backgroundTaskTimer;
        }
        
        // Default to 10 minutes.
        minCacheDuration = 10*60*1000L;
        
        // Default to 8 hours.
        maxCacheDuration = 8*60*60*1000L;
        
        // Default to 30 minutes.
        cleanupTaskInterval = 30*60*1000L;
        
        // Default to 8 hours.
        maxIdleEntityData = 8*60*60*1000L;
        
        // Default to removing idle metadata
        removeIdleEntityData = true;
    }
    
    /**
     *  Get the minimum cache duration for metadata.
     *  
     *  <p>Defaults to: 10 minutes.</p>
     *  
     * @return the minimum cache duration, in milliseconds
     */
    @Nonnull public Long getMinCacheDuration() {
        return minCacheDuration;
    }

    /**
     *  Set the minimum cache duration for metadata.
     *  
     *  <p>Defaults to: 10 minutes.</p>
     *  
     * @param duration the minimum cache duration, in milliseconds
     */
    public void setMinCacheDuration(@Nonnull final Long duration) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        minCacheDuration = Constraint.isNotNull(duration, "Minimum cache duration may not be null");
    }

    /**
     *  Get the maximum cache duration for metadata.
     *  
     *  <p>Defaults to: 8 hours.</p>
     *  
     * @return the maximum cache duration, in milliseconds
     */
    @Nonnull public Long getMaxCacheDuration() {
        return maxCacheDuration;
    }

    /**
     *  Set the maximum cache duration for metadata.
     *  
     *  <p>Defaults to: 8 hours.</p>
     *  
     * @param duration the maximum cache duration, in milliseconds
     */
    public void setMaxCacheDuration(@Nonnull final Long duration) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        maxCacheDuration = Constraint.isNotNull(duration, "Maximum cache duration may not be null");
    }

    /**
     * Get the flag indicating whether idle entity data should be removed. 
     * 
     * @return true if idle entity data should be removed, false otherwise
     */
    public boolean isRemoveIdleEntityData() {
        return removeIdleEntityData;
    }

    /**
     * Set the flag indicating whether idle entity data should be removed. 
     * 
     * @param flag true if idle entity data should be removed, false otherwise
     */
    public void setRemoveIdleEntityData(boolean flag) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        removeIdleEntityData = flag;
    }

    /**
     * Get the maximum idle time in milliseconds for which the resolver will keep data for a given entityID, 
     * before it is removed.
     * 
     * <p>Defaults to: 8 hours.</p>
     * 
     * @return return the maximum idle time in milliseconds
     */
    @Nonnull public Long getMaxIdleEntityData() {
        return maxIdleEntityData;
    }

    /**
     * Set the maximum idle time in milliseconds for which the resolver will keep data for a given entityID, 
     * before it is removed.
     * 
     * <p>Defaults to: 8 hours.</p>
     * 
     * @param max the maximum entity data idle time, in milliseconds
     */
    public void setMaxIdleEntityData(@Nonnull final Long max) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        maxIdleEntityData = Constraint.isNotNull(max, "Max idle entity data may not be null");
    }

    /**
     * Get the interval in milliseconds at which the cleanup task should run.
     * 
     * <p>Defaults to: 30 minutes.</p>
     * 
     * @return return the interval, in milliseconds
     */
    @Nonnull public Long getCleanupTaskInterval() {
        return cleanupTaskInterval;
    }

    /**
     * Set the interval in milliseconds at which the cleanup task should run.
     * 
     * <p>Defaults to: 30 minutes.</p>
     * 
     * @param interval the interval to set, in milliseconds
     */
    public void setCleanupTaskInterval(@Nonnull final Long interval) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        cleanupTaskInterval = Constraint.isNotNull(interval, "Cleanup task interval may not be null");
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
                log.trace("Found requested metadata in backing store, returning");
                return descriptors;
            }
        } finally {
            readLock.unlock();
        }
        
        log.trace("Did not find requested metadata in backing store, will attempt to fetch dynamically");
        
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
    @Nonnull @NonnullElements protected abstract Iterable<EntityDescriptor> fetchByCriteria(
            @Nonnull final CriteriaSet criteria) throws ResolverException;

    /** {@inheritDoc} */
    @Nonnull @NonnullElements protected List<EntityDescriptor> lookupEntityID(@Nonnull String entityID) 
            throws ResolverException {
        getBackingStore().getManagementData(entityID).recordEntityAccess();
        return super.lookupEntityID(entityID);
    }

    /**
     * Process the specified new metadata document, including metadata filtering, and store the 
     * processed metadata in the backing store.
     * 
     * <p>
     * In order to be processed successfully, the metadata (after filtering) must be an instance of
     * {@link EntityDescriptor} and its <code>entityID</code> value must match the value supplied
     * as the required <code>expectedEntityID</code> argument.
     * </p>
     * 
     * @param root the root of the new metadata document being processed
     * @param expectedEntityID the expected entityID of the resolved metadata
     * 
     * @throws FilterException if there is a problem filtering the metadata
     */
    @Nonnull protected void processNewMetadata(@Nonnull final XMLObject root, @Nonnull final String expectedEntityID) 
            throws FilterException {
        
        XMLObject filteredMetadata = filterMetadata(root);
        
        if (filteredMetadata == null) {
            log.info("Metadata filtering process produced a null document, resulting in an empty data set");
            return;
        }
        
        // TODO handling case where exists already one (or more) entries for a given entityID when this is called
        // - either have to overwrite, or somehow pick one or the otheri, or (?) merge.
        
        if (filteredMetadata instanceof EntityDescriptor) {
            EntityDescriptor entityDescriptor = (EntityDescriptor) filteredMetadata;
            if (!Objects.equals(entityDescriptor.getEntityID(), expectedEntityID)) {
                log.warn("New metadata's entityID '{}' does not match expected entityID '{}', will not process", 
                        entityDescriptor.getEntityID(), expectedEntityID);
               return; 
            }
            preProcessEntityDescriptor(entityDescriptor, getBackingStore());
        } else {
            log.warn("Document root was not an EntityDescriptor: {}", root.getClass().getName());
        }
    
    }
    
    /** {@inheritDoc} */
    protected void preProcessEntityDescriptor(@Nonnull EntityDescriptor entityDescriptor, 
            @Nonnull EntityBackingStore backingStore) {
        
        super.preProcessEntityDescriptor(entityDescriptor, backingStore);
        
        DynamicEntityBackingStore dynamicBackingStore = (DynamicEntityBackingStore) backingStore;
        EntityManagementData mgmtData = dynamicBackingStore.getManagementData(entityDescriptor.getEntityID());
        mgmtData.setExpirationTime(computeExpirationTime(entityDescriptor));
    }

    /**
     * Compute the effective expiration time for the specified metadata.
     * 
     * @param entityDescriptor the EntityDescriptor instance to evaluate
     * @return the effective expiration time for the metadata
     */
    @Nonnull protected DateTime computeExpirationTime(@Nonnull final EntityDescriptor entityDescriptor) {
        DateTime now = new DateTime(ISOChronology.getInstanceUTC());
        DateTime lowerBound = now.plus(getMinCacheDuration());
        
        DateTime expiration = SAML2Support.getEarliestExpiration(entityDescriptor, 
                now.plus(getMaxCacheDuration()), now);
        if (expiration.isBefore(lowerBound)) {
            expiration = lowerBound;
        }
        
        return expiration;
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
                    entityData = new EntityManagementData(entityID);
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
        
        /** The entity ID managed by this instance. */
        private String entityID;
        
        /** Expiration time of the associated metadata. */
        private DateTime expirationTime;
        
        /** The last time in milliseconds at which the entity's backing store data was accessed. */
        private DateTime lastAccessedTime;
        
        /** Read-write lock instance which governs access to the entity's backing store data. */
        private ReadWriteLock readWriteLock;
        
        /** Constructor. 
         * 
         * @param id the entity ID managed by this instance
         */
        protected EntityManagementData(@Nonnull final String id) {
            entityID = Constraint.isNotNull(id, "Entity ID was null");
            expirationTime = new DateTime(ISOChronology.getInstanceUTC()).plus(getMaxCacheDuration());
            lastAccessedTime = new DateTime(ISOChronology.getInstanceUTC());
            readWriteLock = new ReentrantReadWriteLock(true);
        }
        
        /**
         * Get the entity ID managed by this instance.
         * 
         * @return the entity ID
         */
        @Nonnull public String getEntityID() {
            return entityID;
        }
        
        /**
         * Get the expiration time of the metadata. 
         * 
         * @return the expiration time
         */
        @Nonnull public DateTime getExpirationTime() {
            return expirationTime;
        }

        /**
         * Set the expiration time of the metadata.
         * 
         * @param dateTime the new expiration time
         */
        public void setExpirationTime(@Nonnull final DateTime dateTime) {
            expirationTime = Constraint.isNotNull(dateTime, "Expiration time may not be null");
        }

        /**
         * Get the last time at which the entity's backing store data was accessed.
         * 
         * @return the time in milliseconds since the epoch
         */
        @Nonnull public DateTime getLastAccessedTime() {
            return lastAccessedTime;
        }
        
        /**
         * Record access of the entity's backing store data.
         */
        public void recordEntityAccess() {
            lastAccessedTime = new DateTime(ISOChronology.getInstanceUTC());
        }

        /**
         * Get the read-write lock instance which governs access to the entity's backing store data. 
         * 
         * @return the lock instance
         */
        @Nonnull public ReadWriteLock getReadWriteLock() {
            return readWriteLock;
        }
        
    }
    
    /**
     * Background maintenance task which cleans expired and idle metadata from the backing store, and removes
     * orphaned entity management data.
     */
    protected class BackingStoreCleanupSweeper extends TimerTask {
        
        /** Logger. */
        private final Logger log = LoggerFactory.getLogger(BackingStoreCleanupSweeper.class);

        /** {@inheritDoc} */
        public void run() {
            if (isDestroyed() || !isInitialized()) {
                // just in case the metadata resolver was destroyed before this task runs, 
                // or if it somehow is being called on a non-successfully-inited resolver instance.
                log.debug("BackingStoreCleanupSweeper will not run because: inited: {}, destroyed: {}",
                        isInitialized(), isDestroyed());
                return;
            }
            
            removeExpiredAndIdleMetadata();
            
            // Cleanup mgmt data entries that don't have any indexed descriptors associated with them
            getBackingStore().cleanupOrphanedManagementData();
        }

        /**
         *  Purge metadata which is either 1) expired or 2) (if {@link #isRemoveIdleEntityData()} is true) 
         *  which hasn't been accessed within the last {@link #getMaxIdleEntityData()} milliseconds.
         */
        private void removeExpiredAndIdleMetadata() {
            DateTime now = new DateTime(ISOChronology.getInstanceUTC());
            DateTime earliestValidLastAccessed = now.minus(getMaxIdleEntityData());
            
            DynamicEntityBackingStore backingStore = getBackingStore();
            Map<String, List<EntityDescriptor>> indexedDescriptors = backingStore.getIndexedDescriptors();
            
            for (String entityID : indexedDescriptors.keySet()) {
                Lock writeLock = backingStore.getManagementData(entityID).getReadWriteLock().writeLock();
                try {
                    writeLock.lock();
                    
                    EntityManagementData mgmtData = backingStore.getManagementData(entityID);
                    
                    if (isRemoveData(mgmtData, now, earliestValidLastAccessed)) {
                        backingStore.getOrderedDescriptors().removeAll(indexedDescriptors.get(entityID));
                        indexedDescriptors.remove(entityID);
                        backingStore.removeManagementData(entityID);
                    }
                    
                } finally {
                    writeLock.unlock();
                }
            }
            
        }
        
        /**
         * Determine whether metadata should be removed based on expiration and idle time data.
         * 
         * @param mgmtData the management data instance for the entity
         * @param now the current time
         * @param earliestValidLastAccessed the earliest last accessed time which would be valid
         * 
         * @return true if the entity is expired or exceeds the max idle time, false otherwise
         */
        private boolean isRemoveData(EntityManagementData mgmtData, DateTime now, DateTime earliestValidLastAccessed) {
            if (isRemoveIdleEntityData() && mgmtData.getLastAccessedTime().isBefore(earliestValidLastAccessed)) {
                log.debug("Entity metadata exceeds maximum idle time, removing: {}", mgmtData.getEntityID());
                return true;
            } else if (now.isAfter(mgmtData.getExpirationTime())) {
                log.debug("Entity metadata is expired, removing: {}", mgmtData.getEntityID());
                return true;
            } else {
                return false;
            }
        }
        
    }

}
