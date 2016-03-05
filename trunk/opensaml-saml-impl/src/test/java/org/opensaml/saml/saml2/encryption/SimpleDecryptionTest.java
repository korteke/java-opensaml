/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.saml.saml2.encryption;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import java.security.Key;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.SecretKey;

import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.EncryptedAttribute;
import org.opensaml.saml.saml2.core.EncryptedID;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NewEncryptedID;
import org.opensaml.saml.saml2.core.NewID;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.saml.saml2.encryption.Encrypter;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.encryption.support.DecryptionException;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.encryption.support.EncryptionException;
import org.opensaml.xmlsec.encryption.support.DataEncryptionParameters;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.StaticKeyInfoCredentialResolver;
import org.w3c.dom.Document;

/**
 * Simple tests for SAML 2 decrypter, using a hardcoded key (so not testing complex encrypted key resolution, etc).
 */
public class SimpleDecryptionTest extends XMLObjectBaseTestCase {
    
    private KeyInfoCredentialResolver keyResolver;
    
    private String encURI;
    private DataEncryptionParameters encParams;
    
    private Encrypter encrypter;
    
    /**
     * Constructor.
     *
     */
    public SimpleDecryptionTest() {
        encURI = EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128;
        
    }
    
    @BeforeMethod
    protected void setUp() throws Exception {
        Credential encCred = AlgorithmSupport.generateSymmetricKeyAndCredential(encURI);
        encCred.getSecretKey();
        keyResolver = new StaticKeyInfoCredentialResolver(encCred);
        encParams = new DataEncryptionParameters();
        encParams.setAlgorithm(encURI);
        encParams.setEncryptionCredential(encCred);
        
        encrypter = new Encrypter(encParams);
        
    }
    
    /**
     * Test decryption of an EncryptedAssertion.
     *  
     * @throws XMLParserException  thrown if there is an error parsing the control XML file
     * @throws EncryptionException  thrown if there is an error encrypting the control XML
     */
    @Test
    public void testEncryptedAssertion() throws XMLParserException, EncryptionException {
        String filename = "/org/opensaml/saml/saml2/encryption/Assertion.xml";
        Document targetDOM = getDOM(filename);
        
        Assertion target = (Assertion) unmarshallElement(filename);
        EncryptedAssertion encryptedTarget = encrypter.encrypt(target);
        
        Decrypter decrypter = new Decrypter(keyResolver, null, null);
        
        SAMLObject decryptedTarget = null;
        try {
            decryptedTarget = decrypter.decrypt(encryptedTarget);
        } catch (DecryptionException e) {
            Assert.fail("Error on decryption of encrypted SAML 2 type to element: " + e);
        }
        
        Assert.assertNotNull(decryptedTarget, "Decrypted target was null");
        Assert.assertTrue(decryptedTarget instanceof Assertion, "Decrypted target was not the expected type");
        
        assertXMLEquals(targetDOM, decryptedTarget);
    }
    
    /**
     * Test decryption of an Assertion as an EncryptedID.
     *  
     * @throws XMLParserException  thrown if there is an error parsing the control XML file
     * @throws EncryptionException  thrown if there is an error encrypting the control XML
     */
    @Test
    public void testEncryptedAssertionAsID() throws XMLParserException, EncryptionException {
        String filename = "/org/opensaml/saml/saml2/encryption/Assertion.xml";
        Document targetDOM = getDOM(filename);
        
        Assertion target = (Assertion) unmarshallElement(filename);
        EncryptedID encryptedTarget = encrypter.encryptAsID(target);
        
        Decrypter decrypter = new Decrypter(keyResolver, null, null);
        
        SAMLObject decryptedTarget = null;
        try {
            decryptedTarget = decrypter.decrypt(encryptedTarget);
        } catch (DecryptionException e) {
            Assert.fail("Error on decryption of encrypted SAML 2 type to element: " + e);
        }
        
        Assert.assertNotNull(decryptedTarget, "Decrypted target was null");
        Assert.assertTrue(decryptedTarget instanceof Assertion, "Decrypted target was not the expected type");
        
        assertXMLEquals(targetDOM, decryptedTarget);
    }
    
    /**
     * Test decryption of an NameID as an EncryptedID.
     *  
     * @throws XMLParserException  thrown if there is an error parsing the control XML file
     * @throws EncryptionException  thrown if there is an error encrypting the control XML
     */
    @Test
    public void testEncryptedNameID() throws XMLParserException, EncryptionException {
        String filename = "/org/opensaml/saml/saml2/encryption/NameID.xml";
        Document targetDOM = getDOM(filename);
        
        NameID target = (NameID) unmarshallElement(filename);
        EncryptedID encryptedTarget = encrypter.encrypt(target);
        
        Decrypter decrypter = new Decrypter(keyResolver, null, null);
        
        SAMLObject decryptedTarget = null;
        try {
            decryptedTarget = decrypter.decrypt(encryptedTarget);
        } catch (DecryptionException e) {
            Assert.fail("Error on decryption of encrypted SAML 2 type to element: " + e);
        }
        
        Assert.assertNotNull(decryptedTarget, "Decrypted target was null");
        Assert.assertTrue(decryptedTarget instanceof NameID, "Decrypted target was not the expected type");
        
        assertXMLEquals(targetDOM, decryptedTarget);
    }
    
