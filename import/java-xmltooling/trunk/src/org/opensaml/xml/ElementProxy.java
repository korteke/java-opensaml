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

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opensaml.xml.util.DOMCachingXMLObjectAwareMap;
import org.opensaml.xml.util.XMLObjectChildrenList;
import org.opensaml.xml.validation.AbstractValidatingDOMCachingXMLObject;

/**
 * An XMLObject that proxies a DOM element. This can be used represent the content of elements that do not have their
 * own XMLObject representation. Generally this would be used as a catch-all mechanism such as when working with XML
 * documents that contain content that may not be known at the time, such as elements defined in XML Schema that contain
 * &lt;any&gt; constructs.
 * 
 * <strong>NOTE:</strong> the implemention of the method {@link org.opensaml.xml.XMLObject#getOrderedChildren()}
 * <strong>IS</strong> mutable and serves as the mechanism by which child XMLObjects may be added.
 */
public class ElementProxy extends AbstractValidatingDOMCachingXMLObject implements AttributeExtensibleXMLObject {

    /** Attributes of the proxied Element */
    private DOMCachingXMLObjectAwareMap<QName, String> attributes;
    
    /** Text content of the proxied Element */
    private String textContent;
    
    /** Children of the proxied Element */
    private XMLObjectChildrenList<XMLObject> children;
    
    /**
     * Constructor
     * 
     * @param namespaceURI the namespace the proxied element is in
     * @param elementLocalName the local name of the proxied element
     * @param namespacePrefix the prefix for the given namespace
     */
    protected ElementProxy(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        attributes = new DOMCachingXMLObjectAwareMap<QName, String>(this);
        children = new XMLObjectChildrenList<XMLObject>(this);
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

    /*
     * @see org.opensaml.xml.AttributeExtensibleXMLObject#getAttributes()
     */
    public Map<QName, String> getAttributes() {
        return attributes;
    }

    /**
     * Gets the list of child XMLObjects in insertion order.
     * 
     * @return the list of child XMLObjects in insertion order
     */
    public List<XMLObject> getOrderedChildren() {
        return children;
    }
}