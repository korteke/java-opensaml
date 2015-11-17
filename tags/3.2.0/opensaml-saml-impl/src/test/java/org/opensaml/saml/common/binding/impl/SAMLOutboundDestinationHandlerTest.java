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

package org.opensaml.saml.common.binding.impl;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.binding.impl.SAMLOutboundDestinationHandler;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test the {@link SAMLOutboundDestinationHandler}.
 */
public class SAMLOutboundDestinationHandlerTest extends XMLObjectBaseTestCase {
    
    private SAMLOutboundDestinationHandler handler;
    private MessageContext<SAMLObject> messageContext;
    
    @BeforeMethod
    public void setUp() {
        SAMLObjectBuilder<Endpoint> endpointBuilder = (SAMLObjectBuilder<Endpoint>) builderFactory
                .getBuilder(AssertionConsumerService.DEFAULT_ELEMENT_NAME);
        Endpoint samlEndpoint = endpointBuilder.buildObject();
        samlEndpoint.setLocation("http://example.org");
        
        handler = new SAMLOutboundDestinationHandler();
        messageContext = new MessageContext<>();
        messageContext.getSubcontext(SAMLPeerEntityContext.class, true).
            getSubcontext(SAMLEndpointContext.class, true).setEndpoint(samlEndpoint);
    }
    
    @Test
    public void testSAML1Response() throws MessageHandlerException {
        SAMLObjectBuilder<org.opensaml.saml.saml1.core.Response> requestBuilder = 
                (SAMLObjectBuilder<org.opensaml.saml.saml1.core.Response>) builderFactory
                .getBuilder(org.opensaml.saml.saml1.core.Response.DEFAULT_ELEMENT_NAME);
        org.opensaml.saml.saml1.core.Response samlMessage = requestBuilder.buildObject();
        messageContext.setMessage(samlMessage);
        
        Assert.assertNull(samlMessage.getRecipient(), "Recipient was not null");
        
        handler.invoke(messageContext);
        
        Assert.assertNotNull(samlMessage.getRecipient(), "Recipient was null");
    }
    
    @Test
    public void testSAML2Request() throws MessageHandlerException {
        SAMLObjectBuilder<AuthnRequest> requestBuilder = (SAMLObjectBuilder<AuthnRequest>) builderFactory
                .getBuilder(AuthnRequest.DEFAULT_ELEMENT_NAME);
        AuthnRequest samlMessage = requestBuilder.buildObject();
        messageContext.setMessage(samlMessage);
        
        Assert.assertNull(samlMessage.getDestination(), "Destination was not null");
        
        handler.invoke(messageContext);
        
        Assert.assertNotNull(samlMessage.getDestination(), "Destination was null");
    }

    @Test
    public void testSAML2Response() throws MessageHandlerException {
        SAMLObjectBuilder<org.opensaml.saml.saml2.core.Response> requestBuilder = 
                (SAMLObjectBuilder<org.opensaml.saml.saml2.core.Response>) builderFactory
                .getBuilder(org.opensaml.saml.saml2.core.Response.DEFAULT_ELEMENT_NAME);
        org.opensaml.saml.saml2.core.Response samlMessage = requestBuilder.buildObject();
        messageContext.setMessage(samlMessage);
        
        Assert.assertNull(samlMessage.getDestination(), "Destination was not null");
        
        handler.invoke(messageContext);
        
        Assert.assertNotNull(samlMessage.getDestination(), "Destination was null");
    }
    
}
