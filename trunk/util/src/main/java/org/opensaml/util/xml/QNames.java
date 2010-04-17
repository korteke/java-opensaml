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

import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import org.opensaml.util.Objects;
import org.opensaml.util.Strings;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** Set of helper methods for working with DOM QNames. */
public final class QNames {
    
    /** Constructor. */
    private QNames() {
    }

    /**
     * Constructs a QName from a string (attribute element content) value.
     * 
     * @param qname the QName string
     * @param owningElement parent DOM element of the Node which contains the QName value
     * 
     * @return the QName respresented by the string
     */
    public static QName constructQName(String qname, Element owningElement) {
        String nsURI;
        String nsPrefix;
        String name;

        if (qname.indexOf(":") > -1) {
            StringTokenizer qnameTokens = new StringTokenizer(qname, ":");
            nsPrefix = qnameTokens.nextToken();
            name = qnameTokens.nextToken();
        } else {
            nsPrefix = "";
            name = qname;
        }

        nsURI = Namespaces.lookupNamespaceURI(owningElement, nsPrefix);
        return constructQName(nsURI, name, nsPrefix);
    }

    /**
     * Constructs a QName.
     * 
     * @param namespaceURI the namespace of the QName
     * @param localName the local name of the QName
     * @param prefix the prefix of the QName, may be null
     * 
     * @return the QName
     */
    public static QName constructQName(String namespaceURI, String localName, String prefix) {
        if (Strings.isNullOrEmpty(prefix)) {
            return new QName(namespaceURI, localName);
        } else if (Strings.isNullOrEmpty(namespaceURI)) {
            return new QName(localName);
        }

        return new QName(namespaceURI, localName, prefix);
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
     * Shortcut for checking a DOM element node's namespace and local name.
     * 
     * @param e An element to compare against
     * @param ns An XML namespace to compare
     * @param localName A local name to compare
     * @return true iff the element's local name and namespace match the parameters
     */
    public static boolean isElementNamed(Element e, String ns, String localName) {
        return e != null && Objects.equals(ns, e.getNamespaceURI())
                && Objects.equals(localName, e.getLocalName());
    }

    /**
     * Converts a QName into a string that can be used for attribute values or element content.
     * 
     * @param qname the QName to convert to a string
     * 
     * @return the string value of the QName
     */
    public static String qnameToContentString(QName qname) {
        StringBuffer buf = new StringBuffer();

        if (qname.getPrefix() != null) {
            buf.append(qname.getPrefix());
            buf.append(":");
        }
        buf.append(qname.getLocalPart());
        return buf.toString();
    }

}