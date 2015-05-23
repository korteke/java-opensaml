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

package org.opensaml.security.httpclient.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.apache.http.HttpHost;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;
import org.opensaml.security.httpclient.HttpClientSecurityConstants;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.security.x509.tls.impl.ThreadLocalX509CredentialContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A wrapper for instances of {@link LayeredConnectionSocketFactory} which loads and clears
 * a thread-local instance of {@link X509Credential} used for client TLS.
 * 
 * <p>
 * Typically used with {@link org.opensaml.security.x509.tls.impl.ThreadLocalX509CredentialKeyManager}.
 * </p>
 * 
 */
public class ThreadLocalClientTLSSocketFactory implements LayeredConnectionSocketFactory {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(ThreadLocalClientTLSSocketFactory.class);
    
    /** The HttpClient socket factory instance wrapped by this implementation. */
    @Nonnull private LayeredConnectionSocketFactory wrappedFactory;
    
    /**
     * Constructor.
     *
     * @param factory the wrapped socket factory
     */
    public ThreadLocalClientTLSSocketFactory(@Nonnull final LayeredConnectionSocketFactory factory) {
        wrappedFactory = Constraint.isNotNull(factory, "Socket factory was null");
    }
    
    /** {@inheritDoc} */
    public Socket createSocket(HttpContext context) throws IOException {
        log.trace("In createSocket");
        return wrappedFactory.createSocket(context);
    }
    
    /** {@inheritDoc} */
    public Socket connectSocket(int connectTimeout, @Nullable Socket socket, @Nonnull HttpHost host,
            @Nonnull InetSocketAddress remoteAddress, @Nullable InetSocketAddress localAddress,
            @Nullable HttpContext context) throws IOException {
        log.trace("In connectSocket");
        try {
            setup(context);
            return wrappedFactory.connectSocket(connectTimeout, socket, host, remoteAddress, localAddress, context);
        } finally {
            teardown(context);
        }
    }

    /** {@inheritDoc} */
    public Socket createLayeredSocket(@Nonnull Socket socket, @Nonnull String target, int port,
            @Nullable HttpContext context) throws IOException {
        log.trace("In createLayeredSocket");
        try {
            setup(context);
            return wrappedFactory.createLayeredSocket(socket, target, port, context);
        } finally {
            teardown(context);
        }
    }
    
    /**
     * Load the {@link ThreadLocalX509CredentialContext} with the client TLS credential obtained from 
     * the {@link HttpContext}.
     * 
     * @param context the HttpContext instance
     */
    protected void setup(@Nullable final HttpContext context) {
        log.trace("Attempting to setup thread-local client TLS X509Credential");
        if (context == null) {
            log.trace("HttpContext was null, skipping thread-local setup");
            return;
        }
        if (!ThreadLocalX509CredentialContext.haveCurrent()) {
            X509Credential credential = 
                    (X509Credential) context.getAttribute(
                            HttpClientSecurityConstants.CONTEXT_KEY_CLIENT_TLS_CREDENTIAL);
            if (credential != null) {
                log.trace("Loading ThreadLocalX509CredentialContext with client TLS credential: {}", credential);
                ThreadLocalX509CredentialContext.loadCurrent(credential);
            } else {
                log.trace("HttpContext did not contain a client TLS credential, nothing to do");
            }
        } else {
            log.trace("ThreadLocalX509CredentialContext was already loaded with client TLS credential, skipping setup");
        }
    }
    
    /**
     * Clear the {@link ThreadLocalX509CredentialContext} of the client TLS credential obtained from 
     * the {@link HttpContext}.
     * 
     * @param context the HttpContext instance
     */
    protected void teardown(@Nullable final HttpContext context) {
        log.trace("Clearing thread-local client TLS X509Credential");
        ThreadLocalX509CredentialContext.clearCurrent();
    }
    
}
