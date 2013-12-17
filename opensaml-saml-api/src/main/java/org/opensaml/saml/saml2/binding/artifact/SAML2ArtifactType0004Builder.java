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

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.messaging.context.SamlArtifactContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAML 2, type 0x0004, artifact builder.
 */
public class SAML2ArtifactType0004Builder implements SAML2ArtifactBuilder<SAML2ArtifactType0004> {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(SAML2ArtifactType0004Builder.class);

    /** {@inheritDoc} */
    public SAML2ArtifactType0004 buildArtifact(byte[] artifact) {
        return SAML2ArtifactType0004.parseArtifact(artifact);
    }

    /** {@inheritDoc} */
    public SAML2ArtifactType0004 buildArtifact(MessageContext<SAMLObject> requestContext) {
        try {
            Integer arsIndex = getArsEndpointIndex(requestContext);
            if (arsIndex == null) {
                return null;
            }

            byte[] endpointIndex = intToByteArray(arsIndex);
            byte[] trimmedIndex = new byte[2];
            trimmedIndex[0] = endpointIndex[2];
            trimmedIndex[1] = endpointIndex[3];

            MessageDigest sha1Digester = MessageDigest.getInstance("SHA-1");
            byte[] source = sha1Digester.digest(getSourceEntityId(requestContext).getBytes());

            SecureRandom handleGenerator = SecureRandom.getInstance("SHA1PRNG");
            byte[] assertionHandle;
            assertionHandle = new byte[20];
            handleGenerator.nextBytes(assertionHandle);

            return new SAML2ArtifactType0004(trimmedIndex, source, assertionHandle);
        } catch (NoSuchAlgorithmException e) {
            log.error("JVM does not support required cryptography algorithms: SHA-1/SHA1PRNG.", e);
            throw new InternalError("JVM does not support required cryptography algorithms: SHA-1/SHA1PRNG.");
        }
    }
    
    /**
     * Get the artifact context.
     * 
     * @param requestContext the current message context
     * @return the SAML artifact context, or null
     */
    protected SamlArtifactContext getArtifactContext(MessageContext<SAMLObject> requestContext) {
        return requestContext.getSubcontext(SamlArtifactContext.class, false);
    }
    
    /**
     * Gets the index of the source artifact resolution service.
     * 
     * @param requestContext current request context
     * 
     * @return the index of the attribute resolution service
     */
    protected Integer getArsEndpointIndex(MessageContext<SAMLObject> requestContext) {
        SamlArtifactContext artifactContext = getArtifactContext(requestContext);

        if (artifactContext == null || artifactContext.getSourceArtifactResolutionServiceEndpointIndex() == null) {
            log.error("No artifact resolution service endpoint index is available");
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
    protected String getSourceEntityId(MessageContext<SAMLObject> requestContext) {
        SamlArtifactContext artifactContext = getArtifactContext(requestContext);
        Constraint.isNotNull(artifactContext, "Message context did not contain a SamlArtifactContext");
        Constraint.isNotNull(artifactContext.getSourceEntityId(), 
                "SamlArtifactContext did not contain a source entityID");
        return artifactContext.getSourceEntityId();
    }

    /**
     * Converts an integer into an unsigned 4-byte array.
     * 
     * @param integer integer to convert
     * 
     * @return 4-byte array representing integer
     */
    private byte[] intToByteArray(int integer) {
        byte[] intBytes = new byte[4];
        intBytes[0] = (byte) ((integer & 0xff000000) >>> 24);
        intBytes[1] = (byte) ((integer & 0x00ff0000) >>> 16);
        intBytes[2] = (byte) ((integer & 0x0000ff00) >>> 8);
        intBytes[3] = (byte) ((integer & 0x000000ff));

        return intBytes;
    }
}