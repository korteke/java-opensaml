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

import java.util.List;


import org.opensaml.ws.wstrust.IssuedTokens;
import org.opensaml.ws.wstrust.RequestSecurityTokenResponse;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;

/**
 * Unmarshaller for the &lt;wst:IssuedTokens&gt; element.
 * 
 * @see IssuedTokens
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class IssuedTokensUnmarshaller extends AbstractWSTrustObjectUnmarshaller {

    /**
     * Default constructor.
     * <p>
     * {@inheritDoc}
     */
    public IssuedTokensUnmarshaller() {
        super(IssuedTokens.ELEMENT_NAME.getNamespaceURI(),
              IssuedTokens.ELEMENT_NAME.getLocalPart());
    }

    /**
     * Unmarshalls the list of &lt;wst:RequestSecurityTokenResponse&gt; child
     * elements.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processChildElement(XMLObject parentXMLObject,
            XMLObject childXMLObject) throws UnmarshallingException {
        if (childXMLObject instanceof RequestSecurityTokenResponse) {
            IssuedTokens issuedTokens= (IssuedTokens) parentXMLObject;
            List<RequestSecurityTokenResponse> rstrs= issuedTokens.getRequestSecurityTokenResponses();
            RequestSecurityTokenResponse rstr= (RequestSecurityTokenResponse) childXMLObject;
            rstrs.add(rstr);
        }
        else {
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }
}
