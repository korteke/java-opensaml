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

package org.opensaml.xmlsec.keyinfo.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.security.SecurityException;
import org.opensaml.security.SecurityHelper;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoCriterion;
import org.opensaml.xmlsec.keyinfo.impl.BasicProviderKeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.KeyInfoProvider;
import org.opensaml.xmlsec.keyinfo.impl.provider.InlineX509DataProvider;
import org.opensaml.xmlsec.keyinfo.impl.provider.RSAKeyValueProvider;
import org.opensaml.xmlsec.signature.KeyInfo;


/**
 * Test resolution of credentials from X509Data child of KeyInfo,
 * where the X509Data contains various identifiers for the entity cert
 * within a cert chain.
 */
public class CertChainX509DataTest extends XMLObjectBaseTestCase {
    
    private KeyInfoCredentialResolver resolver;
    
    private RSAPublicKey pubKey;
    private final String rsaBase64 = 
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzVp5BZoctb2GuoDf8QUS" +
        "pGcRct7FKtldC7GG+kN6XvUJW+vgc2jOQ6zfLiKqq6ARN1qdC7a4CrkE6Q6TRQXU" +
        "tqeWn4lLTmC1gQ7Ys0zs7N2d+jBjIyD1GEOLNNyD98j4drnehCqQz4mKszW5EWoi" +
        "MJmEorea/kTGL3en7ir0zp+oez2SOQA+0XWu1VoeTlUqGV5Ucd6sRYaPpmYVtKuH" +
        "1H04uZVsH+BIZHwZc4MP5OYH+HDouq6xqUUtc8Zm7V9UQIPiNtM+ndOINDdlrCub" +
        "LbM4GCqCETiQol8I62mvP0qBXCC6JVkKbbVRwSFGJcg5ZvJiBZXmX+EXhaX5vp1G" +
        "MQIDAQAB";
    
    private X509Certificate entityCert;
    private String entityCertBase64 = 
        "MIIDjDCCAnSgAwIBAgIBKjANBgkqhkiG9w0BAQUFADAtMRIwEAYDVQQKEwlJbnRl" +
        "cm5ldDIxFzAVBgNVBAMTDmNhLmV4YW1wbGUub3JnMB4XDTA3MDQwOTA2MTIwOVoX" +
        "DTE3MDQwNjA2MTIwOVowMTESMBAGA1UEChMJSW50ZXJuZXQyMRswGQYDVQQDExJm" +
        "b29iYXIuZXhhbXBsZS5vcmcwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIB" +
        "AQDNWnkFmhy1vYa6gN/xBRKkZxFy3sUq2V0LsYb6Q3pe9Qlb6+BzaM5DrN8uIqqr" +
        "oBE3Wp0LtrgKuQTpDpNFBdS2p5afiUtOYLWBDtizTOzs3Z36MGMjIPUYQ4s03IP3" +
        "yPh2ud6EKpDPiYqzNbkRaiIwmYSit5r+RMYvd6fuKvTOn6h7PZI5AD7Rda7VWh5O" +
        "VSoZXlRx3qxFho+mZhW0q4fUfTi5lWwf4EhkfBlzgw/k5gf4cOi6rrGpRS1zxmbt" +
        "X1RAg+I20z6d04g0N2WsK5stszgYKoIROJCiXwjraa8/SoFcILolWQpttVHBIUYl" +
        "yDlm8mIFleZf4ReFpfm+nUYxAgMBAAGjgbIwga8wCQYDVR0TBAIwADAsBglghkgB" +
        "hvhCAQ0EHxYdT3BlblNTTCBHZW5lcmF0ZWQgQ2VydGlmaWNhdGUwHQYDVR0OBBYE" +
        "FDgRgTkjaKoK6DoZfUZ4g9LDJUWuMFUGA1UdIwROMEyAFNXuZVPeUdqHrULqQW7y" +
        "r9buRpQLoTGkLzAtMRIwEAYDVQQKEwlJbnRlcm5ldDIxFzAVBgNVBAMTDmNhLmV4" +
        "YW1wbGUub3JnggEBMA0GCSqGSIb3DQEBBQUAA4IBAQCPj3Si4Eiw9abNgPBUhBXW" +
        "d6eRYlIHaHcnez6j6g7foAOyuVIUso9Q5c6pvL87lmasK55l09YPXw1qmiH+bHMc" +
        "rwEPODpLx7xd3snlOCi7FyxahxwSs8yfTu8Pq95rWt0LNcfHxQK938Cpnav6jgDo" +
        "2uH/ywAOFFSnoBzGHAfScHMfj8asZ6THosYsklII7FSU8j49GV2utkvGB3mcu4ST" +
        "uLdeRCZmi93vq1D4JVGsXC4UaHjg114+a+9q0XZdz6a1UW4pt1ryXIPotCS62M71" +
        "pkJf5neHUinKAqgoRfPXowudZg1Zl8DjzoOBn+MNHRrR5KYbVGvdHcxoJLCwVB/v";
    
