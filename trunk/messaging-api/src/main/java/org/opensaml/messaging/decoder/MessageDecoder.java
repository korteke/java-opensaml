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
package org.opensaml.messaging.decoder;

import org.opensaml.messaging.context.MessageContext;


/**
 * Interface for component that decodes message data from a source into a {@link MessageContext}.
 * 
 * <p>
 * The data on which the decoder operates is supplied in an implementation-specific manner.
 * </p>
 * 
 * @param <MessageType> the message type of the message context on which to operate
 */
public interface MessageDecoder<MessageType> {
    
    /**
     * Initialize the decoder.  Must be called prior to calling <code>decode</code>.
     */
    public void initialize();
    
    /**
     * Destroy the decoder.  Must be called after calling <code>decode</code>.
     */
    public void destroy();
    
    /**
     * Decode message data from the source and store it so that it may be retrieved via {@link #getMessageContext()}.
     * 
     * @throws MessageDecodingException if there is a problem decoding the message context
     */
    public void decode() throws MessageDecodingException;
    
    /**
     * Get the decoded message context.
     * 
     * @return the decoded message context
     */
    public MessageContext<MessageType> getMessageContext();
    
}
