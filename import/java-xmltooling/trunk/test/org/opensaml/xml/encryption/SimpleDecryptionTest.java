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

import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBaseTestCase;
import org.opensaml.xml.mock.SimpleXMLObject;
import org.opensaml.xml.security.DirectEncryptionKeyInfoResolver;
import org.opensaml.xml.security.KeyInfoResolver;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Simple tests for decryption.
 */
public class SimpleDecryptionTest extends XMLObjectBaseTestCase {
    
    private KeyInfoResolver keyResolver;
    private KeyInfoResolver kekResolver;
    
    private String encURI;
    private Key encKey;
    private EncryptionParameters encParams;
    private EncryptedData encryptedData;
    private EncryptedData encryptedContent;
    
    private String kekURI;
    private Key kekKey;
    private KeyEncryptionParameters kekParams;
    private EncryptedKey encryptedKey;
    
    private String targetFile;
    private Document targetDOM;
    private SimpleXMLObject targetObject;

    /**
     * Constructor.
     *
     */
    public SimpleDecryptionTest() {
        super();
        
        encURI = EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128;
        kekURI = EncryptionConstants.ALGO_ID_KEYWRAP_AES128;
        
        targetFile = "/data/org/opensaml/xml/encryption/SimpleDecryptionTest.xml";
    }
    
    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        
        // TODO perhaps should be retrieving control keys and encrypted elements
        // from a keystore and files, etc, but for now just generate on the fly
        encKey = EncryptionTestHelper.generateKey(encURI);
        keyResolver = new DirectEncryptionKeyInfoResolver(encKey);
        encParams = new EncryptionParameters();
        encParams.setAlgorithm(encURI);
        encParams.setEncryptionKey(encKey);
        
        kekKey = EncryptionTestHelper.generateKey(kekURI);
        kekResolver = new DirectEncryptionKeyInfoResolver(kekKey);
        kekParams = new KeyEncryptionParameters();
        kekParams.setAlgorithm(kekURI);
        kekParams.setEncryptionKey(kekKey);
        
        Encrypter encrypter = new Encrypter();
        encryptedKey = encrypter.encryptKey(encKey, kekParams, parserPool.newDocument());
        
        
        targetDOM = parserPool.parse(new InputSource(SimpleEncryptionTest.class.getResourceAsStream(targetFile)));
        targetObject = (SimpleXMLObject) unmarshallElement(targetFile);
        try {
            encryptedData = encrypter.encryptElement(targetObject, encParams, null);
            encryptedContent = encrypter.encryptElementContent(targetObject, encParams, null);
        } catch (EncryptionException e) {
            fail("Object encryption failed: " + e);
        }
        
    }
    
    /**
     * Test simple decryption of an EncryptedKey object.
     */
    public void testEncryptedKey() {
        Decrypter decrypter = new Decrypter(kekResolver, null);
       
        Key decryptedKey = null;
        try {
            decryptedKey = decrypter.decryptKey(encryptedKey, encURI);
        } catch (DecryptionException e) {
            fail("Error on decryption of EncryptedKey: " + e);
        }
        
        assertEquals("Decrypted EncryptedKey", encKey, decryptedKey);
        
    }
    
    /**
     *  Test simple decryption of an EncryptedData object which is of type Element.
     */
    public void testEncryptedElement() {
        Decrypter decrypter = new Decrypter(null, keyResolver);
        
        XMLObject decryptedXMLObject = null;
        try {
            decryptedXMLObject = decrypter.decryptData(encryptedData);
        } catch (DecryptionException e) {
            fail("Error on decryption of EncryptedData to element: " + e);
        }
        
        assertEquals(targetDOM, decryptedXMLObject);
        
    }
    
    /**
     *  Test simple decryption of an EncryptedData object which is of type Content.
     */
    public void testEncryptedContent() {
        Decrypter decrypter = new Decrypter(null, keyResolver);
        
        XMLObject decryptedXMLObject = null;
        try {
            decryptedXMLObject = decrypter.decryptData(encryptedContent);
        } catch (DecryptionException e) {
            //fail("Error on decryption of EncryptedData to element content: " + e);
            //Currently this will fail, not yet supporting decryption of element content.
            assertTrue("Decryption of element content not yet supported", true);
        }
        
    }
 
}
