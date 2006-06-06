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

package org.opensaml.soap.common;

import java.util.Collections;
import java.util.List;

import javolution.util.FastList;

import org.opensaml.xml.AttributeExtensibleXMLObject;
import org.opensaml.xml.ElementExtensibleXMLObject;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.util.AttributeMap;
import org.opensaml.xml.util.XMLObjectChildrenList;
import org.opensaml.xml.validation.AbstractValidatingXMLObject;

/**
 * Abstract class implementating validation and element and attribute extensibility.
 */
public class AbstractExtensibleSOAPObject extends AbstractValidatingXMLObject implements SOAPObject,
        AttributeExtensibleXMLObject, ElementExtensibleXMLObject {

    /** "Any" type children */
    private XMLObjectChildrenList<XMLObject> unknownXMLObject;

    /** Attributes of the proxied Element */
    private AttributeMap attributes;

    /**
     * Constructor
     * 
     * @param namespaceURI namespace of the element
     * @param elementLocalName name of the element
     * @param namespacePrefix namespace prefix of the element
     */
    protected AbstractExtensibleSOAPObject(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        attributes = new AttributeMap(this);
        unknownXMLObject = new XMLObjectChildrenList<XMLObject>(this);
    }

    /** {@inheritDoc } */
    public List<XMLObject> getOrderedChildren() {
        FastList<XMLObject> children = new FastList<XMLObject>();

        children.addAll(unknownXMLObject);

        return Collections.unmodifiableList(children);
    }

    /** {@inheritDoc } */
    public List<XMLObject> getUnknownXMLObjects() {
        return unknownXMLObject;
    }

    /** {@inheritDoc } */
    public AttributeMap getUnknownAttributes() {
        return attributes;
    }
}