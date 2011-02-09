/*
 * Copyright [2007] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xml.security;

import java.io.InputStream;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;

import junit.framework.TestCase;

/**
 * Unit test for {@link SecurityHelper}.
 */
public class SecurityHelperTest extends TestCase {

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
    public void testDecodeRSAPrivateKeyPEMNoEncrypt() throws Exception {
        testPrivKey(rsaPrivKeyPEMNoEncrypt, null, "RSA");
    }

    /** Test decoding an RSA private key, in PEM format, with encryption. */
    public void testDecodeRSAPrivateKeyPEMEncrypt() throws Exception {
        testPrivKey(rsaPrivKeyPEMEncrypt, privKeyPassword, "RSA");
    }

    /** Test decoding an RSA private key, in DER format, without encryption. */
    public void testDecodeRSAPrivateKeyDERNoEncrypt() throws Exception {
        testPrivKey(rsaPrivKeyDERNoEncrypt, null, "RSA");
    }
    
    /** Test decoding an DSA private key, in PEM format, without encryption. */
    public void testDecodeDSAPrivateKeyPEMNoEncrypt() throws Exception {
        testPrivKey(dsaPrivKeyPEMNoEncrypt, null, "DSA");
    }

    /** Test decoding an DSA private key, in PEM format, with encryption. */
    public void testDecodeDSAPrivateKeyPEMEncrypt() throws Exception {
        testPrivKey(dsaPrivKeyPEMEncrypt, privKeyPassword, "DSA");
    }

    /** Test decoding an DSA private key, in DER format, without encryption. */
    public void testDecodeDSAPrivateKeyDERNoEncrypt() throws Exception {
        testPrivKey(dsaPrivKeyDERNoEncrypt, null, "DSA");
    }
    
    /** Test deriving a public key from an RSA and DSA private key. */
    public void testDerivePublicKey() throws Exception{
        PrivateKey privKey = testPrivKey(rsaPrivKeyPEMNoEncrypt, null, "RSA");
        PublicKey pubKey = SecurityHelper.derivePublicKey(privKey);
        assertNotNull(pubKey);
        assertEquals("RSA", pubKey.getAlgorithm());
        
        pubKey = null;
        privKey = testPrivKey(dsaPrivKeyPEMNoEncrypt, null, "DSA");
        pubKey = SecurityHelper.derivePublicKey(privKey);
        assertNotNull(pubKey);
        assertEquals("DSA", pubKey.getAlgorithm());
    }

    
    /** Test the evaluation that 2 keys are members of the same key pair. 
     * 
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     * @throws SecurityException */
    public void testKeyPairMatching() throws NoSuchAlgorithmException, NoSuchProviderException, SecurityException {
        //TODO this will fail, need to refactor helper method to remove use of algorithm URI's
        //org.opensaml.xml.Configuration.setGlobalSecurityConfiguration(
        //       DefaultSecurityConfigurationBootstrap.buildDefaultConfig());
        KeyPair kp1rsa = SecurityHelper.generateKeyPair("RSA", 1024, null);
        KeyPair kp2rsa = SecurityHelper.generateKeyPair("RSA", 1024, null);
        KeyPair kp1dsa = SecurityHelper.generateKeyPair("DSA", 1024, null);
        KeyPair kp2dsa = SecurityHelper.generateKeyPair("DSA", 1024, null);
        
        assertTrue(SecurityHelper.matchKeyPair(kp1rsa.getPublic(), kp1rsa.getPrivate()));
        assertTrue(SecurityHelper.matchKeyPair(kp2rsa.getPublic(), kp2rsa.getPrivate()));
        assertFalse(SecurityHelper.matchKeyPair(kp1rsa.getPublic(), kp2rsa.getPrivate()));
        assertFalse(SecurityHelper.matchKeyPair(kp2rsa.getPublic(), kp1rsa.getPrivate()));
        
        assertTrue(SecurityHelper.matchKeyPair(kp1dsa.getPublic(), kp1dsa.getPrivate()));
        assertTrue(SecurityHelper.matchKeyPair(kp2dsa.getPublic(), kp2dsa.getPrivate()));
        assertFalse(SecurityHelper.matchKeyPair(kp1dsa.getPublic(), kp2dsa.getPrivate()));
        assertFalse(SecurityHelper.matchKeyPair(kp2dsa.getPublic(), kp1dsa.getPrivate()));
        
        try {
            // key algorithm type mismatch, should be an error
            assertFalse(SecurityHelper.matchKeyPair(kp1rsa.getPublic(), kp2dsa.getPrivate()));
            fail("Key algorithm mismatch should have caused evaluation failure");
        } catch (SecurityException e) {
           // expected 
        }
        
        try {
            // null key, should be an error
            assertFalse(SecurityHelper.matchKeyPair(kp1rsa.getPublic(), null));
            fail("Null key should have caused failure");
        } catch (SecurityException e) {
           // expected 
        }
        try {
            // null key, should be an error
            assertFalse(SecurityHelper.matchKeyPair(null, kp1rsa.getPrivate()));
            fail("Key algorithm mismatch should have caused evaluation failure");
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
        assertNotNull(key);
        assertEquals(algo, key.getAlgorithm());
        
        return key;
    }
}