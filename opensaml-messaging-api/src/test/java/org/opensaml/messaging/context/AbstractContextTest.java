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

package org.opensaml.messaging.context;

import java.util.Iterator;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the AbstractContext implementation.
 */
@Test
public class AbstractContextTest {
    
    /**
     * Test the no-arg constructor.
     */
    public void testNoArgConstructor() {
        TestContext context = new TestContext();
        Assert.assertNull(context.getParent());
        Assert.assertNotNull(context.getId());
        Assert.assertNotNull(context.getCreationTime());
    }
    
    /**
     *  Test the constructor which takes an Id value.
     */
    public void testIdConstructor() {
        TestContext context = new TestContext("abc123");
        Assert.assertNull(context.getParent());
        
        Assert.assertEquals(context.getId(), "abc123", "Unexpected context id");
    }
    
    /**
     *  Test the constructor which takes a new parent context value.
     */
    public void testParentConstructor() {
        TestContext parent = new TestContext();
        TestContext child = new TestContext(parent);
        
        Assert.assertNull(parent.getParent());
        Assert.assertNotNull(child.getParent());
        Assert.assertTrue(child.getParent() == parent, "Parent of child is not the expected value");
    }
    
    /**
     *  Test the constructor which takes both a new Id and new parent context.
     */
    public void testIdParentConstructor() {
        TestContext parent = new TestContext();
        TestContext child = new TestContext("abc123", parent);
        
        Assert.assertEquals(child.getId(), "abc123", "Unexpected context id");
        
        Assert.assertNull(parent.getParent());
        Assert.assertNotNull(child.getParent());
        Assert.assertTrue(child.getParent() == parent, "Parent of child is not the expected value");
        
    }
    
    /**
     *  Test basic adding and removing of subcontexts.
     */
    public void testAddRemoveSubcontexts() {
        TestContext parent = new TestContext();
        TestContext child = new TestContext();
        
        Assert.assertNull(parent.getParent());
        Assert.assertNull(child.getParent());
        
        parent.addSubcontext(child);
        
        Assert.assertNull(parent.getParent());
        Assert.assertNotNull(child.getParent());
        Assert.assertTrue(child.getParent() == parent, "Parent of child is not the expected value");
        Assert.assertTrue(parent.getSubcontext(TestContext.class, false) == child, "Child of parent is not the expected value");
        
        // Here test removal by class.
        parent.removeSubcontext(TestContext.class);
        
        Assert.assertNull(parent.getParent());
        Assert.assertNull(child.getParent());
        
        parent.addSubcontext(child);
        
        Assert.assertNull(parent.getParent());
        Assert.assertNotNull(child.getParent());
        Assert.assertTrue(child.getParent() == parent, "Parent of child is not the expected value");
        Assert.assertTrue(parent.getSubcontext(TestContext.class, false) == child, "Child of parent is not the expected value");
        
        // Here test removal by instance.
        parent.removeSubcontext(child);
        
        Assert.assertNull(parent.getParent());
        Assert.assertNull(child.getParent());
    }
    
    /**
     *  Test clearing all subcontexts from the parent.
     */
    public void testClearSubcontexts() {
        TestContext parent = new TestContext();
        TestContext child = new TestContext();
        
        Assert.assertNull(parent.getParent());
        Assert.assertNull(child.getParent());
        
        parent.addSubcontext(child);
        
        Assert.assertNull(parent.getParent());
        Assert.assertNotNull(child.getParent());
        Assert.assertTrue(child.getParent() == parent, "Parent of child is not the expected value");
        Assert.assertTrue(parent.getSubcontext(TestContext.class, false) == child, "Child of parent is not the expected value");
        
        parent.clearSubcontexts();
        
        Assert.assertNull(parent.getParent());
        Assert.assertNull(child.getParent());
    }
    
    /**
     *  Test basic iteration of subcontexts.
     */
    public void testBasicIteration() {
        TestContext parent = new TestContext();
        TestContext child = new TestContext();
        
        Assert.assertNull(child.getParent());
        
        parent.addSubcontext(child);
        
        Assert.assertTrue(child.getParent() == parent, "Parent of child is not the expected value");
        Assert.assertTrue(parent.getSubcontext(TestContext.class, false) == child, "Child of parent is not the expected value");
        
        Iterator<Context> iterator = parent.iterator();
        
        Assert.assertTrue(iterator.hasNext());
        Context returnedContext = iterator.next();
        Assert.assertTrue(returnedContext == child);
        
        Assert.assertFalse(iterator.hasNext());
    }
    
