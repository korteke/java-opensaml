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

package org.opensaml.soap.soap11.profile.impl;

import java.util.Collections;

import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.profile.RequestContextBuilder;
import org.opensaml.profile.action.ActionTestingSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.Fault;
import org.opensaml.soap.soap11.FaultCode;
import org.opensaml.soap.util.SOAPSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicates;

/** {@link AddSOAPFault} unit test. */
public class AddSOAPFaultTest extends OpenSAMLInitBaseTestCase {
    
    private ProfileRequestContext<Object,Fault> prc;
    
    private AddSOAPFault action;
    
    @BeforeMethod public void setUp() throws ComponentInitializationException {
        prc = new RequestContextBuilder().buildProfileRequestContext();
        action = new AddSOAPFault();
    }

    @Test public void testMinimal() throws ComponentInitializationException {
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assert.assertNull(prc.getOutboundMessageContext().getMessage());
        
        final Fault fault = prc.getOutboundMessageContext().getSubcontext(SOAP11Context.class, true).getFault();
        Assert.assertNotNull(fault);
        
        Assert.assertNotNull(fault.getCode());
        Assert.assertEquals(fault.getCode().getValue(), FaultCode.SERVER);
        
        Assert.assertNull(fault.getMessage());
    }

    @Test public void testFixedMessage() throws ComponentInitializationException {
        action.setFaultString("Foo");
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assert.assertNull(prc.getOutboundMessageContext().getMessage());
        
        final Fault fault = prc.getOutboundMessageContext().getSubcontext(SOAP11Context.class, true).getFault();
        Assert.assertNotNull(fault);
        
        Assert.assertNotNull(fault.getCode());
        Assert.assertEquals(fault.getCode().getValue(), FaultCode.SERVER);
        
        Assert.assertEquals(fault.getMessage().getValue(), "Foo");
    }
    
    @Test public void testCodeAndStringViaLookupWithDetailedErrors() throws ComponentInitializationException {
        action.setFaultCodeLookupStrategy(new Function<ProfileRequestContext, QName>() {
            @Nullable public QName apply(@Nullable ProfileRequestContext input) {
                return FaultCode.CLIENT;
            }});
        action.setFaultStringLookupStrategy(new Function<ProfileRequestContext, String>() {
            @Nullable public String apply(@Nullable ProfileRequestContext input) {
                return "TheClientError";
            }});
        
        action.setDetailedErrorsCondition(Predicates.<ProfileRequestContext>alwaysTrue());
        
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assert.assertNull(prc.getOutboundMessageContext().getMessage());
        
        final Fault fault = prc.getOutboundMessageContext().getSubcontext(SOAP11Context.class, true).getFault();
        Assert.assertNotNull(fault);
        
        Assert.assertNotNull(fault.getCode());
        Assert.assertEquals(fault.getCode().getValue(), FaultCode.CLIENT);
        
        Assert.assertNotNull(fault.getMessage());
        Assert.assertEquals(fault.getMessage().getValue(), "TheClientError");
        
        Assert.assertNull(fault.getActor());
        
        Assert.assertNull(fault.getDetail());
    }
    
    @Test public void testCodeAndStringViaLookupWithoutDetailedErrors() throws ComponentInitializationException {
        action.setFaultCodeLookupStrategy(new Function<ProfileRequestContext, QName>() {
            @Nullable public QName apply(@Nullable ProfileRequestContext input) {
                return FaultCode.CLIENT;
            }});
        action.setFaultStringLookupStrategy(new Function<ProfileRequestContext, String>() {
            @Nullable public String apply(@Nullable ProfileRequestContext input) {
                return "TheClientError";
            }});
        
        action.setDetailedErrorsCondition(Predicates.<ProfileRequestContext>alwaysFalse());
        
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assert.assertNull(prc.getOutboundMessageContext().getMessage());
        
        final Fault fault = prc.getOutboundMessageContext().getSubcontext(SOAP11Context.class, true).getFault();
        Assert.assertNotNull(fault);
        
        Assert.assertNotNull(fault.getCode());
        Assert.assertEquals(fault.getCode().getValue(), FaultCode.CLIENT);
        
        Assert.assertNull(fault.getMessage());
        
        Assert.assertNull(fault.getActor());
        
        Assert.assertNull(fault.getDetail());
    }
    
