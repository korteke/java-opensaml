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

package org.opensaml.saml.saml1.binding.decoding;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.messaging.decoder.servlet.BaseHttpServletRequestXmlMessageDecoder;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.decoding.SAMLMessageDecoder;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.soap.soap11.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAML 1.1 HTTP SOAP 1.1 binding decoder.
 */
public class HTTPSOAP11Decoder extends BaseHttpServletRequestXmlMessageDecoder<SAMLObject> 
        implements SAMLMessageDecoder {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(HTTPSOAP11Decoder.class);

    /** {@inheritDoc} */
    public String getBindingURI() {
        return SAMLConstants.SAML1_SOAP11_BINDING_URI;
    }

    /** {@inheritDoc} */
    protected void doDecode() throws MessageDecodingException {
        MessageContext<SAMLObject> messageContext = new MessageContext<SAMLObject>();
        HttpServletRequest request = getHttpServletRequest();

        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            throw new MessageDecodingException("This message deocoder only supports the HTTP POST method");
        }

        log.debug("Unmarshalling SOAP message");
        Envelope soapMessage;
        try {
            soapMessage = (Envelope) unmarshallMessage(request.getInputStream());
        } catch (IOException e) {
            log.error("Unable to obtain input stream from HttpServletRequest", e);
            throw new MessageDecodingException("Unable to obtain input stream from HttpServletRequest", e);
        }
        //TODO expose the envelope somehow, via view context, etc ?

        List<XMLObject> soapBodyChildren = soapMessage.getBody().getUnknownXMLObjects();
        if (soapBodyChildren.size() < 1 || soapBodyChildren.size() > 1) {
            log.error("Unexpected number of children in the SOAP body, " + soapBodyChildren.size()
                    + ".  Unable to extract SAML message");
            throw new MessageDecodingException(
                    "Unexpected number of children in the SOAP body, unable to extract SAML message");
        }

        XMLObject incommingMessage = soapBodyChildren.get(0);
        if (!(incommingMessage instanceof SAMLObject)) {
            log.error("Unexpected SOAP body content.  Expected a SAML request but recieved {}", incommingMessage
                    .getElementQName());
            throw new MessageDecodingException("Unexpected SOAP body content.  Expected a SAML request but recieved "
                    + incommingMessage.getElementQName());
        }

        SAMLObject samlMessage = (SAMLObject) incommingMessage;
        log.debug("Decoded SOAP messaged which included SAML message of type {}", samlMessage.getElementQName());
        messageContext.setMessage(samlMessage);

        //TODO 
        //populateMessageContext(samlMsgCtx);
        
        setMessageContext(messageContext);
    }

}