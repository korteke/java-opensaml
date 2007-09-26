/*
 * Copyright [2007] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xml.signature.impl;

import java.util.Set;

import org.apache.log4j.Logger;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.SecurityHelper;
import org.opensaml.xml.security.SigningUtil;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xml.security.x509.PKIXTrustEngine;
import org.opensaml.xml.security.x509.PKIXTrustEvaluator;
import org.opensaml.xml.security.x509.PKIXValidationInformation;
import org.opensaml.xml.security.x509.PKIXValidationInformationResolver;
import org.opensaml.xml.security.x509.X509Credential;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureTrustEngine;
import org.opensaml.xml.util.Pair;

/**
 * An implementation of {@link SignatureTrustEngine} which evaluates the validity and trustworthiness
 * of XML and raw signatures.
 * 
 * <p>Processing is  performed as described in {@link BaseSignatureTrustEngine}. If based on this processing,
 * it is determined that the Signature's KeyInfo is not present or does not contain a valid (and trusted)
 * signing key, then trust engine validation fails. Since the PKIX engine is based on the assumption that 
 * trusted signing keys are not known in advance, the signing key must be present in, or derivable from,
 * the information in the Signature's KeyInfo element.</p>
 */
public class PKIXSignatureTrustEngine 
    extends BaseSignatureTrustEngine<Pair<Set<String>, Iterable<PKIXValidationInformation>>>
    implements PKIXTrustEngine<Signature> {
    
    /** Class logger. */
    private static Logger log = Logger.getLogger(PKIXSignatureTrustEngine.class);
    
    /** Resolver used for resolving trusted credentials. */
    private PKIXValidationInformationResolver pkixResolver;
    
    /** The external PKIX trust evaluator used to establish trust. */
    private PKIXTrustEvaluator pkixTrustEvaluator;
    
    /**
     * Constructor.
     *
     * @param resolver credential resolver used to resolve trusted credentials.
     * @param keyInfoResolver KeyInfo credential resolver used to obtain the (advisory) signing credential 
     *          from a Signature's KeyInfo element.
     */
    public PKIXSignatureTrustEngine(PKIXValidationInformationResolver resolver,
            KeyInfoCredentialResolver keyInfoResolver) {
        
        super(keyInfoResolver);
        if (resolver == null) {
            throw new IllegalArgumentException("PKIX trust information resolver may not be null");
        }
        pkixResolver = resolver;
        
        pkixTrustEvaluator = new PKIXTrustEvaluator();
    }
    
    /**
     * Get the PKIXTrustEvaluator instance used to evalute trust.  The parameters of this
     * evaluator may be modified to adjust trust evaluation processing.
     * 
     * @return the PKIX trust evaluator instance that will be used
     */
    public PKIXTrustEvaluator getPKIXTrustEvaluator() {
        return pkixTrustEvaluator;
    }
    
    /** {@inheritDoc} */
    public PKIXValidationInformationResolver getPKIXResolver() {
        return pkixResolver;
    }

    /** {@inheritDoc} */
    public boolean validate(Signature signature, CriteriaSet trustBasisCriteria) throws SecurityException {
        
        checkParams(signature, trustBasisCriteria);
        
        Pair<Set<String>, Iterable<PKIXValidationInformation>> validationPair = 
            resolveValidationInfo(trustBasisCriteria);
        
        if (validate(signature, validationPair)) {
            return true;
        }
        
        log.error("PKIX validation of signature failed, unable to resolve valid and trusted signing key");
        return false;
    }
    
    /** {@inheritDoc} */
    public boolean validate(byte[] signature, byte[] content, String algorithmURI, CriteriaSet trustBasisCriteria,
            Credential candidateCredential) throws SecurityException {
        
        if (candidateCredential == null || SecurityHelper.extractVerificationKey(candidateCredential) == null) {
            if (log.isDebugEnabled()) {
                log.debug("Candidate credential was either not supplied or did not contain verification key");
                log.debug("PKIX trust engine requires supplied key, skipping PKIX trust evaluation");
            }
            return false;
        }
        
        checkParamsRaw(signature, content, algorithmURI, trustBasisCriteria);
        
        Pair<Set<String>, Iterable<PKIXValidationInformation>> validationPair = 
            resolveValidationInfo(trustBasisCriteria);
        
        if (SigningUtil.verifyWithURI(candidateCredential, algorithmURI, signature, content)) {
            log.debug("Successfully verified raw signature using supplied candidate credential");
            log.debug("Attempting to establish trust of supplied candidate credential");
            if (evaluateTrust(candidateCredential, validationPair)) {
                log.debug("Successfully established trust of supplied candidate credential");
                return true;
            } else {
                log.debug("Failed to establish trust of supplied candidate credential");
            }
        }
        
        log.error("PKIX validation of raw signature failed, " +
                "unable to establish trust of supplied verification credential");
        return false;
    }

    /** {@inheritDoc} */
    protected boolean evaluateTrust(Credential untrustedCredential,
            Pair<Set<String>,Iterable<PKIXValidationInformation>> validationPair) throws SecurityException {
        
        if (! (untrustedCredential instanceof X509Credential)) {
            log.info("Can not evaluate trust of non-X509Credential");
            return false;
        }
        X509Credential untrustedX509Credential = (X509Credential) untrustedCredential;
        
        Set<String> trustedNames = validationPair.getFirst();
        Iterable<PKIXValidationInformation> validationInfoSet = validationPair.getSecond();
        
        for (PKIXValidationInformation validationInfo : validationInfoSet) {
            try {
                if (pkixTrustEvaluator.pkixValidate(validationInfo, trustedNames, untrustedX509Credential)) {
                    log.debug("Signature trust established via PKIX validation of signing credential");
                    return true;
                }
            } catch (SecurityException e) {
                // log the operational error, but allow other validation info sets to be tried
                log.error("Error performing PKIX validation on untrusted credential", e);
            }
        }
        
        log.debug("Signature trust could not be established via PKIX validation of signing credential");
        return false;
    }
    
    /**
     * Resolve and return a set of trusted validation information.
     * 
     * @param trustBasisCriteria criteria used to describe and/or resolve the information
     *          which serves as the basis for trust evaluation
     * @return a pair consisting of an optional set of trusted names, and an iterable of
     *          trusted PKIXValidationInformation
     * @throws SecurityException thrown if there is an error resolving the information from the 
     *          trusted resolver
     */
    protected Pair<Set<String>, Iterable<PKIXValidationInformation>>
    resolveValidationInfo(CriteriaSet trustBasisCriteria) throws SecurityException {
        
        Set<String> trustedNames = null;
        if (pkixTrustEvaluator.isNameChecking()) {
            if (pkixResolver.supportsTrustedNameResolution()) {
                trustedNames = pkixResolver.resolveTrustedNames(trustBasisCriteria);
            } else {
                log.debug("PKIX resolver does not support resolution of trusted names, skipping name checking");
            }
        }
        Iterable<PKIXValidationInformation> validationInfoSet = pkixResolver.resolve(trustBasisCriteria);
        
        Pair<Set<String>, Iterable<PKIXValidationInformation>> validationPair = 
            new Pair<Set<String>, Iterable<PKIXValidationInformation>>(trustedNames, validationInfoSet);
        
        return validationPair;
    }

}
