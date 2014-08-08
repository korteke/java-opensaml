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

package org.opensaml.saml.common.binding.security.impl;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.security.impl.MessageReplaySecurityHandler;
import org.opensaml.saml.common.messaging.context.SAMLMessageInfoContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.storage.ReplayCache;
import org.opensaml.storage.impl.MemoryStorageService;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Testing SAML message replay security policy rule.
 */
public class MessageReplaySecurityHandlerTest extends XMLObjectBaseTestCase {
    
    private MessageContext<SAMLObject> messageContext;
    
    private MessageReplaySecurityHandler handler;

    private String messageID;

    private MemoryStorageService storageService;

    private ReplayCache replayCache;

    @BeforeMethod
    protected void setUp() throws Exception {
        messageContext = new MessageContext<>();
        
        messageID = "abc123";

        messageContext.getSubcontext(SAMLPeerEntityContext.class, true).setEntityId("issuer");
        messageContext.getSubcontext(SAMLMessageInfoContext.class, true).setMessageId(messageID);

        storageService = new MemoryStorageService();
        storageService.setId("test");
        storageService.initialize();
        
        replayCache = new ReplayCache();
        replayCache.setStorage(storageService);
        replayCache.initialize();
        
        handler = new MessageReplaySecurityHandler();
        handler.setReplayCache(replayCache);
        handler.initialize();
    }

    @AfterMethod
    protected void tearDown() {
        handler = null;
        
        replayCache.destroy();
        replayCache = null;
        
        storageService.destroy();
        storageService = null;
    }
    
    /**
     * Test valid message ID.
     * @throws MessageHandlerException 
     */
    @Test
    public void testNoReplay() throws MessageHandlerException {
        handler.invoke(messageContext);
    }

    /**
     * Test valid message ID, distinct ID.
     * @throws MessageHandlerException 
     */
    @Test
    public void testNoReplayDistinctIDs() throws MessageHandlerException {
        handler.invoke(messageContext);

        messageContext.getSubcontext(SAMLMessageInfoContext.class).setMessageId("someOther" + messageID);
        handler.invoke(messageContext);
    }

    /**
     * Test invalid replay of message ID.
     * @throws MessageHandlerException 
     */
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testReplay() throws MessageHandlerException {
        handler.invoke(messageContext);
        handler.invoke(messageContext);
    }

    /**
     * Test valid replay of message ID due to replay cache expiration.
     * 
     * @throws InterruptedException
     * @throws MessageHandlerException 
     * @throws ComponentInitializationException 
     */
    @Test
    public void testReplayValidWithExpiration() throws InterruptedException, MessageHandlerException, ComponentInitializationException {
        handler = new MessageReplaySecurityHandler();
        handler.setReplayCache(replayCache);
        
        // Set rule with 3 second expiration, with no clock skew
        handler.setExpires(3 * 1000);
        handler.initialize();
        
        handler.invoke(messageContext);

        // Now sleep for 4 seconds to be sure has expired, and retry same message id
        Thread.sleep(4000L);
        handler.invoke(messageContext);
    }

}
