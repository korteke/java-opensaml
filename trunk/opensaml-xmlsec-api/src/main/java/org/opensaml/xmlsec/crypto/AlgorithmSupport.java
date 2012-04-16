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

package org.opensaml.xmlsec.crypto;

import java.security.KeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import net.shibboleth.utilities.java.support.collection.LazySet;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.apache.xml.security.Init;
import org.apache.xml.security.algorithms.JCEMapper;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.ApacheXMLSecurityConstants;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * Helper methods for working with XML security algorithm URI's.
 */
public final class AlgorithmSupport {
    
    /** Additional algorithm URI's which imply RSA keys. */
    private static Set<String> rsaAlgorithmURIs;

    /** Additional algorithm URI's which imply DSA keys. */
    private static Set<String> dsaAlgorithmURIs;

    /** Additional algorithm URI's which imply ECDSA keys. */
    private static Set<String> ecdsaAlgorithmURIs;
    
    /** Constructor. */
    private AlgorithmSupport() { }

    /**
     * Get the Java security JCA/JCE algorithm identifier associated with an algorithm URI.
     * 
     * @param algorithmURI the algorithm URI to evaluate
     * @return the Java algorithm identifier, or null if the mapping is unavailable or indeterminable from the URI
     */
    public static String getAlgorithmID(String algorithmURI) {
        return StringSupport.trimOrNull(JCEMapper.translateURItoJCEID(algorithmURI));
    }

    /**
     * Check whether the signature method algorithm URI indicates HMAC.
     * 
     * @param signatureAlgorithm the signature method algorithm URI
     * @return true if URI indicates HMAC, false otherwise
     */
    public static boolean isHMAC(String signatureAlgorithm) {
        String algoClass = StringSupport.trimOrNull(JCEMapper.getAlgorithmClassFromURI(signatureAlgorithm));
        return ApacheXMLSecurityConstants.ALGO_CLASS_MAC.equals(algoClass);
    }
    
    /**
     * Get the Java security JCA/JCE key algorithm specifier associated with an algorithm URI.
     * 
     * @param algorithmURI the algorithm URI to evaluate
     * @return the Java key algorithm specifier, or null if the mapping is unavailable or indeterminable from the URI
     */
    public static String getKeyAlgorithm(String algorithmURI) {
        // The default Apache config file currently only includes the key algorithm for
        // the block ciphers and key wrap URI's. Note: could use a custom config file which contains others.
        String apacheValue = StringSupport.trimOrNull(JCEMapper.getJCEKeyAlgorithmFromURI(algorithmURI));
        if (apacheValue != null) {
            return apacheValue;
        }
    
        // HMAC uses any symmetric key, so there is no implied specific key algorithm
        if (isHMAC(algorithmURI)) {
            return null;
        }
    
        // As a last ditch fallback, check some known common and supported ones.
        if (rsaAlgorithmURIs.contains(algorithmURI)) {
            return "RSA";
        }
        if (dsaAlgorithmURIs.contains(algorithmURI)) {
            return "DSA";
        }
        if (ecdsaAlgorithmURIs.contains(algorithmURI)) {
            return "ECDSA";
        }
    
        return null;
    }

    /**
     * Get the length of the key indicated by the algorithm URI, if applicable and available.
     * 
     * @param algorithmURI the algorithm URI to evaluate
     * @return the length of the key indicated by the algorithm URI, or null if the length is either unavailable or
     *         indeterminable from the URI
     */
    public static Integer getKeyLength(String algorithmURI) {
        Logger log = getLogger();
        String algoClass = StringSupport.trimOrNull(JCEMapper.getAlgorithmClassFromURI(algorithmURI));
    
        if (ApacheXMLSecurityConstants.ALGO_CLASS_BLOCK_ENCRYPTION.equals(algoClass)
                || ApacheXMLSecurityConstants.ALGO_CLASS_SYMMETRIC_KEY_WRAP.equals(algoClass)) {
    
            try {
                int keyLength = JCEMapper.getKeyLengthFromURI(algorithmURI);
                return new Integer(keyLength);
            } catch (NumberFormatException e) {
                log.warn("XML Security config contained invalid key length value for algorithm URI: " + algorithmURI);
            }
        }
    
        log.info("Mapping from algorithm URI {} to key length not available", algorithmURI);
        return null;
    }

