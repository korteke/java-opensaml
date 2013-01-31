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

package org.opensaml.saml.common.messaging;

import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.saml.common.messaging.context.SamlSigningContext;
import org.opensaml.saml.config.SAMLConfigurationSupport;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.SignatureSigningConfiguration;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureSupport;
import org.opensaml.xmlsec.signature.support.Signer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A support class for SAML security-related message handler operations. */
public final class SamlMessageSecuritySupport {
    
    /** Logger. */
    private static final Logger LOG = LoggerFactory.getLogger(SamlMessageSecuritySupport.class);

    /** Constructor. */
    private SamlMessageSecuritySupport() { }
    
    /**
     * Signs the SAML message represented in the message context if it a {@link SignableSAMLObject}
     * and the message context contains signing credentials as determined 
     * by {@link #getContextSigningCredential(MessageContext)}.
     * 
     * @param messageContext current message context
     * 
     * @throws SecurityException  if there is a problem preparing the signature
     * @throws MarshallingException  if there is a problem marshalling the SAMLObject
     * @throws SignatureException  if there is a problem with the signature operation
     * 
     */
    @SuppressWarnings("unchecked")
    public static void signMessage(MessageContext<SAMLObject> messageContext) 
            throws SecurityException, MarshallingException, SignatureException {
        SAMLObject outboundSAML = messageContext.getMessage();
        Credential signingCredential = getContextSigningCredential(messageContext);

        if (outboundSAML instanceof SignableSAMLObject && signingCredential != null) {
            SignableSAMLObject signableMessage = (SignableSAMLObject) outboundSAML;

            XMLObjectBuilder<Signature> signatureBuilder = 
                    XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(Signature.DEFAULT_ELEMENT_NAME);
            Signature signature = signatureBuilder.buildObject(Signature.DEFAULT_ELEMENT_NAME);
            
            signature.setSigningCredential(signingCredential);
            //TODO security config, keyinfo gen from context
            SignatureSupport.prepareSignatureParams(signature, signingCredential, null, null);
            
            signableMessage.setSignature(signature);

            Marshaller marshaller = 
                    XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(signableMessage);
            marshaller.marshall(signableMessage);

            Signer.signObject(signature);
        }
    }

    /**
     * Get the signing credential from the message context.
     * 
     * @param messageContext the message context
     * 
     * @return the signing credential to use, may be null
     */
    @Nullable public static Credential getContextSigningCredential(MessageContext<SAMLObject> messageContext) {
        SamlSigningContext context = messageContext.getSubcontext(SamlSigningContext.class);
        if (context != null) {
            SignatureSigningConfiguration configuration = context.getSigningConfiguration();
            if (configuration != null) {
                return configuration.getSigningCredential();
            }
        }
        return null;
    }
    
    /**
     * Check whether the specified URL scheme is allowed.
     * 
     * @param scheme the URL scheme to check.
     * 
     * @return true if allowed, otherwise false
     */
    public static boolean checkUrlScheme(String scheme) {
        return SAMLConfigurationSupport.getAllowedBindingUrlSchemes().contains(scheme);
    }

}
