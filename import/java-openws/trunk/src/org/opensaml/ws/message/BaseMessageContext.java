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
 * Base class for message context implementations.
 */
public abstract class BaseMessageContext implements MessageContext {

    /** The inbound message. */
    private XMLObject inboundMessage;

    /** Inbound message transport. */
    private InTransport inboundTransport;

    /** Outbound message transport. */
    private OutTransport outboundTransport;

    /** Outbound message. */
    private XMLObject outboundMessage;

    /** {@inheritDoc} */
    public XMLObject getInboundMessage() {
        return inboundMessage;
    }

    /** {@inheritDoc} */
    public InTransport getMessageInTransport() {
        return inboundTransport;
    }

    /** {@inheritDoc} */
    public OutTransport getMessageOutTransport() {
        return outboundTransport;
    }

    /** {@inheritDoc} */
    public XMLObject getOutboundMessage() {
        return outboundMessage;
    }

    /** {@inheritDoc} */
    public void setInboundMessage(XMLObject message) {
        inboundMessage = message;
    }

    /** {@inheritDoc} */
    public void setMessageInTransport(InTransport transport) {
        inboundTransport = transport;
    }

    /** {@inheritDoc} */
    public void setMessageOutTransport(OutTransport transport) {
        outboundTransport = transport;
    }

    /** {@inheritDoc} */
    public void setOutboundMessage(XMLObject message) {
        outboundMessage = message;
    }
}