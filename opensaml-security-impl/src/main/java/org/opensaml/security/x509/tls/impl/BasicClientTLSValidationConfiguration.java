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

package org.opensaml.security.x509.tls.impl;

import javax.annotation.Nullable;

import org.opensaml.security.trust.TrustEngine;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.security.x509.tls.CertificateNameOptions;
import org.opensaml.security.x509.tls.ClientTLSValidationConfiguration;

/**
 * Basic implementation of {@link ClientTLSValidationConfiguration}.
 */
public class BasicClientTLSValidationConfiguration implements ClientTLSValidationConfiguration {
    
    /** A {@link TrustEngine} instance used to validate a client TLS {@link X509Credential}. **/
    @Nullable private TrustEngine<? super X509Credential> x509TrustEngine;
    
    /** A {@link CertificateNameOptions} instance used to validate a client TLS {@link X509Credential}. **/
    @Nullable private CertificateNameOptions certificateNameOptions;

    /** {@inheritDoc} */
    @Override
    @Nullable public TrustEngine<? super X509Credential> getX509TrustEngine() {
        return x509TrustEngine;
    }

    /**
     * Set a {@link TrustEngine} instance used to validate a client TLS {@link X509Credential}.
     * 
     * @param engine a trust engine instance, may be null
     */
    public void setX509TrustEngine(@Nullable final TrustEngine<? super X509Credential> engine) {
        x509TrustEngine = engine;
    }

    /** {@inheritDoc} */
    @Override
    @Nullable public CertificateNameOptions getCertificateNameOptions() {
        return certificateNameOptions;
    }

    /**
     * Set a {@link CertificateNameOptions} instance to use when evaluating a client TLS {@link X509Credential}.
     * 
     * @param options an options instance, may be null
     */
    public void setCertificateNameOptions(@Nullable final CertificateNameOptions options) {
        certificateNameOptions = options;
    }

}
