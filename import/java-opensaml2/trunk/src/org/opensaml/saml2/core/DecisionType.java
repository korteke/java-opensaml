/*
 * Copyright [2005] [University Corporation for Advanced Internet Development, Inc.]
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

package org.opensaml.saml2.core;

/**
 * A type safe enumeration of {@link org.opensaml.saml2.core.AuthzDecisionStatement} decision types.
 */
public final class DecisionType {

    /** Permit decision type */
    public final static DecisionType PERMIT = new DecisionType("Permit");

    /** Deny decision type */
    public final static DecisionType DENY = new DecisionType("Deny");

    /** Indeterminate decision type */
    public final static DecisionType INDETERMINATE = new DecisionType("Indeterminate");

    /** The decision type string */
    private String decisionType;

    /**
     * Constructor
     * 
     * @param decisionType the decision type string
     */
    public DecisionType(String decisionType) {
        this.decisionType = decisionType;
    }

    /*
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return decisionType;
    }
}