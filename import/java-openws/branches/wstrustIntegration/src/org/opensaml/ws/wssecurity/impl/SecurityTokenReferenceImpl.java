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


import org.opensaml.ws.wssecurity.Embedded;
import org.opensaml.ws.wssecurity.KeyIdentifier;
import org.opensaml.ws.wssecurity.Reference;
import org.opensaml.ws.wssecurity.SecurityTokenReference;
import org.opensaml.xml.AbstractExtensibleXMLObject;
import org.opensaml.xml.XMLObject;

/**
 * SecurityTokenReferenceImpl
 * 
 * @author Valery Tschopp &lt;tschopp@switch.ch&gt;
 * @version $Revision$
 */
public class SecurityTokenReferenceImpl extends AbstractExtensibleXMLObject
        implements SecurityTokenReference {

    /** The &lt;wsu:Id&gt; attribute value */
    private String id_= null;

    /** List of &lt;wsse:Usage&gt; attribute values */
    private List<String> usages_= null;

    /** The &lt;wsse11:TokenType&gt; attribute value */
    private String tokenType_= null;

    /** The &lt;wsse:Embedded&gt; child element */
    private Embedded embedded_= null;

    /** The &lt;wsse:KeyIdentifier&gt; child element */
    private KeyIdentifier keyIdentifier_= null;

    /** the &lt;wsse:Reference&gt; child element */
    private Reference reference_= null;

    /**
     * Constructor.
     * <p>
     * {@inheritDoc}
     */
    public SecurityTokenReferenceImpl(String namespaceURI,
            String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        usages_= new ArrayList<String>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.SecurityTokenReference#getEmbedded()
     */
    public Embedded getEmbedded() {
        return embedded_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.SecurityTokenReference#getKeyIdentifier()
     */
    public KeyIdentifier getKeyIdentifier() {
        return keyIdentifier_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.SecurityTokenReference#getReference()
     */
    public Reference getReference() {
        return reference_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.SecurityTokenReference#getUsages()
     */
    public List<String> getUsages() {
        return usages_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.SecurityTokenReference#setEmbedded(org.opensaml.ws.wssecurity.Embedded)
     */
    public void setEmbedded(Embedded embedded) {
        embedded_= prepareForAssignment(embedded_, embedded);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.SecurityTokenReference#setKeyIdentifier(org.opensaml.ws.wssecurity.KeyIdentifier)
     */
    public void setKeyIdentifier(KeyIdentifier keyIdentifier) {
        keyIdentifier_= prepareForAssignment(keyIdentifier_, keyIdentifier);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.SecurityTokenReference#setReference(org.opensaml.ws.wssecurity.Reference)
     */
    public void setReference(Reference reference) {
        reference_= prepareForAssignment(reference_, reference);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.SecurityTokenReference#setUsages(java.util.List)
     */
    public void setUsages(List<String> usages) {
        usages_= prepareForAssignment(usages_, usages);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.AttributedTokenType#getTokenType()
     */
    public String getTokenType() {
        return tokenType_;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.ws.wssecurity.AttributedTokenType#setTokenType(java.lang.String)
     */
    public void setTokenType(String tokenType) {
        tokenType_= prepareForAssignment(tokenType_, tokenType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opensaml.xml.XMLObject#getOrderedChildren()
     */
    public List<XMLObject> getOrderedChildren() {
        // TODO check order and eventually add ds:KeyInfo support !!!!
        List<XMLObject> children= new ArrayList<XMLObject>();
        if (reference_ != null) {
            children.add(reference_);
        }
        if (keyIdentifier_ != null) {
            children.add(keyIdentifier_);
        }
        if (embedded_ != null) {
            children.add(embedded_);
        }
        if (!getUnknownXMLObjects().isEmpty()) {
            children.addAll(getUnknownXMLObjects());
        }
        return Collections.unmodifiableList(children);
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
