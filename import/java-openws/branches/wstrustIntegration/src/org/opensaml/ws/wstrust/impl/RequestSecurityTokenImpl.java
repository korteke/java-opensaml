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

import java.util.Collections;
import java.util.List;


import org.opensaml.ws.wstrust.Claims;
import org.opensaml.ws.wstrust.RequestSecurityToken;
import org.opensaml.xml.XMLObject;

/**
 * RequestSecurityTokenImpl
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class RequestSecurityTokenImpl extends AbstractRequestSecurityTokenType
        implements RequestSecurityToken {

    /** The &lt;wst:Claims&gt; child element */
    private Claims claims_= null;

    /**
     * Constructor.
     * <p>
     * {@inheritDoc}
     */
    public RequestSecurityTokenImpl(String namespaceURI,
            String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.RequestSecurityToken#getClaims()
     */
    public Claims getClaims() {
        return claims_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wstrust.RequestSecurityToken#setClaims(org.opensaml.ws.wstrust.Claims)
     */
    public void setClaims(Claims claims) {
        claims_= prepareForAssignment(claims_, claims);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.xml.XMLObject#getOrderedChildren()
     */
    public List<XMLObject> getOrderedChildren() {
        List<XMLObject> children= getCommonChildren();
        // TODO add all possible children, in which order ???
        if (claims_ != null) {
            children.add(claims_);
        }
        return Collections.unmodifiableList(children);
    }

}
