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

import java.security.Key;
import java.security.KeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashSet;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.algorithm.descriptors.BlockEncryptionAES128CBC;
import org.opensaml.xmlsec.algorithm.descriptors.BlockEncryptionAES128GCM;
import org.opensaml.xmlsec.algorithm.descriptors.BlockEncryptionAES192CBC;
import org.opensaml.xmlsec.algorithm.descriptors.BlockEncryptionAES192GCM;
import org.opensaml.xmlsec.algorithm.descriptors.BlockEncryptionAES256CBC;
import org.opensaml.xmlsec.algorithm.descriptors.BlockEncryptionAES256GCM;
import org.opensaml.xmlsec.algorithm.descriptors.BlockEncryptionDESede;
import org.opensaml.xmlsec.algorithm.descriptors.DigestSHA256;
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
 * Unit test for {@link KeySupport}.
 */
public class AlgorithmSupportTest extends OpenSAMLInitBaseTestCase {
    
    @Test
    public void testIsKeyTransportAlgorithm() {
        Assert.assertTrue(AlgorithmSupport.isKeyEncryptionAlgorithm(new KeyTransportRSA15()));
        Assert.assertTrue(AlgorithmSupport.isKeyEncryptionAlgorithm(new KeyTransportRSAOAEP()));
        Assert.assertTrue(AlgorithmSupport.isKeyEncryptionAlgorithm(new KeyTransportRSAOAEPMGF1P()));
        Assert.assertTrue(AlgorithmSupport.isKeyEncryptionAlgorithm(new SymmetricKeyWrapAES128()));
        Assert.assertTrue(AlgorithmSupport.isKeyEncryptionAlgorithm(new SymmetricKeyWrapAES192()));
        Assert.assertTrue(AlgorithmSupport.isKeyEncryptionAlgorithm(new SymmetricKeyWrapAES256()));
        Assert.assertTrue(AlgorithmSupport.isKeyEncryptionAlgorithm(new SymmetricKeyWrapDESede()));
        
        //Test some failure cases
        Assert.assertFalse(AlgorithmSupport.isKeyEncryptionAlgorithm(new BlockEncryptionAES128CBC()));
        Assert.assertFalse(AlgorithmSupport.isKeyEncryptionAlgorithm(new SignatureRSASHA256()));
        Assert.assertFalse(AlgorithmSupport.isKeyEncryptionAlgorithm(new DigestSHA256()));
    }

    @Test
    public void testIsDataEncryptionAlgorithm() {
        Assert.assertTrue(AlgorithmSupport.isDataEncryptionAlgorithm(new BlockEncryptionAES128CBC()));
        Assert.assertTrue(AlgorithmSupport.isDataEncryptionAlgorithm(new BlockEncryptionAES128GCM()));
        Assert.assertTrue(AlgorithmSupport.isDataEncryptionAlgorithm(new BlockEncryptionAES192CBC()));
        Assert.assertTrue(AlgorithmSupport.isDataEncryptionAlgorithm(new BlockEncryptionAES192GCM()));
        Assert.assertTrue(AlgorithmSupport.isDataEncryptionAlgorithm(new BlockEncryptionAES256CBC()));
        Assert.assertTrue(AlgorithmSupport.isDataEncryptionAlgorithm(new BlockEncryptionAES256GCM()));
        Assert.assertTrue(AlgorithmSupport.isDataEncryptionAlgorithm(new BlockEncryptionDESede()));
        
        //Test some failure cases
        Assert.assertFalse(AlgorithmSupport.isDataEncryptionAlgorithm(new KeyTransportRSA15()));
        Assert.assertFalse(AlgorithmSupport.isDataEncryptionAlgorithm(new SymmetricKeyWrapAES128()));
        Assert.assertFalse(AlgorithmSupport.isDataEncryptionAlgorithm(new SignatureRSASHA256()));
        Assert.assertFalse(AlgorithmSupport.isDataEncryptionAlgorithm(new DigestSHA256()));
    }
    