    private String entityCertSKIBase64 = "OBGBOSNoqgroOhl9RniD0sMlRa4=";


    private X509Certificate caCert;
    private String caCertBase64 = 
        "MIIDXTCCAkWgAwIBAgIBATANBgkqhkiG9w0BAQUFADAtMRIwEAYDVQQKEwlJbnRl" +
        "cm5ldDIxFzAVBgNVBAMTDmNhLmV4YW1wbGUub3JnMB4XDTA3MDQwOTA1NDcxMloX" +
        "DTE3MDQwNjA1NDcxMlowLTESMBAGA1UEChMJSW50ZXJuZXQyMRcwFQYDVQQDEw5j" +
        "YS5leGFtcGxlLm9yZzCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBANxM" +
        "5/6mBCcX+S7HApcKtfqdFRZzi6Ra91nkEzXOUcO+BPUdYqSxKGnCCso25ZOZP3gn" +
        "JVkY8Pi7VWrCM6wRgIMyQDvNYqCpNjkZGFkrMoa6fm8BSaDHJ1fz6l/eEl0CVU3U" +
        "uUAf0mXQLGm6Jannq8aMolRujlhE5iRaOJ2qp6wqsvyatK+vTgDngnwYVa4Cqu0j" +
        "UeNF28quST5D3gIuZ0OeFHSM2Z1WUKkwwsHqVkxBBcH1QE1JOGIoSnrxxl/o4VlL" +
        "WGEI8zq5qixE8VYtBBmijBwIL5ETy2fwiqcsvimQaQAtAfbtpO3kBSs8n7nnzMUH" +
        "fRlcebGkwwcNfYcD5hcCAwEAAaOBhzCBhDAdBgNVHQ4EFgQU1e5lU95R2oetQupB" +
        "bvKv1u5GlAswVQYDVR0jBE4wTIAU1e5lU95R2oetQupBbvKv1u5GlAuhMaQvMC0x" +
        "EjAQBgNVBAoTCUludGVybmV0MjEXMBUGA1UEAxMOY2EuZXhhbXBsZS5vcmeCAQEw" +
        "DAYDVR0TBAUwAwEB/zANBgkqhkiG9w0BAQUFAAOCAQEAbqrozetM/iBVIUir9k14" +
        "HbLEP0lZ6jOPWFIUFXMOn0t8+Ul7PMp9Rdn+7OGQIoJw97/mam3kpQ/HmjJMFYv6" +
        "MGsDQ4vAOiQiaTKzgMhrnCdVpVH9uz4ARHiysCujnjH9sehTWgybY8pXzzSG5BAj" +
        "EGowHq01nXxq2K4yAJSdAUBYLfuSKW1uRU6cmEa9uzl9EvoZfAF3BLnGlPqu4Zaj" +
        "H2NC9ZY0y19LX4yeJLHL1sY4fyxb3x8QhcCXiI16awBTr/VnUpJjSe9vh+OudWGe" +
        "yCL/KhjlrDkjJ3hIxBF5mP/Y27cFpRnC2gECkieURvh52OyuqkzpbOrTN5rD9fNi" +
        "nA==";
    
    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        List<KeyInfoProvider> providers = new ArrayList<KeyInfoProvider>();
        providers.add(new InlineX509DataProvider());
        providers.add(new RSAKeyValueProvider());
        resolver = new BasicProviderKeyInfoCredentialResolver(providers);
        
        pubKey = SecurityHelper.buildJavaRSAPublicKey(rsaBase64);
        entityCert = SecurityHelper.buildJavaX509Cert(entityCertBase64);
        caCert = SecurityHelper.buildJavaX509Cert(caCertBase64);
        
