/*
 * Copyright [2007] [University Corporation for Advanced Internet Development, Inc.]
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

package org.opensaml.ws.transport.http;

/**
 * Utilities for working with HTTP transports.
 */
public class HTTPTransportUtils {
    
    /** Constructor. */
    protected HTTPTransportUtils() {}

    /**
     * Adds Cache-Control and Pragma headers meant to disable caching.
     * 
     * @param transport transport to add headers to
     */
    public static void addNoCacheHeaders(HTTPOutTransport transport) {
        transport.setHeader("Cache-control", "no-cache, no-store");
        transport.setHeader("Pragma", "no-cache");
    }

    /**
     * Sets the character encoding of the transport to UTF-8.
     * 
     * @param transport transport to set character encoding type
     */
    public static void setUTF8Encoding(HTTPOutTransport transport) {
        transport.setCharacterEncoding("UTF-8");
    }

    /**
     * Sets the MIME content type of the transport.
     * 
     * @param transport the transport to set content type on
     * @param contentType the content type to set
     */
    public static void setContentType(HTTPOutTransport transport, String contentType) {
        transport.setHeader("Content-Type", contentType);
    }
}