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
import java.util.List;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.keyinfo.impl.BasicKeyInfoGeneratorFactory;
import org.opensaml.xmlsec.signature.DEREncodedKeyValue;
import org.opensaml.xmlsec.signature.KeyInfo;

/**
 * Tests the factory and impl for BasicKeyInfoGenerator.
 */
public class BasicKeyInfoGeneratorTest extends XMLObjectBaseTestCase {
    
    private BasicCredential credential;
    
    private BasicKeyInfoGeneratorFactory factory;
    private KeyInfoGenerator generator;
    
    private String keyNameFoo = "FOO";
    private String keyNameBar = "BAR";
    private String entityID = "someEntityID";
    
    private PublicKey pubKey;
    private final String rsaBase64 = 
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzVp5BZoctb2GuoDf8QUS" +
        "pGcRct7FKtldC7GG+kN6XvUJW+vgc2jOQ6zfLiKqq6ARN1qdC7a4CrkE6Q6TRQXU" +
        "tqeWn4lLTmC1gQ7Ys0zs7N2d+jBjIyD1GEOLNNyD98j4drnehCqQz4mKszW5EWoi" +
        "MJmEorea/kTGL3en7ir0zp+oez2SOQA+0XWu1VoeTlUqGV5Ucd6sRYaPpmYVtKuH" +
        "1H04uZVsH+BIZHwZc4MP5OYH+HDouq6xqUUtc8Zm7V9UQIPiNtM+ndOINDdlrCub" +
        "LbM4GCqCETiQol8I62mvP0qBXCC6JVkKbbVRwSFGJcg5ZvJiBZXmX+EXhaX5vp1G" +
        "MQIDAQAB";

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        factory = new BasicKeyInfoGeneratorFactory();
        generator = null;
        
        pubKey = KeySupport.buildJavaRSAPublicKey(rsaBase64);
        
        credential = new BasicCredential(pubKey);
        credential.setEntityId(entityID);
        credential.getKeyNames().add(keyNameFoo);
        credential.getKeyNames().add(keyNameBar);
    }
    
    /**
     * Test no options - should produce null KeyInfo.
     * @throws SecurityException
     */
    @Test
    public void testNoOptions() throws SecurityException {
        // all options false by default
        generator = factory.newInstance();
        KeyInfo keyInfo = generator.generate(credential);
        
        Assert.assertNull(keyInfo, "Generated KeyInfo with no options should have been null");
    }
    
    /**
     * Test emit public key.
     * @throws SecurityException
     */
    @Test
    public void testEmitPublicKey() throws SecurityException, KeyException {
        factory.setEmitPublicKeyValue(true);
        factory.setEmitPublicDEREncodedKeyValue(true);
        
        generator = factory.newInstance();
        KeyInfo keyInfo = generator.generate(credential);
        
        Assert.assertNotNull(keyInfo, "Generated KeyInfo was null");
        Assert.assertNotNull(keyInfo.getOrderedChildren(), "Generated KeyInfo children list was null");
        
        Assert.assertEquals(keyInfo.getOrderedChildren().size(), 2, "Unexpected number of KeyInfo children");
        Assert.assertEquals(keyInfo.getKeyValues().size(), 1, "Unexpected number of KeyValue elements");
        Assert.assertEquals(keyInfo.getDEREncodedKeyValues().size(), 1,
                "Unexpected number of DEREncodedKeyValue elements");
        PublicKey generatedKey = KeyInfoSupport.getKey(keyInfo.getKeyValues().get(0));
        Assert.assertEquals(generatedKey, pubKey, "Unexpected key value");
        PublicKey generatedKey2 = KeyInfoSupport.getKey(keyInfo.getDEREncodedKeyValues().get(0));
        Assert.assertEquals(pubKey, generatedKey2, "Unexpected key value");
    }
    
    /**
     * Test emit credential key names.
     * @throws SecurityException
     */
    @Test
    public void testEmitKeynames() throws SecurityException {
        factory.setEmitKeyNames(true);
        
        generator = factory.newInstance();
        KeyInfo keyInfo = generator.generate(credential);
        
        Assert.assertNotNull(keyInfo, "Generated KeyInfo was null");
        Assert.assertNotNull(keyInfo.getOrderedChildren(), "Generated KeyInfo children list was null");
        
        Assert.assertEquals(keyInfo.getKeyNames().size(), 2, "Unexpected number of KeyName elements");
        List<String> keyNames = KeyInfoSupport.getKeyNames(keyInfo);
        Assert.assertTrue(keyNames.contains(keyNameFoo), "Failed to find expected KeyName value");
        Assert.assertTrue(keyNames.contains(keyNameBar), "Failed to find expected KeyName value");
    }
    
    /**
     * Test emit entity ID as key name.
     * @throws SecurityException
     */
    @Test
    public void testEmitEntityIDAsKeyName() throws SecurityException {
        factory.setEmitEntityIDAsKeyName(true);
        
        generator = factory.newInstance();
        KeyInfo keyInfo = generator.generate(credential);
        
        Assert.assertNotNull(keyInfo, "Generated KeyInfo was null");
        Assert.assertNotNull(keyInfo.getOrderedChildren(), "Generated KeyInfo children list was null");
        
        Assert.assertEquals(keyInfo.getKeyNames().size(), 1, "Unexpected number of KeyName elements");
        List<String> keyNames = KeyInfoSupport.getKeyNames(keyInfo);
        Assert.assertTrue(keyNames.contains(entityID), "Failed to find expected KeyName value");
    }
    
    /** 
     * Test that the options passed to the generator are really cloned. 
     * After newInstance() is called, changes to the factory options should not be 
     * reflected in the generator.
     * @throws SecurityException */
    @Test
    public void testProperOptionsCloning() throws SecurityException {
        generator = factory.newInstance();
        KeyInfo keyInfo = generator.generate(credential);
        
        Assert.assertNull(keyInfo, "Generated KeyInfo was null");
        
        factory.setEmitKeyNames(true);
        factory.setEmitEntityIDAsKeyName(true);
        factory.setEmitPublicKeyValue(true);
        factory.setEmitPublicDEREncodedKeyValue(true);
        
        keyInfo = generator.generate(credential);
        
        Assert.assertNull(keyInfo, "Generated KeyInfo was null");
        
        generator = factory.newInstance();
        keyInfo = generator.generate(credential);
        
        Assert.assertNotNull(keyInfo, "Generated KeyInfo was null");
        Assert.assertNotNull(keyInfo.getOrderedChildren(), "Generated KeyInfo children list was null");
        Assert.assertEquals(keyInfo.getOrderedChildren().size(), 5, "Unexpected # of KeyInfo children found");
    }

}