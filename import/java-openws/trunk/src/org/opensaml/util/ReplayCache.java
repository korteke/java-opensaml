/*
 * Copyright [2007] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.util;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.joda.time.DateTime;

/**
 * Class that uses an underlying {@link StorageService} to track information associated with messages in order to detect
 * message replays.
 * 
 * This class is thread-safre and uses a basic read/write lock to ensure consistency of the underlying store.
 */
public class ReplayCache {

    /** Backing storage for the replay cache. */
    private StorageService<String, ReplayCacheEntry> storage;

    /** Default time, in milliseconds, that message state is valid. */
    private long defaultDuration;

    /** Read/Write lock. */
    private ReentrantReadWriteLock rwLock;

    /**
     * Constructor.
     * 
     * @param storageService the StorageService which serves as the backing store for the cache
     * @param duration default length of time that message state is valid
     */
    public ReplayCache(StorageService<String, ReplayCacheEntry> storageService, long duration) {
        storage = storageService;
        defaultDuration = duration;
        rwLock = new ReentrantReadWriteLock(true);
    }

    /**
     * Checks if the message has been replayed. If the message has not been seen before then it is added to the list of
     * seen of messages for the default duration.
     * 
     * @param messageId unique ID of the message
     * 
     * @return true if the given message ID has been seen before
     */
    public boolean isReplay(String messageId) {
        return isReplay(messageId, defaultDuration);
    }

    /**
     * Checks if the message has been replayed. If the message has not been seen before then it is added to the list of
     * seen of messages for the given duration.
     * 
     * @param messageId unique ID of the message
     * @param duration length of time, in milliseconds, to keep the message state
     * 
     * @return true if the given message ID has been seen before
     */
    public boolean isReplay(String messageId, long duration) {
        boolean replayed = true;
        Lock readLock = rwLock.readLock();
        readLock.lock();
        ReplayCacheEntry cacheEntry = storage.get(messageId);
        if (cacheEntry == null || cacheEntry.isExpired()) {
            replayed = false;
            addMessageID(messageId, new DateTime().plus(duration));
        }
        readLock.unlock();

        return replayed;
    }

    /**
     * Checks if the message has been replayed. If the message has not been seen before then it is added until the given
     * expiration.
     * 
     * @param messageId unique ID of the message
     * @param expiration time the message state expires
     * 
     * @return true if the given message ID has been seen before
     */
    public boolean isReplay(String messageId, DateTime expiration) {
        boolean replayed = true;
        Lock readLock = rwLock.readLock();
        readLock.lock();
        ReplayCacheEntry cacheEntry = storage.get(messageId);
        if (cacheEntry == null || cacheEntry.isExpired()) {
            replayed = false;
            addMessageID(messageId, expiration);
        }
        readLock.unlock();

        return replayed;
    }

    /**
     * Accquires a write lock and adds the message state to the underlying storage service.
     * 
     * @param messageId unique ID of the message
     * @param expiration time the message state expires
     */
    protected void addMessageID(String messageId, DateTime expiration) {
        Lock writeLock = rwLock.writeLock();
        writeLock.lock();
        storage.put(messageId, new ReplayCacheEntry(expiration));
        writeLock.unlock();
    }
    
    /** Replay cache storage service entry. */
    protected class ReplayCacheEntry implements ExpiringObject, Serializable{
        
        /** Serial version UID. */
        private static final long serialVersionUID = 2398693920546938083L;
        
        /** Time when this entry expires. */
        private DateTime expirationTime;
        
        /**
         * Constructor.
         *
         * @param expiration time when this entry expires
         */
        public ReplayCacheEntry(DateTime expiration){
            expirationTime = expiration;
        }
        
        /** {@inheritDoc} */
        public DateTime getExpirationTime() {
            return expirationTime;
        }
        
        /** {@inheritDoc} */
        public boolean isExpired() {
            return expirationTime.isBeforeNow();
        }
    }
}