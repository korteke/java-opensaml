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

import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Instance of evaluable credential criteria for evaluating whether a credential contains a particular usage specifier.
 */
public class EvaluableUsageCredentialCriterion implements EvaluableCredentialCriterion {

    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(EvaluableUsageCredentialCriterion.class);

    /** Base criteria. */
    private final UsageType usage;

    /**
     * Constructor.
     * 
     * @param criteria the criteria which is the basis for evaluation
     */
    public EvaluableUsageCredentialCriterion(@Nonnull final UsageCriterion criteria) {
        usage = Constraint.isNotNull(criteria, "Criterion instance cannot be null").getUsage();
    }

    /**
     * Constructor.
     * 
     * @param newUsage the criteria value which is the basis for evaluation
     */
    public EvaluableUsageCredentialCriterion(@Nonnull final UsageType newUsage) {
        usage = Constraint.isNotNull(newUsage, "Usage cannot be null");
    }

    /** {@inheritDoc} */
    @Nullable public Boolean evaluate(@Nullable final Credential target) {
        if (target == null) {
            log.error("Credential target was null");
            return null;
        }
        
        UsageType credUsage = target.getUsageType();
        if (credUsage == null) {
            log.info("Could not evaluate criteria, credential contained no usage specifier");
            return null;
        }

        return matchUsage(credUsage, usage);
    }

    /**
     * Match usage enum type values from credential and criteria.
     * 
     * @param credentialUsage the usage value from the credential
     * @param criteriaUsage the usage value from the criteria
     * @return true if the two usage specifiers match for purposes of resolving credentials, false otherwise
     */
    protected boolean matchUsage(@Nonnull final UsageType credentialUsage, @Nonnull final UsageType criteriaUsage) {
        if (credentialUsage == UsageType.UNSPECIFIED || criteriaUsage == UsageType.UNSPECIFIED) {
            return true;
        }
        return credentialUsage == criteriaUsage;
    }

}