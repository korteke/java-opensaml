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

package org.opensaml.xmlsec.keyinfo.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import java.security.KeyException;
import java.security.PublicKey;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.security.SecurityException;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.keyinfo.impl.StaticKeyInfoGenerator;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.KeyName;
import org.w3c.dom.Element;

/**
 * Test the static KeyInfo generator.
 */
public class StaticKeyInfoGeneratorTest extends XMLObjectBaseTestCase {
    
    private StaticKeyInfoGenerator generator;
    
    private KeyInfo origKeyInfo;
    
    private String expectedKeyName1;
    private String expectedKeyName2;
    private String expectedKeyAlgorithm;
    private PublicKey expectedKeyValue;
    
    /**
     * Constructor.
     *
     */
    public StaticKeyInfoGeneratorTest() {
        expectedKeyName1 = "Foo";
        expectedKeyName2 = "Bar";
        expectedKeyAlgorithm = "RSA";
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        origKeyInfo = (KeyInfo) buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME);
        
        KeyName keyname1 = (KeyName) buildXMLObject(KeyName.DEFAULT_ELEMENT_NAME);
        keyname1.setValue(expectedKeyName1);
        origKeyInfo.getKeyNames().add(keyname1);
        
        KeyName keyname2 = (KeyName) buildXMLObject(KeyName.DEFAULT_ELEMENT_NAME);
        keyname2.setValue(expectedKeyName2);
        origKeyInfo.getKeyNames().add(keyname2);
        
        expectedKeyValue = KeySupport.generateKeyPair(expectedKeyAlgorithm, 1024, null).getPublic();
        KeyInfoSupport.addPublicKey(origKeyInfo, expectedKeyValue);
        
        generator = new StaticKeyInfoGenerator(origKeyInfo);
    }
    
    /**
     * Simple test, should return the same instance every time.
     * 
     * @throws SecurityException
     * @throws KeyException
     */
    @Test
    public void testSimple() throws SecurityException, KeyException {
        Assert.assertNull(origKeyInfo.getParent(), "Original KeyInfo should NOT have parent");
        
        KeyInfo keyInfo = generator.generate(null);
        checkKeyInfo(keyInfo);
        Assert.assertTrue(origKeyInfo == keyInfo, "KeyInfo instances were not the same");
        
        keyInfo = generator.generate(null);
        checkKeyInfo(keyInfo);
        Assert.assertTrue(origKeyInfo == keyInfo, "KeyInfo instances were not the same");
        
        keyInfo = generator.generate(null);
        checkKeyInfo(keyInfo);
        Assert.assertTrue(origKeyInfo == keyInfo, "KeyInfo instances were not the same");
    }
    
    /**
     * Test with cloning, original KeyInfo has no cached DOM.
     * 
     * @throws SecurityException
     * @throws KeyException
     */
    @Test
    public void testWithCloningNoDOMCache() throws SecurityException, KeyException {
        EncryptedData encData = (EncryptedData) buildXMLObject(EncryptedData.DEFAULT_ELEMENT_NAME);
        
        Assert.assertNull(origKeyInfo.getDOM(), "Original KeyInfo should not have a cached DOM");
        
        KeyInfo keyInfo = generator.generate(null);
        checkKeyInfo(keyInfo);
        Assert.assertTrue(origKeyInfo == keyInfo, "KeyInfo instances were not the same");
        
        encData.setKeyInfo(origKeyInfo);
        Assert.assertNotNull(origKeyInfo.getParent(), "Original KeyInfo should have parent");
        
        keyInfo = generator.generate(null);
        checkKeyInfo(keyInfo);
        Assert.assertFalse(origKeyInfo == keyInfo, "KeyInfo instances should have differed due to cloning");
        Assert.assertNotNull(keyInfo.getDOM(), "Generated KeyInfo should have a cached DOM");
        
        Assert.assertNull(origKeyInfo.getDOM(), "Original KeyInfo marshalled DOM should have been cleared after cloning");
    }
    
    /**
     * Test with cloning, original KeyInfo has a cached DOM.
     * 
     * @throws SecurityException
     * @throws KeyException
     * @throws MarshallingException 
     */
    @Test
    public void testWithCloningWithDOMCache() throws SecurityException, KeyException, MarshallingException {
        EncryptedData encData = (EncryptedData) buildXMLObject(EncryptedData.DEFAULT_ELEMENT_NAME);
        
        XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(origKeyInfo).marshall(origKeyInfo);
        Assert.assertNotNull(origKeyInfo.getDOM(), "Original KeyInfo should have a cached DOM");
        Element origDOM = origKeyInfo.getDOM();
        
        KeyInfo keyInfo = generator.generate(null);
        checkKeyInfo(keyInfo);
        Assert.assertTrue(origKeyInfo == keyInfo, "KeyInfo instances were not the same");
        
        encData.setKeyInfo(origKeyInfo);
        Assert.assertNotNull(origKeyInfo.getParent(), "Original KeyInfo should have parent");
        
        keyInfo = generator.generate(null);
        checkKeyInfo(keyInfo);
        Assert.assertFalse(origKeyInfo == keyInfo, "KeyInfo instances should have differed due to cloning");
        Assert.assertNull(keyInfo.getDOM(), "Generated KeyInfo should NOT have a cached DOM");
        
        Assert.assertNotNull(origKeyInfo.getDOM(), "KeyInfo cached DOM should NOT have been cleared after cloning");
        Assert.assertTrue(origDOM.isSameNode(origKeyInfo.getDOM()), "DOM Elements were not the same");
    }
    
    /**
     * Check the correctness of the generated KeyInfo.
     * 
     * @param keyInfo the KeyInfo to check
     * @throws KeyException if there is an error extracting the Java key from the KeyInfo
     */
    private void checkKeyInfo(KeyInfo keyInfo) throws KeyException {
        Assert.assertNotNull(keyInfo, "KeyInfo was null");
        
        Assert.assertEquals(keyInfo.getKeyNames().size(), 2, "Number of KeyNames");
        Assert.assertEquals(keyInfo.getKeyNames().get(0).getValue(), expectedKeyName1, "Unexpected value for KeyName");
        Assert.assertEquals(keyInfo.getKeyNames().get(1).getValue(), expectedKeyName2, "Unexpected value for KeyName");
        
        Assert.assertEquals(keyInfo.getKeyValues().size(), 1, "Number of KeyValues");
        PublicKey pubKey = KeyInfoSupport.getKey(keyInfo.getKeyValues().get(0));
        Assert.assertEquals(pubKey, expectedKeyValue, "Unexpected public key value");
    }

}
