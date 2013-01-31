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

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.BasicEndpointSelector;
import org.opensaml.saml.common.binding.SAMLMessageContext;
import org.opensaml.saml.common.messaging.context.SamlLocalEntityContext;
import org.opensaml.saml.common.messaging.context.SamlMetadataContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.NameIdentifier;
import org.opensaml.saml.saml1.core.RequestAbstractType;
import org.opensaml.saml.saml1.core.Response;
import org.opensaml.saml.saml2.metadata.ArtifactResolutionService;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml.saml2.metadata.provider.MetadataProvider;
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
            String sourceLocation = getSourceLocation(requestContext);
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
     * Gets the source location used to for the artifacts created by this encoder.
     * 
     * @param requestContext current request context
     * 
     * @return source location used to for the artifacts created by this encoder
     */
    protected String getSourceLocation(MessageContext<SAMLObject> requestContext) {
        BasicEndpointSelector selector = new BasicEndpointSelector();
        selector.setEndpointType(ArtifactResolutionService.DEFAULT_ELEMENT_NAME);
        selector.getSupportedIssuerBindings().add(SAMLConstants.SAML1_SOAP11_BINDING_URI);
        selector.setMetadataProvider(getMetadataProvider(requestContext));
        selector.setEntityMetadata(getLocalEntityMetadata(requestContext));
        selector.setEntityRoleMetadata(getLocalEntityRoleMetadata(requestContext));

        Endpoint acsEndpoint = selector.selectEndpoint();

        if (acsEndpoint == null) {
            log.error("Unable to select source location for artifact.  No artifact resolution service defined for issuer.");
            return null;
        }

        return acsEndpoint.getLocation();
    }

    /**
     * Get the local entity role metadata.
     * 
     * @param requestContext the message context
     * @return local entity role metadata
     */
    private RoleDescriptor getLocalEntityRoleMetadata(MessageContext<SAMLObject> requestContext) {
        SamlLocalEntityContext localContext = requestContext.getSubcontext(SamlLocalEntityContext.class, false);
        Constraint.isNotNull(localContext, "Message context did not contain a LocalEntityContext");
        SamlMetadataContext mdContext = localContext.getSubcontext(SamlMetadataContext.class, false);
        Constraint.isNotNull(mdContext, "LocalEntityContext did not contain a SamlMetadataContext");
        return mdContext.getRoleDescriptor();
        
    }

    /**
     * Get the local entity metadata.
     * 
     * @param requestContext the message context
     * @return the local entity metadata
     */
    private EntityDescriptor getLocalEntityMetadata(MessageContext<SAMLObject> requestContext) {
        SamlLocalEntityContext localContext = requestContext.getSubcontext(SamlLocalEntityContext.class, false);
        Constraint.isNotNull(localContext, "Message context did not contain a LocalEntityContext");
        SamlMetadataContext mdContext = localContext.getSubcontext(SamlMetadataContext.class, false);
        Constraint.isNotNull(mdContext, "LocalEntityContext did not contain a SamlMetadataContext");
        return mdContext.getEntityDescriptor();
    }

    /**
     * @param requestContext
     * @return
     */
    private MetadataProvider getMetadataProvider(MessageContext<SAMLObject> requestContext) {
        // TODO Auto-generated method stub
        return null;
    }
}