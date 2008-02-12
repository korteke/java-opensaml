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

import org.opensaml.ws.wstrust.RequestSecurityToken;
import org.opensaml.ws.wstrust.RequestSecurityTokenCollection;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.signature.Signature;

/**
 * RequestSecurityTokenCollectionImpl
 * 
 * @see RequestSecurityTokenCollection
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class RequestSecurityTokenCollectionImpl extends AbstractWSTrustObject implements RequestSecurityTokenCollection {

    /** The list of &lt;wst:RequestSecurityToken&gt; child elements */
    private List<RequestSecurityToken> requestSecurityTokens_ = null;

    /**
     * Constructor.
     * 
     * @param namespaceURI The namespace of the element
     * @param elementLocalName The local name of the element
     * @param namespacePrefix The namespace prefix of the element
     */
    public RequestSecurityTokenCollectionImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        requestSecurityTokens_ = new ArrayList<RequestSecurityToken>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.RequestSecurityTokenCollection#getRequestSecurityTokens()
     */
    public List<RequestSecurityToken> getRequestSecurityTokens() {
        return requestSecurityTokens_;
    }

    /**
     * Returns the list of {@link RequestSecurityToken} child elements and the {@link Signature} child element.
     * 
     * {@inheritDoc}
     */
    public List<XMLObject> getOrderedChildren() {
        List<XMLObject> children = new ArrayList<XMLObject>();
        for (XMLObject rst : requestSecurityTokens_) {
            children.add(rst);
        }
        // if (getSignature() != null) {
        // children.add(getSignature());
        // }
        return Collections.unmodifiableList(children);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.RequestSecurityTokenCollection#setRequestSecurityTokens(java.util.List)
     */
    public void setRequestSecurityTokens(List<RequestSecurityToken> requestSecurityTokens) {
        requestSecurityTokens_ = prepareForAssignment(requestSecurityTokens_, requestSecurityTokens);
    }

}
