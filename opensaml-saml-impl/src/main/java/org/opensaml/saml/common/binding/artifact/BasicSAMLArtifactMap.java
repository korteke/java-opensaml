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
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.component.AbstractDestructableIdentifiableInitializableComponent;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentValidationException;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.joda.time.DateTime;
import org.opensaml.saml.common.SAMLObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Basic artifact map implementation. */
public class BasicSAMLArtifactMap extends AbstractDestructableIdentifiableInitializableComponent
    implements SAMLArtifactMap {

    /** Class Logger. */
    private final Logger log = LoggerFactory.getLogger(BasicSAMLArtifactMap.class);

    /** Artifact mapping storage. */
    @NonnullAfterInit private Map<String, ExpiringSAMLArtifactMapEntry> artifactStore;

    /** Lifetime of an artifact in milliseconds. */
    private long artifactLifetime;

    /** Factory for SAMLArtifactMapEntry instances. */
    @Nonnull private SAMLArtifactMapEntryFactory entryFactory;

    /** Number of seconds between cleanup checks. Default value: (300) */
    private long cleanupInterval;

    /** Timer used to schedule cleanup tasks. */
    @NonnullAfterInit private Timer cleanupTaskTimer;

    /** Task that cleans up expired records. */
    @NonnullAfterInit private TimerTask cleanupTask;

    /** Constructor. */
    public BasicSAMLArtifactMap() {
        setId(getClass().getName());
        artifactLifetime = 60000L;
        cleanupInterval = 300;
        entryFactory = new ExpiringSAMLArtifactMapEntryFactory();
    }
    
    /** {@inheritDoc} */
    public synchronized void setId(@Nonnull @NotEmpty final String componentId) {
        super.setId(componentId);
    }
    
    /** {@inheritDoc} */
    public void validate() throws ComponentValidationException {

    }

    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        artifactStore = new ConcurrentHashMap<String, ExpiringSAMLArtifactMapEntry>();

        if (cleanupInterval > 0) {
            cleanupTask = new Cleanup();
            cleanupTaskTimer = new Timer();
            cleanupTaskTimer.schedule(cleanupTask, cleanupInterval * 1000, cleanupInterval * 1000);
        }
    }
    
    /** {@inheritDoc} */
    protected void doDestroy() {
        if (cleanupTask != null) {
            cleanupTask.cancel();
            cleanupTask = null;
            cleanupTaskTimer = null;
        }
        super.doDestroy();
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
     * @param factory map entry factory
     */
    public void setEntryFactory(@Nonnull final SAMLArtifactMapEntryFactory factory) {
        entryFactory = Constraint.isNotNull(factory, "SAMLArtifactMapEntryFactory cannot be null");
    }
    
    /** {@inheritDoc} */
    public boolean contains(@Nonnull @NotEmpty final String artifact) throws IOException {
        return artifactStore.containsKey(artifact);
    }

    /** {@inheritDoc} */
    @Nullable public SAMLArtifactMapEntry get(@Nonnull @NotEmpty final String artifact) throws IOException {
        log.debug("Attempting to retrieve entry for artifact: {}", artifact);
        ExpiringSAMLArtifactMapEntry entry = artifactStore.get(artifact);

        if (entry == null) {
            log.debug("No entry found for artifact: {}", artifact);
            return null;
        }

        if (!entry.isValid()) {
            log.debug("Entry for artifact was expired: {}", artifact);
            remove(artifact);
            return null;
        }

        log.debug("Found valid entry for artifact: {}", artifact);
        return entry;
    }

    /** {@inheritDoc} */
    public void put(@Nonnull @NotEmpty final String artifact, @Nonnull @NotEmpty final String relyingPartyId,
            @Nonnull @NotEmpty final String issuerId, @Nonnull final SAMLObject samlMessage)
            throws IOException {

        ExpiringSAMLArtifactMapEntry artifactEntry =
                (ExpiringSAMLArtifactMapEntry) entryFactory.newEntry(artifact, issuerId, relyingPartyId, samlMessage);
        artifactEntry.setExpiration(System.currentTimeMillis() + getArtifactLifetime());

        if (log.isDebugEnabled()) {
            log.debug("Storing new artifact entry '{}' for relying party '{}', expiring at '{}'", new Object[] {
                    artifact, relyingPartyId, new DateTime(artifactEntry.getExpiration()),});
        }

        artifactStore.put(artifact, artifactEntry);
    }

    /** {@inheritDoc} */
    public void remove(@Nonnull @NotEmpty final String artifact) throws IOException {
        log.debug("Removing artifact entry: {}", artifact);
        
        artifactStore.remove(artifact);
    }

    /**
     * A cleanup task that relies on the weakly consistent iterator support in the
     * map implementation.
     */
    protected class Cleanup extends TimerTask {
        
        /** {@inheritDoc} */
        public void run() {
            log.info("Running cleanup task");
            
            final Long now = System.currentTimeMillis();
            
            Iterator<Map.Entry<String, ExpiringSAMLArtifactMapEntry>> i = artifactStore.entrySet().iterator();
            while (i.hasNext()) {
                final Map.Entry<String, ExpiringSAMLArtifactMapEntry> entry = i.next(); 
                if (!entry.getValue().isValid(now)) {
                    i.remove();
                }
            }
        }
    }
    
}