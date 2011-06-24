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

package org.opensaml.messaging.context;

import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;

/**
 * Basic implementation of {@link InOutOperationContext}.
 * 
 * @param <InboundMessageType> the inbound message type
 * @param <OutboundMessageType> the outbound message type
 */
public class BasicInOutOperationContext<InboundMessageType, OutboundMessageType> extends AbstractSubcontextContainer
        implements InOutOperationContext<InboundMessageType, OutboundMessageType> {

    /** The inbound message context. */
    private MessageContext<InboundMessageType> inboundContext;

    /** The outbound message context. */
    private MessageContext<OutboundMessageType> outboundContext;

    /** The context unique identifier. */
    private String id;

    /** The context creation timestamp. */
    private DateTime creationTime;

    /** Constructor. Sets ID to a generated UUID and creation time to now. */
    protected BasicInOutOperationContext() {
        id = UUID.randomUUID().toString();
        creationTime = new DateTime(ISOChronology.getInstanceUTC());
    }

    /**
     * Constructor.
     * 
     * @param inbound the inbound message context
     * @param outbound the outbound message context
     */
    public BasicInOutOperationContext(MessageContext<InboundMessageType> inbound,
            MessageContext<OutboundMessageType> outbound) {
        this();

        inboundContext = inbound;
        outboundContext = outbound;

    }

    /** {@inheritDoc} */
    public MessageContext<InboundMessageType> getInboundMessageContext() {
        return inboundContext;
    }

    /**
     * Sets the inbound message context.
     * 
     * @param context inbound message context, may be null
     */
    public void setInboundMessageContext(MessageContext<InboundMessageType> context) {
        inboundContext = context;
    }

    /** {@inheritDoc} */
    public MessageContext<OutboundMessageType> getOutboundMessageContext() {
        return outboundContext;
    }

    /**
     * Sets the outbound message context.
     * 
     * @param context outbound message context, may be null
     */
    public void setOutboundMessageContext(MessageContext<OutboundMessageType> context) {
        outboundContext = context;
    }

    /** {@inheritDoc} */
    public DateTime getCreationTime() {
        return creationTime;
    }

    /** {@inheritDoc} */
    public String getId() {
        return id;
    }

}
