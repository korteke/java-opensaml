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

package org.opensaml.saml.common.messaging.context;

import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.messaging.context.BaseContext;

/**
 * Context that carries information about SAML artifact usage.
 */
public class SAMLArtifactContext extends BaseContext {

    /** The artifact type. */
    @Nullable private byte[] artifactType;
    
    /** The artifact source entityID. */
    @Nullable @NotEmpty private String sourceEntityId;
    
    /** The artifact resolution endpoint URL. */
    @Nullable @NotEmpty private String sourceArtifactResolutionServiceEndpointUrl;
    
    /** The artifact resolution endpoint index. */
    @Nullable private Integer sourceArtifactResolutionServiceEndpointIndex;

    /**
     * Gets the artifact type.
     * 
     * @return artifact type, may be null
     */
    @Nullable public byte[] getArtifactType() {
        return artifactType;
    }

    /**
     * Sets the artifact type.
     * 
     * @param type the new artifact type
     */
    public void setArtifactType(@Nullable final byte[] type) {
        artifactType = type;
    }

    /**
     * Get the artifact source entityID.
     * 
     * @return the source entityID, may be null
     */
    @Nullable @NotEmpty public String getSourceEntityId() {
        return sourceEntityId;
    }

    /**
     * Set the artifact source entityID.
     * 
     * @param entityId the new source entityID
     */
    public void setSourceEntityId(@Nullable final String entityId) {
        sourceEntityId = StringSupport.trimOrNull(entityId);
    }

    /**
     * Get the artifact resolution service endpoint URL.
     * 
     * @return the URL
     */
    @Nullable @NotEmpty public String getSourceArtifactResolutionServiceEndpointURL() {
        return sourceArtifactResolutionServiceEndpointUrl;
    }

    /**
     * Set the artifact resolution service endpoint URL.
     * 
     * @param url the new URL
     */
    public void setSourceArtifactResolutionServiceEndpointURL(@Nullable final String url) {
        sourceArtifactResolutionServiceEndpointUrl = StringSupport.trimOrNull(url);
    }

    /**
     * Get the artifact resolution service endpoint index.
     * 
     * @return the index
     */
    @Nullable public Integer getSourceArtifactResolutionServiceEndpointIndex() {
        return sourceArtifactResolutionServiceEndpointIndex;
    }

    /**
     * Set the source artifact resolution service endpoint index.
     * 
     * @param index the new index
     */
    public void setSourceArtifactResolutionServiceEndpointIndex(@Nullable final Integer index) {
        sourceArtifactResolutionServiceEndpointIndex = index;
    }

}