/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
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

import javax.servlet.ServletRequest;

import org.apache.log4j.Logger;
import org.opensaml.ws.security.SecurityPolicyContext;
import org.opensaml.ws.security.SecurityPolicyException;
import org.opensaml.ws.security.SecurityPolicyRule;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.trust.TrustEngine;

/**
 * Base rule which uses a trust engine to evaluate a token extracted from the request
 * or message.
 * 
 * @param <RequestType> type of request being processed
 * @param <TokenType> type of token which is being evaluated by the underlying trust engine
 */
public abstract class BaseTrustEngineRule<TokenType, RequestType extends ServletRequest> 
        implements SecurityPolicyRule<RequestType> {
    
    /** Logger. */
    private static Logger log = Logger.getLogger(BaseTrustEngineRule.class);

    /** Trust engine used to verify the particular token type. */
    private TrustEngine<TokenType> trustEngine;

    /**
     * Constructor.
     * 
     * @param engine Trust engine used to verify the particular token type
     */
    public BaseTrustEngineRule(TrustEngine<TokenType> engine) {
        trustEngine = engine;
    }

    /**
     * Gets the engine used to validate the untrusted token.
     * 
     * @return engine engine used to validate the untrusted token
     */
    public TrustEngine<TokenType> getTrustEngine() {
        return trustEngine;
    }

    /** {@inheritDoc} */
    public abstract void evaluate(RequestType request, XMLObject message, SecurityPolicyContext context)
            throws SecurityPolicyException;
    
    /**
     * Subclasses are required to implement this method to build a criteria set for the trust engine
     * according to trust engine and application-specific needs.
     * 
     * @param entityID the candidate issuer entity ID which is being evaluated 
     * @param request the protocol request
     * @param message the incoming message
     * @param context the security policy context to use for evaluation and storage of related state info
     * @return a newly constructly set of criteria suitable for the configured trust engine
     */
    protected abstract CriteriaSet buildCriteriaSet(String entityID, RequestType request, XMLObject message, 
            SecurityPolicyContext context);

    /**
     * Evaluate the token against the specified criteria using the configured trust engine.
     * 
     * @param token the token to be evaluated
     * @param criteriaSet the set of criteria against which to evaluate the token
     * @return true if the token satisfies the criteria as determined by the trust engine, otherwise false
     * @throws SecurityPolicyException thrown if there is a fatal error during trust engine evaluation
     */
    protected boolean evaluate(TokenType token, CriteriaSet criteriaSet) throws SecurityPolicyException {
        try {
            return getTrustEngine().validate(token, criteriaSet);
        } catch (SecurityException e) {
            log.error("There was an error evaluating the request's token using the trust engine", e);
            return false;
        }
    }

}