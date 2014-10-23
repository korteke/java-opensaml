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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.locks.Lock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
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
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.metadata.resolver.filter.FilterException;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
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
     * response Content-Type header.
     * 
     * @param types the new supported content types to set
     */
    public void setSupportedContentTypes(@Nullable final List<String> types) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        if (types == null) {
            supportedContentTypes = Collections.emptyList();
        } else {
            supportedContentTypes = Lists.newArrayList(StringSupport.normalizeStringCollection(types));
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
    }
    
   /** {@inheritDoc} */
    protected void doDestroy() {
        httpClient = null;
        
        supportedContentTypes = null;
        supportedContentTypesValue = null;
        
        super.doDestroy();
    }
    
    /** {@inheritDoc} */
    @Nonnull @NonnullElements protected Iterable<EntityDescriptor> fetchByCriteria(@Nonnull final CriteriaSet criteria) 
            throws ResolverException {
        String entityID = StringSupport.trimOrNull(criteria.get(EntityIdCriterion.class).getEntityId());
        Lock writeLock = getBackingStore().getManagementData(entityID).getReadWriteLock().writeLock(); 
        
        try {
            writeLock.lock();
            
            List<EntityDescriptor> descriptors = lookupEntityID(entityID);
            if (!descriptors.isEmpty()) {
                log.debug("Metadata was resolved and stored by another thread " 
                        + "while this thread was waiting on the write lock");
                return descriptors;
            }
            
            HttpUriRequest request = buildHttpRequest(criteria);
            if (request == null) {
                log.debug("Could not build request based on input criteria, unable to query");
                return Collections.emptyList();
            }
        
            HttpResponse response = httpClient.execute(request);
            
            processResponse(response, request.getURI());
            
            return lookupEntityID(entityID);
            
        } catch (IOException e) {
            log.error("Error executing HTTP request", e);
            return Collections.emptyList();
        } finally {
            writeLock.unlock();
        }
        
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
     * Process the received HTTP response, including validating the response, unmarshalling the received metadata,
     * and storing the metadata in the backing store.  
     * 
     * @param response the received response
     * @param requestURI the original request URI
     * @throws ResolverException if there is a fatal error processing the response
     */
    protected void processResponse(@Nonnull final HttpResponse response, @Nonnull final URI requestURI) 
            throws ResolverException {
        
        int httpStatusCode = response.getStatusLine().getStatusCode();
        
        // TODO should we be seeing/doing this? Probably not if we don't do conditional GET.
        // But we will if we do pre-emptive refreshing of metadata in background thread.
        if (httpStatusCode == HttpStatus.SC_NOT_MODIFIED) {
            log.debug("Metadata document from '{}' has not changed since last retrieval", requestURI);
            return;
        }

        if (httpStatusCode != HttpStatus.SC_OK) {
            log.warn("Non-ok status code '{}' returned from remote metadata source: {}", httpStatusCode, requestURI);
            return;
        }
        
        
        XMLObject root = null;
        try {
            try {
                validateResponse(response, requestURI);
            } catch (ResolverException e) {
                log.error("Problem validating dynamic metadata HTTP response", e);
                return;
            }
            
            try {
                InputStream ins = response.getEntity().getContent();
                root = unmarshallMetadata(ins);
            } catch (IOException | UnmarshallingException e) {
                log.error("Error unmarshalling HTTP response stream", e);
                return;
            }
        } finally {
            closeResponse(response, requestURI);
        }
            
        try {
            processNewMetadata(root);
        } catch (FilterException e) {
            log.error("Metadata filtering problem processing new metadata", e);
            return;
        }
        
    }

    /**
     * Close the HTTP response instance.
     * 
     * @param response the received response
     * @param requestURI the original request URI
     */
    protected void closeResponse(@Nonnull final HttpResponse response, @Nonnull final URI requestURI) {
        if (response instanceof CloseableHttpResponse) {
            try {
                ((CloseableHttpResponse)response).close();
            } catch (final IOException e) {
                log.error("Error closing HTTP response from {}", requestURI, e);
            }
        }
    }
    
    /**
     * Validate the received HTTP response instance, such as checking for supported content types.
     * 
     * @param response the received response
     * @param requestURI the original request URI
     * @throws ResolverException if the response was not valid, or if there is a fatal error validating the response
     */
    public void validateResponse(@Nonnull final HttpResponse response, @Nonnull final URI requestURI) 
            throws ResolverException {
        if (!getSupportedContentTypes().isEmpty()) {
            Header contentType = response.getEntity().getContentType();
            if (contentType != null && contentType.getValue() != null) {
                if (!getSupportedContentTypes().contains(contentType.getValue())) {
                    throw new ResolverException("HTTP response specified an unsupported Content-Type: " 
                            + contentType.getValue());
                }
            }
        }
        
        // TODO other validation
        
    }


}
