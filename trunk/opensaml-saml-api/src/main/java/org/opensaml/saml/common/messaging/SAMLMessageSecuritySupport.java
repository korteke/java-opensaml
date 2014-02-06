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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.config.SAMLConfigurationSupport;
import org.opensaml.security.SecurityException;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.opensaml.xmlsec.signature.SignableXMLObject;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureSupport;

/** A support class for SAML security-related message handler operations. */
public final class SAMLMessageSecuritySupport {

    /** Constructor. */
    private SAMLMessageSecuritySupport() {
        
    }
    
    /**
     * Signs the SAML message represented in the message context if it is a {@link SignableXMLObject}
     * and the message context contains signing parameters as determined 
     * by {@link #getContextSigningParameters(MessageContext)}.
     * 
     * @param messageContext current message context
     * 
     * @throws SecurityException  if there is a problem preparing the signature
     * @throws MarshallingException  if there is a problem marshalling the SAMLObject
     * @throws SignatureException  if there is a problem with the signature operation
     * 
     */
    public static void signMessage(@Nonnull final MessageContext<SAMLObject> messageContext) 
            throws SecurityException, MarshallingException, SignatureException {
        Constraint.isNotNull(messageContext, "Message context cannot be null");
        
        final SAMLObject outboundSAML = messageContext.getMessage();
        final SignatureSigningParameters parameters = getContextSigningParameters(messageContext);

        if (outboundSAML instanceof SignableXMLObject && parameters != null) {
            SignatureSupport.signObject((SignableXMLObject) outboundSAML, parameters);
        }
    }

    /**
     * Get the signing parameters from the message context.
     * 
     * @param messageContext the message context
     * 
     * @return the signing parameters to use, may be null
     */
    @Nullable public static SignatureSigningParameters getContextSigningParameters(
            @Nonnull final MessageContext<SAMLObject> messageContext) {
        Constraint.isNotNull(messageContext, "Message context cannot be null");

        final SecurityParametersContext context = messageContext.getSubcontext(SecurityParametersContext.class);
        if (context != null) {
            return context.getSignatureSigningParameters();
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
    public static boolean checkUrlScheme(@Nonnull @NotEmpty String scheme) {
        return SAMLConfigurationSupport.getAllowedBindingUrlSchemes().contains(scheme);
    }

}
