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

package org.opensaml.saml.saml2.binding.artifact;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.messaging.context.SAMLArtifactContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAML 2, type 0x0004, artifact builder.
 */
public class SAML2ArtifactType0004Builder implements SAML2ArtifactBuilder<SAML2ArtifactType0004> {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(SAML2ArtifactType0004Builder.class);

    /** {@inheritDoc} */
    @Override
    @Nullable public SAML2ArtifactType0004 buildArtifact(@Nonnull @NotEmpty byte[] artifact) {
        return SAML2ArtifactType0004.parseArtifact(artifact);
    }

    /** {@inheritDoc} */
    @Override
    @Nullable public SAML2ArtifactType0004 buildArtifact(@Nonnull final MessageContext<SAMLObject> requestContext) {
        try {
            final String sourceId = getSourceEntityId(requestContext);
            if (sourceId == null) {
                return null;
            }

            final Integer arsIndex = getArsEndpointIndex(requestContext);
            if (arsIndex == null) {
                return null;
            }

            final byte[] endpointIndex = intToByteArray(arsIndex);
            final byte[] trimmedIndex = new byte[2];
            trimmedIndex[0] = endpointIndex[2];
            trimmedIndex[1] = endpointIndex[3];

            final MessageDigest sha1Digester = MessageDigest.getInstance("SHA-1");
            final byte[] source = sha1Digester.digest(sourceId.getBytes());

            final SecureRandom handleGenerator = SecureRandom.getInstance("SHA1PRNG");
            final byte[] assertionHandle = new byte[20];
            handleGenerator.nextBytes(assertionHandle);

            return new SAML2ArtifactType0004(trimmedIndex, source, assertionHandle);
        } catch (final NoSuchAlgorithmException e) {
            log.warn("JVM does not support required cryptography algorithms: SHA-1/SHA1PRNG.", e);
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
    @Nullable protected Integer getArsEndpointIndex(@Nonnull final MessageContext<SAMLObject> requestContext) {
        final SAMLArtifactContext artifactContext = getArtifactContext(requestContext);

        if (artifactContext == null || artifactContext.getSourceArtifactResolutionServiceEndpointIndex() == null) {
            log.warn("No artifact resolution service endpoint index is available");
            return null;
        }

        return artifactContext.getSourceArtifactResolutionServiceEndpointIndex();
    }

    /**
     * Get the local entityId.
     * 
     * @param requestContext the message context
     * 
     * @return the local entityId
     */
    @Nullable protected String getSourceEntityId(@Nonnull final MessageContext<SAMLObject> requestContext) {
        final SAMLArtifactContext artifactContext = getArtifactContext(requestContext);
        if (artifactContext != null) {
            if (artifactContext.getSourceEntityId() != null) {
                return artifactContext.getSourceEntityId(); 
            } else {
                log.warn("SAMLArtifactContext did not contain a source entityID");
            }
        } else {
            log.warn("Message context did not contain a SAMLArtifactContext");
        }
        return null;
    }

    /**
     * Converts an integer into an unsigned 4-byte array.
     * 
     * @param integer integer to convert
     * 
     * @return 4-byte array representing integer
     */
    @Nonnull @NotEmpty private byte[] intToByteArray(final int integer) {
        byte[] intBytes = new byte[4];
        intBytes[0] = (byte) ((integer & 0xff000000) >>> 24);
        intBytes[1] = (byte) ((integer & 0x00ff0000) >>> 16);
        intBytes[2] = (byte) ((integer & 0x0000ff00) >>> 8);
        intBytes[3] = (byte) ((integer & 0x000000ff));

        return intBytes;
    }
    
}