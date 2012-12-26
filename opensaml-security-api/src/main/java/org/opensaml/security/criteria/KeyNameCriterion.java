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
 * An implementation of {@link Criterion} which specifies key name criteria.
 */
public final class KeyNameCriterion implements Criterion {

    /** Key name of resolved credentials.  */
    private String keyName;
    
    /**
     * Constructor.
     *
     * @param name key name
     */
    public KeyNameCriterion(@Nonnull final String name) {
        setKeyName(name);
    }

    /**
     * Get the key name criteria.
     * 
     * @return Returns the keyName.
     */
    @Nonnull public String getKeyName() {
        return keyName;
    }

    /**
     * Set the key name criteria.
     * 
     * @param name The keyName to set.
     */
    public void setKeyName(@Nonnull final String name) {
        String trimmed = StringSupport.trimOrNull(name);
        Constraint.isNotNull(trimmed, "Key name criteria value cannot be null or empty");

        keyName = trimmed;
    }

}