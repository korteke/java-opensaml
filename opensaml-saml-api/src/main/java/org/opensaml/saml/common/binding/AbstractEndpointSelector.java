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

package org.opensaml.saml.common.binding;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.annotation.constraint.Live;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;

/**
 * Endpoint selectors choose the endpoint that should be used to contact a peer.
 */
@NotThreadSafe
public abstract class AbstractEndpointSelector {

    /** Bindings supported by the issuer. */
    @Nonnull @NonnullElements private List<String> supportedIssuerBindings;

    /** SAML request within the message flow. */
    @Nullable private SAMLObject samlRequest;

    /** SAML response within the message flow. */
    @Nullable private SAMLObject samlResponse;

    /** Resolver of metadata for the relying party. */
    @Nullable private MetadataResolver metadataResolver;

    /** Metadata of party to select endpoing for. */
    @Nullable private EntityDescriptor entityMetadata;

    /** Role metadata of party to select endpoing for. */
    @Nullable private RoleDescriptor entityRoleMetadata;

    /** Type of endpoint needed. */
    @Nullable private QName endpointType;

    /** Constructor. */
    public AbstractEndpointSelector() {
        supportedIssuerBindings = new ArrayList<>(5);
    }

    /**
     * Gets type of endpoint needed.
     * 
     * @return type of endpoint needed
     */
    @Nullable public QName getEndpointType() {
        return endpointType;
    }

    /**
     * Sets the type of endpoint needed.
     * 
     * @param type type of endpoint needed
     */
    public void setEndpointType(@Nullable final QName type) {
        endpointType = type;
    }

    /**
     * Gets the metadata resolver used to look up entity information.
     * 
     * @return metadata resolver used to look up entity information
     */
    @Nullable public MetadataResolver getMetadataResolver() {
        return metadataResolver;
    }

    /**
     * Sets the metadata resolver used to look up entity information.
     * 
     * @param resolver metadata resolver used to look up entity information
     */
    public void setMetadataResolver(@Nullable final MetadataResolver resolver) {
        metadataResolver = resolver;
    }

    /**
     * Gets the metadata of the entity.
     * 
     * @return metadata of the entity
     */
    @Nullable public EntityDescriptor getEntityMetadata() {
        return entityMetadata;
    }

    /**
     * Sets the metadata of the entity.
     * 
     * @param entity metadata of the entity
     */
    public void setEntityMetadata(@Nullable final EntityDescriptor entity) {
        entityMetadata = entity;
    }

    /**
     * Gets the role of the entity.
     * 
     * @return role of the entity
     */
    @Nullable public RoleDescriptor getEntityRoleMetadata() {
        return entityRoleMetadata;
    }

    /**
     * Sets the role of the entity.
     * 
     * @param role role of the entity
     */
    public void setEntityRoleMetadata(@Nullable final RoleDescriptor role) {
        entityRoleMetadata = role;
    }

    /**
     * Gets the SAML request made.
     * 
     * @return SAML request made
     */
    @Nullable public SAMLObject getSamlRequest() {
        return samlRequest;
    }

    /**
     * Sets the SAML request made.
     * 
     * @param request SAML request made
     */
    public void setSamlRequest(@Nullable final SAMLObject request) {
        samlRequest = request;
    }

    /**
     * Gets the response to the SAML request.
     * 
     * @return response to the SAML request
     */
    @Nullable public SAMLObject getSamlResponse() {
        return samlResponse;
    }

    /**
     * Sets the response to the SAML request.
     * 
     * @param response response to the SAML request
     */
    public void setSamlResponse(@Nullable final SAMLObject response) {
        samlResponse = response;
    }

    /**
     * Gets the list of bindings supported by the message issuer.
     * 
     * @return list of bindings supported by the message issuer
     */
    @Nonnull @NonnullElements @Live public List<String> getSupportedIssuerBindings() {
        return supportedIssuerBindings;
    }

    /**
     * Selects the endpoint to which messages should be sent.
     * 
     * @return endpoint to which messages should be sent, or null if no suitable endpoint can be determined
     */
    @Nullable public abstract Endpoint selectEndpoint();
    
}