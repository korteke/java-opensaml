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

package org.opensaml.core.xml.util;

import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.Assert;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.core.xml.mock.SimpleXMLObjectBuilder;
import org.opensaml.core.xml.util.XMLObjectChildrenList;

/**
 * Test case for {@link org.opensaml.core.xml.util.XMLObjectChildrenList}
 */
public class XMLObjectChildrenListTest {

    private SimpleXMLObjectBuilder sxoBuilder = new SimpleXMLObjectBuilder();
    
    /**
     * Tests the add methods of ths list.
     */
    @Test
    public void testAdd() {
        SimpleXMLObject parentObject = sxoBuilder.buildObject();

        XMLObjectChildrenList<SimpleXMLObject> objectList = new XMLObjectChildrenList<SimpleXMLObject>(parentObject);
        Assert.assertEquals(objectList.size(), 0,
                "XMLObject list was supposed to be empty but instead had " + objectList.size() + " elements");

        objectList.add(null);
        Assert.assertEquals(objectList.size(), 0, "XMLObject list allowed a null element to be added");

        // Test adding a single element
        SimpleXMLObject child1 = sxoBuilder.buildObject();
        objectList.add(child1);
        Assert.assertEquals(objectList.size(), 1,
                "XMLObject list was supposed to have 1 element but instead had " + objectList.size());
        Assert.assertEquals(child1.getParent(), parentObject, "Child 1 did not have the correct parent object");

        // Test adding an collection of children
        List<SimpleXMLObject> childList = new LinkedList<SimpleXMLObject>();
        SimpleXMLObject child2 = sxoBuilder.buildObject();
        childList.add(child2);
        SimpleXMLObject child3 = sxoBuilder.buildObject();
        childList.add(child3);

        objectList.addAll(childList);
        Assert.assertEquals(objectList.size(), 3,
                "XMLObject list was supposed to have 3 element but instead had " + objectList.size());
        Assert.assertEquals(child2.getParent(), parentObject, "Child 2 did not have the correct parent object");
        Assert.assertEquals(child3.getParent(), parentObject, "Child 3 did not have the correct parent object");
    }

    /**
     * Tests the set method of the list.
     */
    @Test
    public void testSet() {
        SimpleXMLObject parentObject = sxoBuilder.buildObject();

        XMLObjectChildrenList<SimpleXMLObject> objectList = new XMLObjectChildrenList<SimpleXMLObject>(parentObject);
        Assert.assertEquals(objectList.size(), 0,
                "XMLObject list was supposed to be empty but instead had " + objectList.size() + " elements");

        // Test adding a single element
        SimpleXMLObject child1 = sxoBuilder.buildObject();
        objectList.add(child1);
        Assert.assertEquals(objectList.size(), 1,
                "XMLObject list was supposed to have 1 element but instead had " + objectList.size());
        Assert.assertEquals(child1.getParent(), parentObject, "Child 1 did not have the correct parent object");

        objectList.set(0, null);
        Assert.assertNotNull(objectList.get(0), "XMLObject list allowed a null element to be set");

        SimpleXMLObject child2 = sxoBuilder.buildObject();
        SimpleXMLObject replacedChild = objectList.set(0, child2);
        Assert.assertEquals(objectList.size(), 1,
                "XMLObject list was supposed to have 1 element but instead had " + objectList.size());

        // Make sure Child 2 got it's parent set correctly and that the element now in the list is Child 2
        Assert.assertEquals(child2.getParent(), parentObject, "Child 2 did not have the correct parent object");
        Assert.assertEquals(objectList.get(0), child2, "Child element was not Child 2");

        // Make sure Child 1 got it's parent nulled out and is no longer in the list
        Assert.assertNull(replacedChild.getParent(), "Replaced child element parent was not null");
        Assert.assertFalse(objectList
                .contains(child1), "Child1 still appears in the object list even though it should have been removed");
    }

