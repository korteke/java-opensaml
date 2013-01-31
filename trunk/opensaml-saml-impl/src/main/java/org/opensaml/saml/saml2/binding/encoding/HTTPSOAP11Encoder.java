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

package org.opensaml.saml.saml2.binding.encoding;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import net.shibboleth.utilities.java.support.net.HttpServletSupport;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.soap.common.SOAPObjectBuilder;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.Body;
import org.opensaml.soap.soap11.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * SAML 2.0 SOAP 1.1 over HTTP binding encoder.
 */
public class HTTPSOAP11Encoder extends BaseSAML2MessageEncoder {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(HTTPSOAP11Encoder.class);

    /** Constructor. */
    public HTTPSOAP11Encoder() {
        super();
    }

    /** {@inheritDoc} */
    public String getBindingURI() {
        return SAMLConstants.SAML2_SOAP11_BINDING_URI;
    }

    /** {@inheritDoc} */
    public boolean providesMessageConfidentiality(MessageContext messageContext) throws MessageEncodingException {
        //TODO
        /*
        if (messageContext.getOutboundMessageTransport().isConfidential()) {
            return true;
        }
        */

        return false;
    }

    /** {@inheritDoc} */
    public boolean providesMessageIntegrity(MessageContext messageContext) throws MessageEncodingException {
        //TODO
        /*
        if (messageContext.getOutboundMessageTransport().isIntegrityProtected()) {
            return true;
        }
        */

        return false;
    }
    
    /** {@inheritDoc} */
    public void prepareContext() throws MessageEncodingException {
        MessageContext<SAMLObject> messageContext = getMessageContext();

        SAMLObject samlMessage = messageContext.getMessage();
        if (samlMessage == null) {
            throw new MessageEncodingException("No outbound SAML message contained in message context");
        }
        
        Envelope envelope = buildSOAPMessage(samlMessage);
        storeSOAPEnvelope(envelope);
    }

    /** {@inheritDoc} */
    protected void doEncode() throws MessageEncodingException {
        Envelope envelope = getSOAPEnvelope();
        Element envelopeElem = marshallMessage(envelope);

        HttpServletResponse response = getHttpServletResponse();
        HttpServletSupport.addNoCacheHeaders(response);
        HttpServletSupport.setUTF8Encoding(response);
        HttpServletSupport.setContentType(response, "text/xml");
        response.setHeader("SOAPAction", "http://www.oasis-open.org/committees/security");

        try {
            SerializeSupport.writeNode(envelopeElem, response.getOutputStream());
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
     * @param samlMessage body of the SOAP message
     * 
     * @return the SOAP message
     */
    @SuppressWarnings("unchecked")
    protected Envelope buildSOAPMessage(SAMLObject samlMessage) {
        if (log.isDebugEnabled()) {
            log.debug("Building SOAP message");
        }
        XMLObjectBuilderFactory builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();

        SOAPObjectBuilder<Envelope> envBuilder = (SOAPObjectBuilder<Envelope>) builderFactory
                .getBuilder(Envelope.DEFAULT_ELEMENT_NAME);
        Envelope envelope = envBuilder.buildObject();

        if (log.isDebugEnabled()) {
            log.debug("Adding SAML message to the SOAP message's body");
        }
        SOAPObjectBuilder<Body> bodyBuilder = (SOAPObjectBuilder<Body>) builderFactory
                .getBuilder(Body.DEFAULT_ELEMENT_NAME);
        Body body = bodyBuilder.buildObject();
        body.getUnknownXMLObjects().add(samlMessage);
        envelope.setBody(body);

        return envelope;
    }
    
    /** {@inheritDoc} */
    protected XMLObject getMessageToLog() {
        return getMessageContext().getSubcontext(SOAP11Context.class, true).getEnvelope();
    }
}