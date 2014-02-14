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

package org.opensaml.saml.common.binding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.criterion.EndpointCriterion;
import org.opensaml.saml.criterion.RoleDescriptorCriterion;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

/** Test for {@link DefaultEndpointResolver}. */
public class DefaultEndpointResolverTest extends XMLObjectBaseTestCase {

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
        endpointCrit = new EndpointCriterion(ep);
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
    public void testNoEndpoints() throws UnmarshallingException, ResolverException {
        final RoleDescriptorCriterion roleCrit =
                new RoleDescriptorCriterion(loadMetadata("/data/org/opensaml/saml/common/binding/SPNoEndpoints.xml"));
        final AssertionConsumerService ep = resolver.resolveSingle(new CriteriaSet(endpointCrit, roleCrit));
        Assert.assertNull(ep);
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