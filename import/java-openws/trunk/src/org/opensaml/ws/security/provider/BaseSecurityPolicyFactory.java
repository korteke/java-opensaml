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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletRequest;

import org.opensaml.ws.security.SecurityPolicyFactory;
import org.opensaml.ws.security.SecurityPolicyRule;
import org.opensaml.ws.security.SecurityPolicyRuleFactory;

/**
 * Base class for {@link SecurityPolicyFactory} implementations.
 * 
 * @param <RequestType> type of incoming protocol request
 */
public abstract class BaseSecurityPolicyFactory<RequestType extends ServletRequest> implements
        SecurityPolicyFactory<RequestType> {

    /** Factories for the security policy rules for the security policy produced by this factory. */
    private List<SecurityPolicyRuleFactory<RequestType>> policyRuleFactories;

    /** Constructor. */
    protected BaseSecurityPolicyFactory() {
        policyRuleFactories = new ArrayList<SecurityPolicyRuleFactory<RequestType>>();
    }

    /** {@inheritDoc} */
    public List<SecurityPolicyRuleFactory<RequestType>> getPolicyRuleFactories() {
        return policyRuleFactories;
    }

    /**
     * Gets a set of newly built {@link SecurityPolicyRule}s from the registered factories. Created rules are in the
     * same order as the registered factories.
     * 
     * @return newly built {@link SecurityPolicyRule}s from the registered factories
     */
    protected List<SecurityPolicyRule<RequestType>> getPolicyRuleInstances() {
        ArrayList<SecurityPolicyRule<RequestType>> ruleInstances = new ArrayList<SecurityPolicyRule<RequestType>>();

        for (SecurityPolicyRuleFactory<RequestType> ruleFactory : policyRuleFactories) {
            ruleInstances.add(ruleFactory.createRuleInstance());
        }

        return ruleInstances;
    }
}