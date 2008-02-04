/*
 * Copyright 2008 Members of the EGEE Collaboration.
 * Copyright 2008 University Corporation for Advanced Internet Development, Inc.
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

package org.opensaml.xacml.policy.provider;

import org.opensaml.xacml.ctx.RequestType;
import org.opensaml.xacml.policy.PolicyType;

/**
 * A context for processing obligations.
 */
public class ObligationProcessingContext {

    /** The effective XACML policy. */
    private PolicyType effectivePolicy;

    /** XACML request context bearing the obligations. */
    private RequestType requestContext;

    /**
     * Constructor.
     * 
     * @param policy effective XACML policy for the given XACML request context
     * @param context XACML request context bearing the obligations
     */
    public ObligationProcessingContext(PolicyType policy, RequestType context) {
        if(effectivePolicy == null){
            throw new IllegalArgumentException("Effective XACML policy may not be null");
        }
        effectivePolicy = policy;
        
        if (context == null) {
            throw new IllegalArgumentException("Request context may not be null");
        }
        requestContext = context;
    }

    /**
     * Gets the XACML request context.
     * 
     * @return XACML request context
     */
    public RequestType getRequestContext() {
        return requestContext;
    }

    /**
     * Gets the effective XACML policy for the given XACML request context.
     * 
     * @return effective XACML policy for the given XACML request context
     */
    public PolicyType getEffectivePolicy() {
        return effectivePolicy;
    }
}