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

package org.opensaml.security;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.SecretKey;

import org.opensaml.security.SecurityException;
import org.opensaml.security.SecurityHelper;
import org.opensaml.security.SigningUtil;
import org.opensaml.security.credential.Credential;

/**
 * Test the SigningUtil operations for generating and verifying simple, raw signatures and MAC's.
 */
public class SigningUtilTest {
    
    private SecretKey secretKeyAES128;
    private KeyPair keyPairRSA;
    private Credential credAES;
    private Credential credRSA;
    
    private String data;
    private byte[] controlSignatureRSA;
    private byte[] controlSignatureHMAC;
    
    private String rsaJCAAlgorithm;
    private String hmacJCAAlgorithm;
    
    public SigningUtilTest() throws NoSuchAlgorithmException, NoSuchProviderException {
        data = "Hello, here is some secret data that is to be signed";
        
        rsaJCAAlgorithm = "SHA1withRSA";
        
        hmacJCAAlgorithm = "HmacSHA1";
    }
    

    @BeforeMethod
    protected void setUp() throws Exception {
        secretKeyAES128 = SecurityHelper.generateKey("AES", 128, null);
        credAES = SecurityHelper.getSimpleCredential(secretKeyAES128);
        keyPairRSA = SecurityHelper.generateKeyPair("RSA", 1024, null);
        credRSA = SecurityHelper.getSimpleCredential(keyPairRSA.getPublic(), keyPairRSA.getPrivate());
        
        controlSignatureRSA = getControlSignature(data.getBytes(), keyPairRSA.getPrivate(), rsaJCAAlgorithm);
        Assert.assertNotNull(controlSignatureRSA);
        Assert.assertTrue(controlSignatureRSA.length > 0);
        
        controlSignatureHMAC = getControlSignature(data.getBytes(), secretKeyAES128, hmacJCAAlgorithm);
        Assert.assertNotNull(controlSignatureHMAC);
        Assert.assertTrue(controlSignatureHMAC.length > 0);
    }

    @Test
    public void testSigningWithPrivateKey() throws SecurityException {
        byte[] signature = SigningUtil.sign(credRSA, rsaJCAAlgorithm, false, data.getBytes());
        Assert.assertNotNull(signature);
        Assert.assertTrue(Arrays.equals(controlSignatureRSA, signature), "Signature was not the expected value");
    }
    
    @Test
    public void testSigningWithHMAC() throws SecurityException {
        byte[] signature = SigningUtil.sign(credAES, hmacJCAAlgorithm, true, data.getBytes());
        Assert.assertNotNull(signature);
        Assert.assertTrue(Arrays.equals(controlSignatureHMAC, signature), "Signature was not the expected value");
    }
    
    @Test
    public void testVerificationWithPublicKey() throws SecurityException, NoSuchAlgorithmException, NoSuchProviderException {
        Assert.assertTrue(SigningUtil.verify(credRSA, rsaJCAAlgorithm, false, controlSignatureRSA, data.getBytes()),
                "Signature failed to verify, should have succeeded");
        
        KeyPair badKP = SecurityHelper.generateKeyPair("RSA", 1024, null);
        Credential badCred = SecurityHelper.getSimpleCredential(badKP.getPublic(), badKP.getPrivate());
        
        Assert.assertFalse(SigningUtil.verify(badCred, rsaJCAAlgorithm, false, controlSignatureRSA, data.getBytes()),
                "Signature verified successfully, should have failed due to wrong verification key");
        
        String tamperedData = data + "HAHA All your base are belong to us";
        
        Assert.assertFalse(SigningUtil.verify(credRSA, rsaJCAAlgorithm, false, controlSignatureRSA, tamperedData.getBytes()),
                "Signature verified successfully, should have failed due to tampered data");
    }

    @Test
    public void testVerificationWithHMAC() throws SecurityException, NoSuchAlgorithmException, NoSuchProviderException {
        Assert.assertTrue(SigningUtil.verify(credAES, hmacJCAAlgorithm, true, controlSignatureHMAC, data.getBytes()),
                "Signature failed to verify, should have succeeded");
        
        SecretKey badKey = SecurityHelper.generateKey("AES", 128, null);
        Credential badCred = SecurityHelper.getSimpleCredential(badKey);
        
        Assert.assertFalse(SigningUtil.verify(badCred, hmacJCAAlgorithm, true, controlSignatureHMAC, data.getBytes()),
                "Signature verified successfully, should have failed due to wrong verification key");
        
        String tamperedData = data + "HAHA All your base are belong to us";
        
        Assert.assertFalse(SigningUtil.verify(credAES, hmacJCAAlgorithm, true, controlSignatureHMAC, tamperedData.getBytes()),
                "Signature verified successfully, should have failed due to tampered data");
        
    }
    
    private byte[] getControlSignature(byte[] data, SecretKey secretKey, String algorithm) 
            throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(algorithm);
        mac.init(secretKeyAES128);
        return mac.doFinal(data);
    }

    private byte[] getControlSignature(byte[] data, PrivateKey privateKey, String algorithm) 
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sig = Signature.getInstance(algorithm);
        sig.initSign(privateKey);
        sig.update(data);
        return sig.sign();
    }
}
