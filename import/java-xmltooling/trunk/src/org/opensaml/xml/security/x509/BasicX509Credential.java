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

import java.security.PublicKey;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.opensaml.xml.security.credential.BasicCredential;

/**
 * A basic implementation of {@link X509Credential}.
 */
public class BasicX509Credential extends BasicCredential implements X509Credential {

    /** Entity certificate. */
    protected X509Certificate entityCert;

    /** Entity certificate chain, must include entity certificate. */
    protected Collection<X509Certificate> entityCertChain;

    /** CRLs for this credential. */
    protected Collection<X509CRL> crls;

    /** {@inheritDoc} */
    public Collection<PublicKey> getPublicKeys() {
        HashSet<PublicKey> keys = new HashSet<PublicKey>(super.getPublicKeys());
        Collection<X509Certificate> certChain = getEntityCertificateChain();
        if (certChain != null) {
            for (X509Certificate cert : certChain) {
                keys.add(cert.getPublicKey());
            }
        }
        return keys;
    }

    /** {@inheritDoc} */
    public Collection<X509CRL> getCRLs() {
        return crls;
    }

    /**
     * Sets the CRLs for this credential.
     * 
     * @param newCRLs CRLs for this credential
     */
    public void setCRLs(Collection<X509CRL> newCRLs) {
        crls = newCRLs;
    }

    /** {@inheritDoc} */
    public X509Certificate getEntityCertificate() {
        return entityCert;
    }

    /**
     * Sets the entity certificate for this credential.
     * 
     * @param cert entity certificate for this credential
     */
    public void setEntityCertificate(X509Certificate cert) {
        entityCert = cert;
    }

    /** {@inheritDoc} */
    public Collection<X509Certificate> getEntityCertificateChain() {
        if (entityCertChain == null && entityCert != null) {
            HashSet<X509Certificate> constructedChain = new HashSet<X509Certificate>();
            constructedChain.add(entityCert);
            return constructedChain;
        }

        return entityCertChain;
    }

    /**
     * Sets the entity certificate chain for this credential. This <strong>MUST</strong> include the entity
     * certificate.
     * 
     * @param certs ntity certificate chain for this credential
     */
    public void setEntityCertificateChain(Collection<X509Certificate> certs) {
        entityCertChain = new ArrayList<X509Certificate>(certs);
    }
}