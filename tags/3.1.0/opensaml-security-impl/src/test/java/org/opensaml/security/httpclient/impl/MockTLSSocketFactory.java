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
import java.net.UnknownHostException;
import java.security.cert.Certificate;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;

/**
 * Mock HttpClient SSL/TLS socket factory.
 */
public class MockTLSSocketFactory implements LayeredConnectionSocketFactory {
    
    private Socket socket;
    
    public MockTLSSocketFactory() {
        socket = new Socket();
    }
    
    public MockTLSSocketFactory(List<Certificate> certs, String host) {
       socket = new MockSSLSocket(certs, host); 
    }

    /** {@inheritDoc} */
    public Socket createSocket(HttpContext context) throws IOException {
        return this.socket;
    }

    /** {@inheritDoc} */
    public Socket connectSocket(int connectTimeout, Socket sock, HttpHost host, InetSocketAddress remoteAddress,
            InetSocketAddress localAddress, HttpContext context) throws IOException {
        return this.socket;
    }

    /** {@inheritDoc} */
    public Socket createLayeredSocket(Socket socket, String target, int port, HttpContext context) throws IOException,
            UnknownHostException {
        return this.socket;
    }

}
