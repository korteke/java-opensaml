/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
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

package org.opensaml.xml.security.x509;

import java.util.Set;

import org.apache.log4j.Logger;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.SecurityException;

/**
 * Trust engine implementation which evaluates an X509Credential token based on PKIX validation processing
 * using validation information from a trusted source.
 * 
 */
public class PKIXX509CredentialTrustEngine implements PKIXTrustEngine<X509Credential> {

    /** Class logger. */
    private static Logger log = Logger.getLogger(PKIXX509CredentialTrustEngine.class);

    /** Resolver used for resolving trusted credentials. */
    private PKIXValidationInformationResolver pkixResolver;
    
    /** The external PKIX trust evaluator used to establish trust. */
    private PKIXTrustEvaluator pkixTrustEvaluator;

    /**
     * Constructor.
     * 
     * @param resolver credential resolver used to resolve trusted credentials
     */
    public PKIXX509CredentialTrustEngine(PKIXValidationInformationResolver resolver) {
        if (resolver == null) {
            throw new IllegalArgumentException("PKIX trust information resolver may not be null");
        }
        pkixResolver = resolver;
        
        pkixTrustEvaluator = new PKIXTrustEvaluator();
    }
    
    /** {@inheritDoc} */
    public PKIXValidationInformationResolver getPKIXResolver() {
        return pkixResolver;
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
    public boolean validate(X509Credential untrustedCredential, CriteriaSet trustBasisCriteria) 
            throws SecurityException {
        
        if (log.isDebugEnabled()) {
            log.debug("PKIX validating credential for entity " + untrustedCredential.getEntityId());
        }
        if (untrustedCredential == null) {
            log.error("X.509 credential was null, unable to perform validation");
            return false;
        }
        if (untrustedCredential.getEntityCertificate() == null) {
            log.error("Untrusted X.509 credential's entity certificate was null, unable to perform validation");
            return false;
        }
        
        Set<String> trustedNames = null;
        if (pkixTrustEvaluator.isNameChecking()) {
            if (pkixResolver.supportsTrustedNameResolution()) {
                trustedNames = pkixResolver.resolveTrustedNames(trustBasisCriteria);
            } else {
                log.debug("PKIX resolver does not support resolution of trusted names, skipping name checking");
            }
        }
        
        return validate(untrustedCredential, trustedNames, pkixResolver.resolve(trustBasisCriteria));
    }
    
    /**
     * Perform PKIX validation on the untrusted credential, using PKIX validation information based
     * on the supplied set of trusted credentials.
     * 
     * @param untrustedX509Credential the credential to evaluate
     * @param validationInfoSet the set of validation information which serves as ths basis for trust evaluation
     * @param trustedNames the set of trusted names for name checking purposes
     * 
     * @return true if PKIX validation of the untrusted credential is successful, otherwise false
     * @throws SecurityException
     */
    protected boolean validate(X509Credential untrustedX509Credential, Set<String> trustedNames,
            Iterable<PKIXValidationInformation> validationInfoSet) {
        
        log.debug("Beginning PKIX validation using trusted validation information");
        
        for (PKIXValidationInformation validationInfo : validationInfoSet) {
            if (pkixTrustEvaluator.pkixValidate(validationInfo, trustedNames, untrustedX509Credential)) {
                return true;
            }
        }
        return false;
    }
    

}