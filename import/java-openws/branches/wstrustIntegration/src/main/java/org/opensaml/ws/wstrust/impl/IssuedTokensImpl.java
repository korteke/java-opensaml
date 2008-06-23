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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opensaml.ws.wstrust.IssuedTokens;
import org.opensaml.ws.wstrust.RequestSecurityTokenResponse;
import org.opensaml.xml.XMLObject;

/**
 * IssuedTokensImpl
 * 
 */
public class IssuedTokensImpl extends AbstractWSTrustObject implements IssuedTokens {

    /** List of RequestSecurityTokenReponse */
    private List<RequestSecurityTokenResponse> requestSecurityTokenResponses_ = null;

    /**
     * Constructor.
     * 
     * @param namespaceURI The namespace of the element
     * @param elementLocalName The local name of the element
     * @param namespacePrefix The namespace prefix of the element
     */
    public IssuedTokensImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        requestSecurityTokenResponses_ = new ArrayList<RequestSecurityTokenResponse>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.IssuedTokens#getSecurityTokenResponses()
     */
    public List<RequestSecurityTokenResponse> getRequestSecurityTokenResponses() {
        return requestSecurityTokenResponses_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.IssuedTokens#setSecurityTokenResponses(java.util.List)
     */
    public void setRequestSecurityTokenResponses(List<RequestSecurityTokenResponse> requestSecurityTokenResponses) {
        requestSecurityTokenResponses_ = prepareForAssignment(requestSecurityTokenResponses_,
                requestSecurityTokenResponses);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.impl.AbstractWSTrustObject#getOrderedChildren()
     */
    @Override
    public List<XMLObject> getOrderedChildren() {
        List<XMLObject> rstrs = new ArrayList<XMLObject>(requestSecurityTokenResponses_.size());
        for (XMLObject rstr : requestSecurityTokenResponses_) {
            rstrs.add(rstr);
        }
        return Collections.unmodifiableList(rstrs);
    }

}
