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

package org.opensaml.xmlsec.keyinfo;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import java.math.BigInteger;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import net.shibboleth.utilities.java.support.codec.Base64Support;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.security.x509.X509Support;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.signature.DEREncodedKeyValue;
import org.opensaml.xmlsec.signature.DSAKeyValue;
import org.opensaml.xmlsec.signature.Exponent;
import org.opensaml.xmlsec.signature.G;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.KeyValue;
import org.opensaml.xmlsec.signature.Modulus;
import org.opensaml.xmlsec.signature.P;
import org.opensaml.xmlsec.signature.Q;
import org.opensaml.xmlsec.signature.RSAKeyValue;
import org.opensaml.xmlsec.signature.X509CRL;
import org.opensaml.xmlsec.signature.X509Certificate;
import org.opensaml.xmlsec.signature.X509Data;
import org.opensaml.xmlsec.signature.X509Digest;
import org.opensaml.xmlsec.signature.X509IssuerSerial;
import org.opensaml.xmlsec.signature.X509SKI;
import org.opensaml.xmlsec.signature.X509SubjectName;
import org.opensaml.xmlsec.signature.Y;
import org.opensaml.xmlsec.signature.support.SignatureConstants;

import com.google.common.base.Strings;

/**
 * Test to exercise the KeyInfoSupport methods to convert between XMLObject's contained within KeyInfo and Java security
 * native types.
 */
public class KeyInfoSupportTest extends XMLObjectBaseTestCase {

    /** Cert which contains no X.509 v3 extensions. */
    private final String certNoExtensions = "MIIBwjCCASugAwIBAgIJAMrW6QSeKNBJMA0GCSqGSIb3DQEBBAUAMCMxITAfBgNV"
            + "BAMTGG5vZXh0ZW5zaW9ucy5leGFtcGxlLm9yZzAeFw0wNzA1MTkxNzU2NTVaFw0w"
            + "NzA2MTgxNzU2NTVaMCMxITAfBgNVBAMTGG5vZXh0ZW5zaW9ucy5leGFtcGxlLm9y"
            + "ZzCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEAw8xxu6TLqEdmnyXVZjiUoRHN"
            + "6yHyobZaRK+tBEmWkD4nTlOVmTKWBCO/F4OnugaJbSTH+7Jk37l8/XYYBSIkW0+L"
            + "2BglzQ2JCux/uoRu146QDIk9f5PIFs+Fxy7VRVUUZiOsonB/PNVqA7OVbPxzr1SK"
            + "PSE0s9CHaDjCaEs2BnMCAwEAATANBgkqhkiG9w0BAQQFAAOBgQAuI/l80wb8K6RT"
            + "1EKrAcfr9JAlJR4jmVnCK7j3Ulx++U98ze2G6/cluLxrbnqwXmxJNC3nt6xkQVJU"
            + "X1UFg+zkmRrst2Nv8TTrR7S30az068BHfrZLRSUConG9jXXj+hJq+w/ojmrq8Mzv" + "JSczkA2BvsEUBARYo53na7RMgk+xWg==";

    /*
     * These test examples are from the NIST PKI path processing test suite:
     * http://csrc.nist.gov/pki/testing/x509paths.html Data file: http://csrc.nist.gov/pki/testing/PKITS_data.zip
     */

    /* certs/BasicSelfIssuedNewKeyCACert.crt */
    /** Test cert subject DN 1. */
    private final String cert1SubjectDN = "CN=Basic Self-Issued New Key CA,O=Test Certificates,C=US";

    /**
     * Test cert 1 SKI value. Base64 encoded version of cert's plain (non-DER encoded) subject key identifier, which is:
     * AF:B9:F9:1D:C2:45:18:CC:B8:21:E2:A7:47:BC:49:BD:19:B5:78:28
     */
    private final String cert1SKIPlainBase64 = "r7n5HcJFGMy4IeKnR7xJvRm1eCg=";
    
    /** Test cert 1 SHA-1 digest. */
    private final String cert1DigestBase64 = "EmkP8ttMw28A/JoA3KcO11eez7Q=";

