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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.component.AbstractDestructableIdentifiableInitializableComponent;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A metadata provider that uses registered resolvers, in turn, to answer queries.
 * 
 * The Iterable of entity descriptors returned is the first non-null and non-empty Iterable found 
 * while iterating over the registered resolvers in resolver list order.
 * 
 */
public class ChainingMetadataResolver extends AbstractDestructableIdentifiableInitializableComponent 
        implements MetadataResolver{

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(ChainingMetadataResolver.class);

    /** Registered resolvers. */
    private List<MetadataResolver> resolvers;

    /** Constructor. */
    public ChainingMetadataResolver() {
        super();
        resolvers = Collections.EMPTY_LIST;
    }

    /**
     * Gets an immutable the list of currently registered resolvers.
     * 
     * @return list of currently registered resolvers
     */
    public List<MetadataResolver> getResolvers() {
        return resolvers;
    }

    /**
     * Sets the current set of metadata resolvers.
     * 
     * @param newResolvers the metadata resolvers to use
     * 
     * @throws ResolverException thrown if there is a problem adding the metadata provider
     */
    public void setResolvers(List<MetadataResolver> newResolvers) throws ResolverException {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        
        if (newResolvers == null || newResolvers.isEmpty()) {
            resolvers = Collections.emptyList();
            return;
        }
        
        resolvers = Collections.unmodifiableList(newResolvers);
    }

    /** {@inheritDoc} */
    public boolean isRequireValidMetadata() {
        log.warn("Attempt to access unsupported requireValidMetadata property on ChainingMetadataResolver");
        return false;
    }

    /** {@inheritDoc} */
    public void setRequireValidMetadata(boolean requireValidMetadata) {
        throw new UnsupportedOperationException("Setting require valid metadata is not supported on chaining resolver");
    }

    /** {@inheritDoc} */
    public MetadataFilter getMetadataFilter() {
        log.warn("Attempt to access unsupported MetadataFilter property on ChainingMetadataResolver");
        return null;
    }

    /** {@inheritDoc} */
    public void setMetadataFilter(MetadataFilter newFilter) {
        throw new UnsupportedOperationException("Metadata filters are not supported on ChainingMetadataProviders");
    }
    
    /** {@inheritDoc} */
    @Nullable public EntityDescriptor resolveSingle(CriteriaSet criteria) throws ResolverException {
        ComponentSupport.ifNotInitializedThrowUninitializedComponentException(this);
        
        Iterable<EntityDescriptor> iterable = resolve(criteria);
        if (iterable != null) {
            Iterator<EntityDescriptor> iterator = iterable.iterator();
            if (iterator != null && iterator.hasNext()) {
                return iterator.next();
            }
        }
        return null;
    }
    
    /** {@inheritDoc} */
    @Nonnull public Iterable<EntityDescriptor> resolve(CriteriaSet criteria) throws ResolverException {
        ComponentSupport.ifNotInitializedThrowUninitializedComponentException(this);
        
        for (MetadataResolver resolver : resolvers) {
            try {
                Iterable<EntityDescriptor> descriptors = resolver.resolve(criteria);
                if (descriptors != null && descriptors.iterator().hasNext()) {
                    return descriptors;
                }
            } catch (ResolverException e) {
                log.warn("Error retrieving metadata from resolver of type {}, proceeding to next resolver",
                        resolver.getClass().getName(), e);
                continue;
            }
        }
        
        return Collections.emptyList();
    }

    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        if (resolvers == null) {
            log.warn("ChainingMetadataResolver was not configured with any member MetadataResolvers");
            resolvers = Collections.emptyList();
        }
    }

    /** {@inheritDoc} */
    protected void doDestroy() {
        super.destroy();
        
        resolvers = Collections.emptyList();
    }

}