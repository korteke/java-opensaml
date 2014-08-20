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

package org.opensaml.saml.common.binding.impl;

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
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.binding.EndpointResolver;
import org.opensaml.saml.common.binding.impl.DefaultEndpointResolver;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.criterion.BindingCriterion;
import org.opensaml.saml.criterion.EndpointCriterion;
import org.opensaml.saml.criterion.RoleDescriptorCriterion;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import com.google.common.collect.Lists;

/** Test for {@link DefaultEndpointResolver}. */
public class DefaultEndpointResolverTest extends XMLObjectBaseTestCase {

    private static final String LOCATION = "https://sp.example.org/ACS";
    private static final String LOCATION_POST = "https://sp.example.org/POST2";
    private static final String LOCATION_ART = "https://sp.example.org/Art2";
    
    private EndpointResolver<AssertionConsumerService> resolver;
    
    private EndpointCriterion<AssertionConsumerService> endpointCrit;
    
    @BeforeClass
    public void classSetUp() throws ComponentInitializationException {
        resolver = new DefaultEndpointResolver<>();
        resolver.initialize();
    }
    
    @BeforeMethod
    public void setUp() {
        final AssertionConsumerService ep = (AssertionConsumerService) builderFactory.getBuilderOrThrow(
                AssertionConsumerService.DEFAULT_ELEMENT_NAME).buildObject(
                        AssertionConsumerService.DEFAULT_ELEMENT_NAME);
        ep.setBinding(SAMLConstants.SAML2_POST_BINDING_URI);
        ep.setLocation(LOCATION);
        endpointCrit = new EndpointCriterion(ep, false);
    }

    @Test(expectedExceptions = ResolverException.class)
    public void testNoCriteria() throws ResolverException {
        resolver.resolveSingle(new CriteriaSet());
    }
    
    @Test
    public void testNoMetadata() throws ResolverException {
        final AssertionConsumerService ep = resolver.resolveSingle(new CriteriaSet(endpointCrit));
        Assert.assertNull(ep);
    }
    
    @Test
    public void testSignedRequest() throws ResolverException {
        final CriteriaSet crits = new CriteriaSet(new EndpointCriterion(endpointCrit.getEndpoint(), true));
        final AssertionConsumerService ep = resolver.resolveSingle(crits);
        Assert.assertNotNull(ep);
        Assert.assertSame(ep, endpointCrit.getEndpoint());
    }

    /** SP requests an endpoint but we don't support the binding. */
    @Test
    public void testSignedRequestBadBinding() throws ResolverException {
        final CriteriaSet crits = new CriteriaSet(new EndpointCriterion(endpointCrit.getEndpoint(), true),
                new BindingCriterion(Collections.<String>emptyList()));
        final AssertionConsumerService ep = resolver.resolveSingle(crits);
        Assert.assertNull(ep);
    }
    
    /** An SP with no endpoints in metadata. */
    @Test
    public void testNoEndpoints() throws UnmarshallingException, ResolverException {
        final RoleDescriptorCriterion roleCrit =
                new RoleDescriptorCriterion(loadMetadata("/data/org/opensaml/saml/common/binding/SPNoEndpoints.xml"));
        final AssertionConsumerService ep = resolver.resolveSingle(new CriteriaSet(endpointCrit, roleCrit));
        Assert.assertNull(ep);
    }

    /** No endpoint with the location requested. */
    @Test
    public void testBadLocation() throws UnmarshallingException, ResolverException {
        final RoleDescriptorCriterion roleCrit =
                new RoleDescriptorCriterion(loadMetadata("/data/org/opensaml/saml/common/binding/SPWithEndpoints.xml"));
        final CriteriaSet crits = new CriteriaSet(endpointCrit, roleCrit);
        final AssertionConsumerService ep = resolver.resolveSingle(crits);
        Assert.assertNull(ep);
    }

    /** No endpoint at a location with the right binding requested. */
    @Test
    public void testBadBinding() throws UnmarshallingException, ResolverException {
        endpointCrit.getEndpoint().setLocation(LOCATION_POST);
        endpointCrit.getEndpoint().setBinding(SAMLConstants.SAML2_SOAP11_BINDING_URI);
        final RoleDescriptorCriterion roleCrit =
                new RoleDescriptorCriterion(loadMetadata("/data/org/opensaml/saml/common/binding/SPWithEndpoints.xml"));
        final CriteriaSet crits = new CriteriaSet(endpointCrit, roleCrit);
        final AssertionConsumerService ep = resolver.resolveSingle(crits);
        Assert.assertNull(ep);
    }

    /** Endpoint matches but we don't support the binding. */
    @Test
    public void testUnsupportedBinding() throws UnmarshallingException, ResolverException {
        endpointCrit.getEndpoint().setLocation(LOCATION_POST);
        final RoleDescriptorCriterion roleCrit =
                new RoleDescriptorCriterion(loadMetadata("/data/org/opensaml/saml/common/binding/SPWithEndpoints.xml"));
        final CriteriaSet crits = new CriteriaSet(endpointCrit, roleCrit,
                new BindingCriterion(Collections.singletonList(SAMLConstants.SAML2_ARTIFACT_BINDING_URI)));
        final AssertionConsumerService ep = resolver.resolveSingle(crits);
        Assert.assertNull(ep);
    }
    
