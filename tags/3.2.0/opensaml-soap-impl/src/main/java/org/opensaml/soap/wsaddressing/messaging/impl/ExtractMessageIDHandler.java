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

package org.opensaml.soap.wsaddressing.messaging.impl;

import java.util.List;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.soap.messaging.SOAPMessagingSupport;
import org.opensaml.soap.wsaddressing.MessageID;
import org.opensaml.soap.wsaddressing.messaging.WSAddressingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler implementation that handles wsa:MessageID header on the inbound SOAP envelope.
 * 
 * <p>
 * If the header is present, the value is stored in the message context via
 * {@link WSAddressingContext#setMessageIDURI(String)}.
 * </p>
 */
public class ExtractMessageIDHandler extends AbstractMessageHandler {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(ExtractMessageIDHandler.class);

    /** {@inheritDoc} */
    protected void doInvoke(MessageContext messageContext) throws MessageHandlerException {
        MessageID header = getMessageID(messageContext);
        String headerValue = header != null ? StringSupport.trimOrNull(header.getValue()) : null;
        log.debug("Extracted inbound WS-Addressing MessageID value: {}", headerValue);
        if (header != null && headerValue != null) {
            messageContext.getSubcontext(WSAddressingContext.class, true).setMessageIDURI(headerValue);
            SOAPMessagingSupport.registerUnderstoodHeader(messageContext, header);
        }
    }
    
    /**
     * Get message MessageID URI value.
     * 
     * @param messageContext the current message context
     * @return the message MessageID URI value
     */
    protected MessageID getMessageID(@Nonnull final MessageContext messageContext) {
        List<XMLObject> messageIDs = SOAPMessagingSupport.getInboundHeaderBlock(messageContext, MessageID.ELEMENT_NAME);
        if (messageIDs != null && !messageIDs.isEmpty()) {
            return (MessageID) messageIDs.get(0);
        }
        return null; 
    }

}
