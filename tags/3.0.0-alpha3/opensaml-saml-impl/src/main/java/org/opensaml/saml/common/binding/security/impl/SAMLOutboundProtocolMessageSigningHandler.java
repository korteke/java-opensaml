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

package org.opensaml.saml.common.binding.security.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.component.ComponentSupport;

import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.messaging.SAMLMessageSecuritySupport;
import org.opensaml.saml.saml1.core.Response;
import org.opensaml.saml.saml2.core.StatusResponseType;
import org.opensaml.security.SecurityException;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A message handler implementation that signs an outbound SAML protocol message if the message context
 * contains an instance of {@link SignatureSigningParameters} as determined by
 * {@link SAMLMessageSecuritySupport#getContextSigningParameters(MessageContext)}.
 */
public class SAMLOutboundProtocolMessageSigningHandler extends AbstractMessageHandler<SAMLObject> {
    
    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(SAMLOutboundProtocolMessageSigningHandler.class);

    /** Whether to sign responses containing errors. */
    private boolean signErrorResponses;
    
    /** Constructor. */
    public SAMLOutboundProtocolMessageSigningHandler() {
        signErrorResponses = true;
    }
    
    /**
     * Set whether to sign response messages that contain errors (defaults to true).
     * 
     * @param flag  flag to set
     */
    public void setSignErrorResponses(final boolean flag) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        signErrorResponses = flag;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInvoke(@Nonnull final MessageContext<SAMLObject> messageContext) throws MessageHandlerException {
        final SignatureSigningParameters signingParameters = 
                SAMLMessageSecuritySupport.getContextSigningParameters(messageContext);
        if (signingParameters != null) {
            if (!signErrorResponses && isErrorResponse(messageContext.getMessage())) {
                log.debug("{} Message context contained signing parameters, but error response signatures are disabled",
                        getLogPrefix());
            } else {
                try {
                    SAMLMessageSecuritySupport.signMessage(messageContext);
                } catch (SecurityException | MarshallingException | SignatureException e) {
                    throw new MessageHandlerException("Error signing outbound protocol message", e);
                }
            }
        } else {
            log.debug("{} Message context did not contain signing parameters, outbound message will not be signed",
                    getLogPrefix());
        }
    }

    /**
     * Get whether the message is a SAML response containing an error status.
     * 
     * @param message   message to check
     * 
     * @return  true iff the message is a SAML response containing an error status
     */
    private boolean isErrorResponse(@Nullable final SAMLObject message) {
        if (message != null) {
            if (message instanceof Response) {
                if (((Response) message).getStatus() != null) {
                    final org.opensaml.saml.saml1.core.StatusCode s1 = ((Response) message).getStatus().getStatusCode();
                    return s1 != null && s1.getValue() != null
                            && !org.opensaml.saml.saml1.core.StatusCode.SUCCESS.equals(s1.getValue());
                }
            } else if (message instanceof StatusResponseType) {
                if (((StatusResponseType) message).getStatus() != null) {
                    final org.opensaml.saml.saml2.core.StatusCode s2 =
                            ((StatusResponseType) message).getStatus().getStatusCode();
                    return s2 != null && s2.getValue() != null
                            && !org.opensaml.saml.saml2.core.StatusCode.SUCCESS.equals(s2.getValue());
                }
            }
        }
        
        return false;
    }
}