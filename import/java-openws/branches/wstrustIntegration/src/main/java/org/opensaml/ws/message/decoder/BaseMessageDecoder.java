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

package org.opensaml.ws.message.decoder;

import java.io.InputStream;

import org.opensaml.ws.message.MessageContext;
import org.opensaml.ws.security.SecurityPolicy;
import org.opensaml.ws.security.SecurityPolicyResolver;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.parse.ParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.util.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Base class for message decoders.
 */
public abstract class BaseMessageDecoder implements MessageDecoder {
    
    /** Used to log protocol messages. */
    private Logger protocolMessageLog = LoggerFactory.getLogger("PROTOCOL_MESSAGE");

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(BaseMessageDecoder.class);

    /** Parser pool used to deserialize the message. */
    private ParserPool parserPool;

    /** Constructor. */
    public BaseMessageDecoder() {
        parserPool = new BasicParserPool();
    }

    /**
     * Constructor.
     * 
     * @param pool parser pool used to deserialize messages
     */
    public BaseMessageDecoder(ParserPool pool) {
        if (pool == null) {
            throw new IllegalArgumentException("Parser pool may not be null");
        }

        parserPool = pool;
    }

    /** {@inheritDoc} */
    public void decode(MessageContext messageContext) throws MessageDecodingException, SecurityException {
        log.debug("Beginning to decode message from inbound transport of type: {}", messageContext
                .getInboundMessageTransport().getClass().getName());
        doDecode(messageContext);

        SecurityPolicyResolver policyResolver = messageContext.getSecurityPolicyResolver();
        if (policyResolver != null) {
            Iterable<SecurityPolicy> securityPolicies = policyResolver.resolve(messageContext);
            if (securityPolicies != null) {
                for (SecurityPolicy policy : securityPolicies) {
                    if (policy != null) {
                        log.debug("Evaluating security policy of type '{}' for decoded message", policy.getClass()
                                .getName());
                        policy.evaluate(messageContext);
                    }
                }
            } else {
                log.debug("No security policy resolved for this message context, no security policy evaluation attempted");
            }
        } else {
            log.debug("No security policy resolver attached to this message context, no security policy evaluation attempted");
        }

        log.debug("Successfully decoded message.");
        if(protocolMessageLog.isDebugEnabled() && messageContext.getInboundMessage() != null){
            protocolMessageLog.debug("\n" + XMLHelper.prettyPrintXML(messageContext.getInboundMessage().getDOM()));
        }
    }

    /**
     * Decodes a message, updating the message context. Security policy evaluation is handled outside this method.
     * 
     * @param messageContext current message context
     * 
     * @throws MessageDecodingException thrown if there is a problem decoding the message
     */
    protected abstract void doDecode(MessageContext messageContext) throws MessageDecodingException;

    /**
     * Gets the parser pool used to deserialize incomming messages.
     * 
     * @return parser pool used to deserialize incomming messages
     */
    protected ParserPool getParserPool() {
        return parserPool;
    }

    /**
     * Sets the parser pool used to deserialize incomming messages.
     * 
     * @param pool parser pool used to deserialize incomming messages
     */
    protected void setParserPool(ParserPool pool) {
        if (pool == null) {
            throw new IllegalArgumentException("Parser pool may not be null");
        }
        parserPool = pool;
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
        log.debug("Parsing message stream into DOM document");

        try {
            Document messageDoc = parserPool.parse(messageStream);
            Element messageElem = messageDoc.getDocumentElement();

            if (log.isTraceEnabled()) {
                log.trace("Resultant DOM message was:\n{}", XMLHelper.nodeToString(messageElem));
            }

            log.debug("Unmarshalling message DOM");
            Unmarshaller unmarshaller = Configuration.getUnmarshallerFactory().getUnmarshaller(messageElem);
            if (unmarshaller == null) {
                log.error("Unable to unmarshall message, no unmarshaller registered for message element "
                        + XMLHelper.getNodeQName(messageElem));
                throw new MessageDecodingException(
                        "Unable to unmarshall message, no unmarshaller registered for message element "
                                + XMLHelper.getNodeQName(messageElem));
            }

            XMLObject message = unmarshaller.unmarshall(messageElem);

            log.debug("Message succesfully unmarshalled");
            return message;
        } catch (XMLParserException e) {
            log.error("Encountered error parsing message into its DOM representation", e);
            throw new MessageDecodingException("Encountered error parsing message into its DOM representation", e);
        } catch (UnmarshallingException e) {
            log.error("Encountered error unmarshalling message from its DOM representation", e);
            throw new MessageDecodingException("Encountered error unmarshalling message from its DOM representation", e);
        }
    }
}