    /**
     * Test decryption of an NewID as an NewEncryptedID.
     *  
     * @throws XMLParserException  thrown if there is an error parsing the control XML file
     * @throws EncryptionException  thrown if there is an error encrypting the control XML
     */
    @Test
    public void testEncryptedNewID() throws XMLParserException, EncryptionException {
        String filename = "/org/opensaml/saml/saml2/encryption/NewID.xml";
        Document targetDOM = getDOM(filename);
        
        NewID target = (NewID) unmarshallElement(filename);
        NewEncryptedID encryptedTarget = encrypter.encrypt(target);
        
        Decrypter decrypter = new Decrypter(keyResolver, null, null);
        
        SAMLObject decryptedTarget = null;
        try {
            decryptedTarget = decrypter.decrypt(encryptedTarget);
        } catch (DecryptionException e) {
            Assert.fail("Error on decryption of encrypted SAML 2 type to element: " + e);
        }
        
        Assert.assertNotNull(decryptedTarget, "Decrypted target was null");
        Assert.assertTrue(decryptedTarget instanceof NewID, "Decrypted target was not the expected type");
        
        assertXMLEquals(targetDOM, decryptedTarget);
    }
    
    /**
     * Test decryption of an EncryptedAttribute.
     *  
     * @throws XMLParserException  thrown if there is an error parsing the control XML file
     * @throws EncryptionException  thrown if there is an error encrypting the control XML
     */
    @Test
    public void testEncryptedAttribute() throws XMLParserException, EncryptionException {
        String filename = "/org/opensaml/saml/saml2/encryption/Attribute.xml";
        Document targetDOM = getDOM(filename);
        
        Attribute target = (Attribute) unmarshallElement(filename);
        EncryptedAttribute encryptedTarget = encrypter.encrypt(target);
        
        Decrypter decrypter = new Decrypter(keyResolver, null, null);
        
        SAMLObject decryptedTarget = null;
        try {
            decryptedTarget = decrypter.decrypt(encryptedTarget);
        } catch (DecryptionException e) {
            Assert.fail("Error on decryption of encrypted SAML 2 type to element: " + e);
        }
        
        Assert.assertNotNull(decryptedTarget, "Decrypted target was null");
        Assert.assertTrue(decryptedTarget instanceof Attribute, "Decrypted target was not the expected type");
        
        assertXMLEquals(targetDOM, decryptedTarget);
    }
    
    /**
     *  Test error condition of invalid data decryption key.
     * @throws EncryptionException 
     *  
     * @throws XMLParserException  thrown if there is an error parsing the control XML file
     * @throws EncryptionException  thrown if there is an error encrypting the control XML
     * @throws NoSuchProviderException security provider was invalid
     * @throws NoSuchAlgorithmException security/key algorithm was invalid
     * @throws KeyException 
     */
    @Test
    public void testErrorInvalidDataDecryptionKey() 
            throws XMLParserException, EncryptionException, NoSuchAlgorithmException, NoSuchProviderException, KeyException {
        Key badKey = AlgorithmSupport.generateSymmetricKey(encURI);
        BasicCredential encCred = new BasicCredential((SecretKey) badKey);
        KeyInfoCredentialResolver badEncResolver = new StaticKeyInfoCredentialResolver(encCred);
        
        String filename = "/org/opensaml/saml/saml2/encryption/Assertion.xml";
        
        Assertion target = (Assertion) unmarshallElement(filename);
        EncryptedAssertion encryptedTarget = encrypter.encrypt(target);
        
        Decrypter decrypter = new Decrypter(badEncResolver, null, null);
        
        try {
            decrypter.decrypt(encryptedTarget);
            Assert.fail("Decryption should have failed due to bad decryption key");
        } catch (DecryptionException e) {
            // do nothing, should faile
        }
        
    }
    
    /**
     * Parse the XML file and return the DOM Document.
     * 
     * @param filename file containing control XML
     * @return parsed Document
     * @throws XMLParserException if parser encounters an error
     */
    private Document getDOM(String filename) throws XMLParserException {
        Document targetDOM = parserPool.parse(SimpleDecryptionTest.class.getResourceAsStream(filename));
        return targetDOM;
    }
    
}
