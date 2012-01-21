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

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.util.StringSupport;

/** Utilities for working with HTTP Servlet requests and responses. */
@Deprecated
public final class HttpServletSupport {

    /** Constructor. */
    private HttpServletSupport() {
    }

    /**
     * Adds Cache-Control and Pragma headers meant to disable caching.
     * 
     * @param response transport to add headers to
     */
    public static void addNoCacheHeaders(HttpServletResponse response) {
        response.setHeader("Cache-control", "no-cache, no-store");
        response.setHeader("Pragma", "no-cache");
    }

    /**
     * Sets the character encoding of the transport to UTF-8.
     * 
     * @param response transport to set character encoding type
     */
    public static void setUTF8Encoding(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
    }

    /**
     * Sets the MIME content type of the response.
     * 
     * @param response the transport to set content type on
     * @param contentType the content type to set
     */
    public static void setContentType(HttpServletResponse response, String contentType) {
        response.setHeader("Content-Type", contentType);
    }

    /**
     * Gets the request URI as returned by {@link HttpServletRequest#getRequestURI()} but without the servlet context
     * path.
     * 
     * @param request request to get the URI from
     * 
     * @return constructed URI
     */
    public static String getRequestPathWithoutContext(HttpServletRequest request) {
        String servletPath = request.getServletPath();

        if (request.getPathInfo() == null) {
            return servletPath;
        } else {
            return servletPath + request.getPathInfo();
        }
    }

    /**
     * Gets the URL that was requested to generate this request. This includes the scheme, host, port, path, and query
     * string.
     * 
     * @param request current request
     * 
     * @return URL that was requested to generate this request
     */
    public static URI getFullRequestUri(final HttpServletRequest request) {
        StringBuffer requestUrl = request.getRequestURL();

        String encodedQuery = StringSupport.trimOrNull(request.getQueryString());
        if (encodedQuery != null) {
            requestUrl.append("?").append(encodedQuery);
        }

        return URI.create(requestUrl.toString());
    }
}