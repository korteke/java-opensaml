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

package org.opensaml.storage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.Live;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.annotation.constraint.Positive;
import net.shibboleth.utilities.java.support.collection.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;


/**
 * Partial implementation of {@link StorageService} that stores data in-memory with no persistence
 * using a simple map.
 * 
 * <p>Abstract methods supply the map of data to manipulate and the lock to use, which allows
 * optimizations in cases where locking isn't required or data isn't shared.<p> 
 */
public abstract class AbstractMapBackedStorageService extends AbstractStorageService {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AbstractMapBackedStorageService.class);

    /** Constructor. */
    public AbstractMapBackedStorageService() {
        setContextSize(Integer.MAX_VALUE);
        setKeySize(Integer.MAX_VALUE);
        setValueSize(Integer.MAX_VALUE);
    }

    /** {@inheritDoc} */
    public boolean create(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key,
            @Nonnull @NotEmpty final String value, @Nullable final Long expiration) throws IOException {
        final Lock writeLock = getLock().writeLock();
        
        try {
            writeLock.lock();
            
            final Map<String,Map<String,MutableStorageRecord>> contextMap = getContextMap();
            
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
                Long exp = record.getExpiration();
                if (exp == null || System.currentTimeMillis() < exp) {
                    return false;
                }
                
                // It's dead, so we can just remove it now and create the new record.
            }
            
            dataMap.put(key, new MutableStorageRecord(value, expiration));
            log.trace("Inserted record '{}' in context '{}' with expiration '{}'",
                    new Object[] { key, context, expiration });
            
            return true;
            
        } finally {
            writeLock.unlock();
        }
    }
    
    /** {@inheritDoc} */
    @Nullable public StorageRecord read(@Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key) throws IOException {
        return readImpl(context, key, null).getSecond();
    }

    /** {@inheritDoc} */
    @Nonnull public Pair<Long, StorageRecord> read(@Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, final long version) throws IOException {
        return readImpl(context, key, version);
    }

    /** {@inheritDoc} */
    @Nullable public boolean update(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key,
            @Nonnull @NotEmpty final String value, @Nullable final Long expiration) throws IOException {
        try {
            return updateImpl(null, context, key, value, expiration) != null;
        } catch (VersionMismatchException e) {
            throw new IOException("Unexpected exception thrown by update.", e);
        }
    }

    /** {@inheritDoc} */
    @Nullable public Long updateWithVersion(final long version, @Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, @Nonnull @NotEmpty final String value, @Nullable final Long expiration)
                    throws IOException, VersionMismatchException {
        return updateImpl(version, context, key, value, expiration);
    }

    /** {@inheritDoc} */
    @Nullable public boolean updateExpiration(@Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, @Nullable final Long expiration) throws IOException {
        try {
            return updateImpl(null, context, key, null, expiration) != null;
        } catch (VersionMismatchException e) {
            throw new IOException("Unexpected exception thrown by update.", e);
        }
    }

    /** {@inheritDoc} */
    public boolean deleteWithVersion(long version, String context, String key) throws IOException,
            VersionMismatchException {
        return deleteImpl(version, context, key);
    }
    
    /** {@inheritDoc} */
    public boolean delete(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key)
            throws IOException {
        try {
            return deleteImpl(null, context, key);
        } catch (VersionMismatchException e) {
            throw new IOException("Unexpected exception thrown by delete.", e);
        }
    }

    /** {@inheritDoc} */
    public void updateContextExpiration(@Nonnull @NotEmpty final String context, @Nullable final Long expiration)
            throws IOException {
        final Lock writeLock = getLock().writeLock();
        
        try {
            writeLock.lock();
            
            final Map<String,Map<String,MutableStorageRecord>> contextMap = getContextMap();

            final Map<String, MutableStorageRecord> dataMap = contextMap.get(context);
            if (dataMap != null) {    
                Long now = System.currentTimeMillis();
                for (MutableStorageRecord record : dataMap.values()) {
                    final Long exp = record.getExpiration();
                    if (exp == null || now < exp) {
                        record.setExpiration(expiration);
                    }
                }
                log.debug("Updated expiration of valid records in context '{}' to '{}'", context, expiration);
            }
        } finally {
            writeLock.unlock();
        }
    }
    
    /** {@inheritDoc} */
    public void deleteContext(@Nonnull @NotEmpty final String context) throws IOException {
        
        final Lock writeLock = getLock().writeLock();
        
        try {
            writeLock.lock();
            getContextMap().remove(context);
        } finally {
            writeLock.unlock();
        }
        
        log.debug("Deleted context '{}'", context);
    }

    /** {@inheritDoc} */
    public void reap(@Nonnull @NotEmpty final String context) throws IOException {

        final Lock writeLock = getLock().writeLock();
        
        try {
            writeLock.lock();
            
            final Map<String,Map<String,MutableStorageRecord>> contextMap = getContextMap();
            
            final Map<String, MutableStorageRecord> dataMap = contextMap.get(context);
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
     * Get the map of contexts to manipulate during operations.
     * 
     * @return map of contexts to manipulate
     */
    @Nonnull @NonnullElements @Live protected abstract Map<String, Map<String, MutableStorageRecord>> getContextMap();
    
    /**
     * Get the shared lock to synchronize access.
     * 
     * @return shared lock
     */
    @Nonnull protected abstract ReadWriteLock getLock();
    
    /**
     * Internal method to implement read functions.
     *
     * @param context       a storage context label
     * @param key           a key unique to context
     * @param version       only return record if newer than optionally supplied version
     * 
     * @return  a pair consisting of the version of the record read back, if any, and the record itself
     * @throws IOException  if errors occur in the read process 
     */
    @Nonnull protected Pair<Long, StorageRecord> readImpl(@Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, @Nullable final Long version) throws IOException {

        Lock readLock = getLock().readLock();
        try {
            readLock.lock();
            
            final Map<String,Map<String,MutableStorageRecord>> contextMap = getContextMap();
            
            final Map<String, MutableStorageRecord> dataMap = contextMap.get(context);
            if (dataMap == null) {
                log.debug("Read failed, context '{}' not found", context);
                return new Pair();
            }

            StorageRecord record = dataMap.get(key);
            if (record == null) {
                log.debug("Read failed, key '{}' not found in context '{}'", key, context);
                return new Pair();
            } else {
                Long exp = record.getExpiration();
                if (exp != null && System.currentTimeMillis() >= exp) {
                    log.debug("Read failed, key '{}' expired in context '{}'", key, context);
                    return new Pair();
                }
            }
            
            if (version != null && record.getVersion() == version) {
                // Nothing's changed, so just echo back the version.
                return new Pair(version, null);
            }
            
            return new Pair(record.getVersion(), record);
            
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Internal method to implement update functions.
     * 
     * @param version       only update if the current version matches this value
     * @param context       a storage context label
     * @param key           a key unique to context
     * @param value         updated value
     * @param expiration    expiration for record. or null
     * 
     * @return the version of the record after update, null if no record exists
     * @throws IOException  if errors occur in the update process
     * @throws VersionMismatchException if the record has already been updated to a newer version
     */
    @Nullable protected Long updateImpl(@Nullable final Long version, @Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, @Nullable final String value, @Nullable final Long expiration)
                    throws IOException, VersionMismatchException {

        final Lock writeLock = getLock().writeLock();
        
        try {
            writeLock.lock();
            
            final Map<String,Map<String,MutableStorageRecord>> contextMap = getContextMap();

            final Map<String, MutableStorageRecord> dataMap = contextMap.get(context);
            if (dataMap == null) {
                log.debug("Update failed, context '{}' not found", context);
                return null;
            }
            
            MutableStorageRecord record = dataMap.get(key);
            if (record == null) {
                log.debug("Update failed, key '{}' not found in context '{}'", key, context);
                return null;
            } else {
                Long exp = record.getExpiration();
                if (exp != null && System.currentTimeMillis() >= exp) {
                    log.debug("Update failed, key '{}' expired in context '{}'", key, context);
                    return null;
                }
            }
    
            if (version != null && version != record.getVersion()) {
                // Caller is out of sync.
                throw new VersionMismatchException();
            }
    
            if (value != null) {
                record.setValue(value);
                record.incrementVersion();
            }
    
            record.setExpiration(expiration);
    
            log.trace("Updated record '{}' in context '{}' with expiration '{}'",
                    new Object[] { key, context, expiration });

            return record.getVersion();
            
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * Internal method to implement delete functions.
     * 
     * @param version       only update if the current version matches this value
     * @param context       a storage context label
     * @param key           a key unique to context
     * 
     * @return true iff the record existed and was deleted
     * @throws IOException  if errors occur in the update process
     * @throws VersionMismatchException if the record has already been updated to a newer version
     */
    protected boolean deleteImpl(@Nullable @Positive final Long version, @Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key) throws IOException, VersionMismatchException {

        final Lock writeLock = getLock().writeLock();
        
        try {
            writeLock.lock();

            final Map<String,Map<String,MutableStorageRecord>> contextMap = getContextMap();
            
            final Map<String, MutableStorageRecord> dataMap = contextMap.get(context);
            if (dataMap == null) {
                log.debug("Deleting record '{}' in context '{}'....context not found", key, context);
                return false;
            }

            MutableStorageRecord record = dataMap.get(key);
            if (record == null) {
                log.debug("Deleting record '{}' in context '{}'....key not found", key, context);
                return false;
            } else if (version != null && record.getVersion() != version) {
                throw new VersionMismatchException();
            } else {
                dataMap.remove(key);
                log.trace("Deleted record '{}' in context '{}'", key, context);
                return true;
            }
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * Locates and removes expired records from the input map.
     * 
     * <p>This method <strong>MUST</strong> be called while holding a write lock, if locking is required.</p>
     * 
     * @param dataMap       the map to reap
     * @param expiration    time at which to consider records expired
     * 
     * @return  true iff anything was purged
     */
    protected boolean reapWithLock(@Nonnull @NonnullElements final Map<String, MutableStorageRecord> dataMap,
            final long expiration) {
        
        return Iterables.removeIf(dataMap.entrySet(), new Predicate<Entry<String, MutableStorageRecord>>() {
                public boolean apply(@Nullable final Entry<String, MutableStorageRecord> entry) {
                    Long exp = entry.getValue().getExpiration();
                    return exp != null && exp <= expiration;
                }
            }
        );
    }
    
}