    /** Test cert 1. */
    private final String cert1 = "MIICgjCCAeugAwIBAgIBEzANBgkqhkiG9w0BAQUFADBAMQswCQYDVQQGEwJVUzEa"
            + "MBgGA1UEChMRVGVzdCBDZXJ0aWZpY2F0ZXMxFTATBgNVBAMTDFRydXN0IEFuY2hv"
            + "cjAeFw0wMTA0MTkxNDU3MjBaFw0xMTA0MTkxNDU3MjBaMFAxCzAJBgNVBAYTAlVT"
            + "MRowGAYDVQQKExFUZXN0IENlcnRpZmljYXRlczElMCMGA1UEAxMcQmFzaWMgU2Vs"
            + "Zi1Jc3N1ZWQgTmV3IEtleSBDQTCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEA"
            + "tCkygqcMEOy3i8p6ZV3685us1lOugSU4pUMRJNRH/lV2ykesk+JRcQy1s7WS12j9"
            + "GCnSJ919/TgeKLmV3ps1fC1B8HziC0mzBAr+7f5LkJqSf0kS0kfpyLOoO8VSJCip"
            + "/8uENkSkpvX+Lak96OKzhtyvi4KpUdQKfwpg6xUqakECAwEAAaN8MHowHwYDVR0j"
            + "BBgwFoAU+2zULYGeyid6ng2wPOqavIf/SeowHQYDVR0OBBYEFK+5+R3CRRjMuCHi"
            + "p0e8Sb0ZtXgoMA4GA1UdDwEB/wQEAwIBBjAXBgNVHSAEEDAOMAwGCmCGSAFlAwIB"
            + "MAEwDwYDVR0TAQH/BAUwAwEB/zANBgkqhkiG9w0BAQUFAAOBgQCuRBfDy2gSPp2k"
            + "ZR7OAvt+xDx4toJ9ImImUvJ94AOLd6Uxsi2dvQT5HLrIBrTYsSfQj1pA50XY2F7k"
            + "3eM/+JhYCcyZD9XtAslpOkjwACPJnODFAY8PWC00CcOxGb6q+S/VkrCwvlBeMjev" + "IH4bHvAymWsZndBZhcG8gBmDrZMwhQ==";

    /* certs/GoodCACert.crt */
    /** Test cert subject DN 2. */
    private final String cert2SubjectDN = "CN=Good CA,O=Test Certificates,C=US";

    /** Test cert 2. */
    private final String cert2 = "MIICbTCCAdagAwIBAgIBAjANBgkqhkiG9w0BAQUFADBAMQswCQYDVQQGEwJVUzEa"
            + "MBgGA1UEChMRVGVzdCBDZXJ0aWZpY2F0ZXMxFTATBgNVBAMTDFRydXN0IEFuY2hv"
            + "cjAeFw0wMTA0MTkxNDU3MjBaFw0xMTA0MTkxNDU3MjBaMDsxCzAJBgNVBAYTAlVT"
            + "MRowGAYDVQQKExFUZXN0IENlcnRpZmljYXRlczEQMA4GA1UEAxMHR29vZCBDQTCB"
            + "nzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEArsI1lQuXKwOxSkOVRaPwlhMQtgp0"
            + "p7HT4rKLGqojfY0twvMDc4rC9uj97wlh98kkraMx3r0wlllYSQ+Cp9mCCNu/C/Y2"
            + "IbZCyG+io4A3Um3q/QGvbHlclmrJb0j0MQi3o88GhE8Q6Vy6SGwFXGpKDJMpLSFp"
            + "Pxz8lh7M6J56Ex8CAwEAAaN8MHowHwYDVR0jBBgwFoAU+2zULYGeyid6ng2wPOqa"
            + "vIf/SeowHQYDVR0OBBYEFLcupoLLwsi8qHsnRNc1M9+aFZTHMA4GA1UdDwEB/wQE"
            + "AwIBBjAXBgNVHSAEEDAOMAwGCmCGSAFlAwIBMAEwDwYDVR0TAQH/BAUwAwEB/zAN"
            + "BgkqhkiG9w0BAQUFAAOBgQCOls9+0kEUS71w+KoQhfkVLdAKANXUmGCVZHL1zsya"
            + "cPP/Q8IsCNvwjefZpgc0cuhtnHt2uDd0/zYLRmgcvJwfx5vwOfmDN13mMB8Za+cg"
            + "3sZ/NI8MqQseKvS3fWqXaK6FJoKLzxId0iUGntbF4c5+rPFArzqM6IE7f9cMD5Fq" + "rA==";

    /* crls/BasicSelfIssuedCRLSigningKeyCACRL.crl */
    /** Test cert issuer DN 1. */
    private final String crl1IssuerDN = "CN=Basic Self-Issued CRL Signing Key CA,O=Test Certificates,C=US";

    /** Test CRL 1. */
    private final String crl1 = "MIIBdTCB3wIBATANBgkqhkiG9w0BAQUFADBYMQswCQYDVQQGEwJVUzEaMBgGA1UE"
            + "ChMRVGVzdCBDZXJ0aWZpY2F0ZXMxLTArBgNVBAMTJEJhc2ljIFNlbGYtSXNzdWVk"
            + "IENSTCBTaWduaW5nIEtleSBDQRcNMDEwNDE5MTQ1NzIwWhcNMTEwNDE5MTQ1NzIw"
            + "WjAiMCACAQMXDTAxMDQxOTE0NTcyMFowDDAKBgNVHRUEAwoBAaAvMC0wHwYDVR0j"
            + "BBgwFoAUD3LKM0OpxBFRq2PaRIcPYaT0vkcwCgYDVR0UBAMCAQEwDQYJKoZIhvcN"
            + "AQEFBQADgYEAXM2Poz2eZPdkc5wsOeLn1w64HD6bHRTcmMKOWh/lRzH9fqfVn1Ix"
            + "yBD30KKEP3fH8bp+JGKtBa4ce//w4s5V9SfTzCR/yB2muM5CBeEG7B+HTNVpjXhZ"
            + "0jOUHDsnaIA9bz2mx58rOZ/Xw4Prd73Mf5azrSRomdEavwUcjD4qAvg=";

