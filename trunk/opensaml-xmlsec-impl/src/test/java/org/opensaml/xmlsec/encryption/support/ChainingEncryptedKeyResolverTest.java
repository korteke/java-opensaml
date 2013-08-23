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

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import java.util.ArrayList;
import java.util.List;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.encryption.support.ChainingEncryptedKeyResolver;
import org.opensaml.xmlsec.encryption.support.EncryptedKeyResolver;
import org.opensaml.xmlsec.encryption.support.InlineEncryptedKeyResolver;
import org.opensaml.xmlsec.encryption.support.SimpleRetrievalMethodEncryptedKeyResolver;
import org.opensaml.xmlsec.mock.SignableSimpleXMLObject;

/**
 * Test the encrypted key resolver which dereferences RetrievalMethods.
 */
public class ChainingEncryptedKeyResolverTest extends XMLObjectBaseTestCase {
    
    /** The resolver instance to be tested. */
    private ChainingEncryptedKeyResolver resolver;
    
    
    @BeforeMethod
    protected void setUp() throws Exception {
        resolver = new ChainingEncryptedKeyResolver();
        resolver.getResolverChain().add(new InlineEncryptedKeyResolver());
        resolver.getResolverChain().add(new SimpleRetrievalMethodEncryptedKeyResolver());
    }
    
