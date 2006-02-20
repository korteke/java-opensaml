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

import java.util.List;

import org.apache.xml.security.signature.XMLSignature;
import org.opensaml.xml.AbstractXMLObject;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SigningContext;
import org.opensaml.xml.util.XMLConstants;

/**
 * XMLObject representing XML Digital XMLSecSignatureImpl, version 20020212, Signature element.  
 * This class, along with it's respective marshaller and unmarshaller use the Apache XMLSec 1.3 
 * APIs to perform signing and verification.
 */
public class XMLSecSignatureImpl extends AbstractXMLObject implements Signature {
    
    /** The reference URI to the content to be signed */
    private String referenceURI;
    
    /** Signing information */
    private SigningContext signingContext;
    
    /** XML XMLSecSignatureImpl construct */
    private XMLSignature signature;
    
    /**
     * Constructor.  Note, the provided signing context is NOT altered by this class and thus 
     * can be reused if deemed appropriate.
     * 
     * @param signingContext configuration information for computing the signature
     */
    XMLSecSignatureImpl(final SigningContext signingContext) {
        super(XMLConstants.XMLSIG_NS, LOCAL_NAME);
        setElementNamespacePrefix(XMLConstants.XMLSIG_PREFIX);
        this.signingContext = signingContext;
    }
    
    /*
     * @see org.opensaml.xml.signature.Signature#getSigningContext()
     */
    public SigningContext getSigningContext(){
        return signingContext;
    }
    
    /*
     * @see org.opensaml.xml.signature.Signature#setSigningContext(org.opensaml.xml.signature.SigningContext)
     */
    public void setSigningContext(SigningContext newContext){
        signingContext = newContext;
    }
    
    /*
     * @see org.opensaml.xml.signature.Signature#getReferenceURI()
     */
    public String getReferenceURI() {
        return referenceURI;
    }
    
    /*
     * @see org.opensaml.xml.signature.Signature#setReferenceURI(java.lang.String)
     */
    public void setReferenceURI(String newID) {
        referenceURI = newID;
    }

    /**
     * Gets the XML signature construct.
     * 
     * @return the XML signature construct
     */
    public XMLSignature getXMLSignature() {
        return signature;
    }
    
    /**
     * Sets the XML signature construct.
     * 
     * @param xmlSignature the XML signature construct
     */
    protected void setXMLSignature(XMLSignature xmlSignature) {
        signature = xmlSignature;
    }
    
    /*
     * @see org.opensaml.xml.XMLObject#getOrderedChildren()
     */
    public List<XMLObject> getOrderedChildren() {
        //No children
        return null;
    }
}