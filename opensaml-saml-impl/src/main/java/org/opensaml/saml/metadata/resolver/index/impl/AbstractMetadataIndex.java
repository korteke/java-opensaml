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

package org.opensaml.saml.metadata.resolver.index.impl;

import java.util.Set;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.saml.metadata.resolver.index.MetadataIndexKey;
import org.opensaml.saml.metadata.resolver.index.MetadataIndex;
import org.opensaml.saml.metadata.resolver.index.MetadataIndexStore;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;

/**
 * Abstract implementation of {@link MetadataIndex}.
 * 
 * <p>
 * Subclasses then just need to implement the 2 complementary strategies represented by
 * {@link #generateKeys(CriteriaSet)} and {@link #generateKeys(EntityDescriptor)}.
 * </p>
 */
public abstract class AbstractMetadataIndex implements MetadataIndex {
    
    /** {@inheritDoc} */
    public void index(@Nonnull final EntityDescriptor descriptor, @Nonnull final MetadataIndexStore store) {
        Constraint.isNotNull(descriptor, "EntityDescriptor was null");
        Constraint.isNotNull(store, "MetadataIndexStore was null");
        Set<MetadataIndexKey> keys = generateKeys(descriptor);
        if (keys != null) {
            for (MetadataIndexKey key : keys) {
                store.add(key, descriptor);
            }
        }
    }
    
    /**
     * Generate the set of {@link MetadataIndexKey} under which to index the supplied {@link EntityDescriptor}.
     * 
     * @param descriptor the descriptor to process
     * @return the set of index keys representing the descriptor.  May be null or empty, 
     *         but will not contain null elements
     */
    @Nullable @NonnullElements protected abstract Set<MetadataIndexKey> generateKeys(
            @Nonnull final EntityDescriptor descriptor);

}
