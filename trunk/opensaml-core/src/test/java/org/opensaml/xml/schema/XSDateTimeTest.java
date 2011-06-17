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

package org.opensaml.xml.schema;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.xml.XMLObjectBaseTestCase;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.schema.impl.XSDateTimeBuilder;
import org.w3c.dom.Document;

/**
 * Unit test for {@link XSDateTime}
 */
public class XSDateTimeTest extends XMLObjectBaseTestCase {
    
    private QName expectedXMLObjectQName;
    private DateTime expectedValue;
    
    /** {@inheritDoc} */
    protected void setUp() throws Exception{
        super.setUp();
        expectedXMLObjectQName = new QName("urn:example.org:foo", "bar", "foo");
        expectedValue = new DateTime(2010, 04, 05, 18, 52, 42, 790, ISOChronology.getInstanceUTC());
    }

    /**
     * Tests Marshalling a dateTime type.
     * 
     * @throws MarshallingException 
     * @throws XMLParserException 
     */
    public void testMarshall() throws MarshallingException, XMLParserException{
        String testDocumentLocation = "/data/org/opensaml/xml/schema/xsDateTime-basic.xml";
        
        XSDateTimeBuilder xsdtBuilder = (XSDateTimeBuilder) builderFactory.getBuilder(XSDateTime.TYPE_NAME);
        XSDateTime xsDateTime = xsdtBuilder.buildObject(expectedXMLObjectQName, XSDateTime.TYPE_NAME);
        xsDateTime.setValue(expectedValue);
        
        Marshaller marshaller = marshallerFactory.getMarshaller(xsDateTime);
        marshaller.marshall(xsDateTime);
        
        Document document = parserPool.parse(XSDateTimeTest.class.getResourceAsStream(testDocumentLocation));
        assertEquals("Marshalled XSDateTime does not match example document", document, xsDateTime);
    }
    
    /**
     * Tests Unmarshalling a dateTime type.
     * 
     * @throws XMLParserException 
     * @throws UnmarshallingException 
     */
    public void testUnmarshall() throws XMLParserException, UnmarshallingException{
        String testDocumentLocation = "/data/org/opensaml/xml/schema/xsDateTime-basic.xml";
        
        Document document = parserPool.parse(XSDateTimeTest.class.getResourceAsStream(testDocumentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(document.getDocumentElement());
        XSDateTime xsDateTime = (XSDateTime) unmarshaller.unmarshall(document.getDocumentElement());
        
        assertEquals("Unexpected XSDate QName", expectedXMLObjectQName, xsDateTime.getElementQName());
        assertEquals("Unexpected XSDateTime schema type", XSDateTime.TYPE_NAME, xsDateTime.getSchemaType());
        // For equivalence testing of DateTime instances, need to make sure are in the same chronology
        assertEquals("Unexpected value of XSDateTime", expectedValue, 
                xsDateTime.getValue().withChronology(ISOChronology.getInstanceUTC()));
    }
    
    /**
     * Tests Unmarshalling a dateTime type in canonical form, i.e. no trailing zeros in fractional seconds.
     * 
     * @throws XMLParserException 
     * @throws UnmarshallingException 
     */
    public void testUnmarshallCanonical() throws XMLParserException, UnmarshallingException{
        String testDocumentLocation = "/data/org/opensaml/xml/schema/xsDateTime-canonical.xml";
        
        Document document = parserPool.parse(XSDateTimeTest.class.getResourceAsStream(testDocumentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(document.getDocumentElement());
        XSDateTime xsDateTime = (XSDateTime) unmarshaller.unmarshall(document.getDocumentElement());
        
        assertEquals("Unexpected XSDate QName", expectedXMLObjectQName, xsDateTime.getElementQName());
        assertEquals("Unexpected XSDateTime schema type", XSDateTime.TYPE_NAME, xsDateTime.getSchemaType());
        // For equivalence testing of DateTime instances, need to make sure are in the same chronology
        assertEquals("Unexpected value of XSDateTime", expectedValue, 
                xsDateTime.getValue().withChronology(ISOChronology.getInstanceUTC()));
    }
    
    /**
     * Tests Unmarshalling a dateTime type that has no fractional seconds.
     * 
     * @throws XMLParserException 
     * @throws UnmarshallingException 
     */
    public void testUnmarshallNoFractional() throws XMLParserException, UnmarshallingException{
        String testDocumentLocation = "/data/org/opensaml/xml/schema/xsDateTime-nofractional.xml";
        expectedValue = expectedValue.withMillisOfSecond(0);
        
        Document document = parserPool.parse(XSDateTimeTest.class.getResourceAsStream(testDocumentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(document.getDocumentElement());
        XSDateTime xsDateTime = (XSDateTime) unmarshaller.unmarshall(document.getDocumentElement());
        
        assertEquals("Unexpected XSDate QName", expectedXMLObjectQName, xsDateTime.getElementQName());
        assertEquals("Unexpected XSDateTime schema type", XSDateTime.TYPE_NAME, xsDateTime.getSchemaType());
        // For equivalence testing of DateTime instances, need to make sure are in the same chronology
        assertEquals("Unexpected value of XSDateTime", expectedValue, 
                xsDateTime.getValue().withChronology(ISOChronology.getInstanceUTC()));
    }
}