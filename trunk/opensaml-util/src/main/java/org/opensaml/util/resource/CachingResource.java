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

/**
 * A {@link Resource} which may cache the fetched data. This is useful when fetching data from a remote source that may
 * not change as often as the data is read.
 * 
 * When using a caching resource the {@link #getInputStream()} method will return null if the resource has not changed
 * since the last request. If you need to re-read the resource use {@link #expireCache()} prior to calling
 * {@link #getInputStream()}. Note, this does <strong>not</strong> mean that I/O operations will not occur if the
 * resource is cached. Such operations may still be necessary to determine if the cached copy is fresh.
 */
public interface CachingResource extends Resource {

    /**
     * Gets the time, in milliseconds since the epoch, when the resource data was cached.
     * 
     * @return time when the resource data was cached or 0 if the data is not cached
     */
    public long getCacheInstant();

    /** Expires any cached resource data. */
    public void expireCache();
    
    /**
     * Gets the input stream to the resource's data.
     * 
     * @param returnCache whether to return the cached copy {@link #getInputStream()} returns null
     * 
     * @return the resource data, never null
     * 
     * @throws ResourceException thrown if there is a problem getting the resource data
     */
    public InputStream getInputStream(boolean returnCache) throws ResourceException;
}