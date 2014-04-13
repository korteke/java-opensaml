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

package org.opensaml.saml.saml1.binding.decoding.impl;

import java.io.ByteArrayInputStream;

import javax.servlet.http.HttpServletRequest;

import net.shibboleth.utilities.java.support.codec.Base64Support;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.messaging.decoder.servlet.BaseHttpServletRequestXMLMessageDecoder;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.binding.decoding.SAMLMessageDecoder;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.ResponseAbstractType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAML 1.X HTTP POST message decoder.
 */
public class HTTPPostDecoder extends BaseHttpServletRequestXMLMessageDecoder<SAMLObject> implements SAMLMessageDecoder {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(HTTPPostDecoder.class);

    /** {@inheritDoc} */
    public String getBindingURI() {
        return SAMLConstants.SAML1_POST_BINDING_URI;
    }

    /** {@inheritDoc} */
    protected void doDecode() throws MessageDecodingException {
        MessageContext<SAMLObject> messageContext = new MessageContext<SAMLObject>();
        HttpServletRequest request = getHttpServletRequest();

        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            throw new MessageDecodingException("This message decoder only supports the HTTP POST method");
        }

        String relayState = request.getParameter("TARGET");
        log.debug("Decoded SAML relay state (TARGET parameter) of: {}", relayState);
        SAMLBindingSupport.setRelayState(messageContext, relayState);

        String base64Message = request.getParameter("SAMLResponse");
        byte[] decodedBytes = Base64Support.decode(base64Message);
        if (decodedBytes == null) {
            log.error("Unable to Base64 decode SAML message");
            throw new MessageDecodingException("Unable to Base64 decode SAML message");
        }

        SAMLObject inboundMessage = (SAMLObject) unmarshallMessage(new ByteArrayInputStream(decodedBytes));
        messageContext.setMessage(inboundMessage);
        log.debug("Decoded SAML message");

        populateBindingContext(messageContext);
        
        setMessageContext(messageContext);
    }
    
    /**
     * Populate the context which carries information specific to this binding.
     * 
     * @param messageContext the current message context
     */
    protected void populateBindingContext(MessageContext<SAMLObject> messageContext) {
        SAMLBindingContext bindingContext = messageContext.getSubcontext(SAMLBindingContext.class, true);
        bindingContext.setBindingUri(getBindingURI());
        bindingContext.setHasBindingSignature(false);
        bindingContext.setIntendedDestinationEndpointURIRequired(
                messageContext.getMessage() instanceof ResponseAbstractType);
    }

}