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

import javax.crypto.KeyGenerator;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.keys.KeyInfo;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObjectBaseTestCase;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.mock.SimpleXMLObject;
import org.w3c.dom.Element;

/**
 *
 */
public class SimpleEncryptionTest extends XMLObjectBaseTestCase {

    /**
     * Constructor
     *
     */
    public SimpleEncryptionTest() {
        super();
    }
    
    //TODO Will change when schema XMLObjects are implemented
    
    //TODO these aren't really unit tests yet, just testing the 
    // Encrypter functionality.
    
    public void testSymmetricWithKeyNameNoWrap() {
        SimpleXMLObject sxo = (SimpleXMLObject) unmarshallElement("/data/org/opensaml/xml/encryption/SimpleEncryptionTest.xml");
        //printXML(sxo.getDOM(), null);
        
        Encrypter encrypter = new Encrypter();
        
        // Just going to encrypt with a random key, and store a KeyInfo/KeyName,
        // pretend we got this key from a key store or metadata.
        String algoURI = XMLCipher.AES_256;
        Key encKey = generateKey(algoURI);
        
        EncryptionParameters encParams = new EncryptionParameters();
        encParams.setAlgorithm(algoURI);
        encParams.setEncryptionKey(encKey);
        
        EncryptedData encData = null;
        try {
            encData = encrypter.encryptElement(sxo, encParams, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        KeyInfo keyInfo = new KeyInfo(sxo.getDOM().getOwnerDocument());
        keyInfo.addKeyName("BrentKeyName");
        encData.getXMLEncData().setKeyInfo(keyInfo);
        
        Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(encData);
        Element encDataElement = null;
        try {
            encDataElement = marshaller.marshall(encData);
        } catch (MarshallingException e) {
            e.printStackTrace();
        }
        
        printXML(encDataElement, null);
        
    }
    
    public void testSymmetricWithSymmetricWrap() {
        SimpleXMLObject sxo = (SimpleXMLObject) unmarshallElement("/data/org/opensaml/xml/encryption/SimpleEncryptionTest.xml");
        //printXML(sxo.getDOM(), null);
        
        Encrypter encrypter = new Encrypter();
        
        String algoURI = XMLCipher.AES_256;
        Key encKey = generateKey(algoURI);
        
        EncryptionParameters encParams = new EncryptionParameters();
        encParams.setAlgorithm(algoURI);
        encParams.setEncryptionKey(encKey);
        
        String kekAlgoURI = XMLCipher.TRIPLEDES_KeyWrap;
        Key kekKey = generateKey(kekAlgoURI);
        
        KeyEncryptionParameters kekParams = new KeyEncryptionParameters();
        kekParams.setAlgorithm(kekAlgoURI);
        kekParams.setEncryptionKey(kekKey);
        
        EncryptedData encData = null;
        try {
            encData = encrypter.encryptElement(sxo, encParams, kekParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(encData);
        Element encDataElement = null;
        try {
            encDataElement = marshaller.marshall(encData);
        } catch (MarshallingException e) {
            e.printStackTrace();
        }
        
        printXML(encDataElement, null);
        
    }
    
    /**
     * Generates a random Java JCE Key object from the specified XML Encryption algorithm URI
     * 
     * @param algoURI  The XML Encryption algorithm URI
     * @return
     */
    protected Key generateKey(String algoURI) {
        String jceAlgorithmName = JCEMapper.getJCEKeyAlgorithmFromURI(algoURI);
        int keyLength = JCEMapper.getKeyLengthFromURI(algoURI);
        Key key = null;
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(jceAlgorithmName);
            keyGenerator.init(keyLength);
            key = keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return key;
    }
 
}