    /**
     *  Test that calling remove() on the iterator throws the expected exception.
     */
    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testNoRemoveIterator() {
        TestContext parent = new TestContext();
        Iterator<Context> iterator = parent.iterator();
        iterator.remove();
    }
    
    /**
     *  Test auto creation of subcontexts.
     */
    public void testAutoCreateSubcontext() {
        TestContext parent = new TestContext();
        
        //Default is not to autocreate subcontexts
        TestContext child1 = parent.getSubcontext(TestContext.class);
        Assert.assertNull(child1);
        
        parent.setAutoCreateSubcontexts(true);
        TestContext child2 = parent.getSubcontext(TestContext.class);
        Assert.assertNotNull(child2);
    }
    
    /**
     *  Test case of attempting to add a duplicate subcontext class to a parent,
     *  when replace is in effect.
     */
    public void testDuplicateAddWithReplace() {
        TestContext parent = new TestContext();
        Assert.assertFalse(parent.containsSubcontext(TestContext.class));
        
        TestContext child1 = new TestContext();
        parent.addSubcontext(child1);
        Assert.assertTrue(parent.containsSubcontext(TestContext.class));
        Assert.assertTrue(parent.getSubcontext(TestContext.class, false) == child1);
        Assert.assertTrue(child1.getParent() == parent);
        
        TestContext child2 = new TestContext();
        parent.addSubcontext(child2, true);
        Assert.assertTrue(parent.containsSubcontext(TestContext.class));
        Assert.assertTrue(parent.getSubcontext(TestContext.class, false) == child2);
        Assert.assertTrue(child2.getParent() == parent);
        Assert.assertNull(child1.getParent());
    }
    
    /**
     *  Test case of attempting to add a duplicate subcontext class to a parent,
     *  when replace is not in effect.
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testDuplicateAddWithoutReplace() {
        TestContext parent = new TestContext();
        Assert.assertFalse(parent.containsSubcontext(TestContext.class));
        
        TestContext child1 = new TestContext();
        parent.addSubcontext(child1);
        Assert.assertTrue(parent.containsSubcontext(TestContext.class));
        Assert.assertTrue(parent.getSubcontext(TestContext.class, false) == child1);
        Assert.assertTrue(child1.getParent() == parent);
        
        TestContext child2 = new TestContext();
        parent.addSubcontext(child2, false);
    }
    
    /**
     *  Test adding a subcontext to a parent, and then adding it to another parent.
     */
    public void testAddWith2Parents() {
        TestContext parent1 = new TestContext();
        Assert.assertFalse(parent1.containsSubcontext(TestContext.class));
        
        TestContext child = new TestContext();
        parent1.addSubcontext(child);
        Assert.assertTrue(parent1.containsSubcontext(TestContext.class));
        Assert.assertTrue(parent1.getSubcontext(TestContext.class, false) == child);
        Assert.assertTrue(child.getParent() == parent1);
        
        TestContext parent2 = new TestContext();
        Assert.assertFalse(parent2.containsSubcontext(TestContext.class));
        parent2.addSubcontext(child);
        Assert.assertTrue(parent2.containsSubcontext(TestContext.class));
        Assert.assertTrue(parent2.getSubcontext(TestContext.class, false) == child);
        Assert.assertTrue(child.getParent() == parent2);
        Assert.assertFalse(parent1.containsSubcontext(TestContext.class));
        Assert.assertNull(parent1.getSubcontext(TestContext.class, false));
    }
    
    public static class TestContext extends AbstractContext {

        /**
         * Constructor.
         *
         */
        public TestContext() {
            super();
        }

        /**
         * Constructor.
         *
         * @param newParent
         */
        public TestContext(Context newParent) {
            super(newParent);
        }

        /**
         * Constructor.
         *
         * @param contextId
         * @param newParent
         */
        public TestContext(String contextId, Context newParent) {
            super(contextId, newParent);
        }

        /**
         * Constructor.
         *
         * @param contextId
         */
        public TestContext(String contextId) {
            super(contextId);
        }
        
    }

}
