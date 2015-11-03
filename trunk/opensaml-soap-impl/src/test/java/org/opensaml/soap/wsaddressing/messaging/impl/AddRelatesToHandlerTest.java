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

import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.ContextDataLookupFunction;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.soap.SOAPMessagingBaseTestCase;
import org.opensaml.soap.messaging.SOAPMessagingSupport;
import org.opensaml.soap.wsaddressing.RelatesTo;
import org.opensaml.soap.wsaddressing.messaging.WSAddressingContext;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 */
public class AddRelatesToHandlerTest extends SOAPMessagingBaseTestCase {
    
    private AddRelatesToHandler handler;
    
    @BeforeMethod
    protected void setUp() throws ComponentInitializationException {
        handler = new AddRelatesToHandler();
    }
    
    @Test
    public void testNoInput() throws ComponentInitializationException, MessageHandlerException {
        handler.initialize();
        handler.invoke(getMessageContext());
        
        Assert.assertTrue(SOAPMessagingSupport.getOutboundHeaderBlock(getMessageContext(), RelatesTo.ELEMENT_NAME).isEmpty());
    }

    @Test
    public void testContext() throws ComponentInitializationException, MessageHandlerException {
        getMessageContext().getSubcontext(WSAddressingContext.class, true).setRelatesToURI("urn:test:abc123");
        getMessageContext().getSubcontext(WSAddressingContext.class, true).setRelatesToRelationshipType(RelatesTo.RELATIONSHIP_TYPE_REPLY);
        
        handler.initialize();
        handler.invoke(getMessageContext());
        
        Assert.assertFalse(SOAPMessagingSupport.getOutboundHeaderBlock(getMessageContext(), RelatesTo.ELEMENT_NAME).isEmpty());
        RelatesTo relatesTo = (RelatesTo) SOAPMessagingSupport.getOutboundHeaderBlock(getMessageContext(), RelatesTo.ELEMENT_NAME).get(0);
        Assert.assertEquals(relatesTo.getValue(), "urn:test:abc123");
        Assert.assertEquals(relatesTo.getRelationshipType(), RelatesTo.RELATIONSHIP_TYPE_REPLY);
    }
    
    @Test
    public void testLookup() throws ComponentInitializationException, MessageHandlerException {
        handler.setRelatesToURILookup(new ContextDataLookupFunction<MessageContext, String>() {
            @Nullable public String apply(@Nullable MessageContext input) {
                return "urn:test:def456";
            }
        });
        handler.setRelationshipType("urn:test:foo");
        
        handler.initialize();
        handler.invoke(getMessageContext());
        
        Assert.assertFalse(SOAPMessagingSupport.getOutboundHeaderBlock(getMessageContext(), RelatesTo.ELEMENT_NAME).isEmpty());
        RelatesTo relatesTo = (RelatesTo) SOAPMessagingSupport.getOutboundHeaderBlock(getMessageContext(), RelatesTo.ELEMENT_NAME).get(0);
        Assert.assertEquals(relatesTo.getValue(), "urn:test:def456");
        Assert.assertEquals(relatesTo.getRelationshipType(), "urn:test:foo");
    }

}
