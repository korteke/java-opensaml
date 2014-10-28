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

import java.io.UnsupportedEncodingException;

import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;

import org.apache.velocity.VelocityContext;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.messaging.SAMLMessageSecuritySupport;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.crypto.XMLSigningUtil;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * SAML 2.0 HTTP-POST-SimpleSign binding message encoder.
 */
public class HTTPPostSimpleSignEncoder extends HTTPPostEncoder {
    
    /** Default template ID. */
    public static final String DEFAULT_TEMPLATE_ID = "/templates/saml2-post-simplesign-binding.vm";

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(HTTPPostSimpleSignEncoder.class);
    
    /** Constructor. */
    public HTTPPostSimpleSignEncoder() {
        setVelocityTemplateId(DEFAULT_TEMPLATE_ID);
    }

    /** {@inheritDoc} */
    public String getBindingURI() {
        return SAMLConstants.SAML2_POST_SIMPLE_SIGN_BINDING_URI;
    }

    /** {@inheritDoc} */
    protected void populateVelocityContext(VelocityContext velocityContext, MessageContext<SAMLObject> messageContext,
            String endpointURL) throws MessageEncodingException {

        super.populateVelocityContext(velocityContext, messageContext, endpointURL);

        SignatureSigningParameters signingParameters = 
                SAMLMessageSecuritySupport.getContextSigningParameters(messageContext);
        
        if (signingParameters == null || signingParameters.getSigningCredential() == null) {
            log.debug("No signing credential was supplied, skipping HTTP-Post simple signing");
            return;
        }

        String sigAlgURI = getSignatureAlgorithmURI(signingParameters);
        velocityContext.put("SigAlg", sigAlgURI);

        String formControlData = buildFormDataToSign(velocityContext, messageContext, sigAlgURI);
        velocityContext.put("Signature", generateSignature(signingParameters.getSigningCredential(), 
                sigAlgURI, formControlData));

        
        KeyInfoGenerator kiGenerator = signingParameters.getKeyInfoGenerator();
        if (kiGenerator != null) {
            String kiBase64 = buildKeyInfo(signingParameters.getSigningCredential(), kiGenerator);
            if (!Strings.isNullOrEmpty(kiBase64)) {
                velocityContext.put("KeyInfo", kiBase64);
            }
        }
    }

    /**
     * Build the {@link KeyInfo} from the signing credential.
     * 
     * @param signingCredential the credential used for signing
     * @param kiGenerator the generator for the KeyInfo
     * @throws MessageEncodingException thrown if there is an error generating or marshalling the KeyInfo
     * @return the marshalled, serialized and base64-encoded KeyInfo, or null if none was generated
     */
    protected String buildKeyInfo(Credential signingCredential, KeyInfoGenerator kiGenerator)
            throws MessageEncodingException {

        try {
            KeyInfo keyInfo = kiGenerator.generate(signingCredential);
            if (keyInfo != null) {
                Marshaller marshaller = XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(keyInfo);
                if (marshaller == null) {
                    log.error("No KeyInfo marshaller available from configuration");
                    throw new MessageEncodingException("No KeyInfo marshaller was configured");
                }
                String kiXML = SerializeSupport.nodeToString(marshaller.marshall(keyInfo));
                String kiBase64 = Base64Support.encode(kiXML.getBytes(), Base64Support.UNCHUNKED);
                return kiBase64;
            } else {
                return null;
            }
        } catch (final SecurityException e) {
            log.error("Error generating KeyInfo from signing credential", e);
            throw new MessageEncodingException("Error generating KeyInfo from signing credential", e);
        } catch (final MarshallingException e) {
            log.error("Error marshalling KeyInfo based on signing credential", e);
            throw new MessageEncodingException("Error marshalling KeyInfo based on signing credential", e);
        }
    }

    /**
     * Build the form control data string over which the signature is computed.
     * 
     * @param velocityContext the Velocity context which is already populated with the values for SAML message and relay
     *            state
     * @param messageContext the SAML message context being processed
     * @param sigAlgURI the signature algorithm URI
     * 
     * @return the form control data string for signature computation
     */
    protected String buildFormDataToSign(VelocityContext velocityContext, MessageContext<SAMLObject> messageContext,
            String sigAlgURI) {
        StringBuilder builder = new StringBuilder();

        boolean isRequest = false;
        if (velocityContext.get("SAMLRequest") != null) {
            isRequest = true;
        }

        String msgB64;
        if (isRequest) {
            msgB64 = (String) velocityContext.get("SAMLRequest");
        } else {
            msgB64 = (String) velocityContext.get("SAMLResponse");
        }

        String msg = null;
        try {
            msg = new String(Base64Support.decode(msgB64), "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            // All JVM's required to support UTF-8
        }

        if (isRequest) {
            builder.append("SAMLRequest=" + msg);
        } else {
            builder.append("SAMLResponse=" + msg);
        }

        String relayState = SAMLBindingSupport.getRelayState(messageContext);
        if (relayState != null) {
            builder.append("&RelayState=" + relayState);
        }

        builder.append("&SigAlg=" + sigAlgURI);

        return builder.toString();
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
        
        if (signingParameters.getSignatureAlgorithm() != null) {
            return signingParameters.getSignatureAlgorithm();
        }

        throw new MessageEncodingException("The signing algorithm URI could not be determined");
    }

    /**
     * Generates the signature over the string of concatenated form control data as indicated by the SimpleSign spec.
     * 
     * @param signingCredential credential that will be used to sign
     * @param algorithmURI algorithm URI of the signing credential
     * @param formData form control data to be signed
     * 
     * @return base64 encoded signature of form control data
     * 
     * @throws MessageEncodingException there is an error computing the signature
     */
    protected String generateSignature(Credential signingCredential, String algorithmURI, String formData)
            throws MessageEncodingException {

        log.debug(String.format(
                "Generating signature with key type '%s', algorithm URI '%s' over form control string '%s'",
                CredentialSupport.extractSigningKey(signingCredential).getAlgorithm(), algorithmURI, formData));

        String b64Signature = null;
        try {
            byte[] rawSignature =
                    XMLSigningUtil.signWithURI(signingCredential, algorithmURI, formData.getBytes("UTF-8"));
            b64Signature = Base64Support.encode(rawSignature, Base64Support.UNCHUNKED);
            log.debug("Generated digital signature value (base64-encoded) {}", b64Signature);
        } catch (SecurityException e) {
            log.error("Error during URL signing process", e);
            throw new MessageEncodingException("Unable to sign form control string", e);
        } catch (UnsupportedEncodingException e) {
            // UTF-8 encoding is required to be supported by all JVMs
        }

        return b64Signature;
    }

}
