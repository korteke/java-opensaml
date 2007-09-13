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

package org.opensaml.xml;

import java.util.Collections;
import java.util.List;

import org.opensaml.xml.schema.XSAny;
import org.opensaml.xml.util.AttributeMap;
import org.opensaml.xml.util.XMLHelper;
import org.opensaml.xml.util.XMLObjectChildrenList;
import org.opensaml.xml.validation.AbstractValidatingXMLObject;

/**
 * An XMLObject that proxies a DOM element. This can be used represent the content of elements that do not have their
 * own XMLObject representation. Generally this would be used as a catch-all mechanism such as when working with XML
 * documents that contain content that may not be known at the time, such as elements defined in XML Schema that contain
 * &lt;any&gt; constructs.
 * 
 * @deprecated use {@link XSAny}
 */
public class ElementProxy extends AbstractValidatingXMLObject implements AttributeExtensibleXMLObject,
        ElementExtensibleXMLObject {

    /** Attributes of the proxied Element. */
    private AttributeMap attributes;

    /** Text content of the proxied Element. */
    private String textContent;

    /** Children of the proxied Element. */
    private XMLObjectChildrenList<XMLObject> children;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the proxied element is in
     * @param elementLocalName the local name of the proxied element
     * @param namespacePrefix the prefix for the given namespace
     */
    protected ElementProxy(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        attributes = new AttributeMap(this);
        children = new XMLObjectChildrenList<XMLObject>(this);
    }

    /**
     * Sets the name of the element this XMLObject is proxying. This <strong>MUST</strong> must be set before
     * marshalling can occur and would ideally be set immediately after it's built.
     * 
     * @param namespaceURI the namespace the proxied element is in
     * @param elementLocalName the local name of the proxied element
     * @param namespacePrefix the prefix for the given namespace
     */
    public void setElementQName(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super.setElementQName(XMLHelper.constructQName(namespacePrefix, elementLocalName, namespacePrefix));
    }

    /**
     * Gets the text content for the proxied DOM Element.
     * 
     * @return the text content for the proxied DOM Element
     */
    public String getTextContent() {
        return textContent;
    }

    /**
     * Sets the text content for the proxied DOM Element.
     * 
     * @param newContent the text content for the proxied DOM Element
     */
    public void setTextContent(String newContent) {
        textContent = prepareForAssignment(textContent, newContent);
    }

    /** {@inheritDoc} */
    public AttributeMap getUnknownAttributes() {
        return attributes;
    }

    /** {@inheritDoc} */
    public List<XMLObject> getUnknownXMLObjects() {
        return children;
    }

    /**
     * Gets the list of child XMLObjects in insertion order.
     * 
     * @return the list of child XMLObjects in insertion order
     */
    public List<XMLObject> getOrderedChildren() {
        return Collections.unmodifiableList(children);
    }
}