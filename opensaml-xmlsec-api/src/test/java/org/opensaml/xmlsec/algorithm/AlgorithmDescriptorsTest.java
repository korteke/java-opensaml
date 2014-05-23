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

import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.xmlsec.algorithm.descriptors.BlockEncryptionAES128CBC;
import org.opensaml.xmlsec.algorithm.descriptors.BlockEncryptionAES128GCM;
import org.opensaml.xmlsec.algorithm.descriptors.BlockEncryptionAES192CBC;
import org.opensaml.xmlsec.algorithm.descriptors.BlockEncryptionAES192GCM;
import org.opensaml.xmlsec.algorithm.descriptors.BlockEncryptionAES256CBC;
import org.opensaml.xmlsec.algorithm.descriptors.BlockEncryptionAES256GCM;
import org.opensaml.xmlsec.algorithm.descriptors.BlockEncryptionDESede;
import org.opensaml.xmlsec.algorithm.descriptors.DigestMD5;
import org.opensaml.xmlsec.algorithm.descriptors.DigestRIPEMD160;
import org.opensaml.xmlsec.algorithm.descriptors.DigestSHA1;
import org.opensaml.xmlsec.algorithm.descriptors.DigestSHA224;
import org.opensaml.xmlsec.algorithm.descriptors.DigestSHA256;
import org.opensaml.xmlsec.algorithm.descriptors.DigestSHA384;
import org.opensaml.xmlsec.algorithm.descriptors.DigestSHA512;
import org.opensaml.xmlsec.algorithm.descriptors.HMACMD5;
import org.opensaml.xmlsec.algorithm.descriptors.HMACRIPEMD160;
import org.opensaml.xmlsec.algorithm.descriptors.HMACSHA1;
import org.opensaml.xmlsec.algorithm.descriptors.HMACSHA224;
import org.opensaml.xmlsec.algorithm.descriptors.HMACSHA256;
import org.opensaml.xmlsec.algorithm.descriptors.HMACSHA384;
import org.opensaml.xmlsec.algorithm.descriptors.HMACSHA512;
import org.opensaml.xmlsec.algorithm.descriptors.KeyTransportRSA15;
import org.opensaml.xmlsec.algorithm.descriptors.KeyTransportRSAOAEP;
import org.opensaml.xmlsec.algorithm.descriptors.KeyTransportRSAOAEPMGF1P;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureDSASHA1;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureDSASHA256;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureECDSASHA1;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureECDSASHA224;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureECDSASHA256;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureECDSASHA384;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureECDSASHA512;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureRSAMD5;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureRSARIPEMD160;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureRSASHA1;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureRSASHA224;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureRSASHA256;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureRSASHA384;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureRSASHA512;
import org.opensaml.xmlsec.algorithm.descriptors.SymmetricKeyWrapAES128;
import org.opensaml.xmlsec.algorithm.descriptors.SymmetricKeyWrapAES192;
import org.opensaml.xmlsec.algorithm.descriptors.SymmetricKeyWrapAES256;
import org.opensaml.xmlsec.algorithm.descriptors.SymmetricKeyWrapDESede;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 */
public class AlgorithmDescriptorsTest {
   
