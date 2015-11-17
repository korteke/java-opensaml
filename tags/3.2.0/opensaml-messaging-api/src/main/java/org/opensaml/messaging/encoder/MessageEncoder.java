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

import net.shibboleth.utilities.java.support.component.DestructableComponent;
import net.shibboleth.utilities.java.support.component.InitializableComponent;

import org.opensaml.messaging.context.MessageContext;

/**
 * Interface for component that encodes message data from a {@link MessageContext} to a sink. Before the encoder can be
 * used the {@link #initialize()} method must be called. After the encoder has been used the {@link #destroy()} should
 * be invoked in order to clean up any resources.
 * 
 * <p>
 * The sink data or structure on which the encoder operates is supplied in an implementation-specific manner.
 * </p>
 * 
 * @param <MessageType> the message type of the message context on which to operate
 */
public interface MessageEncoder<MessageType> extends InitializableComponent, DestructableComponent {

    /**
     * This method should prepare the message context by creating and populating any binding-specific data structures
     * required in the MessageContext, prior to actually encoding.
     * 
     * <p>
     * This method should be called after the MessageContext has been set, and before any binding-specific Handler or
     * HandlerChains are invoked.
     * </p>
     * 
     * <p>
     * Example: For a SOAP encoder, this method would create and store the basic SOAP Envelope structure in the message
     * context, so that Handlers that are invoked have a place to which to add headers.
     * </p>
     * 
     * <p>
     * This method may be a no-op if not required by the binding, or if the message type of the context implies that the
     * binding-specific structures have already been created elsewhere (e.g. message-oriented code where the calling
     * code already knows its SOAP, and is operating on the raw SOAP envelope anyway).
     * </p>
     * 
     * @throws MessageEncodingException if there is a problem preparing the message context for encoding
     */
    void prepareContext() throws MessageEncodingException;

    /**
     * Encode the {@link MessageContext} supplied via {@link #setMessageContext(MessageContext)} to the sink.
     * 
     * @throws MessageEncodingException if there is a problem encoding the message context
     */
    void encode() throws MessageEncodingException;

    /**
     * Set the {@link MessageContext} which is to be encoded.
     * 
     * @param messageContext the message context
     */
    void setMessageContext(MessageContext<MessageType> messageContext);
}