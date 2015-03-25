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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.component.AbstractInitializableComponent;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.xml.ParserPool;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.security.SecurityException;
import org.opensaml.soap.client.SOAPClient;
import org.opensaml.soap.client.SOAPClientContext;
import org.opensaml.soap.client.SOAPClientException;
import org.opensaml.soap.client.SOAPFaultException;
import org.opensaml.soap.common.SOAPException;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.Envelope;
import org.opensaml.soap.soap11.Fault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.google.common.base.Function;

/**
 * SOAP client that uses HTTP as the underlying transport and POST as the binding.
 */
@ThreadSafe
public class HttpSOAPClient extends AbstractInitializableComponent implements SOAPClient {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(HttpSOAPClient.class);

    /** HTTP client used to send requests and receive responses. */
    @NonnullAfterInit private HttpClient httpClient;

    /** Pool of XML parsers used to parser incoming responses. */
    @NonnullAfterInit private ParserPool parserPool;

    /**
     * Strategy used to look up the {@link SOAPClientContext} associated with the
     * outbound message context.
     */
    @Nonnull private Function<MessageContext, SOAPClientContext> soapClientContextLookupStrategy;

    /**
     * Strategy used to look up the {@link SOAP11Context} associated with the
     * outbound message context.
     */
    @Nonnull private Function<MessageContext, SOAP11Context> soap11ContextLookupStrategy;
    
    /** Constructor. */
    public HttpSOAPClient() {
        soapClientContextLookupStrategy =
                new ChildContextLookup<MessageContext, SOAPClientContext>(SOAPClientContext.class, false);
        soap11ContextLookupStrategy =
                new ChildContextLookup<MessageContext, SOAP11Context>(SOAP11Context.class, false);
    }

    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (httpClient == null) {
            throw new ComponentInitializationException("HttpClient cannot be null");
        } else if (parserPool == null) {
            throw new ComponentInitializationException("ParserPool cannot be null");
        }
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
        
