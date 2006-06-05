/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.soap.soap11.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.collections.map.TypedMap;
import org.opensaml.soap.soap11.Body;
import org.opensaml.soap.soap11.Envelope;
import org.opensaml.soap.soap11.Header;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.util.DOMCachingXMLObjectAwareMap;
import org.opensaml.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.xml.util.XMLObjectChildrenList;
import org.opensaml.xml.validation.AbstractValidatingXMLObject;

/**
 * Concrete implementation of {@link org.opensaml.soap.soap11.Envelope}.
 */
public class EnvelopeImpl extends AbstractValidatingXMLObject implements Envelope {
    
    /** SOAP header */
    private Header header;
    
    /** SOAP body */
    private Body body;
    
    /** "Any" type children */
    private XMLObjectChildrenList<XMLObject> unknownXMLObject;
    
    /** Attributes of the proxied Element */
    private Map<QName, String> attributes;
    
    /**
     * Constructor
     *
     * @param namespaceURI namespace of the element
     * @param elementLocalName name of the element
     * @param namespacePrefix namespace prefix of the element
     */
    protected EnvelopeImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        attributes = TypedMap.decorate(new DOMCachingXMLObjectAwareMap(this), QName.class, String.class);
        unknownXMLObject = new XMLObjectChildrenList<XMLObject>(this);
    }

    /** {@inheritDoc } */
    public Header getHeader() {
        return header;
    }

    /** {@inheritDoc } */
    public void setHeader(Header newHeader) {
        header = prepareForAssignment(header, newHeader);
    }

    /** {@inheritDoc } */
    public Body getBody() {
        return body;
    }

    /** {@inheritDoc } */
    public void setBody(Body newBody) {
        body = prepareForAssignment(body, newBody);
    }

    /** {@inheritDoc } */
    public List<XMLObject> getOrderedChildren() {
        ArrayList<XMLObject> children = new ArrayList<XMLObject>();
        
        children.add(header);
        children.add(body);
        children.addAll(unknownXMLObject);
        
        return Collections.unmodifiableList(children);
    }

    /** {@inheritDoc } */
    public List<XMLObject> getUnknownXMLObjects() {
        return unknownXMLObject;
    }

    /** {@inheritDoc } */
    public Map<QName, String> getUnknownAttributes() {
        return attributes;
    }
}