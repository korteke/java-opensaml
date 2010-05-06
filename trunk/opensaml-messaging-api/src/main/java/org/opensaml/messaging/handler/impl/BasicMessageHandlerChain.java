/*
 * Copyright 2010 University Corporation for Advanced Internet Development, Inc.
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
package org.opensaml.messaging.handler.impl;

import java.util.ArrayList;
import java.util.List;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandler;
import org.opensaml.messaging.handler.MessageHandlerChain;
import org.opensaml.messaging.handler.MessageHandlerException;

/**
 * A basic implementation of {@link MessageHandlerChain}.
 * 
 * @param <MessageType> the type of message being handled
 */
public class BasicMessageHandlerChain<MessageType> extends BaseMessageHandler<MessageType> {

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
    public List<MessageHandler<MessageType>> getHandlers() {
        return members;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public void invoke(MessageContext<MessageType> msgContext) throws MessageHandlerException {
        for (MessageHandler handler: members) {
            if (handler != null) {
                handler.invoke(msgContext);
            }
        }
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
    public void setHandlers(List<MessageHandler<MessageType>> handlers) {
        ArrayList<MessageHandler<MessageType>> newMembers = new ArrayList<MessageHandler<MessageType>>();
        newMembers.addAll(handlers);
        members = newMembers;
    }

}
