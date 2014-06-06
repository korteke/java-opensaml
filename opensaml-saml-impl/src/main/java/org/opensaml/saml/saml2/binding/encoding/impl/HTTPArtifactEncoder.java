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

package org.opensaml.saml.saml2.binding.encoding.impl;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletResponse;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.codec.HTMLEncoder;
import net.shibboleth.utilities.java.support.collection.Pair;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.net.URLBuilder;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.binding.artifact.AbstractSAMLArtifact;
import org.opensaml.saml.common.binding.artifact.SAMLArtifactMap;
import org.opensaml.saml.common.messaging.context.SAMLArtifactContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLSelfEntityContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.config.SAMLConfigurationSupport;
import org.opensaml.saml.saml2.binding.artifact.AbstractSAML2Artifact;
import org.opensaml.saml.saml2.binding.artifact.SAML2ArtifactBuilder;
import org.opensaml.saml.saml2.binding.artifact.SAML2ArtifactType0004;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAML 2 Artifact Binding encoder, support both HTTP GET and POST.
 */
public class HTTPArtifactEncoder extends BaseSAML2MessageEncoder {
    
    /** Default template ID. */
    @Nonnull @NotEmpty public static final String DEFAULT_TEMPLATE_ID = "/templates/saml2-post-artifact-binding.vm";

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(HTTPArtifactEncoder.class);

    /** Whether the POST encoding should be used, instead of GET. */
    private boolean postEncoding;

    /** Velocity engine used to evaluate the template when performing POST encoding. */
    @Nullable private VelocityEngine velocityEngine;

    /** ID of the velocity template used when performing POST encoding. */
    @Nonnull @NotEmpty private String velocityTemplateId;

    /** SAML artifact map used to store created artifacts for later retrieval. */
    @NonnullAfterInit private SAMLArtifactMap artifactMap;

    /** Default artifact type to use when encoding messages. */
    @Nonnull @NotEmpty private byte[] defaultArtifactType;

    /** Constructor. */
    public HTTPArtifactEncoder() {
        defaultArtifactType = SAML2ArtifactType0004.TYPE_CODE;
        setVelocityTemplateId(DEFAULT_TEMPLATE_ID);
    }

    /** {@inheritDoc} */
    @Override
    public String getBindingURI() {
        return SAMLConstants.SAML2_ARTIFACT_BINDING_URI;
    }
    
    /**
     * Get whether the encoder will encode the artifact via POST encoding.
     * 
     * @return true if POST encoding will be used, false if GET encoding will be used
     */
    public boolean isPostEncoding() {
        return postEncoding;
    }

