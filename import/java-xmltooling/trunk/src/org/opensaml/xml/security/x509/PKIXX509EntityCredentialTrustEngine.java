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

import org.apache.log4j.Logger;
import org.opensaml.xml.security.SecurityException;

/**
 * A trust engine that uses the X509 certificate and CRLs associated with a role to perform PKIX validation on security
 * tokens.
 */
public class PKIXX509EntityCredentialTrustEngine extends BasePKIXTrustEngine<X509Credential, X509Credential> {

    /** Class logger. */
    private static Logger log = Logger.getLogger(PKIXX509EntityCredentialTrustEngine.class);

    /** Max path depth during validation. */
    private int verificationDepth;

    /**
     * Constructor.
     * 
     * @param depth max path depth that can be reached during validation, a value of -1 indicates an unlimited depth
     */
    public PKIXX509EntityCredentialTrustEngine(int depth) {
        verificationDepth = depth;
    }

    /** {@inheritDoc} */
    public boolean validate(X509Credential untrustedCredential, X509Credential trustedCredential)
            throws SecurityException {
        if (log.isDebugEnabled()) {
            log.debug("PKIX validating credential for entity " + untrustedCredential.getEntityID());
        }

        if (untrustedCredential == null) {
            log.error("X.509 credential was null, unable to perform validation");
            return false;
        }

        if (!checkName(untrustedCredential, trustedCredential)) {
            return false;
        }

        if (log.isDebugEnabled()) {
            log.debug("Beginning PKIX validation process");
        }

        PKIXValidationInformation validationInfo = new BasicPKIXValdiationInformation(trustedCredential
                .getEntityCertificateChain(), trustedCredential.getCRLs(), verificationDepth);
        return pkixValidate(validationInfo, untrustedCredential);
    }
}