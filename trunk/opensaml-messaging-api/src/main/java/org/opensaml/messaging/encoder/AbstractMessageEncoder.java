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

package org.opensaml.messaging.encoder;

import net.shibboleth.utilities.java.support.component.AbstractInitializableComponent;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.UnmodifiableComponent;

import org.opensaml.messaging.context.MessageContext;

/**
 * Abstract message encoder.
 * 
 * @param <MessageType> the message type of the message context on which to operate
 */
public abstract class AbstractMessageEncoder<MessageType> extends AbstractInitializableComponent implements
        MessageEncoder<MessageType>, UnmodifiableComponent {

    /** The message context. */
    private MessageContext<MessageType> messageContext;

    /** {@inheritDoc} */
    public void setMessageContext(MessageContext<MessageType> context) {
        messageContext = context;
    }

    /** {@inheritDoc} */
    public void destroy() {
        // Default implementation is a no-op
    }

    /**
     * Get the message context.
     * 
     * @return the message context.
     */
    protected MessageContext<MessageType> getMessageContext() {
        return messageContext;
    }

    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();

        if (messageContext == null) {
            throw new ComponentInitializationException("Message context can not be null");
        }
    }
}