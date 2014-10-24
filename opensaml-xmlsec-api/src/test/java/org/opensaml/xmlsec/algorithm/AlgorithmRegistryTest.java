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

package org.opensaml.xmlsec.algorithm;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.security.SecurityProviderTestSupport;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.xmlsec.algorithm.descriptors.DigestSHA256;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureRSASHA256;
import org.opensaml.xmlsec.config.GlobalAlgorithmRegistryInitializer;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests for the AlgorithmRegistry.
 */
public class AlgorithmRegistryTest extends OpenSAMLInitBaseTestCase {
    
    private SecurityProviderTestSupport providerSupport;
    
    public AlgorithmRegistryTest() {
        providerSupport = new SecurityProviderTestSupport();
    }
    
    @DataProvider
    public Object[][] loadBCTestData() {
        return new Object[][] {
                new Object[] {Boolean.FALSE},
                new Object[] {Boolean.TRUE},
        };
    }
    
    @Test
    public void testRegistrationAndDeregistration() {
        AlgorithmRegistry registry = new AlgorithmRegistry();
        
        Assert.assertNull(registry.get(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256));
        Assert.assertNull(registry.get(SignatureConstants.ALGO_ID_DIGEST_SHA256));
        
        registry.register(new SignatureRSASHA256());
        registry.register(new DigestSHA256());
        
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256));
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_DIGEST_SHA256));
        
        registry.deregister(new SignatureRSASHA256());
        Assert.assertNull(registry.get(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256));
        
        registry.deregister(SignatureConstants.ALGO_ID_DIGEST_SHA256);
        Assert.assertNull(registry.get(SignatureConstants.ALGO_ID_DIGEST_SHA256));
    }
    
    @Test
    public void testSignatureIndexing() {
        AlgorithmRegistry registry = new AlgorithmRegistry();
        Assert.assertNull(registry.get(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256));
        Assert.assertNull(registry.getSignatureAlgorithm(JCAConstants.KEY_ALGO_RSA, JCAConstants.DIGEST_SHA256));
        
        registry.register(new SignatureRSASHA256());
        
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256));
        Assert.assertNotNull(registry.getSignatureAlgorithm(JCAConstants.KEY_ALGO_RSA, JCAConstants.DIGEST_SHA256));
        
        registry.deregister(new SignatureRSASHA256());
        Assert.assertNull(registry.get(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256));
        Assert.assertNull(registry.getSignatureAlgorithm(JCAConstants.KEY_ALGO_RSA, JCAConstants.DIGEST_SHA256));
    }
    
    @Test
    public void testDigestIndexing() {
        AlgorithmRegistry registry = new AlgorithmRegistry();
        Assert.assertNull(registry.get(SignatureConstants.ALGO_ID_DIGEST_SHA256));
        Assert.assertNull(registry.getDigestAlgorithm(JCAConstants.DIGEST_SHA256));
        
        registry.register(new DigestSHA256());
        
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_DIGEST_SHA256));
        Assert.assertNotNull(registry.getDigestAlgorithm(JCAConstants.DIGEST_SHA256));
        
        registry.deregister(new DigestSHA256());
        Assert.assertNull(registry.get(SignatureConstants.ALGO_ID_DIGEST_SHA256));
        Assert.assertNull(registry.getDigestAlgorithm(JCAConstants.DIGEST_SHA256));
    }
    
    @Test
    public void testGlobalRegistryPresence() {
        AlgorithmRegistry registry = AlgorithmSupport.getGlobalAlgorithmRegistry();
        Assert.assertNotNull(registry);
        
        // Test all expected types from default auto-loaded set
        
        // BlockEncryption
        Assert.assertNotNull(registry.get(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128));
        Assert.assertNotNull(registry.get(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM));
        Assert.assertNotNull(registry.get(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192));
        Assert.assertNotNull(registry.get(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192_GCM));
        Assert.assertNotNull(registry.get(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256));
        Assert.assertNotNull(registry.get(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM));
        Assert.assertNotNull(registry.get(EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES));
        
        // Digest
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_DIGEST_NOT_RECOMMENDED_MD5));
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_DIGEST_RIPEMD160));
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_DIGEST_SHA1));
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_DIGEST_SHA224));
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_DIGEST_SHA256));
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_DIGEST_SHA384));
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_DIGEST_SHA512));
        
        // HMAC
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5));
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_MAC_HMAC_RIPEMD160));
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_MAC_HMAC_SHA1));
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_MAC_HMAC_SHA224));
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_MAC_HMAC_SHA256));
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_MAC_HMAC_SHA384));
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_MAC_HMAC_SHA512));
        
        // KeyTransport
        Assert.assertNotNull(registry.get(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15));
        Assert.assertNotNull(registry.get(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP));
        Assert.assertNotNull(registry.get(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11));
        
        // Signature
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_SIGNATURE_DSA_SHA1));
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_SIGNATURE_DSA_SHA256));
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA1));
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA224));
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA256));
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA384));
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA512));
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5));
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_SIGNATURE_RSA_RIPEMD160));
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1));
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA224));
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256));
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA384));
        Assert.assertNotNull(registry.get(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512));
        
        // SymmetricKeyWrap
        Assert.assertNotNull(registry.get(EncryptionConstants.ALGO_ID_KEYWRAP_AES128));
        Assert.assertNotNull(registry.get(EncryptionConstants.ALGO_ID_KEYWRAP_AES192));
        Assert.assertNotNull(registry.get(EncryptionConstants.ALGO_ID_KEYWRAP_AES256));
        Assert.assertNotNull(registry.get(EncryptionConstants.ALGO_ID_KEYWRAP_TRIPLEDES));
        
    }
    
    @Test(dataProvider = "loadBCTestData")
    public void testGlobalRegistryRuntimeSupported(boolean loadBC) throws InitializationException {
        AlgorithmRegistry originalRegistry = AlgorithmSupport.getGlobalAlgorithmRegistry();
        Assert.assertNotNull(originalRegistry);
        AlgorithmRegistry registry = originalRegistry;
        
        if (loadBC) {
            providerSupport.loadBC();
            new GlobalAlgorithmRegistryInitializer().init();
            registry = AlgorithmSupport.getGlobalAlgorithmRegistry();
        }
        
        Assert.assertNotNull(registry);
        
        try {
            // First test all default algos expected to be supported unconditionally on 
            // vanilla Java 7+ with no additional security providers
            
            // BlockEncryption
            Assert.assertTrue(registry.isRuntimeSupported(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128));
            Assert.assertTrue(registry.isRuntimeSupported(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192));
            Assert.assertTrue(registry.isRuntimeSupported(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256));
            Assert.assertTrue(registry.isRuntimeSupported(EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES));
            
            // Digest
            Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_DIGEST_NOT_RECOMMENDED_MD5));
            Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_DIGEST_SHA1));
            Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_DIGEST_SHA256));
            Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_DIGEST_SHA384));
            Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_DIGEST_SHA512));
            
            // HMAC
            Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5));
            Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_MAC_HMAC_SHA1));
            Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_MAC_HMAC_SHA256));
            Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_MAC_HMAC_SHA384));
            Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_MAC_HMAC_SHA512));
            
            // KeyTransport
            Assert.assertTrue(registry.isRuntimeSupported(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15));
            Assert.assertTrue(registry.isRuntimeSupported(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP));
            
            // Signature
            Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_SIGNATURE_DSA_SHA1));
            Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5));
            Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1));
            Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256));
            Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA384));
            Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512));
            
            // SymmetricKeyWrap
            Assert.assertTrue(registry.isRuntimeSupported(EncryptionConstants.ALGO_ID_KEYWRAP_AES128));
            Assert.assertTrue(registry.isRuntimeSupported(EncryptionConstants.ALGO_ID_KEYWRAP_AES192));
            Assert.assertTrue(registry.isRuntimeSupported(EncryptionConstants.ALGO_ID_KEYWRAP_AES256));
            Assert.assertTrue(registry.isRuntimeSupported(EncryptionConstants.ALGO_ID_KEYWRAP_TRIPLEDES));
            
            
            
            // Conditional environment tests
            
            if (providerSupport.haveSunEC() || providerSupport.haveBC() || providerSupport.haveJavaGreaterOrEqual(8)) {
                Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA1));
                Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA256));
                Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA384));
                Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA512));
                
                if (providerSupport.haveBC() || providerSupport.haveJavaGreaterOrEqual(8)) {
                    Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA224));
                }
            } else {
                Assert.assertFalse(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA1));
                Assert.assertFalse(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA224));
                Assert.assertFalse(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA256));
                Assert.assertFalse(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA384));
                Assert.assertFalse(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA512));
            }
            
            if (providerSupport.haveBC() || providerSupport.haveJavaGreaterOrEqual(8)) {
                Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_DIGEST_SHA224));
                Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA224));
                Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_MAC_HMAC_SHA224));
                
                Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_SIGNATURE_DSA_SHA256));
                
                Assert.assertTrue(registry.isRuntimeSupported(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM));
                Assert.assertTrue(registry.isRuntimeSupported(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192_GCM));
                Assert.assertTrue(registry.isRuntimeSupported(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM));
                Assert.assertTrue(registry.isRuntimeSupported(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11));
            } else {
                if (providerSupport.isOpenJDK()) {
                    Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_DIGEST_SHA224));
                    Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA224));
                    Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_MAC_HMAC_SHA224));
                } else {
                    Assert.assertFalse(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_DIGEST_SHA224));
                    Assert.assertFalse(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA224));
                    Assert.assertFalse(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_MAC_HMAC_SHA224));
                }
                
                Assert.assertFalse(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_SIGNATURE_DSA_SHA256));
                
                Assert.assertFalse(registry.isRuntimeSupported(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM));
                Assert.assertFalse(registry.isRuntimeSupported(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192_GCM));
                Assert.assertFalse(registry.isRuntimeSupported(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM));
                Assert.assertFalse(registry.isRuntimeSupported(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11));
            }
            
            if (providerSupport.haveBC()) {
                Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_DIGEST_RIPEMD160));
                Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_MAC_HMAC_RIPEMD160));
                Assert.assertTrue(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_SIGNATURE_RSA_RIPEMD160));
            } else {
                Assert.assertFalse(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_DIGEST_RIPEMD160));
                Assert.assertFalse(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_MAC_HMAC_RIPEMD160));
                Assert.assertFalse(registry.isRuntimeSupported(SignatureConstants.ALGO_ID_SIGNATURE_RSA_RIPEMD160));
            }
        
        } finally {
            providerSupport.unloadBC();
            ConfigurationService.register(AlgorithmRegistry.class, originalRegistry);
        }
        
    }
    


}
