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

import javax.servlet.http.HttpServletRequest;

import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.messaging.decoder.servlet.BaseHttpServletRequestXmlMessageDecoder;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.decoding.SAMLMessageDecoder;
import org.opensaml.saml.common.messaging.context.SamlProtocolContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAML 1.X HTTP Artifact message decoder.
 * 
 * <strong>NOTE: This decoder is not yet implemented.</strong>
 */
public class HTTPArtifactDecoder extends BaseHttpServletRequestXmlMessageDecoder<SAMLObject> 
        implements SAMLMessageDecoder {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(HTTPArtifactDecoder.class);

    /** {@inheritDoc} */
    public String getBindingURI() {
        return SAMLConstants.SAML1_ARTIFACT_BINDING_URI;
    }

    /** {@inheritDoc} */
    protected void doDecode() throws MessageDecodingException {
        MessageContext<SAMLObject> messageContext = new MessageContext<SAMLObject>();
        HttpServletRequest request = getHttpServletRequest();
        
        decodeTarget(messageContext, request);
        processArtifacts(messageContext, request);

        //TODO
        //populateMessageContext(samlMsgCtx);
        
        setMessageContext(messageContext);
    }

    /**
     * Decodes the TARGET parameter and adds it to the message context.
     * 
     * @param messageContext current message context
     * @param request current servlet request
     * 
     * @throws MessageDecodingException thrown if there is a problem decoding the TARGET parameter.
     */
    protected void decodeTarget(MessageContext messageContext, HttpServletRequest request) 
            throws MessageDecodingException {
        String target = StringSupport.trim(request.getParameter("TARGET"));
        if (target == null) {
            log.error("URL TARGET parameter was missing or did not contain a value.");
            throw new MessageDecodingException("URL TARGET parameter was missing or did not contain a value.");
        }
        messageContext.getSubcontext(SamlProtocolContext.class, true).setRelayState(target);
    }

    /**
     * Process the incoming artifacts by decoding the artifacts, dereferencing them from the artifact source and 
     * storing the resulting response (with assertions) in the message context.
     * 
     * @param messageContext current message context
     * @param request current servlet request
     * 
     * @throws MessageDecodingException thrown if there is a problem decoding or dereferencing the artifacts
     */
    protected void processArtifacts(MessageContext messageContext, HttpServletRequest request) 
            throws MessageDecodingException {
        String[] encodedArtifacts = request.getParameterValues("SAMLart");
        if (encodedArtifacts == null || encodedArtifacts.length == 0) {
            log.error("URL SAMLart parameter was missing or did not contain a value.");
            throw new MessageDecodingException("URL SAMLart parameter was missing or did not contain a value.");
        }
        
        // TODO decode artifact(s); resolve issuer resolution endpoint; dereference using 
        // Request/AssertionArtifact(s) over synchronous backchannel binding;
        // store response as the inbound SAML message.
    }
    
}