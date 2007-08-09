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

package org.opensaml.ws.soap.common;

import org.opensaml.ws.message.MessageContext;

/**
 * An extension to the basic {@link MessageContext} that adds SOAP related information.
 * 
 * @param <InboundEnvelopeType> Inbound SOAP envelope type
 * @param <OutboundEnvelopeType> Outbound SOAP envelope type
 */
public interface SOAPMessageContext<InboundEnvelopeType extends SOAPObject, OutboundEnvelopeType extends SOAPObject>
        extends MessageContext {

    /**
     * Gets the inbound SOAP envelope. This may or may not be the same object as returned from
     * {@link MessageContext#getInboundMessage()} as it is possible that SOAP envelope was carried inside another
     * message.
     * 
     * @return inbound SOAP envelope
     */
    public InboundEnvelopeType getInboundEnvelope();

    /**
     * Sets the inbound SOAP envelope.
     * 
     * @param envelope inbound SOAP envelope
     */
    public void setInboundEnvelope(InboundEnvelopeType envelope);

    /**
     * Gets the outbound SOAP envelope.
     * 
     * @return outbound SOAP envelope
     */
    public OutboundEnvelopeType getOutboundEnvelope();

    /**
     * Sets the outbound SOAP envelope.
     * 
     * @param envelope outbound SOAP envelope
     */
    public void setOutboundEnvelope(OutboundEnvelopeType envelope);
}