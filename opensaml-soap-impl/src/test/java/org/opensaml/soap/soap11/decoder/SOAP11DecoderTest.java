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

package org.opensaml.soap.soap11.decoder;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import java.util.Collections;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.security.SecurityException;
import org.opensaml.soap.common.SOAPHandler;
import org.opensaml.soap.soap11.Envelope;
import org.opensaml.ws.message.BaseMessageContext;
import org.opensaml.ws.message.MessageContext;
import org.opensaml.ws.message.decoder.MessageDecodingException;
import org.opensaml.ws.message.handler.BasicHandlerChain;
import org.opensaml.ws.message.handler.HandlerChain;
import org.opensaml.ws.message.handler.HandlerException;
import org.opensaml.ws.message.handler.StaticHandlerChainResolver;
import org.opensaml.ws.transport.InputStreamInTransportAdapter;

/**
 * Test basic SOAP 1.1 message decoding.
 */
public class SOAP11DecoderTest extends XMLObjectBaseTestCase {
    
    private TestContext messageContext;
    
    private SOAP11Decoder decoder;
    
    private HandlerChain handlerChain;

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        messageContext = new TestContext();
        decoder = new SOAP11Decoder();
        
        handlerChain = new BasicHandlerChain();
        StaticHandlerChainResolver handlerChainResolver = new StaticHandlerChainResolver(handlerChain);
        messageContext.setPreSecurityInboundHandlerChainResolver(handlerChainResolver);
    }
    
    /**
     * Test basic no header case.
     * @throws MessageDecodingException
     * @throws SecurityException
     */
    @Test
    public void testBasicNoHeaders() throws MessageDecodingException, SecurityException {
        String soapMessage = "/data/org/opensaml/soap/soap11/SOAPNoHeaders.xml";
        InputStreamInTransportAdapter inTransport = getInTransportResource(soapMessage);
        
        messageContext.setInboundMessageTransport(inTransport);
        decoder.decode(messageContext);
        
        XMLObject msg = messageContext.getInboundMessage();
        Assert.assertNotNull(msg);
        
        Assert.assertTrue(msg instanceof Envelope);
    }
    
    /**
     * Test with a header soap11:mustUnderstand false.
     * @throws SecurityException
     */
// TODO disable until mustUnderstand support is complete
//    public void testNotMustUnderstandHeader() throws SecurityException {
//        String soapMessage = "/data/org/opensaml/soap/soap11/SOAPHeaderNotMustUnderstand.xml";
//        InputStreamInTransportAdapter inTransport = getInTransportResource(soapMessage);
//        
//        messageContext.setInboundMessageTransport(inTransport);
//        try {
//            decoder.decode(messageContext);
//        } catch (MessageDecodingException e) {
//            fail("A not mustUnderstand header caused decoding failure");
//        }
//        
//    }
    
    /**
     * Test with a header soap11:mustUnderstand true, no handler that understands.
     * @throws SecurityException
     */
 // TODO disable until mustUnderstand support is complete
//    public void testMustUnderstandHeaderFail() throws SecurityException {
//        String soapMessage = "/data/org/opensaml/soap/soap11/SOAPHeaderMustUnderstand.xml";
//        InputStreamInTransportAdapter inTransport = getInTransportResource(soapMessage);
//        
//        messageContext.setInboundMessageTransport(inTransport);
//        try {
//            decoder.decode(messageContext);
//            fail("A mustUnderstand header was not understood, but decoder incorrectly succeeded");
//        } catch (MessageDecodingException e) {
//            // expected
//        }
//        
//    }
    
    /**
     * Test with a header soap11:mustUnderstand true, with handler that understands.
     * @throws SecurityException
     */
 // TODO disable until mustUnderstand support is complete
//    public void testMustUnderstandHeaderWithHandler() throws SecurityException {
//        String soapMessage = "/data/org/opensaml/soap/soap11/SOAPHeaderMustUnderstand.xml";
//        InputStreamInTransportAdapter inTransport = getInTransportResource(soapMessage);
//        
//        handlerChain.getHandlers().add(new TestHeaderHandler());
//        
//        messageContext.setInboundMessageTransport(inTransport);
//        try {
//            decoder.decode(messageContext);
//        } catch (MessageDecodingException e) {
//            fail("A mustUnderstand header should have been understood by handler, but decoding failed");
//        }
//        
//    }
    
    /**
     * Test 2-Handler chain, populating header and body info in context.
     * @throws SecurityException
     * @throws MessageDecodingException 
     */
    @Test
    public void testHandlerChainInvocation() throws SecurityException, MessageDecodingException {
        String soapMessage = "/data/org/opensaml/soap/soap11/SOAPHeaderNotMustUnderstand.xml";
        InputStreamInTransportAdapter inTransport = getInTransportResource(soapMessage);
        
        handlerChain.getHandlers().add(new TestHeaderHandler());
        handlerChain.getHandlers().add(new TestBodyHandler());
        
        messageContext.setInboundMessageTransport(inTransport);
        decoder.decode(messageContext);
        
        Assert.assertEquals(messageContext.transaction, "5", "Invalid test header value");
        Assert.assertNotNull(messageContext.bodyMessage, "Context body message was null");
        
    }
    
    //
    // Helper stuff
    //
    
    protected InputStreamInTransportAdapter getInTransportResource(String resouceName) {
        return new InputStreamInTransportAdapter(this.getClass().getResourceAsStream(resouceName));
    }
    
    public class TestContext extends BaseMessageContext {
        public  XMLObject bodyMessage;
        public String transaction;
    }
    
    public class TestHeaderHandler implements SOAPHandler {
        
        private QName tHeaderName = new QName("http://example.org/soap/ns/transaction", "Transaction", "t");
        
        /** {@inheritDoc} */
        public Set<QName> understandsHeaders() {
            return Collections.singleton(tHeaderName);
        }
        /** {@inheritDoc} */
        public void invoke(MessageContext msgContext) throws HandlerException {
            TestContext context = (TestContext) msgContext;
            Envelope env = (Envelope) context.getInboundMessage();
            String headerValue =
                ((XSAny)env.getHeader().getUnknownXMLObjects(tHeaderName).get(0)).getTextContent();
            context.transaction = headerValue;
        }
    }
    
    public class TestBodyHandler implements SOAPHandler {

        /** {@inheritDoc} */
        public Set<QName> understandsHeaders() {
            return Collections.emptySet();
        }

        /** {@inheritDoc} */
        public void invoke(MessageContext msgContext) throws HandlerException {
            TestContext context = (TestContext) msgContext;
            Envelope env = (Envelope) context.getInboundMessage();
            context.bodyMessage = env.getBody().getUnknownXMLObjects().get(0);
        }
    }
    
}
