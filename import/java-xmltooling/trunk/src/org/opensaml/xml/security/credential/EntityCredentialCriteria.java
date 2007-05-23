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

import org.opensaml.xml.util.DatatypeHelper;

/**
 * An implementation of {@link CredentialCriteria} which specifies criteria pertaining 
 * to the owner and peer entity IDs the resolved credential. 
 * 
 * The owner entity ID is the entity which owns the resolved credential.  The peer entity ID is the 
 * entity which is the remote peer, relative to the resolution process.  The owner ID and peer ID may be the 
 * same, depending on the particular use case for resolving credentials.
 */
public final class EntityCredentialCriteria implements CredentialCriteria {
    
    /** Owner entity ID for which to resolve credential. */
    private String ownerID;
    
    /** Peer entity ID for which to resolve credential. This may be the same as the owner entity.  */
    private String peerID;
    
    /**
    * Constructor.
     *
     * @param owner the entity ID which owns the resolved credential
     * @param peer the entity ID which is the peer relative to the credential resolution process
     */
    public EntityCredentialCriteria(String owner, String peer) {
        setOwnerID(owner);
        setPeerID(peer);
    }

    /**
     * Get the entity ID which will own the resolved credential.
     * 
     * @return the owner entity ID.
     */
    public String getOwnerID() {
        return ownerID;
    }

    /**
     * Set the entity ID which will own the resolved credential.
     * 
     * @param owner The ownerID to set.
     */
    public void setOwnerID(String owner) {
        String trimmed = DatatypeHelper.safeTrimOrNullString(owner);
        if (trimmed == null) {
            throw new IllegalArgumentException("Owner criteria must be supplied");
        }
        ownerID = trimmed;
    }

    /**
     * Get the entity ID which is the remote peer relative to the resolution process.
     * 
     * @return the peer entity ID.
     */
    public String getPeerID() {
        return peerID;
    }

    /**
     * Set the entity ID which is the remote peer relative to the resolution process.
     * 
     * @param peer The peerID to set.
     */
    public void setPeerID(String peer) {
        peerID = peer;
    }

}
