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

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLProtocolContext;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.security.messaging.impl.BaseTrustEngineSecurityHandler;
import org.opensaml.security.trust.TrustEngine;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureValidationParametersCriterion;

import com.google.common.base.Strings;

/**
 * Base class for SAML security message handlers which evaluate a signature with a signature trust engine.
 */
public abstract class BaseSAMLXMLSignatureSecurityHandler extends BaseTrustEngineSecurityHandler<Signature> {
    
    /** The context representing the SAML peer entity. */
    @Nullable private SAMLPeerEntityContext peerContext;
    
    /** The SAML protocol context in operation. */
    @Nullable private SAMLProtocolContext samlProtocolContext;
    
    /**
     * Get the {@link SAMLPeerEntityContext} associated with the message.
     * 
     * @return the peer context
     */
    @Nullable protected SAMLPeerEntityContext getSAMLPeerEntityContext() {
        return peerContext;
    }

    /**
     * Get the {@link SAMLProtocolContext} associated with the message.
     * 
     * @return the protocol context
     */
    @Nullable protected SAMLProtocolContext getSAMLProtocolContext() {
        return samlProtocolContext;
    }

    /** {@inheritDoc} */
    @Override
    protected boolean doPreInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        
        if (!super.doPreInvoke(messageContext)) {
            return false;
        }
        
        peerContext = messageContext.getSubcontext(SAMLPeerEntityContext.class);
        if (peerContext == null || peerContext.getRole() == null) {
            throw new MessageHandlerException("SAMLPeerEntityContext was missing or unpopulated");
        }
        
        samlProtocolContext = messageContext.getSubcontext(SAMLProtocolContext.class);
        if (samlProtocolContext == null || samlProtocolContext.getProtocol() == null) {
            throw new MessageHandlerException("SAMLProtocolContext was missing or unpopulated");
        }
        
        return true;
    }
    
    /** {@inheritDoc} */
    @Override
    @Nullable protected TrustEngine<Signature> resolveTrustEngine(@Nonnull final MessageContext messageContext) {
        final SecurityParametersContext secParams = messageContext.getSubcontext(SecurityParametersContext.class);
        if (secParams == null || secParams.getSignatureValidationParameters() == null) {
            return null;
        } else {
            return secParams.getSignatureValidationParameters().getSignatureTrustEngine();
        }
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull protected CriteriaSet buildCriteriaSet(@Nullable final String entityID,
            @Nonnull final MessageContext messageContext) throws MessageHandlerException {
        
        final CriteriaSet criteriaSet = new CriteriaSet();
        if (!Strings.isNullOrEmpty(entityID)) {
            criteriaSet.add(new EntityIdCriterion(entityID) );
        }

        criteriaSet.add(new EntityRoleCriterion(peerContext.getRole()));
        criteriaSet.add(new ProtocolCriterion(samlProtocolContext.getProtocol()));
        criteriaSet.add( new UsageCriterion(UsageType.SIGNING) );
        
        final SecurityParametersContext secParamsContext =
                messageContext.getSubcontext(SecurityParametersContext.class);
        if (secParamsContext != null && secParamsContext.getSignatureValidationParameters() != null) {
            criteriaSet.add(
                    new SignatureValidationParametersCriterion(secParamsContext.getSignatureValidationParameters()));
        }
        
        return criteriaSet;
    }

}