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


import org.opensaml.ws.wstrust.RequestSecurityToken;
import org.opensaml.ws.wstrust.RequestSecurityTokenCollection;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;

/**
 * RequestSecurityTokenCollectionUnmarshaller
 * 
 * @see RequestSecurityTokenCollection
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class RequestSecurityTokenCollectionUnmarshaller extends
        AbstractWSTrustObjectUnmarshaller {

    /**
     * Default constructor.
     * <p>
     * {@inheritDoc}
     */
    public RequestSecurityTokenCollectionUnmarshaller() {
        super(RequestSecurityTokenCollection.ELEMENT_NAME.getNamespaceURI(),
              RequestSecurityTokenCollection.ELEMENT_NAME.getLocalPart());
    }

    /**
     * Unmarshalls the {@link RequestSecurityToken} child elements and add them
     * to the RST list.
     * <p>
     * {@inheritDoc}
     */
    @Override
    protected void processChildElement(XMLObject parentXMLObject,
            XMLObject childXMLObject) throws UnmarshallingException {
        if (childXMLObject instanceof RequestSecurityToken) {
            RequestSecurityTokenCollection rstc= (RequestSecurityTokenCollection) parentXMLObject;
            List<RequestSecurityToken> rsts= rstc.getRequestSecurityTokens();
            RequestSecurityToken rst= (RequestSecurityToken) childXMLObject;
            rsts.add(rst);
        }
        else {
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }
}
