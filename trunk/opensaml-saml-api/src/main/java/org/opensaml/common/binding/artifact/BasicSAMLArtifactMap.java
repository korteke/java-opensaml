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

package org.opensaml.common.binding.artifact;

import java.util.Map;

import org.opensaml.common.SAMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Basic artifact map implementation. */
public class BasicSAMLArtifactMap implements SAMLArtifactMap {

    /** Class Logger. */
    private final Logger log = LoggerFactory.getLogger(BasicSAMLArtifactMap.class);

    /** Artifact mapping storage. */
    private Map<String, SAMLArtifactMapEntry> artifactStore;

    /** Lifetime of an artifact in milliseconds. */
    private long artifactLifetime;

    /** Factory for SAMLArtifactMapEntry instances. */
    private SAMLArtifactMapEntryFactory entryFactory;

    /**
     * Constructor.
     * 
     * @param storage artifact mapping storage
     * @param lifetime lifetime of an artifact in milliseconds
     */
    public BasicSAMLArtifactMap(Map<String, SAMLArtifactMapEntry> storage, long lifetime) {
        this(new BasicSAMLArtifactMapEntryFactory(), storage, lifetime);
    }

    /**
     * Constructor.
     * 
     * @param factory the SAML artifact map entry factory to use
     * @param storage artifact mapping storage
     * @param lifetime lifetime of an artifact in milliseconds
     */
    public BasicSAMLArtifactMap(SAMLArtifactMapEntryFactory factory, Map<String, SAMLArtifactMapEntry> storage,
            long lifetime) {
        entryFactory = factory;
        artifactStore = storage;
        artifactLifetime = lifetime;
    }

    /** {@inheritDoc} */
    public boolean contains(String artifact) {
        return artifactStore.containsKey(artifact);
    }

    /** {@inheritDoc} */
    public SAMLArtifactMapEntry get(String artifact) {
        log.debug("Attempting to retrieve entry for artifact: {}", artifact);
        SAMLArtifactMapEntry entry = artifactStore.get(artifact);

        if (entry == null) {
            log.debug("No entry found for artifact: {}", artifact);
            return null;
        }

        if (entry.isExpired()) {
            log.debug("Entry for artifact was expired: {}", artifact);
            remove(artifact);
            return null;
        }

        log.debug("Found valid entry for artifact: {}", artifact);
        return entry;
    }

    /** {@inheritDoc} */
    public void put(String artifact, String relyingPartyId, String issuerId, SAMLObject samlMessage)
            throws MarshallingException {

        SAMLArtifactMapEntry artifactEntry =
                entryFactory.newEntry(artifact, issuerId, relyingPartyId, samlMessage, artifactLifetime);

        if (log.isDebugEnabled()) {
            log.debug("Storing new artifact entry '{}' for relying party '{}', expiring at '{}'", new Object[] {
                    artifact, relyingPartyId, artifactEntry.getExpirationTime()});
        }

        artifactStore.put(artifact, artifactEntry);
    }

    /** {@inheritDoc} */
    public void remove(String artifact) {
        log.debug("Removing artifact entry: {}", artifact);
        artifactStore.remove(artifact);
    }

}