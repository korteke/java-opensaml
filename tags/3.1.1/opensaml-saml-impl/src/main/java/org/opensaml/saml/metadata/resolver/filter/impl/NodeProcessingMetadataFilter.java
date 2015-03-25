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
import net.shibboleth.utilities.java.support.component.AbstractInitializableComponent;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.metadata.resolver.filter.FilterException;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import org.opensaml.saml.metadata.resolver.filter.MetadataNodeProcessor;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;

/**
 * An implementation of {@link MetadataFilter} which applies a {@link MetadataNodeProcessor} to each element node in the
 * metadata document tree. The node processors will be applied in the order of {@link List} provided by
 * {@link #setNodeProcessors(List)}. The metadata document tree is traversed depth-first.
 */
public class NodeProcessingMetadataFilter extends AbstractInitializableComponent implements MetadataFilter {

    /** The ordered list of metadata node processors. */
    @Nonnull @NonnullElements private List<MetadataNodeProcessor> processors;
    
    /** Constructor. */
    public NodeProcessingMetadataFilter() {
        processors = Collections.emptyList();
    }

    /**
     * Get the list of metadata node processors.
     * 
     * @return the list of metadata node processors.
     */
    @Nonnull @NonnullElements @Live public List<MetadataNodeProcessor> getNodeProcessors() {
        return processors;
    }

    /**
     * Set the list of metadata node processors.
     * 
     * @param newProcessors the new list of processors to set.
     */
    public void setNodeProcessors(@Nonnull @NonnullElements final List<MetadataNodeProcessor> newProcessors) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        Constraint.isNotNull(newProcessors, "MetadataNodeProcessor list cannot be null");

        processors = new ArrayList<>(Collections2.filter(newProcessors, Predicates.notNull()));
    }

    /** {@inheritDoc} */
    @Override @Nullable public XMLObject filter(@Nullable final XMLObject metadata) throws FilterException {
        ComponentSupport.ifNotInitializedThrowUninitializedComponentException(this);

        if (metadata == null) {
            return null;
        }

        processNode(metadata);

        return metadata;
    }

    /** {@inheritDoc} */
    @Override protected void doDestroy() {
        processors = null;
        super.doDestroy();
    }

    /**
     * Process an individual metadata node.
     * 
     * @param node the metadata node to process.
     * 
     * @throws FilterException if a fatal error is encountered while processing a node
     */
    protected void processNode(XMLObject node) throws FilterException {
        
        for (final MetadataNodeProcessor processor : getNodeProcessors()) {
            processor.process(node);
        }

        final List<XMLObject> children = node.getOrderedChildren();
        if (children != null) {
            for (final XMLObject child : node.getOrderedChildren()) {
                if (child != null) {
                    processNode(child);
                }
            }
        }
    }

}