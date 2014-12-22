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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NotLive;
import net.shibboleth.utilities.java.support.annotation.constraint.Unmodifiable;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 * Abstract subclass for dynamic metadata resolvers that implement metadata resolution based on HTTP requests.
 */
public abstract class AbstractDynamicHTTPMetadataResolver extends AbstractDynamicMetadataResolver {
    
    /** Default list of supported content MIME types. */
    public static final String[] DEFAULT_CONTENT_TYPES = 
            new String[] {"application/samlmetadata+xml", "application/xml", "text/xml"};
    
    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(AbstractDynamicHTTPMetadataResolver.class);
    
    /** HTTP Client used to pull the metadata. */
    private HttpClient httpClient;
    
    /** List of supported MIME types for use in Accept request header and validation of 
     * response Content-Type header.*/
    private List<String> supportedContentTypes;
    
    /** Generated Accept request header value. */
    private String supportedContentTypesValue;
    
    /** HttpClient ResponseHandler instance to use. */
    private ResponseHandler<XMLObject> responseHandler;
    
    /** HttpClient credentials provider. */
    private CredentialsProvider credentialsProvider;
    
    /**
     * Constructor.
     *
     * @param client the instance of {@link HttpClient} used to fetch remote metadata
     */
    public AbstractDynamicHTTPMetadataResolver(@Nonnull final HttpClient client) {
        this(null, client);
    }
    
    /**
     * Constructor.
     *
     * @param backgroundTaskTimer the {@link Timer} instance used to run resolver background managment tasks
     * @param client the instance of {@link HttpClient} used to fetch remote metadata
     */
    public AbstractDynamicHTTPMetadataResolver(@Nullable final Timer backgroundTaskTimer, 
            @Nonnull final HttpClient client) {
        super(backgroundTaskTimer);
        
        httpClient = Constraint.isNotNull(client, "HttpClient may not be null");
        
        // The default handler
        responseHandler = new BasicMetadataResponseHandler();
    }
    
