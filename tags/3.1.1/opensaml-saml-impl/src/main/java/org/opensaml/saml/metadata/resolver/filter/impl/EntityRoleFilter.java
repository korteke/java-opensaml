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

package org.opensaml.saml.metadata.resolver.filter.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotLive;
import net.shibboleth.utilities.java.support.annotation.constraint.Unmodifiable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.metadata.resolver.filter.FilterException;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A filter that removes roles from an entity descriptor. For those roles specified within the SAML metadata
 * specification the role element QName is used to identify the role. For other roles, those that appear as
 * &lt;RoleDescriptor xsi:type="someRoleType"&gt; the role schema type is used to identify the role.
 * 
 * If the entity descriptor does not contain any roles after filter it may, optionally be removed as well. If the root
 * element of the metadata document is an entity descriptor it will never be removed, regardless of of whether it still
 * contains roles.
 * 
 * If and entities descriptor does not contains any entity descriptors after filter it may, optionally, be removed as
 * well. If the root element of the metadata document is an entities descriptor it will never be removed, regardless of
 * of whether it still contains entity descriptors.
 */
public class EntityRoleFilter implements MetadataFilter {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(EntityRoleFilter.class);

    /** List of roles that are NOT removed by this filter. */
    @Nonnull @NonnullElements private List<QName> roleWhiteList;

    /** Whether to keep entity descriptors that contain no roles; default value: true. */
    private boolean removeRolelessEntityDescriptors;

    /** Whether to keep entities descriptors that contain no entity descriptors; default value: true. */
    private boolean removeEmptyEntitiesDescriptors;

    /** QName of extension role element. */
    @Nonnull private final QName extRoleDescriptor;

    /**
     * Constructor.
     * 
     * @param keptRoles list of roles NOT removed by this filter
     */
    public EntityRoleFilter(@Nullable final List<QName> keptRoles) {
        roleWhiteList = new ArrayList<>();

        if (keptRoles != null) {
            roleWhiteList.addAll(keptRoles);
        }
        roleWhiteList = Collections.unmodifiableList(roleWhiteList);

        removeRolelessEntityDescriptors = true;
        removeEmptyEntitiesDescriptors = true;
        
        extRoleDescriptor = new QName(SAMLConstants.SAML20MD_NS, "RoleDescriptor");
    }

    /**
     * Get the unmodifiable list of roles that are NOT removed by this filter.
     * 
     * @return unmodifiable list of roles that are NOT removed by this filter
     */
    @Nonnull @NonnullElements @Unmodifiable @NotLive public List<QName> getRoleWhiteList() {
        return roleWhiteList;
    }

    /**
     * Get whether to remove an entity descriptor if it does not contain any roles after filtering.
     * 
     * @return whether to remove an entity descriptor if it does not contain any roles after filtering
     */
    public boolean getRemoveRolelessEntityDescriptors() {
        return removeRolelessEntityDescriptors;
    }

    /**
     * Set whether to remove an entity descriptor if it does not contain any roles after filtering.
     * 
     * @param remove whether to remove an entity descriptor if it does not contain any roles after filtering
     */
    public void setRemoveRolelessEntityDescriptors(final boolean remove) {
        removeRolelessEntityDescriptors = remove;
    }

    /**
     * Get whether to remove an entities descriptor if it does not contain any entity descriptor or entities
     * descriptors.
     * 
     * @return whether to remove an entities descriptor if it does not contain any entity descriptor or entities
     *         descriptors
     */
    public boolean getRemoveEmptyEntitiesDescriptors() {
        return removeEmptyEntitiesDescriptors;
    }

    /**
     * Set whether to remove an entities descriptor if it does not contain any entity descriptor or entities
     * descriptors.
     * 
     * @param remove whether to remove an entities descriptor if it does not contain any entity descriptor or entities
     *            descriptors
     */
    public void setRemoveEmptyEntitiesDescriptors(final boolean remove) {
        removeEmptyEntitiesDescriptors = remove;
    }

