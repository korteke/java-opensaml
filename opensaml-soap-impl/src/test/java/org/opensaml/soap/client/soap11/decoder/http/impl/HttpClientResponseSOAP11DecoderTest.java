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

package org.opensaml.soap.client.soap11.decoder.http.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHttpResponse;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.soap.common.SOAP11FaultDecodingException;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.Body;
import org.opensaml.soap.soap11.Envelope;
import org.opensaml.soap.soap11.Fault;
import org.opensaml.soap.util.SOAPSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

public class HttpClientResponseSOAP11DecoderTest extends XMLObjectBaseTestCase {
    
    private HttpClientResponseSOAP11Decoder<XMLObject> decoder;
    
    @BeforeMethod
    public void setUp() {
        decoder = new HttpClientResponseSOAP11Decoder<>();
        decoder.setParserPool(parserPool);
    }
    
    @Test
    public void testDecodeToPayload() throws ComponentInitializationException, MessageDecodingException, MarshallingException, IOException {
        Envelope envelope = buildMessageSkeleton();
        envelope.getBody().getUnknownXMLObjects().add(buildXMLObject(simpleXMLObjectQName));
        HttpResponse httpResponse = buildResponse(HttpStatus.SC_OK, envelope);
        
        decoder.setBodyHandler(new TestPayloadBodyHandler());
        decoder.setHttpResponse(httpResponse);
        decoder.initialize();
        
        decoder.decode();
        
        MessageContext<XMLObject> messageContext = decoder.getMessageContext();
        
        Assert.assertNotNull(messageContext);
        Assert.assertNotNull(messageContext.getMessage());
        Assert.assertTrue(messageContext.getMessage() instanceof SimpleXMLObject);
        
        SOAP11Context soapContext = messageContext.getSubcontext(SOAP11Context.class, false);
        Assert.assertNotNull(soapContext);
        Assert.assertNotNull(soapContext.getEnvelope());
        Assert.assertEquals(soapContext.getHTTPResponseStatus(), new Integer(HttpStatus.SC_OK));
    }
    
    @Test
    public void testDecodeToEnvelope() throws ComponentInitializationException, MessageDecodingException, MarshallingException, IOException {
        Envelope envelope = buildMessageSkeleton();
        envelope.getBody().getUnknownXMLObjects().add(buildXMLObject(simpleXMLObjectQName));
        HttpResponse httpResponse = buildResponse(HttpStatus.SC_OK, envelope);
        
        decoder.setBodyHandler(new TestEnvelopeBodyHandler());
        decoder.setHttpResponse(httpResponse);
        decoder.initialize();
        
        decoder.decode();
        
        MessageContext<XMLObject> messageContext = decoder.getMessageContext();
        
        Assert.assertNotNull(messageContext);
        Assert.assertNotNull(messageContext.getMessage());
        Assert.assertTrue(messageContext.getMessage() instanceof Envelope);
        
        SOAP11Context soapContext = messageContext.getSubcontext(SOAP11Context.class, false);
        Assert.assertNotNull(soapContext);
        Assert.assertNotNull(soapContext.getEnvelope());
        Assert.assertEquals(soapContext.getHTTPResponseStatus(), new Integer(HttpStatus.SC_OK));
    }
    
    @Test(expectedExceptions=SOAP11FaultDecodingException.class)
    public void testFault() throws ComponentInitializationException, MessageDecodingException, MarshallingException, IOException {
        Fault fault = SOAPSupport.buildSOAP11Fault(new QName("urn:test:soap:fault:foo", "TestFault", "foo"), "Test fault", null, null, null);
        
        Envelope envelope = buildMessageSkeleton();
        envelope.getBody().getUnknownXMLObjects().add(fault);
        HttpResponse httpResponse = buildResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, envelope);
        
        decoder.setBodyHandler(new TestPayloadBodyHandler());
        decoder.setHttpResponse(httpResponse);
        decoder.initialize();
        
        decoder.decode();
    }
    
    private HttpResponse buildResponse(int statusResponseCode, Envelope envelope) throws MarshallingException, IOException {
        BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, statusResponseCode, null);
        Element envelopeElement = XMLObjectSupport.marshall(envelope);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializeSupport.writeNode(envelopeElement, baos);
        baos.flush();
        
        ByteArrayEntity entity = new ByteArrayEntity(baos.toByteArray());
        response.setEntity(entity);
        return response;
    }

    private Envelope buildMessageSkeleton() {
        Envelope envelope = buildXMLObject(Envelope.DEFAULT_ELEMENT_NAME);
        Body body = buildXMLObject(Body.DEFAULT_ELEMENT_NAME);
        envelope.setBody(body);
        return envelope;
    }
    
    public class TestEnvelopeBodyHandler extends AbstractMessageHandler<XMLObject> {
        /** {@inheritDoc} */
        protected void doInvoke(MessageContext msgContext) throws MessageHandlerException {
            Envelope env = (Envelope) msgContext.getSubcontext(SOAP11Context.class).getEnvelope();
            msgContext.setMessage(env);
        }
    }
    
    public class TestPayloadBodyHandler extends AbstractMessageHandler<XMLObject> {
        /** {@inheritDoc} */
        protected void doInvoke(MessageContext msgContext) throws MessageHandlerException {
            Envelope env = (Envelope) msgContext.getSubcontext(SOAP11Context.class).getEnvelope();
            msgContext.setMessage(env.getBody().getUnknownXMLObjects().get(0));
        }
    }

}
