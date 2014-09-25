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
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.saml.common.SAMLTestSupport;
import org.opensaml.saml.criterion.RoleDescriptorCriterion;
import org.opensaml.saml.saml2.metadata.EncryptionMethod;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.EncryptionParameters;
import org.opensaml.xmlsec.KeyTransportAlgorithmPredicate;
import org.opensaml.xmlsec.criterion.EncryptionConfigurationCriterion;
import org.opensaml.xmlsec.criterion.KeyInfoGenerationProfileCriterion;
import org.opensaml.xmlsec.encryption.MGF;
import org.opensaml.xmlsec.encryption.OAEPparams;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.encryption.support.RSAOAEPParameters;
import org.opensaml.xmlsec.impl.BasicEncryptionConfiguration;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;
import org.opensaml.xmlsec.keyinfo.impl.BasicKeyInfoGeneratorFactory;
import org.opensaml.xmlsec.keyinfo.impl.X509KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.signature.DigestMethod;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

public class SAMLMetadataEncryptionParametersResolverTest extends XMLObjectBaseTestCase {
    
    private MetadataCredentialResolver mdCredResolver;
    
    private SAMLMetadataEncryptionParametersResolver resolver;
    
    private CriteriaSet criteriaSet;
    
    private EncryptionConfigurationCriterion configCriterion;
    
    private BasicEncryptionConfiguration config1, config2, config3;
    
    private Credential rsaCred1;
    private String rsaCred1KeyName = "RSACred1";
    
    private Credential dsaCred1;
    private String dsaCred1KeyName = "DSACred1";
    
    private String defaultRSAKeyTransportAlgo = EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP;
    private String defaultAES128DataAlgo = EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128;
    private String defaultAES192DataAlgo = EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192;
    private String defaultAES256DataAlgo = EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256;
    
    private NamedKeyInfoGeneratorManager defaultKeyTransportKeyInfoGeneratorManager = new NamedKeyInfoGeneratorManager();
    private NamedKeyInfoGeneratorManager defaultDataEncryptionKeyInfoGeneratorManager = new NamedKeyInfoGeneratorManager();
    
    private RoleDescriptorCriterion roleDescCriterion;
    
    private RoleDescriptor roleDesc;
    
    private String targetEntityID = "urn:test:foo";
    
