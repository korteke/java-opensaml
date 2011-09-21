/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xml.util;

import java.io.Writer;
import java.util.Map;

import org.opensaml.util.StringSupport;
import org.opensaml.util.xml.NamespaceSupport;
import org.opensaml.util.xml.QNameSupport;
import org.opensaml.util.xml.SerializeSupport;
import org.opensaml.xml.parse.XMLParserException;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

/**
 * A helper class for working with W3C DOM objects.
 */
public final class XMLHelper {

    /**
     * Ensures that all the visibly used namespaces referenced by the given Element or its descendants are declared by
     * the given Element or one of its descendants.
     * 
     * <strong>NOTE:</strong> This is a very costly operation.
     * 
     * @param domElement the element to act as the root of the namespace declarations
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
        String namespacePrefix = domElement.getPrefix();

        // Check if the namespace for this element is already declared on this element
        boolean nsDeclaredOnElement = false;
        if (namespacePrefix == null) {
            nsDeclaredOnElement = domElement.hasAttributeNS(null, XMLConstants.XMLNS_PREFIX);
        } else {
            nsDeclaredOnElement = domElement.hasAttributeNS(XMLConstants.XMLNS_NS, namespacePrefix);
        }

        if (!nsDeclaredOnElement) {
            // Namspace for element was not declared on the element itself, see if the namespace is declared on
            // an ancestral element within the subtree where namespaces much be rooted
            namespaceURI = NamespaceSupport.lookupNamespaceURI(domElement, upperNamespaceSearchBound, namespacePrefix);

            if (namespaceURI == null) {
                // Namespace for the element is not declared on any ancestral nodes within the subtree where namespaces
                // must be rooted. Resolve the namespace from ancestors outside that subtree.
                namespaceURI = NamespaceSupport.lookupNamespaceURI(upperNamespaceSearchBound, null, namespacePrefix);
                if (namespaceURI != null) {
                    // Namespace resolved outside the subtree where namespaces must be declared so declare the namespace
                    // on this element (within the subtree).
                    NamespaceSupport.appendNamespaceDeclaration(domElement, namespaceURI, namespacePrefix);
                } else {
                    // Namespace couldn't be resolved from any ancestor. If the namespace prefix is null then the
                    // element is simply in the undeclared default document namespace, which is fine. If it isn't null
                    // then a namespace prefix, that hasn't properly been declared, is being used.
                    if (namespacePrefix != null) {
                        throw new XMLParserException("Unable to resolve namespace prefix " + namespacePrefix
                                + " found on element " + QNameSupport.getNodeQName(domElement));
                    }
                }
            }
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
            if (!StringSupport.isNullOrEmpty(namespacePrefix)) {
                // If it's the "xmlns" prefix then it is the namespace declaration,
                // don't try to look it up and redeclare it
                if (namespacePrefix.equals(XMLConstants.XMLNS_PREFIX)
                        || namespacePrefix.equals(XMLConstants.XML_PREFIX)) {
                    continue;
                }

                // check to see if the namespace for the prefix has already been defined within the XML fragment
                namespaceURI =
                        NamespaceSupport.lookupNamespaceURI(domElement, upperNamespaceSearchBound, namespacePrefix);
                if (namespaceURI == null) {
                    namespaceURI =
                            NamespaceSupport.lookupNamespaceURI(upperNamespaceSearchBound, null, namespacePrefix);
                    if (namespaceURI == null) {
                        throw new XMLParserException("Unable to resolve namespace prefix " + namespacePrefix
                                + " found on attribute " + QNameSupport.getNodeQName(attributeNode)
                                + " found on element " + QNameSupport.getNodeQName(domElement));
                    }

                    NamespaceSupport.appendNamespaceDeclaration(domElement, namespaceURI, namespacePrefix);
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
     * Writes a Node out to a Writer using the DOM, level 3, Load/Save serializer. The written content is encoded using
     * the encoding specified in the writer configuration.
     * 
     * @param node the node to write out
     * @param output the writer to write the XML to
     */
    public static void writeNode(Node node, Writer output) {
        writeNode(node, output, null);
    }

    /**
     * Writes a Node out to a Writer using the DOM, level 3, Load/Save serializer. The written content is encoded using
     * the encoding specified in the writer configuration.
     * 
     * @param node the node to write out
     * @param output the writer to write the XML to
     * @param serializerParams parameters to pass to the {@link DOMConfiguration} of the serializer instance, obtained
     *            via {@link LSSerializer#getDomConfig()}. May be null.
     */
    public static void writeNode(Node node, Writer output, Map<String, Object> serializerParams) {
        DOMImplementationLS domImplLS = SerializeSupport.getDomLsImplementation(node);

        LSSerializer serializer = SerializeSupport.getLsSerializer(domImplLS, serializerParams);

        LSOutput serializerOut = domImplLS.createLSOutput();
        serializerOut.setCharacterStream(output);

        serializer.write(node, serializerOut);
    }
}