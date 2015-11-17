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


import java.util.Collection;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.ext.saml2mdattr.EntityAttributes;
import org.opensaml.saml.metadata.resolver.filter.FilterException;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.Multimap;

/**
 * A filter that adds {@link EntityAttributes} extension content to entities in order to drive software
 * behavior based on them.
 * 
 * The entities to annotate are identified with a {@link Predicate}, and multiple attributes can be
 * associated with each.
 */
public class EntityAttributesFilter implements MetadataFilter {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(EntityAttributesFilter.class);

    /** Rules for adding attributes. */
    @Nonnull @NonnullElements private Multimap<Predicate<EntityDescriptor>,Attribute> applyMap;

    /** Builder for {@link Extensions}. */
    @Nonnull private final SAMLObjectBuilder<Extensions> extBuilder;

    /** Builder for {@link EntityAttributes}. */
    @Nonnull private final SAMLObjectBuilder<EntityAttributes> entityAttributesBuilder;

    /** Constructor. */
    public EntityAttributesFilter() {
        extBuilder = (SAMLObjectBuilder<Extensions>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Extensions>getBuilderOrThrow(
                        Extensions.DEFAULT_ELEMENT_NAME);
        entityAttributesBuilder = (SAMLObjectBuilder<EntityAttributes>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<EntityAttributes>getBuilderOrThrow(
                        EntityAttributes.DEFAULT_ELEMENT_NAME);
    }
    
    /**
     * Set the mappings from {@link Predicate} to {@link Attribute} collection to apply.
     * 
     * @param rules rules to apply
     */
    public void setRules(@Nonnull @NonnullElements final Map<Predicate<EntityDescriptor>,Collection<Attribute>> rules) {
        Constraint.isNotNull(rules, "Rules map cannot be null");
        
        applyMap = ArrayListMultimap.create(rules.size(), 1);
        for (final Map.Entry<Predicate<EntityDescriptor>,Collection<Attribute>> entry : rules.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                applyMap.putAll(entry.getKey(), Collections2.filter(entry.getValue(), Predicates.notNull()));
            }
        }
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
    
    /**
     * Filters entity descriptor.
     * 
     * @param descriptor entity descriptor to filter
     */
    protected void filterEntityDescriptor(@Nonnull final EntityDescriptor descriptor) {
        for (final Map.Entry<Predicate<EntityDescriptor>,Collection<Attribute>> entry : applyMap.asMap().entrySet()) {
            if (!entry.getValue().isEmpty() && entry.getKey().apply(descriptor)) {
                
                // Put extension objects in place.
                Extensions extensions = descriptor.getExtensions();
                if (extensions == null) {
                    extensions = extBuilder.buildObject();
                    descriptor.setExtensions(extensions);
                }
                final Collection<XMLObject> entityAttributesCollection =
                        extensions.getUnknownXMLObjects(EntityAttributes.DEFAULT_ELEMENT_NAME);
                if (entityAttributesCollection.isEmpty()) {
                    entityAttributesCollection.add(entityAttributesBuilder.buildObject());
                }
                final EntityAttributes entityAttributes =
                        (EntityAttributes) entityAttributesCollection.iterator().next();
                
                for (final Attribute attribute : entry.getValue()) {
                    try {
                        log.info("Adding EntityAttribute ({}) to EntityDescriptor ({})", attribute.getName(),
                                descriptor.getEntityID());
                        final Attribute copy = XMLObjectSupport.cloneXMLObject(attribute);
                        entityAttributes.getAttributes().add(copy);
                    } catch (final MarshallingException | UnmarshallingException e) {
                        log.error("Error cloning Attribute", e);
                    }
                }
            }
        }
    }
    
    /**
     * Filters entities descriptor.
     * 
     * @param descriptor entities descriptor to filter
     */
    protected void filterEntitiesDescriptor(@Nonnull final EntitiesDescriptor descriptor) {
        
        // First we check any contained EntitiesDescriptors.
        for (final EntitiesDescriptor group : descriptor.getEntitiesDescriptors()) {
            filterEntitiesDescriptor(group);
        }
        
        // Next, check contained EntityDescriptors.
        for (final EntityDescriptor entity : descriptor.getEntityDescriptors()) {
            filterEntityDescriptor(entity);
        }
    }

}