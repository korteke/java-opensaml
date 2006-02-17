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

/**
 * 
 */

package org.opensaml.xml.signature;

import org.apache.log4j.Logger;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.signature.Reference;
import org.apache.xml.security.signature.SignedInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.w3c.dom.Element;

/**
 * An unmarshaller for {@link org.opensaml.xml.signature.Signature} objects. This unmarshaller does NOT verify the
 * signature but instead simply creates the appropriate signing context and XMLSignature objects attached to the
 * Signature object. Verification of the signature is handled by the unmarshaller for the parent object of the Signature
 * by way of the {@link org.opensaml.xml.io.AbstractXMLObjectUnmarshaller}.
 */
public class SignatureUnmarshaller implements Unmarshaller<XMLObject> {

    /** Logger */
    private static Logger log = Logger.getLogger(SignatureUnmarshaller.class);

    /*
     * @see org.opensaml.xml.io.Unmarshaller#unmarshall(org.w3c.dom.Element)
     */
    public Signature unmarshall(Element signatureElement) throws UnmarshallingException {
        if (log.isDebugEnabled()) {
            log.debug("Starting to unmarshall Signature element");
        }
        SigningContext signatureContext = new SigningContext();
        Signature signature = new Signature(signatureContext);

        try {
            if (log.isDebugEnabled()) {
                log.debug("Constructing XMLSignature object");
            }

            XMLSignature xmlSignature = new XMLSignature(signatureElement, "");

            SignedInfo signedInfo = xmlSignature.getSignedInfo();
            if (log.isDebugEnabled()) {
                log.debug("Adding Canonicalization, Digest, and Signature methods to signing context");
            }
            signatureContext.setCanonicalizationAlgortihm(signedInfo.getCanonicalizationMethodURI());
            signatureContext.setSignatureAlgorithm(signedInfo.getSignatureMethodURI());

            Reference documentReference = signedInfo.item(0);
            if (documentReference != null) {
                signatureContext.setDigestAlgorithm(documentReference.getMessageDigestAlgorithm().getAlgorithmURI());

                if(log.isDebugEnabled()){
                    log.debug("Setting Signature reference URI to " + documentReference.getURI());
                }
                signature.setReferenceURI(documentReference.getURI());
                
                if (log.isDebugEnabled()) {
                    log.debug("Adding transforms to signing context");
                }
                Transforms documentTransforms = documentReference.getTransforms();
                if (documentTransforms != null) {
                    for (int i = 0; i < documentTransforms.getLength(); i++) {
                        signatureContext.getTransforms().add(documentTransforms.item(i).getURI());
                    }
                }
            }

            KeyInfo keyInfo = xmlSignature.getKeyInfo();
            if (keyInfo != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Adding any public key data to signing context");
                }
                signatureContext.setPublicKey(keyInfo.getPublicKey());

                if (log.isDebugEnabled()) {
                    log.debug("Adding any X509 certificates to signing context");
                }
                X509Data x509data = keyInfo.itemX509Data(0);
                if (x509data != null) {
                    for (int i = 0; i < x509data.lengthCertificate(); i++) {
                        signatureContext.getCertificates().add(x509data.itemCertificate(i).getX509Certificate());
                    }
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("Creating new Signature XMLObject with created SigningContext and XMLSignature objects");
            }
            signature.setXMLSignature(xmlSignature);
            signature.setReferenceURI(xmlSignature.getId());

            return signature;
        } catch (XMLSecurityException e) {
            throw new UnmarshallingException("Unable to unmarshall Signature", e);
        }
    }
}