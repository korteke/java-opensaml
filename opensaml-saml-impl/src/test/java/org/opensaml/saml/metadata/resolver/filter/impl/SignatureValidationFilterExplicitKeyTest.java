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

package org.opensaml.saml.metadata.resolver.filter.impl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.metadata.resolver.filter.FilterException;
import org.opensaml.saml.metadata.resolver.impl.DOMMetadataResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.credential.impl.StaticCredentialResolver;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.security.x509.X509Support;
import org.opensaml.xmlsec.SignatureValidationParameters;
import org.opensaml.xmlsec.config.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.opensaml.xmlsec.signature.support.SignatureValidationParametersCriterion;
import org.opensaml.xmlsec.signature.support.impl.ExplicitKeySignatureTrustEngine;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

/**
 * Unit tests for {@link SignatureValidationFilter}.
 */
public class SignatureValidationFilterExplicitKeyTest extends XMLObjectBaseTestCase {
    
    private final String switchMDFileValid = "/org/opensaml/saml/saml2/metadata/provider/metadata.aaitest_signed.xml";
    private final String switchMDFileInvalid = "/org/opensaml/saml/saml2/metadata/provider/metadata.aaitest_signed.invalid.xml";
    
    private Document switchMDDocumentValid;
    private Document switchMDDocumentInvalid;
    
    private SignatureTrustEngine switchSigTrustEngine;
    
    private String switchMDCertBase64 = 
        "MIICrzCCAhgCAQAwDQYJKoZIhvcNAQEEBQAwgZ8xCzAJBgNVBAYTAkNIMUAwPgYDVQQKEzdTV0lU" +
        "Q0ggLSBUZWxlaW5mb3JtYXRpa2RpZW5zdGUgZnVlciBMZWhyZSB1bmQgRm9yc2NodW5nMQwwCgYD" +
        "VQQLEwNBQUkxIjAgBgNVBAMTGVNXSVRDSGFhaSBNZXRhZGF0YSBTaWduZXIxHDAaBgkqhkiG9w0B" +
        "CQEWDWFhaUBzd2l0Y2guY2gwHhcNMDUwODAzMTEyMjUxWhcNMTUwODAxMTEyMjUxWjCBnzELMAkG" +
        "A1UEBhMCQ0gxQDA+BgNVBAoTN1NXSVRDSCAtIFRlbGVpbmZvcm1hdGlrZGllbnN0ZSBmdWVyIExl" +
        "aHJlIHVuZCBGb3JzY2h1bmcxDDAKBgNVBAsTA0FBSTEiMCAGA1UEAxMZU1dJVENIYWFpIE1ldGFk" +
        "YXRhIFNpZ25lcjEcMBoGCSqGSIb3DQEJARYNYWFpQHN3aXRjaC5jaDCBnzANBgkqhkiG9w0BAQEF" +
        "AAOBjQAwgYkCgYEAsmyBYNZ8mKYutdyQShzuOgnVxDP1UBZE+57S2ORZg1qi4JExOJEPnviHuh6H" +
        "EajljhAMGHxr656paDpfXkmGq/Ybk3xmXy2FTnFGpjFpZUV6dY/oJ82rve27C/NVcwZw2nYRl5C5" +
        "aCCgx/QlWsBTw+9972141+wBDH7dXlJ+UGkCAwEAATANBgkqhkiG9w0BAQQFAAOBgQCcLuNwTINk" +
        "fhBlVCIuTixR1R6mYu/+4KUJWtHlRCOUZhSLFept8HxEvfwnuX9xm+Q6Ju/sOgmI1INuSstUGWwV" +
        "y0AbpCphUDDmIh9A85ye8DrVaBHQrj5b/JEjCvkY0zhLJzgDzZ6btT40TuCnk2GpdAClu5SyCTiy" +
        "56+zDYqPqg==";
    
    private final String openIDFileValid = "/org/opensaml/saml/saml2/metadata/provider/openid-metadata.xml";
    private final String openIDFileInvalid = "/org/opensaml/saml/saml2/metadata/provider/openid-metadata-invalid.xml";
    
