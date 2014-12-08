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

package org.opensaml.saml.common.binding.security.impl;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.net.BasicURLComparator;
import net.shibboleth.utilities.java.support.net.URIComparator;
import net.shibboleth.utilities.java.support.net.URIException;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.messaging.MessageException;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Message handler which checks the validity of the SAML protocol message receiver 
 * endpoint against requirements indicated in the message.
 */
public class ReceivedEndpointSecurityHandler extends AbstractMessageHandler {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(ReceivedEndpointSecurityHandler.class);
    
    /** The URI comparator to use in performing the validation. */
    @Nonnull private URIComparator uriComparator;
    
    /** The HttpServletRequest being processed. */
    @NonnullAfterInit private HttpServletRequest httpServletRequest;

    /** Constructor. */
    public ReceivedEndpointSecurityHandler() {
        uriComparator = new BasicURLComparator();
    }

    /**
     * Get the URI comparator instance to use.
     * 
     * @return the uriComparator.
     */
    @Nonnull public URIComparator getURIComparator() {
        return uriComparator;
    }

    /**
     * Set the URI comparator instance to use.
     * 
     * @param comparator the new URI comparator to use
     */
    public void setURIComparator(@Nonnull final URIComparator comparator) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        uriComparator = Constraint.isNotNull(comparator, "URIComparator cannot be null");
    }
    
    /**
     * Get the HTTP servlet request being processed.
     * 
     * @return Returns the request.
     */
    @NonnullAfterInit public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    /**
     * Set the HTTP servlet request being processed.
     * 
     * @param request The to set.
     */
    public void setHttpServletRequest(@Nonnull final HttpServletRequest request) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        httpServletRequest = Constraint.isNotNull(request, "HttpServletRequest cannot be null");
    }

    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (uriComparator == null) {
            throw new ComponentInitializationException("URIComparator cannot be null");
        } else if (httpServletRequest == null) {
            throw new ComponentInitializationException("HttpServletRequest cannot be null");
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        checkEndpointURI(messageContext, getURIComparator());
    }
    
    /**
     * Compare the message endpoint URI's specified.
     * 
     * <p>The comparison is performed using the specified instance of {@link URIComparator}.</p>
     * 
     * @param messageDestination the intended message destination endpoint URI
     * @param receiverEndpoint the endpoint URI at which the message was received
     * @param comparator the comparator instance to use
     * 
     * @return true if the endpoints are equivalent, false otherwise
     * 
     * @throws URIException if one of the URI's to evaluate is invalid 
     */
    protected boolean compareEndpointURIs(@Nonnull @NotEmpty final String messageDestination, 
            @Nonnull @NotEmpty final String receiverEndpoint, 
            @Nonnull final URIComparator comparator) throws URIException {
        Constraint.isNotNull(messageDestination, "Message destination URI was null");
        Constraint.isNotNull(receiverEndpoint, "Receiver endpoint URI was null");
        Constraint.isNotNull(comparator, "URIComparator was null");
        return comparator.compare(messageDestination, receiverEndpoint);
    }
    
    /**
     * Check the validity of the SAML protocol message receiver endpoint against
     * requirements indicated in the message.
     * 
     * @param messageContext current message context
     * @param comparator the URI comparator instance to use, if null an internal default will be used
     * 
     * @throws MessageHandlerException thrown if the message was received at an endpoint consistent 
     *              with message requirements, or if there is a problem decoding and processing
     *              the message Destination or receiver endpoint information
     */
    protected void checkEndpointURI(@Nonnull final MessageContext<SAMLObject> messageContext, 
            @Nonnull final URIComparator comparator) throws MessageHandlerException {
        Constraint.isNotNull(comparator, "URIComparator may not be null");
        log.debug("{} Checking SAML message intended destination endpoint against receiver endpoint", getLogPrefix());
        
        String messageDestination;
        try {
            messageDestination = StringSupport.trimOrNull(
                    SAMLBindingSupport.getIntendedDestinationEndpointURI(messageContext));
        } catch (MessageException e) {
            throw new MessageHandlerException("Error obtaining message intended destination endpoint URI", e);
        }
        
        final boolean bindingRequires = SAMLBindingSupport.isIntendedDestinationEndpointURIRequired(messageContext);
        
        if (messageDestination == null) {
            if (bindingRequires) {
                log.error("{} SAML message intended destination endpoint URI required by binding was empty",
                        getLogPrefix());
                throw new MessageHandlerException(
                        "SAML message intended destination (required by binding) was not present");
            } else {
                log.debug("{} SAML message intended destination endpoint was empty, not required by binding, skipping",
                        getLogPrefix());
                return;
            }
        }
        
        String receiverEndpoint;
        try {
            receiverEndpoint = StringSupport.trimOrNull(
                    SAMLBindingSupport.getActualReceiverEndpointURI(messageContext, getHttpServletRequest()));
        } catch (MessageException e) {
            throw new MessageHandlerException("Error obtaining message received endpoint URI", e);
        }
        
        log.debug("{} Intended message destination endpoint: {}", getLogPrefix(), messageDestination);
        log.debug("{} Actual message receiver endpoint: {}", getLogPrefix(), receiverEndpoint);
        
        boolean matched;
        try {
            matched = compareEndpointURIs(messageDestination, receiverEndpoint, comparator);
        } catch (URIException e) {
            throw new MessageHandlerException("Error comparing endpoint URI's", e);
        }
        if (!matched) {
            log.error("{} SAML message intended destination endpoint '{}' did not match the recipient endpoint '{}'",
                    getLogPrefix(), messageDestination, receiverEndpoint);
            throw new MessageHandlerException("SAML message failed received endpoint check");
        } else {
            log.debug("{} SAML message intended destination endpoint matched recipient endpoint", getLogPrefix());
        }
    }

}