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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
 */
public abstract class BaseTrustEngineSecurityHandler<TokenType> extends AbstractMessageHandler {

    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(BaseTrustEngineSecurityHandler.class);

    /** Trust engine used to verify the particular token type. */
    @Nullable private TrustEngine<? super TokenType> trustEngine;

    /**
     * Gets the trust engine used to validate the untrusted token.
     * 
     * @return trust engine used to validate the untrusted token
     */
    @Nullable protected TrustEngine<? super TokenType> getTrustEngine() {
        return trustEngine;
    }

    /** {@inheritDoc} */
    @Override
    protected boolean doPreInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        
        if (!super.doPreInvoke(messageContext)) {
            return false;
        }
        
        final TrustEngine<? super TokenType> engine = resolveTrustEngine(messageContext);
        if (engine == null) {
            throw new MessageHandlerException("TrustEngine could not be resolved from MessageContext");
        } else {
            trustEngine = engine;
        }
        
        return true;
    }

    /**
     * Resolve a TrustEngine instance of the appropriate type from the message context.
     * 
     * @param messageContext the message context which is being evaluated
     * @return the resolved TrustEngine, may be null
     */
    @Nullable protected abstract TrustEngine<? super TokenType> resolveTrustEngine(
            @Nonnull final MessageContext messageContext);

    /**
     * Subclasses are required to implement this method to build a criteria set for the trust engine
     * according to trust engine and application-specific needs.
     * 
     * @param entityID the candidate issuer entity ID which is being evaluated 
     * @param messageContext the message context which is being evaluated
     * @return a newly constructly set of criteria suitable for the configured trust engine
     * @throws MessageHandlerException thrown if criteria set can not be constructed
     */
    @Nullable protected abstract CriteriaSet buildCriteriaSet(@Nullable final String entityID, 
            @Nonnull final MessageContext messageContext) throws MessageHandlerException;

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
    protected boolean evaluate(@Nonnull final TokenType token, @Nullable final String entityID,
            @Nonnull final MessageContext messageContext) throws MessageHandlerException {
        
        final CriteriaSet criteriaSet = buildCriteriaSet(entityID, messageContext);
        if (criteriaSet == null) {
            log.error("{} Returned criteria set was null, can not perform trust engine evaluation of token",
                    getLogPrefix());
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
    protected boolean evaluate(@Nonnull final TokenType token, @Nullable final CriteriaSet criteriaSet)
            throws MessageHandlerException {
        try {
            return getTrustEngine().validate(token, criteriaSet);
        } catch (final SecurityException e) {
            log.error("{} There was an error evaluating the request's token using the trust engine", getLogPrefix(), e);
            throw new MessageHandlerException("Error during trust engine evaluation of the token", e);
        }
    }

}