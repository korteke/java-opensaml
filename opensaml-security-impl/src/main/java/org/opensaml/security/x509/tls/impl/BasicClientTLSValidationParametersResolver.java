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

package org.opensaml.security.x509.tls.impl;

import java.util.Collections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.security.trust.TrustEngine;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.security.x509.tls.CertificateNameOptions;
import org.opensaml.security.x509.tls.ClientTLSValidationConfiguration;
import org.opensaml.security.x509.tls.ClientTLSValidationConfigurationCriterion;
import org.opensaml.security.x509.tls.ClientTLSValidationParameters;
import org.opensaml.security.x509.tls.ClientTLSValidationParametersResolver;

/**
 * Basic implementation of {@link ClientTLSValidationParametersResolver}.
 * 
 * <p>
 * The following {@link net.shibboleth.utilities.java.support.resolver.Criterion} inputs are supported:
 * <ul>
 * <li>{@link ClientTLSValidationConfigurationCriterion} - required</li> 
 * </ul>
 * </p>
 */
public class BasicClientTLSValidationParametersResolver implements ClientTLSValidationParametersResolver {

    /** {@inheritDoc} */
    @Nonnull @NonnullElements public Iterable<ClientTLSValidationParameters> resolve(CriteriaSet criteria) 
            throws ResolverException {
        ClientTLSValidationParameters params = resolveSingle(criteria);
        if (params != null) {
            return Collections.singletonList(params);
        } else {
            return Collections.emptyList();
        }
    }

    /** {@inheritDoc} */
    @Nonnull public ClientTLSValidationParameters resolveSingle(CriteriaSet criteria) throws ResolverException {
        Constraint.isNotNull(criteria, "CriteriaSet was null");
        Constraint.isNotNull(criteria.get(ClientTLSValidationConfigurationCriterion.class), 
                "Resolver requires an instance of ClientTLSValidationConfigurationCriterion");
        
        ClientTLSValidationParameters params = new ClientTLSValidationParameters();
        
        params.setX509TrustEngine(resolveTrustEngine(criteria));
        
        params.setCertificateNameOptions(resolveNameOptions(criteria));
        
        return params;
    }
    
    /**
     * Resolve and return the effective {@link TrustEngine<X509Credential>}.
     * 
     * @param criteria the input criteria being evaluated
     * 
     * @return the effective resolver, or null
     */
    @Nullable protected TrustEngine<X509Credential> resolveTrustEngine(@Nonnull final CriteriaSet criteria) {
        
        for (ClientTLSValidationConfiguration config : 
            criteria.get(ClientTLSValidationConfigurationCriterion.class).getConfigurations()) {
            if (config.getX509TrustEngine() != null) {
                return config.getX509TrustEngine();
            }
        }
        return null;
    }

    /**
     * Resolve and return the effective {@link CertificateNameOptions}.
     * 
     * @param criteria the input criteria being evaluated
     * 
     * @return the effective name options, or null
     */
    @Nullable protected CertificateNameOptions resolveNameOptions(@Nonnull final CriteriaSet criteria) {
        
        for (ClientTLSValidationConfiguration config : 
            criteria.get(ClientTLSValidationConfigurationCriterion.class).getConfigurations()) {
            if (config.getCertificateNameOptions() != null) {
                return config.getCertificateNameOptions();
            }
        }
        return null;
    }

}
