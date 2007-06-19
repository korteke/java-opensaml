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
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Set;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.log4j.Logger;
import org.apache.xml.security.algorithms.JCEMapper;
import org.opensaml.xml.security.credential.BasicCredential;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.credential.CredentialCriteria;
import org.opensaml.xml.security.credential.CredentialCriteriaSet;
import org.opensaml.xml.security.x509.PKIXCriteria;
import org.opensaml.xml.security.x509.PKIXCriteriaSet;
import org.opensaml.xml.util.DatatypeHelper;

/**
 * Helper methods for security-related requirements.
 */
public final class SecurityHelper {
    
    /** Class logger. */
    private static Logger log = Logger.getLogger(SecurityHelper.class);
    
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
     * Get the Java security JCA/JCE key algorithm specifier associated with an algorithm URI.
     * 
     * @param algorithmURI the algorithm URI to evaluate
     * @return the Java key algorithm specifier, or null if the mapping is unavailable
     *          or indeterminable from the URI
     */
    public static String getKeyAlgorithmFromURI(String algorithmURI) {
            return DatatypeHelper.safeTrimOrNullString(JCEMapper.getJCEKeyAlgorithmFromURI(algorithmURI)); 
    }
    
    /**
     * Get the length of the key indicated by the algorithm URI, if applicable and available.
     * 
     * @param algorithmURI the algorithm URI to evaluate
     * @return the length of the key indicated by the algorithm URI, or null if the length is either
     *          unavailable or indeterminable from the URI
     */
    public static Integer getKeyLengthFromURI(String algorithmURI) {
        String algoClass = 
            DatatypeHelper.safeTrimOrNullString(JCEMapper.getAlgorithmClassFromURI(algorithmURI));
        
        if (ApacheXMLSecurityConstants.ALGO_CLASS_BLOCK_ENCRYPTION.equals(algoClass) 
                || ApacheXMLSecurityConstants.ALGO_CLASS_SYMMETRIC_KEY_WRAP.equals(algoClass))  {
            
            try {
                int keyLength = JCEMapper.getKeyLengthFromURI(algorithmURI);
                return new Integer(keyLength);
            } catch (NumberFormatException e) {
                log.warn("XML Security config contained invalid key length value for algorithm URI: "
                        + algorithmURI);
            }                   
        } 
        if (log.isInfoEnabled()) {
            log.info("Mapping from algorithm URI '" + algorithmURI + "' to key length not available");
        }
        return null;
    }
    
    /**
     * Generates a random Java JCE symmetric Key object from the specified XML Encryption algorithm URI.
     * 
     * @param algoURI  The XML Encryption algorithm URI
     * @return a randomly-generated symmetric Key
     * @throws NoSuchAlgorithmException thrown if the specified algorithm is invalid
     * @throws KeyException thrown if the length of the key to generate could not be determined
     */
    public static SecretKey generateSymmetricKey(String algoURI) throws NoSuchAlgorithmException, KeyException {
        String jceAlgorithmName = getKeyAlgorithmFromURI(algoURI);
        if (DatatypeHelper.isEmpty(jceAlgorithmName)) {
            log.error("Mapping from algorithm URI '" + algoURI 
                    + "' to key algorithm not available, key generation failed");
            throw new NoSuchAlgorithmException("Algorithm URI'" + algoURI + "' is invalid for key generation");
        }
        Integer keyLength = getKeyLengthFromURI(algoURI);
        if (keyLength == null) {
            log.error("Key length could not be determined from algorithm URI, can't generate key");
            throw new KeyException("Key length not determinable from algorithm URI, could not generate new key");
        }
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
     * Get the key length in bits of the specified key.
     * 
     * @param key the key to evaluate
     * @return length of the key in bits, or null if the length can not be determined
     */
    public static Integer getKeyLength(Key key) {
        // TODO investigate techniques (and use cases) to determine length in other cases,
        // e.g. RSA and DSA keys, and non-RAW format symmetric keys
        if (key instanceof SecretKey && "RAW".equals(key.getFormat())) {
            return key.getEncoded().length;
        }
        log.debug("Unable to determine length in bits of specified Key instance");
        return null;
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
    
    /**
     * Get the CredentialCriteria from the general more general criteria set and return as a 
     * type-specific set.
     * 
     * @param generalCriteria set of criteria
     * @return a new set containing only CredentialCriteria
     */
    public static CredentialCriteriaSet getCredentialCriteria(Set<Criteria> generalCriteria) {
        CredentialCriteriaSet criteriaSet = new CredentialCriteriaSet();
        for (Criteria criteria : generalCriteria) {
            if (criteria instanceof CredentialCriteria) {
               criteriaSet.add((CredentialCriteria) criteria) ;
            }
        }
        return criteriaSet;
    } 
    
    /**
     * Get the PKIXCriteria from the general more general criteria set and return as a 
     * type-specific set.
     * 
     * @param generalCriteria set of criteria
     * @return a new set containing only PKIXCriteria
     */
    public static PKIXCriteriaSet getPKIXCriteria(Set<Criteria> generalCriteria) {
        PKIXCriteriaSet criteriaSet = new PKIXCriteriaSet();
        for (Criteria criteria : generalCriteria) {
            if (criteria instanceof PKIXCriteria) {
               criteriaSet.add((PKIXCriteria) criteria) ;
            }
        }
        return criteriaSet;
    }

}
