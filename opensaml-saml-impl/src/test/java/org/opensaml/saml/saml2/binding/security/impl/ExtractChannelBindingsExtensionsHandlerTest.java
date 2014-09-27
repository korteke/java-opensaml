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
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.ext.saml2cb.ChannelBindings;
import org.opensaml.saml.saml1.profile.SAML1ActionTestingSupport;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Extensions;
import org.opensaml.saml.saml2.profile.SAML2ActionTestingSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/** {@link ExtractChannelBindingsExtensionsHandler} unit test. */
public class ExtractChannelBindingsExtensionsHandlerTest extends OpenSAMLInitBaseTestCase {
    
    private MessageContext messageCtx;
    
    private ExtractChannelBindingsExtensionsHandler handler;
    
    @BeforeMethod public void setUp() throws ComponentInitializationException {
        handler = new ExtractChannelBindingsExtensionsHandler();
        handler.initialize();
        
        messageCtx = new MessageContext();
        messageCtx.getSubcontext(SAMLBindingContext.class, true).setHasBindingSignature(true);
    }
    
    /** Test that the handler returns nothing on a missing message. */
    @Test public void testMissingMessage() throws MessageHandlerException {

        handler.invoke(messageCtx);
        Assert.assertNull(messageCtx.getSubcontext(ChannelBindingsContext.class));
        
        messageCtx.setMessage(SAML1ActionTestingSupport.buildResponse());
        handler.invoke(messageCtx);
        Assert.assertNull(messageCtx.getSubcontext(ChannelBindingsContext.class));
    }

    /** Test that the handler does nothing when no extensions exist. */
    @Test public void testNoExtensions() throws MessageHandlerException {
        messageCtx.setMessage(SAML2ActionTestingSupport.buildAuthnRequest());
        
        handler.invoke(messageCtx);
        Assert.assertNull(messageCtx.getSubcontext(ChannelBindingsContext.class));
    }
    
    /** Test that the handler ignores unsigned bindings. */
    @Test public void testUnsigned() throws MessageHandlerException {
        final Extensions ext = XMLObjectProviderRegistrySupport.getBuilderFactory().<Extensions>getBuilderOrThrow(
                Extensions.DEFAULT_ELEMENT_NAME).buildObject(Extensions.DEFAULT_ELEMENT_NAME);

        messageCtx.setMessage(SAML2ActionTestingSupport.buildAuthnRequest());
        ((AuthnRequest) messageCtx.getMessage()).setExtensions(ext);
        
        final ChannelBindings cb = XMLObjectProviderRegistrySupport.getBuilderFactory().<ChannelBindings>getBuilderOrThrow(
                ChannelBindings.DEFAULT_ELEMENT_NAME).buildObject(ChannelBindings.DEFAULT_ELEMENT_NAME);
        cb.setValue("foo");
        ext.getUnknownXMLObjects().add(cb);

        final ChannelBindings cb2 = XMLObjectProviderRegistrySupport.getBuilderFactory().<ChannelBindings>getBuilderOrThrow(
                ChannelBindings.DEFAULT_ELEMENT_NAME).buildObject(ChannelBindings.DEFAULT_ELEMENT_NAME);
        cb2.setValue("bar");
        ext.getUnknownXMLObjects().add(cb2);
        
        messageCtx.getSubcontext(SAMLBindingContext.class).setHasBindingSignature(false);
        
        handler.invoke(messageCtx);
        final ChannelBindingsContext cbCtx = messageCtx.getSubcontext(ChannelBindingsContext.class);
        Assert.assertNull(cbCtx);
    }
    
    /** Test that the handler works. */
    @Test public void testSuccess() throws MessageHandlerException {
        final Extensions ext = XMLObjectProviderRegistrySupport.getBuilderFactory().<Extensions>getBuilderOrThrow(
                Extensions.DEFAULT_ELEMENT_NAME).buildObject(Extensions.DEFAULT_ELEMENT_NAME);

        messageCtx.setMessage(SAML2ActionTestingSupport.buildAuthnRequest());
        ((AuthnRequest) messageCtx.getMessage()).setExtensions(ext);
        
        final ChannelBindings cb = XMLObjectProviderRegistrySupport.getBuilderFactory().<ChannelBindings>getBuilderOrThrow(
                ChannelBindings.DEFAULT_ELEMENT_NAME).buildObject(ChannelBindings.DEFAULT_ELEMENT_NAME);
        cb.setValue("foo");
        ext.getUnknownXMLObjects().add(cb);

        final ChannelBindings cb2 = XMLObjectProviderRegistrySupport.getBuilderFactory().<ChannelBindings>getBuilderOrThrow(
                ChannelBindings.DEFAULT_ELEMENT_NAME).buildObject(ChannelBindings.DEFAULT_ELEMENT_NAME);
        cb2.setValue("bar");
        ext.getUnknownXMLObjects().add(cb2);
        
        handler.invoke(messageCtx);
        final ChannelBindingsContext cbCtx = messageCtx.getSubcontext(ChannelBindingsContext.class);
        Assert.assertNotNull(cbCtx);
        Assert.assertEquals(cbCtx.getChannelBindings().size(), 2);
        
        final ChannelBindings[] array = cbCtx.getChannelBindings().toArray(new ChannelBindings[2]);
        Assert.assertTrue("foo".equals(array[0].getValue()) || "bar".equals(array[0].getValue()));
        Assert.assertTrue("foo".equals(array[1].getValue()) || "bar".equals(array[1].getValue()));
    }

}