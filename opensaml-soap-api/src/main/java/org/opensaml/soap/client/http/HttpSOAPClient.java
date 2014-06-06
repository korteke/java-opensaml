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
import javax.annotation.concurrent.ThreadSafe;

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
public class HttpSOAPClient implements SOAPClient {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(HttpSOAPClient.class);

    /** HTTP client used to send requests and receive responses. */
    private final HttpClient httpClient;

    /** Pool of XML parsers used to parser incoming responses. */
    private final ParserPool parserPool;

    /**
     * Strategy used to look up the {@link SOAPClientContext} associated with the
     * outbound message context.
     */
    private Function<MessageContext, SOAPClientContext> soapClientContextLookupStrategy;

    /**
     * Strategy used to look up the {@link SOAP11Context} associated with the
     * outbound message context.
     */
    private Function<MessageContext, SOAP11Context> soap11ContextLookupStrategy;
    
    /**
     * Constructor.
     * 
     * @param client Client used to make outbound HTTP requests. This client SHOULD employ a
     *            thread-safe {@link HttpClient} and may be shared with other objects.
     * @param parser pool of XML parsers used to parse incoming responses
     */
    public HttpSOAPClient(@Nonnull final HttpClient client, @Nonnull final ParserPool parser) {
        httpClient = Constraint.isNotNull(client, "HttpClient cannot be null");
        parserPool = Constraint.isNotNull(parser, "ParserPool cannot be null");
        
        soapClientContextLookupStrategy =
                new ChildContextLookup<MessageContext, SOAPClientContext>(SOAPClientContext.class, false);
        soap11ContextLookupStrategy =
                new ChildContextLookup<MessageContext, SOAP11Context>(SOAP11Context.class, false);
    }

    /**
     * Gets the strategy used to look up the {@link SOAPClientContext} associated with the outbound message
     * context.
     * 
     * @return strategy used to look up the {@link SOAPClientContext} associated with the outbound message
     *         context
     */
    @Nonnull public Function<MessageContext, SOAPClientContext> getSOAPClientContextLookupStrategy() {
        return soapClientContextLookupStrategy;
    }

    /**
     * Sets the strategy used to look up the {@link SOAPClientContext} associated with the outbound message
     * context.
     * 
     * @param strategy strategy used to look up the {@link SOAPClientContext} associated with the outbound
     *            message context
     */
    public void setSOAPClientContextLookupStrategy(
            @Nonnull final Function<MessageContext, SOAPClientContext> strategy) {
        soapClientContextLookupStrategy =
                Constraint.isNotNull(strategy, "SOAP client context lookup strategy cannot be null");
    }

    /**
     * Gets the strategy used to look up the {@link SOAP11Context} associated with the outbound message
     * context.
     * 
     * @return strategy used to look up the {@link SOAP11Context} associated with the outbound message
     *         context
     */
    @Nonnull public Function<MessageContext, SOAP11Context> getSOAP11ContextLookupStrategy() {
        return soap11ContextLookupStrategy;
    }

    /**
     * Sets the strategy used to look up the {@link SOAP11Context} associated with the outbound message
     * context.
     * 
     * @param strategy strategy used to look up the {@link SOAP11Context} associated with the outbound
     *            message context
     */
    public void setSOAP11ContextLookupStrategy(
            @Nonnull final Function<MessageContext, SOAP11Context> strategy) {
        soap11ContextLookupStrategy =
                Constraint.isNotNull(strategy, "SOAP 1.1 context lookup strategy cannot be null");
    }
    
