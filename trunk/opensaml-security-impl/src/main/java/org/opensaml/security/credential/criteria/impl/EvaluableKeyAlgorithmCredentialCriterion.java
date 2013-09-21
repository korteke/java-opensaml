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

package org.opensaml.security.credential.criteria.impl;

import java.security.Key;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.security.credential.Credential;
import org.opensaml.security.criteria.KeyAlgorithmCriterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Instance of evaluable credential criteria for evaluating the credential key algorithm.
 */
public class EvaluableKeyAlgorithmCredentialCriterion implements EvaluableCredentialCriterion {

    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(EvaluableKeyAlgorithmCredentialCriterion.class);

    /** Base criteria. */
    private final String keyAlgorithm;

    /**
     * Constructor.
     * 
     * @param criteria the criteria which is the basis for evaluation
     */
    public EvaluableKeyAlgorithmCredentialCriterion(@Nonnull final KeyAlgorithmCriterion criteria) {
        keyAlgorithm = Constraint.isNotNull(criteria, "Criterion instance cannot be null").getKeyAlgorithm();
    }

    /**
     * Constructor.
     * 
     * @param newKeyAlgorithm the criteria value which is the basis for evaluation
     */
    public EvaluableKeyAlgorithmCredentialCriterion(@Nonnull final String newKeyAlgorithm) {
        String trimmed = StringSupport.trimOrNull(newKeyAlgorithm);
        Constraint.isNotNull(trimmed, "Key algorithm cannot be null or empty");

        keyAlgorithm = trimmed;
    }

    /** {@inheritDoc} */
    @Nullable public Boolean evaluate(@Nullable final Credential target) {
        if (target == null) {
            log.error("Credential target was null");
            return null;
        }
        
        Key key = getKey(target);
        if (key == null) {
            log.info("Could not evaluate criteria, credential contained no key");
            return null;
        }
        
        String algorithm = StringSupport.trimOrNull(key.getAlgorithm());
        if (algorithm == null) {
            log.info("Could not evaluate criteria, key does not specify an algorithm via getAlgorithm()");
            return null;
        }

        return keyAlgorithm.equals(algorithm);
    }

    /**
     * Get the key contained within the credential.
     * 
     * @param credential the credential containing a key
     * @return the key from the credential
     */
    @Nullable private Key getKey(@Nonnull final Credential credential) {
        if (credential.getPublicKey() != null) {
            return credential.getPublicKey();
        } else if (credential.getSecretKey() != null) {
            return credential.getSecretKey();
        } else if (credential.getPrivateKey() != null) {
            // There should have been a corresponding public key, but just in case...
            return credential.getPrivateKey();
        } else {
            return null;
        }
    }
    
    /** {@inheritDoc} */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EvaluableKeyAlgorithmCredentialCriterion [keyAlgorithm=");
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

        if (obj instanceof EvaluableKeyAlgorithmCredentialCriterion) {
            return keyAlgorithm.equals(((EvaluableKeyAlgorithmCredentialCriterion) obj).keyAlgorithm);
        }

        return false;
    }

}