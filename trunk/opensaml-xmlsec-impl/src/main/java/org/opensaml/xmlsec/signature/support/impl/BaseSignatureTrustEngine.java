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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.SignatureValidationParameters;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoCriterion;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.opensaml.xmlsec.signature.support.SignatureValidationParametersCriterion;
import org.opensaml.xmlsec.signature.support.SignatureValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * A base implementation of {@link SignatureTrustEngine} which evaluates the validity and trustworthiness of XML and raw
 * signatures.
 * 
 * <p>
 * When processing XML signatures, the supplied KeyInfoCredentialResolver will be used to resolve credential(s)
 * containing the (advisory) signing key from the KeyInfo element of the Signature, if present. If any of these
 * credentials do contain the valid signing key, they will be evaluated for trustworthiness against trusted information,
 * which will be resolved in an implementation-specific manner.
 * 
 * <p>
 * Subclasses are required to implement {@link #evaluateTrust(Credential, Object)} using an implementation-specific
 * trust model.
 * </p>
 * 
 * @param <TrustBasisType> the type of trusted information which has been resolved and which will serve as the basis for
 *            trust evaluation
 * 
 */
public abstract class BaseSignatureTrustEngine<TrustBasisType> implements SignatureTrustEngine {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(BaseSignatureTrustEngine.class);

    /** KeyInfo credential resolver used to obtain the signing credential from a Signature's KeyInfo. */
    private final KeyInfoCredentialResolver keyInfoCredentialResolver;

    /**
     * Constructor.
     * 
     * @param keyInfoResolver KeyInfo credential resolver used to obtain the (advisory) signing credential from a
     *            Signature's KeyInfo element.
     */
    public BaseSignatureTrustEngine(@Nonnull final KeyInfoCredentialResolver keyInfoResolver) {
        keyInfoCredentialResolver = Constraint.isNotNull(keyInfoResolver, "KeyInfo credential resolver cannot be null");
    }

    /** {@inheritDoc} */
    @Nullable public KeyInfoCredentialResolver getKeyInfoResolver() {
        return keyInfoCredentialResolver;
    }
    
    /** {@inheritDoc} */
    public final boolean validate(@Nonnull final Signature signature, @Nullable final CriteriaSet trustBasisCriteria)
            throws SecurityException {
        
        checkParams(signature, trustBasisCriteria);
        
        SignatureValidationParametersCriterion validationCriterion = 
                trustBasisCriteria.get(SignatureValidationParametersCriterion.class);
        if (validationCriterion != null) {
            log.debug("Performing signature algorithm whitelist/blacklist validation using params from CriteriaSet");
            SignatureAlgorithmValidator algorithmValidator = 
                    new SignatureAlgorithmValidator(validationCriterion.getSignatureValidationParameters());
            try {
                algorithmValidator.validate(signature);
            } catch (SignatureException e) {
                log.warn("XML signature failed algorithm whitelist/blacklist validation");
                return false;
            }
        }
        
        return doValidate(signature, trustBasisCriteria);
    }
    
    /**
     * Validate the signature using the supplied trust criteria.
     * 
     * @param signature the signature to validate
     * @param trustBasisCriteria criteria used to describe and/or resolve the information
     *          which serves as the basis for trust evaluation
     * @return true if signature is valid and trusted, false otherwise
     * @throws SecurityException if there is a fatal error evaluating the signature
     */
    protected abstract boolean doValidate(@Nonnull final Signature signature, 
            @Nullable final CriteriaSet trustBasisCriteria) throws SecurityException;
        
    
    /** {@inheritDoc} */
    public final boolean validate(@Nonnull final byte[] signature, @Nonnull final byte[] content,
            @Nonnull final String algorithmURI, @Nullable final CriteriaSet trustBasisCriteria,
            @Nullable final Credential candidateCredential) throws SecurityException {
        
        checkParamsRaw(signature, content, algorithmURI, trustBasisCriteria);
        
        SignatureValidationParametersCriterion validationCriterion = 
                trustBasisCriteria.get(SignatureValidationParametersCriterion.class);
        if (validationCriterion != null) {
            log.debug("Performing signature algorithm whitelist/blacklist validation using params from CriteriaSet");
            SignatureValidationParameters params = validationCriterion.getSignatureValidationParameters();
            if (!AlgorithmSupport.validateAlgorithmURI(algorithmURI, params.getWhitelistedAlgorithmURIs(), 
                    params.getBlacklistedAlgorithmsURIs())) {
                log.warn("Simple/raw signature failed algorithm whitelist/blacklist validation");
                return false;
            }
        }
        
        return doValidate(signature, content, algorithmURI, trustBasisCriteria, candidateCredential);
    }
    
    /**
     * Determines whether a raw signature over specified content is valid and signed by a trusted credential.
     * 
     * <p>A candidate verification credential may optionally be supplied.  If one is supplied and is
     * determined to successfully verify the signature, an attempt will be made to establish
     * trust on this basis.</p>
     * 
     * <p>If a candidate credential is not supplied, or it does not successfully verify the signature,
     * some implementations may be able to resolve candidate verification credential(s) in an
     * implementation-specific manner based on the trusted criteria supplied, and then attempt 
     * to verify the signature and establish trust on this basis.</p>
     * 
     * @param signature the signature value
     * @param content the content that was signed
     * @param algorithmURI the signature algorithm URI which was used to sign the content
     * @param trustBasisCriteria criteria used to describe and/or resolve the information
     *          which serves as the basis for trust evaluation
     * @param candidateCredential the untrusted candidate credential containing the validation key
     *          for the signature (optional)
     * 
     * @return true if the signature was valid for the provided content and was signed by a key
     *          contained within a credential established as trusted based on the supplied criteria,
     *          otherwise false
     * 
     * @throws SecurityException thrown if there is a problem attempting to verify the signature such as the signature
     *             algorithim not being supported
     */
    protected abstract boolean doValidate(@Nonnull final byte[] signature, @Nonnull final byte[] content,
            @Nonnull final String algorithmURI, @Nullable final CriteriaSet trustBasisCriteria,
            @Nullable final Credential candidateCredential) throws SecurityException;


    /**
     * Attempt to establish trust by resolving signature verification credentials from the Signature's KeyInfo. If any
     * credentials so resolved correctly verify the signature, attempt to establish trust using subclass-specific trust
     * logic against trusted information as implemented in {@link #evaluateTrust(Credential, Object)}.
     * 
     * @param signature the Signature to evaluate
     * @param trustBasis the information which serves as the basis for trust evaluation
     * @return true if the signature is verified by any KeyInfo-derived credential which can be established as trusted,
     *         otherwise false
     * @throws SecurityException if an error occurs during signature verification or trust processing
     */
    protected boolean validate(@Nonnull final Signature signature, @Nullable final TrustBasisType trustBasis)
            throws SecurityException {

        log.debug("Attempting to verify signature and establish trust using KeyInfo-derived credentials");

        if (signature.getKeyInfo() != null) {

            KeyInfoCriterion keyInfoCriteria = new KeyInfoCriterion(signature.getKeyInfo());
            CriteriaSet keyInfoCriteriaSet = new CriteriaSet(keyInfoCriteria);

            try {
                for (Credential kiCred : getKeyInfoResolver().resolve(keyInfoCriteriaSet)) {
                    if (verifySignature(signature, kiCred)) {
                        log.debug("Successfully verified signature using KeyInfo-derived credential");
                        log.debug("Attempting to establish trust of KeyInfo-derived credential");
                        if (evaluateTrust(kiCred, trustBasis)) {
                            log.debug("Successfully established trust of KeyInfo-derived credential");
                            return true;
                        } else {
                            log.debug("Failed to establish trust of KeyInfo-derived credential");
                        }
                    }
                }
            } catch (ResolverException e) {
                throw new SecurityException("Error resolving KeyInfo from KeyInfoResolver", e);
            }
        } else {
            log.debug("Signature contained no KeyInfo element, could not resolve verification credentials");
        }

        log.debug("Failed to verify signature and/or establish trust using any KeyInfo-derived credentials");
        return false;
    }

    /**
     * Evaluate the untrusted KeyInfo-derived credential with respect to the specified trusted information.
     * 
     * @param untrustedCredential the untrusted credential being evaluated
     * @param trustBasis the information which serves as the basis for trust evaluation
     * 
     * @return true if the trust can be established for the untrusted credential, otherwise false
     * 
     * @throws SecurityException if an error occurs during trust processing
     */
    protected abstract boolean evaluateTrust(@Nonnull final Credential untrustedCredential,
            @Nullable final TrustBasisType trustBasis) throws SecurityException;

    /**
     * Attempt to verify a signature using the key from the supplied credential.
     * 
     * @param signature the signature on which to attempt verification
     * @param credential the credential containing the candidate validation key
     * @return true if the signature can be verified using the key from the credential, otherwise false
     */
    protected boolean verifySignature(@Nonnull final Signature signature, @Nonnull final Credential credential) {
        try {
            SignatureValidator.validate(signature, credential);
        } catch (SignatureException e) {
            log.debug("Signature validation using candidate validation credential failed", e);
            return false;
        }
        
        log.debug("Signature validation using candidate credential was successful");
        return true;
    }

    /**
     * Check the signature and credential criteria for required values.
     * 
     * @param signature the signature to be evaluated
     * @param trustBasisCriteria the set of trusted credential criteria
     * @throws SecurityException thrown if required values are absent or otherwise invalid
     */
    protected void checkParams(@Nonnull final Signature signature, @Nonnull final CriteriaSet trustBasisCriteria)
            throws SecurityException {

        if (signature == null) {
            throw new SecurityException("Signature cannot be null");
        } else if (trustBasisCriteria == null) {
            throw new SecurityException("Trust basis criteria set cannot be null");
        } else if (trustBasisCriteria.isEmpty()) {
            throw new SecurityException("Trust basis criteria set cannot be empty");
        }
    }

    /**
     * Check the signature and credential criteria for required values.
     * 
     * @param signature the signature to be evaluated
     * @param content the data over which the signature was computed
     * @param algorithmURI the signing algorithm URI which was used
     * @param trustBasisCriteria the set of trusted credential criteria
     * @throws SecurityException thrown if required values are absent or otherwise invalid
     */
    protected void checkParamsRaw(@Nonnull final byte[] signature, @Nonnull final byte[] content,
            @Nonnull final String algorithmURI, @Nonnull final CriteriaSet trustBasisCriteria)
            throws SecurityException {

        if (signature == null || signature.length == 0) {
            throw new SecurityException("Signature byte array cannot be null or empty");
        } else if (content == null || content.length == 0) {
            throw new SecurityException("Content byte array cannot be null or empty");
        } else if (Strings.isNullOrEmpty(algorithmURI)) {
            throw new SecurityException("Signature algorithm cannot be null or empty");
        } else if (trustBasisCriteria == null) {
            throw new SecurityException("Trust basis criteria set cannot be null");
        } else if (trustBasisCriteria.isEmpty()) {
            throw new SecurityException("Trust basis criteria set cannot be empty");
        }
    }

}