    /* These are just randomly generated RSA and DSA public keys using OpenSSL. */

    /** Test RSA key 1. */
    private final String rsaPubKey1 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw/WnsbA8frhQ+8EoPgMr"
            + "QjpINjt20U/MvsvmoAgQnAgEF4OYt9Vj9/2YvMO4NvX1fNDFzoYRyOMrypF7skAP"
            + "cITUhdcPSEpI4nsf5yFZLziK/tQ26RsccE7WhpGB8eHu9tfseelgyioorvmt+JCo"
            + "P15c5rYUuIfVC+eEsYolw344q6N61OACHETuySL0a1+GFu3WoISXte1pQIst7HKv"
            + "BbHH41HEWAxT6e0hlD5PyKL4lBJadGHXg8Zz4r2jV2n6+Ox7raEWmtVCGFxsAoCR"
            + "alu6nvs2++5Nnb4C1SE640esfYhfeMd5JYfsTNMaQ8sZLpsWdglAGpa/Q87K19LI" + "wwIDAQAB";

    /** Test DSA key 1. */
    private final String dsaPubKey1 = "MIIDOjCCAi0GByqGSM44BAEwggIgAoIBAQCWV7IK073aK2C3yggy69qXkxCw30j5"
            + "Ig0s1/GHgq5jEZf8FTGVpehX5qaYlRC3TBMSN4WAgkG+nFnsjHb6kIYkayV8ZVvI"
            + "IgEBCeaZg016f90G+Rre5C38G3OwsODKjPsVZCV5YQ9rm6lWMOfMRSUzJuFA0fdx"
            + "RLssAfKLI5JmzupliO2iH5FU3+dQr0UvcPwPjjRDA9JIi3ShKdmq9f/SzRM9AJPs"
            + "sjc0v4lRVMKWkTHLjbRH2XiOxsok/oL7NVTJ9hvd3xqi1/O3MM2pNhYaQoA0kLqq"
            + "sr006dNftgo8n/zrBFMC6iP7tmxhuRxgXXkNo5xiQCvAX7HsGno4y9ilAhUAjKlv"
            + "CQhbGeQo3fWbwVJMdokSK5ECggEAfERqa+S8UwjuvNxGlisuBGzR7IqqHSQ0cjFI"
            + "BD61CkYh0k0Y9am6ZL2jiAkRICdkW6f9lmGy0HidCwC56WeAYpLyfJslBAjC4r0t"
            + "6U8a822fECVcbsPNLDULoQG0KjVRtYfFH5GedNQ8LRkG8b+XIe4G74+vXOatVu8L"
            + "9QXQKYx9diOAHx8ghpt1pC0UAqPzAgVGNWIPQ+VO7WEYOYuVw+/uFoHiaU1OZOTF"
            + "C4VXk2+33AasT4i6It7DIESp+ye9lPnNU6nLEBNSnXdnBgaH27m8QnFRTfrjimiG"
            + "BwBTQvbjequRvM5dExfUqyCd2BUOK1lbaQmjZnCMH6k3ZFiAYgOCAQUAAoIBAGnD"
            + "wMuRoRGJHUhjjeePKwP9BgCc7dtKlB7QMnIHGPv03hdVPo9ezaQ5mFxdzQdXoLR2"
            + "BFucDtSj1je3e5L9KEnHZ5fHnislBnzSvYR5V8LwTa5mbNS4VHkAv8Eh3WG9tp1S"
            + "/f9ymefKHB7ISlskT7kODCIbr5HHU/n1zXtMRjoslY1A+nFlWiAaIvjnj/C8x0BW"
            + "BkhuSKX/2PbljnmIdGV7mJK9/XUHnyKgZBxXEul2mlvGkrgUvyv+qYsCFsKSSrkB"
            + "1Mj2Ql5xmTMaePMEmvOr6fDAP0OH8cvADEZjx0s/5vvoBFPGGmPrHJluEVS0Fu8I" + "9sROg9YjyuhRV0b8xHo=";

    private X509Certificate xmlCert1, xmlCert2;

    private X509CRL xmlCRL1;

    private X509Data xmlX509Data;

    private KeyInfo keyInfo;

    private KeyValue keyValue;

    private DSAKeyValue xmlDSAKeyValue1, xmlDSAKeyValue1NoParams;

    private RSAKeyValue xmlRSAKeyValue1;

    private int numExpectedCerts;

    private int numExpectedCRLs;

    private java.security.cert.X509Certificate javaCert1;

    private java.security.cert.X509CRL javaCRL1;

    private RSAPublicKey javaRSAPubKey1;

    private DSAPublicKey javaDSAPubKey1;

    private DSAParams javaDSAParams1;

