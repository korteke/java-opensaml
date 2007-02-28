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

package org.opensaml.ws.security;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.security.x509.X509Credential;
import org.opensaml.xml.util.DatatypeHelper;

/**
 * An adapter that exposes the X.509 certificates contained in the HTTP request attribute as an
 * {@link X509Credentiall}.
 */
public class HttpRequestX509CredentialAdapter implements X509Credential {

    /** HTTP header to pull certificate info from. */
    public static final String X509_CERT_HTTP_HEADER = "javax.servlet.request.X509Certificate";

    /** ID of the peer. */
    private String entityId;

    /** Certificate chain for the entity, entity certificate is first element. */
    private List<X509Certificate> certChain;

    /**
     * Constructor.
     * 
     * @param entity entity that issued the request
     * @param httpRequest the HTTP request
     * 
     * @throws IllegalArgumentException thrown if the entity is null or empty or the request does not contain any
     *             certificates
     */
    public HttpRequestX509CredentialAdapter(String entity, HttpServletRequest httpRequest)
            throws IllegalArgumentException {
        if (DatatypeHelper.isEmpty(entity)) {
            throw new IllegalArgumentException("Entity ID may not be null or empty");
        }

        X509Certificate[] chain = (X509Certificate[]) httpRequest.getAttribute(X509_CERT_HTTP_HEADER);
        if (chain == null | chain.length == 0) {
            throw new IllegalArgumentException("HTTP Request does not contain X.509 certificates in header "
                    + X509_CERT_HTTP_HEADER);
        }

        entityId = DatatypeHelper.safeTrim(entity);
        certChain = Arrays.asList(chain);
    }

    /** {@inheritDoc} */
    public Collection<X509CRL> getCRLs() {
        return null;
    }

    /** {@inheritDoc} */
    public X509Certificate getEntityCertificate() {
        return certChain.get(0);
    }

    /** {@inheritDoc} */
    public Collection<X509Certificate> getEntityCertificateChain() {
        return certChain;
    }

    /** {@inheritDoc} */
    public String getEntityId() {
        return entityId;
    }

    /** {@inheritDoc} */
    public Collection<String> getKeyNames() {
        return null;
    }

    /** {@inheritDoc} */
    public PrivateKey getPrivateKey() {
        return null;
    }

    /** {@inheritDoc} */
    public Collection<PublicKey> getPublicKeys() {
        return null;
    }

    /** {@inheritDoc} */
    public UsageType getUsageType() {
        return UsageType.SIGNING;
    }
}