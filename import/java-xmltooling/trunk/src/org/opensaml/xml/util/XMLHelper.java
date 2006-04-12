/*
 * Copyright [2005] [University Corporation for Advanced Internet Development, Inc.]
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

package org.opensaml.xml.util;

import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.parsers.FactoryConfigurationError;

import org.opensaml.xml.parse.XMLParserException;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

/**
 * A helper class for working with W3C DOM objects.
 */
public class XMLHelper {

    /**
     * Checks if the given element has an xsi:type defined for it
     * 
     * @param e the DOM element
     * 
     * @return true if there is a type, false if not
     */
    public static boolean hasXSIType(Element e) {
        if (e != null) {
            if (e.getAttributeNodeNS(XMLConstants.XSI_NS, "type") != null) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the XSI type for a given element if it has one.
     * 
     * @param e the element
     * 
     * @return the type or null
     */
    public static QName getXSIType(Element e) {
        if (hasXSIType(e)) {
            Attr attribute = e.getAttributeNodeNS(XMLConstants.XSI_NS, "type");
            String attributeValue = attribute.getTextContent().trim();
            StringTokenizer tokenizer = new StringTokenizer(attributeValue, ":");
            String prefix = null;
            String localPart;
            if (tokenizer.countTokens() > 1) {
                prefix = tokenizer.nextToken();
                localPart = tokenizer.nextToken();
            } else {
                localPart = tokenizer.nextToken();
            }

            return new QName(e.lookupNamespaceURI(prefix), localPart, prefix);
        }

        return null;
    }

    /**
     * Gets the ID attribute of a DOM element.
     * 
     * @param domElement the DOM element
     * 
     * @return the ID attribute or null if there isn't one
     */
    public static Attr getIdAttribute(Element domElement) {
        if (!domElement.hasAttributes()) {
            return null;
        }

        NamedNodeMap attributes = domElement.getAttributes();
        Attr attribute;
        for (int i = 0; i < attributes.getLength(); i++) {
            attribute = (Attr) attributes.item(i);
            if (attribute.isId()) {
                return attribute;
            }
        }

        return null;
    }

    /**
     * Gets the QName for the given DOM node.
     * 
     * @param domNode the DOM node
     * 
     * @return the QName for the element or null if the element was null
     */
    public static QName getNodeQName(Node domNode) {
        if (domNode != null) {
            return constructQName(domNode.getNamespaceURI(), domNode.getLocalName(), domNode.getPrefix());
        }

        return null;
    }
    
    /**
     * Constructs an attribute owned by the given document with the given name.
     * 
     * @param owningDocument the owning document
     * @param attributeName the name of that attribute
     * 
     * @return the constructed attribute
     * 
     * @throws IllegalArgumentException thrown if the local name is name null or empty
     */
    public static Attr constructAttribute(Document owningDocument, QName attributeName) throws IllegalArgumentException{
        return constructAttribute(owningDocument, attributeName.getNamespaceURI(), attributeName.getLocalPart(), attributeName.getPrefix());
    }
    
    /**
     * Constructs an attribute owned by the given document with the given name.
     * 
     * @param document the owning document
     * @param namespaceURI the URI fo the namespace the attribute is in
     * @param localName the local name
     * @param prefix the prefix of the namespace that attribute is in
     * 
     * @return the constructed attribute
     * 
     * @throws IllegalArgumentException thrown if the local name is name null or empty
     */
    public static Attr constructAttribute(Document document, String namespaceURI, String localName, String prefix) throws IllegalArgumentException{
        localName = DatatypeHelper.safeTrimOrNullString(localName);
        
        if(localName == null){
            throw new IllegalArgumentException("Local name may not be null or empty");
        }
        
        String qualifiedName;
        prefix = DatatypeHelper.safeTrimOrNullString(prefix);
        if(prefix != null){
            qualifiedName = prefix + ":" + DatatypeHelper.safeTrimOrNullString(localName);
        }else{
            qualifiedName = DatatypeHelper.safeTrimOrNullString(localName);
        }
        
        if(DatatypeHelper.isEmpty(namespaceURI)){
            return document.createAttribute(qualifiedName);
        }else{
            return document.createAttributeNS(namespaceURI, qualifiedName);
        }
    }

    /**
     * Constructs a QName from an attributes value.
     * 
     * @param attribute the attribute with a QName value
     * 
     * @return a QName from an attributes value, or null if the given attribute is null
     */
    public static QName getAttributeValueAsQName(Attr attribute) {
        if (attribute == null || DatatypeHelper.isEmpty(attribute.getValue())) {
            return null;
        }

        String attributeValue = attribute.getTextContent();
        String[] valueComponents = attributeValue.split(":");
        if (valueComponents.length == 1) {
            return constructQName(attribute.lookupNamespaceURI(null), valueComponents[0], null);
        } else {
            return constructQName(attribute.lookupNamespaceURI(valueComponents[0]), valueComponents[1],
                    valueComponents[0]);
        }
    }

    /**
     * Deconstructs a QName value into a string appropriate for the value of an attribute.
     * 
     * @param qname the QName value for the attribute
     * 
     * @return the QName as a string for use with an attribute value
     */
    public static String getQNameAsAttributeValue(QName qname) {
        StringBuffer buf = new StringBuffer();

        if (qname.getPrefix() != null) {
            buf.append(qname.getPrefix());
            buf.append(":");
        }
        buf.append(qname.getLocalPart());
        return buf.toString();
    }

    /**
     * Constructs a QName
     * 
     * @param namespaceURI the namespace of the QName
     * @param localName the local name of the QName
     * @param prefix the prefix of the QName, may be null
     * 
     * @return the QName
     */
    public static QName constructQName(String namespaceURI, String localName, String prefix) {
        if (DatatypeHelper.isEmpty(prefix)) {
            return new QName(namespaceURI, localName);
        } else if (DatatypeHelper.isEmpty(namespaceURI)) {
            return new QName(localName);
        }

        return new QName(namespaceURI, localName, prefix);
    }
    
    /**
     * Constructs an element, rooted in the given document, with the given name.
     * 
     * @param document the document containing the element
     * @param elementName the name of the element, must contain a local name, may contain a namespace URI and prefix
     * 
     * @return the element
     * 
     * @throws IllegalArgumentException thrown if the local name is null or empty
     */
    public static Element constructElement(Document document, QName elementName) throws IllegalArgumentException{
        return constructElement(document, elementName.getNamespaceURI(), elementName.getLocalPart(), elementName.getPrefix());
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
     * 
     * @throws IllegalArgumentException thrown if the local name is null or empty
     */
    public static Element constructElement(Document document, String namespaceURI, String localName, String prefix) throws IllegalArgumentException{
        localName = DatatypeHelper.safeTrimOrNullString(localName);
        
        if(localName == null){
            throw new IllegalArgumentException("Local name may not be null or empty");
        }
        
        String qualifiedName;
        prefix = DatatypeHelper.safeTrimOrNullString(prefix);
        if(prefix != null){
            qualifiedName = prefix + ":" + DatatypeHelper.safeTrimOrNullString(localName);
        }else{
            qualifiedName = DatatypeHelper.safeTrimOrNullString(localName);
        }
        
        if(!DatatypeHelper.isEmpty(namespaceURI)){
            return document.createElementNS(namespaceURI, qualifiedName);
        }else{
            return document.createElement(qualifiedName);
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
     * Adds a namespace decleration (xmlns:) attribute to the given element.
     * 
     * @param domElement the element to add the attribute to
     * @param namespaceURI the URI of the namespace
     * @param prefix the prefix for the namespace
     */
    public static void appendNamespaceDecleration(Element domElement, String namespaceURI, String prefix) {
        String nsURI = DatatypeHelper.safeTrimOrNullString(namespaceURI);
        String nsPrefix = DatatypeHelper.safeTrimOrNullString(prefix);

        String attributeName;
        if (nsPrefix == null) {
            attributeName = XMLConstants.XMLNS_PREFIX;
        } else {
            attributeName = XMLConstants.XMLNS_PREFIX + ":" + nsPrefix;
        }

        String attributeValue;
        if (nsURI == null) {
            attributeValue = "";
        } else {
            attributeValue = nsURI;
        }

        domElement.setAttributeNS(XMLConstants.XMLNS_NS, attributeName, attributeValue);
    }

    /**
     * Adopts an element into a document if the child is not already in the document.
     * 
     * @param adoptee the element to be adopted
     * @param adopter the document into which the element is adopted
     */
    public static void adoptElement(Element adoptee, Document adopter) {
        if (!(adoptee.getOwnerDocument().equals(adopter))) {
            adopter.adoptNode(adoptee);
        }
    }

    /**
     * Looks up the namespace URI associated with the given prefix starting at the given element. This method differs
     * from the {@link Node#lookupNamespaceURI(java.lang.String)} in that it only those namespaces declared by an xmlns
     * attribute are inspected. The Node method also checks the namespace a particular node was created in by way of a
     * call like {@link Document#createElementNS(java.lang.String, java.lang.String)} even if the resulting element
     * doesn't have an namespace delcaration attribute.
     * 
     * @param startingElement the starting element
     * @param prefix the prefix to look up
     */
    public static String lookupNamespaceURI(Element startingElement, String prefix) {
        return lookupNamespaceURI(startingElement, null, prefix);
    }

    /**
     * Looks up the namespace URI associated with the given prefix starting at the given element. This method differs
     * from the {@link Node#lookupNamespaceURI(java.lang.String)} in that it only those namespaces declared by an xmlns
     * attribute are inspected. The Node method also checks the namespace a particular node was created in by way of a
     * call like {@link Document#createElementNS(java.lang.String, java.lang.String)} even if the resulting element
     * doesn't have an namespace delcaration attribute.
     * 
     * @param startingElement the starting element
     * @param stopingElement the ancestor of the starting element that serves as the upper-bound for the search
     * @param prefix the prefix to look up
     */
    public static String lookupNamespaceURI(Element startingElement, Element stopingElement, String prefix) {
        String namespaceURI;

        if (startingElement == stopingElement) {
            return null;
        }

        // This code is a modified version of the lookup code within Xerces
        if (startingElement.hasAttributes()) {
            NamedNodeMap map = startingElement.getAttributes();
            int length = map.getLength();
            for (int i = 0; i < length; i++) {
                Node attr = map.item(i);
                String attrPrefix = attr.getPrefix();
                String value = attr.getNodeValue();
                namespaceURI = attr.getNamespaceURI();
                if (namespaceURI != null && namespaceURI.equals(XMLConstants.XMLNS_NS)) {
                    // at this point we are dealing with DOM Level 2 nodes only
                    if (prefix == null && attr.getNodeName().equals(XMLConstants.XMLNS_PREFIX)) {
                        // default namespace
                        return value;
                    } else if (attrPrefix != null && attrPrefix.equals(XMLConstants.XMLNS_PREFIX)
                            && attr.getLocalName().equals(prefix)) {
                        // non default namespace
                        return value;
                    }
                }
            }
        }

        Element ancestor = getElementAncestor(startingElement);

        if (ancestor != null) {
            return lookupNamespaceURI(ancestor, stopingElement, prefix);
        }

        return null;
    }

    /**
     * Looks up the namespace prefix associated with the given URI starting at the given element. This method differs
     * from the {@link Node#lookupPrefix(java.lang.String)} in that it only those namespaces declared by an xmlns
     * attribute are inspected. The Node method also checks the namespace a particular node was created in by way of a
     * call like {@link Document#createElementNS(java.lang.String, java.lang.String)} even if the resulting element
     * doesn't have an namespace delcaration attribute.
     * 
     * @param startingElement the starting element
     * @param namespaceURI the uri to look up
     */
    public static String lookupPrefix(Element startingElement, String namespaceURI) {
        return lookupPrefix(startingElement, null, namespaceURI);
    }

    /**
     * Looks up the namespace prefix associated with the given URI starting at the given element. This method differs
     * from the {@link Node#lookupPrefix(java.lang.String)} in that it only those namespaces declared by an xmlns
     * attribute are inspected. The Node method also checks the namespace a particular node was created in by way of a
     * call like {@link Document#createElementNS(java.lang.String, java.lang.String)} even if the resulting element
     * doesn't have an namespace delcaration attribute.
     * 
     * @param startingElement the starting element
     * @param stopingElement the ancestor of the starting element that serves as the upper-bound for the search
     * @param namespaceURI the uri to look up
     */
    public static String lookupPrefix(Element startingElement, Element stopingElement, String namespaceURI) {
        String namespace;

        if (startingElement == stopingElement) {
            return null;
        }

        // This code is a modified version of the lookup code within Xerces
        if (startingElement.hasAttributes()) {
            NamedNodeMap map = startingElement.getAttributes();
            int length = map.getLength();
            for (int i = 0; i < length; i++) {
                Node attr = map.item(i);
                String attrPrefix = attr.getPrefix();
                String value = attr.getNodeValue();
                namespace = attr.getNamespaceURI();
                if (namespace != null && namespace.equals(XMLConstants.XMLNS_NS)) {
                    // DOM Level 2 nodes
                    if (((attr.getNodeName().equals(XMLConstants.XMLNS_PREFIX)) || (attrPrefix != null && attrPrefix
                            .equals(XMLConstants.XMLNS_PREFIX))
                            && value.equals(namespaceURI))) {

                        String localname = attr.getLocalName();
                        String foundNamespace = startingElement.lookupNamespaceURI(localname);
                        if (foundNamespace != null && foundNamespace.equals(namespaceURI)) {
                            return localname;
                        }
                    }

                }
            }
        }
        Element ancestor = getElementAncestor(startingElement);

        if (ancestor != null) {
            return lookupPrefix(ancestor, stopingElement, namespaceURI);
        }
        return null;
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
     * Converts a Node into a String using the DOM, level 3, Load/Save Serializer.
     * 
     * @param node the node to be written to a string
     * 
     * @return the string representation of the node
     */
    public static String nodeToString(Node node) {
        DOMImplementation domImpl = node.getOwnerDocument().getImplementation();
        DOMImplementationLS domImplLS = (DOMImplementationLS) domImpl.getFeature("LS", "3.0");
        LSSerializer serializer = domImplLS.createLSSerializer();
        return serializer.writeToString(node);
    }

    /**
     * Ensures that all the visibly used namespaces referenced by the given Element or its descendants are declared by
     * the given Element or one of its descendants.
     * 
     * <strong>NOTE:</strong> This is a very costly operation.
     * 
     * @param domElement the element to act as the root of the namespace declerations
     * 
     * @throws XMLParserException thrown if a namespace prefix is encountered that can't be resolved to a namespace URI
     */
    public static void rootNamespaces(Element domElement) throws XMLParserException {
        rootNamespaces(domElement, domElement);
    }

    /**
     * Recursively called function that ensures all the visibly used namespaces referenced by the given Element or its
     * descendants are declared if they don't appear in the list of already resolved namespaces.
     * 
     * @param domElement the Element
     * @param upperNamespaceSearchBound the "root" element of the fragment where namespaces may be rooted
     * 
     * @throws XMLParserException thrown if a namespace prefix is encountered that can't be resolved to a namespace URI
     */
    private static void rootNamespaces(Element domElement, Element upperNamespaceSearchBound) throws XMLParserException {
        String namespaceURI = null;
        String namespacePrefix = null;

        // Make sure this element's namespace is rooted or has been rooted in an ancestor
        namespacePrefix = domElement.getPrefix();
        if (DatatypeHelper.isEmpty(namespacePrefix)) {
            namespaceURI = lookupNamespaceURI(upperNamespaceSearchBound, "");
        } else {
            namespaceURI = lookupNamespaceURI(upperNamespaceSearchBound, namespacePrefix);
        }

        if (namespaceURI == null) {
            namespaceURI = lookupNamespaceURI(upperNamespaceSearchBound, null, namespacePrefix);
            if (namespaceURI == null) {
                throw new XMLParserException("Unable to resolve namespace prefix " + namespacePrefix
                        + " found on element " + getNodeQName(domElement));
            }

            appendNamespaceDecleration(domElement, namespaceURI, namespacePrefix);
        }

        // Make sure all the attribute URIs are rooted here or have been rooted in an ancestor
        NamedNodeMap attributes = domElement.getAttributes();
        Node attributeNode;
        for (int i = 0; i < attributes.getLength(); i++) {
            namespacePrefix = null;
            namespaceURI = null;
            attributeNode = attributes.item(i);

            // Shouldn't need this check, but just to be safe, we have it
            if (attributeNode.getNodeType() != Node.ATTRIBUTE_NODE) {
                continue;
            }

            namespacePrefix = attributeNode.getPrefix();
            if (!DatatypeHelper.isEmpty(namespacePrefix)) {
                // check to see if the namespace for the prefix has already been defined within the XML fragment
                namespaceURI = lookupNamespaceURI(domElement, upperNamespaceSearchBound, namespacePrefix);
                if (namespaceURI == null) {
                    namespaceURI = lookupNamespaceURI(upperNamespaceSearchBound, null, namespacePrefix);
                    if (namespaceURI == null) {
                        throw new XMLParserException("Unable to resolve namespace prefix " + namespacePrefix
                                + " found on attribute " + getNodeQName(attributeNode) + " found on element "
                                + getNodeQName(domElement));
                    }

                    appendNamespaceDecleration(domElement, namespaceURI, namespacePrefix);
                }
            }
        }

        // Now for the child elements, we pass a copy of the resolved namespace list in order to
        // maintain proper scoping of namespaces.
        NodeList childNodes = domElement.getChildNodes();
        Node childNode;
        for (int i = 0; i < childNodes.getLength(); i++) {
            childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                rootNamespaces((Element) childNode, upperNamespaceSearchBound);
            }
        }
    }

    /**
     * Verifies that the XML parser used by the JVM supports DOM level 3 and JAXP 1.3.
     */
    public static void verifyUsableXmlParser() {
        try {
            Class.forName("javax.xml.validation.SchemaFactory");
            Element.class.getDeclaredMethod("setIdAttributeNS", new Class[] { String.class, String.class,
                    java.lang.Boolean.TYPE });
        } catch (NoSuchMethodException e) {
            throw new FactoryConfigurationError("OpenSAML requires an xml parser that supports DOM3 calls. "
                    + "Sun JAXP 1.3 has been included with this release and is strongly recommended. "
                    + "If you are using Java 1.4, make sure that you have enabled the Endorsed "
                    + "Standards Override Mechanism for this parser "
                    + "(see http://java.sun.com/j2se/1.4.2/docs/guide/standards/ for details).");
        } catch (ClassNotFoundException e) {
            throw new FactoryConfigurationError("OpenSAML requires an xml parser that supports JAXP 1.3. "
                    + "Sun JAXP 1.3 has been included with this release and is strongly recommended. "
                    + "If you are using Java 1.4, make sure that you have enabled the Endorsed "
                    + "Standards Override Mechanism for this parser "
                    + "(see http://java.sun.com/j2se/1.4.2/docs/guide/standards/ for details).");
        }
    }
}