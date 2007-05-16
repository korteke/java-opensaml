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

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.log4j.Logger;
import org.apache.xml.security.Init;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.keyinfo.KeyInfoGenerator;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.impl.KeyInfoBuilder;
import org.opensaml.xml.util.DatatypeHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A class for encrypting XMLObjects, their content, and keys.
 */
public class Encrypter {
    
    /** Class logger. */
    private Logger log = Logger.getLogger(Encrypter.class);
    
    /** Unmarshaller used to create EncryptedData objects from DOM element. */
    private Unmarshaller encryptedDataUnmarshaller;
    
    /** Unmarshaller used to create EncryptedData objects from DOM element. */
    private Unmarshaller encryptedKeyUnmarshaller;
    
    /** Builder instance for building KeyInfo objects. */
    private KeyInfoBuilder keyInfoBuilder;
    
    /**
     * Constructor.
     * 
     */
    public Encrypter() {
        UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
        encryptedDataUnmarshaller =  unmarshallerFactory.getUnmarshaller(EncryptedData.DEFAULT_ELEMENT_NAME);
        encryptedKeyUnmarshaller =  unmarshallerFactory.getUnmarshaller(EncryptedKey.DEFAULT_ELEMENT_NAME);
        
        XMLObjectBuilderFactory  builderFactory = Configuration.getBuilderFactory();
        keyInfoBuilder = (KeyInfoBuilder) builderFactory.getBuilder(KeyInfo.DEFAULT_ELEMENT_NAME);
    }
    
    // TODO fix docs for new overloaded methods, inline key placement, etc
    // TODO need logging
    
    /**
     * Encrypts the DOM representation of the XMLObject.
     * 
     * @param xmlObject the XMLObject to be encrypted
     * @param encParams parameters for encrypting the data
     * 
     * @return the resulting EncryptedData element
     * @throws EncryptionException exception thrown on encryption errors
     */
    public EncryptedData encryptElement(XMLObject xmlObject, EncryptionParameters encParams) 
        throws EncryptionException {
        List<KeyEncryptionParameters> emptyKEKParamsList = new ArrayList<KeyEncryptionParameters>();
        return encryptElement(xmlObject, encParams, emptyKEKParamsList, false);
    }

    /**
     * Encrypts the DOM representation of the XMLObject.
     * 
     * @param xmlObject the XMLObject to be encrypted
     * @param encParams parameters for encrypting the data
     * @param kekParams parameters for encrypting the encryption key
     * 
     * @return the resulting EncryptedData element
     * @throws EncryptionException exception thrown on encryption errors
     */
    public EncryptedData encryptElement(XMLObject xmlObject, EncryptionParameters encParams, 
            KeyEncryptionParameters kekParams)  throws EncryptionException {
        List<KeyEncryptionParameters> kekParamsList = new ArrayList<KeyEncryptionParameters>();
        kekParamsList.add(kekParams);
        return encryptElement(xmlObject, encParams, kekParamsList, false);
    }
    
    /**
     * Encrypts the DOM representation of the XMLObject.
     * 
     * @param xmlObject the XMLObject to be encrypted
     * @param encParams parameters for encrypting the data
     * @param kekParamsList parameters for encrypting the encryption key
     * 
     * @return the resulting EncryptedData element
     * @throws EncryptionException exception thrown on encryption errors
     */
    public EncryptedData encryptElement(XMLObject xmlObject, EncryptionParameters encParams, 
            List<KeyEncryptionParameters> kekParamsList)  throws EncryptionException {
        return encryptElement(xmlObject, encParams, kekParamsList, false);
    }
    
    /**
     * Encrypts the DOM representation of the content of an XMLObject.
     * 
     * @param xmlObject the XMLObject to be encrypted
     * @param encParams parameters for encrypting the data
     * 
     * @return the resulting EncryptedData element
     * @throws EncryptionException exception thrown on encryption errors
     */
    public EncryptedData encryptElementContent(XMLObject xmlObject, EncryptionParameters encParams) 
        throws EncryptionException {
        List<KeyEncryptionParameters> emptyKEKParamsList = new ArrayList<KeyEncryptionParameters>();
        return encryptElement(xmlObject, encParams, emptyKEKParamsList, true);
    }
    
    /**
     * Encrypts the DOM representation of the content of an XMLObject.
     * 
     * @param xmlObject the XMLObject to be encrypted
     * @param encParams parameters for encrypting the data
     * @param kekParams parameters for encrypting the encryption key
     * 
     * @return the resulting EncryptedData element
     * @throws EncryptionException exception thrown on encryption errors
     */
    public EncryptedData encryptElementContent(XMLObject xmlObject, EncryptionParameters encParams, 
            KeyEncryptionParameters kekParams) throws EncryptionException {
        List<KeyEncryptionParameters> kekParamsList = new ArrayList<KeyEncryptionParameters>();
        kekParamsList.add(kekParams);
        return encryptElement(xmlObject, encParams, kekParamsList, true);
    }
    
