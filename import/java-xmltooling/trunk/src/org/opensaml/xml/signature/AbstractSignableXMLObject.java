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

/**
 * 
 */
package org.opensaml.xml.signature;

import org.opensaml.xml.AbstractDOMCachingXMLObject;

/**
 * Extension to {@link org.opensaml.xml.DOMCachingXMLObject} that implements {@link org.opensaml.xml.signature.SignableXMLObject}.
 */
public abstract class AbstractSignableXMLObject extends AbstractDOMCachingXMLObject implements SignableXMLObject {

    /** Signature */
    private Signature signature;
    
    /**
     * Constructor
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     */
    protected AbstractSignableXMLObject(String namespaceURI, String elementLocalName) {
        super(namespaceURI, elementLocalName);
    }
    
    /*
     * @see org.opensaml.xml.SignableXMLObject#isSigned()
     */
    public boolean isSigned() {
        if(signature != null && signature.getXMLSignature() != null) {
            return true;
        }
        
        return false;
    }

    /*
     * @see org.opensaml.xml.SignableXMLObject#getSignature()
     */
    public Signature getSignature(){
        return signature;
    }
    
    /*
     * @see org.opensaml.xml.SignableXMLObject#setSignature(org.opensaml.xml.signature.Signature)
     */
    public void setSignature(Signature newSignature){
        signature = prepareForAssignment(signature, newSignature);
    }
}