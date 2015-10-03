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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import org.opensaml.saml.metadata.resolver.index.MetadataIndexKey;
import org.opensaml.saml.metadata.resolver.index.SimpleStringMetadataIndexKey;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;

import com.google.common.base.Function;

public class UppercaseEntityIdDescriptorFunction implements Function<EntityDescriptor, Set<MetadataIndexKey>> {
    
    @Nullable public Set<MetadataIndexKey> apply(@Nullable EntityDescriptor input) {
        if (input == null) {
            return Collections.emptySet();
        }
        HashSet<MetadataIndexKey> result = new HashSet<>();
        if (input != null) {
            result.add(new SimpleStringMetadataIndexKey(input.getEntityID().toUpperCase()));
        }
        return result;
    }
    
}