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

import net.shibboleth.utilities.java.support.component.UnmodifiableComponentException;

import org.joda.time.DateTime;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.messaging.context.SamlMessageInfoContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Security message handler implementation that checks for validity of SAML message issue instant date and time.
 */
public class MessageLifetimeSecurityHandler extends AbstractMessageHandler<SAMLObject> {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(MessageLifetimeSecurityHandler.class);

    /**
     * Clock skew - the number of seconds before a lower time bound, or after an upper time bound, to consider still
     * acceptable Default value: 3 minutes.
     */
    private int clockSkew;

    /** Amount of time in seconds for which a message is valid after it is issued. Default value: 5 minutes */
    private int messageLifetime;
    
    /** Whether this rule is required to be met. */
    private boolean requiredRule;

    /** Constructor. */
    public MessageLifetimeSecurityHandler() {
        clockSkew = 60*3;
        messageLifetime = 60*5;
        requiredRule = true;
    }
    
    /**
     * Get the clock skew.
     * 
     * @return Returns the clockSkew.
     */
    public int getClockSkew() {
        return clockSkew;
    }

    /**
     * Set the clock skew.
     * 
     * @param skew The clockSkew to set.
     */
    public void setClockSkew(final int skew) {
        if (isInitialized()) {
            throw new UnmodifiableComponentException("Clock skew can not be changed after handler has been initialized");
        }
        
        clockSkew = skew;
    }

    /**
     * Gets the amount of time, in seconds, for which a message is valid.
     * 
     * @return amount of time, in seconds, for which a message is valid
     */
    public int getMessageLifetime() {
        return messageLifetime;
    }

    /**
     * Sets the amount of time, in seconds, for which a message is valid.
     * 
     * @param lifetime amount of time, in seconds, for which a message is valid
     */
    public synchronized void setMessageLifetime(final int lifetime) {
        if (isInitialized()) {
            throw new UnmodifiableComponentException("Message lifetime can not be changed after handler has been initialized");
        }

        messageLifetime = lifetime;
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

    /** {@inheritDoc} */
    public void doInvoke(MessageContext<SAMLObject> messageContext) throws MessageHandlerException {
        SamlMessageInfoContext msgInfoContext = messageContext.getSubcontext(SamlMessageInfoContext.class, true);
        
        if (msgInfoContext.getMessageIssueInstant() == null) {
            if(requiredRule){
                log.warn("Inbound SAML message issue instant not present in message context");
                throw new MessageHandlerException("Inbound SAML message issue instant not present in message context");
            }else{
                return;
            }
        }

        DateTime issueInstant = msgInfoContext.getMessageIssueInstant();
        DateTime now = new DateTime();
        DateTime latestValid = now.plusSeconds(getClockSkew());
        DateTime expiration = issueInstant.plusSeconds(getClockSkew() + getMessageLifetime());

        // Check message wasn't issued in the future
        if (issueInstant.isAfter(latestValid)) {
            log.warn("Message was not yet valid: message time was {}, latest valid is: {}", issueInstant, latestValid);
            throw new MessageHandlerException("Message was rejected because was issued in the future");
        }

        // Check message has not expired
        if (expiration.isBefore(now)) {
            log.warn("Message was expired: message issue time was '" + issueInstant + "', message expired at: '"
                    + expiration + "', current time: '" + now + "'");
            throw new MessageHandlerException("Message was rejected due to issue instant expiration");
        }

    }
}