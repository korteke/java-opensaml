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

package org.opensaml.ws.security;

import org.opensaml.xml.util.DatatypeHelper;

/**
 * Stores state that is maintained by {@link SecurityPolicy} instances, and which is used in the evaluation of
 * {@link SecurityPolicyRule}'s.
 */
public class SecurityPolicyContext {

    /** Message issuer, as determined by security policy rules. */
    private String issuer;

    /** Whether the issuer was authenticated. */
    private Boolean issuerAuthenticated;

    /**
     * Get the issuer as determined by the security policy evaluation.
     * 
     * @return the issuer of the message
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * Set the issuer as determined by the security policy evaluation.
     * 
     * @param newIssuer the new issuer value to store
     */
    public void setIssuer(String newIssuer) {
        issuer = DatatypeHelper.safeTrimOrNullString(newIssuer);
    }

    /**
     * Gets whether the message issuer was authenticated.
     * 
     * @return {@link Boolean#TRUE} if the issuer was authenticated, {@link Boolean#FALSE} if the issuer failed
     *         authentication, or null if no authentication was attempted
     */
    public Boolean isIssuerAuthenticated() {
        return issuerAuthenticated;
    }

    /**
     * Sets whether the message issuer was authenticated.
     * 
     * @param authenticated <code>true</code> if the issuer was authenticated, <code>false</code> if the issuer
     *            failed authentication
     */
    public void setIssuerAuthenticated(boolean authenticated) {
        issuerAuthenticated = authenticated;
    }
}