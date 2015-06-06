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

package org.opensaml.soap.wsaddressing.messaging.impl;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.security.IdentifierGenerationStrategy;

import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.soap.SOAPMessagingBaseTestCase;
import org.opensaml.soap.messaging.SOAPMessagingSupport;
import org.opensaml.soap.wsaddressing.MessageID;
import org.opensaml.soap.wsaddressing.messaging.context.WSAddressingContext;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 */
public class AddMessageIDHandlerTest extends SOAPMessagingBaseTestCase {
    
    private AddMessageIDHandler handler;
    
    @BeforeMethod
    protected void setUp() throws ComponentInitializationException {
        handler = new AddMessageIDHandler();
    }
    
    @Test
    public void testDefaultInternalUUID() throws ComponentInitializationException, MessageHandlerException {
        handler.initialize();
        handler.invoke(getMessageContext());
        
        Assert.assertFalse(SOAPMessagingSupport.getInboundHeaderBlock(getMessageContext(), MessageID.ELEMENT_NAME).isEmpty());
        MessageID messageID = (MessageID) SOAPMessagingSupport.getInboundHeaderBlock(getMessageContext(), MessageID.ELEMENT_NAME).get(0);
        Assert.assertTrue(messageID.getValue().startsWith("urn:uuid:"));
    }

    @Test
    public void testStrategy() throws ComponentInitializationException, MessageHandlerException {
        handler.setIdentifierGenerationStrategy(new IdentifierGenerationStrategy() {
            @Nonnull public String generateIdentifier(boolean xmlSafe) {
                if (xmlSafe) {
                    return "urn:test:abc123:xmlsafe";
                } else {
                    return "urn:test:abc123";
                }
            }
            @Nonnull public String generateIdentifier() {
                return generateIdentifier(true);
            }
        });
        
        handler.initialize();
        handler.invoke(getMessageContext());
        
        Assert.assertFalse(SOAPMessagingSupport.getInboundHeaderBlock(getMessageContext(), MessageID.ELEMENT_NAME).isEmpty());
        MessageID messageID = (MessageID) SOAPMessagingSupport.getInboundHeaderBlock(getMessageContext(), MessageID.ELEMENT_NAME).get(0);
        Assert.assertEquals(messageID.getValue(), "urn:test:abc123");
    }
    
    @Test
    public void testContext() throws ComponentInitializationException, MessageHandlerException {
        getMessageContext().getSubcontext(WSAddressingContext.class, true).setMessageIDURI("urn:test:abc123");
        
        handler.initialize();
        handler.invoke(getMessageContext());
        
        Assert.assertFalse(SOAPMessagingSupport.getInboundHeaderBlock(getMessageContext(), MessageID.ELEMENT_NAME).isEmpty());
        MessageID messageID = (MessageID) SOAPMessagingSupport.getInboundHeaderBlock(getMessageContext(), MessageID.ELEMENT_NAME).get(0);
        Assert.assertEquals(messageID.getValue(), "urn:test:abc123");
    }

    @Test
    public void testContextOverride() throws ComponentInitializationException, MessageHandlerException {
        handler.setIdentifierGenerationStrategy(new IdentifierGenerationStrategy() {
            @Nonnull public String generateIdentifier(boolean xmlSafe) {
                if (xmlSafe) {
                    return "urn:test:abc123:xmlsafe";
                } else {
                    return "urn:test:abc123";
                }
            }
            @Nonnull public String generateIdentifier() {
                return generateIdentifier(true);
            }
        });
        
        getMessageContext().getSubcontext(WSAddressingContext.class, true).setMessageIDURI("urn:test:def456");
        
        handler.initialize();
        handler.invoke(getMessageContext());
        
        Assert.assertFalse(SOAPMessagingSupport.getInboundHeaderBlock(getMessageContext(), MessageID.ELEMENT_NAME).isEmpty());
        MessageID messageID = (MessageID) SOAPMessagingSupport.getInboundHeaderBlock(getMessageContext(), MessageID.ELEMENT_NAME).get(0);
        Assert.assertEquals(messageID.getValue(), "urn:test:def456");
    }

}