    /** Test error case of empty resolver chain. */
    @Test
    public void testEmptyChain() {
        String filename =  "/data/org/opensaml/xmlsec/encryption/support/ChainingEncryptedKeyResolverSingleInline.xml";
        SignableSimpleXMLObject sxo =  (SignableSimpleXMLObject) unmarshallElement(filename);
        Assert.assertNotNull(sxo);
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0));
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0).getEncryptedData());
        
        EncryptedData encData = (EncryptedData) sxo.getSimpleXMLObjects().get(0).getEncryptedData();
        
        Assert.assertNotNull(encData.getKeyInfo());
        Assert.assertFalse(encData.getKeyInfo().getEncryptedKeys().isEmpty());
        Assert.assertTrue(encData.getKeyInfo().getRetrievalMethods().isEmpty());
        
        List<EncryptedKey> allKeys = getEncryptedKeys(sxo);
        Assert.assertFalse(allKeys.isEmpty());
        
        resolver.getRecipients().add("foo");
        
        //Make the resolver chain empty before resolving
        resolver.getResolverChain().clear();
        
        try {
            generateList(encData, resolver);
            Assert.fail("Resolver called with empty chain, should have thrown IllegalStateException");
        } catch (IllegalStateException e) {
            // do nothing, error expected
        }        
    }
    
    /** One recipient specified to resolver, EncryptedKey in instance inline. */
    @Test
    public void testSingleEKInline() {
        String filename =  "/data/org/opensaml/xmlsec/encryption/support/ChainingEncryptedKeyResolverSingleInline.xml";
        SignableSimpleXMLObject sxo =  (SignableSimpleXMLObject) unmarshallElement(filename);
        Assert.assertNotNull(sxo);
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0));
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0).getEncryptedData());
        
        EncryptedData encData = (EncryptedData) sxo.getSimpleXMLObjects().get(0).getEncryptedData();
        
        Assert.assertNotNull(encData.getKeyInfo());
        Assert.assertFalse(encData.getKeyInfo().getEncryptedKeys().isEmpty());
        Assert.assertTrue(encData.getKeyInfo().getRetrievalMethods().isEmpty());
        
        List<EncryptedKey> allKeys = getEncryptedKeys(sxo);
        Assert.assertFalse(allKeys.isEmpty());
        
        resolver.getRecipients().add("foo");
        
        List<EncryptedKey> resolved = generateList(encData, resolver);
        Assert.assertEquals(resolved.size(), 1, "Incorrect number of resolved EncryptedKeys found");
        
        Assert.assertTrue(resolved.get(0) == allKeys.get(0), "Unexpected EncryptedKey instance found");
    }
    
    /** One recipient specified to resolver, EncryptedKey in instance via RetrievalMethod . */
    @Test
    public void testSingleEKRetrievalMethod() {
        String filename =  "/data/org/opensaml/xmlsec/encryption/support/ChainingEncryptedKeyResolverSingleRetrievalMethod.xml";
        SignableSimpleXMLObject sxo =  (SignableSimpleXMLObject) unmarshallElement(filename);
        Assert.assertNotNull(sxo);
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0));
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0).getEncryptedData());
        
        EncryptedData encData = (EncryptedData) sxo.getSimpleXMLObjects().get(0).getEncryptedData();
        
        Assert.assertNotNull(encData.getKeyInfo());
        Assert.assertTrue(encData.getKeyInfo().getEncryptedKeys().isEmpty());
        Assert.assertFalse(encData.getKeyInfo().getRetrievalMethods().isEmpty());
        
        List<EncryptedKey> allKeys = getEncryptedKeys(sxo);
        Assert.assertFalse(allKeys.isEmpty());
        
        resolver.getRecipients().add("foo");
        
        List<EncryptedKey> resolved = generateList(encData, resolver);
        Assert.assertEquals(resolved.size(), 1, "Incorrect number of resolved EncryptedKeys found");
        
        Assert.assertTrue(resolved.get(0) == allKeys.get(0), "Unexpected EncryptedKey instance found");
    }
    
    /** One recipient specified to resolver, EncryptedKeys in instance inline and via RetrievalMethod . */
    @Test
    public void testMultiEKWithOneRecipient() {
        String filename =  "/data/org/opensaml/xmlsec/encryption/support/ChainingEncryptedKeyResolverMultiple.xml";
        SignableSimpleXMLObject sxo =  (SignableSimpleXMLObject) unmarshallElement(filename);
        Assert.assertNotNull(sxo);
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0));
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0).getEncryptedData());
        
        EncryptedData encData = (EncryptedData) sxo.getSimpleXMLObjects().get(0).getEncryptedData();
        
        Assert.assertNotNull(encData.getKeyInfo());
        Assert.assertFalse(encData.getKeyInfo().getEncryptedKeys().isEmpty());
        Assert.assertFalse(encData.getKeyInfo().getRetrievalMethods().isEmpty());
        
        List<EncryptedKey> allKeys = getEncryptedKeys(sxo);
        Assert.assertFalse(allKeys.isEmpty());
        
        resolver.getRecipients().add("foo");
        
        List<EncryptedKey> resolved = generateList(encData, resolver);
        Assert.assertEquals(resolved.size(), 2, "Incorrect number of resolved EncryptedKeys found");
        
        Assert.assertTrue(resolved.get(0) == allKeys.get(0), "Unexpected EncryptedKey instance found");
        Assert.assertTrue(resolved.get(1) == allKeys.get(3), "Unexpected EncryptedKey instance found");
    }
    
    /** Two recipients specified to resolver, EncryptedKeys in instance inline and via RetrievalMethod . */
    @Test
    public void testMultiEKWithTwoRecipients() {
        String filename =  "/data/org/opensaml/xmlsec/encryption/support/ChainingEncryptedKeyResolverMultiple.xml";
        SignableSimpleXMLObject sxo =  (SignableSimpleXMLObject) unmarshallElement(filename);
        Assert.assertNotNull(sxo);
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0));
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0).getEncryptedData());
        
        EncryptedData encData = (EncryptedData) sxo.getSimpleXMLObjects().get(0).getEncryptedData();
        
        Assert.assertNotNull(encData.getKeyInfo());
        Assert.assertFalse(encData.getKeyInfo().getEncryptedKeys().isEmpty());
        Assert.assertFalse(encData.getKeyInfo().getRetrievalMethods().isEmpty());
        
        List<EncryptedKey> allKeys = getEncryptedKeys(sxo);
        Assert.assertFalse(allKeys.isEmpty());
        
        resolver.getRecipients().add("foo");
        resolver.getRecipients().add("baz");
        
        List<EncryptedKey> resolved = generateList(encData, resolver);
        Assert.assertEquals(resolved.size(), 4, "Incorrect number of resolved EncryptedKeys found");
        
        Assert.assertTrue(resolved.get(0) == allKeys.get(0), "Unexpected EncryptedKey instance found");
        Assert.assertTrue(resolved.get(1) == allKeys.get(2), "Unexpected EncryptedKey instance found");
        Assert.assertTrue(resolved.get(2) == allKeys.get(3), "Unexpected EncryptedKey instance found");
        Assert.assertTrue(resolved.get(3) == allKeys.get(5), "Unexpected EncryptedKey instance found");
    }
    
    /**
     * Extract all the EncryptedKey's from the SignableSimpleXMLObject.
     * 
     * @param sxo the mock object to process
     * @return a list of EncryptedKey elements
     */
    private List<EncryptedKey> getEncryptedKeys(SignableSimpleXMLObject sxo) {
        List<EncryptedKey> allKeys = new ArrayList<EncryptedKey>();
        allKeys.addAll(sxo.getSimpleXMLObjects().get(0).getEncryptedData().getKeyInfo().getEncryptedKeys());
        for (XMLObject xmlObject : sxo.getUnknownXMLObjects()) {
           if (xmlObject instanceof EncryptedKey)  {
               allKeys.add((EncryptedKey) xmlObject);
           }
        }
        return allKeys;
    }

    /**
     * Resolve EncryptedKeys and put them in an ordered list.
     * 
     * @param encData the EncryptedData context
     * @param ekResolver the resolver to test
     * @return list of resolved EncryptedKeys
     */
    private List<EncryptedKey> generateList(EncryptedData encData, EncryptedKeyResolver ekResolver) {
        List<EncryptedKey> resolved = new ArrayList<EncryptedKey>();
        for (EncryptedKey encKey : ekResolver.resolve(encData)) {
            resolved.add(encKey);
        }
        return resolved;
    }


}
