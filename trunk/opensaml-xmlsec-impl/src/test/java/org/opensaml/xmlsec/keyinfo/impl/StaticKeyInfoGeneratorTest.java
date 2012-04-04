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
import org.testng.AssertJUnit;
import java.security.KeyException;
import java.security.PublicKey;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.security.SecurityException;
import org.opensaml.security.SecurityHelper;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.keyinfo.KeyInfoHelper;
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
        
        expectedKeyValue = SecurityHelper.generateKeyPair(expectedKeyAlgorithm, 1024, null).getPublic();
        KeyInfoHelper.addPublicKey(origKeyInfo, expectedKeyValue);
        
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
        AssertJUnit.assertNull("Original KeyInfo should NOT have parent", origKeyInfo.getParent());
        
        KeyInfo keyInfo = generator.generate(null);
        checkKeyInfo(keyInfo);
        AssertJUnit.assertTrue("KeyInfo instances were not the same", origKeyInfo == keyInfo);
        
        keyInfo = generator.generate(null);
        checkKeyInfo(keyInfo);
        AssertJUnit.assertTrue("KeyInfo instances were not the same", origKeyInfo == keyInfo);
        
        keyInfo = generator.generate(null);
        checkKeyInfo(keyInfo);
        AssertJUnit.assertTrue("KeyInfo instances were not the same", origKeyInfo == keyInfo);
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
        
        AssertJUnit.assertNull("Original KeyInfo should not have a cached DOM", origKeyInfo.getDOM());
        
        KeyInfo keyInfo = generator.generate(null);
        checkKeyInfo(keyInfo);
        AssertJUnit.assertTrue("KeyInfo instances were not the same", origKeyInfo == keyInfo);
        
        encData.setKeyInfo(origKeyInfo);
        AssertJUnit.assertNotNull("Original KeyInfo should have parent", origKeyInfo.getParent());
        
        keyInfo = generator.generate(null);
        checkKeyInfo(keyInfo);
        AssertJUnit.assertFalse("KeyInfo instances should have differed due to cloning", origKeyInfo == keyInfo);
        AssertJUnit.assertNotNull("Generated KeyInfo should have a cached DOM", keyInfo.getDOM());
        
        AssertJUnit.assertNull("Original KeyInfo marshalled DOM should have been cleared after cloning", origKeyInfo.getDOM());
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
        AssertJUnit.assertNotNull("Original KeyInfo should have a cached DOM", origKeyInfo.getDOM());
        Element origDOM = origKeyInfo.getDOM();
        
        KeyInfo keyInfo = generator.generate(null);
        checkKeyInfo(keyInfo);
        AssertJUnit.assertTrue("KeyInfo instances were not the same", origKeyInfo == keyInfo);
        
        encData.setKeyInfo(origKeyInfo);
        AssertJUnit.assertNotNull("Original KeyInfo should have parent", origKeyInfo.getParent());
        
        keyInfo = generator.generate(null);
        checkKeyInfo(keyInfo);
        AssertJUnit.assertFalse("KeyInfo instances should have differed due to cloning", origKeyInfo == keyInfo);
        AssertJUnit.assertNull("Generated KeyInfo should NOT have a cached DOM", keyInfo.getDOM());
        
        AssertJUnit.assertNotNull("KeyInfo cached DOM should NOT have been cleared after cloning", origKeyInfo.getDOM());
        AssertJUnit.assertTrue("DOM Elements were not the same", origDOM.isSameNode(origKeyInfo.getDOM()));
    }
    
    /**
     * Check the correctness of the generated KeyInfo.
     * 
     * @param keyInfo the KeyInfo to check
     * @throws KeyException if there is an error extracting the Java key from the KeyInfo
     */
    private void checkKeyInfo(KeyInfo keyInfo) throws KeyException {
        AssertJUnit.assertNotNull("KeyInfo was null", keyInfo);
        
        AssertJUnit.assertEquals("Number of KeyNames", 2, keyInfo.getKeyNames().size());
        AssertJUnit.assertEquals("Unexpected value for KeyName", expectedKeyName1, keyInfo.getKeyNames().get(0).getValue());
        AssertJUnit.assertEquals("Unexpected value for KeyName", expectedKeyName2, keyInfo.getKeyNames().get(1).getValue());
        
        AssertJUnit.assertEquals("Number of KeyValues", 1, keyInfo.getKeyValues().size());
        PublicKey pubKey = KeyInfoHelper.getKey(keyInfo.getKeyValues().get(0));
        AssertJUnit.assertEquals("Unexpected public key value", expectedKeyValue, pubKey);
    }

}
