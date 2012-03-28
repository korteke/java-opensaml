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

import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.resolver.Criterion;

import com.google.common.base.Strings;

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
    public KeyAlgorithmCriterion(String algorithm) {
        setKeyAlgorithm(algorithm);
    }
 
    /**
     * Get the key algorithm criteria.
     * 
     * @return returns the keyAlgorithm.
     */
    public String getKeyAlgorithm() {
        return keyAlgorithm;
    }

    /**
     * Set the key algorithm criteria.
     * 
     * @param algorithm The keyAlgorithm to set.
     */
    public void setKeyAlgorithm(String algorithm) {
        if (Strings.isNullOrEmpty(algorithm)) {
            throw new IllegalArgumentException("Key algorithm criteria value must be supplied");
        }
        keyAlgorithm = StringSupport.trimOrNull(algorithm);
    }

}
