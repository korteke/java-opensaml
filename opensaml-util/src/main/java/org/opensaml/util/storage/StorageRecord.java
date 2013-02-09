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

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;

import com.google.common.base.Optional;

/**
 * Represents a versioned record in a {@link StorageService}.
 */
public class StorageRecord {

    /** Version field. */
    private final int version;
    
    /** Value field. */
    private final String value;
    
    /** Expiration field. */
    private final Long expiration;
    
    /**
     * Constructor.
     *
     * @param ver   version
     * @param val   value
     * @param exp   optional expiration
     */
    public StorageRecord(int ver, @Nonnull @NotEmpty final String val, @Nonnull final Optional<Long> exp) {
        version = ver;
        value = val;
        expiration = exp.orNull();
    }

    /**
     * Constructor.
     *
     * @param ver   version
     * @param exp   optional expiration
     */
    public StorageRecord(int ver, @Nonnull final Optional<Long> exp) {
        version = ver;
        value = null;
        expiration = exp.orNull();
    }
    
    /**
     * Gets the record version.
     * 
     * @return  the record version
     */
    public int getVersion() {
        return version;
    }

    /**
     * Gets the record value.
     * 
     * @return  the record value
     */
    @Nonnull public String getValue() {
        return value;
    }

    /**
     * Gets the optional record expiration.
     * 
     * @return  the optional record expiration
     */
    @Nonnull public Optional<Long> getExpiration() {
        return Optional.fromNullable(expiration);
    }

}