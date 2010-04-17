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

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.xml.namespace.QName;

import org.opensaml.util.Strings;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/** Set of helper methods for working with DOM Attributes. */
public final class Attributes {

    /** Constructor. */
    private Attributes() {
    }

    /**
     * Adds a <code>xml:base</code> attribute to the given Element.
     * 
     * @param element the element to which to add the attribute
     * @param base the base value
     */
    public static void addXMLBase(Element element, String base) {
        Attr attr = constructAttribute(element.getOwnerDocument(), XmlConstants.XML_BASE_ATTRIB_NAME);
        attr.setValue(base);
        element.setAttributeNodeNS(attr);
    }

    /**
     * Adds a <code>xml:id</code> attribute to the given Element.
     * 
     * @param element the element to which to add the attribute
     * @param id the Id value
     */
    public static void addXMLId(Element element, String id) {
        Attr attr = constructAttribute(element.getOwnerDocument(), XmlConstants.XML_ID_ATTRIB_NAME);
        attr.setValue(id);
        element.setAttributeNodeNS(attr);
    }

    /**
     * Adds a <code>xml:lang</code> attribute to the given Element.
     * 
     * @param element the element to which to add the attribute
     * @param lang the lang value
     */
    public static void addXMLLang(Element element, String lang) {
        Attr attr = constructAttribute(element.getOwnerDocument(), XmlConstants.XML_LANG_ATTRIB_NAME);
        attr.setValue(lang);
        element.setAttributeNodeNS(attr);
    }

    /**
     * Adds a <code>xml:space</code> attribute to the given Element.
     * 
     * @param element the element to which to add the attribute
     * @param space the space value
     */
    public static void addXMLSpace(Element element, XmlSpace space) {
        Attr attr = constructAttribute(element.getOwnerDocument(), XmlConstants.XML_SPACE_ATTRIB_NAME);
        attr.setValue(space.toString());
        element.setAttributeNodeNS(attr);
    }

    /**
     * Adds a namespace declaration (xmlns:) attribute to the given element.
     * 
     * @param domElement the element to add the attribute to
     * @param namespaceURI the URI of the namespace
     * @param prefix the prefix for the namespace
     */
    public static void appendNamespaceDeclaration(Element domElement, String namespaceURI, String prefix) {
        String nsURI = Strings.trimOrNull(namespaceURI);
        String nsPrefix = Strings.trimOrNull(prefix);

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

        domElement.setAttributeNS(XmlConstants.XMLNS_NS, attributeName, attributeValue);
    }

    /**
     * Constructs an attribute owned by the given document with the given name.
     * 
     * @param owningDocument the owning document
     * @param attributeName the name of that attribute
     * 
     * @return the constructed attribute
     */
    public static Attr constructAttribute(Document owningDocument, QName attributeName) {
        return constructAttribute(owningDocument, attributeName.getNamespaceURI(), attributeName.getLocalPart(),
                attributeName.getPrefix());
    }

    /**
     * Constructs an attribute owned by the given document with the given name.
     * 
     * @param document the owning document
     * @param namespaceURI the URI for the namespace the attribute is in
     * @param localName the local name
     * @param prefix the prefix of the namespace that attribute is in
     * 
     * @return the constructed attribute
     */
    public static Attr constructAttribute(Document document, String namespaceURI, String localName, String prefix) {
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

        if (Strings.isNullOrEmpty(namespaceURI)) {
            return document.createAttributeNS(null, qualifiedName);
        } else {
            return document.createAttributeNS(namespaceURI, qualifiedName);
        }
    }

    /**
     * Gets the value of an attribute from an element.
     * 
     * @param element the element from which to retrieve the attribute value
     * @param attributeName the name of the attribute
     * 
     * @return the value of the attribute or null if the element does not have such an attribute
     */
    public static String getAttributeValue(Element element, QName attributeName) {
        return getAttributeValue(element, attributeName.getNamespaceURI(), attributeName.getLocalPart());
    }

    /**
     * Gets the value of an attribute from an element.
     * 
     * @param element the element from which to retrieve the attribute value
     * @param namespace the namespace URI of the attribute
     * @param attributeLocalName the local (unqualified) attribute name
     * 
     * @return the value of the attribute or null if the element does not have such an attribute
     */
    public static String getAttributeValue(Element element, String namespace, String attributeLocalName) {
        Attr attr = element.getAttributeNodeNS(namespace, attributeLocalName);

        if (attr == null) {
            return null;
        }

        return attr.getValue();
    }

    /**
     * Parses the attribute's value. If the value is 0 or "false" then false is returned, if the value is 1 or "true"
     * then true is returned, if the value is anything else then null returned.
     * 
     * @param attribute attribute whose value will be converted to a boolean
     * 
     * @return boolean value of the attribute or null
     */
    public static Boolean getAttributeValueAsBoolean(Attr attribute) {
        if (attribute == null) {
            return null;
        }

        String valueStr = attribute.getValue();
        if ("0".equals(valueStr) || "false".equals(valueStr)) {
            return Boolean.FALSE;
        } else if ("1".equals(valueStr) || "true".equals(valueStr)) {
            return Boolean.TRUE;
        } else {
            return null;
        }
    }

