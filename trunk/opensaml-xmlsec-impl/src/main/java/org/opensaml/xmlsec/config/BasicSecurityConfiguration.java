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

package org.opensaml.xmlsec.config;

import java.security.Key;
import java.security.interfaces.DSAParams;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.SecurityConfiguration;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Basic in-memory implementation of {@link SecurityConfiguration}.
 */
public class BasicSecurityConfiguration implements SecurityConfiguration {
    
    /** The name of the KeyInfoCredentialResolver default config. */
    public static final String KEYINFO_RESOLVER_DEFAULT_CONFIG = "_KEYINFO_RESOLVER_DEFAULT_";
    
    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(BasicSecurityConfiguration.class);
    
    /** JCA algorithm to signature URI mappings. */
    private final Map<String, String> signatureAlgorithms;
    
    /** Signature canonicalization algorithm URI. */
    private String signatureCanonicalization;
    
    /** Signature Reference digest method algorithm URI. */
    private String signatureReferenceDigestMethod;
    
    /** Signature HMAC output length. */
    private Integer signatureHMACOutputLength;
    
    /** JCA algorithm to data encryption URI mappings. */
    private final Map<DataEncryptionIndex, String> dataEncryptionAlgorithms;
    
    /** JCA algorithm to key transport encryption URI mappings. */
    private final Map<KeyTransportEncryptionIndex, String> keyTransportEncryptionAlgorithms;
    
    /** Encryption algorithm URI for auto-generated data encryption keys. */
    private String autoGenEncryptionURI;
    
    /** Manager for named KeyInfoGenerator instances. */
    private NamedKeyInfoGeneratorManager keyInfoGeneratorManager;
    
    /** Set of named KeyInfoCredentialResolvers. */
    private final Map<String, KeyInfoCredentialResolver> keyInfoCredentialResolvers;
    
    /** Default DSA key family parameters. */
    private final Map<Integer, DSAParams> dsaParams;
    
    /** Constructor. */
    public BasicSecurityConfiguration() {
        signatureAlgorithms = new HashMap<String, String>();
        dataEncryptionAlgorithms = new HashMap<DataEncryptionIndex, String>();
        keyTransportEncryptionAlgorithms = new HashMap<KeyTransportEncryptionIndex, String>();
        keyInfoCredentialResolvers = new HashMap<String, KeyInfoCredentialResolver>();
        dsaParams = new HashMap<Integer, DSAParams>();
    }
    
    // Signature-related config
    
    /** {@inheritDoc} */
    @Nullable public String getSignatureAlgorithmURI(@Nonnull final String jcaAlgorithmName) {
        return signatureAlgorithms.get(jcaAlgorithmName);
    }
    
    /** {@inheritDoc} */
    @Nullable public String getSignatureAlgorithmURI(@Nonnull final Credential credential) {
        Key key = CredentialSupport.extractSigningKey(credential);
        if (key == null) {
            log.debug("Could not extract signing key from credential, unable to map to algorithm URI");
            return null;
        } else if (key.getAlgorithm() == null) {
            log.debug("Signing key algorithm value was not available, unable to map to algorithm URI");
            return null;
        }
        return getSignatureAlgorithmURI(key.getAlgorithm());
    }
    
    /**
     * Register a mapping from the specified JCA key algorithm name to a signature algorithm URI.
     * 
     * @param jcaAlgorithmName the JCA key algorithm name to register
     * @param algorithmURI the algorithm URI to register
     */
    public void registerSignatureAlgorithmURI(@Nonnull final String jcaAlgorithmName,
            @Nonnull final String algorithmURI) {
        signatureAlgorithms.put(jcaAlgorithmName, algorithmURI);
    }
    
    /**
     * Deregister a mapping for the specified JCA key algorithm name.
     * 
     * @param jcaAlgorithmName the JCA key algorithm name to deregister
     */
    public void deregisterSignatureAlgorithmURI(@Nonnull final String jcaAlgorithmName) {
        signatureAlgorithms.remove(jcaAlgorithmName);
    }

