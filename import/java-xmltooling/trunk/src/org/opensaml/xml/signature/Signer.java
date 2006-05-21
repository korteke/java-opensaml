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

import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xml.security.Init;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.w3c.dom.Element;

/**
 * This class is responsible for creating the digital signatures for the given signable XMLObjects.
 * 
 * This must be done as a seperate step because in order to support the following cases:
 * <ul>
 * <li>Multiple signable objects appear in the DOM tree, in which case the order that the objects should be signed in
 * is not known (e.g. object 1 could appear first in the tree, but contain a reference to signable object 2)</li>
 * <li>The DOM tree resulting from marshalling of the XMLObject tree is grafted onto another DOM tree which may cause
 * element ID conflicts that would invalidate the signature</li>
 * </ul>
 */
public class Signer {

    /** Logger */
    private static Logger log = Logger.getLogger(Signer.class);

    /**
     * Signs the given XMLObject in the order provided.
     * 
     * @param xmlObjects an orderded list of XMLObject to be signed
     */
    public static void signObjects(List<SignableXMLObject> xmlObjects) {
        for (SignableXMLObject xmlObject : xmlObjects) {
            signObject(xmlObject);
        }
    }

    /**
     * Signs a single XMLObject.
     * 
     * @param signableXMLObject the object to be signed
     */
    public static void signObject(SignableXMLObject signableXMLObject) {
        Element signableDOM = signableXMLObject.getDOM();
        Signature signatureXMLObject = signableXMLObject.getSignature();

        if (log.isDebugEnabled()) {
            log.debug("Starting to digitally sign " + signableXMLObject.getElementQName());
        }

        try {
            if (log.isDebugEnabled()) {
                log.debug("Creating XMLSignature object");
            }
            XMLSignature xmlSignature = new XMLSignature(signableDOM.getOwnerDocument(), "", signatureXMLObject
                    .getSignatureAlgorithm(), signatureXMLObject.getCanonicalizationAlgorithm());

            KeyInfo keyInfo = signatureXMLObject.getKeyInfo();
            if (signatureXMLObject.getKeyInfo() != null) {
                if (keyInfo.getKeys() != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Adding public keys to signature key info");
                    }
                    for (PublicKey key : keyInfo.getKeys()) {
                        xmlSignature.addKeyInfo(key);
                    }
                }

                if (keyInfo.getCertificates() != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Adding X.509 certifiacte(s) into signature's X509 data");
                    }
                    for (X509Certificate cert : keyInfo.getCertificates()) {
                        xmlSignature.addKeyInfo(cert);
                    }
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("Adding content to XMLSignature.");
            }
            for (ContentReference contentReference : signatureXMLObject.getContentReferences()) {
                contentReference.createReference(xmlSignature);
            }

            if (log.isDebugEnabled()) {
                log.debug("Added XMLSignature to Signature XMLObject");
            }
            signatureXMLObject.setXMLSignature(xmlSignature);

            if (log.isDebugEnabled()) {
                log.debug("Creating signing DOM element");
            }
            xmlSignature.sign(signatureXMLObject.getSigningKey());

        } catch (XMLSecurityException e) {
            log.error("Unable to construct signature Element " + signableXMLObject.getElementQName(), e);
        }
    }

    /*
     * Initialize the Apache XML security library if it hasn't been already
     */
    static {
        if (!Init.isInitialized()) {
            if (log.isDebugEnabled()) {
                log.debug("Initializing XML security library");
            }
            Init.init();
        }
    }
}