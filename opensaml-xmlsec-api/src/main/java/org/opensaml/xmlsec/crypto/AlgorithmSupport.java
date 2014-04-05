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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import net.shibboleth.utilities.java.support.collection.LazySet;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.apache.xml.security.Init;
import org.apache.xml.security.algorithms.JCEMapper;
import org.opensaml.core.config.ConfigurationService;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.ApacheXMLSecurityConstants;
import org.opensaml.xmlsec.algorithm.AlgorithmRegistry;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * Helper methods for working with XML security algorithm URI's.
 */
public final class AlgorithmSupport {
    
    /** Logger. */
    private static final Logger LOG = LoggerFactory.getLogger(AlgorithmSupport.class);
    
    /** Additional algorithm URI's which imply RSA keys. */
    private static Set<String> rsaAlgorithmURIs;

    /** Additional algorithm URI's which imply DSA keys. */
    private static Set<String> dsaAlgorithmURIs;

    /** Additional algorithm URI's which imply ECDSA keys. */
    private static Set<String> ecdsaAlgorithmURIs;
    
    /** Constructor. */
    private AlgorithmSupport() {
        
    }
    
    /**
     * Get the global {@link AlgorithmRegistry} instance.
     * 
     * @return the global algorithm registry, or null if nothing registered
     */
    @Nullable public static AlgorithmRegistry getGlobalAlgorithmRegistry() {
        return ConfigurationService.get(AlgorithmRegistry.class);
    }

    /**
     * Get the Java security JCA/JCE algorithm identifier associated with an algorithm URI.
     * 
     * @param algorithmURI the algorithm URI to evaluate
     * @return the Java algorithm identifier, or null if the mapping is unavailable or indeterminable from the URI
     */
    @Nullable public static String getAlgorithmID(@Nonnull final String algorithmURI) {
        return StringSupport.trimOrNull(JCEMapper.translateURItoJCEID(algorithmURI));
    }

    /**
     * Check whether the signature method algorithm URI indicates HMAC.
     * 
     * @param signatureAlgorithm the signature method algorithm URI
     * @return true if URI indicates HMAC, false otherwise
     */
    public static boolean isHMAC(@Nonnull final String signatureAlgorithm) {
        String algoClass = StringSupport.trimOrNull(JCEMapper.getAlgorithmClassFromURI(signatureAlgorithm));
        return ApacheXMLSecurityConstants.ALGO_CLASS_MAC.equals(algoClass);
    }
    
