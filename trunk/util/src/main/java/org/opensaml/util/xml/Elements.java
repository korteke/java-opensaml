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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.opensaml.util.Objects;
import org.opensaml.util.Strings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/** Set of helper methods for working with DOM Elements. */
public final class Elements {

    /** Constructor. */
    private Elements() {
    }

    /**
     * Adopts an element into a document if the child is not already in the document.
     * 
     * @param adoptee the element to be adopted
     * @param adopter the document into which the element is adopted
     */
    public static void adoptElement(Element adoptee, Document adopter) {
        if (!(adoptee.getOwnerDocument().equals(adopter))) {
            if (adopter.adoptNode(adoptee) == null) {
                // This can happen if the adopter and adoptee were produced by different DOM implementations
                throw new RuntimeException("DOM Element node adoption failed. This is most likely caused by the "
                        + "Element and Document being produced by different DOM implementations.");
            }
        }
    }

    /**
     * Appends the child Element to the parent Element, adopting the child Element into the parent's Document if needed.
     * 
     * @param parentElement the parent Element
     * @param childElement the child Element
     */
    public static void appendChildElement(Element parentElement, Element childElement) {
        Document parentDocument = parentElement.getOwnerDocument();
        adoptElement(childElement, parentDocument);

        parentElement.appendChild(childElement);
    }
    
    /**
     * Creates a text node with the given content and appends it as child to the given element.
     * 
     * @param domElement the element to recieve the text node
     * @param textContent the content for the text node
     */
    public static void appendTextContent(Element domElement, String textContent) {
        if (textContent == null) {
            return;
        }
        Document parentDocument = domElement.getOwnerDocument();
        Text textNode = parentDocument.createTextNode(textContent);
        domElement.appendChild(textNode);
    }

    /**
     * Constructs an element, rooted in the given document, with the given name.
     * 
     * @param document the document containing the element
     * @param elementName the name of the element, must contain a local name, may contain a namespace URI and prefix
     * 
     * @return the element
     */
    public static Element constructElement(Document document, QName elementName) {
        return constructElement(document, elementName.getNamespaceURI(), elementName.getLocalPart(), elementName
                .getPrefix());
    }

    /**
     * Constructs an element, rooted in the given document, with the given information.
     * 
     * @param document the document containing the element
     * @param namespaceURI the URI of the namespace the element is in
     * @param localName the element's local name
     * @param prefix the prefix of the namespace the element is in
     * 
     * @return the element
     */
    public static Element constructElement(Document document, String namespaceURI, String localName, String prefix) {
        String trimmedLocalName = Strings.trimOrNull(localName);

        if (trimmedLocalName == null) {
            throw new IllegalArgumentException("Local name may not be null or empty");
        }

        String qualifiedName;
        String trimmedPrefix = Strings.trimOrNull(prefix);
        if (trimmedPrefix != null) {
            qualifiedName = trimmedPrefix + ":" + Strings.trimOrNull(trimmedLocalName);
        } else {
            qualifiedName = Strings.trimOrNull(trimmedLocalName);
        }

        if (!Strings.isNullOrEmpty(namespaceURI)) {
            return document.createElementNS(namespaceURI, qualifiedName);
        } else {
            return document.createElementNS(null, qualifiedName);
        }
    }

    /**
     * Gets the child elements of the given element in a single iteration.
     * 
     * @param root element to get the child elements of
     * 
     * @return child elements indexed by namespace qualifed tag name, never null
     */
    public static Map<QName, List<Element>> getChildElements(Element root) {
        Map<QName, List<Element>> children = new HashMap<QName, List<Element>>();
        NodeList childNodes = root.getChildNodes();

        int numOfNodes = childNodes.getLength();
        Node childNode;
        Element e;
        QName qname;
        List<Element> elements;
        for (int i = 0; i < numOfNodes; i++) {
            childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                e = (Element) childNode;
                qname = QNames.getNodeQName(e);
                elements = children.get(qname);
                if (elements == null) {
                    elements = new ArrayList<Element>();
                    children.put(qname, elements);
                }

                elements.add(e);
            }
        }

