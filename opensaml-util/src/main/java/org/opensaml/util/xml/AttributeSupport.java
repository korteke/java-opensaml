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

import org.opensaml.util.Assert;
import org.opensaml.util.StringSupport;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/** Set of helper methods for working with DOM Attributes. */
public final class AttributeSupport {

    /** Constructor. */
    private AttributeSupport() {
    }

    /**
     * Adds a <code>xml:base</code> attribute to the given Element.
     * 
     * @param element the element to which to add the attribute
     * @param base the base value
     */
    public static void addXMLBase(final Element element, final String base) {
        Assert.isNotNull(element, "Element may not be null");
        Assert.isNotNull(base, "base attribute value may not be null");

        final Attr attr = constructAttribute(element.getOwnerDocument(), XmlConstants.XML_BASE_ATTRIB_NAME);
        attr.setValue(base);
        element.setAttributeNodeNS(attr);
    }

    /**
     * Adds a <code>xml:id</code> attribute to the given Element.
     * 
     * @param element the element to which to add the attribute
     * @param id the Id value
     */
    public static void addXMLId(final Element element, final String id) {
        Assert.isNotNull(element, "Element may not be null");
        Assert.isNotNull(id, "id attribute value may not be null");

        final Attr attr = constructAttribute(element.getOwnerDocument(), XmlConstants.XML_ID_ATTRIB_NAME);
        attr.setValue(id);
        element.setAttributeNodeNS(attr);
    }

    /**
     * Adds a <code>xml:lang</code> attribute to the given Element.
     * 
     * @param element the element to which to add the attribute
     * @param lang the lang value
     */
    public static void addXMLLang(final Element element, final String lang) {
        Assert.isNotNull(element, "Element may not be null");
        Assert.isNotNull(lang, "lang attribute value may not be null");

        final Attr attr = constructAttribute(element.getOwnerDocument(), XmlConstants.XML_LANG_ATTRIB_NAME);
        attr.setValue(lang);
        element.setAttributeNodeNS(attr);
    }

    /**
     * Adds a <code>xml:space</code> attribute to the given Element.
     * 
     * @param element the element to which to add the attribute
     * @param space the space value
     */
    public static void addXMLSpace(final Element element, final XmlSpace space) {
        Assert.isNotNull(element, "Element may not be null");
        Assert.isNotNull(space, "space attribute value may not be null");

        final Attr attr = constructAttribute(element.getOwnerDocument(), XmlConstants.XML_SPACE_ATTRIB_NAME);
        attr.setValue(space.toString());
        element.setAttributeNodeNS(attr);
    }

    /**
     * Adds an attribute name and value to a DOM Element. This is particularly useful for attributes whose names appear
     * in namespace-qualified form.
     * 
     * @param attributeName the attribute name in QName form
     * @param attributeValues the attribute values
     * @param element the target element to which to marshall
     * @param isIDAttribute flag indicating whether the attribute being marshalled should be handled as an ID-typed
     *            attribute
     */
    public static void appendAttribute(final Element element, final QName attributeName,
            final List<String> attributeValues, final boolean isIDAttribute) {
        appendAttribute(element, attributeName, StringSupport.listToStringValue(attributeValues, " "), isIDAttribute);
    }

    /**
     * Adds an non-id attribute name and value to a DOM Element. This is particularly useful for attributes whose names
     * appear in namespace-qualified form.
     * 
     * @param attributeName the attribute name in QName form
     * @param attributeValue the attribute values
     * @param element the target element to which to marshall
     */
    public static void appendAttribute(final Element element, final QName attributeName, final String attributeValue) {
        appendAttribute(element, attributeName, attributeValue, false);
    }

    /**
     * Adds an attribute name and value to a DOM Element. This is particularly useful for attributes whose names appear
     * in namespace-qualified form.
     * 
     * @param attributeName the attribute name in QName form
     * @param attributeValue the attribute value
     * @param element the target element to which to marshall
     * @param isIDAttribute flag indicating whether the attribute being marshalled should be handled as an ID-typed
     *            attribute
     */
    public static void appendAttribute(final Element element, final QName attributeName, final String attributeValue,
            final boolean isIDAttribute) {
        Assert.isNotNull(element, "Element may not be null");
        Assert.isNotNull(attributeName, "Attribute name may not be null");
        Assert.isNotNull(attributeValue, "Attribute value may not be null");

        final Document document = element.getOwnerDocument();
        final Attr attribute = constructAttribute(document, attributeName);
        attribute.setValue(attributeValue);
        element.setAttributeNodeNS(attribute);
        if (isIDAttribute) {
            element.setIdAttributeNode(attribute, true);
        }
    }

