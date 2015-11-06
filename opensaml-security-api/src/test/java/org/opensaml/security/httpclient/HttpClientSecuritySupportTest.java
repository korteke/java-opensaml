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

package org.opensaml.security.httpclient;

import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_CLIENT_TLS_CREDENTIAL;
import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_CRITERIA_SET;
import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_HOSTNAME_VERIFIER;
import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_TLS_CIPHER_SUITES;
import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_TLS_PROTOCOLS;
import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_TRUST_ENGINE;

import java.io.File;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;

import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.StrictHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensaml.security.SecurityException;
import org.opensaml.security.trust.TrustEngine;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.security.x509.X509Support;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

/**
 *
 */
public class HttpClientSecuritySupportTest {
    
    private X509Certificate cert;
    
    private String certDERPath = "/data/certificate.der";
    
    
    @BeforeClass
    public void generatedTestData() throws NoSuchAlgorithmException, NoSuchProviderException, CertificateException, URISyntaxException {
        cert = X509Support.decodeCertificate(new File(HttpClientSecuritySupportTest.class.getResource(certDERPath).toURI()));
    }
    
    @Test(expectedExceptions=ConstraintViolationException.class)
    public void testMarshalSecurityParametersNullContext() {
        HttpClientSecurityParameters params = new HttpClientSecurityParameters();
        HttpClientSecuritySupport.marshalSecurityParameters(null, params, false);
    }
    
    @Test
    public void testMarshalNullSecurityParameters() {
        HttpClientContext context = HttpClientContext.create();
        
        HttpClientSecuritySupport.marshalSecurityParameters(context, null, false);
        
        Assert.assertNull(context.getCredentialsProvider());
        Assert.assertNull(context.getAttribute(CONTEXT_KEY_TRUST_ENGINE));
        Assert.assertNull(context.getAttribute(CONTEXT_KEY_CRITERIA_SET));
        Assert.assertNull(context.getAttribute(CONTEXT_KEY_TLS_PROTOCOLS));
        Assert.assertNull(context.getAttribute(CONTEXT_KEY_TLS_CIPHER_SUITES));
        Assert.assertNull(context.getAttribute(CONTEXT_KEY_CLIENT_TLS_CREDENTIAL));
        Assert.assertNull(context.getAttribute(CONTEXT_KEY_HOSTNAME_VERIFIER));
    }
    
