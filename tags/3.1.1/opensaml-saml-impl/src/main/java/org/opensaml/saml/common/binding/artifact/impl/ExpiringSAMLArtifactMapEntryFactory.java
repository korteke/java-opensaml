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

package org.opensaml.saml.common.binding.artifact.impl;


import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;

import org.opensaml.core.xml.XMLRuntimeException;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.artifact.ExpiringSAMLArtifactMapEntry;
import org.opensaml.saml.common.binding.artifact.SAMLArtifactMap.SAMLArtifactMapEntry;
import org.opensaml.saml.common.binding.artifact.SAMLArtifactMap.SAMLArtifactMapEntryFactory;

/**
 * A factory for instances of {@link ExpiringSAMLArtifactMapEntry}.
 */
public class ExpiringSAMLArtifactMapEntryFactory implements SAMLArtifactMapEntryFactory {

    /** {@inheritDoc} */
    @Override
    @Nonnull public SAMLArtifactMapEntry newEntry(@Nonnull @NotEmpty final String artifact,
            @Nonnull @NotEmpty final String issuerId, @Nonnull @NotEmpty final String relyingPartyId,
            @Nonnull SAMLObject samlMessage) {
        
        try {
            return new ExpiringSAMLArtifactMapEntry(artifact, issuerId, relyingPartyId, samlMessage);
        } catch (final MarshallingException | UnmarshallingException e) {
            throw new XMLRuntimeException("Error creating BasicSAMLArtifactMapEntry", e);
        }
    }

}