/*
 * Licensed to the University Corporation for Advanced Internet Development, Inc.
 * under one or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache 
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

import org.opensaml.messaging.context.MessageContext;

/**
 * Abstract message decoder.
 *
 * @param <MessageType> the message type of the message context on which to operate
 */
public abstract class AbstractMessageDecoder<MessageType> implements MessageDecoder<MessageType> {
    
    /** Message context. */
    private MessageContext<MessageType> messageContext;
    
    /** {@inheritDoc} */
    public MessageContext<MessageType> getMessageContext() {
        return messageContext;
    }
    
    /** {@inheritDoc} */
    public void initialize() {
        //Default implementation is a no-op
    }
    
    /** {@inheritDoc} */
    public void destroy() {
        //Default implementation is a no-op
    }
    
    /**
     * Set the message context.
     * 
     * @param context the message context
     */
    protected void setMessageContext(MessageContext<MessageType> context) {
       messageContext = context;
    }
    
}
