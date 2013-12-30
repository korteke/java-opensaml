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
    private final Logger log = LoggerFactory.getLogger(SAML1ArtifactType0002Builder.class);

    /** {@inheritDoc} */
    public SAML1ArtifactType0002 buildArtifact(byte[] artifact) {
        return SAML1ArtifactType0002.parseArtifact(artifact);
    }

    /** {@inheritDoc} */
    public SAML1ArtifactType0002 buildArtifact(MessageContext<SAMLObject> requestContext, Assertion assertion) {
        try {
            String sourceLocation = getArsEndpointUrl(requestContext);
            if (sourceLocation == null) {
                return null;
            }

            SecureRandom handleGenerator = SecureRandom.getInstance("SHA1PRNG");
            byte[] assertionHandle = new byte[20];
            handleGenerator.nextBytes(assertionHandle);
            return new SAML1ArtifactType0002(assertionHandle, sourceLocation);
        } catch (NoSuchAlgorithmException e) {
            log.error("JVM does not support required cryptography algorithms: SHA1PRNG.", e);
            throw new InternalError("JVM does not support required cryptography algorithms: SHA1PRNG.");
        }
    }
    
    /**
     * Get the artifact context.
     * 
     * @param requestContext the current message context
     * @return the SAML artifact context, or null
     */
    protected SAMLArtifactContext getArtifactContext(MessageContext<SAMLObject> requestContext) {
        return requestContext.getSubcontext(SAMLArtifactContext.class, false);
    }
    
    /**
     * Gets the index of the source artifact resolution service.
     * 
     * @param requestContext current request context
     * 
     * @return the index of the attribute resolution service
     */
    protected String getArsEndpointUrl(MessageContext<SAMLObject> requestContext) {
        SAMLArtifactContext artifactContext = getArtifactContext(requestContext);

        if (artifactContext == null || artifactContext.getSourceArtifactResolutionServiceEndpointURL() == null) {
            log.error("No artifact resolution service endpoint URL is available");
            return null;
        }

        return artifactContext.getSourceArtifactResolutionServiceEndpointURL();
    }

}