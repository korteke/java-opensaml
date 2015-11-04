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

import javax.annotation.Nullable;
import javax.net.ssl.SSLPeerUnverifiedException;

import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.util.EntityUtils;
import org.opensaml.security.httpclient.HttpClientSecurityConstants;
import org.opensaml.security.trust.TrustEngine;
import org.opensaml.security.x509.X509Credential;
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

    /** HttpClient credentials provider. */
    private BasicCredentialsProvider credentialsProvider;
    
    /** Optional trust engine used in evaluating server TLS credentials. */
    private TrustEngine<? super X509Credential> tlsTrustEngine;

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
     * Sets the optional trust engine used in evaluating server TLS credentials.
     * 
     * <p>
     * Must be used in conjunction with an HttpClient instance which is configured with either a 
     * {@link org.opensaml.security.httpclient.impl.SecurityEnhancedTLSSocketFactory} or the (deprecated)
     * {@link org.opensaml.security.httpclient.impl.TrustEngineTLSSocketFactory}. If such a socket
     * factory is not configured, then this will result in no TLS trust evaluation being performed
     * and a {@link ResolverException} will ultimately be thrown.
     * </p>
     * 
     * @param engine the trust engine instance to use
     */
    public void setTLSTrustEngine(@Nullable final TrustEngine<? super X509Credential> engine) {
        tlsTrustEngine = engine;
    }

    /**
     * Sets the username and password used to access the metadata URL. To disable BASIC authentication pass null for the
     * credentials instance.
     * 
     * An {@link AuthScope} will be generated based off the metadata URI's hostname and port.
     * 
     * @param credentials the username and password credentials
     */
    public void setBasicCredentials(@Nullable final UsernamePasswordCredentials credentials) {
        setBasicCredentialsWithScope(credentials, null);
    }

    /**
     * Sets the username and password used to access the metadata URL. To disable BASIC authentication pass null for the
     * credentials instance.
     * 
     * <p>
     * If the <code>authScope</code> is null, an {@link AuthScope} will be generated based off the metadata URI's
     * hostname and port.
     * </p>
     * 
     * @param credentials the username and password credentials
     * @param scope the HTTP client auth scope with which to scope the credentials, may be null
     */
    public void setBasicCredentialsWithScope(@Nullable final UsernamePasswordCredentials credentials,
            @Nullable final AuthScope scope) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);

        if (credentials != null) {
            AuthScope authScope = scope;
            if (authScope == null) {
                authScope = new AuthScope(metadataURI.getHost(), metadataURI.getPort());
            }
            BasicCredentialsProvider provider = new BasicCredentialsProvider();
            provider.setCredentials(authScope, credentials);
            credentialsProvider = provider;
        } else {
            log.debug("Either username or password were null, disabling basic auth");
            credentialsProvider = null;
        }

    }

    /** {@inheritDoc} */
    @Override
    protected void doDestroy() {
        httpClient = null;
        credentialsProvider = null;
        metadataURI = null;
        cachedMetadataETag = null;
        cachedMetadataLastModified = null;

        super.doDestroy();
    }

    /** {@inheritDoc} */
    @Override
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
    @Override
    protected byte[] fetchMetadata() throws ResolverException {
        final HttpGet httpGet = buildHttpGet();
        final HttpClientContext context = buildHttpClientContext();
        HttpResponse response = null;

        try {
            log.debug("Attempting to fetch metadata document from '{}'", metadataURI);
            response = httpClient.execute(httpGet, context);
            checkTLSCredentialTrusted(context);
            final int httpStatusCode = response.getStatusLine().getStatusCode();

            if (httpStatusCode == HttpStatus.SC_NOT_MODIFIED) {
                log.debug("Metadata document from '{}' has not changed since last retrieval", getMetadataURI());
                return null;
            }

            if (httpStatusCode != HttpStatus.SC_OK) {
                final String errMsg =
                        "Non-ok status code " + httpStatusCode + " returned from remote metadata source " + metadataURI;
                log.error(errMsg);
                throw new ResolverException(errMsg);
            }

            processConditionalRetrievalHeaders(response);

            final byte[] rawMetadata = getMetadataBytesFromResponse(response);
            log.debug("Successfully fetched {} bytes of metadata from {}", rawMetadata.length, getMetadataURI());

            return rawMetadata;
        } catch (final IOException e) {
            final String errMsg = "Error retrieving metadata from " + metadataURI;
            log.error(errMsg, e);
            throw new ResolverException(errMsg, e);
        } finally {
            try {
                if (response != null && response instanceof CloseableHttpResponse) {
                    ((CloseableHttpResponse) response).close();
                }
            } catch (final IOException e) {
                log.error("Error closing HTTP response from {}", metadataURI, e);
            }
        }
    }

    /**
     * Check that trust engine evaluation of the server TLS credential was actually performed.
     * 
     * @param context the current HTTP context instance in use
     * @throws SSLPeerUnverifiedException thrown if the TLS credential was not actually evaluated by the trust engine
     */
    protected void checkTLSCredentialTrusted(HttpClientContext context) throws SSLPeerUnverifiedException {
        if (tlsTrustEngine != null && "https".equalsIgnoreCase(metadataURI.getScheme())) {
            if (context.getAttribute(HttpClientSecurityConstants.CONTEXT_KEY_SERVER_TLS_CREDENTIAL_TRUSTED) == null) {
                log.warn("Configured TLS trust engine was not used to verify server TLS credential, " 
                        + "the appropriate socket factory was likely not configured");
                throw new SSLPeerUnverifiedException(
                        "Evaluation of server TLS credential with configured TrustEngine was not performed");
            }
        }
    }

    /**
     * Builds the {@link HttpGet} instance used to fetch the metadata. The returned method advertises support for GZIP
     * and deflate compression, enables conditional GETs if the cached metadata came with either an ETag or
     * Last-Modified information, and sets up basic authentication if such is configured.
     * 
     * @return the constructed HttpGet instance
     */
    protected HttpGet buildHttpGet() {
        final HttpGet getMethod = new HttpGet(getMetadataURI());

        if (cachedMetadataETag != null) {
            getMethod.setHeader("If-None-Match", cachedMetadataETag);
        }
        if (cachedMetadataLastModified != null) {
            getMethod.setHeader("If-Modified-Since", cachedMetadataLastModified);
        }

        return getMethod;
    }

    /**
     * Build the {@link HttpClientContext} instance which will be used to invoke the {@link HttpClient} request.
     * 
     * @return a new instance of {@link HttpClientContext}
     */
    protected HttpClientContext buildHttpClientContext() {
        final HttpClientContext context = HttpClientContext.create();
        if (credentialsProvider != null) {
            context.setCredentialsProvider(credentialsProvider);
        }
        if (tlsTrustEngine != null) {
            context.setAttribute(HttpClientSecurityConstants.CONTEXT_KEY_TRUST_ENGINE, tlsTrustEngine);
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
            final InputStream ins = response.getEntity().getContent();
            return inputstreamToByteArray(ins);
        } catch (final IOException e) {
            log.error("Unable to read response", e);
            throw new ResolverException("Unable to read response", e);
        } finally {
            // Make sure entity has been completely consumed.
            EntityUtils.consumeQuietly(response.getEntity());
        }
    }
}