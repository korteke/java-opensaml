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

import net.shibboleth.utilities.java.support.component.AbstractDestructableIdentifiedInitializableComponent;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * A {@link MetadataResolver} implementation that answers requests by composing the answers of child
 * {@link MetadataResolver}s.
 */
public class CompositeMetadataResolver extends AbstractDestructableIdentifiedInitializableComponent 
        implements MetadataResolver{
    
    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(CompositeMetadataResolver.class);

    /** Resolvers composed by this resolver. */
    private List<MetadataResolver> resolvers;

    /** Constructor. */
    public CompositeMetadataResolver() {
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
    public Iterable<EntityDescriptor> resolve(CriteriaSet criteria) throws ResolverException {
        ComponentSupport.ifNotInitializedThrowUninitializedComponentException(this);
        return new CompositeMetadataResolverIterable(resolvers, criteria);
    }

    /** {@inheritDoc} */
    public EntityDescriptor resolveSingle(CriteriaSet criteria) throws ResolverException {
        ComponentSupport.ifNotInitializedThrowUninitializedComponentException(this);
        EntityDescriptor metadata = null;
        for (MetadataResolver resolver : resolvers) {
            metadata = resolver.resolveSingle(criteria);
            if (metadata != null) {
                return metadata;
            }
        }

        return null;
    }
    
    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        if (resolvers == null) {
            log.warn("CompositeMetadataResolver was not configured with any member MetadataResolvers");
            resolvers = Collections.emptyList();
        }
    }
    
    /** {@inheritDoc} */
    protected void doDestroy() {
        super.destroy();
        
        resolvers = Collections.emptyList();
    }

    /**
     * {@link Iterable} implementation that provides an {@link Iterator} that lazily iterates over each composed
     * resolver.
     */
    private static class CompositeMetadataResolverIterable implements Iterable<EntityDescriptor> {

        /** Class logger. */
        private final Logger log = LoggerFactory.getLogger(CompositeMetadataResolverIterable.class);

        /** Resolvers over which to iterate. */
        private final List<MetadataResolver> resolvers;

        /** Criteria being search for. */
        private final CriteriaSet criteria;

        /**
         * Constructor.
         * 
         * @param composedResolvers resolvers from which results will be pulled
         * @param metadataCritiera criteria for the resolver query
         */
        public CompositeMetadataResolverIterable(final List<MetadataResolver> composedResolvers,
                final CriteriaSet metadataCritiera) {
            resolvers = ImmutableList.<MetadataResolver> builder()
                            .addAll(Iterables.filter(composedResolvers, Predicates.notNull())).build();

            criteria = metadataCritiera;
        }

        /** {@inheritDoc} */
        public Iterator<EntityDescriptor> iterator() {
            return new CompositeMetadataResolverIterator();
        }

        /** {@link Iterator} implementation that lazily iterates over each composed resolver. */
        private class CompositeMetadataResolverIterator implements Iterator<EntityDescriptor> {

            /** Iterator over the composed resolvers. */
            private Iterator<MetadataResolver> resolverIterator;

            /** Current resolver from which we are getting results. */
            private MetadataResolver currentResolver;

            /** Iterator over the results of the current resolver. */
            private Iterator<EntityDescriptor> currentResolverMetadataIterator;

            /** Constructor. */
            public CompositeMetadataResolverIterator() {
                resolverIterator = resolvers.iterator();
            }

            /** {@inheritDoc} */
            public boolean hasNext() {
                if (!currentResolverMetadataIterator.hasNext()) {
                    proceedToNextResolverIterator();
                }

                return currentResolverMetadataIterator.hasNext();
            }

            /** {@inheritDoc} */
            public EntityDescriptor next() {
                if (!currentResolverMetadataIterator.hasNext()) {
                    proceedToNextResolverIterator();
                }

                return currentResolverMetadataIterator.next();
            }

            /** {@inheritDoc} */
            public void remove() {
                throw new UnsupportedOperationException();
            }

            /**
             * Proceed to the next composed resolvers that has a response to the resolution query.
             */
            private void proceedToNextResolverIterator() {
                try {
                    while (resolverIterator.hasNext()) {
                        currentResolver = resolverIterator.next();
                        currentResolverMetadataIterator = currentResolver.resolve(criteria).iterator();
                        if (currentResolverMetadataIterator.hasNext()) {
                            return;
                        }
                    }
                } catch (ResolverException e) {
                    log.debug("Error encountered attempting to fetch results from resolver", e);
                }
            }
        }
    }
}