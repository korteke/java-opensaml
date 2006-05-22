/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xml.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.opensaml.xml.XMLObjectBaseTestCase;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.mock.SimpleXMLObject;
import org.opensaml.xml.util.DOMCachingXMLObjectAwareMap;

/**
 * Unit test for {@link org.opensaml.xml.util.DOMCachingXMLObjectAwareMap}.
 */
public class DOMCachingXMLObjectAwareMapTest extends XMLObjectBaseTestCase {
    
    /**
     * Constructor
     */
    public DOMCachingXMLObjectAwareMapTest() {
        
    }

    /**
     * Tests changes caused by map functions correctly invalidates a cached DOM.
     */
    public void testMapFunctions() {
        QName fooName = new QName("foo");
        SimpleXMLObject xmlObject = buildDOMCachingSimpleXMLObject();
        DOMCachingXMLObjectAwareMap domMap = new DOMCachingXMLObjectAwareMap(xmlObject);
        
        // Test put()
        domMap.put(fooName, "bar");
        assertNull("Cached DOM was not released when Map#put was called", xmlObject.getDOM());
        
        //Test putting existing object does not invalidate dom
        marshallXMLObject(xmlObject);
        domMap.put(fooName, "bar");
        assertNotNull("Cached DOM was incorrectly released when Map#put was called with element that already existed in map", xmlObject.getDOM());
        
        // Test remove()
        marshallXMLObject(xmlObject);
        domMap.remove(fooName);
        assertNull("Cached DOM was not released when Map#remove was called", xmlObject.getDOM());
        
        //Test removing non-existant item doesn't invalidate dom
        marshallXMLObject(xmlObject);
        domMap.remove(fooName);
        assertNotNull("Cached DOM was incorrectly released when Map#remove was called with non-existant key", xmlObject.getDOM());
        
        // Test clear();
        domMap.put(fooName, "bar");
        marshallXMLObject(xmlObject);
        domMap.clear();
        assertNull("Cached DOM was not released when Map#clear was called", xmlObject.getDOM());
        
        // Test clear doesn't invalidate DOM when map is empty
        marshallXMLObject(xmlObject);
        domMap.clear();
        assertNotNull("Cached DOM was incorrectly released when Map#clear was called on empty map", xmlObject.getDOM());
        
    }

    /**
     * Tests that changes caused by mutations in map methods that return collections correctly invalidate a cached DOM.
     */
    public void testCollectionFunctions() {
        QName fooName = new QName("foo");
        SimpleXMLObject xmlObject = buildDOMCachingSimpleXMLObject();
        DOMCachingXMLObjectAwareMap domMap = new DOMCachingXMLObjectAwareMap(xmlObject);
        Set<Map.Entry> entrySet;
        
        // Test EntrySet remove
        domMap.put(fooName, "bar");
        marshallXMLObject(xmlObject);
        entrySet = domMap.entrySet();
        Iterator setItr = entrySet.iterator();
        entrySet.remove(setItr.next());
        assertNull("Cached DOM was not released when Set#remove was called", xmlObject.getDOM());
        
        // Test EntrySet remove does not invalidate DOM when asked to remove non-existant item
        domMap.put(fooName, "bar");
        marshallXMLObject(xmlObject);
        entrySet = domMap.entrySet();
        entrySet.remove(null);
        assertNotNull("Cached DOM was incorrectl released when Set#remove was called with item not in set", xmlObject.getDOM());
        
        // Test EntrySet removeAll
        domMap.put(fooName, "bar");
        marshallXMLObject(xmlObject);
        entrySet = domMap.entrySet();
        entrySet.removeAll(entrySet);
        assertNull("Cached DOM was not released when Set#removeAll was called", xmlObject.getDOM());
        
        // Test EntrySet removeAll does not invalidate DOM when asked to remove non-existant items
        marshallXMLObject(xmlObject);
        entrySet = domMap.entrySet();
        entrySet.removeAll(entrySet);
        assertNotNull("Cached DOM was incorrectly released when Set#removeAll was called with empty set", xmlObject.getDOM());
        
        // Test EntrySet clear
        domMap.put(fooName, "bar");
        marshallXMLObject(xmlObject);
        entrySet = domMap.entrySet();
        entrySet.clear();
        assertNull("Cached DOM was not released when Set#rclear was called", xmlObject.getDOM());
        
        // Test EntrySet clear does not invalidate DOM when asked to clear an empty set
        marshallXMLObject(xmlObject);
        entrySet = domMap.entrySet();
        entrySet.clear();
        assertNotNull("Cached DOM was incorrectly released when Set#rclear was called on an empty set", xmlObject.getDOM());
    }

    /**
     * Tests that changes causes by mutations of iterators over collections returned by the map correctly invalidate a
     * cached DOM.
     * 
     */
    public void testIteratorFunctions() {
        QName fooName = new QName("foo");
        SimpleXMLObject xmlObject = buildDOMCachingSimpleXMLObject();
        DOMCachingXMLObjectAwareMap domMap = new DOMCachingXMLObjectAwareMap(xmlObject);
        Set<Map.Entry> entrySet;
        
        // Test EntrySet remove
        domMap.put(fooName, "bar");
        marshallXMLObject(xmlObject);
        entrySet = domMap.entrySet();
        Iterator setItr = entrySet.iterator();
        setItr.next();
        setItr.remove();
        assertNull("Cached DOM was not released when Iterator#remove was called", xmlObject.getDOM());
    }

    /**
     * Gets a XMLObject that has a cached DOM.
     * 
     * @return a XMLObject that has a cached DOM
     */
    protected SimpleXMLObject buildDOMCachingSimpleXMLObject() {
        SimpleXMLObject xmlObject = (SimpleXMLObject) buildXMLObject(simpleXMLObjectQName);

        xmlObject.setId("Foo");
        marshallXMLObject(xmlObject);

        return xmlObject;
    }
    
    /**
     * Marshalls the XMLObject so that it has a cached DOM.
     * 
     * @param xmlObject the object to marshall
     */
    protected void marshallXMLObject(SimpleXMLObject xmlObject) {
        Marshaller marshaller = marshallerFactory.getMarshaller(simpleXMLObjectQName);
        try {
            marshaller.marshall(xmlObject);
            assertNotNull(xmlObject.getDOM());
        } catch (MarshallingException e) {
            fail("Unable to marshall xmlobject");
        }
    }
}