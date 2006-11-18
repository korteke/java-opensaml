/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xml.signature;

import java.security.cert.CertificateException;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.opensaml.xml.XMLObjectBaseTestCase;

/**
 *
 */
public class KeyInfoHelperTest extends XMLObjectBaseTestCase {
    
    /* These test examples are from the NIST PKI path processing test suite:
     * http://csrc.nist.gov/pki/testing/x509paths.html
     * Data file:
     * http://csrc.nist.gov/pki/testing/PKITS_data.zip
     */
    
    /* certs/BasicSelfIssuedNewKeyCACert.crt */
    private final String cert1SubjectDN = "CN=Basic Self-Issued New Key CA,O=Test Certificates,C=US";
    private final String cert1 = 
        "MIICgjCCAeugAwIBAgIBEzANBgkqhkiG9w0BAQUFADBAMQswCQYDVQQGEwJVUzEa" +
        "MBgGA1UEChMRVGVzdCBDZXJ0aWZpY2F0ZXMxFTATBgNVBAMTDFRydXN0IEFuY2hv" +
        "cjAeFw0wMTA0MTkxNDU3MjBaFw0xMTA0MTkxNDU3MjBaMFAxCzAJBgNVBAYTAlVT" +
        "MRowGAYDVQQKExFUZXN0IENlcnRpZmljYXRlczElMCMGA1UEAxMcQmFzaWMgU2Vs" +
        "Zi1Jc3N1ZWQgTmV3IEtleSBDQTCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEA" +
        "tCkygqcMEOy3i8p6ZV3685us1lOugSU4pUMRJNRH/lV2ykesk+JRcQy1s7WS12j9" +
        "GCnSJ919/TgeKLmV3ps1fC1B8HziC0mzBAr+7f5LkJqSf0kS0kfpyLOoO8VSJCip" +
        "/8uENkSkpvX+Lak96OKzhtyvi4KpUdQKfwpg6xUqakECAwEAAaN8MHowHwYDVR0j" +
        "BBgwFoAU+2zULYGeyid6ng2wPOqavIf/SeowHQYDVR0OBBYEFK+5+R3CRRjMuCHi" +
        "p0e8Sb0ZtXgoMA4GA1UdDwEB/wQEAwIBBjAXBgNVHSAEEDAOMAwGCmCGSAFlAwIB" +
        "MAEwDwYDVR0TAQH/BAUwAwEB/zANBgkqhkiG9w0BAQUFAAOBgQCuRBfDy2gSPp2k" +
        "ZR7OAvt+xDx4toJ9ImImUvJ94AOLd6Uxsi2dvQT5HLrIBrTYsSfQj1pA50XY2F7k" +
        "3eM/+JhYCcyZD9XtAslpOkjwACPJnODFAY8PWC00CcOxGb6q+S/VkrCwvlBeMjev" +
        "IH4bHvAymWsZndBZhcG8gBmDrZMwhQ==";
    
    /* certs/GoodCACert.crt */
    private final String cert2SubjectDN = "CN=Good CA,O=Test Certificates,C=US";
    private final String cert2 = 
        "MIICbTCCAdagAwIBAgIBAjANBgkqhkiG9w0BAQUFADBAMQswCQYDVQQGEwJVUzEa" +
        "MBgGA1UEChMRVGVzdCBDZXJ0aWZpY2F0ZXMxFTATBgNVBAMTDFRydXN0IEFuY2hv" +
        "cjAeFw0wMTA0MTkxNDU3MjBaFw0xMTA0MTkxNDU3MjBaMDsxCzAJBgNVBAYTAlVT" +
        "MRowGAYDVQQKExFUZXN0IENlcnRpZmljYXRlczEQMA4GA1UEAxMHR29vZCBDQTCB" +
        "nzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEArsI1lQuXKwOxSkOVRaPwlhMQtgp0" +
        "p7HT4rKLGqojfY0twvMDc4rC9uj97wlh98kkraMx3r0wlllYSQ+Cp9mCCNu/C/Y2" +
        "IbZCyG+io4A3Um3q/QGvbHlclmrJb0j0MQi3o88GhE8Q6Vy6SGwFXGpKDJMpLSFp" +
        "Pxz8lh7M6J56Ex8CAwEAAaN8MHowHwYDVR0jBBgwFoAU+2zULYGeyid6ng2wPOqa" +
        "vIf/SeowHQYDVR0OBBYEFLcupoLLwsi8qHsnRNc1M9+aFZTHMA4GA1UdDwEB/wQE" +
        "AwIBBjAXBgNVHSAEEDAOMAwGCmCGSAFlAwIBMAEwDwYDVR0TAQH/BAUwAwEB/zAN" +
        "BgkqhkiG9w0BAQUFAAOBgQCOls9+0kEUS71w+KoQhfkVLdAKANXUmGCVZHL1zsya" +
        "cPP/Q8IsCNvwjefZpgc0cuhtnHt2uDd0/zYLRmgcvJwfx5vwOfmDN13mMB8Za+cg" +
        "3sZ/NI8MqQseKvS3fWqXaK6FJoKLzxId0iUGntbF4c5+rPFArzqM6IE7f9cMD5Fq" +
        "rA==";
    
