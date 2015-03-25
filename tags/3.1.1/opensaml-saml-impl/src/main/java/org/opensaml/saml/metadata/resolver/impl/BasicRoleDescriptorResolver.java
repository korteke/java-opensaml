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

package org.opensaml.saml.metadata.resolver.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.component.AbstractIdentifiedInitializableComponent;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.RoleDescriptorResolver;
import org.opensaml.saml.saml2.common.SAML2Support;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * Simple impl of {@link RoleDescriptorResolver} which wraps an instance of {@link MetadataResolver} to
 * support basic EntityDescriptor resolution, and then performs further role-related filtering over the
 * returned EntityDescriptor.
 */
public class BasicRoleDescriptorResolver extends AbstractIdentifiedInitializableComponent 
        implements RoleDescriptorResolver {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(BasicRoleDescriptorResolver.class);
    
    /** Whether metadata is required to be valid. */
    private boolean requireValidMetadata;
    
    /** Resolver of EntityDescriptors. */
    private MetadataResolver entityDescriptorResolver;
    
    /**
     * Constructor.
     *
     * @param mdResolver the resolver of EntityDescriptors
     */
    public BasicRoleDescriptorResolver(@Nonnull MetadataResolver mdResolver) {
        entityDescriptorResolver = Constraint.isNotNull(mdResolver, "Resolver for EntityDescriptors may not be null");
        setId(UUID.randomUUID().toString()); 
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean isRequireValidMetadata() {
        return requireValidMetadata;
    }

    /** {@inheritDoc} */
    @Override
    public void setRequireValidMetadata(boolean require) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);

        requireValidMetadata = require;
    }
    
    /** {@inheritDoc} */
    @Override
    @Nullable public RoleDescriptor resolveSingle(CriteriaSet criteria) throws ResolverException {
        ComponentSupport.ifNotInitializedThrowUninitializedComponentException(this);
        
        Iterable<RoleDescriptor> iterable = resolve(criteria);
        if (iterable != null) {
            Iterator<RoleDescriptor> iterator = iterable.iterator();
            if (iterator != null && iterator.hasNext()) {
                return iterator.next();
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull public Iterable<RoleDescriptor> resolve(CriteriaSet criteria) throws ResolverException {
        ComponentSupport.ifNotInitializedThrowUninitializedComponentException(this);
        
        EntityIdCriterion entityIdCriterion = criteria.get(EntityIdCriterion.class);
        EntityRoleCriterion entityRoleCriterion = criteria.get(EntityRoleCriterion.class);
        ProtocolCriterion protocolCriterion = criteria.get(ProtocolCriterion.class);
        // TODO support BindingCriterion
        
        if (entityIdCriterion == null || Strings.isNullOrEmpty(entityIdCriterion.getEntityId())) {
            //TODO throw or just log?
            throw new ResolverException("Entity Id was not supplied in criteria set");
        }
        
        if (entityRoleCriterion == null || entityRoleCriterion.getRole() == null) {
            //TODO throw or just log?
            throw new ResolverException("Entity role was not supplied in criteria set");
        }
        
        if (protocolCriterion != null) {
            RoleDescriptor role = getRole(entityIdCriterion.getEntityId(), entityRoleCriterion.getRole(), 
                    protocolCriterion.getProtocol());
            if (role != null) {
                return Collections.singletonList(role);
            } else {
                return Collections.emptyList();
            }
        } else {
            return getRole(entityIdCriterion.getEntityId(), entityRoleCriterion.getRole());
        }
        
    }

    /**
     * Get role descriptors for a given entityID and role.
     * 
     * @param entityID  entityID to lookup
     * @param roleName  role to lookup
     * @return  list of roles
     * @throws ResolverException if an error occurs
     */
    @Nonnull @NonnullElements protected List<RoleDescriptor> getRole(
            @Nullable final String entityID, @Nullable final QName roleName) throws ResolverException {
        if (Strings.isNullOrEmpty(entityID)) {
            log.debug("EntityDescriptor entityID was null or empty, skipping search for roles");
            return Collections.emptyList();
        }

        if (roleName == null) {
            log.debug("Role descriptor name was null, skipping search for roles");
            return Collections.emptyList();
        }

        List<RoleDescriptor> roleDescriptors = doGetRole(entityID, roleName);
        if (roleDescriptors == null || roleDescriptors.isEmpty()) {
            log.debug("Entity descriptor {} did not contain any {} roles", entityID, roleName);
            return Collections.emptyList();
        }

        Iterator<RoleDescriptor> roleDescItr = roleDescriptors.iterator();
        while (roleDescItr.hasNext()) {
            if (!isValid(roleDescItr.next())) {
                log.debug("Metadata document contained a role of type {} for entity {}, but it was invalid", roleName,
                        entityID);
                roleDescItr.remove();
            }
        }

        if (roleDescriptors.isEmpty()) {
            log.debug("Entity descriptor {} did not contain any valid {} roles", entityID, roleName);
        }
        return roleDescriptors;
    }

    /**
     * Gets the identified roles from an EntityDescriptor. This method should not check if the provider is initialized,
     * if arguments are null, if the roles are valid, etc. All of this is done by the invoker of this method.
     * 
     * @param entityID ID of the entity from which to retrieve the roles, never null
     * @param roleName name of the roles to search for, never null
     * 
     * @return the modifiable list of identified roles or an empty list if no roles exists
     * 
     * @throws ResolverException thrown if there is a problem searching for the roles
     */
    @Nonnull @NonnullElements protected List<RoleDescriptor> doGetRole(@Nullable final String entityID,
            @Nullable final QName roleName) throws ResolverException {
        EntityDescriptor entity = doGetEntityDescriptor(entityID);
        if (entity == null) {
            log.debug("Metadata document did not contain a descriptor for entity {}", entityID);
            return Collections.emptyList();
        }

        List<RoleDescriptor> descriptors = entity.getRoleDescriptors(roleName);
        if (descriptors != null && !descriptors.isEmpty()) {
            return new ArrayList<RoleDescriptor>(descriptors);
        }

        return Collections.emptyList();
    }

    /**
     * Resolve an entity descriptor based on entityID using the wrapped metadata resolver.
     * 
     * @param entityID the entityID of the EntityDescriptor to resolve.
     * @return the resolved EntityDescriptor, or null if not found
     * @throws ResolverException  if there is a problem resolving the EntityDescriptor
     */
    protected EntityDescriptor doGetEntityDescriptor(String entityID) throws ResolverException {
        //TODO should perhaps pass through the entire original CriteriaSet
        return entityDescriptorResolver.resolveSingle(new CriteriaSet(new EntityIdCriterion(entityID)));
    }

    /**
     * Get role descriptor for a given entityID and role and protocol.
     * 
     * @param entityID  entityID to lookup
     * @param roleName  role to lookup
     * @param supportedProtocol protocol to lookup
     * 
     * @return  list of roles
     * @throws ResolverException if an error occurs
     */
    @Nullable protected RoleDescriptor getRole(@Nullable final String entityID, @Nullable final QName roleName,
            @Nullable final String supportedProtocol) 
            throws ResolverException {
        if (Strings.isNullOrEmpty(entityID)) {
            log.debug("EntityDescriptor entityID was null or empty, skipping search for role");
            return null;
        }

        if (roleName == null) {
            log.debug("Role descriptor name was null, skipping search for role");
            return null;
        }

        if (Strings.isNullOrEmpty(supportedProtocol)) {
            log.debug("Supported protocol was null, skipping search for role.");
            return null;
        }

        RoleDescriptor role = doGetRole(entityID, roleName, supportedProtocol);
        if (role == null) {
            log.debug("Metadata document does not contain a role of type {} supporting protocol {} for entity {}",
                    new Object[] { roleName, supportedProtocol, entityID });
            return null;
        }

        if (!isValid(role)) {
            log.debug("Metadata document contained a role of type {} supporting protocol {} for entity {}, " 
                    + "but it was not longer valid", new Object[] { roleName, supportedProtocol, entityID });
            return null;
        }

        return role;
    }

    /**
     * Gets the role which supports the given protocol.
     * 
     * @param entityID ID of the entity from which to retrieve roles, never null
     * @param roleName name of the role to search for, never null
     * @param supportedProtocol protocol to search for, never null
     * 
     * @return the role supporting the protocol or null if no such role exists
     * 
     * @throws ResolverException thrown if there is a problem search for the roles
     */
    protected RoleDescriptor doGetRole(String entityID, QName roleName, String supportedProtocol) 
            throws ResolverException {
        List<RoleDescriptor> roles = doGetRole(entityID, roleName);
        if (roles == null || roles.isEmpty()) {
            log.debug("Metadata document did not contain any role descriptors of type {} for entity {}", roleName,
                    entityID);
            return null;
        }

        Iterator<RoleDescriptor> rolesItr = roles.iterator();
        RoleDescriptor role = null;
        while (rolesItr.hasNext()) {
            role = rolesItr.next();
            if (role != null && role.isSupportedProtocol(supportedProtocol)) {
                return role;
            }
        }

        return null;
    }
    
    /**
     * Returns whether the given descriptor is valid. If valid metadata is not required this method always returns true.
     * 
     * @param descriptor the descriptor to check
     * 
     * @return true if valid metadata is not required or the given descriptor is valid, false otherwise
     */
    protected boolean isValid(XMLObject descriptor) {
        if (descriptor == null) {
            return false;
        }

        if (!isRequireValidMetadata()) {
            return true;
        }

        return SAML2Support.isValid(descriptor);
    }


}
