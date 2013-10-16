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

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.XMLAssertTestNG;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.custommonkey.xmlunit.Diff;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.Body;
import org.opensaml.soap.soap11.Envelope;
import org.opensaml.soap.soap11.Header;
import org.opensaml.soap.util.SOAPSupport;
import org.opensaml.soap.wsaddressing.Action;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Test basic SOAP 1.1 message encoding.
 */
public class HTTPSOAP11EncoderTest extends XMLObjectBaseTestCase {
    
    /**
     * Test basic encoding of a message in an envelope, using payload-oriented messaging.
     * 
     * @throws ComponentInitializationException 
     * @throws XMLParserException
     * @throws UnmarshallingException
     * @throws MessageEncodingException
     * @throws UnsupportedEncodingException 
     */
    @Test
    public void testBasicEncodingAsPayload() throws ComponentInitializationException, MessageEncodingException, UnsupportedEncodingException, XMLParserException, UnmarshallingException {
        XMLObjectBuilder<XSAny> xsAnyBuilder = getBuilder(XSAny.TYPE_NAME);
        XSAny payload =  xsAnyBuilder.buildObject("http://example.org/soap/ns/message", "GetLastTradePriceResponse", "m");
        
        XSAny price =  xsAnyBuilder.buildObject("http://example.org/soap/ns/message", "Price", "m");
        price.setTextContent("34.5");
        
        payload.getUnknownXMLObjects().add(price);
        
        MessageContext<XMLObject> messageContext = new MessageContext<XMLObject>();
        messageContext.setMessage(payload);
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        HTTPSOAP11Encoder<XMLObject> encoder = new HTTPSOAP11Encoder<XMLObject>();
        encoder.setMessageContext(messageContext);
        encoder.setHttpServletResponse(response);
        
        encoder.initialize();
        encoder.prepareContext();
        encoder.encode();

        Assert.assertEquals(response.getContentType(), "text/xml", "Unexpected content type");
        Assert.assertEquals("UTF-8", response.getCharacterEncoding(), "Unexpected character encoding");
        Assert.assertEquals(response.getHeader("Cache-control"), "no-cache, no-store", "Unexpected cache controls");
        Assert.assertEquals(response.getHeader("SOAPAction"), "");
        
        Envelope encodedEnv = (Envelope) parseUnmarshallResourceByteArray(response.getContentAsByteArray(), false);
        
        String soapMessage = "/data/org/opensaml/soap/soap11/SOAPNoHeaders.xml";
        Envelope controlEnv = (Envelope) parseUnmarshallResource(soapMessage, false);
        
        XMLAssertTestNG.assertXMLIdentical(new Diff(controlEnv.getDOM().getOwnerDocument(), encodedEnv.getDOM().getOwnerDocument()), true);
    }
    
