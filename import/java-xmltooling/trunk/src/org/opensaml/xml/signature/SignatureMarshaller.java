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

import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.transforms.params.InclusiveNamespaces;
import org.opensaml.xml.Namespace;
import org.opensaml.xml.SignableXMLObject;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A marshaller for {@link org.opensaml.xml.signature.Signature} objects that creates the XML Digital Signature element
 * Signature and its children. This marshaller does not do the actual signing.  Creation of the signature is handled by the
 * marshaller of the for the parent object of the Signature by way of the {@link org.opensaml.xml.io.AbstractXMLObjectMarshaller}.
 */
public class SignatureMarshaller implements Marshaller<XMLObject> {

    /** Logger */
    private static Logger log = Logger.getLogger(SignatureMarshaller.class);

    /**
     * Constructor
     */
    public SignatureMarshaller() {

    }

    /*
     * @see org.opensaml.xml.io.Marshaller#marshall(T, org.w3c.dom.Document)
     */
    public Element marshall(XMLObject xmlObject, Document document) throws MarshallingException {
        Signature signature = (Signature) xmlObject;

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
            for(String transform : signatureContext.getTransforms()) {
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
            dsig.addDocument("#" + signature.getId(), dsigTransforms, signatureContext.getDigestAlgorithm());

            signature.setXMLSignature(dsig);

            if (log.isDebugEnabled()) {
                log.debug("Creating Signature DOM element");
            }
            return dsig.getElement();

        } catch (XMLSecurityException e) {
            log.error("Unable to construct signature Element " + xmlObject.getElementQName(), e);
            throw new MarshallingException("Unable to construct signature Element " + xmlObject.getElementQName(), e);
        }
    }
}