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
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.Criterion;

/** {@link Criterion} representing an entity role. */
public final class EntityRoleCriterion implements Criterion {

    /** The entity role. */
    @Nonnull private final QName role;

    /**
     * Constructor.
     * 
     * @param samlRole the entity role
     */
    public EntityRoleCriterion(@Nonnull final QName samlRole) {
        role = Constraint.isNotNull(samlRole, "SAML role cannot be null");
    }

    /**
     * Gets the entity role.
     * 
     * @return the entity role
     */
    @Nonnull public QName getRole() {
        return role;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EntityRoleCriterion [role=");
        builder.append(role);
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return role.hashCode();
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

        if (obj instanceof EntityRoleCriterion) {
            return role.equals(((EntityRoleCriterion) obj).role);
        }

        return false;
    }
}