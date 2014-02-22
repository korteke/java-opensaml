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

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.annotation.constraint.ThreadSafeAfterInit;
import net.shibboleth.utilities.java.support.component.AbstractDestructableIdentifiedInitializableComponent;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tracks non-replayable values in order to detect replays of the values, commonly used to track message identifiers.
 * 
 * <p>This class is thread-safe and uses a synchronized method to prevent race conditions within the underlying
 * store (lacking an atomic "check and insert" operation).</p>
 */
@ThreadSafeAfterInit
public class ReplayCache extends AbstractDestructableIdentifiedInitializableComponent {

    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(ReplayCache.class);

    /** Backing storage for the replay cache. */
    private StorageService storage;

    /** Flag controlling behavior on storage failure. */
    private boolean strict;
    
    /**
     * Get the backing store for the cache.
     * 
     * @return the backing store.
     */
    @NonnullAfterInit public StorageService getStorage() {
        return storage;
    }
    

    /**
     * Set the backing store for the cache.
     * 
     * @param storageService backing store to use
     */
    public void setStorage(@Nonnull final StorageService storageService) {
        storage = storageService;
    }
    

    /**
     * Get the strictness flag.
     * 
     * @return true iff we should treat storage failures as a replay
     */
    public boolean isStrict() {
        return strict;
    }

    /**
     * Set the strictness flag.
     * 
     * @param flag true iff we should treat storage failures as a replay
     */
    public void setStrict(boolean flag) {
        strict = flag;
    }


    /** {@inheritDoc} */ 
    public void doInitialize() throws ComponentInitializationException {
        if (storage == null) {
            throw new ComponentInitializationException("StorageService cannot be null");
        }
    }
    
    /** {@inheritDoc} */
    public synchronized void setId(@Nonnull @NotEmpty String componentId) {
        super.setId(componentId);
    }
    
    /**
     * Returns true iff the check value is not found in the cache, and stores it.
     * 
     * @param context   a context label to subdivide the cache
     * @param s         value to check
     * @param expires   time (in milliseconds since beginning of epoch) for disposal of value from cache
     * 
     * @return true iff the check value is not found in the cache
     */
    public synchronized boolean check(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String s,
            final long expires) {

        String key;
        
        StorageCapabilities caps = storage.getCapabilities();
        if (context.length() > caps.getContextSize()) {
            log.error("context {} too long for StorageService (limit {})", context, caps.getContextSize());
            return false;
        } else if (s.length() > caps.getKeySize()) {
            key = DigestUtils.sha1Hex(s);
        } else {
            key = s;
        }

        try {
            StorageRecord entry = storage.read(context, key);
            if (entry == null) {
                log.debug("Value '{}' was not a replay, adding to cache with expiration time {}", s, expires);
                storage.create(context, key, "x", expires);
                return true;
            } else {
                log.debug("Replay of value '{}' detected in cache, expires at {}", s, entry.getExpiration());
                return false;
            }
        } catch (IOException e) {
            log.error("Exception reading/writing to storage service, returning {}", e, strict ? "failure" : "success");
            return !strict;
        }
    }

}