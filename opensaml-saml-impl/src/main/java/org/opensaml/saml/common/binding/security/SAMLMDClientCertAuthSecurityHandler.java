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

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLProtocolContext;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.security.messaging.impl.BaseClientCertAuthSecurityHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAML specialization of {@link BaseClientCertAuthSecurityHandler} which provides support for X509Credential 
 * trust engine validation based on SAML metadata.
 */
public class SAMLMDClientCertAuthSecurityHandler extends BaseClientCertAuthSecurityHandler<SAMLObject> {

    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(SAMLMDClientCertAuthSecurityHandler.class);

    /** {@inheritDoc} */
    protected CriteriaSet buildCriteriaSet(String entityID, MessageContext<SAMLObject> messageContext) 
        throws MessageHandlerException {
        
        CriteriaSet criteriaSet = super.buildCriteriaSet(entityID, messageContext);
        
        SAMLPeerEntityContext peerEntityContext = messageContext.getSubcontext(SAMLPeerEntityContext.class);
        Constraint.isNotNull(peerEntityContext, "SAMLPeerEntityContext was null");
        Constraint.isNotNull(peerEntityContext.getRole(), "SAML peer role was null");
        criteriaSet.add(new EntityRoleCriterion(peerEntityContext.getRole()));
        
        SAMLProtocolContext protocolContext = getSamlProtocolContext(messageContext);
        Constraint.isNotNull(protocolContext, "SAMLProtocolContext was null");
        Constraint.isNotNull(protocolContext.getProtocol(), "SAML protocol was null");
        criteriaSet.add(new ProtocolCriterion(protocolContext.getProtocol()));

        return criteriaSet;
    }
    
    /**
     * Get the current SAML Protocol context.
     * 
     * @param messageContext the current message context
     * @return the current SAML protocol context
     */
    protected SAMLProtocolContext getSamlProtocolContext(MessageContext<SAMLObject> messageContext) {
        //TODO is this the final resting place?
        return messageContext.getSubcontext(SAMLProtocolContext.class, false);
    }

    /** {@inheritDoc} */
    protected String getCertificatePresenterEntityID(MessageContext<SAMLObject> messageContext) {
        return messageContext.getSubcontext(SAMLPeerEntityContext.class, true).getEntityId();
    }

    /** {@inheritDoc} */
    protected void setAuthenticatedCertificatePresenterEntityID(MessageContext<SAMLObject> messageContext,
            String entityID) {
        messageContext.getSubcontext(SAMLPeerEntityContext.class, true).setEntityId(entityID);
    }

    /** {@inheritDoc} */
    protected void setAuthenticatedState(MessageContext<SAMLObject> messageContext, boolean authenticated) {
        //TODO this may change
        messageContext.getSubcontext(SAMLPeerEntityContext.class, true).setAuthenticated(authenticated);
    }
    
}