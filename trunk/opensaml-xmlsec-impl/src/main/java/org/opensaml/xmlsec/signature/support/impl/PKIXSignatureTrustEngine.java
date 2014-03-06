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

package org.opensaml.xmlsec.signature.support.impl;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.collection.Pair;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.x509.PKIXTrustEngine;
import org.opensaml.security.x509.PKIXTrustEvaluator;
import org.opensaml.security.x509.PKIXValidationInformation;
import org.opensaml.security.x509.PKIXValidationInformationResolver;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.security.x509.impl.BasicX509CredentialNameEvaluator;
import org.opensaml.security.x509.impl.CertPathPKIXTrustEvaluator;
import org.opensaml.security.x509.impl.X509CredentialNameEvaluator;
import org.opensaml.xmlsec.crypto.XMLSigningUtil;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of {@link SignatureTrustEngine} which evaluates the validity and trustworthiness of XML and raw
 * signatures.
 * 
 * <p>
 * Processing is performed as described in {@link BaseSignatureTrustEngine}. If based on this processing, it is
 * determined that the Signature's KeyInfo is not present or does not contain a valid (and trusted) signing key, then
 * trust engine validation fails. Since the PKIX engine is based on the assumption that trusted signing keys are not
 * known in advance, the signing key must be present in, or derivable from, the information in the Signature's KeyInfo
 * element.
 * </p>
 */