    private String openIDCertBase64 = 
        "MIICfTCCAeagAwIBAgIGAReueFpXMA0GCSqGSIb3DQEBBQUAMIGBMQswCQYDVQQGEwJVUzELMAkG" +
        "A1UECBMCQ0ExFDASBgNVBAcTC1NpbWkgVmFsbGV5MR4wHAYDVQQKExVSYXBhdHRvbmkgQ29ycG9y" +
        "YXRpb24xFDASBgNVBAsTC1NTTyBTdXBwb3J0MRkwFwYDVQQDExBtbHNzdGdzd21pY2hpZ2FuMB4X" +
        "DTA4MDEyNTAxMDMxOFoXDTA5MDEyNDAxMDMxOFowgYExCzAJBgNVBAYTAlVTMQswCQYDVQQIEwJD" +
        "QTEUMBIGA1UEBxMLU2ltaSBWYWxsZXkxHjAcBgNVBAoTFVJhcGF0dG9uaSBDb3Jwb3JhdGlvbjEU" +
        "MBIGA1UECxMLU1NPIFN1cHBvcnQxGTAXBgNVBAMTEG1sc3N0Z3N3bWljaGlnYW4wgZ8wDQYJKoZI" +
        "hvcNAQEBBQADgY0AMIGJAoGBAIOnt2MOfIYvvyhiKBS2yb5IXFx+SFEa/TLSUPkE9gZJCIe22GGf" +
        "iwzsC8ubpifebZUru1fespnaCE8rc7MtWXERW7x6Dp8wg/91NOgUB00eEUlA72DhDjelsYTJa+Az" +
        "ztBsWh6J3HFKNdNaSVTS+CqbmgdTlDW+BExbtHUfSP0RAgMBAAEwDQYJKoZIhvcNAQEFBQADgYEA" +
        "YT8js8O7gbLq4X/yuGCiuKHofQHFAE6pAWaxdTD+Bd2pu48GKICYAhFwHTqrG3bOqObfsILz4Pca" +
        "vCfzIS7/dk9oPnjeH7GqbxUZMsms4qDZzdNkNDUDWj82lJzIMfZyUKbn2waTsgg3mKja0dGw2UBy" +
        "urPV4NvVcNaIQZJunHI=";
    
    private KeyInfoCredentialResolver kiResolver;
    
    @BeforeClass
    public void buildKeyInfoCredentialResolver() {
        kiResolver = DefaultSecurityConfigurationBootstrap.buildBasicInlineKeyInfoCredentialResolver();
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        switchMDDocumentValid = parserPool.parse(SignatureValidationFilterExplicitKeyTest.class.getResourceAsStream(switchMDFileValid));
        switchMDDocumentInvalid = parserPool.parse(SignatureValidationFilterExplicitKeyTest.class.getResourceAsStream(switchMDFileInvalid));
        
        X509Certificate switchCert = X509Support.decodeCertificate(switchMDCertBase64);
        X509Credential switchCred = CredentialSupport.getSimpleCredential(switchCert, null);
        StaticCredentialResolver switchCredResolver = new StaticCredentialResolver(switchCred);
        switchSigTrustEngine = new ExplicitKeySignatureTrustEngine(switchCredResolver, kiResolver);
    }

    @Test
    public void testValidSWITCHStandalone() throws UnmarshallingException {
        XMLObject xmlObject = unmarshallerFactory.getUnmarshaller(switchMDDocumentValid
                .getDocumentElement()).unmarshall(switchMDDocumentValid.getDocumentElement());
        
        SignatureValidationFilter filter = new SignatureValidationFilter(switchSigTrustEngine);
        try {
            filter.filter(xmlObject);
        } catch (FilterException e) {
            Assert.fail("Filter failed validation, should have succeeded: " + e.getMessage());
        }
    }
    
    @Test
    public void testSWITCHStandaloneBlacklistedSignatureAlgorithm() throws UnmarshallingException, FilterException {
        XMLObject xmlObject = unmarshallerFactory.getUnmarshaller(switchMDDocumentValid
                .getDocumentElement()).unmarshall(switchMDDocumentValid.getDocumentElement());
        
        SignatureValidationFilter filter = new SignatureValidationFilter(switchSigTrustEngine);
        
        SignatureValidationParameters sigParams = new SignatureValidationParameters();
        sigParams.setBlacklistedAlgorithms(Collections.singleton(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1));
        CriteriaSet defaultCriteriaSet = new CriteriaSet(new SignatureValidationParametersCriterion(sigParams));
        filter.setDefaultCriteria(defaultCriteriaSet);
        
        XMLObject filtered = filter.filter(xmlObject);
        Assert.assertNull(filtered);
    }
    
    @Test
    public void testInvalidSWITCHStandalone() throws UnmarshallingException, FilterException {
        XMLObject xmlObject = unmarshallerFactory.getUnmarshaller(switchMDDocumentInvalid
                .getDocumentElement()).unmarshall(switchMDDocumentInvalid.getDocumentElement());
        
        SignatureValidationFilter filter = new SignatureValidationFilter(switchSigTrustEngine);
        XMLObject filtered = filter.filter(xmlObject);
        Assert.assertNull(filtered);
    }
    
