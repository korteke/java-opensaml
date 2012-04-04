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
import org.testng.AssertJUnit;
import java.util.ArrayList;
import java.util.List;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.encryption.support.EncryptedKeyResolver;
import org.opensaml.xmlsec.encryption.support.SimpleRetrievalMethodEncryptedKeyResolver;
import org.opensaml.xmlsec.mock.SignableSimpleXMLObject;

/**
 * Test the encrypted key resolver which dereferences RetrievalMethods.
 */
public class SimpleRetrievalMethodEncryptedKeyResolverTest extends XMLObjectBaseTestCase {
    
    /** The resolver instance to be tested. */
    private SimpleRetrievalMethodEncryptedKeyResolver resolver;
    
    
    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        resolver = new SimpleRetrievalMethodEncryptedKeyResolver();
    }
    
    /** No recipients specified to resolver, one EncryptedKey in instance. */
    @Test
    public void testSingleEKNoRecipient() {
        String filename =  "/data/org/opensaml/xmlsec/encryption/support/SimpleRetrievalMethodEncryptedKeyResolverSingle.xml";
        SignableSimpleXMLObject sxo =  (SignableSimpleXMLObject) unmarshallElement(filename);
        AssertJUnit.assertNotNull(sxo);
        AssertJUnit.assertNotNull(sxo.getSimpleXMLObjects().get(0));
        AssertJUnit.assertNotNull(sxo.getSimpleXMLObjects().get(0).getEncryptedData());
        
        EncryptedData encData = (EncryptedData) sxo.getSimpleXMLObjects().get(0).getEncryptedData();
        
        AssertJUnit.assertNotNull(encData.getKeyInfo());
        AssertJUnit.assertFalse(encData.getKeyInfo().getRetrievalMethods().isEmpty());
        
        List<EncryptedKey> allKeys = getEncryptedKeys(sxo);
        AssertJUnit.assertFalse(allKeys.isEmpty());
        
        resolver.getRecipients().clear();
        
        List<EncryptedKey> resolved = generateList(encData, resolver);
        AssertJUnit.assertEquals("Incorrect number of resolved EncryptedKeys found", 1, resolved.size());
        
        AssertJUnit.assertTrue("Unexpected EncryptedKey instance found", resolved.get(0) == allKeys.get(0));
    }
    
    /** One recipients specified to resolver, one EncryptedKey in instance. */
    @Test
    public void testSingleEKWithRecipient() {
        String filename =  "/data/org/opensaml/xmlsec/encryption/support/SimpleRetrievalMethodEncryptedKeyResolverSingle.xml";
        SignableSimpleXMLObject sxo =  (SignableSimpleXMLObject) unmarshallElement(filename);
        AssertJUnit.assertNotNull(sxo);
        AssertJUnit.assertNotNull(sxo.getSimpleXMLObjects().get(0));
        AssertJUnit.assertNotNull(sxo.getSimpleXMLObjects().get(0).getEncryptedData());
        
        EncryptedData encData = (EncryptedData) sxo.getSimpleXMLObjects().get(0).getEncryptedData();
        
        AssertJUnit.assertNotNull(encData.getKeyInfo());
        AssertJUnit.assertFalse(encData.getKeyInfo().getRetrievalMethods().isEmpty());
        
        List<EncryptedKey> allKeys = getEncryptedKeys(sxo);
        AssertJUnit.assertFalse(allKeys.isEmpty());
        
        resolver.getRecipients().add("foo");
        
        List<EncryptedKey> resolved = generateList(encData, resolver);
        AssertJUnit.assertEquals("Incorrect number of resolved EncryptedKeys found", 1, resolved.size());
        
        AssertJUnit.assertTrue("Unexpected EncryptedKey instance found", resolved.get(0) == allKeys.get(0));
    }
    
    /** One recipients specified to resolver, RetrievalMethod has Transforms, so should fail. */
    @Test
    public void testSingleEKWithTransform() {
        String filename =  
            "/data/org/opensaml/xmlsec/encryption/support/SimpleRetrievalMethodEncryptedKeyResolverSingleWithTransforms.xml";
        SignableSimpleXMLObject sxo =  (SignableSimpleXMLObject) unmarshallElement(filename);
        AssertJUnit.assertNotNull(sxo);
        AssertJUnit.assertNotNull(sxo.getSimpleXMLObjects().get(0));
        AssertJUnit.assertNotNull(sxo.getSimpleXMLObjects().get(0).getEncryptedData());
        
        EncryptedData encData = (EncryptedData) sxo.getSimpleXMLObjects().get(0).getEncryptedData();
        
        AssertJUnit.assertNotNull(encData.getKeyInfo());
        AssertJUnit.assertFalse(encData.getKeyInfo().getRetrievalMethods().isEmpty());
        
        List<EncryptedKey> allKeys = getEncryptedKeys(sxo);
        AssertJUnit.assertFalse(allKeys.isEmpty());
        
        resolver.getRecipients().add("foo");
        
        List<EncryptedKey> resolved = generateList(encData, resolver);
        AssertJUnit.assertEquals("Incorrect number of resolved EncryptedKeys found", 0, resolved.size());
    }
    
    /** One recipients specified to resolver, three EncryptedKeys in instance, 
     * two RetrievalMethod references. */
    @Test
    public void testMultiEKWithOneRecipient() {
        String filename =  "/data/org/opensaml/xmlsec/encryption/support/SimpleRetrievalMethodEncryptedKeyResolverMultiple.xml";
        SignableSimpleXMLObject sxo =  (SignableSimpleXMLObject) unmarshallElement(filename);
        AssertJUnit.assertNotNull(sxo);
        AssertJUnit.assertNotNull(sxo.getSimpleXMLObjects().get(0));
        AssertJUnit.assertNotNull(sxo.getSimpleXMLObjects().get(0).getEncryptedData());
        
        EncryptedData encData = (EncryptedData) sxo.getSimpleXMLObjects().get(0).getEncryptedData();
        
        AssertJUnit.assertNotNull(encData.getKeyInfo());
        AssertJUnit.assertFalse(encData.getKeyInfo().getRetrievalMethods().isEmpty());
        
        List<EncryptedKey> allKeys = getEncryptedKeys(sxo);
        AssertJUnit.assertFalse(allKeys.isEmpty());
        
        resolver.getRecipients().add("foo");
        
        List<EncryptedKey> resolved = generateList(encData, resolver);
        AssertJUnit.assertEquals("Incorrect number of resolved EncryptedKeys found", 1, resolved.size());
        
        AssertJUnit.assertTrue("Unexpected EncryptedKey instance found", resolved.get(0) == allKeys.get(0));
    }
    
    /** Two recipients specified to resolver, three EncryptedKeys in instance, 
     * two RetrievalMethod references. */
    @Test
    public void testMultiEKWithTwoRecipients() {
        String filename =  "/data/org/opensaml/xmlsec/encryption/support/SimpleRetrievalMethodEncryptedKeyResolverMultiple.xml";
        SignableSimpleXMLObject sxo =  (SignableSimpleXMLObject) unmarshallElement(filename);
        AssertJUnit.assertNotNull(sxo);
        AssertJUnit.assertNotNull(sxo.getSimpleXMLObjects().get(0));
        AssertJUnit.assertNotNull(sxo.getSimpleXMLObjects().get(0).getEncryptedData());
        
        EncryptedData encData = (EncryptedData) sxo.getSimpleXMLObjects().get(0).getEncryptedData();
        
        AssertJUnit.assertNotNull(encData.getKeyInfo());
        AssertJUnit.assertFalse(encData.getKeyInfo().getRetrievalMethods().isEmpty());
        
        List<EncryptedKey> allKeys = getEncryptedKeys(sxo);
        AssertJUnit.assertFalse(allKeys.isEmpty());
        
        resolver.getRecipients().add("foo");
        resolver.getRecipients().add("baz");
        
        List<EncryptedKey> resolved = generateList(encData, resolver);
        AssertJUnit.assertEquals("Incorrect number of resolved EncryptedKeys found", 2, resolved.size());
        
        AssertJUnit.assertTrue("Unexpected EncryptedKey instance found", resolved.get(0) == allKeys.get(0));
        AssertJUnit.assertTrue("Unexpected EncryptedKey instance found", resolved.get(1) == allKeys.get(2));
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
