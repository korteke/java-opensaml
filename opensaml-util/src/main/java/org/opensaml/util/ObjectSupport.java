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

package org.opensaml.util;

/** Helper methods for working with Objects. */
public final class ObjectSupport {

    /** Constructor. */
    private ObjectSupport() {
    }

    /**
     * Performs a safe (null-aware) {@link Object#hashCode()}.
     * 
     * @param o object for which to get the hash code, may be null
     * 
     * @return the hash code for the object of 0 if the given object is null
     */
    public static int hashCode(final Object o) {
        if (o == null) {
            return 0;
        }

        return o.hashCode();
    }

    /**
     * Performs a safe (null-aware) {@link Object#equals(Object)} check.
     * 
     * @param o1 first object, may be null
     * @param o2 second object, may be null
     * 
     * @return true of the objects are equal or both null, false otherwise
     */
    public static boolean equals(final Object o1, final Object o2) {
        if (o1 == null || o2 == null) {
            return o1 == o2;
        }

        return o1.equals(o2);
    }

    /**
     * Null-safe check to determine if the given object is equal to any of a list of objects.
     * 
     * @param o1 object to check if it's equal to any object in a list
     * @param objects list of objects
     * 
     * @return true of the given object is equal to any object in the given list
     */
    public static boolean equalsAny(final Object o1, final Object... objects) {
        if (o1 == null || objects == null) {
            return o1 == objects;
        }

        for (Object object : objects) {
            if (equals(o1, object)) {
                return true;
            }
        }

        return false;
    }
}