    /**
     * Adds an attribute to the given element. The value of the attribute is the instant now + the given duration
     * expressed in XML dateTime format.
     * 
     * @param element element to which the attribute will be added, not null
     * @param attributeName name of the attribute, not null
     * @param duration duration, in milliseconds, must be greater than 0
     */
    public static void appendDateTimeAttribute(final Element element, QName attributeName, long duration) {
        appendAttribute(element, attributeName, DomTypeSupport.longToDateTime(duration));
    }

    /**
     * Adds an attribute to given element. The value of the attribute is the given duration in XML duration format.
     * 
     * @param element element to which the attribute will be added, not null
     * @param attributeName name of the attribute, not null
     * @param duration duration, in milliseconds, must be greater than 0
     */
    public static void appendDurationAttribute(final Element element, QName attributeName, long duration) {
        appendAttribute(element, attributeName, DomTypeSupport.longToDuration(duration));
    }

    /**
     * Constructs an attribute owned by the given document with the given name.
     * 
     * @param owningDocument the owning document
     * @param attributeName the name of that attribute
     * 
     * @return the constructed attribute
     */
    public static Attr constructAttribute(final Document owningDocument, final QName attributeName) {
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
    public static Attr constructAttribute(final Document document, final String namespaceURI, final String localName,
            final String prefix) {
        Assert.isNotNull(document, "Document may not null");

        final String trimmedLocalName = StringSupport.trimOrNull(localName);
        Assert.isNotNull(trimmedLocalName, "Attribute local name may not be null or empty");

        String qualifiedName;
        final String trimmedPrefix = StringSupport.trimOrNull(prefix);
        if (trimmedPrefix != null) {
            qualifiedName = trimmedPrefix + ":" + StringSupport.trimOrNull(trimmedLocalName);
        } else {
            qualifiedName = StringSupport.trimOrNull(trimmedLocalName);
        }

        if (StringSupport.isNullOrEmpty(namespaceURI)) {
            return document.createAttributeNS(null, qualifiedName);
        } else {
            return document.createAttributeNS(namespaceURI, qualifiedName);
        }
    }

    /**
     * Gets the attribute with the given name.
     * 
     * @param element element that may contain the attribute, may be null
     * @param attributeName name of the attribute, may be null
     * 
     * @return the attribute or null if the given element or attribute was null or the given attribute did not contain
     *         an attribute with the given name
     */
    public static Attr getAttribute(Element element, QName attributeName) {
        if (element == null || attributeName == null) {
            return null;
        }

        if (StringSupport.isNullOrEmpty(attributeName.getNamespaceURI())) {
            return element.getAttributeNodeNS(null, attributeName.getLocalPart());
        }
        return element.getAttributeNodeNS(attributeName.getNamespaceURI(), attributeName.getLocalPart());
    }

    /**
     * Gets the value of an attribute from an element.
     * 
     * @param element the element from which to retrieve the attribute value
     * @param attributeName the name of the attribute
     * 
     * @return the value of the attribute or null if the element does not have such an attribute
     */
    public static String getAttributeValue(final Element element, final QName attributeName) {
        if (StringSupport.isNullOrEmpty(attributeName.getNamespaceURI())) {
            return getAttributeValue(element, null, attributeName.getLocalPart());
        }
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
    public static String getAttributeValue(final Element element, final String namespace,
            final String attributeLocalName) {
        if (element == null) {
            return null;
        }

        final Attr attr = element.getAttributeNodeNS(namespace, attributeLocalName);
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
    public static Boolean getAttributeValueAsBoolean(final Attr attribute) {
        if (attribute == null) {
            return null;
        }

        final String valueStr = attribute.getValue();
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
    public static List<String> getAttributeValueAsList(final Attr attribute) {
        if (attribute == null) {
            return Collections.emptyList();
        }
        return StringSupport.stringToList(attribute.getValue(), XmlConstants.LIST_DELIMITERS);
    }

    /**
     * Constructs a QName from an attributes value.
     * 
     * @param attribute the attribute with a QName value
     * 
     * @return a QName from an attributes value, or null if the given attribute is null
     */
    public static QName getAttributeValueAsQName(final Attr attribute) {
        if (attribute == null || StringSupport.isNullOrEmpty(attribute.getValue())) {
            return null;
        }

        final String attributeValue = attribute.getTextContent();
        final String[] valueComponents = attributeValue.split(":");
        if (valueComponents.length == 1) {
            return QNameSupport.constructQName(attribute.lookupNamespaceURI(null), valueComponents[0], null);
        } else {
            return QNameSupport.constructQName(attribute.lookupNamespaceURI(valueComponents[0]), valueComponents[1],
                    valueComponents[0]);
        }
    }

    /**
     * Gets the value of a dateTime-type attribute in milliseconds since the epoch.
     * 
     * @param attribute attribute from which to extract the value, may be null
     * 
     * @return date/time in millisecond since the epoch, or null if the attribute was null
     */
    public static Long getDateTimeAttributeAsLong(final Attr attribute) {
        if (attribute == null) {
            return null;
        }

        return DomTypeSupport.dateTimeToLong(attribute.getValue());
    }

    /**
     * Gets the value of a duration-type attribute in milliseconds.
     * 
     * @param attribute attribute from which to extract the value, may be null
     * 
     * @return duration, in millisecond, or null if the attribute was null
     */
    public static Long getDurationAttributeValueAsLong(final Attr attribute) {
        if (attribute == null) {
            return null;
        }

        return DomTypeSupport.durationToLong(attribute.getValue());
    }

    /**
     * Gets the ID attribute of a DOM element.
     * 
     * @param element the DOM element
     * 
     * @return the ID attribute or null if there isn't one
     */
    public static Attr getIdAttribute(final Element element) {
        if (element == null || !element.hasAttributes()) {
            return null;
        }

        final NamedNodeMap attributes = element.getAttributes();
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
    public static String getXMLBase(final Element element) {
        return getAttributeValue(element, XmlConstants.XML_BASE_ATTRIB_NAME);
    }

    /**
     * Gets the <code>xml:id</code> attribute from a given Element.
     * 
     * @param element the element from which to extract the attribute
     * 
     * @return the value of the xml:id attribute, or null if not present
     */
    public static String getXMLId(final Element element) {
        return getAttributeValue(element, XmlConstants.XML_ID_ATTRIB_NAME);
    }

    /**
     * Gets the <code>xml:lang</code> attribute from a given Element.
     * 
     * @param element the element from which to extract the attribute
     * 
     * @return the value of the xml:lang attribute, or null if not present
     */
    public static String getXMLLang(final Element element) {
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
    public static Locale getXMLLangAsLocale(final Element element) {
        if (element == null) {
            return null;
        }

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
    public static XmlSpace getXMLSpace(final Element element) {
        return XmlSpace.valueOf(getAttributeValue(element, XmlConstants.XML_SPACE_ATTRIB_NAME));
    }

    /**
     * Checks if the given attribute has an attribute with the given name.
     * 
     * @param element element to check
     * @param name name of the attribute
     * 
     * @return true if the element has an attribute with the given name, false otherwise
     */
    public static boolean hasAttribute(final Element element, final QName name) {
        if (element == name || name == null) {
            return false;
        }

        if (StringSupport.isNullOrEmpty(name.getNamespaceURI())) {
            return element.hasAttributeNS(null, name.getLocalPart());
        }

        return element.hasAttributeNS(name.getNamespaceURI(), name.getLocalPart());
    }

    /**
     * Removes an attribute from an element.
     * 
     * @param element element from which the attribute should be removed
     * @param attributeName name of the attribute to be removed
     * 
     * @return true if the element contained the attribute and it was removed, false if the element did not contain such
     *         an attribute
     */
    public static boolean removeAttribute(final Element element, final QName attributeName) {
        if (hasAttribute(element, attributeName)) {
            if (StringSupport.isNullOrEmpty(attributeName.getNamespaceURI())) {
                element.removeAttributeNS(null, attributeName.getLocalPart());
            } else {
                element.removeAttributeNS(attributeName.getNamespaceURI(), attributeName.getLocalPart());
            }

            return true;
        }

        return false;
    }
}