public class PKIXSignatureTrustEngine extends
        BaseSignatureTrustEngine<Pair<Set<String>, Iterable<PKIXValidationInformation>>> implements
        PKIXTrustEngine<Signature> {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(PKIXSignatureTrustEngine.class);

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
     * @param resolver credential resolver used to resolve trusted credentials.
     * @param keyInfoResolver KeyInfo credential resolver used to obtain the (advisory) signing credential from a
     *            Signature's KeyInfo element.
     */
    public PKIXSignatureTrustEngine(@Nonnull final PKIXValidationInformationResolver resolver,
            @Nonnull final KeyInfoCredentialResolver keyInfoResolver) {

        super(keyInfoResolver);
        
        pkixResolver = Constraint.isNotNull(resolver, "PKIX trust information resolver cannot be null");
        pkixTrustEvaluator = new CertPathPKIXTrustEvaluator();
        credNameEvaluator = new BasicX509CredentialNameEvaluator();
    }

    /**
     * Constructor.
     * 
     * @param resolver credential resolver used to resolve trusted credentials.
     * @param keyInfoResolver KeyInfo credential resolver used to obtain the (advisory) signing credential from a
     *            Signature's KeyInfo element.
     * * @param pkixEvaluator the PKIX trust evaluator to use
     * @param nameEvaluator the X.509 credential name evaluator to use (may be null)
     */
    public PKIXSignatureTrustEngine(@Nonnull final PKIXValidationInformationResolver resolver,
            @Nonnull final KeyInfoCredentialResolver keyInfoResolver, @Nonnull final PKIXTrustEvaluator pkixEvaluator, 
            @Nullable final X509CredentialNameEvaluator nameEvaluator) {

        super(keyInfoResolver);
        
        pkixResolver = Constraint.isNotNull(resolver, "PKIX trust information resolver cannot be null");
        pkixTrustEvaluator = Constraint.isNotNull(pkixEvaluator, "PKIX trust evaluator cannot be null");
        credNameEvaluator = nameEvaluator;
    }
    
    /**
     * Get the PKIXTrustEvaluator instance used to evalute trust.
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
     * Get the X509CredentialNameEvaluator instance used to evalute a credential 
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
    @Nonnull public PKIXValidationInformationResolver getPKIXResolver() {
        return pkixResolver;
    }

    /** {@inheritDoc} */
    protected boolean doValidate(@Nonnull final Signature signature, @Nullable final CriteriaSet trustBasisCriteria)
            throws SecurityException {

        Pair<Set<String>, Iterable<PKIXValidationInformation>> validationPair  = 
            resolveValidationInfo(trustBasisCriteria);

        if (validate(signature, validationPair)) {
            return true;
        }

        log.debug("PKIX validation of signature failed, unable to resolve valid and trusted signing key");
        return false;
    }

    /** {@inheritDoc} */
    protected boolean doValidate(@Nonnull final byte[] signature, @Nonnull final byte[] content,
            @Nonnull final String algorithmURI, @Nullable final CriteriaSet trustBasisCriteria,
            @Nullable final Credential candidateCredential) throws SecurityException {

        if (candidateCredential == null || CredentialSupport.extractVerificationKey(candidateCredential) == null) {
            log.debug("Candidate credential was either not supplied or did not contain verification key");
            log.debug("PKIX trust engine requires supplied key, skipping PKIX trust evaluation");
            return false;
        }

        Pair<Set<String>, Iterable<PKIXValidationInformation>> validationPair = 
            resolveValidationInfo(trustBasisCriteria);

        try {
            if (XMLSigningUtil.verifyWithURI(candidateCredential, algorithmURI, signature, content)) {
                log.debug("Successfully verified raw signature using supplied candidate credential");
                log.debug("Attempting to establish trust of supplied candidate credential");
                if (evaluateTrust(candidateCredential, validationPair)) {
                    log.debug("Successfully established trust of supplied candidate credential");
                    return true;
                } else {
                    log.debug("Failed to establish trust of supplied candidate credential");
                }
            } else {
                log.debug("Cryptographic verification of raw signature failed with candidate credential");
            }
        } catch (SecurityException e) {
            // Java 7 now throws this exception under conditions such as mismatched key sizes.
            // Swallow this, it's logged by the verifyWithURI method already.
        }

        log.debug("PKIX validation of raw signature failed, "
                + "unable to establish trust of supplied verification credential");
        return false;
    }

    /** {@inheritDoc} */
    protected boolean evaluateTrust(@Nonnull final Credential untrustedCredential,
            @Nullable final Pair<Set<String>, Iterable<PKIXValidationInformation>> validationPair)
                    throws SecurityException {

        if (!(untrustedCredential instanceof X509Credential)) {
            log.debug("Can not evaluate trust of non-X509Credential");
            return false;
        }
        X509Credential untrustedX509Credential = (X509Credential) untrustedCredential;

        Set<String> trustedNames = validationPair.getFirst();
        Iterable<PKIXValidationInformation> validationInfoSet = validationPair.getSecond();
        if (validationInfoSet == null) {
            log.debug("PKIX validation information not available. Aborting PKIX validation");
            return false;
        }
        
        if (!checkNames(trustedNames, untrustedX509Credential)) {
            log.debug("Evaluation of credential against trusted names failed. Aborting PKIX validation");
            return false;
        }

        for (PKIXValidationInformation validationInfo : validationInfoSet) {
            try {
                if (pkixTrustEvaluator.validate(validationInfo, untrustedX509Credential)) {
                    log.debug("Signature trust established via PKIX validation of signing credential");
                    return true;
                }
            } catch (SecurityException e) {
                // log the operational error, but allow other validation info sets to be tried
                log.debug("Error performing PKIX validation on untrusted credential", e);
            }
        }

        log.debug("Signature trust could not be established via PKIX validation of signing credential");
        return false;
    }

    /**
     * Resolve and return a set of trusted validation information.
     * 
     * @param trustBasisCriteria criteria used to describe and/or resolve the information which serves as the basis for
     *            trust evaluation
     * @return a pair consisting of an optional set of trusted names, and an iterable of trusted
     *         PKIXValidationInformation
     * @throws SecurityException thrown if there is an error resolving the information from the trusted resolver
     */
    @Nonnull protected Pair<Set<String>, Iterable<PKIXValidationInformation>> resolveValidationInfo(
            @Nullable final CriteriaSet trustBasisCriteria) throws SecurityException {

        Set<String> trustedNames = null;
        if (pkixResolver.supportsTrustedNameResolution()) {
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
        Iterable<PKIXValidationInformation> validationInfoSet;
        try {
            validationInfoSet = pkixResolver.resolve(trustBasisCriteria);
        } catch (ResolverException e) {
            throw new SecurityException("Error resolving trusted PKIX validation information", e);
        }

        return new Pair<Set<String>, Iterable<PKIXValidationInformation>>(trustedNames, validationInfoSet);
    }
    
    /**
     * Evaluate the credential against the set of trusted names.
     * 
     * <p>Evaluates to true if no intsance of {@link X509CredentialNameEvaluator} is configured.</p>
     * 
     * @param trustedNames set of trusted names
     * @param untrustedCredential the credential being evaluated
     * @return true if evaluation is successful, false otherwise
     * @throws SecurityException thrown if there is an error evaluation the credential
     */
    protected boolean checkNames(@Nullable final Set<String> trustedNames,
            @Nonnull final X509Credential untrustedCredential)  throws SecurityException {
        
        if (credNameEvaluator == null) {
            log.debug("No credential name evaluator was available, skipping trusted name evaluation");
           return true; 
        } else {
            return credNameEvaluator.evaluate(untrustedCredential, trustedNames);
        }

    }

}