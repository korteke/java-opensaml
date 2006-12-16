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

import java.io.ByteArrayInputStream;
import java.security.Key;
import java.security.KeyException;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.ParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.security.KeyInfoResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Decrypts XMLObject and their keys.
 */
public class Decrypter {
    
    /** Parser pool, used in decryption od EncryptedData objects. */
    private static ParserPool parserPool;
    
    /** Class logger. */
    private Logger log = Logger.getLogger(Decrypter.class);
    
    /** Resolver for data encryption keys. */
    private KeyInfoResolver resolver;
    
    /** Resolver for key encryption keys. */
    private KeyInfoResolver kekResolver;
    
    /**
     * Constructor.
     *
     * @param newKEKResolver resolver for key encryption keys.
     * @param newResolver resolver for data encryption keys.
     */
    public Decrypter(KeyInfoResolver newKEKResolver, KeyInfoResolver newResolver) {
        this.kekResolver = newKEKResolver;
        this.resolver = newResolver; 
    }
    
    /**
     * Set a new key encryption key resolver.
     * 
     * @param newKEKResolver the new key encryption key resolver
     */
    public void setKEKREsolver(KeyInfoResolver newKEKResolver) {
        this.kekResolver = newKEKResolver;
    }
    
    /**
     * Set a new data encryption key resolver.
     * 
     * @param newResolver the new data encryption key resolver
     */
    public void setKeyResolver(KeyInfoResolver newResolver) {
        this.resolver = newResolver;
    }
    
    /**
     * Decrypts the supplied EncryptedData and returns the resulting XMLObject.
     * 
     * @param encryptedData encrypted data element containing the data to be decrypted
     * @return the decrypted XMLObject
     * @throws DecryptionException exception indicating a decryption error
     */
    public XMLObject decryptData(EncryptedData encryptedData) throws DecryptionException {
        if (resolver == null && kekResolver == null) {
            throw new DecryptionException("Unable to decrypt EncryptedData, no key resolvers are set");
        }
        
        //TODO - see below
        if (encryptedData.getType().equals(EncryptionConstants.TYPE_CONTENT)) {
            throw new DecryptionException("Decryption of EncryptedData elements of type 'Content' " 
                    + "is not currently supported");
        }
        
        // This is the key that will ultimately be used to decrypt the EncryptedData
        Key dataEncKey = resolveDataDecryptionKey(encryptedData);
        
        if (dataEncKey == null) {
            throw new DecryptionException("Unable to resolve the data encryption key");
        }
        
        Element targetElement = encryptedData.getDOM();
        if (targetElement == null) {
           Marshaller marshaller = 
               Configuration.getMarshallerFactory().getMarshaller(EncryptedData.DEFAULT_ELEMENT_NAME);
           try {
               targetElement = marshaller.marshall(encryptedData);
           } catch (MarshallingException e) {
               throw new DecryptionException("Error marshalling EncryptedData for decryption", e);
           }
        }
        
        XMLCipher xmlCipher;
        try {
            xmlCipher = XMLCipher.getInstance();
            xmlCipher.init(XMLCipher.DECRYPT_MODE, dataEncKey);
        } catch (XMLEncryptionException e) {
            throw new DecryptionException("Error initialzing cipher instance on data decryption", e);
        }
        
        byte[] bytes = null;
        try {
            bytes = xmlCipher.decryptToByteArray(targetElement);
        } catch (XMLEncryptionException e) {
            throw new DecryptionException("Error decrypting the encrypted data element", e);
        }
        if (bytes == null) {
            throw new DecryptionException("EncryptedData could not be decrypted");
        }
        
        // TODO
        // We really want to handle parsing into a DocumentFragment rather than
        // a Document so that we can handle EncryptedData with type of 'Content'.
        // And then unmarshall the 1 to N top-level Element nodes into a List of XMLObjects
        // and return that, or something along those lines.
        // Maybe need to use an LSParser.parseWithContext ... ?
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        Document document;
        try {
            document = parserPool.parse(input);
        } catch (XMLParserException e) {
            throw new DecryptionException("XML parsing error on decrypted byte array", e);
        }
        
        Unmarshaller unmarshaller = 
            Configuration.getUnmarshallerFactory().getUnmarshaller(document.getDocumentElement());
        XMLObject xmlObject;
        try {
            xmlObject = unmarshaller.unmarshall(document.getDocumentElement());
        } catch (UnmarshallingException e) {
            throw new DecryptionException("Error unmarshalling decrypted data", e);
        }
        
        return xmlObject;
        
    }
    
 
    
