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

package org.opensaml.saml.common.binding.security;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLProtocolContext;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.security.messaging.impl.BaseTrustEngineSecurityHandler;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureValidationParametersCriterion;

import com.google.common.base.Strings;

/**
 * Base class for SAML security message handlers which evaluate a signature with a signature trust engine.
 */
public abstract class BaseSAMLXMLSignatureSecurityHandler
        extends BaseTrustEngineSecurityHandler<Signature, SAMLObject> {
    
    /** {@inheritDoc} */
    @Override
    @Nonnull protected CriteriaSet buildCriteriaSet(@Nullable final String entityID,
            @Nonnull final MessageContext<SAMLObject> messageContext) throws MessageHandlerException {
        
        final CriteriaSet criteriaSet = new CriteriaSet();
        if (!Strings.isNullOrEmpty(entityID)) {
            criteriaSet.add(new EntityIdCriterion(entityID) );
        }
        
        try {
            SAMLPeerEntityContext peerEntityContext = messageContext.getSubcontext(SAMLPeerEntityContext.class);
            Constraint.isNotNull(peerEntityContext, "SAMLPeerEntityContext was null");
            Constraint.isNotNull(peerEntityContext.getRole(), "SAML peer role was null");
            criteriaSet.add(new EntityRoleCriterion(peerEntityContext.getRole()));

            SAMLProtocolContext protocolContext = getSamlProtocolContext(messageContext);
            Constraint.isNotNull(protocolContext, "SAMLProtocolContext was null");
            Constraint.isNotNull(protocolContext.getProtocol(), "SAML protocol was null");
            criteriaSet.add(new ProtocolCriterion(protocolContext.getProtocol()));
        }  catch (ConstraintViolationException e) {
            throw new MessageHandlerException(e);
        }
        
        criteriaSet.add( new UsageCriterion(UsageType.SIGNING) );
        
        SecurityParametersContext secParamsContext = messageContext.getSubcontext(SecurityParametersContext.class);
        if (secParamsContext != null && secParamsContext.getSignatureValidationParameters() != null) {
            criteriaSet.add(new SignatureValidationParametersCriterion(
                    secParamsContext.getSignatureValidationParameters()));
        }
        
        return criteriaSet;
    }
    
    /**
     * Get the current SAML Protocol context.
     * 
     * @param messageContext the current message context
     * @return the current SAML protocol context
     */
    protected SAMLProtocolContext getSamlProtocolContext(@Nonnull final MessageContext<SAMLObject> messageContext) {
        //TODO is this the final resting place?
        return messageContext.getSubcontext(SAMLProtocolContext.class, false);
    }

}