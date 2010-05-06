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

import javax.xml.namespace.QName;

/** XML related constants. */
public final class XmlConstants {

    // XML
    /** XML core namespace. */
    public static final String XML_NS = "http://www.w3.org/XML/1998/namespace";

    /** XML core prefix for xml attributes. */
    public static final String XML_PREFIX = "xml";

    /** QName for the xml:base attribute. */
    public static final QName XML_BASE_ATTRIB_NAME = new QName(XML_NS, "base", XML_PREFIX);

    /** QName for the xml:id attribute. */
    public static final QName XML_ID_ATTRIB_NAME = new QName(XML_NS, "id", XML_PREFIX);
    
    /** QName for the xml:lan attribute. */
    public static final QName XML_LANG_ATTRIB_NAME = new QName(XML_NS, "lang", XML_PREFIX);
        
    /** QName for the xml:space attribute. */
    public static final QName XML_SPACE_ATTRIB_NAME = new QName(XML_NS, "space", XML_PREFIX);
    
    /**
     * A string which contains the valid delimiters for the XML Schema 'list' type. These are: space, newline, carriage
     * return, and tab.
     */
    public static final String LIST_DELIMITERS = " \n\r\t";
        
    // XML Namespace
    /** XML namespace for xmlns attributes. */
    public static final String XMLNS_NS = "http://www.w3.org/2000/xmlns/";

    /** XML namespace prefix for xmlns attributes. */
    public static final String XMLNS_PREFIX = "xmlns";

    // XML Schema
    /** XML Schema namespace. */
    public static final String XSD_NS = "http://www.w3.org/2001/XMLSchema";

    /** XML Schema QName prefix. */
    public static final String XSD_PREFIX = "xsd";

    /** XML Schema Instance namespace. */
    public static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";

    /** XML Schema Instance QName prefix. */
    public static final String XSI_PREFIX = "xsi";

    /** XML Schema instance <code>xsi:type</code> attribute QName. */
    public static final QName XSI_TYPE_ATTRIB_NAME = new QName(XSI_NS, "type", XSI_PREFIX);

    /** XML Schema instance <code>xsi:type</code> attribute QName. */
    public static final QName XSI_SCHEMA_LOCATION_ATTRIB_NAME = new QName(XSI_NS, "schemaLocation", XSI_PREFIX);

    /** XML Schema instance <code>xsi:type</code> attribute QName. */
    public static final QName XSI_NO_NAMESPACE_SCHEMA_LOCATION_ATTRIB_NAME = new QName(XSI_NS,
            "noNamespaceSchemaLocation", XSI_PREFIX);

    /** XML Schema instance <code>xsi:type</code> attribute QName. */
    public static final QName XSI_NIL_ATTRIB_NAME = new QName(XSI_NS, "nil", XSI_PREFIX);

    /** Constructor. */
    private XmlConstants() {
    }

}