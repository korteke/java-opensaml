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

package org.opensaml.messaging.decoder.servlet;

import java.io.InputStream;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.xml.ParserPool;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 * Base class for message decoders which decode XML messages from an {@link HttpServletRequest}.
 * 
 * @param <MessageType> the message type of the message context on which to operate
 */
public abstract class BaseHttpServletRequestXmlMessageDecoder<MessageType extends XMLObject>
    extends AbstractHttpServletRequestMessageDecoder<MessageType> {
    
    /** Used to log protocol messages. */
    private Logger protocolMessageLog = LoggerFactory.getLogger("PROTOCOL_MESSAGE");

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(BaseHttpServletRequestXmlMessageDecoder.class);

    /** Parser pool used to deserialize the message. */
    private ParserPool parserPool;

    /** Constructor. */
    public BaseHttpServletRequestXmlMessageDecoder() {
        parserPool = XMLObjectProviderRegistrySupport.getParserPool();
    }

    /** {@inheritDoc} */
    public void decode() throws MessageDecodingException {
        log.debug("Beginning to decode message from HttpServletRequest");
        
        super.decode();
        
        logDecodedMessage();

        log.debug("Successfully decoded message from HttpServletRequest.");
    }
    
    /**
     * Gets the parser pool used to deserialize incoming messages.
     * 
     * @return parser pool used to deserialize incoming messages
     */
    @Nonnull public ParserPool getParserPool() {
        return parserPool;
    }

    /**
     * Sets the parser pool used to deserialize incoming messages.
     * 
     * @param pool parser pool used to deserialize incoming messages
     */
    public void setParserPool(@Nonnull final ParserPool pool) {
        Constraint.isNotNull(pool, "ParserPool cannot be null");
        parserPool = pool;
    }
    
    /** {@inheritDoc} */
    protected void doDestroy() {
        parserPool = null;
        
        super.doDestroy();
    }
    
    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (parserPool == null) {
            throw new ComponentInitializationException("Parser pool cannot be null");
        }
    }

    /**
     * Log the decoded message to the protocol message logger.
     */
    protected void logDecodedMessage() {
        if (protocolMessageLog.isDebugEnabled() ){
            XMLObject message = getMessageToLog();
            if (message == null) {
                log.warn("Decoded message was null, nothing to log");
                return;
            }
            
            try {
                Element dom = XMLObjectSupport.marshall(message);
                protocolMessageLog.debug("\n" + SerializeSupport.prettyPrintXML(dom));
            } catch (MarshallingException e) {
                log.error("Unable to marshall message for logging purposes", e);
            }
        }
    }
    
    /**
     * Get the XMLObject which will be logged as the protocol message.
     * 
     * @return the XMLObject message considered to be the protocol message for logging purposes
     */
    protected XMLObject getMessageToLog() {
        return getMessageContext().getMessage();
    }

    /**
     * Helper method that deserializes and unmarshalls the message from the given stream.
     * 
     * @param messageStream input stream containing the message
     * 
     * @return the inbound message
     * 
     * @throws MessageDecodingException thrown if there is a problem deserializing and unmarshalling the message
     */
    protected XMLObject unmarshallMessage(InputStream messageStream) throws MessageDecodingException {
        try {
            XMLObject message = XMLObjectSupport.unmarshallFromInputStream(getParserPool(), messageStream);
            return message;
        } catch (XMLParserException e) {
            log.error("Error unmarshalling message from input stream", e);
            throw new MessageDecodingException("Error unmarshalling message from input stream", e);
        } catch (UnmarshallingException e) {
            log.error("Error unmarshalling message from input stream", e);
            throw new MessageDecodingException("Error unmarshalling message from input stream", e);
        }
    }

}