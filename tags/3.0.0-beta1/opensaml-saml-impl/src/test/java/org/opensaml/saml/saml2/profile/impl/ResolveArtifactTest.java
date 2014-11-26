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

import java.io.IOException;

import org.opensaml.profile.RequestContextBuilder;
import org.opensaml.profile.action.ActionTestingSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.saml.common.binding.artifact.SAMLArtifactMap;
import org.opensaml.saml.common.binding.artifact.impl.BasicSAMLArtifactMap;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.profile.SAMLEventIds;
import org.opensaml.saml.saml2.core.ArtifactResolve;
import org.opensaml.saml.saml2.core.ArtifactResponse;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.profile.SAML2ActionTestingSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Function;

/** {@link ResolveArtifact} unit test. */
public class ResolveArtifactTest extends OpenSAMLInitBaseTestCase {

    private SAMLArtifactMap artifactMap;
    
    private ProfileRequestContext<ArtifactResolve,ArtifactResponse> prc;
    
    private ResolveArtifact action;
    
    @BeforeMethod public void setUp() throws ComponentInitializationException {
        prc = new RequestContextBuilder().setOutboundMessage(
                SAML2ActionTestingSupport.buildArtifactResponse()).buildProfileRequestContext();
        prc.getInboundMessageContext().getSubcontext(SAMLPeerEntityContext.class, true).setEntityId("SP");
        
        artifactMap = new BasicSAMLArtifactMap();
        artifactMap.initialize();
        
        action = new ResolveArtifact();
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
        prc.getInboundMessageContext().setMessage(SAML2ActionTestingSupport.buildArtifactResolve(null));
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }
    
    @Test public void testNoResponse() {
        prc.getInboundMessageContext().setMessage(SAML2ActionTestingSupport.buildArtifactResolve("foo"));
        prc.getOutboundMessageContext().setMessage(null);
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }
    
    @Test public void testMissingArtifacts() throws IOException {
        artifactMap.put("bar", "SP", "IdP", SAML2ActionTestingSupport.buildResponse());
        prc.getInboundMessageContext().setMessage(SAML2ActionTestingSupport.buildArtifactResolve("foo"));
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.UNABLE_RESOLVE_ARTIFACT);
        Assert.assertNull(prc.getOutboundMessageContext().getMessage().getMessage());
    }

    @Test public void testWrongSP() throws IOException {
        artifactMap.put("foo", "SP2", "IdP", SAML2ActionTestingSupport.buildResponse());
        prc.getInboundMessageContext().setMessage(SAML2ActionTestingSupport.buildArtifactResolve("foo"));
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.UNABLE_RESOLVE_ARTIFACT);
        Assert.assertNull(prc.getOutboundMessageContext().getMessage().getMessage());
        Assert.assertNull(artifactMap.get("foo"));
    }

    @Test public void testWrongIdP() throws IOException {
        artifactMap.put("foo", "SP", "IdP2", SAML2ActionTestingSupport.buildResponse());
        prc.getInboundMessageContext().setMessage(SAML2ActionTestingSupport.buildArtifactResolve("foo"));
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.UNABLE_RESOLVE_ARTIFACT);
        Assert.assertNull(prc.getOutboundMessageContext().getMessage().getMessage());
        Assert.assertNull(artifactMap.get("foo"));
    }

    @Test public void testOne() throws IOException {
        artifactMap.put("foo", "SP", "IdP", SAML2ActionTestingSupport.buildResponse());
        prc.getInboundMessageContext().setMessage(SAML2ActionTestingSupport.buildArtifactResolve("foo"));
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        Assert.assertTrue(prc.getOutboundMessageContext().getMessage().getMessage() instanceof Response);
        Assert.assertNull(artifactMap.get("foo"));
    }

}