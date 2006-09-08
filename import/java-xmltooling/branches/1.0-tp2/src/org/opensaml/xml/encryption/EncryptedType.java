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

import java.util.List;

import javax.xml.namespace.QName;

import org.opensaml.xml.AbstractXMLObject;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.util.XMLConstants;

/**
 * XMLObject representing XML Encryption, version 20021210, EncryptedType type. This is the base type for
 * {@link EncryptedData} and {@link EncryptedKey} types.
 */
public abstract class EncryptedType extends AbstractXMLObject {
    
    /** Local name of the XSI type */
    public final static String TYPE_LOCAL_NAME = "EncryptedType"; 
        
    /** QName of the XSI type */
    public final static QName TYPE_NAME = new QName(XMLConstants.XMLENC_NS, TYPE_LOCAL_NAME, XMLConstants.XMLENC_PREFIX);

    /** Unique ID for the encrypted element */
    private String id;

    /** Unencrypted content type information */
    private String type;

    /** MIME type of plaintext content */
    private String mimeType;

    /** Encoding applied to plaintext prior to encryption */
    private String encoding;

    /** Parameters for encrypting the plaintext content */
    private EncryptionParameters encParams;

    /**
     * Constructor
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected EncryptedType(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /**
     * Gets the unique ID for the XML element.
     * 
     * @return the unique ID for the XML element
     */
    public String getID() {
        return id;
    }

    /**
     * Sets the unique ID for the XML element.
     * 
     * @param newID the unique ID for the XML element
     */
    public void setID(String newID) {
        id = prepareForAssignment(id, newID);
    }

    /**
     * Gets the type information for the plaintext content.
     * 
     * @return the type information for the plaintext content
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type information for the plaintext content.
     * 
     * @param newType the type information for the plaintext content
     */
    public void setType(String newType) {
        type = prepareForAssignment(type, newType);
    }

    /**
     * Gets the MIME type of the plaintext content.
     * 
     * @return the MIME type of the plaintext content
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Sets the MIME type of the plaintext content.
     * 
     * @param newType the MIME type of the plaintext content
     */
    public void setMimeType(String newType) {
        mimeType = prepareForAssignment(mimeType, newType);
    }

    /**
     * Gets the encoding applied to the plaintext content prior to encryption.
     * 
     * @return the encoding applied to the plaintext content prior to encryption
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets the encoding applied to the plaintext content prior to encryption.
     * 
     * @param newEncoding the encoding applied to the plaintext content prior to encryption
     */
    public void setEncoding(String newEncoding) {
        encoding = prepareForAssignment(encoding, newEncoding);
    }

    /**
     * Gets the parameters for encrypting the plaintext content.
     * 
     * @return the parameters for encrypting the plaintext content
     */
    public EncryptionParameters getEncryptionParameters() {
        return encParams;
    }

    /**
     * Sets the parameters for encrypting the plaintext content.
     * 
     * @param newParams the parameters for encrypting the plaintext content
     */
    public void setEncryptionParameters(EncryptionParameters newParams) {
        encParams = prepareForAssignment(encParams, newParams);
    }
    
    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        // Children are created and managed by the XML security library
        return null;
    }
}