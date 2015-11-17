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

import org.opensaml.messaging.context.BaseContext;
import org.opensaml.storage.impl.client.ClientStorageService.ClientStorageSource;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import net.shibboleth.utilities.java.support.annotation.constraint.Live;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;

/**
 * A subcontext for driving the saving of data to a client from one or more
 * instances of a {@link ClientStorageService}.
 */
public class ClientStorageSaveContext extends BaseContext {

    /** Storage operations to perform. */
    @Nonnull @NonnullElements private Collection<ClientStorageServiceOperation> storageOperations;
    
    /** Constructor. */
    public ClientStorageSaveContext() {
        storageOperations = new ArrayList<>();
    }
    
    /**
     * Get the storage operations to perform.
     * 
     * @return modifiable collection of storage operations
     */
    @Nonnull @NonnullElements @Live public Collection<ClientStorageServiceOperation> getStorageOperations() {
        return storageOperations;
    }
    
    /**
     * Get whether a particular storage source is implicated by the queued operations.
     * 
     * @param source storage source to check for
     * @return true iff the operations include at least one against the specified source
     */
    public boolean isSourceRequired(@Nonnull final ClientStorageSource source) {
        return Iterables.any(storageOperations, new Predicate<ClientStorageServiceOperation>() {
            public boolean apply(ClientStorageServiceOperation input) {
                return input.getStorageSource() == source;
            }
        });
    }
    
}