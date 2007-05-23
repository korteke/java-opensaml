/*
 * Copyright [2007] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xml.security.credential;

import org.opensaml.xml.util.DatatypeHelper;

/**
 * An implementation of {@link CredentialCriteria} which specifies criteria pertaining 
 * to characteristics of a key to be resolved.
 */
public final class KeyConstraintCredentialCriteria implements CredentialCriteria {
    
    /** Key algorithm type of resolved credentials. */
    private String keyAlgorithm;
    
    /** Key length of resolved credentials. */
    private Integer keyLength;
    
    /**
     * Constructor.
     *
     * @param algorithm key algorithm
     * @param length key length 
     */
    public KeyConstraintCredentialCriteria(String algorithm, Integer length) {
        setKeyAlgorithm(algorithm);
        setKeyLength(length);
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
        keyAlgorithm = DatatypeHelper.safeTrimOrNullString(algorithm);
    }

    /**
     * Get the key length.
     * 
     * @return Returns the keyLength.
     */
    public Integer getKeyLength() {
        return keyLength;
    }

    /**
     * Set the key length.
     * 
     * @param length The keyLength to set.
     */
    public void setKeyLength(Integer length) {
        keyLength = length;
    }

}
