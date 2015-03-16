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

package org.opensaml.security.x509.impl;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.security.SecurityException;
import org.opensaml.security.x509.PKIXTrustEngine;
import org.opensaml.security.x509.PKIXTrustEvaluator;
import org.opensaml.security.x509.PKIXValidationInformation;
import org.opensaml.security.x509.PKIXValidationInformationResolver;
import org.opensaml.security.x509.X509Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Trust engine implementation which evaluates an {@link X509Credential} token based on PKIX validation processing using
 * validation information from a trusted source.
 */
public class PKIXX509CredentialTrustEngine implements PKIXTrustEngine<X509Credential> {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(PKIXX509CredentialTrustEngine.class);

    /** Resolver used for resolving trusted credentials. */
    private final PKIXValidationInformationResolver pkixResolver;

    /** The external PKIX trust evaluator used to establish trust. */
    private final PKIXTrustEvaluator pkixTrustEvaluator;
    
    /** The external credential name evaluator used to establish trusted name compliance. */
    private final X509CredentialNameEvaluator credNameEvaluator;

    /**
     * Constructor.
     * 
     * <p>The PKIX trust evaluator used defaults to {@link CertPathPKIXTrustEvaluator}.</p>
     * 
     * <p>The X.509 credential name evaluator used defaults to {@link BasicX509CredentialNameEvaluator}.</p>
     * 
     * @param resolver credential resolver used to resolve trusted credentials
     */
    public PKIXX509CredentialTrustEngine(@Nonnull final PKIXValidationInformationResolver resolver) {
        pkixResolver = Constraint.isNotNull(resolver, "PKIX trust information resolver cannot be null");

        pkixTrustEvaluator = new CertPathPKIXTrustEvaluator();
        credNameEvaluator = new BasicX509CredentialNameEvaluator();
    }
    
    /**
     * Constructor.
     * 
     * @param resolver credential resolver used to resolve trusted credentials
     * @param pkixEvaluator the PKIX trust evaluator to use
     * @param nameEvaluator the X.509 credential name evaluator to use (may be null)
     */
    public PKIXX509CredentialTrustEngine(@Nonnull final PKIXValidationInformationResolver resolver,
            @Nonnull final PKIXTrustEvaluator pkixEvaluator,
            @Nullable final X509CredentialNameEvaluator nameEvaluator) {

        pkixResolver = Constraint.isNotNull(resolver, "PKIX trust information resolver cannot be null");
        pkixTrustEvaluator = Constraint.isNotNull(pkixEvaluator, "PKIX trust evaluator may not be null");
        credNameEvaluator = nameEvaluator;
    }

    /** {@inheritDoc} */
    @Nonnull public PKIXValidationInformationResolver getPKIXResolver() {
        return pkixResolver;
    }

    /**
     * Get the {@link PKIXTrustEvaluator} instance used to evaluate trust.
     * 
     * <p>The parameters of this evaluator may be modified to
     * adjust trust evaluation processing.</p>
     * 
     * @return the PKIX trust evaluator instance that will be used
     */
    @Nonnull public PKIXTrustEvaluator getPKIXTrustEvaluator() {
        return pkixTrustEvaluator;
    }
    
    /**
     * Get the {@link X509CredentialNameEvaluator} instance used to evaluate a credential 
     * against trusted names.
     * 
     * <p>The parameters of this evaluator may be modified to
     * adjust trust evaluation processing.</p>
     * 
     * @return the PKIX trust evaluator instance that will be used
     */
    @Nullable public X509CredentialNameEvaluator getX509CredentialNameEvaluator() {
        return credNameEvaluator;
    }

    /** {@inheritDoc} */
    public boolean validate(@Nonnull final X509Credential untrustedCredential, @Nullable CriteriaSet trustBasisCriteria)
        throws SecurityException {
        
        log.debug("Attempting PKIX validation of untrusted credential");

        // Nulls here technically violate the method constraints anyway
        if (untrustedCredential == null) {
            log.error("X.509 credential was null, unable to perform validation");
            return false;
        } else if (untrustedCredential.getEntityCertificate() == null) {
            log.error("Untrusted X.509 credential's entity certificate was null, unable to perform validation");
            return false;
        }

        Set<String> trustedNames = null;
        if (getPKIXResolver().supportsTrustedNameResolution()) {
            try {
                trustedNames = pkixResolver.resolveTrustedNames(trustBasisCriteria);
            } catch (UnsupportedOperationException e) {
                throw new SecurityException("Error resolving trusted names", e);
            } catch (ResolverException e) {
                throw new SecurityException("Error resolving trusted names", e);
            }
        } else {
            log.debug("PKIX resolver does not support resolution of trusted names, skipping name checking");
        }

        try {
            return validate(untrustedCredential, trustedNames, pkixResolver.resolve(trustBasisCriteria));
        } catch (ResolverException e) {
            throw new SecurityException("Error resolving trusted credentials", e);
        }
    }

    /**
     * Perform PKIX validation on the untrusted credential, using PKIX validation information based on the supplied set
     * of trusted credentials.
     * 
     * @param untrustedX509Credential the credential to evaluate
     * @param trustedNames the set of trusted names for name checking purposes
     * @param validationInfoSet the set of validation information which serves as the basis for trust evaluation
     * 
     * @return true if PKIX validation of the untrusted credential is successful, otherwise false
     * @throws SecurityException thrown if there is an error validating the untrusted credential
     *          against trusted names or validation information
     */
    protected boolean validate(@Nonnull final X509Credential untrustedX509Credential,
            @Nullable final Set<String> trustedNames,
            @Nonnull final Iterable<PKIXValidationInformation> validationInfoSet) throws SecurityException {
        
        log.debug("Beginning PKIX validation using trusted validation information");

        if (!checkNames(trustedNames, untrustedX509Credential)) {
            log.debug("Evaluation of credential against trusted names failed. Aborting PKIX validation");
            return false;
        }

        for (PKIXValidationInformation validationInfo : validationInfoSet) {
            try {
                if (getPKIXTrustEvaluator().validate(validationInfo, untrustedX509Credential)) {
                    log.debug("Credential trust established via PKIX validation");
                    return true;
                }
            } catch (SecurityException e) {
                // log the operational error, but allow other validation info sets to be tried
                log.debug("Error performing PKIX validation on untrusted credential", e);
            }
        }
        log.debug("Trust of untrusted credential could not be established via PKIX validation");
        return false;
    }
    
    /**
     * Evaluate the credential against the set of trusted names.
     * 
     * <p>Evaluates to true if no instance of {@link X509CredentialNameEvaluator} is configured.</p>
     * 
     * @param trustedNames set of trusted names
     * @param untrustedCredential the credential being evaluated
     * @return true if evaluation is successful, false otherwise
     * @throws SecurityException thrown if there is an error evaluation the credential
     */
    protected boolean checkNames(@Nullable final Set<String> trustedNames,
            @Nonnull final X509Credential untrustedCredential)  throws SecurityException {
        
        if (getX509CredentialNameEvaluator() == null) {
            log.debug("No credential name evaluator was available, skipping trusted name evaluation");
           return true; 
        } else if (trustedNames == null) {
            log.debug("Trusted names was null, signalling PKIX resolver does not support trusted names resolution, " 
                    + "skipping trusted name evaluation");
           return true; 
        } else {
            return credNameEvaluator.evaluate(untrustedCredential, trustedNames);
        }
    }

}