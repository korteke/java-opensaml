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
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.saml2.ecp.RelayState;
import org.opensaml.soap.messaging.SOAPMessagingSupport;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.Envelope;
import org.opensaml.soap.util.SOAPSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/** {@link AddRelayStateHeaderHandler} unit test. */
public class AddRelayStateHeaderHandlerTest extends OpenSAMLInitBaseTestCase {
    
    private MessageContext messageCtx;
    
    private AddRelayStateHeaderHandler handler;
    
    @BeforeMethod public void setUp() throws ComponentInitializationException {
        messageCtx = new MessageContext();
        handler = new AddRelayStateHeaderHandler();
        handler.initialize();
    }
    
    /** Test that the handler does nothing on a missing Endpoint context. */
    @Test public void testMissingEndpointContext() throws MessageHandlerException {

        handler.invoke(messageCtx);
        
        final List<XMLObject> headers =
                SOAPMessagingSupport.getInboundHeaderBlock(messageCtx, RelayState.DEFAULT_ELEMENT_NAME, null, true);
        
        Assert.assertTrue(headers.isEmpty());
    }
    
    /** Test that the handler errors on a missing SOAP context. */
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testMissingEnvelope() throws MessageHandlerException {

        messageCtx.getSubcontext(SAMLBindingContext.class, true).setRelayState("foo");
        
        handler.invoke(messageCtx);
    }

    /** Test that the handler works. */
    @Test public void testSuccess() throws MessageHandlerException {

        final Envelope env = XMLObjectProviderRegistrySupport.getBuilderFactory().<Envelope>getBuilderOrThrow(
                Envelope.DEFAULT_ELEMENT_NAME).buildObject(Envelope.DEFAULT_ELEMENT_NAME);
        messageCtx.getSubcontext(SOAP11Context.class, true).setEnvelope(env);
        
        messageCtx.getSubcontext(SAMLBindingContext.class, true).setRelayState("foo");
        
        handler.invoke(messageCtx);
        
        final List<XMLObject> headers =
                SOAPMessagingSupport.getInboundHeaderBlock(messageCtx, RelayState.DEFAULT_ELEMENT_NAME, null, true);
        
        Assert.assertEquals(headers.size(), 1);
        Assert.assertEquals(((RelayState) headers.get(0)).getValue(), "foo");
    }
    
}