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

package org.opensaml.saml.criterion;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.resolver.Criterion;

/** {@link Criterion} representing a SAML binding. */
public final class BindingCriterion implements Criterion {

    /** The SAML binding URI. */
    @Nonnull @NotEmpty private final String binding;

    /**
     * Constructor.
     * 
     * @param bindingUri the SAML binding URI
     */
    public BindingCriterion(@Nonnull @NotEmpty final String bindingUri) {
        binding = Constraint.isNotNull(StringSupport.trimOrNull(bindingUri), "Binding URI cannot be null or empty");
    }

    /**
     * Get the SAML binding URI.
     * 
     * @return the SAML binding URI
     */
    @Nonnull @NotEmpty public String getBinding() {
        return binding;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("BindingCriterion [binding=");
        builder.append(binding);
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return binding.hashCode();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof BindingCriterion) {
            return binding.equals(((BindingCriterion) obj).binding);
        }

        return false;
    }
}