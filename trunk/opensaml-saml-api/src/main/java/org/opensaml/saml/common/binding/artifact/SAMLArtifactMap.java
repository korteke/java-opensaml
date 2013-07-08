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

package org.opensaml.saml.common.binding.artifact;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.component.DestructableComponent;
import net.shibboleth.utilities.java.support.component.IdentifiableComponent;
import net.shibboleth.utilities.java.support.component.InitializableComponent;
import net.shibboleth.utilities.java.support.component.ValidatableComponent;

import org.opensaml.saml.common.SAMLObject;

/**
 * Maps an artifact to a SAML message and back again.
 * 
 * <p>An implementation of this interface MUST ensure that the persisted SAML message is no longer tied to any 
 * parent {@link org.opensaml.core.xml.XMLObject} that may have contained it. This ensures that it can be
 * safely added to another object once retrieved from the map. This might for example be achieved by:
 * 1) cloning the SAMLObject prior to storage, or 2) by serializing it to a string and re-parsing 
 * and unmarshalling it once retrieved from the underlying data store.
 * This requirement may be handled by the SAMLArtifactMap directly, or by the use of of a specific 
 * implementation of {@link SAMLArtifactMapEntryFactory}.</p>
 */
public interface SAMLArtifactMap extends InitializableComponent, DestructableComponent,
    IdentifiableComponent, ValidatableComponent {

    /**
     * Checks if a given artifact has a map entry.
     * 
     * @param artifact the artifact to check
     * 
     * @return true iff this map has an entry for the given artifact
     * @throws IOException if an error occurs retrieving the information
     */
    public boolean contains(@Nonnull @NotEmpty final String artifact) throws IOException;

    /**
     * Creates a mapping between a given artifact and the SAML message to which it maps.
     * 
     * @param artifact the artifact
     * @param relyingPartyId ID of the party the artifact was sent to
     * @param issuerId ID of the issuer of the artifact
     * @param samlMessage the SAML message
     * 
     * @throws IOException  if an error occurs storing the information
     */
    public void put(@Nonnull @NotEmpty final String artifact, @Nonnull @NotEmpty final String relyingPartyId,
            @Nonnull @NotEmpty final String issuerId, @Nonnull final SAMLObject samlMessage)
            throws IOException;

    /**
     * Gets the artifact entry for the given artifact.
     * 
     * @param artifact the artifact to retrieve the entry for
     * 
     * @return the entry, or null if the artifact has already expired or did not exist
     * @throws IOException if an error occurs retrieving the information
     */
    @Nullable public SAMLArtifactMapEntry get(@Nonnull @NotEmpty final String artifact) throws IOException;

    /**
     * Removes the artifact from this map.
     * 
     * @param artifact artifact to be removed
     * @throws IOException if an error occurs retrieving the information
     */
    public void remove(@Nonnull @NotEmpty final String artifact) throws IOException;
    
    /**
     * Represents a mapping between an artifact and a SAML message with some associated information. 
     */
    public interface SAMLArtifactMapEntry {

        /**
         * Gets the artifact that maps to the SAML message.
         * 
         * @return artifact that maps to the SAML message
         */
        @Nonnull @NotEmpty public String getArtifact();

        /**
         * Gets the ID of the issuer of the artifact.
         * 
         * @return ID of the issuer of the artifact
         */
        @Nonnull @NotEmpty public String getIssuerId();

        /**
         * Gets the ID of the relying party the artifact was sent to.
         * 
         * @return ID of the relying party the artifact was sent to
         */
        @Nonnull @NotEmpty public String getRelyingPartyId();

        /**
         * Gets SAML message the artifact maps to.
         * 
         * @return SAML message the artifact maps to
         */
        @Nonnull public SAMLObject getSamlMessage();
    }
    
    /**
     * A factory for producing SAMLArtifactMapEntry instances based on standard inputs,
     * and reading/writing them from/to storage.
     */
    public interface SAMLArtifactMapEntryFactory {
        
        /**
         * Factory method which produces a {@link SAMLArtifactMapEntry}.
         * 
         * @param artifact the artifact
         * @param issuerId ID of the issuer of the artifact
         * @param relyingPartyId ID of the party the artifact was sent to
         * @param samlMessage the SAML message
         * 
         * @return the new map entry instance
         */
        @Nonnull public SAMLArtifactMapEntry newEntry(@Nonnull @NotEmpty final String artifact,
                @Nonnull @NotEmpty final String issuerId, @Nonnull @NotEmpty final String relyingPartyId, 
                @Nonnull SAMLObject samlMessage);
        
    }
    
}