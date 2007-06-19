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

package org.opensaml.xml.signature;

import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xml.security.trust.TrustEngine;

/**
 * Evaluates the trustworthiness and validity of XML or raw Signatures against implementation-specific requirements.
 */
public interface SignatureTrustEngine extends TrustEngine<Signature> {
    
    /**
     * Get the KeyInfoCredentialResolver instance used to resolve (advisory) signing credential information
     * from KeyInfo elements contained within a Signature element.
     * 
     * Note that credential(s) obtained via this resolver are not themselves trusted.  They must be evaluated
     * against the trusted credential information obtained from the trusted credential resolver.
     * 
     * @return a KeyInfoCredentialResolver instance
     */
    public KeyInfoCredentialResolver getKeyInfoResolver();

    /**
     * Determines whether a raw signature is valid with respect to the Credential data supplied.
     * 
     * It is the responsibility of the application to ensure that the Credential information supplied is in
     * fact associated with the peer who created the signature.
     * 
     * @param signature the signature value
     * @param content the content that was signed
     * @param algorithm the signature algorithm used
     * @param credential credential containing the validation key for the signature
     * 
     * @return true if the signature was valid for the provided content
     * 
     * @throws SecurityException thrown if there is a problem attempting to verify the signature such as the signature
     *             algorithim not being supported
     */
    public boolean validate(byte[] signature, byte[] content, String algorithm, Credential credential) 
        throws SecurityException;
}