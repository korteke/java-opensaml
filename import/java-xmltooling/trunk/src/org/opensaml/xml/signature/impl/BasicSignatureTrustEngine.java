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

import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.credential.CredentialCriteriaSet;
import org.opensaml.xml.security.credential.CredentialResolver;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xml.security.trust.AbstractTrustEngine;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureTrustEngine;

/**
 * A basic implementation of {@link SignatureTrustEngine} which evaluates the validity and trustworthiness
 * of XML and raw signatures.
 * 
 * When processing XML signatures, the supplied KeyInfoCredentialResolver will be used to resolve credential(s)
 * containing the (advisory) signing key from the KeyInfo element of the Signature, if present.  If any of these
 * credentials do contain the valid signing key, they will be evaluated for trustworthiness against the 
 * set of trusted credentials supplied by the trusted credential resolver. If the Signature's KeyInfo is not
 * present or does not contain a valid signing key, then all trusted credentials obtained by the trusted credential
 * resolver will be used to attempt to validate the signature.
 */
public class BasicSignatureTrustEngine extends AbstractTrustEngine<Signature> implements SignatureTrustEngine {
    
    /** KeyInfo credential resolver used to obtain the signing credential from a Signature's KeyInfo. */
    private KeyInfoCredentialResolver keyInfoCredentialResolver;
    
    /**
     * Constructor.
     *
     * @param resolver credential resolver used to resolve trusted credentials.
     * @param keyInfoResolver KeyInfo credential resolver used to obtain the (advisory) signing credential 
     *          from a Signature's KeyInfo element.
     */
    public BasicSignatureTrustEngine(CredentialResolver resolver, KeyInfoCredentialResolver keyInfoResolver) {
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

    /** {@inheritDoc} */
    public boolean validate(Signature token, CredentialCriteriaSet trustedCredentialCriteria) throws SecurityException {
        // TODO Auto-generated method stub
        return false;
    }

}
