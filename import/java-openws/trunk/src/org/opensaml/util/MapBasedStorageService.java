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

package org.opensaml.util;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Map} based {@link StorageService} implementation.
 * 
 * @param <KeyType> object type of the keys
 * @param <ValueType> object type of the values
 */
public class MapBasedStorageService<KeyType, ValueType> implements StorageService<KeyType, ValueType> {

    /** Backing map. */
    private Map<KeyType, ValueType> store;

    /** Constructor. */
    public MapBasedStorageService() {
        store = new HashMap<KeyType, ValueType>();
    }

    /** {@inheritDoc} */
    public boolean contains(KeyType key) {
        return store.containsKey(key);
    }

    /** {@inheritDoc} */
    public ValueType get(KeyType key) {
        return store.get(key);
    }

    /** {@inheritDoc} */
    public ValueType put(KeyType key, ValueType value) {
        return store.put(key, value);
    }

    /** {@inheritDoc} */
    public ValueType remove(KeyType key) {
        return store.remove(key);
    }
}