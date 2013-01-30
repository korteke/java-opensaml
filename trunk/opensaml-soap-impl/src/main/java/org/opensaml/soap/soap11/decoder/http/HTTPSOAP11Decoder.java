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

package org.opensaml.soap.soap11.decoder.http;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.messaging.decoder.servlet.BaseHttpServletRequestXmlMessageDecoder;
import org.opensaml.messaging.handler.MessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic SOAP 1.1 decoder for HTTP transport.
 * 
 * <p>
 * This decoder takes a mandatory {@link MessageHandler} instance which is used to determine
 * populate the message that is returned as the {@link MessageContext#getMessage()}.
 * </p>
 * 
 *  <p>
 *  A SOAP message oriented message exchange style might just populate the Envelope as the message.
 *  An application-specific payload-oriented message exchange would handle a specific type
 * of payload structure.  
 * </p>
 * 
 * @param <MessageType> the message type of the message context on which to operate
 */
public class HTTPSOAP11Decoder<MessageType extends XMLObject> 
    extends BaseHttpServletRequestXmlMessageDecoder<MessageType> {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(HTTPSOAP11Decoder.class);
    
    /** Message handler to use in processing the message body. */
    private MessageHandler<MessageType> bodyHandler;
    
    /** Constructor. */
    public HTTPSOAP11Decoder() {
        super();
    }

    /**
     * Get the configured body handler MessageHandler.
     * 
     * @return Returns the bodyHandler.
     */
    public MessageHandler<MessageType> getBodyHandler() {
        return bodyHandler;
    }

    /**
     * Set the configured body handler MessageHandler.
     * 
     * @param newBodyHandler The bodyHandler to set.
     */
    public void setBodyHandler(MessageHandler<MessageType> newBodyHandler) {
        bodyHandler = newBodyHandler;
    }

    /** {@inheritDoc} */
    protected void doDecode() throws MessageDecodingException {
        MessageContext<MessageType> messageContext = new MessageContext<MessageType>();
        HttpServletRequest request = getHttpServletRequest();

        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            throw new MessageDecodingException("This message decoder only supports the HTTP POST method");
        }

        log.debug("Unmarshalling SOAP message");
        Envelope soapMessage;
        try {
            soapMessage = (Envelope) unmarshallMessage(request.getInputStream());
            messageContext.getSubcontext(SOAP11Context.class, true).setEnvelope(soapMessage);
        } catch (IOException e) {
            log.error("Unable to obtain input stream from HttpServletRequest", e);
            throw new MessageDecodingException("Unable to obtain input stream from HttpServletRequest", e);
        }
        
        try {
            getBodyHandler().invoke(messageContext);
        } catch (MessageHandlerException e) {
            log.error("Error processing SOAP Envelope body", e);
            throw new MessageDecodingException("Error processing SOAP Envelope body", e);
        }
        
        if (messageContext.getMessage() == null) {
            log.warn("Body handler did not properly populate the message in message context");
            throw new MessageDecodingException("Body handler did not properly populate the message in message context");
        }
        
        setMessageContext(messageContext);
        
    }
    
    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (getBodyHandler() == null) {
            throw new ComponentInitializationException("Body handler MessageHandler cannot be null");
        }
    }    
    
    /** {@inheritDoc} */
    protected XMLObject getMessageToLog() {
        return getMessageContext().getSubcontext(SOAP11Context.class, true).getEnvelope();
    }

}