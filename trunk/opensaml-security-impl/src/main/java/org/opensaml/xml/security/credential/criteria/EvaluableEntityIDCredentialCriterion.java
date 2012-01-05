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

package org.opensaml.xml.security.credential.criteria;

import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.criteria.EntityIDCriterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * Instance of evaluable credential criteria for evaluating a credential's entity ID.
 */
public class EvaluableEntityIDCredentialCriterion implements EvaluableCredentialCriterion {

    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(EvaluableEntityIDCredentialCriterion.class);

    /** Base criteria. */
    private String entityID;

    /**
     * Constructor.
     * 
     * @param criteria the criteria which is the basis for evaluation
     */
    public EvaluableEntityIDCredentialCriterion(EntityIDCriterion criteria) {
        if (criteria == null) {
            throw new NullPointerException("Criterion instance may not be null");
        }
        entityID = criteria.getEntityID();
    }

    /**
     * Constructor.
     * 
     * @param newEntityID the criteria value which is the basis for evaluation
     */
    public EvaluableEntityIDCredentialCriterion(String newEntityID) {
        if (Strings.isNullOrEmpty(newEntityID)) {
            throw new IllegalArgumentException("Entity ID may not be null");
        }
        entityID = newEntityID;
    }

    /** {@inheritDoc} */
    public Boolean evaluate(Credential target) {
        if (target == null) {
            log.error("Credential target was null");
            return null;
        }
        if (Strings.isNullOrEmpty(target.getEntityId())) {
            log.info("Could not evaluate criteria, credential contained no entity ID");
            return null;
        }
        Boolean result = entityID.equals(target.getEntityId());
        return result;
    }

}
