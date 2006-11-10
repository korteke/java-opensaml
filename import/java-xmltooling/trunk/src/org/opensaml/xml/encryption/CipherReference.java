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

package org.opensaml.xml.encryption;

import javax.xml.namespace.QName;

import org.opensaml.xml.util.XMLConstants;
import org.opensaml.xml.validation.ValidatingXMLObject;

/**
 * XMLObject representing XML Encryption, version 20021210, CipherReference element.
 */
public interface CipherReference extends ValidatingXMLObject {
    
    /** Element local name */
    public final static String DEFAULT_ELEMENT_LOCAL_NAME = "CipherReference";
    
    /** Default element name */
    public final static QName DEFAULT_ELEMENT_NAME = new QName(XMLConstants.XMLENC_NS, DEFAULT_ELEMENT_LOCAL_NAME, XMLConstants.XMLENC_PREFIX);
    
    /** Local name of the XSI type */
    public final static String TYPE_LOCAL_NAME = "CipherReferenceType"; 
        
    /** QName of the XSI type */
    public final static QName TYPE_NAME = new QName(XMLConstants.XMLENC_NS, TYPE_LOCAL_NAME, XMLConstants.XMLENC_PREFIX);
    
    /** URI attribute name */
    public final static String URI_ATTRIB_NAME = "URI";
    
    /**
     * Get the URI attribute that describes from where to deference the encrypted data
     * 
     * @return the URI attribute string
     */
    public String getURI();
    
    /**
     * Set the URI attribute that describes from where to deference the encrypted data
     * 
     * @param newURI the new URI attribute string value
     */
    public void setURI(String newURI);
    
    /**
     * Get the Transforms child element, which describes which transformations to apply 
     * when dereferencing the data
     * 
     * @return the Transforms child element
     */
    public Transforms getTransforms();
    
    /**
     * Set the Transforms child element, which describes which transformations to apply 
     * when dereferencing the data
     * 
     * @param newTransforms the new Transforms child element
     */
    public void setTransforms(Transforms newTransforms);

}
