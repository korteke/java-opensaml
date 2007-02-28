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

import org.opensaml.xml.signature.Signature;

/**
 * An adapter class capable of exposing {@link Signature}s as an {@link X509Credential}.
 */
public class XMLSignatureX509CredentialAdapter extends KeyInfoX509CredentialAdapter {

    /** The signature. */
    private Signature signature;

    /**
     * Constructor.
     * 
     * @param entity the entity that issued the signature
     * @param sig the siganture
     * 
     * @throws IllegalArgumentException throw if either the entity is null or empty, if the signature or its key info is
     *             null
     * @throws GeneralSecurityException thrown if the key, certificate, or CRL information is represented in an
     *             unsupported format
     */
    public XMLSignatureX509CredentialAdapter(String entity, Signature sig) throws IllegalArgumentException,
            GeneralSecurityException {
        setEntityId(entity);

        if (sig == null) {
            throw new IllegalArgumentException("Signature may not be null");
        }
        signature = sig;
        parseKeyInfo(sig.getKeyInfo());
    }

    /**
     * Gets the signature being adapted.
     * 
     * @return signature being adapted
     */
    public Signature getSignature() {
        return signature;
    }
}