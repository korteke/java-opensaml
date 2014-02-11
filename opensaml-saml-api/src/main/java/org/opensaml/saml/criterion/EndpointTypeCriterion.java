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

/** {@link Criterion} representing a SAML metadata endpoint type. */
public final class EndpointTypeCriterion implements Criterion {

    /** The endpoint type. */
    @Nonnull private final QName type;

    /**
     * Constructor.
     * 
     * @param endpointType the endpoint type
     */
    public EndpointTypeCriterion(@Nonnull final QName endpointType) {
        type = Constraint.isNotNull(endpointType, "SAML metadata endpoint type cannot be null");
    }

    /**
     * Get the endpoint type.
     * 
     * @return the endpoint type
     */
    @Nonnull public QName getEndpointType() {
        return type;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EndpointTypeCriterion [type=");
        builder.append(type);
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return type.hashCode();
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

        if (obj instanceof EndpointTypeCriterion) {
            return type.equals(((EndpointTypeCriterion) obj).type);
        }

        return false;
    }
}