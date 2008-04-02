/*
 * Copyright 2008 Members of the EGEE Collaboration.
 * Copyright 2008 University Corporation for Advanced Internet Development, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opensaml.ws.wstrust.impl;


import org.opensaml.ws.wstrust.Authenticator;
import org.opensaml.ws.wstrust.RequestSecurityTokenResponse;
import org.opensaml.ws.wstrust.RequestedAttachedReference;
import org.opensaml.ws.wstrust.RequestedProofToken;
import org.opensaml.ws.wstrust.RequestedSecurityToken;
import org.opensaml.ws.wstrust.RequestedUnattachedReference;
import org.opensaml.ws.wstrust.Status;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;

/**
 * RequestSecurityTokenResponseUnmarshaller
 * 
 * @see RequestSecurityTokenResponse
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class RequestSecurityTokenResponseUnmarshaller extends
        AbstractRequestSecurityTokenTypeUnmarshaller {

    /**
     * Default constructor.
     */
    public RequestSecurityTokenResponseUnmarshaller() {
        super(RequestSecurityTokenResponse.ELEMENT_NAME.getNamespaceURI(),
              RequestSecurityTokenResponse.ELEMENT_NAME.getLocalPart());
    }

    /**
     * Unmarshalls the following additional elements:
     * <ul>
     * <li>{@link Authenticator}
     * <li>{@link RequestedAttachedReference}
     * <li>{@link RequestedUnattachedReference}
     * <li>{@link RequestedProofToken}
     * <li>{@link RequestedSecurityToken}
     * <li>{@link Status}
     * </ul>
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processChildElement(XMLObject parentXMLObject,
            XMLObject childXMLObject) throws UnmarshallingException {
        RequestSecurityTokenResponse rstr= (RequestSecurityTokenResponse) parentXMLObject;
        if (childXMLObject instanceof Authenticator) {
            Authenticator authenticator= (Authenticator) childXMLObject;
            rstr.setAuthenticator(authenticator);
        }
        else if (childXMLObject instanceof RequestedAttachedReference) {
            RequestedAttachedReference requestedAttachedReference= (RequestedAttachedReference) childXMLObject;
            rstr.setRequestedAttachedReference(requestedAttachedReference);
        }
        else if (childXMLObject instanceof RequestedUnattachedReference) {
            RequestedUnattachedReference requestedUnattachedReference= (RequestedUnattachedReference) childXMLObject;
            rstr.setRequestedUnattachedReference(requestedUnattachedReference);
        }
        else if (childXMLObject instanceof RequestedProofToken) {
            RequestedProofToken requestedProofToken= (RequestedProofToken) childXMLObject;
            rstr.setRequestedProofToken(requestedProofToken);
        }
        else if (childXMLObject instanceof RequestedSecurityToken) {
            RequestedSecurityToken requestedSecurityToken= (RequestedSecurityToken) childXMLObject;
            rstr.setRequestedSecurityToken(requestedSecurityToken);
        }
        else if (childXMLObject instanceof Status) {
            Status status= (Status) childXMLObject;
            rstr.setStatus(status);
        }
        else {
            // common elements
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }

}
