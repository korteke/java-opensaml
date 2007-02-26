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

import java.security.PublicKey;
import java.util.Collection;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.trust.TrustEngine;

/**
 * Trust engine that evaluates X509 credentials aginst key expressed within a given trusted credential.
 * 
 * The credential being tested is valid if its public key or the public key of its entity certificate matches any of the
 * public keys produced by the given key resolver or the public keys of any of the certificates produced by the key
 * resolver.
 */
public class BasicX509CredentialTrustEngine implements TrustEngine<X509Credential, X509Credential> {

    /** Class logger. */
    private static Logger log = Logger.getLogger(BasicX509CredentialTrustEngine.class);

    /** {@inheritDoc} */
    public boolean validate(X509Credential untrustedCredential, X509Credential trustedCredential)
            throws SecurityException {

        if (untrustedCredential == null) {
            return false;
        }

        if (log.isDebugEnabled()) {
            log.debug("Validating X509 credential for entity " + untrustedCredential.getEntityID());
        }

        Collection<PublicKey> trustedKeys = getKeys(trustedCredential);
        Collection<PublicKey> credentialKeys = getKeys(untrustedCredential);
        for (PublicKey trustedKey : trustedKeys) {
            for (PublicKey credentialKey : credentialKeys) {
                if (trustedKey.equals(credentialKey)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Validated X509 credential for entity " + untrustedCredential.getEntityID()
                                + " against trusted public keys");
                    }
                    return true;
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("X509 credential for entity " + untrustedCredential.getEntityID()
                    + " did not validated against any trusted keys");
        }

        return false;
    }

    /**
     * Gets the public keys from the credential, including those found in its entity certificate.
     * 
     * @param credential credential to extract the public keys from
     * 
     * @return public keys from the credential
     */
    protected Collection<PublicKey> getKeys(X509Credential credential) {
        HashSet<PublicKey> keys = new HashSet<PublicKey>(credential.getPublicKeys());
        if (credential.getEntityCertificate() != null) {
            keys.add(credential.getEntityCertificate().getPublicKey());
        }

        return keys;
    }
}