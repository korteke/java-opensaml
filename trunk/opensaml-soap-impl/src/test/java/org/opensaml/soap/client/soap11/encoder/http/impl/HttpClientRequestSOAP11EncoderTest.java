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

package org.opensaml.soap.client.soap11.encoder.http.impl;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.apache.http.client.methods.HttpPost;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.Envelope;
import org.opensaml.soap.soap11.Header;
import org.opensaml.soap.wsaddressing.Action;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class HttpClientRequestSOAP11EncoderTest extends XMLObjectBaseTestCase {
    
    private HttpClientRequestSOAP11Encoder<XMLObject> encoder;
    
    private MessageContext<XMLObject> messageContext;
    
    private HttpPost request;
    
    @BeforeMethod
    public void setUp() {
        request = new HttpPost("http://example.org/soap/receiver");
        
        messageContext = new MessageContext<>();
        
        encoder = new HttpClientRequestSOAP11Encoder();
        encoder.setMessageContext(messageContext);
        encoder.setHttpRequest(request);
    }
    
    @Test
    public void testBasic() throws ComponentInitializationException, MessageEncodingException {
        SimpleXMLObject sxo = buildXMLObject(SimpleXMLObject.ELEMENT_NAME);
        messageContext.setMessage(sxo);
        
        encoder.initialize();
        encoder.prepareContext();
        
        SOAP11Context soapContext = messageContext.getSubcontext(SOAP11Context.class);
        Assert.assertNotNull(soapContext);
        Envelope envelope = soapContext.getEnvelope();
        Assert.assertNotNull(envelope);
        
        encoder.encode();
        
        Assert.assertNotNull(request.getEntity());
        
        Assert.assertTrue(request.getEntity().getContentType().getValue().startsWith("text/xml;"), "Unexpected content type");
        Assert.assertEquals(request.getEntity().getContentType().getElements()[0].getParameterByName("charset").getValue(), "UTF-8", "Unexpected character encoding");
        Assert.assertEquals(request.getFirstHeader("SOAPAction").getValue(), "");
    }

    @Test
    public void testAction() throws ComponentInitializationException, MessageEncodingException {
        SimpleXMLObject sxo = buildXMLObject(SimpleXMLObject.ELEMENT_NAME);
        messageContext.setMessage(sxo);
        
        encoder.initialize();
        encoder.prepareContext();
        
        SOAP11Context soapContext = messageContext.getSubcontext(SOAP11Context.class);
        Assert.assertNotNull(soapContext);
        Envelope envelope = soapContext.getEnvelope();
        Assert.assertNotNull(envelope);
        
        // "For real" this would be added by a MessageHandler.  Here just add manually.
        Action action = buildXMLObject(Action.ELEMENT_NAME);
        action.setValue("urn:test:action:foo");
        if (envelope.getHeader() == null) {
            envelope.setHeader((Header) buildXMLObject(Header.DEFAULT_ELEMENT_NAME));
        }
        envelope.getHeader().getUnknownXMLObjects().add(action);
        
        encoder.encode();
        
        Assert.assertNotNull(request.getEntity());
        
        Assert.assertTrue(request.getEntity().getContentType().getValue().startsWith("text/xml;"), "Unexpected content type");
        Assert.assertEquals(request.getEntity().getContentType().getElements()[0].getParameterByName("charset").getValue(), "UTF-8", "Unexpected character encoding");
        Assert.assertEquals(request.getFirstHeader("SOAPAction").getValue(), "urn:test:action:foo");
    }

}
