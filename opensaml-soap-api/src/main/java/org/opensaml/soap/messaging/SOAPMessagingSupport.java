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

package org.opensaml.soap.messaging;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.collection.LazyList;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.soap.messaging.context.InboundSOAPContext;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.ActorBearing;
import org.opensaml.soap.soap11.Envelope;
import org.opensaml.soap.soap11.Header;
import org.opensaml.soap.util.SOAPSupport;

/**
 * Support class for SOAP messaging.
 */
public final class SOAPMessagingSupport {
    
    /** Constructor. */
    private SOAPMessagingSupport() {}
    
    /**
     * Get the current {@link InboundSOAPContext} for the given {@link @link MessageContext}. 
     * 
     * @param messageContext the current message context
     * 
     * @return the current inbound SOAP context. May be null if autoCreate=false, otherwise will be non-null
     */
    @Nonnull public static InboundSOAPContext getInboundSOAPContext(@Nonnull final MessageContext messageContext) {
        Constraint.isNotNull(messageContext, "Message context cannot be null");
        return messageContext.getSubcontext(InboundSOAPContext.class, true);
    }

    /**
     * Get the current {@link SOAP11Context} for the given {@link @link MessageContext}. 
     * 
     * @param messageContext the current message context
     * @param autoCreate whether to auto-create the context if it does not exist
     * 
     * @return the current SOAP 1.1 context. May be null if autoCreate=false, otherwise will be non-null
     */
    @Nullable public static SOAP11Context getSOAP11Context(@Nonnull final MessageContext messageContext,
            boolean autoCreate) {
        Constraint.isNotNull(messageContext, "Message context cannot be null");
        return messageContext.getSubcontext(SOAP11Context.class, autoCreate);
    }

    /**
     * Register a header as understood.
     * 
     * @param msgContext the current message context
     * @param header the header that was understood
     */
    public static void registerUnderstoodHeader(@Nonnull final MessageContext<? extends XMLObject> msgContext, 
            @Nonnull final XMLObject header) {
        InboundSOAPContext inboundContext = getInboundSOAPContext(msgContext);
        
        inboundContext.getUnderstoodHeaders().add(header);
    }
    
    /**
     * Check whether a header was understood.
     * 
     * @param msgContext the current message context
     * @param header the header that is to be checked for understanding
     * @return true if header was understood, false otherwise
     */
    public static boolean checkUnderstoodHeader(@Nonnull final MessageContext<? extends XMLObject> msgContext,
            @Nonnull final XMLObject header) {
        InboundSOAPContext inboundContext = getInboundSOAPContext(msgContext);
        
        return inboundContext.getUnderstoodHeaders().contains(header);
    }
    
    /**
     * Determine whether the message represented by the message context 
     * contains a SOAP Envelope.
     * 
     * @param messageContext the current message context
     * @return true iff the message context contains a SOAP Envelope
     */
    public static boolean isSOAPMessage(@Nonnull final MessageContext<? extends XMLObject> messageContext) {
        Constraint.isNotNull(messageContext, "Message context cannot be null");
        
        XMLObject inboundMessage = messageContext.getMessage();
        if (inboundMessage == null) {
            return false;
        }
        // SOAP 1.1 Envelope
        if (inboundMessage instanceof Envelope) {
            return true;
        }
        //TODO SOAP 1.2 support when object providers are implemented
        return false;
    }
    
    /**
     * Get a header block from the SOAP envelope contained within the specified message context's
     * SOAP subcontext.
     * 
     * @param messageContext the message context being processed
     * @param headerName the name of the header block to return 
     * 
     * @return the list of matching header blocks
     */
    @Nonnull public static List<XMLObject> getInboundHeaderBlock(
            @Nonnull final MessageContext<? extends XMLObject> messageContext, @Nonnull final QName headerName) {
            
        final InboundSOAPContext inboundContext = getInboundSOAPContext(messageContext);
        
        return getHeaderBlock(messageContext, headerName, 
                inboundContext.getNodeActors(), inboundContext.isFinalDestination());
    }
    
    /**
     * Get a header block from the SOAP envelope contained within the specified message context's
     * SOAP subcontext.
     * 
     * @param messageContext the message context being processed
     * @param headerName the name of the header block to return 
     * 
     * @return the list of matching header blocks
     */
    @Nonnull public static List<XMLObject> getOutboundHeaderBlock(
            @Nonnull final MessageContext<? extends XMLObject> messageContext, @Nonnull final QName headerName) {
            
        return getHeaderBlock(messageContext, headerName, null, true);
    }
    
