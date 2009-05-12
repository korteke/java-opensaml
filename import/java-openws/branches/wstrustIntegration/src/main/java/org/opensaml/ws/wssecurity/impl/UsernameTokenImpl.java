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

import javax.xml.namespace.QName;

import org.opensaml.ws.wssecurity.Created;
import org.opensaml.ws.wssecurity.Iteration;
import org.opensaml.ws.wssecurity.Nonce;
import org.opensaml.ws.wssecurity.Password;
import org.opensaml.ws.wssecurity.Salt;
import org.opensaml.ws.wssecurity.Username;
import org.opensaml.ws.wssecurity.UsernameToken;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.util.AttributeMap;
import org.opensaml.xml.util.IndexedXMLObjectChildrenList;

/**
 * Implementation of {@link UsernameToken}.
 */
public class UsernameTokenImpl extends AbstractWSSecurityObject implements UsernameToken {

    /** The &lt;wsu:Id&gt; attribute value. */
    private String id;

    /** The &lt;wsu:Created&gt; child element. */
    private Created created;

    /** The &lt;wsse:Nonce&gt; child element. */
    private Nonce nonce;

    /** The &lt;wsse:Password&gt; child element. */
    private Password password;

    /** The &lt;wsse:Username&gt; child element. */
    private Username username;

    /** The &lt;wsse11:Salt&gt; child element. */
    private Salt salt;

    /** The &lt;wsse11:Iteration&gt; child element. */
    private Iteration iteration;
    
    /** Wildcard attributes. */
    private AttributeMap unknownAttributes;
    
    /** Wildcard child elements. */
    private IndexedXMLObjectChildrenList<XMLObject> unknownChildren;

    /**
     * Constructor.
     * 
     * @param namespaceURI namespace of the element
     * @param elementLocalName name of the element
     * @param namespacePrefix namespace prefix of the element
     */
    public UsernameTokenImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        unknownAttributes = new AttributeMap(this);
        unknownChildren = new IndexedXMLObjectChildrenList<XMLObject>(this);
    }

    /** {@inheritDoc} */
    public Created getCreated() {
        return created;
    }

    /** {@inheritDoc} */
    public Nonce getNonce() {
        return nonce;
    }

    /** {@inheritDoc} */
    public Password getPassword() {
        return password;
    }

    /** {@inheritDoc} */
    public Username getUsername() {
        return username;
    }

    /** {@inheritDoc} */
    public void setCreated(Created newCreated) {
        created = prepareForAssignment(created, newCreated);
    }

    /** {@inheritDoc} */
    public void setNonce(Nonce newNonce) {
        nonce = prepareForAssignment(nonce, newNonce);

    }

    /** {@inheritDoc} */
    public void setPassword(Password newPassword) {
        password = prepareForAssignment(password, newPassword);
    }

    /** {@inheritDoc} */
    public void setUsername(Username newUsername) {
        username = prepareForAssignment(username, newUsername);
    }

    /** {@inheritDoc} */
    public Iteration getIteration() {
        return iteration;
    }

    /** {@inheritDoc} */
    public Salt getSalt() {
        return salt;
    }

    /** {@inheritDoc} */
    public void setIteration(Iteration newIteration) {
        iteration = prepareForAssignment(iteration, newIteration);
    }

    /** {@inheritDoc} */
    public void setSalt(Salt newSalt) {
        salt = prepareForAssignment(salt, newSalt);
    }

    /** {@inheritDoc} */
    public String getId() {
        return id;
    }

    /** {@inheritDoc} */
    public void setId(String newId) {
        String oldId = id;
        id = prepareForAssignment(id, newId);
        registerOwnID(oldId, id);
    }
    
    /** {@inheritDoc} */
    public AttributeMap getUnknownAttributes() {
        return unknownAttributes;
    }

    /** {@inheritDoc} */
    public List<XMLObject> getUnknownXMLObjects() {
        return unknownChildren;
    }

    /** {@inheritDoc} */
    public List<XMLObject> getUnknownXMLObjects(QName typeOrName) {
        return (List<XMLObject>) unknownChildren.subList(typeOrName);
    }
    
    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        ArrayList<XMLObject> children = new ArrayList<XMLObject>();
        if (username != null) {
            children.add(username);
        }
        if (password != null) {
            children.add(password);
        }
        if (nonce != null) {
            children.add(nonce);
        }
        if (salt != null) {
            children.add(salt);
        }
        if (iteration != null) {
            children.add(iteration);
        }
        if (created != null) {
            children.add(created);
        }
        
        if (!getUnknownXMLObjects().isEmpty()) {
            children.addAll(getUnknownXMLObjects());
        }
        return Collections.unmodifiableList(children);
    }

}