    /* crls/BasicSelfIssuedCRLSigningKeyCACRL.crl */
    private final String crl1IssuerDN = "CN=Basic Self-Issued CRL Signing Key CA,O=Test Certificates,C=US";
    private final String crl1 = 
        "MIIBdTCB3wIBATANBgkqhkiG9w0BAQUFADBYMQswCQYDVQQGEwJVUzEaMBgGA1UE" +
        "ChMRVGVzdCBDZXJ0aWZpY2F0ZXMxLTArBgNVBAMTJEJhc2ljIFNlbGYtSXNzdWVk" +
        "IENSTCBTaWduaW5nIEtleSBDQRcNMDEwNDE5MTQ1NzIwWhcNMTEwNDE5MTQ1NzIw" +
        "WjAiMCACAQMXDTAxMDQxOTE0NTcyMFowDDAKBgNVHRUEAwoBAaAvMC0wHwYDVR0j" +
        "BBgwFoAUD3LKM0OpxBFRq2PaRIcPYaT0vkcwCgYDVR0UBAMCAQEwDQYJKoZIhvcN" +
        "AQEFBQADgYEAXM2Poz2eZPdkc5wsOeLn1w64HD6bHRTcmMKOWh/lRzH9fqfVn1Ix" +
        "yBD30KKEP3fH8bp+JGKtBa4ce//w4s5V9SfTzCR/yB2muM5CBeEG7B+HTNVpjXhZ" +
        "0jOUHDsnaIA9bz2mx58rOZ/Xw4Prd73Mf5azrSRomdEavwUcjD4qAvg=";
    
    

    private X509Certificate xmlCert1, xmlCert2;
    private X509CRL xmlCRL1;
    private X509Data xmlX509Data;
    private KeyInfo keyInfo;
    private int numExpectedCerts;
    private int numExpectedCRLs;
    


    /**
     * Constructor
     *
     */
    public KeyInfoHelperTest() {
        super();
    }

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        
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
        
        keyInfo = (KeyInfo) buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME);
        keyInfo.getX509Datas().add(xmlX509Data);
        
        numExpectedCerts = 2;
        numExpectedCRLs = 1;
    }



    public void testCertConversionXMLtoJava() {
        java.security.cert.X509Certificate javaCert = null;
        try {
            javaCert = KeyInfoHelper.getCertificate(xmlCert1);
        } catch (CertificateException e) {
            fail("Conversion from XML X509Certificate format to java.security.cert.X509Certificate failed: " + e);
        }
        assertNotNull("Cert1 was null, failed to convert from XML to Java representation", javaCert);
        assertEquals("Cert1 SubjectDN", cert1SubjectDN, javaCert.getSubjectX500Principal().getName(X500Principal.RFC2253));
        
        List<java.security.cert.X509Certificate> javaCertList = null;
        
        try {
            javaCertList = KeyInfoHelper.getCertificates(xmlX509Data);
        } catch (CertificateException e) {
            fail("Obtaining certs from X509Data failed: " + e);
        }
        assertEquals("# of certs returned", numExpectedCerts, javaCertList.size());
        assertEquals("Cert1 SubjectDN", cert1SubjectDN, javaCertList.get(0).getSubjectX500Principal().getName(X500Principal.RFC2253));
        assertEquals("Cert2 SubjectDN", cert2SubjectDN, javaCertList.get(1).getSubjectX500Principal().getName(X500Principal.RFC2253));
        
        try {
            javaCertList = KeyInfoHelper.getCertificates(keyInfo);
        } catch (CertificateException e) {
            fail("Obtaining certs from KeyInfo failed: " + e);
        }
        assertEquals("# of certs returned", numExpectedCerts, javaCertList.size());
        assertEquals("Cert1 SubjectDN", cert1SubjectDN, javaCertList.get(0).getSubjectX500Principal().getName(X500Principal.RFC2253));
        assertEquals("Cert2 SubjectDN", cert2SubjectDN, javaCertList.get(1).getSubjectX500Principal().getName(X500Principal.RFC2253));
        
    }
    
    public void testCRLConversionXMLtoJava() {
        java.security.cert.X509CRL javaCRL = null;
        try {
            javaCRL = KeyInfoHelper.getCRL(xmlCRL1);
        } catch (Exception e) {
            fail("Conversion from XML X509CRL format to java.security.cert.X509CRL failed: " + e);
        }
        assertNotNull("CRL was null, failed to convert from XML to Java representation", javaCRL);
        assertEquals("CRL IssuerDN", crl1IssuerDN, javaCRL.getIssuerX500Principal().getName(X500Principal.RFC2253));
        
        
        List<java.security.cert.X509CRL> javaCRLList = null;
        
        try {
            javaCRLList = KeyInfoHelper.getCRLs(xmlX509Data);
        } catch (Exception e) {
            fail("Obtaining CRLs from X509Data failed: " + e);
        }
        assertEquals("# of CRLs returned", numExpectedCRLs, javaCRLList.size());
        assertEquals("CRL IssuerDN", crl1IssuerDN, javaCRLList.get(0).getIssuerX500Principal().getName(X500Principal.RFC2253));
        
        try {
            javaCRLList = KeyInfoHelper.getCRLs(keyInfo);
        } catch (Exception e) {
            fail("Obtaining CRLs from KeInfo failed: " + e);
        }
        assertEquals("# of CRLs returned", numExpectedCRLs, javaCRLList.size());
        assertEquals("CRL IssuerDN", crl1IssuerDN, javaCRLList.get(0).getIssuerX500Principal().getName(X500Principal.RFC2253));
        
    }
    

}
