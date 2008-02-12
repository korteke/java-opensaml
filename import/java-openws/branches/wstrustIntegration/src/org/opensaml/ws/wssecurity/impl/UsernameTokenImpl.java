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
import org.opensaml.ws.wssecurity.Iteration;
import org.opensaml.ws.wssecurity.Nonce;
import org.opensaml.ws.wssecurity.Password;
import org.opensaml.ws.wssecurity.Salt;
import org.opensaml.ws.wssecurity.Username;
import org.opensaml.ws.wssecurity.UsernameToken;
import org.opensaml.xml.AbstractExtensibleXMLObject;
import org.opensaml.xml.XMLObject;

/**
 * UsernameTokenImpl
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class UsernameTokenImpl extends AbstractExtensibleXMLObject implements
        UsernameToken {

    /** The &lt;wsu:Id&gt; attribute value */
    private String id_= null;

    /** The &lt;wsu:Created&gt; child element */
    private Created created_= null;

    /** The &lt;wsse:Nonce&gt; child element */
    private Nonce nonce_= null;

    /** The &lt;wsse:Password&gt; child element */
    private Password password_= null;

    /** The &lt;wsse:Username&gt; child element */
    private Username username_= null;

    /** The &lt;wsse11:Salt&gt; child element */
    private Salt salt_= null;

    /** The &lt;wsse11:Iteration&gt; child element */
    private Iteration iteration_= null;

    /**
     * Constructor.
     * <p>
     * {@inheritDoc}
     */
    public UsernameTokenImpl(String namespaceURI, String elementLocalName,
            String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.xml.security.UsernameToken#getCreated()
     */
    public Created getCreated() {
        return created_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.xml.security.UsernameToken#getNonce()
     */
    public Nonce getNonce() {
        return nonce_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.xml.security.UsernameToken#getPassword()
     */
    public Password getPassword() {
        return password_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.xml.security.UsernameToken#getUsername()
     */
    public Username getUsername() {
        return username_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.xml.security.UsernameToken#setCreated(org.glite.xml.security.Created)
     */
    public void setCreated(Created created) {
        created_= prepareForAssignment(created_, created);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.xml.security.UsernameToken#setNonce(org.glite.xml.security.Nonce)
     */
    public void setNonce(Nonce nonce) {
        nonce_= prepareForAssignment(nonce_, nonce);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.xml.security.UsernameToken#setPassword(org.glite.xml.security.Password)
     */
    public void setPassword(Password password) {
        password_= prepareForAssignment(password_, password);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glite.xml.security.UsernameToken#setUsername(org.glite.xml.security.Username)
     */
    public void setUsername(Username username) {
        username_= prepareForAssignment(username_, username);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.xml.XMLObject#getOrderedChildren()
     */
    public List<XMLObject> getOrderedChildren() {
        ArrayList<XMLObject> children= new ArrayList<XMLObject>();
        if (username_ != null) {
            children.add(username_);
        }
        if (password_ != null) {
            children.add(password_);
        }
        if (nonce_ != null) {
            children.add(nonce_);
        }
        if (salt_ != null) {
            children.add(salt_);
        }
        if (iteration_ != null) {
            children.add(iteration_);
        }
        if (created_ != null) {
            children.add(created_);
        }
        // xs:any element
        if (!getUnknownXMLObjects().isEmpty()) {
            children.addAll(getUnknownXMLObjects());
        }
        return Collections.unmodifiableList(children);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.UsernameToken#getIteration()
     */
    public Iteration getIteration() {
        return iteration_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.UsernameToken#getSalt()
     */
    public Salt getSalt() {
        return salt_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.UsernameToken#setIteration(org.opensaml.ws.wssecurity.Iteration)
     */
    public void setIteration(Iteration iteration) {
        iteration_= prepareForAssignment(iteration_, iteration);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.UsernameToken#setSalt(org.opensaml.ws.wssecurity.Salt)
     */
    public void setSalt(Salt salt) {
        salt_= prepareForAssignment(salt_, salt);
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
        id_= prepareForAssignment(id_, id);
    }

}
