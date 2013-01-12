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

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.collection.LazyList;
import net.shibboleth.utilities.java.support.collection.LazyMap;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.core.xml.XMLObject;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;

/**
 * A list which indexes XMLObjects by their schema type and element QName for quick retrival based on those items.
 * 
 * @param <ElementType> the type of element added to the list
 */
@NotThreadSafe
public class IndexedXMLObjectChildrenList<ElementType extends XMLObject> extends XMLObjectChildrenList<ElementType> {

    /** Index of objects by type and name. */
    private final Map<QName, List<ElementType>> objectIndex;

    /**
     * Constructor.
     * 
     * @param parent the parent of the {@link XMLObject}s added to the list
     */
    public IndexedXMLObjectChildrenList(@Nonnull final XMLObject parent) {
        super(parent);
        objectIndex = new LazyMap<QName, List<ElementType>>();
    }

    /**
     * Constructor.
     * 
     * @param parent the parent of all elements
     * @param col collection to add to this list
     */
    public IndexedXMLObjectChildrenList(@Nonnull final XMLObject parent, @Nonnull final Collection<ElementType> col) {
        super(parent);
        Constraint.isNotNull(col, "Initial collection cannot be null");
        
        objectIndex = new LazyMap<QName, List<ElementType>>();
        
        // This does call our add, which handles the null case properly, but
        // I didn't want to depend on that implementation. Keeping the fail silently
        // behavior means not using an Immutable collection copy.
        addAll(Collections2.filter(col, Predicates.notNull()));
    }

    /** {@inheritDoc} */
    public void add(int index, @Nullable final ElementType element) {
        super.add(index, element);
        indexElement(element);
    }

    /** {@inheritDoc} */
    public void clear() {
        super.clear();
        objectIndex.clear();
    }

    /**
     * Retrieves all the SAMLObjects that have given schema type or element name, or a
     * null if no such objects exist.
     * 
     * @param typeOrName the schema type or element name
     * 
     * @return list of SAMLObjects that have given schema type or element name or null
     */
    @Nonnull public List<ElementType> get(@Nonnull final QName typeOrName) {
        checkAndCreateIndex(typeOrName);
        return objectIndex.get(typeOrName);
    }

    /**
     * Check for the existence of an index for the specified QName and create it
     * if it doesn't exist.
     * 
     * @param index the index to check
     */
    protected void checkAndCreateIndex(QName index) {
        if (!objectIndex.containsKey(index)) {
            objectIndex.put(index, new LazyList<ElementType>());
        }
    }

    /**
     * Indexes the given SAMLObject by type and element name. A null input is ignored.
     * 
     * @param element the SAMLObject to index
     */
    protected void indexElement(@Nullable final ElementType element) {
        if (element == null) {
            return;
        }

        QName type = element.getSchemaType();
        if (type != null) {
            indexElement(type, element);
        }

        indexElement(element.getElementQName(), element);
    }

    /**
     * Indexes the given SAMLobject by the given index.
     * 
     * @param index the index for the element
     * @param element the element to be indexed
     */
    protected void indexElement(QName index, ElementType element) {
        List<ElementType> objects = get(index);
        objects.add(element);
    }

    /**
     * Removes a given element from the list and index.
     * 
     * @param element the element to be removed
     * 
     * @return true if the element was in the list and removed, false if not
     */
    public boolean remove(@Nullable final ElementType element) {
        
        boolean elementRemoved = super.remove(element);
        if (elementRemoved) {
            removeElementFromIndex(element);
        }

        return elementRemoved;
    }

    /** {@inheritDoc} */
    @Nonnull public ElementType remove(int index) {
        ElementType returnValue = super.remove(index);

        removeElementFromIndex(returnValue);

        return returnValue;
    }

    /**
     * Removes the given element from the schema type and element QName index. A null input is
     * ignored.
     * 
     * @param element the element to remove from the index
     */
    protected void removeElementFromIndex(@Nullable final ElementType element) {
        if (element == null) {
            return;
        }

        QName type = element.getSchemaType();
        if (type != null) {
            removeElementFromIndex(type, element);
        }

        removeElementFromIndex(element.getElementQName(), element);
    }

    /**
     * Removes an object from the given index id.
     * 
     * @param index the id of the index
     * @param element the element to be removed from that index
     */
    protected void removeElementFromIndex(@Nonnull final QName index, @Nonnull final ElementType element) {
        List<ElementType> objects = get(index);
        objects.remove(element);
    }