    /**
     * Get a header block from the SOAP envelope contained within the specified message context's
     * SOAP subcontext.
     * 
     * @param messageContext the message context being processed
     * @param headerName the name of the header block to return 
     * @param targetNodes the explicitly specified SOAP node actors (1.1) or roles (1.2) for which the header is desired
     * @param isFinalDestination true specifies that headers targeted for message final destination should be returned,
     *          false means they should not be returned
     *          
     * @return the list of matching header blocks
     */
    @Nonnull public static List<XMLObject> getHeaderBlock(
            @Nonnull final MessageContext messageContext, @Nonnull final QName headerName,
            @Nullable Set<String> targetNodes, boolean isFinalDestination) {
        Constraint.isNotNull(messageContext, "Message context cannot be null");
        
        final SOAP11Context soap11 = getSOAP11Context(messageContext, false);
        
        // SOAP 1.1 Envelope
        if (soap11 != null && soap11.getEnvelope() != null) {
            return getSOAP11HeaderBlock(soap11.getEnvelope(), headerName, targetNodes, isFinalDestination);
        }
        
        //TODO SOAP 1.2 support when object providers are implemented
        return Collections.emptyList();
    }
    
    /**
     * Get a header block from the SOAP 1.1 envelope.
     * 
     * @param envelope the SOAP 1.1 envelope to process 
     * @param headerName the name of the header block to return 
     * @param targetNodes the explicitly specified SOAP node actors for which the header is desired
     * @param isFinalDestination true specifies that headers targeted for message final destination should be returned,
     *          false specifies they should not be returned
     * @return the list of matching header blocks
     */
    @Nonnull public static List<XMLObject> getSOAP11HeaderBlock(@Nonnull final Envelope envelope,
            @Nonnull final QName headerName, @Nullable final Set<String> targetNodes, boolean isFinalDestination) {
        Constraint.isNotNull(envelope, "Envelope cannot be null");
        Constraint.isNotNull(headerName, "Header name cannot be null");
        
        Header envelopeHeader = envelope.getHeader();
        if (envelopeHeader == null) {
            return Collections.emptyList();
        }
        
        LazyList<XMLObject> headers = new LazyList<XMLObject>();
        for (XMLObject header : envelopeHeader.getUnknownXMLObjects(headerName)) {
            if (isSOAP11HeaderTargetedToNode(header, targetNodes, isFinalDestination)) {
                headers.add(header);
            }
        }
        
        return headers;
    }
    
    /**
     * Evaluate whether the specified header block is targeted to a SOAP 1.1 node given the specified 
     * parameters.
     * 
     * @param header the header to evaluate
     * @param nodeActors the explicitly specified node actors for which the header is desired
     * @param isFinalDestination true specifies that headers targeted for message final destination should be returned,
     *          false specifies they should not be returned
     * @return the list of matching header blocks
     */
    public static boolean isSOAP11HeaderTargetedToNode(@Nonnull final XMLObject header,
            @Nullable final Set<String> nodeActors, boolean isFinalDestination) {
        String headerActor = SOAPSupport.getSOAP11ActorAttribute(header);
        if (headerActor == null) {
            if (isFinalDestination) {
                return true;
            }
        } else if (ActorBearing.SOAP11_ACTOR_NEXT.equals(headerActor)) {
            return true;
        } else if (nodeActors != null && nodeActors.contains(headerActor)) {
            return true;
        }
        return false;
    }
    
    /**
     * Add a header block to the SOAP envelope contained within the specified message context's
     * SOAP subcontext.
     * 
     * @param messageContext the message context being processed
     * @param headerBlock the header block to add
     */
    public static void addHeaderBlock(@Nonnull final MessageContext messageContext,
            @Nonnull final XMLObject headerBlock) {
        Constraint.isNotNull(messageContext, "Message context cannot be null");
        
        // SOAP 1.1 Envelope
        final SOAP11Context soap11 = getSOAP11Context(messageContext, false);
        
        if (soap11 != null && soap11.getEnvelope() != null) {
            addSOAP11HeaderBlock(soap11.getEnvelope(), headerBlock);
        } else {
            //TODO SOAP 1.2 support when object providers are implemented
            throw new IllegalArgumentException("Message context did not contain a SOAP Envelope");
        }
    }

    /**
     * Add a header to the SOAP 1.1 Envelope.
     * 
     * @param envelope the SOAP 1.1 envelope to process
     * @param headerBlock the header to add
     */
    public static void addSOAP11HeaderBlock(@Nonnull final Envelope envelope, @Nonnull final XMLObject headerBlock) {
        Constraint.isNotNull(envelope, "Envelope cannot be null");
        Constraint.isNotNull(headerBlock, "Header block cannot be null");
        
        Header envelopeHeader = envelope.getHeader();
        if (envelopeHeader == null) {
            envelopeHeader = (Header) XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(
                    Header.DEFAULT_ELEMENT_NAME).buildObject(Header.DEFAULT_ELEMENT_NAME);
            envelope.setHeader(envelopeHeader);
        }
        
        envelopeHeader.getUnknownXMLObjects().add(headerBlock);
    }
    
}
