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

import org.opensaml.ws.wstrust.Authenticator;
import org.opensaml.ws.wstrust.CombinedHash;
import org.opensaml.xml.AbstractElementExtensibleXMLObject;
import org.opensaml.xml.XMLObject;

/**
 * AuthenticatorImpl
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class AuthenticatorImpl extends AbstractElementExtensibleXMLObject implements Authenticator {

    /** the wst:Authenticator/wst:CombinedHash child element */
    private CombinedHash combinedHash_ = null;

    /**
     * Constructor.
     * 
     * @param namespaceURI namespace of the element
     * @param elementLocalName name of the element
     * @param namespacePrefix namespace prefix of the element
     */
    public AuthenticatorImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.Authenticator#getCombinedHash()
     */
    public CombinedHash getCombinedHash() {
        return combinedHash_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.Authenticator#setCombinedHash(org.opensaml.ws.wstrust.CombinedHash)
     */
    public void setCombinedHash(CombinedHash combinedHash) {
        combinedHash_ = prepareForAssignment(combinedHash_, combinedHash);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.xml.AbstractElementExtensibleXMLObject#getOrderedChildren()
     */
    public List<XMLObject> getOrderedChildren() {
        ArrayList<XMLObject> children = new ArrayList<XMLObject>();
        if (combinedHash_ != null) {
            children.add(combinedHash_);
        }
        // xs:any element
        if (!getUnknownXMLObjects().isEmpty()) {
            children.addAll(getUnknownXMLObjects());
        }
        return Collections.unmodifiableList(children);
    }

}
