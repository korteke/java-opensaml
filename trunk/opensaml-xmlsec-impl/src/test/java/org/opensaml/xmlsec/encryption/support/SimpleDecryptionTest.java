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

package org.opensaml.xmlsec.encryption.support;

import java.security.Key;
import java.security.KeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import java.util.Collections;

import javax.crypto.SecretKey;

import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.security.SecurityProviderTestSupport;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.StaticKeyInfoCredentialResolver;
import org.opensaml.xmlsec.mock.SignableSimpleXMLObject;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

/**
 * Simple tests for decryption.
 */
public class SimpleDecryptionTest extends XMLObjectBaseTestCase {
    
    private KeyInfoCredentialResolver keyResolver;
    private KeyInfoCredentialResolver kekResolver;
    
    private String encURI;
    private Key encKey;
    private DataEncryptionParameters encParams;
    private EncryptedData encryptedData;
    private EncryptedData encryptedContent;
    private Credential encCred;
    
    private String kekURI;
    private KeyEncryptionParameters kekParams;
    private EncryptedKey encryptedKey;
    private Credential kekCred;
    
    private String targetFile;
    private Document targetDOM;
    private SignableSimpleXMLObject targetObject;
    
    private SecurityProviderTestSupport providerSupport;

    /**
     * Constructor.
     *
     */
    public SimpleDecryptionTest() {
        super();
        
        providerSupport = new SecurityProviderTestSupport();
        
        encURI = EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128;
        kekURI = EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP;
        
        targetFile = "/org/opensaml/xmlsec/encryption/support/SimpleDecryptionTest.xml";
    }
    
    @BeforeMethod
    protected void setUp() throws Exception {
        encCred = AlgorithmSupport.generateSymmetricKeyAndCredential(encURI);
        encKey = encCred.getSecretKey();
        keyResolver = new StaticKeyInfoCredentialResolver(encCred);
        encParams = new DataEncryptionParameters();
        encParams.setAlgorithm(encURI);
        encParams.setEncryptionCredential(encCred);
        
        
        kekCred = AlgorithmSupport.generateKeyPairAndCredential(kekURI, 1024, true);
        kekResolver = new StaticKeyInfoCredentialResolver(kekCred);
        kekParams = new KeyEncryptionParameters();
        kekParams.setAlgorithm(kekURI);
        kekParams.setEncryptionCredential(kekCred);
        
        Encrypter encrypter = new Encrypter();
        encryptedKey = encrypter.encryptKey(encKey, kekParams, parserPool.newDocument());
        
        
        targetDOM = parserPool.parse(SimpleDecryptionTest.class.getResourceAsStream(targetFile));
        targetObject = (SignableSimpleXMLObject) unmarshallElement(targetFile);
        try {
            encryptedData = encrypter.encryptElement(targetObject, encParams);
            encryptedContent = encrypter.encryptElementContent(targetObject, encParams);
        } catch (EncryptionException e) {
            Assert.fail("Object encryption failed: " + e);
        }
        
    }
    
    /**
     * Test simple decryption of an EncryptedKey object.
     */
    @Test
    public void testEncryptedKey() {
        Decrypter decrypter = new Decrypter(null, kekResolver, null);
       
        Key decryptedKey = null;
        try {
            decryptedKey = decrypter.decryptKey(encryptedKey, encURI);
        } catch (DecryptionException e) {
            Assert.fail("Error on decryption of EncryptedKey: " + e);
        }
        
        Assert.assertEquals(encKey, decryptedKey, "Decrypted EncryptedKey");
        
    }
    
    /**
     *  Test simple decryption of an EncryptedData object which is of type Element.
     */
    @Test
    public void testEncryptedElement() {
        Decrypter decrypter = new Decrypter(keyResolver, null, null);
        
        XMLObject decryptedXMLObject = null;
        try {
            decryptedXMLObject = decrypter.decryptData(encryptedData);
        } catch (DecryptionException e) {
            Assert.fail("Error on decryption of EncryptedData to element: " + e);
        }
        
        assertXMLEquals(targetDOM, decryptedXMLObject);
        
    }
    
