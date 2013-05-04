/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.security.messaging.impl;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.security.SecurityException;
import org.opensaml.security.trust.TrustEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base rule which uses a trust engine to evaluate a token extracted from the request or message.
 * 
 * @param <TokenType> type of token which is being evaluated by the underlying trust engine
 * @param <MessageType> type of message contained in the message context being evaluated
 */
public abstract class BaseTrustEngineSecurityHandler<TokenType, MessageType> extends AbstractMessageHandler<MessageType> {

    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(BaseTrustEngineSecurityHandler.class);

    /** Trust engine used to verify the particular token type. */
    private TrustEngine<TokenType> trustEngine;

    /**
     * Gets the engine used to validate the untrusted token.
     * 
     * @return engine engine used to validate the untrusted token
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
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        Constraint.isNotNull(trustEngine, "TrustEngine was not supplied");
    }

    /**
     * Subclasses are required to implement this method to build a criteria set for the trust engine
     * according to trust engine and application-specific needs.
     * 
     * @param entityID the candidate issuer entity ID which is being evaluated 
     * @param messageContext the message context which is being evaluated
     * @return a newly constructly set of criteria suitable for the configured trust engine
     * @throws MessageHandlerException thrown if criteria set can not be constructed
     */
    protected abstract CriteriaSet buildCriteriaSet(String entityID, MessageContext<MessageType> messageContext)
        throws MessageHandlerException;

    /**
     * Evaluate the token using the configured trust engine against criteria built using
     * the specified candidate issuer entity ID and message context information.
     * 
     * @param token the token to be evaluated
     * @param entityID the candidate issuer entity ID which is being evaluated 
     * @param messageContext the message context which is being evaluated
     * @return true if the token satisfies the criteria as determined by the trust engine, otherwise false
     * @throws MessageHandlerException thrown if there is a fatal error during trust engine evaluation
     */
    protected boolean evaluate(TokenType token, String entityID, MessageContext<MessageType> messageContext)
        throws MessageHandlerException {
        
        CriteriaSet criteriaSet = buildCriteriaSet(entityID, messageContext);
        if (criteriaSet == null) {
            log.error("Returned criteria set was null, can not perform trust engine evaluation of token");
            throw new MessageHandlerException("Returned criteria set was null");
        }
        
        return evaluate(token, criteriaSet);
    }
    
    /**
     * Evaluate the token against the specified criteria using the configured trust engine.
     * 
     * @param token the token to be evaluated
     * @param criteriaSet the set of criteria against which to evaluate the token
     * @return true if the token satisfies the criteria as determined by the trust engine, otherwise false
     * @throws MessageHandlerException thrown if there is a fatal error during trust engine evaluation
     */
    protected boolean evaluate(TokenType token, CriteriaSet criteriaSet) throws MessageHandlerException {
        try {
            return getTrustEngine().validate(token, criteriaSet);
        } catch (SecurityException e) {
            log.error("There was an error evaluating the request's token using the trust engine", e);
            throw new MessageHandlerException("Error during trust engine evaluation of the token", e);
        }
    }

}