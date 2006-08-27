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

import java.security.cert.X509Certificate;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.xml.security.Init;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A marshaller for {@link org.opensaml.xml.signature.Signature} objects.  This marshaller is really a no-op
 * class.  All the creation of the signature DOM elements is handled by {@link org.opensaml.xml.signature.Signer} 
 * when it signs the object.
 */
public class SignatureMarshaller implements Marshaller {

    /** Logger */
    private static Logger log = Logger.getLogger(SignatureMarshaller.class);

    /**
     * Constructor
     */
    public SignatureMarshaller() {
        if (!Init.isInitialized()) {
            if (log.isDebugEnabled()) {
                log.debug("Initializing XML security library");
            }
            Init.init();
        }
    }

    /** {@inheritDoc} */
    public Element marshall(XMLObject xmlObject) throws MarshallingException {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            return marshall(xmlObject, document);
        } catch (ParserConfigurationException e) {
            throw new MarshallingException("Unable to create Document to place marshalled elements in", e);
        }
    }

    /** {@inheritDoc} */
    public Element marshall(XMLObject xmlObject, Element parentElement) throws MarshallingException {
        Element signatureElement = createSignatureElement((Signature) xmlObject, parentElement.getOwnerDocument());
        XMLHelper.appendChildElement(parentElement, signatureElement);
        return signatureElement;
    }

    /** {@inheritDoc} */
    public Element marshall(XMLObject xmlObject, Document document) throws MarshallingException {
        Element signatureElement = createSignatureElement((Signature) xmlObject, document);
        
        Element documentRoot = document.getDocumentElement();
        if (documentRoot != null) {
            document.replaceChild(documentRoot, signatureElement);
        } else {
            document.appendChild(signatureElement);
        }
        
        return signatureElement;
    }
    
    /**
     * Creates the signature elements but does not compute the signatuer.
     * 
     * @param signature the XMLObject to be signed
     * @param document the owning document
     * 
     * @return the Signature element
     * 
     * @throws MarshallingException thrown if the signature can not be constructed
     */
    private Element createSignatureElement(Signature signature, Document document) throws MarshallingException {
        if (log.isDebugEnabled()) {
            log.debug("Starting to marshall " + signature.getElementQName());
        }

        try {
            if (log.isDebugEnabled()) {
                log.debug("Creating XMLSignature object");
            }
            XMLSignature dsig = new XMLSignature(document, "", signature.getSignatureAlgorithm(), signature
                    .getCanonicalizationAlgorithm());

            KeyInfo keyInfo = signature.getKeyInfo();
            if (signature.getKeyInfo() != null) {
                if (keyInfo.getPublicKey() != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Adding public key to signature key info");
                    }
                    dsig.addKeyInfo(keyInfo.getPublicKey());
                }

                if (keyInfo.getCertificates() != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Adding X.509 certifiacte(s) into signature's X509 data");
                    }
                    for (X509Certificate cert : keyInfo.getCertificates()) {
                        dsig.addKeyInfo(cert);
                    }
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("Adding content to XMLSignature.");
            }
            for (ContentReference contentReference : signature.getContentReferences()) {
                contentReference.createReference(dsig);
            }

            if (log.isDebugEnabled()) {
                log.debug("Creating Signature DOM element");
            }
            signature.setXMLSignature(dsig);
            Element signatureElement = dsig.getElement();
            signature.setDOM(signatureElement);
            return signatureElement;

        } catch (XMLSecurityException e) {
            log.error("Unable to construct signature Element " + signature.getElementQName(), e);
            throw new MarshallingException("Unable to construct signature Element " + signature.getElementQName(), e);
        }
    }
}