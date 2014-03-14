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
import java.util.List;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.collection.LazySet;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.Resolver;

import org.opensaml.xmlsec.WhitelistBlacklistConfiguration;
import org.opensaml.xmlsec.WhitelistBlacklistConfiguration.Precedence;
import org.opensaml.xmlsec.WhitelistBlacklistParameters;

/**
 * Abstract base class for security parameters resolvers which supplies commonly used functionality for reuse.
 * 
 * @param <ProductType> the type of output produced by the resolver
 */
public abstract class AbstractSecurityParametersResolver<ProductType> 
        implements Resolver<ProductType, CriteriaSet>{
    
    /**
     * Resolve and populate the effective whitelist or blacklist on the supplied instance of 
     * {@link WhitelistBlacklistParameters}.
     * 
     * @param params the whitelist/blacklist parameters instance to populate
     * @param criteria the input criteria being evaluated
     * @param configs the effective list of {@link WhitelistBlacklistConfiguration} instances to consider
     */
    protected void resolveAndPopulateWhiteAndBlacklists(@Nonnull final WhitelistBlacklistParameters params, 
            @Nonnull final CriteriaSet criteria, 
            @Nonnull @NonnullElements @NotEmpty final List<? extends WhitelistBlacklistConfiguration> configs) {
        
        Collection<String> whitelist = resolveEffectiveWhitelist(criteria, configs);
        Collection<String> blacklist = resolveEffectiveBlacklist(criteria, configs);
        
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
        
        WhitelistBlacklistConfiguration.Precedence precedence = resolveWhitelistBlacklistPrecedence(criteria, configs);
        switch(precedence) {
            case WHITELIST:
                params.setWhitelistedAlgorithmURIs(whitelist);
                break;
            case BLACKLIST:
                params.setBlacklistedAlgorithmURIs(blacklist);
                break;
            default:
                throw new IllegalArgumentException("WhitelistBlacklistPrecedence value is unknown: " + precedence);
                    
        }
        
    }

    /**
     * Resolve and return the effective algorithm blacklist based on supplied configuration.
     * 
     * @param criteria the input criteria being evaluated
     * @param configs the effective list of {@link WhitelistBlacklistConfiguration} instances to consider
     * 
     * @return the effective algorithm blacklist
     */
    @Nonnull protected Collection<String> resolveEffectiveBlacklist(@Nonnull final CriteriaSet criteria, 
            @Nonnull @NonnullElements @NotEmpty final List<? extends WhitelistBlacklistConfiguration> configs) {
        
        LazySet<String> accumulator = new LazySet<>();
        for (WhitelistBlacklistConfiguration config : configs) {
            accumulator.addAll(config.getBlacklistedAlgorithmsURIs());
            if (!config.isBlacklistMerge()) {
                break;
            }
        }
        return accumulator;
    }

    /**
     * Resolve and return the effective algorithm whitelist based on supplied configuration.
     * 
     * @param criteria the input criteria being evaluated
     * @param configs the effective list of {@link WhitelistBlacklistConfiguration} instances to consider
     * 
     * @return the effective algorithm whitelist
     */
    @Nonnull protected Collection<String> resolveEffectiveWhitelist(@Nonnull final CriteriaSet criteria, 
            @Nonnull @NonnullElements @NotEmpty final List<? extends WhitelistBlacklistConfiguration> configs) {
        
        LazySet<String> accumulator = new LazySet<>();
        for (WhitelistBlacklistConfiguration config : configs) {
            accumulator.addAll(config.getWhitelistedAlgorithmURIs());
            if (!config.isWhitelistMerge()) {
                break;
            }
        }
        return accumulator;
    }

    /**
     * Resolve and return the effective algorithm whitelist/blacklist precedence based 
     * on supplied configuration.
     * 
     * @param criteria the input criteria being evaluated
     * @param configs the effective list of {@link WhitelistBlacklistConfiguration} instances to consider
     * 
     * @return the effective algorithm whitelist/blacklist precedence
     */
    @Nonnull protected Precedence resolveWhitelistBlacklistPrecedence(@Nonnull final CriteriaSet criteria, 
            @Nonnull @NonnullElements @NotEmpty final List<? extends WhitelistBlacklistConfiguration> configs) {
        
        return configs.get(0).getWhitelistBlacklistPrecedence();
    }

}