    /**
     * Gets the value of a list-type attribute as a list.
     * 
     * @param attribute attribute whose value will be turned into a list
     * 
     * @return list of values, never null
     */
    public static List<String> getAttributeValueAsList(Attr attribute) {
        if (attribute == null) {
            return Collections.emptyList();
        }
        return Strings.stringToList(attribute.getValue(), XmlConstants.LIST_DELIMITERS);
    }

    /**
     * Constructs a QName from an attributes value.
     * 
     * @param attribute the attribute with a QName value
     * 
     * @return a QName from an attributes value, or null if the given attribute is null
     */
    public static QName getAttributeValueAsQName(Attr attribute) {
        if (attribute == null || Strings.isNullOrEmpty(attribute.getValue())) {
            return null;
        }

        String attributeValue = attribute.getTextContent();
        String[] valueComponents = attributeValue.split(":");
        if (valueComponents.length == 1) {
            return QNames.constructQName(attribute.lookupNamespaceURI(null), valueComponents[0], null);
        } else {
            return QNames.constructQName(attribute.lookupNamespaceURI(valueComponents[0]), valueComponents[1],
                    valueComponents[0]);
        }
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
     * Gets the <code>xml:base</code> attribute from a given Element.
     * 
     * @param element the element from which to extract the attribute
     * 
     * @return the value of the xml:base attribute, or null if not present
     */
    public static String getXMLBase(Element element) {
        return getAttributeValue(element, XmlConstants.XML_BASE_ATTRIB_NAME);
    }

    /**
     * Gets the <code>xml:id</code> attribute from a given Element.
     * 
     * @param element the element from which to extract the attribute
     * 
     * @return the value of the xml:id attribute, or null if not present
     */
    public static String getXMLId(Element element) {
        return getAttributeValue(element, XmlConstants.XML_ID_ATTRIB_NAME);
    }

    /**
     * Gets the <code>xml:lang</code> attribute from a given Element.
     * 
     * @param element the element from which to extract the attribute
     * 
     * @return the value of the xml:lang attribute, or null if not present
     */
    public static String getXMLLang(Element element) {
        return getAttributeValue(element, XmlConstants.XML_LANG_ATTRIB_NAME);
    }

    /**
     * Gets the locale currently active for the element. This is done by looking for an xml:lang attribute and parsing
     * its content. If no xml:lang attribute is present the default locale is returned. This method only uses the
     * language primary tag, as defined by RFC3066.
     * 
     * @param element element to retrieve local information for
     * 
     * @return the active local of the element
     */
    public static Locale getXMLLangAsLocale(Element element) {
        String lang = getXMLLang(element);
        if (lang != null) {
            if (lang.contains("-")) {
                lang = lang.substring(0, lang.indexOf("-"));
            }
            return new Locale(lang.toUpperCase());
        } else {
            return Locale.getDefault();
        }
    }

    /**
     * Gets the <code>xml:space</code> attribute from a given Element.
     * 
     * @param element the element from which to extract the attribute
     * 
     * @return the value of the xml:space attribute, or null if not present
     */
    public static XmlSpace getXMLSpace(Element element) {
        return XmlSpace.valueOf(getAttributeValue(element, XmlConstants.XML_SPACE_ATTRIB_NAME));
    }

    /**
     * Marshall an attribute name and value to a DOM Element. This is particularly useful for attributes whose names
     * appear in namespace-qualified form.
     * 
     * @param attributeName the attribute name in QName form
     * @param attributeValues the attribute values
     * @param domElement the target element to which to marshall
     * @param isIDAttribute flag indicating whether the attribute being marshalled should be handled as an ID-typed
     *            attribute
     */
    public static void marshallAttribute(QName attributeName, List<String> attributeValues, Element domElement,
            boolean isIDAttribute) {
        marshallAttribute(attributeName, Strings.listToStringValue(attributeValues, " "), domElement, isIDAttribute);
    }

    /**
     * Marshall an attribute name and value to a DOM Element. This is particularly useful for attributes whose names
     * appear in namespace-qualified form.
     * 
     * @param attributeName the attribute name in QName form
     * @param attributeValue the attribute value
     * @param domElement the target element to which to marshall
     * @param isIDAttribute flag indicating whether the attribute being marshalled should be handled as an ID-typed
     *            attribute
     */
    public static void marshallAttribute(QName attributeName, String attributeValue, Element domElement,
            boolean isIDAttribute) {
        Document document = domElement.getOwnerDocument();
        Attr attribute = constructAttribute(document, attributeName);
        attribute.setValue(attributeValue);
        domElement.setAttributeNodeNS(attribute);
        if (isIDAttribute) {
            domElement.setIdAttributeNode(attribute, true);
        }
    }
}