        httpClient = Constraint.isNotNull(client, "HttpClient cannot be null");
    }
    
    /**
     * Set the pool of XML parsers used to parse incoming responses.
     * 
     * @param parser parser pool
     */
    public void setParserPool(@Nonnull final ParserPool parser) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        parserPool = Constraint.isNotNull(parser, "ParserPool cannot be null");
    }
    



    /**
     * Get the strategy used to look up the {@link SOAPClientContext} associated with the outbound message
     * context.
     * 
     * @return strategy used to look up the {@link SOAPClientContext} associated with the outbound message
     *         context
     */
    @Nonnull public Function<MessageContext,SOAPClientContext> getSOAPClientContextLookupStrategy() {
        return soapClientContextLookupStrategy;
    }

    /**
     * Set the strategy used to look up the {@link SOAPClientContext} associated with the outbound message
     * context.
     * 
     * @param strategy strategy used to look up the {@link SOAPClientContext} associated with the outbound
     *            message context
     */
    public void setSOAPClientContextLookupStrategy(@Nonnull final Function<MessageContext,SOAPClientContext> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        soapClientContextLookupStrategy =
                Constraint.isNotNull(strategy, "SOAP client context lookup strategy cannot be null");
    }

    /**
     * Get the strategy used to look up the {@link SOAP11Context} associated with the outbound message
     * context.
     * 
     * @return strategy used to look up the {@link SOAP11Context} associated with the outbound message
     *         context
     */
    @Nonnull public Function<MessageContext,SOAP11Context> getSOAP11ContextLookupStrategy() {
        return soap11ContextLookupStrategy;
    }

    /**
     * Set the strategy used to look up the {@link SOAP11Context} associated with the outbound message
     * context.
     * 
     * @param strategy strategy used to look up the {@link SOAP11Context} associated with the outbound
     *            message context
     */
    public void setSOAP11ContextLookupStrategy(@Nonnull final Function<MessageContext,SOAP11Context> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        soap11ContextLookupStrategy =
                Constraint.isNotNull(strategy, "SOAP 1.1 context lookup strategy cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    public void send(@Nonnull @NotEmpty final String endpoint, @Nonnull final InOutOperationContext context)
            throws SOAPException, SecurityException {
        Constraint.isNotNull(endpoint, "Endpoint cannot be null");
        Constraint.isNotNull(context, "Operation context cannot be null");
        
        final SOAP11Context soapCtx = soap11ContextLookupStrategy.apply(context.getOutboundMessageContext());
        final SOAPClientContext clientCtx = soapClientContextLookupStrategy.apply(context.getOutboundMessageContext());

        HttpSOAPRequestParameters soapRequestParams = null;
        
        if (soapCtx == null || soapCtx.getEnvelope() == null) {
            throw new SOAPClientException("Operation context did not contain an outbound SOAP Envelope");
        } else if (clientCtx != null) {
            soapRequestParams = (HttpSOAPRequestParameters) clientCtx.getSOAPRequestParameters();
        }
        
        HttpPost post = null;
        try {
            post = createPostMethod(endpoint, soapRequestParams, soapCtx.getEnvelope());

            final HttpResponse result = httpClient.execute(post);
            final int code = result.getStatusLine().getStatusCode();
            log.debug("Received HTTP status code of {} when POSTing SOAP message to {}", code, endpoint);

            if (code == HttpStatus.SC_OK) {
                processSuccessfulResponse(result, context);
            } else if (code == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                processFaultResponse(result, context);
            } else {
                throw new SOAPClientException("Received " + code + " HTTP response status code from HTTP request to "
                        + endpoint);
            }
        } catch (final IOException e) {
            throw new SOAPClientException("Unable to send request to " + endpoint, e);
        } finally {
            if (post != null) {
                post.reset();
            }
        }
    }

    /**
     * Create the post method used to send the SOAP request.
     * 
     * @param endpoint endpoint to which the message is sent
     * @param requestParams HTTP request parameters
     * @param message message to be sent
     * 
     * @return the post method to be used to send this message
     * 
     * @throws SOAPClientException thrown if the message could not be marshalled
     */
    protected HttpPost createPostMethod(@Nonnull @NotEmpty final String endpoint,
            @Nullable final HttpSOAPRequestParameters requestParams, @Nonnull final Envelope message)
            throws SOAPClientException {
        log.debug("POSTing SOAP message to {}", endpoint);

        final HttpPost post = new HttpPost(endpoint);
        post.setEntity(createRequestEntity(message, Charset.forName("UTF-8")));
        if (requestParams != null && requestParams.getSOAPAction() != null) {
            post.setHeader(HttpSOAPRequestParameters.SOAP_ACTION_HEADER, requestParams.getSOAPAction());
        }

        return post;
    }

    /**
     * Create the request entity that makes up the POST message body.
     * 
     * @param message message to be sent
     * @param charset character set used for the message
     * 
     * @return request entity that makes up the POST message body
     * 
     * @throws SOAPClientException thrown if the message could not be marshalled
     */
    protected HttpEntity createRequestEntity(@Nonnull final Envelope message, @Nullable final Charset charset)
            throws SOAPClientException {
        try {
            final Marshaller marshaller = Constraint.isNotNull(
                    XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(message),
                    "SOAP Envelope marshaller not available");
            final ByteArrayOutputStream arrayOut = new ByteArrayOutputStream();

            if (log.isDebugEnabled()) {
                log.debug("Outbound SOAP message is:\n" +
                        SerializeSupport.prettyPrintXML(marshaller.marshall(message)));
            }
            SerializeSupport.writeNode(marshaller.marshall(message), arrayOut);
            return new ByteArrayEntity(arrayOut.toByteArray(), ContentType.create("text/xml", charset));
        } catch (final MarshallingException e) {
            throw new SOAPClientException("Unable to marshall SOAP envelope", e);
        }
    }

    /**
     * Process a successful, as determined by an HTTP 200 status code, response.
     * 
     * @param httpResponse the HTTP response
     * @param context current operation context
     * 
     * @throws SOAPClientException thrown if there is a problem reading the response from the {@link HttpPost}
     */
    protected void processSuccessfulResponse(@Nonnull final HttpResponse httpResponse,
            @Nonnull final InOutOperationContext context) throws SOAPClientException {
        try {
            if (httpResponse.getEntity() == null) {
                throw new SOAPClientException("No response body from server");
            }
            final Envelope response = unmarshallResponse(httpResponse.getEntity().getContent());
            context.setInboundMessageContext(new MessageContext());
            context.getInboundMessageContext().getSubcontext(SOAP11Context.class, true).setEnvelope(response);
            //TODO: goes away?
            //evaluateSecurityPolicy(messageContext);
        } catch (final IOException e) {
            throw new SOAPClientException("Unable to read response", e);
        }
    }

    /**
     * Process a SOAP fault, as determined by an HTTP 500 status code, response.
     * 
     * @param httpResponse the HTTP response
     * @param context current operation context
     * 
     * @throws SOAPClientException thrown if the response can not be read from the {@link HttpPost}
     * @throws SOAPFaultException an exception containing the SOAP fault
     */
    protected void processFaultResponse(@Nonnull final HttpResponse httpResponse,
            @Nonnull final InOutOperationContext context) throws SOAPClientException, SOAPFaultException {
        try {
            if (httpResponse.getEntity() == null) {
                throw new SOAPClientException("No response body from server");
            }
            final Envelope response = unmarshallResponse(httpResponse.getEntity().getContent());
            context.setInboundMessageContext(new MessageContext());
            context.getInboundMessageContext().getSubcontext(SOAP11Context.class, true).setEnvelope(response);

            if (response.getBody() != null) {
                final List<XMLObject> faults = response.getBody().getUnknownXMLObjects(Fault.DEFAULT_ELEMENT_NAME);
                if (faults.size() < 1) {
                    throw new SOAPClientException("HTTP status code was 500 but SOAP response did not contain a Fault");
                }
                
                String code = "(not set)";
                String msg = "(not set)";
                final Fault fault = (Fault) faults.get(0);
                if (fault.getCode() != null) {
                    code = fault.getCode().getValue().toString();
                }
                if (fault.getMessage() != null) {
                    msg = fault.getMessage().getValue();
                }
                
                log.debug("SOAP fault code {} with message {}", code, msg);
                final SOAPFaultException faultException = new SOAPFaultException("SOAP Fault: " + code
                        + " Fault Message: " + msg);
                faultException.setFault(fault);
                throw faultException;
            } else {
                throw new SOAPClientException("HTTP status code was 500 but SOAP response did not contain a Body");
            }
        } catch (final IOException e) {
            throw new SOAPClientException("Unable to read response", e);
        }
    }

    /**
     * Unmarshall the incoming response from a POST request.
     * 
     * @param responseStream input stream bearing the response
     * 
     * @return the response
     * 
     * @throws SOAPClientException thrown if the incoming response can not be unmarshalled into an {@link Envelope}
     */
    protected Envelope unmarshallResponse(@Nonnull final InputStream responseStream) throws SOAPClientException {
        try {
            final Element responseElem = parserPool.parse(responseStream).getDocumentElement();
            if (log.isDebugEnabled()) {
                log.debug("Inbound SOAP message was:\n" + SerializeSupport.prettyPrintXML(responseElem));
            }
            final Unmarshaller unmarshaller = Constraint.isNotNull(
                    XMLObjectProviderRegistrySupport.getUnmarshallerFactory().getUnmarshaller(responseElem),
                    "SOAP envelope unmarshaller not available");
            return (Envelope) unmarshaller.unmarshall(responseElem);
        } catch (final XMLParserException e) {
            throw new SOAPClientException("Unable to parse the XML within the response", e);
        } catch (final UnmarshallingException e) {
            throw new SOAPClientException("Unable to unmarshall the response DOM", e);
        }
    }

    /**
     * Evaluates the security policy associated with the given message context. If no policy resolver is registered or
     * no policy is located during the resolution process then no policy is evaluated. Note that neither the inbound or
     * outbound message transport is available.
     * 
     * @param messageContext current message context
     * 
     * @throws SOAPClientException thrown if there is a problem resolving or evaluating a security policy
     */
    protected void evaluateSecurityPolicy(SOAPClientContext messageContext) throws SOAPClientException {
        //TODO: I think this goes away, with the policy layer living outside the client?
        /*
        SecurityPolicyResolver policyResolver = messageContext.getSecurityPolicyResolver();
        if (policyResolver == null) {
            return;
        }

        SecurityPolicy policy = null;
        try {
            policy = policyResolver.resolveSingle(messageContext);
            if (policy == null) {
                return;
            }
        } catch (ResolverException e) {
            throw new SOAPClientException("Unable to resolve security policy for inbound SOAP response", e);
        }

        try {
            log.debug("Evaluating security policy for inbound SOAP response");
            policy.evaluate(messageContext);
        } catch (SecurityException e) {
            throw new SOAPClientException("Inbound SOAP response does not meet security policy", e);
        }
        */
    }
    
}