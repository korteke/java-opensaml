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
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.collection.LazySet;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.Resolver;

import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.WhitelistBlacklistConfiguration;
import org.opensaml.xmlsec.WhitelistBlacklistConfiguration.Precedence;
import org.opensaml.xmlsec.WhitelistBlacklistParameters;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * Abstract base class for security parameters resolvers which supplies commonly used functionality for reuse.
 * 
 * @param <ProductType> the type of output produced by the resolver
 */
public abstract class AbstractSecurityParametersResolver<ProductType> 
        implements Resolver<ProductType, CriteriaSet>{
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(AbstractSecurityParametersResolver.class);
    
    /**
     * Resolve a {@link KeyInfoGenerator} instance based on a {@link NamedKeyInfoGeneratorManager}, 
     * {@link Credential} and optional KeyInfo generation profile name.
     * 
     * @param credential the credential for which a KeyInfo generator is needed
     * @param manager the named KeyInfo generator manager instance
     * @param keyInfoProfileName KeyInfo generation profile name
     * 
     * @return the resolved KeyInfo generator instance, or null
     */
    @Nullable protected KeyInfoGenerator lookupKeyInfoGenerator(@Nonnull final Credential credential, 
            @Nullable final NamedKeyInfoGeneratorManager manager, @Nullable String keyInfoProfileName) {
        Constraint.isNotNull(credential, "Credential may not be null");
        
        if (manager == null) {
            return null;
        }
        
        KeyInfoGeneratorFactory factory = null;
        if (keyInfoProfileName != null) {
            factory = manager.getFactory(keyInfoProfileName, credential);
        } else {
            factory = manager.getDefaultManager().getFactory(credential);
        }
        
        if (factory != null) {
            return factory.newInstance();
        }
        
        return null;
    }
    
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
        log.trace("Resolved effective whitelist: {}", whitelist);
        
        Collection<String> blacklist = resolveEffectiveBlacklist(criteria, configs);
        log.trace("Resolved effective blacklist: {}", blacklist);
        
        if (whitelist.isEmpty() && blacklist.isEmpty()) {
            log.trace("Both empty, nothing to populate");
            return;
        }
        
        if (whitelist.isEmpty()) {
            log.trace("Whitelist empty, populating blacklist");
            params.setBlacklistedAlgorithmURIs(blacklist);
            return;
        }
        
        if (blacklist.isEmpty()) {
            log.trace("Blacklist empty, populating whitelist");
            params.setWhitelistedAlgorithmURIs(whitelist);
            return;
        }
        
        WhitelistBlacklistConfiguration.Precedence precedence = resolveWhitelistBlacklistPrecedence(criteria, configs);
        log.trace("Resolved effective precedence: {}", precedence);
        switch(precedence) {
            case WHITELIST:
                log.trace("Based on precedence, populating whitelist");
                params.setWhitelistedAlgorithmURIs(whitelist);
                break;
            case BLACKLIST:
                log.trace("Based on precedence, populating blacklist");
                params.setBlacklistedAlgorithmURIs(blacklist);
                break;
            default:
                throw new IllegalArgumentException("WhitelistBlacklistPrecedence value is unknown: " + precedence);
                    
        }
        
    }
    
    /**
     * Get a predicate which operates according to the effective configured whitelist and blacklist policy.
     * 
     * @param criteria the input criteria being evaluated
     * @param configs the effective list of {@link WhitelistBlacklistConfiguration} instances to consider
     * 
     * @return a predicate instance which operates accordingly to the effective whitelist and blacklist policy
     */
    @Nonnull protected Predicate<String> resolveWhitelistBlacklistPredicate(@Nonnull final CriteriaSet criteria, 
            @Nonnull @NonnullElements @NotEmpty final List<? extends WhitelistBlacklistConfiguration> configs) {
        
        Collection<String> whitelist = resolveEffectiveWhitelist(criteria, configs);
        log.trace("Resolved effective whitelist: {}", whitelist);
        
        Collection<String> blacklist = resolveEffectiveBlacklist(criteria, configs);
        log.trace("Resolved effective blacklist: {}", blacklist);
        
        if (whitelist.isEmpty() && blacklist.isEmpty()) {
            log.trace("Both empty, returning alwaysTrue predicate");
            return Predicates.alwaysTrue();
        }
        
        if (whitelist.isEmpty()) {
            log.trace("Whitelist empty, returning BlacklistPredicate");
            return new BlacklistPredicate(blacklist);
        }
        
        if (blacklist.isEmpty()) {
            log.trace("Blacklist empty, returning WhitelistPredicate");
            return new WhitelistPredicate(whitelist);
        }
        
        WhitelistBlacklistConfiguration.Precedence precedence = resolveWhitelistBlacklistPrecedence(criteria, configs);
        log.trace("Resolved effective precedence: {}", precedence);
        switch(precedence) {
            case WHITELIST:
                log.trace("Based on precedence, returning WhitelistPredicate");
                return new WhitelistPredicate(whitelist);
            case BLACKLIST:
                log.trace("Based on precedence, returning BlacklistPredicate");
                return new BlacklistPredicate(blacklist);
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
            accumulator.addAll(config.getBlacklistedAlgorithmURIs());
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
