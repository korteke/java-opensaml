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


import org.opensaml.ws.wsaddressing.EndpointReference;
import org.opensaml.ws.wssecurity.SecurityTokenReference;
import org.opensaml.ws.wstrust.OnBehalfOf;
import org.opensaml.xml.AbstractElementExtensibleXMLObject;
import org.opensaml.xml.XMLObject;

/**
 * OnBehalfOfImpl
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class OnBehalfOfImpl extends AbstractElementExtensibleXMLObject
        implements OnBehalfOf {

    /** the {@link SecurityTokenReference} child element */
    private SecurityTokenReference securityTokenReference_= null;

    /** the {@link EndpointReference} child element */
    private EndpointReference endpointReference_= null;

    /**
     * Constructor.
     * <p>
     * {@inheritDoc}
     */
    public OnBehalfOfImpl(String namespaceURI, String elementLocalName,
            String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.OnBehalfOf#getEndpointReference()
     */
    public EndpointReference getEndpointReference() {
        return endpointReference_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.OnBehalfOf#setEndpointReference(org.opensaml.ws.wsaddressing.EndpointReference)
     */
    public void setEndpointReference(EndpointReference endpointReference) {
        endpointReference_= prepareForAssignment(endpointReference_,
                                                 endpointReference);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.OnBehalfOf#getSecurityTokenReference()
     */
    public SecurityTokenReference getSecurityTokenReference() {
        return securityTokenReference_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.OnBehalfOf#setSecurityTokenReference(org.opensaml.ws.wssecurity.SecurityTokenReference)
     */
    public void setSecurityTokenReference(
            SecurityTokenReference securityTokenReference) {
        securityTokenReference_= prepareForAssignment(securityTokenReference_,
                                                      securityTokenReference);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.impl.AbstractWSTrustObject#getOrderedChildren()
     */
    @Override
    public List<XMLObject> getOrderedChildren() {
        List<XMLObject> children= new ArrayList<XMLObject>();
        if (endpointReference_ != null) {
            children.add(endpointReference_);
        }
        else if (securityTokenReference_ != null) {
            children.add(securityTokenReference_);
        }
        else {
            // xs:any element: security token
            if (!getUnknownXMLObjects().isEmpty()) {
                children.addAll(getUnknownXMLObjects());
            }
        }
        return Collections.unmodifiableList(children);
    }
}
