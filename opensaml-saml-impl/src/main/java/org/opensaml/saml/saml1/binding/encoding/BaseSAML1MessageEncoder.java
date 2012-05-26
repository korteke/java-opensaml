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

import java.net.URI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.messaging.encoder.servlet.BaseHttpServletResponseXmlMessageEncoder;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.saml.common.binding.BindingException;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.binding.SAMLMessageContext;
import org.opensaml.saml.common.binding.encoding.SAMLMessageEncoder;
import org.opensaml.saml.common.context.SamlProtocolContext;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureSupport;
import org.opensaml.xmlsec.signature.support.Signer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO pull allowed URL scheme check out in to a separate class
//TODO not sure if message signing should be in here either, it's not really part of the encoding process

/**
 * Base class for SAML 1 message encoders.
 */
public abstract class BaseSAML1MessageEncoder extends BaseHttpServletResponseXmlMessageEncoder<SAMLObject> 
        implements SAMLMessageEncoder {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(BaseSAML1MessageEncoder.class);

    /** The list of schemes allowed to appear in URLs related to the encoded message. Defaults to 'http' and 'https'. */
    private List<String> allowedURLSchemes;

    /** Constructor. */
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
     * Gets the response URL from the message context.
     * 
     * @param messageContext current message context
     * 
     * @return response URL from the message context
     * 
     * @throws MessageEncodingException throw if no relying party endpoint is available
     */
    protected URI getEndpointURL(MessageContext<SAMLObject> messageContext) throws MessageEncodingException {
        URI endpointUrl;
        try {
            endpointUrl = SAMLBindingSupport.getEndpointURL(messageContext);
        } catch (BindingException e) {
            throw new MessageEncodingException("Could not obtain message endpoint URL", e);
        }

        //TODO
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
    protected void signMessage(MessageContext<SAMLObject> messageContext) throws MessageEncodingException {
        SAMLObject outboundSAML = messageContext.getMessage();
        Credential signingCredential = getContextSigningCredential(messageContext);

        if (outboundSAML instanceof SignableSAMLObject && signingCredential != null) {
            SignableSAMLObject signableMessage = (SignableSAMLObject) outboundSAML;

            XMLObjectBuilder<Signature> signatureBuilder = 
                    XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(Signature.DEFAULT_ELEMENT_NAME);
            Signature signature = signatureBuilder.buildObject(Signature.DEFAULT_ELEMENT_NAME);
            
            signature.setSigningCredential(signingCredential);
            try {
                //TODO pull SecurityConfiguration from SAMLMessageContext?  needs to be added
                //TODO pull binding-specific keyInfoGenName from encoder setting, etc?
                SignatureSupport.prepareSignatureParams(signature, signingCredential, null, null);
            } catch (SecurityException e) {
                throw new MessageEncodingException("Error preparing signature for signing", e);
            }
            
            signableMessage.setSignature(signature);

            try {
                Marshaller marshaller = 
                        XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(signableMessage);
                if (marshaller == null) {
                    throw new MessageEncodingException("No marshaller registered for "
                            + signableMessage.getElementQName() + ", unable to marshall in preperation for signing");
                }
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
    
    /**
     * @param messageContext
     * @return
     */
    protected Credential getContextSigningCredential(MessageContext<SAMLObject> messageContext) {
        // TODO Auto-generated method stub
        return null;
    }
}
