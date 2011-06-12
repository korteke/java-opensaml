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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.jcip.annotations.NotThreadSafe;

import org.opensaml.util.Assert;
import org.opensaml.util.Pair;
import org.opensaml.util.StringSupport;

/**
 * Representation of an HTTP(S) URL. This class exists because {@link java.net.URI} and {@link java.net.URL} do not
 * allow for modification of URL components.
 * 
 * In addition, this classes stores and outputs URLs in canonical form. This means that scheme and host information are
 * all lowercased, the port only appears if it is different from the normal port for the given scheme (i.e., 80 for HTTP
 * and 443 for HTTPS), the path is case sensitive and always contains at least "/", and query parameters and references
 * are case sensitive. In addition, when converted to a string, via {@link #toString()}, the URL is properly
 * application/x-www-form-urlencoded encoded.
 * 
 * This class accepts non-ASCII characters for the host, path, query, and reference and will properly encode them.
 * 
 * Note, while the query string of a URL does not technically have a defined structure, this class assumes the usual
 * 'name=value' parameter pairs separated by the '&' character.
 */
@NotThreadSafe
public class HttpUrl {

    /** Scheme identifier for HTTP URLs. */
    public static final String HTTP_SCHEME = "http";

    /** Scheme identifier for HTTPS URLs. */
    public static final String HTTPS_SCHEME = "https";

    /** URL schema (http or https). */
    private String scheme;

    /** Host for the URL. */
    private String host;

    /** URL port number. */
    private int port;

    /** URL path. */
    private String path;

    /** Parameters in the query string. */
    private List<Pair<String, String>> queryParams;

    /** URL fragment. */
    private String reference;

    /** Constructor. */
    public HttpUrl() {
        queryParams = new ArrayList<Pair<String, String>>();
    }

    /**
     * Constructor.
     * 
     * @param baseURL fully resolved URL (i.e., has scheme, host, non-relative path) to parse and use as basis for
     *            creating other URLs
     * 
     * @throws IllegalArgumentException thrown if the given base URL is not well formed
     */
    public HttpUrl(String baseURL) {
        String trimmedUrl = StringSupport.trimOrNull(baseURL);
        if (trimmedUrl == null) {
            queryParams = new ArrayList<Pair<String, String>>();
            return;
        }

        try {
            URL url = new URL(trimmedUrl);

            setScheme(url.getProtocol());
            setHost(url.getHost());
            setPort(url.getPort());
            setPath(url.getPath());

            queryParams = HttpSupport.parseQueryString(url.getQuery());

            setReference(url.getRef());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Given URL is not well formed", e);
        }
    }

    /**
     * Gets the URL reference in its decoded form.
     * 
     * @return URL reference in its decoded form, may be null
     */
    public String getReference() {
        return reference;
    }

    /**
     * Sets the URL reference in its decoded form.
     * 
     * @param newReference URL reference in its decoded form
     */
    public void setReference(String newReference) {
        reference = StringSupport.trimOrNull(newReference);
    }

    public void setEncodedReference(String newReference) {
        setReference(HttpSupport.urlDecode(newReference));
    }

    /**
     * Gets the host component of the URL.
     * 
     * @return host component of the URL, never null
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets the host component of the URL.
     * 
     * @param newHost host component of the URL, never null or empty
     */
    public void setHost(String newHost) {
        String trimmedHost = StringSupport.trimOrNull(newHost);
        Assert.isNotNull(trimmedHost, "Host can not be null or empty");
        host = trimmedHost.toLowerCase();
    }

    /**
     * Gets the path component of the URL.
     * 
     * @return path component of the URL, never null
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the URL-decoded path component of the URL.
     * 
     * @param newPath URL-decoded path component of the URL
     */
    public void setPath(String newPath) {
        String trimmedPath = StringSupport.trimOrNull(newPath);
        if (trimmedPath == null) {
            path = "/";
        }

        if (!trimmedPath.startsWith("/")) {
            path = "/" + trimmedPath;
        } else {
            path = trimmedPath;
        }
    }

