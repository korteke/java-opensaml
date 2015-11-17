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

package org.opensaml.soap.soap11.messaging.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.error.TypedMessageErrorHandler;
import org.opensaml.soap.messaging.SOAPMessagingSupport;
import org.opensaml.soap.soap11.Fault;
import org.opensaml.soap.util.SOAPSupport;
import org.opensaml.soap.wsaddressing.messaging.WSAddressingContext;

/**
 * An implementation of {@link TypedMessageErrorHandler} which simply decorates the supplied
 * {@link MessageContext} with a {@link Fault} and optionally a WS-Addressing Action URI.
 * 
 * <p>
 * The actual SOAP 1.1. fault is assumed to be emitted later, perhaps directly by a specialized
 * fault-aware {@link org.opensaml.messaging.encoder.MessageEncoder}.
 * </p>
 * 
 * @param <MessageType> the type of message supported
 */
public class SOAP11FaultContextDecoratingErrorHandler<MessageType> implements TypedMessageErrorHandler<MessageType> {
    
    /** The handled type of Throwable. */
    @Nonnull private Class<? extends Throwable> handledThrowable;
    
    /** The fault code to emit. */
    @Nonnull private QName faultCode;
    
    /** The fault string to emit. */
    @Nonnull private String faultString;
    
    /** The fault actor to emit. */
    @Nullable private String faultActor;
    
    /** The WS-Addressing fault action URI to supply via the context. */
    @Nullable private String wsAddressingActionURI;
    
    /**
     * Constructor.
     *
     * @param throwable the handled type of {@link Throwable}
     * @param code the fault code
     * @param message the fault string
     */
    public SOAP11FaultContextDecoratingErrorHandler(@Nonnull final Class<? extends Throwable> throwable, 
            @Nonnull final QName code, @Nonnull final String message) {
        this(throwable, code, message, null, null);
    }
    
    /**
     * Constructor.
     *
     * @param throwable the handled type of {@link Throwable}
     * @param code the fault code
     * @param message the fault string
     * @param actor the fault actor
     * @param addressingActionURI the WS-Addressing action URI
     */
    public SOAP11FaultContextDecoratingErrorHandler(@Nonnull final Class<? extends Throwable> throwable, 
            @Nonnull final QName code, @Nonnull final String message, @Nullable final String actor, 
            @Nullable final String addressingActionURI) {
        super();
        handledThrowable = Constraint.isNotNull(throwable, "Handled Throwable type cannot be null");
        faultCode = Constraint.isNotNull(code, "Fault code cannot be null");
        faultString = Constraint.isNotNull(StringSupport.trim(message), "Fault string cannot be null or empty");
        faultActor = StringSupport.trimOrNull(actor);
        wsAddressingActionURI = StringSupport.trimOrNull(addressingActionURI);
    }

    /** {@inheritDoc} */
    public boolean handlesError(@Nonnull final Throwable t) {
        return handledThrowable.isInstance(t);
    }
    
    /** {@inheritDoc} */
    public boolean handleError(@Nonnull final Throwable t, @Nonnull final MessageContext<MessageType> messageContext) {
        //TODO can support details?
        //TODO add Function<MessageContext,Fault> support?
        Fault fault = SOAPSupport.buildSOAP11Fault(faultCode, faultString, faultActor, null, null);
        
        SOAPMessagingSupport.registerSOAP11Fault(messageContext, fault);
        
        if (wsAddressingActionURI != null) {
            messageContext.getSubcontext(WSAddressingContext.class, true).setFaultActionURI(wsAddressingActionURI);
        }
        
        return true;
    }

}
