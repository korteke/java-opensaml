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

package org.opensaml.storage.impl.client;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.storage.impl.client.ClientStorageService.ClientStorageSource;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.logic.Constraint;

/**
 * A wrapper for a storage operation to capture the current or intended state of the data for
 * a client.
 */
public class ClientStorageServiceOperation {
    
    /** ID of storage service for tracking/logging. */
    @Nonnull @NotEmpty private final String storageServiceId;
    
    /** Storage key. */
    @Nonnull @NotEmpty private final String storageKey;
    
    /** Storage value. */
    @Nullable @NotEmpty private final String storageValue;
    
    /** Storage source. */
    @Nonnull private final ClientStorageSource storageSource;
    
    /**
     * Constructor.
     *
     * @param id storage service ID
     * @param key storage key to update
     * @param value storage value
     * @param source storage source
     */
    public ClientStorageServiceOperation(@Nonnull @NotEmpty final String id, @Nonnull @NotEmpty final String key,
            @Nullable final String value, @Nonnull final ClientStorageSource source) {
        storageServiceId = Constraint.isNotEmpty(id, "StorageService ID cannot be null or empty");
        storageKey = Constraint.isNotEmpty(key, "Key cannot be null or empty");
        storageValue = value;
        storageSource = Constraint.isNotNull(source, "Storage source cannot be null");
    }
    
    /**
     * Get Storage Service ID.
     * 
     * @return  storage service ID
     */
    @Nonnull @NotEmpty public String getStorageServiceID() {
        return storageServiceId;
    }

    /**
     * Get storage key to update.
     * 
     * @return  storage key
     */
    @Nonnull @NotEmpty public String getKey() {
        return storageKey;
    }

    /**
     * Get new storage value.
     * 
     * @return  storage value
     */
    @Nullable @NotEmpty public String getValue() {
        return storageValue;
    }

    /**
     * Get storage source.
     * 
     * @return storage source
     */
    @Nonnull public ClientStorageSource getStorageSource() {
        return storageSource;
    }
    
}