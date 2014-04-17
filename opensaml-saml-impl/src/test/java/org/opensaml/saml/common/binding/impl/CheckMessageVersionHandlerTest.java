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

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.binding.impl.CheckMessageVersionHandler;
import org.opensaml.saml.saml1.core.RequestAbstractType;
import org.opensaml.saml.saml1.profile.SAML1ActionTestingSupport;
import org.opensaml.saml.saml2.core.AttributeQuery;
import org.opensaml.saml.saml2.profile.SAML2ActionTestingSupport;
import org.testng.annotations.Test;

/** {@link CheckMessageVersionHandler} unit test. */
public class CheckMessageVersionHandlerTest extends OpenSAMLInitBaseTestCase {

    @Test(expectedExceptions = MessageHandlerException.class)
    public void testNoMessageThrows() throws ComponentInitializationException, MessageHandlerException {
        final MessageContext<SAMLObject> messageCtx = new MessageContext<>();

        final CheckMessageVersionHandler handler = new CheckMessageVersionHandler();
        handler.setId("test");
        handler.initialize();
        
        handler.invoke(messageCtx);
    }

    @Test
    public void testNoMessageSilent() throws ComponentInitializationException, MessageHandlerException {
        final MessageContext<SAMLObject> messageCtx = new MessageContext<>();

        final CheckMessageVersionHandler handler = new CheckMessageVersionHandler();
        handler.setId("test");
        handler.setIgnoreMissingOrUnrecognized(true);
        handler.initialize();
        
        handler.invoke(messageCtx);
    }
    
    @Test(expectedExceptions = MessageHandlerException.class)
    public void testBadMessageThrows() throws ComponentInitializationException, MessageHandlerException {
        final MessageContext<SAMLObject> messageCtx = new MessageContext<>();
        messageCtx.setMessage(SAML1ActionTestingSupport.buildAssertion());

        final CheckMessageVersionHandler handler = new CheckMessageVersionHandler();
        handler.setId("test");
        handler.initialize();
        
        handler.invoke(messageCtx);
    }

    @Test
    public void testBadMessageSilent() throws ComponentInitializationException, MessageHandlerException {
        final MessageContext<SAMLObject> messageCtx = new MessageContext<>();
        messageCtx.setMessage(SAML1ActionTestingSupport.buildAssertion());

        final CheckMessageVersionHandler handler = new CheckMessageVersionHandler();
        handler.setId("test");
        handler.setIgnoreMissingOrUnrecognized(true);
        handler.initialize();
        
        handler.invoke(messageCtx);
    }
    
    /** Test that the handler accepts SAML 1.0 and 1.1 messages. */
    @Test
    public void testSaml1Message() throws MessageHandlerException, ComponentInitializationException {
        final MessageContext<SAMLObject> messageCtx = new MessageContext<>();
        messageCtx.setMessage(SAML1ActionTestingSupport.buildAttributeQueryRequest(null));

        final CheckMessageVersionHandler handler = new CheckMessageVersionHandler();
        handler.setId("test");
        handler.initialize();
        
        handler.invoke(messageCtx);
    }

    /** Test that the handler errors out on SAML 2 messages. */
    @Test(expectedExceptions = MessageHandlerException.class)
    public void testSaml2MessageFail() throws MessageHandlerException, ComponentInitializationException {
        final MessageContext<SAMLObject> messageCtx = new MessageContext<>();
        messageCtx.setMessage(SAML1ActionTestingSupport.buildAttributeQueryRequest(null));
        ((RequestAbstractType) messageCtx.getMessage()).setVersion(SAMLVersion.VERSION_20);

        final CheckMessageVersionHandler handler = new CheckMessageVersionHandler();
        handler.setId("test");
        handler.initialize();
        
        handler.invoke(messageCtx);
    }
 
    /** Test that the handler accepts SAML 2.0 messages. */
    @Test
    public void testSaml2Message() throws MessageHandlerException, ComponentInitializationException {
        final MessageContext<SAMLObject> messageCtx = new MessageContext<>();
        messageCtx.setMessage(SAML2ActionTestingSupport.buildAttributeQueryRequest(null));

        final CheckMessageVersionHandler handler = new CheckMessageVersionHandler();
        handler.setId("test");
        handler.initialize();
        
        handler.invoke(messageCtx);
    }

    /** Test that the handler errors out on SAML 1 messages. */
    @Test(expectedExceptions = MessageHandlerException.class)
    public void testSaml1MessageFail() throws MessageHandlerException, ComponentInitializationException {
        final MessageContext<SAMLObject> messageCtx = new MessageContext<>();
        messageCtx.setMessage(SAML2ActionTestingSupport.buildAttributeQueryRequest(null));
        ((AttributeQuery) messageCtx.getMessage()).setVersion(SAMLVersion.VERSION_11);

        final CheckMessageVersionHandler handler = new CheckMessageVersionHandler();
        handler.setId("test");
        handler.initialize();
        
        handler.invoke(messageCtx);
    }

}