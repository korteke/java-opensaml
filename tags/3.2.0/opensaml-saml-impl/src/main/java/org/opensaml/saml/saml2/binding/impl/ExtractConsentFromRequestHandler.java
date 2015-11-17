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

package org.opensaml.saml.saml2.binding.impl;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.messaging.context.SAMLConsentContext;
import org.opensaml.saml.saml2.core.RequestAbstractType;

import com.google.common.base.Function;

/**
 * MessageHandler to get the Consent attribute from a {@link RequestAbstractType} message.
 */
public class ExtractConsentFromRequestHandler extends AbstractMessageHandler {
    
    /** Strategy for locating {@link SAMLConsentContext}. */
    @Nonnull private Function<MessageContext,SAMLConsentContext> consentContextStrategy;

    /** Constructor. */
    public ExtractConsentFromRequestHandler() {
        super();
        consentContextStrategy = new ChildContextLookup<>(SAMLConsentContext.class, true);
    }
    
    /**
     * Set the strategy for locating {@link SAMLConsentContext}.
     * 
     * @param strategy  lookup strategy
     */
    public void setConsentContextLookupStrategy(@Nonnull final Function<MessageContext,SAMLConsentContext> strategy) {
        consentContextStrategy = Constraint.isNotNull(strategy, "SAMLConsentContext lookup strategy cannot be null");
    }

    /** {@inheritDoc} */
    @Override
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {

        final Object request = messageContext.getMessage();
        if (request == null) {
            throw new MessageHandlerException("Message not found");
        } else if (!(request instanceof RequestAbstractType)) {
            throw new MessageHandlerException("Message was not a RequestAbstractType");
        }
        
        final SAMLConsentContext consentContext = consentContextStrategy.apply(messageContext);
        if (consentContext == null) {
            throw new MessageHandlerException("SAMLConsentContext to populate not found");
        }
        
        consentContext.setConsent(((RequestAbstractType) request).getConsent());
    }
    
}