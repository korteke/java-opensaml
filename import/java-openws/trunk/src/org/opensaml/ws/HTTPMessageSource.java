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

package org.opensaml.ws;

import java.util.List;

import javax.servlet.http.Cookie;

import org.opensaml.ws.util.URLBuilder;

/**
 * A source of messages delivered by HTTP.
 * 
 * This message source is synchronous.
 */
public interface HTTPMessageSource extends MessageSource {
    
    /** HTTP version identifier. */
    public static enum HTTP_VERSION{ HTTP1_1, HTTP1_0};
    
    /**
     * Gets the HTTP version used to receive the message.
     * 
     * @return HTTP version used to receive the message
     */
    public HTTP_VERSION getVersion();
    
    /**
     * Gets the status code of the request.
     * 
     * @return status code of the request
     */
    public int getStatusCode();
    
    /**
     * Gets the list of HTTP header names.
     * 
     * @return HTTP header names
     */
    public List<String> getHeaderNames();
    
    /**
     * Gets the first value of the header with the given name.
     * 
     * @param name header name
     * 
     * @return first value of the header with the given name, or null
     */
    public String getHeaderValue(String name);
    
    /**
     * Gets all the values of the header with the given name.
     * 
     * @param name header name
     * 
     * @return header values or null
     */
    public List<String> getHeaderValues(String name);
    
    /**
     * Gets information about the URL from which the message was received.
     * 
     * @return  information about the URL from which the message was received
     */
    public URLBuilder getURLInformation();
    
    /**
     * Gets the cookies received with the message.
     * 
     * @return cookies received with the message
     */
    public List<Cookie> getCookies();
}