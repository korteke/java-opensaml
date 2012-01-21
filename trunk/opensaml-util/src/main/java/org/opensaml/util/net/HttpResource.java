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

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.GregorianCalendar;

import net.jcip.annotations.NotThreadSafe;
import net.jcip.annotations.ThreadSafe;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import org.opensaml.util.Assert;
import org.opensaml.util.StringSupport;
import org.opensaml.util.constraint.documented.NotEmpty;
import org.opensaml.util.constraint.documented.NotNull;
import org.opensaml.util.constraint.documented.Null;
import org.opensaml.util.resource.Resource;
import org.opensaml.util.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO authentication

/** A resource that fetches data from a remote source via HTTP. */
@NotThreadSafe
@Deprecated
public class HttpResource implements Resource {

    /** Property name under which the ETag data is stored. */
    public static final String ETAG_PROP = "etag";

    /** Property name under which the Last-Modified data is stored. */
    public static final String LAST_MODIFIED_PROP = "modified";

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(HttpResource.class);

    /** Client used to make HTTP requests. */
    private final HttpClient httpClient;

    /** URL of remote resource. */
    private final String resourceUrl;

    /** Strategy used to customize an {@link HttpGet} before using it. */
    private HttpGetCustomizationStrategy httpGetCustomizationStrategy;

    /** Strategy used to customize an {@link HttpResponse} before get its content. */
    private HttpResponseCustomizationStrategy httpResponseCustomizationStrategy;

    /**
     * Constructor.
     * 
     * @param client client used to fetch the remote resource data
     * @param url URL of the remote resource data
     */
    public HttpResource(@NotNull final HttpClient client, @NotNull @NotEmpty final String url) {
        httpClient = Assert.isNotNull(client, "HTTP client may not be null");
        resourceUrl = Assert.isNotNull(StringSupport.trimOrNull(url), "Resource URL may not be null or empty");
    }

    /**
     * Gets the strategy used customize the {@link HttpGet} used to fetch the resource.
     * 
     * @return strategy used customize the {@link HttpGet} used to fetch the resource
     */
    public @Null HttpGetCustomizationStrategy getHttpGetCustomizationStrategy() {
        return httpGetCustomizationStrategy;
    }

    /**
     * Sets the strategy used customize the {@link HttpGet} used to fetch the resource.
     * 
     * @param strategy strategy used customize the {@link HttpGet} used to fetch the resource
     */
    public void setHttpGetCustomizationStrategy(@Null final HttpGetCustomizationStrategy strategy) {
        httpGetCustomizationStrategy = strategy;
    }

    /**
     * Gets the strategy used to customize the {@link HttpResponse} before its content is returned.
     * 
     * @return strategy used to customize the {@link HttpResponse} before its content is returned
     */
    public HttpResponseCustomizationStrategy getHttpResponseCustomizationStrategy() {
        return httpResponseCustomizationStrategy;
    }

    /**
     * Sets the strategy used to customize the {@link HttpResponse} before its content is returned.
     * 
     * @param strategy strategy used to customize the {@link HttpResponse} before its content is returned
     */
    public void setHttpResponseCustomizationStrategy(@Null final HttpResponseCustomizationStrategy strategy) {
        httpResponseCustomizationStrategy = strategy;
    }

    /** {@inheritDoc} */
    public String getLocation() {
        return resourceUrl;
    }

    /** {@inheritDoc} */
    public long getLastModifiedTime() throws ResourceException {
        try {
            HttpResponse httpResponse = getResourceHeaders();

            final Header httpHeader = httpResponse.getFirstHeader(HttpHeaders.LAST_MODIFIED);
            if (httpHeader != null) {
                final Date lastModDate = DateUtils.parseDate(httpHeader.getValue());
                final GregorianCalendar cal = new GregorianCalendar();
                cal.setTime(lastModDate);
                return cal.getTimeInMillis();
            } else {
                throw new ResourceException(resourceUrl + " did not return the required " + HttpHeaders.LAST_MODIFIED
                        + " header");
            }
        } catch (DateParseException e) {
            throw new ResourceException("Unable to parse HTTP " + HttpHeaders.LAST_MODIFIED + " header", e);
        }
    }

