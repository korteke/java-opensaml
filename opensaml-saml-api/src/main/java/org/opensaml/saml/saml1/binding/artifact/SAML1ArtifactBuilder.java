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

package org.opensaml.saml.saml1.binding.artifact;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml1.core.Assertion;

/**
 * Builder of typed SAML 1 artifacts.
 * 
 * Builders must be thread safe and reusable.
 * 
 * @param <ArtifactType> type of artifact built by this builder
 */
public interface SAML1ArtifactBuilder<ArtifactType extends AbstractSAML1Artifact> {

    /**
     * Builds an artifact, for the given assertion, destined for the outbound message recipient.
     * 
     * @param requestContext request context
     * @param assertion assertion to build artifact for
     * 
     * @return constructed artifact
     */
    @Nullable ArtifactType buildArtifact(@Nonnull final MessageContext<SAMLObject> requestContext,
            @Nonnull final Assertion assertion);

    /**
     * Builds a populated artifact given the artifact's byte-array representation.
     * 
     * @param artifact the byte representation of the artifact
     * 
     * @return populated artifact
     */
    @Nullable ArtifactType buildArtifact(@Nonnull @NotEmpty byte[] artifact);

}