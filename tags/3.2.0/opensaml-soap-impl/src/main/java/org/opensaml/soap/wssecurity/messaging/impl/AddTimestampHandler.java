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

package org.opensaml.soap.wssecurity.messaging.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.component.ComponentSupport;

import org.joda.time.DateTime;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.ContextDataLookupFunction;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.soap.messaging.AbstractHeaderGeneratingMessageHandler;
import org.opensaml.soap.wssecurity.Created;
import org.opensaml.soap.wssecurity.Expires;
import org.opensaml.soap.wssecurity.Timestamp;
import org.opensaml.soap.wssecurity.messaging.WSSecurityContext;
import org.opensaml.soap.wssecurity.messaging.WSSecurityMessagingSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler implementation that adds a wsse:Timestamp header to the wsse:Security header
 *  of the outbound SOAP envelope.
 */
public class AddTimestampHandler extends AbstractHeaderGeneratingMessageHandler {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(AddTimestampHandler.class);
    
    /** Context lookup function for the Created time. */
    private ContextDataLookupFunction<MessageContext, DateTime> createdLookup;
    
    /** Context lookup function for the Expires time. */
    private ContextDataLookupFunction<MessageContext, DateTime> expiresLookup;
    
    /** Flag indicating whether to use the current time as the Created time, if no value
     * is explicitly supplied by the other supported mechanisms. */
    private boolean useCurrentTimeAsDefaultCreated;
    
    /** Parameter indicating the offset from Created, in milliseconds, used to calculate the Expires time, 
     * if no Expires value is explicitly supplied via the other supported mechanisms. */
    private Long expiresOffsetFromCreated;
    
    /** The effective Created value to use. */
    private DateTime createdValue;
    
    /** The effective Expires value to use. */
    private DateTime expiresValue;
    
    /**
     * Get the context lookup function for the Created time.
     * 
     * @return the lookup function
     */
    @Nullable public ContextDataLookupFunction<MessageContext, DateTime> getCreatedLookup() {
        return createdLookup;
    }

    /**
     * Set the context lookup function for the Created time.
     * 
     * @param lookup the lookup function
     */
    public void setCreatedLookup(@Nullable final ContextDataLookupFunction<MessageContext, DateTime> lookup) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        createdLookup = lookup;
    }

    /**
     * Get the context lookup function for the Expires time.
     * 
     * @return the lookup function
     */
    @Nullable public ContextDataLookupFunction<MessageContext, DateTime> getExpiresLookup() {
        return expiresLookup;
    }

    /**
     * Set the context lookup function for the Expires time.
     * 
     * @param lookup the lookup function
     */
    public void setExpiresLookup(@Nullable final ContextDataLookupFunction<MessageContext, DateTime> lookup) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        expiresLookup = lookup;
    }

    /**
     * Get the flag indicating whether to use the current time as the Created time, if no value
     * is explicitly supplied by the other supported mechanisms. 
     * 
     * @return true if should use current time, false if not
     */
    public boolean isUseCurrentTimeAsDefaultCreated() {
        return useCurrentTimeAsDefaultCreated;
    }

    /**
     * Set the flag indicating whether to use the current time as the Created time, if no value
     * is explicitly supplied by the other supported mechanisms. 
     * 
     * @param flag true if should use currnet time, false if not
     */
    public void setUseCurrentTimeAsDefaultCreated(boolean flag) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        useCurrentTimeAsDefaultCreated = flag;
    }
    
    /**
     * Get the parameter indicating the offset from Created, in milliseconds, used to calculate the Expires time, 
     * if no Expires value is explicitly supplied via the other supported mechanisms. 
     * 
     * @return the expires offset, or null
     */
    @Nullable public Long getExpiresOffsetFromCreated() {
        return expiresOffsetFromCreated;
    }

    /**
     * Set the parameter indicating the offset from Created, in milliseconds, used to calculate the Expires time, 
     * if no Expires value is explicitly supplied via the other supported mechanisms. 
     * 
     * @param value the expires off set, or null
     */
    public void setExpiresOffsetFromCreated(@Nullable final Long value) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        expiresOffsetFromCreated = value;
    }

    /** {@inheritDoc} */
    protected boolean doPreInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        if (!super.doPreInvoke(messageContext)) {
            return false;
        }
        
        createdValue = getCreatedValue(messageContext);
        expiresValue = getExpiresValue(messageContext, createdValue);
        if (createdValue == null && expiresValue == null) {
            log.debug("No WS-Security Timestamp Created or Expires values available, skipping further processing");
            return false;
        }
        return true;
    }

    /** {@inheritDoc} */
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        log.debug("Processing addition of outbound WS-Security Timestamp");
        Timestamp timestamp = (Timestamp) XMLObjectSupport.buildXMLObject(Timestamp.ELEMENT_NAME);
        
        if (createdValue != null) {
            log.debug("WS-Security Timestamp Created value added was: {}", createdValue);
            Created created = (Created) XMLObjectSupport.buildXMLObject(Created.ELEMENT_NAME);
            created.setDateTime(createdValue);
            timestamp.setCreated(created);
        }
            
        if (expiresValue != null) {
            log.debug("WS-Security Timestamp Expires value added was: {}", createdValue);
            Expires expires = (Expires) XMLObjectSupport.buildXMLObject(Expires.ELEMENT_NAME);
            expires.setDateTime(expiresValue);
            timestamp.setExpires(expires);
        }
        
        WSSecurityMessagingSupport.addSecurityHeaderBlock(messageContext, timestamp, isEffectiveMustUnderstand(),
                getEffectiveTargetNode(), true);
    }
    
    /**
     * Get the Created value.
     * 
     * @param messageContext the current message context
     * 
     * @return the effective Created DateTime value to use
     */
    @Nullable protected DateTime getCreatedValue(@Nonnull final MessageContext messageContext) {
        DateTime value = null;
        WSSecurityContext security = messageContext.getSubcontext(WSSecurityContext.class, false);
        if (security != null) {
            value = security.getTimestampCreated();
        }
        
        if (value == null && getCreatedLookup() != null) {
            value = getCreatedLookup().apply(messageContext);
        }
        
        if (value == null) {
            if (isUseCurrentTimeAsDefaultCreated()) {
                value = new DateTime();
            }
        }
        return value;
    }
    
    /**
     * Get the Expires value.
     * 
     * @param messageContext the current message context
     * @param created the created value, if any
     * 
     * @return the effective Expires DateTime value to use
     */
    @Nullable protected DateTime getExpiresValue(@Nonnull final MessageContext messageContext, 
            @Nullable final DateTime created) {
        DateTime value = null;
        WSSecurityContext security = messageContext.getSubcontext(WSSecurityContext.class, false);
        if (security != null) {
            value = security.getTimestampExpires();
        }
        
        if (value == null && getExpiresLookup() != null) {
            value = getExpiresLookup().apply(messageContext);
        }
        
        if (value == null) {
            if (getExpiresOffsetFromCreated() != null && created != null) {
                return created.plus(getExpiresOffsetFromCreated());
            }
        }
        return value;
    }

}