    @BeforeClass
    public void buildCredentials() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPair rsaKeyPair = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_RSA, 2048, null);
        rsaCred1 = CredentialSupport.getSimpleCredential(rsaKeyPair.getPublic(), null);
        rsaCred1.getKeyNames().add(rsaCred1KeyName);
        
        KeyPair dsaKeyPair = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_DSA, 1024, null);
        dsaCred1 = CredentialSupport.getSimpleCredential(dsaKeyPair.getPublic(), null);
        dsaCred1.getKeyNames().add(dsaCred1KeyName);
    }
    
    @BeforeMethod
    public void setUp() throws ComponentInitializationException {
        mdCredResolver = new MetadataCredentialResolver();
        mdCredResolver.setKeyInfoCredentialResolver(SAMLTestSupport.buildBasicInlineKeyInfoResolver());
        mdCredResolver.initialize();
        
        resolver = new SAMLMetadataEncryptionParametersResolver(mdCredResolver);
        
        config1 = new BasicEncryptionConfiguration();
        config2 = new BasicEncryptionConfiguration();
        config3 = new BasicEncryptionConfiguration();
        
        // Set these as defaults on the last config in the chain, just so don't have to set in every test.
        config3.setDataEncryptionAlgorithms(Lists.newArrayList(
                defaultAES128DataAlgo,
                defaultAES192DataAlgo,
                defaultAES256DataAlgo,
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES,
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM,
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192_GCM,
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM
                ));
        config3.setKeyTransportEncryptionAlgorithms(Lists.newArrayList(
                defaultRSAKeyTransportAlgo, 
                EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15,
                EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11,
                EncryptionConstants.ALGO_ID_KEYWRAP_AES128,
                EncryptionConstants.ALGO_ID_KEYWRAP_AES192,
                EncryptionConstants.ALGO_ID_KEYWRAP_AES256,
                EncryptionConstants.ALGO_ID_KEYWRAP_TRIPLEDES
                ));
        
        BasicKeyInfoGeneratorFactory basicFactory1 = new BasicKeyInfoGeneratorFactory();
        X509KeyInfoGeneratorFactory x509Factory1 = new X509KeyInfoGeneratorFactory();
        defaultKeyTransportKeyInfoGeneratorManager = new NamedKeyInfoGeneratorManager();
        defaultKeyTransportKeyInfoGeneratorManager.registerDefaultFactory(basicFactory1);
        defaultKeyTransportKeyInfoGeneratorManager.registerDefaultFactory(x509Factory1);
        config3.setKeyTransportKeyInfoGeneratorManager(defaultKeyTransportKeyInfoGeneratorManager);
        
        BasicKeyInfoGeneratorFactory basicFactory2 = new BasicKeyInfoGeneratorFactory();
        X509KeyInfoGeneratorFactory x509Factory2 = new X509KeyInfoGeneratorFactory();
        defaultDataEncryptionKeyInfoGeneratorManager = new NamedKeyInfoGeneratorManager();
        defaultDataEncryptionKeyInfoGeneratorManager.registerDefaultFactory(basicFactory2);
        defaultDataEncryptionKeyInfoGeneratorManager.registerDefaultFactory(x509Factory2);
        config3.setDataKeyInfoGeneratorManager(defaultDataEncryptionKeyInfoGeneratorManager);
        
        configCriterion = new EncryptionConfigurationCriterion(config1, config2, config3);
        
        roleDesc = buildRoleDescriptorSkeleton();
        roleDescCriterion = new RoleDescriptorCriterion(roleDesc);
        
        criteriaSet = new CriteriaSet(configCriterion, roleDescCriterion);
    }
    
    @Test
    public void testBasic() throws ResolverException {
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey()));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getPublicKey(), rsaCred1.getPublicKey());
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), defaultRSAKeyTransportAlgo);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        Assert.assertNull(params.getDataEncryptionCredential());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        Assert.assertNull(params.getDataKeyInfoGenerator());
        
        Assert.assertNotNull(params.getRSAOAEPParameters());
        Assert.assertTrue(params.getRSAOAEPParameters().isEmpty());
    }
    
    @Test
    public void testWithRSAOAEPParametersFromConfig() throws ResolverException {
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey()));
        config3.setRSAOAEPParameters(new RSAOAEPParameters(EncryptionConstants.ALGO_ID_DIGEST_SHA256, null, "oaep-params-3"));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getPublicKey(), rsaCred1.getPublicKey());
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), defaultRSAKeyTransportAlgo);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        Assert.assertNull(params.getDataEncryptionCredential());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        Assert.assertNull(params.getDataKeyInfoGenerator());
        
        Assert.assertNotNull(params.getRSAOAEPParameters());
        Assert.assertEquals(params.getRSAOAEPParameters().getDigestMethod(), EncryptionConstants.ALGO_ID_DIGEST_SHA256);
        Assert.assertNull(params.getRSAOAEPParameters().getMaskGenerationFunction());
        Assert.assertEquals(params.getRSAOAEPParameters().getOAEPParams(), "oaep-params-3");
    }
    
    @Test
    public void testWithAlgorithmOverrides() throws ResolverException {
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey()));
        
        config2.setDataEncryptionAlgorithms(Lists.newArrayList(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256));
        config2.setKeyTransportEncryptionAlgorithms(Lists.newArrayList(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getPublicKey(), rsaCred1.getPublicKey());
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        Assert.assertNull(params.getDataEncryptionCredential());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);
        Assert.assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testWithBlacklist() throws ResolverException {
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey()));
        
        config1.setBlacklistedAlgorithms(Lists.newArrayList(defaultRSAKeyTransportAlgo, defaultAES128DataAlgo, defaultAES192DataAlgo));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getPublicKey(), rsaCred1.getPublicKey());
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        Assert.assertNull(params.getDataEncryptionCredential());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), defaultAES256DataAlgo);
        Assert.assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testWithWhitelist() throws ResolverException {
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey()));
        
        config1.setWhitelistedAlgorithms(Lists.newArrayList(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256, EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getPublicKey(), rsaCred1.getPublicKey());
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        Assert.assertNull(params.getDataEncryptionCredential());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);
        Assert.assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testGeneratedDataCredential() throws ResolverException {
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey()));
        
        resolver.setAutoGenerateDataEncryptionCredential(true);
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getPublicKey(), rsaCred1.getPublicKey());
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), defaultRSAKeyTransportAlgo);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        Assert.assertNotNull(params.getDataEncryptionCredential());
        Assert.assertNotNull(params.getDataEncryptionCredential().getSecretKey());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        Assert.assertEquals(KeySupport.getKeyLength(params.getDataEncryptionCredential().getSecretKey()), new Integer(128));
        Assert.assertNotNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testEncryptionMethod() throws ResolverException {
        KeyDescriptor keyDescriptor = buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey());
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15));
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256));
        roleDesc.getKeyDescriptors().add(keyDescriptor);
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getPublicKey(), rsaCred1.getPublicKey());
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        Assert.assertNull(params.getDataEncryptionCredential());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);
        Assert.assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testEncryptionMethodWithRSAOAEPParameters() throws ResolverException {
        EncryptionParameters params;
        EncryptionMethod rsaEncryptionMethod;
        DigestMethod digestMethod;
        MGF mgf;
        OAEPparams oaepParams;
        
        KeyDescriptor keyDescriptor = buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey());
        roleDesc.getKeyDescriptors().add(keyDescriptor);
        
        // Shouldn't resolve, since not RSA OAEP 
        rsaEncryptionMethod = buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        keyDescriptor.getEncryptionMethods().clear();
        keyDescriptor.getEncryptionMethods().add(rsaEncryptionMethod);
        params = resolver.resolveSingle(criteriaSet);
        Assert.assertNull(params.getRSAOAEPParameters());
        
        // Should resolve empty instance
        rsaEncryptionMethod = buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        keyDescriptor.getEncryptionMethods().clear();
        keyDescriptor.getEncryptionMethods().add(rsaEncryptionMethod);
        params = resolver.resolveSingle(criteriaSet);
        Assert.assertNotNull(params.getRSAOAEPParameters());
        Assert.assertTrue(params.getRSAOAEPParameters().isEmpty());
        
        // Note: in tests below, having an MGF with RSAOAEP (vs RSAOAEP11) doesn't technically make sense,
        // but here it doesn't matter and we can't really test RSAOAEP11 directly b/c it isn't runtime supported on Java 7
        // without BC.
        
        // Should resolve digest from metadata
        rsaEncryptionMethod = buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        digestMethod = buildXMLObject(DigestMethod.DEFAULT_ELEMENT_NAME);
        digestMethod.setAlgorithm(EncryptionConstants.ALGO_ID_DIGEST_SHA256);
        rsaEncryptionMethod.getUnknownXMLObjects().add(digestMethod);
        keyDescriptor.getEncryptionMethods().clear();
        keyDescriptor.getEncryptionMethods().add(rsaEncryptionMethod);
        params = resolver.resolveSingle(criteriaSet);
        Assert.assertNotNull(params.getRSAOAEPParameters());
        Assert.assertEquals(params.getRSAOAEPParameters().getDigestMethod(), EncryptionConstants.ALGO_ID_DIGEST_SHA256);
        Assert.assertNull(params.getRSAOAEPParameters().getMaskGenerationFunction());
        Assert.assertNull(params.getRSAOAEPParameters().getOAEPParams());
        
        // Should resolve all values from metadata
        rsaEncryptionMethod = buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        digestMethod = buildXMLObject(DigestMethod.DEFAULT_ELEMENT_NAME);
        digestMethod.setAlgorithm(EncryptionConstants.ALGO_ID_DIGEST_SHA256);
        rsaEncryptionMethod.getUnknownXMLObjects().add(digestMethod);
        mgf = buildXMLObject(MGF.DEFAULT_ELEMENT_NAME);
        mgf.setAlgorithm(EncryptionConstants.ALGO_ID_MGF1_SHA256);
        rsaEncryptionMethod.getUnknownXMLObjects().add(mgf);
        oaepParams = buildXMLObject(OAEPparams.DEFAULT_ELEMENT_NAME);
        oaepParams.setValue("oaep-params-md");
        rsaEncryptionMethod.setOAEPparams(oaepParams);
        keyDescriptor.getEncryptionMethods().clear();
        keyDescriptor.getEncryptionMethods().add(rsaEncryptionMethod);
        params = resolver.resolveSingle(criteriaSet);
        Assert.assertNotNull(params.getRSAOAEPParameters());
        Assert.assertEquals(params.getRSAOAEPParameters().getDigestMethod(), EncryptionConstants.ALGO_ID_DIGEST_SHA256);
        Assert.assertEquals(params.getRSAOAEPParameters().getMaskGenerationFunction(), EncryptionConstants.ALGO_ID_MGF1_SHA256);
        Assert.assertEquals(params.getRSAOAEPParameters().getOAEPParams(), "oaep-params-md");
        
        // Should resolve digest from metadata, should NOT resolve OAEPParms from config by default
        config3.setRSAOAEPParameters(new RSAOAEPParameters(SignatureConstants.ALGO_ID_DIGEST_SHA1, null, "oaep-params-3"));
        rsaEncryptionMethod = buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        digestMethod = buildXMLObject(DigestMethod.DEFAULT_ELEMENT_NAME);
        digestMethod.setAlgorithm(EncryptionConstants.ALGO_ID_DIGEST_SHA256);
        rsaEncryptionMethod.getUnknownXMLObjects().add(digestMethod);
        keyDescriptor.getEncryptionMethods().clear();
        keyDescriptor.getEncryptionMethods().add(rsaEncryptionMethod);
        params = resolver.resolveSingle(criteriaSet);
        Assert.assertNotNull(params.getRSAOAEPParameters());
        Assert.assertEquals(params.getRSAOAEPParameters().getDigestMethod(), EncryptionConstants.ALGO_ID_DIGEST_SHA256);
        Assert.assertNull(params.getRSAOAEPParameters().getMaskGenerationFunction());
        Assert.assertNull(params.getRSAOAEPParameters().getOAEPParams());
        
        // Should resolve digest from metadata, should resolve OAEPParms from config3
        config3.setRSAOAEPParameters(new RSAOAEPParameters(SignatureConstants.ALGO_ID_DIGEST_SHA1, null, "oaep-params-3"));
        resolver.setMergeMetadataRSAOAEPParametersWithConfig(true);
        rsaEncryptionMethod = buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        digestMethod = buildXMLObject(DigestMethod.DEFAULT_ELEMENT_NAME);
        digestMethod.setAlgorithm(EncryptionConstants.ALGO_ID_DIGEST_SHA256);
        rsaEncryptionMethod.getUnknownXMLObjects().add(digestMethod);
        keyDescriptor.getEncryptionMethods().clear();
        keyDescriptor.getEncryptionMethods().add(rsaEncryptionMethod);
        params = resolver.resolveSingle(criteriaSet);
        Assert.assertNotNull(params.getRSAOAEPParameters());
        Assert.assertEquals(params.getRSAOAEPParameters().getDigestMethod(), EncryptionConstants.ALGO_ID_DIGEST_SHA256);
        Assert.assertNull(params.getRSAOAEPParameters().getMaskGenerationFunction());
        Assert.assertEquals(params.getRSAOAEPParameters().getOAEPParams(), "oaep-params-3");
    }
    
    @Test
    public void testKeyTransportAlgorithmPredicate() throws ResolverException {
        KeyDescriptor keyDescriptor = buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey());
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP));
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15));
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256));
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192));
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128));
        roleDesc.getKeyDescriptors().add(keyDescriptor);
        
        
        // Data algorithm -> key transport algorithm preferences mappings
        HashMap<String,String> algoMap = new HashMap<>();
        algoMap.put(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256, EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        algoMap.put(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192, EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        KeyTransportAlgorithmPredicate predicate = new MapBasedKeyTransportAlgorithmPredicate(algoMap);
        
        // Without the predicate, for control
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        
        config1.setKeyTransportAlgorithmPredicate(predicate);
        
        // Explicit preference with predicate, mapping # 1
        params = resolver.resolveSingle(criteriaSet);
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        
        // Change algo ordering
        keyDescriptor.getEncryptionMethods().clear();
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15));
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP));
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192));
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128));
        
        // Explicit preference with predicate, mapping # 2
        params = resolver.resolveSingle(criteriaSet);
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192);
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        
    }
    
    @Test
    public void testEncryptionMethodWithBlacklist() throws ResolverException {
        KeyDescriptor keyDescriptor = buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey());
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15));
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES));
        roleDesc.getKeyDescriptors().add(keyDescriptor);
        
        config1.setBlacklistedAlgorithms(Lists.newArrayList(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15, EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getPublicKey(), rsaCred1.getPublicKey());
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), defaultRSAKeyTransportAlgo);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        Assert.assertNull(params.getDataEncryptionCredential());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        Assert.assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testEncryptionMethodWithWhitelist() throws ResolverException {
        KeyDescriptor keyDescriptor = buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey());
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15));
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES));
        roleDesc.getKeyDescriptors().add(keyDescriptor);
        
        config1.setWhitelistedAlgorithms(Lists.newArrayList(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP, EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getPublicKey(), rsaCred1.getPublicKey());
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        Assert.assertNull(params.getDataEncryptionCredential());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192);
        Assert.assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testMultipleKeyDescriptors() throws ResolverException {
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(dsaCred1KeyName, UsageType.SIGNING, dsaCred1.getPublicKey()));
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey()));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getPublicKey(), rsaCred1.getPublicKey());
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), defaultRSAKeyTransportAlgo);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        Assert.assertNull(params.getDataEncryptionCredential());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        Assert.assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testOnlySigningDescriptor() throws ResolverException {
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(dsaCred1KeyName, UsageType.SIGNING, dsaCred1.getPublicKey()));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNull(params);
    }
    
    @Test
    public void testDSACredWithUnspecifiedUse() throws ResolverException {
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(dsaCred1KeyName, null, dsaCred1.getPublicKey()));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNull(params);
    }
    
    @Test
    public void testRSACredWithUnspecifiedUse() throws ResolverException {
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(rsaCred1KeyName, null, rsaCred1.getPublicKey()));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getPublicKey(), rsaCred1.getPublicKey());
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), defaultRSAKeyTransportAlgo);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        Assert.assertNull(params.getDataEncryptionCredential());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        Assert.assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testKeyInfoGenerationProfile() throws ResolverException {
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey()));
        resolver.setAutoGenerateDataEncryptionCredential(true);
        
        criteriaSet.add(new KeyInfoGenerationProfileCriterion("testKeyInfoProfile"));
        
        defaultDataEncryptionKeyInfoGeneratorManager.setUseDefaultManager(true);
        defaultKeyTransportKeyInfoGeneratorManager.setUseDefaultManager(true);
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params.getDataKeyInfoGenerator());
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        defaultDataEncryptionKeyInfoGeneratorManager.setUseDefaultManager(false);
        defaultKeyTransportKeyInfoGeneratorManager.setUseDefaultManager(false);
        
        params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNull(params.getDataKeyInfoGenerator());
        Assert.assertNull(params.getKeyTransportKeyInfoGenerator());
        
        defaultDataEncryptionKeyInfoGeneratorManager.setUseDefaultManager(false);
        defaultKeyTransportKeyInfoGeneratorManager.setUseDefaultManager(false);
        defaultDataEncryptionKeyInfoGeneratorManager.registerFactory("testKeyInfoProfile", new BasicKeyInfoGeneratorFactory());
        defaultKeyTransportKeyInfoGeneratorManager.registerFactory("testKeyInfoProfile", new BasicKeyInfoGeneratorFactory());
        
        params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params.getDataKeyInfoGenerator());
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
    }
    
    @Test
    public void testResolve() throws ResolverException {
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey()));
        
        Iterable<EncryptionParameters> paramsIter = resolver.resolve(criteriaSet);
        Assert.assertNotNull(paramsIter);
        
        Iterator<EncryptionParameters> iterator = paramsIter.iterator();
        Assert.assertNotNull(iterator);
        
        Assert.assertTrue(iterator.hasNext());
        
        EncryptionParameters params = iterator.next();
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getPublicKey(), rsaCred1.getPublicKey());
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), defaultRSAKeyTransportAlgo);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        Assert.assertNull(params.getDataEncryptionCredential());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        Assert.assertNull(params.getDataKeyInfoGenerator());
        
        Assert.assertFalse(iterator.hasNext());
    }
    
    @Test
    public void testNoCredentials() throws ResolverException {
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNull(params);
    }
    
    @Test
    public void testNoKeyTransportAlgorithms() throws ResolverException {
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey())); 
        config3.setKeyTransportEncryptionAlgorithms(new ArrayList<String>());
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNull(params);
    }
    
    @Test
    public void testNoDataEncryptionAlgorithmForEncrypterAutoGen() throws ResolverException {
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey())); 
        config3.setDataEncryptionAlgorithms(new ArrayList<String>());
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
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
    
    private KeyDescriptor buildKeyDescriptor(String keyName, UsageType use, Object ... contentItems) {
        KeyDescriptor keyDesc = buildXMLObject(KeyDescriptor.DEFAULT_ELEMENT_NAME);
        KeyInfo keyInfo = buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME);
        
        for (Object contentItem : contentItems) {
            if (contentItem instanceof PublicKey) {
                KeyInfoSupport.addPublicKey(keyInfo, (PublicKey) contentItem);
            } else if (contentItem instanceof X509Certificate) {
                try {
                    KeyInfoSupport.addCertificate(keyInfo, (X509Certificate) contentItem);
                } catch (CertificateEncodingException e) {
                    throw new RuntimeException("CertificateEncodingException ading cert to KeyInfo", e);
                }
            } else {
                throw new RuntimeException("Saw unknown KeyInfo content type: " + contentItem.getClass().getName());
            }
        }
        
        if (keyName != null) {
            KeyInfoSupport.addKeyName(keyInfo, keyName);
        }
        
        keyDesc.setKeyInfo(keyInfo);
        
        if (use != null) {
            keyDesc.setUse(use);
        }
        
        return keyDesc;
    }
    
    private EncryptionMethod buildEncryptionMethod(String algorithm) {
       EncryptionMethod encMethod = buildXMLObject(EncryptionMethod.DEFAULT_ELEMENT_NAME); 
       encMethod.setAlgorithm(algorithm);
       return encMethod;
    }
    
    
    // Test utility classes
    
    public class MapBasedKeyTransportAlgorithmPredicate implements KeyTransportAlgorithmPredicate {
        private Map<String,String> algoMap;
        
        public MapBasedKeyTransportAlgorithmPredicate(Map<String,String> algoMap) {
            this.algoMap = algoMap;
        }
        
        public boolean apply(@Nullable SelectionInput input) {
            return this.algoMap.get(input.getDataEncryptionAlgorithm()).equals(input.getKeyTransportAlgorithm());
        }
    }

}
