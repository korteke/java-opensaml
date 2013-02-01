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
import java.math.BigInteger;
import java.security.KeyException;
import java.security.PublicKey;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.security.SecurityException;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.security.x509.X509Support;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.keyinfo.impl.X509KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.X509Data;
import org.opensaml.xmlsec.signature.X509Digest;
import org.opensaml.xmlsec.signature.X509IssuerSerial;
import org.opensaml.xmlsec.signature.X509SKI;
import org.opensaml.xmlsec.signature.support.SignatureConstants;

/**
 * Tests the factory and impl for X509KeyInfoGenerator.
 */
public class X509KeyInfoGeneratorTest extends XMLObjectBaseTestCase {

    private static String subjectAltNameExtensionOID = "2.5.29.17";

    private BasicX509Credential credential;

    private X509KeyInfoGeneratorFactory factory;

    private KeyInfoGenerator generator;

    private String keyNameFoo = "FOO";

    private String keyNameBar = "BAR";

    private String entityID = "someEntityID";

    private PublicKey pubKey;

    private X509Certificate entityCert;

    private String entityCertBase64 = "MIIDzjCCAragAwIBAgIBMTANBgkqhkiG9w0BAQUFADAtMRIwEAYDVQQKEwlJbnRl"
            + "cm5ldDIxFzAVBgNVBAMTDmNhLmV4YW1wbGUub3JnMB4XDTA3MDUyMTE4MjM0MFoX"
            + "DTE3MDUxODE4MjM0MFowMTESMBAGA1UEChMJSW50ZXJuZXQyMRswGQYDVQQDExJm"
            + "b29iYXIuZXhhbXBsZS5vcmcwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIB"
            + "AQDNWnkFmhy1vYa6gN/xBRKkZxFy3sUq2V0LsYb6Q3pe9Qlb6+BzaM5DrN8uIqqr"
            + "oBE3Wp0LtrgKuQTpDpNFBdS2p5afiUtOYLWBDtizTOzs3Z36MGMjIPUYQ4s03IP3"
            + "yPh2ud6EKpDPiYqzNbkRaiIwmYSit5r+RMYvd6fuKvTOn6h7PZI5AD7Rda7VWh5O"
            + "VSoZXlRx3qxFho+mZhW0q4fUfTi5lWwf4EhkfBlzgw/k5gf4cOi6rrGpRS1zxmbt"
            + "X1RAg+I20z6d04g0N2WsK5stszgYKoIROJCiXwjraa8/SoFcILolWQpttVHBIUYl"
            + "yDlm8mIFleZf4ReFpfm+nUYxAgMBAAGjgfQwgfEwCQYDVR0TBAIwADAsBglghkgB"
            + "hvhCAQ0EHxYdT3BlblNTTCBHZW5lcmF0ZWQgQ2VydGlmaWNhdGUwHQYDVR0OBBYE"
            + "FDgRgTkjaKoK6DoZfUZ4g9LDJUWuMFUGA1UdIwROMEyAFNXuZVPeUdqHrULqQW7y"
            + "r9buRpQLoTGkLzAtMRIwEAYDVQQKEwlJbnRlcm5ldDIxFzAVBgNVBAMTDmNhLmV4"
            + "YW1wbGUub3JnggEBMEAGA1UdEQQ5MDeCEmFzaW1vdi5leGFtcGxlLm9yZ4YbaHR0"
            + "cDovL2hlaW5sZWluLmV4YW1wbGUub3JnhwQKAQIDMA0GCSqGSIb3DQEBBQUAA4IB"
            + "AQBLiDMyQ60ldIytVO1GCpp1S1sKJyTF56GVxHh/82hiRFbyPu+2eSl7UcJfH4ZN"
            + "bAfHL1vDKTRJ9zoD8WRzpOCUtT0IPIA/Ex+8lFzZmujO10j3TMpp8Ii6+auYwi/T"
            + "osrfw1YCxF+GI5KO49CfDRr6yxUbMhbTN+ssK4UzFf36UbkeJ3EfDwB0WU70jnlk"
            + "yO8f97X6mLd5QvRcwlkDMftP4+MB+inTlxDZ/w8NLXQoDW6p/8r91bupXe0xwuyE"
            + "vow2xjxlzVcux2BZsUZYjBa07ZmNNBtF7WaQqH7l2OBCAdnBhvme5i/e0LK3Ivys" + "+hcVyvCXs5XtFTFWDAVYvzQ6";

