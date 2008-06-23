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

import org.opensaml.ws.wstrust.RequestSecurityTokenResponse;
import org.opensaml.ws.wstrust.RequestSecurityTokenResponseCollection;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.util.AttributeMap;

/**
 * RequestSecurityTokenResponseCollectionImpl
 * 
 * @see RequestSecurityTokenResponseCollection
 * 
 */
public class RequestSecurityTokenResponseCollectionImpl extends AbstractWSTrustObject implements
        RequestSecurityTokenResponseCollection {

    /** xs:anyAttribute for this element. */
    private AttributeMap anyAttributes_;

    /** The list of &lt;wst:RequestSecurityTokenResponse&gt; child elements */
    private List<RequestSecurityTokenResponse> requestSecurityTokenResponses_ = null;

    /**
     * Constructor.
     * 
     * @param namespaceURI The namespace of the element
     * @param elementLocalName The local name of the element
     * @param namespacePrefix The namespace prefix of the element
     * 
     */
    public RequestSecurityTokenResponseCollectionImpl(String namespaceURI, String elementLocalName,
            String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        anyAttributes_ = new AttributeMap(this);
        requestSecurityTokenResponses_ = new ArrayList<RequestSecurityTokenResponse>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.RequestSecurityTokenResponseCollection#getRequestSecurityTokenResponses()
     */
    public List<RequestSecurityTokenResponse> getRequestSecurityTokenResponses() {
        return requestSecurityTokenResponses_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.RequestSecurityTokenResponseCollection#setRequestSecurityTokenResponses(java.util.List)
     */
    public void setRequestSecurityTokenResponses(List<RequestSecurityTokenResponse> requestSecurityTokenResponses) {
        requestSecurityTokenResponses_ = prepareForAssignment(requestSecurityTokenResponses_,
                requestSecurityTokenResponses);
    }

    /**
     * Returns the list of {@link RequestSecurityTokenResponse} child elements.
     * <p>
     * {@inheritDoc}
     */
    public List<XMLObject> getOrderedChildren() {
        List<XMLObject> children = new ArrayList<XMLObject>();
        for (XMLObject rstr : requestSecurityTokenResponses_) {
            children.add(rstr);
        }
        return Collections.unmodifiableList(children);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.xml.AttributeExtensibleXMLObject#getUnknownAttributes()
     */
    public AttributeMap getUnknownAttributes() {
        return anyAttributes_;
    }

}
