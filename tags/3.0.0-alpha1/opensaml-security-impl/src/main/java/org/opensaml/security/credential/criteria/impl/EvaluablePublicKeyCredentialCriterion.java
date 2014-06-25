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

import java.security.PublicKey;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.AbstractTriStatePredicate;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.security.credential.Credential;
import org.opensaml.security.criteria.PublicKeyCriterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Instance of evaluable credential criteria for evaluating whether a credential contains a particular
 * public key.
 */
public class EvaluablePublicKeyCredentialCriterion extends AbstractTriStatePredicate<Credential> 
        implements EvaluableCredentialCriterion {
    
    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(EvaluablePublicKeyCredentialCriterion.class);
    
    /** Base criteria. */
    private final PublicKey publicKey;
    
    /**
     * Constructor.
     *
     * @param criteria the criteria which is the basis for evaluation
     */
    public EvaluablePublicKeyCredentialCriterion(@Nonnull final PublicKeyCriterion criteria) {
        publicKey = Constraint.isNotNull(criteria, "Criterion instance cannot be null").getPublicKey();
    }
    
    /**
     * Constructor.
     *
     * @param newPublicKey the criteria value which is the basis for evaluation
     */
    public EvaluablePublicKeyCredentialCriterion(@Nonnull final PublicKey newPublicKey) {
        publicKey = Constraint.isNotNull(newPublicKey, "Public key cannot be null");
    }

    /** {@inheritDoc} */
    @Nullable public boolean apply(@Nullable final Credential target) {
        if (target == null) {
            log.error("Credential target was null");
            return isNullInputSatisfies();
        }
        
        PublicKey key = target.getPublicKey();
        if (key == null) {
            log.info("Credential contained no public key, does not satisfy public key criteria");
            return false;
        }
        
        return publicKey.equals(key);
    }
    
    /** {@inheritDoc} */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EvaluablePublicKeyCredentialCriterion [publicKey=");
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

        if (obj instanceof EvaluablePublicKeyCredentialCriterion) {
            return publicKey.equals(((EvaluablePublicKeyCredentialCriterion) obj).publicKey);
        }

        return false;
    }
}