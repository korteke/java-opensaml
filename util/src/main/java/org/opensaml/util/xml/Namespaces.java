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

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/** Set of helper methods for working with DOM namespaces. */
public final class Namespaces {

    /** Constructor. */
    private Namespaces() {
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
    public static String lookupNamespaceURI(Element startingElement, Element stopingElement, String prefix) {
        String namespaceURI;

        // This code is a modified version of the lookup code within Xerces
        if (startingElement.hasAttributes()) {
            NamedNodeMap map = startingElement.getAttributes();
            int length = map.getLength();
            for (int i = 0; i < length; i++) {
                Node attr = map.item(i);
                String attrPrefix = attr.getPrefix();
                String value = attr.getNodeValue();
                namespaceURI = attr.getNamespaceURI();
                if (namespaceURI != null && namespaceURI.equals(XmlConstants.XMLNS_NS)) {
                    // at this point we are dealing with DOM Level 2 nodes only
                    if (prefix == null && attr.getNodeName().equals(XmlConstants.XMLNS_PREFIX)) {
                        // default namespace
                        return value;
                    } else if (attrPrefix != null && attrPrefix.equals(XmlConstants.XMLNS_PREFIX)
                            && attr.getLocalName().equals(prefix)) {
                        // non default namespace
                        return value;
                    }
                }
            }
        }

        if (startingElement != stopingElement) {
            Element ancestor = Elements.getElementAncestor(startingElement);
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
    public static String lookupNamespaceURI(Element startingElement, String prefix) {
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
    public static String lookupPrefix(Element startingElement, Element stopingElement, String namespaceURI) {
        String namespace;

        // This code is a modified version of the lookup code within Xerces
        if (startingElement.hasAttributes()) {
            NamedNodeMap map = startingElement.getAttributes();
            int length = map.getLength();
            for (int i = 0; i < length; i++) {
                Node attr = map.item(i);
                String attrPrefix = attr.getPrefix();
                String value = attr.getNodeValue();
                namespace = attr.getNamespaceURI();
                if (namespace != null && namespace.equals(XmlConstants.XMLNS_NS)) {
                    // DOM Level 2 nodes
                    if (attr.getNodeName().equals(XmlConstants.XMLNS_PREFIX)
                            || (attrPrefix != null && attrPrefix.equals(XmlConstants.XMLNS_PREFIX))
                            && value.equals(namespaceURI)) {

                        String localname = attr.getLocalName();
                        String foundNamespace = startingElement.lookupNamespaceURI(localname);
                        if (foundNamespace != null && foundNamespace.equals(namespaceURI)) {
                            return localname;
                        }
                    }

                }
            }
        }

        if (startingElement != stopingElement) {
            Element ancestor = Elements.getElementAncestor(startingElement);
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
    public static String lookupPrefix(Element startingElement, String namespaceURI) {
        return lookupPrefix(startingElement, null, namespaceURI);
    }
}