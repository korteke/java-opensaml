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

package org.opensaml.xml.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/** Helper class for working with various datatypes. */
public final class DatatypeHelper {

    /** Constructor. */
    private DatatypeHelper() {

    }

    /**
     * Removes preceeding or proceeding whitespace from a string or return null if the string is null or of zero length
     * after trimming (i.e. if the string only contained whitespace).
     * 
     * @param s the string to trim
     * 
     * @return the trimmed string or null
     */
    public static String safeTrimOrNullString(String s) {
        if (s != null) {
            String sTrimmed = s.trim();
            if (sTrimmed.length() > 0) {
                return sTrimmed;
            }
        }

        return null;
    }

    /**
     * Converts a delimited string into a list.
     * 
     * @param string the string to be split into a list
     * @param delimiter the delimiter between values. This string may contain
     *                  multiple delimiter characters, as allowed by
     *                  {@link StringTokenizer}
     * 
     * @return the list of values or an empty list if the given string is null or empty
     */
    public static List<String> stringToList(String string, String delimiter) {
        if (delimiter == null) {
            throw new IllegalArgumentException("String delimiter may not be null");
        }

        ArrayList<String> values = new ArrayList<String>();

        String trimmedString = safeTrimOrNullString(string);
        if (trimmedString != null) {
            StringTokenizer tokens = new StringTokenizer(trimmedString, delimiter);
            while (tokens.hasMoreTokens()) {
                values.add(tokens.nextToken());
            }
        }

        return values;
    }

    /**
     * Converts a List of strings into a single string, with values separated by a
     * specified delimiter.
     * 
     * @param values list of strings
     * @param delimiter the delimiter used between values
     * 
     * @return delimited string of values
     */
    public static String listToStringValue(List<String> values, String delimiter) {
        if (delimiter == null) {
            throw new IllegalArgumentException("String delimiter may not be null");
        }
        
        StringBuilder stringValue = new StringBuilder();
        Iterator<String> valueItr = values.iterator();
        while(valueItr.hasNext()){
            stringValue.append(valueItr.next());
            if(valueItr.hasNext()){
                stringValue.append(delimiter);
            }
        }
        
        return stringValue.toString();
    }
}