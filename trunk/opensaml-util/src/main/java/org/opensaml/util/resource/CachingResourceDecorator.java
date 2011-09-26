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

import java.io.InputStream;

import net.jcip.annotations.ThreadSafe;

import org.opensaml.util.Assert;
import org.opensaml.util.constraint.documented.NotEmpty;
import org.opensaml.util.constraint.documented.NotNull;
import org.opensaml.util.resource.ResourceCache.CachedResource;

/** A decorator that may be used to wrap a {@link Resource} in order to add content caching. */
@ThreadSafe
public class CachingResourceDecorator implements CachingResource {

    /** The resource used to fetch new information. */
    private final Resource backingResource;

    /** The cache of resource data. */
    private final ResourceCache resourceCache;

    /**
     * Constructor.
     * 
     * @param resource the resource backing this resource
     * @param cache the resource cache used to cache responses from the given resource
     * 
     */
    public CachingResourceDecorator(@NotNull final Resource resource, @NotNull final ResourceCache cache) {
        backingResource = Assert.isNotNull(resource, "Given Resource can not be null");
        resourceCache = Assert.isNotNull(cache, "Given ResourceCache can not be null");
    }

    /** {@inheritDoc} */
    public @NotNull @NotEmpty String getLocation() {
        return backingResource.getLocation();
    }

    /** {@inheritDoc} */
    public boolean exists() throws ResourceException {
        CachedResource cacheEntry = getCachedResource(true);

        if (cacheEntry == null) {
            return false;
        }

        return true;
    }

    /** {@inheritDoc} */
    public @NotNull InputStream getInputStream() throws ResourceException {
        return getCachedResource(true).getInputStream();
    }

    /** {@inheritDoc} */
    public long getLastModifiedTime() throws ResourceException {
        return getCachedResource(true).getLastModifiedTime();
    }

    /** {@inheritDoc} */
    public long getCacheInstant() {
        try {
            CachedResource cacheEntry = getCachedResource(false);

            if (cacheEntry == null) {
                return -1;
            }

            return cacheEntry.getLastModifiedTime();
        } catch (ResourceException e) {
            // TODO logging
            return -1;
        }
    }

    /** {@inheritDoc} */
    public void expireCache() {
        resourceCache.remove(backingResource.getLocation());
    }

    /**
     * Gets the resource wrapped by this decorator.
     * 
     * @return resource wrapped by this decorator
     */
    protected @NotNull Resource getBackingResource() {
        return backingResource;
    }

    /**
     * Gets the cache used to cache resource content.
     * 
     * @return cache used to cache resource content
     */
    protected @NotNull ResourceCache getResourceCache() {
        return resourceCache;
    }

    /**
     * Gets the cache entry for the cached resource. If the resource content has not yet been cached, fetch it, cache
     * it, and return the new entry.
     * 
     * @return the cache entry for the cached content
     * 
     * @throws ResourceException thrown if their is a problem fetching the resource
     */
    protected synchronized CachedResource getCachedResource(boolean fetchIfNecessary) throws ResourceException {
        // TODO
        return null;
    }
}