    /** No endpoint with a requested index. */
    @Test
    public void testBadIndex() throws UnmarshallingException, ResolverException {
        endpointCrit.getEndpoint().setLocation(null);
        endpointCrit.getEndpoint().setBinding(null);
        endpointCrit.getEndpoint().setIndex(5);
        final RoleDescriptorCriterion roleCrit =
                new RoleDescriptorCriterion(loadMetadata("/data/org/opensaml/saml/common/binding/SPWithEndpoints.xml"));
        final CriteriaSet crits = new CriteriaSet(endpointCrit, roleCrit);
        final AssertionConsumerService ep = resolver.resolveSingle(crits);
        Assert.assertNull(ep);
    }
    
    /** Requested location/binding are in metadata. */
    @Test
    public void testInMetadata() throws UnmarshallingException, ResolverException {
        endpointCrit.getEndpoint().setLocation(LOCATION_POST);
        final RoleDescriptorCriterion roleCrit =
                new RoleDescriptorCriterion(loadMetadata("/data/org/opensaml/saml/common/binding/SPWithEndpoints.xml"));
        final CriteriaSet crits = new CriteriaSet(endpointCrit, roleCrit);
        final AssertionConsumerService ep = resolver.resolveSingle(crits);
        Assert.assertNotNull(ep);
        Assert.assertEquals(ep.getBinding(), SAMLConstants.SAML2_POST_BINDING_URI);
        Assert.assertEquals(ep.getLocation(), LOCATION_POST);
        Assert.assertEquals(ep.getIndex(), Integer.valueOf(2));
    }

    /** Get the default endpoint. */
    @Test
    public void testDefault() throws UnmarshallingException, ResolverException {
        endpointCrit.getEndpoint().setLocation(null);
        endpointCrit.getEndpoint().setBinding(null);
        final RoleDescriptorCriterion roleCrit =
                new RoleDescriptorCriterion(loadMetadata("/data/org/opensaml/saml/common/binding/SPWithEndpoints.xml"));
        final CriteriaSet crits = new CriteriaSet(endpointCrit, roleCrit);
        final AssertionConsumerService ep = resolver.resolveSingle(crits);
        Assert.assertNotNull(ep);
        Assert.assertEquals(ep.getBinding(), SAMLConstants.SAML2_ARTIFACT_BINDING_URI);
        Assert.assertEquals(ep.getLocation(), LOCATION_ART);
        Assert.assertEquals(ep.getIndex(), Integer.valueOf(4));
    }

    /** Get the default endpoint with a binding. */
    @Test
    public void testDefaultForBinding() throws UnmarshallingException, ResolverException {
        endpointCrit.getEndpoint().setLocation(null);
        endpointCrit.getEndpoint().setBinding(null);
        final RoleDescriptorCriterion roleCrit =
                new RoleDescriptorCriterion(loadMetadata("/data/org/opensaml/saml/common/binding/SPWithEndpoints.xml"));
        final CriteriaSet crits = new CriteriaSet(endpointCrit, roleCrit,
                new BindingCriterion(Collections.singletonList(SAMLConstants.SAML2_POST_BINDING_URI)));
        final AssertionConsumerService ep = resolver.resolveSingle(crits);
        Assert.assertNotNull(ep);
        Assert.assertEquals(ep.getBinding(), SAMLConstants.SAML2_POST_BINDING_URI);
        Assert.assertEquals(ep.getLocation(), LOCATION_POST.replace("POST2", "POST"));
        Assert.assertEquals(ep.getIndex(), Integer.valueOf(1));
    }
    
    /** All endpoints of the right type. */
    @Test
    public void testMultiple() throws UnmarshallingException, ResolverException {
        endpointCrit.getEndpoint().setLocation(null);
        endpointCrit.getEndpoint().setBinding(null);
        final RoleDescriptorCriterion roleCrit =
                new RoleDescriptorCriterion(loadMetadata("/data/org/opensaml/saml/common/binding/SPWithEndpoints.xml"));
        final CriteriaSet crits = new CriteriaSet(endpointCrit, roleCrit);
        final List<AssertionConsumerService> eps = Lists.newArrayList(resolver.resolve(crits));
        Assert.assertEquals(eps.size(), 4);
    }

    /** All endpoints of the right type and binding. */
    @Test
    public void testMultipleWithBinding() throws UnmarshallingException, ResolverException {
        endpointCrit.getEndpoint().setLocation(null);
        final RoleDescriptorCriterion roleCrit =
                new RoleDescriptorCriterion(loadMetadata("/data/org/opensaml/saml/common/binding/SPWithEndpoints.xml"));
        final CriteriaSet crits = new CriteriaSet(endpointCrit, roleCrit);
        final List<AssertionConsumerService> eps = Lists.newArrayList(resolver.resolve(crits));
        Assert.assertEquals(eps.size(), 2);
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