    /** {@inheritDoc} */
    @Nullable public String getSignatureCanonicalizationAlgorithm() {
        return signatureCanonicalization;
    }
    
    /**
     * Set a canonicalization algorithm URI suitable for use as a Signature CanonicalizationMethod value.
     * 
     * @param algorithmURI a canonicalization algorithm URI
     */
    public void setSignatureCanonicalizationAlgorithm(@Nullable final String algorithmURI) {
        signatureCanonicalization = algorithmURI;
    }

    /** {@inheritDoc} */
    @Nullable public String getSignatureReferenceDigestMethod() {
        return signatureReferenceDigestMethod;
    }
    
    /**
     * Set a digest method algorithm URI suitable for use as a Signature Reference DigestMethod value.
     * 
     * @param algorithmURI a digest method algorithm URI
     */
    public void setSignatureReferenceDigestMethod(@Nullable final String algorithmURI) {
        signatureReferenceDigestMethod = algorithmURI;
    }
 
    /** {@inheritDoc} */
    @Nullable public Integer getSignatureHMACOutputLength() {
        return signatureHMACOutputLength;
    }
    
    /**
     * Set the value to be used as the Signature SignatureMethod HMACOutputLength value, used
     * only when signing with an HMAC algorithm.  This value is optional when using HMAC.
     * 
     * @param length the HMAC output length value to use when performing HMAC signing (may be null)
     */
    public void setSignatureHMACOutputLength(@Nullable final Integer length) {
        signatureHMACOutputLength = length;
    }
    
    //  Encryption-related config
    
    /** {@inheritDoc} */
    @Nullable public String getDataEncryptionAlgorithmURI(@Nonnull final String jcaAlgorithmName,
            @Nullable final Integer keyLength) {
        DataEncryptionIndex index = new DataEncryptionIndex(jcaAlgorithmName, keyLength);
        String algorithmURI = dataEncryptionAlgorithms.get(index);
        if (algorithmURI != null) {
            return algorithmURI;
        }
        if (keyLength != null) {
            // Fall through to default, i.e. with no specific key length registered
            log.debug("No data encryption algorithm mapping available for JCA name + key length, " 
                    + "trying JCA name alone");
            index = new DataEncryptionIndex(jcaAlgorithmName, null);
            return dataEncryptionAlgorithms.get(index);
        }
        return null;
    }
    
    /** {@inheritDoc} */
    @Nullable public String getDataEncryptionAlgorithmURI(@Nonnull final Credential credential) {
        Key key = CredentialSupport.extractEncryptionKey(credential);
        if (key == null) {
            log.debug("Could not extract data encryption key from credential, unable to map to algorithm URI");
            return null;
        } else if (key.getAlgorithm() == null){
            log.debug("Data encryption key algorithm value was not available, unable to map to algorithm URI");
            return null;
        }
        Integer length = KeySupport.getKeyLength(key);
        return getDataEncryptionAlgorithmURI(key.getAlgorithm(), length);
    }
    
    /**
     * Register a mapping from the specified JCA key algorithm name to an encryption algorithm URI.
     * 
     * @param jcaAlgorithmName the JCA key algorithm name to register
     * @param keyLength the key length to register (may be null)
     * @param algorithmURI the algorithm URI to register
     */
    public void registerDataEncryptionAlgorithmURI(@Nonnull final String jcaAlgorithmName,
            @Nullable final Integer keyLength, @Nonnull final String algorithmURI) {
        DataEncryptionIndex index = new DataEncryptionIndex(jcaAlgorithmName, keyLength);
        dataEncryptionAlgorithms.put(index, algorithmURI);
    }
    
    /**
     * Deregister a mapping for the specified JCA key algorithm name.
     * 
     * @param jcaAlgorithmName the JCA algorithm name to deregister
     * @param keyLength the key length to deregister (may be null)
     */
    public void deregisterDataEncryptionAlgorithmURI(@Nonnull final String jcaAlgorithmName,
            @Nullable final Integer keyLength) {
        DataEncryptionIndex index = new DataEncryptionIndex(jcaAlgorithmName, keyLength);
        dataEncryptionAlgorithms.remove(index);
    }
    
