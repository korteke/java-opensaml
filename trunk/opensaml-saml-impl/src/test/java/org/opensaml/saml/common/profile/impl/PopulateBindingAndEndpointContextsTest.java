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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.profile.ProfileException;
import org.opensaml.profile.RequestContextBuilder;
import org.opensaml.profile.action.ActionTestingSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.binding.BindingDescriptor;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.profile.SAMLEventIds;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.profile.SAML2ActionTestingSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;

/** Unit test for {@link PopulateBindingAndEndpointContexts}. */
public class PopulateBindingAndEndpointContextsTest extends XMLObjectBaseTestCase {

    private static final String RELAY_STATE = "foo";
    private static final String LOCATION = "https://sp.example.org/ACS";
    private static final String LOCATION_POST = "https://sp.example.org/POST2";
    private static final String LOCATION_ART = "https://sp.example.org/Art2";

    private ProfileRequestContext prc;
    
    private PopulateBindingAndEndpointContexts action;
    
    @BeforeMethod
    public void setUp() throws ComponentInitializationException {
        final AuthnRequest request = SAML2ActionTestingSupport.buildAuthnRequest();
        request.setAssertionConsumerServiceURL(LOCATION_POST);
        request.setProtocolBinding(SAMLConstants.SAML2_POST_BINDING_URI);
        prc = new RequestContextBuilder().setInboundMessage(request).buildProfileRequestContext();
        prc.getInboundMessageContext().getSubcontext(SAMLBindingContext.class, true).setRelayState(RELAY_STATE);
        
        action = new PopulateBindingAndEndpointContexts();
        action.setId("test");
        final List<BindingDescriptor> bindings = Lists.newArrayList();
        bindings.add(new BindingDescriptor(SAMLConstants.SAML2_POST_BINDING_URI));
        action.setBindings(bindings);
        action.initialize();
    }

    @Test(expectedExceptions = ComponentInitializationException.class)
    public void testBadEndpointType() throws ComponentInitializationException {
        PopulateBindingAndEndpointContexts badaction = new PopulateBindingAndEndpointContexts();
        badaction.setEndpointType(AuthnRequest.DEFAULT_ELEMENT_NAME);
        badaction.initialize();
    }
    
    @Test
    public void testNoOutboundContext() throws ProfileException {
        prc.setOutboundMessageContext(null);
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }

