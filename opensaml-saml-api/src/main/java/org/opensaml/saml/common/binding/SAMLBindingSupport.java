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

package org.opensaml.saml.common.binding;

import java.net.URI;
import java.net.URISyntaxException;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.context.SamlPeerEntityContext;
import org.opensaml.saml.common.context.SamlEndpointContext;
import org.opensaml.saml.common.context.SamlProtocolContext;
import org.opensaml.saml.saml1.core.Response;
import org.opensaml.saml.saml2.core.StatusResponseType;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/** A support class for SAML binding operations. */
public final class SAMLBindingSupport {
    
    /** Logger. */
    private static final Logger LOG = LoggerFactory.getLogger(SAMLBindingSupport.class);

    /** Constructor. */
    private SAMLBindingSupport() { }
    
    /**
     * Get the SAML protocol relay state from a message context.
     * 
     * @param messageContext the message context on which to operate
     * @return the relay state or null
     */
    public static String getRelayState(MessageContext<SAMLObject> messageContext) {
        SamlProtocolContext protocolContext = messageContext.getSubcontext(SamlProtocolContext.class);
        if (protocolContext == null) { 
            return null;
        } else {
            return protocolContext.getRelayState();
        }
    }
    
    /**
     * Checks that the relay state is 80 bytes or less if it is not null.
     * 
     * @param relayState relay state to check
     * 
     * @return true if the relay state is not empty and is less than 80 bytes
     */
    public static boolean checkRelayState(String relayState) {
        if (!Strings.isNullOrEmpty(relayState)) {
            if (relayState.getBytes().length > 80) {
                LOG.warn("Relay state exceeds 80 bytes, some application may not support this.");
            }

            return true;
        }

        return false;
    }
    
    /**
     * Gets the response URL from the relying party endpoint. If the SAML message is a 
     * {@link StatusResponseType} and the relying party endpoint contains a response location 
     * then that location is returned otherwise the normal endpoint location is returned.
     * 
     * @param messageContext current message context
     * 
     * @return response URL from the relying party endpoint
     * 
     * @throws BindingException throw if no relying party endpoint is available
     */
    public static URI getEndpointURL(MessageContext<SAMLObject> messageContext) throws BindingException {
        SamlPeerEntityContext peerContext = messageContext.getSubcontext(SamlPeerEntityContext.class, false);
        Constraint.isNotNull(peerContext, "Message context contained no PeerEntityContext");
        SamlEndpointContext endpointContext = peerContext.getSubcontext(SamlEndpointContext.class, false);
        Constraint.isNotNull(endpointContext, "PeerEntityContext contained no SamlEndpointContext");
        
        Endpoint endpoint = endpointContext.getEndpoint();
        
        if (endpoint == null) {
            throw new BindingException("Endpoint for relying party was null.");
        }

        SAMLObject message = messageContext.getMessage();
        if ((message instanceof StatusResponseType || message instanceof Response) 
                && !Strings.isNullOrEmpty(endpoint.getResponseLocation())) {
            try {
                return new URI(endpoint.getResponseLocation());
            } catch (URISyntaxException e) {
                throw new BindingException("The endpoint response location " + endpoint.getResponseLocation()
                        + " is not a valid URL", e);
            }
        } else {
            if (Strings.isNullOrEmpty(endpoint.getLocation())) {
                throw new BindingException("Relying party endpoint location was null or empty.");
            }
            try {
                return new URI(endpoint.getLocation());
            } catch (URISyntaxException e) {
                throw new BindingException("The endpoint location " + endpoint.getLocation()
                        + " is not a valid URL", e);
            }
        }
    }

}