    /** {@inheritDoc} */
    public String getKeyTransportEncryptionAlgorithmURI(@Nonnull final String jcaAlgorithmName,
            @Nullable final Integer keyLength, @Nullable final String wrappedKeyAlgorithm) {
        
        KeyTransportEncryptionIndex index = 
            new KeyTransportEncryptionIndex(jcaAlgorithmName, keyLength, wrappedKeyAlgorithm);
        String algorithmURI = keyTransportEncryptionAlgorithms.get(index);
        if (algorithmURI != null) {
            return algorithmURI;
        }
        
        if (wrappedKeyAlgorithm != null) {
            // Fall through to case of no specific wrapped key algorithm registered
            log.debug("No data encryption algorithm mapping available for JCA name + key length + wrapped algorithm, " 
                    + "trying JCA name + key length");
            index = new KeyTransportEncryptionIndex(jcaAlgorithmName, keyLength, null);
            algorithmURI = keyTransportEncryptionAlgorithms.get(index);
            if (algorithmURI != null) {
                return algorithmURI;
            }
        }
        if (keyLength != null) {
            // Fall through to case of no specific key length registered
            log.debug("No data encryption algorithm mapping available for JCA name + key length + wrapped algorithm, " 
                    + "trying JCA name + wrapped algorithm");
            index = new KeyTransportEncryptionIndex(jcaAlgorithmName, null, wrappedKeyAlgorithm);
            algorithmURI = keyTransportEncryptionAlgorithms.get(index);
            if (algorithmURI != null) {
                return algorithmURI;
            }
        }
        // Fall through to case of no specific key length or wrapped key algorithm registered
        log.debug("No data encryption algorithm mapping available for JCA name + key length + wrapped algorithm, " 
                + "trying JCA name alone");
        index = new KeyTransportEncryptionIndex(jcaAlgorithmName, null, null);
        return keyTransportEncryptionAlgorithms.get(index);
    }
    
    /** {@inheritDoc} */
    @Nullable public String getKeyTransportEncryptionAlgorithmURI(@Nonnull final Credential credential,
            @Nullable final String wrappedKeyAlgorithm) {
        Key key = CredentialSupport.extractEncryptionKey(credential);
        if (key == null) {
            log.debug("Could not extract key transport encryption key from credential, unable to map to algorithm URI");
            return null;
        } else if (key.getAlgorithm() == null){
            log.debug("Key transport encryption key algorithm value was not available, unable to map to algorithm URI");
            return null;
        }
        Integer length = KeySupport.getKeyLength(key);
        return getKeyTransportEncryptionAlgorithmURI(key.getAlgorithm(), length, wrappedKeyAlgorithm);
    }

    /**
     * Register a mapping from the specified JCA key algorithm name to an encryption algorithm URI.
     * 
     * @param jcaAlgorithmName the JCA algorithm name to register
     * @param keyLength the key length to register (may be null)
     * @param wrappedKeyAlgorithm the JCA algorithm name of the key to be encrypted (may be null)
     * @param algorithmURI the algorithm URI to register
     */
    public void registerKeyTransportEncryptionAlgorithmURI(@Nonnull final String jcaAlgorithmName,
            @Nullable final Integer keyLength, @Nullable final String wrappedKeyAlgorithm,
            @Nonnull final String algorithmURI) {
        
        KeyTransportEncryptionIndex index = 
            new KeyTransportEncryptionIndex(jcaAlgorithmName, keyLength, wrappedKeyAlgorithm);
        keyTransportEncryptionAlgorithms.put(index, algorithmURI);
    }

