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

import org.opensaml.xmlsec.SignatureValidationConfiguration;
import org.opensaml.xmlsec.SignatureValidationParameters;
import org.opensaml.xmlsec.SignatureValidationParametersResolver;
import org.opensaml.xmlsec.criterion.SignatureValidationConfiguratonCriterion;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;

/**
 * Basic implementation of {@link SignatureValidationParametersResolver}.
 * 
 * <p>
 * The following {@link net.shibboleth.utilities.java.support.resolver.Criterion} inputs are supported:
 * <ul>
 * <li>{@link SignatureValidationConfiguratonCriterion} - required</li> 
 * </ul>
 * </p>
 */
public class BasicSignatureValidationParametersResolver 
        extends AbstractSecurityParametersResolver<SignatureValidationParameters> 
        implements SignatureValidationParametersResolver {

    /** {@inheritDoc} */
    @Nonnull public Iterable<SignatureValidationParameters> resolve(@Nonnull final CriteriaSet criteria) 
            throws ResolverException {
        
        SignatureValidationParameters params = resolveSingle(criteria);
        if (params != null) {
            return Collections.singletonList(params);
        } else {
            return Collections.emptyList();
        }
    }

    /** {@inheritDoc} */
    @Nullable
    public SignatureValidationParameters resolveSingle(@Nonnull final CriteriaSet criteria) throws ResolverException {
        Constraint.isNotNull(criteria, "CriteriaSet was null");
        Constraint.isNotNull(criteria.get(SignatureValidationConfiguratonCriterion.class), 
                "Resolver requires an instance of SignatureValidationConfigurationCriterion");
        
        SignatureValidationParameters params = new SignatureValidationParameters();
        
        resolveAndPopulateWhiteAndBlacklists(params, criteria, 
                criteria.get(SignatureValidationConfiguratonCriterion.class).getConfigurations());
        
        params.setSignatureTrustEngine(resolveSignatureTrustEngine(criteria));
        
        return params;
    }

    /**
     * Resolve and return the effective {@link SignatureTrustEngine}.
     * 
     * @param criteria the input criteria being evaluated
     * 
     * @return the effective resolver, or null
     */
    @Nullable protected SignatureTrustEngine resolveSignatureTrustEngine(@Nonnull final CriteriaSet criteria) {
        
        for (SignatureValidationConfiguration config : criteria.get(SignatureValidationConfiguratonCriterion.class)
                .getConfigurations()) {
            if (config.getSignatureTrustEngine() != null) {
                return config.getSignatureTrustEngine();
            }
        }
        return null;
    }

}
