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

import org.opensaml.ws.security.SecurityPolicy;
import org.opensaml.ws.transport.InTransport;
import org.opensaml.ws.transport.OutTransport;
import org.opensaml.xml.XMLObject;

/**
 * Base class for message context implementations.
 */
public class BaseMessageContext implements MessageContext {

    /** The inbound message. */
    private XMLObject inboundMessage;

    /** Issuer of the inbound message. */
    private String inboundMessageIssuer;

    /** Inbound message transport. */
    private InTransport inboundTransport;

    /** Outbound message. */
    private XMLObject outboundMessage;

    /** Issuer of the outbound message. */
    private String outboundMessageIssuer;

    /** Outbound message transport. */
    private OutTransport outboundTransport;

    /** Security policy for this message context. */
    private SecurityPolicy securityPolicy;

    /** {@inheritDoc} */
    public XMLObject getInboundMessage() {
        return inboundMessage;
    }

    /** {@inheritDoc} */
    public String getInboundMessageIssuer() {
        return inboundMessageIssuer;
    }

    /** {@inheritDoc} */
    public InTransport getInboundMessageTransport() {
        return inboundTransport;
    }

    /** {@inheritDoc} */
    public OutTransport getOutboundMessageTransport() {
        return outboundTransport;
    }

    /** {@inheritDoc} */
    public XMLObject getOutboundMessage() {
        return outboundMessage;
    }

    /** {@inheritDoc} */
    public String getOutboundMessageIssuer() {
        return outboundMessageIssuer;
    }

    /** {@inheritDoc} */
    public SecurityPolicy getSecurityPolicy() {
        return securityPolicy;
    }

    /** {@inheritDoc} */
    public void setInboundMessage(XMLObject message) {
        inboundMessage = message;
    }

    /** {@inheritDoc} */
    public void setInboundMessageIssuer(String issuer) {
        inboundMessageIssuer = issuer;
    }

    /** {@inheritDoc} */
    public void setInboundMessageTransport(InTransport transport) {
        inboundTransport = transport;
    }

    /** {@inheritDoc} */
    public void setOutboundMessageTransport(OutTransport transport) {
        outboundTransport = transport;
    }

    /** {@inheritDoc} */
    public void setOutboundMessage(XMLObject message) {
        outboundMessage = message;
    }

    /** {@inheritDoc} */
    public void setOutboundMessageIssuer(String issuer) {
        outboundMessageIssuer = issuer;
    }

    /** {@inheritDoc} */
    public void setSecurityPolicy(SecurityPolicy policy) {
        securityPolicy = policy;
    }
}