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

import org.opensaml.xml.ElementExtensibleXMLObject;
import org.opensaml.xml.util.XMLConstants;
import org.opensaml.xml.validation.ValidatingXMLObject;

/**
 * XMLObject representing XML Encryption, version 20021210, EncryptionMethod element.
 */
public interface EncryptionMethod extends ValidatingXMLObject, ElementExtensibleXMLObject {
    
    /** Element local name */
    public final static String DEFAULT_ELEMENT_LOCAL_NAME = "EncryptionMethod";
    
    /** Default element name */
    public final static QName DEFAULT_ELEMENT_NAME = new QName(XMLConstants.XMLENC_NS, DEFAULT_ELEMENT_LOCAL_NAME, XMLConstants.XMLENC_PREFIX);
    
    /** Local name of the XSI type */
    public final static String TYPE_LOCAL_NAME = "EncryptionMethodType"; 
        
    /** QName of the XSI type */
    public final static QName TYPE_NAME = new QName(XMLConstants.XMLENC_NS, TYPE_LOCAL_NAME, XMLConstants.XMLENC_PREFIX);
    
    /** Algorithm attribute name */
    public final static String ALGORITHM_ATTRIB_NAME = "Algorithm";
    
   
    /**
     * Gets the algorithm URI attribute used in this EncryptionMethod
     * 
     * @return the Algorithm attribute URI attribute string
     */
    public String getAlgorithm();
 
    /**
     * Sets the algorithm URI attribute used in this EncryptionMethod
     * 
     * @param newAlgorithm the new Algorithm URI attribute string
     */
    public void setAlgorithm(String newAlgorithm);
    
    /**
     * Gets the KeySize child element.
     * 
     * @return the KeySize child element
     */
    public KeySize getKeySize();
    
    /**
     * Sets the KeySize child element.
     * 
     * @param newKeySize the new KeySize child element
     */
    public void setKeySize(KeySize newKeySize);
    
    /**
     * Gets the OAEPparams child element.
     * 
     * @return the OAEPparams child element
     */
    public OAEPparams getOAEPparams();
    
    /**
     * Sets the OAEPparams child element.
     * 
     * @param newOAEPparams the new OAEPparams child element
     */
    public void setOAEPparams(OAEPparams newOAEPparams);
    
}