    /**
     * Encrypts the DOM representation of the content of an XMLObject.
     * 
     * @param xmlObject the XMLObject to be encrypted
     * @param encParams parameters for encrypting the data
     * @param kekParamsList parameters for encrypting the encryption key
     * 
     * @return the resulting EncryptedData element
     * @throws EncryptionException exception thrown on encryption errors
     */
    public EncryptedData encryptElementContent(XMLObject xmlObject, EncryptionParameters encParams, 
            List<KeyEncryptionParameters> kekParamsList) throws EncryptionException {
        return encryptElement(xmlObject, encParams, kekParamsList, true);
    }
    
    /**
     * Encrypts a key.
     * 
     * @param key the key to encrypt
     * @param kekParamsList parameters for encrypting the key
     * @param containingDocument the document that will own the resulting element
     * 
     * @return the resulting list of EncryptedKey element
     * 
     * @throws EncryptionException  exception thrown on encryption errors
     */
    public List<EncryptedKey> encryptKey(Key key, List<KeyEncryptionParameters> kekParamsList,
            Document containingDocument) throws EncryptionException {
        
        checkParams(kekParamsList, false);
        
        List<EncryptedKey> encKeys = new ArrayList<EncryptedKey>();
        
        for (KeyEncryptionParameters kekParam : kekParamsList) {
            try {
                EncryptedKey encKey = encryptKey(key, kekParam, containingDocument);
                encKeys.add(encKey);
            } catch (EncryptionException e) {
                //TODO log, rethrow
            }            
        }
        return encKeys;
    }
    
    /**
     * Encrypts a key.
     * 
     * @param key the key to encrypt
     * @param kekParams parameters for encrypting the key
     * @param containingDocument the document that will own the resulting element
     * 
     * @return the resulting EncryptedKey element
     * 
     * @throws EncryptionException  exception thrown on encryption errors
     */
    public EncryptedKey encryptKey(Key key, KeyEncryptionParameters kekParams, Document containingDocument)
            throws EncryptionException {
        
        checkParams(kekParams, false);
        
        Key encryptionKey = extractEncryptionKey(kekParams.getEncryptionCredential()); 
        String encryptionAlgorithmURI = kekParams.getAlgorithm();
        
        EncryptedKey encryptedKey = encryptKey(key, encryptionKey, encryptionAlgorithmURI, containingDocument);
        
        if (kekParams.getKeyInfoGenerator() != null) {
            KeyInfoGenerator generator = kekParams.getKeyInfoGenerator();
            try {
                encryptedKey.setKeyInfo( generator.generate(kekParams.getEncryptionCredential()) );
            } catch (SecurityException e) {
                throw new EncryptionException("Error generating EncryptedKey KeyInfo", e);
            }
        }
        
        if (kekParams.getRecipient() != null) {
            encryptedKey.setRecipient(kekParams.getRecipient());
        }
        
        return encryptedKey;
    }
    
    /**
     * Encrypts a key.
     * 
     * @param targetKey the key to encrypt
     * @param encryptionKey the key with which to encrypt the target key
     * @param encryptionAlgorithmURI the XML Encryption algorithm URI corresponding to the encryption key
     * @param containingDocument the document that will own the resulting element
     * @return the new EncryptedKey object
     * @throws EncryptionException exception thrown on encryption errors
     */
    protected EncryptedKey encryptKey(Key targetKey, Key encryptionKey, String encryptionAlgorithmURI, 
            Document containingDocument) throws EncryptionException {
        
        XMLCipher xmlCipher;
        try {
            xmlCipher = XMLCipher.getInstance(encryptionAlgorithmURI);
            xmlCipher.init(XMLCipher.WRAP_MODE, encryptionKey);
        } catch (XMLEncryptionException e) {
            throw new EncryptionException("Error initializing cipher instance on key encryption", e); 
        }
        
        org.apache.xml.security.encryption.EncryptedKey apacheEncryptedKey;
        try {
            apacheEncryptedKey = xmlCipher.encryptKey(containingDocument, targetKey);
        } catch (XMLEncryptionException e) {
            throw new EncryptionException("Error encrypting element on key encryption", e);
        }
        
        EncryptedKey encryptedKey;
        try {
            Element encKeyElement = xmlCipher.martial(containingDocument, apacheEncryptedKey);
            encryptedKey = (EncryptedKey) encryptedKeyUnmarshaller.unmarshall(encKeyElement);
        } catch (UnmarshallingException e) {
            throw new EncryptionException("Error unmarshalling EncryptedKey element");
        }
        
        return encryptedKey;
    }
    
