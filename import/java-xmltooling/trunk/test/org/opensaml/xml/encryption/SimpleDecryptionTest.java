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
import java.security.KeyPair;

import javax.crypto.SecretKey;

import org.opensaml.xml.StaticKeyInfoCredentialResolver;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBaseTestCase;
import org.opensaml.xml.mock.SimpleXMLObject;
import org.opensaml.xml.security.SecurityTestHelper;
import org.opensaml.xml.security.credential.BasicCredential;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialResolver;
import org.w3c.dom.Document;

/**
 * Simple tests for decryption.
 */
public class SimpleDecryptionTest extends XMLObjectBaseTestCase {
    
    private KeyInfoCredentialResolver keyResolver;
    private KeyInfoCredentialResolver kekResolver;
    
    private String encURI;
    private Key encKey;
    private EncryptionParameters encParams;
    private EncryptedData encryptedData;
    private EncryptedData encryptedContent;
    
    private String kekURI;
    private KeyPair kekKeyPair;
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
        kekURI = EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15;
        
        targetFile = "/data/org/opensaml/xml/encryption/SimpleDecryptionTest.xml";
    }
    
    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        
        encKey = SecurityTestHelper.generateKeyFromURI(encURI);
        BasicCredential encCred = new BasicCredential();
        encCred.setSecretKey((SecretKey) encKey);
        keyResolver = new StaticKeyInfoCredentialResolver(encCred);
        encParams = new EncryptionParameters();
        encParams.setAlgorithm(encURI);
        encParams.setEncryptionKey(encKey);
        
        kekKeyPair = SecurityTestHelper.generateKeyPairFromURI(kekURI, 1024);
        BasicCredential kekCred = new BasicCredential();
        kekCred.setPublicKey(kekKeyPair.getPublic());
        kekCred.setPrivateKey(kekKeyPair.getPrivate());
        kekResolver = new StaticKeyInfoCredentialResolver(kekCred);
        kekParams = new KeyEncryptionParameters();
        kekParams.setAlgorithm(kekURI);
        kekParams.setEncryptionKey(kekKeyPair.getPublic());
        
        Encrypter encrypter = new Encrypter();
        encryptedKey = encrypter.encryptKey(encKey, kekParams, parserPool.newDocument());
        
        
        targetDOM = parserPool.parse(SimpleDecryptionTest.class.getResourceAsStream(targetFile));
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
        Decrypter decrypter = new Decrypter(null, kekResolver, null);
       
        Key decryptedKey = null;
        try {
            decryptedKey = decrypter.decryptKey(encryptedKey, encURI);
        } catch (DecryptionException e) {
            fail("Error on decryption of EncryptedKey: " + e);
        }
        
        assertEquals("Decrypted EncryptedKey", decryptedKey, encKey);
        
    }
    
    /**
     *  Test simple decryption of an EncryptedData object which is of type Element.
     */
    public void testEncryptedElement() {
        Decrypter decrypter = new Decrypter(keyResolver, null, null);
        
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
        Decrypter decrypter = new Decrypter(keyResolver, null, null);
        
        XMLObject decryptedXMLObject = null;
        try {
            decryptedXMLObject = decrypter.decryptData(encryptedContent);
            fail("This should have failed, decryption of element content not yet supported");
        } catch (DecryptionException e) {
            //fail("Error on decryption of EncryptedData to element content: " + e);
            //Currently this will fail, not yet supporting decryption of element content.
            assertTrue("Decryption of element content not yet supported", true);
        }
        
    }
 
}
