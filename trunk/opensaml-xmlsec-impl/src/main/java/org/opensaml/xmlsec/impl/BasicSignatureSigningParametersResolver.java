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

import java.security.interfaces.DSAParams;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.security.credential.Credential;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.SignatureSigningConfiguration;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.SignatureSigningParametersResolver;
import org.opensaml.xmlsec.criterion.KeyInfoGenerationProfileCriterion;
import org.opensaml.xmlsec.criterion.SignatureSigningConfigurationCriterion;
import org.opensaml.xmlsec.crypto.AlgorithmSupport;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * Basic implementation of {@link SignatureSigningParametersResolver}.
 * 
 * <p>
 * The following {@link net.shibboleth.utilities.java.support.resolver.Criterion} inputs are supported:
 * <ul>
 * <li>{@link SignatureSigningConfigurationCriterion} - required</li> 
 * <li>{@link KeyInfoGenerationProfileCriterion} - optional</li> 
 * </ul>
 * </p>
 */
public class BasicSignatureSigningParametersResolver 
        extends AbstractSecurityParametersResolver<SignatureSigningParameters> 
        implements SignatureSigningParametersResolver {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(BasicSignatureSigningParametersResolver.class);

    /** {@inheritDoc} */
    @Nonnull
    public Iterable<SignatureSigningParameters> resolve(@Nonnull final CriteriaSet criteria) throws ResolverException {
        SignatureSigningParameters params = resolveSingle(criteria);
        if (params != null) {
            return Collections.singletonList(params);
        } else {
            return Collections.emptyList();
        }
    }

    /** {@inheritDoc} */
    @Nullable
    public SignatureSigningParameters resolveSingle(@Nonnull final CriteriaSet criteria) throws ResolverException {
        Constraint.isNotNull(criteria, "CriteriaSet was null");
        Constraint.isNotNull(criteria.get(SignatureSigningConfigurationCriterion.class), 
                "Resolver requires an instance of SignatureSigningConfigurationCriterion");
        
        Predicate<String> whitelistBlacklistPredicate = getWhitelistBlacklistPredicate(criteria);
        
        SignatureSigningParameters params = new SignatureSigningParameters();
        
        resolveAndPopulateCredentialAndSignatureAlgorithm(params, criteria, whitelistBlacklistPredicate);
        
        params.setSignatureReferenceDigestMethod(resolveReferenceDigestMethod(criteria, whitelistBlacklistPredicate));
        
        params.setSignatureCanonicalizationAlgorithm(resolveCanonicalizationAlgorithm(criteria));
        
        if (params.getSigningCredential() != null) {
            params.setKeyInfoGenerator(resolveKeyInfoGenerator(criteria, params.getSigningCredential()));
            params.setSignatureHMACOutputLength(resolveHMACOutputLength(criteria, params.getSigningCredential(), 
                    params.getSignatureAlgorithmURI()));
            params.setDSAParams(resolveDSAParams(criteria, params.getSigningCredential()));
        }
        
        validate(params);
        
        return params;
    }
    
    /**
     * Validate that the {@link SignatureSigningParameters} instance has all the required properties populated.
     * 
     * @param params the parameters instance to evaluate
     * @throws ResolverException if params instance is not populated with all required data
     */
    protected void validate(@Nonnull final SignatureSigningParameters params) throws ResolverException {
        // TODO Auto-generated method stub
        
    }

    /**
     * Get a predicate which implements the effective configured whitelist/blacklist policy.
     * 
     * @param criteria the input criteria being evaluated
     * 
     * @return a whitelist/blacklist predicate instance
     */
    @Nonnull protected Predicate<String> getWhitelistBlacklistPredicate(@Nonnull final CriteriaSet criteria) {
        return resolveWhitelistBlacklistPredicate(criteria, 
                criteria.get(SignatureSigningConfigurationCriterion.class).getConfigurations());
    }

    /**
     * Resolve and populate the signing credential and signature method algorithm URI on the 
     * supplied parameters instance.
     * 
     * @param params the parameters instance being populated
     * @param criteria the input criteria being evaluated
     * @param whitelistBlacklistPredicate  the whitelist/blacklist predicate with which to evaluate the 
     *          candidate signing method algorithm URIs
     */
    protected void resolveAndPopulateCredentialAndSignatureAlgorithm(@Nonnull final SignatureSigningParameters params, 
            @Nonnull final CriteriaSet criteria, Predicate<String> whitelistBlacklistPredicate) {
        
        List<Credential> credentials = getEffectiveSigningCredentials(criteria);
        List<String> algorithms = getEffectiveSignatureAlgorithms(criteria, whitelistBlacklistPredicate);
        
        for (Credential credential : credentials) {
            for (String algorithm : algorithms) {
                if (credentialSupportsAlgorithm(credential, algorithm)) {
                    params.setSigningCredential(credential);
                    params.setSignatureAlgorithmURI(algorithm);
                    return;
                }
            }
        }
        
    }

    /**
     * Evaluate whether the specified credential is supported for use with the specified algorithm URI.
     * 
     * @param credential the credential to evaluate
     * @param algorithm the algorithm URI to evaluate
     * @return true if credential may be used with the supplied algorithm URI, false otherwise
     */
    protected boolean credentialSupportsAlgorithm(@Nonnull final Credential credential, 
            @Nonnull @NotEmpty final String algorithm) {
        
        // TODO consult AlgorithmRegistry, etc
        
        return false;
    }

    /**
     * Get the effective list of signing credentials to consider.
     * 
     * @param criteria the input criteria being evaluated
     * @return the list of credentials
     */
    @Nonnull protected List<Credential> getEffectiveSigningCredentials(@Nonnull final CriteriaSet criteria) {
        ArrayList<Credential> accumulator = new ArrayList<>();
        for (SignatureSigningConfiguration config : criteria.get(SignatureSigningConfigurationCriterion.class)
                .getConfigurations()) {
            
            accumulator.addAll(config.getSigningCredentials());
            
        }
        return accumulator;
    }
    
    /**
     * Get the effective list of signature algorithm URIs to consider, including application of 
     * whitelist/blacklist policy.
     * 
     * @param criteria the input criteria being evaluated
     * @param whitelistBlacklistPredicate  the whitelist/blacklist predicate to use
     * @return the list of effective algorithm URIs
     */
    @Nonnull protected List<String> getEffectiveSignatureAlgorithms(@Nonnull final CriteriaSet criteria, 
            @Nonnull final Predicate<String> whitelistBlacklistPredicate) {
        ArrayList<String> accumulator = new ArrayList<>();
        for (SignatureSigningConfiguration config : criteria.get(SignatureSigningConfigurationCriterion.class)
                .getConfigurations()) {
            
            accumulator.addAll(Collections2.filter(config.getSignatureAlgorithmURIs(), whitelistBlacklistPredicate));
            
        }
        return accumulator;
    }

    /**
     * Resolve and return the digest method algorithm URI to use, including application of whitelist/blacklist policy.
     * 
     * @param criteria the input criteria being evaluated
     * @param whitelistBlacklistPredicate  the whitelist/blacklist predicate to use
     * @return the resolved digest method algorithm URI
     */
    @Nullable protected String resolveReferenceDigestMethod(@Nonnull final CriteriaSet criteria, 
            @Nonnull final Predicate<String> whitelistBlacklistPredicate) {
        for (SignatureSigningConfiguration config : criteria.get(SignatureSigningConfigurationCriterion.class)
                .getConfigurations()) {
            
            for (String digestMethod : config.getSignatureReferenceDigestMethods()) {
                if (whitelistBlacklistPredicate.apply(digestMethod)) {
                    return digestMethod;
                }
            }
            
        }
        return null;
    }

    /**
     * Resolve and return the canonicalization algorithm URI to use.
     * 
     * @param criteria the input criteria being evaluated
     * @return the canonicalization algorithm URI
     */
    @Nullable protected String resolveCanonicalizationAlgorithm(@Nonnull final CriteriaSet criteria) {
        for (SignatureSigningConfiguration config : criteria.get(SignatureSigningConfigurationCriterion.class)
                .getConfigurations()) {
            
            if (config.getSignatureCanonicalizationAlgorithm() != null) {
                return config.getSignatureCanonicalizationAlgorithm();
            }
            
        }
        return null;
    }

    /**
     * Resolve and return the {@link KeyInfoGenerator} instance to use with the specified credential.
     * 
     * @param criteria the input criteria being evaluated
     * @param signingCredential the credential being evaluated
     * @return KeyInfo generator instance, or null
     */
    @Nullable protected KeyInfoGenerator resolveKeyInfoGenerator(@Nonnull final CriteriaSet criteria, 
            @Nonnull final Credential signingCredential) {
        
        String name = null;
        if (criteria.get(KeyInfoGenerationProfileCriterion.class) != null) {
            name = criteria.get(KeyInfoGenerationProfileCriterion.class).getName();
        }
        
        for (SignatureSigningConfiguration config : criteria.get(SignatureSigningConfigurationCriterion.class)
                .getConfigurations()) {
            
            KeyInfoGenerator kig = lookupKeyInfoGenerator(signingCredential, config.getKeyInfoGeneratorManager(), name);
            if (kig != null) {
                return kig;
            }
            
        }
        
        return null;
    }

    /**
     * Resolve and return the effective HMAC output length to use, if applicable to the specified signing credential
     * and signature method algorithm URI.
     * 
     * @param criteria the input criteria being evaluated
     * @param signingCredential the signing credential being evaluated
     * @param algorithmURI the signature method algorithm URI being evaluated
     * @return the HMAC output length to use, or null
     */
    @Nullable protected Integer resolveHMACOutputLength(@Nonnull final CriteriaSet criteria, 
            @Nonnull final Credential signingCredential, @Nonnull @NotEmpty final String algorithmURI) {
        
        if (AlgorithmSupport.isHMAC(algorithmURI)) {
            for (SignatureSigningConfiguration config : criteria.get(SignatureSigningConfigurationCriterion.class)
                    .getConfigurations()) {
                if (config.getSignatureHMACOutputLength() != null) {
                    return config.getSignatureHMACOutputLength();
                }
            }
        }
        return null;
    }

    /**
     * Resolve and return the DSAParams instance to use, if applicable.  Only effective for DSA signing credentials.
     * 
     * @param criteria the input criteria being evaluated
     * @param credential  the credential being evaluated
     * @return the DSAParams instance, or null
     */
    @Nullable protected DSAParams resolveDSAParams(@Nonnull final CriteriaSet criteria, 
            @Nonnull final Credential credential) {
        if (credential.getPublicKey() != null && "DSA".equals(credential.getPublicKey().getAlgorithm())) {
            Integer keyLength = KeySupport.getKeyLength(credential.getPublicKey());
            if (keyLength != null) {
                for (SignatureSigningConfiguration config : criteria.get(SignatureSigningConfigurationCriterion.class)
                        .getConfigurations()) {
                    if (config.getDSAParams(keyLength) != null) {
                        return config.getDSAParams(keyLength);
                    }
                }
            }
            
        }
        return null;
    }

}
