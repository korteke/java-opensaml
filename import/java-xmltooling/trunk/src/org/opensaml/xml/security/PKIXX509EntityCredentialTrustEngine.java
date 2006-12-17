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

package org.opensaml.xml.security;

import org.apache.log4j.Logger;

/**
 * A trust engine that uses the X509 certificate and CRLs associated with a role to perform PKIX validation on security
 * tokens.
 */
public class PKIXX509EntityCredentialTrustEngine extends BasePKIXTrustEngine<X509EntityCredential, X509KeyInfoResolver>
        implements PKIXTrustEngine<X509EntityCredential, X509KeyInfoResolver> {

    /** Class logger. */
    private Logger log = Logger.getLogger(PKIXX509EntityCredentialTrustEngine.class);


    /** Constructor. */
    public PKIXX509EntityCredentialTrustEngine() {
        setDefaultKeyResolver(new InlineX509KeyInfoResolver());
        setCheckName(true);
    }


    /** {@inheritDoc} */
    public boolean validate(X509EntityCredential entityCredential, KeyInfoSource keyInfoSource,
            X509KeyInfoResolver keyResolver) throws SecurityException {

        if (log.isDebugEnabled()) {
            log.debug("Attempting to validate X.509 credential against role descriptor");
        }

        if (entityCredential == null) {
            log.error("X.509 credential was null, unable to perform validation");
            return false;
        }

        if (checkName()) {
            if (log.isDebugEnabled()) {
                log.debug("Checking the entity certificate information against the peer and key names");
            }
            if (!checkEntityNames(entityCredential, keyInfoSource, keyResolver)) {
                return false;
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Beginning PKIX validation process");
        }
        
        for(PKIXValidationInformation info : getPKIXValidationInformation(keyInfoSource.getName())){
            if (pkixValidate(entityCredential, info)) {
                return true;
            }
        }

        return false;
    }
}