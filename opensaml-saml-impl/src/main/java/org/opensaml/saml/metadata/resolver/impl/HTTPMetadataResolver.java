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

package org.opensaml.saml.metadata.resolver.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A metadata provider that pulls metadata using an HTTP GET. Metadata is cached until one of these criteria is met:
 * <ul>
 * <li>The smallest cacheDuration within the metadata is exceeded</li>
 * <li>The earliest validUntil time within the metadata is exceeded</li>
 * <li>The maximum cache duration is exceeded</li>
 * </ul>
 * 
 * Metadata is filtered prior to determining the cache expiration data. This allows a filter to remove XMLObjects that
 * may effect the cache duration but for which the user of this provider does not care about.
 * 
 * It is the responsibility of the caller to re-initialize, via {@link #initialize()}, if any properties of this
 * provider are changed.
 */
public class HTTPMetadataResolver extends AbstractReloadingMetadataResolver {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(HTTPMetadataResolver.class);

    /** HTTP Client used to pull the metadata. */
    private HttpClient httpClient;

    /** URL to the Metadata. */
    private URI metadataURI;

    /** The ETag provided when the currently cached metadata was fetched. */
    private String cachedMetadataETag;

    /** The Last-Modified information provided when the currently cached metadata was fetched. */
    private String cachedMetadataLastModified;
    
    /**
     * Constructor.
     * 
     * @param client HTTP client used to pull in remote metadata
     * @param metadataURL URL to the remove remote metadata
     * 
     * @throws ResolverException thrown if the HTTP client is null or the metadata URL provided is invalid
     */
    public HTTPMetadataResolver(HttpClient client, String metadataURL) throws ResolverException {
        this(null, client, metadataURL);
    }

    /**
     * Constructor.
     * 
     * @param backgroundTaskTimer timer used to schedule background metadata refresh tasks
     * @param client HTTP client used to pull in remote metadata
     * @param metadataURL URL to the remove remote metadata
     * 
     * @throws ResolverException thrown if the HTTP client is null or the metadata URL provided is invalid
     */
    public HTTPMetadataResolver(Timer backgroundTaskTimer, HttpClient client, String metadataURL)
            throws ResolverException {
        super(backgroundTaskTimer);

        if (client == null) {
            throw new ResolverException("HTTP client may not be null");
        }
        httpClient = client;

        try {
            metadataURI = new URI(metadataURL);
        } catch (URISyntaxException e) {
            throw new ResolverException("Illegal URL syntax", e);
        }
    }

    /**
     * Gets the URL to fetch the metadata.
     * 
     * @return the URL to fetch the metadata
     */
    public String getMetadataURI() {
        return metadataURI.toASCIIString();
    }

    /**
     * Sets the username and password used to access the metadata URL. To disable BASIC authentication set the username
     * and password to null. If the <code>authScope</code> is null, one will be generated based off of 
     * the metadata URI's hostname and port.
     * 
     * @param username the username
     * @param password the password
     * @param authScope the HTTP client auth scope with which to scope the credentials, may be null
     */
    public void setBasicCredentials(String username, String password, AuthScope authScope) {
        // TODO This approach from client v3 is problematic in client v4,
        // due to casting below. Also issue with AuthScope collisions if client is used
        // by multiple components.
        // May need to rethink how authN support is handled.
        // Maybe just require setting creds on the HttpClient that is passed in.
        UsernamePasswordCredentials creds = null;
        if (username != null && password != null) {
            creds = new UsernamePasswordCredentials(username, password);
        }
        
        AuthScope scope = authScope;
        if (scope == null) {
            scope = new AuthScope(metadataURI.getHost(), metadataURI.getPort());
        }
        
        if (httpClient instanceof AbstractHttpClient) {
            ((AbstractHttpClient) httpClient).getCredentialsProvider().setCredentials(scope, creds);
        } else {
            log.warn("Client is not an instance of AbstractHttpClient, can not set HTTP basic auth credentials");
        }
        
    }

    /** {@inheritDoc} */
    public synchronized void destroy() {
        httpClient = null;
        metadataURI = null;
        cachedMetadataETag = null;
        cachedMetadataLastModified = null;
        
        super.destroy();
    }

    /** {@inheritDoc} */
    protected String getMetadataIdentifier() {
        return metadataURI.toString();
    }

    /**
     * Gets the metadata document from the remote server.
     * 
     * @return the metadata from remote server, or null if the metadata document has not changed since the last
     *         retrieval
     * 
     * @throws ResolverException thrown if there is a problem retrieving the metadata from the remote server
     */
    protected byte[] fetchMetadata() throws ResolverException {
        HttpGet httpGet = buildHttpGet();

        try {
            log.debug("Attempting to fetch metadata document from '{}'", metadataURI);
            HttpResponse response = httpClient.execute(httpGet);
            int httpStatusCode = response.getStatusLine().getStatusCode();

            if (httpStatusCode == HttpStatus.SC_NOT_MODIFIED) {
                log.debug("Metadata document from '{}' has not changed since last retrieval", getMetadataURI());
                return null;
            }

            if (httpStatusCode != HttpStatus.SC_OK) {
                String errMsg = "Non-ok status code " + httpStatusCode
                        + " returned from remote metadata source " + metadataURI;
                log.error(errMsg);
                throw new ResolverException(errMsg);
            }

            processConditionalRetrievalHeaders(response);

            byte[] rawMetadata = getMetadataBytesFromResponse(response);
            log.debug("Successfully fetched {} bytes of metadata from {}", rawMetadata.length, getMetadataURI());

            return rawMetadata;
        } catch (IOException e) {
            String errMsg = "Error retrieving metadata from " + metadataURI;
            log.error(errMsg, e);
            throw new ResolverException(errMsg, e);
        } finally {
            httpGet.reset();
        }
    }

    /**
     * Builds the {@link HttpGet} instance used to fetch the metadata. 
     * The returned method advertises support for GZIP and deflate compression, 
     * enables conditional GETs if the cached metadata came with either an ETag or Last-Modified
     * information, and sets up basic authentication if such is configured.
     * 
     * @return the constructed HttpGet instance
     */
    protected HttpGet buildHttpGet() {
        HttpGet getMethod = new HttpGet(getMetadataURI());
        
        // TODO Connection header is already unconditionally added by our HttpClientBuilder config, keep here?
        getMethod.addHeader("Connection", "close");
        // TODO Accept-Encoding could be handled by a request interceptor or decorator client impl. 
        // Maybe remove depending on what do below re: response handling.
        getMethod.setHeader("Accept-Encoding", "gzip,deflate");
        if (cachedMetadataETag != null) {
            getMethod.setHeader("If-None-Match", cachedMetadataETag);
        }
        if (cachedMetadataLastModified != null) {
            getMethod.setHeader("If-Modified-Since", cachedMetadataLastModified);
        }

        return getMethod;
    }

    /**
     * Records the ETag and Last-Modified headers, from the response, if they are present.
     * 
     * @param response GetMethod containing a valid HTTP response
     */
    protected void processConditionalRetrievalHeaders(HttpResponse response) {
        Header httpHeader = response.getFirstHeader("ETag");
        if (httpHeader != null) {
            cachedMetadataETag = httpHeader.getValue();
        }

        httpHeader = response.getFirstHeader("Last-Modified");
        if (httpHeader != null) {
            cachedMetadataLastModified = httpHeader.getValue();
        }
    }

    /**
     * Extracts the raw metadata bytes from the response taking in to account possible deflate and GZip compression.
     * 
     * @param response GetMethod containing a valid HTTP response
     * 
     * @return the raw metadata bytes
     * 
     * @throws ResolverException thrown if there is a problem getting the raw metadata bytes from the response
     */
    protected byte[] getMetadataBytesFromResponse(HttpResponse response) throws ResolverException {
        log.debug("Attempting to extract metadata from response to request for metadata from '{}'", getMetadataURI());
        try {
            InputStream ins = response.getEntity().getContent();
            
            // TODO HttpClient v4 supports auto-decompressing via an interceptor and/or decorator client impl.
            // Figure out how to detect automatic support, if possible, and support along with explicit
            // decompression below (or not). Maybe just make it wholly the responsibility of the HttpClient
            // instance which is passed in.

            Header httpHeader = response.getFirstHeader("Content-Encoding");
            if (httpHeader != null) {
                String contentEncoding = httpHeader.getValue();
                if ("deflate".equalsIgnoreCase(contentEncoding)) {
                    log.debug("Metadata document from '{}' was deflate compressed, decompressing it", metadataURI);
                    ins = new InflaterInputStream(ins);
                }

                if ("gzip".equalsIgnoreCase(contentEncoding)) {
                    log.debug("Metadata document from '{}' was GZip compressed, decompressing it", metadataURI);
                    ins = new GZIPInputStream(ins);
                }
            }

            return inputstreamToByteArray(ins);
        } catch (IOException e) {
            log.error("Unable to read response", e);
            throw new ResolverException("Unable to read response", e);
        } finally {
            // Make sure entity has been completely consumed.
            EntityUtils.consumeQuietly(response.getEntity());
        }
    }
}