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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * A registry of {@link AlgorithmDescriptor} instances, to support various use cases for working with algorithm URIs.
 */
public class AlgorithmRegistry {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(AlgorithmRegistry.class);
    
    /** Map of registered algorithm descriptors. */
    private Map<String, AlgorithmDescriptor> descriptors;
    
    /** Set containing algorithms which are supported by the runtime environment. */
    private Set<String> runtimeSupported;
    
    /** Index of digest type to AlgorithmDescriptor. */
    private Map<String, DigestAlgorithm> digestAlgorithms;
    
    /** Index of (KeyType,DigestType) to AlgorithmDescriptor. */
    private Map<SignatureAlgorithmIndex, SignatureAlgorithm> signatureAlgorithms;
    
    /** Constructor. */
    public  AlgorithmRegistry() {
        descriptors = new HashMap<>();
        runtimeSupported = new HashSet<>();
        digestAlgorithms = new HashMap<>();
        signatureAlgorithms = new HashMap<>();
    }
    
    /**
     * Get the algorithm descriptor instance associated with the specified algorithm URI.
     * @param algorithmURI the algorithm URI to resolve
     * 
     * @return the resolved algorithm descriptor or null
     */
    @Nullable public AlgorithmDescriptor get(@Nullable final String algorithmURI) {
        String trimmed = StringSupport.trimOrNull(algorithmURI);
        if (trimmed == null) {
            return null;
        }
        
        return descriptors.get(trimmed);
    }
    
    /**
     * Retrieve indication of whether the runtime environment supports the algorithm. 
     * 
     * <p>
     * This evaluation is performed dynamically when the algorithm is registered.
     * </p> 
     * 
     * @param algorithmURI the algorithm URI to evaluate
     * 
     * @return true if the algorithm is supported by the current runtime environment, false otherwise
     */
    public boolean isRuntimeSupported(@Nullable final String algorithmURI) {
        String trimmed = StringSupport.trimOrNull(algorithmURI);
        if (trimmed == null) {
            return false;
        }
        
        return runtimeSupported.contains(trimmed);
    }
    
    /**
     * Clear all registered algorithms.
     */
    public void clear() {
        descriptors.clear();
        runtimeSupported.clear();
        digestAlgorithms.clear();
        signatureAlgorithms.clear();
    }
    
    /**
     * Register an algorithm.
     * 
     * @param descriptor the algorithm
     */
    public void register(@Nonnull final AlgorithmDescriptor descriptor) {
        Constraint.isNotNull(descriptor, "AlgorithmDescriptor was null");
        
        log.info("Registering algorithm descriptor with URI: {}", descriptor.getURI());
        
        AlgorithmDescriptor old = descriptors.get(descriptor.getURI());
        if (old != null) {
            log.debug("Registry contained existing descriptor with URI, removing old instance and re-registering: {}",
                    descriptor.getURI());
            deindex(old);
            deregister(old);
        }
        descriptors.put(descriptor.getURI(), descriptor);
        index(descriptor);
    }

    /**
     * Deregister an algorithm.
     * 
     * @param descriptor the algorithm
     */
    public void deregister(@Nonnull final AlgorithmDescriptor descriptor) {
        Constraint.isNotNull(descriptor, "AlgorithmDescriptor was null");
        if (descriptors.containsKey(descriptor.getURI())) {
            deindex(descriptor);
            descriptors.remove(descriptor.getURI());
        } else {
            log.debug("Registry did not contain descriptor with URI, nothing to do: {}", descriptor.getURI());
        }
    }
    
    /**
     * Deregister an algorithm.
     * 
     * @param uri the algorithm URI
     */
    public void deregister(@Nonnull final String uri) {
        Constraint.isNotNull(uri, "AlgorithmDescriptor URI was null");
        AlgorithmDescriptor descriptor = get(uri);
        if (descriptor != null) {
            deregister(descriptor);
        }
    }
    
    /**
     * Lookup a digest method algorithm descriptor by the JCA digest method ID.
     * 
     * @param digestMethod the JCA digest method ID.
     * 
     * @return the algorithm descriptor, or null
     */
    @Nullable public DigestAlgorithm getDigestAlgorithm(@Nonnull final String digestMethod) {
        Constraint.isNotNull(digestMethod, "Digest method was null");
        return digestAlgorithms.get(digestMethod);
    }
    
    /**
     * Lookup a signature algorithm descriptor by the JCA key algorithm and digest method IDs.
     * 
     * @param keyType the JCA key algorithm ID.
     * @param digestMethod the JCA digest method ID.
     * 
     * @return the algorithm descriptor, or null
     */
    @Nullable public SignatureAlgorithm getSignatureAlgorithm(@Nonnull final String keyType, 
            @Nonnull final String digestMethod) {
        Constraint.isNotNull(keyType, "Key type was null");
        Constraint.isNotNull(digestMethod, "Digest type was null");
        return signatureAlgorithms.get(new SignatureAlgorithmIndex(keyType, digestMethod));
    }

