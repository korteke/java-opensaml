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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.xmlsec.DecryptionConfiguration;
import org.opensaml.xmlsec.DecryptionParameters;
import org.opensaml.xmlsec.DecryptionParametersResolver;
import org.opensaml.xmlsec.WhitelistBlacklistConfiguration;
import org.opensaml.xmlsec.WhitelistBlacklistConfiguration.Precedence;
import org.opensaml.xmlsec.criterion.DecryptionConfiguratonCriterion;
import org.opensaml.xmlsec.encryption.support.EncryptedKeyResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;

/**
 *
 */
public class BasicDecryptionParametersResolver implements DecryptionParametersResolver {

    /** {@inheritDoc} */
    @Nonnull public Iterable<DecryptionParameters> resolve(CriteriaSet criteria) throws ResolverException {
        DecryptionParameters params = resolveSingle(criteria);
        if (params != null) {
            return Collections.singletonList(params);
        } else {
            return Collections.emptyList();
        }
    }

    /** {@inheritDoc} */
    @Nullable public DecryptionParameters resolveSingle(CriteriaSet criteria) throws ResolverException {
        Constraint.isNotNull(criteria.get(DecryptionConfiguratonCriterion.class), 
                "Resolver requires an instance of DecryptionConfigurationCriterion");
        
        DecryptionParameters params = new DecryptionParameters();
        
        resolveWhiteAndBlacklists(params, criteria);
        
        params.setDataKeyInfoCredentialResolver(resolveDataKeyInfoCredentialResolver(criteria));
        params.setKEKKeyInfoCredentialResolver(resolveKEKKeyInfoCredentialResolver(criteria));
        params.setEncryptedKeyResolver(resolveEncryptedKeyResolver(criteria));
        
        return params;
    }

    /**
     * @param criteria
     * @return
     */
    protected EncryptedKeyResolver resolveEncryptedKeyResolver(CriteriaSet criteria) {
        for (DecryptionConfiguration config : criteria.get(DecryptionConfiguratonCriterion.class).getConfigurations()) {
            if (config.getEncryptedKeyResolver() != null) {
                return config.getEncryptedKeyResolver();
            }
        }
        return null;
    }

    /**
     * @param criteria
     * @return
     */
    protected KeyInfoCredentialResolver resolveKEKKeyInfoCredentialResolver(CriteriaSet criteria) {
        for (DecryptionConfiguration config : criteria.get(DecryptionConfiguratonCriterion.class).getConfigurations()) {
            if (config.getKEKKeyInfoCredentialResolver() != null) {
                return config.getKEKKeyInfoCredentialResolver();
            }
        }
        return null;
    }

    /**
     * @param criteria
     * @return
     */
    protected KeyInfoCredentialResolver resolveDataKeyInfoCredentialResolver(CriteriaSet criteria) {
        for (DecryptionConfiguration config : criteria.get(DecryptionConfiguratonCriterion.class).getConfigurations()) {
            if (config.getDataKeyInfoCredentialResolver() != null) {
                return config.getDataKeyInfoCredentialResolver();
            }
        }
        return null;
    }

    /**
     * @param params
     * @param criteria
     */
    protected void resolveWhiteAndBlacklists(DecryptionParameters params, CriteriaSet criteria) {
        Collection<String> whitelist = resolveEffectiveWhitelist(criteria);
        Collection<String> blacklist = resolveEffectiveBlacklist(criteria);
        
        if (whitelist.isEmpty() && blacklist.isEmpty()) {
            return;
        }
        
        if (whitelist.isEmpty()) {
            params.setBlacklistedAlgorithmURIs(blacklist);
            return;
        }
        
        if (blacklist.isEmpty()) {
            params.setWhitelistedAlgorithmURIs(whitelist);
            return;
        }
        
        WhitelistBlacklistConfiguration.Precedence precedence = resolveWhitelistBlacklistPrecedence(criteria);
        switch(precedence) {
            case WHITELIST:
                params.setWhitelistedAlgorithmURIs(whitelist);
                break;
            case BLACKLIST:
                params.setBlacklistedAlgorithmURIs(blacklist);
                break;
        }
        
    }

    /**
     * @param criteria
     * @return
     */
    protected Precedence resolveWhitelistBlacklistPrecedence(CriteriaSet criteria) {
        return criteria.get(DecryptionConfiguratonCriterion.class)
                .getConfigurations().get(0).getWhitelistBlacklistPrecedence();
    }

    /**
     * @param criteria
     * @return
     */
    protected Collection<String> resolveEffectiveBlacklist(CriteriaSet criteria) {
        HashSet<String> accumulator = new HashSet<>();
        for (DecryptionConfiguration config : criteria.get(DecryptionConfiguratonCriterion.class).getConfigurations()) {
            accumulator.addAll(config.getBlacklistedAlgorithmsURIs());
            if (!config.isBlacklistMerge()) {
                break;
            }
        }
        return accumulator;
    }

    /**
     * @param criteria
     * @return
     */
    protected Collection<String> resolveEffectiveWhitelist(CriteriaSet criteria) {
        HashSet<String> accumulator = new HashSet<>();
        for (DecryptionConfiguration config : criteria.get(DecryptionConfiguratonCriterion.class).getConfigurations()) {
            accumulator.addAll(config.getWhitelistedAlgorithmURIs());
            if (!config.isWhitelistMerge()) {
                break;
            }
        }
        return accumulator;
    }

}
