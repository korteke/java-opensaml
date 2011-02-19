/*
 * Copyright 2010 University Corporation for Advanced Internet Development, Inc.
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

import java.util.Collection;
import java.util.Iterator;

import org.opensaml.util.Assert;

/** Set of helper methods for working with collections. */
public final class CollectionSupport {

    /** Constructor. */
    private CollectionSupport() {
    }

    /**
     * Adds the given element to the given collection if, and only if, it is not null.
     * 
     * @param <T> type of the collection elements
     * @param collection collection to which the element is added
     * @param element element to be added to the collection, may be null
     * 
     * @return true if this operation resulted in a change to the collection, false otherwise
     */
    public static <T> boolean nonNullAdd(final Collection<T> collection, final T element) {
        if (element == null) {
            return false;
        }

        return collection.add(element);
    }

    /**
     * Removes the given element from the given collection if, and only if, it is not null.
     * 
     * @param collection collection to which the element is added
     * @param element element to be added to the collection, may be null
     * 
     * @return true if this operation resulted in a change to the collection, false otherwise
     */
    public static boolean nonNullRemove(final Collection<?> collection, final Object element) {
        if (element == null) {
            return false;
        }

        return collection.remove(element);
    }

    /**
     * Clears the target collection and then adds all non-null elements from the source collection to the target
     * collection.
     * 
     * @param <T> type of elements in the collections
     * @param source source collection, may be null, empty, or contain null elements
     * @param target target collection, may not be null
     */
    public static <T> void nonNullReplace(final Collection<T> source, final Collection<T> target) {
        Assert.isNotNull(target, "Target collection may not be null");

        target.clear();

        if (source == null || source.isEmpty()) {
            return;
        }

        for (T element : source) {
            nonNullAdd(target, element);
        }
    }

    /**
     * Copies all the non-null elements from the source collection to the target collection in iteration order. If
     * either the source or target collection is <code>null</code> this method simply returns.
     * 
     * @param <CollectionType> type of the target collection
     * @param <T> type of elements
     * @param source source collection, may be null
     * @param target target collection, may be null
     * 
     * @return the target collection
     */
    public static <CollectionType extends Collection<T>, T> CollectionType addNonNull(
            final Collection<? extends T> source, final CollectionType target) {
        if (source == null || source.isEmpty() || target == null) {
            return target;
        }

        T element;
        final Iterator<? extends T> srcItr = source.iterator();
        while (srcItr.hasNext()) {
            element = srcItr.next();
            if (element != null) {
                target.add(element);
            }
        }

        return target;
    }

    /**
     * Converts the given set of elements in to a list.
     * 
     * @param <T> element type
     * @param elements elements to add to the list, may include null but null values will be removed
     * 
     * @return the constructed list
     */
    public static <T> LazyList<T> toList(final T... elements) {
        return toList(true, elements);
    }

    /**
     * Converts the given set of elements in to a list.
     * 
     * @param <T> element type
     * @param elements elements to add to the list, may include null
     * @param removeNulls whether null elements should be removed
     * 
     * @return the constructed list
     */
    public static <T> LazyList<T> toList(final boolean removeNulls, final T... elements) {
        final LazyList<T> list = new LazyList<T>();

        if (elements != null) {
            for (T element : elements) {
                if (removeNulls && element == null) {
                    continue;
                }
                list.add(element);
            }
        }

        return list;
    }

    /**
     * Converts the given set of elements in to a set.
     * 
     * @param <T> element type
     * @param elements elements to add to the list, may include null but null elements will be removed
     * 
     * @return the constructed set
     */
    public static <T> LazySet<T> toSet(final T... elements) {
        return toSet(true, elements);
    }

    /**
     * Converts the given set of elements in to a set.
     * 
     * @param <T> element type
     * @param elements elements to add to the list, may include null
     * @param removeNulls whether null elements should be removed
     * 
     * @return the constructed set
     */
    public static <T> LazySet<T> toSet(final boolean removeNulls, final T... elements) {
        final LazySet<T> set = new LazySet<T>();

        if (elements != null) {
            for (T element : elements) {
                if (removeNulls && element == null) {
                    continue;
                }
                set.add(element);
            }
        }

        return set;
    }
}