    /**
     * Get the Java security JCA/JCE key algorithm specifier associated with an algorithm URI.
     * 
     * @param algorithmURI the algorithm URI to evaluate
     * @return the Java key algorithm specifier, or null if the mapping is unavailable or indeterminable from the URI
     */
    @Nullable public static String getKeyAlgorithm(@Nonnull final String algorithmURI) {
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
        } else if (dsaAlgorithmURIs.contains(algorithmURI)) {
            return "DSA";
        } else if (ecdsaAlgorithmURIs.contains(algorithmURI)) {
            return "EC";
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
    @Nullable public static Integer getKeyLength(@Nonnull final String algorithmURI) {
        Logger log = getLogger();
        String algoClass = StringSupport.trimOrNull(JCEMapper.getAlgorithmClassFromURI(algorithmURI));
    
        if (ApacheXMLSecurityConstants.ALGO_CLASS_BLOCK_ENCRYPTION.equals(algoClass)
                || ApacheXMLSecurityConstants.ALGO_CLASS_SYMMETRIC_KEY_WRAP.equals(algoClass)) {
    
            try {
                return JCEMapper.getKeyLengthFromURI(algorithmURI);
            } catch (NumberFormatException e) {
                log.warn("XML Security config contained invalid key length value for algorithm URI {}", algorithmURI);
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
    @Nonnull public static SecretKey generateSymmetricKey(@Nonnull final String algoURI)
            throws NoSuchAlgorithmException, KeyException {
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
    @Nonnull public static KeyPair generateKeyPair(@Nonnull final String algoURI, int keyLength) 
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
    @Nonnull public static Credential generateSymmetricKeyAndCredential(@Nonnull final String algorithmURI) 
            throws NoSuchAlgorithmException, KeyException {
        SecretKey key = generateSymmetricKey(algorithmURI);
        return new BasicCredential(key);
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
    @Nonnull public static Credential generateKeyPairAndCredential(@Nonnull final String algorithmURI, int keyLength,
            boolean includePrivate) throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPair keyPair = generateKeyPair(algorithmURI, keyLength);
        BasicCredential credential = new BasicCredential(keyPair.getPublic());
        if (includePrivate) {
            credential.setPrivateKey(keyPair.getPrivate());
        }
        return credential;
    }
    
    /**
     * Validate the supplied algorithm URI against the specified whitelist and blacklist.
     * 
     * @param algorithmURI the algorithm URI to evaluate
     * @param whitelistedAlgorithmURIs the algorithm whitelist
     * @param blacklistedAlgorithmURIs the algorithm blacklist
     * 
     * @return true if algorithm URI satisfies the specified whitelist and blacklist, otherwise false
     */
    public static boolean validateAlgorithmURI(@Nonnull final String algorithmURI, 
            @Nullable final Collection<String> whitelistedAlgorithmURIs,
            @Nullable final Collection<String> blacklistedAlgorithmURIs) {
        
        if (blacklistedAlgorithmURIs != null) {
            LOG.debug("Saw non-null algorithm blacklist: {}", blacklistedAlgorithmURIs);
            if (blacklistedAlgorithmURIs.contains(algorithmURI)) {
                LOG.warn("Algorithm failed blacklist validation: {}", algorithmURI);
                return false;
            } else {
                LOG.debug("Algorithm passed blacklist validation: {}", algorithmURI);
            }
        } else {
            LOG.debug("Saw null algorithm blacklist, nothing to evaluate");
        }
        
        if (whitelistedAlgorithmURIs != null) {
            LOG.debug("Saw non-null algorithm whitelist: {}", whitelistedAlgorithmURIs);
            if (!whitelistedAlgorithmURIs.isEmpty()) {
                if (!whitelistedAlgorithmURIs.contains(algorithmURI)) {
                    LOG.warn("Algorithm failed whitelist validation: {}", algorithmURI);
                    return false;
                } else {
                    LOG.debug("Algorithm passed whitelist validation: {}", algorithmURI);
                }
            } else {
               LOG.debug("Non-null algorithm whitelist was empty, skipping evaluation");
            }
        } else {
            LOG.debug("Saw null algorithm whitelist, nothing to evaluate");
        }
        
        return true;
    }
    
    /**
     * Get an SLF4J Logger.
     * 
     * @return a Logger instance
     */
    @Nonnull private static Logger getLogger() {
        return LoggerFactory.getLogger(AlgorithmSupport.class);
    }

    static {
        // We use some Apache XML Security utility functions, so need to make sure library
        // is initialized.
        if (!Init.isInitialized()) {
            Init.init();
        }

        // Additonal algorithm URI to JCA key algorithm mappings, beyond what is currently
        // supplied in the Apache XML Security mapper config.
        dsaAlgorithmURIs = new LazySet<String>();
        dsaAlgorithmURIs.add(SignatureConstants.ALGO_ID_SIGNATURE_DSA_SHA1);

        ecdsaAlgorithmURIs = new LazySet<String>();
        ecdsaAlgorithmURIs.add(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA1);
        ecdsaAlgorithmURIs.add(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA256);
        ecdsaAlgorithmURIs.add(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA384);
        ecdsaAlgorithmURIs.add(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA512);

        rsaAlgorithmURIs = new HashSet<String>(10);
        rsaAlgorithmURIs.add(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
        rsaAlgorithmURIs.add(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        rsaAlgorithmURIs.add(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA384);
        rsaAlgorithmURIs.add(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512);
        rsaAlgorithmURIs.add(SignatureConstants.ALGO_ID_SIGNATURE_RSA_RIPEMD160);
        rsaAlgorithmURIs.add(SignatureConstants.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5);
    }

}