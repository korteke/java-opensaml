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

import java.util.HashSet;

import org.apache.log4j.Logger;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.credential.CredentialCriteriaSet;
import org.opensaml.xml.security.credential.CredentialResolver;
import org.opensaml.xml.util.DatatypeHelper;

/**
 * Trust engine implementation which evaluates an X509Credential token based on PKIX validation processing
 * using validation information from a trusted source.
 * 
 */
public class PKIXX509CredentialTrustEngine implements PKIXTrustEngine {

    /** Class logger. */
    private static Logger log = Logger.getLogger(PKIXX509CredentialTrustEngine.class);

    /** Max path depth during validation. */
    private int verificationDepth;
    
    /** Resolver used for resolving trusted credentials. */
    private CredentialResolver credentialResolver;
    
    /** The external PKIX trust evaluator used to establish trust. */
    private PKIXTrustEvaluator pkixTrustEvaluator;

    /**
     * Constructor.
     * 
     * @param depth max path depth that can be reached during validation, a value of -1 indicates an unlimited depth
     * @param resolver credential resolver used to resolve trusted credentials
     */
    public PKIXX509CredentialTrustEngine(int depth, CredentialResolver resolver) {
        if (resolver == null) {
            throw new IllegalArgumentException("PKIX trust information resolver may not be null");
        }
        credentialResolver = resolver;
        verificationDepth = depth;
        
        pkixTrustEvaluator = new PKIXTrustEvaluator();
    }
    
    /** {@inheritDoc} */
    public CredentialResolver getCredentialResolver() {
        return credentialResolver;
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
    public boolean validate(X509Credential untrustedCredential, CredentialCriteriaSet trustedCredentialCriteria) 
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
        
        return validate(untrustedCredential, getCredentialResolver().resolveCredentials(trustedCredentialCriteria));
    }
    
    /**
     * Perform PKIX validation on the untrusted credential, using PKIX validation information based
     * on the supplied set of trusted credentials.
     * 
     * @param untrustedX509Credential the credential to evaluate
     * @param trustedCredentials the set of trusted credentials which serve as ths basis for trust evaluation
     * @return true if PKIX validation of the untrusted credential is successful, otherwise false
     * @throws SecurityException
     */
    protected boolean validate(X509Credential untrustedX509Credential, Iterable<Credential> trustedCredentials) {
        
        log.debug("Beginning PKIX validation using trusted credentials");
        
        for (Credential trustedCredential : trustedCredentials) {
            if ( ! (trustedCredential instanceof X509Credential)) {
                if (log.isDebugEnabled()) {
                    log.debug("Skipping evaluation against trusted, non-X509Credential");
                }
                continue;
            }
            X509Credential trustedX509Credential = (X509Credential) trustedCredential;
            
            PKIXValidationInformation validationInfo = 
                new BasicPKIXValdiationInformation(trustedX509Credential.getEntityCertificateChain(), 
                        trustedX509Credential.getCRLs(), verificationDepth);
            
            HashSet<String> trustedNames = new HashSet<String>(trustedX509Credential.getKeyNames());
            if (! DatatypeHelper.isEmpty(trustedX509Credential.getEntityId())) {
                trustedNames.add(trustedX509Credential.getEntityId());
            }
            
            if (pkixTrustEvaluator.pkixValidate(validationInfo, trustedNames, untrustedX509Credential)) {
                return true;
            }
        }
        
        return false;
    }
    
}