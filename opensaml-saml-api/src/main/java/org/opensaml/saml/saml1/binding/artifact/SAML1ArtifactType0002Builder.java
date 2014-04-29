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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.messaging.context.SAMLArtifactContext;
import org.opensaml.saml.saml1.core.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAML 1, type 0x0002, artifact builder.
 */
public class SAML1ArtifactType0002Builder implements SAML1ArtifactBuilder<SAML1ArtifactType0002> {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(SAML1ArtifactType0002Builder.class);

    /** {@inheritDoc} */
    @Override
    @Nullable public SAML1ArtifactType0002 buildArtifact(@Nonnull @NotEmpty byte[] artifact) {
        try {
            return SAML1ArtifactType0002.parseArtifact(artifact);
        } catch (final IllegalArgumentException e) {
            log.warn("Error parsing type 2 artifact", e);
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    @Nullable public SAML1ArtifactType0002 buildArtifact(@Nonnull final MessageContext<SAMLObject> requestContext,
            @Nonnull final Assertion assertion) {
        try {
            final String sourceLocation = getArsEndpointUrl(requestContext);
            if (sourceLocation == null) {
                return null;
            }

            final SecureRandom handleGenerator = SecureRandom.getInstance("SHA1PRNG");
            final byte[] assertionHandle = new byte[20];
            handleGenerator.nextBytes(assertionHandle);
            return new SAML1ArtifactType0002(assertionHandle, sourceLocation);
        } catch (final NoSuchAlgorithmException e) {
            log.warn("JVM does not support required cryptography algorithms: SHA1PRNG.", e);
            return null;
        }
    }
    
    /**
     * Get the artifact context.
     * 
     * @param requestContext the current message context
     * @return the SAML artifact context, or null
     */
    @Nullable protected SAMLArtifactContext getArtifactContext(
            @Nonnull final MessageContext<SAMLObject> requestContext) {
        return requestContext.getSubcontext(SAMLArtifactContext.class);
    }
    
    /**
     * Gets the index of the source artifact resolution service.
     * 
     * @param requestContext current request context
     * 
     * @return the index of the attribute resolution service
     */
    @Nullable protected String getArsEndpointUrl(@Nonnull final MessageContext<SAMLObject> requestContext) {
        SAMLArtifactContext artifactContext = getArtifactContext(requestContext);

        if (artifactContext == null || artifactContext.getSourceArtifactResolutionServiceEndpointURL() == null) {
            log.warn("No artifact resolution service endpoint URL is available");
            return null;
        }

        return artifactContext.getSourceArtifactResolutionServiceEndpointURL();
    }

}