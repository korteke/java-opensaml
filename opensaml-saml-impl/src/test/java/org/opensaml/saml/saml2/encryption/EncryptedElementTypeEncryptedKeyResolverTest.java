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
import org.testng.AssertJUnit;
import java.util.ArrayList;
import java.util.List;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.encryption.EncryptedElementTypeEncryptedKeyResolver;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.encryption.support.EncryptedKeyResolver;

/**
 * Test the SAML EncryptedElementType encrypted key resolver, with keys as peers.
 */
public class EncryptedElementTypeEncryptedKeyResolverTest extends XMLObjectBaseTestCase {
    
    /** The resolver instance to be tested. */
    private EncryptedElementTypeEncryptedKeyResolver resolver;
    
    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        resolver = new EncryptedElementTypeEncryptedKeyResolver();
    }

    /** No recipients specified to resolver, one EncryptedKey in instance. */
    @Test
    public void  testSingleEKNoRecipients() {
        String filename = 
            "/data/org/opensaml/saml/saml2/encryption/EncryptedElementTypeEncryptedKeyResolverSingleNoRecipient.xml";
        EncryptedAssertion encAssertion = (EncryptedAssertion) unmarshallElement(filename);
        
        AssertJUnit.assertNotNull(encAssertion.getEncryptedData());
        EncryptedData encData = encAssertion.getEncryptedData();
        
        List<EncryptedKey> allKeys = encAssertion.getEncryptedKeys();
        AssertJUnit.assertFalse(allKeys.isEmpty());
        
        resolver.getRecipients().clear();
        
        List<EncryptedKey> resolved = generateList(encData, resolver);
        AssertJUnit.assertEquals("Incorrect number of resolved EncryptedKeys found", 1, resolved.size());
        
        AssertJUnit.assertTrue("Unexpected EncryptedKey instance found", resolved.get(0) == allKeys.get(0));
    }
    
    /** Multiple recipients specified to resolver, one EncryptedKey in instance with no recipient. */
    @Test
    public void  testSingleEKMultiRecipientWithImplicitMatch() {
        String filename = 
            "/data/org/opensaml/saml/saml2/encryption/EncryptedElementTypeEncryptedKeyResolverSingleNoRecipient.xml";
        EncryptedAssertion encAssertion = (EncryptedAssertion) unmarshallElement(filename);
        
        AssertJUnit.assertNotNull(encAssertion.getEncryptedData());
        EncryptedData encData = encAssertion.getEncryptedData();
        
        List<EncryptedKey> allKeys = encAssertion.getEncryptedKeys();
        AssertJUnit.assertFalse(allKeys.isEmpty());
        
        resolver.getRecipients().add("foo");
        resolver.getRecipients().add("bar");
        resolver.getRecipients().add("baz");
        
        List<EncryptedKey> resolved = generateList(encData, resolver);
        AssertJUnit.assertEquals("Incorrect number of resolved EncryptedKeys found", 1, resolved.size());
        
        AssertJUnit.assertTrue("Unexpected EncryptedKey instance found", resolved.get(0) == allKeys.get(0));
    }
    
    /** One recipient specified to resolver, one matching EncryptedKey in instance. */
    @Test
    public void  testSingleEKOneRecipientWithMatch() {
        String filename = 
            "/data/org/opensaml/saml/saml2/encryption/EncryptedElementTypeEncryptedKeyResolverSingleWithRecipient.xml";
        EncryptedAssertion encAssertion = (EncryptedAssertion) unmarshallElement(filename);
        
        AssertJUnit.assertNotNull(encAssertion.getEncryptedData());
        EncryptedData encData = encAssertion.getEncryptedData();
        
        List<EncryptedKey> allKeys = encAssertion.getEncryptedKeys();
        AssertJUnit.assertFalse(allKeys.isEmpty());
        
        resolver.getRecipients().add("foo");
        
        List<EncryptedKey> resolved = generateList(encData, resolver);
        AssertJUnit.assertEquals("Incorrect number of resolved EncryptedKeys found", 1, resolved.size());
        
        AssertJUnit.assertTrue("Unexpected EncryptedKey instance found", resolved.get(0) == allKeys.get(0));
    }
    
    /** One recipient specified to resolver, zero matching EncryptedKey in instance. */
    @Test
    public void  testSingleEKOneRecipientNoMatch() {
        String filename = 
            "/data/org/opensaml/saml/saml2/encryption/EncryptedElementTypeEncryptedKeyResolverSingleWithRecipient.xml";
        EncryptedAssertion encAssertion = (EncryptedAssertion) unmarshallElement(filename);
        
        AssertJUnit.assertNotNull(encAssertion.getEncryptedData());
        EncryptedData encData = encAssertion.getEncryptedData();
        
        List<EncryptedKey> allKeys = encAssertion.getEncryptedKeys();
        AssertJUnit.assertFalse(allKeys.isEmpty());
        
        resolver.getRecipients().add("bar");
        
        List<EncryptedKey> resolved = generateList(encData, resolver);
        AssertJUnit.assertEquals("Incorrect number of resolved EncryptedKeys found", 0, resolved.size());
    }
    
    /** No recipients specified to resolver. */
    @Test
    public void  testMultiEKNoRecipients() {
        String filename = "/data/org/opensaml/saml/saml2/encryption/EncryptedElementTypeEncryptedKeyResolverMultiple.xml";
        EncryptedAssertion encAssertion = (EncryptedAssertion) unmarshallElement(filename);
        
        AssertJUnit.assertNotNull(encAssertion.getEncryptedData());
        EncryptedData encData = encAssertion.getEncryptedData();
        
        List<EncryptedKey> allKeys = encAssertion.getEncryptedKeys();
        AssertJUnit.assertFalse(allKeys.isEmpty());
        
        resolver.getRecipients().clear();
        
        List<EncryptedKey> resolved = generateList(encData, resolver);
        AssertJUnit.assertEquals("Incorrect number of resolved EncryptedKeys found", 4, resolved.size());
        
        AssertJUnit.assertTrue("Unexpected EncryptedKey instance found", resolved.get(0) == allKeys.get(0));
        AssertJUnit.assertTrue("Unexpected EncryptedKey instance found", resolved.get(1) == allKeys.get(1));
        AssertJUnit.assertTrue("Unexpected EncryptedKey instance found", resolved.get(2) == allKeys.get(2));
        AssertJUnit.assertTrue("Unexpected EncryptedKey instance found", resolved.get(3) == allKeys.get(3));
    }
    
    /** One recipient specified to resolver, one matching & and one recipient-less 
     *  EncryptedKey in instance. */
    @Test
    public void  testMultiEKOneRecipientWithMatch() {
        String filename = "/data/org/opensaml/saml/saml2/encryption/EncryptedElementTypeEncryptedKeyResolverMultiple.xml";
        EncryptedAssertion encAssertion = (EncryptedAssertion) unmarshallElement(filename);
        
        AssertJUnit.assertNotNull(encAssertion.getEncryptedData());
        EncryptedData encData = encAssertion.getEncryptedData();
        
        List<EncryptedKey> allKeys = encAssertion.getEncryptedKeys();
        AssertJUnit.assertFalse(allKeys.isEmpty());
        
        resolver.getRecipients().clear();
        
        resolver.getRecipients().add("foo");
        
        List<EncryptedKey> resolved = generateList(encData, resolver);
        AssertJUnit.assertEquals("Incorrect number of resolved EncryptedKeys found", 2, resolved.size());
        
        AssertJUnit.assertTrue("Unexpected EncryptedKey instance found", resolved.get(0) == allKeys.get(0));
        AssertJUnit.assertTrue("Unexpected EncryptedKey instance found", resolved.get(1) == allKeys.get(2));
    }
    
    /** Multi recipient specified to resolver, several matching EncryptedKey in instance. */
    @Test
    public void  testMultiEKOneRecipientWithMatches() {
        String filename = "/data/org/opensaml/saml/saml2/encryption/EncryptedElementTypeEncryptedKeyResolverMultiple.xml";
        EncryptedAssertion encAssertion = (EncryptedAssertion) unmarshallElement(filename);
        
        AssertJUnit.assertNotNull(encAssertion.getEncryptedData());
        EncryptedData encData = encAssertion.getEncryptedData();
        
        List<EncryptedKey> allKeys = encAssertion.getEncryptedKeys();
        AssertJUnit.assertFalse(allKeys.isEmpty());
        
        resolver.getRecipients().add("foo");
        resolver.getRecipients().add("baz");
        
        List<EncryptedKey> resolved = generateList(encData, resolver);
        AssertJUnit.assertEquals("Incorrect number of resolved EncryptedKeys found", 3, resolved.size());
        
        AssertJUnit.assertTrue("Unexpected EncryptedKey instance found", resolved.get(0) == allKeys.get(0));
        AssertJUnit.assertTrue("Unexpected EncryptedKey instance found", resolved.get(1) == allKeys.get(2));
        AssertJUnit.assertTrue("Unexpected EncryptedKey instance found", resolved.get(2) == allKeys.get(3));
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
