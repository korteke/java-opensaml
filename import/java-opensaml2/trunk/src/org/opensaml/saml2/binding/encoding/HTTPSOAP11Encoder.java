/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.saml2.binding.encoding;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.opensaml.Configuration;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.binding.SAMLMessageContext;
import org.opensaml.log.Level;
import org.opensaml.ws.message.MessageContext;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.ws.soap.common.SOAPObjectBuilder;
import org.opensaml.ws.soap.soap11.Body;
import org.opensaml.ws.soap.soap11.Envelope;
import org.opensaml.ws.transport.http.HTTPOutTransport;
import org.opensaml.ws.transport.http.HTTPTransportUtils;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Element;

/**
 * SAML 2.0 SOAP 1.1 over HTTP binding encoder.
 */
public class HTTPSOAP11Encoder extends BaseSAML2MessageEncoder {

    /** Class logger. */
    private final Logger log = Logger.getLogger(HTTPSOAP11Encoder.class);

    /** Constructor. */
    public HTTPSOAP11Encoder() {
        super();
    }

    /** {@inheritDoc} */
    public String getBindingURI() {
        return "urn:oasis:names:tc:SAML:2.0:bindings:SOAP";
    }

    /** {@inheritDoc} */
    protected void doEncode(MessageContext messageContext) throws MessageEncodingException {
        if (!(messageContext instanceof SAMLMessageContext)) {
            log.error("Invalid message context type, this encoder only support SAMLMessageContext");
            throw new MessageEncodingException(
                    "Invalid message context type, this encoder only support SAMLMessageContext");
        }

        if (!(messageContext.getOutboundMessageTransport() instanceof HTTPOutTransport)) {
            log.error("Invalid outbound message transport type, this encoder only support HTTPOutTransport");
            throw new MessageEncodingException(
                    "Invalid outbound message transport type, this encoder only support HTTPOutTransport");
        }

        SAMLMessageContext samlMsgCtx = (SAMLMessageContext) messageContext;

        SAMLObject samlMessage = samlMsgCtx.getOutboundSAMLMessage();
        if (samlMessage == null) {
            throw new MessageEncodingException("No outbound SAML message contained in message context");
        }

        Envelope envelope = buildSOAPMessage(samlMessage);
        samlMsgCtx.setOutboundMessage(envelope);

        Element envelopeElem = marshallMessage(envelope);
        if (log.isEnabledFor(Level.TRAIL)) {
            log.log(Level.TRAIL, "Writting SOAP message to response:\n" + XMLHelper.nodeToString(envelopeElem));
        }

        try {
            HTTPOutTransport outTransport = (HTTPOutTransport) messageContext.getOutboundMessageTransport();
            HTTPTransportUtils.addNoCacheHeaders(outTransport);
            HTTPTransportUtils.setUTF8Encoding(outTransport);
            HTTPTransportUtils.setContentType(outTransport, "text/xml");
            outTransport.setHeader("SOAPAction", "http://www.oasis-open.org/committees/security");
            Writer out = new OutputStreamWriter(outTransport.getOutgoingStream(), "UTF-8");
            XMLHelper.writeNode(envelopeElem, out);
            out.flush();
        } catch (UnsupportedEncodingException e) {
            log.fatal("JVM does not support required UTF-8 encoding");
            throw new MessageEncodingException("JVM does not support required UTF-8 encoding");
        } catch (IOException e) {
            log.error("Unable to write message content to outbound stream", e);
            throw new MessageEncodingException("Unable to write message content to outbound stream", e);
        }
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
        XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();

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
}