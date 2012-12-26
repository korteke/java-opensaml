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

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.resolver.Criterion;

/**
 * An implementation of {@link Criterion} which specifies criteria based on
 * the digest of an X.509 certificate.
 */
public final class X509DigestCriterion implements Criterion {
    
    /** Digest algorithm. */
    private String algorithm;
    
    /** X.509 certificate digest. */
    private byte[] x509digest;
    
    /**
     * Constructor.
     *
     * @param alg algorithm of digest computation
     * @param digest certificate digest
     */
    public X509DigestCriterion(@Nonnull final String alg, @Nonnull final byte[] digest) {
        setAlgorithm(alg);
        setDigest(digest);
    }

    /**
     * Get the digest algorithm.
     * 
     * @return the digest algorithm
     */
    @Nonnull public String getAlgorithm() {
        return algorithm;
    }

    /**
     * Set the digest algorithm.
     * 
     * @param alg the digest algorithm to set
     */
    public void setAlgorithm(@Nonnull final String alg) {
        String trimmed = StringSupport.trimOrNull(alg);
        Constraint.isNotNull(trimmed, "Certificate digest algorithm cannot be null or empty");

        algorithm = trimmed;
    }
    
    /**
     * Get the certificate digest.
     * 
     * @return the digest
     */
    @Nonnull public byte[] getDigest() {
        return x509digest;
    }

    /**
     * Set the certificate digest.
     * 
     * @param digest the certificate digest to set
     */
    public void setDigest(@Nonnull final byte[] digest) {
        if (digest == null || digest.length == 0) {
            throw new IllegalArgumentException("Certificate digest criteria value cannot be null or empty");
        }
        x509digest = digest;
    }

}