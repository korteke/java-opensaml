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
import java.util.ArrayList;
import java.util.List;

import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.security.SecurityProviderTestSupport;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.encryption.MGF;
import org.opensaml.xmlsec.encryption.OAEPparams;
import org.opensaml.xmlsec.keyinfo.impl.StaticKeyInfoGenerator;
import org.opensaml.xmlsec.mock.SignableSimpleXMLObject;
import org.opensaml.xmlsec.signature.DigestMethod;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.KeyName;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

/**
 * Simple tests for encryption.
 */
public class SimpleEncryptionTest extends XMLObjectBaseTestCase {
    
    private Encrypter encrypter;
    
    private DataEncryptionParameters encParams;
    private String algoURI;
    
    private List<KeyEncryptionParameters> kekParamsList;
    
    private KeyEncryptionParameters kekParamsAES;
    private String kekURIAES;
    
    private KeyEncryptionParameters kekParamsRSA;
    private String kekURIRSA;
    
    private KeyInfo keyInfo;
    private KeyInfo kekKeyInfoAES;
    private KeyInfo kekKeyInfoRSA;
    
    private String expectedKeyName;
    private String expectedKEKKeyNameAES;
    private String expectedKEKKeyNameRSA;
    private String expectedRecipientRSA;
    private String expectedRecipientAES;
    private String targetFile;
    
    private SecurityProviderTestSupport providerSupport;
    

    /**
     * Constructor.
     *
     */
    public SimpleEncryptionTest() {
        super();
        
        providerSupport = new SecurityProviderTestSupport();
        
        expectedKeyName = "SuperSecretKey";
        expectedKEKKeyNameAES = "KEKKeyAES";
        expectedKEKKeyNameAES = "KEKKeyRSA";
        expectedRecipientRSA = "CoolRecipientRSA";
        expectedRecipientAES = "CoolRecipientAES";
        targetFile = "/data/org/opensaml/xmlsec/encryption/support/SimpleEncryptionTest.xml";
        
        algoURI = EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128;
        kekURIRSA = EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP;
        kekURIAES = EncryptionConstants.ALGO_ID_KEYWRAP_AES128;
    }
    
    @BeforeMethod
    protected void setUp() throws Exception {
        encrypter = new Encrypter();
        
        encParams = new DataEncryptionParameters();
        encParams.setAlgorithm(algoURI);
        encParams.setEncryptionCredential(AlgorithmSupport.generateSymmetricKeyAndCredential(algoURI));
        
        kekParamsList = new ArrayList<KeyEncryptionParameters>();
        
        kekParamsAES = new KeyEncryptionParameters();
        kekParamsAES.setAlgorithm(kekURIAES);
        kekParamsAES.setEncryptionCredential(AlgorithmSupport.generateSymmetricKeyAndCredential(kekURIAES));
        kekParamsAES.setRecipient(expectedRecipientAES);
        
        kekParamsRSA = new KeyEncryptionParameters();
        kekParamsRSA.setAlgorithm(kekURIRSA);
        kekParamsRSA.setEncryptionCredential(AlgorithmSupport.generateKeyPairAndCredential(kekURIRSA, 1024, false));
        kekParamsRSA.setRecipient(expectedRecipientRSA);
        
        keyInfo = (KeyInfo) buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME);
        KeyName keyName = (KeyName) buildXMLObject(KeyName.DEFAULT_ELEMENT_NAME);
        keyName.setValue(expectedKeyName);
        keyInfo.getKeyNames().add(keyName);
        
