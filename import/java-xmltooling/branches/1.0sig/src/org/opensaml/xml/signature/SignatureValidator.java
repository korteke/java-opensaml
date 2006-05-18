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

import java.security.Key;

import org.apache.log4j.Logger;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.validation.ValidationException;
import org.opensaml.xml.validation.Validator;

/**
 * A validator that verifies an {@link org.opensaml.xml.signature.impl.SignatureImpl} against a given key.
 */
public class SignatureValidator implements Validator {

    /** Logger */
    private static Logger log = Logger.getLogger(SignatureValidator.class);

    /** Key used to verify the signature */
    private Key verificationKey;

    /**
     * Constructor
     * 
     * @throws IllegalArgumentException thrown if the verification key is null
     */
    public SignatureValidator(Key verificationKey) throws IllegalArgumentException {
        if (verificationKey == null) {
            throw new IllegalArgumentException("Verification key may not be null");
        }
        this.verificationKey = verificationKey;
    }

    /** {@inheritDoc} */
    public void validate(XMLObject xmlObject) throws ValidationException {
        Signature signature = (Signature) xmlObject;

        if (signature == null) {
            XMLSignature xmlSignature = signature.getXMLSignature();
            if (log.isDebugEnabled()) {
                log.debug("Attempting to validate digital signature using provided key");
            }
            try {
                if (xmlSignature.checkSignatureValue(verificationKey)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Digital signature validated successfully");
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Digital signature could not be validated with given key");
                    }
                    throw new ValidationException("Digital signature does not validate with the given key");
                }
            } catch (XMLSignatureException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Digital signature could not be validated with given key", e);
                }
                throw new ValidationException("Digital signature does not validate with the given key", e);
            }
        }
    }
}