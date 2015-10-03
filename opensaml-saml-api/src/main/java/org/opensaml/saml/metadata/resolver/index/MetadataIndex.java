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

package org.opensaml.saml.metadata.resolver.index;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotLive;
import net.shibboleth.utilities.java.support.annotation.constraint.Unmodifiable;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.saml.saml2.metadata.EntityDescriptor;

/**
 * A component which is capable of indexing an {@link EntityDescriptor}, using one or more implementation-specific
 * instances of {@link MetadataIndexKey} and stored in a {@link MetadataIndexStore}.
 */
public interface MetadataIndex {
    
    /**
     * Index the supplied {@link EntityDescriptor} into the supplied {@link MetadataIndexStore}.
     * 
     * <p>
     * An implementation typically will simply generate one or more instances of {@link MetadataIndexKey}
     * based on the descriptor, and use those to store the descriptor in the {@link MetadataIndexStore}.
     * </p>
     * 
     * @param descriptor the descriptor to index
     * @param store the store for the indexed data
     */
    public void index(@Nonnull final EntityDescriptor descriptor, @Nonnull final MetadataIndexStore store);
    
    /**
     * Generate a set of one or more {@link MetadataIndexKey} instances based on the input {@link CriteriaSet}.
     * 
     * <p>
     * These index key instances reflect the type of indexing performed by and "understood" by the implementation,
     * and as such should correspond directly to the {@link MetadataIndexKey} types generated during descriptor
     * indexing via {@link #index(EntityDescriptor, MetadataIndexStore)}.
     * </p>
     * 
     * @param criteriaSet the criteria set to process
     * @return the set of index keys generated from the criteria.  May be null or empty, 
     *         but will not contain null elements.
     */
    @Nullable @NonnullElements @Unmodifiable @NotLive 
    public Set<MetadataIndexKey> generateKeys(@Nonnull final CriteriaSet criteriaSet);

}