    /**
     * Generates a random Java JCE symmetric Key object from the specified XML Encryption algorithm URI.
     * 
     * @param algoURI The XML Encryption algorithm URI
     * @return a randomly-generated symmetric Key
     * @throws NoSuchAlgorithmException thrown if the specified algorithm is invalid
     * @throws KeyException thrown if the length of the key to generate could not be determined
     */
    public static SecretKey generateSymmetricKey(String algoURI) throws NoSuchAlgorithmException, KeyException {
        Logger log = getLogger();
        String jceAlgorithmName = getKeyAlgorithm(algoURI);
        if (Strings.isNullOrEmpty(jceAlgorithmName)) {
            log.error("Mapping from algorithm URI '" + algoURI
                    + "' to key algorithm not available, key generation failed");
            throw new NoSuchAlgorithmException("Algorithm URI'" + algoURI + "' is invalid for key generation");
        }
        Integer keyLength = getKeyLength(algoURI);
        if (keyLength == null) {
            log.error("Key length could not be determined from algorithm URI, can't generate key");
            throw new KeyException("Key length not determinable from algorithm URI, could not generate new key");
        }
        KeyGenerator keyGenerator = KeyGenerator.getInstance(jceAlgorithmName);
        keyGenerator.init(keyLength);
        return keyGenerator.generateKey();
    }

    /**
     * Randomly generates a Java JCE KeyPair object from the specified XML Encryption algorithm URI.
     * 
     * @param algoURI  The XML Encryption algorithm URI
     * @param keyLength  the length of key to generate
     * @return a randomly-generated KeyPair
     * @throws NoSuchProviderException  provider not found
     * @throws NoSuchAlgorithmException  algorithm not found
     */
    public static KeyPair generateKeyPair(String algoURI, int keyLength) 
            throws NoSuchAlgorithmException, NoSuchProviderException {
        String jceAlgorithmName = JCEMapper.getJCEKeyAlgorithmFromURI(algoURI);
        return KeySupport.generateKeyPair(jceAlgorithmName, keyLength, null);
    }

    /**
     * Generate a random symmetric key and return in a BasicCredential.
     * 
     * @param algorithmURI The XML Encryption algorithm URI
     * @return a basic credential containing a randomly generated symmetric key
     * @throws KeyException 
     * @throws NoSuchAlgorithmException algorithm not found
     */
    public static Credential generateSymmetricKeyAndCredential(String algorithmURI) 
            throws NoSuchAlgorithmException, KeyException {
        SecretKey key = generateSymmetricKey(algorithmURI);
        BasicCredential credential = new BasicCredential(key);
        return credential;
    }

    /**
     * Generate a random asymmetric key pair and return in a BasicCredential.
     * 
     * @param algorithmURI The XML Encryption algorithm URI
     * @param keyLength key length
     * @param includePrivate if true, the private key will be included as well
     * @return a basic credential containing a randomly generated asymmetric key pair
     * @throws NoSuchAlgorithmException algorithm not found
     * @throws NoSuchProviderException provider not found
     */
    public static Credential generateKeyPairAndCredential(String algorithmURI, int keyLength, boolean includePrivate) 
            throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPair keyPair = generateKeyPair(algorithmURI, keyLength);
        BasicCredential credential = new BasicCredential(keyPair.getPublic());
        if (includePrivate) {
            credential.setPrivateKey(keyPair.getPrivate());
        }
        return credential;
    }
    
    /**
     * Get an SLF4J Logger.
     * 
     * @return a Logger instance
     */
    private static Logger getLogger() {
        return LoggerFactory.getLogger(AlgorithmSupport.class);
    }

    static {
        // We use some Apache XML Security utility functions, so need to make sure library
        // is initialized.
        if (!Init.isInitialized()) {
            Init.init();
        }

        // Additonal algorithm URI to JCA key algorithm mappins, beyond what is currently
        // supplied in the Apache XML Security mapper config.
        dsaAlgorithmURIs = new LazySet<String>();
        dsaAlgorithmURIs.add(SignatureConstants.ALGO_ID_SIGNATURE_DSA);

        ecdsaAlgorithmURIs = new LazySet<String>();
        ecdsaAlgorithmURIs.add(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA1);

        rsaAlgorithmURIs = new HashSet<String>(10);
        rsaAlgorithmURIs.add(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
        rsaAlgorithmURIs.add(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        rsaAlgorithmURIs.add(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA384);
        rsaAlgorithmURIs.add(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512);
        rsaAlgorithmURIs.add(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512);
        rsaAlgorithmURIs.add(SignatureConstants.ALGO_ID_SIGNATURE_RSA_RIPEMD160);
        rsaAlgorithmURIs.add(SignatureConstants.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5);
    }

}
