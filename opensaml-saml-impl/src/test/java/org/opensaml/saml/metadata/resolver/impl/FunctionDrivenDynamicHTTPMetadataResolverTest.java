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

package org.opensaml.saml.metadata.resolver.impl;

import net.shibboleth.utilities.java.support.codec.StringDigester;
import net.shibboleth.utilities.java.support.codec.StringDigester.OutputFormat;
import net.shibboleth.utilities.java.support.httpclient.HttpClientBuilder;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.velocity.VelocityEngine;

import org.apache.http.client.HttpClient;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

public class FunctionDrivenDynamicHTTPMetadataResolverTest extends XMLObjectBaseTestCase {
    
    private FunctionDrivenDynamicHTTPMetadataResolver resolver;
    
    @AfterMethod
    public void tearDown() {
        if (resolver != null) {
            resolver.destroy();
        }
    }
    
    @Test
    public void testTemplateFromRepoDefaultContentTypes() throws Exception {
        // Repo should return 'text/xml', which is supported by default.
        String template = "http://svn.shibboleth.net/view/java-opensaml/trunk/opensaml-saml-impl/src/test/resources/data/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml?view=co";
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                true, 
                new StringDigester("SHA-1", OutputFormat.HEX_LOWER));
        
        HttpClient httpClient = new HttpClientBuilder().buildClient();
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClient);
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);
        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        Assert.assertNotNull(ed);
        Assert.assertEquals(ed.getEntityID(), entityID);
    }
   
    @Test
    public void testTemplateFromRepoWithExplicitContentType() throws Exception {
        // Explicitly request 'text/plain', and then configure it below to be supported.  Also test case-insensitivity.
        String template = "http://svn.shibboleth.net/view/java-opensaml/trunk/opensaml-saml-impl/src/test/resources/data/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml?content-type=text%2Fplain&view=co";
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                true, 
                new StringDigester("SHA-1", OutputFormat.HEX_LOWER));
        
        HttpClient httpClient = new HttpClientBuilder().buildClient();
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClient);
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);
        resolver.setSupportedContentTypes(Lists.newArrayList("application/samlmetadata+xml", "application/xml", "text/xml", "TEXT/PLAIN"));
        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        Assert.assertNotNull(ed);
        Assert.assertEquals(ed.getEntityID(), entityID);
    }
    
    @Test
    public void testTemplateFromRepoUnsupportedContentType() throws Exception {
        // Repo should return 'text/plain', which is not supported by default.
        String template = "http://svn.shibboleth.net/view/java-opensaml/trunk/opensaml-saml-impl/src/test/resources/data/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml?content-type=text%2Fplain&view=co";
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                true, 
                new StringDigester("SHA-1", OutputFormat.HEX_LOWER));
        
        HttpClient httpClient = new HttpClientBuilder().buildClient();
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClient);
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);
        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        Assert.assertNull(ed);
    }
    
    @Test
    public void testTemplateNonexistentDomain() throws Exception {
        // Unresolveable domain.  Should silently fail.
        String template = "http://bogus.example.org/metadata?entityID=${entityID}";
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                true);
        
        HttpClient httpClient = new HttpClientBuilder().buildClient();
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClient);
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);
        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        Assert.assertNull(ed);
    }
    
    @Test
    public void testTemplateNonexistentPath() throws Exception {
        // Bad path, resulting in 404.  Should silently fail.
        String template = "http://shibboleth.net/unittests/metadata?entityID=${entityID}";
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                true);
        
        HttpClient httpClient = new HttpClientBuilder().buildClient();
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClient);
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);
        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        Assert.assertNull(ed);
    }
    
    
}
