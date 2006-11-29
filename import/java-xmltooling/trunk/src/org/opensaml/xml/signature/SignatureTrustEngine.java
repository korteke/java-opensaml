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

import org.opensaml.xml.security.KeyInfoSource;
import org.opensaml.xml.security.KeyResolver;

/**
 * Evaluates the trustworthiness and validity of XML or raw Signatures against implementation-specific requirements.
 */
public interface SignatureTrustEngine {

    /**
     * Verifies that the given signable object are valid.
     * 
     * @param xmlObject the singable XML object
     * @param keyInfo keying information source
     * @param keyResolver resolver to extract keys from keying information
     * 
     * @return true if the xmlObject contained a valid signature
     */
    public boolean validate(SignableXMLObject xmlObject, KeyInfoSource keyInfo, KeyResolver keyResolver);

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
     * @param keyInfo information about the key to use to validate the signature
     * @param keyResolver resolver to convert KeyInfo objects into keys used to validate the signature
     * 
     * @return true if the signature was valid for the provided content
     */
    public boolean validate(byte[] signature, byte[] content, String sigAlg, KeyInfoSource keyInfo,
            KeyResolver keyResolver);
}