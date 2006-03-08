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

package org.opensaml.xml.signature.impl;

import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xml.security.Init;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.signature.Reference;
import org.apache.xml.security.signature.SignedInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureUnmarshaller;
import org.opensaml.xml.signature.SigningContext;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Element;

/**
 * An unmarshaller for {@link org.opensaml.xml.signature.impl.XMLSecSignatureImpl} objects. This class, along with it's
 * respective builder and marshaller use the Apache XMLSec 1.3 APIs to perform signing and verification.
 */
public class XMLSecSignatureUnmarshaller implements SignatureUnmarshaller {

    /** Logger */
    private static Logger log = Logger.getLogger(XMLSecSignatureUnmarshaller.class);
    
    /**
     * Constructor
     */
    public XMLSecSignatureUnmarshaller(){
        if (!Init.isInitialized()) {
            if (log.isDebugEnabled()) {
                log.debug("Initializing XML security library");
            }
            Init.init();
        }
    }

    /*
     * @see org.opensaml.xml.io.Unmarshaller#unmarshall(org.w3c.dom.Element)
     */
    public XMLSecSignatureImpl unmarshall(Element signatureElement) throws UnmarshallingException {
        if (log.isDebugEnabled()) {
            log.debug("Starting to unmarshall XMLSecSignatureImpl element");
        }
        SigningContext signatureContext = new SigningContext();
        XMLSecSignatureImpl signature = new XMLSecSignatureImpl(signatureContext);

        try {
            if (log.isDebugEnabled()) {
                log.debug("Constructing XMLSignature object");
            }

            XMLSignature xmlSignature = new XMLSignature(signatureElement, "");

            SignedInfo signedInfo = xmlSignature.getSignedInfo();
            if (log.isDebugEnabled()) {
                log.debug("Adding Canonicalization, Digest, and XMLSecSignatureImpl methods to signing context");
            }
            signatureContext.setCanonicalizationAlgortihm(signedInfo.getCanonicalizationMethodURI());
            signatureContext.setSignatureAlgorithm(signedInfo.getSignatureMethodURI());

            Reference documentReference = signedInfo.item(0);
            if (documentReference != null) {
                signatureContext.setDigestAlgorithm(documentReference.getMessageDigestAlgorithm().getAlgorithmURI());

                if (log.isDebugEnabled()) {
                    log.debug("Setting XMLSecSignatureImpl reference URI to " + documentReference.getURI());
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
                log
                        .debug("Creating new XMLSecSignatureImpl XMLObject with created SigningContext and XMLSignature objects");
            }
            signature.setXMLSignature(xmlSignature);
            signature.setReferenceURI(xmlSignature.getId());

            return signature;
        } catch (XMLSecurityException e) {
            throw new UnmarshallingException("Unable to unmarshall XMLSecSignatureImpl", e);
        }
    }

    /*
     * @see org.opensaml.xml.signature.SignatureUnmarshaller#verifySignature(org.w3c.dom.Element,
     *      org.opensaml.xml.signature.Signature)
     */
    public void verifySignature(Element domElement, Signature signature) throws UnmarshallingException {
        XMLSecSignatureImpl xmlsecSignature = (XMLSecSignatureImpl) signature;

        try {
            XMLSignature xmlSignature = xmlsecSignature.getXMLSignature();

            List<X509Certificate> certs = signature.getSigningContext().getCertificates();
            if (certs.size() > 0 && log.isDebugEnabled()) {
                log.debug("Attempting to validate digital signature using certifcates found in the signature");
            }
            for (X509Certificate cert : certs) {
                if (xmlSignature.checkSignatureValue(cert)) {
                    if (log.isDebugEnabled()) {
                        log.debug("XMLSecSignatureImpl for ELement " + XMLHelper.getNodeQName(domElement)
                                + " validated with certifcate with subject DN of " + cert.getSubjectDN());
                    }
                    signature.getSigningContext().setValidatingCertificate(cert);
                    return;
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("XMLSecSignatureImpl for Element " + XMLHelper.getNodeQName(domElement)
                                + " could not be validated with certifcates found in signature");
                    }
                }
            }

            PublicKey publicKey = signature.getSigningContext().getPublicKey();
            if (publicKey != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Attempting to validate digital signature using public key found in the signature");
                }
                if (xmlSignature.checkSignatureValue(publicKey)) {
                    if (log.isDebugEnabled()) {
                        log.debug("XMLSecSignatureImpl for Element " + XMLHelper.getNodeQName(domElement)
                                + " validated with public key found in signature.");
                    }
                    return;
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("XMLSecSignatureImpl for Element " + XMLHelper.getNodeQName(domElement)
                                + " could not be validated with public key found in signature");
                    }
                }
            }

            String errorMsg = "Element " + XMLHelper.getNodeQName(domElement)
                    + " could not be validated with any certificates or public key included with the digital signature";
            log.error(errorMsg);
            throw new UnmarshallingException(errorMsg);
        } catch (XMLSecurityException e) {
            String errorMsg = "Received the following error when attempting to validate the signature for Element "
                    + XMLHelper.getNodeQName(domElement);
            log.error(errorMsg, e);
            throw new UnmarshallingException(errorMsg, e);
        }
    }
}