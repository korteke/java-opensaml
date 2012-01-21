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

package org.opensaml.util.resource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** A simple in-memory store of cache resources. */
@Deprecated
public class InMemoryResourceCache implements ResourceCache {

    /** The in-memory store of cached resources. */
    private final Map<String, CachedResource> cacheStore;

    /** Constructor. */
    public InMemoryResourceCache() {
        cacheStore = new ConcurrentHashMap<String, ResourceCache.CachedResource>();
    }

    /** {@inheritDoc} */
    public boolean contains(String location) {
        return cacheStore.containsKey(location);
    }

    /** {@inheritDoc} */
    public CachedResource get(String location) {
        return cacheStore.get(location);
    }

    /** {@inheritDoc} */
    public CachedResource put(CachedResource resource) {
        return cacheStore.put(resource.getLocation(), resource);
    }

    /** {@inheritDoc} */
    public CachedResource remove(String location) {
        return cacheStore.remove(location);
    }
}