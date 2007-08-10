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

import org.apache.log4j.Logger;
import org.opensaml.log.Level;
import org.opensaml.ws.message.MessageContext;
import org.opensaml.ws.security.SecurityPolicy;
import org.opensaml.ws.security.SecurityPolicyException;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.parse.ParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Base class for message decoders.
 */
public abstract class BaseMessageDecoder implements MessageDecoder {

    /** Class logger. */
    private Logger log = Logger.getLogger(BaseMessageDecoder.class);

    /** Security policy used to check the decoded message. */
    private SecurityPolicy securityPolicy;

    /** Parser pool used to deserialize the message. */
    private ParserPool parserPool;

    /** Constructor. */
    public BaseMessageDecoder() {
        parserPool = new BasicParserPool();
    }

    /**
     * Constructor.
     * 
     * @param policy security policy to evaluate a message context against
     */
    public BaseMessageDecoder(SecurityPolicy policy) {
        securityPolicy = policy;
        parserPool = new BasicParserPool();
    }

    /**
     * Constructor.
     * 
     * @param policy security policy to evaluate a message context against
     * @param pool parser pool used to deserialize messages
     */
    public BaseMessageDecoder(SecurityPolicy policy, ParserPool pool) {
        if (pool == null) {
            throw new IllegalArgumentException("Parser pool may not be null");
        }

        securityPolicy = policy;
        parserPool = pool;
    }

    /** {@inheritDoc} */
    public void decode(MessageContext messageContext) throws MessageDecodingException, SecurityPolicyException {
        if (log.isDebugEnabled()) {
            log.debug("Beginning to decode message from inbound transport of type: "
                    + messageContext.getMessageInTransport().getClass().getName());
        }
        doDecode(messageContext);

        if (securityPolicy != null) {
            if (log.isDebugEnabled()) {
                log.debug("Evaluating securit policy for decoded message");
            }
            securityPolicy.evaluate(messageContext);
        }

        if (log.isDebugEnabled()) {
            log.debug("Successfully decoded message.");
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

    /** {@inheritDoc} */
    public SecurityPolicy getSecurityPolicy() {
        return securityPolicy;
    }

    /**
     * Sets the security policy that message contexts are evaluated against.
     * 
     * @param policy security policy that message contexts are evaluated against
     */
    protected void setSecurityPolicy(SecurityPolicy policy) {
        securityPolicy = policy;
    }

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
        if (log.isDebugEnabled()) {
            log.debug("Parsing message stream into DOM document");
        }
        try {
            Document messageDoc = parserPool.parse(messageStream);
            Element messageElem = messageDoc.getDocumentElement();

            if (log.isEnabledFor(Level.TRAIL)) {
                log.log(Level.TRAIL, "Resultant DOM message was:\n" + XMLHelper.nodeToString(messageElem));
            }

            if (log.isDebugEnabled()) {
                log.debug("Unmarshalling message DOM");
            }
            Unmarshaller unmarshaller = Configuration.getUnmarshallerFactory().getUnmarshaller(messageElem);
            if (unmarshaller == null) {
                log.error("Unable to unmarshall message, no unmarshaller registered for message element "
                        + XMLHelper.getNodeQName(messageElem));
            }

            XMLObject message = unmarshaller.unmarshall(messageElem);

            if (log.isDebugEnabled()) {
                log.debug("Message succesfully unmarshalled");
            }
            return message;
        } catch (XMLParserException e) {
            log.error("Encountered error parsing message into its DOM representation", e);
            throw new MessageDecodingException("Encountered error parsing message into its DOM representation", e);
        } catch (UnmarshallingException e) {
            log.error("Encountered error unmarshalling message from its DOM representation", e);
            throw new MessageDecodingException("Encountered error unmarshalling message from its DOM representation", 
                    e);
        }
    }
}