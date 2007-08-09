/*
 * Copyright [2007] [University Corporation for Advanced Internet Development, Inc.]
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

package org.opensaml.ws.security.provider;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.opensaml.ws.security.BaseSecurityPolicyTest;
import org.opensaml.ws.security.ServletRequestX509CredentialAdapter;
import org.opensaml.xml.security.SecurityTestHelper;
import org.opensaml.xml.security.credential.CollectionCredentialResolver;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.trust.ExplicitKeyTrustEngine;
import org.opensaml.xml.security.trust.TrustEngine;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.security.x509.X500DNHandler;
import org.opensaml.xml.security.x509.X509Util;

/**
 * Test client certificate authentication security policy rule.
 */
public class ClientCertAuthRuleTest extends BaseSecurityPolicyTest {
    
//    private String entitySubjectDN;
//    private String entityCN;
//    private String entityDNSAltName;
//    private String entityURIAltName;
//    
//    private X509Certificate entityCert;
//    private String entityCertBase64 = 
//        "MIIDzjCCAragAwIBAgIBMTANBgkqhkiG9w0BAQUFADAtMRIwEAYDVQQKEwlJbnRl" +
//        "cm5ldDIxFzAVBgNVBAMTDmNhLmV4YW1wbGUub3JnMB4XDTA3MDUyMTE4MjM0MFoX" +
//        "DTE3MDUxODE4MjM0MFowMTESMBAGA1UEChMJSW50ZXJuZXQyMRswGQYDVQQDExJm" +
//        "b29iYXIuZXhhbXBsZS5vcmcwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIB" +
//        "AQDNWnkFmhy1vYa6gN/xBRKkZxFy3sUq2V0LsYb6Q3pe9Qlb6+BzaM5DrN8uIqqr" +
//        "oBE3Wp0LtrgKuQTpDpNFBdS2p5afiUtOYLWBDtizTOzs3Z36MGMjIPUYQ4s03IP3" +
//        "yPh2ud6EKpDPiYqzNbkRaiIwmYSit5r+RMYvd6fuKvTOn6h7PZI5AD7Rda7VWh5O" +
//        "VSoZXlRx3qxFho+mZhW0q4fUfTi5lWwf4EhkfBlzgw/k5gf4cOi6rrGpRS1zxmbt" +
//        "X1RAg+I20z6d04g0N2WsK5stszgYKoIROJCiXwjraa8/SoFcILolWQpttVHBIUYl" +
//        "yDlm8mIFleZf4ReFpfm+nUYxAgMBAAGjgfQwgfEwCQYDVR0TBAIwADAsBglghkgB" +
//        "hvhCAQ0EHxYdT3BlblNTTCBHZW5lcmF0ZWQgQ2VydGlmaWNhdGUwHQYDVR0OBBYE" +
//        "FDgRgTkjaKoK6DoZfUZ4g9LDJUWuMFUGA1UdIwROMEyAFNXuZVPeUdqHrULqQW7y" +
//        "r9buRpQLoTGkLzAtMRIwEAYDVQQKEwlJbnRlcm5ldDIxFzAVBgNVBAMTDmNhLmV4" +
//        "YW1wbGUub3JnggEBMEAGA1UdEQQ5MDeCEmFzaW1vdi5leGFtcGxlLm9yZ4YbaHR0" +
//        "cDovL2hlaW5sZWluLmV4YW1wbGUub3JnhwQKAQIDMA0GCSqGSIb3DQEBBQUAA4IB" +
//        "AQBLiDMyQ60ldIytVO1GCpp1S1sKJyTF56GVxHh/82hiRFbyPu+2eSl7UcJfH4ZN" +
//        "bAfHL1vDKTRJ9zoD8WRzpOCUtT0IPIA/Ex+8lFzZmujO10j3TMpp8Ii6+auYwi/T" +
//        "osrfw1YCxF+GI5KO49CfDRr6yxUbMhbTN+ssK4UzFf36UbkeJ3EfDwB0WU70jnlk" +
//        "yO8f97X6mLd5QvRcwlkDMftP4+MB+inTlxDZ/w8NLXQoDW6p/8r91bupXe0xwuyE" +
//        "vow2xjxlzVcux2BZsUZYjBa07ZmNNBtF7WaQqH7l2OBCAdnBhvme5i/e0LK3Ivys" +
//        "+hcVyvCXs5XtFTFWDAVYvzQ6";
//        
//    private X509Certificate otherCert1;
//    private String otherCert1Base64 = 
//        "MIIECTCCAvGgAwIBAgIBMzANBgkqhkiG9w0BAQUFADAtMRIwEAYDVQQKEwlJbnRl" +
//        "cm5ldDIxFzAVBgNVBAMTDmNhLmV4YW1wbGUub3JnMB4XDTA3MDUyNTIwMTYxMVoX" +
//        "DTE3MDUyMjIwMTYxMVowGjEYMBYGA1UEAxMPaWRwLmV4YW1wbGUub3JnMIIBtjCC" +
//        "ASsGByqGSM44BAEwggEeAoGBAI+ktw7R9m7TxjaCrT2MHwWNQUAyXPrqbFCcu+DC" +
//        "irr861U6R6W/GyqWdcy8/D1Hh/I1U94POQn5yfqVPpVH2ZRS4OMFndHWaoo9V5LJ" +
//        "oXTXHiDYB3W4t9tn0fm7It0n7VoUI5C4y9LG32Hq+UIGF/ktNTmo//mEqLS6aJNd" +
//        "bMFpAhUArmKGh0hcpmjukYArWcMRvipB4CMCgYBuCiCrUaHBRRtqrk0P/Luq0l2M" +
//        "2718GwSGeLPZip06gACDG7IctMrgH1J+ZIjsx6vffi977wnMDiktqacmaobV+SCR" +
//        "W9ijJRdkYpUHmlLvuJGnDPjkvewpbGWJsCabpWEvWdYw3ma8RuHOPj4Jkrdd4VcR" +
//        "aFwox/fPJ7cG6kBydgOBhAACgYBxQIPv9DCsmiMHG1FAxSARX0GcRiELJPJ+MtaS" +
//        "tdTrVobNa2jebwc3npLiTvUR4U/CDo1mSZb+Sp/wian8kNZHmGcR6KbtJs9UDsa3" +
//        "V0pbbgpUar4HcxV+NQJBbhn9RGu85g3PDILUrINiUAf26mhPN5Y0paM+HbM68nUf" +
//        "1OLv16OBsjCBrzAJBgNVHRMEAjAAMCwGCWCGSAGG+EIBDQQfFh1PcGVuU1NMIEdl" +
//        "bmVyYXRlZCBDZXJ0aWZpY2F0ZTAdBgNVHQ4EFgQUIHFAEB/3jIIZzJEJ/qdsuI8v" +
//        "N3kwVQYDVR0jBE4wTIAU1e5lU95R2oetQupBbvKv1u5GlAuhMaQvMC0xEjAQBgNV" +
//        "BAoTCUludGVybmV0MjEXMBUGA1UEAxMOY2EuZXhhbXBsZS5vcmeCAQEwDQYJKoZI" +
//        "hvcNAQEFBQADggEBAJt4Q34+pqjW5tHHhkdzTITSBjOOf8EvYMgxTMRzhagLSHTt" +
//        "9RgO5i/G7ELvnwe1j6187m1XD9iEAWKeKbB//ljeOpgnwzkLR9Er5tr1RI3cbil0" +
//        "AX+oX0c1jfRaQnR50Rfb5YoNX6G963iphlxp9C8VLB6eOk/S270XoWoQIkO1ioQ8" +
//        "JY4HE6AyDsOpJaOmHpBaxjgsiko52ZWZeZyaCyL98BXwVxeml7pYnHlXWWidB0N/" +
//        "Zy+LbvWg3urUkiDjMcB6nGImmEfDSxRdybitcMwbwL26z2WOpwL3llm3mcCydKXg" +
//        "Xt8IQhfDhOZOHWckeD2tStnJRP/cqBgO62/qirw=";
//    
//    private CollectionCredentialResolver resolver;
//    private List<Credential> credentials;
//    private BasicX509Credential entityX509Cred;
//    private ClientCertAuthRuleFactory certAuthFactory;
//    
//    private StaticIssuerRuleFactory issuerFactory;
//    private String staticIssuer;
//
//    /** {@inheritDoc} */
//    protected void setUp() throws Exception {
//        super.setUp();
//        
//        // RFC2253 format
//        entitySubjectDN = "CN=foobar.example.org,O=Internet2";
//        entityCN = "foobar.example.org";
//        entityDNSAltName = "asimov.example.org";
//        entityURIAltName = "http://heinlein.example.org";
//        staticIssuer = "SomeCoolIssuer";
//        
//        entityCert = SecurityTestHelper.buildJavaX509Cert(entityCertBase64);
//        otherCert1 = SecurityTestHelper.buildJavaX509Cert(otherCert1Base64);
//        
//        entityX509Cred = new BasicX509Credential();
//        entityX509Cred.setEntityCertificate(entityCert);
//        entityX509Cred.setEntityId(staticIssuer);
//        
//        BasicX509Credential otherCred1 = new BasicX509Credential();
//        otherCred1.setEntityCertificate(otherCert1);
//        otherCred1.setEntityId("other-1");
//        
//        credentials = new ArrayList<Credential>();
//        credentials.add(otherCred1);
//        
//        resolver = new CollectionCredentialResolver(credentials);
//        ExplicitKeyTrustEngine engine = new ExplicitKeyTrustEngine(resolver);
//        
//        certAuthFactory = new ClientCertAuthRuleFactory();
//        certAuthFactory.setTrustEngine((TrustEngine) engine);
//        
//        issuerFactory = new StaticIssuerRuleFactory();
//        issuerFactory.setIssuer(staticIssuer);
//        
//        getPolicyRuleFactories().add(issuerFactory);
//        getPolicyRuleFactories().add(certAuthFactory);
//        
//        httpRequest.setAttribute(ServletRequestX509CredentialAdapter.X509_CERT_REQUEST_ATTRIBUTE, 
//                new X509Certificate[] { entityCert } );
//        confirmRequestCert();
//    }
//
//    /**
//     * Test trusted client. Prior context issuer set.
//     */
//    public void testClientTrusted() {
//        resolver.getCollection().add(entityX509Cred);
//        assertPolicySuccess("Policy should pass");
//        assertEquals("Issuer authentication should have been true", Boolean.TRUE, policy.isIssuerAuthenticated());
//    }
//    
//    /**
//     * Test untrusted client, auth not required. Prior context issuer set.
//     */
//    public void testClientNotTrusted() {
//        policyFactory.setRequireAuthenticatedIssuer(false);
//        assertPolicySuccess("Policy should pass, client is not trusted");
//        assertEquals("Issuer authentication should have been false", Boolean.FALSE, policy.isIssuerAuthenticated() );
//    }
//    
//    /**
//     * Test untrusted client, auth required. Prior context issuer set.
//     */
//    public void testClientNotTrustedAuthRequired() {
//        policyFactory.setRequireAuthenticatedIssuer(true);
//        assertPolicyFail("Policy should NOT pass with untrusted client cert, auth required");
//    }
//    
//    /**
//     * Test trusted client.  No context issuer. Derive issuer from subject DN.
//     */
//    public void testCertIssuerDerivationSubjectDN() {
//        entityX509Cred.setEntityId(entitySubjectDN);
//        resolver.getCollection().add(entityX509Cred);
//        getPolicyRuleFactories().remove(issuerFactory);
//        certAuthFactory.setEvaluateSubjectDN(true);
//        certAuthFactory.setX500SubjectDNFormat(X500DNHandler.FORMAT_RFC2253);
//        assertPolicySuccess("Trusted client, issuer derived from cert subject DN");
//        assertEquals("Issuer authentication should have been true", Boolean.TRUE, policy.isIssuerAuthenticated());
//        assertEquals("Unexpected value for authenticated issuer", entitySubjectDN, policy.getIssuer());
//    }
//    
//    /**
//     * Test trusted client.  No context issuer. Derive issuer from subject common name (CN).
//     */
//    public void testCertIssuerDerivationSubjectCN() {
//        entityX509Cred.setEntityId(entityCN);
//        resolver.getCollection().add(entityX509Cred);
//        getPolicyRuleFactories().remove(issuerFactory);
//        certAuthFactory.setEvaluateSubjectCommonName(true);
//        assertPolicySuccess("Trusted client, issuer derived from cert subject CN");
//        assertEquals("Issuer authentication should have been true", Boolean.TRUE, policy.isIssuerAuthenticated());
//        assertEquals("Unexpected value for authenticated issuer", entityCN, policy.getIssuer());
//    }
//    
//    /**
//     * Test trusted client.  No context issuer. Derive issuer from DNS alt name.
//     */
//    public void testCertIssuerDerivationDNSAltNAme() {
//        entityX509Cred.setEntityId(entityDNSAltName);
//        resolver.getCollection().add(entityX509Cred);
//        getPolicyRuleFactories().remove(issuerFactory);
//        certAuthFactory.getSubjectAltNames().add(X509Util.DNS_ALT_NAME);
//        assertPolicySuccess("Trusted client, issuer derived from cert DNS alt name");
//        assertEquals("Issuer authentication should have been true", Boolean.TRUE, policy.isIssuerAuthenticated());
//        assertEquals("Unexpected value for authenticated issuer", entityDNSAltName, policy.getIssuer());
//    }
//    
//    /**
//     * Test trusted client.  No context issuer. Derive issuer from URI alt name.
//     */
//    public void testCertIssuerDerivationURIAltName() {
//        entityX509Cred.setEntityId(entityURIAltName);
//        resolver.getCollection().add(entityX509Cred);
//        getPolicyRuleFactories().remove(issuerFactory);
//        certAuthFactory.getSubjectAltNames().add(X509Util.URI_ALT_NAME);
//        assertPolicySuccess("Trusted client, issuer derived from cert URI alt name");
//        assertEquals("Issuer authentication should have been true", Boolean.TRUE, policy.isIssuerAuthenticated());
//        assertEquals("Unexpected value for authenticated issuer", entityURIAltName, policy.getIssuer());
//    }
//    
//    /**
//     * Test untrusted client.  No context issuer. Context authN state should be unattempted, since
//     * certificate derivation attempts shouldn't count against them.
//     */
//    public void testUntrustedClientWithCertIssuerDerivations() {
//        getPolicyRuleFactories().remove(issuerFactory);
//        certAuthFactory.setEvaluateSubjectDN(true);
//        certAuthFactory.setEvaluateSubjectCommonName(true);
//        certAuthFactory.getSubjectAltNames().add(X509Util.DNS_ALT_NAME);
//        assertPolicySuccess("Policy should pass, client is not trusted");
//        assertEquals("Issuer authentication should have been unattempted", null, policy.isIssuerAuthenticated() );
//    }
//    
//    /**
//     * Test no client cert in request, auth not required.
//     */
//    public void testNoClientCert() {
//        httpRequest.removeAttribute(ServletRequestX509CredentialAdapter.X509_CERT_REQUEST_ATTRIBUTE);
//        policyFactory.setRequireAuthenticatedIssuer(false);
//        assertPolicySuccess("Policy should pass with no client cert");
//        assertEquals("Issuer authentication should have been unattempted", null, policy.isIssuerAuthenticated());
//    }
//    
//    /**
//     * Test no client cert in request, auth required.
//     */
//    public void testNoClientCertAuthRequired() {
//        httpRequest.removeAttribute(ServletRequestX509CredentialAdapter.X509_CERT_REQUEST_ATTRIBUTE);
//        policyFactory.setRequireAuthenticatedIssuer(true);
//        assertPolicyFail("Policy should NOT pass with no client cert, auth required");
//    }
//    
//    /**
//     * Sanity check that request cert is there.
//     */
//    protected void confirmRequestCert() {
//        // Sanity checks that client cert is there
//        X509Certificate[] certChain = (X509Certificate[]) 
//        httpRequest.getAttribute(ServletRequestX509CredentialAdapter.X509_CERT_REQUEST_ATTRIBUTE);
//        assertNotNull("HTTP request had no client cert chain", certChain);
//        assertTrue("HTTP request had empty client cert chain", certChain.length > 0);
//        assertFalse("HTTP request had null client cert", certChain[0] == null);
//        assertEquals("Unexpected client cert in HTTP request ", entityCert, certChain[0]);
//    }
    
}
