/*
 * Copyright 2008 University Corporation for Advanced Internet Development, Inc.
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

package org.opensaml.util.collections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.jcip.annotations.NotThreadSafe;

/**
 * A list that is lazy initialized. This list takes very little memory when storing zero or one item.
 * 
 * @param <ElementType> type of elements within the list
 */
@NotThreadSafe
public class LazyList<ElementType> implements List<ElementType>, Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = -7741904523916701817L;

    /** Delegate list. */
    private List<ElementType> delegate = Collections.emptyList();

    /** {@inheritDoc} */
    public boolean add(final ElementType item) {
        if (delegate.isEmpty()) {
            delegate = Collections.singletonList(item);
            return true;
        } else {
            delegate = buildList();
            return delegate.add(item);
        }
    }

    /** {@inheritDoc} */
    public void add(final int index, final ElementType element) {
        delegate = buildList();
        delegate.add(index, element);
    }

    /** {@inheritDoc} */
    public boolean addAll(final Collection<? extends ElementType> collection) {
        delegate = buildList();
        return delegate.addAll(collection);
    }

    /** {@inheritDoc} */
    public boolean addAll(final int index, final Collection<? extends ElementType> collection) {
        delegate = buildList();
        return delegate.addAll(index, collection);
    }

    /** {@inheritDoc} */
    public void clear() {
        delegate = Collections.emptyList();
    }

    /** {@inheritDoc} */
    public boolean contains(final Object element) {
        return delegate.contains(element);
    }

    /** {@inheritDoc} */
    public boolean containsAll(final Collection<?> collections) {
        return delegate.containsAll(collections);
    }

    /** {@inheritDoc} */
    public ElementType get(final int index) {
        return delegate.get(index);
    }

    /** {@inheritDoc} */
    public int indexOf(final Object element) {
        return delegate.indexOf(element);
    }

    /** {@inheritDoc} */
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    /** {@inheritDoc} */
    public Iterator<ElementType> iterator() {
        return delegate.iterator();
    }

    /** {@inheritDoc} */
    public int lastIndexOf(final Object element) {
        return delegate.lastIndexOf(element);
    }

    /** {@inheritDoc} */
    public ListIterator<ElementType> listIterator() {
        return delegate.listIterator();
    }

    /** {@inheritDoc} */
    public ListIterator<ElementType> listIterator(final int index) {
        return delegate.listIterator(index);
    }

    /** {@inheritDoc} */
    public boolean remove(final Object element) {
        delegate = buildList();
        return delegate.remove(element);
    }

    /** {@inheritDoc} */
    public ElementType remove(final int index) {
        delegate = buildList();
        return delegate.remove(index);
    }

    /** {@inheritDoc} */
    public boolean removeAll(final Collection<?> collection) {
        delegate = buildList();
        return delegate.removeAll(collection);
    }

    /** {@inheritDoc} */
    public boolean retainAll(final Collection<?> collection) {
        delegate = buildList();
        return delegate.retainAll(collection);
    }

    /** {@inheritDoc} */
    public ElementType set(final int index, final ElementType element) {
        delegate = buildList();
        return delegate.set(index, element);
    }

    /** {@inheritDoc} */
    public int size() {
        return delegate.size();
    }

    /** {@inheritDoc} */
    public List<ElementType> subList(final int fromIndex, final int toIndex) {
        return delegate.subList(fromIndex, toIndex);
    }

    /** {@inheritDoc} */
    public Object[] toArray() {
        return delegate.toArray();
    }

    /** {@inheritDoc} */
    public <T> T[] toArray(T[] type) {
        return delegate.toArray(type);
    }

    /**
     * Builds an appropriate delegate for this list.
     * 
     * @return delegate for this list
     */
    protected List<ElementType> buildList() {
        if (delegate instanceof ArrayList<?>) {
            return delegate;
        }

        return new ArrayList<ElementType>(delegate);
    }

    /** {@inheritDoc} */
    public String toString() {
        return delegate.toString();
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return delegate.hashCode();
    }

    /** {@inheritDoc} */
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        return delegate.equals(((LazyList<?>) obj).delegate);
    }
}