    @Test
    public void testMarshalSecurityParametersEmptyContext() {
        HttpClientContext context = HttpClientContext.create();
        
        HttpClientSecurityParameters params = new HttpClientSecurityParameters();
        params.setCredentialsProvider(new BasicCredentialsProvider());
        params.setTLSTrustEngine(new MockTrustEngine());
        params.setTLSCriteriaSet(new CriteriaSet());
        params.setTLSProtocols(Lists.newArrayList("foo"));
        params.setTLSCipherSuites(Lists.newArrayList("foo"));
        params.setClientTLSCredential(new BasicX509Credential(cert));
        params.setHostnameVerifier(new StrictHostnameVerifier());
        
        HttpClientSecuritySupport.marshalSecurityParameters(context, params, false);
        
        Assert.assertSame(context.getCredentialsProvider(), params.getCredentialsProvider());
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_TRUST_ENGINE), params.getTLSTrustEngine());
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_CRITERIA_SET), params.getTLSCriteriaSet());
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_TLS_PROTOCOLS), params.getTLSProtocols());
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_TLS_CIPHER_SUITES), params.getTLSCipherSuites());
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_CLIENT_TLS_CREDENTIAL), params.getClientTLSCredential());
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_HOSTNAME_VERIFIER), params.getHostnameVerifier());
    }

    
    @Test
    public void testMarshalSecurityParametersWithReplacement() {
        HttpClientContext context = HttpClientContext.create();
        
        context.setCredentialsProvider(new BasicCredentialsProvider());
        context.setAttribute(CONTEXT_KEY_TRUST_ENGINE, new MockTrustEngine());
        context.setAttribute(CONTEXT_KEY_CRITERIA_SET, new CriteriaSet());
        context.setAttribute(CONTEXT_KEY_TLS_PROTOCOLS, Lists.newArrayList("foo"));
        context.setAttribute(CONTEXT_KEY_TLS_CIPHER_SUITES, Lists.newArrayList("foo"));
        context.setAttribute(CONTEXT_KEY_CLIENT_TLS_CREDENTIAL, new BasicX509Credential(cert));
        context.setAttribute(CONTEXT_KEY_HOSTNAME_VERIFIER, new StrictHostnameVerifier());
        
        HttpClientSecurityParameters params = new HttpClientSecurityParameters();
        params.setCredentialsProvider(new BasicCredentialsProvider());
        params.setTLSTrustEngine(new MockTrustEngine());
        params.setTLSCriteriaSet(new CriteriaSet());
        params.setTLSProtocols(Lists.newArrayList("foo"));
        params.setTLSCipherSuites(Lists.newArrayList("foo"));
        params.setClientTLSCredential(new BasicX509Credential(cert));
        params.setHostnameVerifier(new StrictHostnameVerifier());
        
        HttpClientSecuritySupport.marshalSecurityParameters(context, params, true);
        
        Assert.assertSame(context.getCredentialsProvider(), params.getCredentialsProvider());
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_TRUST_ENGINE), params.getTLSTrustEngine());
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_CRITERIA_SET), params.getTLSCriteriaSet());
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_TLS_PROTOCOLS), params.getTLSProtocols());
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_TLS_CIPHER_SUITES), params.getTLSCipherSuites());
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_CLIENT_TLS_CREDENTIAL), params.getClientTLSCredential());
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_HOSTNAME_VERIFIER), params.getHostnameVerifier());
    }

    @Test
    public void testMarshalSecurityParametersWithoutReplacement() {
        HttpClientContext context = HttpClientContext.create();
        
        CredentialsProvider credProvider = new BasicCredentialsProvider();
        TrustEngine<X509Credential> trustEngine = new MockTrustEngine();
        CriteriaSet criteriaSet = new CriteriaSet();
        List<String> protocols = Lists.newArrayList("foo");
        List<String> cipherSuites = Lists.newArrayList("foo");
        X509Credential clientTLSCred = new BasicX509Credential(cert);
        HostnameVerifier verifier = new StrictHostnameVerifier();
        
        context.setCredentialsProvider(credProvider);
        context.setAttribute(CONTEXT_KEY_TRUST_ENGINE, trustEngine);
        context.setAttribute(CONTEXT_KEY_CRITERIA_SET, criteriaSet);
        context.setAttribute(CONTEXT_KEY_TLS_PROTOCOLS, protocols);
        context.setAttribute(CONTEXT_KEY_TLS_CIPHER_SUITES, cipherSuites);
        context.setAttribute(CONTEXT_KEY_CLIENT_TLS_CREDENTIAL, clientTLSCred);
        context.setAttribute(CONTEXT_KEY_HOSTNAME_VERIFIER, verifier);
        
        HttpClientSecurityParameters params = new HttpClientSecurityParameters();
        params.setCredentialsProvider(new BasicCredentialsProvider());
        params.setTLSTrustEngine(new MockTrustEngine());
        params.setTLSCriteriaSet(new CriteriaSet());
        params.setTLSProtocols(Lists.newArrayList("foo"));
        params.setTLSCipherSuites(Lists.newArrayList("foo"));
        params.setClientTLSCredential(new BasicX509Credential(cert));
        params.setHostnameVerifier(new StrictHostnameVerifier());
        
        HttpClientSecuritySupport.marshalSecurityParameters(context, params, false);
        
        Assert.assertSame(context.getCredentialsProvider(), credProvider);
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_TRUST_ENGINE), trustEngine);
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_CRITERIA_SET), criteriaSet);
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_TLS_PROTOCOLS), protocols);
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_TLS_CIPHER_SUITES), cipherSuites);
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_CLIENT_TLS_CREDENTIAL), clientTLSCred);
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_HOSTNAME_VERIFIER), verifier);
    }
    
    @Test
    public void testSetContextValue() {
        String attribName = "MyAttrib";
        HttpClientContext context = null;
        
        // Empty context
        context = HttpClientContext.create();
        HttpClientSecuritySupport.setContextValue(context, attribName, "foo", false);
        Assert.assertEquals(context.getAttribute(attribName), "foo");
        
        // Empty context, null value
        context = HttpClientContext.create();
        HttpClientSecuritySupport.setContextValue(context, attribName, null, false);
        Assert.assertNull(context.getAttribute(attribName));
        
        // Don't replace existing
        context = HttpClientContext.create();
        context.setAttribute(attribName, "bar");
        HttpClientSecuritySupport.setContextValue(context, attribName, "foo", false);
        Assert.assertEquals(context.getAttribute(attribName), "bar");
        
        // Replace existing
        context = HttpClientContext.create();
        context.setAttribute(attribName, "bar");
        HttpClientSecuritySupport.setContextValue(context, attribName, "foo", true);
        Assert.assertEquals(context.getAttribute(attribName), "foo");
        
        // Don't replace because null value
        context = HttpClientContext.create();
        context.setAttribute(attribName, "bar");
        HttpClientSecuritySupport.setContextValue(context, attribName, null, true);
        Assert.assertEquals(context.getAttribute(attribName), "bar");
        
        
        try {
            HttpClientSecuritySupport.setContextValue(null, attribName, "foo", false);
            Assert.fail("Null context value");
        } catch (ConstraintViolationException e) {
            // Expected
        }
        
        try {
            context = HttpClientContext.create();
            HttpClientSecuritySupport.setContextValue(context, null, "foo", false);
            Assert.fail("Null attribute name");
        } catch (ConstraintViolationException e) {
            // Expected
        }
        
    }
    
    
    
    // Helpers
    
    public static class MockTrustEngine implements TrustEngine<X509Credential>  {
        public boolean validate(X509Credential token, CriteriaSet trustBasisCriteria) throws SecurityException {
            return false;
        }
    }

}
