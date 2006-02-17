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

package org.opensaml.xml.signature;

import java.util.List;

import org.apache.xml.security.signature.XMLSignature;
import org.opensaml.xml.AbstractXMLObject;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.util.XMLConstants;

/**
 * XMLObject representing XML Digital Signature, version 20020212, Signature element.
 */
public class Signature extends AbstractXMLObject {

    /** Element local name */
    public final static String LOCAL_NAME = "Signature";
    
    /** ID attribute used as reference by digital signature */
    private String id;
    
    /** Signing information */
    private SigningContext signingContext;
    
    /** XML Signature construct */
    private XMLSignature signature;
    
    /**
     * Constructor.  Note, the provided signing context is NOT altered by this class and thus 
     * can be reused if deemed appropriate.
     * 
     * @param signingContext configuration information for computing the signature
     */
    Signature(final SigningContext signingContext) {
        super(XMLConstants.XMLSIG_NS, LOCAL_NAME);
        setElementNamespacePrefix(XMLConstants.XMLSIG_PREFIX);
        this.signingContext = signingContext;
    }
    
    /**
     * Gets the information need to construct the digital signature for this element.
     * 
     * @return the information need to construct the digital signature for this element
     */
    public SigningContext getSigningContext(){
        return signingContext;
    }
    
    /**
     * Gets the value if the Signature's Id attribute.
     * 
     * @return the value if the Signature's Id attribute
     */
    public String getId() {
        return id;
    }
    
    /**
     * Sets the value if the Signature's Id attribute.
     * 
     * @param newID the value if the Signature's Id attribute
     */
    public void setId(String newID) {
        id = newID;
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