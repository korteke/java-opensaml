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
import java.util.Collections;

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
import org.opensaml.xmlsec.criterion.SignatureSigningConfiguratonCriterion;
import org.opensaml.xmlsec.crypto.AlgorithmSupport;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;

/**
 * Basic implementation of {@link SignatureSigningParametersResolver}.
 * 
 * <p>
 * The following {@link net.shibboleth.utilities.java.support.resolver.Criterion} inputs are supported:
 * <ul>
 * <li>{@link SignatureSigningConfiguratonCriterion} - required</li> 
 * <li>{@link KeyInfoGenerationProfileCriterion} - optional</li> 
 * </ul>
 * </p>
 */
public class BasicSignatureSigningParametersResolver 
        extends AbstractSecurityParametersResolver<SignatureSigningParameters> 
        implements SignatureSigningParametersResolver {

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
        Constraint.isNotNull(criteria.get(SignatureSigningConfiguratonCriterion.class), 
                "Resolver requires an instance of SignatureSigningConfigurationCriterion");
        
        SignatureSigningParameters params = new SignatureSigningParameters();
        
        resolveAndPopulateCredentialAndSigningMethod(params, criteria);
        
        params.setSignatureReferenceDigestMethod(resolveReferenceDigestMethod(criteria));
        
        params.setSignatureCanonicalizationAlgorithm(resolveCanonicalizationAlgorithm(criteria));
        
        params.setKeyInfoGenerator(resolveKeyInfoGenerator(criteria, params.getSigningCredential()));
        
        params.setSignatureHMACOutputLength(resolveHMACOutputLength(criteria, params.getSigningCredential(), 
                params.getSignatureAlgorithmURI()));
        
        params.setDSAParams(resolveDSAParams(criteria, params.getSigningCredential()));
        
        validate(params);
        
        return params;
    }

    /**
     * @param params
     */
    protected void validate(@Nonnull final SignatureSigningParameters params) throws ResolverException {
        // TODO Auto-generated method stub
        
    }

    /**
     * @param params
     * @param criteria
     */
    protected void resolveAndPopulateCredentialAndSigningMethod(@Nonnull final SignatureSigningParameters params, 
            @Nonnull final CriteriaSet criteria) {
        // TODO Auto-generated method stub
        
    }

    /**
     * @param criteria
     * @return
     */
    protected String resolveReferenceDigestMethod(@Nonnull final CriteriaSet criteria) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param criteria
     * @return
     */
    protected String resolveCanonicalizationAlgorithm(@Nonnull final CriteriaSet criteria) {
        for (SignatureSigningConfiguration config : criteria.get(SignatureSigningConfiguratonCriterion.class)
                .getConfigurations()) {
            
            if (config.getSignatureCanonicalizationAlgorithm() != null) {
                return config.getSignatureCanonicalizationAlgorithm();
            }
            
        }
        return null;
    }

    /**
     * @param criteria
     * @param signingCredential
     * @return
     */
    protected KeyInfoGenerator resolveKeyInfoGenerator(@Nonnull final CriteriaSet criteria, 
            @Nonnull final Credential signingCredential) {
        
        String name = null;
        if (criteria.get(KeyInfoGenerationProfileCriterion.class) != null) {
            name = criteria.get(KeyInfoGenerationProfileCriterion.class).getName();
        }
        
        for (SignatureSigningConfiguration config : criteria.get(SignatureSigningConfiguratonCriterion.class)
                .getConfigurations()) {
            
            NamedKeyInfoGeneratorManager manager = config.getKeyInfoGeneratorManager();
            if (manager != null) {
                KeyInfoGeneratorFactory factory = null;
                if (name != null) {
                    factory = manager.getFactory(name, signingCredential);
                } else {
                    factory = manager.getDefaultManager().getFactory(signingCredential);
                }
                
                if (factory != null) {
                    return factory.newInstance();
                }
            }
            
        }
        
        return null;
    }

    /**
     * @param criteria
     * @param signingCredential
     * @param string 
     * @return
     */
    protected Integer resolveHMACOutputLength(@Nonnull final CriteriaSet criteria, 
            @Nonnull final Credential signingCredential, @Nonnull @NotEmpty final String algorithmURI) {
        
        if (AlgorithmSupport.isHMAC(algorithmURI)) {
            for (SignatureSigningConfiguration config : criteria.get(SignatureSigningConfiguratonCriterion.class)
                    .getConfigurations()) {
                if (config.getSignatureHMACOutputLength() != null) {
                    return config.getSignatureHMACOutputLength();
                }
            }
        }
        return null;
    }

    /**
     * @param criteria
     * @param credential 
     * @return
     */
    protected DSAParams resolveDSAParams(@Nonnull final CriteriaSet criteria, @Nonnull final Credential credential) {
        if (credential.getPublicKey() != null && "DSA".equals(credential.getPublicKey().getAlgorithm())) {
            //TODO this KeySupport method currently doesn't support DSA keys. Need to investigate.
            Integer keyLength = KeySupport.getKeyLength(credential.getPublicKey());
            if (keyLength != null) {
                for (SignatureSigningConfiguration config : criteria.get(SignatureSigningConfiguratonCriterion.class)
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