        kekKeyInfoAES = (KeyInfo) buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME);
        keyName = (KeyName) buildXMLObject(KeyName.DEFAULT_ELEMENT_NAME);
        keyName.setValue(expectedKEKKeyNameAES);
        kekKeyInfoAES.getKeyNames().add(keyName);
        
        kekKeyInfoRSA = (KeyInfo) buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME);
        keyName = (KeyName) buildXMLObject(KeyName.DEFAULT_ELEMENT_NAME);
        keyName.setValue(expectedKEKKeyNameRSA);
        kekKeyInfoRSA.getKeyNames().add(keyName);
    }

    /**
     *  Test data basic encryption with symmetric key, no key wrap,
     *  set key name in passed KeyInfo object.
     */
    @Test
    public void testEncryptDataWithKeyNameNoKEK() {
        SignableSimpleXMLObject sxo = (SignableSimpleXMLObject) unmarshallElement(targetFile);
        
        encParams.setKeyInfoGenerator(new StaticKeyInfoGenerator(keyInfo));
        
        EncryptedData encData = null;
        try {
            encData = encrypter.encryptElement(sxo, encParams);
        } catch (EncryptionException e) {
            Assert.fail("Object encryption failed: " + e);
        }
        
        Assert.assertNotNull(encData);
        Assert.assertEquals(encData.getType(), EncryptionConstants.TYPE_ELEMENT, "Type attribute");
        Assert.assertEquals(encData.getEncryptionMethod().getAlgorithm(), algoURI, "Algorithm attribute");
        Assert.assertNotNull(encData.getKeyInfo(), "KeyInfo");
        Assert.assertEquals(encData.getKeyInfo().getKeyNames().get(0).getValue(), expectedKeyName, "KeyName");
        
        Assert.assertEquals(encData.getKeyInfo().getEncryptedKeys().size(), 0, "Number of EncryptedKeys");
    }
    
    /**
     *  Test data basic encryption with symmetric key, one KEK.
     */
    @Test
    public void testEncryptDataSingleKEK() {
        SignableSimpleXMLObject sxo = (SignableSimpleXMLObject) unmarshallElement(targetFile);
        
        kekParamsRSA.setKeyInfoGenerator(new StaticKeyInfoGenerator(kekKeyInfoRSA));
        
        EncryptedData encData = null;
        try {
            encData = encrypter.encryptElement(sxo, encParams, kekParamsRSA);
        } catch (EncryptionException e) {
            Assert.fail("Object encryption failed: " + e);
        }
        
        Assert.assertNotNull(encData);
        Assert.assertEquals(encData.getType(), EncryptionConstants.TYPE_ELEMENT, "Type attribute");
        Assert.assertEquals(encData.getEncryptionMethod().getAlgorithm(), algoURI, "Algorithm attribute");
        Assert.assertNotNull(encData.getKeyInfo(), "KeyInfo");
        
        List<EncryptedKey> encKeys = encData.getKeyInfo().getEncryptedKeys();
        Assert.assertEquals(encData.getKeyInfo().getEncryptedKeys().size(), 1, "Number of EncryptedKeys");
        checkKEKRSA(encKeys.get(0), true);
    }
    
    /**
     *  Test basic data encryption with symmetric key, one KEK.
     */
    @Test
    public void testEncryptDataMultipleKEK() {
        SignableSimpleXMLObject sxo = (SignableSimpleXMLObject) unmarshallElement(targetFile);
        
        kekParamsRSA.setKeyInfoGenerator(new StaticKeyInfoGenerator(kekKeyInfoRSA));
        kekParamsAES.setKeyInfoGenerator(new StaticKeyInfoGenerator(kekKeyInfoAES));
        
        kekParamsList.add(kekParamsRSA);
        kekParamsList.add(kekParamsAES);
        
        EncryptedData encData = null;
        try {
            encData = encrypter.encryptElement(sxo, encParams, kekParamsList);
        } catch (EncryptionException e) {
            Assert.fail("Object encryption failed: " + e);
        }
        
        Assert.assertNotNull(encData);
        Assert.assertEquals(encData.getType(), EncryptionConstants.TYPE_ELEMENT, "Type attribute");
        Assert.assertEquals(encData.getEncryptionMethod().getAlgorithm(), algoURI, "Algorithm attribute");
        Assert.assertNotNull(encData.getKeyInfo(), "KeyInfo");
        
        List<EncryptedKey> encKeys = encData.getKeyInfo().getEncryptedKeys();
        Assert.assertEquals(encData.getKeyInfo().getEncryptedKeys().size(), 2, "Number of EncryptedKeys");
        checkKEKRSA(encKeys.get(0), true);
        checkKEKAES(encKeys.get(1), true);
    }
    
    /**
     *  Test basic content encryption with symmetric key, no key wrap,
     *  set key name in passed KeyInfo object.
     */
    @Test
    public void testEncryptContentWithKeyNameNoKEK() {
        SignableSimpleXMLObject sxo = (SignableSimpleXMLObject) unmarshallElement(targetFile);
        
        encParams.setKeyInfoGenerator(new StaticKeyInfoGenerator(keyInfo));
        
        EncryptedData encData = null;
        try {
            encData = encrypter.encryptElementContent(sxo, encParams);
        } catch (EncryptionException e) {
            Assert.fail("Object encryption failed: " + e);
        }
        
        Assert.assertNotNull(encData);
        Assert.assertEquals(encData.getType(), EncryptionConstants.TYPE_CONTENT, "Type attribute");
        Assert.assertEquals(encData.getEncryptionMethod().getAlgorithm(), algoURI, "Algorithm attribute");
        Assert.assertNotNull(encData.getKeyInfo(), "KeyInfo");
        Assert.assertEquals(encData.getKeyInfo().getKeyNames().get(0).getValue(), expectedKeyName, "KeyName");
        
        Assert.assertEquals(encData.getKeyInfo().getEncryptedKeys().size(), 0, "Number of EncryptedKeys");
    }
    
    /**
     *  Test basic content encryption with symmetric key, one KEK.
     */
    @Test
    public void testEncryptContentSingleKEK() {
        SignableSimpleXMLObject sxo = (SignableSimpleXMLObject) unmarshallElement(targetFile);
        
        kekParamsRSA.setKeyInfoGenerator(new StaticKeyInfoGenerator(kekKeyInfoRSA));
        
        EncryptedData encData = null;
        try {
            encData = encrypter.encryptElementContent(sxo, encParams, kekParamsRSA);
        } catch (EncryptionException e) {
            Assert.fail("Object encryption failed: " + e);
        }
        
        Assert.assertNotNull(encData);
        Assert.assertEquals(encData.getType(), EncryptionConstants.TYPE_CONTENT, "Type attribute");
        Assert.assertEquals(encData.getEncryptionMethod().getAlgorithm(), algoURI, "Algorithm attribute");
        Assert.assertNotNull(encData.getKeyInfo(), "KeyInfo");
        
        List<EncryptedKey> encKeys = encData.getKeyInfo().getEncryptedKeys();
        Assert.assertEquals(encData.getKeyInfo().getEncryptedKeys().size(), 1, "Number of EncryptedKeys");
        checkKEKRSA(encKeys.get(0), true);
    }
    
    /**
     *  Test basic encryption with symmetric key, one KEK.
     */
    @Test
    public void testEncryptContentMultipleKEK() {
        SignableSimpleXMLObject sxo = (SignableSimpleXMLObject) unmarshallElement(targetFile);
        
        kekParamsAES.setKeyInfoGenerator(new StaticKeyInfoGenerator(kekKeyInfoAES));
        kekParamsRSA.setKeyInfoGenerator(new StaticKeyInfoGenerator(kekKeyInfoRSA));
        
        kekParamsList.add(kekParamsRSA);
        kekParamsList.add(kekParamsAES);
        
        EncryptedData encData = null;
        try {
            encData = encrypter.encryptElementContent(sxo, encParams, kekParamsList);
        } catch (EncryptionException e) {
            Assert.fail("Object encryption failed: " + e);
        }
        
        Assert.assertNotNull(encData);
        Assert.assertEquals(encData.getType(), EncryptionConstants.TYPE_CONTENT, "Type attribute");
        Assert.assertEquals(encData.getEncryptionMethod().getAlgorithm(), algoURI, "Algorithm attribute");
        Assert.assertNotNull(encData.getKeyInfo(), "KeyInfo");
        
        List<EncryptedKey> encKeys = encData.getKeyInfo().getEncryptedKeys();
        Assert.assertEquals(encData.getKeyInfo().getEncryptedKeys().size(), 2, "Number of EncryptedKeys");
        checkKEKRSA(encKeys.get(0), true);
        checkKEKAES(encKeys.get(1), true);
    }
    
    /**
     *  Test basic encryption of a symmetric key into an EncryptedKey,
     *  set key encrypting key name in passed KeyInfo object.
     * @throws NoSuchProviderException bad JCA provider
     * @throws NoSuchAlgorithmException  bad JCA algorithm
     * @throws XMLParserException error creating new Document from pool
     * @throws KeyException 
     */
    @Test
    public void testEncryptKeySingleKEK() throws NoSuchAlgorithmException, NoSuchProviderException, 
            XMLParserException, KeyException {
        
        Key targetKey = AlgorithmSupport.generateSymmetricKey(algoURI);
        
        kekParamsRSA.setKeyInfoGenerator(new StaticKeyInfoGenerator(kekKeyInfoRSA));
        
        EncryptedKey encKey = null;
        Document ownerDocument = parserPool.newDocument();
        try {
            encKey = encrypter.encryptKey(targetKey, kekParamsRSA, ownerDocument);
        } catch (EncryptionException e) {
            Assert.fail("Object encryption failed: " + e);
        } 
        
        checkKEKRSA(encKey, true);
    }
    
    /**
     *  Test basic encryption of a symmetric key into an EncryptedKey,
     *  set key encrypting key name in passed KeyInfo object.
     * @throws NoSuchProviderException bad JCA provider
     * @throws NoSuchAlgorithmException  bad JCA algorithm
     * @throws XMLParserException error creating new Document from pool
     * @throws KeyException 
     */
    @Test
    public void testEncryptKeyMultipleKEK() throws NoSuchAlgorithmException, NoSuchProviderException, 
            XMLParserException, KeyException {
        
        Key targetKey = AlgorithmSupport.generateSymmetricKey(algoURI);
        
        kekParamsAES.setKeyInfoGenerator(new StaticKeyInfoGenerator(kekKeyInfoAES));
        kekParamsRSA.setKeyInfoGenerator(new StaticKeyInfoGenerator(kekKeyInfoRSA));
        
        kekParamsList.add(kekParamsAES);
        kekParamsList.add(kekParamsRSA);
        
        List<EncryptedKey> encKeys = null;
        Document ownerDocument = parserPool.newDocument();
        try {
            encKeys = encrypter.encryptKey(targetKey, kekParamsList, ownerDocument);
        } catch (EncryptionException e) {
            Assert.fail("Object encryption failed: " + e);
        }
        
        
        Assert.assertEquals(encKeys.size(), 2, "Number of EncryptedKeys");
        checkKEKAES(encKeys.get(0), true);
        checkKEKRSA(encKeys.get(1), true);
    }
    
    /**
     *  Test basic encryption with auto-generated symmetric key.
     *  
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     */
    @Test
    public void testAutoKeyGen() {
        SignableSimpleXMLObject sxo = (SignableSimpleXMLObject) unmarshallElement(targetFile);
        
        encParams.setEncryptionCredential(null);
        
        kekParamsList.add(kekParamsRSA);
        
        EncryptedData encData = null;
        
        // try with single KEK
        try {
            encData = encrypter.encryptElement(sxo, encParams, kekParamsRSA);
        } catch (EncryptionException e) {
            Assert.fail("Object encryption failed: " + e);
        }
        Assert.assertNotNull(encData);
        
        // try with multiple KEK
        try {
            encData = encrypter.encryptElement(sxo, encParams, kekParamsList);
        } catch (EncryptionException e) {
            Assert.fail("Object encryption failed: " + e);
        }
        Assert.assertNotNull(encData);
    }
    
    /**
     *  Test failure with auto-generated symmetric key and no KEK(s).
     *  
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     */
    @Test
    public void testAutoKeyGenNoKEK() {
        SignableSimpleXMLObject sxo = (SignableSimpleXMLObject) unmarshallElement(targetFile);
        
        encParams.setEncryptionCredential(null);
        
        kekParamsList.clear();
        
        // try with no KEK
        try {
            encrypter.encryptElement(sxo, encParams);
            Assert.fail("Object encryption should have failed: no KEK supplied with auto key generation for data encryption");
        } catch (EncryptionException e) {
            // do nothing, should fail
        }
        
        // try with empty KEK list
        try {
            encrypter.encryptElement(sxo, encParams, kekParamsList);
            Assert.fail("Object encryption should have failed: no KEK supplied with auto key generation for data encryption");
        } catch (EncryptionException e) {
            // do nothing, should fail
        }
    }
    
    /**
     * Test code for the Apache XML-Security issue workaround that requires we 
     * expliclty express SHA-1 DigestMethod on EncryptionMethod,
     * only when key transport algorithm is RSA-OAEP.
     *  
     * @throws NoSuchProviderException bad JCA provider
     * @throws NoSuchAlgorithmException  bad JCA algorithm
     * @throws XMLParserException error creating new Document from pool
     * @throws KeyException 
     */
    @Test
    public void testEncryptKeyDigestMethodsRSAOAEP() throws NoSuchAlgorithmException, NoSuchProviderException, 
            XMLParserException, KeyException {
        
        Key targetKey = AlgorithmSupport.generateSymmetricKey(algoURI);
        
        kekParamsRSA.setAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        
        EncryptedKey encKey = null;
        Document ownerDocument = parserPool.newDocument();
        try {
            encKey = encrypter.encryptKey(targetKey, kekParamsRSA, ownerDocument);
        } catch (EncryptionException e) {
            Assert.fail("Object encryption failed: " + e);
        } 
        
        Assert.assertFalse(encKey.getEncryptionMethod().getUnknownXMLObjects(DigestMethod.DEFAULT_ELEMENT_NAME).isEmpty(),
                "EncryptedKey/EncryptionMethod/DigestMethod list was empty");
        DigestMethod dm = 
                (DigestMethod) encKey.getEncryptionMethod()
                .getUnknownXMLObjects(DigestMethod.DEFAULT_ELEMENT_NAME).get(0);
        Assert.assertEquals(dm.getAlgorithm(), 
                SignatureConstants.ALGO_ID_DIGEST_SHA1, "DigestMethod algorithm URI had unexpected value");
    }
    
    /**
     * Test code for the Apache XML-Security issue workaround that requires we 
     * expliclty express SHA-1 DigestMethod on EncryptionMethod,
     * only when key transport algorithm is RSA-OAEP.
     *  
     * @throws NoSuchProviderException bad JCA provider
     * @throws NoSuchAlgorithmException  bad JCA algorithm
     * @throws XMLParserException error creating new Document from pool
     * @throws KeyException 
     */
    @Test
    public void testEncryptKeyDigestMethodsRSAv15() throws NoSuchAlgorithmException, NoSuchProviderException, 
            XMLParserException, KeyException {
        
        Key targetKey = AlgorithmSupport.generateSymmetricKey(algoURI);
        
        kekParamsRSA.setAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        
        EncryptedKey encKey = null;
        Document ownerDocument = parserPool.newDocument();
        try {
            encKey = encrypter.encryptKey(targetKey, kekParamsRSA, ownerDocument);
        } catch (EncryptionException e) {
            Assert.fail("Object encryption failed: " + e);
        } 
        
        Assert.assertTrue(encKey.getEncryptionMethod().getUnknownXMLObjects(DigestMethod.DEFAULT_ELEMENT_NAME).isEmpty(),
                "EncryptedKey/EncryptionMethod/DigestMethod list was NOT empty");
    }
    
    /**
     *  Test proper error handling of attempt to encrypt with a DSA key.
     *  
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     */
    @Test
    public void testEncryptDataBadKEKDSA() throws NoSuchAlgorithmException, NoSuchProviderException {
        SignableSimpleXMLObject sxo = (SignableSimpleXMLObject) unmarshallElement(targetFile);
        
        KeyEncryptionParameters kekParamsDSA = new KeyEncryptionParameters();
        KeyPair kp = KeySupport.generateKeyPair("DSA", 1024, null);
        kekParamsDSA.setEncryptionCredential(CredentialSupport.getSimpleCredential(kp.getPublic(), null));
        
        try {
            encrypter.encryptElement(sxo, encParams, kekParamsDSA);
            Assert.fail("Object encryption succeeded, should have failed with DSA key attempt");
        } catch (EncryptionException e) {
            // do nothing failure expected
        }
    }
    
    /**
     *  Test encryption of a symmetric key into an EncryptedKey,
     *  using various RSAOAEPParameters options.
     * @throws NoSuchProviderException bad JCA provider
     * @throws NoSuchAlgorithmException  bad JCA algorithm
     * @throws XMLParserException error creating new Document from pool
     * @throws KeyException 
     * @throws EncryptionException 
     */
    @Test
    public void testRSAOAEPParameters() throws NoSuchAlgorithmException, NoSuchProviderException, 
            XMLParserException, KeyException, EncryptionException {
        
        providerSupport.loadBC();
        
        try {
            Document ownerDocument = parserPool.newDocument();
            Key targetKey = AlgorithmSupport.generateSymmetricKey(algoURI);
            String controlOAEPParams = "9lWu3Q==";
            EncryptedKey encKey;
            
            KeyEncryptionParameters kekParams = new KeyEncryptionParameters();
            kekParams.setEncryptionCredential(AlgorithmSupport.generateKeyPairAndCredential(kekURIRSA, 1024, false));
            
            //
            // Encryption 1.0 variant, MGF is hard-coded as SHA-1 in algo spec, not expressed in the XML
            //
            kekParams.setAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
            
            // Defaults via null params
            kekParams.setRSAOAEPParameters(null);
            encKey = encrypter.encryptKey(targetKey, kekParams, ownerDocument);
            Assert.assertEquals(getDigestMethod(encKey), SignatureConstants.ALGO_ID_DIGEST_SHA1); // Our hard-coded default for interop.
            Assert.assertNull(getMGF(encKey)); // Should always be null, implicit in the algo
            Assert.assertNull(getOAEPParams(encKey));
            
            // Defaults via empty params
            kekParams.setRSAOAEPParameters(new RSAOAEPParameters());
            encKey = encrypter.encryptKey(targetKey, kekParams, ownerDocument);
            Assert.assertEquals(getDigestMethod(encKey), SignatureConstants.ALGO_ID_DIGEST_SHA1); // Our hard-coded default for interop.
            Assert.assertNull(getMGF(encKey)); // Should always be null, implicit in the algo
            Assert.assertNull(getOAEPParams(encKey));
            
            // Set everything
            kekParams.setRSAOAEPParameters(new RSAOAEPParameters(
                    EncryptionConstants.ALGO_ID_DIGEST_SHA256, 
                    EncryptionConstants.ALGO_ID_MGF1_SHA256, 
                    controlOAEPParams));
            encKey = encrypter.encryptKey(targetKey, kekParams, ownerDocument);
            Assert.assertEquals(getDigestMethod(encKey), EncryptionConstants.ALGO_ID_DIGEST_SHA256);
            Assert.assertNull(getMGF(encKey)); // Should always be null, implicit in the algo
            Assert.assertEquals(getOAEPParams(encKey), controlOAEPParams);
            
            //
            // Encryption 1.1 variant, MGF is free to vary
            //
            kekParams.setAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11);
            
            // Defaults via null params
            kekParams.setRSAOAEPParameters(null);
            encKey = encrypter.encryptKey(targetKey, kekParams, ownerDocument);
            Assert.assertEquals(getDigestMethod(encKey), SignatureConstants.ALGO_ID_DIGEST_SHA1); // Our hard-coded default for interop.
            Assert.assertEquals(getMGF(encKey), EncryptionConstants.ALGO_ID_MGF1_SHA1); // Our hard-coded default for interop.
            Assert.assertNull(getOAEPParams(encKey));
            
            // Defaults via empty params
            kekParams.setRSAOAEPParameters(new RSAOAEPParameters());
            encKey = encrypter.encryptKey(targetKey, kekParams, ownerDocument);
            Assert.assertEquals(getDigestMethod(encKey), SignatureConstants.ALGO_ID_DIGEST_SHA1); // Our hard-coded default for interop.
            Assert.assertEquals(getMGF(encKey), EncryptionConstants.ALGO_ID_MGF1_SHA1); // Our hard-coded default for interop.
            Assert.assertNull(getOAEPParams(encKey));
            
            // Set everything
            kekParams.setRSAOAEPParameters(new RSAOAEPParameters(
                    EncryptionConstants.ALGO_ID_DIGEST_SHA256, 
                    EncryptionConstants.ALGO_ID_MGF1_SHA256, 
                    controlOAEPParams));
            encKey = encrypter.encryptKey(targetKey, kekParams, ownerDocument);
            Assert.assertEquals(getDigestMethod(encKey), EncryptionConstants.ALGO_ID_DIGEST_SHA256);
            Assert.assertEquals(getMGF(encKey), EncryptionConstants.ALGO_ID_MGF1_SHA256);
            Assert.assertEquals(getOAEPParams(encKey), controlOAEPParams);
        
        } finally {
            providerSupport.unloadBC();
        }
    }
    
    /**
     * Helper method to test AES KEK.
     * 
     * @param encKey EncryptedKey to test
     * @param hasKeyInfo flag indicating expectation of KeyInfo presence
     */
    private void checkKEKAES(EncryptedKey encKey, boolean hasKeyInfo) {
        Assert.assertNotNull(encKey, "EncryptedKey was null");
        Assert.assertEquals(encKey.getEncryptionMethod().getAlgorithm(), kekURIAES, "Algorithm attribute");
        Assert.assertEquals(encKey.getRecipient(), expectedRecipientAES, "Recipient attribute");
        if (! hasKeyInfo) {
            Assert.assertNull(encKey.getKeyInfo(), "Unexpected KeyInfo was present");
            return;
        } else {
            Assert.assertNotNull(encKey.getKeyInfo(), "KeyInfo was not present");
            Assert.assertNotNull(encKey.getKeyInfo().getKeyNames().get(0), "KeyName was not present");
            Assert.assertEquals(encKey.getKeyInfo().getKeyNames().get(0).getValue(), expectedKEKKeyNameAES, 
                    "Unexpected KEK KeyName");
        }
    }
 
    /**
     * Helper method to test RSA KEK.
     * 
     * @param encKey EncryptedKey to test
     * @param hasKeyInfo flag indicating expectation of KeyInfo presence
     */
    private void checkKEKRSA(EncryptedKey encKey, boolean hasKeyInfo) {
        Assert.assertNotNull(encKey, "EncryptedKey was null");
        Assert.assertEquals(encKey.getEncryptionMethod().getAlgorithm(), kekURIRSA, "Algorithm attribute");
        Assert.assertEquals(encKey.getRecipient(), expectedRecipientRSA, "Recipient attribute");
        if (! hasKeyInfo) {
            Assert.assertNull(encKey.getKeyInfo(), "Unexpected KeyInfo was present");
            return;
        } else {
            Assert.assertNotNull(encKey.getKeyInfo(), "KeyInfo was not present");
            Assert.assertNotNull(encKey.getKeyInfo().getKeyNames().get(0), "KeyName was not present");
            Assert.assertEquals(encKey.getKeyInfo().getKeyNames().get(0).getValue(), expectedKEKKeyNameRSA, 
                    "Unexpected KEK KeyName");
        }
    }
    
    private String getDigestMethod(EncryptedKey encryptedKey) {
        List<XMLObject> digestMethods = encryptedKey.getEncryptionMethod().getUnknownXMLObjects(DigestMethod.DEFAULT_ELEMENT_NAME);
        if (digestMethods != null && digestMethods.size() > 0) { 
            return ((DigestMethod)digestMethods.get(0)).getAlgorithm();
        } else {
            return null;
        }
    }
    
    private String getMGF(EncryptedKey encryptedKey) {
        List<XMLObject> mgfs = encryptedKey.getEncryptionMethod().getUnknownXMLObjects(MGF.DEFAULT_ELEMENT_NAME);
        if (mgfs != null && mgfs.size() > 0) {
            return ((MGF)mgfs.get(0)).getAlgorithm();
        } else {
            return null;
        }
    }
    
    private String getOAEPParams(EncryptedKey encryptedKey) {
        OAEPparams params = encryptedKey.getEncryptionMethod().getOAEPparams();
        if (params != null) {
            return params.getValue();
        } else {
            return null;
        }
    }
    
}
