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

package org.opensaml.xml.security;

import org.opensaml.xml.security.credential.Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class for computing and verifying raw signatures and MAC values.
 */
public final class XMLSigningUtil {

    /** Constructor. */
    private XMLSigningUtil() {
    }

    /**
     * Compute the signature or MAC value over the supplied input.
     * 
     * It is up to the caller to ensure that the specified algorithm URI is consistent with the type of signing key
     * supplied in the signing credential.
     * 
     * @param signingCredential the credential containing the signing key
     * @param algorithmURI the algorithm URI to use
     * @param input the input over which to compute the signature
     * @return the computed signature or MAC value
     * @throws SecurityException throw if the computation process results in an error
     */
    public static byte[] signWithURI(Credential signingCredential, String algorithmURI, byte[] input)
            throws SecurityException {

        String jcaAlgorithmID = XMLSecurityHelper.getAlgorithmIDFromURI(algorithmURI);
        if (jcaAlgorithmID == null) {
            throw new SecurityException("Could not derive JCA algorithm identifier from algorithm URI");
        }

        boolean isHMAC = XMLSecurityHelper.isHMAC(algorithmURI);

        return SigningUtil.sign(signingCredential, jcaAlgorithmID, isHMAC, input);
    }
    
    /**
     * Verify the signature value computed over the supplied input against the supplied signature value.
     * 
     * It is up to the caller to ensure that the specified algorithm URI are consistent with the type of verification
     * credential supplied.
     * 
     * @param verificationCredential the credential containing the verification key
     * @param algorithmURI the algorithm URI to use
     * @param signature the computed signature value received from the signer
     * @param input the input over which the signature is computed and verified
     * @return true if the signature value computed over the input using the supplied key and algorithm ID is identical
     *         to the supplied signature value
     * @throws SecurityException thrown if the signature computation or verification process results in an error
     */
    public static boolean verifyWithURI(Credential verificationCredential, String algorithmURI, byte[] signature,
            byte[] input) throws SecurityException {

        String jcaAlgorithmID = XMLSecurityHelper.getAlgorithmIDFromURI(algorithmURI);
        if (jcaAlgorithmID == null) {
            throw new SecurityException("Could not derive JCA algorithm identifier from algorithm URI");
        }

        boolean isHMAC = XMLSecurityHelper.isHMAC(algorithmURI);

        return SigningUtil.verify(verificationCredential, jcaAlgorithmID, isHMAC, signature, input);
    }

    
    /**
     * Get an SLF4J Logger.
     * 
     * @return a Logger instance
     */
    private static Logger getLogger() {
        return LoggerFactory.getLogger(XMLSigningUtil.class);
    }
}
