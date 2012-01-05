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

package org.opensaml.xml.security.criteria;

import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.resolver.Criterion;

import com.google.common.base.Strings;

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
    public KeyNameCriterion(String name) {
        setKeyName(name);
    }

    /**
     * Get the key name criteria.
     * 
     * @return Returns the keyName.
     */
    public String getKeyName() {
        return keyName;
    }

    /**
     * Set the key name criteria.
     * 
     * @param name The keyName to set.
     */
    public void setKeyName(String name) {
        if (Strings.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("Key name criteria value must be supplied");
        }
        keyName = StringSupport.trimOrNull(name);
    }

}