    /**
     * Decrypts the supplied EncryptedKey and returns the resulting Java security Key object.
     * The algorithm of the decrypted key must be supplied by the caller based on knowledge of
     * the associated EncryptedData information.
     * 
     * @param encryptedKey encrypted key element containing the encrypted key to be decrypted
     * @param algorithm  the algorithm associated with the decrypted key
     * @return the decrypted key
     * @throws DecryptionException exception indicating a decryption error
     */
    public Key decryptKey(EncryptedKey encryptedKey, String algorithm) throws DecryptionException {
        if (kekResolver == null) {
            throw new DecryptionException("Unable to decrypt EncryptedKey, no key encryption key resolver is set");
        }
        
        Element targetElement = encryptedKey.getDOM();
        if (targetElement == null) {
           Marshaller marshaller = 
               Configuration.getMarshallerFactory().getMarshaller(EncryptedKey.DEFAULT_ELEMENT_NAME);
           try {
               targetElement = marshaller.marshall(encryptedKey);
           } catch (MarshallingException e) {
               throw new DecryptionException("Error marshalling EncryptedKey for decryption", e);
           }
        }
        
        Key kek = null;
        try {
            kek = kekResolver.resolveKey(encryptedKey.getKeyInfo());
        } catch (KeyException e) {
            throw new DecryptionException("Error resolving the key encryption key", e);
        }
        if (kek == null) {
            throw new DecryptionException("Failed to resolve key encryption key");
        }
        
        XMLCipher xmlCipher;
        try {
            xmlCipher = XMLCipher.getInstance();
            xmlCipher.init(XMLCipher.UNWRAP_MODE, kek);
        } catch (XMLEncryptionException e) {
            throw new DecryptionException("Error initialzing cipher instance on key decryption", e);
        }
        
        org.apache.xml.security.encryption.EncryptedKey encKey; 
        try {
            encKey = xmlCipher.loadEncryptedKey(targetElement.getOwnerDocument(), targetElement);
        } catch (XMLEncryptionException e) {
            throw new DecryptionException("Error when loading library native encrypted key representation", e);
        }
        
        Key key = null;
        try {
            key = xmlCipher.decryptKey(encKey, algorithm);
        } catch (XMLEncryptionException e) {
            throw new DecryptionException("Error decrypting kek encryption key", e);
        }
        if (key == null) {
            throw new DecryptionException("Key could not be decrypted");
        }
        return key;
    }
    
    /**
     * Resolve the data decryption key for the specified EncryptedData element.
     * 
     * @param encryptedData the EncryptedData which is to be decrypted
     * @return the data decryption key
     * @throws DecryptionException exception indicating a decryption error
     */
    private Key resolveDataDecryptionKey(EncryptedData encryptedData) throws DecryptionException {
        Key dataEncKey = null;
        
        // This logic is pretty much straight from the C++ decrypter...
        
        // First try and resolve the data encryption key directly
        if (resolver != null) {
            try {
                dataEncKey = resolver.resolveKey(encryptedData.getKeyInfo());
            } catch (KeyException e) {
                throw new DecryptionException("Error resolving data encryption key", e);
            }
        }
        
        // If no key was resolved and have a KEK resolver, try to resolve keys from
        // KeyInfo/EncryptedKey's obtained from directly within this EncryptedData.
        if (dataEncKey == null && kekResolver != null) {
            String algorithm = encryptedData.getEncryptionMethod().getAlgorithm();
            if (algorithm == null || algorithm.equals("")) {
                throw new DecryptionException("No EncryptionMethod/@Algorithm set, key decryption cannot proceed.");
            }
           
            if (encryptedData.getKeyInfo() != null) {
                List<EncryptedKey> encryptedKeys = encryptedData.getKeyInfo().getEncryptedKeys();
                for (EncryptedKey encryptedKey: encryptedKeys) {
                    try {
                        dataEncKey = decryptKey(encryptedKey, algorithm);
                        if (dataEncKey != null) {
                            break;
                        }
                    } catch (DecryptionException e) {
                       log.warn(e.getMessage());
                    }
                }
            }
            
            // If no key was resolved, and our data encryption key resolver is an EncryptedKey resolver
            // specialization capable of resolving EncryptedKey's from another context, try that.
            if (dataEncKey == null && resolver instanceof EncryptedKeyInfoResolver) {
                EncryptedKeyInfoResolver ekr = (EncryptedKeyInfoResolver) resolver;
                EncryptedKey encryptedKey = null;
                try {
                    encryptedKey = ekr.resolveKey(encryptedData);
                } catch (KeyException e) {
                    throw new DecryptionException("Error in EncryptedKeyInfoResolver", e);
                }
                if (encryptedKey != null) {
                    try {
                        dataEncKey = decryptKey(encryptedKey, algorithm); 
                    } catch (DecryptionException e) {
                        log.warn(e.getMessage());
                    }
                }
            }
        }
        
        return dataEncKey;
    }
    
    static {
        // Create the parser pool used in decryption of EncryptedData elements
        HashMap<String, Boolean> features = new HashMap<String, Boolean>();
        features.put("http://apache.org/xml/features/validation/schema/normalized-value", Boolean.FALSE);
        features.put("http://apache.org/xml/features/dom/defer-node-expansion", Boolean.FALSE);

        parserPool = new ParserPool(true, null, features);
    }

}