    @Test
    public void testCredentialSupportsAlgorithmForSigning() throws NoSuchAlgorithmException, KeyException, NoSuchProviderException {
        Credential credential;
        KeyPair kp;
        
        credential = CredentialSupport.getSimpleCredential(KeySupport.generateKey(JCAConstants.KEY_ALGO_DESEDE, 168, null));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new HMACSHA1()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new HMACSHA224()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new HMACSHA256()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new HMACSHA384()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new HMACSHA512()));
        
        credential = CredentialSupport.getSimpleCredential(KeySupport.generateKey(JCAConstants.KEY_ALGO_AES, 128, null));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new HMACSHA1()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new HMACSHA224()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new HMACSHA256()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new HMACSHA384()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new HMACSHA512()));
        
        credential = CredentialSupport.getSimpleCredential(KeySupport.generateKey(JCAConstants.KEY_ALGO_AES, 192, null));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new HMACSHA1()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new HMACSHA224()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new HMACSHA256()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new HMACSHA384()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new HMACSHA512()));
        
        credential = CredentialSupport.getSimpleCredential(KeySupport.generateKey(JCAConstants.KEY_ALGO_AES, 256, null));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new HMACSHA1()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new HMACSHA224()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new HMACSHA256()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new HMACSHA384()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new HMACSHA512()));
        
        kp = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_RSA, 2048, null);
        credential = CredentialSupport.getSimpleCredential(kp.getPublic(), kp.getPrivate());
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new SignatureRSASHA1()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new SignatureRSASHA224()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new SignatureRSASHA256()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new SignatureRSASHA384()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new SignatureRSASHA512()));
        
        kp = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_RSA, 4096, null);
        credential = CredentialSupport.getSimpleCredential(kp.getPublic(), kp.getPrivate());
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new SignatureRSASHA1()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new SignatureRSASHA224()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new SignatureRSASHA256()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new SignatureRSASHA384()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new SignatureRSASHA512()));
        
        kp = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_DSA, 1024, null);
        credential = CredentialSupport.getSimpleCredential(kp.getPublic(), kp.getPrivate());
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new SignatureDSASHA1()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new SignatureDSASHA256()));
        
        try {
            kp = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_EC, 256, null);
            credential = CredentialSupport.getSimpleCredential(kp.getPublic(), kp.getPrivate());
            Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new SignatureECDSASHA1()));
            Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new SignatureECDSASHA224()));
            Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new SignatureECDSASHA256()));
            Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new SignatureECDSASHA384()));
            Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new SignatureECDSASHA512()));
        } catch (NoSuchAlgorithmException e) {
            // EC support isn't universal, e.g. OpenJDK 7 doesn't ship with an EC provider out-of-the-box.
            // Just ignore unsupported algorithm failures here for now. 
        }
        
        // Test some failure cases
        kp = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_DSA, 1024, null);
        credential = CredentialSupport.getSimpleCredential(kp.getPublic(), kp.getPrivate());
        Assert.assertFalse(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new SignatureECDSASHA1()));
        Assert.assertFalse(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new SignatureRSASHA256()));
        Assert.assertFalse(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new HMACSHA512()));
        
        credential = CredentialSupport.getSimpleCredential(KeySupport.generateKey(JCAConstants.KEY_ALGO_AES, 256, null));
        Assert.assertFalse(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new SignatureECDSASHA1()));
        Assert.assertFalse(AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, new SignatureRSASHA256()));
    }
    
    @Test
    public void testCredentialSupportsAlgorithmForEncryption() throws NoSuchAlgorithmException, NoSuchProviderException {
        Credential credential;
        KeyPair kp;
        
        credential = CredentialSupport.getSimpleCredential(KeySupport.generateKey(JCAConstants.KEY_ALGO_DESEDE, 168, null));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForEncryption(credential, new BlockEncryptionDESede()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForEncryption(credential, new SymmetricKeyWrapDESede()));
        
        credential = CredentialSupport.getSimpleCredential(KeySupport.generateKey(JCAConstants.KEY_ALGO_AES, 128, null));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForEncryption(credential, new BlockEncryptionAES128CBC()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForEncryption(credential, new BlockEncryptionAES128GCM()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForEncryption(credential, new SymmetricKeyWrapAES128()));
        
        credential = CredentialSupport.getSimpleCredential(KeySupport.generateKey(JCAConstants.KEY_ALGO_AES, 192, null));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForEncryption(credential, new BlockEncryptionAES192CBC()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForEncryption(credential, new BlockEncryptionAES192GCM()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForEncryption(credential, new SymmetricKeyWrapAES192()));
        
        credential = CredentialSupport.getSimpleCredential(KeySupport.generateKey(JCAConstants.KEY_ALGO_AES, 256, null));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForEncryption(credential, new BlockEncryptionAES256CBC()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForEncryption(credential, new BlockEncryptionAES256GCM()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForEncryption(credential, new SymmetricKeyWrapAES256()));
        
        kp = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_RSA, 2048, null);
        credential = CredentialSupport.getSimpleCredential(kp.getPublic(), kp.getPrivate());
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForEncryption(credential, new KeyTransportRSA15()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForEncryption(credential, new KeyTransportRSAOAEP()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForEncryption(credential, new KeyTransportRSAOAEPMGF1P()));
        
        kp = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_RSA, 4096, null);
        credential = CredentialSupport.getSimpleCredential(kp.getPublic(), kp.getPrivate());
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForEncryption(credential, new KeyTransportRSA15()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForEncryption(credential, new KeyTransportRSAOAEP()));
        Assert.assertTrue(AlgorithmSupport.credentialSupportsAlgorithmForEncryption(credential, new KeyTransportRSAOAEPMGF1P()));
        
        // Test some failure cases
        kp = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_DSA, 1024, null);
        credential = CredentialSupport.getSimpleCredential(kp.getPublic(), kp.getPrivate());
        Assert.assertFalse(AlgorithmSupport.credentialSupportsAlgorithmForEncryption(credential, new KeyTransportRSA15()));
        Assert.assertFalse(AlgorithmSupport.credentialSupportsAlgorithmForEncryption(credential, new SymmetricKeyWrapAES128()));
        Assert.assertFalse(AlgorithmSupport.credentialSupportsAlgorithmForEncryption(credential, new BlockEncryptionAES128CBC()));
        
        credential = CredentialSupport.getSimpleCredential(KeySupport.generateKey(JCAConstants.KEY_ALGO_AES, 256, null));
        Assert.assertFalse(AlgorithmSupport.credentialSupportsAlgorithmForEncryption(credential, new KeyTransportRSA15()));
        Assert.assertFalse(AlgorithmSupport.credentialSupportsAlgorithmForEncryption(credential, new SymmetricKeyWrapAES128()));
        Assert.assertFalse(AlgorithmSupport.credentialSupportsAlgorithmForEncryption(credential, new BlockEncryptionAES128CBC()));
    }
    
    @Test
    public void testCheckKeyAlgorithmAndLength() throws NoSuchAlgorithmException, NoSuchProviderException {
        Key key;
        
        key = KeySupport.generateKey(JCAConstants.KEY_ALGO_DESEDE, 168, null);
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new BlockEncryptionDESede()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new SymmetricKeyWrapDESede()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new HMACSHA1()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new HMACSHA224()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new HMACSHA256()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new HMACSHA384()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new HMACSHA512()));
        
        key = KeySupport.generateKey(JCAConstants.KEY_ALGO_AES, 128, null);
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new BlockEncryptionAES128CBC()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new BlockEncryptionAES128GCM()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new SymmetricKeyWrapAES128()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new HMACSHA1()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new HMACSHA224()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new HMACSHA256()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new HMACSHA384()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new HMACSHA512()));
        
        key = KeySupport.generateKey(JCAConstants.KEY_ALGO_AES, 192, null);
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new BlockEncryptionAES192CBC()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new BlockEncryptionAES192GCM()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new SymmetricKeyWrapAES192()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new HMACSHA1()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new HMACSHA224()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new HMACSHA256()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new HMACSHA384()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new HMACSHA512()));
        
        key = KeySupport.generateKey(JCAConstants.KEY_ALGO_AES, 256, null);
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new BlockEncryptionAES256CBC()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new BlockEncryptionAES256GCM()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new SymmetricKeyWrapAES256()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new HMACSHA1()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new HMACSHA224()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new HMACSHA256()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new HMACSHA384()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new HMACSHA512()));
        
        key = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_RSA, 2048, null).getPrivate();
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new KeyTransportRSA15()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new KeyTransportRSAOAEP()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new KeyTransportRSAOAEPMGF1P()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new SignatureRSASHA1()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new SignatureRSASHA224()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new SignatureRSASHA256()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new SignatureRSASHA384()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new SignatureRSASHA512()));
        
        key = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_DSA, 1024, null).getPrivate();
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new SignatureDSASHA1()));
        Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new SignatureDSASHA256()));
        
        try {
            key = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_EC, 256, null).getPrivate();
            Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new SignatureECDSASHA1()));
            Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new SignatureECDSASHA224()));
            Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new SignatureECDSASHA256()));
            Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new SignatureECDSASHA384()));
            Assert.assertTrue(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new SignatureECDSASHA512()));
        } catch (NoSuchAlgorithmException e) {
            // EC support isn't universal, e.g. OpenJDK 7 doesn't ship with an EC provider out-of-the-box.
            // Just ignore unsupported algorithm failures here for now.
        }
        
        // Test some failure cases
        key = KeySupport.generateKey(JCAConstants.KEY_ALGO_AES, 128, null);
        Assert.assertFalse(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new BlockEncryptionDESede()));
        Assert.assertFalse(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new BlockEncryptionAES192CBC()));
        Assert.assertFalse(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new SymmetricKeyWrapAES256()));
        Assert.assertFalse(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new SignatureRSASHA1()));
        Assert.assertFalse(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new KeyTransportRSA15()));
        
        key = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_RSA, 2048, null).getPrivate();
        Assert.assertFalse(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new BlockEncryptionAES192CBC()));
        Assert.assertFalse(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new SymmetricKeyWrapAES256()));
        Assert.assertFalse(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new SignatureDSASHA1()));
        Assert.assertFalse(AlgorithmSupport.checkKeyAlgorithmAndLength(key, new SignatureECDSASHA256()));
    }
    
    /** Test mapping algorithm URI's to JCA key algorithm specifiers. */
    @Test
    public void testGetKeyAlgorithm() {
        // Encryption related.
        Assert.assertEquals(AlgorithmSupport.getKeyAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15), "RSA");
        Assert.assertEquals(AlgorithmSupport.getKeyAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP), "RSA");
        
        Assert.assertEquals(AlgorithmSupport.getKeyAlgorithm(EncryptionConstants.ALGO_ID_KEYWRAP_AES128), "AES");
        Assert.assertEquals(AlgorithmSupport.getKeyAlgorithm(EncryptionConstants.ALGO_ID_KEYWRAP_AES192), "AES");
        Assert.assertEquals(AlgorithmSupport.getKeyAlgorithm(EncryptionConstants.ALGO_ID_KEYWRAP_AES256), "AES");
        Assert.assertEquals(AlgorithmSupport.getKeyAlgorithm(EncryptionConstants.ALGO_ID_KEYWRAP_TRIPLEDES), "DESede");
        
        Assert.assertEquals(AlgorithmSupport.getKeyAlgorithm(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM), "AES");
        Assert.assertEquals(AlgorithmSupport.getKeyAlgorithm(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192_GCM), "AES");
        Assert.assertEquals(AlgorithmSupport.getKeyAlgorithm(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM), "AES");
        
        //Signature related.
        Assert.assertEquals(AlgorithmSupport.getKeyAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA), "RSA");
        Assert.assertEquals(AlgorithmSupport.getKeyAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1), "RSA");
        Assert.assertEquals(AlgorithmSupport.getKeyAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA224), "RSA");
        Assert.assertEquals(AlgorithmSupport.getKeyAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256), "RSA");
        Assert.assertEquals(AlgorithmSupport.getKeyAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA384), "RSA");
        Assert.assertEquals(AlgorithmSupport.getKeyAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512), "RSA");
        Assert.assertEquals(AlgorithmSupport.getKeyAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5), "RSA");
        Assert.assertEquals(AlgorithmSupport.getKeyAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_RIPEMD160), "RSA");
        Assert.assertEquals(AlgorithmSupport.getKeyAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_DSA_SHA1), "DSA");
        Assert.assertEquals(AlgorithmSupport.getKeyAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_DSA_SHA256), "DSA");
        Assert.assertEquals(AlgorithmSupport.getKeyAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA1), "EC");
        Assert.assertEquals(AlgorithmSupport.getKeyAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA224), "EC");
        Assert.assertEquals(AlgorithmSupport.getKeyAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA256), "EC");
        Assert.assertEquals(AlgorithmSupport.getKeyAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA384), "EC");
        Assert.assertEquals(AlgorithmSupport.getKeyAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA512), "EC");
        
        // Mac related.  No specific key algorithm is indicated, any symmetric key will do. Should always return null;
        Assert.assertNull(AlgorithmSupport.getKeyAlgorithm(SignatureConstants.ALGO_ID_MAC_HMAC_SHA1));
        Assert.assertNull(AlgorithmSupport.getKeyAlgorithm(SignatureConstants.ALGO_ID_MAC_HMAC_SHA224));
        Assert.assertNull(AlgorithmSupport.getKeyAlgorithm(SignatureConstants.ALGO_ID_MAC_HMAC_SHA256));
        Assert.assertNull(AlgorithmSupport.getKeyAlgorithm(SignatureConstants.ALGO_ID_MAC_HMAC_SHA384));
        Assert.assertNull(AlgorithmSupport.getKeyAlgorithm(SignatureConstants.ALGO_ID_MAC_HMAC_SHA512));
        Assert.assertNull(AlgorithmSupport.getKeyAlgorithm(SignatureConstants.ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5));
        Assert.assertNull(AlgorithmSupport.getKeyAlgorithm(SignatureConstants.ALGO_ID_MAC_HMAC_RIPEMD160));
    }    
    
    /** Test algorithm URI whitelist and blacklist validation. */
    @Test
    public void testValidateAlgorithmURI() {
        String targetURI = "urn:test:target";
        HashSet<String> whiteList = new HashSet<>();
        HashSet<String> blackList = new HashSet<>();
        
        Assert.assertTrue(AlgorithmSupport.validateAlgorithmURI(targetURI, null, null));
        Assert.assertTrue(AlgorithmSupport.validateAlgorithmURI(targetURI, whiteList, null));
        Assert.assertTrue(AlgorithmSupport.validateAlgorithmURI(targetURI, null, blackList));
        Assert.assertTrue(AlgorithmSupport.validateAlgorithmURI(targetURI, whiteList, blackList));
        
        whiteList.add("urn:test:target");
        Assert.assertTrue(AlgorithmSupport.validateAlgorithmURI(targetURI, whiteList, blackList));
        whiteList.clear();
        
        blackList.add("urn:test:NOTtarget");
        Assert.assertTrue(AlgorithmSupport.validateAlgorithmURI(targetURI, whiteList, blackList));
        blackList.clear();
        
        whiteList.add("urn:test:NOTtarget");
        Assert.assertFalse(AlgorithmSupport.validateAlgorithmURI(targetURI, whiteList, blackList));
        whiteList.clear();
    }
    
    @Test
    public void testGenerateSymmetricKey() throws NoSuchAlgorithmException, KeyException {
        Assert.assertNotNull(AlgorithmSupport.generateSymmetricKey(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128));
        Assert.assertNotNull(AlgorithmSupport.generateSymmetricKey(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM));
        Assert.assertNotNull(AlgorithmSupport.generateSymmetricKey(EncryptionConstants.ALGO_ID_KEYWRAP_AES128));
        
        Assert.assertNotNull(AlgorithmSupport.generateSymmetricKey(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192));
        Assert.assertNotNull(AlgorithmSupport.generateSymmetricKey(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192_GCM));
        Assert.assertNotNull(AlgorithmSupport.generateSymmetricKey(EncryptionConstants.ALGO_ID_KEYWRAP_AES192));
        
        Assert.assertNotNull(AlgorithmSupport.generateSymmetricKey(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256));
        Assert.assertNotNull(AlgorithmSupport.generateSymmetricKey(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM));
        Assert.assertNotNull(AlgorithmSupport.generateSymmetricKey(EncryptionConstants.ALGO_ID_KEYWRAP_AES256));
        
        Assert.assertNotNull(AlgorithmSupport.generateSymmetricKey(EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES));
        Assert.assertNotNull(AlgorithmSupport.generateSymmetricKey(EncryptionConstants.ALGO_ID_KEYWRAP_TRIPLEDES));
    }

}