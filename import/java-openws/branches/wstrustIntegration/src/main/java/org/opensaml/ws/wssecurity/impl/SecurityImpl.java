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

package org.opensaml.ws.wssecurity.impl;

import org.opensaml.ws.wssecurity.Security;
import org.opensaml.xml.AbstractExtensibleXMLObject;
import org.opensaml.xml.schema.XSBooleanValue;

/**
 * SecurityImpl implements the &lt;wsse:Security&gt; header
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class SecurityImpl extends AbstractExtensibleXMLObject implements Security {

    /** The wsse:Security/@(S11|S12):mustUnderstand attribute */
    private XSBooleanValue mustUnderstand_ = null;

    /** The wsse:Security/@S12:role attribute */
    private String role_ = null;

    /** The wsse:Security/@S11:actor attribute */
    private String actor_ = null;

    /**
     * Constructor.
     * <p>
     * {@inheritDoc}
     */
    public SecurityImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /**
     * {@inheritDoc}
     */
    public XSBooleanValue getMustUnderstand() {
        return mustUnderstand_;
    }

    /**
     * {@inheritDoc}
     */
    public void setMustUnderstand(XSBooleanValue mustUnderstand) {
        mustUnderstand_ = prepareForAssignment(mustUnderstand_, mustUnderstand);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.Security#getActor()
     */
    public String getActor() {
        return actor_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.Security#getRole()
     */
    public String getRole() {
        return role_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.Security#setActor(java.lang.String)
     */
    public void setActor(String actor) {
        actor_ = prepareForAssignment(actor_, actor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.Security#setRole(java.lang.String)
     */
    public void setRole(String role) {
        role_ = prepareForAssignment(role_, role);
    }

}
