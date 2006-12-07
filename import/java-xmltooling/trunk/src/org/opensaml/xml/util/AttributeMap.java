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
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import javolution.util.FastMap;

import org.opensaml.xml.XMLObject;

/**
 * A map of attribute names and attribute values that invalidates the DOM of the attribute owning XMLObject when the
 * attributes change.
 * 
 * <strong>Note:</strong> 
 */
public class AttributeMap implements Map<QName, String> {

    /** XMLObject owning the attributes. */
    private XMLObject attributeOwner;

    /** Map of attributes. */
    private FastMap<QName, String> attributes;

    /**
     * Constructor.
     *
     * @param attributeOwner the XMLObject that owns these attributes
     * 
     * @throws NullPointerException thrown if the given XMLObject is null
     */
    public AttributeMap(XMLObject attributeOwner) throws NullPointerException {
        if (attributeOwner == null) {
            throw new NullPointerException("Attribute owner XMLObject may not be null");
        }

        this.attributeOwner = attributeOwner;
        attributes = new FastMap<QName, String>();
    }

    /** {@inheritDoc} */
    public String put(QName attributeName, String value) {
        if (value != get(attributeName)) {
            releaseDOM();
            attributes.put(attributeName, value);
        }
        return null;
    }

    /** {@inheritDoc} */
    public void clear() {
        for (QName attributeName : attributes.keySet()) {
            remove(attributeName);
        }
    }

    /**
     * Returns the set of keys.
     * 
     * @return unmodifiable set of keys
     */
    public Set<QName> keySet() {
        return Collections.unmodifiableSet(attributes.keySet());
    }

    /** {@inheritDoc} */
    public int size() {
        return attributes.size();
    }

    /** {@inheritDoc} */
    public boolean isEmpty() {
        return attributes.isEmpty();
    }

    /** {@inheritDoc} */
    public boolean containsKey(Object key) {
        return attributes.containsKey(key);
    }

    /** {@inheritDoc} */
    public boolean containsValue(Object value) {
        return attributes.containsValue(value);
    }

    /** {@inheritDoc} */
    public String get(Object key) {
        return attributes.get(key);
    }

    /** {@inheritDoc} */
    public String remove(Object key) {
        String removedValue = attributes.remove(key);
        if (removedValue != null) {
            releaseDOM();
        }

        return removedValue;
    }

    /** {@inheritDoc} */
    public void putAll(Map<? extends QName, ? extends String> t) {
        if (t != null && t.size() > 0) {
            for (Entry<? extends QName, ? extends String> entry : t.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Returns the values in this map.
     * 
     * @return an unmodifiable collection of values
     */
    public Collection<String> values() {
        return Collections.unmodifiableCollection(attributes.values());
    }

    /**
     * Returns the set of entries.
     * 
     * @return unmodifiable set of entries
     */
    public Set<Entry<QName, String>> entrySet() {
        return Collections.unmodifiableSet(attributes.entrySet());
    }

    /**
     * Releases the DOM caching associated XMLObject and its ancestors.
     */
    private void releaseDOM() {
        attributeOwner.releaseDOM();
        attributeOwner.releaseParentDOM(true);
    }
}