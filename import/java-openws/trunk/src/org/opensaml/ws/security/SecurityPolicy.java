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

import javax.servlet.ServletRequest;

import org.opensaml.xml.XMLObject;

/**
 * A policy used to verify the security of an incoming request.  Its security mechanisms may be used to 
 * check transport layer items (e.g client certificates and basic auth passwords) and the payload valiators 
 * may be used to check the payload of a request to ensure it meets certain criteria (e.g. valid digital signature).
 * 
 * @param <RequestType> type of incoming protocol request
 * @param <IssuerType> the message issuer type
 * 
 */
public interface SecurityPolicy<RequestType extends ServletRequest, IssuerType> {
    
    /**
     * Get the {@link SecurityPolicyContext} instance which stores various
     * items of state related to the evaluation of this policy.
     * 
     * @return security policy context information as determined by the registered 
     *          security policy rules
     */
    public SecurityPolicyContext<IssuerType> getSecurityPolicyContext();
    
    /**
     * Convenience method for getting the issuer of the message as determined by the registered validators,
     * from the security policy context.
     * 
     * @return issuer of the message as determined by the registered validators
     */
    public IssuerType getIssuer();
    
   
    /**
     * Evaluates this policy.
     * 
     * @param request the protocol request
     * @param message the incoming message
     * 
     * @throws SecurityPolicyException thrown if the request does not meet the requirements of this policy
     */
    public void evaluate(RequestType request, XMLObject message) throws SecurityPolicyException;
}