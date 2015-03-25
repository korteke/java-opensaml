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

package org.opensaml.messaging.error;

import org.opensaml.messaging.encoder.MessageEncoder;
import org.opensaml.messaging.handler.MessageHandler;


/**
 * A specialization of error handler where the error is handled via use of a specified message handler
 * and message encoder.
 * 
 * @param <MessageType> the type of message being handled
 */
public interface PipelineMessageErrorHandler<MessageType> extends MessageErrorHandler<MessageType> {
    
    /**
     * Get the handler to invoke on the outbound error message.
     * 
     * @return the outbound error handler
     */
    MessageHandler<MessageType> getHandler();
    
    /**
     * Set the handler to invoke on the outbound error message.
     * 
     * @param handler the outbound error handler
     */
    void setHandler(MessageHandler<MessageType> handler);

    /**
     * Get the message encoder used to encode the outbound error message.
     * 
     * @return the outbound error message encoder
     */
    MessageEncoder<MessageType> getMessageEncoder();
    
    /**
     * Set the message encoder used to encode the outbound error message.
     * 
     * @param messageEncoder the outbound error message encoder
     */
    void setMessageEncoder(MessageEncoder<MessageType> messageEncoder);
    
}
