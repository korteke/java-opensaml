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

package org.opensaml.messaging.handler.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandler;
import org.opensaml.messaging.handler.MessageHandlerChain;
import org.opensaml.messaging.handler.MessageHandlerException;

/**
 * A basic implementation of {@link MessageHandlerChain}.
 * 
 * @param <MessageType> the type of message being handled
 */
public class BasicMessageHandlerChain<MessageType> extends AbstractMessageHandler<MessageType> 
    implements MessageHandlerChain<MessageType> {

    /** The list of members of the handler chain. */
    private List<MessageHandler<MessageType>> members;
    
    /** 
     * {@inheritDoc}
     * 
     * <p>
     * The returned list is immutable.  Changes to the list
     * should be accomplished through {@link BasicMessageHandlerChain#setHandlers(List)}.
     * </p>
     * 
     * */
    @Nullable public List<MessageHandler<MessageType>> getHandlers() {
        return members;
    }
    
    /**
     * Set the list of message handler chain members.
     * 
     * <p>
     * The supplied list is copied before being stored.  Later modifications to 
     * the originally supplied list will not be reflected in the handler chain membership.
     * </p>
     * 
     * @param handlers the list of message handler members
     */
    public void setHandlers(@Nullable final List<MessageHandler<MessageType>> handlers) {
        if (handlers != null) {
            ArrayList<MessageHandler<MessageType>> newMembers = new ArrayList<MessageHandler<MessageType>>();
            newMembers.addAll(handlers);
            members = newMembers;
        } else {
            members = null;
        }
    }

    /** {@inheritDoc} */
    public void doInvoke(@Nonnull final MessageContext<MessageType> msgContext) throws MessageHandlerException {
        if (members != null) {
            for (MessageHandler handler: members) {
                if (handler != null) {
                    handler.invoke(msgContext);
                }
            }
        }
    }
}