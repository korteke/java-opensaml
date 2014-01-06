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

package org.opensaml.saml.saml1.binding;

import javax.annotation.Nonnull;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml1.core.RequestAbstractType;

import com.google.common.base.Objects;

/**
 * Checks whether the inbound SAML request has the appropriate version.
 */
public class CheckRequestVersionHandler extends AbstractMessageHandler<RequestAbstractType> {

    /** {@inheritDoc} */
    @Override
    protected void doInvoke(@Nonnull final MessageContext<RequestAbstractType> messageContext)
            throws MessageHandlerException {

        final RequestAbstractType request = messageContext.getMessage();
        if (request == null) {
            throw new MessageHandlerException("Request was not found");
        }

        if (!Objects.equal(SAMLVersion.VERSION_10, request.getVersion())
                && !Objects.equal(SAMLVersion.VERSION_11, request.getVersion())) {
            throw new MessageHandlerException("Request version was invalid");
        }
    }
    
}