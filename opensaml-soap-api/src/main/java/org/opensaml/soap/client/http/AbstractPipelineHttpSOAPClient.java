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

package org.opensaml.soap.client.http;

import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_CRITERIA_SET;
import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_TRUST_ENGINE;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.component.AbstractInitializableComponent;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.context.httpclient.HttpClientRequestContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.messaging.decoder.httpclient.HttpClientResponseMessageDecoder;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.messaging.encoder.httpclient.HttpClientRequestMessageEncoder;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.messaging.pipeline.httpclient.HttpClientMessagePipeline;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.security.httpclient.HttpClientSecurityParameters;
import org.opensaml.security.httpclient.HttpClientSecuritySupport;
import org.opensaml.security.messaging.HttpClientSecurityContext;
import org.opensaml.soap.client.SOAPClient;
import org.opensaml.soap.client.SOAPFaultException;
import org.opensaml.soap.common.SOAP11FaultDecodingException;
import org.opensaml.soap.common.SOAPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;

/**
 * SOAP client that is based on {@link HttpClientMessagePipeline}.
 * 
 * @param <OutboundMessageType> the outbound message type
 * @param <InboundMessageType> the inbound message type
 */
@ThreadSafe
public abstract class AbstractPipelineHttpSOAPClient<OutboundMessageType, InboundMessageType> 
        extends AbstractInitializableComponent implements SOAPClient {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AbstractPipelineHttpSOAPClient.class);

    /** HTTP client used to send requests and receive responses. */
    @NonnullAfterInit private HttpClient httpClient;
    
    /** HTTP client security parameters. */
    @Nullable private HttpClientSecurityParameters httpClientSecurityParameters;
    
    /** Strategy for building the criteria set which is input to the TLS trust engine. */
    @Nullable private Function<InOutOperationContext<?, ?>, CriteriaSet> tlsCriteriaSetStrategy;
    
    /** Constructor. */
    public AbstractPipelineHttpSOAPClient() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (httpClient == null) {
            throw new ComponentInitializationException("HttpClient cannot be null");
        } 
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doDestroy() {
        httpClient = null;
        httpClientSecurityParameters = null;
        tlsCriteriaSetStrategy = null;
        
        super.doDestroy();
    }
    
    /**
     * Get the client used to make outbound HTTP requests.
     * 
     * @return the client instance
     */
    @Nonnull public HttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * Set the client used to make outbound HTTP requests.
     * 
     * <p>This client SHOULD employ a thread-safe {@link HttpClient} and may be shared with other objects.</p>
     * 
     * @param client client object
     */
    public void setHttpClient(@Nonnull final HttpClient client) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        
        httpClient = Constraint.isNotNull(client, "HttpClient cannot be null");
    }
    
    /**
     * Get the optional client security parameters.
     * 
     * @return the client security parameters, or null
     */
    @Nullable public HttpClientSecurityParameters getHttpClientSecurityParameters() {
        return httpClientSecurityParameters;
    }

    /**
     * Set the optional client security parameters.
     * 
     * @param params the new client security parameters
     */
    public void setHttpClientSecurityParameters(@Nullable final HttpClientSecurityParameters params) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        
        httpClientSecurityParameters = params;
    }
    
    /**
     * Get the strategy function which builds the dynamically-populated criteria set which is 
     * input to the TLS TrustEngine, if no static criteria set is supplied either via context 
     * or locally-configured {@link HttpClientSecurityParameters}.
     * 
     * @return the strategy function, or null
     */
    @Nullable public Function<InOutOperationContext<?, ?>, CriteriaSet> getTLSCriteriaSetStrategy() {
        return tlsCriteriaSetStrategy;
    }
    
    /**
     * Set the strategy function which builds the dynamically-populated criteria set which is 
     * input to the TLS TrustEngine, if no static criteria set is supplied either via context
     * or locally-configured {@link HttpClientSecurityParameters}.
     * 
     * @param function the strategy function, or null
     */
    public void setTLSCriteriaSetStrategy(@Nullable final Function<InOutOperationContext<?, ?>, CriteriaSet> function) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        
        tlsCriteriaSetStrategy = function;
    }
    
    /** {@inheritDoc} */
    // Checkstyle: CyclomaticComplexity OFF
    public void send(@Nonnull @NotEmpty final String endpoint, @Nonnull final InOutOperationContext operationContext)
            throws SOAPException, SecurityException {
        Constraint.isNotNull(endpoint, "Endpoint cannot be null");
        Constraint.isNotNull(operationContext, "Operation context cannot be null");
        
        HttpClientMessagePipeline<InboundMessageType, OutboundMessageType> pipeline = null;
        try {
            // Pipeline resolution
            pipeline = resolvePipeline(operationContext);
            
            // Outbound payload handling
            if (pipeline.getOutboundPayloadMessageHandler() != null) {
                pipeline.getOutboundPayloadMessageHandler().invoke(operationContext.getOutboundMessageContext());
            }
            
            HttpUriRequest httpRequest = buildHttpRequest(endpoint, operationContext);
            HttpClientContext httpContext = buildHttpContext(httpRequest, operationContext);
            
            // Request encoding + outbound transport handling
            HttpClientRequestMessageEncoder<OutboundMessageType> encoder = pipeline.getEncoder();
            encoder.setHttpRequest(httpRequest);
            encoder.setMessageContext(operationContext.getOutboundMessageContext());
            encoder.initialize();
            encoder.prepareContext();
            if (pipeline.getOutboundTransportMessageHandler() != null) {
                pipeline.getOutboundTransportMessageHandler().invoke(operationContext.getOutboundMessageContext());
            }
            encoder.encode();
            
            // HttpClient execution
            HttpResponse httpResponse = getHttpClient().execute(httpRequest, httpContext);
            HttpClientSecuritySupport.checkTLSCredentialEvaluated(httpContext, httpRequest.getURI().getScheme());
            
            // Response decoding
            HttpClientResponseMessageDecoder<InboundMessageType> decoder = pipeline.getDecoder();
            decoder.setHttpResponse(httpResponse);
            decoder.initialize();
            decoder.decode();
            operationContext.setInboundMessageContext(decoder.getMessageContext());
            
            // Inbound message handling
            if (pipeline.getInboundMessageHandler() != null) {
                pipeline.getInboundMessageHandler().invoke(operationContext.getInboundMessageContext());
            }
            
        } catch (SOAP11FaultDecodingException e) {
            SOAPFaultException faultException = new SOAPFaultException(e.getMessage(), e);
            faultException.setFault(e.getFault());
            throw faultException;
        } catch (SSLException e) {
            throw new SecurityException("Problem establising TLS connection to: " + endpoint, e);
        } catch (ComponentInitializationException e) {
            throw new SOAPException("Problem initializing a SOAP client component", e);
        } catch (MessageEncodingException e) {
            throw new SOAPException("Problem encoding SOAP request message to: " + endpoint, e);
        } catch (MessageDecodingException e) {
            throw new SOAPException("Problem decoding SOAP response message from: " + endpoint, e);
        } catch (MessageHandlerException e) {
            throw new SOAPException("Problem handling SOAP message exchange with: " + endpoint, e);
        } catch (ClientProtocolException e) {
            throw new SOAPException("Client protocol problem sending SOAP request message to: " + endpoint, e);
        } catch (IOException e) {
            throw new SOAPException("I/O problem with SOAP message exchange with: " + endpoint, e);
        } finally {
            if (pipeline != null) {
                pipeline.getEncoder().destroy();
                pipeline.getDecoder().destroy();
            }
        }
    }
    // Checkstyle: CyclomaticComplexity ON
    
    /**
     * Resolve and return a new instance of the {@link HttpClientMessagePipeline} to be processed.
     * 
     * <p>
     * Each call to this (factory) method MUST produce a new instance of the pipeline.
     * </p>
     * 
     * <p>
     * The default behavior is to simply call {@link #newPipeline()}.
     * </p>
     * 
     * @param operationContext the current operation context
     * 
     * @return a new pipeline instance
     * 
     * @throws SOAPException if there is an error obtaining a new pipeline instance
     */
    @Nonnull protected HttpClientMessagePipeline<InboundMessageType, OutboundMessageType> 
            resolvePipeline(@Nonnull final InOutOperationContext operationContext) throws SOAPException {
        try {
            return newPipeline();
        } catch (SOAPException e) {
            log.warn("Problem resolving pipeline instance", e);
            throw e;
        } catch (Exception e) {
            // This is to handle RuntimeExceptions, for example thrown by Spring dynamic factory approaches
            log.warn("Problem resolving pipeline instance", e);
            throw new SOAPException("Could not resolve pipeline", e);
        }
    }
    
    /**
     * Get a new instance of the {@link HttpClientMessagePipeline} to be processed.
     * 
     * <p>
     * Each call to this (factory) method MUST produce a new instance of the pipeline.
     * </p>
     * 
     * @return the new pipeline instance
     * 
     * @throws SOAPException if there is an error obtaining a new pipeline instance
     */
    @Nonnull protected abstract HttpClientMessagePipeline<InboundMessageType, OutboundMessageType> newPipeline() 
            throws SOAPException;
    
    /**
     * Check that trust engine evaluation of the server TLS credential was actually performed.
     * 
     * @param context the current HTTP context instance in use
     * @param request the HTTP URI request
     * @throws SSLPeerUnverifiedException thrown if the TLS credential was not actually evaluated by the trust engine
     * 
     * @deprecated use {@link HttpClientSecuritySupport#checkTLSCredentialEvaluated(HttpClientContext, String)}
     */
    @Deprecated
    protected void checkTLSCredentialTrusted(@Nonnull final HttpClientContext context, 
            @Nonnull final HttpUriRequest request) throws SSLPeerUnverifiedException {
        HttpClientSecuritySupport.checkTLSCredentialEvaluated(context, request.getURI().getScheme());
    }
    
    /**
     * Build the {@link HttpUriRequest} instance to be executed by the HttpClient.
     * 
     * @param endpoint the endpoint to which the message will be sent
     * @param operationContext the current operation context
     * @return the HTTP request to be executed
     */
    @Nonnull protected HttpUriRequest buildHttpRequest(@Nonnull @NotEmpty final String endpoint, 
            @Nonnull final InOutOperationContext operationContext) {
        return new HttpPost(endpoint);
    }

    /**
     * Build the {@link HttpClientContext} instance to be used by the HttpClient.
     * 
     * @param request the HTTP client request
     * @param operationContext the current operation context
     * @return the client context instance
     */
    @Nonnull protected HttpClientContext buildHttpContext(@Nonnull final HttpUriRequest request, 
            @Nonnull final InOutOperationContext operationContext) {
        
        HttpClientContext clientContext = resolveClientContext(operationContext);
        
        HttpClientSecurityParameters contextSecurityParameters = resolveContextSecurityParameters(operationContext);
        
        HttpClientSecuritySupport.marshalSecurityParameters(clientContext, contextSecurityParameters, false);
        
        HttpClientSecuritySupport.marshalSecurityParameters(clientContext, getHttpClientSecurityParameters(), false);
        
        if ("https".equalsIgnoreCase(request.getURI().getScheme())) {
            if (clientContext.getAttribute(CONTEXT_KEY_TRUST_ENGINE) != null
                    && clientContext.getAttribute(CONTEXT_KEY_CRITERIA_SET) == null) {
                clientContext.setAttribute(CONTEXT_KEY_CRITERIA_SET, 
                        buildTLSCriteriaSet(request, operationContext));
            }
        }
        
        return clientContext;
    }
    
    /**
     * Resolve the {@link HttpClientSecurityParameters} instance present in the current operation context.
     * 
     * <p>
     * The default implementation returns the outbound subcontext value 
     * {@link HttpClientSecurityContext#getSecurityParameters()}.
     * </p>
     * 
     * <p>
     * Note that any values supplied via this instance will override those supplied locally via
     * {@link #setHttpClientSecurityParameters(HttpClientSecurityParameters)}.
     * </p>
     * 
     * @param operationContext the current operation context
     * @return the client security parameters resolved from the current operation context, or null
     */
    protected HttpClientSecurityParameters resolveContextSecurityParameters(
            @Nonnull final InOutOperationContext operationContext) {
        HttpClientSecurityContext securityContext = 
                operationContext.getOutboundMessageContext().getSubcontext(HttpClientSecurityContext.class);
        if (securityContext != null) {
            return securityContext.getSecurityParameters();
        } else {
            return null;
        }
    }

    /**
     * Resolve the effective {@link HttpClientContext} instance to use for the current request.
     * 
     * <p>
     * The default implementation first attempts to resolve the outbound subcontext value
     * {@link HttpClientRequestContext#getHttpClientContext()}. If no context value is present,
     * a new empty context instance will be returned via {@link HttpClientContext#create()}.
     * </p>
     * 
     * <p>
     * Note that any security-related attributes supplied directly the client context returned here
     * will override the corresponding values supplied via both operation context and locally-configured
     * instances of {@link HttpClientSecurityParameters}.
     * </p>
     * 
     * @param operationContext the current operation context
     * @return the effective client context instance to use
     */
    @Nonnull protected HttpClientContext resolveClientContext(@Nonnull final InOutOperationContext operationContext) {
        HttpClientRequestContext requestContext = 
                operationContext.getOutboundMessageContext().getSubcontext(HttpClientRequestContext.class);
        if (requestContext != null && requestContext.getHttpClientContext() != null) {
            return requestContext.getHttpClientContext();
        } else {
            return HttpClientContext.create();
        }
    }

    /**
     * Build the dynamic {@link CriteriaSet} instance to be used for TLS trust evaluation.
     * 
     * @param request the HTTP client request
     * @param operationContext the current operation context
     * @return the new criteria set instance
     */
    @Nonnull protected CriteriaSet buildTLSCriteriaSet(@Nonnull final HttpUriRequest request, 
            @Nonnull final InOutOperationContext operationContext) {
        
        CriteriaSet criteriaSet = new CriteriaSet();
        if (getTLSCriteriaSetStrategy() != null) {
            CriteriaSet resolved = getTLSCriteriaSetStrategy().apply(operationContext);
            if (resolved != null) {
                criteriaSet.addAll(resolved);
            }
        }
        if (!criteriaSet.contains(UsageType.class)) {
            criteriaSet.add(new UsageCriterion(UsageType.SIGNING));
        }
        return criteriaSet;
    }

}