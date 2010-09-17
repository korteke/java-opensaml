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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/** String utility methods. */
public final class StringSupport {

    /** Constructor. */
    private StringSupport() {
    }

    /**
     * Checks if the given string is null or empty (containing only whitespace).
     * 
     * @param s the string to check, may be null
     * 
     * @return true if the given string is null or empty, false if not
     */
    public static boolean isNullOrEmpty(final String s) {
        String temp = trimOrNull(s);
        if (temp == null) {
            return true;
        }

        return false;
    }

    /**
     * Converts a List of strings into a single string, with values separated by a specified delimiter.
     * 
     * @param values list of strings
     * @param delimiter the delimiter used between values
     * 
     * @return delimited string of values
     */
    public static String listToStringValue(final List<String> values, final String delimiter) {
        if (delimiter == null) {
            throw new IllegalArgumentException("String delimiter may not be null");
        }

        final StringBuilder stringValue = new StringBuilder();
        final Iterator<String> valueItr = values.iterator();
        while (valueItr.hasNext()) {
            stringValue.append(valueItr.next());
            if (valueItr.hasNext()) {
                stringValue.append(delimiter);
            }
        }

        return stringValue.toString();
    }

    /**
     * Returns an empty string if the given string is null or the given string if it is not.
     * 
     * @param s string to check
     * 
     * @return an empty string if the given string is null or the given string if it is not
     */
    public static String nullToEmpty(final String s) {
        if (s == null) {
            return "";
        }

        return s;
    }

    /**
     * Converts a delimited string into a list.
     * 
     * @param string the string to be split into a list
     * @param delimiter the delimiter between values. This string may contain multiple delimiter characters, as allowed
     *            by {@link StringTokenizer}
     * 
     * @return the list of values or an empty list if the given string is null or empty
     */
    public static List<String> stringToList(final String string, final String delimiter) {
        if (delimiter == null) {
            throw new IllegalArgumentException("String delimiter may not be null");
        }

        final ArrayList<String> values = new ArrayList<String>();

        final String trimmedString = trimOrNull(string);
        if (trimmedString != null) {
            final StringTokenizer tokens = new StringTokenizer(trimmedString, delimiter);
            while (tokens.hasMoreTokens()) {
                values.add(tokens.nextToken());
            }
        }

        return values;
    }

    /**
     * Safely trims a string.
     * 
     * @param s the string to trim, may be null
     * 
     * @return the trimmed string or null if the given string was null
     */
    public static String trim(final String s) {
        if (s == null) {
            return null;
        }

        return s.trim();
    }

    /**
     * Safely trims a string and, if empty, converts it to null.
     * 
     * @param s the string to trim, may be null
     * 
     * @return the trimmed string or null if the given string was null or the trimmed string was empty
     */
    public static String trimOrNull(final String s) {
        final String temp = trim(s);
        if (temp == null || temp.length() == 0) {
            return null;
        }

        return temp;
    }
}