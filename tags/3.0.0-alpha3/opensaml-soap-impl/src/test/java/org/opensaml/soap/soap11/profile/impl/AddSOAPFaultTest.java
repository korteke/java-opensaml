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

import org.opensaml.profile.RequestContextBuilder;
import org.opensaml.profile.action.ActionTestingSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.soap.soap11.Fault;
import org.opensaml.soap.soap11.FaultCode;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
        
        final Fault fault = prc.getOutboundMessageContext().getMessage();
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
        
        final Fault fault = prc.getOutboundMessageContext().getMessage();
        Assert.assertNotNull(fault);
        Assert.assertEquals(fault.getMessage().getValue(), "Foo");
    }
    
 }