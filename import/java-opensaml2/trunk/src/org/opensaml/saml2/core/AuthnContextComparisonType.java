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
 * A type safe enumeration of {@link org.opensaml.saml2.core.RequestedAuthnContext} comparison types.
 */
public final class AuthnContextComparisonType {

    /** exact comparison type */
    public final static AuthnContextComparisonType EXACT = new AuthnContextComparisonType("exact");

    /** minimum comparison type */
    public final static AuthnContextComparisonType MINIMUM = new AuthnContextComparisonType("minimum");

    /** maximum comparison type */
    public final static AuthnContextComparisonType MAXIMUM = new AuthnContextComparisonType("maximum");

    /** better comparison type */
    public final static AuthnContextComparisonType BETTER = new AuthnContextComparisonType("better");

    /** The decision type string */
    private String comparisonType;

    /**
     * Constructor
     * 
     * @param comparisonType the decision type string
     */
    private AuthnContextComparisonType(String comparisonType) {
        this.comparisonType= comparisonType;
    }

    /*
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return comparisonType;
    }
}