    @Test
    public void testNoBindings() throws ProfileException {
        final BindingDescriptor binding = new BindingDescriptor(SAMLConstants.SAML2_POST_BINDING_URI);
        binding.setActivationCondition(Predicates.<ProfileRequestContext>alwaysFalse());
        PopulateBindingAndEndpointContexts badaction = new PopulateBindingAndEndpointContexts();
        badaction.setId("test");
        badaction.setBindings(Collections.singletonList(binding));
        
        badaction.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.ENDPOINT_RESOLUTION_FAILED);
    }
    
    @Test
    public void testNoMetadata() throws ProfileException {
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.ENDPOINT_RESOLUTION_FAILED);
    }

    /** An SP with no endpoints in metadata. */
    @Test
    public void testNoEndpoints() throws UnmarshallingException, ProfileException {
        final RoleDescriptor role = loadMetadata("/data/org/opensaml/saml/common/binding/SPNoEndpoints.xml");
        final SAMLMetadataContext mdCtx = new SAMLMetadataContext();
        mdCtx.setRoleDescriptor(role);
        prc.getOutboundMessageContext().getSubcontext(SAMLPeerEntityContext.class, true).addSubcontext(mdCtx);
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.ENDPOINT_RESOLUTION_FAILED);
    }

    /** No endpoint with the location requested. */
    @Test
    public void testBadLocation() throws UnmarshallingException, ProfileException {
        final RoleDescriptor role = loadMetadata("/data/org/opensaml/saml/common/binding/SPWithEndpoints.xml");
        final SAMLMetadataContext mdCtx = new SAMLMetadataContext();
        mdCtx.setRoleDescriptor(role);
        prc.getOutboundMessageContext().getSubcontext(SAMLPeerEntityContext.class, true).addSubcontext(mdCtx);
        
        ((AuthnRequest) prc.getInboundMessageContext().getMessage()).setAssertionConsumerServiceURL(LOCATION);
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.ENDPOINT_RESOLUTION_FAILED);
    }

    /** No endpoint at a location with the right binding requested. */
    @Test
    public void testBadBinding() throws UnmarshallingException, ProfileException {
        final RoleDescriptor role = loadMetadata("/data/org/opensaml/saml/common/binding/SPWithEndpoints.xml");
        final SAMLMetadataContext mdCtx = new SAMLMetadataContext();
        mdCtx.setRoleDescriptor(role);
        prc.getOutboundMessageContext().getSubcontext(SAMLPeerEntityContext.class, true).addSubcontext(mdCtx);

        ((AuthnRequest) prc.getInboundMessageContext().getMessage()).setProtocolBinding(SAMLConstants.SAML2_SOAP11_BINDING_URI);
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.ENDPOINT_RESOLUTION_FAILED);
    }

    /** Endpoint matches but we don't support the binding. */
    @Test
    public void testUnsupportedBinding() throws UnmarshallingException, ProfileException {
        final RoleDescriptor role = loadMetadata("/data/org/opensaml/saml/common/binding/SPWithEndpoints.xml");
        final SAMLMetadataContext mdCtx = new SAMLMetadataContext();
        mdCtx.setRoleDescriptor(role);
        prc.getOutboundMessageContext().getSubcontext(SAMLPeerEntityContext.class, true).addSubcontext(mdCtx);
        
        ((AuthnRequest) prc.getInboundMessageContext().getMessage()).setAssertionConsumerServiceURL(LOCATION_ART);
        ((AuthnRequest) prc.getInboundMessageContext().getMessage()).setProtocolBinding(SAMLConstants.SAML2_ARTIFACT_BINDING_URI);
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.ENDPOINT_RESOLUTION_FAILED);
    }
    
    /** No endpoint with a requested index. */
    @Test
    public void testBadIndex() throws UnmarshallingException, ProfileException {
        final RoleDescriptor role = loadMetadata("/data/org/opensaml/saml/common/binding/SPWithEndpoints.xml");
        final SAMLMetadataContext mdCtx = new SAMLMetadataContext();
        mdCtx.setRoleDescriptor(role);
        prc.getOutboundMessageContext().getSubcontext(SAMLPeerEntityContext.class, true).addSubcontext(mdCtx);

        ((AuthnRequest) prc.getInboundMessageContext().getMessage()).setAssertionConsumerServiceIndex(10);
        ((AuthnRequest) prc.getInboundMessageContext().getMessage()).setAssertionConsumerServiceURL(null);
        ((AuthnRequest) prc.getInboundMessageContext().getMessage()).setProtocolBinding(null);

        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.ENDPOINT_RESOLUTION_FAILED);
    }
    
    /** Requested location/binding are in metadata. */
    @Test
    public void testInMetadata() throws UnmarshallingException, ProfileException {
        final RoleDescriptor role = loadMetadata("/data/org/opensaml/saml/common/binding/SPWithEndpoints.xml");
        final SAMLMetadataContext mdCtx = new SAMLMetadataContext();
        mdCtx.setRoleDescriptor(role);
        prc.getOutboundMessageContext().getSubcontext(SAMLPeerEntityContext.class, true).addSubcontext(mdCtx);
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        final SAMLBindingContext bindingCtx = prc.getOutboundMessageContext().getSubcontext(SAMLBindingContext.class, false);
        Assert.assertNotNull(bindingCtx);
        Assert.assertEquals(bindingCtx.getRelayState(), RELAY_STATE);
        Assert.assertEquals(bindingCtx.getBindingUri(), SAMLConstants.SAML2_POST_BINDING_URI);
        
        final SAMLEndpointContext epCtx = prc.getOutboundMessageContext().getSubcontext(
                SAMLPeerEntityContext.class, false).getSubcontext(SAMLEndpointContext.class, false);
        Assert.assertNotNull(epCtx);
        Assert.assertNotNull(epCtx.getEndpoint());
        Assert.assertEquals(epCtx.getEndpoint().getBinding(), SAMLConstants.SAML2_POST_BINDING_URI);
        Assert.assertEquals(epCtx.getEndpoint().getLocation(), LOCATION_POST);
    }

    /** Requested index is in metadata. */
    @Test
    public void testIndexInMetadata() throws UnmarshallingException, ProfileException {
        final RoleDescriptor role = loadMetadata("/data/org/opensaml/saml/common/binding/SPWithEndpoints.xml");
        final SAMLMetadataContext mdCtx = new SAMLMetadataContext();
        mdCtx.setRoleDescriptor(role);
        prc.getOutboundMessageContext().getSubcontext(SAMLPeerEntityContext.class, true).addSubcontext(mdCtx);

        ((AuthnRequest) prc.getInboundMessageContext().getMessage()).setAssertionConsumerServiceIndex(2);
        ((AuthnRequest) prc.getInboundMessageContext().getMessage()).setAssertionConsumerServiceURL(null);
        ((AuthnRequest) prc.getInboundMessageContext().getMessage()).setProtocolBinding(null);

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        final SAMLBindingContext bindingCtx = prc.getOutboundMessageContext().getSubcontext(SAMLBindingContext.class, false);
        Assert.assertNotNull(bindingCtx);
        Assert.assertEquals(bindingCtx.getRelayState(), RELAY_STATE);
        Assert.assertEquals(bindingCtx.getBindingUri(), SAMLConstants.SAML2_POST_BINDING_URI);
        
        final SAMLEndpointContext epCtx = prc.getOutboundMessageContext().getSubcontext(
                SAMLPeerEntityContext.class, false).getSubcontext(SAMLEndpointContext.class, false);
        Assert.assertNotNull(epCtx);
        Assert.assertNotNull(epCtx.getEndpoint());
        Assert.assertEquals(epCtx.getEndpoint().getBinding(), SAMLConstants.SAML2_POST_BINDING_URI);
        Assert.assertEquals(epCtx.getEndpoint().getLocation(), LOCATION_POST);
    }

    /** No endpoint with a requested index. */
    @Test
    public void testIndexUnsupportedBinding() throws UnmarshallingException, ProfileException {
        final RoleDescriptor role = loadMetadata("/data/org/opensaml/saml/common/binding/SPWithEndpoints.xml");
        final SAMLMetadataContext mdCtx = new SAMLMetadataContext();
        mdCtx.setRoleDescriptor(role);
        prc.getOutboundMessageContext().getSubcontext(SAMLPeerEntityContext.class, true).addSubcontext(mdCtx);

        ((AuthnRequest) prc.getInboundMessageContext().getMessage()).setAssertionConsumerServiceIndex(3);
        ((AuthnRequest) prc.getInboundMessageContext().getMessage()).setAssertionConsumerServiceURL(null);
        ((AuthnRequest) prc.getInboundMessageContext().getMessage()).setProtocolBinding(null);

        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.ENDPOINT_RESOLUTION_FAILED);
    }
    
    /** Get the default endpoint. */
    @Test
    public void testDefault() throws UnmarshallingException, ProfileException {
        final RoleDescriptor role = loadMetadata("/data/org/opensaml/saml/common/binding/SPWithEndpoints.xml");
        final SAMLMetadataContext mdCtx = new SAMLMetadataContext();
        mdCtx.setRoleDescriptor(role);
        prc.getOutboundMessageContext().getSubcontext(SAMLPeerEntityContext.class, true).addSubcontext(mdCtx);

        ((AuthnRequest) prc.getInboundMessageContext().getMessage()).setAssertionConsumerServiceURL(null);
        ((AuthnRequest) prc.getInboundMessageContext().getMessage()).setProtocolBinding(null);
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        final SAMLBindingContext bindingCtx = prc.getOutboundMessageContext().getSubcontext(SAMLBindingContext.class, false);
        Assert.assertNotNull(bindingCtx);
        Assert.assertEquals(bindingCtx.getRelayState(), RELAY_STATE);
        Assert.assertEquals(bindingCtx.getBindingUri(), SAMLConstants.SAML2_POST_BINDING_URI);
        
        final SAMLEndpointContext epCtx = prc.getOutboundMessageContext().getSubcontext(
                SAMLPeerEntityContext.class, false).getSubcontext(SAMLEndpointContext.class, false);
        Assert.assertNotNull(epCtx);
        Assert.assertNotNull(epCtx.getEndpoint());
        Assert.assertEquals(epCtx.getEndpoint().getBinding(), SAMLConstants.SAML2_POST_BINDING_URI);
        Assert.assertEquals(epCtx.getEndpoint().getLocation(), LOCATION_POST.replace("POST2", "POST"));
    }
    
    @Nonnull private SPSSODescriptor loadMetadata(@Nonnull @NotEmpty final String path) throws UnmarshallingException {
        
        try {
            final URL url = getClass().getResource(path);
            Document doc = parserPool.parse(new FileInputStream(new File(url.toURI())));
            final Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(doc.getDocumentElement());
            return (SPSSODescriptor) unmarshaller.unmarshall(doc.getDocumentElement());
        } catch (FileNotFoundException | XMLParserException | URISyntaxException e) {
            throw new UnmarshallingException(e);
        }
    }
    
}