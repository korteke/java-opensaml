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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import javax.servlet.http.HttpServletResponse;

import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.collection.Pair;
import net.shibboleth.utilities.java.support.net.HttpServletSupport;
import net.shibboleth.utilities.java.support.net.UrlBuilder;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.messaging.SAMLMessageSecuritySupport;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.RequestAbstractType;
import org.opensaml.saml.saml2.core.StatusResponseType;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.xmlsec.SecurityConfiguration;
import org.opensaml.xmlsec.SecurityConfigurationSupport;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.crypto.XMLSigningUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SAML 2.0 HTTP Redirect encoder using the DEFLATE encoding method.
 * 
 * This encoder only supports DEFLATE compression and DSA-SHA1 and RSA-SHA1 signatures.
 */
public class HTTPRedirectDeflateEncoder extends BaseSAML2MessageEncoder {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(HTTPRedirectDeflateEncoder.class);

    /** Constructor. */
    public HTTPRedirectDeflateEncoder() {
        super();
    }

    /** {@inheritDoc} */
    public String getBindingURI() {
        return SAMLConstants.SAML2_REDIRECT_BINDING_URI;
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
    protected void doEncode() throws MessageEncodingException {
        MessageContext<SAMLObject> messageContext = getMessageContext();
        SAMLObject outboundMessage = messageContext.getMessage();

        String endpointURL = getEndpointURL(messageContext).toString();

        removeSignature(outboundMessage);

        String encodedMessage = deflateAndBase64Encode(outboundMessage);

        String redirectURL = buildRedirectURL(messageContext, endpointURL, encodedMessage);

        HttpServletResponse response = getHttpServletResponse();
        HttpServletSupport.addNoCacheHeaders(response);
        HttpServletSupport.setUTF8Encoding(response);

        try {
            response.sendRedirect(redirectURL);
        } catch (IOException e) {
            throw new MessageEncodingException("Problem sending HTTP redirect", e);
        }
    }

    /**
     * Removes the signature from the protocol message.
     * 
     * @param message current message context
     */
    protected void removeSignature(SAMLObject message) {
        if (message instanceof SignableSAMLObject) {
            SignableSAMLObject signableMessage = (SignableSAMLObject) message;
            if (signableMessage.isSigned()) {
                log.debug("Removing SAML protocol message signature");
                signableMessage.setSignature(null);
            }
        }
    }

    /**
     * DEFLATE (RFC1951) compresses the given SAML message.
     * 
     * @param message SAML message
     * 
     * @return DEFLATE compressed message
     * 
     * @throws MessageEncodingException thrown if there is a problem compressing the message
     */
    protected String deflateAndBase64Encode(SAMLObject message) throws MessageEncodingException {
        log.debug("Deflating and Base64 encoding SAML message");
        try {
            String messageStr = SerializeSupport.nodeToString(marshallMessage(message));

            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            Deflater deflater = new Deflater(Deflater.DEFLATED, true);
            DeflaterOutputStream deflaterStream = new DeflaterOutputStream(bytesOut, deflater);
            deflaterStream.write(messageStr.getBytes("UTF-8"));
            deflaterStream.finish();

            return Base64Support.encode(bytesOut.toByteArray(), Base64Support.UNCHUNKED);
        } catch (IOException e) {
            throw new MessageEncodingException("Unable to DEFLATE and Base64 encode SAML message", e);
        }
    }

    /**
     * Builds the URL to redirect the client to.
     * 
     * @param messageContext current message context
     * @param endpoint endpoint URL to send encoded message to
     * @param message Deflated and Base64 encoded message
     * 
     * @return URL to redirect client to
     * 
     * @throws MessageEncodingException thrown if the SAML message is neither a RequestAbstractType or Response
     */
    protected String buildRedirectURL(MessageContext<SAMLObject> messageContext, String endpoint, String message)
            throws MessageEncodingException {
        log.debug("Building URL to redirect client to");
        
        UrlBuilder urlBuilder = null;
        try {
            urlBuilder = new UrlBuilder(endpoint);
        } catch (MalformedURLException e) {
            throw new MessageEncodingException("Endpoint URL " + endpoint + " is not a valid URL", e);
        }

        List<Pair<String, String>> queryParams = urlBuilder.getQueryParams();
        queryParams.clear();
        
        SAMLObject outboundMessage = messageContext.getMessage();

        if (outboundMessage instanceof RequestAbstractType) {
            queryParams.add(new Pair<String, String>("SAMLRequest", message));
        } else if (outboundMessage instanceof StatusResponseType) {
            queryParams.add(new Pair<String, String>("SAMLResponse", message));
        } else {
            throw new MessageEncodingException(
                    "SAML message is neither a SAML RequestAbstractType or StatusResponseType");
        }

        String relayState = SAMLBindingSupport.getRelayState(messageContext);
        if (SAMLBindingSupport.checkRelayState(relayState)) {
            queryParams.add(new Pair<String, String>("RelayState", relayState));
        }

        SignatureSigningParameters signingParameters = 
                SAMLMessageSecuritySupport.getContextSigningParameters(messageContext);
        if (signingParameters != null && signingParameters.getSigningCredential() != null) {
            String sigAlgURI =  getSignatureAlgorithmURI(signingParameters);
            Pair<String, String> sigAlg = new Pair<String, String>("SigAlg", sigAlgURI);
            queryParams.add(sigAlg);
            String sigMaterial = urlBuilder.buildQueryString();

            queryParams.add(new Pair<String, String>("Signature", generateSignature(
                    signingParameters.getSigningCredential(), sigAlgURI, sigMaterial)));
        } else {
            log.debug("No signing credential was supplied, skipping HTTP-Redirect DEFLATE signing");
        }
        
        return urlBuilder.buildURL();
    }

    /**
     * Gets the signature algorithm URI to use.
     * 
     * @param signingParameters the signing parameters to use
     * 
     * @return signature algorithm to use with the associated signing credential
     * 
     * @throws MessageEncodingException thrown if the algorithm URI is not supplied explicitly and 
     *          could not be derived from the supplied credential
     */
    protected String getSignatureAlgorithmURI(SignatureSigningParameters signingParameters)
            throws MessageEncodingException {
        
        if (signingParameters.getSignatureAlgorithmURI() != null) {
            return signingParameters.getSignatureAlgorithmURI();
        }
        
        SecurityConfiguration globalSecurityConfig = SecurityConfigurationSupport.getGlobalXMLSecurityConfiguration();
        if (globalSecurityConfig != null) {
            String signAlgo = globalSecurityConfig.getSignatureAlgorithmURI(signingParameters.getSigningCredential());
            if (signAlgo != null) {
                return signAlgo;
            }
        }

        throw new MessageEncodingException("The signing algorithm URI could not be determined");
    }

    /**
     * Generates the signature over the query string.
     * 
     * @param signingCredential credential that will be used to sign query string
     * @param algorithmURI algorithm URI of the signing credential
     * @param queryString query string to be signed
     * 
     * @return base64 encoded signature of query string
     * 
     * @throws MessageEncodingException there is an error computing the signature
     */
    protected String generateSignature(Credential signingCredential, String algorithmURI, String queryString)
            throws MessageEncodingException {

        log.debug(String.format("Generating signature with key type '%s', algorithm URI '%s' over query string '%s'",
                CredentialSupport.extractSigningKey(signingCredential).getAlgorithm(), algorithmURI, queryString));

        String b64Signature = null;
        try {
            byte[] rawSignature =
                    XMLSigningUtil.signWithURI(signingCredential, algorithmURI, queryString.getBytes("UTF-8"));
            b64Signature = Base64Support.encode(rawSignature, Base64Support.UNCHUNKED);
            log.debug("Generated digital signature value (base64-encoded) {}", b64Signature);
        } catch (SecurityException e) {
            log.error("Error during URL signing process", e);
            throw new MessageEncodingException("Unable to sign URL query string", e);
        } catch (UnsupportedEncodingException e) {
            // UTF-8 encoding is required to be supported by all JVMs
        }

        return b64Signature;
    }
}