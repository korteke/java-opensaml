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

import java.security.PublicKey;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.List;

import org.opensaml.xml.signature.KeyInfo;

/**
 * Resolves information about a key into the referenced key.
 */
public interface X509KeyInfoResolver extends KeyInfoResolver<PublicKey> {

    /**
     * Resolves the X509 certificates within the KeyInfo.
     * 
     * @param keyInfo the keying information
     * 
     * @return X509 certificates within the KeyInfo
     * 
     * @throws CertificateException thrown if the certificate data in the KeyInfo can not be converted into
     *             {@link X509Certificate}s.
     */
    public List<X509Certificate> resolveCertificates(KeyInfo keyInfo) throws CertificateException;

    /**
     * Resolves the X509 certificate revocation lists within the KeyInfo.
     * 
     * @param keyInfo the keying information
     * 
     * @return X509 certificate revocation lists within the KeyInfo
     * 
     * @throws CRLException thrown if the CRL data in the KeyInfo can not be convereted into {@link X509CRL}s.
     */
    public List<X509CRL> resolveCRLS(KeyInfo keyInfo) throws CRLException;
}