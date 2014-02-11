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

package org.opensaml.saml.metadata;



import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EntitiesDescriptorGroupNameTest {
    
    @Test
    public void testConstructor() {
        EntitiesDescriptorGroupName groupName = new EntitiesDescriptorGroupName("foo");
        Assert.assertEquals(groupName.getName(), "foo");
        
        groupName = new EntitiesDescriptorGroupName("    foo    ");
        Assert.assertEquals(groupName.getName(), "foo");
    }
    
    @Test(expectedExceptions=ConstraintViolationException.class)
    public void testConstructorOnNull() {
        new EntitiesDescriptorGroupName(null);
    }
    
    @Test(expectedExceptions=ConstraintViolationException.class)
    public void testConstructorOnEmpty() {
        new EntitiesDescriptorGroupName("            ");
    }
    
    @Test
    public void testHashCodeAndEquals() {
        EntitiesDescriptorGroupName foo1 = new EntitiesDescriptorGroupName("foo");
        EntitiesDescriptorGroupName foo2 = new EntitiesDescriptorGroupName("foo");
        EntitiesDescriptorGroupName bar = new EntitiesDescriptorGroupName("bar");
        
        Assert.assertTrue(foo1.equals(foo1));
        Assert.assertTrue(foo1.equals(foo2));
        Assert.assertFalse(foo1.equals(bar));
        
        Assert.assertTrue(foo1.hashCode() == foo2.hashCode());
    }

}
