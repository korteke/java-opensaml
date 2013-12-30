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

package org.opensaml.saml.common.binding;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLProtocolContext;

/**
 * SAML {@link org.opensaml.messaging.handler.MessageHandler} that attaches protocol
 * and role information to a message context via {@link SAMLProtocolContext} and
 * {@link SAMLPeerEntityContext} objects.
 * 
 * <p>A profile flow would typically run this handler after message decoding occurs,
 * to bootstrap subsequent handlers.</p>
 */
public class SAMLProtocolAndRoleHandler extends AbstractMessageHandler<SAMLObject> {
    
    /** Protocol value to add to context. */
    @NonnullAfterInit @NotEmpty private String samlProtocol;

    /** Role type to add to context. */
    @NonnullAfterInit private QName peerRole;

    /**
     * Set the protocol constant to attach.
     * 
     * @param protocol the protocol constant to set
     */
    public void setSAMLProtocol(@Nonnull @NotEmpty final String protocol) {
        samlProtocol = Constraint.isNotNull(StringSupport.trimOrNull(protocol), "SAML protocol cannot be null");
    }

    /**
     * Set the operational role to attach.
     * 
     * @param role the operational role to set
     */
    public void setPeerSamlRole(@Nonnull final QName role) {
        peerRole = Constraint.isNotNull(role, "SAML peer role cannot be null");
    }

    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (samlProtocol == null || peerRole == null) {
            throw new ComponentInitializationException("SAML protocol or peer role was null");
        }
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInvoke(MessageContext<SAMLObject> messageContext) throws MessageHandlerException {
        messageContext.getSubcontext(SAMLProtocolContext.class, true).setProtocol(samlProtocol);
        messageContext.getSubcontext(SAMLPeerEntityContext.class, true).setRole(peerRole);
    }
    
}