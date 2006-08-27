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

import org.apache.log4j.Logger;
import org.apache.xml.security.Init;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.SignedInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Element;

/**
 * An unmarshaller for {@link org.opensaml.xml.signature.Signature} objects.
 */
public class SignatureUnmarshaller implements Unmarshaller {

    /** Logger */
    private static Logger log = Logger.getLogger(SignatureUnmarshaller.class);

    /**
     * Constructor
     */
    public SignatureUnmarshaller() {
        if (!Init.isInitialized()) {
            if (log.isDebugEnabled()) {
                log.debug("Initializing XML security library");
            }
            Init.init();
        }
    }

    /** {@inheritDoc} */
    public Signature unmarshall(Element signatureElement) throws UnmarshallingException {
        if (log.isDebugEnabled()) {
            log.debug("Starting to unmarshall XMLSecSignatureImpl element");
        }
        Signature signature = new Signature(signatureElement.getNamespaceURI(), signatureElement.getLocalName(),
                signatureElement.getPrefix());

        try {
            if (log.isDebugEnabled()) {
                log.debug("Constructing XMLSignature object");
            }

            XMLSignature xmlSignature = new XMLSignature(signatureElement, "");

            SignedInfo signedInfo = xmlSignature.getSignedInfo();
            if (log.isDebugEnabled()) {
                log.debug("Adding Canonicalization, Digest, and XMLSecSignatureImpl methods to signing context");
            }
            signature.setCanonicalizationAlgorithm(signedInfo.getCanonicalizationMethodURI());
            signature.setSignatureAlgorithm(signedInfo.getSignatureMethodURI());

            org.apache.xml.security.keys.KeyInfo xmlSecKeyInfo = xmlSignature.getKeyInfo();
            if (xmlSecKeyInfo != null) {
                Unmarshaller unmarshaller = Configuration.getUnmarshallerFactory().getUnmarshaller(
                        xmlSecKeyInfo.getElement());
                KeyInfo keyInfo = (KeyInfo) unmarshaller.unmarshall(xmlSecKeyInfo.getElement());
                signature.setKeyInfo(keyInfo);
            }

            if (log.isDebugEnabled()) {
                log
                        .debug("Creating new XMLSecSignatureImpl XMLObject with created SigningContext and XMLSignature objects");
            }
            signature.setXMLSignature(xmlSignature);

            return signature;
        } catch (XMLSecurityException e) {
            throw new UnmarshallingException("Unable to unmarshall XMLSecSignatureImpl", e);
        }
    }
}