    /**
     *  Test EncryptedData decryption which should fail due to blacklist validation.
     * @throws DecryptionException 
     */
    @Test(expectedExceptions=DecryptionException.class)
    public void testEncryptedDataAlgorithmBlacklistFail() throws DecryptionException {
        Decrypter decrypter = new Decrypter(keyResolver, null, null, null, Collections.singleton(encURI));
        decrypter.decryptData(encryptedData);
    }
    
    /**
     *  Test EncryptedData decryption which should fail due to whitelist validation.
     * @throws DecryptionException 
     */
    @Test(expectedExceptions=DecryptionException.class)
    public void testEncryptedDataAlgorithmWhitelistFail() throws DecryptionException {
        Decrypter decrypter = new Decrypter(keyResolver, null, null, Collections.singleton("urn-x:some:bogus:algo"), null);
        decrypter.decryptData(encryptedData);
    }
    
    /**
     *  Test EncryptedData decryption which should pass the whitelist validation b/c the list specifies
     *  the algorithm in use.
     * @throws DecryptionException 
     */
    @Test()
    public void testEncryptedDataAlgorithmWhitelistPass() throws DecryptionException {
        Decrypter decrypter = new Decrypter(keyResolver, null, null, Collections.singleton(encURI), null);
        decrypter.decryptData(encryptedData);
    }
    
    /**
     *  Test EncryptedKey decryption which should fail due to blacklist validation.
     * @throws DecryptionException 
     */
    @Test(expectedExceptions=DecryptionException.class)
    public void testEncryptedKeyAlgorithmBlacklistFail() throws DecryptionException {
        // Note: here testing the implicit digest method and MGF, which are the SHA-1 variants.
        Decrypter decrypter = new Decrypter(null, kekResolver, null, null, Collections.singleton(kekURI));
        decrypter.decryptKey(encryptedKey, encURI);
    }
    