    /**
     * Deregister a mapping for the specified JCA key algorithm name.
     * 
     * @param jcaAlgorithmName the JCA algorithm name to deregister
     * @param keyLength the key length to deregister (may be null)
     * @param wrappedKeyAlgorithm the JCA algorithm name of the key to be encrypted (may be null)
     */
    public void deregisterKeyTransportEncryptionAlgorithmURI(@Nonnull final String jcaAlgorithmName,
            @Nullable final Integer keyLength, @Nullable final String wrappedKeyAlgorithm) {
        
        KeyTransportEncryptionIndex index = 
            new KeyTransportEncryptionIndex(jcaAlgorithmName, keyLength, wrappedKeyAlgorithm);
        keyTransportEncryptionAlgorithms.remove(index);

    }
    
    /** {@inheritDoc} */
    @Nullable public String getAutoGeneratedDataEncryptionKeyAlgorithmURI() {
        return autoGenEncryptionURI;
    }
    
    /**
     * Set the encryption algorithm URI to be used when auto-generating random data encryption keys.
     * 
     * @param algorithmURI the encryption algorithm URI to use
     */
    public void setAutoGeneratedDataEncryptionKeyAlgorithmURI(@Nullable final String algorithmURI) {
        autoGenEncryptionURI = algorithmURI;
    }
    
    
    // KeyInfo-related config
    
    /** {@inheritDoc} */
    @Nullable public NamedKeyInfoGeneratorManager getKeyInfoGeneratorManager() {
        return keyInfoGeneratorManager;
    }
    /**
     * Set the manager for named KeyInfoGenerator instances.
     * 
     * @param keyInfoManager the KeyInfoGenerator manager to use
     */
    public void setKeyInfoGeneratorManager(@Nullable final NamedKeyInfoGeneratorManager keyInfoManager) {
        keyInfoGeneratorManager = keyInfoManager;
    }
    
    /** {@inheritDoc} */
    @Nullable public KeyInfoCredentialResolver getDefaultKeyInfoCredentialResolver() {
        return keyInfoCredentialResolvers.get(KEYINFO_RESOLVER_DEFAULT_CONFIG);
    }
    
    /**
     * Set the default KeyInfoCredentialResolver config.
     * 
     * @param resolver the default KeyInfoCredentialResolver
     */
    public void setDefaultKeyInfoCredentialResolver(@Nullable final KeyInfoCredentialResolver resolver) {
        keyInfoCredentialResolvers.put(KEYINFO_RESOLVER_DEFAULT_CONFIG, resolver);
    }

    /** {@inheritDoc} */
    @Nullable public KeyInfoCredentialResolver getKeyInfoCredentialResolver(@Nonnull final String name) {
        return keyInfoCredentialResolvers.get(name);
    }
    
    /**
     * Register a named KeyInfoCredentialResolver configuration.
     * 
     * @param name the name of the configuration
     * @param resolver the KeyInfoCredentialResolver to register
     */
    public void registerKeyInfoCredentialResolver(@Nonnull final String name,
            @Nonnull final KeyInfoCredentialResolver resolver) {
        keyInfoCredentialResolvers.put(name, resolver);
    }
    
    /**
     * Deregister a named KeyInfoCredentialResolver configuration.
     * 
     * @param name the name of the configuration
     */
    public void deregisterKeyInfoCredentialResolver(@Nonnull final String name) {
        keyInfoCredentialResolvers.remove(name);
    }
 
    // Miscellaneous config

    /** {@inheritDoc} */
    @Nullable public DSAParams getDSAParams(int keyLength) {
        return dsaParams.get(keyLength);
    }
    
    /**
     * Set a DSA parameters instance which defines the default DSA key information to be used 
     * within a DSA "key family".
     * 
     * @param keyLength the key length of the DSA parameters 
     * @param params the default DSA parameters instance
     */
    public void setDSAParams(int keyLength, @Nonnull final DSAParams params) {
        dsaParams.put(keyLength, params);
    }
    
    
    /**
     * Class used as an index to the data encryption algorithm URI map.
     */
    protected class DataEncryptionIndex {
        
        /** The JCA key algorithm name. */
        private final String keyAlgorithm;
        
