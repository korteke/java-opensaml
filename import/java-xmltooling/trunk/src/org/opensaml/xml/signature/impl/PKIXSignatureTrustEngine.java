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

import org.apache.log4j.Logger;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.credential.CredentialCriteriaSet;
import org.opensaml.xml.security.credential.CredentialResolver;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xml.security.x509.PKIXX509EntityCredentialTrustEngine;
import org.opensaml.xml.security.x509.X509Credential;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureTrustEngine;

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
public class PKIXSignatureTrustEngine extends BaseSignatureTrustEngine {
    
    /** Class logger. */
    private static Logger log = Logger.getLogger(PKIXSignatureTrustEngine.class);
    
    /** The external explicit key trust engine to use as a basis for trust in this implementation. */
    private PKIXX509EntityCredentialTrustEngine pkixTrustEngine;
    
    /**
     * Constructor.
     *
     * @param verificationDepth  max path depth that can be reached during validation, 
     *          a value of -1 indicates an unlimited depth
     * @param resolver credential resolver used to resolve trusted credentials.
     * @param keyInfoResolver KeyInfo credential resolver used to obtain the (advisory) signing credential 
     *          from a Signature's KeyInfo element.
     */
    public PKIXSignatureTrustEngine(int verificationDepth, CredentialResolver resolver,
            KeyInfoCredentialResolver keyInfoResolver) {
        
        super(resolver, keyInfoResolver);
        
        pkixTrustEngine = new PKIXX509EntityCredentialTrustEngine(verificationDepth, resolver);
    }

    /** {@inheritDoc} */
    public boolean validate(Signature signature, CredentialCriteriaSet trustedCredentialCriteria) 
            throws SecurityException {
        
        checkParams(signature, trustedCredentialCriteria);
        
        Iterable<Credential> trustedCredentials = 
            getCredentialResolver().resolveCredentials(trustedCredentialCriteria);
        
        if (validate(signature, trustedCredentials)) {
            return true;
        }
        
        log.error("PKIX validation of signature failed, unable to resolve valid and trusted signing key");
        return false;
    }
    
    /** {@inheritDoc} */
    protected boolean evaluateTrust(Credential untrustedCredential, Iterable<Credential> trustedCredentials) 
            throws SecurityException {
        
        if (! (untrustedCredential instanceof X509Credential)) {
            log.info("Can not evaluate trust of non-X509Credential");
            return false;
        }
        
        return pkixTrustEngine.validate((X509Credential) untrustedCredential, trustedCredentials);
    }

}
