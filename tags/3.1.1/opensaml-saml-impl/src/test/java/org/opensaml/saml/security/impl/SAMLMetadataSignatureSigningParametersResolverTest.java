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

package org.opensaml.saml.security.impl;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.crypto.SecretKey;

import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.saml.criterion.RoleDescriptorCriterion;
import org.opensaml.saml.ext.saml2alg.DigestMethod;
import org.opensaml.saml.ext.saml2alg.SigningMethod;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.criterion.SignatureSigningConfigurationCriterion;
import org.opensaml.xmlsec.impl.BasicSignatureSigningConfiguration;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;
import org.opensaml.xmlsec.keyinfo.impl.BasicKeyInfoGeneratorFactory;
import org.opensaml.xmlsec.keyinfo.impl.X509KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SAMLMetadataSignatureSigningParametersResolverTest extends XMLObjectBaseTestCase {
    
    private SAMLMetadataSignatureSigningParametersResolver resolver;
    
    private CriteriaSet criteriaSet;
    
    private SignatureSigningConfigurationCriterion configCriterion;
    
    private BasicSignatureSigningConfiguration config1, config2, config3;
    
    private Credential rsaCred1024, rsaCred2048, rsaCred4096, dsaCred, ecCred, hmacCred;
    
    private String defaultReferenceDigest = SignatureConstants.ALGO_ID_DIGEST_SHA1;
    
    private String defaultC14N = SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS;
    
    private String defaultRSAAlgo = SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1;
    private String defaultDSAAlgo = SignatureConstants.ALGO_ID_SIGNATURE_DSA_SHA1;
    private String defaultECAlgo = SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA1;
    private String defaultHMACAlgo = SignatureConstants.ALGO_ID_MAC_HMAC_SHA1;
    
    private Integer defaultHMACOutputLength = 128;
     
    private NamedKeyInfoGeneratorManager defaultKeyInfoGeneratorManager = new NamedKeyInfoGeneratorManager();
    
    private RoleDescriptorCriterion roleDescCriterion;
    
    private RoleDescriptor roleDesc;
    
    private String targetEntityID = "urn:test:foo";
    
    @BeforeClass
    public void buildCredentials() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPair rsaKeyPair1024 = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_RSA, 1024, null);
        rsaCred1024 = CredentialSupport.getSimpleCredential(rsaKeyPair1024.getPublic(), rsaKeyPair1024.getPrivate());
        
        KeyPair rsaKeyPair2048 = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_RSA, 2048, null);
        rsaCred2048 = CredentialSupport.getSimpleCredential(rsaKeyPair2048.getPublic(), rsaKeyPair2048.getPrivate());
        
        KeyPair rsaKeyPair4096 = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_RSA, 4096, null);
        rsaCred4096 = CredentialSupport.getSimpleCredential(rsaKeyPair4096.getPublic(), rsaKeyPair4096.getPrivate());
        
        KeyPair dsaKeyPair = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_DSA, 1024, null);
        dsaCred = CredentialSupport.getSimpleCredential(dsaKeyPair.getPublic(), dsaKeyPair.getPrivate()); 
        
        try {
            KeyPair ecKeyPair = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_EC, 256, null);
            ecCred = CredentialSupport.getSimpleCredential(ecKeyPair.getPublic(), ecKeyPair.getPrivate()); 
        } catch (NoSuchAlgorithmException e) {
            // EC support isn't universal, e.g. OpenJDK 7 doesn't ship with an EC provider out-of-the-box.
            // Just ignore unsupported algorithm failures here for now.
        }
        
        SecretKey hmacKey = KeySupport.generateKey(JCAConstants.KEY_ALGO_AES, 128, null);
        hmacCred = CredentialSupport.getSimpleCredential(hmacKey);
    }
    
    @BeforeMethod
    public void setUp() {
        resolver = new SAMLMetadataSignatureSigningParametersResolver();
        
        config1 = new BasicSignatureSigningConfiguration();
        config2 = new BasicSignatureSigningConfiguration();
        config3 = new BasicSignatureSigningConfiguration();
        
        // Set these as defaults on the last config in the chain, just so don't have to set in every test.
        config3.setSignatureAlgorithms(Arrays.asList(defaultRSAAlgo, defaultDSAAlgo, defaultECAlgo, defaultHMACAlgo));
        config3.setSignatureReferenceDigestMethods(Collections.singletonList(defaultReferenceDigest));
        config3.setSignatureCanonicalizationAlgorithm(defaultC14N);
        config3.setSignatureHMACOutputLength(defaultHMACOutputLength);
        
        BasicKeyInfoGeneratorFactory basicFactory = new BasicKeyInfoGeneratorFactory();
        X509KeyInfoGeneratorFactory x509Factory = new X509KeyInfoGeneratorFactory();
        defaultKeyInfoGeneratorManager = new NamedKeyInfoGeneratorManager();
        defaultKeyInfoGeneratorManager.registerDefaultFactory(basicFactory);
        defaultKeyInfoGeneratorManager.registerDefaultFactory(x509Factory);
        config3.setKeyInfoGeneratorManager(defaultKeyInfoGeneratorManager);
        
        configCriterion = new SignatureSigningConfigurationCriterion(config1, config2, config3);
        
        roleDesc = buildRoleDescriptorSkeleton();
        roleDescCriterion = new RoleDescriptorCriterion(roleDesc);
        
        criteriaSet = new CriteriaSet(configCriterion, roleDescCriterion);
    }
    
    @Test
    public void testBasicRSA() throws ResolverException {
        config1.setSigningCredentials(Collections.singletonList(rsaCred2048));
        
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getSigningCredential(), rsaCred2048);
        Assert.assertEquals(params.getSignatureAlgorithm(), defaultRSAAlgo);
        Assert.assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
        Assert.assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        Assert.assertNull(params.getSignatureHMACOutputLength());
        Assert.assertNotNull(params.getKeyInfoGenerator());
    }
    
    @Test
    public void testRSAWithRoleDescriptorSigningMethod() throws ResolverException {
        config1.setSigningCredentials(Collections.singletonList(rsaCred2048));
        
        addRoleDescriptorExtension(roleDesc, buildSigningMethod(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256, null, null));
        
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getSigningCredential(), rsaCred2048);
        Assert.assertEquals(params.getSignatureAlgorithm(), SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        Assert.assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
        Assert.assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        Assert.assertNull(params.getSignatureHMACOutputLength());
        Assert.assertNotNull(params.getKeyInfoGenerator());
    }

    @Test
    public void testRSAWithEntityDescriptorSigningMethod() throws ResolverException {
        config1.setSigningCredentials(Collections.singletonList(rsaCred2048));
        
        addEntityDescriptorExtension(roleDesc, buildSigningMethod(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256, null, null));
        
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getSigningCredential(), rsaCred2048);
        Assert.assertEquals(params.getSignatureAlgorithm(), SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        Assert.assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
        Assert.assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        Assert.assertNull(params.getSignatureHMACOutputLength());
        Assert.assertNotNull(params.getKeyInfoGenerator());
    }
    
    @Test
    public void testRSAWithRoleDescriptorDigestMethod() throws ResolverException {
        config1.setSigningCredentials(Collections.singletonList(rsaCred2048));
        
        addRoleDescriptorExtension(roleDesc, buildDigestMethod(SignatureConstants.ALGO_ID_DIGEST_SHA256));
        
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getSigningCredential(), rsaCred2048);
        Assert.assertEquals(params.getSignatureAlgorithm(), defaultRSAAlgo);
        Assert.assertEquals(params.getSignatureReferenceDigestMethod(), SignatureConstants.ALGO_ID_DIGEST_SHA256);
        Assert.assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        Assert.assertNull(params.getSignatureHMACOutputLength());
        Assert.assertNotNull(params.getKeyInfoGenerator());
    }

    @Test
    public void testRSAWithEntityDescriptorDigestMethod() throws ResolverException {
        config1.setSigningCredentials(Collections.singletonList(rsaCred2048));
        
        addEntityDescriptorExtension(roleDesc, buildDigestMethod(SignatureConstants.ALGO_ID_DIGEST_SHA256));
        
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getSigningCredential(), rsaCred2048);
        Assert.assertEquals(params.getSignatureAlgorithm(), defaultRSAAlgo);
        Assert.assertEquals(params.getSignatureReferenceDigestMethod(), SignatureConstants.ALGO_ID_DIGEST_SHA256);
        Assert.assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        Assert.assertNull(params.getSignatureHMACOutputLength());
        Assert.assertNotNull(params.getKeyInfoGenerator());
    }
    
    @Test
    public void testRSAWithSigningMethodBlacklisted() throws ResolverException {
        config1.setSigningCredentials(Collections.singletonList(rsaCred2048));
        
        config1.setBlacklistedAlgorithms(Collections.singletonList(SignatureConstants.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5));
        
        addRoleDescriptorExtension(roleDesc, buildSigningMethod(SignatureConstants.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5, null, null));
        addRoleDescriptorExtension(roleDesc, buildSigningMethod(SignatureConstants.ALGO_ID_SIGNATURE_DSA_SHA1, null, null));
        addRoleDescriptorExtension(roleDesc, buildSigningMethod(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256, null, null));
        
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getSigningCredential(), rsaCred2048);
        Assert.assertEquals(params.getSignatureAlgorithm(), SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        Assert.assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
        Assert.assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        Assert.assertNull(params.getSignatureHMACOutputLength());
        Assert.assertNotNull(params.getKeyInfoGenerator());
    }

    @Test
    public void testRSAWithDigestMethodBlacklisted() throws ResolverException {
        config1.setSigningCredentials(Collections.singletonList(rsaCred2048));
        
        config1.setBlacklistedAlgorithms(Collections.singletonList(SignatureConstants.ALGO_ID_DIGEST_NOT_RECOMMENDED_MD5));
        
        addRoleDescriptorExtension(roleDesc, buildDigestMethod(SignatureConstants.ALGO_ID_DIGEST_NOT_RECOMMENDED_MD5));
        
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getSigningCredential(), rsaCred2048);
        Assert.assertEquals(params.getSignatureAlgorithm(), defaultRSAAlgo);
        Assert.assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
        Assert.assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        Assert.assertNull(params.getSignatureHMACOutputLength());
        Assert.assertNotNull(params.getKeyInfoGenerator());
    }
    
    @Test
    public void testECWithSigningMethodWhitelisted() throws ResolverException {
        if (ecCred != null) {
            config1.setSigningCredentials(Arrays.asList(rsaCred2048, dsaCred, ecCred));
            
            config1.setWhitelistedAlgorithms(Arrays.asList(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA256, defaultReferenceDigest));
            
            addRoleDescriptorExtension(roleDesc, buildSigningMethod(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512, null, null));
            addRoleDescriptorExtension(roleDesc, buildSigningMethod(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256, null, null));
            addRoleDescriptorExtension(roleDesc, buildSigningMethod(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA256, null, null));
            
            SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
            
            Assert.assertNotNull(params);
            Assert.assertEquals(params.getSigningCredential(), ecCred);
            Assert.assertEquals(params.getSignatureAlgorithm(), SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA256);
            Assert.assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
            Assert.assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
            Assert.assertNull(params.getSignatureHMACOutputLength());
            Assert.assertNotNull(params.getKeyInfoGenerator());
        }
    }
    
    @Test
    public void testMultipleCredsWithSigningMethodSelection() throws ResolverException {
        config1.setSigningCredentials(Arrays.asList(rsaCred2048, hmacCred, dsaCred));
        
        addRoleDescriptorExtension(roleDesc, buildSigningMethod(SignatureConstants.ALGO_ID_SIGNATURE_DSA_SHA1, null, null));
        
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getSigningCredential(), dsaCred);
        Assert.assertEquals(params.getSignatureAlgorithm(), defaultDSAAlgo);
        Assert.assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
        Assert.assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        Assert.assertNull(params.getSignatureHMACOutputLength());
        Assert.assertNotNull(params.getKeyInfoGenerator());
    }
    
    @Test
    public void testRSAMinKeyLength() throws ResolverException {
        config1.setSigningCredentials(Arrays.asList(rsaCred1024, rsaCred2048, rsaCred4096));
        
        addRoleDescriptorExtension(roleDesc, buildSigningMethod(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256, 4096, null));
        
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getSigningCredential(), rsaCred4096);
        Assert.assertEquals(params.getSignatureAlgorithm(), SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        Assert.assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
        Assert.assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        Assert.assertNull(params.getSignatureHMACOutputLength());
        Assert.assertNotNull(params.getKeyInfoGenerator());
    }
    
    @Test
    public void testRSAMaxKeyLength() throws ResolverException {
        config1.setSigningCredentials(Arrays.asList(rsaCred4096, rsaCred2048, rsaCred1024));
        
        addRoleDescriptorExtension(roleDesc, buildSigningMethod(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256, null, 1024));
        
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getSigningCredential(), rsaCred1024);
        Assert.assertEquals(params.getSignatureAlgorithm(), SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        Assert.assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
        Assert.assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        Assert.assertNull(params.getSignatureHMACOutputLength());
        Assert.assertNotNull(params.getKeyInfoGenerator());
    }
    
    @Test
    public void testNoCredentials() throws ResolverException {
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNull(params);
    }
    
    @Test
    public void testNoAlgorithms() throws ResolverException {
        config1.setSigningCredentials(Collections.singletonList(rsaCred2048));
        config3.setSignatureAlgorithms(new ArrayList<String>());
        
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNull(params);
    }
    
    @Test
    public void testNoReferenceDigestMethods() throws ResolverException {
        config1.setSigningCredentials(Collections.singletonList(rsaCred2048));
        config3.setSignatureReferenceDigestMethods(new ArrayList<String>());
        
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNull(params);
    }
    
    @Test
    public void testNoC14NAlgorithm() throws ResolverException {
        config1.setSigningCredentials(Collections.singletonList(rsaCred2048));
        config3.setSignatureCanonicalizationAlgorithm(null);
        
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNull(params);
    }
    
    @Test(expectedExceptions=ConstraintViolationException.class)
    public void testNullCriteriaSet() throws ResolverException {
        resolver.resolve(null);
    }

    @Test(expectedExceptions=ConstraintViolationException.class)
    public void testAbsentCriterion() throws ResolverException {
        resolver.resolve(new CriteriaSet());
    }
    
    
    
    
    // Helper methods
    
    private RoleDescriptor buildRoleDescriptorSkeleton() {
        EntityDescriptor entityDesc = buildXMLObject(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        entityDesc.setEntityID(targetEntityID);
        
        SPSSODescriptor spSSODesc = buildXMLObject(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        spSSODesc.setParent(entityDesc);
        
        return spSSODesc;
    }
    
    private SigningMethod buildSigningMethod(String algorithm, Integer minKeySize, Integer maxKeySize) {
        SigningMethod signingMethod = buildXMLObject(SigningMethod.DEFAULT_ELEMENT_NAME);
        signingMethod.setAlgorithm(algorithm);
        signingMethod.setMinKeySize(minKeySize);
        signingMethod.setMaxKeySize(maxKeySize);
        return signingMethod;
    }
    
    private DigestMethod buildDigestMethod(String algorithm) {
        DigestMethod digestMethod = buildXMLObject(DigestMethod.DEFAULT_ELEMENT_NAME);
        digestMethod.setAlgorithm(algorithm);
        return digestMethod;
    }
    
    private void addRoleDescriptorExtension(RoleDescriptor roleDescriptor, XMLObject extension) {
        Extensions extensions = roleDescriptor.getExtensions();
        if (extensions == null) {
            extensions = buildExtensions();
            roleDescriptor.setExtensions(extensions);
        }
        extensions.getUnknownXMLObjects().add(extension);
    }
    
    private void addEntityDescriptorExtension(RoleDescriptor roleDescriptor, XMLObject extension) {
        EntityDescriptor entityDescriptor = (EntityDescriptor) roleDescriptor.getParent();
        Extensions extensions = entityDescriptor.getExtensions();
        if (extensions == null) {
            extensions = buildExtensions();
            entityDescriptor.setExtensions(extensions);
        }
        extensions.getUnknownXMLObjects().add(extension);
    }

    private Extensions buildExtensions() {
        return buildXMLObject(Extensions.DEFAULT_ELEMENT_NAME);
    }
    
}