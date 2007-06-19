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

/**
 * XMLObject representing XML Encryption, version 20021210, EncryptedKey element.
 */
public interface EncryptedKey extends EncryptedType {

    /** Element local name */
    public final static String DEFAULT_ELEMENT_LOCAL_NAME = "EncryptedKey";
    
    /** Default element name */
    public final static QName DEFAULT_ELEMENT_NAME = new QName(XMLConstants.XMLENC_NS, DEFAULT_ELEMENT_LOCAL_NAME, XMLConstants.XMLENC_PREFIX);
    
    /** Local name of the XSI type */
    public final static String TYPE_LOCAL_NAME = "EncryptedKeyType"; 
        
    /** QName of the XSI type */
    public final static QName TYPE_NAME = new QName(XMLConstants.XMLENC_NS, TYPE_LOCAL_NAME, XMLConstants.XMLENC_PREFIX);
    
    /** Recipient attribute name */
    public final static String RECIPIENT_ATTRIB_NAME = "Recipient";
    
    /**
     * Gets the hint about for whom this encrypted key is intended
     * 
     * @return the hint about who this encrypted key is inteded for
     */
    public String getRecipient();
    
    /**
     * Sets the hint about for whom this encrypted key is intended
     * 
     * @param newRecipient the hint about who this encrypted key is inteded for
     */
    public void setRecipient(String newRecipient);
    
    /**
     * Gets the child element containing pointers to EncryptedData and EncryptedKey elements
     * encrypted using this key.
     * 
     * @return the element containing a list of pointers to encrypted elements
     */
    public ReferenceList getReferenceList();
    
    /**
     * Sets the child element containing pointers to EncryptedData and EncryptedKey elements
     * encrypted using this key.
     * 
     * @param newReferenceList the new reference list for this encrypted key
     */
    public void setReferenceList(ReferenceList newReferenceList);
    
    /**
     * Gets the child element carrying the human readable name for this key.
     * 
     * @return the human readable name for this key
     */
    public CarriedKeyName getCarriedKeyName();
    
    /**
     * Sets the child element carrying the human readable name for this key.
     * 
     * @param newCarriedKeyName the human readable name for this key
     */
    public void setCarriedKeyName(CarriedKeyName newCarriedKeyName);
    
}