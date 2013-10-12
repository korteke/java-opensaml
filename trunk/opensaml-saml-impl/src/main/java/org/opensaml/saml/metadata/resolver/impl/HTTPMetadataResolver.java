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

import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
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
    
    /** Username and password credential used for HTTP BASIC authentication. */
    private UsernamePasswordCredentials usernamePasswordCredentials;
    
    /** HttpClient AuthScope instance to use. */
    private AuthScope authScope;
    
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
     * @param scope the HTTP client auth scope with which to scope the credentials, may be null
     */
    public void setBasicCredentials(String username, String password, AuthScope scope) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);

        if (username != null && password != null) {
            usernamePasswordCredentials = new UsernamePasswordCredentials(username, password);
            if (scope == null) {
                authScope = new AuthScope(metadataURI.getHost(), metadataURI.getPort());
            } else {
                authScope = scope;
            }
        } else {
            log.debug("Either username or password were null, disabling basic auth");
            usernamePasswordCredentials = null;
            authScope = null;
        }
        
    }

    /** {@inheritDoc} */
    protected void doDestroy() {
        httpClient = null;
        usernamePasswordCredentials = null;
        authScope = null;
        metadataURI = null;
        cachedMetadataETag = null;
        cachedMetadataLastModified = null;
        
        super.doDestroy();
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
        HttpClientContext context = buildHttpClientContext();

        try {
            log.debug("Attempting to fetch metadata document from '{}'", metadataURI);
            HttpResponse response = httpClient.execute(httpGet, context);
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
        
        if (cachedMetadataETag != null) {
            getMethod.setHeader("If-None-Match", cachedMetadataETag);
        }
        if (cachedMetadataLastModified != null) {
            getMethod.setHeader("If-Modified-Since", cachedMetadataLastModified);
        }

        return getMethod;
    }
    
    /**
     * Build the {@link HttpClientContext} instance which will be used to invoke the 
     * {@link HttpClient} request.
     * 
     * @return a new instance of {@link HttpClientContext}
     */
    protected HttpClientContext buildHttpClientContext() {
        HttpClientContext context = HttpClientContext.create();
        if (usernamePasswordCredentials != null) {
            BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(authScope, usernamePasswordCredentials);
            context.setCredentialsProvider(credentialsProvider);
        }
        return context;
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