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

import org.opensaml.ws.wssecurity.Created;
import org.opensaml.ws.wssecurity.Expires;
import org.opensaml.ws.wstrust.Lifetime;
import org.opensaml.xml.XMLObject;

/**
 * LifetimeImpl
 * 
 */
public class LifetimeImpl extends AbstractWSTrustObject implements Lifetime {

    /** The wsu:Created child element */
    private Created created_ = null;

    /** The wsu:Expires child element */
    private Expires expires_ = null;

    /**
     * Constructor.
     * 
     * @param namespaceURI The namespace of the element
     * @param elementLocalName The local name of the element
     * @param namespacePrefix The namespace prefix of the element
     */
    public LifetimeImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.xml.trust.Lifetime#getCreated()
     */
    public Created getCreated() {
        return created_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.xml.trust.Lifetime#getExpires()
     */
    public Expires getExpires() {
        return expires_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.xml.trust.Lifetime#setCreated(org.glite.xml.security.Created)
     */
    public void setCreated(Created created) {
        created_ = prepareForAssignment(created_, created);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.xml.trust.Lifetime#setExpires(org.glite.xml.security.Expires)
     */
    public void setExpires(Expires expires) {
        expires_ = prepareForAssignment(expires_, expires);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.xml.trust.impl.AbstractWSTrustObject#getOrderedChildren()
     */
    public List<XMLObject> getOrderedChildren() {
        ArrayList<XMLObject> children = new ArrayList<XMLObject>();
        if (created_ != null) {
            children.add(created_);
        }
        if (expires_ != null) {
            children.add(expires_);
        }
        return Collections.unmodifiableList(children);
    }

}
