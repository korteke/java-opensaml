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

package org.opensaml.security.trust.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;
import org.opensaml.security.trust.TrustedCredentialTrustEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Trust engine that evaluates a credential's key against key(s) expressed within a set of trusted credentials obtained
 * from a trusted credential resolver.
 * 
 * The credential being tested is valid if its public key or secret key matches the public key, or secret key
 * respectively, contained within any of the trusted credentials produced by the given credential resolver.
 */
public class ExplicitKeyTrustEngine implements TrustedCredentialTrustEngine<Credential> {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(ExplicitKeyTrustEngine.class);

    /** Resolver used for resolving trusted credentials. */
    private final CredentialResolver credentialResolver;

    /** Trust evaluator. */
    private final ExplicitKeyTrustEvaluator trustEvaluator;

    /**
     * Constructor.
     * 
     * @param resolver credential resolver which is used to resolve trusted credentials
     */
    public ExplicitKeyTrustEngine(@Nonnull final CredentialResolver resolver) {
        credentialResolver = Constraint.isNotNull(resolver, "Credential resolver cannot be null");

        trustEvaluator = new ExplicitKeyTrustEvaluator();
    }

    /** {@inheritDoc} */
    @Nonnull public CredentialResolver getCredentialResolver() {
        return credentialResolver;
    }

    /** {@inheritDoc} */
    public boolean validate(@Nonnull final Credential untrustedCredential,
            @Nullable final CriteriaSet trustBasisCriteria) throws SecurityException {

        if (untrustedCredential == null) {
            log.error("Credential was null, unable to perform validation");
            return false;
        }

        log.debug("Attempting to validate untrusted credential");
        try {
            Iterable<Credential> trustedCredentials = getCredentialResolver().resolve(trustBasisCriteria);
            return trustEvaluator.validate(untrustedCredential, trustedCredentials);
        } catch (ResolverException e) {
            throw new SecurityException("Error resolving trusted credentials", e);
        }
    }

}