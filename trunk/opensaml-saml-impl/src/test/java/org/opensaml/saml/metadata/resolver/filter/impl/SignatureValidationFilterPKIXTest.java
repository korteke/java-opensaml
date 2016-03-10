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

import java.io.InputStream;
import java.security.PrivateKey;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.security.x509.PKIXValidationInformation;
import org.opensaml.security.x509.X509Support;
import org.opensaml.security.x509.impl.BasicPKIXValidationInformation;
import org.opensaml.security.x509.impl.BasicX509CredentialNameEvaluator;
import org.opensaml.security.x509.impl.CertPathPKIXTrustEvaluator;
import org.opensaml.security.x509.impl.StaticPKIXValidationInformationResolver;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.config.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.keyinfo.impl.X509KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureSupport;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.opensaml.xmlsec.signature.support.Signer;
import org.opensaml.xmlsec.signature.support.impl.PKIXSignatureTrustEngine;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.collections.Lists;
import org.w3c.dom.Element;

public class SignatureValidationFilterPKIXTest extends XMLObjectBaseTestCase {
    
    private static final String DATA_PATH = "/org/opensaml/saml/metadata/resolver/filter/impl/";
    
    private SignatureValidationFilter filter;
    
    @BeforeMethod
    public void setUp() {
        filter = new SignatureValidationFilter(buildTrustEngine());
        filter.setDynamicTrustedNamesStrategy(new BasicDynamicTrustedNamesStrategy());
    }
    
    @Test
    public void testEntityDescriptor() throws Exception {
        Credential signingCredential = buildSigningCredential("entity.key", "entity.crt", "ca.crt");
        XMLObject entityDescriptor = generateSignedMetadata(signingCredential, "EntityDescriptor.xml");
        
        filter.filter(entityDescriptor);
    }
    
    @Test()
    public void testEntityDescriptorInvalidEntityID() throws Exception {
        Credential signingCredential = buildSigningCredential("entity.key", "entity.crt", "ca.crt");
        // This metadata file is identical to the success case except the document entityID is changed, so
        // as to not match the URI alt name in the cert. So the dynamic trusted named fed to the trust engine 
        // will not match.
        XMLObject entityDescriptor = generateSignedMetadata(signingCredential, "EntityDescriptor-invalid-entityid.xml");
        
        XMLObject filtered = filter.filter(entityDescriptor);
        Assert.assertNull(filtered);
    }

    private XMLObject generateSignedMetadata(Credential signingCredential, String unsignedMetadata) 
            throws SecurityException, SignatureException, MarshallingException, UnmarshallingException {
        
        XMLObject unsignedObject = unmarshallElement(DATA_PATH + unsignedMetadata);
        if (!(unsignedObject instanceof SignableSAMLObject)) {
            Assert.fail("Not a signable SAML object");
        }
        SignableSAMLObject signableSAML = (SignableSAMLObject) unsignedObject;
        
        SignatureSigningParameters params = new SignatureSigningParameters();
        params.setSigningCredential(signingCredential);
        params.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        params.setSignatureCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        params.setSignatureReferenceDigestMethod(SignatureConstants.ALGO_ID_DIGEST_SHA256);
        X509KeyInfoGeneratorFactory kigf = new X509KeyInfoGeneratorFactory();
        kigf.setEmitEntityCertificate(true);
        kigf.setEmitEntityCertificateChain(true);
        kigf.setEmitX509SubjectName(true);
        params.setKeyInfoGenerator(kigf.newInstance());
        
        Signature signature = buildXMLObject(Signature.DEFAULT_ELEMENT_NAME);
        signableSAML.setSignature(signature);
        
        SignatureSupport.prepareSignatureParams(signature, params);
        
        Element dom = XMLObjectSupport.marshall(signableSAML);
        
        Signer.signObject(signature);
        
        // Unmarshall a new tree around the signed DOM to avoid any XMLSignature weirdness
        return unmarshallerFactory.getUnmarshaller(dom).unmarshall(dom);
    }

    private SignatureTrustEngine buildTrustEngine() {
        Collection<X509Certificate> roots = getCertificates("root.crt");
        
        PKIXValidationInformation pkixInfo = getPKIXInfoSet(roots, new HashSet<X509CRL>(), 10);
        
        StaticPKIXValidationInformationResolver resolver = new StaticPKIXValidationInformationResolver(
                Lists.newArrayList(pkixInfo), 
                new HashSet<String>(), 
                true);
        
        PKIXSignatureTrustEngine engine = new PKIXSignatureTrustEngine(
                resolver, 
                DefaultSecurityConfigurationBootstrap.buildBasicInlineKeyInfoCredentialResolver(), 
                new CertPathPKIXTrustEvaluator(),
                new BasicX509CredentialNameEvaluator());
        
        return engine;
    }
    
    private PKIXValidationInformation getPKIXInfoSet(Collection<X509Certificate> certs, Collection<X509CRL> crls, Integer depth) {
        return new BasicPKIXValidationInformation(certs, crls, depth);
    }
    
    private BasicX509Credential buildSigningCredential(String key, String cert, String ... chainMembers) {
        X509Certificate entityCert = getCertificate(cert);
        PrivateKey privateKey = getPrivateKey(key);
        
        BasicX509Credential cred = new BasicX509Credential(entityCert, privateKey);
        
        HashSet<X509Certificate> certChain = new HashSet<>();
        certChain.add(entityCert);
        
        for (String member: chainMembers) {
            certChain.add( getCertificate(member) );
        }
        
        cred.setEntityCertificateChain(certChain);
        
        return cred;
    }
    
    private PrivateKey getPrivateKey(String fileName) {
        try {
            InputStream ins = getInputStream(fileName);
            byte[] encoded = new byte[ins.available()];
            ins.read(encoded);
            return KeySupport.decodePrivateKey(encoded, null);
        } catch (Exception e) {
            Assert.fail("Could not create private key from file: " + fileName + ": " + e.getMessage());
        }
        return null;
    }

    private Collection<X509Certificate> getCertificates(String ... certNames) {
        Set<X509Certificate> certs = new HashSet<>();
        for (String certName : certNames) {
           certs.add( getCertificate(certName) );
        }
        return certs;
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
