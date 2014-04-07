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

import net.shibboleth.utilities.java.support.component.DestructableComponent;
import net.shibboleth.utilities.java.support.component.InitializableComponent;

import org.opensaml.messaging.context.MessageContext;

/**
 * Interface for component that decodes message data from a source into a {@link MessageContext}. Before the decoder can
 * be used the {@link #initialize()} method must be called. After the decoder has been used the {@link #destroy()}
 * method should be invoked in order to clean up any resources.
 * 
 * <p>
 * The data on which the decoder operates is supplied in an implementation-specific manner.
 * </p>
 * 
 * @param <MessageType> the message type of the message context on which to operate
 */
public interface MessageDecoder<MessageType> extends InitializableComponent, DestructableComponent {

    /**
     * Decode message data from the source and store it so that it may be retrieved via {@link #getMessageContext()}.
     * 
     * @throws MessageDecodingException if there is a problem decoding the message context
     */
    void decode() throws MessageDecodingException;

    /**
     * Get the decoded message context.
     * 
     * @return the decoded message context
     */
    MessageContext<MessageType> getMessageContext();
}