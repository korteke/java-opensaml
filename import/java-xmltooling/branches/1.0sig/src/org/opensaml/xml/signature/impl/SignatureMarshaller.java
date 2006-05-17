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

package org.opensaml.xml.signature.impl;

import java.security.PublicKey;
import java.security.cert.X509Certificate;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.xml.security.Init;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.signature.ContentReference;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.SignableXMLObject;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A marshaller for {@link org.opensaml.xml.signature.impl.SignatureImpl} objects. This class, along with it's
 * respective builder and unmarshaller use the Apache XMLSec 1.3 APIs to perform signing and verification.
 */
public class SignatureMarshaller implements org.opensaml.xml.signature.SignatureMarshaller {

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

    /*
     * @see org.opensaml.xml.io.Marshaller#marshall(org.opensaml.xml.XMLObject)
     */
    public Element marshall(XMLObject xmlObject) throws MarshallingException {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            return marshall(xmlObject, document);
        } catch (ParserConfigurationException e) {
            throw new MarshallingException("Unable to create Document to place marshalled elements in", e);
        }
    }

    /*
     * @see org.opensaml.xml.io.Marshaller#marshall(org.opensaml.xml.XMLObject, org.w3c.dom.Element)
     */
    public Element marshall(XMLObject xmlObject, Element parentElement) throws MarshallingException {
        return marshall(xmlObject, parentElement.getOwnerDocument());
    }

    /*
     * @see org.opensaml.xml.io.Marshaller#marshall(org.opensaml.xml.XMLObject, org.w3c.dom.Document)
     */
    public Element marshall(XMLObject xmlObject, Document document) throws MarshallingException {
        SignatureImpl signature = (SignatureImpl) xmlObject;

        if (log.isDebugEnabled()) {
            log.debug("Starting to marshall " + xmlObject.getElementQName());
        }

        XMLObject parentXMLObject = xmlObject.getParent();
        if (!(parentXMLObject instanceof SignableXMLObject)) {
            throw new MarshallingException(
                    "Parent XMLObject was not an instance of SignableXMLObject, can not create digital signature");
        }

        try {
            if (log.isDebugEnabled()) {
                log.debug("Creating XMLSignature object");
            }
            XMLSignature dsig = new XMLSignature(document, "", signature.getSignatureAlgorithm(), signature
                    .getCanonicalizationAlgorithm());

            KeyInfo keyInfo = signature.getKeyInfo();
            if (signature.getKeyInfo() != null) {
                if (keyInfo.getKeys() != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Adding public keys to signature key info");
                    }
                    for (PublicKey key : keyInfo.getKeys()) {
                        dsig.addKeyInfo(key);
                    }
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
                log.debug("Added XMLSignature to Signature XMLObject");
            }
            signature.setXMLSignature(dsig);

            if (log.isDebugEnabled()) {
                log.debug("Creating XMLSecSignatureImpl DOM element");
            }
            return dsig.getElement();

        } catch (XMLSecurityException e) {
            log.error("Unable to construct signature Element " + xmlObject.getElementQName(), e);
            throw new MarshallingException("Unable to construct signature Element " + xmlObject.getElementQName(), e);
        }
    }

    /*
     * @see org.opensaml.xml.signature.SignatureMarshaller#signElement(org.w3c.dom.Element,
     *      org.opensaml.xml.signature.Signature)
     */
    public void signElement(Element domElement, Signature signature) throws MarshallingException {
        SignatureImpl xmlsecSignature = (SignatureImpl) signature;
        XMLSignature dsig = xmlsecSignature.getXMLSignature();

        try {
            dsig.sign(xmlsecSignature.getSigningKey());
        } catch (XMLSignatureException e) {
            log.error("Unable compute digital signature for " + XMLHelper.getNodeQName(domElement), e);
            throw new MarshallingException(
                    "Unable compute digital signature for " + XMLHelper.getNodeQName(domElement), e);
        }
    }
}