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

package org.opensaml.soap.soap11.profile.impl;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.context.EventContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.CurrentOrPreviousEventLookup;
import org.opensaml.soap.soap11.Fault;
import org.opensaml.soap.soap11.FaultCode;
import org.opensaml.soap.soap11.FaultString;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.MessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;

/**
 * Action that builds a SOAP 1.1 {@link Fault} object in the outbound message context.
 * 
 * <p>Options allow for the creation of a {@link FaultString} either explicitly,
 * or via lookup strategy.</p>
 * 
 * @event {@link org.opensaml.profile.action.EventIds#PROCEED_EVENT_ID}
 */
public class AddSOAPFault extends AbstractProfileAction {

    /** Class logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(AddSOAPFault.class);
    
    /** Predicate determining whether detailed error information is permitted. */
    @Nonnull private Predicate<ProfileRequestContext> detailedErrorsCondition;

    /** Optional method to obtain fault code. */
    @Nullable private Function<ProfileRequestContext,QName> faultCodeLookupStrategy;

    /** Optional method to obtain a fault string. */
    @Nullable private Function<ProfileRequestContext,String> faultStringLookupStrategy;
    
    /** Default fault codes to insert. */
    @Nonnull @NonnullElements private QName defaultFaultCode;
    
    /** A default fault string to include. */
    @Nullable private String faultString;
    
    /** Whether to include detailed status information. */
    private boolean detailedErrors;
    
    /** Constructor. */
    public AddSOAPFault() {
        detailedErrorsCondition = Predicates.alwaysFalse();
        defaultFaultCode = FaultCode.SERVER;
        detailedErrors = false;
    }

