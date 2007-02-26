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
import org.opensaml.xml.security.trust.TrustEngine;

/**
 * Evaluates the trustworthiness and validity of XML or raw Signatures against implementation-specific requirements.
 * 
 * @param <TrustedCredentialType> type of credential to validate the signature against
 */
public interface SignatureTrustEngine<TrustedCredentialType extends Credential> extends
        TrustEngine<Signature, TrustedCredentialType> {

    /**
     * Determines whether a raw signature is correct and valid with respect to the source of KeyInfo data supplied. It
     * is the responsibility of the application to ensure that the KeyInfo information supplied is in fact associated
     * with the peer who created the signature.
     * 
     * Note that the keyInfo parameter is not part of the implicitly trusted set of key information supplied via the
     * KeyInfoSource, but rather advisory data that may have accompanied the signature itself.
     * 
     * @param signature the signature value
     * @param content the content that was signed
     * @param sigAlg the signature algorithm used
     * @param trustedCredential credential information to validate signature against
     * 
     * @return true if the signature was valid for the provided content
     * 
     * @throws SecurityException thrown if there is a problem attempting to verify the signature such as the signature
     *             algorithim not being supported
     */
    public boolean validate(byte[] signature, byte[] content, String sigAlg, TrustedCredentialType trustedCredential)
            throws SecurityException;
}