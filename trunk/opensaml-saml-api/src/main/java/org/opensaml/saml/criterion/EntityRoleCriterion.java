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

import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.logic.Assert;
import net.shibboleth.utilities.java.support.resolver.Criterion;

/** {@link Criterion} representing an entity role. */
public final class EntityRoleCriterion implements Criterion {

    /** The entity role. */
    private final QName role;

    /**
     * Constructor.
     * 
     * @param samlRole the entity role, never null
     */
    public EntityRoleCriterion(QName samlRole) {
        role = Assert.isNotNull(samlRole, "SAML role can not be null");
    }

    /**
     * Gets the entity role.
     * 
     * @return the entity role, never null
     */
    public QName getRole() {
        return role;
    }

    /** {@inheritDoc} */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EntityRoleCriterion [role=");
        builder.append(role);
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return role.hashCode();
    }

    /** {@inheritDoc} */
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