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

package org.opensaml.saml.metadata.resolver.index.impl;

import java.util.Set;


import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.metadata.resolver.index.MetadataIndexKey;
import org.opensaml.saml.metadata.resolver.index.MetadataIndexStore;
import org.opensaml.saml.metadata.resolver.index.SimpleStringMetadataIndexKey;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/**
 *
 */
public class FunctionDrivenMetadataIndexTest extends OpenSAMLInitBaseTestCase {
    
    private FunctionDrivenMetadataIndex metadataIndex;
    
    private MetadataIndexStore store;
    private Set<EntityDescriptor> result;
    
    private EntityDescriptor a, b, c;
    private MetadataIndexKey keyA, keyB, keyC;
    
    @BeforeMethod
    protected void setUp() {
        metadataIndex = new FunctionDrivenMetadataIndex(new UppercaseEntityIdDescriptorFunction(), new SimpleStringCriteriaFunction());
        store = new MetadataIndexStore();
        
        a = (EntityDescriptor) XMLObjectSupport.buildXMLObject(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        a.setEntityID("urn:test:a");
        keyA = new SimpleStringMetadataIndexKey("URN:TEST:A");
        
        b = (EntityDescriptor) XMLObjectSupport.buildXMLObject(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        b.setEntityID("urn:test:b");
        keyB = new SimpleStringMetadataIndexKey("URN:TEST:B");
        
        c = (EntityDescriptor) XMLObjectSupport.buildXMLObject(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        c.setEntityID("urn:test:c");
        keyC = new SimpleStringMetadataIndexKey("URN:TEST:C");
    }
    
    @Test
    public void testIndex() {
        Assert.assertTrue(store.getKeys().isEmpty());
        
        metadataIndex.index(a, store);
        
        Assert.assertEquals(store.getKeys().size(), 1);
        Assert.assertTrue(store.getKeys().contains(keyA));
        
        result = store.lookup(keyA);
        Assert.assertEquals(result.size(), 1);
        Assert.assertTrue(result.contains(a));
        
        metadataIndex.index(b, store);
        metadataIndex.index(c, store);
        
        Assert.assertEquals(store.getKeys().size(), 3);
        Assert.assertTrue(store.getKeys().contains(keyA));
        Assert.assertTrue(store.getKeys().contains(keyB));
        Assert.assertTrue(store.getKeys().contains(keyC));
        
        result = store.lookup(keyA);
        Assert.assertEquals(result.size(), 1);
        Assert.assertTrue(result.contains(a));
        
        result = store.lookup(keyB);
        Assert.assertEquals(result.size(), 1);
        Assert.assertTrue(result.contains(b));
        
        result = store.lookup(keyC);
        Assert.assertEquals(result.size(), 1);
        Assert.assertTrue(result.contains(c));
    }
    
    @Test
    public void testGenerateKeysFromCriteria() {
        CriteriaSet criteriaSet = new CriteriaSet();
        
        criteriaSet.add(new SimpleStringCriterion("URN:TEST:A"));
        
        Set<MetadataIndexKey> keys = metadataIndex.generateKeys(criteriaSet);
        
        Assert.assertEquals(keys.size(), 1);
        Assert.assertTrue(keys.contains(keyA));
    }

}
