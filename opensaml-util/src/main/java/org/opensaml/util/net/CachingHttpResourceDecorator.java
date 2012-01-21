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

package org.opensaml.util.net;

import java.util.Map;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.opensaml.util.constraint.documented.NotNull;
import org.opensaml.util.net.HttpResource.HttpGetCustomizationStrategy;
import org.opensaml.util.net.HttpResource.HttpResponseCustomizationStrategy;
import org.opensaml.util.resource.CachingResourceDecorator;
import org.opensaml.util.resource.ResourceCache;
import org.opensaml.util.resource.ResourceCache.CachedResource;

/**
 * An extension to the {@link CachingResourceDecorator} that wraps a {@link HttpResponse} and in addition to the normal
 * caching supports HTTP conditional gets.
 */
@Deprecated
public class CachingHttpResourceDecorator extends CachingResourceDecorator {

    /** Property name under which the ETag data is stored. */
    public static final String ETAG_PROP = "etag";

    /** Property name under which the Last-Modified data is stored. */
    public static final String LAST_MODIFIED_PROP = "modified";

    /**
     * Constructor. An instance of {@link ConnditionalHttpGetStrategy} and {@link CachedResponseStrategy} will be added
     * to the provided {@link HttpResource}.
     * 
     * @param resource the resource to be wrapped
     * @param cache the cache used to store resource data
     */
    public CachingHttpResourceDecorator(@NotNull final HttpResource resource, @NotNull final ResourceCache cache) {
        super(resource, cache);
        resource.setHttpGetCustomizationStrategy(new ConnditionalHttpGetStrategy());
        resource.setHttpResponseCustomizationStrategy(new CachedResponseStrategy());
    }

    /** A factory that produces {@link HttpGet} instances that supports conditional gets. */
    private class ConnditionalHttpGetStrategy implements HttpGetCustomizationStrategy {

        /** {@inheritDoc} */
        public HttpGet customize(@NotNull final HttpGet httpGet) {
            final CachedResource cacheEntry = getResourceCache().get(getBackingResource().getLocation());
            if (cacheEntry != null) {
                final Map<String, String> entryProperties = cacheEntry.getProperties();

                if (entryProperties.containsKey(ETAG_PROP)) {
                    httpGet.setHeader(HttpHeaders.IF_NONE_MATCH, entryProperties.get(ETAG_PROP));
                }

                if (entryProperties.containsKey(LAST_MODIFIED_PROP)) {
                    httpGet.setHeader(HttpHeaders.IF_MODIFIED_SINCE, entryProperties.get(LAST_MODIFIED_PROP));
                }
            }

            return httpGet;
        }
    }

    /**
     * If the conditional get indicate the resource had not been modified then return the cached copy. If the resource
     * is new, then cache it.
     */
    private class CachedResponseStrategy implements HttpResponseCustomizationStrategy {

        /** {@inheritDoc} */
        public HttpResponse customize(HttpResponse httpResponse) {
            // TODO Auto-generated method stub
            return null;
        }
    }
}