    @Test
    public void testEntityDescriptor() throws UnmarshallingException, CertificateException, XMLParserException {
        X509Certificate cert = X509Support.decodeCertificate(openIDCertBase64);
        X509Credential cred = CredentialSupport.getSimpleCredential(cert, null);
        StaticCredentialResolver credResolver = new StaticCredentialResolver(cred);
        SignatureTrustEngine trustEngine = new ExplicitKeySignatureTrustEngine(credResolver, kiResolver);
        
        Document mdDoc = parserPool.parse(SignatureValidationFilterExplicitKeyTest.class.getResourceAsStream(openIDFileValid));
        XMLObject xmlObject = 
            unmarshallerFactory.getUnmarshaller(mdDoc.getDocumentElement()).unmarshall(mdDoc.getDocumentElement());
        Assert.assertTrue(xmlObject instanceof EntityDescriptor);
        EntityDescriptor ed = (EntityDescriptor) xmlObject;
        Assert.assertTrue(ed.isSigned());
        Assert.assertNotNull(ed.getSignature(), "Signature was null");
        
        SignatureValidationFilter filter = new SignatureValidationFilter(trustEngine);
        try {
            filter.filter(ed);
        } catch (FilterException e) {
            Assert.fail("Filter failed validation, should have succeeded: " + e.getMessage());
        }
    }
    
    @Test
    public void testEntityDescriptorInvalid() throws UnmarshallingException, CertificateException, XMLParserException, FilterException {
        X509Certificate cert = X509Support.decodeCertificate(openIDCertBase64);
        X509Credential cred = CredentialSupport.getSimpleCredential(cert, null);
        StaticCredentialResolver credResolver = new StaticCredentialResolver(cred);
        SignatureTrustEngine trustEngine = new ExplicitKeySignatureTrustEngine(credResolver, kiResolver);
        
        Document mdDoc = parserPool.parse(SignatureValidationFilterExplicitKeyTest.class.getResourceAsStream(openIDFileInvalid));
        XMLObject xmlObject = 
            unmarshallerFactory.getUnmarshaller(mdDoc.getDocumentElement()).unmarshall(mdDoc.getDocumentElement());
        Assert.assertTrue(xmlObject instanceof EntityDescriptor);
        EntityDescriptor ed = (EntityDescriptor) xmlObject;
        Assert.assertTrue(ed.isSigned());
        Assert.assertNotNull(ed.getSignature(), "Signature was null");
        
        SignatureValidationFilter filter = new SignatureValidationFilter(trustEngine);
        XMLObject filtered = filter.filter(xmlObject);
        Assert.assertNull(filtered);
    }
    
    @Test
    public void testEntityDescriptorWithProvider() throws CertificateException, XMLParserException, UnmarshallingException {
        X509Certificate cert = X509Support.decodeCertificate(openIDCertBase64);
        X509Credential cred = CredentialSupport.getSimpleCredential(cert, null);
        StaticCredentialResolver credResolver = new StaticCredentialResolver(cred);
        SignatureTrustEngine trustEngine = new ExplicitKeySignatureTrustEngine(credResolver, kiResolver);
        
        Document mdDoc = parserPool.parse(SignatureValidationFilterExplicitKeyTest.class.getResourceAsStream(openIDFileValid));
        
        DOMMetadataResolver mdProvider = new DOMMetadataResolver(mdDoc.getDocumentElement());
        mdProvider.setParserPool(parserPool);
        mdProvider.setId("test");
        mdProvider.setRequireValidMetadata(false);
        
        SignatureValidationFilter filter = new SignatureValidationFilter(trustEngine);
        mdProvider.setMetadataFilter(filter);
        
        try {
            mdProvider.initialize();
        } catch (ComponentInitializationException e) {
            Assert.fail("Failed when initializing metadata provider");
        }
    }
    
    @Test
    public void testInvalidEntityDescriptorWithProvider() throws CertificateException, XMLParserException, UnmarshallingException, ComponentInitializationException {
        X509Certificate cert = X509Support.decodeCertificate(openIDCertBase64);
        X509Credential cred = CredentialSupport.getSimpleCredential(cert, null);
        StaticCredentialResolver credResolver = new StaticCredentialResolver(cred);
        SignatureTrustEngine trustEngine = new ExplicitKeySignatureTrustEngine(credResolver, kiResolver);
        
        Document mdDoc = parserPool.parse(SignatureValidationFilterExplicitKeyTest.class.getResourceAsStream(openIDFileInvalid));
        
        DOMMetadataResolver mdProvider = new DOMMetadataResolver(mdDoc.getDocumentElement());
        mdProvider.setParserPool(parserPool);
        mdProvider.setRequireValidMetadata(false);
        
        SignatureValidationFilter filter = new SignatureValidationFilter(trustEngine);
        mdProvider.setId("test");
        mdProvider.setMetadataFilter(filter);
        
        mdProvider.initialize();
        
        Assert.assertFalse(mdProvider.iterator().hasNext());
    }

}