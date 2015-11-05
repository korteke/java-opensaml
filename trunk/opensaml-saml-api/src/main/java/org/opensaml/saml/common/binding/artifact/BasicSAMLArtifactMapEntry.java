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
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.core.xml.util.XMLObjectSupport.CloneOutputOption;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.artifact.SAMLArtifactMap.SAMLArtifactMapEntry;

/** Basic implementation of {@link SAMLArtifactMapEntry}. */
public class BasicSAMLArtifactMapEntry implements SAMLArtifactMapEntry {
    
    /** SAML artifact being mapped. */
    @Nonnull @NotEmpty private final String artifact;

    /** EntityID of the issuer of the artifact. */
    @Nonnull @NotEmpty private final String issuer;

    /** EntityID of the intended recipient of the artifact. */
    @Nonnull @NotEmpty private final String relyingParty;

    /** SAML message mapped to the artifact. */
    @Nonnull private final SAMLObject message;

    /**
     * Constructor.
     * 
     * @param samlArtifact artifact associated with the message
     * @param issuerId issuer of the artifact
     * @param relyingPartyId intended recipient of the artifact
     * @param samlMessage SAML message mapped to the artifact
     * @throws MarshallingException if an error occurs isolating a message from its parent
     * @throws UnmarshallingException if an error occurs isolating a message from its parent
     */
    public BasicSAMLArtifactMapEntry(@Nonnull @NotEmpty final String samlArtifact,
            @Nonnull @NotEmpty final String issuerId, @Nonnull @NotEmpty final String relyingPartyId, 
            @Nonnull final SAMLObject samlMessage) throws MarshallingException, UnmarshallingException {
        artifact = samlArtifact;
        issuer = issuerId;
        relyingParty = relyingPartyId;
        if (!samlMessage.hasParent()) {
            message = samlMessage;
        } else {
            message = XMLObjectSupport.cloneXMLObject(samlMessage, CloneOutputOption.RootDOMInNewDocument);
        }
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull @NotEmpty public String getArtifact() {
        return artifact;
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull @NotEmpty public String getIssuerId() {
        return issuer;
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull @NotEmpty public String getRelyingPartyId() {
        return relyingParty;
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull public SAMLObject getSamlMessage() {
        return message;
    }
    
}