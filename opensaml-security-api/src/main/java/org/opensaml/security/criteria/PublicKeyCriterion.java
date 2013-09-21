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

package org.opensaml.security.criteria;

import java.security.PublicKey;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.Criterion;

/**
 * An implementation of {@link Criterion} which specifies public key criteria.
 */
public final class PublicKeyCriterion implements Criterion {

    /** Specifier of public key associated with resolved credentials. */
    private PublicKey publicKey;
    
    /**
     * Constructor.
     *
     * @param pubKey public key
     */
    public PublicKeyCriterion(@Nonnull final PublicKey pubKey) {
        setPublicKey(pubKey);
    }
    
    /**
     * Get the public key criteria.
     * 
     * @return Returns the publicKey.
     */
    @Nonnull public PublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * Set the public key criteria. 
     * 
     * @param key The publicKey to set.
     */
    public void setPublicKey(@Nonnull final PublicKey key) {
        Constraint.isNotNull(key, "Public key criteria value cannot be null");

        publicKey = key;
    }
    
    /** {@inheritDoc} */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PublicKeyCriterion [publicKey=");
        builder.append(publicKey);
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return publicKey.hashCode();
    }

    /** {@inheritDoc} */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof PublicKeyCriterion) {
            return publicKey.equals(((PublicKeyCriterion) obj).publicKey);
        }

        return false;
    }

}