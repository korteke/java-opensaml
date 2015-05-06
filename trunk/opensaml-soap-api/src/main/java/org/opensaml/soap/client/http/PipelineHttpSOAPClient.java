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
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.messaging.decoder.httpclient.HttpClientResponseMessageDecoder;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.messaging.encoder.httpclient.HttpClientRequestMessageEncoder;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.messaging.pipeline.httpclient.HttpClientMessagePipeline;
import org.opensaml.messaging.pipeline.httpclient.HttpClientMessagePipelineFactory;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.security.httpclient.HttpClientSecurityConstants;
import org.opensaml.security.trust.TrustEngine;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.soap.client.SOAPClient;
import org.opensaml.soap.client.SOAPFaultException;
import org.opensaml.soap.common.SOAP11FaultDecodingException;
import org.opensaml.soap.common.SOAPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SOAP client that is based on {@link HttpClientMessagePipeline}.
 * 
 * @param <OutboundMessageType> the outbound message type
 * @param <InboundMessageType> the inbound message type
 */
@ThreadSafe
public class PipelineHttpSOAPClient<OutboundMessageType, InboundMessageType> extends AbstractInitializableComponent 
        implements SOAPClient {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(PipelineHttpSOAPClient.class);

    /** HTTP client used to send requests and receive responses. */
    @NonnullAfterInit private HttpClient httpClient;
    
    /** Factory for the client message pipeline. */
    private HttpClientMessagePipelineFactory<InboundMessageType, OutboundMessageType> pipelineFactory;
    
    /** The name of the specific pipeline to resolve and use. */
    private String pipelineName;
    
    /** Flag indicating whether presence of a pipeline factory instance is required at init time. 
     * Defaults to: <code>true</code>. */
    private boolean initRequiresPipelineFactory = true;
    
    /** HttpClient credentials provider. */
    private CredentialsProvider credentialsProvider;
    
    /** Optional trust engine used in evaluating server TLS credentials. */
    private TrustEngine<? super X509Credential> tlsTrustEngine;
    
    /** Constructor. */
    public PipelineHttpSOAPClient() {

    }

    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (httpClient == null) {
            throw new ComponentInitializationException("HttpClient cannot be null");
        } 
        
        if (initRequiresPipelineFactory && pipelineFactory == null) {
            throw new ComponentInitializationException("HttpClientPipelineFactory cannot be null");
        } 
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doDestroy() {
        httpClient = null;
        pipelineFactory = null;
        pipelineName = null;
        credentialsProvider = null;
        tlsTrustEngine = null;
        
        super.doDestroy();
    }

    /**
     * Flag indicating whether presence of a pipeline factory instance is required at init time.
     * 
     * <p>
     * Defaults to: <code>true</code>.
     * </p>
     * 
     * <p>
     * This check might be disabled if Spring lookup-method injection is being used to supply an 
     * implementation of {@link #getPipeline()}.
     * </p>
     * 
     * @param flag the flag value
     */
    public void setInitRequiresPipelineFactory(boolean flag) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        
        initRequiresPipelineFactory = flag;
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
     * Set the message pipeline factory.
     * 
     * @param factory the message pipeline factory
     */
    public void setPipelineFactory(
            @Nonnull final HttpClientMessagePipelineFactory<InboundMessageType, OutboundMessageType> factory) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        
        pipelineFactory = Constraint.isNotNull(factory, "HttpClientPipelineFactory cannot be null"); 
    }
    
    /**
     * Set the name of the pipeline to use.  Null may be specified.
     * 
     * <p>
     * The effective pipeline name is resolved via {@link #resolvePipelineName(InOutOperationContext)}.
     * </p>
     * 
     * 
     * @param name the pipeline name, or null
     */
    public void setPipelineName(@Nullable final String name) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        
        pipelineName = StringSupport.trimOrNull(name);
    }
    
    /**
     * Sets the optional trust engine used in evaluating server TLS credentials.
     * 
     * <p>
     * Must be used in conjunction with an HttpClient instance which is configured with a 
     * {@link org.opensaml.security.httpclient.impl.TrustEngineTLSSocketFactory}. If this socket
     * factory is not configured, then this will result in no TLS trust evaluation being performed
     * and a {@link SSLPeerUnverifiedException} will ultimately be thrown.
     * </p>
     * 
     * @param engine the trust engine instance to use
     */
    public void setTLSTrustEngine(@Nullable final TrustEngine<? super X509Credential> engine) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        
        tlsTrustEngine = engine;
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
     * A convenience method to set a (single) username and password used for BASIC authentication.
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
     * A convenience method to set a (single) username and password used for BASIC authentication.
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
    
    
    /** {@inheritDoc} */
    public void send(@Nonnull @NotEmpty final String endpoint, @Nonnull final InOutOperationContext operationContext)
            throws SOAPException, SecurityException {
        Constraint.isNotNull(endpoint, "Endpoint cannot be null");
        Constraint.isNotNull(operationContext, "Operation context cannot be null");
        
        HttpClientMessagePipeline<InboundMessageType, OutboundMessageType> pipeline = null;
        try {
            // Pipeline resolution
            String resolvedPipelineName = resolvePipelineName(operationContext);
            if (resolvedPipelineName != null) {
                pipeline = getPipeline(resolvedPipelineName);
            } else {
                pipeline = getPipeline();
            }
            if (pipeline == null) {
                throw new SOAPException("Could not resolve SOAP processing pipeline with name: " 
                        + resolvedPipelineName);
            }
            
            // Outbound payload handling
            if (pipeline.getOutboundPayloadMessageHandler() != null) {
                pipeline.getOutboundPayloadMessageHandler().invoke(operationContext.getOutboundMessageContext());
            }
            
            HttpUriRequest httpRequest = buildHttpRequest(endpoint, operationContext);
            HttpClientContext httpContext = buildHttpContext(endpoint, operationContext);
            
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
            HttpResponse httpResponse = httpClient.execute(httpRequest, httpContext);
            checkTLSCredentialTrusted(httpContext, httpRequest);
            
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
            pipeline.getEncoder().destroy();
            pipeline.getDecoder().destroy();
        }
    }
    
    /**
     * Check that trust engine evaluation of the server TLS credential was actually performed.
     * 
     * @param context the current HTTP context instance in use
     * @param request the HTTP URI request
     * @throws SSLPeerUnverifiedException thrown if the TLS credential was not actually evaluated by the trust engine
     */
    protected void checkTLSCredentialTrusted(@Nonnull final HttpClientContext context, 
            @Nonnull final HttpUriRequest request) throws SSLPeerUnverifiedException {
        if (tlsTrustEngine != null && "https".equalsIgnoreCase(request.getURI().getScheme())) {
            if (context.getAttribute(HttpClientSecurityConstants.CONTEXT_KEY_SERVER_TLS_CREDENTIAL_TRUSTED) == null) {
                log.warn("Configured TLS trust engine was not used to verify server TLS credential, " 
                        + "the appropriate socket factory was likely not configured");
                throw new SSLPeerUnverifiedException(
                        "Evaluation of server TLS credential with configured TrustEngine was not performed");
            }
        }
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
     * @param endpoint the endpoint to which the message will be sent
     * @param operationContext the current operation context
     * @return the client context instance
     */
    @Nonnull protected HttpClientContext buildHttpContext(@Nonnull @NotEmpty final String endpoint, 
            @Nonnull final InOutOperationContext operationContext) {
        
        final HttpClientContext context = HttpClientContext.create();
        if (credentialsProvider != null) {
            context.setCredentialsProvider(credentialsProvider);
        }
        if (tlsTrustEngine != null) {
            context.setAttribute(HttpClientSecurityConstants.CONTEXT_KEY_TRUST_ENGINE, tlsTrustEngine);
            context.setAttribute(HttpClientSecurityConstants.CONTEXT_KEY_CRITERIA_SET, 
                    buildTLSCriteriaSet(endpoint, operationContext));
        }
        return context;
    }
    
    /**
     * Build the {@link CriteriaSet} instance to be used for TLS trust evaluation.
     * 
     * @param endpoint the endpoint to which the message will be sent
     * @param operationContext the current operation context
     * @return the new criteria set instance
     */
    @Nonnull protected CriteriaSet buildTLSCriteriaSet(@Nonnull @NotEmpty final String endpoint, 
            @Nonnull final InOutOperationContext operationContext) {
        
        CriteriaSet criteriaSet = new CriteriaSet();
        criteriaSet.add(new UsageCriterion(UsageType.SIGNING));
        //TODO need support for other custom criteria, based on e.g. peer entityID
        //     -- probably support pluggable CriteriaSet builder strategy
        return criteriaSet;
    }

    /**
     * Get the {@link HttpClientMessagePipeline} instance to be processed.
     * @return the new pipeline instance
     */
    @Nullable protected HttpClientMessagePipeline<InboundMessageType, OutboundMessageType> getPipeline() {
        // Note: in a Spring environment, the actual factory impl might be a proxy via ServiceLocatorFactoryBean,
        // or this entire method might be overridden with lookup-method injection.
        return pipelineFactory.newInstance();
    }
    
    /**
     * Get the {@link HttpClientMessagePipeline} instance to be processed.
     * @param name the pipeline name to resolve
     * @return the new pipeline instance
     */
    @Nullable protected HttpClientMessagePipeline<InboundMessageType, OutboundMessageType> getPipeline(
            @Nullable final String name) {
        // Note: in a Spring environment, the actual factory impl might be a proxy via ServiceLocatorFactoryBean
        return pipelineFactory.newInstance(name);
    }
    
    /**
     * Resolve the name of the pipeline to use.
     * 
     * @param operationContext the current operation context
     * @return the pipeline name, may be null
     */
    @Nullable protected String resolvePipelineName(@Nonnull final InOutOperationContext operationContext) {
        //TODO support dynamic resolution from operation context data/subcontext, perhaps via pluggable strategy.
        return pipelineName;
    }

}