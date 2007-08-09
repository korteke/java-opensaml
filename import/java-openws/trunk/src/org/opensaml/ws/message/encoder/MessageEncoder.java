/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
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

package org.opensaml.ws.message.encoder;

import org.opensaml.ws.message.MessageContext;

/**
 * Encodes a message onto the outbound transport.
 * 
 * Message encoders <strong>MUST</strong> must be thread safe and stateless.
 */
public interface MessageEncoder {

    /**
     * Encodes the message in the binding specific manner.
     * 
     * @param messageContext current message context
     * 
     * @throws MessageEncodingException thrown if the problem can not be encoded
     */
    public void encode(MessageContext messageContext) throws MessageEncodingException;
}