    /** {@inheritDoc} */
    @Override
    @Nullable public XMLObject filter(@Nullable final XMLObject metadata) throws FilterException {
        if (metadata == null) {
            return null;
        }

        if (metadata instanceof EntitiesDescriptor) {
            filterEntitiesDescriptor((EntitiesDescriptor) metadata);
        } else {
            filterEntityDescriptor((EntityDescriptor) metadata);
        }
        
        return metadata;
    }

// Checkstyle: CyclomaticComplexity OFF    
    /**
     * Filters {@link EntitiesDescriptor}.
     * 
     * @param descriptor entities descriptor to filter
     * 
     * @throws FilterException thrown if an effective role name can not be determined
     */
    protected void filterEntitiesDescriptor(@Nonnull final EntitiesDescriptor descriptor) throws FilterException {
        // First we filter out any contained EntityDescriptors
        List<EntityDescriptor> entityDescriptors = descriptor.getEntityDescriptors();
        if (entityDescriptors != null && !entityDescriptors.isEmpty()) {
            List<EntityDescriptor> emptyEntityDescriptors = new ArrayList<>();
            Iterator<EntityDescriptor> entityDescriptorsItr = entityDescriptors.iterator();
            EntityDescriptor entityDescriptor;
            List<RoleDescriptor> entityRoles;
            while (entityDescriptorsItr.hasNext()) {
                entityDescriptor = entityDescriptorsItr.next();
                filterEntityDescriptor(entityDescriptor);
                if (getRemoveRolelessEntityDescriptors()) {
                    entityRoles = entityDescriptor.getRoleDescriptors();
                    if (entityRoles == null || entityRoles.isEmpty()) {
                        log.trace("Filtering out entity descriptor {} from entity group {}", entityDescriptor
                                .getEntityID(), descriptor.getName());
                        emptyEntityDescriptors.add(entityDescriptor);
                    }
                }
            }
            entityDescriptors.removeAll(emptyEntityDescriptors);
        }

        // Next, contained EntityDescriptors
        List<EntitiesDescriptor> entitiesDescriptors = descriptor.getEntitiesDescriptors();
        if (entitiesDescriptors != null && !entitiesDescriptors.isEmpty()) {
            List<EntitiesDescriptor> emptyEntitiesDescriptors = new ArrayList<>();
            Iterator<EntitiesDescriptor> entitiesDescriptorsItr = entitiesDescriptors.iterator();
            EntitiesDescriptor entitiesDescriptor;
            while (entitiesDescriptorsItr.hasNext()) {
                entitiesDescriptor = entitiesDescriptorsItr.next();
                filterEntitiesDescriptor(entitiesDescriptor);
                if (getRemoveEmptyEntitiesDescriptors()) {
                    // Remove the EntitiesDescriptor if does not contain any EntitiesDescriptors or EntityDescriptors
                    if ((entitiesDescriptor.getEntityDescriptors() == null || entitiesDescriptor.getEntityDescriptors()
                            .isEmpty())
                            && (entitiesDescriptor.getEntitiesDescriptors() == null || entitiesDescriptor
                                    .getEntitiesDescriptors().isEmpty())) {
                        log.trace("Filtering out entity descriptor {} from entity group {}", entitiesDescriptor
                                .getName(), descriptor.getName());
                        emptyEntitiesDescriptors.add(entitiesDescriptor);
                    }
                }
            }
            entitiesDescriptors.removeAll(emptyEntitiesDescriptors);
        }
    }
// Checkstyle: CyclomaticComplexity ON

    /**
     * Filters entity descriptor roles.
     * 
     * @param descriptor entity descriptor to filter
     * 
     * @throws FilterException thrown if an effective role name can not be determined
     */
    protected void filterEntityDescriptor(@Nonnull final EntityDescriptor descriptor) throws FilterException {
        List<RoleDescriptor> roles = descriptor.getRoleDescriptors();

        if (roles != null && !roles.isEmpty()) {
            Iterator<RoleDescriptor> rolesItr = roles.iterator();
            QName roleName;
            while (rolesItr.hasNext()) {
                roleName = getRoleName(rolesItr.next());
                if (!roleWhiteList.contains(roleName)) {
                    log.trace("Filtering out role {} from entity {}", roleName, descriptor.getEntityID());
                    rolesItr.remove();
                }
            }
        }
    }

    /**
     * Gets the effective name for the role. This is either the element QName for roles defined within the SAML metadata
     * specification or the element schema type QName for those that are not.
     * 
     * @param role role to get the effective name for
     * 
     * @return effective name of the role
     * 
     * @throws FilterException thrown if the effective role name can not be determined
     */
    protected QName getRoleName(@Nonnull final RoleDescriptor role) throws FilterException {
        QName roleName = role.getElementQName();

        if (extRoleDescriptor.equals(roleName)) {
            roleName = role.getSchemaType();
            if (roleName == null) {
                throw new FilterException("Role descriptor element was " + extRoleDescriptor
                        + " but did not contain a schema type.  This is illegal.");
            }
        }

        return roleName;
    }
    
}