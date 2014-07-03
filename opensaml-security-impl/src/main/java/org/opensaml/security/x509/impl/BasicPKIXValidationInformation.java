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

package org.opensaml.security.x509.impl;

import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.security.x509.PKIXValidationInformation;

/**
 * Basic implementation of {@link PKIXValidationInformation}.
 */
public class BasicPKIXValidationInformation implements PKIXValidationInformation {

    /** Certs used as the trust anchors. */
    private final Collection<X509Certificate> trustAnchors;

    /** CRLs used during validation. */
    private final Collection<X509CRL> trustedCRLs;

    /** Max verification depth during PKIX validation. */
    private final Integer verificationDepth;

    /**
     * Constructor.
     * 
     * @param anchors certs used as trust anchors during validation
     * @param crls CRLs used during validation
     * @param depth max verification path depth
     */
    public BasicPKIXValidationInformation(@Nullable final Collection<X509Certificate> anchors,
            @Nullable final Collection<X509CRL> crls, @Nullable final Integer depth) {

        if (null == depth) {
            verificationDepth = 1; 
        } else {
            verificationDepth = depth;
        }
        trustAnchors = anchors;
        trustedCRLs = crls;
    }

    /** {@inheritDoc} */
    @Override
    @Nullable public Collection<X509CRL> getCRLs() {
        return trustedCRLs;
    }

    /** {@inheritDoc} */
    @Override
    @Nullable public Collection<X509Certificate> getCertificates() {
        return trustAnchors;
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull public Integer getVerificationDepth() {
        return verificationDepth;
    }
}