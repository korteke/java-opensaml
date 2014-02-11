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

/** {@link Criterion} representing a SAML binding location. */
public final class BindingLocationCriterion implements Criterion {

    /** The binding location URI. */
    @Nonnull @NotEmpty private final String location;

    /**
     * Constructor.
     * 
     * @param locationUri the binding location URI, never null or empty
     */
    public BindingLocationCriterion(@Nonnull @NotEmpty final String locationUri) {
        location = Constraint.isNotNull(StringSupport.trimOrNull(locationUri), "Location cannot be null or empty");
    }

    /**
     * Gets the binding location URI.
     * 
     * @return the binding location URI
     */
    @Nonnull @NotEmpty public String getLocation() {
        return location;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("BindingLocation [location=");
        builder.append(location);
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return location.hashCode();
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

        if (obj instanceof BindingLocationCriterion) {
            return location.equals(((BindingLocationCriterion) obj).location);
        }

        return false;
    }
}