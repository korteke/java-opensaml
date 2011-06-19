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

package org.opensaml.util.storage;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tracks information associated with messages in order to detect message replays.
 * 
 * This class is thread-safe and uses a basic reentrant lock to avoid corruption of the underlying store, as well as to
 * prevent race conditions with respect to replay checking.
 */
public class ReplayCache {

    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(ReplayCache.class);

    /** Backing storage for the replay cache. */
    private Map<String, ReplayCacheEntry> storage;

    /** Time, in milliseconds, that message state is valid. */
    private long entryDuration;

    /** Replay cache lock. */
    private ReentrantLock cacheLock;

    /**
     * Constructor.
     * 
     * @param cacheStorage the backing data store for this service
     * @param duration default length of time that message state is valid
     */
    public ReplayCache(Map<String, ReplayCacheEntry> cacheStorage, long duration) {
        storage = cacheStorage;
        entryDuration = duration;
        cacheLock = new ReentrantLock(true);
    }

    /**
     * Checks if the message has been replayed. If the message has not been seen before then it is added to the list of
     * seen of messages for the default duration.
     * 
     * @param issuerId unique ID of the message issuer
     * @param messageId unique ID of the message
     * 
     * @return true if the given message ID has been seen before
     */
    public boolean isReplay(String issuerId, String messageId) {
        log.debug("Attempting to acquire lock for replay cache check");
        cacheLock.lock();
        log.debug("Lock acquired");

        try {
            boolean replayed = true;
            String entryHash = issuerId + messageId;

            ReplayCacheEntry cacheEntry = storage.get(entryHash);

            if (cacheEntry == null || cacheEntry.isExpired()) {
                if (log.isDebugEnabled()) {
                    if (cacheEntry == null) {
                        log.debug("Message ID {} was not a replay", messageId);
                    } else if (cacheEntry.isExpired()) {
                        log.debug("Message ID {} expired in replay cache at {}", messageId, cacheEntry
                                .getExpirationTime().toString());
                        storage.remove(entryHash);
                    }
                }
                replayed = false;
                addMessageID(entryHash, new DateTime().plus(entryDuration));
            } else {
                log.debug("Replay of message ID {} detected in replay cache, will expire at {}", messageId, cacheEntry
                        .getExpirationTime().toString());
            }

            return replayed;
        } finally {
            cacheLock.unlock();
        }
    }

    /**
     * Acquires a write lock and adds the message state to the underlying storage service.
     * 
     * @param messageId unique ID of the message
     * @param expiration time the message state expires
     */
    protected void addMessageID(String messageId, DateTime expiration) {
        log.debug("Writing message ID {} to replay cache with expiration time {}", messageId, expiration.toString());
        storage.put(messageId, new ReplayCacheEntry(messageId, expiration));
    }
}