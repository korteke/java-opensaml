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

import org.opensaml.ws.security.SecurityPolicy;
import org.opensaml.ws.security.SecurityPolicyContext;
import org.opensaml.ws.security.SecurityPolicyException;
import org.opensaml.ws.security.SecurityPolicyRule;
import org.opensaml.xml.XMLObject;

/**
 * Basic security policy implementation which evaluates a given set of {@link SecurityPolicyRule} in an ordered manner.
 * 
 * A policy evaluates succesfully if, and only if:
 * <ul>
 * <li>All policy rules evaluate succesfully</li>
 * <li>If an issuer is provided by a policy rule it is equal to the ID of the issuer provided by all previous rule</li>
 * <li>If issuer authentication is required, at least one rule must authenticate the issuer and the issuer may not fail
 * authentication with any rule</li>
 * </ul>
 * 
 * @param <RequestType> the message request type
 */
public class BasicSecurityPolicy<RequestType extends ServletRequest> implements SecurityPolicy<RequestType> {

    /** Whether the issuer of the message must be authenticated in order to pass this policy. */
    private boolean requireAuthenticatedIssuer;

    /** Issuer of the message. */
    private String issuer;

    /** Whether the issuer was authenticated. */
    private Boolean issuerAuthenticated;

    /** Security policy context which stores state for use in evaluation. */
    private SecurityPolicyContext policyContext;

    /** Security policy rules which will be evaluated by this policy. */
    private List<SecurityPolicyRule<RequestType>> securityRules;

    /**
     * Constructor.
     * 
     * Message issuer is required to be authenticated.
     */
    public BasicSecurityPolicy() {
        requireAuthenticatedIssuer = true;
        securityRules = new ArrayList<SecurityPolicyRule<RequestType>>();
        policyContext = createNewContext();
    }

    /**
     * Constructor.
     * 
     * @param authenticatedIssuer indicates whether the issuer must be authenticated
     */
    public BasicSecurityPolicy(boolean authenticatedIssuer) {
        requireAuthenticatedIssuer = authenticatedIssuer;
        securityRules = new ArrayList<SecurityPolicyRule<RequestType>>();
        policyContext = createNewContext();
    }

    /** {@inheritDoc} */
    public SecurityPolicyContext getSecurityPolicyContext() {
        return policyContext;
    }

    /** {@inheritDoc} */
    public String getIssuer() {
        return issuer;
    }

    /** {@inheritDoc} */
    public Boolean isIssuerAuthenticated() {
        return issuerAuthenticated;
    }

    /** {@inheritDoc} */
    public List<SecurityPolicyRule<RequestType>> getPolicyRules() {
        return securityRules;
    }

    /** {@inheritDoc} */
    public void evaluate(RequestType request, XMLObject message) throws SecurityPolicyException {
        ArrayList<Boolean> issuerAuthenticationTracker = new ArrayList<Boolean>(securityRules.size());
        for (SecurityPolicyRule<RequestType> rule : securityRules) {
            rule.evaluate(request, message, policyContext);
            issuerAuthenticationTracker.add(policyContext.isIssuerAuthenticated());
            if (issuer != null && policyContext.getIssuer() != null && !issuer.equals(policyContext.getIssuer())) {
                throw new SecurityPolicyException("Policy rules presented two or more, different, issuer IDs");
            } else {
                issuer = policyContext.getIssuer();
            }
        }

        if (issuerAuthenticationTracker.contains(Boolean.FALSE)) {
            issuerAuthenticated = Boolean.FALSE;
        } else if (issuerAuthenticationTracker.contains(Boolean.TRUE)) {
            issuerAuthenticated = Boolean.TRUE;
        } else {
            issuerAuthenticated = null;
        }

        if (requireAuthenticatedIssuer && issuerAuthenticated != Boolean.TRUE) {
            throw new SecurityPolicyException("Issuer was not authenticated by security policy rules.");
        }
    }

    /**
     * Get a new instance of {@link SecurityPolicyContext} to use for a given policy evaluation.
     * 
     * Subclasses may choose to override this method to create a context of the appropriate subtype.
     * 
     * @return a new security policy context instance
     */
    protected SecurityPolicyContext createNewContext() {
        return new SecurityPolicyContext();
    }
}