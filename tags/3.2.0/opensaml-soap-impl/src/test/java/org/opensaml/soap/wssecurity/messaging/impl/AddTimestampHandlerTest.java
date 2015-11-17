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

package org.opensaml.soap.wssecurity.messaging.impl;

import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.ContextDataLookupFunction;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.soap.SOAPMessagingBaseTestCase;
import org.opensaml.soap.messaging.SOAPMessagingSupport;
import org.opensaml.soap.wssecurity.Security;
import org.opensaml.soap.wssecurity.Timestamp;
import org.opensaml.soap.wssecurity.messaging.WSSecurityContext;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 */
public class AddTimestampHandlerTest extends SOAPMessagingBaseTestCase {
    
    private AddTimestampHandler handler;
    
    @BeforeMethod
    protected void setUp() throws ComponentInitializationException {
        handler = new AddTimestampHandler();
    }
    
    @Test
    public void testNoInput() throws ComponentInitializationException, MessageHandlerException {
        handler.initialize();
        handler.invoke(getMessageContext());
        
        Assert.assertTrue(SOAPMessagingSupport.getOutboundHeaderBlock(getMessageContext(), Security.ELEMENT_NAME).isEmpty());
    }
    
    @Test
    public void testNoInputUsingCurrentTimeAndOffset() throws ComponentInitializationException, MessageHandlerException {
        handler.setUseCurrentTimeAsDefaultCreated(true);
        handler.setExpiresOffsetFromCreated(5*60*1000L);
        
        handler.initialize();
        handler.invoke(getMessageContext());
        
        Assert.assertFalse(SOAPMessagingSupport.getOutboundHeaderBlock(getMessageContext(), Security.ELEMENT_NAME).isEmpty());
        Security security = (Security) SOAPMessagingSupport.getOutboundHeaderBlock(getMessageContext(), Security.ELEMENT_NAME).get(0);
        
        Assert.assertFalse(security.getUnknownXMLObjects(Timestamp.ELEMENT_NAME).isEmpty());
        Timestamp timestamp = (Timestamp) security.getUnknownXMLObjects(Timestamp.ELEMENT_NAME).get(0);
        Assert.assertNotNull(timestamp.getCreated());
        Assert.assertNotNull(timestamp.getCreated().getDateTime());
        Assert.assertNotNull(timestamp.getExpires());
        Assert.assertEquals(timestamp.getExpires().getDateTime(), timestamp.getCreated().getDateTime().plus(5*60*1000));
    }
    
    @Test
    public void testContextBothValues() throws ComponentInitializationException, MessageHandlerException {
        DateTime created = new DateTime(ISOChronology.getInstanceUTC());
        DateTime expires = created.plusMinutes(5);
        getMessageContext().getSubcontext(WSSecurityContext.class, true).setTimestampCreated(created);
        getMessageContext().getSubcontext(WSSecurityContext.class, true).setTimestampExpires(expires);
        
        handler.initialize();
        handler.invoke(getMessageContext());
        
        Assert.assertFalse(SOAPMessagingSupport.getOutboundHeaderBlock(getMessageContext(), Security.ELEMENT_NAME).isEmpty());
        Security security = (Security) SOAPMessagingSupport.getOutboundHeaderBlock(getMessageContext(), Security.ELEMENT_NAME).get(0);
        
        Assert.assertFalse(security.getUnknownXMLObjects(Timestamp.ELEMENT_NAME).isEmpty());
        Timestamp timestamp = (Timestamp) security.getUnknownXMLObjects(Timestamp.ELEMENT_NAME).get(0);
        Assert.assertNotNull(timestamp.getCreated());
        Assert.assertEquals(timestamp.getCreated().getDateTime(), created);
        Assert.assertNotNull(timestamp.getExpires());
        Assert.assertEquals(timestamp.getExpires().getDateTime(), expires);
    }
    
    @Test
    public void testContextCreated() throws ComponentInitializationException, MessageHandlerException {
        DateTime created = new DateTime(ISOChronology.getInstanceUTC());
        getMessageContext().getSubcontext(WSSecurityContext.class, true).setTimestampCreated(created);
        
        handler.initialize();
        handler.invoke(getMessageContext());
        
        Assert.assertFalse(SOAPMessagingSupport.getOutboundHeaderBlock(getMessageContext(), Security.ELEMENT_NAME).isEmpty());
        Security security = (Security) SOAPMessagingSupport.getOutboundHeaderBlock(getMessageContext(), Security.ELEMENT_NAME).get(0);
        
        Assert.assertFalse(security.getUnknownXMLObjects(Timestamp.ELEMENT_NAME).isEmpty());
        Timestamp timestamp = (Timestamp) security.getUnknownXMLObjects(Timestamp.ELEMENT_NAME).get(0);
        Assert.assertNotNull(timestamp.getCreated());
        Assert.assertEquals(timestamp.getCreated().getDateTime(), created);
        Assert.assertNull(timestamp.getExpires());
    }
    
