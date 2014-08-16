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

package org.opensaml.security.x509;

import javax.annotation.Nullable;

import org.opensaml.security.messaging.CertificateNameOptions;
import org.opensaml.security.trust.TrustEngine;

/**
 * Parameters used in validating an {@link X509Credential}.
 */
public class X509CredentialValidationParameters {
    
    /** A {@link TrustEngine} instance used to validate an {@link X509Credential}. **/
    @Nullable private TrustEngine<X509Credential> x509TrustEngine;
    
    /** A {@link CertificateNameOptions} instance used to validate an {@link X509Credential}. **/
    @Nullable private CertificateNameOptions certificateNameOptions;

    /**
     * Get a {@link TrustEngine} instance used to validate an {@link X509Credential}.
     * 
     * @return a trust engine instance, may be null
     */
    @Nullable public TrustEngine<X509Credential> getX509TrustEngine() {
        return x509TrustEngine;
    }

    /**
     * Set a {@link TrustEngine} instance used to validate an {@link X509Credential}.
     * 
     * @param engine a trust engine instance, may be null
     */
    public void setX509TrustEngine(@Nullable final TrustEngine<X509Credential> engine) {
        x509TrustEngine = engine;
    }

    /**
     * Get a {@link CertificateNameOptions} instance to use when evaluating an {@link X509Credential}.
     * 
     * @return an options instance, may be null
     */
    @Nullable public CertificateNameOptions getCertificateNameOptions() {
        return certificateNameOptions;
    }

    /**
     * Set a {@link CertificateNameOptions} instance to use when evaluating an {@link X509Credential}.
     * 
     * @param options an options instance, may be null
     */
    public void setCertificateNameOptions(@Nullable final CertificateNameOptions options) {
        certificateNameOptions = options;
    }

}
