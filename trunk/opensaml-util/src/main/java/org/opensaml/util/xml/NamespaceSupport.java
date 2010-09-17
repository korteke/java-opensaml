/*
 * Copyright 2010 University Corporation for Advanced Internet Development, Inc.
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

package org.opensaml.util.xml;

import org.opensaml.util.Assert;
import org.opensaml.util.ObjectSupport;
import org.opensaml.util.StringSupport;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/** Set of helper methods for working with DOM namespaces. */
public final class NamespaceSupport {

    /** Constructor. */
    private NamespaceSupport() {
    }

    /**
     * Adds a namespace declaration (xmlns:) attribute to the given element.
     * 
     * @param element the element to add the attribute to
     * @param namespaceURI the URI of the namespace
     * @param prefix the prefix for the namespace
     */
    public static void appendNamespaceDeclaration(final Element element, final String namespaceURI, final String prefix) {
        Assert.isNotNull(element, "Element may not be null");

        final String nsURI = StringSupport.trimOrNull(namespaceURI);
        final String nsPrefix = StringSupport.trimOrNull(prefix);

        // This results in xmlns="" being emitted, which seems wrong.
        if (nsURI == null && nsPrefix == null) {
            return;
        }

        String attributeName;
        if (nsPrefix == null) {
            attributeName = XmlConstants.XMLNS_PREFIX;
        } else {
            attributeName = XmlConstants.XMLNS_PREFIX + ":" + nsPrefix;
        }

        String attributeValue;
        if (nsURI == null) {
            attributeValue = "";
        } else {
            attributeValue = nsURI;
        }

        element.setAttributeNS(XmlConstants.XMLNS_NS, attributeName, attributeValue);
    }

    /**
     * Looks up the namespace URI associated with the given prefix starting at the given element. This method differs
     * from the {@link Node#lookupNamespaceURI(java.lang.String)} in that it only those namespaces declared by an xmlns
     * attribute are inspected. The Node method also checks the namespace a particular node was created in by way of a
     * call like {@link org.w3c.dom.Document#createElementNS(java.lang.String, java.lang.String)} even if the resulting
     * element doesn't have an namespace delcaration attribute.
     * 
     * @param startingElement the starting element
     * @param stopingElement the ancestor of the starting element that serves as the upper-bound, inclusive, for the
     *            search
     * @param prefix the prefix to look up
     * 
     * @return the namespace URI for the given prefer or null
     */
    public static String lookupNamespaceURI(final Element startingElement, final Element stopingElement,
            final String prefix) {
        Assert.isNotNull(startingElement, "Starting element may not be null");

        // This code is a modified version of the lookup code within Xerces
        if (startingElement.hasAttributes()) {
            final NamedNodeMap map = startingElement.getAttributes();
            final int length = map.getLength();
            Node attr;
            String value;
            for (int i = 0; i < length; i++) {
                attr = map.item(i);
                value = attr.getNodeValue();
                if (ObjectSupport.equals(attr.getNamespaceURI(), XmlConstants.XMLNS_NS)) {
                    // at this point we are dealing with DOM Level 2 nodes only
                    if (ObjectSupport.equals(prefix, XmlConstants.XMLNS_PREFIX)) {
                        // default namespace
                        return value;
                    } else if (ObjectSupport.equals(attr.getPrefix(), XmlConstants.XMLNS_PREFIX)
                            && ObjectSupport.equals(attr, prefix)) {
                        // non default namespace
                        return value;
                    }
                }
            }
        }

        if (startingElement != stopingElement) {
            final Element ancestor = ElementSupport.getElementAncestor(startingElement);
            if (ancestor != null) {
                return lookupNamespaceURI(ancestor, stopingElement, prefix);
            }
        }

        return null;
    }

    /**
     * Looks up the namespace URI associated with the given prefix starting at the given element. This method differs
     * from the {@link Node#lookupNamespaceURI(java.lang.String)} in that it only those namespaces declared by an xmlns
     * attribute are inspected. The Node method also checks the namespace a particular node was created in by way of a
     * call like {@link org.w3c.dom.Document#createElementNS(java.lang.String, java.lang.String)} even if the resulting
     * element doesn't have an namespace delcaration attribute.
     * 
     * @param startingElement the starting element
     * @param prefix the prefix to look up
     * 
     * @return the namespace URI for the given prefix
     */
    public static String lookupNamespaceURI(final Element startingElement, final String prefix) {
        return lookupNamespaceURI(startingElement, null, prefix);
    }

    /**
     * Looks up the namespace prefix associated with the given URI starting at the given element. This method differs
     * from the {@link Node#lookupPrefix(java.lang.String)} in that it only those namespaces declared by an xmlns
     * attribute are inspected. The Node method also checks the namespace a particular node was created in by way of a
     * call like {@link org.w3c.dom.Document#createElementNS(java.lang.String, java.lang.String)} even if the resulting
     * element doesn't have an namespace delcaration attribute.
     * 
     * @param startingElement the starting element
     * @param stopingElement the ancestor of the starting element that serves as the upper-bound, inclusive, for the
     *            search
     * @param namespaceURI the uri to look up
     * 
     * @return the prefix for the given namespace URI
     */
    public static String lookupPrefix(final Element startingElement, final Element stopingElement,
            final String namespaceURI) {
        Assert.isNotNull(startingElement, "Starting element may not be null");

        // This code is a modified version of the lookup code within Xerces
        if (startingElement.hasAttributes()) {
            final NamedNodeMap map = startingElement.getAttributes();
            final int length = map.getLength();
            Node attr;
            String localName;
            String foundNamespace;
            for (int i = 0; i < length; i++) {
                attr = map.item(i);
                if (ObjectSupport.equals(attr.getNamespaceURI(), XmlConstants.XMLNS_NS)) {
                    // DOM Level 2 nodes
                    if (ObjectSupport.equals(attr.getNodeName(), XmlConstants.XMLNS_PREFIX)
                            || (ObjectSupport.equals(attr.getPrefix(), XmlConstants.XMLNS_PREFIX))
                            && ObjectSupport.equals(attr.getNodeValue(), namespaceURI)) {

                        localName = attr.getLocalName();
                        foundNamespace = startingElement.lookupNamespaceURI(localName);
                        if (ObjectSupport.equals(foundNamespace, namespaceURI)) {
                            return localName;
                        }
                    }

                }
            }
        }

        if (startingElement != stopingElement) {
            final Element ancestor = ElementSupport.getElementAncestor(startingElement);
            if (ancestor != null) {
                return lookupPrefix(ancestor, stopingElement, namespaceURI);
            }
        }

        return null;
    }

    /**
     * Looks up the namespace prefix associated with the given URI starting at the given element. This method differs
     * from the {@link Node#lookupPrefix(java.lang.String)} in that it only those namespaces declared by an xmlns
     * attribute are inspected. The Node method also checks the namespace a particular node was created in by way of a
     * call like {@link org.w3c.dom.Document#createElementNS(java.lang.String, java.lang.String)} even if the resulting
     * element doesn't have an namespace delcaration attribute.
     * 
     * @param startingElement the starting element
     * @param namespaceURI the uri to look up
     * 
     * @return the prefix for the given namespace URI
     */
    public static String lookupPrefix(final Element startingElement, final String namespaceURI) {
        return lookupPrefix(startingElement, null, namespaceURI);
    }
}