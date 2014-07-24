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

package org.opensaml.saml.common.binding.impl;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.component.ComponentSupport;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml1.core.ResponseAbstractType;
import org.opensaml.saml.saml2.core.StatusResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Handler that checks whether a SAML message has an appropriate version. */
public class CheckMessageVersionHandler extends AbstractMessageHandler {
    
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(CheckMessageVersionHandler.class);
    
    /** Flag controlling handling of missing or unrecognized messages. */
    private boolean ignoreMissingOrUnrecognized;
    
    /**
     * Set whether to ignore cases where a message does not exist or is not recognized.
     * 
     * @param flag  flag to set
     */
    public void setIgnoreMissingOrUnrecognized(final boolean flag) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        ignoreMissingOrUnrecognized = flag;
    }
    
// Checkstyle: CyclomaticComplexity OFF
    /** {@inheritDoc} */
    @Override
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        
        final Object message = messageContext.getMessage();
        if (message == null) {
            log.debug("Message was not found");
            if (!ignoreMissingOrUnrecognized) {
                throw new MessageHandlerException("Message was not found");
            }
        } else if (message instanceof org.opensaml.saml.saml1.core.RequestAbstractType) {
            final SAMLVersion version = ((org.opensaml.saml.saml1.core.RequestAbstractType) message).getVersion();
            if (version.getMajorVersion() != 1) { 
                throw new MessageHandlerException("Request major version  was invalid");
            }
        } else if (message instanceof ResponseAbstractType) {
            final SAMLVersion version = ((ResponseAbstractType) message).getVersion();
            if (version.getMajorVersion() != 1) { 
                throw new MessageHandlerException("Request major version  was invalid");
            }
        } else if (message instanceof org.opensaml.saml.saml2.core.RequestAbstractType) {
            final SAMLVersion version = ((org.opensaml.saml.saml2.core.RequestAbstractType) message).getVersion();
            if (version.getMajorVersion() != 2) { 
                throw new MessageHandlerException("Response major version  was invalid");
            }
        } else if (message instanceof StatusResponseType) {
            final SAMLVersion version = ((StatusResponseType) message).getVersion();
            if (version.getMajorVersion() != 2) { 
                throw new MessageHandlerException("Response major version  was invalid");
            }
        } else {
            log.debug("Message type was not recognized");
            if (!ignoreMissingOrUnrecognized) {
                throw new MessageHandlerException("Message type was not recognized");
            }
        }
    }
// Checkstyle: CyclomaticComplexity ON
    
}