    /** {@inheritDoc} */
    public boolean exists() throws ResourceException {
        HttpResponse httpResponse = getResourceHeaders();
        int statusCode = httpResponse.getStatusLine().getStatusCode();

        if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_NOT_MODIFIED) {
            return true;
        }

        return false;
    }

    /** {@inheritDoc} */
    public InputStream getInputStream() throws ResourceException {
        final HttpGet httpRequest = buildGetMethod();

        try {
            log.debug("Attempting to fetch data from '{}'", resourceUrl);
            HttpResponse response = httpClient.execute(httpRequest);
            final int httpStatus = response.getStatusLine().getStatusCode();

            if (httpStatus >= 200 && httpStatus <= 299) {
                if (httpResponseCustomizationStrategy != null) {
                    response = httpResponseCustomizationStrategy.customize(response);
                }

                final HttpEntity responseEntity = response.getEntity();
                if (responseEntity == null) {
                    throw new ResourceException(resourceUrl + " returned the successful status code " + httpStatus
                            + " but did not return any content");
                }

                return response.getEntity().getContent();
            } else {
                log.debug("Unacceptable status code, {}, returned when fetching metadata from '{}'", httpStatus,
                        resourceUrl);
                throw new ResourceException("Unable to read resource from " + resourceUrl
                        + ", received a status code of " + httpStatus);
            }
        } catch (IOException e) {
            httpRequest.abort();
            throw new ResourceException("Error retrieving metadata from " + resourceUrl, e);
        }
    }

    /**
     * Attempts to fetch only the headers for a given resource. If the HEAD requests are unsupported than a more costly
     * GET request is performed.
     * 
     * @return the response from the request
     * 
     * @throws ResourceException thrown if there is a problem contacting the resource
     */
    private HttpResponse getResourceHeaders() throws ResourceException {
        HttpUriRequest httpRequest = new HttpHead(resourceUrl);

        try {
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            int statusCode = httpResponse.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_METHOD_NOT_ALLOWED || statusCode == HttpStatus.SC_NOT_IMPLEMENTED) {
                log.debug(resourceUrl + " does not support HEAD requests, falling back to GET request");
                httpRequest = buildGetMethod();
                httpResponse = httpClient.execute(httpRequest);
                statusCode = httpResponse.getStatusLine().getStatusCode();
            }

            return httpResponse;
        } catch (IOException e) {
            throw new ResourceException("Error contacting resource " + resourceUrl, e);
        } finally {
            httpRequest.abort();
        }
    }

    /**
     * Gets the, potentially customized, {@link HttpGet} method.
     * 
     * @return the {@link HttpGet} method
     */
    private HttpGet buildGetMethod() {
        final HttpGet httpGet = new HttpGet(resourceUrl);

        if (httpGetCustomizationStrategy == null) {
            return httpGet;
        }
        return httpGetCustomizationStrategy.customize(httpGet);
    }

    /** Strategy that can be used to customize an {@link HttpGet} object before it is used. */
    @ThreadSafe
    public static interface HttpGetCustomizationStrategy {

        /**
         * Customizes the given {@link HttpGet} object.
         * 
         * @param httpGet the current {@link HttpGet}
         * 
         * @return the customized {@link HttpGet}
         */
        public HttpGet customize(@NotNull final HttpGet httpGet);
    }

    /**
     * Strategy used to perform any {@link HttpResponse} processing prior to returning the {@link InputStream} for its
     * content.
     */
    @ThreadSafe
    public static interface HttpResponseCustomizationStrategy {

        /**
         * Customizes the given {@link HttpResponse}.
         * 
         * @param httpResponse the current {@link HttpResponse}
         * 
         * @return the customized {@link HttpResponse}
         */
        public HttpResponse customize(@NotNull final HttpResponse httpResponse);
    }
}