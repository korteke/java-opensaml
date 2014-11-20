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

import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.metadata.AffiliationDescriptor;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

/**
 * Function which implements a basic strategy for extracting trusted names for PKIX trust engine evaluation.
 * 
 * <p>
 * Names are extracted as follows from these signed metadata element types:
 * <ul>
 * <li><code>EntityDescriptor</code>: the <code>entityID</code> attribute</li> 
 * <li><code>EntitiesDescriptor</code>: the <code>Name</code> attribute</li> 
 * <li><code>RoleDescriptor</code>: the <code>entityID</code> attribute of the parent 
 *     <code>EntityDescriptor</code></li> 
 * <li><code>AffiliationDescriptor</code>: 1) the <code>affiliationOwnerID</code> attribute and 
 *     2) the <code>entityID</code> attribute of the parent <code>EntityDescriptor</code></li> 
 * </ul>
 * </p>
 */
public class BasicDynamicTrustedNamesStrategy implements Function<XMLObject, Set<String>> {

    /** {@inheritDoc} */
    @Nonnull @NonnullElements public Set<String> apply(@Nullable XMLObject input) {
        if (input == null) {
            return Collections.emptySet();
        }
        
        Set<String> rawResult = null;
        
        if (input instanceof EntityDescriptor) {
            rawResult = Collections.singleton(((EntityDescriptor)input).getEntityID());
        } else if (input instanceof EntitiesDescriptor) {
            rawResult = Collections.singleton(((EntitiesDescriptor)input).getName());
        } else if (input instanceof RoleDescriptor) {
            XMLObject parent = input.getParent();
            if (parent instanceof EntityDescriptor) {
                rawResult = Collections.singleton(((EntityDescriptor)parent).getEntityID());
            }
        } else if (input instanceof AffiliationDescriptor) {
            rawResult = Sets.newHashSet();
            
            rawResult.add(((AffiliationDescriptor)input).getOwnerID());
            
            XMLObject parent = input.getParent();
            if (parent instanceof EntityDescriptor) {
                rawResult.add(((EntityDescriptor)parent).getEntityID());
            }
        }
        
        if (rawResult != null) {
            return Sets.newHashSet(StringSupport.normalizeStringCollection(rawResult));
        } else {
            return Collections.emptySet();
        }
        
    }

}
