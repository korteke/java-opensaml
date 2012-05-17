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

package org.opensaml.saml.saml2.binding.decoding;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import javax.servlet.http.HttpServletRequest;

import net.shibboleth.utilities.java.support.codec.Base64Support;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.messaging.decoder.servlet.BaseHttpServletRequestXmlMessageDecoder;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.decoding.SAMLMessageDecoder;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * SAML 2.0 HTTP Redirect decoder using the DEFLATE encoding method.
 * 
 * This decoder only supports DEFLATE compression.
 */
public class HTTPRedirectDeflateDecoder extends BaseHttpServletRequestXmlMessageDecoder<SAMLObject> 
    implements SAMLMessageDecoder {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(HTTPRedirectDeflateDecoder.class);

    /** {@inheritDoc} */
    public String getBindingURI() {
        return SAMLConstants.SAML2_REDIRECT_BINDING_URI;
    }

    /** {@inheritDoc} */
    protected void doDecode() throws MessageDecodingException {
        MessageContext<SAMLObject> messageContext = new MessageContext<SAMLObject>();
        HttpServletRequest request = getHttpServletRequest();

        String relayState = request.getParameter("RelayState");
        //TODO what to do with storing RelayState
        log.debug("Decoded RelayState: {}", relayState);

        InputStream samlMessageIns;
        if (!Strings.isNullOrEmpty(request.getParameter("SAMLRequest"))) {
            samlMessageIns = decodeMessage(request.getParameter("SAMLRequest"));
        } else if (!Strings.isNullOrEmpty(request.getParameter("SAMLResponse"))) {
            samlMessageIns = decodeMessage(request.getParameter("SAMLResponse"));
        } else {
            throw new MessageDecodingException(
                    "No SAMLRequest or SAMLResponse query path parameter, invalid SAML 2 HTTP Redirect message");
        }

        SAMLObject samlMessage = (SAMLObject) unmarshallMessage(samlMessageIns);
        messageContext.setMessage(samlMessage);
        log.debug("Decoded SAML message");

        //TODO
        //populateMessageContext(samlMsgCtx);
        
        setMessageContext(messageContext);
    }

    /**
     * Base64 decodes the SAML message and then decompresses the message.
     * 
     * @param message Base64 encoded, DEFALTE compressed, SAML message
     * 
     * @return the SAML message
     * 
     * @throws MessageDecodingException thrown if the message can not be decoded
     */
    protected InputStream decodeMessage(String message) throws MessageDecodingException {
        log.debug("Base64 decoding and inflating SAML message");

        byte[] decodedBytes = Base64Support.decode(message);
        if(decodedBytes == null){
            log.error("Unable to Base64 decode incoming message");
            throw new MessageDecodingException("Unable to Base64 decode incoming message");
        }
        
        try {
            ByteArrayInputStream bytesIn = new ByteArrayInputStream(decodedBytes);
            InflaterInputStream inflater = new InflaterInputStream(bytesIn, new Inflater(true));
            return inflater;
        } catch (Exception e) {
            log.error("Unable to Base64 decode and inflate SAML message", e);
            throw new MessageDecodingException("Unable to Base64 decode and inflate SAML message", e);
        }
    }
}