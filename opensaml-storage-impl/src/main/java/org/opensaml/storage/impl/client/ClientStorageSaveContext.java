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

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.BaseContext;
import org.opensaml.storage.impl.client.ClientStorageService.ClientStorageSource;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import net.shibboleth.utilities.java.support.annotation.constraint.Live;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.logic.Constraint;

/**
 * A subcontext for driving the saving of data to a client from one or more
 * instances of a {@link ClientStorageService}.
 */
public class ClientStorageSaveContext extends BaseContext {

    /** Storage operations to perform. */
    @Nonnull @NonnullElements private Collection<StorageOperation> storageOperations;
    
    /** Constructor. */
    public ClientStorageSaveContext() {
        storageOperations = new ArrayList<>();
    }
    
    /**
     * Get the storage operations to perform.
     * 
     * @return modifiable collection of storage operations
     */
    @Nonnull @NonnullElements @Live public Collection<StorageOperation> getStorageOperations() {
        return storageOperations;
    }
    
    /**
     * Get whether a particular storage source is implicated by the queued operations.
     * 
     * @param source storage source to check for
     * @return true iff the operations include at least one against the specified source
     */
    public boolean isSourceRequired(@Nonnull final ClientStorageSource source) {
        return Iterables.any(storageOperations, new Predicate<StorageOperation>() {
            public boolean apply(StorageOperation input) {
                return input.getStorageSource() == source;
            }
        });
    }

    /**
     * A wrapper for a storage operation.
     */
    public static class StorageOperation {
        
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
        public StorageOperation(@Nonnull @NotEmpty final String id, @Nonnull @NotEmpty final String key,
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
        @Nonnull @NotEmpty public String getStorageKey() {
            return storageKey;
        }

        /**
         * Get new storage value.
         * 
         * @return  storage value
         */
        @Nullable @NotEmpty public String getStorageValue() {
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
    
}