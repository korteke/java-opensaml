/*
 * Licensed to the University Corporation for Advanced Internet Development, Inc.
 * under one or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache 
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

package org.opensaml.util.net;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opensaml.util.Pair;
import org.opensaml.util.StringSupport;

/**
 * Utilities for working with HTTP-related data.
 */
public class HttpSupport {

    /** Constructor. */
    protected HttpSupport() {
    }

    /**
     * URL Decode the given string.
     * 
     * @param value the string to decode
     * @return the decoded string
     */
    public static String urlDecode(String value) {
        if (value == null) {
            return null;
        }

        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // UTF-8 encoding is required to be supported by all JVMs
            return null;
        }
    }

    /**
     * URL Encode the given string.
     * 
     * @param value the string to encode
     * @return the encoded string
     */
    public static String urlEncode(String value) {
        if (value == null) {
            return null;
        }

        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // UTF-8 encoding is required to be supported by all JVMs
            return null;
        }
    }

    /**
     * Get the first raw (i.e.URL-encoded) query string component with the specified parameter name. This method assumes
     * the common query string format of one or more 'paramName=paramValue' pairs separate by '&'.
     * 
     * The component will be returned as a string in the form 'paramName=paramValue' (minus the quotes).
     * 
     * @param queryString the URL encoded HTTP URL query string
     * @param paramName the URL decoded name of the parameter to find
     * @return the found component, or null if query string or param name is null/empty or the parameter is not found
     */
    public static String getRawQueryStringParameter(String queryString, String paramName) {
        String trimmedQuery = StringSupport.trimOrNull(queryString);
        String trimmedName = StringSupport.trimOrNull(paramName);
        if (trimmedQuery == null || trimmedName == null) {
            return null;
        }

        if (trimmedQuery.startsWith("?")) {
            trimmedQuery = trimmedQuery.substring(1);
        }

        if (trimmedQuery.endsWith("#")) {
            trimmedQuery = trimmedQuery.substring(0, trimmedQuery.length() - 1);
        }

        String encodedName = HttpSupport.urlEncode(trimmedName);

        int index = queryString.indexOf(encodedName);
        while (index != -1) {
            // check if this is an valueless parameter at the end of the query string
            if (index + encodedName.length() == queryString.length()) {
                return encodedName;
            }

            // check if the next character after the name is an = or if we've just found another parameter
            // name that happens to start with the name we're looking for
            if (queryString.charAt(index++) == '=') {
                return queryString.substring(index - encodedName.length() - 1, queryString.indexOf("&", index));
            }
        }

        return null;
    }

    /**
     * Parses a URL-encoded query string in to a set of name/value pairs. This method assumes the common query string
     * format of one or more 'paramName=paramValue' pairs separate by '&'. Both parameter names and values will be URL
     * decoded. Parameters without values will be represented in the returned map as a key associated with the value
     * <code>null</code>.
     * 
     * @param queryString URL encoded query string
     * 
     * @return the parameters from the query string, never null
     */
    public static List<Pair<String, String>> parseQueryString(String queryString) {
        String trimmedQuery = StringSupport.trimOrNull(queryString);
        if (trimmedQuery == null) {
            return Collections.emptyList();
        }

        if (trimmedQuery.startsWith("?")) {
            trimmedQuery = trimmedQuery.substring(1);
        }

        if (trimmedQuery.endsWith("#")) {
            trimmedQuery = trimmedQuery.substring(0, trimmedQuery.length() - 1);
        }

        ArrayList<Pair<String, String>> queryParams = new ArrayList<Pair<String, String>>();
        String[] paramPairs = trimmedQuery.split("&");
        String[] param;
        for (String paramPair : paramPairs) {
            param = paramPair.split("=");
            if (param.length == 1) {
                queryParams.add(new Pair(HttpSupport.urlDecode(param[0]), null));
            } else {
                queryParams.add(new Pair(HttpSupport.urlDecode(param[0]), HttpSupport.urlDecode(param[1])));
            }
        }

        return queryParams;
    }
}