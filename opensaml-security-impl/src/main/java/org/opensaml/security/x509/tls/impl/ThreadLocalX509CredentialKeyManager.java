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

package org.opensaml.security.x509.tls.impl;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509KeyManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of {@link X509KeyManager} which returns data based on the thread-local credential 
 * instance obtained via {@link ThreadLocalX509CredentialContext}.
 */
public class ThreadLocalX509CredentialKeyManager implements X509KeyManager {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(ThreadLocalX509CredentialKeyManager.class);
    
    /** The alias representing the supplied static credential. */
    private String internalAlias = "internalAlias-ThreadLocal";

    /** {@inheritDoc} */
    public String chooseClientAlias(String[] arg0, Principal[] arg1, Socket arg2) {
        log.trace("In chooseClientAlias");
        return internalAlias;
    }
    
    /** {@inheritDoc} */
    public String[] getClientAliases(String arg0, Principal[] arg1) {
        log.trace("In getClientAliases");
        return new String[] {internalAlias};
    }

    /** {@inheritDoc} */
    public X509Certificate[] getCertificateChain(String arg0) {
        log.trace("In getCertificateChain");
        return internalAlias.equals(arg0) && ThreadLocalX509CredentialContext.haveCurrent() 
                ? ThreadLocalX509CredentialContext.getCredential().getEntityCertificateChain()
                        .toArray(new X509Certificate[0]) : null;
    }

    /** {@inheritDoc} */
    public PrivateKey getPrivateKey(String arg0) {
        log.trace("In getPrivateKey");
        return internalAlias.equals(arg0) && ThreadLocalX509CredentialContext.haveCurrent() 
                ? ThreadLocalX509CredentialContext.getCredential().getPrivateKey() : null;
    }

    /** {@inheritDoc} */
    public String chooseServerAlias(String arg0, Principal[] arg1, Socket arg2) {
        log.trace("In chooseServerAlias");
        return internalAlias;
    }

    /** {@inheritDoc} */
    public String[] getServerAliases(String arg0, Principal[] arg1) {
        log.trace("In getServerAliases");
        return new String[] {internalAlias};
    }

}