    /** {@inheritDoc} */
    public void send(String endpoint, InOutOperationContext context) throws SOAPException, SecurityException {
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

            HttpResponse result = httpClient.execute(post);
            int code = result.getStatusLine().getStatusCode();
            log.debug("Received HTTP status code of {} when POSTing SOAP message to {}", code, endpoint);

            if (code == HttpStatus.SC_OK) {
                processSuccessfulResponse(result, context);
            } else if (code == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                processFaultResponse(result, context);
            } else {
                throw new SOAPClientException("Received " + code + " HTTP response status code from HTTP request to "
                        + endpoint);
            }
        } catch (IOException e) {
            throw new SOAPClientException("Unable to send request to " + endpoint, e);
        } finally {
            if (post != null) {
                post.reset();
            }
        }
    }

    /**
     * Creates the post method used to send the SOAP request.
     * 
     * @param endpoint endpoint to which the message is sent
     * @param requestParams HTTP request parameters
     * @param message message to be sent
     * 
     * @return the post method to be used to send this message
     * 
     * @throws SOAPClientException thrown if the message could not be marshalled
     */
    protected HttpPost createPostMethod(String endpoint, HttpSOAPRequestParameters requestParams, Envelope message)
            throws SOAPClientException {
        log.debug("POSTing SOAP message to {}", endpoint);

        HttpPost post = new HttpPost(endpoint);
        post.setEntity(createRequestEntity(message, Charset.forName("UTF-8")));
        if (requestParams != null && requestParams.getSOAPAction() != null) {
            post.setHeader(HttpSOAPRequestParameters.SOAP_ACTION_HEADER, requestParams.getSOAPAction());
        }

        return post;
    }

    /**
     * Creates the request entity that makes up the POST message body.
     * 
     * @param message message to be sent
     * @param charset character set used for the message
     * 
     * @return request entity that makes up the POST message body
     * 
     * @throws SOAPClientException thrown if the message could not be marshalled
     */
    protected HttpEntity createRequestEntity(Envelope message, Charset charset) throws SOAPClientException {
        try {
            Marshaller marshaller = Constraint.isNotNull(
                    XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(message),
                    "SOAP Envelope marshaller not available");
            ByteArrayOutputStream arrayOut = new ByteArrayOutputStream();

            if (log.isDebugEnabled()) {
                log.debug("Outbound SOAP message is:\n" +
                        SerializeSupport.prettyPrintXML(marshaller.marshall(message)));
            }
            SerializeSupport.writeNode(marshaller.marshall(message), arrayOut);
            return new ByteArrayEntity(arrayOut.toByteArray(), ContentType.create("text/xml", charset));
        } catch (MarshallingException e) {
            throw new SOAPClientException("Unable to marshall SOAP envelope", e);
        }
    }

    /**
     * Processes a successful, as determined by an HTTP 200 status code, response.
     * 
     * @param httpResponse the HTTP response
     * @param context current operation context
     * 
     * @throws SOAPClientException thrown if there is a problem reading the response from the {@link HttpPost}
     */
    protected void processSuccessfulResponse(HttpResponse httpResponse, InOutOperationContext context)
            throws SOAPClientException {
        try {
            if (httpResponse.getEntity() == null) {
                throw new SOAPClientException("No response body from server");
            }
            Envelope response = unmarshallResponse(httpResponse.getEntity().getContent());
            context.setInboundMessageContext(new MessageContext());
            context.getInboundMessageContext().getSubcontext(SOAP11Context.class, true).setEnvelope(response);
            //TODO: goes away?
            //evaluateSecurityPolicy(messageContext);
        } catch (IOException e) {
            throw new SOAPClientException("Unable to read response", e);
        }
    }

    /**
     * Processes a SOAP fault, as determined by an HTTP 500 status code, response.
     * 
     * @param httpResponse the HTTP response
     * @param context current operation context
     * 
     * @throws SOAPClientException thrown if the response can not be read from the {@link HttpPost}
     * @throws SOAPFaultException an exception containing the SOAP fault
     */
    protected void processFaultResponse(HttpResponse httpResponse, InOutOperationContext context)
            throws SOAPClientException, SOAPFaultException {
        try {
            if (httpResponse.getEntity() == null) {
                throw new SOAPClientException("No response body from server");
            }
            Envelope response = unmarshallResponse(httpResponse.getEntity().getContent());
            context.setInboundMessageContext(new MessageContext());
            context.getInboundMessageContext().getSubcontext(SOAP11Context.class, true).setEnvelope(response);

            if (response.getBody() != null) {
                List<XMLObject> faults = response.getBody().getUnknownXMLObjects(Fault.DEFAULT_ELEMENT_NAME);
                if (faults.size() < 1) {
                    throw new SOAPClientException("HTTP status code was 500 but SOAP response did not contain a Fault");
                }
                
                String code = "(not set)";
                String msg = "(not set)";
                Fault fault = (Fault) faults.get(0);
                if (fault.getCode() != null) {
                    code = fault.getCode().getValue().toString();
                }
                if (fault.getMessage() != null) {
                    msg = fault.getMessage().getValue();
                }
                
                log.debug("SOAP fault code {} with message {}", code, msg);
                SOAPFaultException faultException = new SOAPFaultException("SOAP Fault: " + code
                        + " Fault Message: " + msg);
                faultException.setFault(fault);
                throw faultException;
            } else {
                throw new SOAPClientException("HTTP status code was 500 but SOAP response did not contain a Body");
            }
        } catch (IOException e) {
            throw new SOAPClientException("Unable to read response", e);
        }
    }

    /**
     * Unmarshalls the incoming response from a POST request.
     * 
     * @param responseStream input stream bearing the response
     * 
     * @return the response
     * 
     * @throws SOAPClientException thrown if the incoming response can not be unmarshalled into an {@link Envelope}
     */
    protected Envelope unmarshallResponse(InputStream responseStream) throws SOAPClientException {
        try {
            Element responseElem = parserPool.parse(responseStream).getDocumentElement();
            if (log.isDebugEnabled()) {
                log.debug("Inbound SOAP message was:\n" + SerializeSupport.prettyPrintXML(responseElem));
            }
            Unmarshaller unmarshaller = Constraint.isNotNull(
                    XMLObjectProviderRegistrySupport.getUnmarshallerFactory().getUnmarshaller(responseElem),
                    "SOAP envelope unmarshaller not available");
            return (Envelope) unmarshaller.unmarshall(responseElem);
        } catch (XMLParserException e) {
            throw new SOAPClientException("Unable to parse the XML within the response", e);
        } catch (UnmarshallingException e) {
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