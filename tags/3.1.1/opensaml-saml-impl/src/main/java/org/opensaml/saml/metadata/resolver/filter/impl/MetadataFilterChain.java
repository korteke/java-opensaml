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
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.Live;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.metadata.resolver.filter.FilterException;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;

/**
 * A filter that allows the composition of {@link MetadataFilter}s. Filters will be executed on the given metadata
 * document in the order they were added to the chain.
 */
public class MetadataFilterChain implements MetadataFilter {

    /** Class logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(MetadataFilterChain.class);

    /** Registered filters. */
    @Nonnull @NonnullElements private List<MetadataFilter> filters;

    /**
     * Constructor.
     */
    public MetadataFilterChain() {
        filters = Collections.emptyList();
    }

    /** {@inheritDoc} */
    @Override
    @Nullable public final XMLObject filter(@Nullable final XMLObject xmlObject) throws FilterException {
        if (xmlObject == null) {
            return null;
        }
        
        synchronized (filters) {
            if (filters == null || filters.isEmpty()) {
                log.debug("No filters configured, nothing to do");
                return xmlObject;
            }
            
            XMLObject current = xmlObject;
            for (MetadataFilter filter : filters) {
                if (current == null) {
                    return null;
                }
                log.debug("Applying filter {}", filter.getClass().getName());
                current = filter.filter(current);
            }
            
            return current;
        }
    }

    /**
     * Get the list of {@link MetadataFilter}s that make up this chain.
     * 
     * @return the filters that make up this chain
     */
    @Nonnull @NonnullElements @Live public List<MetadataFilter> getFilters() {
        return filters;
    }

    /**
     * Set the list of {@link MetadataFilter}s that make up this chain.
     * 
     * @param newFilters list of {@link MetadataFilter}s that make up this chain
     */
    public void setFilters(@Nonnull @NonnullElements final List<MetadataFilter> newFilters) {
        Constraint.isNotNull(newFilters, "Filter collection cannot be null");
        
        filters = new ArrayList<>(Collections2.filter(newFilters, Predicates.notNull()));
    }
    
}