    /**
     *  Test EncryptedKey decryption which should fail due to whitelist validation.
     * @throws DecryptionException 
     * @throws XMLParserException 
     * @throws EncryptionException 
     */
    @Test(expectedExceptions=DecryptionException.class)
    public void testEncryptedKeyDigestMethodBlacklistFail() throws DecryptionException, EncryptionException, XMLParserException {
        providerSupport.loadBC();
        
        try {
            // Encrypt and test with explicit digest method and MGF
            KeyEncryptionParameters params = new KeyEncryptionParameters();
            params.setAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11);
            params.setEncryptionCredential(kekCred);
            params.setRSAOAEPParameters(new RSAOAEPParameters(EncryptionConstants.ALGO_ID_DIGEST_SHA256, EncryptionConstants.ALGO_ID_MGF1_SHA256, null));
            
            Encrypter encrypter = new Encrypter();
            encryptedKey = encrypter.encryptKey(encKey, params, parserPool.newDocument());
            
            Decrypter decrypter = new Decrypter(null, kekResolver, null, null, Collections.singleton(EncryptionConstants.ALGO_ID_DIGEST_SHA256));
            decrypter.decryptKey(encryptedKey, encURI);
        } finally {
            providerSupport.unloadBC();
        }
    }
    
    /**
     *  Test EncryptedKey decryption which should fail due to whitelist validation.
     * @throws DecryptionException 
     * @throws XMLParserException 
     * @throws EncryptionException 
     */
    @Test(expectedExceptions=DecryptionException.class)
    public void testEncryptedKeyMGFBlacklistFail() throws DecryptionException, EncryptionException, XMLParserException {
        providerSupport.loadBC();
        
        try {
            // Encrypt and test with explicit digest method and MGF
            KeyEncryptionParameters params = new KeyEncryptionParameters();
            params.setAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11);
            params.setEncryptionCredential(kekCred);
            params.setRSAOAEPParameters(new RSAOAEPParameters(EncryptionConstants.ALGO_ID_DIGEST_SHA256, EncryptionConstants.ALGO_ID_MGF1_SHA256, null));
            
            Encrypter encrypter = new Encrypter();
            encryptedKey = encrypter.encryptKey(encKey, params, parserPool.newDocument());
            
            Decrypter decrypter = new Decrypter(null, kekResolver, null, null, Collections.singleton(EncryptionConstants.ALGO_ID_MGF1_SHA256));
            decrypter.decryptKey(encryptedKey, encURI);
        } finally {
            providerSupport.unloadBC();
        }
    }
    
    /**
     *  Test EncryptedKey decryption which should fail due to whitelist validation.
     * @throws DecryptionException 
     * @throws XMLParserException 
     * @throws EncryptionException 
     */
    @Test(expectedExceptions=DecryptionException.class)
    public void testEncryptedKeyAlgorithmWhitelistFail() throws DecryptionException, EncryptionException, XMLParserException {
        // Note: here testing the implicit digest method and MGF, which are the SHA-1 variants.
        Decrypter decrypter = new Decrypter(null, kekResolver, null, Collections.singleton("urn-x:some:bogus:algo"), null);
        decrypter.decryptKey(encryptedKey, encURI);
    }
    
    /**
     *  Test EncryptedKey decryption which should fail due to whitelist validation.
     * @throws DecryptionException 
     * @throws XMLParserException 
     * @throws EncryptionException 
     */
    @Test(expectedExceptions=DecryptionException.class)
    public void testEncryptedKeyDigestMethodWhitelistFail() throws DecryptionException, EncryptionException, XMLParserException {
        providerSupport.loadBC();
        
        try {
            // Encrypt and test with explicit digest method and MGF
            KeyEncryptionParameters params = new KeyEncryptionParameters();
            params.setAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11);
            params.setEncryptionCredential(kekCred);
            params.setRSAOAEPParameters(new RSAOAEPParameters(EncryptionConstants.ALGO_ID_DIGEST_SHA256, EncryptionConstants.ALGO_ID_MGF1_SHA256, null));
            
            Encrypter encrypter = new Encrypter();
            encryptedKey = encrypter.encryptKey(encKey, params, parserPool.newDocument());
            
            Decrypter decrypter = new Decrypter(null, kekResolver, null, 
                    Arrays.asList(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11, 
                            "urn-x:some:bogus:algo",
                            EncryptionConstants.ALGO_ID_MGF1_SHA256), 
                            null);
            decrypter.decryptKey(encryptedKey, encURI);
        } finally {
            providerSupport.unloadBC();
        }
    }
    
    /**
     *  Test EncryptedKey decryption which should fail due to whitelist validation.
     * @throws DecryptionException 
     * @throws XMLParserException 
     * @throws EncryptionException 
     */
    @Test(expectedExceptions=DecryptionException.class)
    public void testEncryptedKeyMGFWhitelistFail() throws DecryptionException, EncryptionException, XMLParserException {
        providerSupport.loadBC();
        
        try {
            // Encrypt and test with explicit digest method and MGF
            KeyEncryptionParameters params = new KeyEncryptionParameters();
            params.setAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11);
            params.setEncryptionCredential(kekCred);
            params.setRSAOAEPParameters(new RSAOAEPParameters(EncryptionConstants.ALGO_ID_DIGEST_SHA256, EncryptionConstants.ALGO_ID_MGF1_SHA256, null));
            
            Encrypter encrypter = new Encrypter();
            encryptedKey = encrypter.encryptKey(encKey, params, parserPool.newDocument());
            
            Decrypter decrypter = new Decrypter(null, kekResolver, null, 
                    Arrays.asList(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11, 
                            EncryptionConstants.ALGO_ID_DIGEST_SHA256,
                            "urn-x:some:bogus:algo"),
                            null);
            decrypter.decryptKey(encryptedKey, encURI);
        } finally {
            providerSupport.unloadBC();
        }
    }
    
    /**
     *  Test EncryptedKey decryption which should pass the whitelist validation b/c the list specifies
     *  the algorithms in use.
     * @throws DecryptionException 
     * @throws XMLParserException 
     * @throws EncryptionException 
     */
    @Test()
    public void testEncryptedKeyAlgorithmWhitelistPass() throws DecryptionException, EncryptionException, XMLParserException {
        // Note: have to whitelist the implicit digest method and MGF, which are the SHA-1 variants.
        Decrypter decrypter = new Decrypter(null, kekResolver, null, 
                Arrays.asList(kekURI, SignatureConstants.ALGO_ID_DIGEST_SHA1, EncryptionConstants.ALGO_ID_MGF1_SHA1), null);
        decrypter.decryptKey(encryptedKey, encURI);
        
        providerSupport.loadBC();
        
        try {
            // Encrypt and test with explicit digest method and MGF
            KeyEncryptionParameters params = new KeyEncryptionParameters();
            params.setAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11);
            params.setEncryptionCredential(kekCred);
            params.setRSAOAEPParameters(new RSAOAEPParameters(EncryptionConstants.ALGO_ID_DIGEST_SHA256, EncryptionConstants.ALGO_ID_MGF1_SHA256, null));
            
            Encrypter encrypter = new Encrypter();
            encryptedKey = encrypter.encryptKey(encKey, params, parserPool.newDocument());
            
            decrypter = new Decrypter(null, kekResolver, null, 
                    Arrays.asList(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11, 
                            EncryptionConstants.ALGO_ID_DIGEST_SHA256, 
                            EncryptionConstants.ALGO_ID_MGF1_SHA256), 
                            null);
            decrypter.decryptKey(encryptedKey, encURI);
        } finally {
            providerSupport.unloadBC();
        }
        
    }
    
    /**
     *  Test decryption of an EncryptedData object which is of type Element, where the decryption
     *  key is found as an inline EncryptedKey within EncryptedData/KeyInfo.
     */
    @Test
    public void testEncryptedElementWithEncryptedKeyInline() {
        KeyInfo keyInfo = (KeyInfo) buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME);
        keyInfo.getEncryptedKeys().add(encryptedKey);
        encryptedData.setKeyInfo(keyInfo);
        
        EncryptedKeyResolver ekr = new InlineEncryptedKeyResolver();
        
        Decrypter decrypter = new Decrypter(null, kekResolver, ekr);
        
        XMLObject decryptedXMLObject = null;
        try {
            decryptedXMLObject = decrypter.decryptData(encryptedData);
        } catch (DecryptionException e) {
            Assert.fail("Error on decryption of EncryptedData to element: " + e);
        }
        
        assertXMLEquals(targetDOM, decryptedXMLObject);
        
    }
    
    /**
     *  Test error condition of no resolvers configured.
     */
    @Test
    public void testErrorNoResolvers() {
        Decrypter decrypter = new Decrypter(null, null, null);
        
        try {
            decrypter.decryptData(encryptedData);
            Assert.fail("Decryption should have failed, no resolvers configured");
        } catch (DecryptionException e) {
            // do nothing, should fail
        }
        
    }
    
    /**
     *  Test error condition of invalid data decryption key.
     *  
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     * @throws KeyException 
     */
    @Test
    public void testErrorInvalidDataDecryptionKey() throws NoSuchAlgorithmException, NoSuchProviderException, KeyException {
        Key badKey = AlgorithmSupport.generateSymmetricKey(encURI);
        BasicCredential encCred = new BasicCredential((SecretKey) badKey);
        KeyInfoCredentialResolver badEncResolver = new StaticKeyInfoCredentialResolver(encCred);
        
        Decrypter decrypter = new Decrypter(badEncResolver, null, null);
        
        try {
            decrypter.decryptData(encryptedData);
            Assert.fail("Decryption should have failed, invalid data decryption key");
        } catch (DecryptionException e) {
            // do nothing, should fail
        }
        
    }
    
    /**
     *  Test error condition of invalid key decryption key.
     *  
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     */
    @Test
    public void testErrorInvalidKeyDecryptionKey() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPair badKeyPair = AlgorithmSupport.generateKeyPair(kekURI, 1024);
        BasicCredential kekCred = new BasicCredential(badKeyPair.getPublic(), badKeyPair.getPrivate());
        KeyInfoCredentialResolver badKEKResolver = new StaticKeyInfoCredentialResolver(kekCred);
        
        Decrypter decrypter = new Decrypter(null, badKEKResolver, null);
        
        try {
            decrypter.decryptKey(encryptedKey, encURI);
            Assert.fail("Decryption should have failed, invalid key decryption key");
        } catch (DecryptionException e) {
            // do nothing, should fail
        }
        
    }
    
    /**
     *  Test simple decryption of an EncryptedData object which is of type Content.
     */
    @Test
    public void testEncryptedContent() {
        Decrypter decrypter = new Decrypter(keyResolver, null, null);
        
        try {
            decrypter.decryptData(encryptedContent);
            Assert.fail("This should have failed, decryption of element content not yet supported");
        } catch (DecryptionException e) {
            //fail("Error on decryption of EncryptedData to element content: " + e);
            //Currently this will fail, not yet supporting decryption of element content.
            Assert.assertTrue(true, "Decryption of element content not yet supported");
        }
        
    }
    
}
