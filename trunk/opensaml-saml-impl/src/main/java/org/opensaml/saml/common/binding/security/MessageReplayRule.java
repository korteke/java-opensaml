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

package org.opensaml.saml.common.binding.security;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.joda.time.DateTime;
import org.opensaml.saml.common.binding.SAMLMessageContext;
import org.opensaml.util.storage.ReplayCache;
import org.opensaml.ws.message.MessageContext;
import org.opensaml.ws.security.SecurityPolicyException;
import org.opensaml.ws.security.SecurityPolicyRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Security policy rule implementation that which checks for replay of SAML messages.
 */
public class MessageReplayRule implements SecurityPolicyRule {

    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(MessageReplayRule.class);

    /** Message replay cache instance to use. */
    private ReplayCache replayCache;

    /** Whether this rule is required to be met. */
    private boolean requiredRule;
    
    /** Time in seconds to expire cache entries. Default value: (180) */
    private long expires;

    /**
     * Constructor.
     * 
     * @param newReplayCache the new replay cache instance
     */
    public MessageReplayRule(@Nonnull ReplayCache newReplayCache) {
        replayCache = newReplayCache;
        requiredRule = true;
        expires = 180;
    }

    /**
     * Gets whether this rule is required to be met.
     * 
     * @return whether this rule is required to be met
     */
    public boolean isRequiredRule() {
        return requiredRule;
    }

    /**
     * Sets whether this rule is required to be met.
     * 
     * @param required whether this rule is required to be met
     */
    public void setRequiredRule(boolean required) {
        requiredRule = required;
    }

    /**
     * Gets the lifetime in seconds of replay entries.
     * 
     * @return lifetime in seconds of entries
     */
    public long getExpires() {
        return expires;
    }

    /**
     * Sets the lifetime in seconds of replay entries.
     * 
     * @param exp lifetime in seconds of entries
     */
    public void setExpires(long exp) {
        expires = exp;
    }

    /** {@inheritDoc} */
    public void evaluate(@Nonnull MessageContext messageContext) throws SecurityPolicyException {
        if (!(messageContext instanceof SAMLMessageContext)) {
            log.debug("Invalid message context type, this policy rule only supports SAMLMessageContext");
            return;
        }

        SAMLMessageContext samlMsgCtx = (SAMLMessageContext) messageContext;

        String messageIssuer = StringSupport.trimOrNull(samlMsgCtx.getInboundMessageIssuer());
        if (messageIssuer == null) {
            messageIssuer = "(unknown)";
        }

        String messageId = StringSupport.trimOrNull(samlMsgCtx.getInboundSAMLMessageId());
        if (messageId == null) {
            if (requiredRule) {
                log.warn("Message contained no ID, replay check not possible");
                throw new SecurityPolicyException("SAML message from issuer " + messageIssuer
                        + " did not contain an ID");
            }
            return;
        }

        DateTime issueInstant = samlMsgCtx.getInboundSAMLMessageIssueInstant();
        if (issueInstant == null) {
            issueInstant = new DateTime();
        }
        
        if (!replayCache.check(getClass().getName(), messageId, issueInstant.getMillis() / 1000 + expires)) {
            log.warn("Replay detected of message '" + messageId + "' from issuer " + messageIssuer);
            throw new SecurityPolicyException("Rejecting replayed message ID '" + messageId + "' from issuer "
                    + messageIssuer);
        }

    }
}