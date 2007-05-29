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

import java.security.Key;

import org.apache.log4j.Logger;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.credential.CredentialCriteriaSet;
import org.opensaml.xml.security.credential.CredentialResolver;

/**
 * Trust engine that evaluates a credential's key against key(s) expressed within a set of trusted credentials
 * obtained from a credential resolver.
 * 
 * The credential being tested is valid if its public key or secret key matches the
 * public key, or secret key respectively, contained within any of the trusted credentials produced
 * by the given credential resolver.
 */
public class ExplicitKeyTrustEngine extends AbstractTrustEngine<Credential> implements TrustEngine<Credential> {

    /** Class logger. */
    private static Logger log = Logger.getLogger(ExplicitKeyTrustEngine.class);
    
    /**
     * Constructor.
     * 
     * @param credentialResolver credential resolver which is used to resolve trusted credentials
     */
    public ExplicitKeyTrustEngine(CredentialResolver credentialResolver) {
        if (credentialResolver == null) {
            throw new IllegalArgumentException("Credential resolver may not be null");
        }
        setCredentialResolver(credentialResolver); 
    }

    /** {@inheritDoc} */
    protected boolean validate(Credential untrustedCredential, Credential trustedCredential)
            throws SecurityException {
        
        Key untrustedKey = null;
        Key trustedKey = null;
        if (untrustedCredential.getPublicKey() != null) {
            untrustedKey = untrustedCredential.getPublicKey();
            trustedKey = trustedCredential.getPublicKey();
        } else {
            untrustedKey = untrustedCredential.getSecretKey();
            trustedKey = trustedCredential.getSecretKey();
        }
        if (untrustedKey == null) {
            if (log.isDebugEnabled()) {
                log.debug("Untrusted credential contained no key, unable to evaluate");
            }
            return false;
        } else if (trustedKey == null) {
            if (log.isDebugEnabled()) {
                log.debug("Trusted credential contained no key of the appropriate type, unable to evaluate");
            }
            return false;
        }
        
        if (untrustedKey.equals(trustedKey)) {
            if (log.isDebugEnabled()) {
                log.debug("Validated credential for entity " + untrustedCredential.getEntityId()
                        + " against trusted key");
            }
            return true;
        }

        if (log.isDebugEnabled()) {
            log.debug("Credential for entity " + untrustedCredential.getEntityId()
                    + " did not validate against trusted key");
        }

        return false;
    }

    /** {@inheritDoc} */
    public boolean validate(Credential untrustedCredential, CredentialCriteriaSet trustedCredentialCriteria) 
            throws SecurityException {
        
        if (untrustedCredential == null) {
            return false;
        }

        if (log.isDebugEnabled()) {
            log.debug("Validating credential for entity " + untrustedCredential.getEntityId());
        }
        
        for (Credential trustedCredential : getCredentialResolver().resolveCredentials(trustedCredentialCriteria)) {
            if (validate(untrustedCredential, trustedCredential)) {
                return true;
            }
        }

        return false;
    }
    
}