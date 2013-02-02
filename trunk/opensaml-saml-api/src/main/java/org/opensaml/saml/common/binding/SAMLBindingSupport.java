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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.messaging.MessageException;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.servlet.HttpServletRequestContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.saml.common.messaging.context.SamlBindingContext;
import org.opensaml.saml.common.messaging.context.SamlEndpointContext;
import org.opensaml.saml.common.messaging.context.SamlPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SamlProtocolContext;
import org.opensaml.saml.saml2.core.StatusResponseType;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.util.net.BasicUrlComparator;
import org.opensaml.util.net.UriComparator;
import org.opensaml.util.net.UriException;
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
    @Nullable public static String getRelayState(@Nonnull final MessageContext<SAMLObject> messageContext) {
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
    public static boolean checkRelayState(@Nullable final String relayState) {
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
    @Nonnull public static URI getEndpointURL(@Nonnull final MessageContext<SAMLObject> messageContext) 
            throws BindingException {
        SamlPeerEntityContext peerContext = messageContext.getSubcontext(SamlPeerEntityContext.class, false);
        Constraint.isNotNull(peerContext, "Message context contained no PeerEntityContext");
        SamlEndpointContext endpointContext = peerContext.getSubcontext(SamlEndpointContext.class, false);
        Constraint.isNotNull(endpointContext, "PeerEntityContext contained no SamlEndpointContext");
        
        Endpoint endpoint = endpointContext.getEndpoint();
        
        if (endpoint == null) {
            throw new BindingException("Endpoint for relying party was null.");
        }

        SAMLObject message = messageContext.getMessage();
        if ((message instanceof org.opensaml.saml.saml2.core.StatusResponseType 
                || message instanceof org.opensaml.saml.saml1.core.Response) 
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
    
    /**
     * Sets the destination attribute on the outbound message if it is a {@link StatusResponseType} message.
     * 
     * @param outboundMessage outbound SAML message
     * @param endpointURL destination endpoint
     */
    public static void setSaml2ResponseDestination(@Nonnull final SAMLObject outboundMessage, 
            @Nullable final String endpointURL) {
        if (outboundMessage instanceof StatusResponseType) {
            ((StatusResponseType) outboundMessage).setDestination(endpointURL);
        }
    }
    
    /**
     * Determine whether the SAML message represented by the message context is digitally signed.
     * 
     * <p>
     * First the SAML protocol message is examined as to whether an XML signature is present.
     * If not, then the presence of a binding signature is evaluated by looking at 
     * {@link SamlBindingContext#isHasBindingSignature()}.
     * </p>
     * 
     * 
     * @param messageContext current message context
     * @return true if the message is considered to be digitally signed, false otherwise
     */
    public static boolean isMessageSigned(@Nonnull final MessageContext<SAMLObject> messageContext) {
        SAMLObject samlMessage = Constraint.isNotNull(messageContext.getMessage(),
                "SAML message was not present in message context");
        if (samlMessage instanceof SignableSAMLObject) {
            return ((SignableSAMLObject)samlMessage).isSigned();
        } else {
            SamlBindingContext bindingContext = messageContext.getSubcontext(SamlBindingContext.class, false);
            if (bindingContext != null) {
                return bindingContext.isHasBindingSignature();
            } else {
                return false;
            }
        }
    }
    
    /**
     * Determine whether the binding in use requires the presence within the message 
     * of information indicating the intended message destination endpoint URI.
     * 
     * @param messageContext current SAML message context
     * @return true if the intended message destination endpoint is required, false if not
     */
    public static boolean isIntendedDestinationEndpointUriRequired(
            @Nonnull final MessageContext<SAMLObject> messageContext) {
        SamlBindingContext bindingContext = messageContext.getSubcontext(SamlBindingContext.class, false);
        if (bindingContext == null) {
            return false;
        }
        return bindingContext.isIntendedDestinationEndpointUriRequired();
    }
    
    /**
     * Extract the message information which indicates to what receiver endpoint URI the
     * SAML message was intended to be delivered.
     * 
     * @param messageContext the SAML message context being processed
     * @return the value of the intended destination endpoint URI, or null if not present or empty
     * @throws MessageException thrown if the message is not an instance of SAML message that
     *              could be processed by the decoder
     */
    @Nullable public static String getIntendedDestinationEndpointUri(
            @Nonnull final MessageContext<SAMLObject> messageContext)  throws MessageException {
        SAMLObject samlMessage = Constraint.isNotNull(messageContext.getMessage(), 
                "SAML message was not present in message context");
        String messageDestination = null;
        //SAML 2 Request
        if (samlMessage instanceof org.opensaml.saml.saml2.core.RequestAbstractType) {
            org.opensaml.saml.saml2.core.RequestAbstractType request =  
                    (org.opensaml.saml.saml2.core.RequestAbstractType) samlMessage;
            messageDestination = StringSupport.trimOrNull(request.getDestination());
        //SAML 2 Response
        } else if (samlMessage instanceof org.opensaml.saml.saml2.core.StatusResponseType) {
            org.opensaml.saml.saml2.core.StatusResponseType response = 
                    (org.opensaml.saml.saml2.core.StatusResponseType) samlMessage;
            messageDestination = StringSupport.trimOrNull(response.getDestination());
        //SAML 1 Response
        } else if (samlMessage instanceof org.opensaml.saml.saml1.core.ResponseAbstractType) {
            org.opensaml.saml.saml1.core.ResponseAbstractType response = 
                    (org.opensaml.saml.saml1.core.ResponseAbstractType) samlMessage;
            messageDestination = StringSupport.trimOrNull(response.getRecipient());
        //SAML 1 Request
        } else if (samlMessage instanceof org.opensaml.saml.saml1.core.RequestAbstractType) {
            // don't treat as an error, just return null
            return null;
        } else {
            LOG.error("Invalid SAML message type encountered: {}", samlMessage.getElementQName().toString());
            throw new MessageException("Invalid SAML message type encountered");
        }
        return messageDestination;
        
    }
    
    /**
     * Extract the transport endpoint URI at which this message was received.
     * 
     * @param messageContext current message context
     * @return string representing the transport endpoint URI at which the current message was received
     * @throws MessageException thrown if the endpoint can not be extracted from the message
     *                              context and converted to a string representation
     */
    public static String getActualReceiverEndpointUri(@Nonnull final MessageContext<SAMLObject> messageContext) 
            throws MessageException {
        HttpServletRequestContext requestContext = messageContext.getSubcontext(HttpServletRequestContext.class, false);
        if (requestContext == null || requestContext.getHttpServletRequest() == null) {
            throw new MessageException("HttpServletRequest could not be obtained from message context");
        }
        
        return requestContext.getHttpServletRequest().getRequestURL().toString();
    }

    /**
     * Compare the message endpoint URI's specified.
     * 
     * <p>The comparison is performed using {@link BasicUrlComparator}.</p>
     * 
     * <p>Subclasses should override if binding-specific behavior is required.
     * In this case, see also {@link #getActualReceiverEndpointUri(SAMLMessageContext)}.</p>
     * 
     * @param messageDestination the intended message destination endpoint URI
     * @param receiverEndpoint the endpoint URI at which the message was received
     * 
     * @return true if the endpoints are equivalent, false otherwise
     * 
     * @throws UriException if one of the URI's to evaluate is invalid 
     */
    public static boolean compareEndpointUris(@Nonnull @NotEmpty final String messageDestination, 
            @Nonnull @NotEmpty final String receiverEndpoint) throws UriException {
        return compareEndpointUris(messageDestination, receiverEndpoint, new BasicUrlComparator());
    }
    
    /**
     * Compare the message endpoint URI's specified.
     * 
     * <p>The comparison is performed using the specified instance of {@link UriComparator}.</p>
     * 
     * @param messageDestination the intended message destination endpoint URI
     * @param receiverEndpoint the endpoint URI at which the message was received
     * @param comparator the comparator instance to use
     * 
     * @return true if the endpoints are equivalent, false otherwise
     * 
     * @throws UriException if one of the URI's to evaluate is invalid 
     */
    public static boolean compareEndpointUris(@Nonnull @NotEmpty final String messageDestination, 
            @Nonnull @NotEmpty final String receiverEndpoint, 
            @Nonnull final UriComparator comparator) throws UriException {
        Constraint.isNotNull(messageDestination, "Message destination URI was null");
        Constraint.isNotNull(receiverEndpoint, "Receiver endpoint URI was null");
        Constraint.isNotNull(comparator, "UriComparator was null");
        return comparator.compare(messageDestination, receiverEndpoint);
    }
    
    /**
     * Check the validity of the SAML protocol message receiver endpoint against
     * requirements indicated in the message.
     * 
     * @param messageContext current message context
     * @param comparator the URI comparator instance to use, if null an internal default will be used
     * 
     * @return true if the message was received at an endpoint consistent with message requirements, 
     *              false otherwise
     * 
     * @throws MessageException thrown if there is a problem decoding and processing
     *              the message Destination or receiver endpoint information
     */
    public static boolean checkEndpointUri(@Nonnull final MessageContext<SAMLObject> messageContext, 
            @Nullable final UriComparator comparator) throws MessageException {
        
        LOG.debug("Checking SAML message intended destination endpoint against receiver endpoint");
        
        String messageDestination = StringSupport.trimOrNull(getIntendedDestinationEndpointUri(messageContext));
        
        boolean bindingRequires = isIntendedDestinationEndpointUriRequired(messageContext);
        
        if (messageDestination == null) {
            if (bindingRequires) {
                LOG.error("SAML message intended destination endpoint URI required by binding was empty");
                throw new SecurityException("SAML message intended destination (required by binding) was not present");
            } else {
                LOG.debug("SAML message intended destination endpoint in message was empty, not required by binding, skipping");
                return true;
            }
        }
        
        String receiverEndpoint = StringSupport.trimOrNull(getActualReceiverEndpointUri(messageContext));
        
        LOG.debug("Intended message destination endpoint: {}", messageDestination);
        LOG.debug("Actual message receiver endpoint: {}", receiverEndpoint);
        
        boolean matched;
        try {
            if (comparator != null) {
                matched = compareEndpointUris(messageDestination, receiverEndpoint, comparator);
            } else {
                matched = compareEndpointUris(messageDestination, receiverEndpoint);
            }
        } catch (UriException e) {
            throw new MessageException("Error comparing endpoint URI's", e);
        }
        if (!matched) {
            LOG.error("SAML message intended destination endpoint '{}' did not match the recipient endpoint '{}'",
                    messageDestination, receiverEndpoint);
        } else {
            LOG.debug("SAML message intended destination endpoint matched recipient endpoint");
        }
        return matched;
    }

}