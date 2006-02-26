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

/**
 * 
 */

package org.opensaml.xml.encryption.impl;

import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.opensaml.xml.encryption.EncryptableXMLObject;
import org.opensaml.xml.encryption.EncryptableXMLObjectMarshaller;
import org.opensaml.xml.encryption.EncryptionContext;
import org.opensaml.xml.io.AbstractXMLObjectMarshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A marshaller for {@link org.opensaml.xml.encryption.EncryptableXMLObject} objects. This class uses the Apache XMLSec
 * 1.3 APIs to perform the encryption.
 */
public abstract class XMLSecEncryptableXMLObjectMarshaller extends AbstractXMLObjectMarshaller implements
        EncryptableXMLObjectMarshaller {

    /** Logger */
    private static Logger log = Logger.getLogger(XMLSecEncryptableXMLObjectMarshaller.class);

    /**
     * Constructor
     * 
     * @param targetNamespaceURI the namespace URI of either the schema type QName or element QName of the elements this
     *            unmarshaller operates on
     * @param targetLocalName the local name of either the schema type QName or element QName of the elements this
     *            unmarshaller operates on
     * 
     * @throws NullPointerException if any of the arguments are null (or empty in the case of String parameters)
     */
    public XMLSecEncryptableXMLObjectMarshaller(String targetNamespaceURI, String targetLocalName)
            throws NullPointerException {
        super(targetNamespaceURI, targetLocalName);
    }

    /*
     * @see org.opensaml.xml.encryption.EncryptableXMLObjectMarshaller#encryptElement(org.w3c.dom.Element,
     *      org.opensaml.xml.encryption.EncryptableXMLObject)
     */
    public Element encryptElement(Element element, EncryptableXMLObject xmlObject) throws MarshallingException {
        if (log.isDebugEnabled()) {
            log.debug("Starting to encrypt Element representation of " + xmlObject.getElementQName());
        }
        EncryptionContext encryptionContext = xmlObject.getEncryptionContext();
        Document owningDocument = element.getOwnerDocument();

        if (log.isDebugEnabled()) {
            log.debug("Gennerating symmetric key to be used to encrypt Element representation of " + xmlObject.getElementQName());
        }
        Key dataEncryptionKey = getSymmetricKey(encryptionContext.getDataEncryptionAlgorithm());

        try {
            if (log.isDebugEnabled()) {
                log.debug("Generating EncryptedKey information using key encryption algorithm " + XMLCipher.RSA_v1dot5);
            }
            Element encryptedKey = getEncryptedKey(xmlObject, dataEncryptionKey, owningDocument);

            if (log.isDebugEnabled()) {
                log.debug("Generating EncryptedData information using data encryption algortihm "
                        + encryptionContext.getDataEncryptionAlgorithm());
            }
            Element encryptedData = getEncryptedData(encryptionContext.getDataEncryptionAlgorithm(), dataEncryptionKey, element);
            
            if(log.isDebugEnabled()){
                log.debug("Creating element " + xmlObject.getEncryptedElementName() + " which will contain the encrypted information");
            }
            Element encryptedElement = createEncryptedElement(owningDocument, xmlObject.getEncryptedElementName(), encryptedKey, encryptedData);
            
            if (log.isDebugEnabled()) {
                log.debug("Finished encrypting " + xmlObject.getElementQName());
            }
            return encryptedElement;
            
        } catch (Exception e) {
            String errorMsg = "Unable to encrypt DOM representation of XMLObject " + xmlObject.getElementQName();
            log.error(errorMsg, e);
            throw new MarshallingException(errorMsg, e);
        }
    }

    /**
     * Gets the Element that will contain the content from encrypting the given Element
     * 
     * @param element the Element to be encrypted
     * @param xmlObject the XMLObject represented by the given element
     * 
     * @return the Element that will contain the resulting of encrypting the given Element
     */
    protected Element getElementContainer(Element element, EncryptableXMLObject xmlObject) {
        QName encryptedElementName = xmlObject.getEncryptedElementName();

        if (encryptedElementName == null) {
            if (log.isDebugEnabled()) {
                log
                        .debug("XMLObject "
                                + xmlObject.getElementQName()
                                + " did not specify an encrypted element name, encrypt content will be placed directly in its DOM Element");
            }
            return element;
        } else {
            if (log.isDebugEnabled()) {
                log.debug("XMLObject " + xmlObject.getElementQName() + " specified an encrypted element name of "
                        + encryptedElementName + ", creating element to place encrypted content in");
            }
            Document owningDocument = element.getOwnerDocument();
            return owningDocument.createElementNS(encryptedElementName.getNamespaceURI(), encryptedElementName
                    .getLocalPart());
        }
    }

    /**
     * Generates the symmetric key to use for data encryption.
     * 
     * @param encryptionAlgorithm the data encryption algorithm to be used
     * 
     * @return the symmetric key to perform the algorithm
     * 
     * @throws MarshallingException thrown if the key can not be generated because the algorithm is not supported
     */
    protected Key getSymmetricKey(String encryptionAlgorithm) throws MarshallingException {
        KeyGenerator keyGenerator;

        try {
            if (encryptionAlgorithm.equals(XMLCipher.TRIPLEDES)) {
                if (log.isDebugEnabled()) {
                    log.debug("Creating symmetric key for Triple DES encryption");
                }
                keyGenerator = KeyGenerator.getInstance("DESede");
                return keyGenerator.generateKey();
            } else if (encryptionAlgorithm.equals(XMLCipher.AES_128)) {
                if (log.isDebugEnabled()) {
                    log.debug("Creating symmetric key for 128bit AES encryption");
                }
                keyGenerator = KeyGenerator.getInstance("AES");
                keyGenerator.init(128);
                return keyGenerator.generateKey();
            } else if (encryptionAlgorithm.equals(XMLCipher.AES_192)) {
                if (log.isDebugEnabled()) {
                    log.debug("Creating symmetric key for 192bit AES encryption");
                }
                keyGenerator = KeyGenerator.getInstance("AES");
                keyGenerator.init(192);
                return keyGenerator.generateKey();
            } else if (encryptionAlgorithm.equals(XMLCipher.AES_256)) {
                if (log.isDebugEnabled()) {
                    log.debug("Creating symmetric key for 256bit AES encryption");
                }
                keyGenerator = KeyGenerator.getInstance("AES");
                keyGenerator.init(256);
                return keyGenerator.generateKey();
            } else {
                String erroMsg = "Symmetric key generation algorithm " + encryptionAlgorithm
                        + " is not supported by this JCE";
                log.error(erroMsg);
                throw new MarshallingException(erroMsg);
            }
        } catch (NoSuchAlgorithmException e) {
            String erroMsg = "Symmetric key generation algorithm " + encryptionAlgorithm
                    + " is not supported by this JCE";
            log.error(erroMsg, e);
            throw new MarshallingException(erroMsg, e);
        }
    }

    /**
     * Generates the EncryptedKey construct that will encrypt the data encryption symmetric key with the 
     * RSA public key.
     * 
     * @param xmlObject the XMLObject representing the Element to be encrypted
     * @param dataEncryptionKey the symmetric key used to perform the encryption
     * @param owningDocument the document that will own the EncryptedKey element
     * 
     * @return the EncryptedKey construct
     * 
     * @throws XMLSecurityException thrown if the EncryptedKey can not be generated
     */
    protected Element getEncryptedKey(EncryptableXMLObject xmlObject, Key dataEncryptionKey, Document owningDocument) throws XMLSecurityException{
        EncryptionContext encryptionContext = xmlObject.getEncryptionContext();

        XMLCipher keyCipher = XMLCipher.getInstance(XMLCipher.RSA_v1dot5);
        keyCipher.init(XMLCipher.WRAP_MODE, encryptionContext.getKeyEncryptionKey());
        EncryptedKey encryptedKey = keyCipher.encryptKey(owningDocument, dataEncryptionKey);
        Element encryptedKeyElement = keyCipher.martial(encryptedKey);
        
        if(log.isTraceEnabled()){
            log.trace("Generated EncryptedKey: \n" + XMLHelper.nodeToString(encryptedKeyElement));
        }
        return encryptedKeyElement;
    }
    
    /**
     * Generates the EncryptedData construct containing the encrypted contents of the given Element.
     * 
     * @param encryptionAlgorithm the algortihm to use to encrypt the data
     * @param encryptionKey the symmetric key to use to encrypt the data
     * @param element the data to be encrypted
     * 
     * @return the encrypted data
     * 
     * @throws Exception thrown if an error occurs while encrypting the data
     */
    protected Element getEncryptedData(String encryptionAlgorithm, Key encryptionKey, Element element) throws Exception{
        XMLCipher xmlCipher = XMLCipher.getInstance(encryptionAlgorithm);
        xmlCipher.init(XMLCipher.ENCRYPT_MODE, encryptionKey);
        EncryptedData encryptedData = xmlCipher.encryptData(element.getOwnerDocument(), element);
        Element encryptedDataElement = xmlCipher.martial(encryptedData);
        
        if(log.isTraceEnabled()){
            log.trace("Generated EncryptedData: \n" + XMLHelper.nodeToString(encryptedDataElement));
        }
        
        return encryptedDataElement;
    }


    /**
     * Generates the element that will contain the encrypted key and encrypated data information.
     *  
     * @param owningDocument the document owning the element to be created
     * @param elementName the name of the element to be created
     * @param encryptedKey the encrypted key information
     * @param encryptedData the encrypted data information
     * 
     * @return the encrypted element
     */
    protected Element createEncryptedElement(Document owningDocument, QName elementName, Element encryptedKey, Element encryptedData){
        Element encryptedElement = owningDocument.createElementNS(elementName.getNamespaceURI(), elementName
                .getLocalPart());
        
        encryptedElement.appendChild(encryptedData);
        encryptedElement.appendChild(encryptedKey);
        
        return encryptedElement;
    }
}