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

package org.opensaml.saml.saml1.binding.encoding.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletResponse;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.collection.Pair;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.net.UrlBuilder;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.binding.artifact.SAMLArtifactMap;
import org.opensaml.saml.common.messaging.context.SAMLArtifactContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLSelfEntityContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.config.SAMLConfigurationSupport;
import org.opensaml.saml.saml1.binding.artifact.AbstractSAML1Artifact;
import org.opensaml.saml.saml1.binding.artifact.SAML1ArtifactBuilder;
import org.opensaml.saml.saml1.binding.artifact.SAML1ArtifactType0001;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAML 1.X HTTP Artifact message encoder.
 */
public class HTTPArtifactEncoder extends BaseSAML1MessageEncoder {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(HTTPArtifactEncoder.class);

    /** SAML artifact map used to store created artifacts for later retrival. */
    @NonnullAfterInit private SAMLArtifactMap artifactMap;

    /** Default artifact type to use when encoding messages. */
    @Nonnull @NotEmpty private byte[] defaultArtifactType;

    /** Constructor. */
    public HTTPArtifactEncoder() {
        defaultArtifactType = SAML1ArtifactType0001.TYPE_CODE;
    }

    /** {@inheritDoc} */
    @Override
    public String getBindingURI() {
        return SAMLConstants.SAML1_ARTIFACT_BINDING_URI;
    }
    
    /**
     * Get the SAML artifact map to use.
     * 
     * @return the artifactMap.
     */
    @NonnullAfterInit public SAMLArtifactMap getArtifactMap() {
        return artifactMap;
    }

    /**
     * Set the SAML artifact map to use.
     * 
     * @param newArtifactMap the new artifactMap 
     */
    public void setArtifactMap(@Nonnull final SAMLArtifactMap newArtifactMap) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        
        artifactMap = Constraint.isNotNull(newArtifactMap, "SAMLArtifactMap cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (artifactMap == null) {
            throw new ComponentInitializationException("SAMLArtifactMap cannot be null");
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void doDestroy() {
        artifactMap = null;
        
        super.doDestroy();
    }

    // Checkstyle: CyclomaticComplexity|MethodLength OFF
    /** {@inheritDoc} */
    @Override
    protected void doEncode() throws MessageEncodingException {
        final MessageContext<SAMLObject> messageContext = getMessageContext();

        final String requester = getInboundMessageIssuer(messageContext);
        final String issuer = getOutboundMessageIssuer(messageContext);
        if (requester == null || issuer == null) {
            throw new MessageEncodingException("Unable to obtain issuer or relying party for message encoding");
        }
        
        final String endpointUrl = getEndpointURL(messageContext).toString();
        
        final UrlBuilder urlBuilder;
        try {
            urlBuilder = new UrlBuilder(endpointUrl);
        } catch (MalformedURLException e) {
            throw new MessageEncodingException("Endpoint URL " + endpointUrl + " is not a valid URL", e);
        }
        
        final List<Pair<String, String>> queryParams = urlBuilder.getQueryParams();
        queryParams.clear();

        final String relayState = SAMLBindingSupport.getRelayState(messageContext);
        if (SAMLBindingSupport.checkRelayState(relayState)) {
            queryParams.add(new Pair<String, String>("TARGET", relayState));
        }

        final SAML1ArtifactBuilder artifactBuilder;
        final byte[] artifactType = getSAMLArtifactType(messageContext);
        if (artifactType != null) {
            artifactBuilder = SAMLConfigurationSupport.getSAML1ArtifactBuilderFactory()
                    .getArtifactBuilder(artifactType);
        } else {
            artifactBuilder = SAMLConfigurationSupport.getSAML1ArtifactBuilderFactory()
                    .getArtifactBuilder(defaultArtifactType);
            storeSAMLArtifactType(messageContext, defaultArtifactType);
        }

        final SAMLObject outboundMessage = messageContext.getMessage();
        if (!(outboundMessage instanceof Response)) {
            throw new MessageEncodingException("Outbound message was not a SAML 1 Response");
        }
        final Response samlResponse = (Response) outboundMessage;
        for (Assertion assertion : samlResponse.getAssertions()) {
            final AbstractSAML1Artifact artifact = artifactBuilder.buildArtifact(messageContext, assertion);
            if (artifact == null) {
                log.error("Unable to build artifact for message to relying party {}", requester);
                throw new MessageEncodingException("Unable to builder artifact for message to relying party");
            }

            try {
                artifactMap.put(artifact.base64Encode(), requester, issuer, assertion);
            } catch (final IOException e) {
                log.error("Unable to store assertion mapping for artifact", e);
                throw new MessageEncodingException("Unable to store assertion mapping for artifact", e);
            }
            final String artifactString = artifact.base64Encode();
            queryParams.add(new Pair<String, String>("SAMLart", artifactString));
        }

        final String encodedEndpoint = urlBuilder.buildURL();
        log.debug("Sending redirect to URL {} for relying party {}", encodedEndpoint, requester);
        
        final HttpServletResponse response = getHttpServletResponse();
        if (response == null) {
            throw new MessageEncodingException("HttpServletResponse was null");
        }
        
        try {
            response.sendRedirect(encodedEndpoint);
        } catch (IOException e) {
            throw new MessageEncodingException("Problem sending HTTP redirect", e);
        }
    }
// Checkstyle: CyclomaticComplexity|MethodLength ON
    
    /**
     * Get the outbound message issuer.
     * 
     * @param messageContext  the message context
     * @return the outbound message issuer
     */
    @Nullable private String getOutboundMessageIssuer(@Nonnull final MessageContext<SAMLObject> messageContext) {

        final SAMLSelfEntityContext selfCtx = messageContext.getSubcontext(SAMLSelfEntityContext.class);
        if (selfCtx == null) {
            return null;
        }
        
        return selfCtx.getEntityId();
    }

    /**
     * Get the requester.
     * 
     * @param messageContext the message context
     * @return the requester
     */
    @Nullable private String getInboundMessageIssuer(@Nonnull final MessageContext<SAMLObject> messageContext) {
        final SAMLPeerEntityContext peerCtx = messageContext.getSubcontext(SAMLPeerEntityContext.class);
        if (peerCtx == null) {
            return null;
        }
        
        return peerCtx.getEntityId();
    }

    /**
     * Store the SAML artifact type in the message context.
     * 
     * @param messageContext  the message context
     * 
     * @param artifactType the artifact type to store
     */
    private void storeSAMLArtifactType(@Nonnull final MessageContext<SAMLObject> messageContext,
            @Nonnull @NotEmpty final byte[] artifactType) {
        messageContext.getSubcontext(SAMLArtifactContext.class, true).setArtifactType(artifactType);
    }

    /**
     * Get the SAML artifact type from the message context.
     * 
     * @param messageContext the message context
     * 
     * @return the artifact type
     */
    @Nullable private byte[] getSAMLArtifactType(@Nonnull final MessageContext<SAMLObject> messageContext) {
        return messageContext.getSubcontext(SAMLArtifactContext.class, true).getArtifactType();
    }
    
}