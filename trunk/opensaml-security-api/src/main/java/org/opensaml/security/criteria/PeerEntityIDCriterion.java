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

package org.opensaml.security.criteria;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.resolver.Criterion;

/**
 * An implementation of {@link Criterion} which specifies criteria pertaining 
 * to peer entity IDs.  This is typically used only in conjunction with a
 * {@link org.opensaml.core.criterion.EntityIdCriterion}, where the peer is interpreted to be relative
 * to that primary entity ID. In this sense it serves to scope the primary entity ID.
 * 
 * Note that the peer entity ID may be either local or remote,
 * depending on whether the associated primary entity ID is remote or local.
 */
public final class PeerEntityIDCriterion implements Criterion {
    
    /** Peer entity ID criteria. */
    private String peerID;
    
    /**
    * Constructor.
     *
     * @param peer the entity ID which is the peer relative to a primary entity ID
     */
    public PeerEntityIDCriterion(@Nonnull final String peer) {
        setPeerID(peer);
    }

    /**
     * Get the entity ID which is the peer relative to a primary entity ID.
     * 
     * @return the peer entity ID.
     */
    @Nonnull public String getPeerID() {
        return peerID;
    }

    /**
     * Set the entity ID which is the peer relative to a primary entity ID.
     * 
     * @param peer The peerID to set.
     */
    public void setPeerID(@Nonnull final String peer) {
        String trimmed = StringSupport.trimOrNull(peer);
        Constraint.isNotNull(trimmed, "Peer entity ID criteria cannot be null");
        
        peerID = trimmed;
    }
    
    /** {@inheritDoc} */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PeerEntityIDCriterion [peerID=");
        builder.append(peerID);
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return peerID.hashCode();
    }

    /** {@inheritDoc} */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof PeerEntityIDCriterion) {
            return peerID.equals(((PeerEntityIDCriterion) obj).peerID);
        }

        return false;
    }
    
}