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


import org.opensaml.ws.wstrust.Claims;
import org.opensaml.ws.wstrust.RequestSecurityToken;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;

/**
 * RequestSecurityTokenUnmarshaller
 * 
 * @see RequestSecurityToken
 * 
 */
public class RequestSecurityTokenUnmarshaller extends
        AbstractRequestSecurityTokenTypeUnmarshaller {

    /**
     * Default constructor.
     */
    public RequestSecurityTokenUnmarshaller() {
        super();
    }

    /**
     * Unmarshalls the additional {@link Claims} child element.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processChildElement(XMLObject parentXMLObject,
            XMLObject childXMLObject) throws UnmarshallingException {
        if (childXMLObject instanceof Claims) {
            Claims claims= (Claims) childXMLObject;
            RequestSecurityToken rst= (RequestSecurityToken) parentXMLObject;
            rst.setClaims(claims);
        }
        else {
            // common elements
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }
}
