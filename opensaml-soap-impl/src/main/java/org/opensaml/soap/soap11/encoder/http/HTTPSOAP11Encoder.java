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

package org.opensaml.soap.soap11.encoder.http;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.shibboleth.utilities.java.support.net.HttpServletSupport;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.messaging.encoder.servlet.BaseHttpServletResponseXmlMessageEncoder;
import org.opensaml.soap.common.SOAPObjectBuilder;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.Body;
import org.opensaml.soap.soap11.Envelope;
import org.opensaml.soap.soap11.Header;
import org.opensaml.soap.wsaddressing.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * Basic SOAP 1.1 encoder for HTTP transport.
 * 
 * @param <MessageType> the message type of the message context on which to operate
 */
public class HTTPSOAP11Encoder<MessageType extends XMLObject> 
        extends BaseHttpServletResponseXmlMessageEncoder<MessageType> {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(HTTPSOAP11Encoder.class);
    
    /** SOAP Envelope builder. */
    private SOAPObjectBuilder<Envelope> envBuilder;
    
    /** SOAP Body builder. */
    private SOAPObjectBuilder<Body> bodyBuilder;
    
    /** Constructor. */
    public HTTPSOAP11Encoder() {
        super();
        XMLObjectBuilderFactory builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
        envBuilder = (SOAPObjectBuilder<Envelope>) builderFactory.getBuilder(Envelope.DEFAULT_ELEMENT_NAME);
        bodyBuilder = (SOAPObjectBuilder<Body>) builderFactory.getBuilder(Body.DEFAULT_ELEMENT_NAME);
    }
    
    /** {@inheritDoc} */
    public void prepareContext() throws MessageEncodingException {
        MessageContext<MessageType> messageContext = getMessageContext();
        MessageType message = messageContext.getMessage();
        if (message == null) {
            throw new MessageEncodingException("No outbound message contained in message context");
        }
        
        if (message instanceof Envelope) {
            storeSOAPEnvelope((Envelope) message);
        } else {
            buildAndStoreSOAPMessage(message);
        }
        
    }

    /** {@inheritDoc} */
    protected void doEncode() throws MessageEncodingException {
        Envelope envelope = getSOAPEnvelope();
        Element envelopeElem = marshallMessage(envelope);
        
        prepareHttpServletResponse();

        try {
            SerializeSupport.writeNode(envelopeElem, getHttpServletResponse().getOutputStream());
        } catch (IOException e) {
            throw new MessageEncodingException("Problem writing SOAP envelope to servlet output stream", e);
        }
    }
    
    /**
     * Store the constructed SOAP envelope in the message context for later encoding.
     * 
     * @param envelope the SOAP envelope
     */
    protected void storeSOAPEnvelope(Envelope envelope) {
        getMessageContext().getSubcontext(SOAP11Context.class, true).setEnvelope(envelope);
    }

    /**
     * Retrieve the previously stored SOAP envelope from the message context.
     * 
     * @return the previously stored SOAP envelope
     */
    protected Envelope getSOAPEnvelope() {
        return getMessageContext().getSubcontext(SOAP11Context.class, true).getEnvelope();
    }

    /**
     * Builds the SOAP message to be encoded.
     * 
     * @param payload body of the SOAP message
     */
    @SuppressWarnings("unchecked")
    protected void buildAndStoreSOAPMessage(XMLObject payload) {
        Envelope envelope = getSOAPEnvelope();
        if (envelope == null) {
            envelope = envBuilder.buildObject();
            storeSOAPEnvelope(envelope);
        }
        
        Body body = envelope.getBody();
        if (body == null) {
            body = bodyBuilder.buildObject();
            envelope.setBody(body);
        }
        
        if (!body.getUnknownXMLObjects().isEmpty()) {
            log.warn("Existing SOAP Envelope Body already contained children");
        }
        
        body.getUnknownXMLObjects().add(payload);
    }
    
    
    /**
     * <p>
     * This implementation performs the following actions on the context's {@link HttpServletResponse}:
     * <ol>
     *   <li>Adds the HTTP header: "Cache-control: no-cache, no-store"</li>
     *   <li>Adds the HTTP header: "Pragma: no-cache"</li>
     *   <li>Sets the character encoding to: "UTF-8"</li>
     *   <li>Sets the content type to: "text/xml"</li>
     *   <li>Sets the SOAPAction HTTP header the value returned by {@link #getSOAPAction(MessageContext)}, if
     *   that returns non-null.</li>
     * </ol>
     * </p>
     * 
     * <p>
     * Subclasses should NOT set the SOAPAction HTTP header in this method. Instead, they should override 
     * the method {@link #getSOAPAction(MessageContext)}.
     * </p>
     * 
     * @throws MessageEncodingException thrown if there is a problem preprocessing the transport
     */
    protected void prepareHttpServletResponse() throws MessageEncodingException {
        HttpServletResponse response = getHttpServletResponse();
        HttpServletSupport.addNoCacheHeaders(response);
        HttpServletSupport.setUTF8Encoding(response);
        HttpServletSupport.setContentType(response, "text/xml");
        
        String soapAction = getSOAPAction();
        if (soapAction != null) {
            response.setHeader("SOAPAction", soapAction);
        } else {
            response.setHeader("SOAPAction", "");
        }
    }
 
    /**
     * Determine the value of the SOAPAction HTTP header to send.
     * 
     * <p>
     * The default behavior is to return the value of the SOAP Envelope's WS-Addressing Action header,
     * if present.
     * </p>
     * 
     * @return a SOAPAction HTTP header URI value
     */
    protected String getSOAPAction() {
        Envelope env = getSOAPEnvelope();
        Header header = env.getHeader();
        if (header == null) {
            return null;
        }
        List<XMLObject> objList = header.getUnknownXMLObjects(Action.ELEMENT_NAME);
        if (objList == null || objList.isEmpty()) {
            return null;
        } else {
            return ((Action)objList.get(0)).getValue();
        }
    }
    
    /** {@inheritDoc} */
    protected XMLObject getMessageToLog() {
        return getMessageContext().getSubcontext(SOAP11Context.class, true).getEnvelope();
    }
    
}