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

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.metadata.resolver.filter.FilterException;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A filter that allows the composition of {@link MetadataFilter}s. Filters will be executed on the given metadata
 * document in the order they were added to the chain.
 */
public class MetadataFilterChain implements MetadataFilter {

    /** Class logger. */
    private Logger log = LoggerFactory.getLogger(MetadataFilterChain.class);

    /** Registered filters. */
    private List<MetadataFilter> filters;

    /**
     * Constructor.
     */
    public MetadataFilterChain() {
        filters = Collections.emptyList();
    }

    /** {@inheritDoc} */
    public final XMLObject filter(XMLObject xmlObject) throws FilterException {
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
     * Gets the list of {@link MetadataFilter}s that make up this chain.
     * 
     * @return the filters that make up this chain
     */
    public List<MetadataFilter> getFilters() {
        return filters;
    }

    /**
     * Sets the list of {@link MetadataFilter}s that make up this chain.
     * 
     * @param newFilters list of {@link MetadataFilter}s that make up this chain
     */
    public void setFilters(List<MetadataFilter> newFilters) {
        if (newFilters == null || newFilters.isEmpty()) {
            filters.clear();
        }

        ArrayList<MetadataFilter> checkedFilters = new ArrayList<MetadataFilter>();
        for (MetadataFilter filter : newFilters) {
            if (filter != null) {
                checkedFilters.add(filter);
            }
        }

        filters = checkedFilters;
    }
}