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

package org.opensaml.ws.security.impl;

import java.util.List;

import javax.servlet.ServletRequest;

import org.opensaml.ws.security.SecurityPolicy;
import org.opensaml.ws.security.SecurityPolicyContext;
import org.opensaml.ws.security.SecurityPolicyException;
import org.opensaml.ws.security.SecurityPolicyRule;
import org.opensaml.xml.XMLObject;

/**
 * Basic security policy implementation which just evaluates a given 
 * set of  {@link SecurityPolicyRule} in an ordered manner.
 * 
 * @param <RequestType> the message request type
 * @param <IssuerType> the message issuer type
 * 
 */
public class BasicSecurityPolicyImpl<RequestType extends ServletRequest, IssuerType> 
    implements SecurityPolicy<RequestType, IssuerType> {
    
    /** Security policy context which stores state for use in evaluation.  */
    private SecurityPolicyContext<IssuerType> context;
    
    /** Security policy rules which will be evaluated by this policy. */
    private List<SecurityPolicyRule<RequestType, IssuerType>> rules;

    /** {@inheritDoc} */
    public void evaluate(RequestType request, XMLObject message) throws SecurityPolicyException {
        context = createNewContext();
        for (SecurityPolicyRule<RequestType, IssuerType> rule : rules) {
           rule.evaluate(request, message, context);
        }
    }

    /** {@inheritDoc} */
    public IssuerType getIssuer() {
        return getSecurityPolicyContext().getIssuer();
    }

    /** {@inheritDoc} */
    public SecurityPolicyContext<IssuerType> getSecurityPolicyContext() {
        return context;
    }
    
    /**
     * Get a new instance of {@link SecurityPolicyContext} to use for a given
     * policy evaluation.
     * 
     * Subclasses may choose to override this method to create a context
     * of the appropriate subtype.
     * 
     * @return a new security policy context instance
     */
    protected SecurityPolicyContext<IssuerType> createNewContext() {
        return new SecurityPolicyContext<IssuerType>();
    }

}
