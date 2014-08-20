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

import org.opensaml.saml.saml2.metadata.Endpoint;

import com.google.common.base.Objects;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.Criterion;

/**
 * {@link Criterion} representing a SAML metadata endpoint object.
 *
 * @param <EndpointType> the type of endpoint
 */
public final class EndpointCriterion<EndpointType extends Endpoint> implements Criterion {

    /** Is this endpoint implicitly trusted? */
    private final boolean trusted;
    
    /** The endpoint. */
    @Nonnull private final EndpointType endpoint;

    /**
     * Constructor.
     * 
     * @param ep the endpoint
     * @param trust if true, the endpoint should be implicitly trusted regardless of verification by other criteria
     */
    public EndpointCriterion(@Nonnull final EndpointType ep, final boolean trust) {
        endpoint = Constraint.isNotNull(ep, "Endpoint cannot be null");
        trusted = trust;
    }

    /**
     * Get the endpoint.
     * 
     * @return the endpoint type
     */
    @Nonnull public EndpointType getEndpoint() {
        return endpoint;
    }
    
    /**
     * Get the trust indicator for the endpoint.
     * 
     * @return true iff the endpoint does not require independent verification against a trusted source of endpoints
     */
    public boolean isTrusted() {
        return trusted;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EndpointCriterion [type=")
            .append(endpoint.getElementQName());
        if (endpoint.getBinding() != null) {
            builder.append(", Binding=")
                .append(endpoint.getBinding());
        }
        if (endpoint.getLocation() != null) {
            builder.append(", Location=")
                .append(endpoint.getLocation());
        }
        if (endpoint.getResponseLocation() != null) {
            builder.append(", ResponseLocation=")
                .append(endpoint.getResponseLocation());
        }
        builder.append("trusted=").append(trusted)
            .append(']');
        return builder.toString();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return toString().hashCode();
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

        if (obj instanceof EndpointCriterion) {
            final Endpoint endpoint2 = ((EndpointCriterion) obj).getEndpoint();
            if (!Objects.equal(endpoint.getElementQName(), endpoint2.getElementQName())) {
                return false;
            } else if (!Objects.equal(endpoint.getBinding(), endpoint2.getBinding())) {
                return false;
            } else if (!Objects.equal(endpoint.getLocation(), endpoint2.getLocation())) {
                return false;
            } else if (!Objects.equal(endpoint.getResponseLocation(), endpoint2.getResponseLocation())) {
                return false;
            }
            
            return true;
        }

        return false;
    }
}