    /** {@inheritDoc} */
    @Nullable public ElementType set(int index, @Nullable final ElementType element) {
        ElementType returnValue = super.set(index, element);

        removeElementFromIndex(returnValue);

        indexElement(element);
        return returnValue;
    }

    /**
     * Returns a view of the list that only contains elements stored under the given index. The returned list is backed
     * by this list and supports all optional operations, so changes made to the returned list are reflected in this
     * list.
     * 
     * @param index index of the elements returned in the list view
     * 
     * @return a view of this list that contains only the elements stored under the given index
     */
    @Nonnull public List<? extends ElementType> subList(@Nonnull final QName index) {
        checkAndCreateIndex(index);
        return new ListView<ElementType>(this, index);
    }
}

/**
 * A special list that works as a view of an IndexedXMLObjectChildrenList showing only the sublist associated with a
 * given index. Operations performed on this sublist are reflected in the backing list. Index-based mutation operations
 * are not supported.
 * 
 * @param <ElementType> the XMLObject type that this list operates on
 */
class ListView<ElementType extends XMLObject> extends AbstractList<ElementType> {

    /** List that backs this view. */
    private final IndexedXMLObjectChildrenList<ElementType> backingList;

    /** Index that points to the list, in the backing list, that this view operates on. */
    private final QName index;

    /** List, in the backing list, that the given index points to. */
    private List<ElementType> indexList;

    /**
     * Constructor.
     * 
     * @param newBackingList the list that backs this view
     * @param newIndex the element schema type or name of the sublist this view operates on
     */
    public ListView(@Nonnull final IndexedXMLObjectChildrenList<ElementType> newBackingList,
            @Nonnull final QName newIndex) {
        backingList = newBackingList;
        index = newIndex;
        indexList = backingList.get(index);
    }

    /** {@inheritDoc} */
    public boolean add(@Nullable final ElementType o) {
        boolean result = backingList.add(o);
        indexList = backingList.get(index);
        return result;
    }

    /** {@inheritDoc} */
    public void add(int newIndex, @Nullable final ElementType element) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public boolean addAll(@Nonnull final Collection<? extends ElementType> c) {
        boolean result = backingList.addAll(c);
        indexList = backingList.get(index);
        return result;
    }

    /** {@inheritDoc} */
    public boolean addAll(int i, @Nonnull final Collection<? extends ElementType> c) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public void clear() {
        // Create a copy of the current list to avoid a potential concurrent modification error.
        LazyList<ElementType> copy = new LazyList<ElementType>();
        copy.addAll(indexList);
        backingList.removeAll(copy);
        indexList = backingList.get(index);
    }

    /**
     * Checks to see if the given element is contained in this list.
     * 
     * @param element the element to check for
     * 
     * @return true if the element is in this list, false if not
     */
    public boolean contains(@Nonnull final Object element) {
        return indexList.contains(element);
    }

    /** {@inheritDoc} */
    public boolean containsAll(@Nonnull final Collection<?> c) {
        return indexList.containsAll(c);
    }

    /** {@inheritDoc} */
    @Nonnull public ElementType get(int newIndex) {
        return indexList.get(newIndex);
    }

    /** {@inheritDoc} */
    public int indexOf(@Nonnull final Object o) {
        return indexList.indexOf(o);
    }

    /** {@inheritDoc} */
    public boolean isEmpty() {
        return indexList.isEmpty();
    }

    /** {@inheritDoc} */
    public int lastIndexOf(@Nonnull final Object o) {
        return indexList.lastIndexOf(o);
    }

    /** {@inheritDoc} */
    @Nonnull public ElementType remove(int newIndex) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public boolean remove(@Nullable final Object o) {
        boolean result = backingList.remove(o);
        indexList = backingList.get(index);
        return result;
    }

    /** {@inheritDoc} */
    public boolean removeAll(@Nonnull final Collection<?> c) {
        boolean result = backingList.removeAll(c);
        indexList = backingList.get(index);
        return result;
    }

    /** {@inheritDoc} */
    public boolean retainAll(@Nonnull final Collection<?> c) {
        boolean result = backingList.retainAll(c);
        indexList = backingList.get(index);
        return result;
    }

    /** {@inheritDoc} */
    public ElementType set(int newIndex, @Nonnull final ElementType element) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public int size() {
        return indexList.size();
    }

    /** {@inheritDoc} */
    @Nonnull public Object[] toArray() {
        return indexList.toArray();
    }

    /** {@inheritDoc} */
    @Nonnull public <T extends Object> T[] toArray(@Nonnull final T[] a) {
        return indexList.toArray(a);
    }
}