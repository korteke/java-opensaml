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

import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

/** Set of helper methods for working with DOM data types. */
public final class Types {

    /** JAXP DatatypeFactory. */
    private static DatatypeFactory dataTypeFactory;
    
    /** Constructor. */
    private Types() {
    }

    /**
     * Converts a lexical duration, as defined by XML Schema 1.0, into milliseconds.
     * 
     * @param duration lexical duration representation
     * 
     * @return duration in milliseconds
     */
    public static long durationToLong(String duration) {
        Duration xmlDuration = getDataTypeFactory().newDuration(duration);
        return xmlDuration.getTimeInMillis(new GregorianCalendar());
    }

    /**
     * Gets a static instance of a JAXP DatatypeFactory.
     * 
     * @return the factory or null if the factory could not be created
     */
    public static DatatypeFactory getDataTypeFactory() {
        if (dataTypeFactory == null) {
            try {
                dataTypeFactory = DatatypeFactory.newInstance();
            } catch (DatatypeConfigurationException e) {
                // do nothing
            }
        }

        return dataTypeFactory;
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
            Attr attribute = e.getAttributeNodeNS(XmlConstants.XSI_NS, "type");
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

            return QNames.constructQName(e.lookupNamespaceURI(prefix), localPart, prefix);
        }

        return null;
    }

    /**
     * Checks if the given element has an xsi:type defined for it.
     * 
     * @param e the DOM element
     * 
     * @return true if there is a type, false if not
     */
    public static boolean hasXSIType(Element e) {
        if (e != null) {
            if (e.getAttributeNodeNS(XmlConstants.XSI_NS, "type") != null) {
                return true;
            }
        }

        return false;
    }

    /**
     * Converts a duration in milliseconds to a lexical duration, as defined by XML Schema 1.0.
     * 
     * @param duration the duration
     * 
     * @return the lexical representation
     */
    public static String longToDuration(long duration) {
        Duration xmlDuration = getDataTypeFactory().newDuration(duration);
        return xmlDuration.toString();
    }
}