    /**
     * Test basic encoding of a message in an envelope, using SOAP-message oriented messaging.
     * 
     * @throws ComponentInitializationException 
     * @throws XMLParserException
     * @throws UnmarshallingException
     * @throws MessageEncodingException
     * @throws UnsupportedEncodingException 
     */
    @Test
    public void testBasicEncodingAsSOAPEnvelope() throws ComponentInitializationException, MessageEncodingException, UnsupportedEncodingException, XMLParserException, UnmarshallingException {
        Envelope envelope = buildXMLObject(Envelope.DEFAULT_ELEMENT_NAME);
        Body body = buildXMLObject(Body.DEFAULT_ELEMENT_NAME);
        envelope.setBody(body);
        
        XMLObjectBuilder<XSAny> xsAnyBuilder = getBuilder(XSAny.TYPE_NAME);
        XSAny payload =  xsAnyBuilder.buildObject("http://example.org/soap/ns/message", "GetLastTradePriceResponse", "m");
        
        XSAny price =  xsAnyBuilder.buildObject("http://example.org/soap/ns/message", "Price", "m");
        price.setTextContent("34.5");
        
        payload.getUnknownXMLObjects().add(price);
        
        body.getUnknownXMLObjects().add(payload);
        
        MessageContext<XMLObject> messageContext = new MessageContext<XMLObject>();
        messageContext.setMessage(envelope);
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        HTTPSOAP11Encoder<XMLObject> encoder = new HTTPSOAP11Encoder<XMLObject>();
        encoder.setMessageContext(messageContext);
        encoder.setHttpServletResponse(response);
        
        encoder.initialize();
        encoder.prepareContext();
        encoder.encode();

        Assert.assertEquals(response.getContentType(), "text/xml", "Unexpected content type");
        Assert.assertEquals("UTF-8", response.getCharacterEncoding(), "Unexpected character encoding");
        Assert.assertEquals(response.getHeader("Cache-control"), "no-cache, no-store", "Unexpected cache controls");
        Assert.assertEquals(response.getHeader("SOAPAction"), "");
        
        Envelope encodedEnv = (Envelope) parseUnmarshallResourceByteArray(response.getContentAsByteArray(), false);
        
        String soapMessage = "/data/org/opensaml/soap/soap11/SOAPNoHeaders.xml";
        Envelope controlEnv = (Envelope) parseUnmarshallResource(soapMessage, false);
        
        XMLAssertTestNG.assertXMLIdentical(new Diff(controlEnv.getDOM().getOwnerDocument(), encodedEnv.getDOM().getOwnerDocument()), true);
    }
    
    /**
     * Test basic encoding of a message in an envelope, using payload-oriented messaging. 
     * Supply an Envelope and header via SOAP subcontext.
     * 
     * @throws ComponentInitializationException 
     * @throws XMLParserException
     * @throws UnmarshallingException
     * @throws MessageEncodingException
     * @throws UnsupportedEncodingException 
     */
    @Test
    public void testEncodingAsPayloadWithHeader() throws ComponentInitializationException, MessageEncodingException, UnsupportedEncodingException, XMLParserException, UnmarshallingException {
        Envelope envelope = buildXMLObject(Envelope.DEFAULT_ELEMENT_NAME);
        Header header = buildXMLObject(Header.DEFAULT_ELEMENT_NAME);
        envelope.setHeader(header);
        
        XMLObjectBuilder<XSAny> xsAnyBuilder = getBuilder(XSAny.TYPE_NAME);
        
        XSAny transactionHeader = xsAnyBuilder.buildObject("http://example.org/soap/ns/transaction", "Transaction", "t");
        SOAPSupport.addSOAP11MustUnderstandAttribute(transactionHeader, true);
        transactionHeader.setTextContent("5");
        header.getUnknownXMLObjects().add(transactionHeader);
        
        XSAny payload =  xsAnyBuilder.buildObject("http://example.org/soap/ns/message", "GetLastTradePriceResponse", "m");
        
        XSAny price =  xsAnyBuilder.buildObject("http://example.org/soap/ns/message", "Price", "m");
        price.setTextContent("34.5");
        
        payload.getUnknownXMLObjects().add(price);
        
        MessageContext<XMLObject> messageContext = new MessageContext<XMLObject>();
        messageContext.setMessage(payload);
        messageContext.getSubcontext(SOAP11Context.class, true).setEnvelope(envelope);
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        HTTPSOAP11Encoder<XMLObject> encoder = new HTTPSOAP11Encoder<XMLObject>();
        encoder.setMessageContext(messageContext);
        encoder.setHttpServletResponse(response);
        
        encoder.initialize();
        encoder.prepareContext();
        encoder.encode();

        Assert.assertEquals(response.getContentType(), "text/xml", "Unexpected content type");
        Assert.assertEquals("UTF-8", response.getCharacterEncoding(), "Unexpected character encoding");
        Assert.assertEquals(response.getHeader("Cache-control"), "no-cache, no-store", "Unexpected cache controls");
        Assert.assertEquals(response.getHeader("SOAPAction"), "");
        
        Envelope encodedEnv = (Envelope) parseUnmarshallResourceByteArray(response.getContentAsByteArray(), false);
        
        String soapMessage = "/data/org/opensaml/soap/soap11/SOAPHeaderMustUnderstand.xml";
        Envelope controlEnv = (Envelope) parseUnmarshallResource(soapMessage, false);
        
        XMLAssertTestNG.assertXMLIdentical(new Diff(controlEnv.getDOM().getOwnerDocument(), encodedEnv.getDOM().getOwnerDocument()), true);
    }
    
