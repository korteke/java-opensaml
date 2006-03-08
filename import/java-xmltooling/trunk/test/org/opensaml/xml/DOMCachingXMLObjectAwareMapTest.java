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

package org.opensaml.xml;

import javax.xml.namespace.QName;

/**
 * Unit test for {@link org.opensaml.xml.util.DOMCachingXMLObjectAwareMap}.
 */
public class DOMCachingXMLObjectAwareMapTest extends XMLObjectBaseTestCase {

    /** QName for SimpleXMLObject */
    private QName simpleXMLObjectQName;
    
    /**
     * Tests changes caused by map functions correctly invalidates a cached DOM.
     */
    public void testMapFunctions() {
        
    }
    
    /**
     * Tests that changes caused by mutations in map methods that return collections correctly invalidate a cached DOM.
     */
    public void testCollectionFunctions() {
        
    }
    
    /**
     * Tests that changes causes by mutations of iterators over collections returned by the map correctly invalidate a cached DOM.
     *
     */
    public void testIteratorFunctions() {
        
    }
}