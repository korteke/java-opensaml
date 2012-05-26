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

package org.opensaml.saml.saml1.binding.encoding;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.shibboleth.utilities.java.support.collection.Pair;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.net.UriSupport;

import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.binding.artifact.SAMLArtifactMap;
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
    private final Logger log = LoggerFactory.getLogger(HTTPArtifactEncoder.class);

    /** SAML artifact map used to store created artifacts for later retrival. */
    private SAMLArtifactMap artifactMap;

    /** Default artifact type to use when encoding messages. */
    private byte[] defaultArtifactType;

    /**
     * Constructor.
     * 
     * @param map SAML artifact map used to store created artifacts for later retrival
     */
    public HTTPArtifactEncoder(SAMLArtifactMap map) {
        defaultArtifactType = SAML1ArtifactType0001.TYPE_CODE;
    }

    /** {@inheritDoc} */
    public String getBindingURI() {
        return SAMLConstants.SAML1_ARTIFACT_BINDING_URI;
    }
    
    /**
     * Get the SAML artifact map to use.
     * 
     * @return the artifactMap.
     */
    public SAMLArtifactMap getArtifactMap() {
        return artifactMap;
    }

    /**
     * Set the SAML artifact map to use.
     * 
     * @param newArtifactMap the new artifactMap 
     */
    public void setArtifactMap(SAMLArtifactMap newArtifactMap) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        artifactMap = newArtifactMap;
    }

    /** {@inheritDoc} */
    public boolean providesMessageConfidentiality(MessageContext messageContext) throws MessageEncodingException {
        return false;
    }

    /** {@inheritDoc} */
    public boolean providesMessageIntegrity(MessageContext messageContext) throws MessageEncodingException {
        return false;
    }
    
    /** {@inheritDoc} */
    protected void doDestroy() {
        artifactMap = null;
        super.doDestroy();
    }

    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        if (artifactMap == null) {
            throw new ComponentInitializationException("SAML artifact map must be supplied");
        }
    }

    /** {@inheritDoc} */
    protected void doEncode() throws MessageEncodingException {
        MessageContext<SAMLObject> messageContext = getMessageContext();

        URI endpointUrl = getEndpointURL(messageContext);

        List<Pair<String, String>> params = UriSupport.parseQueryString(endpointUrl.getQuery());

        String relayState = SAMLBindingSupport.getRelayState(messageContext);
        if (SAMLBindingSupport.checkRelayState(relayState)) {
            params.add(new Pair<String, String>("TARGET", relayState));
        }

        SAML1ArtifactBuilder artifactBuilder;
        byte[] artifactType = getSamlArtifactType(messageContext);
        if (artifactType != null) {
            artifactBuilder = SAMLConfigurationSupport.getSAML1ArtifactBuilderFactory()
                    .getArtifactBuilder(artifactType);
        } else {
            artifactBuilder = SAMLConfigurationSupport.getSAML1ArtifactBuilderFactory()
                    .getArtifactBuilder(defaultArtifactType);
            storeSamlArtifactType(defaultArtifactType);
        }

        AbstractSAML1Artifact artifact;
        String artifactString;
        SAMLObject outboundMessage = messageContext.getMessage();
        if (!(outboundMessage instanceof Response)) {
            throw new MessageEncodingException("Outbound message was not a SAML 1 Response");
        }
        Response samlResponse = (Response) outboundMessage;
        for (Assertion assertion : samlResponse.getAssertions()) {
            artifact = artifactBuilder.buildArtifact(messageContext, assertion);
            if (artifact == null) {
                log.error("Unable to build artifact for message to relying party");
                throw new MessageEncodingException("Unable to builder artifact for message to relying party");
            }

            try {
                artifactMap.put(artifact.base64Encode(), getInboundMessageIssuer(messageContext), 
                        getOutboundMessageIssuer(messageContext), assertion);
            } catch (MarshallingException e) {
                log.error("Unable to marshall assertion to be represented as an artifact", e);
                throw new MessageEncodingException("Unable to marshall assertion to be represented as an artifact", e);
            }
            artifactString = artifact.base64Encode();
            params.add(new Pair<String, String>("SAMLart", artifactString));
        }

        endpointUrl = UriSupport.setQuery(endpointUrl, params);

        String encodedEndpoint = endpointUrl.toASCIIString();
        log.debug("Sending redirect to URL {} to relying party {}", encodedEndpoint, 
                getInboundMessageIssuer(messageContext));
        
        HttpServletResponse response = getHttpServletResponse();
        try {
            response.sendRedirect(encodedEndpoint);
        } catch (IOException e) {
            throw new MessageEncodingException("Problem sending HTTP redirect", e);
        }
    }

    /**
     * @param messageContext
     * @return
     */
    private String getOutboundMessageIssuer(MessageContext<SAMLObject> messageContext) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param messageContext
     * @return
     */
    private String getInboundMessageIssuer(MessageContext<SAMLObject> messageContext) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param defaultArtifactType2
     */
    private void storeSamlArtifactType(byte[] artifactType) {
        // TODO Auto-generated method stub
        
    }

    /**
     * @param messageContext
     * @return
     */
    private byte[] getSamlArtifactType(MessageContext<SAMLObject> messageContext) {
        // TODO Auto-generated method stub
        return null;
    }
}