    /**
     * Add the algorithm descriptor to the indexes which support the various lookup methods 
     * available via the registry's API.
     * 
     * @param descriptor the algorithm
     */
    private void index(AlgorithmDescriptor descriptor) {
        if (checkRuntimeSupports(descriptor)) {
            runtimeSupported.add(descriptor.getURI());
        } else {
            log.warn("Algorithm failed runtime support check, will not be usable: {}", descriptor.getURI());
            // Just for good measure, for case where environment has changed 
            // and algorithm is being re-registered.
            runtimeSupported.remove(descriptor.getURI());
        }
        
        if (descriptor instanceof DigestAlgorithm) {
            DigestAlgorithm digestAlgorithm = (DigestAlgorithm) descriptor;
            digestAlgorithms.put(digestAlgorithm.getJCAAlgorithmID(), digestAlgorithm);
        }
        if (descriptor instanceof SignatureAlgorithm) {
            SignatureAlgorithm sigAlg = (SignatureAlgorithm) descriptor;
            signatureAlgorithms.put(new SignatureAlgorithmIndex(sigAlg.getKey(), sigAlg.getDigest()), sigAlg);
        }
    }
    
    /**
     * Remove the algorithm descriptor from the indexes which support the various lookup methods 
     * available via the registry's API.
     * 
     * @param descriptor the algorithm
     */
    private void deindex(AlgorithmDescriptor descriptor) {
        runtimeSupported.remove(descriptor.getURI());
        
        if (descriptor instanceof DigestAlgorithm) {
            DigestAlgorithm digestAlgorithm = (DigestAlgorithm) descriptor;
            digestAlgorithms.remove(digestAlgorithm.getJCAAlgorithmID());
        }
        if (descriptor instanceof SignatureAlgorithm) {
            SignatureAlgorithm sigAlg = (SignatureAlgorithm) descriptor;
            signatureAlgorithms.remove(new SignatureAlgorithmIndex(sigAlg.getKey(), sigAlg.getDigest()));
        }
    }
    
    /**
     * Evaluate whether the algorithm is supported by the current runtime environment.
     * 
     * @param descriptor the algorithm
     * 
     * @return true if runtime supports the algorithm, false otherwise
     */
    private boolean checkRuntimeSupports(AlgorithmDescriptor descriptor) {
        
        try {
            switch(descriptor.getType()) {
                case BlockEncryption:
                case KeyTransport:
                case SymmetricKeyWrap:
                    Cipher.getInstance(descriptor.getJCAAlgorithmID());
                    break;
                    
                case Signature:
                    Signature.getInstance(descriptor.getJCAAlgorithmID());
                    break;
                    
                case Mac:
                    Mac.getInstance(descriptor.getJCAAlgorithmID());
                    break;
                    
                case MessageDigest:
                    MessageDigest.getInstance(descriptor.getJCAAlgorithmID());
                    break;
                    
                default:
                    log.warn("Saw unknown AlgorithmDescriptor type, failing runtime support check: {}",
                            descriptor.getClass().getName());
                
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            if (!checkSpecialCasesRuntimeSupport(descriptor)) {
                log.debug(String.format("AlgorithmDescriptor failed runtime support check: %s", 
                        descriptor.getURI()), e);
                return false;
            }
        } catch (Throwable t) {
            log.error("Fatal error evaluating algorithm runtime support", t);
            return false;
        }
        
        return true;
    }
    
    /**
     * Check for special cases of runtime support which failed the initial simple service class load check.
     * 
     * @param descriptor the algorithm
     * 
     * @return true if algorithm is supported by the runtime environment, false otherwise
     */
    private boolean checkSpecialCasesRuntimeSupport(AlgorithmDescriptor descriptor) {
        log.trace("Checking runtime support failure for special cases: {}", descriptor.getURI());
        try {
            // Per Santuario XMLCipher: Some JDKs don't support RSA/ECB/OAEPPadding.
            // So check specifically for OAEPPadding with explicit SHA-1 digest and MGF1.
            if (EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP.equals(descriptor.getURI())) {
                Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
                log.trace("RSA OAEP algorithm passed as special case with OAEPWithSHA1AndMGF1Padding");
                return true;
            }
            
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            log.trace("Special case eval for algorithm failed with exception", e);
        }
        
        log.trace("Algorithm was not supported by any special cases: {}", descriptor.getURI());
        return false;
    }

    /**
     * Class used as index key for signature algorithm lookup.
     */
    protected class SignatureAlgorithmIndex {
        
        /** Key type. */
        private String key;
        
        /** Digest type. */
        private String digest;
        
        /**
         * Constructor.
         *
         * @param keyType the key type
         * @param digestType the digest type
         */
        public SignatureAlgorithmIndex(@Nonnull final String keyType, @Nonnull final String digestType) {
            key = StringSupport.trim(keyType);
            digest = StringSupport.trim(digestType);
        }
        
        /** {@inheritDoc} */
        public int hashCode() {
            int result = 17;  
            result = 37*result + key.hashCode();
            result = 37*result + digest.hashCode();
            return result;  
        }

        /** {@inheritDoc} */
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            
            if (obj instanceof SignatureAlgorithmIndex) {
               SignatureAlgorithmIndex other = (SignatureAlgorithmIndex) obj; 
               return Objects.equal(this.key, other.key) && Objects.equal(this.digest, other.digest);
            }
            
            return false;
        }

        /** {@inheritDoc} */
        public String toString() {
            ToStringHelper tsh = Objects.toStringHelper(this);
            tsh.add("Key", key);
            tsh.add("Digest", digest);
            return tsh.toString();
        }
        
    }

}
