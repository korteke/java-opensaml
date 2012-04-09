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
import org.testng.Assert;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.opensaml.security.SecurityException;
import org.opensaml.security.SecurityHelper;

/**
 * Unit test for {@link SecurityHelper}.
 */
public class SecurityHelperTest {

    /** Location of non-encrypted, PEM formatted, RSA private key. */
    private String rsaPrivKeyPEMNoEncrypt = "/data/rsa-privkey-nopass.pem";

    /** Location of non-encrypted, DER formatted, RSA private key. */
    private String rsaPrivKeyDERNoEncrypt = "/data/rsa-privkey-nopass.der";
    
    /** Location of non-encrypted, PEM formatted, dSA private key. */
    private String dsaPrivKeyPEMNoEncrypt = "/data/dsa-privkey-nopass.pem";

    /** Location of non-encrypted, DER formatted, dSA private key. */
    private String dsaPrivKeyDERNoEncrypt = "/data/dsa-privkey-nopass.der";

    /** Password for private key. */
    private char[] privKeyPassword = { 'c', 'h', 'a', 'n', 'g', 'e', 'i', 't' };

    /** Location of encrypted, PEM formatted, RSA private key. */
    private String rsaPrivKeyPEMEncrypt = "/data/rsa-privkey-changeit-pass.pem";

    /** Location of encrypted, PEM formatted, DSA private key. */
    private String dsaPrivKeyPEMEncrypt = "/data/dsa-privkey-changeit-pass.pem";

    /** Test decoding an RSA private key, in PEM format, without encryption. */
    @Test
    public void testDecodeRSAPrivateKeyPEMNoEncrypt() throws Exception {
        testPrivKey(rsaPrivKeyPEMNoEncrypt, null, "RSA");
    }

    /** Test decoding an RSA private key, in PEM format, with encryption. */
    @Test
    public void testDecodeRSAPrivateKeyPEMEncrypt() throws Exception {
        testPrivKey(rsaPrivKeyPEMEncrypt, privKeyPassword, "RSA");
    }

    /** Test decoding an RSA private key, in DER format, without encryption. */
    @Test
    public void testDecodeRSAPrivateKeyDERNoEncrypt() throws Exception {
        testPrivKey(rsaPrivKeyDERNoEncrypt, null, "RSA");
    }
    
    /** Test decoding an DSA private key, in PEM format, without encryption. */
    @Test
    public void testDecodeDSAPrivateKeyPEMNoEncrypt() throws Exception {
        testPrivKey(dsaPrivKeyPEMNoEncrypt, null, "DSA");
    }

    /** Test decoding an DSA private key, in PEM format, with encryption. */
    @Test
    public void testDecodeDSAPrivateKeyPEMEncrypt() throws Exception {
        testPrivKey(dsaPrivKeyPEMEncrypt, privKeyPassword, "DSA");
    }

    /** Test decoding an DSA private key, in DER format, without encryption. */
    @Test
    public void testDecodeDSAPrivateKeyDERNoEncrypt() throws Exception {
        testPrivKey(dsaPrivKeyDERNoEncrypt, null, "DSA");
    }
    
    /** Test deriving a public key from an RSA and DSA private key. */
    @Test
    public void testDerivePublicKey() throws Exception{
        PrivateKey privKey = testPrivKey(rsaPrivKeyPEMNoEncrypt, null, "RSA");
        PublicKey pubKey = SecurityHelper.derivePublicKey(privKey);
        Assert.assertNotNull(pubKey);
        Assert.assertEquals(pubKey.getAlgorithm(), "RSA");
        
        pubKey = null;
        privKey = testPrivKey(dsaPrivKeyPEMNoEncrypt, null, "DSA");
        pubKey = SecurityHelper.derivePublicKey(privKey);
        Assert.assertNotNull(pubKey);
        Assert.assertEquals(pubKey.getAlgorithm(), "DSA");
    }

    
    /** Test the evaluation that 2 keys are members of the same key pair. 
     * 
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     * @throws SecurityException */
    @Test
    public void testKeyPairMatching() throws NoSuchAlgorithmException, NoSuchProviderException, SecurityException {
        KeyPair kp1rsa = SecurityHelper.generateKeyPair("RSA", 1024, null);
        KeyPair kp2rsa = SecurityHelper.generateKeyPair("RSA", 1024, null);
        KeyPair kp1dsa = SecurityHelper.generateKeyPair("DSA", 1024, null);
        KeyPair kp2dsa = SecurityHelper.generateKeyPair("DSA", 1024, null);
        
        Assert.assertTrue(SecurityHelper.matchKeyPair(kp1rsa.getPublic(), kp1rsa.getPrivate()));
        Assert.assertTrue(SecurityHelper.matchKeyPair(kp2rsa.getPublic(), kp2rsa.getPrivate()));
        Assert.assertFalse(SecurityHelper.matchKeyPair(kp1rsa.getPublic(), kp2rsa.getPrivate()));
        Assert.assertFalse(SecurityHelper.matchKeyPair(kp2rsa.getPublic(), kp1rsa.getPrivate()));
        
        Assert.assertTrue(SecurityHelper.matchKeyPair(kp1dsa.getPublic(), kp1dsa.getPrivate()));
        Assert.assertTrue(SecurityHelper.matchKeyPair(kp2dsa.getPublic(), kp2dsa.getPrivate()));
        Assert.assertFalse(SecurityHelper.matchKeyPair(kp1dsa.getPublic(), kp2dsa.getPrivate()));
        Assert.assertFalse(SecurityHelper.matchKeyPair(kp2dsa.getPublic(), kp1dsa.getPrivate()));
        
        try {
            // key algorithm type mismatch, should be an error
            Assert.assertFalse(SecurityHelper.matchKeyPair(kp1rsa.getPublic(), kp2dsa.getPrivate()));
            Assert.fail("Key algorithm mismatch should have caused evaluation failure");
        } catch (SecurityException e) {
           // expected 
        }
        
        try {
            // null key, should be an error
            Assert.assertFalse(SecurityHelper.matchKeyPair(kp1rsa.getPublic(), null));
            Assert.fail("Null key should have caused failure");
        } catch (SecurityException e) {
           // expected 
        }
        try {
            // null key, should be an error
            Assert.assertFalse(SecurityHelper.matchKeyPair(null, kp1rsa.getPrivate()));
            Assert.fail("Key algorithm mismatch should have caused evaluation failure");
        } catch (SecurityException e) {
            // expected
        }
    }

    /** Generic key testing. */
    protected PrivateKey testPrivKey(String keyFile, char[] password, String algo) throws Exception {
        InputStream keyInS = SecurityHelperTest.class.getResourceAsStream(keyFile);

        byte[] keyBytes = new byte[keyInS.available()];
        keyInS.read(keyBytes);

        PrivateKey key = SecurityHelper.decodePrivateKey(keyBytes, password);
        Assert.assertNotNull(key);
        Assert.assertEquals(key.getAlgorithm(), algo);
        
        return key;
    }
}