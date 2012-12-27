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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.security.credential.Credential;
import org.opensaml.security.criteria.KeyNameCriterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Instance of evaluable credential criteria for evaluating credential key names.
 */
public class EvaluableKeyNameCredentialCriterion implements EvaluableCredentialCriterion {
    
    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(EvaluableKeyNameCredentialCriterion.class);
    
    /** Base criteria. */
    private final String keyName;
    
    /**
     * Constructor.
     *
     * @param criteria the criteria which is the basis for evaluation
     */
    public EvaluableKeyNameCredentialCriterion(@Nonnull final KeyNameCriterion criteria) {
        keyName = Constraint.isNotNull(criteria, "Criterion instance cannot be null").getKeyName();
    }
    
    /**
     * Constructor.
     *
     * @param newKeyName the criteria value which is the basis for evaluation
     */
    public EvaluableKeyNameCredentialCriterion(@Nonnull final String newKeyName) {
        String trimmed = StringSupport.trimOrNull(newKeyName);
        Constraint.isNotNull(trimmed, "Key name cannot be null or empty");

        keyName = trimmed;
    }

    /** {@inheritDoc} */
    @Nullable public Boolean evaluate(@Nullable final Credential target) {
        if (target == null) {
            log.error("Credential target was null");
            return null;
        }
        if (target.getKeyNames().isEmpty()) {
            log.info("Could not evaluate criteria, credential contained no key names");
            return null;
        }
        return target.getKeyNames().contains(keyName);
    }

}