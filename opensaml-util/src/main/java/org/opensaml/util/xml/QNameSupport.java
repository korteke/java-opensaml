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

import org.opensaml.util.Assert;
import org.opensaml.util.StringSupport;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** Set of helper methods for working with DOM QNames. */
public final class QNameSupport {

    /** Constructor. */
    private QNameSupport() {
    }

    /**
     * Constructs a QName from a string (attribute element content) value.
     * 
     * @param qname the QName string
     * @param owningElement parent DOM element of the Node which contains the QName value
     * 
     * @return the QName respresented by the string
     */
    public static QName constructQName(final Element owningElement, final String qname) {
        Assert.isNotNull(owningElement, "Owning element may not be null");
        Assert.isNotNull(qname, "Name may not be null");

        String nsPrefix;
        String name;
        if (qname.indexOf(":") > -1) {
            final StringTokenizer qnameTokens = new StringTokenizer(qname, ":");
            nsPrefix = qnameTokens.nextToken();
            name = qnameTokens.nextToken();
        } else {
            nsPrefix = "";
            name = qname;
        }

        final String nsURI = NamespaceSupport.lookupNamespaceURI(owningElement, nsPrefix);
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
    public static QName constructQName(final String namespaceURI, final String localName, final String prefix) {
        Assert.isNotNull(localName, "Local name may not be null");
        if (StringSupport.isNullOrEmpty(prefix)) {
            return new QName(namespaceURI, localName);
        } else if (StringSupport.isNullOrEmpty(namespaceURI)) {
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
    public static QName getNodeQName(final Node domNode) {
        if (domNode != null) {
            return constructQName(domNode.getNamespaceURI(), domNode.getLocalName(), domNode.getPrefix());
        }

        return null;
    }

    /**
     * Converts a QName into a string that can be used for attribute values or element content.
     * 
     * @param qname the QName to convert to a string
     * 
     * @return the string value of the QName
     */
    public static String qnameToContentString(final QName qname) {
        Assert.isNotNull(qname, "QName may not be null");

        final StringBuffer buf = new StringBuffer();
        if (qname.getPrefix() != null) {
            buf.append(qname.getPrefix());
            buf.append(":");
        }
        buf.append(qname.getLocalPart());

        return buf.toString();
    }
}