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

package org.opensaml.util.storage.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.collection.Pair;
import net.shibboleth.utilities.java.support.component.AbstractDestructableIdentifiableInitializableComponent;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentValidationException;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.util.storage.StorageCapabilities;
import org.opensaml.util.storage.StorageRecord;
import org.opensaml.util.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;


/**
 * Implementation of {@link StorageService} that stores data in-memory with no persistence.
 */
public class MemoryStorageService extends AbstractDestructableIdentifiableInitializableComponent implements
        StorageService {

    /** Static instance of capabilities interface. */
    private static final StorageCapabilities STORAGE_CAPS = new Capabilities();
    
    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(MemoryStorageService.class);

    /**
     * Number of milliseconds between cleanup checks. Default value: {@value} (5 minutes)
     */
    private long cleanupInterval = 300000;

    /** Timer used to schedule cleanup tasks. */
    private Timer cleanupTaskTimer;

    /** Task that cleans up expired records. */
    private CleanupTask cleanupTask;
    
    /** Map of contexts. */
    private Map<String, Map<String, MutableStorageRecord>> contextMap;
    
    /** A shared lock to synchronize access. */
    private ReadWriteLock lock;


    /**
     * Gets the number of milliseconds between one cleanup and another. A value of 0 or less indicates that no
     * cleanup will be performed.
     * 
     * @return number of milliseconds between one cleanup and another
     */
    public long getCleanupInterval() {
        return cleanupInterval;
    }

    /**
     * Sets the number of milliseconds between one cleanup and another. A value of 0 or less indicates that no
     * cleanup will be performed.
     * 
     * This setting cannot be changed after the service has been initialized.
     * 
     * @param interval number of milliseconds between one cleanup and another
     */
    public synchronized void setCleanupInterval(long interval) {
        if (isInitialized()) {
            return;
        }

        cleanupInterval = interval;
    }

    /**
     * Gets the timer used to schedule cleanup tasks.
     * 
     * @return timer used to schedule cleanup tasks
     */
    @Nullable public Timer getCleanupTaskTimer() {
        return cleanupTaskTimer;
    }

    /**
     * Sets the timer used to schedule cleanup tasks.
     * 
     * This setting can not be changed after the service has been initialized.
     * 
     * @param timer timer used to schedule configuration reload tasks
     */
    public synchronized void setCleanupTaskTimer(@Nullable final Timer timer) {
        if (isInitialized()) {
            return;
        }

        cleanupTaskTimer = timer;
    }
    
    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        contextMap = new HashMap<String, Map<String, MutableStorageRecord>>();
        lock = new ReentrantReadWriteLock(true);

        if (cleanupInterval > 0) {
            Constraint.isNotNull(cleanupTaskTimer, "Cleanup task timer cannot be null");
            cleanupTask = new CleanupTask();
            cleanupTaskTimer.schedule(cleanupTask, cleanupInterval, cleanupInterval);
        }
    }

    /** {@inheritDoc} */
    protected void doDestroy() {
        cleanupTask.cancel();
        cleanupTask = null;
        contextMap = null;
        lock = null;
        super.doDestroy();
    }

    /** {@inheritDoc} */
    public void validate() throws ComponentValidationException {

    }

    /** {@inheritDoc} */
    @Nonnull public StorageCapabilities getCapabilities() {
        return STORAGE_CAPS;
    }

    /** {@inheritDoc} */
    public boolean createString(@Nonnull @NotEmpty String context, @Nonnull @NotEmpty String key,
            @Nonnull @NotEmpty String value, @Nonnull Optional<Long> expiration) throws IOException {

        final Lock writeLock = lock.writeLock();
        
        try {
            writeLock.lock();
            
            // Create new context if necessary.
            Map<String, MutableStorageRecord> dataMap = contextMap.get(context);
            if (dataMap == null) {
                dataMap = new HashMap();
                contextMap.put(context, dataMap);
            }
            
            // Check for a duplicate.
            StorageRecord record = dataMap.get(key);
            if (record != null) {
                // Not yet expired?
                Optional<Long> exp = record.getExpiration();
                if (!exp.isPresent() || System.currentTimeMillis() < (exp.get() * 1000)) {
                    return false;
                }
                
                // It's dead, so we can just remove it now and create the new record.
            }
            
            dataMap.put(key, new MutableStorageRecord(value, expiration));
            log.debug("Inserted record '{}' in context '{}' with expiration '{}'",
                    new Object[] { key, context, expiration.or(0L)});
            
            return true;
            
        } finally {
            writeLock.unlock();
        }
    }

    /** {@inheritDoc} */
    @Nonnull public Pair<Optional<Integer>, Optional<StorageRecord>> readString(@Nonnull @NotEmpty String context,
            @Nonnull @NotEmpty String key, @Nonnull Optional<Integer> version) throws IOException {

        Lock readLock = lock.readLock();
        try {
            readLock.lock();
            
            Map<String, MutableStorageRecord> dataMap = contextMap.get(context);
            if (dataMap == null) {
                return new Pair(Optional.absent(), Optional.absent());
            }

            StorageRecord record = dataMap.get(key);
            if (record == null) {
                return new Pair(Optional.absent(), Optional.absent());
            } else {
                Optional<Long> exp = record.getExpiration();
                if (exp.isPresent() && System.currentTimeMillis() >= (exp.get() * 1000)) {
                    return new Pair(Optional.absent(), Optional.absent());
                }
            }
            
            if (version.isPresent() && record.getVersion() == version.get()) {
                // Nothing's changed, so just echo back the version.
                return new Pair(version, Optional.absent());
            }
            
            return new Pair(Optional.of(record.getVersion()), Optional.of(record));
            
        } finally {
            readLock.unlock();
        }
    }

    /** {@inheritDoc} */
    @Nonnull public Optional<Integer> updateString(@Nonnull @NotEmpty String context, @Nonnull @NotEmpty String key,
            @Nonnull Optional<String> value, @Nonnull Optional<Long> expiration, @Nonnull Optional<Integer> version)
            throws IOException {

        final Lock writeLock = lock.writeLock();
        
        try {
            writeLock.lock();

            Map<String, MutableStorageRecord> dataMap = contextMap.get(context);
            if (dataMap == null) {
                return Optional.absent();
            }            
            
            MutableStorageRecord record = dataMap.get(key);
            if (record == null) {
                return Optional.absent();
            } else {
                Optional<Long> exp = record.getExpiration();
                if (exp.isPresent() && System.currentTimeMillis() >= (exp.get() * 1000)) {
                    return Optional.absent();
                }
            }
    
            if (version.isPresent() && version.get() != record.getVersion()) {
                // Caller is out of sync.
                return Optional.of(-1);
            }
    
            if (value.isPresent()) {
                record.setValue(value.get());
                record.incrementVersion();
            }
    
            record.setExpiration(expiration);
    
            log.debug("Updated record '{}' in context '{}' with expiration '{}'",
                    new Object[] { key, context, expiration.or(0L)});

            return Optional.of(record.getVersion());
            
        } finally {
            writeLock.unlock();
        }
    }

    /** {@inheritDoc} */
    public boolean deleteString(@Nonnull @NotEmpty String context, @Nonnull @NotEmpty String key) throws IOException {

        final Lock writeLock = lock.writeLock();
        
        try {
            writeLock.lock();

            Map<String, MutableStorageRecord> dataMap = contextMap.get(context);
            if (dataMap == null) {
                log.debug("Deleting record '{}' in context '{}'....not found", key, context);
                return false;
            }
            
            if (dataMap.containsKey(key)) {
                dataMap.remove(key);
                log.debug("Deleted record '{}' in context '{}'", key, context);
                return true;
            } else {
                log.debug("Deleting record '{}' in context '{}'....not found", key, context);
                return false;
            }
        } finally {
            writeLock.unlock();
        }
    }

    /** {@inheritDoc} */
    public boolean createText(@Nonnull @NotEmpty String context, @Nonnull @NotEmpty String key,
            @Nonnull @NotEmpty String value, @Nonnull Optional<Long> expiration) throws IOException {
        return createString(context, key, value, expiration);
    }

    /** {@inheritDoc} */
    @Nonnull public Pair<Optional<Integer>, Optional<StorageRecord>> readText(@Nonnull @NotEmpty String context,
            @Nonnull @NotEmpty String key, @Nonnull Optional<Integer> version) throws IOException {
        return readString(context, key, version);
    }

    /** {@inheritDoc} */
    @Nonnull public Optional<Integer> updateText(@Nonnull @NotEmpty String context, @Nonnull @NotEmpty String key,
            @Nonnull Optional<String> value, @Nonnull Optional<Long> expiration, @Nonnull Optional<Integer> version)
            throws IOException {
        return updateString(context, key, value, expiration, version);
    }

    /** {@inheritDoc} */
    public boolean deleteText(@Nonnull @NotEmpty String context, @Nonnull @NotEmpty String key) throws IOException {
        return deleteString(context, key);
    }

    /** {@inheritDoc} */
    public void updateContext(@Nonnull @NotEmpty String context, @Nonnull Optional<Long> expiration)
            throws IOException {

        final Lock writeLock = lock.writeLock();
        
        try {
            writeLock.lock();

            Map<String, MutableStorageRecord> dataMap = contextMap.get(context);
            if (dataMap != null) {    
                Long now = System.currentTimeMillis();
                Optional<Long> exp;
                for (MutableStorageRecord record : dataMap.values()) {
                    exp = record.getExpiration();
                    if (!exp.isPresent() || now < (exp.get() * 1000)) {
                        record.setExpiration(expiration);
                    }
                }
                log.debug("Updated expiration of valid records in context '{}' to '{}'", context, expiration.or(0L));
            }
        } finally {
            writeLock.unlock();
        }
    }

    /** {@inheritDoc} */
    public void deleteContext(@Nonnull @NotEmpty String context) throws IOException {
        
        final Lock writeLock = lock.writeLock();
        
        try {
            writeLock.lock();
            contextMap.remove(context);
        } finally {
            writeLock.unlock();
        }
        
        log.debug("Deleted context '{}'", context);
    }

    /** {@inheritDoc} */
    public void reap(@Nonnull @NotEmpty String context) throws IOException {

        final Lock writeLock = lock.writeLock();
        
        try {
            writeLock.lock();
            
            Map<String, MutableStorageRecord> dataMap = contextMap.get(context);
            if (dataMap != null) {
                if (reapWithLock(dataMap, System.currentTimeMillis())) {
                    if (dataMap.isEmpty()) {
                        contextMap.remove(context);
                    }
                }
            }
            
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * Locates and removes expired records from the input map.
     * 
     * <p>This method <strong>MUST</strong> be called while holding a write lock.</p>
     * 
     * @param dataMap       the map to reap
     * @param expiration    time at which to consider records expired
     * 
     * @return  true iff anything was purged
     */
    protected boolean reapWithLock(@Nonnull Map<String, MutableStorageRecord> dataMap, final long expiration) {
        
        return Iterables.removeIf(dataMap.entrySet(), new Predicate<Entry<String, MutableStorageRecord>>() {
                public boolean apply(@Nullable Entry<String, MutableStorageRecord> entry) {
                    Optional<Long> exp = entry.getValue().getExpiration();
                    return exp.isPresent() && (exp.get() * 1000) <= expiration;
                }
            }
        );
    }

    /**
     * A task that removes expired records.
     */
    protected class CleanupTask extends TimerTask {

        /** {@inheritDoc} */
        public void run() {
            log.info("Running cleanup task");
            
            final Long now = System.currentTimeMillis();
            final Lock writeLock = lock.writeLock();
            boolean purged = false;
            
            try {
                writeLock.lock();
                
                Map<String, MutableStorageRecord> context;
                
                Collection<Map<String, MutableStorageRecord>> contexts = contextMap.values();
                Iterator<Map<String, MutableStorageRecord>> i = contexts.iterator();
                while (i.hasNext()) {
                    context = i.next(); 
                    if (reapWithLock(i.next(), now)) {
                        purged = true;
                        if (context.isEmpty()) {
                            i.remove();
                        }
                    }
                }
                
            } finally {
                writeLock.unlock();
            }
            
            if (purged) {
                log.info("Purged expired record(s) from storage");
            } else {
                log.info("No expired records found in storage");
            }
        }
    }
    
    /**
     * Expresses capabilities of implementation.
     */
    protected static class Capabilities implements StorageCapabilities {

        /** {@inheritDoc} */
        public Optional<Integer> getContextSize() {
            return Optional.absent();
        }

        /** {@inheritDoc} */
        public Optional<Integer> getKeySize() {
            return Optional.absent();
        }

        /** {@inheritDoc} */
        public Optional<Integer> getStringSize() {
            return Optional.absent();
        }

        /** {@inheritDoc} */
        public Optional<Long> getTextSize() {
            return Optional.absent();
        }
        
    }
}
