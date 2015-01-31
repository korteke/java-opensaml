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
import java.security.cert.Certificate;
import java.util.List;

import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

/**
 * Mock SSLSocket impl.  Only implement the methods we care about.
 */
public class MockSSLSocket extends SSLSocket {
    
    private SSLSession session;
    
    public MockSSLSocket(List<Certificate> certs, String host) {
       session = new MockSSLSession(certs, host);
    }

    /** {@inheritDoc} */
    public SSLSession getSession() {
        return session;
    }
    
    
    // Methods below here are just unimplemented stubs    

    
    /** {@inheritDoc} */
    public void addHandshakeCompletedListener(HandshakeCompletedListener listener) {
        // TODO Auto-generated method stub
        
    }

    /** {@inheritDoc} */
    public boolean getEnableSessionCreation() {
        // TODO Auto-generated method stub
        return false;
    }

    /** {@inheritDoc} */
    public String[] getEnabledCipherSuites() {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    public String[] getEnabledProtocols() {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    public boolean getNeedClientAuth() {
        // TODO Auto-generated method stub
        return false;
    }

    /** {@inheritDoc} */
    public String[] getSupportedCipherSuites() {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    public String[] getSupportedProtocols() {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    public boolean getUseClientMode() {
        // TODO Auto-generated method stub
        return false;
    }

    /** {@inheritDoc} */
    public boolean getWantClientAuth() {
        // TODO Auto-generated method stub
        return false;
    }

    /** {@inheritDoc} */
    public void removeHandshakeCompletedListener(HandshakeCompletedListener listener) {
        // TODO Auto-generated method stub
        
    }

    /** {@inheritDoc} */
    public void setEnableSessionCreation(boolean flag) {
        // TODO Auto-generated method stub
        
    }

    /** {@inheritDoc} */
    public void setEnabledCipherSuites(String[] suites) {
        // TODO Auto-generated method stub
        
    }

    /** {@inheritDoc} */
    public void setEnabledProtocols(String[] protocols) {
        // TODO Auto-generated method stub
        
    }

    /** {@inheritDoc} */
    public void setNeedClientAuth(boolean need) {
        // TODO Auto-generated method stub
        
    }

    /** {@inheritDoc} */
    public void setUseClientMode(boolean mode) {
        // TODO Auto-generated method stub
        
    }

    /** {@inheritDoc} */
    public void setWantClientAuth(boolean want) {
        // TODO Auto-generated method stub
        
    }

    /** {@inheritDoc} */
    public void startHandshake() throws IOException {
        // TODO Auto-generated method stub
        
    }
    

}
