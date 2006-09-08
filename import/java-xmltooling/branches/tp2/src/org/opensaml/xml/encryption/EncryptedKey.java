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
public class EncryptedKey extends EncryptedType {

    /** Element local name */
    public final static String DEFAULT_ELEMENT_LOCAL_NAME = "EncryptedKey";
    
    /** Default element name */
    public final static QName DEFAULT_ELEMENT_NAME = new QName(XMLConstants.XMLENC_NS, DEFAULT_ELEMENT_LOCAL_NAME, XMLConstants.XMLENC_PREFIX);
    
    /** Local name of the XSI type */
    public final static String TYPE_LOCAL_NAME = "EncryptedKeyType"; 
        
    /** QName of the XSI type */
    public final static QName TYPE_NAME = new QName(XMLConstants.XMLENC_NS, TYPE_LOCAL_NAME, XMLConstants.XMLENC_PREFIX);
    
    /** Hint about who the encrypted key is intended for */
    private String recipient;
    
    /** Human readable name for this key */
    private String carriedKeyName;
    
    /**
     * Constructor
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected EncryptedKey(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }
    
    /**
     * Gets the hint about who this encrypted key is inteded for.
     * 
     * @return the hint about who this encrypted key is inteded for
     */
    public String getRecipient(){
        return recipient;
    }
    
    /**
     * Sets the hint about who this encrypted key is inteded for.
     * 
     * @param newRecipient the hint about who this encrypted key is inteded for
     */
    public void setRecipient(String newRecipient){
        recipient = prepareForAssignment(recipient, newRecipient);
    }
    
    /**
     * Gets the human readable name for this key.
     * 
     * @return the human readable name for this key
     */
    public String getCarriedKeyName(){
        return carriedKeyName;
    }
    
    /**
     * Sets the human readable name for this key.
     * 
     * @param newKeyName the human readable name for this key
     */
    public void setCarriedKeyName(String newKeyName){
        carriedKeyName = prepareForAssignment(carriedKeyName, newKeyName);
    }
}