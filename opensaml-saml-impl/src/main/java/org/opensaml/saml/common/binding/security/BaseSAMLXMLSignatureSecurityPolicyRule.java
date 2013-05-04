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
import org.opensaml.saml.common.messaging.context.SamlPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SamlProtocolContext;
import org.opensaml.saml.security.MetadataCriterion;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.EntityIDCriterion;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.ws.security.provider.BaseTrustEngineRule;
import org.opensaml.xmlsec.signature.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * Base class for SAML security policy rules which evaluate a signature with a signature trust engine.
 */
public abstract class BaseSAMLXMLSignatureSecurityPolicyRule extends BaseTrustEngineRule<Signature, SAMLObject> {
    
    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(BaseSAMLXMLSignatureSecurityPolicyRule.class);

    /** {@inheritDoc} */
    protected CriteriaSet buildCriteriaSet(String entityID, MessageContext<SAMLObject> messageContext)
        throws MessageHandlerException {
        
        CriteriaSet criteriaSet = new CriteriaSet();
        if (!Strings.isNullOrEmpty(entityID)) {
            criteriaSet.add(new EntityIDCriterion(entityID) );
        }
        
        SamlPeerEntityContext peerContext = messageContext.getSubcontext(SamlPeerEntityContext.class);
        Constraint.isNotNull(peerContext, "SamlPeerEntityContext was null");
        Constraint.isNotNull(peerContext.getRole(), "SAML peer role was null");
        
        SamlProtocolContext protocolContext = getSamlProtocolContext(messageContext);
        Constraint.isNotNull(protocolContext, "SamlProtocolContext was null");
        Constraint.isNotNull(protocolContext.getProtocol(), "SAML protocol was null");
        
        MetadataCriterion mdCriteria = new MetadataCriterion(peerContext.getRole(), protocolContext.getProtocol());
        
        criteriaSet.add(mdCriteria);
        
        criteriaSet.add( new UsageCriterion(UsageType.SIGNING) );
        
        return criteriaSet;
    }
    
    /**
     * Get the current SAML Protocol context.
     * 
     * @param messageContext the current message context
     * @return the current SAML protocol context
     */
    protected SamlProtocolContext getSamlProtocolContext(MessageContext<SAMLObject> messageContext) {
        //TODO is this the final resting place?
        return messageContext.getSubcontext(SamlProtocolContext.class, false);
    }

}
