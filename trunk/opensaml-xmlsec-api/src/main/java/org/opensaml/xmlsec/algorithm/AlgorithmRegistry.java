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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

/**
 * A registry of {@link AlgorithmDescriptor} instances, to support various use cases for working with algorithm URIs.
 */
public class AlgorithmRegistry {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(AlgorithmRegistry.class);
    
    /** Map of registered algorithm descriptors. */
    private Map<String, AlgorithmDescriptor> descriptors;
    
    /** Index of digest type to AlgorithmDescriptor. */
    private Map<String, DigestAlgorithm> digestAlgorithms;
    
    /** Index of (KeyType,DigestType) to AlgorithmDescriptor. */
    private Map<SignatureAlgorithmIndex, SignatureAlgorithm> signatureAlgorithms;
    
    /** Constructor. */
    public  AlgorithmRegistry() {
        descriptors = new HashMap<>();
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
     * Clear all registered algorithms.
     */
    public void clear() {
        descriptors.clear();
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
        if (descriptors.containsKey(descriptor.getURI())) {
            log.debug("Registry contained existing descriptor with URI, removing old instance and re-registering: {}",
                    descriptor.getURI());
            AlgorithmDescriptor old = descriptors.get(descriptor.getURI());
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
