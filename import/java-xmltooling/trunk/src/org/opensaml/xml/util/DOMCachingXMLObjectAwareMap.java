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

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.opensaml.xml.DOMCachingXMLObject;

/**
 * A map that is aware of DOMCachingXMLObjects and will release its cached DOM when modified.  This map 
 * allows supports a null key.
 */
public class DOMCachingXMLObjectAwareMap<KeyType, ValueType> implements Map<KeyType, ValueType>{

    /** The DOM caching XMLObject */
    private DOMCachingXMLObject xmlObject;
    
    private Hashtable backingTable;
    
    /**
     * Constructor
     *
     * @param domCachingXMLObject the XMLObject whose DOM will be invalidated upon map modifications
     */
    public DOMCachingXMLObjectAwareMap(DOMCachingXMLObject domCachingXMLObject){
        xmlObject = domCachingXMLObject;
    }

    public int size() {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean containsKey(Object arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean containsValue(Object arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    public ValueType get(Object arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public ValueType put(KeyType arg0, ValueType arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    public ValueType remove(Object arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public void putAll(Map<? extends KeyType, ? extends ValueType> arg0) {
        // TODO Auto-generated method stub
        
    }

    public void clear() {
        // TODO Auto-generated method stub
        
    }

    public Set<KeyType> keySet() {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<ValueType> values() {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<Entry<KeyType, ValueType>> entrySet() {
        // TODO Auto-generated method stub
        return null;
    }

}