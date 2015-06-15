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

import java.security.Principal;
import java.security.cert.Certificate;
import java.util.List;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import javax.security.cert.X509Certificate;

/**
 *  Mock impl of {@link SSLSession} used in testing hostname verifiers and SSL socket factories.
 */
public class MockSSLSession implements SSLSession {
    
    private List<Certificate> peerCertificates;
    private String peerHost;
    
    public MockSSLSession(List<Certificate> certs, String host) {
        this.peerCertificates = certs;
        this.peerHost = host;
    }
    
    public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
        return peerCertificates.toArray(new Certificate[0]);
    }

    public String getPeerHost() {
        return peerHost;
    }

    
    
    // Methods below here are just unimplemented stubs    

    public int getApplicationBufferSize() {
        return 0;
    }

    public String getCipherSuite() {
        return null;
    }

    public long getCreationTime() {
        return 0;
    }

    public byte[] getId() {
        return null;
    }

    public long getLastAccessedTime() {
        return 0;
    }

    public Certificate[] getLocalCertificates() {
        return null;
    }

    public Principal getLocalPrincipal() {
        return null;
    }

    public int getPacketBufferSize() {
        return 0;
    }

    public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
        return null;
    }

    public int getPeerPort() {
        return 0;
    }

    public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
        return null;
    }

    public String getProtocol() {
        return null;
    }

    public SSLSessionContext getSessionContext() {
        return null;
    }

    public Object getValue(String name) {
        return null;
    }

    public String[] getValueNames() {
        return null;
    }

    public void invalidate() {
    }

    public boolean isValid() {
        return false;
    }

    public void putValue(String name, Object value) {
    }

    public void removeValue(String name) {
    }
    
}
