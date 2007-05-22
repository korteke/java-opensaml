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

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.xml.security.algorithms.JCEMapper;
import org.opensaml.xml.security.credential.BasicCredential;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.util.DatatypeHelper;

/**
 * Helper methods for security-related requirements.
 */
public final class SecurityHelper {
    
    /** Constructor. */
    private SecurityHelper() {}
    
    /**
     * Check whether the signature method algorithm URI indicates HMAC.
     * 
     * @param signatureAlgorithm the signature method algorithm URI
     * @return true if URI indicates HMAC, false otherwise
     */
    public static boolean isHMAC(String signatureAlgorithm) {
        String algoClass = 
            DatatypeHelper.safeTrimOrNullString(JCEMapper.getAlgorithmClassFromURI(signatureAlgorithm));
        return ApacheXMLSecurityConstants.ALGO_CLASS_MAC.equals(algoClass);
    }
    
    /**
     * Generates a random Java JCE symmetric Key object from the specified XML Encryption algorithm URI.
     * 
     * @param algoURI  The XML Encryption algorithm URI
     * @return a randomly-generated symmetric Key
     * @throws NoSuchAlgorithmException thrown if the specified algorithm is invalid
     */
    public static SecretKey generateSymmetricKey(String algoURI) throws NoSuchAlgorithmException {
        String jceAlgorithmName = 
            DatatypeHelper.safeTrimOrNullString(JCEMapper.getJCEKeyAlgorithmFromURI(algoURI));
        if (DatatypeHelper.isEmpty(jceAlgorithmName)) {
            throw new NoSuchAlgorithmException("Algorithm URI'" + algoURI + "' is invalid");
        }
        int keyLength = JCEMapper.getKeyLengthFromURI(algoURI);
        KeyGenerator keyGenerator = KeyGenerator.getInstance(jceAlgorithmName);
        keyGenerator.init(keyLength);
        return keyGenerator.generateKey();
    }
    
    /**
     * Extract the encryption key from the credential.
     * 
     * @param credential the credential containing the encryption key
     * @return the encryption key (either a public key or a secret (symmetric) key
     */
    public static Key extractEncryptionKey(Credential credential) {
        if (credential == null) {
            return null;
        }
        if (credential.getPublicKey() != null) {
            return credential.getPublicKey();
        } else {
            return credential.getSecretKey();
        }
    }
    
    /**
     * Extract the decryption key from the credential.
     * 
     * @param credential the credential containing the decryption key
     * @return the decryption key (either a private key or a secret (symmetric) key
     */
    public static Key extractDecryptionKey(Credential credential) {
        if (credential == null) {
            return null;
        }
        if (credential.getPrivateKey() != null) {
            return credential.getPrivateKey();
        } else {
            return credential.getSecretKey();
        }
    }
    
    /**
     * Extract the signing key from the credential.
     * 
     * @param credential the credential containing the signing key
     * @return the signing key (either a private key or a secret (symmetric) key
     */
    public static Key extractSigningKey(Credential credential) {
        if (credential == null) {
            return null;
        }
        if (credential.getPrivateKey() != null) {
            return credential.getPrivateKey();
        } else {
            return credential.getSecretKey();
        }
    }
    
    /**
     * Extract the verification key from the credential.
     * 
     * @param credential the credential containing the verification key
     * @return the verification key (either a public key or a secret (symmetric) key
     */
    public static Key extractVerificationKey(Credential credential) {
        if (credential == null) {
            return null;
        }
        if (credential.getPublicKey() != null) {
            return credential.getPublicKey();
        } else {
            return credential.getSecretKey();
        }
    }
    
    /**
     * Get a simple, minimal credential containing a secret (symmetric) key.
     * 
     * @param secretKey the symmetric key to wrap
     * @return a credential containing the secret key specified
     */
    public static BasicCredential getSimpleCredential(SecretKey secretKey) {
        if (secretKey == null) {
            throw new IllegalArgumentException("A secret key is required");
        }
        BasicCredential cred = new BasicCredential();
        cred.setSecretKey(secretKey);
        return cred;
    }
    
    /**
     * Get a simple, minimal credential containing a public key, and optionally a private key.
     * 
     * @param publicKey the public key to wrap
     * @param privateKey the private key to wrap, which may be null
     * @return a credential containing the key(s) specified
     */
    public static BasicCredential getSimpleCredential(PublicKey publicKey, PrivateKey privateKey) {
        if (publicKey == null) {
            throw new IllegalArgumentException("A public key is required");
        }
        BasicCredential cred = new BasicCredential();
        cred.setPublicKey(publicKey);
        cred.setPrivateKey(privateKey);
        return cred;
    }
}
