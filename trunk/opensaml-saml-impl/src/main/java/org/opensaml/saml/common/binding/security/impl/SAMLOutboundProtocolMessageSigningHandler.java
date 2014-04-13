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

import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.messaging.SAMLMessageSecuritySupport;
import org.opensaml.security.SecurityException;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A message handler implementation that signs an outbound SAML protocol message if the message context
 * contains an instance of {@link org.opensaml.xmlsec.context.SignatureSigningParameters} as determined by
 * {@link SAMLMessageSecuritySupport#getContextSigningParameters(MessageContext)}.
 */
public class SAMLOutboundProtocolMessageSigningHandler extends AbstractMessageHandler<SAMLObject> {
    
    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(SAMLOutboundProtocolMessageSigningHandler.class);

    /** {@inheritDoc} */
    @Override
    protected void doInvoke(@Nonnull final MessageContext<SAMLObject> messageContext) throws MessageHandlerException {
        final SignatureSigningParameters signingParameters = 
                SAMLMessageSecuritySupport.getContextSigningParameters(messageContext);
        if (signingParameters != null) {
            try {
                SAMLMessageSecuritySupport.signMessage(messageContext);
            } catch (SecurityException | MarshallingException | SignatureException e) {
                throw new MessageHandlerException("Error signing outbound protocol message", e);
            }
        } else {
            log.info("{} Message context did not contain signing parameters, outbound message will not be signed",
                    getLogPrefix());
        }
    }

}