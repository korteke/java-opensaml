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

package org.opensaml.saml.saml1.profile.impl;

import javax.annotation.Nonnull;

import org.opensaml.profile.ProfileException;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;

import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml1.core.RequestAbstractType;
import org.opensaml.saml.saml1.core.ResponseAbstractType;

import com.google.common.base.Objects;

/**
 * Checks whether the inbound SAML request has the appropriate version.
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_MESSAGE_VERSION}
 * @pre ProfileRequestContext.getInboundMessageContext().getMessage() != null
 */
public class CheckRequestVersion extends AbstractProfileAction<RequestAbstractType, ResponseAbstractType> {

    /** {@inheritDoc} */
    @Override
    protected void doExecute(
            @Nonnull final ProfileRequestContext<RequestAbstractType, ResponseAbstractType> profileRequestContext)
                        throws ProfileException {

        final RequestAbstractType request = profileRequestContext.getInboundMessageContext().getMessage();

        if (Objects.equal(SAMLVersion.VERSION_10, request.getVersion())
                || Objects.equal(SAMLVersion.VERSION_11, request.getVersion())) {
            ActionSupport.buildProceedEvent(profileRequestContext);
        } else {
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MESSAGE_VERSION);
        }
    }
    
}