    /**
     * Encrypts the given XMLObject.
     * 
     * @param xmlObject the XMLObject to be encrypted
     * @param encryptionKey the key with which to encrypt the XMLObject
     * @param encryptionAlgorithmURI the XML Encryption algorithm URI corresponding to the encryption key
     * @param encryptContentMode whether just the content of the XMLObject should be encrypted
     * @return the resulting EncryptedData object
     * @throws EncryptionException exception thrown on encryption errors
     */
    protected EncryptedData encryptElement(XMLObject xmlObject, Key encryptionKey, 
            String encryptionAlgorithmURI, boolean encryptContentMode)  throws EncryptionException {
        
        checkAndMarshall(xmlObject);
        
        Element targetElement = xmlObject.getDOM();
        Document ownerDocument = targetElement.getOwnerDocument();
        
        XMLCipher xmlCipher;
        try {
            xmlCipher = XMLCipher.getInstance(encryptionAlgorithmURI);
            xmlCipher.init(XMLCipher.ENCRYPT_MODE, encryptionKey);
        } catch (XMLEncryptionException e) {
            throw new EncryptionException("Error initializing cipher instance", e);
        }
        
        org.apache.xml.security.encryption.EncryptedData apacheEncryptedData;
        try {
            apacheEncryptedData = xmlCipher.encryptData(ownerDocument, targetElement, encryptContentMode);
        } catch (Exception e) {
            throw new EncryptionException("Error encrypting element", e);
        }
        
        EncryptedData encryptedData;
        try {
            Element encDataElement = xmlCipher.martial(ownerDocument, apacheEncryptedData);
            encryptedData = (EncryptedData) encryptedDataUnmarshaller.unmarshall(encDataElement);
        } catch (UnmarshallingException e) {
            throw new EncryptionException("Error unmarshalling EncryptedData element", e);
        }
        
        return encryptedData;
    }
    
    /**
     * Encrypts the given XMLObject.
     * 
     * @param xmlObject the XMLObject to be encrypted
     * @param encParams the encryption parameters to use
     * @param kekParamsList the key encryption parameters to use
     * @param encryptContentMode whether just the content of the XMLObject should be encrypted
     * 
     * @return the resulting EncryptedData object
     * @throws EncryptionException exception thrown on encryption errors
     */
    private EncryptedData encryptElement(XMLObject xmlObject, EncryptionParameters encParams, 
            List<KeyEncryptionParameters> kekParamsList, boolean encryptContentMode) throws EncryptionException {
        
        checkParams(encParams, kekParamsList);
        
        String encryptionAlgorithmURI = encParams.getAlgorithm();
        Key encryptionKey = extractEncryptionKey(encParams.getEncryptionCredential());
        if (encryptionKey == null) {
            encryptionKey = generateEncryptionKey(encryptionAlgorithmURI);
        }
        
        EncryptedData encryptedData =  
            encryptElement(xmlObject, encryptionKey, encryptionAlgorithmURI, encryptContentMode);
        
        if (encParams.getKeyInfoGenerator() != null) {
            KeyInfoGenerator generator = encParams.getKeyInfoGenerator();
            try {
                encryptedData.setKeyInfo( generator.generate(encParams.getEncryptionCredential()) );
            } catch (SecurityException e) {
                throw new EncryptionException("Error generating EncryptedData KeyInfo", e);
            }
        }
        
        checkAndMarshall(encryptedData); //TODO temp
        Document ownerDocument = encryptedData.getDOM().getOwnerDocument();
        
        for (KeyEncryptionParameters kekParams : kekParamsList) {
            try {
                EncryptedKey encryptedKey = encryptKey(encryptionKey, kekParams, ownerDocument);
                if (encryptedData.getKeyInfo() == null) {
                    KeyInfo keyInfo = keyInfoBuilder.buildObject();
                    encryptedData.setKeyInfo(keyInfo);
                }
                encryptedData.getKeyInfo().getEncryptedKeys().add(encryptedKey);
            } catch (EncryptionException e) {
                // TODO: handle exception, log, rethrow?
            }            
        }
        
        return encryptedData;
    }
    