    /**
     * Test basic encoding of a message in an envelope, using payload-oriented messaging.
     * 
     * @throws ComponentInitializationException 
     * @throws XMLParserException
     * @throws UnmarshallingException
     * @throws MessageEncodingException
     * @throws UnsupportedEncodingException 
     */
    @Test
    public void testEncodingWithAction() throws ComponentInitializationException, MessageEncodingException, UnsupportedEncodingException, XMLParserException, UnmarshallingException {
        Envelope envelope = buildXMLObject(Envelope.DEFAULT_ELEMENT_NAME);
        
        Body body = buildXMLObject(Body.DEFAULT_ELEMENT_NAME);
        envelope.setBody(body);
        
        Header header = buildXMLObject(Header.DEFAULT_ELEMENT_NAME);
        envelope.setHeader(header);
        
        Action action = buildXMLObject(Action.ELEMENT_NAME);
        action.setValue("urn:test:soap:action");
        header.getUnknownXMLObjects().add(action);
        
        XMLObjectBuilder<XSAny> xsAnyBuilder = getBuilder(XSAny.TYPE_NAME);
        XSAny payload =  xsAnyBuilder.buildObject("http://example.org/soap/ns/message", "GetLastTradePriceResponse", "m");
        
        XSAny price =  xsAnyBuilder.buildObject("http://example.org/soap/ns/message", "Price", "m");
        price.setTextContent("34.5");
        
        payload.getUnknownXMLObjects().add(price);
        
        body.getUnknownXMLObjects().add(payload);
        
        MessageContext<XMLObject> messageContext = new MessageContext<XMLObject>();
        messageContext.setMessage(envelope);
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        HTTPSOAP11Encoder<XMLObject> encoder = new HTTPSOAP11Encoder<XMLObject>();
        encoder.setMessageContext(messageContext);
        encoder.setHttpServletResponse(response);
        
        encoder.initialize();
        encoder.prepareContext();
        encoder.encode();

        Assert.assertEquals(response.getContentType(), "text/xml", "Unexpected content type");
        Assert.assertEquals("UTF-8", response.getCharacterEncoding(), "Unexpected character encoding");
        Assert.assertEquals(response.getHeader("Cache-control"), "no-cache, no-store", "Unexpected cache controls");
        
        Assert.assertEquals(response.getHeader("SOAPAction"), "urn:test:soap:action");
    }
    
    //
    // Helper stuff
    //

    protected XMLObject parseUnmarshallResource(String resource, boolean dropDOM) throws XMLParserException, UnmarshallingException {
        Document soapDoc = parserPool.parse(this.getClass().getResourceAsStream(resource));
        return unmarshallXMLObject(soapDoc, dropDOM);
    }
    
    protected XMLObject parseUnmarshallResourceByteArray(byte [] input, boolean dropDOM) throws XMLParserException, UnmarshallingException {
        ByteArrayInputStream bais = new ByteArrayInputStream(input);
        Document soapDoc = parserPool.parse(bais);
        return unmarshallXMLObject(soapDoc, dropDOM);
    }

    protected XMLObject unmarshallXMLObject(Document soapDoc, boolean dropDOM) throws UnmarshallingException {
        Element envelopeElem = soapDoc.getDocumentElement();
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(envelopeElem);
        
        Envelope envelope = (Envelope) unmarshaller.unmarshall(envelopeElem);
        if (dropDOM) {
            envelope.releaseDOM();
            envelope.releaseChildrenDOM(true);
        }
        return envelope;
    }

}
