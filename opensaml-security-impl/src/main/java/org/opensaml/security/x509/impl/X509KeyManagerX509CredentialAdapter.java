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

package org.opensaml.security.x509.impl;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.net.ssl.X509KeyManager;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.security.credential.AbstractCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.x509.X509Credential;

/** A class that wraps a {@link X509KeyManager} and exposes it as an {@link X509Credential}. */
public class X509KeyManagerX509CredentialAdapter extends AbstractCredential implements X509Credential {

    /** Alias used to reference the credential in the key manager. */
    private String credentialAlias;

    /** Wrapped key manager. */
    private X509KeyManager keyManager;

    /**
     * Constructor.
     * 
     * @param manager wrapped key manager
     * @param alias alias used to reference the credential in the key manager
     */
    public X509KeyManagerX509CredentialAdapter(X509KeyManager manager, String alias) {
        keyManager = Constraint.isNotNull(manager, "Key manager may not be null");
        credentialAlias = Constraint.isNotNull(StringSupport.trimOrNull(alias), "Entity alias may not be null");
    }

    /** {@inheritDoc} */
    public Collection<X509CRL> getCRLs() {
        return Collections.EMPTY_LIST;
    }

    /** {@inheritDoc} */
    public X509Certificate getEntityCertificate() {
        X509Certificate[] certs = keyManager.getCertificateChain(credentialAlias);
        if (certs != null && certs.length > 0) {
            return certs[0];
        }

        return null;
    }

    /** {@inheritDoc} */
    public Collection<X509Certificate> getEntityCertificateChain() {
        X509Certificate[] certs = keyManager.getCertificateChain(credentialAlias);
        if (certs != null && certs.length > 0) {
            return Arrays.asList(certs);
        }

        return null;
    }

    /** {@inheritDoc} */
    public PrivateKey getPrivateKey() {
        return keyManager.getPrivateKey(credentialAlias);
    }

    /** {@inheritDoc} */
    public PublicKey getPublicKey() {
        return getEntityCertificate().getPublicKey();
    }

    /** {@inheritDoc} */
    public Class<? extends Credential> getCredentialType() {
        return X509Credential.class;
    }

    /** {@inheritDoc} */
    public void setEntityId(String newEntityID) {
        super.setEntityId(newEntityID);
    }

    /** {@inheritDoc} */
    public void setUsageType(UsageType newUsageType) {
        super.setUsageType(newUsageType);
    }
    
}