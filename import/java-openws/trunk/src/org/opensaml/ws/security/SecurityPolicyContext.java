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


/**
 * Stores state that is maintained by {@link SecurityPolicy} instances, and which is
 * used in the evaluation of {@link SecurityPolicyRule}'s.
 * 
 * @param <IssuerType> the message issuer type
 */
public class SecurityPolicyContext<IssuerType> {
    
    /** Message issuer, as determined by security policy rules. */
    private IssuerType issuer;
  
    
    /**
     * Get the issuer as determined by the security policy evaluation.
     * 
     * @return the issuer of the message
     */
    public IssuerType getIssuer() {
        return issuer;
    }
    
    /**
     * Set the issuer as determined by the security policy evaluation.
     * 
     * @param newIssuer the new issuer value to store
     */
    public void setIssuer(IssuerType newIssuer) {
        issuer = newIssuer;
    }

}