    /**
     * Constructor.
     * 
     */
    public KeyInfoSupportTest() {
        super();
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        xmlCert1 = (X509Certificate) buildXMLObject(X509Certificate.DEFAULT_ELEMENT_NAME);
        xmlCert1.setValue(cert1);

        xmlCert2 = (X509Certificate) buildXMLObject(X509Certificate.DEFAULT_ELEMENT_NAME);
        xmlCert2.setValue(cert2);

        xmlCRL1 = (X509CRL) buildXMLObject(X509CRL.DEFAULT_ELEMENT_NAME);
        xmlCRL1.setValue(crl1);

        xmlX509Data = (X509Data) buildXMLObject(X509Data.DEFAULT_ELEMENT_NAME);
        xmlX509Data.getX509Certificates().add(xmlCert1);
        xmlX509Data.getX509Certificates().add(xmlCert2);
        xmlX509Data.getX509CRLs().add(xmlCRL1);

        keyValue = (KeyValue) buildXMLObject(KeyValue.DEFAULT_ELEMENT_NAME);

        keyInfo = (KeyInfo) buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME);
        keyInfo.getX509Datas().add(xmlX509Data);

        numExpectedCerts = 2;
        numExpectedCRLs = 1;

        javaCert1 = X509Support.decodeCertificate(cert1);
        X509Support.decodeCertificate(cert2);
        javaCRL1 = X509Support.decodeCRL(crl1);

        javaDSAPubKey1 = KeySupport.buildJavaDSAPublicKey(dsaPubKey1);
        javaRSAPubKey1 = KeySupport.buildJavaRSAPublicKey(rsaPubKey1);

        xmlRSAKeyValue1 = (RSAKeyValue) buildXMLObject(RSAKeyValue.DEFAULT_ELEMENT_NAME);
        Modulus modulus = (Modulus) buildXMLObject(Modulus.DEFAULT_ELEMENT_NAME);
        Exponent exponent = (Exponent) buildXMLObject(Exponent.DEFAULT_ELEMENT_NAME);
        modulus.setValueBigInt(javaRSAPubKey1.getModulus());
        exponent.setValueBigInt(javaRSAPubKey1.getPublicExponent());
        xmlRSAKeyValue1.setModulus(modulus);
        xmlRSAKeyValue1.setExponent(exponent);

        xmlDSAKeyValue1 = (DSAKeyValue) buildXMLObject(DSAKeyValue.DEFAULT_ELEMENT_NAME);
        P p = (P) buildXMLObject(P.DEFAULT_ELEMENT_NAME);
        Q q = (Q) buildXMLObject(Q.DEFAULT_ELEMENT_NAME);
        G g = (G) buildXMLObject(G.DEFAULT_ELEMENT_NAME);
        Y y1 = (Y) buildXMLObject(Y.DEFAULT_ELEMENT_NAME);
        p.setValueBigInt(javaDSAPubKey1.getParams().getP());
        q.setValueBigInt(javaDSAPubKey1.getParams().getQ());
        g.setValueBigInt(javaDSAPubKey1.getParams().getG());
        y1.setValueBigInt(javaDSAPubKey1.getY());
        xmlDSAKeyValue1.setP(p);
        xmlDSAKeyValue1.setQ(q);
        xmlDSAKeyValue1.setG(g);
        xmlDSAKeyValue1.setY(y1);

