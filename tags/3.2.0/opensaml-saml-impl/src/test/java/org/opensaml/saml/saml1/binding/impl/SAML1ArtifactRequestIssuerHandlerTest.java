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

package org.opensaml.saml.saml1.binding.impl;

import java.io.IOException;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.artifact.impl.BasicSAMLArtifactMap;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.saml1.profile.SAML1ActionTestingSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/** {@link SAML1ArtifactRequestIssuerHandler} unit test. */
public class SAML1ArtifactRequestIssuerHandlerTest extends OpenSAMLInitBaseTestCase {

    private BasicSAMLArtifactMap artifactMap;
    
    private MessageContext<SAMLObject> mc;
    
    private SAML1ArtifactRequestIssuerHandler handler;
    
    @BeforeMethod
    public void setUp() throws ComponentInitializationException {
        artifactMap = new BasicSAMLArtifactMap();
        artifactMap.initialize();
        
        handler = new SAML1ArtifactRequestIssuerHandler();
        handler.setArtifactMap(artifactMap);
        handler.initialize();
        
        mc = new MessageContext<>();
    }

    @Test
    public void testNoMessage() throws MessageHandlerException {
        handler.invoke(mc);
        Assert.assertFalse(mc.containsSubcontext(SAMLPeerEntityContext.class));
    }
    
    @Test
    public void testNoArtifacts() throws MessageHandlerException {
        mc.setMessage(SAML1ActionTestingSupport.buildArtifactRequest((String[]) null));
        
        handler.invoke(mc);
        Assert.assertFalse(mc.containsSubcontext(SAMLPeerEntityContext.class));
    }
    
    @Test
    public void testMissingArtifacts() throws MessageHandlerException {
        mc.setMessage(SAML1ActionTestingSupport.buildArtifactRequest("foo"));
        
        handler.invoke(mc);
        Assert.assertFalse(mc.containsSubcontext(SAMLPeerEntityContext.class));
    }

    @Test
    public void testSuccess() throws MessageHandlerException, IOException {
        mc.setMessage(SAML1ActionTestingSupport.buildArtifactRequest("foo"));
        artifactMap.put("foo", "https://sp.example.org", "https://idp.example.org",
                SAML1ActionTestingSupport.buildAssertion());
        
        handler.invoke(mc);
        
        final SAMLPeerEntityContext peerCtx = mc.getSubcontext(SAMLPeerEntityContext.class);
        Assert.assertNotNull(peerCtx);
        Assert.assertEquals(peerCtx.getEntityId(), "https://sp.example.org");
    }
    
}