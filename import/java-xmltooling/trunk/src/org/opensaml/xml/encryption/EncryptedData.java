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
 * XMLObject representing XML Encryption, version 20021210, EncryptedData element.
 */
public class EncryptedData extends EncryptedType {

    /** Element local name */
    public final static String DEFAULT_ELEMENT_LOCAL_NAME = "EncryptedData";
    
    /** Default element name */
    public final static QName DEFAULT_ELEMENT_NAME = new QName(XMLConstants.XMLENC_NS, DEFAULT_ELEMENT_LOCAL_NAME, XMLConstants.XMLENC_PREFIX);
    
    /** Local name of the XSI type */
    public final static String TYPE_LOCAL_NAME = "EncryptedDataType"; 
        
    /** QName of the XSI type */
    public final static QName TYPE_NAME = new QName(XMLConstants.XMLENC_NS, TYPE_LOCAL_NAME, XMLConstants.XMLENC_PREFIX);
    
    /** Apache XML Security {@link org.apache.xml.security.encryption.EncryptedData} object 
     * which this XMLObject is wrapping */
    org.apache.xml.security.encryption.EncryptedData xmlEncData;
    
    /**
     * Constructor
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected EncryptedData(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }
    
    //TODO this will all change when schema XMLObjects are implemented.

    /**
     * @return Returns the {@link org.apache.xml.security.encryption.EncryptedData} object that this object is wrapping
     */
    public org.apache.xml.security.encryption.EncryptedData getXMLEncData() {
        return xmlEncData;
    }

    /**
     * @param xmlEncData  Sets the {@link org.apache.xml.security.encryption.EncryptedData} object that this object is wrapping
     */
    public void setXMLEncData(org.apache.xml.security.encryption.EncryptedData xmlEncData) {
        this.xmlEncData = xmlEncData;
    }
}