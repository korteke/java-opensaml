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

package org.opensaml.util.net;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opensaml.util.Pair;
import org.opensaml.util.StringSupport;

/** Helper methods for building {@link URI}s and parsing some HTTP URL information. */
@Deprecated
public final class UriSupport {

    /** Constructor. */
    private UriSupport() {
    }

    /**
     * Sets the fragment of a URI.
     * 
     * @param prototype prototype URI that provides information other than the fragment
     * @param fragment fragment for the new URI
     * 
     * @return new URI built from the prototype URI and the given fragment
     */
    public static URI setFragment(final URI prototype, final String fragment) {
        try {
            return new URI(prototype.getScheme(), prototype.getUserInfo(), prototype.getHost(), prototype.getPort(),
                    prototype.getPath(), prototype.getQuery(), trimOrNullFragment(fragment));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Illegal fragment text", e);
        }
    }

    /**
     * Sets the host of a URI.
     * 
     * @param prototype prototype URI that provides information other than the host
     * @param host host for the new URI
     * 
     * @return new URI built from the prototype URI and the given host
     */
    public static URI setHost(final URI prototype, final String host) {
        try {
            return new URI(prototype.getScheme(), prototype.getUserInfo(), StringSupport.trimOrNull(host),
                    prototype.getPort(), prototype.getPath(), prototype.getQuery(), prototype.getFragment());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Illegal host", e);
        }
    }

    /**
     * Sets the path of a URI.
     * 
     * @param prototype prototype URI that provides information other than the path
     * @param path path for the new URI
     * 
     * @return new URI built from the prototype URI and the given path
     */
    public static URI setPath(final URI prototype, final String path) {
        try {
            return new URI(prototype.getScheme(), prototype.getUserInfo(), prototype.getHost(), prototype.getPort(),
                    trimOrNullPath(path), prototype.getQuery(), prototype.getFragment());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Illegal path", e);
        }
    }

    /**
     * Sets the port of a URI.
     * 
     * @param prototype prototype URI that provides information other than the port
     * @param port port for the new URI
     * 
     * @return new URI built from the prototype URI and the given port
     */
    public static URI setPort(final URI prototype, final int port) {
        try {
            return new URI(prototype.getScheme(), prototype.getUserInfo(), prototype.getHost(), port,
                    prototype.getPath(), prototype.getQuery(), prototype.getFragment());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Illegal port", e);
        }
    }

    /**
     * Sets the query of a URI.
     * 
     * @param prototype prototype URI that provides information other than the query
     * @param query query for the new URI
     * 
     * @return new URI built from the prototype URI and the given query
     */
    public static URI setQuery(final URI prototype, final String query) {
        try {
            return new URI(prototype.getScheme(), prototype.getUserInfo(), prototype.getHost(), prototype.getPort(),
                    prototype.getPath(), trimOrNullQuery(query), prototype.getFragment());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Illegal query", e);
        }
    }

    /**
     * Sets the query of a URI.
     * 
     * @param prototype prototype URI that provides information other than the query
     * @param parameters query parameters for the new URI
     * 
     * @return new URI built from the prototype URI and the given query
     */
    public static URI setQuery(final URI prototype, final List<Pair<String, String>> parameters) {
        try {
            return new URI(prototype.getScheme(), prototype.getUserInfo(), prototype.getHost(), prototype.getPort(),
                    prototype.getPath(), buildQuery(parameters), prototype.getFragment());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Illegal query", e);
        }
    }

    /**
     * Sets the scheme of a URI.
     * 
     * @param prototype prototype URI that provides information other than the scheme
     * @param scheme scheme for the new URI
     * 
     * @return new URI built from the prototype URI and the given scheme
     */
    public static URI setScheme(final URI prototype, final String scheme) {
        try {
            return new URI(StringSupport.trimOrNull(scheme), prototype.getUserInfo(), prototype.getHost(),
                    prototype.getPort(), prototype.getPath(), prototype.getQuery(), prototype.getFragment());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Illegal scheme", e);
        }
    }

    /**
     * Builds an RFC-3968 encoded URL query component from a collection of parameters.
     * 
     * @param parameters collection of parameters from which to build the URL query component, may be null or empty
     * 
     * @return RFC-3968 encoded URL query or null if the parameter collection was null or empty
     */
    public static String buildQuery(final List<Pair<String, String>> parameters) {
        if (parameters == null || parameters.size() == 0) {
            return null;
        }

        final StringBuilder builder = new StringBuilder();
        boolean firstParam = true;
        for (Pair<String, String> parameter : parameters) {
            if (firstParam) {
                firstParam = false;
            } else {
                builder.append("&");
            }

            builder.append(urlEncode(parameter.getFirst()));
            builder.append("=");
            if (parameter.getSecond() != null) {
                builder.append(urlEncode(parameter.getSecond()));
            }
        }

        return builder.toString();
    }

