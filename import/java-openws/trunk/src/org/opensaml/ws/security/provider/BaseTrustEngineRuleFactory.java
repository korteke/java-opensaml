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

import org.opensaml.ws.security.SecurityPolicyRule;
import org.opensaml.ws.security.SecurityPolicyRuleFactory;
import org.opensaml.xml.security.trust.TrustEngine;

/**
 * Base abstract factory for rules which use a trust engine to evaluate a token extracted from the request
 * or message.
 * 
 * @param <RequestType> type of request being processed
 * @param <TokenType> type of token which is being evaluated by the underlying trust engine
 */
public abstract class BaseTrustEngineRuleFactory<TokenType, RequestType extends ServletRequest> implements
        SecurityPolicyRuleFactory<RequestType> {

    /** Trust engine used to verify the particular token type. */
    private TrustEngine<TokenType> trustEngine;
 
    /**
     * Gets the engine used to validate the untrusted token.
     * 
     * @return engine used to validate the untrusted token
     */
    public TrustEngine<TokenType> getTrustEngine() {
        return trustEngine;
    }

    /**
     * Sets the engine used to validate the untrusted token.
     * 
     * @param engine engine used to validate the untrusted token
     */
    public void setTrustEngine(TrustEngine<TokenType> engine) {
        trustEngine = engine;
    }

    /** {@inheritDoc} */
    public abstract SecurityPolicyRule<RequestType> createRuleInstance();
}