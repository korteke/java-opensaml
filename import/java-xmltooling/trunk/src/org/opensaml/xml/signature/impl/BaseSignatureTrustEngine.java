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
import org.opensaml.xml.security.credential.UsageCredentialCriteria;
import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialCriteria;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xml.security.trust.AbstractTrustEngine;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureTrustEngine;
import org.opensaml.xml.signature.SignatureValidator;
import org.opensaml.xml.validation.ValidationException;

/**
 * A base implementation of {@link SignatureTrustEngine} which evaluates the validity and trustworthiness
 * of XML and raw signatures.
 * 
 * <p>When processing XML signatures, the supplied KeyInfoCredentialResolver will be used to resolve credential(s)
 * containing the (advisory) signing key from the KeyInfo element of the Signature, if present.  If any of these
 * credentials do contain the valid signing key, they will be evaluated for trustworthiness against the 
 * set of trusted credentials supplied by the trusted credential resolver.</p>
 * 
 * <p>Subclasses are required to implement {@link #evaluateTrust(Credential, Iterable)} using an
 * implementation-specific trust model.</p>
 * 
 */
public abstract class BaseSignatureTrustEngine extends AbstractTrustEngine<Signature> implements SignatureTrustEngine {
    
    /** Class logger. */
    private static Logger log = Logger.getLogger(BaseSignatureTrustEngine.class);
    
    /** KeyInfo credential resolver used to obtain the signing credential from a Signature's KeyInfo. */
    private KeyInfoCredentialResolver keyInfoCredentialResolver;
    
    /**
     * Constructor.
     *
     * @param resolver credential resolver used to resolve trusted credentials.
     * @param keyInfoResolver KeyInfo credential resolver used to obtain the (advisory) signing credential 
     *          from a Signature's KeyInfo element.
     */
    public BaseSignatureTrustEngine(CredentialResolver resolver, KeyInfoCredentialResolver keyInfoResolver) {
        if (resolver == null) {
            throw new IllegalArgumentException("Credential resolver may not be null");
        }
        if (keyInfoResolver == null) {
            throw new IllegalArgumentException("KeyInfo credential resolver may not be null");
        }
        
        setCredentialResolver(resolver);
        keyInfoCredentialResolver = keyInfoResolver;
    }

    /** {@inheritDoc} */
    public KeyInfoCredentialResolver getKeyInfoResolver() {
        return keyInfoCredentialResolver;
    }

    /** {@inheritDoc} */
    public boolean validate(byte[] signature, byte[] content, String sigAlg, Credential credential)
            throws SecurityException {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Attempt to establish trust by resolving signature verification credentials from the Signature's KeyInfo.
     * If any credentials so resolved correctly verify the signature, attempt to establish trust
     * using subclass-specific trust logic against each trusted credential as implemented in 
     * {@link #evaluateTrust(Credential, Credential)}.
     * 
     * @param signature the Signature to evaluate
     * @param trustedCredentials previously resolved trusted credentials
     * @return true if the signature is verified by any KeyInfo-derived credential which can
     *          be established as trusted, otherwise false
     * @throws SecurityException if an error occurs during signature verification or trust processing
     */
    protected boolean validate(Signature signature, Iterable<Credential> trustedCredentials) throws SecurityException {
        
        log.debug("Attempting to verify signature and establish trust using KeyInfo-derived credentials");
        
        if (signature.getKeyInfo() != null) {
            
            KeyInfoCredentialCriteria keyInfoCriteria = new KeyInfoCredentialCriteria(signature.getKeyInfo());
            CredentialCriteriaSet keyInfoCriteriaSet = new CredentialCriteriaSet(keyInfoCriteria);
            
            for (Credential kiCred : getKeyInfoResolver().resolveCredentials(keyInfoCriteriaSet)) {
                if (verifySignature(signature, kiCred)) {
                    log.debug("Successfully verified signature using KeyInfo-derived credential");
                    log.debug("Attempting to establish trust of KeyInfo-derived credential");
                    if (evaluateTrust(kiCred, trustedCredentials)) {
                        log.debug("Successfully established trust of KeyInfo-derived credential");
                        return true;
                    } else {
                        log.debug("Failed to establish trust of KeyInfo-derived credential");
                    }
                }
            }
        } else {
            log.info("Signature contained no KeyInfo element, could not resolve verification credentials");
        }

        log.debug("Failed to verify signature and/or establish trust using any KeyInfo-derived credentials");
        return false;
    }
    
    /**
     * Evaluate the untrusted credential with respect to the specified trusted credential.
     * 
     * @param untrustedCredential the untrusted credential being evaluated
     * @param trustedCredentials the credentials which serve as a basis for trust evaluation
     * 
     * @return true if the trust can be established for the untrusted credential, otherwise false
     * 
     * @throws SecurityException if an error occurs during trust processing
     */
    protected abstract boolean evaluateTrust(Credential untrustedCredential, Iterable<Credential> trustedCredentials)
            throws SecurityException;

    /**
     * Attempt to verify a signature using the key from the supplied credential.
     * 
     * @param signature the signature on which to attempt verification
     * @param credential the credential containing the candidate validation key
     * @return true if the signature can be verified using the key from the credential, otherwise false
     */
    protected boolean verifySignature(Signature signature, Credential credential) {
        SignatureValidator validator = new SignatureValidator(credential);
        try {
            validator.validate(signature);
        } catch (ValidationException e) {
            if (log.isDebugEnabled()) {
                log.debug("Signature validation using candidate validation credential failed", e);
            }
            return false;
        }
        if (log.isDebugEnabled()) {
            log.debug("Signature validation using candidate credential was successful");
        }
        return true;
    }
    
    /**
     * Check the signature and credential criteria for required values.
     * 
     * @param signature the signature to be evaluated
     * @param trustedCredentialCriteria the set of trusted credential criteria
     * @throws SecurityException thrown if required values are absent or otherwise invalid
     */
    protected void checkParams(Signature signature, CredentialCriteriaSet trustedCredentialCriteria) 
            throws SecurityException {
        
        if (signature == null) {
            throw new SecurityException("Signature was null");
        }
        if (trustedCredentialCriteria == null) {
            throw new SecurityException("Trusted credential criteria set was null");
        }
        if (! trustedCredentialCriteria.contains(UsageCredentialCriteria.class)) {
            trustedCredentialCriteria.add( new UsageCredentialCriteria(UsageType.SIGNING) ); 
        }
        
    }

}
