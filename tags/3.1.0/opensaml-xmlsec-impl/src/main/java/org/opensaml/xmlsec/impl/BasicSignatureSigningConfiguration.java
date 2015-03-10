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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotLive;
import net.shibboleth.utilities.java.support.annotation.constraint.Unmodifiable;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.SignatureSigningConfiguration;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

/**
 * Basic implementation of {@link SignatureSigningConfiguration}.
 */
public class BasicSignatureSigningConfiguration extends BasicWhitelistBlacklistConfiguration 
        implements SignatureSigningConfiguration {
    
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(BasicSignatureSigningConfiguration.class);
    
    /** Signing credentials. */
    @Nonnull @NonnullElements private List<Credential> signingCredentials;
    
    /** Signature method algorithm URIs. */
    @Nonnull @NonnullElements private List<String> signatureAlgorithms;
    
    /** Digest method algorithm URIs. */
    @Nonnull @NonnullElements private List<String> signatureReferenceDigestMethods;
    
    /** Signature canonicalization algorithm URI. */
    @Nullable private String signatureCanonicalization;
    
    /** Signature HMAC output length. */
    @Nullable private Integer signatureHMACOutputLength;
    
    /** Manager for named KeyInfoGenerator instances. */
    @Nullable private NamedKeyInfoGeneratorManager keyInfoGeneratorManager;
    
    //TODO chaining to parent config instance on getters? or use a wrapping proxy, etc?
    
    //TODO update for modern coding conventions, Guava, etc
    
    /** Constructor. */
    public BasicSignatureSigningConfiguration() {
        signingCredentials = Collections.emptyList();
        signatureAlgorithms = Collections.emptyList();
        signatureReferenceDigestMethods = Collections.emptyList();
    }
    
    /** {@inheritDoc} */
    @Override
    @Nonnull @NonnullElements @Unmodifiable @NotLive public List<Credential> getSigningCredentials() {
        return ImmutableList.copyOf(signingCredentials);
    }
    
    /**
     * Set the signing credentials to use when signing.
     * 
     * @param credentials the list of signing credentials
     */
    public void setSigningCredentials(@Nullable final List<Credential> credentials) {
        if (credentials == null) {
            signingCredentials = Collections.emptyList();
            return;
        }
        signingCredentials = new ArrayList<>(Collections2.filter(credentials, Predicates.notNull()));
    }
    
    /** {@inheritDoc} */
    @Override
    @Nonnull @NonnullElements @Unmodifiable @NotLive public List<String> getSignatureAlgorithms() {
        return ImmutableList.copyOf(signatureAlgorithms);
    }
    
    /**
     * Set the signature algorithms to use when signing.
     * 
     * @param algorithms the list of signature algorithms
     */
    public void setSignatureAlgorithms(@Nullable final List<String> algorithms) {
        if (algorithms == null) {
            signatureAlgorithms = Collections.emptyList();
            return;
        }
        signatureAlgorithms = new ArrayList<>(StringSupport.normalizeStringCollection(algorithms));
    }
    
    /** {@inheritDoc} */
    @Override
    @Nonnull @NonnullElements @Unmodifiable @NotLive public List<String> getSignatureReferenceDigestMethods() {
        return ImmutableList.copyOf(signatureReferenceDigestMethods);
    }
    
    /**
     * Set a digest method algorithm URI suitable for use as a Signature Reference DigestMethod value.
     * 
     * @param algorithms a list of digest method algorithm URIs
     */
    public void setSignatureReferenceDigestMethods(@Nullable final List<String> algorithms) {
        if (algorithms == null) {
            signatureReferenceDigestMethods = Collections.emptyList();
            return;
        }
        signatureReferenceDigestMethods = new ArrayList<>(StringSupport.normalizeStringCollection(algorithms));
    }

    /** {@inheritDoc} */
    @Override
    @Nullable public String getSignatureCanonicalizationAlgorithm() {
        return signatureCanonicalization;
    }
    
    /**
     * Set a canonicalization algorithm URI suitable for use as a Signature CanonicalizationMethod value.
     * 
     * @param algorithmURI a canonicalization algorithm URI
     */
    public void setSignatureCanonicalizationAlgorithm(@Nullable final String algorithmURI) {
        signatureCanonicalization = StringSupport.trimOrNull(algorithmURI);
    }

    /** {@inheritDoc} */
    @Override
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
    @Override
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

}