    /**
     * Set an instance of {@link CredentialsProvider} used for authentication by the HttpClient instance.
     * 
     * @param provider the credentials provider
     */
    public void setCredentialsProvider(@Nullable final CredentialsProvider provider) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);

        credentialsProvider = provider;
    }
    
    /**
     * A convenience method to set a (single) username and password used to access metadata. 
     * To disable BASIC authentication pass null for the credentials instance.
     * 
     * <p>
     * An {@link AuthScope} will be generated which specifies any host, port, scheme and realm.
     * </p>
     * 
     * <p>To specify multiple usernames and passwords for multiple host, port, scheme, and realm combinations, instead 
     * provide an instance of {@link CredentialsProvider} via {@link #setCredentialsProvider(CredentialsProvider)}.</p>
     * 
     * @param credentials the username and password credentials
     */
    public void setBasicCredentials(@Nullable final UsernamePasswordCredentials credentials) {
        setBasicCredentialsWithScope(credentials, null);
    }

    /**
     * A convenience method to set a (single) username and password used to access metadata.
     * To disable BASIC authentication pass null for the credentials instance.
     * 
     * <p>
     * If the <code>authScope</code> is null, an {@link AuthScope} will be generated which specifies
     * any host, port, scheme and realm.
     * </p>
     * 
     * <p>To specify multiple usernames and passwords for multiple host, port, scheme, and realm combinations, instead 
     * provide an instance of {@link CredentialsProvider} via {@link #setCredentialsProvider(CredentialsProvider)}.</p>
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
                authScope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT);
            }
            BasicCredentialsProvider provider = new BasicCredentialsProvider();
            provider.setCredentials(authScope, credentials);
            credentialsProvider = provider;
        } else {
            log.debug("Either username or password were null, disabling basic auth");
            credentialsProvider = null;
        }

    }
    
    /**
     * Get the list of supported MIME types for use in Accept request header and validation of 
     * response Content-Type header.
     * 
     * @return the supported content types
     */
    @NonnullAfterInit @NotLive @Unmodifiable public List<String> getSupportedContentTypes() {
        return supportedContentTypes;
    }

    /**
     * Set the list of supported MIME types for use in Accept request header and validation of 
     * response Content-Type header. Values will be effectively lower-cased at runtime.
     * 
     * @param types the new supported content types to set
     */
    public void setSupportedContentTypes(@Nullable final List<String> types) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        if (types == null) {
            supportedContentTypes = Collections.emptyList();
        } else {
            supportedContentTypes = Lists.newArrayList(Collections2.transform(
                    StringSupport.normalizeStringCollection(types),
                    new Function<String,String>() {
                        @Nullable public String apply(@Nullable String input) {
                            return input == null ? null : input.toLowerCase();
                        }
                    }
                    ));
        }
    }
    
    /** {@inheritDoc} */
    protected void initMetadataResolver() throws ComponentInitializationException {
        super.initMetadataResolver();
        setBackingStore(createNewBackingStore());
        
        if (getSupportedContentTypes() == null) {
            setSupportedContentTypes(Arrays.asList(DEFAULT_CONTENT_TYPES));
        }
        
        if (! getSupportedContentTypes().isEmpty()) {
            supportedContentTypesValue = StringSupport.listToStringValue(getSupportedContentTypes(), ", ");
        } 
        
        log.debug("Supported content types are: {}", getSupportedContentTypes());
    }
    
   /** {@inheritDoc} */
    protected void doDestroy() {
        httpClient = null;
        credentialsProvider = null;
        
        supportedContentTypes = null;
        supportedContentTypesValue = null;
        
        super.doDestroy();
    }
    
    /** {@inheritDoc} */
    @Nullable protected XMLObject fetchFromOriginSource(@Nonnull final CriteriaSet criteria) 
            throws IOException {
            
        HttpUriRequest request = buildHttpRequest(criteria);
        if (request == null) {
            log.debug("Could not build request based on input criteria, unable to query");
            return null;
        }
        
        HttpClientContext context = buildHttpClientContext();
        
        return httpClient.execute(request, responseHandler, context);
    }
    
    /**
     * Build an appropriate instance of {@link HttpUriRequest} based on the input criteria set.
     * 
     * @param criteria the input criteria set
     * @return the newly constructed request, or null if it can not be built from the supplied criteria
     */
    @Nullable protected HttpUriRequest buildHttpRequest(@Nonnull final CriteriaSet criteria) {
        String url = buildRequestURL(criteria);
        log.debug("Built request URL of: {}", url);
        
        if (url == null) {
            log.debug("Could not construct request URL from input criteria, unable to query");
            return null;
        }
            
        HttpGet getMethod = new HttpGet(url);
        
        if (!Strings.isNullOrEmpty(supportedContentTypesValue)) {
            getMethod.addHeader("Accept", supportedContentTypesValue);
        }
        
        // TODO other headers ?
        
        return getMethod;
    }

    /**
     * Build the request URL based on the input criteria set.
     * 
     * @param criteria the input criteria set
     * @return the request URL, or null if it can not be built based on the supplied criteria
     */
    @Nullable protected abstract String buildRequestURL(@Nonnull final CriteriaSet criteria);
    
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
        return context;
    }
    
    /**
     * Basic HttpClient response handler for processing metadata fetch requests.
     */
    public class BasicMetadataResponseHandler implements ResponseHandler<XMLObject> {

        /** {@inheritDoc} */
        @Override
        public XMLObject handleResponse(@Nonnull final HttpResponse response) throws IOException {
            
            int httpStatusCode = response.getStatusLine().getStatusCode();
            
            // TODO should we be seeing/doing this? Probably not if we don't do conditional GET.
            // But we will if we do pre-emptive refreshing of metadata in background thread.
            if (httpStatusCode == HttpStatus.SC_NOT_MODIFIED) {
                log.debug("Metadata document from '{}' has not changed since last retrieval" );
                return null;
            }

            if (httpStatusCode != HttpStatus.SC_OK) {
                log.warn("Non-ok status code '{}' returned from remote metadata source: {}", httpStatusCode);
                return null;
            }
            
            try {
                validateHttpResponse(response);
            } catch (ResolverException e) {
                log.error("Problem validating dynamic metadata HTTP response", e);
                return null;
            }
            
            try {
                InputStream ins = response.getEntity().getContent();
                return unmarshallMetadata(ins);
            } catch (IOException | UnmarshallingException e) {
                log.error("Error unmarshalling HTTP response stream", e);
                return null;
            }
                
        }
        
        /**
         * Validate the received HTTP response instance, such as checking for supported content types.
         * 
         * @param response the received response
         * @throws ResolverException if the response was not valid, or if there is a fatal error validating the response
         */
        protected void validateHttpResponse(@Nonnull final HttpResponse response) throws ResolverException {
            
            if (!getSupportedContentTypes().isEmpty()) {
                Header contentType = response.getEntity().getContentType();
                if (contentType != null && contentType.getValue() != null) {
                    log.debug("Saw raw Content-Type from response header '{}'", contentType.getValue());
                    String mimeType = getContentTypeMIMEType(contentType.getValue());
                    log.debug("Extracted Content-Type MIME type to evaluate '{}'", mimeType);
                    if (!getSupportedContentTypes().contains(mimeType)) { 
                        throw new ResolverException("HTTP response specified an unsupported Content-Type MIME type: " 
                                + mimeType);
                    }
                }
            }
            
        }

        /**
         * Get the effective Content-Type value to evaluate against the supported types.
         * 
         * @param value the raw Content-Type value header
         * @return the effective value to evaluate
         */
        private String getContentTypeMIMEType(String value) {
            String trimmed = StringSupport.trimOrNull(value);
            if (trimmed == null) {
                return null;
            }
            if (!trimmed.contains(";")) {
                return trimmed.toLowerCase();
            }
            String typeSubtype = trimmed.split(";")[0];
            return StringSupport.trim(typeSubtype).toLowerCase();
        }
        
    }

}