        return children;
    }

    /**
     * Gets the child nodes with the given local tag name. If you need to retrieve multiple, named, children consider
     * using {@link #getChildElements(Element)}.
     * 
     * @param root element to retrieve the children from
     * @param localName local, tag, name of the child element
     * 
     * @return list of child elements, never null
     */
    public static List<Element> getChildElementsByTagName(Element root, String localName) {
        ArrayList<Element> children = new ArrayList<Element>();
        NodeList childNodes = root.getChildNodes();

        int numOfNodes = childNodes.getLength();
        Node childNode;
        Element e;
        for (int i = 0; i < numOfNodes; i++) {
            childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                e = (Element) childNode;
                if (Objects.equals(e.getLocalName(), localName)) {
                    children.add(e);
                }
            }
        }

        return children;
    }

    /**
     * Gets the child nodes with the given namespace qualified tag name. If you need to retrieve multiple, named,
     * children consider using {@link #getChildElements(Element)}.
     * 
     * @param root element to retrieve the children from
     * @param namespaceURI namespace URI of the child element
     * @param localName local, tag, name of the child element
     * 
     * @return list of child elements, never null
     */
    public static List<Element> getChildElementsByTagNameNS(Element root, String namespaceURI, String localName) {
        ArrayList<Element> children = new ArrayList<Element>();
        NodeList childNodes = root.getChildNodes();

        int numOfNodes = childNodes.getLength();
        Node childNode;
        Element e;
        for (int i = 0; i < numOfNodes; i++) {
            childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                e = (Element) childNode;
                if (Objects.equals(e.getNamespaceURI(), namespaceURI)
                        && Objects.equals(e.getLocalName(), localName)) {
                    children.add(e);
                }
            }
        }

        return children;
    }

    /**
     * Gets the ancestor element node to the given node.
     * 
     * @param currentNode the node to retrive the ancestor for
     * 
     * @return the ancestral element node of the current node, or null
     */
    public static Element getElementAncestor(Node currentNode) {
        Node parent = currentNode.getParentNode();
        if (parent != null) {
            short type = parent.getNodeType();
            if (type == Node.ELEMENT_NODE) {
                return (Element) parent;
            }
            return getElementAncestor(parent);
        }
        return null;
    }

    /**
     * Gets the value of a list-type element as a list.
     * 
     * @param element element whose value will be turned into a list
     * 
     * @return list of values, never null
     */
    public static List<String> getElementContentAsList(Element element) {
        if (element == null) {
            return Collections.emptyList();
        }
        return Strings.stringToList(element.getTextContent(), XmlConstants.LIST_DELIMITERS);
    }

    /**
     * Constructs a QName from an element's adjacent Text child nodes.
     * 
     * @param element the element with a QName value
     * 
     * @return a QName from an element's value, or null if the given element is empty
     */
    public static QName getElementContentAsQName(Element element) {
        if (element == null) {
            return null;
        }

        String elementContent = null;
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.TEXT_NODE) {
                elementContent = Strings.trimOrNull(((Text) node).getWholeText());
                break;
            }
        }

        if (elementContent == null) {
            return null;
        }

        String[] valueComponents = elementContent.split(":");
        if (valueComponents.length == 1) {
            return QNames.constructQName(element.lookupNamespaceURI(null), valueComponents[0], null);
        } else {
            return QNames.constructQName(element.lookupNamespaceURI(valueComponents[0]), valueComponents[1],
                    valueComponents[0]);
        }
    }

    /**
     * Gets the first child Element of the node, skipping any Text nodes such as whitespace.
     * 
     * @param n The parent in which to search for children
     * @return The first child Element of n, or null if none
     */
    public static Element getFirstChildElement(Node n) {
        Node child = n.getFirstChild();
        while (child != null && child.getNodeType() != Node.ELEMENT_NODE) {
            child = child.getNextSibling();
        }

        if (child != null) {
            return (Element) child;
        } else {
            return null;
        }
    }

    /**
     * Gets the next sibling Element of the node, skipping any Text nodes such as whitespace.
     * 
     * @param n The sibling to start with
     * @return The next sibling Element of n, or null if none
     */
    public static Element getNextSiblingElement(Node n) {
        Node sib = n.getNextSibling();
        while (sib != null && sib.getNodeType() != Node.ELEMENT_NODE) {
            sib = sib.getNextSibling();
        }

        if (sib != null) {
            return (Element) sib;
        } else {
            return null;
        }
    }

}