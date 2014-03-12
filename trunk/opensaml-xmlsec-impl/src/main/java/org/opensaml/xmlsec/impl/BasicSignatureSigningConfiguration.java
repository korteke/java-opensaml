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

package org.opensaml.xmlsec.impl;

import java.security.Key;
import java.security.interfaces.DSAParams;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.xmlsec.SignatureSigningConfiguration;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic implementation of {@link SignatureSigningConfiguration}.
 */
public class BasicSignatureSigningConfiguration extends BasicWhitelistBlacklistConfiguration 
        implements SignatureSigningConfiguration {
    
    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(BasicSignatureSigningConfiguration.class);
    
    /** Credential to use when signing. */
    private Credential signingCredential;
    
    /** JCA algorithm to signature URI mappings. */
    private final Map<String, String> signatureAlgorithms;
    
    /** Signature canonicalization algorithm URI. */
    private String signatureCanonicalization;
    
    /** Signature Reference digest method algorithm URI. */
    private String signatureReferenceDigestMethod;
    
    /** Signature HMAC output length. */
    private Integer signatureHMACOutputLength;
    
    /** Manager for named KeyInfoGenerator instances. */
    private NamedKeyInfoGeneratorManager keyInfoGeneratorManager;
    
    /** Default DSA key family parameters. */
    private final Map<Integer, DSAParams> dsaParams;
    
    //TODO chaining to parent config instance on getters? or use a wrapping proxy, etc?
    
    //TODO update for modern coding conventions, Guava, etc
    
    /** Constructor. */
    public BasicSignatureSigningConfiguration() {
        super();
        signatureAlgorithms = new HashMap<String, String>();
        dsaParams = new HashMap<Integer, DSAParams>();
    }
    
    /** {@inheritDoc} */
    @Nullable public Credential getSigningCredential() {
        return signingCredential;
    }
    
    /**
     * Set the signing credential to use when signing.
     * 
     * @param credential the signing credential
     */
    @Nullable public void setSigningCredential(@Nullable final Credential credential) {
        signingCredential = credential;
    }
    
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

}