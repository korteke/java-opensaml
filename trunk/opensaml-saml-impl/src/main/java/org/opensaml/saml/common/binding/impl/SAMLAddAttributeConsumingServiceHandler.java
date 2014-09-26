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
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.messaging.context.AttributeConsumingServiceContext;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.metadata.AttributeConsumingService;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Functions;

/**
 * SAML {@link org.opensaml.messaging.handler.MessageHandler} that attaches an {@link AttributeConsumingServiceContext}
 * to the {@link SAMLMetadataContext} based on the content of an {@link AuthnRequest} in the message context.
 */
public class SAMLAddAttributeConsumingServiceHandler extends AbstractMessageHandler {
    
    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(SAMLAddAttributeConsumingServiceHandler.class);

    /** Lookup strategy for {@link SAMLMetadataContext}. */
    @Nonnull private Function<MessageContext,SAMLMetadataContext> metadataContextLookupStrategy;
   
    /** Lookup strategy for an {@link AttributeConsumingService} index. */
    @Nullable private Function<MessageContext,Integer> indexLookupStrategy;

    /** {@link AttributeConsumingService} index. */
    @Nullable private Integer index;
    
    /**
     * Constructor.
     */
    public SAMLAddAttributeConsumingServiceHandler() {
        metadataContextLookupStrategy =
                Functions.compose(
                        new ChildContextLookup<SAMLPeerEntityContext,SAMLMetadataContext>(SAMLMetadataContext.class),
                        new ChildContextLookup<MessageContext,SAMLPeerEntityContext>(SAMLPeerEntityContext.class));
        indexLookupStrategy = new AuthnRequestIndexLookup();
    }

    /**
     * Set the strategy to locate the {@link SAMLMetadataContext} from the {@link MessageContext}.
     * 
     * @param strategy lookup strategy
     */
    public void setMetadataContextLookupStrategy(@Nonnull final Function<MessageContext,SAMLMetadataContext> strategy) {
        metadataContextLookupStrategy = Constraint.isNotNull(strategy,
                "SAMLMetadataContext lookup strategy cannot be null");
    }

    /**
     * Set the strategy to locate the {@link AttributeConsumingService} index from the {@link MessageContext}.
     * 
     * @param strategy lookup strategy
     */
    public void setIndexLookupStrategy(@Nullable final Function<MessageContext,Integer> strategy) {
        indexLookupStrategy = Constraint.isNotNull(strategy,
                "AttributeConsumingService index lookup strategy cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        
        if (!super.doPreInvoke(messageContext)) {
            return false;
        }
        
        if (indexLookupStrategy != null) {
            index = indexLookupStrategy.apply(messageContext);
        }
        
        return true;
    }

    /** {@inheritDoc}*/
    @Override protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        final SAMLMetadataContext metadataContext = metadataContextLookupStrategy.apply(messageContext);
        if (metadataContext == null) {
            log.debug("{} No metadata context found, nothing to do", getLogPrefix());
            return;
        } else if (!(metadataContext.getRoleDescriptor() instanceof SPSSODescriptor)) {
            log.debug("{} Metadata context did not contain an SPSSODescriptor, nothing to do", getLogPrefix());
            return;
        }
        
        final SPSSODescriptor ssoDescriptor = (SPSSODescriptor) metadataContext.getRoleDescriptor();
        
        AttributeConsumingService acs = null;
        if (null != index) {
            log.debug("{} Request specified AttributeConsumingService index {}", getLogPrefix(), index);
            for (final AttributeConsumingService acsEntry : ssoDescriptor.getAttributeConsumingServices()) {
                if (index.intValue() == acsEntry.getIndex()) {
                    acs = acsEntry;
                    break;
                }
            }
        }
        if (null == acs) {
            log.debug("{} Selecting default AttributeConsumingService, if any", getLogPrefix());
            acs = ssoDescriptor.getDefaultAttributeConsumingService();
        }
        if (null != acs) {
            log.debug("{} Selected AttributeConsumingService with index {}", getLogPrefix(), acs.getIndex());
            metadataContext.getSubcontext(
                    AttributeConsumingServiceContext.class, true).setAttributeConsumingService(acs);
        } else {
            log.debug("{} No AttributeConsumingService selected", getLogPrefix());
        }
    }

    /** Default lookup function that reads from a SAML 2 {@link AuthnRequest}. */
    private class AuthnRequestIndexLookup implements Function<MessageContext,Integer> {

        /** {@inheritDoc} */
        @Override
        public Integer apply(@Nullable final MessageContext input) {
            if (input != null) {
                final Object message = input.getMessage();
                if (message != null && message instanceof AuthnRequest) {
                    return ((AuthnRequest) message).getAttributeConsumingServiceIndex();
                }
            }
            
            return null;
        }
        
    }
}