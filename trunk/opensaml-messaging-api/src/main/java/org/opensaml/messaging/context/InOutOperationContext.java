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


/**
* An operation context which represents concretely a message exchange pattern involving an 
* inbound message and an outbound message. This is the typical request-response
* pattern seen in messaging environments, and might be either server-side or client-side.
* 
* @param <InboundMessageType> the inbound message type
* @param <OutboundMessageType> the outbound message type
*/
public class InOutOperationContext<InboundMessageType, OutboundMessageType> extends BaseContext {

    /** The inbound message context. */
    private MessageContext<InboundMessageType> inboundContext;

    /** The outbound message context. */
    private MessageContext<OutboundMessageType> outboundContext;

    /** Constructor. Sets ID to a generated UUID and creation time to now. */
    protected InOutOperationContext() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param inbound the inbound message context
     * @param outbound the outbound message context
     */
    public InOutOperationContext(MessageContext<InboundMessageType> inbound,
            MessageContext<OutboundMessageType> outbound) {
        this();

        inboundContext = inbound;
        outboundContext = outbound;

    }

    /**
     * The inbound message context instance.
     * 
     * @return the inbound message context
     */
    public MessageContext<InboundMessageType> getInboundMessageContext() {
        return inboundContext;
    }

    /**
     * The outbound message context instance.
     * 
     * @return the outbound message context
     */
    public MessageContext<OutboundMessageType> getOutboundMessageContext() {
        return outboundContext;
    }

}
