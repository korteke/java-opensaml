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

package org.opensaml.xml.security.x509;

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import javolution.util.FastSet;

import org.apache.log4j.Logger;
import org.opensaml.xml.security.credential.AbstractCredentialResolver;
import org.opensaml.xml.security.credential.CredentialCriteria;
import org.opensaml.xml.security.credential.CredentialResolver;
import org.opensaml.xml.security.credential.UsageType;

/**
 * A {@link CredentialResolver} that extracts {@link X509Credential} from a key store.
 * 
 * If no key usage type is presented at construction time this resolver will return the key, if available, regardless of
 * the usage type provided to its resolve method.
 */
public class KeyStoreCredentialResolver extends AbstractCredentialResolver<X509Credential> 
    implements CredentialResolver<X509Credential> {

    /** Class logger. */
    private static Logger log = Logger.getLogger(KeyStoreCredentialResolver.class);

    /** Key store credentials are retrieved from. */
    private KeyStore keyStore;

    /** Passwords for keys. The key must be the entity ID, the value the password. */
    private Map<String, String> keyPasswords;

    /** Usage type of all keys in the store. */
    private UsageType keyUsage;

    /**
     * Constructor.
     * 
     * @param store key store credentials are retrieved from
     * @param passwords for key entries, map key is the entity id, map value is the password
     * 
     * @throws IllegalArgumentException thrown if the given keystore is null
     */
    public KeyStoreCredentialResolver(KeyStore store, Map<String, String> passwords) throws IllegalArgumentException {
        this(store, passwords, null);
    }

    /**
     * Constructor.
     * 
     * @param store key store credentials are retrieved from
     * @param passwords for key entries, map key is the entity id, map value is the password
     * @param usage usage type of all keys in the store
     * 
     * @throws IllegalArgumentException thrown if the given keystore is null
     */
    public KeyStoreCredentialResolver(KeyStore store, Map<String, String> passwords, UsageType usage)
            throws IllegalArgumentException {
        if (store == null) {
            throw new IllegalArgumentException("Provided key store may not be null.");
        }

        try {
            store.size();
        } catch (KeyStoreException e) {
            throw new IllegalArgumentException("Keystore has not been initialized.");
        }

        keyStore = store;

        keyUsage = usage;
    }

    /** {@inheritDoc} */
    public Iterable<X509Credential> resolveCredentials(CredentialCriteria criteria) {
        UsageType usage = criteria.getUsage();
        String entity = criteria.getEntityID();
        if (keyUsage != null && keyUsage != usage) {
            return null;
        }

        KeyStore.PasswordProtection keyPassword = null;

        if (keyPasswords.containsKey(entity)) {
            keyPassword = new KeyStore.PasswordProtection(keyPasswords.get(entity).toCharArray());
        }

        BasicX509Credential credential = null;
        try {
            KeyStore.Entry keyEntry = keyStore.getEntry(entity, keyPassword);
            credential = new BasicX509Credential();
            credential.setEntityId(entity);

            if (keyEntry instanceof KeyStore.PrivateKeyEntry) {
                KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyEntry;
                credential.setPrivateKey(privateKeyEntry.getPrivateKey());

                credential.setPublicKey( privateKeyEntry.getCertificate().getPublicKey() );

                credential.setEntityCertificate((X509Certificate) privateKeyEntry.getCertificate());
                credential.setEntityCertificateChain(Arrays.asList((X509Certificate[]) privateKeyEntry
                        .getCertificateChain()));
            } else if (keyEntry instanceof KeyStore.TrustedCertificateEntry) {
                KeyStore.TrustedCertificateEntry trustedCertEntry = (KeyStore.TrustedCertificateEntry) keyEntry;
                X509Certificate cert = (X509Certificate) trustedCertEntry.getTrustedCertificate();

                credential.setPublicKey(cert.getPublicKey());

                credential.setEntityCertificate(cert);

                ArrayList<X509Certificate> certChain = new ArrayList<X509Certificate>();
                certChain.add(cert);
                credential.setEntityCertificateChain(certChain);
            }
        } catch (GeneralSecurityException e) {
            log.error("Unable to retrieve key for entity " + entity, e);
        }
        
        Set<X509Credential> credentials = new FastSet<X509Credential>();
        credentials.add(credential);
        return credentials;
    }
}