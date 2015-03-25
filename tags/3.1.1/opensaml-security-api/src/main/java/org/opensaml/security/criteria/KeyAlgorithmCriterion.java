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

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.resolver.Criterion;


/**
 * An implementation of {@link Criterion} which specifies key algorithm criteria.
 */
public final class KeyAlgorithmCriterion implements Criterion {
    
    /** Key algorithm type of resolved credentials. */
    private String keyAlgorithm;
    
    /**
     * Constructor.
     *
     * @param algorithm key algorithm
     */
    public KeyAlgorithmCriterion(@Nonnull final String algorithm) {
        setKeyAlgorithm(algorithm);
    }
 
    /**
     * Get the key algorithm criteria.
     * 
     * @return returns the keyAlgorithm.
     */
    @Nonnull public String getKeyAlgorithm() {
        return keyAlgorithm;
    }

    /**
     * Set the key algorithm criteria.
     * 
     * @param algorithm The keyAlgorithm to set.
     */
    public void setKeyAlgorithm(@Nonnull final String algorithm) {
        String trimmed = StringSupport.trimOrNull(algorithm);
        Constraint.isNotNull(trimmed, "Key algorithm criteria cannot be null or empty");

        keyAlgorithm = trimmed;
    }
    
    /** {@inheritDoc} */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("KeyAlgorithmCriterion [keyAlgorithm=");
        builder.append(keyAlgorithm);
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return keyAlgorithm.hashCode();
    }

    /** {@inheritDoc} */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof KeyAlgorithmCriterion) {
            return keyAlgorithm.equals(((KeyAlgorithmCriterion) obj).keyAlgorithm);
        }

        return false;
    }

}