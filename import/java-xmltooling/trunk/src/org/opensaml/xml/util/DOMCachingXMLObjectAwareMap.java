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

import java.util.Map;

import org.apache.commons.collections.map.AbstractHashedMap;
import org.opensaml.xml.XMLObject;

/**
 * A map that is aware of DOMCachingXMLObjects and will release its cached DOM when modified. This map allows supports a
 * null key.
 */
public class DOMCachingXMLObjectAwareMap extends AbstractHashedMap implements Map{

    /** The DOM caching XMLObject */
    private XMLObject xmlObject;

    /**
     * Constructor
     * 
     * @param domCachingXMLObject the XMLObject whose DOM will be invalidated upon map modifications
     */
    public DOMCachingXMLObjectAwareMap(XMLObject domCachingXMLObject) {
        super(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR, DEFAULT_THRESHOLD);
        xmlObject = domCachingXMLObject;
    }

    /*
     * @see org.apache.commons.collections.map.AbstractHashedMap#put(java.lang.Object, java.lang.Object)
     */
    public Object put(Object key, Object value) {
        Object previousValue = super.put(key, value);
        
        if(previousValue == null || !previousValue.equals(value)) {
            releaseDOM();
        }
        
        return previousValue;
    }

    /*
     * @see java.util.Map#remove(java.lang.Object)
     */
    public Object remove(Object key) {
        Object removedObject = super.remove(key);
        
        if(removedObject != null) {
            releaseDOM();
        }
        
        return removedObject;
    }
    
    /*
     * @see java.util.Map#clear()
     */
    public void clear() {
        if(!isEmpty()) {
            super.clear();
            releaseDOM();
        }
    }

    /**
     * Releases the DOM caching associated XMLObject and its ancestors.
     */
    private void releaseDOM() {
        xmlObject.releaseDOM();
        xmlObject.releaseParentDOM(true);
    }
}