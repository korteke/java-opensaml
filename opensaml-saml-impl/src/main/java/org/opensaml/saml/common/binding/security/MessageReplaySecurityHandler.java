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
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.joda.time.DateTime;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.messaging.context.SamlMessageInfoContext;
import org.opensaml.saml.common.messaging.context.SamlPeerEntityContext;
import org.opensaml.util.storage.ReplayCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Security message handler implementation that which checks for replay of SAML messages.
 */
public class MessageReplaySecurityHandler extends AbstractMessageHandler<SAMLObject> {

    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(MessageReplaySecurityHandler.class);

    /** Message replay cache instance to use. */
    private ReplayCache replayCache;

    /** Whether this rule is required to be met. */
    private boolean requiredRule;
    
    /** Time in seconds to expire cache entries. Default value: (180) */
    private long expires;

    /** Constructor. */
    public MessageReplaySecurityHandler() {
        requiredRule = true;
        expires = 180;
    }

    /**
     * Get the replay cache instance to use.
     * 
     * @return Returns the replayCache.
     */
    @Nullable public ReplayCache getReplayCache() {
        return replayCache;
    }

    /**
     * Set the replay cache instance to use.
     * 
     * @param cache The replayCache to set.
     */
    public void setReplayCache(@Nonnull final ReplayCache cache) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        replayCache = Constraint.isNotNull(cache, "ReplayCache may not be null");
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
    public void setExpires(final long exp) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        expires = exp;
    }

    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        Constraint.isNotNull(getReplayCache(), "ReplayCache instance was null");
    }

    /** {@inheritDoc} */
    protected void doInvoke(@Nonnull MessageContext<SAMLObject> messageContext) throws MessageHandlerException {
        SamlPeerEntityContext peerContext = messageContext.getSubcontext(SamlPeerEntityContext.class, true);
        
        String entityID = StringSupport.trimOrNull(peerContext.getEntityId());
        if (entityID == null) {
            entityID = "(unknown)";
        }
        
        SamlMessageInfoContext msgInfoContext = messageContext.getSubcontext(SamlMessageInfoContext.class, true);

        String messageId = StringSupport.trimOrNull(msgInfoContext.getMessageId());
        if (messageId == null) {
            if (requiredRule) {
                log.warn("Message contained no ID, replay check not possible");
                throw new MessageHandlerException("SAML message from issuer " + entityID
                        + " did not contain an ID");
            } else {
                return;
            }
        }

        DateTime issueInstant = msgInfoContext.getMessageIssueInstant();
        if (issueInstant == null) {
            issueInstant = new DateTime();
        }
        
        if (!getReplayCache().check(getClass().getName(), messageId, issueInstant.getMillis() / 1000 + expires)) {
            log.warn("Replay detected of message '{}' from issuer '{}'", messageId, entityID);
            throw new MessageHandlerException("Rejecting replayed message ID '" + messageId + "' from issuer "
                    + entityID);
        }

    }

}