    /**
     * Set whether the encoder will encode the artifact via POST encoding.
     * 
     * @param post true if POST encoding will be used, false if GET encoding will be used
     */
    public void setPostEncoding(boolean post) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        postEncoding = post;
    }
    
    /**
     * Get the VelocityEngine instance.
     * 
     * @return return the VelocityEngine instance
     */
    @Nullable public VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }

    /**
     * Set the VelocityEngine instance.
     * 
     * @param newVelocityEngine the new VelocityEngine instane
     */
    public void setVelocityEngine(@Nullable final VelocityEngine newVelocityEngine) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        
        velocityEngine = newVelocityEngine;
    }
    
    /**
     * Get the Velocity template id.
     * 
     * <p>Defaults to {@link #DEFAULT_TEMPLATE_ID}.</p>
     * 
     * @return return the Velocity template id
     */
    @Nonnull @NotEmpty public String getVelocityTemplateId() {
        return velocityTemplateId;
    }

    /**
     * Set the Velocity template id.
     * 
     * <p>Defaults to {@link #DEFAULT_TEMPLATE_ID}.</p>
     * 
     * @param newVelocityTemplateId the new Velocity template id
     */
    public void setVelocityTemplateId(@Nonnull @NotEmpty final String newVelocityTemplateId) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        
        velocityTemplateId = Constraint.isNotNull(StringSupport.trimOrNull(newVelocityTemplateId),
                "Velocity template ID cannot be null or empty");
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
        if (isPostEncoding()) {
            if (velocityEngine == null) {
                throw new ComponentInitializationException("VelocityEngine cannot be null when POST is used");
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void doDestroy() {
        velocityEngine = null;
        velocityTemplateId = null;
        artifactMap = null;
        
        super.doDestroy();
    }

    /** {@inheritDoc} */
    @Override
    protected void doEncode() throws MessageEncodingException {
        final HttpServletResponse response = getHttpServletResponse();
        if (response == null) {
            throw new MessageEncodingException("HttpServletResponse was null");
        }
        response.setCharacterEncoding("UTF-8");

        if (postEncoding) {
            postEncode();
        } else {
            getEncode();
        }
    }

    /**
     * Performs HTTP POST based encoding.
     * 
     * 
     * @throws MessageEncodingException thrown if there is a problem POST encoding the artifact
     */
    protected void postEncode() throws MessageEncodingException {
        log.debug("Performing HTTP POST SAML 2 artifact encoding");
        
        final MessageContext<SAMLObject> messageContext = getMessageContext();

        log.debug("Creating velocity context");
        final VelocityContext context = new VelocityContext();
        final String endpointURL = getEndpointURL(messageContext).toString();
        final String encodedEndpointURL = HTMLEncoder.encodeForHTMLAttribute(endpointURL);
        log.debug("Setting action parameter to: '{}', encoded as '{}'", endpointURL, encodedEndpointURL);
        context.put("action", encodedEndpointURL);
        context.put("SAMLArt", buildArtifact(messageContext).base64Encode());
        context.put("binding", getBindingURI());

        final String relayState = SAMLBindingSupport.getRelayState(messageContext);
        if (SAMLBindingSupport.checkRelayState(relayState)) {
            final String encodedRelayState = HTMLEncoder.encodeForHTMLAttribute(relayState);
            log.debug("Setting RelayState parameter to: '{}', encoded as '{}'", relayState, encodedRelayState);
            context.put("RelayState", encodedRelayState);
        }

        try {
            log.debug("Invoking velocity template");
            final HttpServletResponse response = getHttpServletResponse();
            final OutputStreamWriter outWriter = new OutputStreamWriter(response.getOutputStream());
            velocityEngine.mergeTemplate(velocityTemplateId, "UTF-8", context, outWriter);
        } catch (final Exception e) {
            log.error("Error invoking velocity template to create POST form", e);
            throw new MessageEncodingException("Error creating output document", e);
        }
    }

    /**
     * Performs HTTP GET based encoding.
     * 
     * @throws MessageEncodingException thrown if there is a problem GET encoding the artifact
     */
    protected void getEncode() throws MessageEncodingException {
        log.debug("Performing HTTP GET SAML 2 artifact encoding");

        final MessageContext<SAMLObject> messageContext = getMessageContext();
        
        final String endpointUrl = getEndpointURL(messageContext).toString();
        
        final URLBuilder urlBuilder;
        try {
            urlBuilder = new URLBuilder(endpointUrl);
        } catch (final MalformedURLException e) {
            throw new MessageEncodingException("Endpoint URL " + endpointUrl + " is not a valid URL", e);
        }

        final List<Pair<String, String>> queryParams = urlBuilder.getQueryParams();
        queryParams.clear();

        final AbstractSAMLArtifact artifact = buildArtifact(messageContext);
        if (artifact == null) {
            log.error("Unable to build artifact for message to relying party");
            throw new MessageEncodingException("Unable to builder artifact for message to relying party");
        }
        queryParams.add(new Pair<String, String>("SAMLart", artifact.base64Encode()));

        final String relayState = SAMLBindingSupport.getRelayState(messageContext);
        if (SAMLBindingSupport.checkRelayState(relayState)) {
            queryParams.add(new Pair<String, String>("RelayState", relayState));
        }

        final HttpServletResponse response = getHttpServletResponse();
        try {
            response.sendRedirect(urlBuilder.buildURL());
        } catch (final IOException e) {
            throw new MessageEncodingException("Problem sending HTTP redirect", e);
        }
    }

    /**
     * Builds the SAML 2 artifact for the outgoing message.
     * 
     * @param messageContext current request context
     * 
     * @return SAML 2 artifact for outgoing message
     * 
     * @throws MessageEncodingException thrown if the artifact can not be created
     */
    @Nonnull protected AbstractSAML2Artifact buildArtifact(@Nonnull final MessageContext<SAMLObject> messageContext) 
            throws MessageEncodingException {

        final String requester = getInboundMessageIssuer(messageContext);
        final String issuer = getOutboundMessageIssuer(messageContext);
        if (requester == null || issuer == null) {
            throw new MessageEncodingException("Unable to obtain issuer or relying party for message encoding");
        }
        
        final SAML2ArtifactBuilder artifactBuilder;
        final byte[] artifactType = getSAMLArtifactType(messageContext);
        if (artifactType != null) {
            artifactBuilder = SAMLConfigurationSupport.getSAML2ArtifactBuilderFactory()
                    .getArtifactBuilder(artifactType);
        } else {
            artifactBuilder = SAMLConfigurationSupport.getSAML2ArtifactBuilderFactory()
                    .getArtifactBuilder(defaultArtifactType);
            storeSAMLArtifactType(messageContext, defaultArtifactType);
        }

        final AbstractSAML2Artifact artifact = artifactBuilder.buildArtifact(messageContext);
        if (artifact == null) {
            log.error("Unable to build artifact for message to relying party");
            throw new MessageEncodingException("Unable to builder artifact for message to relying party");
        }
        final String encodedArtifact = artifact.base64Encode();
        try {
            artifactMap.put(encodedArtifact, requester, issuer, messageContext.getMessage());
        } catch (final IOException e) {
            log.error("Unable to store message mapping for artifact", e);
            throw new MessageEncodingException("Unable to store message mapping for artifact", e);
        }

        return artifact;
    }
    
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