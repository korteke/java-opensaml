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

package org.opensaml.saml.security.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.collection.Pair;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.xmlsec.EncryptionConfiguration;
import org.opensaml.xmlsec.EncryptionParameters;
import org.opensaml.xmlsec.EncryptionParametersResolver;
import org.opensaml.xmlsec.SecurityConfigurationSupport;
import org.opensaml.xmlsec.criterion.EncryptionConfigurationCriterion;
import org.opensaml.xmlsec.impl.BasicEncryptionConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * Strategy function for resolving {@link EncryptionParameters} used to encrypt to oneself.
 */
public class InlineSelfEncryptionParametersStrategy 
        implements Function<Pair<ProfileRequestContext, EncryptionParameters>, List<EncryptionParameters>> {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(InlineSelfEncryptionParametersStrategy.class);
    
    /** Credential resolver for self-encryption. */
    @Nonnull private CredentialResolver credentialResolver;
    
    /** Encryption parameters resolver for self-encryption. */
    @Nonnull private EncryptionParametersResolver encParamsresolver;
    
    /** Strategy function for resolving the list of effective base encryption configurations to use. */
    @Nullable private Function<ProfileRequestContext,List<EncryptionConfiguration>> configurationLookupStrategy;
    
    /**
     * Constructor.
     * 
     * @param credResolver resolver for self-encryption credentials
     * @param paramsResolver resolver for self-encryption parameters
     */
    public InlineSelfEncryptionParametersStrategy(@Nonnull final CredentialResolver credResolver, 
            @Nonnull final EncryptionParametersResolver paramsResolver) {
        this(credResolver, paramsResolver, null);
    }
    
    /**
     * Constructor.
     *
     * @param credResolver resolver for self-encryption credentials
     * @param paramsResolver resolver for self-encryption parameters
     * @param configStrategy strategy for resolving the list of effective base encryption configurations
     */
    public InlineSelfEncryptionParametersStrategy(@Nonnull final CredentialResolver credResolver, 
            @Nonnull final EncryptionParametersResolver paramsResolver,
            @Nullable final Function<ProfileRequestContext,List<EncryptionConfiguration>> configStrategy) {
        credentialResolver = Constraint.isNotNull(credResolver, "CredentialResolver was null");
        encParamsresolver = Constraint.isNotNull(paramsResolver, "EncryptionParametersResolver was null");
        configurationLookupStrategy = configStrategy;
    }

    /** {@inheritDoc} */
    @Nullable
    public List<EncryptionParameters> apply(@Nullable Pair<ProfileRequestContext, EncryptionParameters> input) {
        if (input == null || input.getFirst() == null) {
            log.debug("Input Pair or ProfileRequestContext was null, skipping");
            return Collections.emptyList();
        }
        
        List<Credential> credentials = resolveCredentials(input.getFirst());
        if (credentials.isEmpty()) {
            log.debug("No self-encryption credentials were resolved, skipping further processing");
            return Collections.emptyList();
        }
        log.debug("Resolved {} self-encryption credentials", credentials.size());
        
        List<EncryptionConfiguration> baseConfigs = resolveBaseConfigurations(input.getFirst());
        log.debug("Resolved {} base EncryptionConfigurations", baseConfigs.size());
        
        ArrayList<EncryptionParameters> encParams = new ArrayList<>();
        
        for (Credential cred : credentials) {
            BasicEncryptionConfiguration selfConfig = new BasicEncryptionConfiguration();
            selfConfig.setKeyTransportEncryptionCredentials(Collections.singletonList(cred));
            if (input.getSecond() != null && input.getSecond().getDataEncryptionAlgorithm() != null) {
                selfConfig.setDataEncryptionAlgorithms(Collections.singletonList(
                        input.getSecond().getDataEncryptionAlgorithm()));
            }
            
            ArrayList<EncryptionConfiguration> configs = new ArrayList<>();
            configs.add(selfConfig);
            configs.addAll(baseConfigs);
            
            try {
                Iterables.addAll(encParams, encParamsresolver.resolve(
                        new CriteriaSet(new EncryptionConfigurationCriterion(configs))));
            } catch (ResolverException e) {
                log.error("Error resolving self-encryption parameters for Credential '{}', " 
                        + "params from other Credentials may still succeed", cred, e);
            }
        }
        
        log.debug("Resolved {} self-encryption EncryptionParameters", encParams.size());
        
        return encParams;
    }
    
    /**
     * Resolve the list of self-encryption credentials.
     * 
     * @param profileRequestContext the current profile request context
     * 
     * @return the resolved credentials
     */
    @Nonnull protected List<Credential> resolveCredentials(
            @Nonnull final ProfileRequestContext profileRequestContext) {
        try {
            ArrayList<Credential> credentials = new ArrayList<>();
            Iterables.addAll(credentials, credentialResolver.resolve(
                    new CriteriaSet(new UsageCriterion(UsageType.ENCRYPTION))));
            return credentials;
        } catch (ResolverException e) {
            log.error("Error resolving IdP encryption credentials", e);
            return Collections.emptyList();
        }
    }

    /**
     * Resolve the list of effective base {@link EncryptionConfiguration} for self-encryption.
     * 
     * @param profileRequestContext the current profile request context
     * 
     * @return the resolved configurations
     */
    @Nonnull protected List<EncryptionConfiguration> resolveBaseConfigurations(
            @Nonnull final ProfileRequestContext profileRequestContext) {
        List<EncryptionConfiguration> baseConfigs = null;
        if (configurationLookupStrategy != null) {
            log.debug("Self-encryption EncryptionConfiguration lookup strategy was non-null");
            baseConfigs = configurationLookupStrategy.apply(profileRequestContext);
        } else {
            log.debug("Self-encryption EncryptionConfiguration lookup strategy was null");
        }
        if (baseConfigs != null) {
            return baseConfigs;
        } else {
            log.debug("No self-encryption EncryptionConfigurations were resolved, returning global configuration");
            return Collections.singletonList(SecurityConfigurationSupport.getGlobalEncryptionConfiguration());
        }
    }
    
}
