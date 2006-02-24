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

import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.xml.security.Init;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.transforms.params.InclusiveNamespaces;
import org.opensaml.xml.Namespace;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.signature.SignableXMLObject;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureMarshaller;
import org.opensaml.xml.signature.SigningContext;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A marshaller for {@link org.opensaml.xml.signature.impl.XMLSecSignatureImpl} objects. This class, along with it's
 * respective builder and unmarshaller use the Apache XMLSec 1.3 APIs to perform signing and verification.
 */
public class XMLSecSignatureMarshaller implements SignatureMarshaller {

    /** Logger */
    private static Logger log = Logger.getLogger(XMLSecSignatureMarshaller.class);

    /**
     * Constructor
     */
    public XMLSecSignatureMarshaller() {
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
    public Element marshall(XMLObject xmlObject) throws MarshallingException{
        try{
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            return marshall(xmlObject, document);
        }catch(ParserConfigurationException e){
            throw new MarshallingException("Unable to create Document to place marshalled elements in", e);
        }
    }

    /*
     * @see org.opensaml.xml.io.Marshaller#marshall(org.opensaml.xml.XMLObject, org.w3c.dom.Document)
     */
    public Element marshall(XMLObject xmlObject, Document document) throws MarshallingException {
        XMLSecSignatureImpl signature = (XMLSecSignatureImpl) xmlObject;

        if (log.isDebugEnabled()) {
            log.debug("Starting to marshall " + xmlObject.getElementQName());
        }

        XMLObject parentXMLObject = xmlObject.getParent();
        if (!(parentXMLObject instanceof SignableXMLObject)) {
            throw new MarshallingException(
                    "Parent XMLObject was not an instance of SignableXMLObject, can not create digital signature");
        }

        SigningContext signatureContext = signature.getSigningContext();

        try {
            if (log.isDebugEnabled()) {
                log.debug("Creating XMLSignature object");
            }
            XMLSignature dsig = new XMLSignature(document, "", signatureContext.getSignatureAlgorithm(),
                    Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);

            if (signatureContext.getPublicKey() != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Adding public keys to signature key info");
                }
                dsig.addKeyInfo(signatureContext.getPublicKey());
            }

            if (signatureContext.getCertificates() != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Adding X.509 certifiacte(s) into signature's X509 data");
                }
                for (X509Certificate cert : signatureContext.getCertificates()) {
                    dsig.addKeyInfo(cert);
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("Adding content transforms to XMLSignature.");
            }
            Transforms dsigTransforms = new Transforms(dsig.getDocument());
            for (String transform : signatureContext.getTransforms()) {
                if (log.isDebugEnabled()) {
                    log.debug("Adding content transform " + transform);
                }
                dsigTransforms.addTransform(transform);
            }

            // Namespaces that aren't visibly used, such as those used in QName attribute values, would
            // be stripped out by exclusive canonicalization. Need to make sure they aren't by explicitly
            // telling the transformer about them.
            if (log.isDebugEnabled()) {
                log.debug("Adding namespaces to list of inclusive namespaces for signature");
            }
            Set<String> inclusiveNamespacePrefixes = new HashSet<String>();
            for (Namespace namespace : parentXMLObject.getNamespaces()) {
                inclusiveNamespacePrefixes.add(namespace.getNamespacePrefix());
            }

            if (inclusiveNamespacePrefixes != null && inclusiveNamespacePrefixes.size() > 0) {
                InclusiveNamespaces inclusiveNamespaces = new InclusiveNamespaces(document, inclusiveNamespacePrefixes);
                Element transformElem = dsigTransforms.item(1).getElement();
                transformElem.appendChild(inclusiveNamespaces.getElement());
            }

            // Add a reference to the document to be signed
            if (log.isDebugEnabled()) {
                log.debug("Adding in-document URI ID based reference to content being signed");
            }
            dsig.addDocument(signature.getReferenceURI(), dsigTransforms, signatureContext.getDigestAlgorithm());

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
        XMLSecSignatureImpl xmlsecSignature = (XMLSecSignatureImpl) signature;
        SigningContext signingCtx = xmlsecSignature.getSigningContext();
        XMLSignature dsig = xmlsecSignature.getXMLSignature();

        try {
            dsig.sign(signingCtx.getSigningKey());
        } catch (XMLSignatureException e) {
            log.error("Unable compute digital signature for " + XMLHelper.getNodeQName(domElement), e);
            throw new MarshallingException(
                    "Unable compute digital signature for " + XMLHelper.getNodeQName(domElement), e);
        }
    }
}