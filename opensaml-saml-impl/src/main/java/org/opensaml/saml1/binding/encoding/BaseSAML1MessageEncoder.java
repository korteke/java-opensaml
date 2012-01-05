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

package org.opensaml.saml1.binding.encoding;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opensaml.Configuration;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.SignableSAMLObject;
import org.opensaml.common.binding.SAMLMessageContext;
import org.opensaml.common.binding.encoding.SAMLMessageEncoder;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.metadata.Endpoint;
import org.opensaml.ws.message.encoder.BaseMessageEncoder;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.XMLSecurityHelper;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureException;
import org.opensaml.xml.signature.Signer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

//TODO pull allowed URL scheme check out in to a separate class
//TODO pull out the getEndpointURL method to support class and share it with BaseSAML2MessageEncoder
//TODO not sure if message signing should be in here either, it's not really part of the encoding process

/**
 * Base class for SAML 1 message encoders.
 */
public abstract class BaseSAML1MessageEncoder extends BaseMessageEncoder implements SAMLMessageEncoder {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(BaseSAML1MessageEncoder.class);

    /** The list of schemes allowed to appear in URLs related to the encoded message. Defaults to 'http' and 'https'. */
    private List<String> allowedURLSchemes;

    public BaseSAML1MessageEncoder() {
        super();
        setAllowedURLSchemes(new String[] {"http", "https"});
    }

    /**
     * Gets the unmodifiable list of schemes allowed to appear in URLs related to the encoded message.
     * 
     * @return list of URL schemes allowed to appear in a message
     */
    public List<String> getAllowedURLSchemes() {
        return allowedURLSchemes;
    }

    /**
     * Sets the list of list of schemes allowed to appear in URLs related to the encoded message. Note, the appearance
     * of schemes such as 'javascript' may open the system up to attacks (e.g. cross-site scripting attacks).
     * 
     * @param schemes URL schemes allowed to appear in a message
     */
    public void setAllowedURLSchemes(String[] schemes) {
        if (schemes == null || schemes.length == 0) {
            allowedURLSchemes = Collections.emptyList();
        } else {
            List<String> temp = new ArrayList<String>();
            for (String scheme : schemes) {
                temp.add(scheme);
            }
            allowedURLSchemes = Collections.unmodifiableList(temp);
        }
    }

    /**
     * Gets the response URL from the relying party endpoint. If the SAML message is a {@link Response} and the relying
     * party endpoint contains a response location then that location is returned otherwise the normal endpoint location
     * is returned.
     * 
     * @param messageContext current message context
     * 
     * @return response URL from the relying party endpoint
     * 
     * @throws MessageEncodingException throw if no relying party endpoint is available
     */
    protected URI getEndpointURL(SAMLMessageContext messageContext) throws MessageEncodingException {
        Endpoint endpoint = messageContext.getPeerEntityEndpoint();
        if (endpoint == null) {
            throw new MessageEncodingException("Endpoint for relying party was null.");
        }

        URI endpointUrl;
        if (messageContext.getOutboundMessage() instanceof Response
                && !Strings.isNullOrEmpty(endpoint.getResponseLocation())) {
            try {
                endpointUrl = new URI(endpoint.getResponseLocation());
            } catch (URISyntaxException e) {
                throw new MessageEncodingException("The endpoint response location " + endpoint.getResponseLocation()
                        + " is not a valid URL", e);
            }
        } else {
            if (Strings.isNullOrEmpty(endpoint.getLocation())) {
                throw new MessageEncodingException("Relying party endpoint location was null or empty.");
            }
            try {
                endpointUrl = new URI(endpoint.getLocation());
            } catch (URISyntaxException e) {
                throw new MessageEncodingException("The endpoint location " + endpoint.getLocation()
                        + " is not a valid URL", e);
            }
        }

        if (!getAllowedURLSchemes().contains(endpointUrl.getScheme())) {
            throw new MessageEncodingException("Relying party endpoint used the untrusted URL scheme "
                    + endpointUrl.getScheme());
        }
        return endpointUrl;
    }

    /**
     * Signs the given SAML message if it a {@link SignableSAMLObject} and this encoder has signing credentials.
     * 
     * @param messageContext current message context
     * 
     * @throws MessageEncodingException thrown if there is a problem preparing the signature for signing
     */
    @SuppressWarnings("unchecked")
    protected void signMessage(SAMLMessageContext messageContext) throws MessageEncodingException {
        SAMLObject outboundMessage = messageContext.getOutboundSAMLMessage();
        if (outboundMessage instanceof SignableSAMLObject
                && messageContext.getOuboundSAMLMessageSigningCredential() != null) {
            log.debug("Signing outbound SAML message.");
            SignableSAMLObject signableMessage = (SignableSAMLObject) outboundMessage;
            Credential signingCredential = messageContext.getOuboundSAMLMessageSigningCredential();

            XMLObjectBuilder<Signature> signatureBuilder =
                    Configuration.getBuilderFactory().getBuilder(Signature.DEFAULT_ELEMENT_NAME);
            Signature signature = signatureBuilder.buildObject(Signature.DEFAULT_ELEMENT_NAME);
            signature.setSigningCredential(signingCredential);

            try {
                // TODO pull SecurityConfiguration from SAMLMessageContext? needs to be added
                // TODO pull binding-specific keyInfoGenName from encoder setting, etc?
                XMLSecurityHelper.prepareSignatureParams(signature, signingCredential, null, null);
            } catch (SecurityException e) {
                throw new MessageEncodingException("Error preparing signature for signing", e);
            }

            signableMessage.setSignature(signature);

            try {
                Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(signableMessage);
                marshaller.marshall(signableMessage);
                Signer.signObject(signature);
            } catch (MarshallingException e) {
                log.error("Unable to marshall protocol message in preparation for signing", e);
                throw new MessageEncodingException("Unable to marshall protocol message in preparation for signing", e);
            } catch (SignatureException e) {
                log.error("Unable to sign protocol message", e);
                throw new MessageEncodingException("Unable to sign protocol message", e);
            }
        }
    }
}
