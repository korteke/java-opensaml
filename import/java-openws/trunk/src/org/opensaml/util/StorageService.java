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

package org.opensaml.util;

/**
 * Generic data storage facility for use by services that require some degree of persistence. Implementations will vary
 * in how much persistence they can supply.
 * 
 * @param <KeyType> object type of the keys
 * @param <ValueType> object type of the values
 */
public interface StorageService<KeyType, ValueType> {

    /**
     * Checks if a given key exists.
     * 
     * @param key the key to check
     * 
     * @return true of the given key exists, false if not
     */
    public boolean contains(KeyType key);

    /**
     * Gets the value stored under a particular key.
     * 
     * @param key the key
     * 
     * @return the value for that key, or null if there is no value for the given key
     */
    public ValueType get(KeyType key);

    /**
     * Adds a value, indexed by a key, in to storage. Note that implementations of this service may determine, on its
     * own, when to evict items from storage, the expiration time given here is meant only as a system provided hint.
     * 
     * @param key the key
     * @param value the value
     * 
     * @return the value that was registered under that key previously, if there was a previous value
     */
    public ValueType put(KeyType key, ValueType value);

    /**
     * Removes an item from storage.
     * 
     * @param key the key to the value to remove
     * 
     * @return the value that was removed
     */
    public ValueType remove(KeyType key);
}