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

package org.opensaml.messaging.decoder;

import net.shibboleth.utilities.java.support.component.AbstractInitializableComponent;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.component.UnmodifiableComponent;

import org.opensaml.messaging.context.MessageContext;

/**
 * Abstract message decoder.
 * 
 * @param <MessageType> the message type of the message context on which to operate
 */
public abstract class AbstractMessageDecoder<MessageType> extends AbstractInitializableComponent implements
        MessageDecoder<MessageType>, UnmodifiableComponent {

    /** Message context. */
    private MessageContext<MessageType> messageContext;

    /** {@inheritDoc} */
    @Override
    public MessageContext<MessageType> getMessageContext() {
        return messageContext;
    }

    /**
     * Set the message context.
     * 
     * @param context the message context
     */
    protected void setMessageContext(MessageContext<MessageType> context) {
        messageContext = context;
    }

    /** {@inheritDoc} */
    @Override
    public void decode() throws MessageDecodingException {
        ComponentSupport.ifNotInitializedThrowUninitializedComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);

        doDecode();
    }
    
    /** {@inheritDoc} */
    protected void doDestroy() {
        messageContext = null;
        
        super.doDestroy();
    }

    /**
     * Performs the decoding logic. By the time this is called, this decoder has already been initialized and checked to
     * ensure that it has not been destroyed.
     * 
     * @throws MessageDecodingException thrown if there is a problem decoding the message
     */
    protected abstract void doDecode() throws MessageDecodingException;
}