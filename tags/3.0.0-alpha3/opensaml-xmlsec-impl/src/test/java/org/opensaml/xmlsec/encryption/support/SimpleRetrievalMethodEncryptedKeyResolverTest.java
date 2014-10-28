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
import org.testng.Assert;
import java.util.ArrayList;
import java.util.List;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.encryption.support.EncryptedKeyResolver;
import org.opensaml.xmlsec.encryption.support.SimpleRetrievalMethodEncryptedKeyResolver;
import org.opensaml.xmlsec.mock.SignableSimpleXMLObject;

import com.google.common.collect.Sets;

/**
 * Test the encrypted key resolver which dereferences RetrievalMethods.
 */
public class SimpleRetrievalMethodEncryptedKeyResolverTest extends XMLObjectBaseTestCase {
    
    /** The resolver instance to be tested. */
    private SimpleRetrievalMethodEncryptedKeyResolver resolver;
    
    /** No recipients specified to resolver, one EncryptedKey in instance. */
    @Test
    public void testSingleEKNoRecipient() {
        String filename =  "/data/org/opensaml/xmlsec/encryption/support/SimpleRetrievalMethodEncryptedKeyResolverSingle.xml";
        SignableSimpleXMLObject sxo =  (SignableSimpleXMLObject) unmarshallElement(filename);
        Assert.assertNotNull(sxo);
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0));
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0).getEncryptedData());
        
        EncryptedData encData = (EncryptedData) sxo.getSimpleXMLObjects().get(0).getEncryptedData();
        
        Assert.assertNotNull(encData.getKeyInfo());
        Assert.assertFalse(encData.getKeyInfo().getRetrievalMethods().isEmpty());
        
        List<EncryptedKey> allKeys = getEncryptedKeys(sxo);
        Assert.assertFalse(allKeys.isEmpty());
        
        resolver = new SimpleRetrievalMethodEncryptedKeyResolver();
        
        List<EncryptedKey> resolved = generateList(encData, resolver);
        Assert.assertEquals(resolved.size(), 1, "Incorrect number of resolved EncryptedKeys found");
        
        Assert.assertTrue(resolved.get(0) == allKeys.get(0), "Unexpected EncryptedKey instance found");
    }
    
    /** One recipients specified to resolver, one EncryptedKey in instance. */
    @Test
    public void testSingleEKWithRecipient() {
        String filename =  "/data/org/opensaml/xmlsec/encryption/support/SimpleRetrievalMethodEncryptedKeyResolverSingle.xml";
        SignableSimpleXMLObject sxo =  (SignableSimpleXMLObject) unmarshallElement(filename);
        Assert.assertNotNull(sxo);
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0));
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0).getEncryptedData());
        
        EncryptedData encData = (EncryptedData) sxo.getSimpleXMLObjects().get(0).getEncryptedData();
        
        Assert.assertNotNull(encData.getKeyInfo());
        Assert.assertFalse(encData.getKeyInfo().getRetrievalMethods().isEmpty());
        
        List<EncryptedKey> allKeys = getEncryptedKeys(sxo);
        Assert.assertFalse(allKeys.isEmpty());
        
        resolver = new SimpleRetrievalMethodEncryptedKeyResolver(Sets.newHashSet("foo"));
        
        List<EncryptedKey> resolved = generateList(encData, resolver);
        Assert.assertEquals(resolved.size(), 1, "Incorrect number of resolved EncryptedKeys found");
        
        Assert.assertTrue(resolved.get(0) == allKeys.get(0), "Unexpected EncryptedKey instance found");
    }
    
    /** One recipients specified to resolver, RetrievalMethod has Transforms, so should fail. */
    @Test
    public void testSingleEKWithTransform() {
        String filename =  
            "/data/org/opensaml/xmlsec/encryption/support/SimpleRetrievalMethodEncryptedKeyResolverSingleWithTransforms.xml";
        SignableSimpleXMLObject sxo =  (SignableSimpleXMLObject) unmarshallElement(filename);
        Assert.assertNotNull(sxo);
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0));
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0).getEncryptedData());
        
        EncryptedData encData = (EncryptedData) sxo.getSimpleXMLObjects().get(0).getEncryptedData();
        
        Assert.assertNotNull(encData.getKeyInfo());
        Assert.assertFalse(encData.getKeyInfo().getRetrievalMethods().isEmpty());
        
        List<EncryptedKey> allKeys = getEncryptedKeys(sxo);
        Assert.assertFalse(allKeys.isEmpty());
        
        resolver = new SimpleRetrievalMethodEncryptedKeyResolver(Sets.newHashSet("foo"));
        
        List<EncryptedKey> resolved = generateList(encData, resolver);
        Assert.assertEquals(resolved.size(), 0, "Incorrect number of resolved EncryptedKeys found");
    }
    
    /** One recipients specified to resolver, three EncryptedKeys in instance, 
     * two RetrievalMethod references. */
    @Test
    public void testMultiEKWithOneRecipient() {
        String filename =  "/data/org/opensaml/xmlsec/encryption/support/SimpleRetrievalMethodEncryptedKeyResolverMultiple.xml";
        SignableSimpleXMLObject sxo =  (SignableSimpleXMLObject) unmarshallElement(filename);
        Assert.assertNotNull(sxo);
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0));
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0).getEncryptedData());
        
        EncryptedData encData = (EncryptedData) sxo.getSimpleXMLObjects().get(0).getEncryptedData();
        
        Assert.assertNotNull(encData.getKeyInfo());
        Assert.assertFalse(encData.getKeyInfo().getRetrievalMethods().isEmpty());
        
        List<EncryptedKey> allKeys = getEncryptedKeys(sxo);
        Assert.assertFalse(allKeys.isEmpty());
        
        resolver = new SimpleRetrievalMethodEncryptedKeyResolver(Sets.newHashSet("foo"));
        
        List<EncryptedKey> resolved = generateList(encData, resolver);
        Assert.assertEquals(resolved.size(), 1, "Incorrect number of resolved EncryptedKeys found");
        
        Assert.assertTrue(resolved.get(0) == allKeys.get(0), "Unexpected EncryptedKey instance found");
    }
    
    /** Two recipients specified to resolver, three EncryptedKeys in instance, 
     * two RetrievalMethod references. */
    @Test
    public void testMultiEKWithTwoRecipients() {
        String filename =  "/data/org/opensaml/xmlsec/encryption/support/SimpleRetrievalMethodEncryptedKeyResolverMultiple.xml";
        SignableSimpleXMLObject sxo =  (SignableSimpleXMLObject) unmarshallElement(filename);
        Assert.assertNotNull(sxo);
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0));
        Assert.assertNotNull(sxo.getSimpleXMLObjects().get(0).getEncryptedData());
        
        EncryptedData encData = (EncryptedData) sxo.getSimpleXMLObjects().get(0).getEncryptedData();
        
        Assert.assertNotNull(encData.getKeyInfo());
        Assert.assertFalse(encData.getKeyInfo().getRetrievalMethods().isEmpty());
        
        List<EncryptedKey> allKeys = getEncryptedKeys(sxo);
        Assert.assertFalse(allKeys.isEmpty());
        
        resolver = new SimpleRetrievalMethodEncryptedKeyResolver(Sets.newHashSet("foo", "baz"));
        
        List<EncryptedKey> resolved = generateList(encData, resolver);
        Assert.assertEquals(resolved.size(), 2, "Incorrect number of resolved EncryptedKeys found");
        
        Assert.assertTrue(resolved.get(0) == allKeys.get(0), "Unexpected EncryptedKey instance found");
        Assert.assertTrue(resolved.get(1) == allKeys.get(2), "Unexpected EncryptedKey instance found");
    }
    
    /**
     * Extract all the EncryptedKey's from the SimpleXMLObject.
     * 
     * @param sxo the mock object to process
     * @return a list of EncryptedKey elements
     */
    private List<EncryptedKey> getEncryptedKeys(SignableSimpleXMLObject sxo) {
        List<EncryptedKey> allKeys = new ArrayList<EncryptedKey>();
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
