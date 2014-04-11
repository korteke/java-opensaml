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

import java.util.Collections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.xmlsec.DecryptionConfiguration;
import org.opensaml.xmlsec.DecryptionParameters;
import org.opensaml.xmlsec.DecryptionParametersResolver;
import org.opensaml.xmlsec.criterion.DecryptionConfigurationCriterion;
import org.opensaml.xmlsec.encryption.support.EncryptedKeyResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;

/**
 * Basic implementation of {@link DecryptionParametersResolver}.
 * 
 * <p>
 * The following {@link net.shibboleth.utilities.java.support.resolver.Criterion} inputs are supported:
 * <ul>
 * <li>{@link DecryptionConfigurationCriterion} - required</li> 
 * </ul>
 * </p>
 */
public class BasicDecryptionParametersResolver extends AbstractSecurityParametersResolver<DecryptionParameters> 
        implements DecryptionParametersResolver {

    /** {@inheritDoc} */
    @Nonnull public Iterable<DecryptionParameters> resolve(@Nonnull final CriteriaSet criteria) 
            throws ResolverException {
        
        DecryptionParameters params = resolveSingle(criteria);
        if (params != null) {
            return Collections.singletonList(params);
        } else {
            return Collections.emptyList();
        }
    }

    /** {@inheritDoc} */
    @Nullable public DecryptionParameters resolveSingle(@Nonnull final CriteriaSet criteria) throws ResolverException {
        Constraint.isNotNull(criteria, "CriteriaSet was null");
        Constraint.isNotNull(criteria.get(DecryptionConfigurationCriterion.class), 
                "Resolver requires an instance of DecryptionConfigurationCriterion");
        
        DecryptionParameters params = new DecryptionParameters();
        
        resolveAndPopulateWhiteAndBlacklists(params, criteria, 
                criteria.get(DecryptionConfigurationCriterion.class).getConfigurations());
        
        params.setDataKeyInfoCredentialResolver(resolveDataKeyInfoCredentialResolver(criteria));
        params.setKEKKeyInfoCredentialResolver(resolveKEKKeyInfoCredentialResolver(criteria));
        params.setEncryptedKeyResolver(resolveEncryptedKeyResolver(criteria));
        
        return params;
    }

    /**
     * Resolve and return the effective {@link EncryptedKeyResolver}.
     * 
     * @param criteria the input criteria being evaluated
     * 
     * @return the effective resolver, or null
     */
    @Nullable protected EncryptedKeyResolver resolveEncryptedKeyResolver(@Nonnull final CriteriaSet criteria) {
        
        for (DecryptionConfiguration config : criteria.get(DecryptionConfigurationCriterion.class)
                .getConfigurations()) {
            if (config.getEncryptedKeyResolver() != null) {
                return config.getEncryptedKeyResolver();
            }
        }
        return null;
    }

    /**
     * Resolve and return the effective {@link KeyInfoCredentialResolver} used with 
     * {@link org.opensaml.xmlsec.encryption.EncryptedKey} instances.
     * 
     * @param criteria the input criteria being evaluated
     * @return the effective resolver, or null
     */
    @Nullable protected KeyInfoCredentialResolver resolveKEKKeyInfoCredentialResolver(
            @Nonnull final CriteriaSet criteria) {
        
        for (DecryptionConfiguration config : criteria.get(DecryptionConfigurationCriterion.class)
                .getConfigurations()) {
            if (config.getKEKKeyInfoCredentialResolver() != null) {
                return config.getKEKKeyInfoCredentialResolver();
            }
        }
        return null;
    }

    /**
     * Resolve and return the effective {@link KeyInfoCredentialResolver} used with 
     * {@link org.opensaml.xmlsec.encryption.EncryptedData} instances.
     * 
     * @param criteria the input criteria being evaluated
     * @return the effective resolver, or null
     */
    @Nullable protected KeyInfoCredentialResolver resolveDataKeyInfoCredentialResolver(
            @Nonnull final CriteriaSet criteria) {
        
        for (DecryptionConfiguration config : criteria.get(DecryptionConfigurationCriterion.class)
                .getConfigurations()) {
            if (config.getDataKeyInfoCredentialResolver() != null) {
                return config.getDataKeyInfoCredentialResolver();
            }
        }
        return null;
    }



}