    // BlockEncryption
    @Test
    public void testBlockEncryption() {
        BlockEncryptionAlgorithm descriptor;
        
        descriptor = new BlockEncryptionAES128CBC();
        Assert.assertEquals(descriptor.getCipherMode(), JCAConstants.CIPHER_MODE_CBC);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), "AES/CBC/ISO10126Padding");
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_AES);
        Assert.assertEquals(descriptor.getPadding(), JCAConstants.CIPHER_PADDING_ISO10126);
        Assert.assertEquals(descriptor.getURI(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);
        Assert.assertEquals(descriptor.getKeyLength(), new Integer(128));
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.BlockEncryption);
        
        descriptor = new BlockEncryptionAES128GCM();
        Assert.assertEquals(descriptor.getCipherMode(), JCAConstants.CIPHER_MODE_GCM);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), "AES/GCM/NoPadding");
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_AES);
        Assert.assertEquals(descriptor.getPadding(), JCAConstants.CIPHER_PADDING_NONE);
        Assert.assertEquals(descriptor.getURI(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM);
        Assert.assertEquals(descriptor.getKeyLength(), new Integer(128));
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.BlockEncryption);
        
        descriptor = new BlockEncryptionAES192CBC();
        Assert.assertEquals(descriptor.getCipherMode(), JCAConstants.CIPHER_MODE_CBC);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), "AES/CBC/ISO10126Padding");
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_AES);
        Assert.assertEquals(descriptor.getPadding(), JCAConstants.CIPHER_PADDING_ISO10126);
        Assert.assertEquals(descriptor.getURI(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192);
        Assert.assertEquals(descriptor.getKeyLength(), new Integer(192));
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.BlockEncryption);
        
        descriptor = new BlockEncryptionAES192GCM();
        Assert.assertEquals(descriptor.getCipherMode(), JCAConstants.CIPHER_MODE_GCM);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), "AES/GCM/NoPadding");
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_AES);
        Assert.assertEquals(descriptor.getPadding(), JCAConstants.CIPHER_PADDING_NONE);
        Assert.assertEquals(descriptor.getURI(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192_GCM);
        Assert.assertEquals(descriptor.getKeyLength(), new Integer(192));
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.BlockEncryption);
        
        descriptor = new BlockEncryptionAES256CBC();
        Assert.assertEquals(descriptor.getCipherMode(), JCAConstants.CIPHER_MODE_CBC);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), "AES/CBC/ISO10126Padding");
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_AES);
        Assert.assertEquals(descriptor.getPadding(), JCAConstants.CIPHER_PADDING_ISO10126);
        Assert.assertEquals(descriptor.getURI(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);
        Assert.assertEquals(descriptor.getKeyLength(), new Integer(256));
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.BlockEncryption);
        
        descriptor = new BlockEncryptionAES256GCM();
        Assert.assertEquals(descriptor.getCipherMode(), JCAConstants.CIPHER_MODE_GCM);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), "AES/GCM/NoPadding");
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_AES);
        Assert.assertEquals(descriptor.getPadding(), JCAConstants.CIPHER_PADDING_NONE);
        Assert.assertEquals(descriptor.getURI(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM);
        Assert.assertEquals(descriptor.getKeyLength(), new Integer(256));
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.BlockEncryption);
        
        descriptor = new BlockEncryptionDESede();
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
        DigestAlgorithm descriptor;
        
        descriptor = new DigestMD5();
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.DIGEST_MD5); 
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_DIGEST_NOT_RECOMMENDED_MD5); 
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.MessageDigest); 
        
        descriptor = new DigestRIPEMD160();
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.DIGEST_RIPEMD160); 
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_DIGEST_RIPEMD160); 
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.MessageDigest); 
        
        descriptor = new DigestSHA1();
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.DIGEST_SHA1); 
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_DIGEST_SHA1); 
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.MessageDigest); 
        
        descriptor = new DigestSHA224();
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.DIGEST_SHA224); 
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_DIGEST_SHA224); 
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.MessageDigest); 
        
        descriptor = new DigestSHA256();
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.DIGEST_SHA256); 
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_DIGEST_SHA256); 
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.MessageDigest); 
        
        descriptor = new DigestSHA384();
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.DIGEST_SHA384); 
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_DIGEST_SHA384); 
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.MessageDigest); 
        
        descriptor = new DigestSHA512();
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.DIGEST_SHA512); 
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_DIGEST_SHA512); 
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.MessageDigest); 
    }
    
    // HMAC
    @Test
    public void testHMAC() {
        MACAlgorithm descriptor;
        
        descriptor = new HMACMD5();
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_MD5);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.HMAC_MD5);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Mac);
        
        descriptor = new HMACRIPEMD160();
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_RIPEMD160);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.HMAC_RIPEMD160);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_MAC_HMAC_RIPEMD160);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Mac);
        
        descriptor = new HMACSHA1();
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA1);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.HMAC_SHA1);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_MAC_HMAC_SHA1);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Mac);
        
        descriptor = new HMACSHA224();
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA224);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.HMAC_SHA224);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_MAC_HMAC_SHA224);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Mac);
        
        descriptor = new HMACSHA256();
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA256);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.HMAC_SHA256);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_MAC_HMAC_SHA256);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Mac);
        
        descriptor = new HMACSHA384();
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA384);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.HMAC_SHA384);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_MAC_HMAC_SHA384);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Mac);
        
        descriptor = new HMACSHA512();
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA512);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.HMAC_SHA512);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_MAC_HMAC_SHA512);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Mac);
    }
    
    // KeyTransport
    @Test
    public void testKeyTransport() {
        KeyTransportAlgorithm descriptor;
        
        descriptor = new KeyTransportRSA15();
        Assert.assertEquals(descriptor.getCipherMode(), JCAConstants.CIPHER_MODE_ECB);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), "RSA/ECB/PKCS1Padding");
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_RSA);
        Assert.assertEquals(descriptor.getPadding(), JCAConstants.CIPHER_PADDING_PKCS1);
        Assert.assertEquals(descriptor.getURI(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.KeyTransport);
        
        
        descriptor = new KeyTransportRSAOAEPMGF1P();
        Assert.assertEquals(descriptor.getCipherMode(), JCAConstants.CIPHER_MODE_ECB);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), "RSA/ECB/OAEPPadding");
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_RSA);
        Assert.assertEquals(descriptor.getPadding(), JCAConstants.CIPHER_PADDING_OAEP);
        Assert.assertEquals(descriptor.getURI(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.KeyTransport);
        
        descriptor = new KeyTransportRSAOAEP();
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
        SignatureAlgorithm descriptor;
        
        descriptor = new SignatureDSASHA1();
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA1);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.SIGNATURE_DSA_SHA1);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_DSA);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_SIGNATURE_DSA_SHA1);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Signature);
        
        descriptor = new SignatureDSASHA256();
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA256);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.SIGNATURE_DSA_SHA256);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_DSA);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_SIGNATURE_DSA_SHA256);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Signature);
        
        descriptor = new SignatureECDSASHA1();
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA1);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.SIGNATURE_ECDSA_SHA1);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_EC);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA1);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Signature);
        
        descriptor = new SignatureECDSASHA224();
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA224);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.SIGNATURE_ECDSA_SHA224);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_EC);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA224);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Signature);
        
        descriptor = new SignatureECDSASHA256();
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA256);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.SIGNATURE_ECDSA_SHA256);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_EC);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA256);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Signature);
        
        descriptor = new SignatureECDSASHA384();
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA384);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.SIGNATURE_ECDSA_SHA384);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_EC);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA384);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Signature);
        
        descriptor = new SignatureECDSASHA512();
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA512);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.SIGNATURE_ECDSA_SHA512);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_EC);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA512);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Signature);
        
        descriptor = new SignatureRSAMD5();
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_MD5);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.SIGNATURE_RSA_MD5);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_RSA);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Signature);
        
        descriptor = new SignatureRSARIPEMD160();
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_RIPEMD160);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.SIGNATURE_RSA_RIPEMD160);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_RSA);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_SIGNATURE_RSA_RIPEMD160);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Signature);
        
        descriptor = new SignatureRSASHA1();
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA1);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.SIGNATURE_RSA_SHA1);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_RSA);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Signature);
        
        descriptor = new SignatureRSASHA224();
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA224);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.SIGNATURE_RSA_SHA224);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_RSA);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA224);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Signature);
        
        descriptor = new SignatureRSASHA256();
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA256);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.SIGNATURE_RSA_SHA256);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_RSA);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Signature);
        
        descriptor = new SignatureRSASHA384();
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA384);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.SIGNATURE_RSA_SHA384);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_RSA);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA384);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Signature);
        
        descriptor = new SignatureRSASHA512();
        Assert.assertEquals(descriptor.getDigest(), JCAConstants.DIGEST_SHA512);
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.SIGNATURE_RSA_SHA512);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_RSA);
        Assert.assertEquals(descriptor.getURI(), SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512);
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.Signature);
    }
    
    // SymmetricKeyWrap
    @Test
    public void testSymmetricKeyWrap() {
        SymmetricKeyWrapAlgorithm descriptor;
        
        descriptor = new SymmetricKeyWrapAES128();
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.KEYWRAP_ALGO_AES);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_AES);
        Assert.assertEquals(descriptor.getURI(), EncryptionConstants.ALGO_ID_KEYWRAP_AES128);
        Assert.assertEquals(descriptor.getKeyLength(), new Integer(128));
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.SymmetricKeyWrap);
        
        descriptor = new SymmetricKeyWrapAES192();
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.KEYWRAP_ALGO_AES);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_AES);
        Assert.assertEquals(descriptor.getURI(), EncryptionConstants.ALGO_ID_KEYWRAP_AES192);
        Assert.assertEquals(descriptor.getKeyLength(), new Integer(192));
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.SymmetricKeyWrap);
        
        descriptor = new SymmetricKeyWrapAES256();
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.KEYWRAP_ALGO_AES);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_AES);
        Assert.assertEquals(descriptor.getURI(), EncryptionConstants.ALGO_ID_KEYWRAP_AES256);
        Assert.assertEquals(descriptor.getKeyLength(), new Integer(256));
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.SymmetricKeyWrap);
        
        descriptor = new SymmetricKeyWrapDESede();
        Assert.assertEquals(descriptor.getJCAAlgorithmID(), JCAConstants.KEYWRAP_ALGO_DESEDE);
        Assert.assertEquals(descriptor.getKey(), JCAConstants.KEY_ALGO_DESEDE);
        Assert.assertEquals(descriptor.getURI(), EncryptionConstants.ALGO_ID_KEYWRAP_TRIPLEDES);
        Assert.assertEquals(descriptor.getKeyLength(), new Integer(192));
        Assert.assertEquals(descriptor.getType(), AlgorithmDescriptor.AlgorithmType.SymmetricKeyWrap);
        
    }

}
