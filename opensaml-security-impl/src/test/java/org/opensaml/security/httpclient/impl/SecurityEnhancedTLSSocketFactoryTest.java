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

package org.opensaml.security.httpclient.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;

import org.apache.http.HttpHost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.StrictHostnameVerifier;
import org.apache.http.protocol.HttpContext;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.impl.StaticCredentialResolver;
import org.opensaml.security.httpclient.HttpClientSecurityConstants;
import org.opensaml.security.trust.impl.ExplicitKeyTrustEngine;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.security.x509.X509Support;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SecurityEnhancedTLSSocketFactoryTest {
    
    private static final String DATA_PATH = "/data/org/opensaml/security/x509/impl/";
    
    private SecurityEnhancedTLSSocketFactory securityEnhancedSocketFactory;
    
    private HttpContext httpContext;
    
    private String hostname = "foo.example.org";
    
    @BeforeMethod
    public void buildHttpContext() {
        httpContext = new HttpClientContext();
    }
    
    @Test
    public void testNonSSL() throws IOException {
        securityEnhancedSocketFactory = new SecurityEnhancedTLSSocketFactory(buildInnerSSLFactory(null, hostname), null);
        Socket socket = securityEnhancedSocketFactory.createSocket(httpContext);
        
        securityEnhancedSocketFactory.connectSocket(0, socket, new HttpHost(hostname, 80, "http"), null, null, httpContext);
        
        Assert.assertNull(httpContext.getAttribute(HttpClientSecurityConstants.CONTEXT_KEY_SERVER_TLS_CREDENTIAL_TRUSTED));
    }
    
    @Test
    public void testSuccessNoTrustEngine() throws IOException {
       X509Credential cred = getCredential("foo-1A1-good.crt");
       
       securityEnhancedSocketFactory = new SecurityEnhancedTLSSocketFactory(buildInnerSSLFactory(
               Collections.singletonList((Certificate)cred.getEntityCertificate()), hostname), null);
       Socket socket = securityEnhancedSocketFactory.createSocket(httpContext);
       
       securityEnhancedSocketFactory.connectSocket(0, socket, new HttpHost(hostname, 443, "https"), null, null, httpContext);
       
       Assert.assertNull(httpContext.getAttribute(HttpClientSecurityConstants.CONTEXT_KEY_SERVER_TLS_CREDENTIAL_TRUSTED));
    }
    
    @Test
    public void testSuccessWithEngine() throws IOException {
       X509Credential cred = getCredential("foo-1A1-good.crt");
       ExplicitKeyTrustEngine trustEngine = new ExplicitKeyTrustEngine(new StaticCredentialResolver(cred));
       httpContext.setAttribute(HttpClientSecurityConstants.CONTEXT_KEY_TRUST_ENGINE, trustEngine);
       
       securityEnhancedSocketFactory = new SecurityEnhancedTLSSocketFactory(buildInnerSSLFactory(
               Collections.singletonList((Certificate)cred.getEntityCertificate()), hostname), null);
       Socket socket = securityEnhancedSocketFactory.createSocket(httpContext);
       
       securityEnhancedSocketFactory.connectSocket(0, socket, new HttpHost(hostname, 443, "https"), null, null, httpContext);
       
       Assert.assertEquals(httpContext.getAttribute(HttpClientSecurityConstants.CONTEXT_KEY_SERVER_TLS_CREDENTIAL_TRUSTED), Boolean.TRUE);
    }
    
    @Test
    public void testSuccessWithEngineAndVerifier() throws IOException {
       X509Credential cred = getCredential("foo-1A1-good.crt");
       ExplicitKeyTrustEngine trustEngine = new ExplicitKeyTrustEngine(new StaticCredentialResolver(cred));
       httpContext.setAttribute(HttpClientSecurityConstants.CONTEXT_KEY_TRUST_ENGINE, trustEngine);
       
       securityEnhancedSocketFactory = new SecurityEnhancedTLSSocketFactory(buildInnerSSLFactory(
               Collections.singletonList((Certificate)cred.getEntityCertificate()), hostname), new StrictHostnameVerifier());
       Socket socket = securityEnhancedSocketFactory.createSocket(httpContext);
       
       securityEnhancedSocketFactory.connectSocket(0, socket, new HttpHost(hostname, 443, "https"), null, null, httpContext);
       
       Assert.assertEquals(httpContext.getAttribute(HttpClientSecurityConstants.CONTEXT_KEY_SERVER_TLS_CREDENTIAL_TRUSTED), Boolean.TRUE);
    }
    
    @Test(expectedExceptions=SSLPeerUnverifiedException.class)
    public void testFailUntrustedCert() throws IOException {
       X509Credential cred = getCredential("foo-1A1-good.crt");
       List<Credential> emptyCreds = new ArrayList<>();
       ExplicitKeyTrustEngine trustEngine = new ExplicitKeyTrustEngine(new StaticCredentialResolver(emptyCreds));
       httpContext.setAttribute(HttpClientSecurityConstants.CONTEXT_KEY_TRUST_ENGINE, trustEngine);
       
       securityEnhancedSocketFactory = new SecurityEnhancedTLSSocketFactory(buildInnerSSLFactory(
               Collections.singletonList((Certificate)cred.getEntityCertificate()), hostname), new StrictHostnameVerifier());
       Socket socket = securityEnhancedSocketFactory.createSocket(httpContext);
       
       try {
           securityEnhancedSocketFactory.connectSocket(0, socket, new HttpHost(hostname, 443, "https"), null, null, httpContext);
       } catch (Exception e) {
           Assert.assertEquals(httpContext.getAttribute(HttpClientSecurityConstants.CONTEXT_KEY_SERVER_TLS_CREDENTIAL_TRUSTED), Boolean.FALSE);
           throw e;
       }
    }
    
    @Test(expectedExceptions=SSLException.class)
    public void testFailBadHostname() throws IOException {
       X509Credential cred = getCredential("foo-1A1-good.crt");
       ExplicitKeyTrustEngine trustEngine = new ExplicitKeyTrustEngine(new StaticCredentialResolver(cred));
       httpContext.setAttribute(HttpClientSecurityConstants.CONTEXT_KEY_TRUST_ENGINE, trustEngine);
       
       securityEnhancedSocketFactory = new SecurityEnhancedTLSSocketFactory(buildInnerSSLFactory(
               Collections.singletonList((Certificate)cred.getEntityCertificate()), "bogus.example.com"), new StrictHostnameVerifier());
       Socket socket = securityEnhancedSocketFactory.createSocket(httpContext);
       
       
       try {
           securityEnhancedSocketFactory.connectSocket(0, socket, new HttpHost("bogus.example.com", 443, "https"), null, null, httpContext);
       } catch (Exception e) {
           Assert.assertEquals(httpContext.getAttribute(HttpClientSecurityConstants.CONTEXT_KEY_SERVER_TLS_CREDENTIAL_TRUSTED), Boolean.TRUE);
           throw e;
       }
    }

    @Test(expectedExceptions=SSLPeerUnverifiedException.class)
    public void testFailNoCertsInSession() throws IOException {
       X509Credential cred = getCredential("foo-1A1-good.crt");
       ExplicitKeyTrustEngine trustEngine = new ExplicitKeyTrustEngine(new StaticCredentialResolver(cred));
       httpContext.setAttribute(HttpClientSecurityConstants.CONTEXT_KEY_TRUST_ENGINE, trustEngine);
       
       // Pass an empty cert list, to simulate unlikely condition of SSLSession not having any peerCertificates
       securityEnhancedSocketFactory = new SecurityEnhancedTLSSocketFactory(buildInnerSSLFactory(
               new ArrayList<Certificate>(), hostname), new StrictHostnameVerifier());
       Socket socket = securityEnhancedSocketFactory.createSocket(httpContext);
       
       securityEnhancedSocketFactory.connectSocket(0, socket, new HttpHost(hostname, 443, "https"), null, null, httpContext);
    }
    
    // Helper methods
    
    private LayeredConnectionSocketFactory buildInnerSSLFactory(List<Certificate> certs, String host) {
        if (certs == null) {
            return new MockTLSSocketFactory();
        } else {
            return new MockTLSSocketFactory(certs, host);
        }
    }
    
    private BasicX509Credential getCredential(String entityCertFileName, String ... chainMembers) {
        X509Certificate entityCert = getCertificate(entityCertFileName);
        
        BasicX509Credential cred = new BasicX509Credential(entityCert);
        
        HashSet<X509Certificate> certChain = new HashSet<>();
        certChain.add(entityCert);
        
        for (String member: chainMembers) {
            certChain.add( getCertificate(member) );
        }
        
        cred.setEntityCertificateChain(certChain);
        
        return cred;
    }
    
    private X509Certificate getCertificate(String fileName) {
        try {
            InputStream ins = getInputStream(fileName);
            byte[] encoded = new byte[ins.available()];
            ins.read(encoded);
            return X509Support.decodeCertificates(encoded).iterator().next();
        } catch (Exception e) {
            Assert.fail("Could not create certificate from file: " + fileName + ": " + e.getMessage());
        }
        return null;
    }
    
    private InputStream getInputStream(String fileName) {
        return  this.getClass().getResourceAsStream(DATA_PATH + fileName);
    }

}
