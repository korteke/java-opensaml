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

package org.opensaml.saml.saml1.profile.impl;

import java.io.IOException;

import org.opensaml.profile.RequestContextBuilder;
import org.opensaml.profile.action.ActionTestingSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.saml.common.binding.artifact.impl.BasicSAMLArtifactMap;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.profile.SAMLEventIds;
import org.opensaml.saml.saml1.core.Request;
import org.opensaml.saml.saml1.core.Response;
import org.opensaml.saml.saml1.profile.SAML1ActionTestingSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Function;

/** {@link ResolveArtifacts} unit test. */
public class ResolveArtifactsTest extends OpenSAMLInitBaseTestCase {

    private BasicSAMLArtifactMap artifactMap;
    
    private ProfileRequestContext<Request,Response> prc;
    
    private ResolveArtifacts action;
    
    @BeforeMethod public void setUp() throws ComponentInitializationException {
        prc = new RequestContextBuilder().setOutboundMessage(
                SAML1ActionTestingSupport.buildResponse()).buildProfileRequestContext();
        prc.getInboundMessageContext().getSubcontext(SAMLPeerEntityContext.class, true).setEntityId("SP");
        
        artifactMap = new BasicSAMLArtifactMap();
        artifactMap.initialize();
        
        action = new ResolveArtifacts();
        action.setArtifactMap(artifactMap);
        action.setIssuerLookupStrategy(new Function<ProfileRequestContext,String>() {
            public String apply(ProfileRequestContext input) {
                return "IdP";
            }
        });
        action.initialize();
    }

    @Test public void testNoRequest() {
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }

    @Test public void testNoArtifacts() {
        prc.getInboundMessageContext().setMessage(SAML1ActionTestingSupport.buildArtifactRequest((String[]) null));
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }
    
    @Test public void testNoResponse() {
        prc.getInboundMessageContext().setMessage(SAML1ActionTestingSupport.buildArtifactRequest("foo"));
        prc.getOutboundMessageContext().setMessage(null);
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }
    
    @Test public void testMissingArtifacts() throws IOException {
        artifactMap.put("bar", "SP", "IdP", SAML1ActionTestingSupport.buildAssertion());
        prc.getInboundMessageContext().setMessage(SAML1ActionTestingSupport.buildArtifactRequest("foo","bar"));
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.UNABLE_RESOLVE_ARTIFACT);
        Assert.assertTrue(prc.getOutboundMessageContext().getMessage().getAssertions().isEmpty());
        Assert.assertNull(artifactMap.get("bar"));
    }

    @Test public void testWrongMessageType() throws IOException {
        artifactMap.put("foo", "SP", "IdP", SAML1ActionTestingSupport.buildResponse());
        prc.getInboundMessageContext().setMessage(SAML1ActionTestingSupport.buildArtifactRequest("foo"));
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.UNABLE_RESOLVE_ARTIFACT);
        Assert.assertTrue(prc.getOutboundMessageContext().getMessage().getAssertions().isEmpty());
        Assert.assertNull(artifactMap.get("foo"));
    }

    @Test public void testWrongSP() throws IOException {
        artifactMap.put("foo", "SP2", "IdP", SAML1ActionTestingSupport.buildAssertion());
        prc.getInboundMessageContext().setMessage(SAML1ActionTestingSupport.buildArtifactRequest("foo"));
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.UNABLE_RESOLVE_ARTIFACT);
        Assert.assertTrue(prc.getOutboundMessageContext().getMessage().getAssertions().isEmpty());
        Assert.assertNull(artifactMap.get("foo"));
    }

    @Test public void testWrongIdP() throws IOException {
        artifactMap.put("foo", "SP", "IdP2", SAML1ActionTestingSupport.buildAssertion());
        prc.getInboundMessageContext().setMessage(SAML1ActionTestingSupport.buildArtifactRequest("foo"));
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.UNABLE_RESOLVE_ARTIFACT);
        Assert.assertTrue(prc.getOutboundMessageContext().getMessage().getAssertions().isEmpty());
        Assert.assertNull(artifactMap.get("foo"));
    }

    @Test public void testOne() throws IOException {
        artifactMap.put("foo", "SP", "IdP", SAML1ActionTestingSupport.buildAssertion());
        prc.getInboundMessageContext().setMessage(SAML1ActionTestingSupport.buildArtifactRequest("foo"));
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        Assert.assertEquals(prc.getOutboundMessageContext().getMessage().getAssertions().size(), 1);
        Assert.assertNull(artifactMap.get("foo"));
    }

    @Test public void testMultiple() throws IOException {
        artifactMap.put("foo", "SP", "IdP", SAML1ActionTestingSupport.buildAssertion());
        artifactMap.put("bar", "SP", "IdP", SAML1ActionTestingSupport.buildAssertion());
        prc.getInboundMessageContext().setMessage(SAML1ActionTestingSupport.buildArtifactRequest("foo", "bar"));
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        Assert.assertEquals(prc.getOutboundMessageContext().getMessage().getAssertions().size(), 2);
        Assert.assertNull(artifactMap.get("foo"));
        Assert.assertNull(artifactMap.get("bar"));
    }

}