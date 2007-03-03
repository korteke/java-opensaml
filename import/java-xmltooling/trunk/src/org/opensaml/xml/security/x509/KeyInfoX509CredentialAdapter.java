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
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.HashSet;

import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.KeyInfoHelper;

/**
 * An adapter class capable of exposing an {@link KeyInfo} as an {@link X509Credential}.
 */
public class KeyInfoX509CredentialAdapter extends BasicX509Credential implements X509Credential {

    /** Adapted KeyInfo. */
    private KeyInfo keyInfo;

    /**
     * Constructor.
     * 
     * @param info the key information
     * 
     * @throws GeneralSecurityException thrown if the key, certificate, or CRL information is represented in an
     *             unsupported format
     */
    public KeyInfoX509CredentialAdapter(KeyInfo info) throws GeneralSecurityException {
        parseKeyInfo(info);
    }

    /**
     * Gets the adapted key info.
     * 
     * @return adapted key info
     */
    public KeyInfo getKeyInfo() {
        return keyInfo;
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
        entityCertChain = KeyInfoHelper.getCertificates(info);
        publicKeys = KeyInfoHelper.getPublicKeys(info);
        if (entityCertChain != null) {
            for (X509Certificate cert : entityCertChain) {
                if (publicKeys == null) {
                    publicKeys = new HashSet<PublicKey>();
                }
                publicKeys.add(cert.getPublicKey());
            }
        }
        crls = KeyInfoHelper.getCRLs(info);
    }
}