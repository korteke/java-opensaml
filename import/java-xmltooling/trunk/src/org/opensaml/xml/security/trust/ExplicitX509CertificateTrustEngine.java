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

package org.opensaml.xml.security.trust;

import org.apache.log4j.Logger;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.CredentialCriteriaSet;
import org.opensaml.xml.security.credential.CredentialResolver;
import org.opensaml.xml.security.x509.X509Credential;

/**
 * Trust engine that evaluates a credential's X.509 certificate against certificates expressed within a set 
 * of trusted credentials obtained from a credential resolver.
 * 
 * The credential being tested is valid if its entity certificate matches the entity certificate contained 
 * within any of the trusted credentials produced by the given credential resolver.
 */
public class ExplicitX509CertificateTrustEngine implements TrustedCredentialTrustEngine<X509Credential> {

    /** Class logger. */
    private static Logger log = Logger.getLogger(ExplicitX509CertificateTrustEngine.class);
    
    /** Resolver used for resolving trusted credentials. */
    private CredentialResolver credentialResolver;
    
    /** Trust evaluator. */
    private ExplicitX509CertificateTrustEvaluator trustEvaluator;
    
    /**
     * Constructor.
     * 
     * @param resolver credential resolver which is used to resolve trusted credentials
     */
    public ExplicitX509CertificateTrustEngine(CredentialResolver resolver) {
        if (resolver == null) {
            throw new IllegalArgumentException("Credential resolver may not be null");
        }
        credentialResolver = resolver;
        
        trustEvaluator = new ExplicitX509CertificateTrustEvaluator();
    }
    
    /** {@inheritDoc} */
    public CredentialResolver getCredentialResolver() {
        return credentialResolver;
    }

    /** {@inheritDoc} */
    public boolean validate(X509Credential untrustedCredential, CredentialCriteriaSet trustedCredentialCriteria) 
            throws SecurityException {
        
        if (untrustedCredential == null) {
            return false;
        }

        if (log.isDebugEnabled()) {
            log.debug("Validating credential for entity " + untrustedCredential.getEntityId());
        }
        
        return trustEvaluator.validate(untrustedCredential, 
                getCredentialResolver().resolveCredentials(trustedCredentialCriteria));
    }
    
}