    private String entityCertSKIBase64 = "OBGBOSNoqgroOhl9RniD0sMlRa4=";
    private String entityCertDigestBase64 = "w+E2z13/aCCFAQWscM4BaH8U4M4=";

    private X509Certificate caCert;

    private String caCertBase64 = "MIIDXTCCAkWgAwIBAgIBATANBgkqhkiG9w0BAQUFADAtMRIwEAYDVQQKEwlJbnRl"
            + "cm5ldDIxFzAVBgNVBAMTDmNhLmV4YW1wbGUub3JnMB4XDTA3MDQwOTA1NDcxMloX"
            + "DTE3MDQwNjA1NDcxMlowLTESMBAGA1UEChMJSW50ZXJuZXQyMRcwFQYDVQQDEw5j"
            + "YS5leGFtcGxlLm9yZzCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBANxM"
            + "5/6mBCcX+S7HApcKtfqdFRZzi6Ra91nkEzXOUcO+BPUdYqSxKGnCCso25ZOZP3gn"
            + "JVkY8Pi7VWrCM6wRgIMyQDvNYqCpNjkZGFkrMoa6fm8BSaDHJ1fz6l/eEl0CVU3U"
            + "uUAf0mXQLGm6Jannq8aMolRujlhE5iRaOJ2qp6wqsvyatK+vTgDngnwYVa4Cqu0j"
            + "UeNF28quST5D3gIuZ0OeFHSM2Z1WUKkwwsHqVkxBBcH1QE1JOGIoSnrxxl/o4VlL"
            + "WGEI8zq5qixE8VYtBBmijBwIL5ETy2fwiqcsvimQaQAtAfbtpO3kBSs8n7nnzMUH"
            + "fRlcebGkwwcNfYcD5hcCAwEAAaOBhzCBhDAdBgNVHQ4EFgQU1e5lU95R2oetQupB"
            + "bvKv1u5GlAswVQYDVR0jBE4wTIAU1e5lU95R2oetQupBbvKv1u5GlAuhMaQvMC0x"
            + "EjAQBgNVBAoTCUludGVybmV0MjEXMBUGA1UEAxMOY2EuZXhhbXBsZS5vcmeCAQEw"
            + "DAYDVR0TBAUwAwEB/zANBgkqhkiG9w0BAQUFAAOCAQEAbqrozetM/iBVIUir9k14"
            + "HbLEP0lZ6jOPWFIUFXMOn0t8+Ul7PMp9Rdn+7OGQIoJw97/mam3kpQ/HmjJMFYv6"
            + "MGsDQ4vAOiQiaTKzgMhrnCdVpVH9uz4ARHiysCujnjH9sehTWgybY8pXzzSG5BAj"
            + "EGowHq01nXxq2K4yAJSdAUBYLfuSKW1uRU6cmEa9uzl9EvoZfAF3BLnGlPqu4Zaj"
            + "H2NC9ZY0y19LX4yeJLHL1sY4fyxb3x8QhcCXiI16awBTr/VnUpJjSe9vh+OudWGe"
            + "yCL/KhjlrDkjJ3hIxBF5mP/Y27cFpRnC2gECkieURvh52OyuqkzpbOrTN5rD9fNi" + "nA==";

    private String subjectCN;

    private X500Principal subjectName;

    private X500Principal issuerName;

    private BigInteger serialNumber;

    private byte[] subjectKeyIdentifier;
    
    private byte[] x509Digest;

    private String altName1, altName2, altName3;

    private Integer altName1Type, altName2Type, altName3Type;

    private X509CRL caCRL;

