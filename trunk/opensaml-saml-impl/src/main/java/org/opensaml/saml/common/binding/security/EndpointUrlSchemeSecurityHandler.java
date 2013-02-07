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

package org.opensaml.saml.common.binding.security;

import java.net.URI;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.binding.BindingException;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.messaging.SamlMessageSecuritySupport;
import org.opensaml.saml.saml2.metadata.Endpoint;

/**
 * Class which verifies that the {@link Endpoint} to which a message will be delivered
 * contains a valid URL scheme.
 */
public class EndpointUrlSchemeSecurityHandler extends AbstractMessageHandler {

    /** {@inheritDoc} */
    protected void doInvoke(MessageContext messageContext) throws MessageHandlerException {
        URI endpointUrl;
        try {
            endpointUrl = SAMLBindingSupport.getEndpointURL(messageContext);
        } catch (BindingException e) {
            throw new MessageHandlerException("Could not obtain message endpoint URL", e);
        }
        
        if (!SamlMessageSecuritySupport.checkUrlScheme(endpointUrl.getScheme())) {
            throw new MessageHandlerException("Relying party endpoint used the untrusted URL scheme "
                    + endpointUrl.getScheme());
        }
    }

}
