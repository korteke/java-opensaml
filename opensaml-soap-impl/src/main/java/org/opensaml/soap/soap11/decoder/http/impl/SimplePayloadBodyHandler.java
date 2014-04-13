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

package org.opensaml.soap.soap11.decoder.http.impl;

import java.util.List;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A body handler for use with {@link HTTPSOAP11Decoder} that populates the 
 * context message with the payload from the SOAP Envelope Body. The first 
 * child element of of the SOAP Envelope's Body is chosen as the message.
 * 
 * @param <MessageType> the message type of the message context on which to operate
 */
public class SimplePayloadBodyHandler<MessageType extends XMLObject> extends AbstractMessageHandler<MessageType> {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(SimplePayloadBodyHandler.class);

    /** {@inheritDoc} */
    protected void doInvoke(MessageContext messageContext) throws MessageHandlerException {
        Envelope env = (Envelope) messageContext.getSubcontext(SOAP11Context.class).getEnvelope();
        List<XMLObject> bodyChildren = env.getBody().getUnknownXMLObjects();
        if (bodyChildren == null || bodyChildren.isEmpty()) {
            throw new MessageHandlerException("SOAP Envelope Body contained no children");
        } else if (bodyChildren.size() > 1) {
            log.warn("SOAP Envelope Body contained more than one child.  Returning the first as the message");
        }
            
        messageContext.setMessage(env.getBody().getUnknownXMLObjects().get(0));
    }

}
