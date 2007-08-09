/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
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

package org.opensaml.ws.security;

import org.opensaml.ws.message.MessageContext;

/**
 * An individual rule that a message context is required to meet in order to be considered valid.
 * 
 * Rules <strong>MUST</strong> be thread safe and stateless.
 */
public interface SecurityPolicyRule {

    /**
     * Evaluates the message context against the rule.
     * 
     * During evaluation a rule should first, and as quickly as possible, determine if it can evaluate the message
     * context (for example and HTTP-transport based rule would not be able to evaluate a message context based an
     * SMTP-transport). If the rule can be evaluated it should then throw a {@link SecurityPolicyException} if the rule
     * is not met.
     * 
     * @param messageContext the message context being evaluated
     * 
     * @return true if the rule was evaluated, false if not
     * 
     * @throws SecurityPolicyException thrown if the message context does not meet the requirements of an evaluated rule
     */
    public boolean evaluate(MessageContext messageContext) throws SecurityPolicyException;
}