    @Test
    public void testContextExpires() throws ComponentInitializationException, MessageHandlerException {
        DateTime created = new DateTime(ISOChronology.getInstanceUTC());
        DateTime expires = created.plusMinutes(5);
        getMessageContext().getSubcontext(WSSecurityContext.class, true).setTimestampExpires(expires);
        
        handler.initialize();
        handler.invoke(getMessageContext());
        
        Assert.assertFalse(SOAPMessagingSupport.getOutboundHeaderBlock(getMessageContext(), Security.ELEMENT_NAME).isEmpty());
        Security security = (Security) SOAPMessagingSupport.getOutboundHeaderBlock(getMessageContext(), Security.ELEMENT_NAME).get(0);
        
        Assert.assertFalse(security.getUnknownXMLObjects(Timestamp.ELEMENT_NAME).isEmpty());
        Timestamp timestamp = (Timestamp) security.getUnknownXMLObjects(Timestamp.ELEMENT_NAME).get(0);
        Assert.assertNull(timestamp.getCreated());
        Assert.assertNotNull(timestamp.getExpires());
        Assert.assertEquals(timestamp.getExpires().getDateTime(), expires);
    }
    
    @Test
    public void testContextCreatedWithOffset() throws ComponentInitializationException, MessageHandlerException {
        DateTime created = new DateTime(ISOChronology.getInstanceUTC());
        DateTime expires = created.plusMinutes(5);
        getMessageContext().getSubcontext(WSSecurityContext.class, true).setTimestampCreated(created);
        handler.setExpiresOffsetFromCreated(5*60*1000L);
        
        handler.initialize();
        handler.invoke(getMessageContext());
        
        Assert.assertFalse(SOAPMessagingSupport.getOutboundHeaderBlock(getMessageContext(), Security.ELEMENT_NAME).isEmpty());
        Security security = (Security) SOAPMessagingSupport.getOutboundHeaderBlock(getMessageContext(), Security.ELEMENT_NAME).get(0);
        
        Assert.assertFalse(security.getUnknownXMLObjects(Timestamp.ELEMENT_NAME).isEmpty());
        Timestamp timestamp = (Timestamp) security.getUnknownXMLObjects(Timestamp.ELEMENT_NAME).get(0);
        Assert.assertNotNull(timestamp.getCreated());
        Assert.assertEquals(timestamp.getCreated().getDateTime(), created);
        Assert.assertNotNull(timestamp.getExpires());
        Assert.assertEquals(timestamp.getExpires().getDateTime(), expires);
    }
    
    @Test
    public void testLookupBothValues() throws ComponentInitializationException, MessageHandlerException {
        final DateTime created = new DateTime(ISOChronology.getInstanceUTC());
        final DateTime expires = created.plusMinutes(5);
        handler.setCreatedLookup(new ContextDataLookupFunction<MessageContext, DateTime>() {
            @Nullable public DateTime apply(@Nullable MessageContext input) {
                return created;
            }
        });
        handler.setExpiresLookup(new ContextDataLookupFunction<MessageContext, DateTime>() {
            @Nullable public DateTime apply(@Nullable MessageContext input) {
                return expires;
            }
        });
        
        handler.initialize();
        handler.invoke(getMessageContext());
        
        Assert.assertFalse(SOAPMessagingSupport.getOutboundHeaderBlock(getMessageContext(), Security.ELEMENT_NAME).isEmpty());
        Security security = (Security) SOAPMessagingSupport.getOutboundHeaderBlock(getMessageContext(), Security.ELEMENT_NAME).get(0);
        
        Assert.assertFalse(security.getUnknownXMLObjects(Timestamp.ELEMENT_NAME).isEmpty());
        Timestamp timestamp = (Timestamp) security.getUnknownXMLObjects(Timestamp.ELEMENT_NAME).get(0);
        Assert.assertNotNull(timestamp.getCreated());
        Assert.assertEquals(timestamp.getCreated().getDateTime(), created);
        Assert.assertNotNull(timestamp.getExpires());
        Assert.assertEquals(timestamp.getExpires().getDateTime(), expires);
    }
    
    @Test
    public void testLookupCreatedWithOffset() throws ComponentInitializationException, MessageHandlerException {
        final DateTime created = new DateTime(ISOChronology.getInstanceUTC());
        final DateTime expires = created.plusMinutes(5);
        handler.setCreatedLookup(new ContextDataLookupFunction<MessageContext, DateTime>() {
            @Nullable public DateTime apply(@Nullable MessageContext input) {
                return created;
            }
        });
        handler.setExpiresOffsetFromCreated(5*60*1000L);
        
        
        handler.initialize();
        handler.invoke(getMessageContext());
        
        Assert.assertFalse(SOAPMessagingSupport.getOutboundHeaderBlock(getMessageContext(), Security.ELEMENT_NAME).isEmpty());
        Security security = (Security) SOAPMessagingSupport.getOutboundHeaderBlock(getMessageContext(), Security.ELEMENT_NAME).get(0);
        
        Assert.assertFalse(security.getUnknownXMLObjects(Timestamp.ELEMENT_NAME).isEmpty());
        Timestamp timestamp = (Timestamp) security.getUnknownXMLObjects(Timestamp.ELEMENT_NAME).get(0);
        Assert.assertNotNull(timestamp.getCreated());
        Assert.assertEquals(timestamp.getCreated().getDateTime(), created);
        Assert.assertNotNull(timestamp.getExpires());
        Assert.assertEquals(timestamp.getExpires().getDateTime(), expires);
    }
    
}
