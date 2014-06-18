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

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.messaging.context.AttributeConsumingServiceContext;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLProtocolContext;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.metadata.AttributeConsumingService;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;

import com.google.common.base.Function;
import com.google.common.base.Functions;

/**
 * SAML {@link org.opensaml.messaging.handler.MessageHandler} that attaches an {@link AttributeConsumingServiceContext}
 * to the {@link SAMLMetadataContext}.
 */
public class SAMLAddAttributeConsumingServiceHandler extends AbstractMessageHandler<SAMLObject> {

    /** How to get the {@link SAMLMetadataContext} from the message. */
    @Nonnull private Function<MessageContext<SAMLObject>, SAMLMetadataContext> metadataContextStrategy;

    /** How to get the {@link SAMLProtocolContext} from the message. */
    @Nonnull private Function<MessageContext<SAMLObject>, SAMLProtocolContext> protocolContextStrategy;

    /**
     * Constructor.
     */
    public SAMLAddAttributeConsumingServiceHandler() {
        metadataContextStrategy =
                Functions.compose(
                        new ChildContextLookup<SAMLPeerEntityContext, SAMLMetadataContext>(
                                SAMLMetadataContext.class),
                        new ChildContextLookup<MessageContext<SAMLObject>, SAMLPeerEntityContext>(
                                SAMLPeerEntityContext.class));
        protocolContextStrategy = new ChildContextLookup<>(SAMLProtocolContext.class);
    }

    /**
     * Get the strategy which find the {@link SAMLMetadataContext} from the message.
     * 
     * @return Returns strategy.
     */
    public Function<MessageContext<SAMLObject>, SAMLMetadataContext> getMetadataContextStrategy() {
        return metadataContextStrategy;
    }

    /**
     * Set the strategy which find the {@link SAMLMetadataContext} from the message.
     * 
     * @param strategy what to set
     */
    public void setMetadataContextStrategy(@Nonnull Function<MessageContext<SAMLObject>,
            SAMLMetadataContext> strategy) {
        metadataContextStrategy = Constraint.isNotNull(strategy, "Metadata Strategy must not be null");
    }

    /**
     * Get the strategy which find the {@link SAMLProtocolContext} from the message.
     * 
     * @return Returns strategy.
     */
    public Function<MessageContext<SAMLObject>, SAMLProtocolContext> getProtocolContextStrategy() {
        return protocolContextStrategy;
    }

    /**
     * Set the strategy which find the {@link SAMLProtocolContext} from the message.
     * 
     * @param strategy what to set
     */
    public void setProtocolContextStrategy(@Nonnull Function<MessageContext<SAMLObject>,
            SAMLProtocolContext> strategy) {
        protocolContextStrategy = Constraint.isNotNull(strategy, "Metadata Strategy must not be null");
    }

    /** {@inheritDoc}*/
    @Override protected void doInvoke(MessageContext<SAMLObject> messageContext) throws MessageHandlerException {
        final SAMLObject message = messageContext.getMessage();
        if (!(message instanceof AuthnRequest)) {
            return;
        }
        final AuthnRequest authnRequest = (AuthnRequest) message;
        final SAMLMetadataContext metadataContext = metadataContextStrategy.apply(messageContext);
        final String protocol = protocolContextStrategy.apply(messageContext).getProtocol();
        final SPSSODescriptor ssoDescriptor = metadataContext.getEntityDescriptor().getSPSSODescriptor(protocol);
        final Integer acsIndex = authnRequest.getAttributeConsumingServiceIndex();
        AttributeConsumingService acs = null;
        if (null != acsIndex) {
            for (AttributeConsumingService acsEntry : ssoDescriptor.getAttributeConsumingServices()) {
                if (acsIndex.intValue() == acsEntry.getIndex()) {
                    acs = acsEntry;
                    break;
                }
            }
        }
        if (null == acs) {
            acs = ssoDescriptor.getDefaultAttributeConsumingService();
        }
        if (null != acs) {
            metadataContext.getSubcontext(AttributeConsumingServiceContext.class, true).
                setAttributeConsumingService(acs);
        }
    }

}