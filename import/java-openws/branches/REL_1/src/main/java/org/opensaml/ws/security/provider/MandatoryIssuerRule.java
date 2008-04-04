/*
 * Copyright [2007] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.ws.security.provider;

import org.opensaml.ws.message.MessageContext;
import org.opensaml.ws.security.SecurityPolicyException;
import org.opensaml.ws.security.SecurityPolicyRule;
import org.opensaml.xml.util.DatatypeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Security policy rule implementation that which requires that an inbound message context issuer has been set by a
 * previous rule. Should typically run at the end of the security policy rule chain.
 */
public class MandatoryIssuerRule implements SecurityPolicyRule {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(MandatoryIssuerRule.class);

    /** {@inheritDoc} */
    public void evaluate(MessageContext messageContext) throws SecurityPolicyException {

        if (DatatypeHelper.isEmpty(messageContext.getInboundMessageIssuer())) {
            log.error("Mandatory inbound message context issuer was not present");
            throw new SecurityPolicyException("Mandatory inbound message context issuer not present");
        }

    }

}