        xmlDSAKeyValue1NoParams = (DSAKeyValue) buildXMLObject(DSAKeyValue.DEFAULT_ELEMENT_NAME);
        Y y2 = (Y) buildXMLObject(Y.DEFAULT_ELEMENT_NAME);
        y2.setValueBigInt(javaDSAPubKey1.getY());
        xmlDSAKeyValue1NoParams.setY(y2);
        javaDSAParams1 = javaDSAPubKey1.getParams();
    }

    /**
     * Test converting XML X509Certificate to java.security.cert.X509Certificate.
     * 
     * @throws CertificateException
     */
    @Test
    public void testCertConversionXMLtoJava() throws CertificateException {
        java.security.cert.X509Certificate javaCert = null;
        try {
            javaCert = KeyInfoSupport.getCertificate(xmlCert1);
        } catch (CertificateException e) {
            Assert.fail("Conversion from XML X509Certificate format to java.security.cert.X509Certificate failed: " + e);
        }
        Assert.assertNotNull(javaCert, "Cert1 was null, failed to convert from XML to Java representation");
        Assert.assertEquals(javaCert.getSubjectX500Principal().getName(X500Principal.RFC2253), cert1SubjectDN,
                "Cert1 SubjectDN");
        Assert.assertEquals(javaCert, X509Support.decodeCertificate(xmlCert1.getValue()),
                "Java cert was not the expected value");

        List<java.security.cert.X509Certificate> javaCertList = null;

        try {
            javaCertList = KeyInfoSupport.getCertificates(xmlX509Data);
        } catch (CertificateException e) {
            Assert.fail("Obtaining certs from X509Data failed: " + e);
        }
        Assert.assertEquals(javaCertList.size(), numExpectedCerts, "# of certs returned");
        Assert.assertEquals(javaCertList.get(0).getSubjectX500Principal().getName(X500Principal.RFC2253), cert1SubjectDN,
                "Cert1 SubjectDN");
        Assert.assertEquals(javaCertList.get(1).getSubjectX500Principal().getName(X500Principal.RFC2253), cert2SubjectDN,
                "Cert2 SubjectDN");

        try {
            javaCertList = KeyInfoSupport.getCertificates(keyInfo);
        } catch (CertificateException e) {
            Assert.fail("Obtaining certs from KeyInfo failed: " + e);
        }
        Assert.assertEquals(javaCertList.size(), numExpectedCerts, "# of certs returned");
        Assert.assertEquals(javaCertList.get(0).getSubjectX500Principal().getName(X500Principal.RFC2253), cert1SubjectDN,
                "Cert1 SubjectDN");
        Assert.assertEquals(javaCertList.get(1).getSubjectX500Principal().getName(X500Principal.RFC2253), cert2SubjectDN,
                "Cert2 SubjectDN");
    }

    /**
     * Test converting XML X509CRL to java.security.cert.X509CRL.
     * 
     * @throws CRLException
     * @throws CertificateException
     */
    @Test
    public void testCRLConversionXMLtoJava() throws CertificateException, CRLException {
        java.security.cert.X509CRL javaCRL = null;
        try {
            javaCRL = KeyInfoSupport.getCRL(xmlCRL1);
        } catch (CRLException e) {
            Assert.fail("Conversion from XML X509CRL format to java.security.cert.X509CRL failed: " + e);
        }
        Assert.assertNotNull(javaCRL, "CRL was null, failed to convert from XML to Java representation");
        Assert.assertEquals(javaCRL.getIssuerX500Principal().getName(X500Principal.RFC2253), crl1IssuerDN, "CRL IssuerDN");
        Assert.assertEquals(javaCRL, X509Support.decodeCRL(xmlCRL1.getValue()),
                "Java CRL was not the expected value");

        List<java.security.cert.X509CRL> javaCRLList = null;

        try {
            javaCRLList = KeyInfoSupport.getCRLs(xmlX509Data);
        } catch (CRLException e) {
            Assert.fail("Obtaining CRLs from X509Data failed: " + e);
        }
        Assert.assertEquals(javaCRLList.size(), numExpectedCRLs, "# of CRLs returned");
        Assert.assertEquals(javaCRLList.get(0).getIssuerX500Principal().getName(X500Principal.RFC2253), crl1IssuerDN,
                "CRL IssuerDN");

        try {
            javaCRLList = KeyInfoSupport.getCRLs(keyInfo);
        } catch (CRLException e) {
            Assert.fail("Obtaining CRLs from KeInfo failed: " + e);
        }
        Assert.assertEquals(javaCRLList.size(), numExpectedCRLs, "# of CRLs returned");
        Assert.assertEquals(javaCRLList.get(0).getIssuerX500Principal().getName(X500Principal.RFC2253), crl1IssuerDN,
                "CRL IssuerDN");

    }

    /**
     * Test converting java.security.cert.X509Certificate to XML X509Certificate.
     * 
     * @throws CertificateException
     */
    @Test
    public void testCertConversionJavaToXML() throws CertificateException {
        X509Certificate xmlCert = null;
        try {
            xmlCert = KeyInfoSupport.buildX509Certificate(javaCert1);
        } catch (CertificateEncodingException e) {
            Assert.fail("Conversion from Java X509Certificate to XMLObject failed: " + e);
        }

        Assert.assertEquals(X509Support.decodeCertificate(xmlCert.getValue()), javaCert1,
                "Java X509Certificate encoding to XMLObject failed");
    }

    /**
     * Test converting java.security.cert.X509CRL to XML X509CRL.
     * 
     * @throws CRLException
     * @throws CertificateException
     */
    @Test
    public void testCRLConversionJavaToXML() throws CertificateException, CRLException {
        X509CRL xmlCRL = null;
        try {
            xmlCRL = KeyInfoSupport.buildX509CRL(javaCRL1);
        } catch (CRLException e) {
            Assert.fail("Conversion from Java X509CRL to XMLObject failed: " + e);
        }

        Assert.assertEquals(X509Support.decodeCRL(xmlCRL.getValue()), javaCRL1,
                "Java X509CRL encoding to XMLObject failed");
    }

    /** Test conversion of DSA public keys from XML to Java security native type. */
    @Test
    public void testDSAConversionXMLToJava() {
        PublicKey key = null;
        DSAPublicKey dsaKey = null;

        try {
            key = KeyInfoSupport.getDSAKey(xmlDSAKeyValue1);
        } catch (KeyException e) {
            Assert.fail("DSA key conversion XML to Java failed: " + e);
        }
        dsaKey = (DSAPublicKey) key;
        Assert.assertNotNull(dsaKey, "Generated key was not an instance of DSAPublicKey");
        Assert.assertEquals(dsaKey, javaDSAPubKey1, "Generated key was not the expected value");

        try {
            key = KeyInfoSupport.getDSAKey(xmlDSAKeyValue1NoParams, javaDSAParams1);
        } catch (KeyException e) {
            Assert.fail("DSA key conversion XML to Java failed: " + e);
        }
        dsaKey = (DSAPublicKey) key;
        Assert.assertNotNull(dsaKey, "Generated key was not an instance of DSAPublicKey");
        Assert.assertEquals(dsaKey, javaDSAPubKey1, "Generated key was not the expected value");

        try {
            key = KeyInfoSupport.getDSAKey(xmlDSAKeyValue1NoParams);
            Assert.fail("DSA key conversion XML to Java failed should have thrown an exception but didn't");
        } catch (KeyException e) {
            // do nothing, we expect to fail b/c not complete set of DSAParams
        }
    }

    /** Test conversion of RSA public keys from XML to Java security native type. */
    @Test
    public void testRSAConversionXMLToJava() {
        PublicKey key = null;
        RSAPublicKey rsaKey = null;

        try {
            key = KeyInfoSupport.getRSAKey(xmlRSAKeyValue1);
        } catch (KeyException e) {
            Assert.fail("RSA key conversion XML to Java failed: " + e);
        }
        rsaKey = (RSAPublicKey) key;
        Assert.assertNotNull(rsaKey, "Generated key was not an instance of RSAPublicKey");
        Assert.assertEquals(rsaKey, javaRSAPubKey1, "Generated key was not the expected value");
    }

    /** Test conversion of DSA public keys from Java security native type to XML. */
    @Test
    public void testDSAConversionJavaToXML() {
        DSAKeyValue dsaKeyValue = KeyInfoSupport.buildDSAKeyValue(javaDSAPubKey1);
        Assert.assertNotNull("Generated DSAKeyValue was null");
        Assert.assertEquals(dsaKeyValue
                .getY().getValueBigInt(), javaDSAPubKey1.getY(), "Generated DSAKeyValue Y component was not the expected value");
        Assert.assertEquals(dsaKeyValue.getP().getValueBigInt(), javaDSAPubKey1.getParams().getP(),
                "Generated DSAKeyValue P component was not the expected value");
        Assert.assertEquals(dsaKeyValue.getQ().getValueBigInt(), javaDSAPubKey1.getParams().getQ(),
                "Generated DSAKeyValue Q component was not the expected value");
        Assert.assertEquals(dsaKeyValue.getG().getValueBigInt(), javaDSAPubKey1.getParams().getG(),
                "Generated DSAKeyValue G component was not the expected value");
    }

    /** Test conversion of RSA public keys from Java security native type to XML. */
    @Test
    public void testRSAConversionJavaToXML() {
        RSAKeyValue rsaKeyValue = KeyInfoSupport.buildRSAKeyValue(javaRSAPubKey1);
        Assert.assertNotNull("Generated RSAKeyValue was null");
        Assert.assertEquals(rsaKeyValue.getModulus().getValueBigInt(), javaRSAPubKey1.getModulus(),
                "Generated RSAKeyValue modulus component was not the expected value");
        Assert.assertEquals(rsaKeyValue.getExponent().getValueBigInt(),
                javaRSAPubKey1.getPublicExponent(), "Generated RSAKeyValue exponent component was not the expected value");
    }

    /** Tests extracting a DSA public key from a KeyValue. */
    @Test
    public void testGetDSAKey() {
        keyValue.setRSAKeyValue(null);
        keyValue.setDSAKeyValue(xmlDSAKeyValue1);

        PublicKey pk = null;
        DSAPublicKey dsaKey = null;
        try {
            pk = KeyInfoSupport.getKey(keyValue);
        } catch (KeyException e) {
            Assert.fail("Extraction of key from KeyValue failed: " + e);
        }
        Assert.assertTrue(pk instanceof DSAPublicKey, "Generated key was not an instance of DSAPublicKey");
        dsaKey = (DSAPublicKey) pk;
        Assert.assertEquals(dsaKey, javaDSAPubKey1, "Generated key was not the expected value");

        keyValue.setDSAKeyValue(null);
    }

    /** Tests extracting a RSA public key from a KeyValue. */
    @Test
    public void testGetRSAKey() {
        keyValue.setDSAKeyValue(null);
        keyValue.setRSAKeyValue(xmlRSAKeyValue1);

        PublicKey pk = null;
        RSAPublicKey rsaKey = null;
        try {
            pk = KeyInfoSupport.getKey(keyValue);
        } catch (KeyException e) {
            Assert.fail("Extraction of key from KeyValue failed: " + e);
        }
        Assert.assertTrue(pk instanceof RSAPublicKey, "Generated key was not an instance of RSAPublicKey");
        rsaKey = (RSAPublicKey) pk;
        Assert.assertEquals(rsaKey, javaRSAPubKey1, "Generated key was not the expected value");

        keyValue.setRSAKeyValue(null);
    }

    /** Tests adding a public key as a KeyValue to KeyInfo. */
    @Test
    public void testAddDSAPublicKey() {
        keyInfo.getKeyValues().clear();

        KeyInfoSupport.addPublicKey(keyInfo, javaDSAPubKey1);
        KeyValue kv = keyInfo.getKeyValues().get(0);
        Assert.assertNotNull(kv, "KeyValue was null");
        DSAKeyValue dsaKeyValue = kv.getDSAKeyValue();
        Assert.assertNotNull(dsaKeyValue, "DSAKeyValue was null");

        DSAPublicKey javaKey = null;
        try {
            javaKey = (DSAPublicKey) KeyInfoSupport.getDSAKey(dsaKeyValue);
        } catch (KeyException e) {
            Assert.fail("Extraction of Java key failed: " + e);
        }

        Assert.assertEquals(javaKey, javaDSAPubKey1, "Inserted DSA public key was not the expected value");

        keyInfo.getKeyValues().clear();
    }    

    /** Tests adding a public key as a KeyValue to KeyInfo. */
    @Test
    public void testAddRSAPublicKey() {
        keyInfo.getKeyValues().clear();

        KeyInfoSupport.addPublicKey(keyInfo, javaRSAPubKey1);
        KeyValue kv = keyInfo.getKeyValues().get(0);
        Assert.assertNotNull(kv, "KeyValue was null");
        RSAKeyValue rsaKeyValue = kv.getRSAKeyValue();
        Assert.assertNotNull(rsaKeyValue, "RSAKeyValue was null");

        RSAPublicKey javaKey = null;
        try {
            javaKey = (RSAPublicKey) KeyInfoSupport.getRSAKey(rsaKeyValue);
        } catch (KeyException e) {
            Assert.fail("Extraction of Java key failed: " + e);
        }

        Assert.assertEquals(javaKey, javaRSAPubKey1, "Inserted RSA public key was not the expected value");

        keyInfo.getKeyValues().clear();
    }

    /** Tests adding a public key as a DEREncodedKeyValue to KeyInfo. */
    @Test
    public void testAddDEREncodedDSAPublicKey() {
       keyInfo.getXMLObjects(DEREncodedKeyValue.DEFAULT_ELEMENT_NAME).clear();
        
        try {
            KeyInfoSupport.addDEREncodedPublicKey(keyInfo, javaDSAPubKey1);
        } catch (NoSuchAlgorithmException e) {
            Assert.fail("Unsupported key algorithm: " + e);
        } catch (InvalidKeySpecException e) {
            Assert.fail("Unsupported key specification: " + e);
        }
        DEREncodedKeyValue kv = (DEREncodedKeyValue) keyInfo.getXMLObjects(DEREncodedKeyValue.DEFAULT_ELEMENT_NAME).get(0);
        Assert.assertNotNull(kv, "KeyValue was null");
        
        DSAPublicKey javaKey = null;
        try {
            javaKey = (DSAPublicKey) KeyInfoSupport.getKey(kv);
        } catch (KeyException e) {
            Assert.fail("Extraction of Java key failed: " + e);
        }
        
        Assert.assertEquals(javaDSAPubKey1, javaKey, "Inserted RSA public key was not the expected value");
        
        keyInfo.getXMLObjects(DEREncodedKeyValue.DEFAULT_ELEMENT_NAME).clear();
    }
    
    /** Tests adding a public key as a DEREncodedKeyValue to KeyInfo. */
    @Test
    public void testAddDEREncodedRSAPublicKey() {
       keyInfo.getXMLObjects(DEREncodedKeyValue.DEFAULT_ELEMENT_NAME).clear();
        
        try {
            KeyInfoSupport.addDEREncodedPublicKey(keyInfo, javaRSAPubKey1);
        } catch (NoSuchAlgorithmException e) {
            Assert.fail("Unsupported key algorithm: " + e);
        } catch (InvalidKeySpecException e) {
            Assert.fail("Unsupported key specification: " + e);
        }
        DEREncodedKeyValue kv = (DEREncodedKeyValue) keyInfo.getXMLObjects(DEREncodedKeyValue.DEFAULT_ELEMENT_NAME).get(0);
        Assert.assertNotNull(kv, "KeyValue was null");
        
        RSAPublicKey javaKey = null;
        try {
            javaKey = (RSAPublicKey) KeyInfoSupport.getKey(kv);
        } catch (KeyException e) {
            Assert.fail("Extraction of Java key failed: " + e);
        }
        
        Assert.assertEquals(javaRSAPubKey1, javaKey, "Inserted RSA public key was not the expected value");
        
        keyInfo.getXMLObjects(DEREncodedKeyValue.DEFAULT_ELEMENT_NAME).clear();
    }
    
    /**
     * Tests adding a certificate as a X509Data/X509Certificate to KeyInfo.
     * 
     * @throws CertificateException
     */
    @Test
    public void testAddX509Certificate() throws CertificateException {
        keyInfo.getX509Datas().clear();

        KeyInfoSupport.addCertificate(keyInfo, javaCert1);
        X509Data x509Data = keyInfo.getX509Datas().get(0);
        Assert.assertNotNull(x509Data, "X509Data was null");
        X509Certificate x509Cert = x509Data.getX509Certificates().get(0);
        Assert.assertNotNull(x509Cert, "X509Certificate was null");

        java.security.cert.X509Certificate javaCert = null;
        javaCert = (java.security.cert.X509Certificate) KeyInfoSupport.getCertificate(x509Cert);

        Assert.assertEquals(javaCert, javaCert1, "Inserted X509Certificate was not the expected value");

        keyInfo.getX509Datas().clear();
    }

    /**
     * Tests adding a CRL as a X509Data/X509CRL to KeyInfo.
     * 
     * @throws CRLException
     */
    @Test
    public void testAddX509CRL() throws CRLException {
        keyInfo.getX509Datas().clear();

        KeyInfoSupport.addCRL(keyInfo, javaCRL1);
        X509Data x509Data = keyInfo.getX509Datas().get(0);
        Assert.assertNotNull(x509Data, "X509Data was null");
        X509CRL x509CRL = x509Data.getX509CRLs().get(0);
        Assert.assertNotNull(x509CRL, "X509CRL was null");

        java.security.cert.X509CRL javaCRL = null;
        javaCRL = (java.security.cert.X509CRL) KeyInfoSupport.getCRL(x509CRL);

        Assert.assertEquals(javaCRL, javaCRL1, "Inserted X509CRL was not the expected value");

        keyInfo.getX509Datas().clear();
    }

    /** Tests building a new X509SubjectName. */
    @Test
    public void testBuildSubjectName() {
        String name = "cn=foobar.example.org, o=Internet2";
        X509SubjectName xmlSubjectName = KeyInfoSupport.buildX509SubjectName(name);
        Assert.assertNotNull(xmlSubjectName, "Constructed X509SubjectName was null");
        Assert.assertEquals(xmlSubjectName.getValue(), name, "Unexpected subject name value");
    }

    /** Tests building a new X509IssuerSerial. */
    @Test
    public void testBuildIssuerSerial() {
        String name = "cn=CA.example.org, o=Internet2";
        BigInteger serialNumber = new BigInteger("42");
        X509IssuerSerial xmlIssuerSerial = KeyInfoSupport.buildX509IssuerSerial(name, serialNumber);
        Assert.assertNotNull(xmlIssuerSerial, "Constructed X509IssuerSerial was null");

        Assert.assertNotNull(xmlIssuerSerial.getX509IssuerName(), "Constructed X509IssuerName was null");
        Assert.assertEquals(xmlIssuerSerial.getX509IssuerName().getValue(), name, "Unexpected issuer name value");

        Assert.assertNotNull(xmlIssuerSerial.getX509SerialNumber(), "Constructed X509SerialNumber was null");
        Assert.assertEquals(xmlIssuerSerial.getX509SerialNumber().getValue(), serialNumber, "Unexpected serial number");
    }

    /**
     * Tests building a new X509SKI from a certificate containing an SKI value.
     * 
     * @throws CertificateException
     */
    @Test
    public void testBuildSubjectKeyIdentifier() throws CertificateException {
        byte[] skiValue = Base64Support.decode(cert1SKIPlainBase64);
        X509SKI xmlSKI = KeyInfoSupport.buildX509SKI(javaCert1);
        Assert.assertNotNull(xmlSKI, "Constructed X509SKI was null");
        Assert.assertFalse(Strings.isNullOrEmpty(xmlSKI.getValue()), "SKI value was empty");
        byte[] xmlValue = Base64Support.decode(xmlSKI.getValue());
        Assert.assertNotNull(xmlValue, "Decoded XML SKI value was null");
        Assert.assertTrue(Arrays.equals(skiValue, xmlValue), "Incorrect SKI value");

        // Test that a cert with no SKI produces null
        java.security.cert.X509Certificate noExtCert = X509Support.decodeCertificate(certNoExtensions);
        Assert.assertNotNull(noExtCert);
        X509SKI noExtXMLSKI = KeyInfoSupport.buildX509SKI(noExtCert);
        Assert.assertNull(noExtXMLSKI, "Building X509SKI from cert without SKI should have generated null");
    }

    /**
     * Tests building a new X509Digest from a certificate.
     * @throws CertificateException 
     * @throws NoSuchAlgorithmException
     */
    @Test
    public void testBuildDigest() throws CertificateException {
        byte[] digestValue = Base64Support.decode(cert1DigestBase64);
        X509Digest xmlDigest = null;
        try {
            xmlDigest = KeyInfoSupport.buildX509Digest(javaCert1, SignatureConstants.ALGO_ID_DIGEST_SHA1);
        } catch (NoSuchAlgorithmException e) {
            Assert.fail("Digest algorithm missing: " + e);
        }
        Assert.assertNotNull(xmlDigest, "Constructed X509Digest was null");
        Assert.assertFalse(Strings.isNullOrEmpty(xmlDigest.getValue()), "Digest value was empty");
        byte[] xmlValue = Base64Support.decode(xmlDigest.getValue());
        Assert.assertNotNull(xmlValue, "Decoded X509Digest value was null");
        Assert.assertTrue(Arrays.equals(digestValue, xmlValue), "Incorrect digest value");
    }
}