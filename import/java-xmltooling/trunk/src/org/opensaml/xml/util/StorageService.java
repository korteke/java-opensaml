/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
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
 * Generic data storage facility for use by services that require
 * some degree of persistence. Implementations will vary in how much
 * persistence they can supply.
 * 
 * Storage is divided into "contexts" identified by a string label.
 * Keys need to be unique only within a given context, so multiple
 * components can share a single storage service safely as long as they
 * use different labels.
 * 
 * @param <KeyType> object type of the keys
 * @param <ValueType> object type of the values
 */
public interface StorageService<KeyType, ValueType> {

    /**
     * Checks to see if a value exists for a given key.
     * 
     * @param context the storage context
     * @param key the key
     * 
     * @return true if there is at least one cache value corresponding to the key, false if not
     */
    public boolean exists(String context, KeyType key);
    
    /**
     * Gets the value stored under a particular key.
     * 
     * @param context the storage context
     * @param key the key
     * 
     * @return the value for that key, or null if there is no value for the given key
     */
    public ValueType get(String context, KeyType key);
    
    /**
     * Adds a value, indexed by a key, in to storage.  Note that implementations of this 
     * service may determine, on its own, when to evict items from storage, the expiration 
     * time given here is meant only as a system provided hint.
     * 
     * @param context the storage context
     * @param key the key
     * @param value the value
     * @param expiration instance when the given value has expired, or null
     * 
     * @return the value that was registered under that key previously, if there was a previous value
     */
    public ValueType put(String context, KeyType key, ValueType value, DateTime expiration);
    
    /**
     * Removes an item from storage.
     * 
     * @param context the storage context
     * @param key the key to the valye to remove
     * 
     * @return the value that was removed
     */
    public ValueType remove(String context, KeyType key);
}