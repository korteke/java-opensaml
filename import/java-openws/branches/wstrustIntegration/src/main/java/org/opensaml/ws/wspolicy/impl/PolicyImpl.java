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

package org.opensaml.ws.wspolicy.impl;

import org.opensaml.ws.wspolicy.Policy;
import org.opensaml.xml.AbstractExtensibleXMLObject;

/**
 * PolicyImpl
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class PolicyImpl extends AbstractExtensibleXMLObject implements Policy {

    /** the wsu:Id attribute value */
    private String id_ = null;

    /** the wsp:Name attribute value */
    private String name_ = null;

    /**
     * Constructor.
     * 
     * @param namespaceURI The namespace of the element
     * @param elementLocalName The local name of the element
     * @param namespacePrefix The namespace prefix of the element
     */
    protected PolicyImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.AttributedId#getId()
     */
    public String getId() {
        return id_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.AttributedId#setId(java.lang.String)
     */
    public void setId(String id) {
        id_ = prepareForAssignment(id_, id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wspolicy.Policy#getName()
     */
    public String getName() {
        return name_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wspolicy.Policy#setName(java.lang.String)
     */
    public void setName(String name) {
        name_ = prepareForAssignment(name_, name);
    }

}
