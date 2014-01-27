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

package org.opensaml.saml.common.profile.logic;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.saml2.metadata.NameIDFormat;
import org.opensaml.saml.saml2.metadata.SSODescriptor;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * Function to return a set of candidate NameIdentifier/NameID Format values derived from an entity's
 * SAML metadata. 
 */
public class MetadataNameIdentifierFormatStrategy implements Function<ProfileRequestContext, List<String>> {

    /** Strategy function to lookup the {@link SSODescriptor} to read from. */
    @Nullable private Function<ProfileRequestContext, SSODescriptor> ssoDescriptorLookupStrategy;

    /**
     * Set the lookup strategy to use to obtain an {@link SSODescriptor}.
     * 
     * @param strategy  lookup strategy
     */
    public synchronized void setSSODescriptorLookupStrategy(
            @Nonnull final Function<ProfileRequestContext, SSODescriptor> strategy) {
        ssoDescriptorLookupStrategy = Constraint.isNotNull(strategy, "SSODescriptor lookup strategy cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    @Nullable public List<String> apply(@Nullable final ProfileRequestContext input) {
        if (ssoDescriptorLookupStrategy != null) {
            final SSODescriptor role = ssoDescriptorLookupStrategy.apply(input);
            if (role != null) {
                final List<String> strings = Lists.newArrayList();
                for (final NameIDFormat nif : role.getNameIDFormats()) {
                    if (nif.getFormat() != null) {
                        strings.add(nif.getFormat());
                    }
                }
                return strings;
            }
        }
        return Collections.emptyList();
    }

}