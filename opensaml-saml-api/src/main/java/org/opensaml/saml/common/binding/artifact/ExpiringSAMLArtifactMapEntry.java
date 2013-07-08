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

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;

import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.SAMLObject;

/** Extension of {@link BasicSAMLArtifactMapEntry} that tracks expiration. */
public class ExpiringSAMLArtifactMapEntry extends BasicSAMLArtifactMapEntry {
    
    /** Expiration in milliseconds since the start of the Unix epoch. */
    private long expiration;

    /**
     * Constructor.
     * 
     * @param samlArtifact artifact associated with the message
     * @param issuerId issuer of the artifact
     * @param relyingPartyId intended recipient of the artifact
     * @param samlMessage SAML message mapped to the artifact
     * 
     * @throws MarshallingException if an error occurs isolating a message from its parent
     * @throws UnmarshallingException if an error occurs isolating a message from its parent
     */
    public ExpiringSAMLArtifactMapEntry(@Nonnull @NotEmpty final String samlArtifact,
            @Nonnull @NotEmpty final String issuerId, @Nonnull @NotEmpty final String relyingPartyId, 
            @Nonnull final SAMLObject samlMessage) throws MarshallingException, UnmarshallingException {
        super(samlArtifact, issuerId, relyingPartyId, samlMessage);
    }

    /**
     * Returns the expiration in milliseconds since the start of the Unix epoch.
     * 
     * @return  the expiration
     */
    public long getExpiration() {
        return expiration;
    }

    /**
     * Sets the expiration in milliseconds since the start of the Unix epoch.
     * 
     * @param exp the expiration
     */
    public void setExpiration(long exp) {
        expiration = exp;
    }
    
    /**
     * Returns true iff the entry is valid as of now.
     * 
     * @return true iff the entry is valid as of now
     */
    public boolean isValid() {
        return System.currentTimeMillis() < expiration;
    }

    /**
     * Returns true iff the entry is valid as of a specified time.
     * 
     * @param effectiveTime the time to evaluate validity against
     * @return true iff the entry is valid as of a specified time
     */
    public boolean isValid(long effectiveTime) {
        return effectiveTime < expiration;
    }
}