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

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.messaging.encoder.servlet.BaseHttpServletResponseXmlMessageEncoder;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.BindingException;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.binding.encoding.SAMLMessageEncoder;
import org.opensaml.saml.common.messaging.SamlMessageSecuritySupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO pull allowed URL scheme check out in to a separate class

/**
 * Base class for SAML 1 message encoders.
 */
public abstract class BaseSAML1MessageEncoder extends BaseHttpServletResponseXmlMessageEncoder<SAMLObject> 
        implements SAMLMessageEncoder {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(BaseSAML1MessageEncoder.class);

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
        if (!SamlMessageSecuritySupport.checkUrlScheme(endpointUrl.getScheme())) {
            throw new MessageEncodingException("Relying party endpoint used the untrusted URL scheme "
                    + endpointUrl.getScheme());
        }
        return endpointUrl;
    }
    
}