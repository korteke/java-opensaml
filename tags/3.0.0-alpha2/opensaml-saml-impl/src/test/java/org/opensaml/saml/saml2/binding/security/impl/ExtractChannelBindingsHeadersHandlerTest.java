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

package org.opensaml.saml.saml2.binding.security.impl;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.messaging.context.ChannelBindingsContext;
import org.opensaml.saml.ext.saml2cb.ChannelBindings;
import org.opensaml.saml.saml2.binding.security.impl.ExtractChannelBindingsHeadersHandler;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.profile.SAML2ActionTestingSupport;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.ActorBearing;
import org.opensaml.soap.soap11.Envelope;
import org.opensaml.soap.util.SOAPSupport;
import org.testng.Assert;
import org.testng.annotations.Test;

/** {@link ExtractChannelBindingsHeadersHandler} unit test. */
public class ExtractChannelBindingsHeadersHandlerTest extends OpenSAMLInitBaseTestCase {
    
    /** Test that the handler returns nothing on a missing SOAP context. */
    @Test public void testMissingEnvelope() throws MessageHandlerException, ComponentInitializationException {
        final MessageContext<AuthnRequest> messageCtx = new MessageContext<>();
        messageCtx.setMessage(SAML2ActionTestingSupport.buildAuthnRequest());
        
        final ExtractChannelBindingsHeadersHandler handler = new ExtractChannelBindingsHeadersHandler();
        handler.initialize();
        
        handler.invoke(messageCtx);
        Assert.assertNull(messageCtx.getSubcontext(SOAP11Context.class));
    }

    /** Test that the handler does nothing when no headers exist. */
    @Test public void testNoHeaders() throws MessageHandlerException, ComponentInitializationException {
        final Envelope env = XMLObjectProviderRegistrySupport.getBuilderFactory().<Envelope>getBuilderOrThrow(
                Envelope.DEFAULT_ELEMENT_NAME).buildObject(Envelope.DEFAULT_ELEMENT_NAME);

        final MessageContext<AuthnRequest> messageCtx = new MessageContext<>();
        messageCtx.setMessage(SAML2ActionTestingSupport.buildAuthnRequest());
        messageCtx.getSubcontext(SOAP11Context.class, true).setEnvelope(env);
        
        final ExtractChannelBindingsHeadersHandler handler = new ExtractChannelBindingsHeadersHandler();
        handler.initialize();
        
        handler.invoke(messageCtx);
        final ChannelBindingsContext cbCtx = messageCtx.getSubcontext(SOAP11Context.class).getSubcontext(
                ChannelBindingsContext.class);
        Assert.assertNull(cbCtx);
    }
    
    /** Test that the handler works. */
    @Test public void testSuccess() throws MessageHandlerException, ComponentInitializationException {
        final Envelope env = XMLObjectProviderRegistrySupport.getBuilderFactory().<Envelope>getBuilderOrThrow(
                Envelope.DEFAULT_ELEMENT_NAME).buildObject(Envelope.DEFAULT_ELEMENT_NAME);

        final MessageContext<AuthnRequest> messageCtx = new MessageContext<>();
        messageCtx.setMessage(SAML2ActionTestingSupport.buildAuthnRequest());
        messageCtx.getSubcontext(SOAP11Context.class, true).setEnvelope(env);
        
        final ChannelBindings cb = XMLObjectProviderRegistrySupport.getBuilderFactory().<ChannelBindings>getBuilderOrThrow(
                ChannelBindings.DEFAULT_ELEMENT_NAME).buildObject(ChannelBindings.DEFAULT_ELEMENT_NAME);
        cb.setValue("foo");
        SOAPSupport.addSOAP11ActorAttribute(cb, ActorBearing.SOAP11_ACTOR_NEXT);
        SOAPSupport.addHeaderBlock(messageCtx, cb);

        final ChannelBindings cb2 = XMLObjectProviderRegistrySupport.getBuilderFactory().<ChannelBindings>getBuilderOrThrow(
                ChannelBindings.DEFAULT_ELEMENT_NAME).buildObject(ChannelBindings.DEFAULT_ELEMENT_NAME);
        cb2.setValue("bar");
        SOAPSupport.addSOAP11ActorAttribute(cb2, ActorBearing.SOAP11_ACTOR_NEXT);
        SOAPSupport.addHeaderBlock(messageCtx, cb2);

        final ExtractChannelBindingsHeadersHandler handler = new ExtractChannelBindingsHeadersHandler();
        handler.initialize();
        
        handler.invoke(messageCtx);
        final ChannelBindingsContext cbCtx = messageCtx.getSubcontext(SOAP11Context.class).getSubcontext(
                ChannelBindingsContext.class);
        Assert.assertNotNull(cbCtx);
        Assert.assertEquals(cbCtx.getChannelBindings().size(), 2);
        
        final ChannelBindings[] array = cbCtx.getChannelBindings().toArray(new ChannelBindings[2]);
        Assert.assertTrue("foo".equals(array[0].getValue()) || "bar".equals(array[0].getValue()));
        Assert.assertTrue("foo".equals(array[1].getValue()) || "bar".equals(array[1].getValue()));
    }

    /** Test that the handler works with non-default actor flags. */
    @Test public void testActor() throws MessageHandlerException, ComponentInitializationException {
        final Envelope env = XMLObjectProviderRegistrySupport.getBuilderFactory().<Envelope>getBuilderOrThrow(
                Envelope.DEFAULT_ELEMENT_NAME).buildObject(Envelope.DEFAULT_ELEMENT_NAME);

        final MessageContext<AuthnRequest> messageCtx = new MessageContext<>();
        messageCtx.setMessage(SAML2ActionTestingSupport.buildAuthnRequest());
        messageCtx.getSubcontext(SOAP11Context.class, true).setEnvelope(env);
        
        final ChannelBindings cb = XMLObjectProviderRegistrySupport.getBuilderFactory().<ChannelBindings>getBuilderOrThrow(
                ChannelBindings.DEFAULT_ELEMENT_NAME).buildObject(ChannelBindings.DEFAULT_ELEMENT_NAME);
        cb.setValue("foo");
        SOAPSupport.addSOAP11ActorAttribute(cb, ActorBearing.SOAP11_ACTOR_NEXT);
        SOAPSupport.addHeaderBlock(messageCtx, cb);

        final ChannelBindings cb2 = XMLObjectProviderRegistrySupport.getBuilderFactory().<ChannelBindings>getBuilderOrThrow(
                ChannelBindings.DEFAULT_ELEMENT_NAME).buildObject(ChannelBindings.DEFAULT_ELEMENT_NAME);
        cb2.setValue("bar");
        SOAPSupport.addHeaderBlock(messageCtx, cb2);

        final ExtractChannelBindingsHeadersHandler handler = new ExtractChannelBindingsHeadersHandler();
        handler.setNextDestination(false);
        handler.setFinalDestination(true);
        handler.initialize();
        
        handler.invoke(messageCtx);
        final ChannelBindingsContext cbCtx = messageCtx.getSubcontext(SOAP11Context.class).getSubcontext(
                ChannelBindingsContext.class);
        Assert.assertNotNull(cbCtx);
        Assert.assertEquals(cbCtx.getChannelBindings().size(), 1);
        
        final ChannelBindings[] array = cbCtx.getChannelBindings().toArray(new ChannelBindings[2]);
        Assert.assertTrue("bar".equals(array[0].getValue()));
    }
}