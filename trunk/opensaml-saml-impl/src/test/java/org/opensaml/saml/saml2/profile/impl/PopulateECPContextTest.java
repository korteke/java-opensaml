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

package org.opensaml.saml.saml2.profile.impl;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.profile.RequestContextBuilder;
import org.opensaml.profile.action.ActionTestingSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.messaging.context.ECPContext;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.profile.SAML2ActionTestingSupport;
import org.opensaml.saml.saml2.profile.context.EncryptionContext;
import org.opensaml.xmlsec.EncryptionParameters;

/** Unit test for {@link PopulateECPContext}. */
public class PopulateECPContextTest extends OpenSAMLInitBaseTestCase {
    
    private ProfileRequestContext<Object,Response> prc;
    
    private PopulateECPContext action;
    
    @BeforeMethod
    public void setUp() throws NoSuchAlgorithmException, NoSuchProviderException {
        prc = new RequestContextBuilder().setInboundMessage(
                SAML2ActionTestingSupport.buildAuthnRequest()).buildProfileRequestContext();
        action = new PopulateECPContext();
    }
    
    @Test
    public void testUnsignedUnencrypted() throws ComponentInitializationException {
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        final ECPContext ecp = prc.getOutboundMessageContext().getSubcontext(ECPContext.class);
        if (ecp != null) {
            Assert.assertFalse(ecp.isRequestAuthenticated());
            Assert.assertNull(ecp.getSessionKey());
        }
    }
    
    @Test
    public void testAuthenticated() throws ComponentInitializationException {
        
        prc.getInboundMessageContext().getSubcontext(SAMLBindingContext.class, true).setHasBindingSignature(true);
        
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        final ECPContext ecp = prc.getOutboundMessageContext().getSubcontext(ECPContext.class);
        Assert.assertTrue(ecp.isRequestAuthenticated());
        Assert.assertNull(ecp.getSessionKey());
    }

    @Test
    public void testUnencrypted() throws ComponentInitializationException {

        action.setRequireEncryption(false);
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        final ECPContext ecp = prc.getOutboundMessageContext().getSubcontext(ECPContext.class);
        Assert.assertNotNull(ecp.getSessionKey());
    }
    
    @Test
    public void testEncrypted() throws ComponentInitializationException {
        
        prc.getOutboundMessageContext().getSubcontext(
                EncryptionContext.class, true).setAssertionEncryptionParameters(new EncryptionParameters());

        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        final ECPContext ecp = prc.getOutboundMessageContext().getSubcontext(ECPContext.class);
        Assert.assertNotNull(ecp.getSessionKey());
    }

}