        /** The key length.  Optional, may be null. */
        private final Integer keyLength;
        
        /**
         * Constructor.
         *
         * @param jcaAlgorithmName the JCA algorithm name
         * @param length the key length (optional, may be null)
         */
        protected DataEncryptionIndex(@Nonnull final String jcaAlgorithmName, @Nullable final Integer length) {
            keyAlgorithm = Constraint.isNotNull(StringSupport.trimOrNull(jcaAlgorithmName),
                    "JCA Algorithm name cannot be null or empty");
            keyLength = length;
        }
        
        /** {@inheritDoc} */
        public boolean equals(Object obj) {
            if(obj == this){
                return true;
            } else if (!(obj instanceof DataEncryptionIndex)) {
                return false;
            }
            DataEncryptionIndex other = (DataEncryptionIndex) obj;
            
            if (!keyAlgorithm.equals(other.keyAlgorithm)) {
                return false;
            } else if (keyLength == null) {
                return other.keyLength == null;
            } else {
                return keyLength.equals(other.keyLength);
            }
            
        }

        /** {@inheritDoc} */
        public int hashCode() {
            int result = 17;
            result = 37*result + keyAlgorithm.hashCode();
            if (keyLength != null) {
                result = 37*result + keyLength.hashCode();
            }
            return result;
        }

        /** {@inheritDoc} */
        public String toString() {
            return String.format("[%s,%s]", keyAlgorithm, keyLength);
        }

    }
    
    /**
     * Class used as an index to the key transport encryption algorithm URI map.
     */
    protected class KeyTransportEncryptionIndex {
        
        /** The JCA key algorithm name. */
        private final String keyAlgorithm;
        
        /** The key length.  Optional, may be null. */
        private final Integer keyLength;
        
        /** The JCA key algorithm name of the key to be encrypted. */
        private final String wrappedAlgorithm;
        
        /**
         * Constructor.
         *
         * @param jcaAlgorithmName the JCA algorithm name
         * @param length the key length (optional, may be null)
         * @param wrappedKeyAlgorithm the JCA algorithm name of the key to be encrypted (optional, may be null)
         */
        protected KeyTransportEncryptionIndex(@Nonnull final String jcaAlgorithmName,
                @Nullable final Integer length, @Nullable final String wrappedKeyAlgorithm) {
            keyAlgorithm = Constraint.isNotNull(StringSupport.trimOrNull(jcaAlgorithmName),
                    "JCA Algorithm name cannot be null or empty");
            keyLength = length;
            wrappedAlgorithm = StringSupport.trimOrNull(wrappedKeyAlgorithm);
        }
        
        /** {@inheritDoc} */
        public boolean equals(Object obj) {
            if(obj == this){
                return true;
            } else if (!(obj instanceof KeyTransportEncryptionIndex)) {
                return false;
            }
            KeyTransportEncryptionIndex other = (KeyTransportEncryptionIndex) obj;
            
            if (!keyAlgorithm.equals(other.keyAlgorithm)) {
                return false;
            }
            
            if (keyLength == null) {
                if (other.keyLength != null) {
                    return false;
                }
            } else {
                if (!keyLength.equals(other.keyLength)) {
                    return false;
                }
            }
            
            if (wrappedAlgorithm == null) {
                return other.wrappedAlgorithm == null;
            } else {
                return wrappedAlgorithm.equals(other.wrappedAlgorithm);
            }
        }

        /** {@inheritDoc} */
        public int hashCode() {
            int result = 17;
            result = 37*result + keyAlgorithm.hashCode();
            if (keyLength != null) {
                result = 37*result + keyLength.hashCode();
            }
            if (wrappedAlgorithm != null) {
                result = 37*result + wrappedAlgorithm.hashCode();
            }
            return result;
        }

        /** {@inheritDoc} */
        public String toString() {
            return String.format("[%s,%s,%s]", keyAlgorithm, keyLength, wrappedAlgorithm);
        }

    }
}