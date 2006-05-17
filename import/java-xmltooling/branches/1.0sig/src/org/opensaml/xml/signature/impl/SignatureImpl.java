/*
 * Copyright [2005] [University Corporation for Advanced Internet Development, Inc.]
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

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

import org.apache.xml.security.signature.XMLSignature;
import org.opensaml.xml.AbstractXMLObject;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.signature.ContentReference;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.Signature;

/**
 * XMLObject representing XML Digital XMLSecSignatureImpl, version 20020212, Signature element.  
 * This class, along with it's respective marshaller and unmarshaller use the Apache XMLSec 1.3 
 * APIs to perform signing and verification.
 */
public class SignatureImpl extends AbstractXMLObject implements Signature {
    
    /** XML XMLSecSignatureImpl construct */
    private XMLSignature signature;
    
    /** Canonicalization algorithm used in signature */
    private String canonicalizationAlgorithm;
    
    /** Algorithm used to generate the signature */
    private String signatureAlgorithm;
    
    /** Key used to sign the signature */
    private Key signingKey;
    
    /** Public key information to embed in the signature */
    private KeyInfo keyInfo;
    
    /** References to content to be signed */
    private List<ContentReference> contentReferences;
    
    /**
     * Constructor
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected SignatureImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        contentReferences = new ArrayList<ContentReference>();
    }

    /** {@inheritDoc} */
    public String getCanonicalizationAlgorithm() {
        return canonicalizationAlgorithm;
    }

    /** {@inheritDoc} */
    public void setCanonicalizationAlgorithm(String newAlgorithm) {
        canonicalizationAlgorithm = newAlgorithm;
    }

    /** {@inheritDoc} */
    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    /** {@inheritDoc} */
    public void setSignatureAlgorithm(String newAlgorithm) {
        signatureAlgorithm  = newAlgorithm;
    }

    /** {@inheritDoc} */
    public Key getSigningKey() {
        return signingKey;
    }

    /** {@inheritDoc} */
    public void setSigningKey(Key newKey) {
        signingKey = newKey;
    }

    /** {@inheritDoc} */
    public KeyInfo getKeyInfo() {
        return keyInfo;
    }

    /** {@inheritDoc} */
    public void setKeyInfo(KeyInfo newKeyInfo) {
        keyInfo = newKeyInfo;
    }

    /** {@inheritDoc} */
    public List<ContentReference> getContentReferences() {
        return contentReferences;
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        // Children
        return null;
    }
    
    /**
     * Gets the Apache XMLSec signature object backing this signature.
     * 
     * @return the Apache XMLSec signature object backing this signature
     */
    protected XMLSignature getXMLSignature(){
        return signature;
    }
    
    /**
     * Sets the Apache XMLSec signature object backing this signature.
     * 
     * @param newSignature the Apache XMLSec signature object backing this signature
     */
    protected void setXMLSignature(XMLSignature newSignature){
        signature = newSignature;
    }
}