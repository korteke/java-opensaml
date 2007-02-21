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

package org.opensaml.xml.util;

import org.joda.time.DateTime;

/**
 * Class that uses an underlying {@link StorageService} to track state associated with
 * replay of messages of varying kinds.
 */
public class ReplayCache {
    
    /** Backing storage for the replay cache. */
    private StorageService<String, Boolean> storage;
    
    /** StorageService storage context to use for this replay cache. */
    private String context;
    
    /**
     * Constructor.
     *
     * @param storageContext name of the StorageService storage context to use
     * @param storageService the StorageService which serves as the backing store for the cache
     */
    public ReplayCache(String storageContext, StorageService<String, Boolean> storageService) {
        context = storageContext;
        storage = storageService;
    }
    
    /**
     * Get the storage context identifier used by this replay cache.
     * 
     * @return the storage context identifier
     */
    public String getStorageContext() {
        return context;
    }
    
    /**
     * Get the storage service instance used by this replay cache.
     * 
     * @return the storage service instance
     */
    public StorageService<String, Boolean> getStorageService() {
        return storage;
    }
    
    /**
     * Check whether the message identified by the given string key is currently 
     * considered to be replayed.  If message is not in the cache, store it along
     * with expiration information.
     * 
     * @param key message identifier string
     * @param expires  time at which a new cached message identifier should expire
     * @return false if message identifier is already present in the cache, otherwise true
     */
    public boolean check(String key, DateTime expires) {
        if (storage.get(context, key)) {
            return false;
        } else {
            storage.put(context, key, Boolean.TRUE, expires);
            return true;
        }
    }

}
