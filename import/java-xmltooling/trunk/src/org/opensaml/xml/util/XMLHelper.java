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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Map.Entry;

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
     * Constructs a QName from an attributes value.
     * 
     * @param attribute the attribute with a QName value
     * 
     * @return a QName from an attributes value, or null if the given attribute is null
     */
    public static QName getAttributeValueAsQName(Attr attribute) {
        if (attribute == null) {
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
    public static String getQNameAsAttributeValue(QName qname){
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
        rootNamespaces(domElement, new HashMap<String, List<String>>());
    }

    /**
     * Recursively called function that ensures all the visibly used namespaces referenced by the given Element or its
     * descendants are declared if they don't appear in the list of already resolved namespaces.
     * 
     * @param domElement the Element
     * @param declaredNamespaces namespaces/prefix pairs declared on an ancestor of the given element
     * 
     * @throws XMLParserException thrown if a namespace prefix is encountered that can't be resolved to a namespace URI
     */
    private static void rootNamespaces(Element domElement, HashMap<String, List<String>> declaredNamespaces)
            throws XMLParserException {
        String namespaceURI = null;
        String namespacePrefix = null;

        // Make sure this element's namespace is rooted or has been rooted in an ancestor
        namespacePrefix = domElement.getPrefix();
        if (DatatypeHelper.isEmpty(namespacePrefix)) {
            namespaceURI = domElement.lookupNamespaceURI("");
        } else {
            namespaceURI = domElement.lookupNamespaceURI(namespacePrefix);
        }
        rootNamespace(domElement, namespaceURI, namespacePrefix, declaredNamespaces);

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
                namespaceURI = attributeNode.lookupNamespaceURI(namespacePrefix);
                if (!DatatypeHelper.isEmpty(namespaceURI)) {
                    rootNamespace(domElement, namespaceURI, namespacePrefix, declaredNamespaces);
                } else {
                    throw new XMLParserException("Unable to resolve namespace prefix " + namespacePrefix
                            + " into a valid namespace URI");
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
                rootNamespaces((Element) childNode, deepClone(declaredNamespaces));
            }
        }
    }
    
    /**
     * Checks to see if the given namespace/prefix pair has previously been resolved and, if not, declares the namespace on the given element.
     * 
     * @param domElement the element
     * @param namespaceURI the namespace URI
     * @param namespacePrefix the namespace prefix
     * @param declaredNamespaces namespace/prefix pairs already declared on some ancestor of the given element
     */
    private static void rootNamespace(Element domElement, String namespaceURI, String namespacePrefix, HashMap<String, List<String>> declaredNamespaces){
        List<String> namespaceAssociatedPrefixes;
        if (!DatatypeHelper.isEmpty(namespaceURI)) {
            if (declaredNamespaces.containsKey(namespaceURI)) {
                namespaceAssociatedPrefixes = declaredNamespaces.get(namespaceURI);
                if (!namespaceAssociatedPrefixes.contains(namespaceAssociatedPrefixes)) {
                    // Namespace was previously declared but with a different prefix
                    namespaceAssociatedPrefixes.add(namespacePrefix);
                    declaredNamespaces.put(namespaceURI, namespaceAssociatedPrefixes);
                }
            } else {
                // Namespace has never been declared before
                namespaceAssociatedPrefixes = new ArrayList<String>();
                namespaceAssociatedPrefixes.add(namespacePrefix);
                declaredNamespaces.put(namespaceURI, namespaceAssociatedPrefixes);
            }
        }
    }
    
    /**
     * Deep clones the map of declared namespaces.
     * 
     * @param declaredNamespaces the map of declared namespaces
     * 
     * @return the deep clone
     */
    private static HashMap<String, List<String>> deepClone(HashMap<String, List<String>> declaredNamespaces){
        HashMap<String, List<String>> deepClone = new HashMap<String, List<String>>();

        Entry<String, List<String>> mapEntry;
        Iterator<String> values;
        List<String> cloneValues;
        Iterator<Entry<String, List<String>>> entryItr = declaredNamespaces.entrySet().iterator();
        while(entryItr.hasNext()){
            mapEntry = entryItr.next();
            values = mapEntry.getValue().iterator();
            
            cloneValues = new ArrayList<String>();
            while(values.hasNext()){
                cloneValues.add(new String(values.next()));
            }
            
            deepClone.put(new String(mapEntry.getKey()), cloneValues);
        }
        
        return deepClone;
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