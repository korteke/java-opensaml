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

package org.opensaml.saml.common.profile.impl;

import java.util.Collections;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.profile.RequestContextBuilder;
import org.opensaml.profile.action.ActionTestingSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.PreviousEventContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/** Unit test for {@link CheckErrorHandlingStrategy}. */
public class CheckErrorHandlingStrategyTest extends OpenSAMLInitBaseTestCase {

    private ProfileRequestContext prc;
    
    private CheckErrorHandlingStrategy action;
    
    private XMLObjectBuilder<?> endpointBuilder;

    @BeforeMethod public void setUp() throws ComponentInitializationException {
        endpointBuilder = XMLObjectSupport.getBuilder(AssertionConsumerService.DEFAULT_ELEMENT_NAME);
        prc = new RequestContextBuilder().buildProfileRequestContext();
        action = new CheckErrorHandlingStrategy();
        action.initialize();
    }
    
    @Test public void testNoBindingContext() throws Exception {
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.TRAP_ERROR);
    }

    @Test public void testNoEndpointContext() throws Exception {
        prc.getOutboundMessageContext().getSubcontext(SAMLBindingContext.class, true).setBindingUri("foo");
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.TRAP_ERROR);
    }

    @Test public void testNoEvent() throws Exception {
        prc.getOutboundMessageContext().getSubcontext(SAMLBindingContext.class, true).setBindingUri("foo");
        
        final Endpoint ep = (Endpoint) endpointBuilder.buildObject(AssertionConsumerService.DEFAULT_ELEMENT_NAME);
        ep.setLocation("foo");
        prc.getOutboundMessageContext().getSubcontext(SAMLPeerEntityContext.class, true).getSubcontext(
                SAMLEndpointContext.class, true).setEndpoint(ep);
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
    }
    
    @Test public void testNonLocal() throws Exception {
        prc.getOutboundMessageContext().getSubcontext(SAMLBindingContext.class, true).setBindingUri("foo");
        
        final Endpoint ep = (Endpoint) endpointBuilder.buildObject(AssertionConsumerService.DEFAULT_ELEMENT_NAME);
        ep.setLocation("foo");
        prc.getOutboundMessageContext().getSubcontext(SAMLPeerEntityContext.class, true).getSubcontext(
                SAMLEndpointContext.class, true).setEndpoint(ep);
        
        prc.getSubcontext(PreviousEventContext.class, true).setEvent("Foo");
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
    }    
    
    @Test public void testLocal() throws Exception {
        prc.getOutboundMessageContext().getSubcontext(SAMLBindingContext.class, true).setBindingUri("foo");
        
        final Endpoint ep = (Endpoint) endpointBuilder.buildObject(AssertionConsumerService.DEFAULT_ELEMENT_NAME);
        ep.setLocation("foo");
        prc.getOutboundMessageContext().getSubcontext(SAMLPeerEntityContext.class, true).getSubcontext(
                SAMLEndpointContext.class, true).setEndpoint(ep);
        
        prc.getSubcontext(PreviousEventContext.class, true).setEvent("Foo");
        
        action = new CheckErrorHandlingStrategy();
        action.setLocalEvents(Collections.singletonList("Foo"));
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.TRAP_ERROR);
    }    

}