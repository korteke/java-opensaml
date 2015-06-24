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

package org.opensaml.saml.saml2.wssecurity.messaging.impl;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.saml2.core.Assertion;

/**
 * Class which holds messaging data relevant to validating a WS-Security SAML 2.0 Assertion token.
 */
public class SAML20AssertionTokenValidationInput {
    
    /** The message context input. */
    private MessageContext messageContext;
    
    /** The HTTP request input. */
    private HttpServletRequest httpServletRequest;
    
    /** The Assertion being evaluated. */
    private Assertion assertion;

    /**
     * Constructor.
     *
     * @param context the message context being evaluated
     * @param request the HTTP request being evaluated
     * @param samlAssertion the assertion being evaluated
     */
    public SAML20AssertionTokenValidationInput(@Nonnull final MessageContext context,
            @Nonnull final HttpServletRequest request, @Nonnull final Assertion samlAssertion) {
        messageContext = Constraint.isNotNull(context, "MessageContext may not be null");
        httpServletRequest = Constraint.isNotNull(request, "HttpServletRequest may not be null");
        assertion = Constraint.isNotNull(samlAssertion, "Assertion may not be null");
    }

    /**
     * Get the {@link MessageContext} input.
     * 
     * @return the message context input
     */
    @Nonnull public MessageContext getMessageContext() {
        return messageContext;
    }

    /**
     * Get the {@link HttpServletRequest} input.
     * 
     * @return the HTTP servlet request input
     */
    @Nonnull public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }
    
    /**
     * Get the {@link Assertion} being evaluated.
     * 
     * @return the Assertion being validated
     */
    @Nonnull public Assertion getAssertion() {
        return assertion;
    }

}
