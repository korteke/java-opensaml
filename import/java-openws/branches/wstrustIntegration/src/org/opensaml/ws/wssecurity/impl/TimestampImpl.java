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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opensaml.ws.wssecurity.Created;
import org.opensaml.ws.wssecurity.Expires;
import org.opensaml.ws.wssecurity.Timestamp;
import org.opensaml.xml.AbstractExtensibleXMLObject;
import org.opensaml.xml.XMLObject;

/**
 * Concrete implementation of {@link org.opensaml.ws.wssecurity.Timestamp}
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class TimestampImpl extends AbstractExtensibleXMLObject implements Timestamp {

    /** wsu:Timestamp/@wsu:Id attribute */
    private String id_ = null;

    /** wsu:Timestamp/wsu:Created element */
    private Created created_ = null;

    /** wsu:Timestamp/wsu:Expires element */
    private Expires expires_ = null;

    /**
     * Constructor.
     * 
     * @param namespaceURI namespace of the element
     * @param elementLocalName name of the element
     * @param namespacePrefix namespace prefix of the element
     */
    public TimestampImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.Timestamp#getCreated()
     */
    public Created getCreated() {
        return created_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.Timestamp#getExpires()
     */
    public Expires getExpires() {
        return expires_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.Timestamp#setCreated(org.opensaml.ws.wssecurity.Created)
     */
    public void setCreated(Created created) {
        created_ = prepareForAssignment(created_, created);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.Timestamp#setExpires(org.opensaml.ws.wssecurity.Expires)
     */
    public void setExpires(Expires expires) {
        expires_ = prepareForAssignment(expires_, expires);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.impl.AbstractWSSecurityObject#getOrderedChildren()
     */
    @Override
    public List<XMLObject> getOrderedChildren() {
        ArrayList<XMLObject> children = new ArrayList<XMLObject>();
        if (created_ != null) {
            children.add(created_);
        }
        if (expires_ != null) {
            children.add(expires_);
        }
        if (!getUnknownXMLObjects().isEmpty()) {
            children.addAll(getUnknownXMLObjects());
        }

        return Collections.unmodifiableList(children);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.schema.AttributedId#getID()
     */
    public String getId() {
        return id_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.schema.AttributedId#setID(java.lang.String)
     */
    public void setId(String id) {
        id_ = prepareForAssignment(id_, id);
    }

}
