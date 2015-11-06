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

package org.opensaml.soap.messaging.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.soap.messaging.SOAPMessagingSupport;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * Abstract base class for message handlers that generate SOAP headers.
 *
 * @param <MessageType> the type of message being processed.
 */
public abstract class AbstractHeaderGeneratingMessageHandler<MessageType> extends AbstractMessageHandler<MessageType> {
    
    /** The statically configured value for mustUnderstand. */
    private boolean mustUnderstand;
    
    /** Predicate strategy for evaluating mustUnderstand from the message context. */
    @Nullable private Predicate<MessageContext<MessageType>> mustUnderstandStrategy;
    
    /** The effective mustUnderstand value to use. */
    private boolean effectiveMustUnderstand;
    
    /** The statically configured value for target node (SOAP 1.1 actor or SOAP 1.2 role). */
    @Nullable private String targetNode;
    
    /** Function strategy for resolving target node from the message context. */
    @Nullable private Function<MessageContext<MessageType>, String> targetNodeStrategy;
    
    /** The effective target node value to use. */
    private String effectiveTargetNode;
    
    /**
     * Set the statically configured value for mustUnderstand.
     * 
     * @param flag true if header must be understood, false if not
     */
    public void setMustUnderstand(final boolean flag) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        mustUnderstand = flag;
    }
    
    /**
     * Set the predicate strategy for evaluating mustUnderstand from the message context.
     * 
     * @param strategy the predicate strategy
     */
    public void setMustUnderstandStrategy(@Nullable final Predicate<MessageContext<MessageType>> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        mustUnderstandStrategy = strategy;
    }
    
    /**
     * Get the effective value for mustUnderstand.
     * 
     * @return the effective value for mustUnderstand.
     */
    protected boolean isEffectiveMustUnderstand() {
        return effectiveMustUnderstand;
    }

    /**
     * Set the statically configured value for target node (SOAP 1.1 actor or SOAP 1.2 role).
     * 
     * @param node the target node, may be null
     */
    public void setTargetNode(@Nullable final String node) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        targetNode = StringSupport.trimOrNull(node);
    }
    
    /**
     * Set the predicate strategy for evaluating mustUnderstand from the message context.
     * 
     * @param strategy the predicate strategy
     */
    public void setTargetNodeStrategy(@Nullable final Function<MessageContext<MessageType>, String> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        targetNodeStrategy = strategy;
    }
    
    /**
     * Get the effective value for target node (SOAP 1.1 actor or SOAP 1.2 role).
     * 
     * @return the effective value for target node
     */
    protected String getEffectiveTargetNode() {
        return effectiveTargetNode;
    }
    
    /** {@inheritDoc} */
    protected boolean doPreInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        if (!super.doPreInvoke(messageContext)) {
            return false;
        }
        
        if (mustUnderstandStrategy != null) {
            effectiveMustUnderstand = mustUnderstandStrategy.apply(messageContext);
        } else {
            effectiveMustUnderstand = mustUnderstand;
        }
        
        if (targetNodeStrategy != null) {
            effectiveTargetNode = targetNodeStrategy.apply(messageContext);
        } else {
            effectiveTargetNode = targetNode;
        }
        
        return true;
    }
    
    /**
     * Decorate the header based on configured and/or resolved values.
     * 
     * @param messageContext the current message context
     * @param header the header to decorate
     */
    protected void decorateGeneratedHeader(@Nonnull final MessageContext messageContext, 
            @Nonnull final XMLObject header) {
        if (isEffectiveMustUnderstand()) {
            SOAPMessagingSupport.addMustUnderstand(messageContext, header, true);
        }
        if (getEffectiveTargetNode() != null) {
            SOAPMessagingSupport.addTargetNode(messageContext, header, getEffectiveTargetNode());
        }
    }
    
}
