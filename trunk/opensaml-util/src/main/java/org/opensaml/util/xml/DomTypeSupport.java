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

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.opensaml.util.Assert;
import org.opensaml.util.StringSupport;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

/** Set of helper methods for working with DOM data types. */
public final class DomTypeSupport {

    /** JAXP DatatypeFactory. */
    private static DatatypeFactory dataTypeFactory;

    /** Constructor. */
    private DomTypeSupport() {
    }

    /**
     * Converts a lexical dateTime, as defined by XML Schema 1.0, into milliseconds since the epoch.
     * 
     * @param dateTime lexical date/time, may not be null
     * 
     * @return the date/time expressed as milliseconds since the epoch
     */
    public static long dateTimeToLong(final String dateTime) {
        String trimmedString = StringSupport.trimOrNull(dateTime);
        Assert.isNotNull(trimmedString, "Lexical dateTime may not be null or empty");

        XMLGregorianCalendar calendar = dataTypeFactory.newXMLGregorianCalendar(dateTime);
        return calendar.toGregorianCalendar().getTimeInMillis();
    }

    /**
     * Converts a lexical duration, as defined by XML Schema 1.0, into milliseconds.
     * 
     * @param duration lexical duration representation
     * 
     * @return duration in milliseconds
     */
    public static long durationToLong(final String duration) {
        return dataTypeFactory.newDuration(duration).getTimeInMillis(new Date(0));
    }

    /**
     * Gets a static instance of a JAXP DatatypeFactory.
     * 
     * @return the factory or null if the factory could not be created
     */
    public static DatatypeFactory getDataTypeFactory() {
        return dataTypeFactory;
    }

    /**
     * Gets the XSI type for a given element if it has one.
     * 
     * @param e the element
     * 
     * @return the type or null
     */
    public static QName getXSIType(final Element e) {
        if (hasXSIType(e)) {
            final Attr attribute = e.getAttributeNodeNS(XmlConstants.XSI_NS, "type");
            final String attributeValue = attribute.getTextContent().trim();
            final StringTokenizer tokenizer = new StringTokenizer(attributeValue, ":");
            String prefix = null;
            String localPart;
            if (tokenizer.countTokens() > 1) {
                prefix = tokenizer.nextToken();
                localPart = tokenizer.nextToken();
            } else {
                localPart = tokenizer.nextToken();
            }

            return QNameSupport.constructQName(e.lookupNamespaceURI(prefix), localPart, prefix);
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
    public static boolean hasXSIType(final Element e) {
        if (e != null) {
            if (e.getAttributeNodeNS(XmlConstants.XSI_NS, "type") != null) {
                return true;
            }
        }

        return false;
    }

    /**
     * Converts a numerical date/time, given in milliseconds since the epoch, to a lexical dateTime defined by XML
     * Schema 1.0.
     * 
     * @param dateTime the date time to be converted
     * 
     * @return the lexical representation of the date/time
     */
    public static String longToDateTime(final long dateTime) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(dateTime);

        return dataTypeFactory.newXMLGregorianCalendar(calendar).normalize().toXMLFormat();
    }

    /**
     * Converts a duration in milliseconds to a lexical duration, as defined by XML Schema 1.0.
     * 
     * @param duration the duration
     * 
     * @return the lexical representation
     */
    public static String longToDuration(final long duration) {
        return dataTypeFactory.newDuration(duration).toString();
    }

    static {
        try {
            dataTypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("JVM is required to support XML DatatypeFactory but it does not", e);
        }
    }
}