    /**
     * Test the remove methods of the list.
     */
    @Test
    public void testRemove() {
        SimpleXMLObject parentObject = sxoBuilder.buildObject();
        XMLObjectChildrenList<SimpleXMLObject> objectList = new XMLObjectChildrenList<SimpleXMLObject>(parentObject);

        // Test removing a single element
        SimpleXMLObject child1 = sxoBuilder.buildObject();
        objectList.add(child1);
        Assert.assertEquals(objectList.size(), 1,
                "XMLObject list was supposed to have 1 element but instead had " + objectList.size());

        objectList.remove(child1);
        Assert.assertEquals(objectList.size(), 0,
                "XMLObject list was supposed to have 0 element but instead had " + objectList.size());
        Assert.assertNull(child1.getParent(), "Child 1 parent was not null");

        // Test removing an collection of children
        List<SimpleXMLObject> childList = new LinkedList<SimpleXMLObject>();
        SimpleXMLObject child2 = sxoBuilder.buildObject();
        childList.add(child2);
        SimpleXMLObject child3 = sxoBuilder.buildObject();
        childList.add(child3);

        objectList.addAll(childList);
        Assert.assertEquals(objectList.size(), 2,
                "XMLObject list was supposed to have 2 element but instead had " + objectList.size());

        objectList.removeAll(childList);
        Assert.assertEquals(objectList.size(), 0,
                "XMLObject list was supposed to have 0 element but instead had " + objectList.size());
        Assert.assertNull(child2.getParent(), "Child 2 parent was not null");
        Assert.assertNull(child3.getParent(), "Child 3 parent was not null");
    }

    /**
     * Test the iterator methods of the list.
     */
    @Test
    public void testIterator() {
        SimpleXMLObject parentObject = sxoBuilder.buildObject();
        XMLObjectChildrenList<SimpleXMLObject> objectList = new XMLObjectChildrenList<SimpleXMLObject>(parentObject);
        SimpleXMLObject child1 = sxoBuilder.buildObject();
        objectList.add(child1);
        SimpleXMLObject child2 = sxoBuilder.buildObject();
        objectList.add(child2);
        SimpleXMLObject child3 = sxoBuilder.buildObject();
        objectList.add(child3);

        Iterator<SimpleXMLObject> itr = objectList.iterator();

        SimpleXMLObject firstObject = itr.next();
        Assert.assertNotNull(firstObject, "First iterator was null and should not have been");
        Assert.assertEquals(firstObject, child1, "First iterator object should have been child 1 but was not");

        itr.next();
        itr.remove();
        SimpleXMLObject thirdObject = itr.next();
        Assert.assertEquals(thirdObject, child3, "Third iterator object should have been child 3 but was not");
        Assert.assertNull(child2.getParent(), "Child 2 parent was not null");

        SimpleXMLObject child4 = sxoBuilder.buildObject();
        objectList.add(child4);

        try {
            itr.next();
            Assert.fail("Iterator allowed list to change underneath it without failing");
        } catch (ConcurrentModificationException e) {
            // DO NOTHING, THIS IS SUPPOSED TO FAIL
        }
    }

    /**
     * Test the clear method of the list.
     */
    @Test
    public void testClear() {
        SimpleXMLObject parentObject = sxoBuilder.buildObject();
        XMLObjectChildrenList<SimpleXMLObject> objectList = new XMLObjectChildrenList<SimpleXMLObject>(parentObject);

        List<SimpleXMLObject> childList = new LinkedList<SimpleXMLObject>();
        SimpleXMLObject child1 = sxoBuilder.buildObject();
        childList.add(child1);
        SimpleXMLObject child2 = sxoBuilder.buildObject();
        childList.add(child2);
        SimpleXMLObject child3 = sxoBuilder.buildObject();
        childList.add(child3);
        objectList.addAll(childList);

        objectList.clear();
        Assert.assertEquals(objectList.size(), 0,
                "XMLObject list was supposed to have 0 element buts instead had " + objectList.size());
        Assert.assertNull(child1.getParent(), "Child 1 parent was not null");
        Assert.assertNull(child2.getParent(), "Child 2 parent was not null");
        Assert.assertNull(child3.getParent(), "Child 2 parent was not null");
    }
}