    /**
     * Randomly generates a Java JCE symmetric Key object from the specified XML Encryption algorithm URI.
     * 
     * @param algoURI  The XML Encryption algorithm URI
     * @return a randomly-generated symmetric Key
     * @throws EncryptionException thrown if there is a problem generating the key
     */
    protected SecretKey generateEncryptionKey(String algoURI) throws EncryptionException {
        String jceAlgorithmName = JCEMapper.getJCEKeyAlgorithmFromURI(algoURI);
        int keyLength = JCEMapper.getKeyLengthFromURI(algoURI);
        SecretKey key = null;
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(jceAlgorithmName);
            keyGenerator.init(keyLength);
            key = keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
                throw new EncryptionException("Algorithm URI not found when generating encryption key: " 
                        + algoURI);
        }
        return key;
    }
    
    /**
     * Utility method to extract the encryption key from a credential.
     * 
     * @param credential the credential holding either a public key from a key pair, 
     *          or a shared symmetric key.
     * @return the public or symmetric key contained by the credential, or null
     */
    protected Key extractEncryptionKey(Credential credential) {
        if (credential == null) {
            return null;
        } else if (credential.getPublicKey() != null) {
            return credential.getPublicKey();
        } else if (credential.getSecretKey() != null) {
            return credential.getSecretKey();
        } else {
            return null;
        }
    }
    
    
    /**
     * Ensure that the XMLObject is marshalled.
     * 
     * @param xmlObject the object to check and marshall
     * @throws EncryptionException thrown if there is an error when marshalling the XMLObject
     */
    protected void checkAndMarshall(XMLObject xmlObject) throws EncryptionException {
        Element targetElement = xmlObject.getDOM();
        if(targetElement == null){
            Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(xmlObject);
            try {
                targetElement = marshaller.marshall(xmlObject);
            } catch (MarshallingException e) {
                throw new EncryptionException("Error marshalling target XMLObject", e);
            }
        }
    }
    
    /**
     * Check data encryption parameters for consistency and required values.
     * 
     * @param encParams the data encryption parameters to check
     * 
     * @throws EncryptionException thrown if any parameters are missing or have invalid values
     */
    protected void checkParams(EncryptionParameters encParams) throws EncryptionException {
        if (encParams == null ) {
            throw new EncryptionException("Data encryption parameters are required");
        }
        if (DatatypeHelper.isEmpty(encParams.getAlgorithm())) {
            throw new EncryptionException("Data encryption algorithm URI is required");
        }
    }
    
    /**
     * Check key encryption parameters for consistency and required values.
     * 
     * @param kekParams the key encryption parameters to check
     * @param allowEmpty if false, a null parameter is treated as an error
     * 
     * @throws EncryptionException thrown if any parameters are missing or have invalid values
     */
    protected void checkParams(KeyEncryptionParameters kekParams, boolean allowEmpty) throws EncryptionException {
        if (kekParams == null) {
            if (allowEmpty) {
                return;
            } else {
               throw new EncryptionException("Key encryption parameters are required");
            }
        }
        if (DatatypeHelper.isEmpty(kekParams.getAlgorithm())) {
            throw new EncryptionException("Key encryption algorithm URI is required");
        }
        if (extractEncryptionKey(kekParams.getEncryptionCredential()) == null) {
            throw new EncryptionException("Key encryption credential and contained key are required");
        }
    }
    
    /**
     * Check key encryption parameters for consistency and required values.
     * 
     * @param kekParamsList the key encryption parameters list to check
     * @param allowEmpty if false, a null or empty list is treated as an error
     * 
     * @throws EncryptionException thrown if any parameters are missing or have invalid values
     */
    protected void checkParams(List<KeyEncryptionParameters> kekParamsList, boolean allowEmpty)
            throws EncryptionException {
        if (kekParamsList == null || kekParamsList.isEmpty()) {
            if (allowEmpty) {
                return;
            } else {
               throw new EncryptionException("Key encryption parameters list may not be empty");
            }
        }
        for (KeyEncryptionParameters kekParams : kekParamsList) {
            checkParams(kekParams, false);
        }
    }
    
    /**
     * Check the encryption parameters and key encryption parameters for valid combinations of options.
     * 
     * @param encParams the encryption parameters to use
     * @param kekParamsList the key encryption parameters to use
     * @throws EncryptionException exception thrown on encryption errors
     */
    protected void checkParams(EncryptionParameters encParams, List<KeyEncryptionParameters> kekParamsList) 
        throws EncryptionException {
        
        checkParams(encParams);
        checkParams(kekParamsList, true);
        
        if (extractEncryptionKey(encParams.getEncryptionCredential()) == null 
                && (kekParamsList == null || kekParamsList.isEmpty())) {
            throw new EncryptionException("Using a generated encryption key "
                    + "requires a KeyEncryptionParameters object and key encryption key");
        }
    }
 
    /*
     * Initialize the Apache XML security library if it hasn't been already
     */
    static {
        if (!Init.isInitialized()) {
            Init.init();
        }
    }
    
}