    /**
     * Set the predicate used to determine the detailed errors condition.
     * 
     * @param condition predicate for detailed errors condition
     */
    public void setDetailedErrorsCondition(@Nonnull final Predicate<ProfileRequestContext> condition) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        detailedErrorsCondition = Constraint.isNotNull(condition, "Detailed errors condition cannot be null");
    }

    /**
     * Set the optional strategy used to obtain a faultcode to include.
     * 
     * @param strategy strategy used to obtain faultcode
     */
    public void setFaultCodeLookupStrategy(@Nullable final Function<ProfileRequestContext,QName> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        faultCodeLookupStrategy = strategy;
    }
    
    /**
     * Set the optional strategy used to obtain a faultstring to include.
     * 
     * @param strategy strategy used to obtain a fault string
     */
    public void setFaultStringLookupStrategy(@Nullable final Function<ProfileRequestContext,String> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        faultStringLookupStrategy = strategy;
    }
    
    /**
     * Set the default faultcode to insert.
     * 
     * @param code faultcode
     */
    public void setFaultCode(@Nonnull QName code) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        defaultFaultCode = Constraint.isNotNull(code, "Faultcode cannot be null");
    }
    
    /**
     * Set a default faultstring to use in the event that error detail is off,
     * or no specific message is obtained.
     * 
     * @param message default faultstring
     */
    public void setFaultString(@Nullable final String message) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        faultString = StringSupport.trimOrNull(message);
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        ComponentSupport.ifNotInitializedThrowUninitializedComponentException(this);

        detailedErrors = detailedErrorsCondition.apply(profileRequestContext);
        
        log.debug("{} Detailed errors are {}", getLogPrefix(), detailedErrors ? "enabled" : "disabled");

        if (profileRequestContext.getOutboundMessageContext() != null) {
            profileRequestContext.getOutboundMessageContext().setMessage(null);
        } else {
            profileRequestContext.setOutboundMessageContext(new MessageContext<Fault>());
        }
        
        return super.doPreExecute(profileRequestContext);
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {

        final XMLObjectBuilder<Fault> faultBuilder =
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Fault>getBuilderOrThrow(
                        Fault.DEFAULT_ELEMENT_NAME);
        final XMLObjectBuilder<FaultCode> faultCodeBuilder =
                XMLObjectProviderRegistrySupport.getBuilderFactory().<FaultCode>getBuilderOrThrow(
                        FaultCode.DEFAULT_ELEMENT_NAME);
        
        final Fault fault = faultBuilder.buildObject(Fault.DEFAULT_ELEMENT_NAME);
        profileRequestContext.getOutboundMessageContext().setMessage(fault);
       
        final FaultCode code = faultCodeBuilder.buildObject(FaultCode.DEFAULT_ELEMENT_NAME);
        if (faultCodeLookupStrategy != null) {
            final QName fc = faultCodeLookupStrategy.apply(profileRequestContext);
            if (fc == null) {
                code.setValue(defaultFaultCode);
            } else {
                code.setValue(fc);
            }
        } else {
            code.setValue(defaultFaultCode);
        }
        fault.setCode(code);

        // faultstring processing.
        if (!detailedErrors || faultStringLookupStrategy == null) {
            if (faultString != null) {
                log.debug("{} Setting faultstring to defaulted value", getLogPrefix());
                buildFaultString(fault, faultString);
            }
        } else if (faultStringLookupStrategy != null) {
            final String message = faultStringLookupStrategy.apply(profileRequestContext);
            if (message != null) {
                log.debug("{} Current state of request was mappable, setting faultstring to mapped value",
                        getLogPrefix());
                buildFaultString(fault, message);
            } else if (faultString != null) {
                log.debug("{} Current state of request was not mappable, setting faultstring to defaulted value",
                        getLogPrefix());
                buildFaultString(fault, faultString);
            }
        }
    }
    
    /**
     * Build and attach {@link FaultString} element.
     * 
     * @param fault    the element to attach to
     * @param message   the message to set
     */
    private void buildFaultString(@Nonnull final Fault fault, @Nonnull @NotEmpty final String message) {
        final XMLObjectBuilder<FaultString> faultStringBuilder =
                XMLObjectProviderRegistrySupport.getBuilderFactory().<FaultString>getBuilderOrThrow(
                        FaultString.DEFAULT_ELEMENT_NAME);

        final FaultString fs = faultStringBuilder.buildObject(FaultString.DEFAULT_ELEMENT_NAME);
        fs.setValue(message);
        fault.setMessage(fs);
    }
    
    /** A default method to map event IDs to faultcode QName based on {@link EventContext}. */
    public static class FaultCodeMappingFunction implements Function<ProfileRequestContext,QName> {

        /** Code mappings. */
        @Nonnull @NonnullElements private Map<String,QName> codeMappings;
        
        /** Strategy function for access to {@link EventContext} to check. */
        @Nonnull private Function<ProfileRequestContext,EventContext> eventContextLookupStrategy;
        
        /**
         * Constructor.
         *
         * @param mappings the status code mappings to use
         */
        public FaultCodeMappingFunction(@Nonnull @NonnullElements final Map<String,QName> mappings) {
            Constraint.isNotNull(mappings, "Faultcode mappings cannot be null");
            
            codeMappings = Maps.newHashMapWithExpectedSize(mappings.size());
            for (Map.Entry<String,QName> entry : mappings.entrySet()) {
                final String event = StringSupport.trimOrNull(entry.getKey());
                if (event != null && entry.getValue() != null) {
                    codeMappings.put(event, entry.getValue());
                }
            }
            
            eventContextLookupStrategy = new CurrentOrPreviousEventLookup();
        }
        
        /**
         * Set lookup strategy for {@link EventContext} to check.
         * 
         * @param strategy  lookup strategy
         */
        public void setEventContextLookupStrategy(
                @Nonnull final Function<ProfileRequestContext,EventContext> strategy) {
            eventContextLookupStrategy = Constraint.isNotNull(strategy, "EventContext lookup strategy cannot be null");
        }
        
        /** {@inheritDoc} */
        @Override
        @Nullable public QName apply(@Nullable final ProfileRequestContext input) {
            final EventContext eventCtx = eventContextLookupStrategy.apply(input);
            if (eventCtx != null && eventCtx.getEvent() != null) {
                return codeMappings.get(eventCtx.getEvent().toString());
            }
            return null;
        }
    }
    
}