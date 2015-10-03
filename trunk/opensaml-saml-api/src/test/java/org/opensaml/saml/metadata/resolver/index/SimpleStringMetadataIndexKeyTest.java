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

import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 */
public class SimpleStringMetadataIndexKeyTest {
    
    @Test
    public void testConstructor() {
        SimpleStringMetadataIndexKey key = null;
        
        key = new SimpleStringMetadataIndexKey("foobar");
        Assert.assertEquals(key.getValue(), "foobar");
        
        key = new SimpleStringMetadataIndexKey("  foobar  ");
        Assert.assertEquals(key.getValue(), "foobar");
        
        key = new SimpleStringMetadataIndexKey("barney");
        Assert.assertNotEquals(key.getValue(), "foobar");
        
        try {
            key = new SimpleStringMetadataIndexKey("  ");
            Assert.fail("Constructor should have failed on empty input");
        } catch (ConstraintViolationException e) {
            // expected
        }
                
        try {
            key = new SimpleStringMetadataIndexKey(null);
            Assert.fail("Constructor should have failed on null input");
        } catch (ConstraintViolationException e) {
            // expected
        }
    }
    
    @Test
    public void testToString() {
        SimpleStringMetadataIndexKey key = new SimpleStringMetadataIndexKey("foobar");
        Assert.assertEquals(key.toString(), "SimpleStringMetadataIndexKey{foobar}");
    }
    
    @Test
    public void testHashCodeAndEquals() {
        SimpleStringMetadataIndexKey key1, key2 = null;
        
        //Testing equal 
        key1 = new SimpleStringMetadataIndexKey("foobar");
        key2 = new SimpleStringMetadataIndexKey("foobar");
        
        //Basic
        Assert.assertTrue(key1.equals(key2));
        Assert.assertTrue(key1.hashCode() == key2.hashCode());
        
        //Commutative
        Assert.assertTrue(key2.equals(key1));
        
        //Identity
        Assert.assertTrue(key1.equals(key1));
        
        //Does not equal the equivalent String - different class
        Assert.assertFalse(key1.equals("foobar"));
        
        //Testing not equal
        key2 = new SimpleStringMetadataIndexKey("barney");
        
        //Basic
        Assert.assertFalse(key1.equals(key2));
        Assert.assertFalse(key2.equals(key1));
    }

}