    @Test public void testContextFaultWithDetailedErrors() throws ComponentInitializationException {
        final Fault contextFault = SOAPSupport.buildSOAP11Fault(FaultCode.CLIENT, "TheClientError", "TheFaultActor", 
                Collections.singletonList(XMLObjectSupport.buildXMLObject(SimpleXMLObject.ELEMENT_NAME)), null);
        
        action.setContextFaultStrategy(new Function<ProfileRequestContext, Fault>() {
            @Nullable public Fault apply(@Nullable ProfileRequestContext input) {
                return contextFault;
            }});
        
        action.setDetailedErrorsCondition(Predicates.<ProfileRequestContext>alwaysTrue());
        
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assert.assertNull(prc.getOutboundMessageContext().getMessage());
        
        final Fault fault = prc.getOutboundMessageContext().getSubcontext(SOAP11Context.class, true).getFault();
        Assert.assertNotNull(fault);
        
        Assert.assertNotNull(fault.getCode());
        Assert.assertEquals(fault.getCode().getValue(), FaultCode.CLIENT);
        
        Assert.assertEquals(fault.getMessage().getValue(), "TheClientError");
        
        Assert.assertEquals(fault.getActor().getValue(), "TheFaultActor");
        
        Assert.assertNotNull(fault.getDetail());
        Assert.assertEquals(fault.getDetail().getUnknownXMLObjects().size(), 1);
    }
    
    @Test public void testContextFaultWithoutDetailedErrors() throws ComponentInitializationException {
        final Fault contextFault = SOAPSupport.buildSOAP11Fault(FaultCode.CLIENT, "TheClientError", "TheFaultActor", 
                Collections.singletonList(XMLObjectSupport.buildXMLObject(SimpleXMLObject.ELEMENT_NAME)), null);
        
        action.setContextFaultStrategy(new Function<ProfileRequestContext, Fault>() {
            @Nullable public Fault apply(@Nullable ProfileRequestContext input) {
                return contextFault;
            }});
        action.setDetailedErrorsCondition(Predicates.<ProfileRequestContext>alwaysFalse());
        
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assert.assertNull(prc.getOutboundMessageContext().getMessage());
        
        final Fault fault = prc.getOutboundMessageContext().getSubcontext(SOAP11Context.class, true).getFault();
        Assert.assertNotNull(fault);
        
        Assert.assertNotNull(fault.getCode());
        Assert.assertEquals(fault.getCode().getValue(), FaultCode.CLIENT);
        
        Assert.assertNull(fault.getMessage());
        
        Assert.assertNull(fault.getActor());
        
        Assert.assertNull(fault.getDetail());
    }
    
    @Test public void testDefaultContextFaultStrategyFromOutbound() throws ComponentInitializationException {
        final Fault contextFault = SOAPSupport.buildSOAP11Fault(FaultCode.CLIENT, "TheClientError", "TheFaultActor", 
                Collections.singletonList(XMLObjectSupport.buildXMLObject(SimpleXMLObject.ELEMENT_NAME)), null);
        prc.getOutboundMessageContext().getSubcontext(SOAP11Context.class, true).setFault(contextFault);
        
        action.setDetailedErrorsCondition(Predicates.<ProfileRequestContext>alwaysFalse());
        
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assert.assertNull(prc.getOutboundMessageContext().getMessage());
        
        final Fault fault = prc.getOutboundMessageContext().getSubcontext(SOAP11Context.class, true).getFault();
        Assert.assertNotNull(fault);
        
        Assert.assertNotNull(fault.getCode());
        Assert.assertEquals(fault.getCode().getValue(), FaultCode.CLIENT);
        
        Assert.assertNull(fault.getMessage());
        
        Assert.assertNull(fault.getActor());
        
        Assert.assertNull(fault.getDetail());
    }
    
    @Test public void testDefaultContextFaultStrategyFromInbound() throws ComponentInitializationException {
        final Fault contextFault = SOAPSupport.buildSOAP11Fault(FaultCode.CLIENT, "TheClientError", "TheFaultActor", 
                Collections.singletonList(XMLObjectSupport.buildXMLObject(SimpleXMLObject.ELEMENT_NAME)), null);
        prc.getInboundMessageContext().getSubcontext(SOAP11Context.class, true).setFault(contextFault);
        
        action.setDetailedErrorsCondition(Predicates.<ProfileRequestContext>alwaysFalse());
        
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assert.assertNull(prc.getOutboundMessageContext().getMessage());
        
        final Fault fault = prc.getOutboundMessageContext().getSubcontext(SOAP11Context.class, true).getFault();
        Assert.assertNotNull(fault);
        
        Assert.assertNotNull(fault.getCode());
        Assert.assertEquals(fault.getCode().getValue(), FaultCode.CLIENT);
        
        Assert.assertNull(fault.getMessage());
        
        Assert.assertNull(fault.getActor());
        
        Assert.assertNull(fault.getDetail());
    }
    
 }