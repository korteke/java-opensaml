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
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.xmlsec.crypto.AlgorithmSupport;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 */
public class AlgorithmDescriptorsTest extends OpenSAMLInitBaseTestCase {
   
    // BlockEncryption
    @Test
    public void testBlockEncryption() {
        AlgorithmRegistry registry = AlgorithmSupport.getGlobalAlgorithmRegistry();
        Assert.assertNotNull(registry);
        
        BlockEncryptionAlgorithm descriptor;
        
        descriptor = (BlockEncryptionAlgorithm) registry.get(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);
        Assert.assertEquals(descriptor.getCipherMode(), JCAConstants.CIPHER_MODE_CBC);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), "AES/CBC/ISO10126Padding");
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_AES);
        Assert.assertEquals(descriptor.getPadding(), JCAConstants.CIPHER_PADDING_ISO10126);
        Assert.assertEquals(descriptor.getURI(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);
        Assert.assertEquals(descriptor.getKeyLength(), new Integer(128));
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.BlockEncryption);
        
        descriptor = (BlockEncryptionAlgorithm) registry.get(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM);
        Assert.assertEquals(descriptor.getCipherMode(), JCAConstants.CIPHER_MODE_GCM);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), "AES/GCM/NoPadding");
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_AES);
        Assert.assertEquals(descriptor.getPadding(), JCAConstants.CIPHER_PADDING_NONE);
        Assert.assertEquals(descriptor.getURI(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM);
        Assert.assertEquals(descriptor.getKeyLength(), new Integer(128));
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.BlockEncryption);
        
        descriptor = (BlockEncryptionAlgorithm) registry.get(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192);
        Assert.assertEquals(descriptor.getCipherMode(), JCAConstants.CIPHER_MODE_CBC);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), "AES/CBC/ISO10126Padding");
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_AES);
        Assert.assertEquals(descriptor.getPadding(), JCAConstants.CIPHER_PADDING_ISO10126);
        Assert.assertEquals(descriptor.getURI(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192);
        Assert.assertEquals(descriptor.getKeyLength(), new Integer(192));
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.BlockEncryption);
        
        descriptor = (BlockEncryptionAlgorithm) registry.get(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192_GCM);
        Assert.assertEquals(descriptor.getCipherMode(), JCAConstants.CIPHER_MODE_GCM);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), "AES/GCM/NoPadding");
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_AES);
        Assert.assertEquals(descriptor.getPadding(), JCAConstants.CIPHER_PADDING_NONE);
        Assert.assertEquals(descriptor.getURI(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192_GCM);
        Assert.assertEquals(descriptor.getKeyLength(), new Integer(192));
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.BlockEncryption);
        
        descriptor = (BlockEncryptionAlgorithm) registry.get(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);
        Assert.assertEquals(descriptor.getCipherMode(), JCAConstants.CIPHER_MODE_CBC);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), "AES/CBC/ISO10126Padding");
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_AES);
        Assert.assertEquals(descriptor.getPadding(), JCAConstants.CIPHER_PADDING_ISO10126);
        Assert.assertEquals(descriptor.getURI(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);
        Assert.assertEquals(descriptor.getKeyLength(), new Integer(256));
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.BlockEncryption);
        
        descriptor = (BlockEncryptionAlgorithm) registry.get(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM);
        Assert.assertEquals(descriptor.getCipherMode(), JCAConstants.CIPHER_MODE_GCM);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), "AES/GCM/NoPadding");
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_AES);
        Assert.assertEquals(descriptor.getPadding(), JCAConstants.CIPHER_PADDING_NONE);
        Assert.assertEquals(descriptor.getURI(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM);
        Assert.assertEquals(descriptor.getKeyLength(), new Integer(256));
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.BlockEncryption);
        
        descriptor = (BlockEncryptionAlgorithm) registry.get(EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES);
        Assert.assertEquals(descriptor.getCipherMode(), JCAConstants.CIPHER_MODE_CBC);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), "DESede/CBC/ISO10126Padding");
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_DESEDE);
        Assert.assertEquals(descriptor.getPadding(), JCAConstants.CIPHER_PADDING_ISO10126);
        Assert.assertEquals(descriptor.getURI(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES);
        Assert.assertEquals(descriptor.getKeyLength(), new Integer(192));
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.BlockEncryption);
    }
    
    // Digest
    @Test
    public void testDigest() {
        AlgorithmRegistry registry = AlgorithmSupport.getGlobalAlgorithmRegistry();
        Assert.assertNotNull(registry);
        
        DigestAlgorithm descriptor;
        
        descriptor = (DigestAlgorithm) registry.get(SignatureConstants.ALGO_ID_DIGEST_NOT_RECOMMENDED_MD5);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.DIGEST_MD5); 
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_DIGEST_NOT_RECOMMENDED_MD5); 
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.MessageDigest); 
        
        descriptor = (DigestAlgorithm) registry.get(SignatureConstants.ALGO_ID_DIGEST_RIPEMD160);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.DIGEST_RIPEMD160); 
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_DIGEST_RIPEMD160); 
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.MessageDigest); 
        
        descriptor = (DigestAlgorithm) registry.get(SignatureConstants.ALGO_ID_DIGEST_SHA1);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.DIGEST_SHA1); 
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_DIGEST_SHA1); 
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.MessageDigest); 
        
        descriptor = (DigestAlgorithm) registry.get(SignatureConstants.ALGO_ID_DIGEST_SHA256);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.DIGEST_SHA256); 
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_DIGEST_SHA256); 
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.MessageDigest); 
        
        descriptor = (DigestAlgorithm) registry.get(SignatureConstants.ALGO_ID_DIGEST_SHA384);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.DIGEST_SHA384); 
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_DIGEST_SHA384); 
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.MessageDigest); 
        
        descriptor = (DigestAlgorithm) registry.get(SignatureConstants.ALGO_ID_DIGEST_SHA512);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.DIGEST_SHA512); 
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_DIGEST_SHA512); 
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.MessageDigest); 
    }
    
    // HMAC
    @Test
    public void testHMAC() {
        AlgorithmRegistry registry = AlgorithmSupport.getGlobalAlgorithmRegistry();
        Assert.assertNotNull(registry);
        
        MACAlgorithm descriptor;
        
        descriptor = (MACAlgorithm) registry.get(SignatureConstants.ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5);
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_MD5);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.HMAC_MD5);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Mac);
        
        descriptor = (MACAlgorithm) registry.get(SignatureConstants.ALGO_ID_MAC_HMAC_RIPEMD160);
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_RIPEMD160);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.HMAC_RIPEMD160);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_MAC_HMAC_RIPEMD160);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Mac);
        
        descriptor = (MACAlgorithm) registry.get(SignatureConstants.ALGO_ID_MAC_HMAC_SHA1);
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA1);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.HMAC_SHA1);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_MAC_HMAC_SHA1);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Mac);
        
        descriptor = (MACAlgorithm) registry.get(SignatureConstants.ALGO_ID_MAC_HMAC_SHA256);
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA256);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.HMAC_SHA256);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_MAC_HMAC_SHA256);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Mac);
        
        descriptor = (MACAlgorithm) registry.get(SignatureConstants.ALGO_ID_MAC_HMAC_SHA384);
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA384);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.HMAC_SHA384);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_MAC_HMAC_SHA384);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Mac);
        
        descriptor = (MACAlgorithm) registry.get(SignatureConstants.ALGO_ID_MAC_HMAC_SHA512);
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA512);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.HMAC_SHA512);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_MAC_HMAC_SHA512);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Mac);
    }
    
    // KeyTransport
    @Test
    public void testKeyTransport() {
        AlgorithmRegistry registry = AlgorithmSupport.getGlobalAlgorithmRegistry();
        Assert.assertNotNull(registry);
        
        KeyTransportAlgorithm descriptor;
        
        descriptor = (KeyTransportAlgorithm) registry.get(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        Assert.assertEquals(descriptor.getCipherMode(), JCAConstants.CIPHER_MODE_ECB);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), "RSA/ECB/PKCS1Padding");
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_RSA);
        Assert.assertEquals(descriptor.getPadding(), JCAConstants.CIPHER_PADDING_PKCS1);
        Assert.assertEquals(descriptor.getURI(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.KeyTransport);
        
        
        descriptor = (KeyTransportAlgorithm) registry.get(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        Assert.assertEquals(descriptor.getCipherMode(), JCAConstants.CIPHER_MODE_ECB);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), "RSA/ECB/OAEPPadding");
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_RSA);
        Assert.assertEquals(descriptor.getPadding(), JCAConstants.CIPHER_PADDING_OAEP);
        Assert.assertEquals(descriptor.getURI(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.KeyTransport);
        
        descriptor = (KeyTransportAlgorithm) registry.get(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11);
        Assert.assertEquals(descriptor.getCipherMode(), JCAConstants.CIPHER_MODE_ECB);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), "RSA/ECB/OAEPPadding");
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_RSA);
        Assert.assertEquals(descriptor.getPadding(), JCAConstants.CIPHER_PADDING_OAEP);
        Assert.assertEquals(descriptor.getURI(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.KeyTransport);
    }
    
    // Signature
    @Test
    public void testSignature() {
        AlgorithmRegistry registry = AlgorithmSupport.getGlobalAlgorithmRegistry();
        Assert.assertNotNull(registry);
        
        SignatureAlgorithm descriptor;
        
        descriptor = (SignatureAlgorithm) registry.get(SignatureConstants.ALGO_ID_SIGNATURE_DSA_SHA1);
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA1);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.SIGNATURE_DSA_SHA1);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_DSA);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_SIGNATURE_DSA_SHA1);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Signature);
        
        descriptor = (SignatureAlgorithm) registry.get(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA1);
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA1);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.SIGNATURE_ECDSA_SHA1);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_EC);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA1);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Signature);
        
        descriptor = (SignatureAlgorithm) registry.get(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA256);
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA256);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.SIGNATURE_ECDSA_SHA256);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_EC);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA256);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Signature);
        
        descriptor = (SignatureAlgorithm) registry.get(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA384);
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA384);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.SIGNATURE_ECDSA_SHA384);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_EC);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA384);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Signature);
        
        descriptor = (SignatureAlgorithm) registry.get(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA512);
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA512);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.SIGNATURE_ECDSA_SHA512);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_EC);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA512);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Signature);
        
        descriptor = (SignatureAlgorithm) registry.get(SignatureConstants.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5);
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_MD5);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.SIGNATURE_RSA_MD5);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_RSA);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Signature);
        
        descriptor = (SignatureAlgorithm) registry.get(SignatureConstants.ALGO_ID_SIGNATURE_RSA_RIPEMD160);
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_RIPEMD160);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.SIGNATURE_RSA_RIPEMD160);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_RSA);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_SIGNATURE_RSA_RIPEMD160);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Signature);
        
        descriptor = (SignatureAlgorithm) registry.get(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA1);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.SIGNATURE_RSA_SHA1);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_RSA);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Signature);
        
        descriptor = (SignatureAlgorithm) registry.get(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA256);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.SIGNATURE_RSA_SHA256);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_RSA);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Signature);
        
        descriptor = (SignatureAlgorithm) registry.get(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA384);
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA384);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.SIGNATURE_RSA_SHA384);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_RSA);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA384);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Signature);
        
        descriptor = (SignatureAlgorithm) registry.get(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512);
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA512);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.SIGNATURE_RSA_SHA512);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_RSA);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Signature);
    }
    
    // SymmetricKeyWrap
    @Test
    public void testSymmetricKeyWrap() {
        AlgorithmRegistry registry = AlgorithmSupport.getGlobalAlgorithmRegistry();
        Assert.assertNotNull(registry);
        
        SymmetricKeyWrapAlgorithm descriptor;
        
        descriptor = (SymmetricKeyWrapAlgorithm) registry.get(EncryptionConstants.ALGO_ID_KEYWRAP_AES128);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.KEYWRAP_ALGO_AES);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_AES);
        Assert.assertEquals(descriptor.getURI(), EncryptionConstants.ALGO_ID_KEYWRAP_AES128);
        Assert.assertEquals(descriptor.getKeyLength(), new Integer(128));
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.SymmetricKeyWrap);
        
        descriptor = (SymmetricKeyWrapAlgorithm) registry.get(EncryptionConstants.ALGO_ID_KEYWRAP_AES192);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.KEYWRAP_ALGO_AES);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_AES);
        Assert.assertEquals(descriptor.getURI(), EncryptionConstants.ALGO_ID_KEYWRAP_AES192);
        Assert.assertEquals(descriptor.getKeyLength(), new Integer(192));
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.SymmetricKeyWrap);
        
        descriptor = (SymmetricKeyWrapAlgorithm) registry.get(EncryptionConstants.ALGO_ID_KEYWRAP_AES256);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.KEYWRAP_ALGO_AES);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_AES);
        Assert.assertEquals(descriptor.getURI(), EncryptionConstants.ALGO_ID_KEYWRAP_AES256);
        Assert.assertEquals(descriptor.getKeyLength(), new Integer(256));
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.SymmetricKeyWrap);
        
        descriptor = (SymmetricKeyWrapAlgorithm) registry.get(EncryptionConstants.ALGO_ID_KEYWRAP_TRIPLEDES);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.KEYWRAP_ALGO_DESEDE);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_DESEDE);
        Assert.assertEquals(descriptor.getURI(), EncryptionConstants.ALGO_ID_KEYWRAP_TRIPLEDES);
        Assert.assertEquals(descriptor.getKeyLength(), new Integer(192));
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.SymmetricKeyWrap);
        
    }

}
