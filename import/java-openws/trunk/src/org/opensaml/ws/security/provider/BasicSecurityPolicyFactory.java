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

package org.opensaml.ws.security.provider;

import org.opensaml.ws.security.SecurityPolicy;

/**
 * Factory that produces {@link BasicSecurityPolicy} instances.
 */
public class BasicSecurityPolicyFactory extends BaseSecurityPolicyFactory {

    /** Whether the issuer of the message must be authenticated in order to pass this policy. */
    private boolean requireAuthenticatedIssuer;

    /**
     * Gets whether the issuer of the message must be authenticated in order to pass this policy.
     * 
     * @return whether the issuer of the message must be authenticated in order to pass this policy
     */
    public boolean isRequireAuthenticatedIssuer() {
        return requireAuthenticatedIssuer;
    }

    /**
     * Sets whether the issuer of the message must be authenticated in order to pass this policy.
     * 
     * @param require whether the issuer of the message must be authenticated in order to pass this policy
     */
    public void setRequireAuthenticatedIssuer(boolean require) {
        requireAuthenticatedIssuer = require;
    }

    /** {@inheritDoc} */
    public SecurityPolicy createPolicyInstance() {
        return new BasicSecurityPolicy(requireAuthenticatedIssuer);
    }
}