/*
 * Copyright [2007] [University Corporation for Advanced Internet Development, Inc.]
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

package org.opensaml.ws.message;

import org.opensaml.ws.transport.InTransport;
import org.opensaml.ws.transport.OutTransport;
import org.opensaml.xml.XMLObject;

/**
 * A message context represents the entire context for a given message through the receive, process, and/or response
 * phases. It is a basic unit of work within the library.
 * 
 * Message contexts are <strong>NOT</strong> thread safe.
 */
public interface MessageContext {

    /**
     * Gets the inbound message.
     * 
     * @return the inbound message
     */
    public XMLObject getInboundMessage();

    /**
     * Gets the transport used to receive the message.
     * 
     * @return transport used to receive the message
     */
    public InTransport getMessageInTransport();

    /**
     * Gets the transport used to respond to the message.
     * 
     * @return transport used to respond to the message
     */
    public OutTransport getMessageOutTransport();

    /**
     * Gets the outbound message.
     * 
     * @return the outbound message
     */
    public XMLObject getOutboundMessage();

    /**
     * Sets the inbound message.
     * 
     * @param message the inbound message
     */
    public void setInboundMessage(XMLObject message);

    /**
     * Sets the transport used to used to receive the message.
     * 
     * @param transport the transport used to receive the message
     */
    public void setMessageInTransport(InTransport transport);

    /**
     * Sets the transport used to respond to the message.
     * 
     * @param transport the transport used to respond to the message
     */
    public void setMessageOutTransport(OutTransport transport);

    /**
     * Sets the outbound message.
     * 
     * @param message the outbound message
     */
    public void setOutboundMessage(XMLObject message);
}