    /**
     * Get the first raw (i.e.RFC-3968 encoded) query string component with the specified parameter name. This method
     * assumes the common query string format of one or more 'paramName=paramValue' pairs separate by '&'.
     * 
     * The component will be returned as a string in the form 'paramName=paramValue' (minus the quotes).
     * 
     * @param queryString the URL encoded HTTP URL query string
     * @param paramName the URL decoded name of the parameter to find
     * @return the found component, or null if query string or param name is null/empty or the parameter is not found
     */
    public static String getRawQueryStringParameter(final String queryString, final String paramName) {
        final String trimmedQuery = trimOrNullQuery(queryString);
        final String trimmedName = StringSupport.trimOrNull(paramName);
        if (trimmedQuery == null || trimmedName == null) {
            return null;
        }

        final String encodedName = urlEncode(trimmedName);

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
     * Parses a RFC-3968 encoded query string in to a set of name/value pairs. This method assumes the common query
     * string format of one or more 'paramName=paramValue' pairs separate by '&'. Both parameter names and values will
     * be URL decoded. Parameters without values will be represented in the returned map as a key associated with the
     * value <code>null</code>.
     * 
     * @param queryString URL encoded query string
     * 
     * @return the parameters from the query string, never null
     */
    public static List<Pair<String, String>> parseQueryString(final String queryString) {
        final String trimmedQuery = trimOrNullQuery(queryString);
        if (trimmedQuery == null) {
            return Collections.emptyList();
        }

        final ArrayList<Pair<String, String>> queryParams = new ArrayList<Pair<String, String>>();
        final String[] paramPairs = trimmedQuery.split("&");
        String[] param;
        for (String paramPair : paramPairs) {
            param = paramPair.split("=");
            if (param.length == 1) {
                queryParams.add(new Pair(urlDecode(param[0]), null));
            } else {
                queryParams.add(new Pair(urlDecode(param[0]), urlDecode(param[1])));
            }
        }

        return queryParams;
    }

    /**
     * Trims an RFC-3968 encoded URL path component. If the given path is null or empty then null is returned. If the
     * given path ends with '?' then it is removed. If the given path ends with '#' then it is removed.
     * 
     * @param path path to trim
     * 
     * @return the trimmed path or null
     */
    public static String trimOrNullPath(final String path) {
        String trimmedPath = StringSupport.trimOrNull(path);
        if (trimmedPath == null) {
            return null;
        }

        if (trimmedPath.startsWith("?")) {
            trimmedPath = trimmedPath.substring(1);
        }

        if (trimmedPath.endsWith("?") || trimmedPath.endsWith("#")) {
            trimmedPath = trimmedPath.substring(0, trimmedPath.length() - 1);
        }

        return trimmedPath;
    }

    /**
     * Trims an RFC-3968 encoded URL query component. If the given query is null or empty then null is returned. If the
     * given query starts with '?' then it is removed. If the given query ends with '#' then it is removed.
     * 
     * @param query query to trim
     * 
     * @return the trimmed query or null
     */
    public static String trimOrNullQuery(final String query) {
        String trimmedQuery = StringSupport.trimOrNull(query);
        if (trimmedQuery == null) {
            return null;
        }

        if (trimmedQuery.startsWith("?")) {
            trimmedQuery = trimmedQuery.substring(1);
        }

        if (trimmedQuery.endsWith("#")) {
            trimmedQuery = trimmedQuery.substring(0, trimmedQuery.length() - 1);
        }

        return trimmedQuery;
    }

    /**
     * Trims an RFC-3968 encoded URL fragment component. If the given fragment is null or empty then null is returned.
     * If the given fragment starts with '#' then it is removed.
     * 
     * @param fragment fragment to trim
     * 
     * @return the trimmed fragment or null
     */
    public static String trimOrNullFragment(final String fragment) {
        String trimmedFragment = StringSupport.trimOrNull(fragment);
        if (trimmedFragment == null) {
            return null;
        }

        if (trimmedFragment.startsWith("#")) {
            trimmedFragment = trimmedFragment.substring(1);
        }

        return trimmedFragment;
    }

    /**
     * URL Decode the given string.
     * 
     * @param value the string to decode
     * @return the decoded string
     */
    public static String urlDecode(final String value) {
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
    public static String urlEncode(final String value) {
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
}