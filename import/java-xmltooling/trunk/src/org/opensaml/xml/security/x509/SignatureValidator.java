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

package org.opensaml.xml.security.x509;

import java.security.PublicKey;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.impl.SignatureImpl;
import org.opensaml.xml.validation.ValidationException;
import org.opensaml.xml.validation.Validator;

/**
 * A validator that validates an XML Signature on its content.
 */
public class SignatureValidator implements Validator<Signature> {

    /** Class logger. */
    private static Logger log = Logger.getLogger(SignatureValidator.class);

    /** Credential used to validate signature. */
    private X509Credential validationCredential;

    /**
     * Constructor.
     * 
     * @param validatingCredential credential used to validate the signature
     */
    public SignatureValidator(X509Credential validatingCredential) {
        validationCredential = validatingCredential;
    }

    /** {@inheritDoc} */
    public void validate(Signature signature) throws ValidationException {
        if (log.isDebugEnabled()) {
            log.debug("Verify digital signature with against keying information");
        }

        XMLSignature xmlSig = buildSignature(signature);

        Collection<PublicKey> validationKeys = validationCredential.getPublicKeys();
        if (validationKeys == null || validationKeys.size() < 1) {
            throw new ValidationException("No public keys available to validate signature");
        }

        try {
            for (PublicKey trustedKey : validationKeys) {
                if (xmlSig.checkSignatureValue(trustedKey)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Signature validated with public keys from credential");
                    }
                    return;
                }
            }
        } catch (XMLSignatureException e) {
            throw new ValidationException("Unable to evaluate key against signature", e);
        }

        if (log.isDebugEnabled()) {
            log.debug("Signature did not validate against any public keys from credential");
        }
        return;
    }

    /**
     * Constructs an {@link XMLSignature} from the given signature object.
     * 
     * @param signature the signature
     * 
     * @return the constructed XMLSignature
     */
    protected XMLSignature buildSignature(Signature signature) {
        if (log.isDebugEnabled()) {
            log.debug("Creating XMLSignature object");
        }
        return ((SignatureImpl) signature).getXMLSignature();
    }
}