    public void setEncodedPath(String newPath) {
        setPath(HttpSupport.urlDecode(newPath));
    }

    /**
     * Gets the port component of the URL.
     * 
     * @return port component of the URL
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the port component of the URL.
     * 
     * @param newPort port component of the URL
     */
    public void setPort(int newPort) {
        Assert.isGreaterThan(0, newPort, "Port must be greater than 0");
        Assert.isLessThan(65536, newPort, "Port must be less than 65536");
        port = newPort;
    }

    /**
     * Gets the query string parameters for the URL. Parameters may be added and removed.
     * 
     * Note, if you have a query string, you can use {@link HttpSupport#parseQueryString(String)} to parse it and add
     * the results to this list.
     * 
     * @return query string parameters for the URL, never null
     */
    public List<Pair<String, String>> getQueryParams() {
        return queryParams;
    }

    /**
     * Gets the URL scheme (http, https).
     * 
     * @return URL scheme, never null
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * Sets the URL scheme (http, https).
     * 
     * @param newScheme URL scheme (http, https), never null
     */
    public void setScheme(String newScheme) {
        String trimmedScheme = StringSupport.trimOrNull(newScheme);
        Assert.isNotNull(trimmedScheme, "URL scheme can not be null");

        if (HTTP_SCHEME.equalsIgnoreCase(trimmedScheme)) {
            scheme = HTTP_SCHEME;
        } else if (HTTPS_SCHEME.equalsIgnoreCase(trimmedScheme)) {
            scheme = HTTPS_SCHEME;
        } else {
            throw new IllegalArgumentException("HTTP URL scheme can only be http or https");
        }
    }

    /**
     * Gives the URL in its canonical, URL-encoded, form.
     * 
     * @return URL built from the given data
     */
    public String toString() {
        StringBuilder builder = new StringBuilder(buildSchemeHostPort());
        builder.append(buildPathQueryReference());
        return builder.toString();
    }

    /**
     * Constructs a URL fragment containing the scheme, host, and port.
     * 
     * @return URL fragment containing the scheme, host, and port
     */
    public String buildSchemeHostPort() {
        StringBuilder builder = new StringBuilder();

        if (scheme != null && host != null) {
            builder.append(scheme);
            builder.append("://");
            builder.append(host);

            if (port > 0 && !(HTTP_SCHEME.equals(scheme) && port == 80)
                    && !(HTTPS_SCHEME.equals(scheme) && port == 443)) {
                builder.append(":");
                builder.append(Integer.toString(port));
            }
        }

        return builder.toString();
    }

    /**
     * Constructs a URL fragment containing the path, query, and reference.
     * 
     * @return URL fragment containing the path, query, and reference
     */
    public String buildPathQueryReference() {
        StringBuilder builder = new StringBuilder(path);

        String queryString = buildQueryString();
        if (queryString != null) {
            builder.append("?");
            builder.append(HttpSupport.urlEncode(queryString));
        }

        if (reference != null) {
            builder.append("#");
            builder.append(HttpSupport.urlEncode(reference));
        }

        return builder.toString();
    }

    /**
     * Constructs a URL fragment containing the query.
     * 
     * @return URL fragment containing the query, nor null if not query parameters are specified
     */
    public String buildQueryString() {
        if (queryParams.size() == 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        String name;
        String value;

        Pair<String, String> param;
        for (int i = 0; i < queryParams.size(); i++) {
            param = queryParams.get(i);
            name = StringSupport.trimOrNull(param.getFirst());

            if (name != null) {
                builder.append(HttpSupport.urlEncode(name));
                value = StringSupport.trimOrNull(param.getSecond());
                if (value != null) {
                    builder.append("=");
                    builder.append(HttpSupport.urlEncode(value));
                }
                if (i < queryParams.size() - 1) {
                    builder.append("&");
                }
            }
        }
        return builder.toString();
    }
}