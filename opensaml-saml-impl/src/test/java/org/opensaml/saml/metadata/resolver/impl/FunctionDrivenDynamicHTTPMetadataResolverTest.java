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

import java.io.File;
import java.net.URISyntaxException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import net.shibboleth.utilities.java.support.codec.StringDigester;
import net.shibboleth.utilities.java.support.codec.StringDigester.OutputFormat;
import net.shibboleth.utilities.java.support.httpclient.HttpClientBuilder;
import net.shibboleth.utilities.java.support.httpclient.HttpClientSupport;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.velocity.VelocityEngine;

import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.security.credential.impl.StaticCredentialResolver;
import org.opensaml.security.httpclient.impl.SecurityEnhancedTLSSocketFactory;
import org.opensaml.security.trust.TrustEngine;
import org.opensaml.security.trust.impl.ExplicitKeyTrustEngine;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.security.x509.PKIXValidationInformation;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.security.x509.X509Support;
import org.opensaml.security.x509.impl.BasicPKIXValidationInformation;
import org.opensaml.security.x509.impl.BasicX509CredentialNameEvaluator;
import org.opensaml.security.x509.impl.CertPathPKIXTrustEvaluator;
import org.opensaml.security.x509.impl.PKIXX509CredentialTrustEngine;
import org.opensaml.security.x509.impl.StaticPKIXValidationInformationResolver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FunctionDrivenDynamicHTTPMetadataResolverTest extends XMLObjectBaseTestCase {
    
    private static final String DATA_PATH = "/org/opensaml/saml/metadata/resolver/impl/";
    
    private FunctionDrivenDynamicHTTPMetadataResolver resolver;
    
    private HttpClientBuilder httpClientBuilder;
    
    @BeforeMethod
    public void setUp() {
        httpClientBuilder = new HttpClientBuilder();
    }
    
    @AfterMethod
    public void tearDown() {
        if (resolver != null) {
            resolver.destroy();
        }
    }
    
    @Test
    public void testTemplateFromRepoDefaultContentTypes() throws Exception {
        // Repo should return 'text/xml', which is supported by default.
        String template = "http://svn.shibboleth.net/view/java-opensaml/trunk/opensaml-saml-impl/src/test/resources/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml?view=co";
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                true, 
                new StringDigester("SHA-1", OutputFormat.HEX_LOWER));
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
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
        String template = "http://svn.shibboleth.net/view/java-opensaml/trunk/opensaml-saml-impl/src/test/resources/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml?content-type=text%2Fplain&view=co";
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                true, 
                new StringDigester("SHA-1", OutputFormat.HEX_LOWER));
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);
        resolver.setSupportedContentTypes(Arrays.asList("application/samlmetadata+xml", "application/xml", "text/xml", "TEXT/PLAIN"));
        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        Assert.assertNotNull(ed);
        Assert.assertEquals(ed.getEntityID(), entityID);
    }
    
    @Test
    public void testTemplateFromRepoUnsupportedContentType() throws Exception {
        // Repo should return 'text/plain', which is not supported by default.
        String template = "http://svn.shibboleth.net/view/java-opensaml/trunk/opensaml-saml-impl/src/test/resources/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml?content-type=text%2Fplain&view=co";
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                true, 
                new StringDigester("SHA-1", OutputFormat.HEX_LOWER));
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
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
        
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                true);
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
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
        
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                true);
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);
        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        Assert.assertNull(ed);
    }
    
    @Test
    public void testWellKnownLocation() throws Exception {
        //TODO update with permanent test target, if there is a better one.
        String entityID = "https://issues.shibboleth.net/shibboleth";
        
        HTTPEntityIDRequestURLBuilder requestURLBuilder = new HTTPEntityIDRequestURLBuilder();
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
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
    public void testMDQ() throws Exception {
        String baseURL = "http://shibboleth.net:9000";
        String entityID = "https://foo1.example.org/idp/shibboleth";
        
        MetadataQueryProtocolRequestURLBuilder requestURLBuilder = new MetadataQueryProtocolRequestURLBuilder(baseURL);
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
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
    public void testTrustEngineSocketFactoryNoHTTPSNoTrustEngine() throws Exception {
        String template = "http://svn.shibboleth.net/view/java-opensaml/trunk/opensaml-saml-impl/src/test/resources/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml?view=co";
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                true, 
                new StringDigester("SHA-1", OutputFormat.HEX_LOWER));
        
        httpClientBuilder.setTLSSocketFactory(buildTrustEngineSocketFactory());
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
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
    public void testTrustEngineSocketFactoryNoHTTPSWithTrustEngine() throws Exception  {
        String template = "http://svn.shibboleth.net/view/java-opensaml/trunk/opensaml-saml-impl/src/test/resources/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml?view=co";
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                true, 
                new StringDigester("SHA-1", OutputFormat.HEX_LOWER));
        
        httpClientBuilder.setTLSSocketFactory(buildTrustEngineSocketFactory());
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);
        resolver.setTLSTrustEngine(buildExplicitKeyTrustEngine("svn-entity.crt"));
        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        Assert.assertNotNull(ed);
        Assert.assertEquals(ed.getEntityID(), entityID);
    }
    
    @Test
    public void testHTTPSNoTrustEngine() throws Exception  {
        String template = "https://svn.shibboleth.net/java-opensaml/trunk/opensaml-saml-impl/src/test/resources/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml";
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                true, 
                new StringDigester("SHA-1", OutputFormat.HEX_LOWER));
        
        httpClientBuilder.setTLSSocketFactory(buildTrustEngineSocketFactory());
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
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
    public void testHTTPSTrustEngineExplicitKey() throws Exception  {
        String template = "https://svn.shibboleth.net/java-opensaml/trunk/opensaml-saml-impl/src/test/resources/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml";
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                true, 
                new StringDigester("SHA-1", OutputFormat.HEX_LOWER));
        
        httpClientBuilder.setTLSSocketFactory(buildTrustEngineSocketFactory());
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);
        resolver.setTLSTrustEngine(buildExplicitKeyTrustEngine("svn-entity.crt"));
        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        Assert.assertNotNull(ed);
        Assert.assertEquals(ed.getEntityID(), entityID);
    }
    
    @Test
    public void testHTTPSTrustEngineInvalidKey()  throws Exception {
        String template = "https://svn.shibboleth.net/java-opensaml/trunk/opensaml-saml-impl/src/test/resources/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml";
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                true, 
                new StringDigester("SHA-1", OutputFormat.HEX_LOWER));
        
        httpClientBuilder.setTLSSocketFactory(buildTrustEngineSocketFactory());
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);
        resolver.setTLSTrustEngine(buildExplicitKeyTrustEngine("badKey.crt"));
        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        Assert.assertNull(ed);
    }
    
    @Test
    public void testHTTPSTrustEngineValidPKIX() throws Exception  {
        String template = "https://svn.shibboleth.net/java-opensaml/trunk/opensaml-saml-impl/src/test/resources/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml";
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                true, 
                new StringDigester("SHA-1", OutputFormat.HEX_LOWER));
        
        httpClientBuilder.setTLSSocketFactory(buildTrustEngineSocketFactory());
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);
        resolver.setTLSTrustEngine(buildPKIXTrustEngine("svn-rootCA.crt", null, false));
        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        Assert.assertNotNull(ed);
        Assert.assertEquals(ed.getEntityID(), entityID);
    }
    
    @Test
    public void testHTTPSTrustEngineValidPKIXExplicitName() throws Exception  {
        String template = "https://svn.shibboleth.net/java-opensaml/trunk/opensaml-saml-impl/src/test/resources/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml";
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                true, 
                new StringDigester("SHA-1", OutputFormat.HEX_LOWER));
        
        httpClientBuilder.setTLSSocketFactory(buildTrustEngineSocketFactory());
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);
        resolver.setTLSTrustEngine(buildPKIXTrustEngine("svn-rootCA.crt", "*.shibboleth.net", true));
        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        Assert.assertNotNull(ed);
        Assert.assertEquals(ed.getEntityID(), entityID);
    }
    
    @Test
    public void testHTTPSTrustEngineInvalidPKIX() throws Exception  {
        String template = "https://svn.shibboleth.net/java-opensaml/trunk/opensaml-saml-impl/src/test/resources/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml";
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                true, 
                new StringDigester("SHA-1", OutputFormat.HEX_LOWER));
        
        httpClientBuilder.setTLSSocketFactory(buildTrustEngineSocketFactory());
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);
        resolver.setTLSTrustEngine(buildPKIXTrustEngine("badCA.crt", null, false));
        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        Assert.assertNull(ed);
    }
    
    @Test
    public void testHTTPSTrustEngineValidPKIXInvalidName() throws Exception  {
        String template = "https://svn.shibboleth.net/java-opensaml/trunk/opensaml-saml-impl/src/test/resources/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml";
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                true, 
                new StringDigester("SHA-1", OutputFormat.HEX_LOWER));
        
        httpClientBuilder.setTLSSocketFactory(buildTrustEngineSocketFactory());
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);
        resolver.setTLSTrustEngine(buildPKIXTrustEngine("svn-rootCA.crt", "foobar.shibboleth.net", true));
        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        Assert.assertNull(ed);
    }
    
    @Test
    public void testHTTPSTrustEngineWrongSocketFactory() throws Exception  {
        String template = "https://svn.shibboleth.net/java-opensaml/trunk/opensaml-saml-impl/src/test/resources/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml";
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                true, 
                new StringDigester("SHA-1", OutputFormat.HEX_LOWER));
        
        // Trust engine set, but appropriate socket factory not set
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);
        resolver.setTLSTrustEngine(buildExplicitKeyTrustEngine("svn-entity.crt"));
        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        Assert.assertNull(ed);
    }
    
    
    
    // Helpers
    
    private LayeredConnectionSocketFactory buildTrustEngineSocketFactory() {
        return new SecurityEnhancedTLSSocketFactory(
                HttpClientSupport.buildNoTrustTLSSocketFactory(),
                SSLConnectionSocketFactory.STRICT_HOSTNAME_VERIFIER
                );
    }

    private TrustEngine<? super X509Credential> buildExplicitKeyTrustEngine(String cert) throws URISyntaxException, CertificateException {
        File certFile = new File(this.getClass().getResource(DATA_PATH + cert).toURI());
        X509Certificate entityCert = X509Support.decodeCertificate(certFile);
        X509Credential entityCredential = new BasicX509Credential(entityCert);
        return new ExplicitKeyTrustEngine(new StaticCredentialResolver(entityCredential));
    }
    
    private TrustEngine<? super X509Credential> buildPKIXTrustEngine(String cert, String name, boolean nameCheckEnabled) throws URISyntaxException, CertificateException {
        File certFile = new File(this.getClass().getResource(DATA_PATH + cert).toURI());
        X509Certificate rootCert = X509Support.decodeCertificate(certFile);
        PKIXValidationInformation info = new BasicPKIXValidationInformation(Collections.singletonList(rootCert), null, 5);
        Set<String> trustedNames = (Set<String>) (name != null ? Collections.singleton(name) : Collections.emptySet());
        StaticPKIXValidationInformationResolver resolver = new StaticPKIXValidationInformationResolver(Collections.singletonList(info), trustedNames);
        return new PKIXX509CredentialTrustEngine(resolver, 
                new CertPathPKIXTrustEvaluator(),
                (nameCheckEnabled ? new BasicX509CredentialNameEvaluator() : null));
    }
    
}
