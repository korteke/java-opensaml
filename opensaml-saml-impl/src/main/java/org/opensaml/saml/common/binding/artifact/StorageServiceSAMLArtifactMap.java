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

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.component.AbstractDestructableIdentifiableInitializableComponent;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentValidationException;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.storage.StorageRecord;
import org.opensaml.storage.StorageSerializer;
import org.opensaml.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Basic artifact map implementation. */
public class StorageServiceSAMLArtifactMap extends AbstractDestructableIdentifiableInitializableComponent
    implements SAMLArtifactMap {

    /** Storage context label. */
    @Nonnull @NotEmpty public static final String STORAGE_CONTEXT = StorageServiceSAMLArtifactMap.class.getName();
        
    /** Class Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(StorageServiceSAMLArtifactMap.class);

    /** Artifact mapping storage. */
    @NonnullAfterInit private StorageService artifactStore;
    
    /** Maximum size of artifacts we can handle. */
    private int artifactStoreKeySize;

    /** Lifetime of an artifact in milliseconds. */
    private long artifactLifetime;

    /** Factory for SAMLArtifactMapEntry instances. */
    @Nonnull private SAMLArtifactMapEntryFactory entryFactory;

    /** Constructor. */
    public StorageServiceSAMLArtifactMap() {
        setId(getClass().getName());
        entryFactory = new StorageServiceSAMLArtifactMapEntryFactory();
        artifactLifetime = 60000L;
    }

    /** {@inheritDoc} */
    public synchronized void setId(@Nonnull @NotEmpty final String componentId) {
        super.setId(componentId);
    }
    
    /** {@inheritDoc} */
    public void validate() throws ComponentValidationException {
        artifactStore.validate();
    }
    
    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        // We can't shorten the artifacts as lookup keys at the moment because
        // the key is used to recreate the original artifact value.
        artifactStoreKeySize = getStorageService().getCapabilities().getKeySize();
    }
    
    /**
     * Get the artifact store.
     * 
     * @return the artifact store
     */
    @NonnullAfterInit public StorageService getStorageService() {
        return artifactStore;
    }
    
    /**
     * Get the artifact entry lifetime in milliseconds.
     * 
     * @return the artifact entry lifetime in milliseconds
     */
    public long getArtifactLifetime() {
        return artifactLifetime;
    }
    
    /**
     * Get the map entry factory.
     * 
     * @return the map entry factory
     */
    @Nonnull public SAMLArtifactMapEntryFactory getEntryFactory() {
        return entryFactory;
    }
    
    /**
     * Set the artifact store.
     * 
     * @param store the artifact store
     */
    public void setStorageService(@Nonnull final StorageService store) {
        artifactStore = Constraint.isNotNull(store, "StorageService cannot be null");
    }
    
    /**
     * Set the artifact entry lifetime in milliseconds.
     * 
     * @param lifetime artifact entry lifetime in milliseconds
     */
    public void setArtifactLifetime(long lifetime) {
        artifactLifetime = lifetime;
    }
    
    /**
     * Set the map entry factory.
     * 
     * <p>In addition to implementing the {@link SAMLArtifactMapEntryFactory} interface, the
     * injected object must support the {@link StorageSerializer} interface
     * to enable entries to be stored via the injected {@link StorageService} instance.</p> 
     * 
     * @param factory map entry factory
     */
    public void setEntryFactory(@Nonnull final SAMLArtifactMapEntryFactory factory) {
        Constraint.isTrue(factory != null && factory instanceof StorageSerializer<?>,
                "SAMLArtifactMapEntryFactory cannot be null and must support the StorageSerializer interface");
        entryFactory = factory;
    }
    
    /** {@inheritDoc} */
    public boolean contains(@Nonnull @NotEmpty final String artifact) throws IOException {
        if (artifact.length() > artifactStoreKeySize) {
            throw new IOException("Length of artifact (" + artifact.length() + ") exceeds storage capabilities");
        }
        return getStorageService().read(STORAGE_CONTEXT, artifact) != null;
    }

    /** {@inheritDoc} */
    @Nullable public SAMLArtifactMapEntry get(@Nonnull @NotEmpty final String artifact) throws IOException {
        log.debug("Attempting to retrieve entry for artifact: {}", artifact);
        
        if (artifact.length() > artifactStoreKeySize) {
            throw new IOException("Length of artifact (" + artifact.length() + ") exceeds storage capabilities");
        }
        
        StorageRecord record = getStorageService().read(STORAGE_CONTEXT, artifact);
        
        if (record == null) {
            log.debug("No unexpired entry found for artifact: {}", artifact);
            return null;
        }

        log.debug("Found valid entry for artifact: {}", artifact);
        return (SAMLArtifactMapEntry) record.getValue((StorageSerializer) getEntryFactory(), STORAGE_CONTEXT, artifact);
    }

    /** {@inheritDoc} */
    public void put(@Nonnull @NotEmpty final String artifact, @Nonnull @NotEmpty final String relyingPartyId,
            @Nonnull @NotEmpty final String issuerId, @Nonnull final SAMLObject samlMessage) throws IOException {

        if (artifact.length() > artifactStoreKeySize) {
            throw new IOException("Length of artifact (" + artifact.length() + ") exceeds storage capabilities");
        }

        SAMLArtifactMapEntry artifactEntry =
                getEntryFactory().newEntry(artifact, issuerId, relyingPartyId, samlMessage);

        if (log.isDebugEnabled()) {
            log.debug("Storing new artifact entry '{}' for relying party '{}', expiring after {} seconds",
                    new Object[] {artifact, relyingPartyId, getArtifactLifetime() / 1000});
        }

        boolean success = getStorageService().create(STORAGE_CONTEXT, artifact, artifactEntry,
                (StorageSerializer) getEntryFactory(), System.currentTimeMillis() + getArtifactLifetime());
        if (!success) {
            throw new IOException("A duplicate artifact was generated");
        }
    }

    /** {@inheritDoc} */
    public void remove(@Nonnull @NotEmpty final String artifact) throws IOException {
        log.debug("Removing artifact entry: {}", artifact);
        
        if (artifact.length() > artifactStoreKeySize) {
            throw new IOException("Length of artifact (" + artifact.length() + ") exceeds storage capabilities");
        }

        getStorageService().delete(STORAGE_CONTEXT, artifact);
    }

}