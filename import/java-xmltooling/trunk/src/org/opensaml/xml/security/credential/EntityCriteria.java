/*
 * Copyright [2007] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xml.security.credential;

import org.opensaml.xml.security.x509.PKIXCriteria;
import org.opensaml.xml.util.DatatypeHelper;

/**
 * An implementation of {@link Criteria} which specifies criteria pertaining 
 * to primary and remote peer entity IDs.
 * 
 * When used as a {@link CredentialCriteria}, the entity ID is the entity which owns 
 * the resolved credential.  The peer entity ID is the entity which is the remote peer, 
 * relative to the resolution process.  The entity ID and peer ID may be the 
 * same, depending on the particular use case for resolving credentials.
 */
public final class EntityCriteria implements CredentialCriteria, PKIXCriteria {
    
    /** Primary entity ID criteria. */
    private String entityID;
    
    /** Peer entity ID criteria. */
    private String peerID;
    
    /**
    * Constructor.
     *
     * @param entity the primary entity ID represented by the criteria
     * @param peer the entity ID which is the remote peer relative to the primary entity ID
     */
    public EntityCriteria(String entity, String peer) {
        setEntityID(entity);
        setPeerID(peer);
    }

    /**
     * Get the primary entity ID represented by the criteria.
     * 
     * @return the primary entity ID.
     */
    public String getEntityID() {
        return entityID;
    }

    /**
     * Set the primary entity ID represented by the criteria.
     * 
     * @param entity The entityID to set.
     */
    public void setEntityID(String entity) {
        String trimmed = DatatypeHelper.safeTrimOrNullString(entity);
        if (trimmed == null) {
            throw new IllegalArgumentException("Primary entity ID criteria must be supplied");
        }
        entityID = trimmed;
    }

    /**
     * Get the entity ID which is the remote peer relative to the primary entity ID.
     * 
     * @return the peer entity ID.
     */
    public String getPeerID() {
        return peerID;
    }

    /**
     * Set the entity ID which is the remote peer relative to the primary entity ID.
     * 
     * @param peer The peerID to set.
     */
    public void setPeerID(String peer) {
        peerID = peer;
    }

}
