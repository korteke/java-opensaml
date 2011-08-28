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

package org.opensaml.saml.criterion;

import org.opensaml.util.Assert;
import org.opensaml.util.StringSupport;
import org.opensaml.util.criteria.Criterion;

/** {@link Criterion} representing an entity ID. */
public final class EntityIdCriterion implements Criterion {

    /** The entity ID. */
    private final String id;

    /**
     * Constructor.
     * 
     * @param entityId the entity ID, can not be null or empty
     */
    public EntityIdCriterion(final String entityId) {
        id = StringSupport.trimOrNull(entityId);
        Assert.isNotNull(entityId, "Entity ID can not be null or empty");
    }

    /**
     * Gets the entity ID.
     * 
     * @return the entity ID, never null or empty
     */
    public String getEntityId() {
        return id;
    }
}