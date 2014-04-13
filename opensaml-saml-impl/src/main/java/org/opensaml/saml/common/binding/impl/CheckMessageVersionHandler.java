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

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml1.core.ResponseAbstractType;
import org.opensaml.saml.saml2.core.StatusResponseType;

/** Handler that checks whether a SAML message has an appropriate version. */
public class CheckMessageVersionHandler extends AbstractMessageHandler<SAMLObject> {
    
    /** {@inheritDoc} */
    @Override
    protected void doInvoke(@Nonnull final MessageContext<SAMLObject> messageContext)
            throws MessageHandlerException {

        final SAMLObject message = messageContext.getMessage();
        if (message == null) {
            throw new MessageHandlerException("Message was not found");
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
            throw new MessageHandlerException("Message type was not recognized");
        }
    }
    
}