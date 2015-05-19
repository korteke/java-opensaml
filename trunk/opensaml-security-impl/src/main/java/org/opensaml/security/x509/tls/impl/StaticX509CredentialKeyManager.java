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
import java.util.Collection;

import javax.net.ssl.X509KeyManager;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.security.x509.X509Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of {@link X509KeyManager} based on a single statically configured
 * private key and certificate chain, supplied either directly or via an instance of 
 * {@link X509Credential}.
 */
public class StaticX509CredentialKeyManager implements X509KeyManager {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(StaticX509CredentialKeyManager.class);
    
    /** The private key instance. */
    private PrivateKey privateKey;
    
    /** The certificate chain instance. */
    private java.security.cert.X509Certificate[] certificateChain;
    
    /** The alias representing the supplied static credential. */
    private String internalAlias = "internalAlias-" + this.toString();

    /**
     * Constructor.
     *
     * @param credential the static credential managed by this key manager
     */
    public StaticX509CredentialKeyManager(X509Credential credential) {
        super();
        Constraint.isNotNull(credential, "Credential may not be null");
        privateKey = Constraint.isNotNull(credential.getPrivateKey(), 
                "Credential PrivateKey may not be null");
        certificateChain = Constraint.isNotNull(credential.getEntityCertificateChain(), 
                "Credential certificate chain may not be null")
                .toArray(new X509Certificate[0]);
        log.trace("Generated static internal alias was: {}", internalAlias);
    }

    /**
     * Constructor.
     *
     * @param key the private key managed by this key manager
     * @param chain the certificate chain managed by this key manager
     */
    public StaticX509CredentialKeyManager(PrivateKey key, Collection<X509Certificate> chain) {
        super();
        privateKey = Constraint.isNotNull(key, "PrivateKey may not be null");
        certificateChain = Constraint.isNotNull(chain, 
                "Certificate chain may not be null").toArray(new X509Certificate[0]);
        log.trace("Generated static internal alias was: {}", internalAlias);
    }

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
    public java.security.cert.X509Certificate[] getCertificateChain( String arg0) {
        log.trace("In getCertificateChain");
        return internalAlias.equals(arg0) ? certificateChain : null;
    }

    /** {@inheritDoc} */
    public PrivateKey getPrivateKey(String arg0) {
        log.trace("In getPrivateKey");
        return internalAlias.equals(arg0) ? privateKey : null;
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