        new X500Principal("cn=foobar.example.org, O=Internet2");
        new X500Principal("cn=ca.example.org, O=Internet2");
        Base64Support.decode(entityCertSKIBase64);
    }
    
    /**
     * Test resolution with multiple certs, end-entity cert identified by KeyValue.
     * 
     * @throws SecurityException on error resolving credentials
     * @throws ResolverException  on error resolving credentials
     */
    @Test
    public void testResolutionWithKeyValue() throws SecurityException, ResolverException {
        KeyInfo keyInfo = 
            (KeyInfo) unmarshallElement("/data/org/opensaml/xmlsec/keyinfo/impl/X509CertificatesWithKeyValue.xml");
        CriteriaSet criteriaSet = new CriteriaSet( new KeyInfoCriterion(keyInfo) );
        Iterator<Credential> iter = resolver.resolve(criteriaSet).iterator();
        
        Assert.assertTrue(iter.hasNext(), "No credentials were found");
        
        Credential credential = iter.next();
        Assert.assertNotNull(credential, "Credential was null");
        Assert.assertFalse(iter.hasNext(), "Too many credentials returned");
        
        Assert.assertTrue(credential instanceof X509Credential, "Credential is not of the expected type");
        X509Credential x509Credential = (X509Credential) credential;
        
        Assert.assertNotNull(x509Credential.getPublicKey(), "Public key was null");
        Assert.assertEquals(x509Credential.getPublicKey(), pubKey, "Expected public key value not found");
        
        Assert.assertEquals(x509Credential.getKeyNames().size(), 2, "Wrong number of key names");
        Assert.assertTrue(x509Credential.getKeyNames().contains("Foo"), "Expected key name value not found");
        Assert.assertTrue(x509Credential.getKeyNames().contains("Bar"), "Expected key name value not found");
        
        Assert.assertNotNull(x509Credential.getEntityCertificate(), "Entity certificate was null");
        Assert.assertEquals(x509Credential.getEntityCertificate(), entityCert, "Expected X509Certificate value not found");
        
        Assert.assertEquals(x509Credential.getEntityCertificateChain().size(), 2, "Wrong number of certs in cert chain found");
        Assert.assertTrue(x509Credential.getEntityCertificateChain().contains(entityCert), "Cert not found in cert chain");
        Assert.assertTrue(x509Credential.getEntityCertificateChain().contains(caCert), "Cert not found in cert chain");
    }
    
    /**
     * Test resolution with multiple certs, end-entity cert identified by X509SubjectName.
     * 
     * @throws SecurityException on error resolving credentials
     * @throws ResolverException on error resolving credentials
     */
    @Test
    public void testResolutionWithSubjectName() throws SecurityException, ResolverException {
        KeyInfo keyInfo = 
            (KeyInfo) unmarshallElement("/data/org/opensaml/xmlsec/keyinfo/impl/X509CertificatesWithSubjectName.xml");
        CriteriaSet criteriaSet = new CriteriaSet( new KeyInfoCriterion(keyInfo) );
        Iterator<Credential> iter = resolver.resolve(criteriaSet).iterator();
        
        Assert.assertTrue(iter.hasNext(), "No credentials were found");
        
        Credential credential = iter.next();
        Assert.assertNotNull(credential, "Credential was null");
        Assert.assertFalse(iter.hasNext(), "Too many credentials returned");
        
        Assert.assertTrue(credential instanceof X509Credential, "Credential is not of the expected type");
        X509Credential x509Credential = (X509Credential) credential;
        
        Assert.assertNotNull(x509Credential.getPublicKey(), "Public key was null");
        Assert.assertEquals(x509Credential.getPublicKey(), pubKey, "Expected public key value not found");
        
        Assert.assertEquals(x509Credential.getKeyNames().size(), 2, "Wrong number of key names");
        Assert.assertTrue(x509Credential.getKeyNames().contains("Foo"), "Expected key name value not found");
        Assert.assertTrue(x509Credential.getKeyNames().contains("Bar"), "Expected key name value not found");
        
        Assert.assertNotNull(x509Credential.getEntityCertificate(), "Entity certificate was null");
        Assert.assertEquals(x509Credential.getEntityCertificate(), entityCert, "Expected X509Certificate value not found");
        
        Assert.assertEquals(x509Credential.getEntityCertificateChain().size(), 2, "Wrong number of certs in cert chain found");
        Assert.assertTrue(x509Credential.getEntityCertificateChain().contains(entityCert), "Cert not found in cert chain");
        Assert.assertTrue(x509Credential.getEntityCertificateChain().contains(caCert), "Cert not found in cert chain");
    }
    
    /**
     * Test resolution with multiple certs, end-entity cert identified by X509IssuerSerial.
     * 
     * @throws SecurityException on error resolving credentials
     * @throws ResolverException on error resolving credentials
     */
    @Test
    public void testResolutionWithIssuerSerial() throws SecurityException, ResolverException {
        KeyInfo keyInfo = 
            (KeyInfo) unmarshallElement("/data/org/opensaml/xmlsec/keyinfo/impl/X509CertificatesWithIssuerSerial.xml");
        CriteriaSet criteriaSet = new CriteriaSet( new KeyInfoCriterion(keyInfo) );
        Iterator<Credential> iter = resolver.resolve(criteriaSet).iterator();
        
        Assert.assertTrue(iter.hasNext(), "No credentials were found");
        
        Credential credential = iter.next();
        Assert.assertNotNull(credential, "Credential was null");
        Assert.assertFalse(iter.hasNext(), "Too many credentials returned");
        
        Assert.assertTrue(credential instanceof X509Credential, "Credential is not of the expected type");
        X509Credential x509Credential = (X509Credential) credential;
        
        Assert.assertNotNull(x509Credential.getPublicKey(), "Public key was null");
        Assert.assertEquals(x509Credential.getPublicKey(), pubKey, "Expected public key value not found");
        
        Assert.assertEquals(x509Credential.getKeyNames().size(), 2, "Wrong number of key names");
        Assert.assertTrue(x509Credential.getKeyNames().contains("Foo"), "Expected key name value not found");
        Assert.assertTrue(x509Credential.getKeyNames().contains("Bar"), "Expected key name value not found");
        
        Assert.assertNotNull(x509Credential.getEntityCertificate(), "Entity certificate was null");
        Assert.assertEquals(x509Credential.getEntityCertificate(), entityCert, "Expected X509Certificate value not found");
        
        Assert.assertEquals(x509Credential.getEntityCertificateChain().size(), 2, "Wrong number of certs in cert chain found");
        Assert.assertTrue(x509Credential.getEntityCertificateChain().contains(entityCert), "Cert not found in cert chain");
        Assert.assertTrue(x509Credential.getEntityCertificateChain().contains(caCert), "Cert not found in cert chain");
    }
    
    /**
     * Test resolution with multiple certs, end-entity cert identified by X509SubjectName.
     * 
     * @throws SecurityException on error resolving credentials
     * @throws ResolverException on error resolving credentials
     */
    @Test
    public void testResolutionWithSubjectKeyIdentifier() throws SecurityException, ResolverException {
        KeyInfo keyInfo = 
            (KeyInfo) unmarshallElement("/data/org/opensaml/xmlsec/keyinfo/impl/X509CertificatesWithSKI.xml");
        CriteriaSet criteriaSet = new CriteriaSet( new KeyInfoCriterion(keyInfo) );
        Iterator<Credential> iter = resolver.resolve(criteriaSet).iterator();
        
        Assert.assertTrue(iter.hasNext(), "No credentials were found");
        
        Credential credential = iter.next();
        Assert.assertNotNull(credential, "Credential was null");
        Assert.assertFalse(iter.hasNext(), "Too many credentials returned");
        
        Assert.assertTrue(credential instanceof X509Credential, "Credential is not of the expected type");
        X509Credential x509Credential = (X509Credential) credential;
        
        Assert.assertNotNull(x509Credential.getPublicKey(), "Public key was null");
        Assert.assertEquals(x509Credential.getPublicKey(), pubKey, "Expected public key value not found");
        
        Assert.assertEquals(x509Credential.getKeyNames().size(), 2, "Wrong number of key names");
        Assert.assertTrue(x509Credential.getKeyNames().contains("Foo"), "Expected key name value not found");
        Assert.assertTrue(x509Credential.getKeyNames().contains("Bar"), "Expected key name value not found");
        
        Assert.assertNotNull(x509Credential.getEntityCertificate(), "Entity certificate was null");
        Assert.assertEquals(x509Credential.getEntityCertificate(), entityCert, "Expected X509Certificate value not found");
        
        Assert.assertEquals(x509Credential.getEntityCertificateChain().size(), 2, "Wrong number of certs in cert chain found");
        Assert.assertTrue(x509Credential.getEntityCertificateChain().contains(entityCert), "Cert not found in cert chain");
        Assert.assertTrue(x509Credential.getEntityCertificateChain().contains(caCert), "Cert not found in cert chain");
    }

}
