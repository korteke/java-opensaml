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

import java.security.KeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.crypto.KeySupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * Helper methods for working with XML security algorithm URI's.
 */
public final class AlgorithmSupport {
    
    /** Logger. */
    private static final Logger LOG = LoggerFactory.getLogger(AlgorithmSupport.class);
    
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
        AlgorithmRegistry registry = getGlobalAlgorithmRegistry();
        if (registry != null){
            AlgorithmDescriptor descriptor = registry.get(algorithmURI);
            if (descriptor != null) {
                return descriptor.getJCAAlgorithmID();
            }
        }
        return null;
    }

    /**
     * Check whether the signature method algorithm URI indicates HMAC.
     * 
     * @param signatureAlgorithm the signature method algorithm URI
     * @return true if URI indicates HMAC, false otherwise
     */
    public static boolean isHMAC(@Nonnull final String signatureAlgorithm) {
        AlgorithmRegistry registry = getGlobalAlgorithmRegistry();
        if (registry != null){
            AlgorithmDescriptor descriptor = registry.get(signatureAlgorithm);
            if (descriptor != null) {
                return descriptor.getType().equals(AlgorithmDescriptor.AlgorithmType.Mac);
            }
        }
        return false;
    }
    
    /**
     * Get the Java security JCA/JCE key algorithm specifier associated with an algorithm URI.
     * 
     * @param algorithmURI the algorithm URI to evaluate
     * @return the Java key algorithm specifier, or null if the mapping is unavailable or indeterminable from the URI
     */
    @Nullable public static String getKeyAlgorithm(@Nonnull final String algorithmURI) {
        AlgorithmRegistry registry = getGlobalAlgorithmRegistry();
        if (registry != null){
            AlgorithmDescriptor descriptor = registry.get(algorithmURI);
            if (descriptor != null && descriptor instanceof KeySpecifiedAlgorithm) {
                return ((KeySpecifiedAlgorithm)descriptor).getKey();
            }
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
        AlgorithmRegistry registry = getGlobalAlgorithmRegistry();
        if (registry != null){
            AlgorithmDescriptor descriptor = registry.get(algorithmURI);
            if (descriptor != null && descriptor instanceof KeyLengthSpecifiedAlgorithm) {
                return ((KeyLengthSpecifiedAlgorithm)descriptor).getKeyLength();
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
        String jceAlgorithmName = getKeyAlgorithm(algoURI);
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

}