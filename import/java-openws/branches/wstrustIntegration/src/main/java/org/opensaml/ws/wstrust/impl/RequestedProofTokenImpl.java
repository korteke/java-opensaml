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

import org.opensaml.ws.wstrust.ComputedKey;
import org.opensaml.ws.wstrust.RequestedProofToken;
import org.opensaml.xml.AbstractElementExtensibleXMLObject;
import org.opensaml.xml.XMLObject;

/**
 * RequestedProofTokenImpl
 * 
 */
public class RequestedProofTokenImpl extends AbstractElementExtensibleXMLObject implements RequestedProofToken {

    /** {@link ComputedKey} child element */
    private ComputedKey computedKey_ = null;

    /**
     * Constructor.
     * 
     * @param namespaceURI The namespace of the element
     * @param elementLocalName The local name of the element
     * @param namespacePrefix The namespace prefix of the element
     */
    public RequestedProofTokenImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.RequestedProofToken#getComputedKey()
     */
    public ComputedKey getComputedKey() {
        return computedKey_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.RequestedProofToken#setComputedKey(org.opensaml.ws.wstrust.ComputedKey)
     */
    public void setComputedKey(ComputedKey computedKey) {
        computedKey_ = prepareForAssignment(computedKey_, computedKey);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.impl.EntropyImpl#getOrderedChildren()
     */
    @Override
    public List<XMLObject> getOrderedChildren() {
        List<XMLObject> children = new ArrayList<XMLObject>();
        if (computedKey_ != null) {
            children.add(computedKey_);
        }
        // xs:any elements
        if (!getUnknownXMLObjects().isEmpty()) {
            children.addAll(getUnknownXMLObjects());
        }
        return Collections.unmodifiableList(children);
    }

}
