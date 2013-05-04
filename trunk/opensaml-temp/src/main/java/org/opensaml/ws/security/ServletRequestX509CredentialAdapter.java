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

package org.opensaml.ws.security;

import java.security.PublicKey;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletRequest;

import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.AbstractCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.x509.X509Credential;

/**
 * An adapter that exposes the X.509 certificates contained in the servlet request attribute.
 */
public class ServletRequestX509CredentialAdapter extends AbstractCredential implements X509Credential {

    /** Servlet request attribute to pull certificate info from. */
    public static final String X509_CERT_REQUEST_ATTRIBUTE = "javax.servlet.request.X509Certificate";
    
    /** The entity certificate. */
    private X509Certificate cert;
    
    /** The certificate chain. */
    private List<X509Certificate> certChain;

    /**
     * Constructor.
     *
     * @param request the servlet request
     * 
     * @throws SecurityException if request does not contain an X.509 client certificate in 
     *  request attribute 'javax.servlet.request.X509Certificate'
     */
    public ServletRequestX509CredentialAdapter(ServletRequest request) throws SecurityException {
        X509Certificate[] chain = (X509Certificate[]) request.getAttribute(X509_CERT_REQUEST_ATTRIBUTE);
        if (chain == null || chain.length == 0) {
            throw new SecurityException("Servlet request does not contain X.509 certificates in attribute "
                    + X509_CERT_REQUEST_ATTRIBUTE);
        }

        cert = chain[0];
        certChain = Arrays.asList(chain);
        setUsageType(UsageType.SIGNING);
    }

    /** {@inheritDoc} */
    public Class<? extends Credential> getCredentialType() {
        return X509Credential.class;
    }

    /** {@inheritDoc} */
    public X509Certificate getEntityCertificate() {
        return cert;
    }

    /** {@inheritDoc} */
    public Collection<X509Certificate> getEntityCertificateChain() {
        return certChain;
    }

    /** {@inheritDoc} */
    public Collection<X509CRL> getCRLs() {
        return null;
    }

    /** {@inheritDoc} */
    public PublicKey getPublicKey() {
        return getEntityCertificate().getPublicKey();
    }
    
}