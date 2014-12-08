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

package org.opensaml.saml.common.binding.security.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLProtocolContext;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.security.messaging.impl.BaseClientCertAuthSecurityHandler;

/**
 * SAML specialization of {@link BaseClientCertAuthSecurityHandler} which provides support for X509Credential 
 * trust engine validation based on SAML metadata.
 */
public class SAMLMDClientCertAuthSecurityHandler extends BaseClientCertAuthSecurityHandler {

    /** {@inheritDoc} */
    @Override
    @Nonnull protected CriteriaSet buildCriteriaSet(@Nullable final String entityID,
            @Nonnull final MessageContext messageContext) throws MessageHandlerException {
        
        final CriteriaSet criteriaSet = super.buildCriteriaSet(entityID, messageContext);
        
        try {
            final SAMLPeerEntityContext peerEntityContext = messageContext.getSubcontext(SAMLPeerEntityContext.class);
            Constraint.isNotNull(peerEntityContext, "SAMLPeerEntityContext was null");
            Constraint.isNotNull(peerEntityContext.getRole(), "SAML peer role was null");
            criteriaSet.add(new EntityRoleCriterion(peerEntityContext.getRole()));
            
            final SAMLProtocolContext protocolContext = messageContext.getSubcontext(SAMLProtocolContext.class);
            Constraint.isNotNull(protocolContext, "SAMLProtocolContext was null");
            Constraint.isNotNull(protocolContext.getProtocol(), "SAML protocol was null");
            criteriaSet.add(new ProtocolCriterion(protocolContext.getProtocol()));
        } catch (final ConstraintViolationException e) {
            throw new MessageHandlerException(e);
        }

        return criteriaSet;
    }
    
    /** {@inheritDoc} */
    @Override
    @Nullable protected String getCertificatePresenterEntityID(@Nonnull final MessageContext messageContext) {
        return messageContext.getSubcontext(SAMLPeerEntityContext.class, true).getEntityId();
    }

    /** {@inheritDoc} */
    @Override
    protected void setAuthenticatedCertificatePresenterEntityID(@Nonnull final MessageContext messageContext,
            @Nullable final String entityID) {
        messageContext.getSubcontext(SAMLPeerEntityContext.class, true).setEntityId(entityID);
    }

    /** {@inheritDoc} */
    @Override
    protected void setAuthenticatedState(@Nonnull final MessageContext messageContext, final boolean authenticated) {
        //TODO this may change
        messageContext.getSubcontext(SAMLPeerEntityContext.class, true).setAuthenticated(authenticated);
    }
    
}