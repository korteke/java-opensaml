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
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collection;

import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.KeyInfoHelper;
import org.opensaml.xml.util.DatatypeHelper;

/**
 * An adapter class capable of exposing an {@link KeyInfo} as an {@link X509Credential}.
 */
public class KeyInfoX509CredentialAdapter implements X509Credential {

    /** ID of the entity that issued the signature. */
    private String entityId;

    /** The adapted key info. */
    private KeyInfo keyInfo;

    /** Key names in the signature. */
    private Collection<String> keyNames;

    /** Public keys in the signature. */
    private Collection<PublicKey> publicKeys;

    /** X509 certificated in the signature. */
    private Collection<X509Certificate> certs;

    /** X509 CRLs in the signature. */
    private Collection<X509CRL> crls;

    /**
     * Constructor.
     * 
     * @param entity entity that the key information applies to
     * @param info the key information
     * 
     * @throws IllegalArgumentException thrown if the entity id or key info is empty or null
     * @throws GeneralSecurityException thrown if the key, certificate, or CRL information is represented in an
     *             unsupported format
     */
    public KeyInfoX509CredentialAdapter(String entity, KeyInfo info) throws IllegalArgumentException,
            GeneralSecurityException {
        setEntityId(entity);
        parseKeyInfo(info);
    }

    /** Constructor. */
    protected KeyInfoX509CredentialAdapter() {

    }

    /**
     * Gets the adapted key info.
     * 
     * @return adapted key info
     */
    public KeyInfo getKeyInfo() {
        return keyInfo;
    }

    /** {@inheritDoc} */
    public Collection<X509CRL> getCRLs() {
        return crls;
    }

    /** {@inheritDoc} */
    public X509Certificate getEntityCertificate() {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    public Collection<X509Certificate> getEntityCertificateChain() {
        return certs;
    }

    /** {@inheritDoc} */
    public String getEntityId() {
        return entityId;
    }

    /** {@inheritDoc} */
    public Collection<String> getKeyNames() {
        return keyNames;
    }

    /** {@inheritDoc} */
    public PrivateKey getPrivateKey() {
        return null;
    }

    /** {@inheritDoc} */
    public Collection<PublicKey> getPublicKeys() {
        return publicKeys;
    }

    /** {@inheritDoc} */
    public UsageType getUsageType() {
        return UsageType.SIGNING;
    }

    /**
     * Sets the entity ID.
     * 
     * @param entity id of the entity the key info describes
     * 
     * @throws IllegalArgumentException thrown if the given entity ID is null or empty
     */
    protected void setEntityId(String entity) throws IllegalArgumentException {
        if (DatatypeHelper.isEmpty(entity)) {
            throw new IllegalArgumentException("Entity ID may not be null or empty");
        }

        entityId = DatatypeHelper.safeTrim(entity);
    }

    /**
     * Parses the key info into credential data.
     * 
     * @param info key info to parse
     * 
     * @throws GeneralSecurityException thrown if the key, certificate, or CRL information is represented in an
     *             unsupported format
     */
    protected void parseKeyInfo(KeyInfo info) throws GeneralSecurityException {
        if (info == null) {
            throw new IllegalArgumentException("Key Info may not be null");
        }

        keyNames = KeyInfoHelper.getKeyNames(info);
        publicKeys = KeyInfoHelper.getPublicKeys(info);
        certs = KeyInfoHelper.getCertificates(info);
        crls = KeyInfoHelper.getCRLs(info);
    }
}