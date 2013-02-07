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

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Strings;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.logic.Constraint;

/**
 * This class performs simple canonicalization of a URL as follows:
 * 
 * <p>
 * <ul>
 *   <li>The scheme is lower-cased.</li>
 *   <li>The hostname is lower-cased</li>
 *   <li>The port is removed if it is the default port registered for the scheme</li>
 * </ul>
 * </p>
 * 
 * <p>
 * </p>
 */
public final class SimpleUrlCanonicalizer {
    
    /** The scheme-to-port mapping data. */
    private static Map<String, Integer> schemePortMap = new HashMap<String, Integer>();
    
    /** Constructor to prevent instantiation.  */
    private SimpleUrlCanonicalizer() {}

    /**
     * Register a new scheme-to-port mapping.
     * 
     * @param scheme the scheme to register
     * @param port the default port for that scheme
     */
    public static void registerSchemePortMapping(@Nonnull final String scheme, @Nonnull final Integer port) {
        if (scheme == null || port == null) {
            throw new IllegalArgumentException("Scheme and port may not be null");
        }
        schemePortMap.put(scheme.toLowerCase(), port);
    }
    
    /**
     * Deregister a scheme-to-port mapping.
     * 
     * @param scheme the scheme to deregister
     */
    public static void deregisterSchemePortMapping(@Nonnull final String scheme) {
        if (scheme == null) {
            throw new IllegalArgumentException("Scheme may not be null");
        }
        schemePortMap.remove(scheme.toLowerCase());
    }
    
    /**
     * Obtain the default port registered for a scheme.
     * 
     * @param scheme the scheme to look up
     * @return the default port registered for the scheme, or null if none registered
     */
    @Nullable public static Integer getRegisteredPort(@Nonnull final String scheme) {
        return schemePortMap.get(scheme.toLowerCase());
    }
    
    /**
     * Canonicalize the supplied URL.
     * 
     * @param url the URL to canonicalize
     * @return the canonicalized URL
     * @throws MalformedURLException  if the URL is not a valid URL
     */
    @Nonnull @NotEmpty public static String canonicalize(@Nonnull @NotEmpty String url) throws MalformedURLException {
        Constraint.isFalse(Strings.isNullOrEmpty(url), "URL was null or empty");
        UrlBuilder urlBuilder = new UrlBuilder(url);
        canonicalize(urlBuilder);
        return urlBuilder.buildURL();
    }
    
    /**
     * Canonicalize the supplied URLBuilder data.
     * 
     * @param url the URLBuilder to canonicalize
     */
    private static void canonicalize(@Nonnull final UrlBuilder url) {
        if (url.getScheme() != null) {
            url.setScheme(url.getScheme().toLowerCase());
            
            String scheme = url.getScheme();
            Integer port = getRegisteredPort(scheme);
            if (port != null && port == url.getPort()) {
                url.setPort(0);
            }
        }
        
        if (url.getHost() != null) {
            url.setHost(url.getHost().toLowerCase());
        }
    }
    
    static {
        registerSchemePortMapping("ftp", 23);
        registerSchemePortMapping("http", 80);
        registerSchemePortMapping("https", 443);
    }

}
