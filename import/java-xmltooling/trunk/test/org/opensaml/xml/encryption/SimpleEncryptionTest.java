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
import java.security.NoSuchProviderException;

import org.opensaml.xml.XMLObjectBaseTestCase;
import org.opensaml.xml.mock.SimpleXMLObject;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.security.SecurityTestHelper;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.KeyName;

/**
 * Simple tests for encryption.
 */
public class SimpleEncryptionTest extends XMLObjectBaseTestCase {
    
    private Encrypter encrypter;
    private EncryptionParameters encParams;
    private KeyEncryptionParameters kekParams;
    private KeyInfo keyInfo;
    private KeyInfo kekKeyInfo;
    
    private String expectedKeyName;
    private String expectedKEKKeyName;
    private String expectedRecipient;
    private String targetFile;

    /**
     * Constructor.
     *
     */
    public SimpleEncryptionTest() {
        super();
        
        expectedKeyName = "SuperSecretKey";
        expectedKEKKeyName = "SecretKEKKey";
        expectedRecipient = "CoolRecipient";
        targetFile = "/data/org/opensaml/xml/encryption/SimpleEncryptionTest.xml";
    }
    
    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        
        encrypter = new Encrypter();
        encParams = new EncryptionParameters();
        kekParams = new KeyEncryptionParameters();
        keyInfo =  (org.opensaml.xml.signature.KeyInfo) 
            buildXMLObject(org.opensaml.xml.signature.KeyInfo.DEFAULT_ELEMENT_NAME);
        kekKeyInfo =  (org.opensaml.xml.signature.KeyInfo) 
            buildXMLObject(org.opensaml.xml.signature.KeyInfo.DEFAULT_ELEMENT_NAME);
        
    }

    /**
     *  Test basic encryption with symmetric key, no key wrap,
     *  set key name in passed KeyInfo object.
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     */
    public void testSymmetricWithKeyNameNoWrap() throws NoSuchAlgorithmException, NoSuchProviderException {
        SimpleXMLObject sxo = (SimpleXMLObject) unmarshallElement(targetFile);
        
        String algoURI = EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128;
        Key encKey = SecurityTestHelper.generateKeyFromURI(algoURI);
        encParams.setAlgorithm(algoURI);
        encParams.setEncryptionKey(encKey);
        
        KeyName keyName = (KeyName) buildXMLObject(org.opensaml.xml.signature.KeyName.DEFAULT_ELEMENT_NAME);
        keyName.setValue(expectedKeyName);
        keyInfo.getKeyNames().add(keyName);
        encParams.setKeyInfo(keyInfo);
        
        EncryptedData encData = null;
        try {
            encData = encrypter.encryptElement(sxo, encParams, null);
        } catch (EncryptionException e) {
            fail("Object encryption failed: " + e);
        }
        
        assertNotNull(encData);
        assertEquals("Type attribute", EncryptionConstants.TYPE_ELEMENT, encData.getType());
        assertEquals("Algorithm attribute", algoURI, encData.getEncryptionMethod().getAlgorithm());
        assertNotNull("KeyInfo", encData.getKeyInfo());
        assertEquals("KeyName", expectedKeyName, encData.getKeyInfo().getKeyNames().get(0).getValue());
        
        assertEquals("Number of EncryptedKeys", 0, encData.getKeyInfo().getEncryptedKeys().size());
        
    }
    
    /**
     *  Test basic encryption with a symmetric key, with a symmetric key wrap,
     *  set key encrypting key name in passed KeyInfo object.
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     */
    public void testSymmetricWithSymmetricWrap() throws NoSuchAlgorithmException, NoSuchProviderException {
        SimpleXMLObject sxo = (SimpleXMLObject) unmarshallElement(targetFile);
        
        String algoURI = EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128;
        Key encKey = SecurityTestHelper.generateKeyFromURI(algoURI);
        
        encParams.setAlgorithm(algoURI);
        encParams.setEncryptionKey(encKey);
        
        String kekURI = EncryptionConstants.ALGO_ID_KEYWRAP_AES128;
        Key kek = SecurityTestHelper.generateKeyFromURI(kekURI);
        
        kekParams.setAlgorithm(kekURI);
        kekParams.setEncryptionKey(kek);
        
        KeyName kekKeyName = (KeyName) buildXMLObject(org.opensaml.xml.signature.KeyName.DEFAULT_ELEMENT_NAME);
        kekKeyName.setValue(expectedKEKKeyName);
        kekKeyInfo.getKeyNames().add(kekKeyName);
        kekParams.setKeyInfo(kekKeyInfo);
        kekParams.setRecipient(expectedRecipient);
        
        EncryptedData encData = null;
        try {
            encData = encrypter.encryptElement(sxo, encParams, kekParams);
        } catch (EncryptionException e) {
            fail("Object encryption failed: " + e);
        }
        
        assertNotNull(encData);
        assertEquals("Type attribute", EncryptionConstants.TYPE_ELEMENT, encData.getType());
        assertEquals("Algorithm attribute", algoURI, encData.getEncryptionMethod().getAlgorithm());
        assertNotNull("KeyInfo", encData.getKeyInfo());
        
        EncryptedKey ek =  encData.getKeyInfo().getEncryptedKeys().get(0);
        assertNotNull("KeyInfo/EncryptedKey", ek);
        assertEquals("EncryptedKey Recipient attribute", ek.getRecipient(), expectedRecipient);
        assertEquals("EncryptedKey Algorithm attribute", ek.getEncryptionMethod().getAlgorithm(), kekURI);
        assertEquals("KEK KeyName", expectedKEKKeyName, ek.getKeyInfo().getKeyNames().get(0).getValue());
    }
    
    /**
     *  Test basic encryption of element content with symmetric key, no KeyInfo.
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     */
    public void testElementContent() throws NoSuchAlgorithmException, NoSuchProviderException {
        SimpleXMLObject sxo = (SimpleXMLObject) unmarshallElement(targetFile);
        
        String algoURI = EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128;
        Key encKey = SecurityTestHelper.generateKeyFromURI(algoURI);
        encParams.setAlgorithm(algoURI);
        encParams.setEncryptionKey(encKey);
        
        EncryptedData encData = null;
        try {
            encData = encrypter.encryptElementContent(sxo, encParams, null);
        } catch (EncryptionException e) {
            fail("Object encryption failed: " + e);
        }
        
        assertNotNull(encData);
        assertEquals("Type attribute", EncryptionConstants.TYPE_CONTENT, encData.getType());
        assertEquals("Algorithm attribute", algoURI, encData.getEncryptionMethod().getAlgorithm());
        assertNull("KeyInfo", encData.getKeyInfo());
    }
    
    /**
     *  Test basic encryption of a symmetric key into an EncryptedKey,
     *  set key encrypting key name in passed KeyInfo object.
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     */
    public void testEncryptKey() throws NoSuchAlgorithmException, NoSuchProviderException {
        String algoURI = EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128;
        Key encKey = SecurityTestHelper.generateKeyFromURI(algoURI);
        
        String kekURI = EncryptionConstants.ALGO_ID_KEYWRAP_AES128;
        Key kek = SecurityTestHelper.generateKeyFromURI(kekURI);
        
        kekParams.setAlgorithm(kekURI);
        kekParams.setEncryptionKey(kek);
        
        KeyName kekKeyName = (KeyName) buildXMLObject(org.opensaml.xml.signature.KeyName.DEFAULT_ELEMENT_NAME);
        kekKeyName.setValue(expectedKEKKeyName);
        kekKeyInfo.getKeyNames().add(kekKeyName);
        kekParams.setKeyInfo(kekKeyInfo);
        kekParams.setRecipient(expectedRecipient);
        
        EncryptedKey xmlEncKey = null;
        try {
            xmlEncKey = encrypter.encryptKey(encKey, kekParams, parserPool.newDocument());
        } catch (EncryptionException e) {
            fail("Object encryption failed: " + e);
        } catch (XMLParserException e) {
            fail("Failed to obtain new document from parse pool: " + e);
        }
        
        assertNotNull(xmlEncKey);
        assertEquals("Recipient attribute", expectedRecipient, xmlEncKey.getRecipient());
        assertEquals("Algorithm attribute", kekURI, xmlEncKey.getEncryptionMethod().getAlgorithm());
        assertNotNull("KeyInfo", xmlEncKey.getKeyInfo());
        assertEquals("KEK KeyName", expectedKEKKeyName, xmlEncKey.getKeyInfo().getKeyNames().get(0).getValue());
    }
    

 
}
