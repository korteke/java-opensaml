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

package org.opensaml.messaging.handler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.messaging.context.MessageContext;



/**
 * A base abstract implementation of {@link MessageHandler}.
 * 
 * @param <MessageType> the type of message being handled
 */
public abstract class AbstractMessageHandler<MessageType> implements MessageHandler<MessageType> {

    /** The handler unique identifier. */
    private String id;

    /** {@inheritDoc} */
    @Nullable public String getId() {
        return id;
    }
    
    /**
     * Set the handler's unique identifier.
     * 
     * @param newId the handler's new unique identifier
     */
    public void setId(@Nullable final String newId) {
        id = newId;
    }

    /** {@inheritDoc} */
    public void invoke(@Nonnull final MessageContext<MessageType> messageContext) throws MessageHandlerException {
        Constraint.isNotNull(messageContext, "Message context cannot be null");
        
        doInvoke(messageContext);
    }


    /**
     * Performs the handler logic.
     * 
     * @param messageContext the message context on which to invoke the handler
     * @throws MessageHandlerException if the there is an error invoking the handler on the message context
     */
    protected abstract void doInvoke(@Nonnull final MessageContext<MessageType> messageContext)
            throws MessageHandlerException;

}