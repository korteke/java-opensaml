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

package org.opensaml.saml.common.binding.security.impl;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.annotation.Duration;
import net.shibboleth.utilities.java.support.annotation.constraint.NonNegative;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.joda.time.DateTime;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.messaging.context.SAMLMessageInfoContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Security message handler implementation that checks for validity of SAML message issue instant date and time.
 */
public class MessageLifetimeSecurityHandler extends AbstractMessageHandler {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(MessageLifetimeSecurityHandler.class);

    /**
     * Clock skew - milliseconds before a lower time bound, or after an upper time bound, to consider still
     * acceptable Default value: 3 minutes.
     */
    @Duration @NonNegative private long clockSkew;

    /** Amount of time in milliseconds for which a message is valid after it is issued. Default value: 3 minutes */
    @Duration @NonNegative private long messageLifetime;
    
    /** Whether this rule is required to be met. */
    private boolean requiredRule;

    /** Constructor. */
    public MessageLifetimeSecurityHandler() {
        super();
        clockSkew = 60 * 3 * 1000;
        messageLifetime = 180 * 1000;
        requiredRule = true;
    }
    
    /**
     * Get the clock skew.
     * 
     * @return the clock skew
     */
    @NonNegative public long getClockSkew() {
        return clockSkew;
    }

    /**
     * Set the clock skew.
     * 
     * @param skew clock skew to set
     */
    public void setClockSkew(@Duration @NonNegative final long skew) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        clockSkew = Constraint.isGreaterThanOrEqual(0, skew, "Clock skew must be greater than or equal to 0");
    }

    /**
     * Gets the amount of time, in milliseconds, for which a message is valid.
     * 
     * @return amount of time, in milliseconds, for which a message is valid
     */
    @NonNegative public long getMessageLifetime() {
        return messageLifetime;
    }

    /**
     * Sets the amount of time, in milliseconds, for which a message is valid.
     * 
     * @param lifetime amount of time, in milliseconds, for which a message is valid
     */
    public synchronized void setMessageLifetime(@Duration @NonNegative final long lifetime) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        messageLifetime = Constraint.isGreaterThanOrEqual(0, lifetime,
                "Message lifetime must be greater than or equal to 0");
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
    public void setRequiredRule(final boolean required) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        requiredRule = required;
    }

    /** {@inheritDoc} */
    @Override
    public void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        final SAMLMessageInfoContext msgInfoContext = messageContext.getSubcontext(SAMLMessageInfoContext.class, true);
        
        if (msgInfoContext.getMessageIssueInstant() == null) {
            if (requiredRule) {
                log.warn("{} Inbound SAML message issue instant not present in message context", getLogPrefix());
                throw new MessageHandlerException("Inbound SAML message issue instant not present in message context");
            } else {
                return;
            }
        }

        final DateTime issueInstant = msgInfoContext.getMessageIssueInstant();
        final DateTime now = new DateTime();
        final DateTime latestValid = now.plus(getClockSkew());
        final DateTime expiration = issueInstant.plus(getClockSkew() + getMessageLifetime());

        // Check message wasn't issued in the future
        if (issueInstant.isAfter(latestValid)) {
            log.warn("{} Message was not yet valid: message time was {}, latest valid is: {}", getLogPrefix(),
                    issueInstant, latestValid);
            throw new MessageHandlerException("Message was rejected because was issued in the future");
        }

        // Check message has not expired
        if (expiration.isBefore(now)) {
            log.warn(
                    "{} Message was expired: message issue time was '{}', message expired at: '{}', current time: '{}'",
                    getLogPrefix(), issueInstant, expiration, now);
            throw new MessageHandlerException("Message was rejected due to issue instant expiration");
        }
    }
    
}