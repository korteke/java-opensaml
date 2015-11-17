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

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.messaging.context.AbstractSAMLEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLProtocolContext;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.saml.metadata.resolver.RoleDescriptorResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for inbound SAML protocol messages that attempts to locate SAML metadata for
 * a SAML entity, and attaches it with a {@link SAMLMetadataContext} as a child of a 
 * pre-existing concrete instance of {@link AbstractSAMLEntityContext}. The entity context class is configurable
 * and defaults to {@link SAMLPeerEntityContext}.
 * 
 * <p>The handler will no-op in the absence of a populated {@link AbstractSAMLEntityContext} instance 
 * with an entityID and role to look up. A protocol from a {@link SAMLProtocolContext}
 * will be added to the lookup, if available.</p>
 */
public class SAMLMetadataLookupHandler extends AbstractMessageHandler {
    
    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(SAMLMetadataLookupHandler.class);

    /** Resolver used to look up SAML metadata. */
    @NonnullAfterInit private RoleDescriptorResolver metadataResolver;
    
    /** The context class representing the SAML entity whose data is to be resolved. 
     * Defaults to: {@link SAMLPeerEntityContext}. */
    @Nonnull private Class<? extends AbstractSAMLEntityContext> entityContextClass = SAMLPeerEntityContext.class;
    
    /**
     * Set the class type holding the SAML entity data.
     * 
     * <p>Defaults to: {@link SAMLPeerEntityContext}.</p>
     * 
     * @param clazz the entity context class type
     */
    public void setEntityContextClass(@Nonnull final Class<? extends AbstractSAMLEntityContext> clazz) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        entityContextClass = Constraint.isNotNull(clazz, "SAML entity context class may not be null");
    }

    /**
     * Set the {@link RoleDescriptorResolver} to use.
     * 
     * @param resolver  the resolver to use
     */
    public void setRoleDescriptorResolver(@Nonnull final RoleDescriptorResolver resolver) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        metadataResolver = Constraint.isNotNull(resolver, "RoleDescriptorResolver cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (metadataResolver == null) {
            throw new ComponentInitializationException("RoleDescriptorResolver cannot be null");
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        ComponentSupport.ifNotInitializedThrowUninitializedComponentException(this);

        final AbstractSAMLEntityContext entityCtx = messageContext.getSubcontext(entityContextClass);
        final SAMLProtocolContext protocolCtx = messageContext.getSubcontext(SAMLProtocolContext.class);
     
        if (entityCtx == null || entityCtx.getEntityId() == null || entityCtx.getRole() == null) {
            log.info("{} SAML entity context class '{}' missing or did not contain an entityID or role", getLogPrefix(),
                    entityContextClass.getName());
            return;
        }
        
        final EntityIdCriterion entityIdCriterion = new EntityIdCriterion(entityCtx.getEntityId());
        final EntityRoleCriterion roleCriterion = new EntityRoleCriterion(entityCtx.getRole());
        
        ProtocolCriterion protocolCriterion = null;
        if (protocolCtx != null && protocolCtx.getProtocol() != null) {
            protocolCriterion = new ProtocolCriterion(protocolCtx.getProtocol());
        }
        
        final CriteriaSet criteria = new CriteriaSet(entityIdCriterion, protocolCriterion, roleCriterion);
        try {
            final RoleDescriptor roleMetadata = metadataResolver.resolveSingle(criteria);
            if (roleMetadata == null) {
                if (protocolCriterion != null) {
                    log.info("{} No metadata returned for {} in role {} with protocol {}",
                            new Object[]{getLogPrefix(), entityCtx.getEntityId(), entityCtx.getRole(),
                                protocolCriterion.getProtocol(),});
                } else {
                    log.info("{} No metadata returned for {} in role {}",
                            new Object[]{getLogPrefix(), entityCtx.getEntityId(), entityCtx.getRole(),});
                }
                return;
            }

            final SAMLMetadataContext metadataCtx = new SAMLMetadataContext();
            metadataCtx.setEntityDescriptor((EntityDescriptor) roleMetadata.getParent());
            metadataCtx.setRoleDescriptor(roleMetadata);

            entityCtx.addSubcontext(metadataCtx);

            log.debug("{} {} added to MessageContext as child of {}", getLogPrefix(), 
                    SAMLMetadataContext.class.getName(), entityContextClass.getName());
        } catch (final ResolverException e) {
            log.error("{} ResolverException thrown during metadata lookup", getLogPrefix(), e);
        }
    }

}