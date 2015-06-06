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

package org.opensaml.saml.saml2.binding.impl;

import java.util.List;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.saml2.core.StatusResponseType;
import org.opensaml.saml.saml2.ecp.Response;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.profile.SAML2ActionTestingSupport;
import org.opensaml.soap.messaging.SOAPMessagingSupport;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.Envelope;
import org.testng.Assert;
import org.testng.annotations.Test;

/** {@link AddECPResponseHeaderHandler} unit test. */
public class AddECPResponseHeaderHandlerTest extends OpenSAMLInitBaseTestCase {
    
    /** Test that the handler does nothing on a missing Endpoint context. */
    @Test public void testMissingEndpointContext() throws MessageHandlerException, ComponentInitializationException {
        final MessageContext<StatusResponseType> messageCtx = new MessageContext<>();
        messageCtx.setMessage(SAML2ActionTestingSupport.buildResponse());

        final AddECPResponseHeaderHandler handler = new AddECPResponseHeaderHandler();
        handler.initialize();
        
        handler.invoke(messageCtx);
        
        final List<XMLObject> headers =
                SOAPMessagingSupport.getInboundHeaderBlock(messageCtx, Response.DEFAULT_ELEMENT_NAME, null, true);
        
        Assert.assertTrue(headers.isEmpty());
    }
    
    /** Test that the handler errors on a missing SOAP context. */
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testMissingEnvelope() throws MessageHandlerException, ComponentInitializationException {
        final MessageContext<StatusResponseType> messageCtx = new MessageContext<>();
        messageCtx.setMessage(SAML2ActionTestingSupport.buildResponse());
        
        final Endpoint ep = XMLObjectProviderRegistrySupport.getBuilderFactory().<AssertionConsumerService>getBuilderOrThrow(
                AssertionConsumerService.DEFAULT_ELEMENT_NAME).buildObject(AssertionConsumerService.DEFAULT_ELEMENT_NAME);
        ep.setLocation("foo");
        messageCtx.getSubcontext(
                SAMLPeerEntityContext.class, true).getSubcontext(
                        SAMLEndpointContext.class, true).setEndpoint(ep);

        final AddECPResponseHeaderHandler handler = new AddECPResponseHeaderHandler();
        handler.initialize();
        
        handler.invoke(messageCtx);
    }

    /** Test that the handler works. */
    @Test public void testSuccess() throws MessageHandlerException, ComponentInitializationException {
        final Envelope env = XMLObjectProviderRegistrySupport.getBuilderFactory().<Envelope>getBuilderOrThrow(
                Envelope.DEFAULT_ELEMENT_NAME).buildObject(Envelope.DEFAULT_ELEMENT_NAME);

        final MessageContext<StatusResponseType> messageCtx = new MessageContext<>();
        messageCtx.setMessage(SAML2ActionTestingSupport.buildResponse());
        messageCtx.getSubcontext(SOAP11Context.class, true).setEnvelope(env);
        
        final Endpoint ep = XMLObjectProviderRegistrySupport.getBuilderFactory().<AssertionConsumerService>getBuilderOrThrow(
                AssertionConsumerService.DEFAULT_ELEMENT_NAME).buildObject(AssertionConsumerService.DEFAULT_ELEMENT_NAME);
        ep.setLocation("foo");
        messageCtx.getSubcontext(
                SAMLPeerEntityContext.class, true).getSubcontext(
                        SAMLEndpointContext.class, true).setEndpoint(ep);
        
        final AddECPResponseHeaderHandler handler = new AddECPResponseHeaderHandler();
        handler.initialize();
        
        handler.invoke(messageCtx);
        
        final List<XMLObject> headers =
                SOAPMessagingSupport.getInboundHeaderBlock(messageCtx, Response.DEFAULT_ELEMENT_NAME, null, true);
        
        Assert.assertEquals(headers.size(), 1);
        Assert.assertEquals(((Response) headers.get(0)).getAssertionConsumerServiceURL(), "foo");
    }
    
}