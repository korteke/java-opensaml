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

import java.util.HashSet;

import org.apache.log4j.Logger;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.credential.CredentialCriteriaSet;
import org.opensaml.xml.security.credential.CredentialResolver;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xml.security.trust.TrustedCredentialTrustEngine;
import org.opensaml.xml.security.x509.PKIXTrustEvaluator;
import org.opensaml.xml.security.x509.BasicPKIXValdiationInformation;
import org.opensaml.xml.security.x509.PKIXValidationInformation;
import org.opensaml.xml.security.x509.X509Credential;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureTrustEngine;
import org.opensaml.xml.util.DatatypeHelper;

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
public class PKIXSignatureTrustEngine extends BaseSignatureTrustEngine<Iterable<Credential>> 
    implements TrustedCredentialTrustEngine<Signature> {
    
    /** Class logger. */
    private static Logger log = Logger.getLogger(PKIXSignatureTrustEngine.class);
    
    /** The external PKIX trust evaluator used to establish trust. */
    private PKIXTrustEvaluator pkixTrustEvaluator;
    
    /** Resolver used for resolving trusted credentials. */
    private CredentialResolver credentialResolver;
    
    /** Default PKIX path validation depth to use. */
    private int defaultVerificationDepth;
    
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
        super(keyInfoResolver);
        credentialResolver = resolver;
        defaultVerificationDepth = -1;
        
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
    public CredentialResolver getCredentialResolver() {
        return credentialResolver;
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
        X509Credential untrustedX509Credential = (X509Credential) untrustedCredential;
        
        for (Credential trustedCredential : trustedCredentials) {
            if (! (trustedCredential instanceof X509Credential)) {
                log.debug("Skippking evaluation against non-X509Credential");
                continue;
            }
            X509Credential trustedX509Credential = (X509Credential) trustedCredential;
            
            PKIXValidationInformation validationInfo = 
                new BasicPKIXValdiationInformation(trustedX509Credential.getEntityCertificateChain(),
                        trustedX509Credential.getCRLs(), defaultVerificationDepth);
            
            HashSet<String> trustedNames  = new HashSet<String>(trustedX509Credential.getKeyNames());
            if (! DatatypeHelper.isEmpty(trustedX509Credential.getEntityId())) {
                trustedNames.add(trustedX509Credential.getEntityId());
            }
            
            if (pkixTrustEvaluator.pkixValidate(validationInfo, trustedNames, untrustedX509Credential)) {
                log.debug("Signature trust established via PKIX validation of signing credential");
                return true;
            }
        }
        return false;
        
    }

}
