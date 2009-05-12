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

import org.opensaml.ws.wssecurity.Embedded;
import org.opensaml.ws.wssecurity.KeyIdentifier;
import org.opensaml.ws.wssecurity.Reference;
import org.opensaml.ws.wssecurity.SecurityTokenReference;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.util.AttributeMap;
import org.opensaml.xml.util.IndexedXMLObjectChildrenList;

/**
 * SecurityTokenReferenceImpl.
 * 
 */
public class SecurityTokenReferenceImpl extends AbstractWSSecurityObject implements SecurityTokenReference {

    /** The &lt;wsu:Id&gt; attribute value. */
    private String id;

    /** List of &lt;wsse:Usage&gt; attribute values. */
    private List<String> usages;

    /** The &lt;wsse11:TokenType&gt; attribute value. */
    private String tokenType;

    /** The &lt;wsse:Embedded&gt; child element. */
    private Embedded embedded;

    /** The &lt;wsse:KeyIdentifier&gt; child element. */
    private KeyIdentifier keyIdentifier;

    /** the &lt;wsse:Reference&gt; child element. */
    private Reference reference;
    
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
    public SecurityTokenReferenceImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        usages = new ArrayList<String>();
        unknownAttributes = new AttributeMap(this);
        unknownChildren = new IndexedXMLObjectChildrenList<XMLObject>(this);
    }
    

    /** {@inheritDoc} */
    public List<String> getUsages() {
        return usages;
    }

    /** {@inheritDoc} */
    public void setUsages(List<String> newUsages) {
        usages = prepareForAssignment(usages, newUsages);
    }

    /** {@inheritDoc} */
    public String getTokenType() {
        return tokenType;
    }

    /** {@inheritDoc} */
    public void setTokenType(String newTokenType) {
        tokenType = prepareForAssignment(tokenType, newTokenType);
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
    public Embedded getEmbedded() {
        return embedded;
    }
    
    /** {@inheritDoc} */
    public void setEmbedded(Embedded newEmbedded) {
        embedded = prepareForAssignment(embedded, newEmbedded);
    }

    /** {@inheritDoc} */
    public KeyIdentifier getKeyIdentifier() {
        return keyIdentifier;
    }
    
    /** {@inheritDoc} */
    public void setKeyIdentifier(KeyIdentifier newKeyIdentifier) {
        keyIdentifier = prepareForAssignment(keyIdentifier, newKeyIdentifier);
    }

    /** {@inheritDoc} */
    public Reference getReference() {
        return reference;
    }

    /** {@inheritDoc} */
    public void setReference(Reference newReference) {
        reference = prepareForAssignment(reference, newReference);
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
        List<XMLObject> children = new ArrayList<XMLObject>();
        if (reference != null) {
            children.add(reference);
        }
        if (keyIdentifier != null) {
            children.add(keyIdentifier);
        }
        if (embedded != null) {
            children.add(embedded);
        }
        if (!getUnknownXMLObjects().isEmpty()) {
            children.addAll(getUnknownXMLObjects());
        }
        return Collections.unmodifiableList(children);
    }

}
