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

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.security.credential.AbstractCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.x509.X509Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A wrapper that changes a {@link KeyStore} in to a {@link X509Credential}. */
public class KeyStoreX509CredentialAdapter extends AbstractCredential implements X509Credential {

    /** Class logger. */
    private Logger log = LoggerFactory.getLogger(KeyStoreX509CredentialAdapter.class);

    /** Keystore that contains the credential to be exposed. */
    private final KeyStore keyStore;

    /** Alias to the credential to be exposed. */
    private final String credentialAlias;

    /** Password for the key to be exposed. */
    private final char[] keyPassword;

    /**
     * Constructor.
     * 
     * @param store store containing key to be exposed
     * @param alias alias to the credential to be exposed
     * @param password password to the key to be exposed
     */
    public KeyStoreX509CredentialAdapter(@Nonnull final KeyStore store, @Nonnull final String alias,
            @Nullable final char[] password) {
        keyStore = Constraint.isNotNull(store, "Keystore cannot be null");
        credentialAlias = Constraint.isNotNull(StringSupport.trimOrNull(alias),
                "Keystore alias cannot be null or empty");
        keyPassword = password;
    }

    /** {@inheritDoc} */
    @Nullable public Collection<X509CRL> getCRLs() {
        return Collections.EMPTY_LIST;
    }

    /** {@inheritDoc} */
    @Nonnull public X509Certificate getEntityCertificate() {
        try {
            return (X509Certificate) keyStore.getCertificate(credentialAlias);
        } catch (final KeyStoreException e) {
            log.error("Error accessing {} certificates in keystore", credentialAlias, e);
            return null;
        }
    }

    /** {@inheritDoc} */
    @Nonnull public Collection<X509Certificate> getEntityCertificateChain() {
        List<X509Certificate> certsCollection = Collections.EMPTY_LIST;

        try {
            final Certificate[] certs = keyStore.getCertificateChain(credentialAlias);
            if (certs != null) {
                certsCollection = new ArrayList<>(certs.length);
                for (final Certificate cert : certs) {
                    certsCollection.add((X509Certificate) cert);
                }
            }
        } catch (final KeyStoreException e) {
            log.error("Error accessing {} certificates in keystore", credentialAlias, e);
        }
        return certsCollection;
    }

    /** {@inheritDoc} */
    @Nullable public PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) keyStore.getKey(credentialAlias, keyPassword);
        } catch (final KeyStoreException|UnrecoverableKeyException|NoSuchAlgorithmException e) {
            log.error("Error accessing {} private key in keystore", credentialAlias, e);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Nullable public PublicKey getPublicKey() {
        return getEntityCertificate().getPublicKey();
    }

    /** {@inheritDoc} */
    @Nonnull public Class<? extends Credential> getCredentialType() {
        return X509Credential.class;
    }

    /** {@inheritDoc} */
    public void setEntityId(@Nullable final String newEntityID) {
        super.setEntityId(newEntityID);
    }

    /** {@inheritDoc} */
    public void setUsageType(@Nonnull final UsageType newUsageType) {
        super.setUsageType(newUsageType);
    }

}