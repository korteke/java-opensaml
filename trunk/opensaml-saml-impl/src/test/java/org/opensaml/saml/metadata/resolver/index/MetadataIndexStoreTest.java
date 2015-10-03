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

package org.opensaml.saml.metadata.resolver.index;

import java.util.Set;

import org.opensaml.core.OpenSAMLInitBaseTestCase;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 */
public class MetadataIndexStoreTest extends OpenSAMLInitBaseTestCase {
    
    private MetadataIndexStore store;
    
    private MetadataIndexKey key1, key2;
    private EntityDescriptor a, b, c;
    private Set<EntityDescriptor> result;
    
    @BeforeMethod
    protected void setUp() {
        store = new MetadataIndexStore();
        key1 = new SimpleStringMetadataIndexKey("foo");
        key2 = new SimpleStringMetadataIndexKey("bar");
        a = (EntityDescriptor) XMLObjectSupport.buildXMLObject(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        b = (EntityDescriptor) XMLObjectSupport.buildXMLObject(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        c = (EntityDescriptor) XMLObjectSupport.buildXMLObject(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        result = null;
    }
    
    @Test
    public void testAddRemoveLookup() {
        Assert.assertTrue(store.lookup(key1).isEmpty());
        Assert.assertTrue(store.lookup(key2).isEmpty());
        
        store.add(key1, a);
        result = store.lookup(key1);
        Assert.assertEquals(result.size(), 1);
        Assert.assertTrue(result.contains(a));
        
        store.add(key1, b);
        store.add(key1, c);
        result = store.lookup(key1);
        Assert.assertEquals(result.size(), 3);
        Assert.assertTrue(result.contains(a));
        Assert.assertTrue(result.contains(b));
        Assert.assertTrue(result.contains(c));
        
        // Duplicate
        store.add(key1, a);
        result = store.lookup(key1);
        Assert.assertEquals(result.size(), 3);
        Assert.assertTrue(result.contains(a));
        Assert.assertTrue(result.contains(b));
        Assert.assertTrue(result.contains(c));
        
        store.remove(key1, b);
        result = store.lookup(key1);
        Assert.assertEquals(result.size(), 2);
        Assert.assertTrue(result.contains(a));
        Assert.assertTrue(result.contains(c));
        
        //Remove non-existent
        store.remove(key1, b);
        result = store.lookup(key1);
        Assert.assertEquals(result.size(), 2);
        Assert.assertTrue(result.contains(a));
        Assert.assertTrue(result.contains(c));
        
        store.add(key2, b);
        result = store.lookup(key1);
        Assert.assertEquals(result.size(), 2);
        Assert.assertTrue(result.contains(a));
        Assert.assertTrue(result.contains(c));
        result = store.lookup(key2);
        Assert.assertEquals(result.size(), 1);
        Assert.assertTrue(result.contains(b));
        
        store.remove(key1, a);
        store.remove(key1, c);
        store.remove(key2, b);
        
        Assert.assertTrue(store.lookup(key1).isEmpty());
        Assert.assertTrue(store.lookup(key2).isEmpty());
    }
    
    @Test
    public void testGetKeys() {
        Set<MetadataIndexKey> keys = null;
        
        Assert.assertTrue(store.getKeys().isEmpty());
        
        store.add(key1, a);
        keys = store.getKeys();
        Assert.assertFalse(keys.isEmpty());
        Assert.assertEquals(keys.size(), 1);
        
        store.add(key1, b);
        store.add(key2, c);
        keys = store.getKeys();
        Assert.assertFalse(keys.isEmpty());
        Assert.assertEquals(keys.size(), 2);
        
        store.clear(key1);
        store.clear(key2);
        Assert.assertTrue(store.getKeys().isEmpty());
    }
    
    @Test
    public void testClearIndex() {
        Assert.assertTrue(store.lookup(key1).isEmpty());
        
        store.add(key1, a);
        store.add(key1, b);
        store.add(key1, c);
        
        Assert.assertFalse(store.lookup(key1).isEmpty());
        Assert.assertEquals(store.lookup(key1).size(), 3);
        
        store.clear(key1);
        
        Assert.assertTrue(store.lookup(key1).isEmpty());
    }
    
    @Test
    public void testClearAll() {
        Assert.assertTrue(store.lookup(key1).isEmpty());
        Assert.assertTrue(store.lookup(key2).isEmpty());
        
        store.add(key1, a);
        store.add(key2, b);
        
        Assert.assertFalse(store.lookup(key1).isEmpty());
        Assert.assertFalse(store.lookup(key2).isEmpty());
        
        store.clear();
        
        Assert.assertTrue(store.lookup(key1).isEmpty());
        Assert.assertTrue(store.lookup(key2).isEmpty());
    }

}
