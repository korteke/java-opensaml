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

package org.opensaml.soap.soap11.decoder.http.impl;

import java.io.IOException;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.security.SecurityException;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.Envelope;
import org.opensaml.soap.soap11.decoder.http.impl.HTTPSOAP11Decoder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.io.Resources;

/**
 * Test basic SOAP 1.1 message decoding.
 */
public class HTTPSOAP11DecoderTest extends XMLObjectBaseTestCase {
    
    private HTTPSOAP11Decoder decoder;
    
    private MockHttpServletRequest httpRequest;
    
    @BeforeMethod
    protected void setUp() throws Exception {
        httpRequest = new MockHttpServletRequest();
        httpRequest.setMethod("POST");
        
        decoder = new HTTPSOAP11Decoder();
        decoder.setParserPool(parserPool);
        decoder.setHttpServletRequest(httpRequest);
        // Let actual test method do the initialize(), so can set own body handler.
    }
    
    /**
     * Test basic no header case. Message will be an Envelope.
     * 
     * @throws ComponentInitializationException 
     * @throws MessageDecodingException
     * @throws IOException 
     * @throws SecurityException
     */
    @Test
    public void testDecodeToEnvelope() throws ComponentInitializationException, MessageDecodingException, IOException {
        httpRequest.setContent(getServletRequestContent("/data/org/opensaml/soap/soap11/SOAPNoHeaders.xml"));
        
        decoder.setBodyHandler(new TestEnvelopeBodyHandler());
        decoder.initialize();
        
        decoder.decode();
        MessageContext<XMLObject> msgContext = decoder.getMessageContext();
        
        XMLObject msg = msgContext.getMessage();
        Assert.assertNotNull(msg);
        
        Assert.assertTrue(msg instanceof Envelope);
    }
    
    /**
     * Test basic no header case. Message will be an non-Envelope payload.
     * 
     * @throws ComponentInitializationException 
     * @throws MessageDecodingException
     * @throws IOException 
     * @throws SecurityException
     */
    @Test
    public void testDecodeToPayload() throws ComponentInitializationException, MessageDecodingException, IOException {
        httpRequest.setContent(getServletRequestContent("/data/org/opensaml/soap/soap11/SOAPNoHeaders.xml"));
        
        decoder.setBodyHandler(new TestPayloadBodyHandler());
        decoder.initialize();
        
        decoder.decode();
        MessageContext<XMLObject> msgContext = decoder.getMessageContext();
        
        XMLObject msg = msgContext.getMessage();
        Assert.assertNotNull(msg);
        
        Assert.assertTrue(msg instanceof XSAny);
    }
    
    //
    // Helper stuff
    //
    
    /**
     * Get a resource relative to a class.
     * 
     * @param resourceName
     * @return  resource content
     * @throws IOException 
     */
    private byte[] getServletRequestContent(String resourceName) throws IOException {
        return Resources.toByteArray(getClass().getResource(resourceName));
    }

    public class TestEnvelopeBodyHandler extends AbstractMessageHandler<Envelope> {
        /** {@inheritDoc} */
        protected void doInvoke(MessageContext msgContext) throws MessageHandlerException {
            Envelope env = (Envelope) msgContext.getSubcontext(SOAP11Context.class).getEnvelope();
            msgContext.setMessage(env);
        }
    }
    
    public class TestPayloadBodyHandler extends AbstractMessageHandler<Envelope> {
        /** {@inheritDoc} */
        protected void doInvoke(MessageContext msgContext) throws MessageHandlerException {
            Envelope env = (Envelope) msgContext.getSubcontext(SOAP11Context.class).getEnvelope();
            msgContext.setMessage(env.getBody().getUnknownXMLObjects().get(0));
        }
    }
    
}
