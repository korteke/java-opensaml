/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
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

package org.opensaml.xml.security;

import java.security.KeyException;
import java.security.PublicKey;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.List;

import org.opensaml.xml.signature.KeyInfo;

/**
 * A resolver that always returns a specified set of keying information.
 */
public class DirectKeyInfoResolver implements X509KeyInfoResolver {

    /** Keys names to resolve into. */
    private List<String> keyNames;

    /** Public keys to resolve into. */
    private List<PublicKey> keys;

    /** X509 certificated to resolve into. */
    private List<X509Certificate> certs;

    /** X509 CRLs to resolve into. */
    private List<X509CRL> crls;

    /**
     * Constructor.
     * 
     * @param newNames key names to returing during a resolve
     * @param newKeys public keys to return during a resolve
     * @param newCerts certificates to return during a resolve
     * @param newCRLs CRLs to return during a resolve
     */
    public DirectKeyInfoResolver(List<String> newNames, List<PublicKey> newKeys, List<X509Certificate> newCerts,
            List<X509CRL> newCRLs) {
        keyNames = newNames;
        keys = newKeys;
        certs = newCerts;
        crls = newCRLs;
    }

    /** {@inheritDoc} */
    public List<String> resolveKeyNames(KeyInfo keyInfo) {
        return keyNames;
    }

    /** {@inheritDoc} */
    public List<X509CRL> resolveCRLS(KeyInfo keyInfo) throws CRLException {
        return crls;
    }

    /** {@inheritDoc} */
    public List<X509Certificate> resolveCertificates(KeyInfo keyInfo) throws CertificateException {
        return certs;
    }

    /** {@inheritDoc} */
    public PublicKey resolveKey(KeyInfo keyInfo) throws KeyException {
        if (keys != null && keys.size() > 0) {
            return keys.get(0);
        }

        return null;
    }

    /** {@inheritDoc} */
    public List<PublicKey> resolveKeys(KeyInfo keyInfo) throws KeyException {
        return keys;
    }
}