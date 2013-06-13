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

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.util.storage.annotation.Expiration;

/**
 * A base class for objects that have a built-in expiration, suitably annotated for use with
 * a {@link StorageService}.
 */
@Expiration("expiration")
public abstract class AbstractExpiringObject implements ExpiringObject {
    
    /** Moment of expiration in milliseconds since the Unix epoch. */
    @Nonnull private Long expiration;

    /**
     * Constructor.
     * 
     * @param expirationTime time this object should expire in milliseconds since the Unix epoch
     */
    public AbstractExpiringObject(@Nonnull final DateTime expirationTime) {
        Constraint.isNotNull(expirationTime, "Expiration time cannot be null");
        
        expiration = expirationTime.toDateTime(ISOChronology.getInstanceUTC()).getMillis();
    }

    /**
     * Constructor.
     * 
     * @param expirationTime time this object should expire in milliseconds since the Unix epoch
     */
    public AbstractExpiringObject(final long expirationTime) {
        expiration = expirationTime;
    }

    /**
     * Gets the expiration time in milliseconds since the Unix epoch.
     * 
     * @return the expiration time
     */
    public long getExpiration() {
        return expiration;
    }
    
    /**
     * Sets the expiration time in milliseconds since the Unix epoch.
     * 
     * @param expirationTime the expiration time
     */
    void setExpiration(final long expirationTime) {
        expiration = expirationTime;
    }
    
    /**
     * Gets the expiration time as an object.
     * 
     * @return the expiration time
     */
    @Nonnull public DateTime getExpirationDateTime() {
        return new DateTime(expiration, ISOChronology.getInstanceUTC());
    }

    /**
     * Sets the expiration time from an object.
     * 
     * @param expirationTime the expiration time
     */
    void setExpirationDateTime(@Nonnull final DateTime expirationTime) {
        Constraint.isNotNull(expirationTime, "Expiration time cannot be null");
        
        expiration = expirationTime.toDateTime(ISOChronology.getInstanceUTC()).getMillis();
    }
    
    /**
     * Checks whether the object is currently expired.
     * 
     * @return true iff the object has expired
     */
    public boolean isExpired() {
        return getExpirationDateTime().isBeforeNow();
    }

}