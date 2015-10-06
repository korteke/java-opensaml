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
import net.shibboleth.utilities.java.support.annotation.constraint.NotLive;
import net.shibboleth.utilities.java.support.annotation.constraint.Unmodifiable;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.saml.metadata.resolver.index.MetadataIndex;
import org.opensaml.saml.metadata.resolver.index.MetadataIndexKey;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;

import com.google.common.base.Function;

/**
 * Implementation of {@link MetadataIndex} which is based on injected strategy functions.
 */
public class FunctionDrivenMetadataIndex implements MetadataIndex {
    
    /** Function for producing index keys from a CriteriaSet. */
    @Nonnull private Function<CriteriaSet, Set<MetadataIndexKey>> criteriaStrategy;
    
    /** Function for producing index keys from an EntityDescriptor. */
    @Nonnull private Function<EntityDescriptor, Set<MetadataIndexKey>> descriptorStrategy;
    
    /**
     * Constructor.
     * @param descriptorFunction function for producing index keys from an EntityDescriptor
     * @param criteriaFunction  function for producing index keys from a CriteriaSet
     */
    public FunctionDrivenMetadataIndex(
            @Nonnull final Function<EntityDescriptor, Set<MetadataIndexKey>> descriptorFunction, 
            @Nonnull final Function<CriteriaSet, Set<MetadataIndexKey>> criteriaFunction) {
        descriptorStrategy = Constraint.isNotNull(descriptorFunction, "EntityDescriptor strategy function was null");
        criteriaStrategy = Constraint.isNotNull(criteriaFunction, "CriteriaSet strategy function was null");
    }

    /** {@inheritDoc} */
    @Nullable @NonnullElements @Unmodifiable @NotLive 
    public Set<MetadataIndexKey> generateKeys(@Nonnull final CriteriaSet criteriaSet) {
        Constraint.isNotNull(criteriaSet, "CriteriaSet was null");
        return criteriaStrategy.apply(criteriaSet);
    }

    /** {@inheritDoc} */
    @Nullable @NonnullElements @Unmodifiable @NotLive 
    public Set<MetadataIndexKey> generateKeys(@Nonnull final EntityDescriptor descriptor) {
        Constraint.isNotNull(descriptor, "EntityDescriptor was null");
        return descriptorStrategy.apply(descriptor);
    }

}
