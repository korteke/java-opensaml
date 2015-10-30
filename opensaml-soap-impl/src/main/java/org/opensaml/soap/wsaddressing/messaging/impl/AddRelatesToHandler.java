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

package org.opensaml.soap.wsaddressing.messaging.impl;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.ContextDataLookupFunction;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.soap.messaging.SOAPMessagingSupport;
import org.opensaml.soap.wsaddressing.RelatesTo;
import org.opensaml.soap.wsaddressing.messaging.context.WSAddressingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler implementation that adds a wsa:RelatesTo header to the outbound SOAP envelope.
 */
public class AddRelatesToHandler extends AbstractMessageHandler {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(AddRelatesToHandler.class);
    
    /** Optional lookup function for obtaining the RelatesTo URI value. */
    private ContextDataLookupFunction<MessageContext, String> relatesToURILookup;
    
    /** The effective RelatesTo URI value to use. */
    private String relatesToURI;
    
    /** The effective RelatesTo RelationshipType attribute value to use. */
    private String relationshipType;
    
    /**
     * Get the function for looking up the RelatesTo URI value.
     * 
     * @return the lookup function
     */
    public ContextDataLookupFunction<MessageContext, String> getRelatesToURILookup() {
        return relatesToURILookup;
    }

    /**
     * Set the function for looking up the RelatesTo URI value.
     * 
     * @param lookup the lookup function
     */
    public void setRelatesToURILookup(ContextDataLookupFunction<MessageContext, String> lookup) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        relatesToURILookup = lookup;
    }

    /**
     * Get the RelatesTo RelationshipType attribute value to use.
     * 
     * @return the relationship type
     */
    public String getRelationshipType() {
        return relationshipType;
    }

    /**
     * Set the RelatesTo RelationshipType attribute value to use.
     * 
     * @param value the relationship type
     */
    public void setRelationshipType(String value) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        relationshipType = StringSupport.trimOrNull(value);
    }

    /** {@inheritDoc} */
    protected boolean doPreInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        if (!super.doPreInvoke(messageContext)) {
            return false;
        }
        
        WSAddressingContext addressing = messageContext.getSubcontext(WSAddressingContext.class, false);
        if (addressing != null) {
            relatesToURI = addressing.getRelatesToURI();
            if (relationshipType == null) {
                relationshipType = addressing.getRelatesToRelationshipType();
            }
        }
        
        if (relatesToURI == null && getRelatesToURILookup() != null) {
            relatesToURI = getRelatesToURILookup().apply(messageContext);
        }
        
        if (relatesToURI == null) {
            log.debug("No WS-Addressing RelatesTo value found in message context, skipping further processing");
            return false;
        }
        return true;
    }

    /** {@inheritDoc} */
    protected void doInvoke(MessageContext messageContext) throws MessageHandlerException {
        log.debug("Issuing WS-Addressing RelatesTo header with URI '{}' and RelationshipType '{}'", 
                relatesToURI, relationshipType);
        RelatesTo relatesTo = (RelatesTo) XMLObjectSupport.buildXMLObject(RelatesTo.ELEMENT_NAME);
        relatesTo.setValue(relatesToURI);
        relatesTo.setRelationshipType(relationshipType);
        SOAPMessagingSupport.addHeaderBlock(messageContext, relatesTo);
    }

}