    private String caCRLBase64 = "MIIBmjCBgwIBATANBgkqhkiG9w0BAQUFADAtMRIwEAYDVQQKEwlJbnRlcm5ldDIx"
            + "FzAVBgNVBAMTDmNhLmV4YW1wbGUub3JnFw0wNzA1MjEwNTAwMzNaFw0wNzA2MjAw"
            + "NTAwMzNaMCIwIAIBKxcNMDcwNTIxMDQ1ODI5WjAMMAoGA1UdFQQDCgEBMA0GCSqG"
            + "SIb3DQEBBQUAA4IBAQAghL5eW9NsMRCk84mAZ+QMjoCuy7zZJr5vPHk7WrOffL7B"
            + "GWZ6u6D1cSCzZNvrBolip1yb8KSdB9PJqEV1kInXnZegeqjENq+9j8nGdyoYuofh"
            + "A5AU8L9n9fjwYTUkfNfAMWeVVuplJN4yAp03JSJULVqmC63EEP7u7kFS94Mze9sa"
            + "+VqBu7tGyZ55XX8AO39d1c3DoHIPfS1wHHLyuWxnys8GjANJxQiZmFtUfPztp3qH"
            + "/XlfFLgY5EBTanyOk5yycU/l+6P1RBhJZDPicp3iWVsjYHYWS+ovdyWuL7RrLRMb"
            + "zecnCa5eIhSevoMYUkg4h9ckAZUQeHsK08gB/dFh";

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        factory = new X509KeyInfoGeneratorFactory();
        generator = null;

        entityCert = X509Support.decodeCertificate(entityCertBase64);
        pubKey = entityCert.getPublicKey();
        caCert = X509Support.decodeCertificate(caCertBase64);
        caCRL = X509Support.decodeCRL(caCRLBase64);

        subjectCN = "foobar.example.org";
        subjectName = new X500Principal("cn=foobar.example.org, O=Internet2");
        issuerName = new X500Principal("cn=ca.example.org, O=Internet2");
        serialNumber = new BigInteger("49");
        subjectKeyIdentifier = Base64Support.decode(entityCertSKIBase64);
        x509Digest = Base64Support.decode(entityCertDigestBase64);

        altName1 = "asimov.example.org";
        altName1Type = X509Support.DNS_ALT_NAME;
        altName2 = "http://heinlein.example.org";
        altName2Type = X509Support.URI_ALT_NAME;
        altName3 = "10.1.2.3";
        altName3Type = X509Support.IP_ADDRESS_ALT_NAME;

        credential = new BasicX509Credential(entityCert);
        credential.setEntityId(entityID);
        credential.getKeyNames().add(keyNameFoo);
        credential.getKeyNames().add(keyNameBar);

        List<X509Certificate> chain = new ArrayList<X509Certificate>();
        chain.add(entityCert);
        chain.add(caCert);
        credential.setEntityCertificateChain(chain);

