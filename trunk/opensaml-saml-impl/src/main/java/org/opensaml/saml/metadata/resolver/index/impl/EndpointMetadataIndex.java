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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.annotation.constraint.NotLive;
import net.shibboleth.utilities.java.support.annotation.constraint.Unmodifiable;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.saml.metadata.resolver.index.MetadataIndex;
import org.opensaml.saml.metadata.resolver.index.MetadataIndexKey;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

/**
 * An implementation of {@link MetadataIndex} which indexes entities by their role endpoint locations.
 * 
 * <p>
 * The indexed endpoint location keys are scoped by the containing {@link RoleDescriptor} type, {@link Endpoint} type,
 * and whether or not the endpoint value was a standard location ({@link Endpoint#getLocation()}) 
 * or a response location ({@link Endpoint#getResponseLocation()}).
 * </p>
 */
public class EndpointMetadataIndex implements MetadataIndex {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(EndpointMetadataIndex.class);
    
    /** The indexable endpoint types. */
    private Map<QName, Set<QName>> endpointTypes;
    
    /**
     * Constructor.
     * 
     * <p>
     * All endpoints in all roles will be indexed.
     * </p>
     */
    public EndpointMetadataIndex() {
        endpointTypes = Collections.emptyMap();
    }
    
    /**
     * Constructor.
     *
     * @param indexableTypes the map of indexable endpoint types, keyed by role descriptor type
     */
    public EndpointMetadataIndex(Map<QName, Set<QName>> indexableTypes) {
        endpointTypes = new HashMap<>(Constraint.isNotNull(indexableTypes, "Map of indexable types may not be null"));
    }

    /** {@inheritDoc} */
    @Nullable @NonnullElements @Unmodifiable @NotLive
    public Set<MetadataIndexKey> generateKeys(@Nonnull EntityDescriptor descriptor) {
        Constraint.isNotNull(descriptor, "EntityDescriptor was null");
        HashSet<MetadataIndexKey> result = new HashSet<>();
        for (RoleDescriptor role : descriptor.getRoleDescriptors()) {
            QName roleType = role.getSchemaType();
            if (roleType == null) {
                roleType = role.getElementQName();
            }
            for (Endpoint endpoint : role.getEndpoints()) {
                QName endpointType = endpoint.getSchemaType();
                if (endpointType == null) {
                    endpointType = endpoint.getElementQName();
                }
                if (shouldIndex(roleType, endpointType)) {
                    String location = StringSupport.trimOrNull(endpoint.getLocation());
                    if (location != null) {
                        log.trace("Indexing endpoint - role '{}', endpoint type '{}', location '{}'", 
                                roleType, endpointType, location);
                        result.add(new EndpointMetadataIndexKey(roleType, endpointType, location, false));
                    }
                    String responseLocation = StringSupport.trimOrNull(endpoint.getResponseLocation());
                    if (responseLocation != null) {
                        log.trace("Indexing response endpoint - role '{}', endpoint type '{}', response location '{}'", 
                                roleType, endpointType, responseLocation);
                        result.add(new EndpointMetadataIndexKey(roleType, endpointType, responseLocation, true));
                    }
                }
            }
        }
        return result;
    }

    /**
     * Determine whether endpoints of the specified role and endpoint type should be indexed.
     * 
     * @param roleType the role descriptor type
     * @param endpointType the endpoint type
     * @return true if endpoint should be indexed, false otherwise
     */
    private boolean shouldIndex(QName roleType, QName endpointType) {
        if (endpointTypes.isEmpty()) {
            //TODO is this the right default for an empty config?
            return true; 
        }
        Set<QName> indexableEndpoints = endpointTypes.get(roleType);
        if (indexableEndpoints != null && indexableEndpoints.contains(endpointType)) {
            return true;
        }
        return false;
    }

    /** {@inheritDoc} */
    @Nullable @NonnullElements @Unmodifiable @NotLive
    public Set<MetadataIndexKey> generateKeys(@Nonnull CriteriaSet criteriaSet) {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * An implementation of {@link MetadataIndexKey} representing a single SAML metadata endpoint.
     */
    protected static class EndpointMetadataIndexKey implements MetadataIndexKey {
        
        /** The role type. */
        @Nonnull private final QName role;
        
        /** The endpoint type. */
        @Nonnull private final QName endpoint;
        
        /** The location. */
        @Nonnull private final String location;
        
        /** Respone location flag. */
        private final boolean response;

        /**
         * Constructor.
         * 
         * @param roleType the role type
         * @param endpointType the endpoint type
         * @param endpointLocation the endpoint location
         * @param isResponse flag indicating whether location is a response or not
         */
        public EndpointMetadataIndexKey(@Nonnull final QName roleType, 
                @Nonnull final QName endpointType, 
                @Nonnull @NotEmpty final String endpointLocation,
                boolean isResponse) {
            role = Constraint.isNotNull(roleType, "SAML role cannot be null");
            endpoint = Constraint.isNotNull(endpointType, "SAML endpoint type cannot be null");
            location = Constraint.isNotNull(StringSupport.trimOrNull(endpointLocation),
                    "SAML role cannot be null or empty");
            response = isResponse;
        }

        /**
         * Gets the entity role.
         * 
         * @return the entity role
         */
        @Nonnull public QName getRoleType() {
            return role;
        }
        
        /**
         * Gets the entity endpoint type.
         * 
         * @return the endpoint type
         */
        @Nonnull public QName getEndpointType() {
            return endpoint;
        }

        /**
         * Gets the location.
         * 
         * @return the location
         */
        @Nonnull public String getLocation() {
            return location;
        }
        
        /**
         * Gets the response location flag.
         * 
         * @return true if endpoint is a response location, false otherwise
         */
        public boolean isResponse() {
            return response;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("role", role)
                    .add("endpoint", endpoint)
                    .add("location", location)
                    .add("isResponse", response)
                    .toString();
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return Objects.hash(getRoleType(), getEndpointType(), getLocation(), isResponse());
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj instanceof EndpointMetadataIndexKey) {
                EndpointMetadataIndexKey other = (EndpointMetadataIndexKey) obj;
                return this.role.equals(other.role) 
                        && this.endpoint.equals(other.endpoint) 
                        && this.location.equals(other.location) 
                        && this.response == other.response;
            }

            return false;
        }
    }


}
