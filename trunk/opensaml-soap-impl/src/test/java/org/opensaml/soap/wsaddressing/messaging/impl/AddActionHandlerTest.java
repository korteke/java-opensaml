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

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.soap.SOAPMessagingBaseTestCase;
import org.opensaml.soap.messaging.SOAPMessagingSupport;
import org.opensaml.soap.wsaddressing.Action;
import org.opensaml.soap.wsaddressing.messaging.context.WSAddressingContext;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 */
public class AddActionHandlerTest extends SOAPMessagingBaseTestCase {
    
    private AddActionHandler handler;
    
    @BeforeMethod
    protected void setUp() throws ComponentInitializationException {
        handler = new AddActionHandler();
    }
    
    @Test
    public void testNoInput() throws ComponentInitializationException, MessageHandlerException {
        handler.initialize();
        handler.invoke(getMessageContext());
        
        Assert.assertTrue(SOAPMessagingSupport.getInboundHeaderBlock(getMessageContext(), Action.ELEMENT_NAME).isEmpty());
    }
    
    @Test
    public void testLocal() throws ComponentInitializationException, MessageHandlerException {
        handler.setActionURI("urn:test:action1");
        
        handler.initialize();
        handler.invoke(getMessageContext());
        
        Assert.assertFalse(SOAPMessagingSupport.getInboundHeaderBlock(getMessageContext(), Action.ELEMENT_NAME).isEmpty());
        Action action = (Action) SOAPMessagingSupport.getInboundHeaderBlock(getMessageContext(), Action.ELEMENT_NAME).get(0);
        Assert.assertEquals(action.getValue(), "urn:test:action1");
    }

    @Test
    public void testContext() throws ComponentInitializationException, MessageHandlerException {
        getMessageContext().getSubcontext(WSAddressingContext.class, true).setActionURI("urn:test:action1");
        
        handler.initialize();
        handler.invoke(getMessageContext());
        
        Assert.assertFalse(SOAPMessagingSupport.getInboundHeaderBlock(getMessageContext(), Action.ELEMENT_NAME).isEmpty());
        Action action = (Action) SOAPMessagingSupport.getInboundHeaderBlock(getMessageContext(), Action.ELEMENT_NAME).get(0);
        Assert.assertEquals(action.getValue(), "urn:test:action1");
    }

    @Test
    public void testContextOverride() throws ComponentInitializationException, MessageHandlerException {
        handler.setActionURI("urn:test:action1");
        getMessageContext().getSubcontext(WSAddressingContext.class, true).setActionURI("urn:test:action2");
        
        handler.initialize();
        handler.invoke(getMessageContext());
        
        Assert.assertFalse(SOAPMessagingSupport.getInboundHeaderBlock(getMessageContext(), Action.ELEMENT_NAME).isEmpty());
        Action action = (Action) SOAPMessagingSupport.getInboundHeaderBlock(getMessageContext(), Action.ELEMENT_NAME).get(0);
        Assert.assertEquals(action.getValue(), "urn:test:action2");
    }

}
