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

import javax.annotation.Nonnull;

import org.opensaml.util.storage.StorageRecord;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;

import com.google.common.base.Optional;

/**
 * Exposes mutation of {@link StorageRecord} properties.
 */
public class MutableStorageRecord extends StorageRecord {
    
    /**
     * Constructor.
     *
     * @param val   value
     * @param exp   optional expiration
     */
    public MutableStorageRecord(@Nonnull @NotEmpty final String val, @Nonnull final Optional<Long> exp) {
        super(val, exp);
    }    

    /**
     * Set the record value.
     * 
     * @param val   the new record value
     */
    public void setValue(@Nonnull @NotEmpty final String val) {
        super.setValue(val);
    }
    
    /**
     * Set the optional record expiration.
     * 
     * @param exp   the new record expiration
     */
    public void setExpiration(@Nonnull Optional<Long> exp) {
        super.setExpiration(exp);
    }
    
    /**
     * Increment the record version and returns the new value.
     * 
     * @return  the updated version
     */
    public int incrementVersion() {
        return super.incrementVersion();
    }
    
}