        List<X509CRL> crls = new ArrayList<X509CRL>();
        crls.add(caCRL);
        credential.setCRLs(crls);
    }

    /**
     * Test no options - should produce null KeyInfo.
     * 
     * @throws SecurityException
     */
    @Test
    public void testNoOptions() throws SecurityException {
        // all options false by default
        generator = factory.newInstance();
        KeyInfo keyInfo = generator.generate(credential);

        Assert.assertNull(keyInfo, "Generated KeyInfo with no options should have been null");
    }

    /**
     * Test emit public key.
     * 
     * @throws SecurityException
     */
    @Test
    public void testEmitPublicKey() throws SecurityException, KeyException {
        factory.setEmitPublicKeyValue(true);

        generator = factory.newInstance();
        KeyInfo keyInfo = generator.generate(credential);

        Assert.assertNotNull(keyInfo, "Generated KeyInfo was null");
        Assert.assertNotNull(keyInfo.getOrderedChildren(), "Generated KeyInfo children list was null");

        Assert.assertEquals(keyInfo.getOrderedChildren().size(), 1, "Unexpected number of KeyInfo children");
        Assert.assertEquals(keyInfo.getKeyValues().size(), 1, "Unexpected number of KeyValue elements");
        PublicKey generatedKey = KeyInfoSupport.getKey(keyInfo.getKeyValues().get(0));
        Assert.assertEquals(generatedKey, pubKey, "Unexpected key value");
    }

    /**
     * Test emit credential key names.
     * 
     * @throws SecurityException
     */
    @Test
    public void testEmitKeynames() throws SecurityException {
        factory.setEmitKeyNames(true);

        generator = factory.newInstance();
        KeyInfo keyInfo = generator.generate(credential);

        Assert.assertNotNull(keyInfo, "Generated KeyInfo was null");
        Assert.assertNotNull(keyInfo.getOrderedChildren(), "Generated KeyInfo children list was null");

        Assert.assertEquals(keyInfo.getKeyNames().size(), 2, "Unexpected number of KeyName elements");
        List<String> keyNames = KeyInfoSupport.getKeyNames(keyInfo);
        Assert.assertTrue(keyNames.contains(keyNameFoo), "Failed to find expected KeyName value");
        Assert.assertTrue(keyNames.contains(keyNameBar), "Failed to find expected KeyName value");
    }

    /**
     * Test emit entity ID as key name.
     * 
     * @throws SecurityException
     */
    @Test
    public void testEmitEntityIDAsKeyName() throws SecurityException {
        factory.setEmitEntityIDAsKeyName(true);

        generator = factory.newInstance();
        KeyInfo keyInfo = generator.generate(credential);

        Assert.assertNotNull(keyInfo, "Generated KeyInfo was null");
        Assert.assertNotNull(keyInfo.getOrderedChildren(), "Generated KeyInfo children list was null");

        Assert.assertEquals(keyInfo.getKeyNames().size(), 1, "Unexpected number of KeyName elements");
        List<String> keyNames = KeyInfoSupport.getKeyNames(keyInfo);
        Assert.assertTrue(keyNames.contains(entityID), "Failed to find expected KeyName value");
    }

    /**
     * Test emit entity cert.
     * 
     * @throws SecurityException
     * @throws CertificateException
     */
    @Test
    public void testEmitEntityCert() throws SecurityException, CertificateException {
        factory.setEmitEntityCertificate(true);

        generator = factory.newInstance();
        KeyInfo keyInfo = generator.generate(credential);

        Assert.assertNotNull(keyInfo, "Generated KeyInfo was null");
        Assert.assertNotNull(keyInfo.getOrderedChildren(), "Generated KeyInfo children list was null");

        Assert.assertEquals(keyInfo.getX509Datas().size(), 1, "Unexpected number of X509Data elements");
        X509Data x509Data = keyInfo.getX509Datas().get(0);
        Assert.assertEquals(x509Data.getX509Certificates().size(), 1, "Unexpected number of X509Certificate elements");
        List<X509Certificate> certs = KeyInfoSupport.getCertificates(x509Data);
        Assert.assertEquals(certs.get(0), entityCert, "Unexpected certificate value found");
    }

    /**
     * Test emit entity cert chain in X509Data.
     * 
     * @throws SecurityException
     * @throws CertificateException
     */
    @Test
    public void testEmitEntityCertChain() throws SecurityException, CertificateException {
        factory.setEmitEntityCertificateChain(true);

        generator = factory.newInstance();
        KeyInfo keyInfo = generator.generate(credential);

        Assert.assertNotNull(keyInfo, "Generated KeyInfo was null");
        Assert.assertNotNull(keyInfo.getOrderedChildren(), "Generated KeyInfo children list was null");

        Assert.assertEquals(keyInfo.getX509Datas().size(), 1, "Unexpected number of X509Data elements");
        X509Data x509Data = keyInfo.getX509Datas().get(0);
        Assert.assertEquals(x509Data.getX509Certificates().size(), 2, "Unexpected number of X509Certificate elements");
        List<X509Certificate> certs = KeyInfoSupport.getCertificates(x509Data);
        Assert.assertTrue(certs.contains(entityCert), "Expected certificate value not found");
        Assert.assertTrue(certs.contains(caCert), "Expected certificate value not found");
    }

    /**
     * Test combo options of cert and chain - don't emit duplicate of entity cert.
     * 
     * @throws SecurityException
     * @throws CertificateException
     */
    @Test
    public void testEmitCertAndChainCombo() throws SecurityException, CertificateException {
        factory.setEmitEntityCertificate(true);
        factory.setEmitEntityCertificateChain(true);

        generator = factory.newInstance();
        KeyInfo keyInfo = generator.generate(credential);

        Assert.assertNotNull(keyInfo, "Generated KeyInfo was null");
        Assert.assertNotNull(keyInfo.getOrderedChildren(), "Generated KeyInfo children list was null");

        Assert.assertEquals(keyInfo.getX509Datas().size(), 1, "Unexpected number of X509Data elements");
        X509Data x509Data = keyInfo.getX509Datas().get(0);
        Assert.assertEquals(x509Data.getX509Certificates().size(), 2, "Unexpected number of X509Certificate elements");
        List<X509Certificate> certs = KeyInfoSupport.getCertificates(x509Data);
        Assert.assertTrue(certs.contains(entityCert), "Expected certificate value not found");
        Assert.assertTrue(certs.contains(caCert), "Expected certificate value not found");
    }

    /**
     * Test emit CRLs.
     * 
     * @throws SecurityException
     * @throws CRLException
     */
    @Test
    public void testEmitCRLs() throws SecurityException, CRLException {
        factory.setEmitCRLs(true);

        generator = factory.newInstance();
        KeyInfo keyInfo = generator.generate(credential);

        Assert.assertNotNull(keyInfo, "Generated KeyInfo was null");
        Assert.assertNotNull(keyInfo.getOrderedChildren(), "Generated KeyInfo children list was null");

        Assert.assertEquals(keyInfo.getX509Datas().size(), 1, "Unexpected number of X509Data elements");
        X509Data x509Data = keyInfo.getX509Datas().get(0);
        Assert.assertEquals(x509Data.getX509CRLs().size(), 1, "Unexpected number of X509CRL elements");
        X509CRL crl = KeyInfoSupport.getCRL(x509Data.getX509CRLs().get(0));
        Assert.assertEquals(crl, caCRL, "Unexpected CRL value found");
    }

    /**
     * Test emit subject name in X509Data.
     * 
     * @throws SecurityException
     */
    @Test
    public void testEmitX509SubjectName() throws SecurityException {
        factory.setEmitX509SubjectName(true);

        generator = factory.newInstance();
        KeyInfo keyInfo = generator.generate(credential);

        Assert.assertNotNull(keyInfo, "Generated KeyInfo was null");
        Assert.assertNotNull(keyInfo.getOrderedChildren(), "Generated KeyInfo children list was null");

        Assert.assertEquals(keyInfo.getX509Datas().size(), 1, "Unexpected number of X509Data elements");
        X509Data x509Data = keyInfo.getX509Datas().get(0);
        Assert.assertEquals(x509Data.getX509SubjectNames().size(), 1, "Unexpected number of X509SubjectName elements");
        String name = StringSupport.trimOrNull(x509Data.getX509SubjectNames().get(0).getValue());
        Assert.assertEquals(new X500Principal(name), subjectName, "Unexpected X509SubjectName value found");
    }

    /**
     * Test emit issuer name and serial number in X509Data.
     * 
     * @throws SecurityException
     */
    @Test
    public void testEmitX509IssuerSerial() throws SecurityException {
        factory.setEmitX509IssuerSerial(true);

        generator = factory.newInstance();
        KeyInfo keyInfo = generator.generate(credential);

        Assert.assertNotNull(keyInfo, "Generated KeyInfo was null");
        Assert.assertNotNull(keyInfo.getOrderedChildren(), "Generated KeyInfo children list was null");

        Assert.assertEquals(keyInfo.getX509Datas().size(), 1, "Unexpected number of X509Data elements");
        X509Data x509Data = keyInfo.getX509Datas().get(0);
        Assert.assertEquals(x509Data.getX509IssuerSerials().size(), 1, "Unexpected number of X509IssuerSerial elements");
        X509IssuerSerial issuerSerial = x509Data.getX509IssuerSerials().get(0);
        Assert.assertNotNull(issuerSerial.getX509IssuerName(), "X509IssuerName not present");
        Assert.assertNotNull(issuerSerial.getX509SerialNumber(), "X509SerialNumber not present");
        String name = StringSupport.trimOrNull(issuerSerial.getX509IssuerName().getValue());
        Assert.assertEquals(new X500Principal(name), issuerName, "Unexpected X509IssuerName value found");
        BigInteger number = issuerSerial.getX509SerialNumber().getValue();
        Assert.assertEquals(number, serialNumber, "Unexpected serial number value found");
    }

    /**
     * Test emit subject key identifier in X509Data.
     * 
     * @throws SecurityException
     */
    @Test
    public void testEmitX509SKI() throws SecurityException {
        factory.setEmitX509SKI(true);

        generator = factory.newInstance();
        KeyInfo keyInfo = generator.generate(credential);

        Assert.assertNotNull(keyInfo, "Generated KeyInfo was null");
        Assert.assertNotNull(keyInfo.getOrderedChildren(), "Generated KeyInfo children list was null");

        Assert.assertEquals(keyInfo.getX509Datas().size(), 1, "Unexpected number of X509Data elements");
        X509Data x509Data = keyInfo.getX509Datas().get(0);
        Assert.assertEquals(x509Data.getX509SKIs().size(), 1, "Unexpected number of X509SKI elements");
        X509SKI ski = x509Data.getX509SKIs().get(0);
        byte[] skiValue = Base64Support.decode(StringSupport.trimOrNull(ski.getValue()));
        Assert.assertTrue(Arrays.equals(subjectKeyIdentifier, skiValue), "Unexpected SKI value found");
    }
    
    /**
     * Test emit X509Digest in X509Data.
     * @throws SecurityException
     */
    @Test
    public void testEmitX509Digest() throws SecurityException {
        factory.setEmitX509Digest(true);
        factory.setX509DigestAlgorithmURI(SignatureConstants.ALGO_ID_DIGEST_SHA1);
        
        generator = factory.newInstance();
        KeyInfo keyInfo = generator.generate(credential);
        
        Assert.assertNotNull(keyInfo, "Generated KeyInfo was null");
        Assert.assertNotNull(keyInfo.getOrderedChildren(), "Generated KeyInfo children list was null");
        
        Assert.assertEquals(keyInfo.getX509Datas().size(), 1, "Unexpected number of X509Data elements");
        X509Data x509Data = keyInfo.getX509Datas().get(0);
        Assert.assertEquals(x509Data.getXMLObjects(X509Digest.DEFAULT_ELEMENT_NAME).size(), 1,
                "Unexpected number of X509Digest elements");
        X509Digest digest = (X509Digest) x509Data.getXMLObjects(X509Digest.DEFAULT_ELEMENT_NAME).get(0);
        byte[] digestValue = Base64Support.decode(StringSupport.trimOrNull(digest.getValue()));
        Assert.assertTrue(Arrays.equals(x509Digest, digestValue), "Unexpected SHA-1 digest value found");
    }


    /**
     * Test emit subject DN as key name.
     * 
     * @throws SecurityException
     */
    @Test
    public void testEmitSubjectDNAsKeyName() throws SecurityException {
        factory.setEmitSubjectDNAsKeyName(true);

        generator = factory.newInstance();
        KeyInfo keyInfo = generator.generate(credential);

        Assert.assertNotNull(keyInfo, "Generated KeyInfo was null");
        Assert.assertNotNull(keyInfo.getOrderedChildren(), "Generated KeyInfo children list was null");

        Assert.assertEquals(keyInfo.getKeyNames().size(), 1, "Unexpected number of KeyName elements");
        List<String> keyNames = KeyInfoSupport.getKeyNames(keyInfo);
        String name = StringSupport.trimOrNull(keyNames.get(0));
        Assert.assertEquals(new X500Principal(name), subjectName, "Unexpected subject DN key name value found");
    }

    /**
     * Test emit subject CN as key name.
     * 
     * @throws SecurityException
     */
    @Test
    public void testEmitSubjectCNAsKeyName() throws SecurityException {
        factory.setEmitSubjectCNAsKeyName(true);

        generator = factory.newInstance();
        KeyInfo keyInfo = generator.generate(credential);

        Assert.assertNotNull(keyInfo, "Generated KeyInfo was null");
        Assert.assertNotNull(keyInfo.getOrderedChildren(), "Generated KeyInfo children list was null");

        Assert.assertEquals(keyInfo.getKeyNames().size(), 1, "Unexpected number of KeyName elements");
        List<String> keyNames = KeyInfoSupport.getKeyNames(keyInfo);
        Assert.assertTrue(keyNames.contains(subjectCN), "Failed to find expected KeyName value");
    }

    /**
     * Test emit subject alt names as key names.
     * 
     * @throws SecurityException
     * @throws CertificateParsingException
     */
    @Test
    public void testEmitSubjectAltNamesAsKeyNames() throws SecurityException, CertificateParsingException {
        factory.setEmitSubjectAltNamesAsKeyNames(true);

        generator = factory.newInstance();
        KeyInfo keyInfo = generator.generate(credential);

        // Haven't set any alt name types yet, so expect no output
        Assert.assertNull(keyInfo, "Generated KeyInfo was not null");

        // Just a sanity check
        byte[] extensionValue = credential.getEntityCertificate().getExtensionValue(subjectAltNameExtensionOID);
        Assert.assertNotNull(extensionValue, "Entity cert's Java native getExtensionValue() was null");
        Assert.assertTrue(extensionValue.length > 0, "Entity cert's extension value was empty");

        factory.getSubjectAltNames().add(altName1Type);

        generator = factory.newInstance();
        keyInfo = generator.generate(credential);

        Assert.assertNotNull(keyInfo, "Generated KeyInfo was null");
        Assert.assertNotNull(keyInfo.getOrderedChildren(), "Generated KeyInfo children list was null");

        Assert.assertEquals(keyInfo.getKeyNames().size(), 1, "Unexpected number of KeyName elements");
        List<String> keyNames = KeyInfoSupport.getKeyNames(keyInfo);
        Assert.assertTrue(keyNames.contains(altName1), "Failed to find expected KeyName value");

        factory.getSubjectAltNames().add(altName2Type);
        factory.getSubjectAltNames().add(altName3Type);

        generator = factory.newInstance();
        keyInfo = generator.generate(credential);

        Assert.assertNotNull(keyInfo, "Generated KeyInfo was null");
        Assert.assertNotNull(keyInfo.getOrderedChildren(), "Generated KeyInfo children list was null");

        Assert.assertEquals(keyInfo.getKeyNames().size(), 3, "Unexpected number of KeyName elements");
        keyNames = KeyInfoSupport.getKeyNames(keyInfo);
        Assert.assertTrue(keyNames.contains(altName1), "Failed to find expected KeyName value");
        Assert.assertTrue(keyNames.contains(altName2), "Failed to find expected KeyName value");
        Assert.assertTrue(keyNames.contains(altName3), "Failed to find expected KeyName value");
    }

    /**
     * Test emitting combinations of key names.
     * 
     * @throws SecurityException
     */
    @Test
    public void testEmitKeyNamesCombo() throws SecurityException {
        factory.setEmitKeyNames(true);
        factory.setEmitEntityIDAsKeyName(true);
        factory.setEmitSubjectCNAsKeyName(true);

        factory.setEmitSubjectAltNamesAsKeyNames(true);
        factory.getSubjectAltNames().add(altName1Type);
        factory.getSubjectAltNames().add(altName2Type);
        factory.getSubjectAltNames().add(altName3Type);

        generator = factory.newInstance();
        KeyInfo keyInfo = generator.generate(credential);

        Assert.assertNotNull(keyInfo, "Generated KeyInfo was null");
        Assert.assertNotNull(keyInfo.getOrderedChildren(), "Generated KeyInfo children list was null");

        Assert.assertEquals(keyInfo.getKeyNames().size(), 7, "Unexpected number of KeyName elements");
        List<String> keyNames = KeyInfoSupport.getKeyNames(keyInfo);
        Assert.assertTrue(keyNames.contains(keyNameFoo), "Failed to find expected KeyName value");
        Assert.assertTrue(keyNames.contains(keyNameBar), "Failed to find expected KeyName value");
        Assert.assertTrue(keyNames.contains(entityID), "Failed to find expected KeyName value");
        Assert.assertTrue(keyNames.contains(subjectCN), "Failed to find expected KeyName value");
        Assert.assertTrue(keyNames.contains(altName1), "Failed to find expected KeyName value");
        Assert.assertTrue(keyNames.contains(altName2), "Failed to find expected KeyName value");
        Assert.assertTrue(keyNames.contains(altName3), "Failed to find expected KeyName value");
    }

    /**
     * Test that the options passed to the generator are really cloned. After newInstance() is called, changes to the
     * factory options should not be reflected in the generator.
     * 
     * @throws SecurityException
     */
    @Test
    public void testProperOptionsCloning() throws SecurityException {
        generator = factory.newInstance();
        KeyInfo keyInfo = generator.generate(credential);

        Assert.assertNull(keyInfo, "Generated KeyInfo was null");

        factory.setEmitKeyNames(true);
        factory.setEmitEntityIDAsKeyName(true);
        factory.setEmitPublicKeyValue(true);

        keyInfo = generator.generate(credential);

        Assert.assertNull(keyInfo, "Generated KeyInfo was null");

        generator = factory.newInstance();
        keyInfo = generator.generate(credential);

        Assert.assertNotNull(keyInfo, "Generated KeyInfo was null");
        Assert.assertNotNull(keyInfo.getOrderedChildren(), "Generated KeyInfo children list was null");
        Assert.assertEquals(keyInfo.getOrderedChildren().size(), 4, "Unexpected # of KeyInfo children found");
    }

}
