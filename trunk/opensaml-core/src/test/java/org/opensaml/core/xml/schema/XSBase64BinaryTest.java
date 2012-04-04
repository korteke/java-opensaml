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

package org.opensaml.core.xml.schema;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.AssertJUnit;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.XSBase64Binary;
import org.opensaml.core.xml.schema.impl.XSBase64BinaryBuilder;
import org.w3c.dom.Document;

/**
 * Unit test for {@link XSBase64Binary}
 */
public class XSBase64BinaryTest extends XMLObjectBaseTestCase {
    
    private String testDocumentLocation;
    private QName expectedXMLObjectQName;
    private String expectedValue;
    
    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception{
        testDocumentLocation = "/data/org/opensaml/core/xml/schema/xsBase64Binary.xml";
        expectedXMLObjectQName = new QName("urn:example.org:foo", "bar", "foo");
        expectedValue = "abcdABCDE===";
    }

    /**
     * Tests Marshalling a base64Binary type.
     * @throws MarshallingException 
     * @throws XMLParserException 
     */
    @Test
    public void testMarshall() throws MarshallingException, XMLParserException{
        XSBase64BinaryBuilder xsb64bBuilder = (XSBase64BinaryBuilder) builderFactory.getBuilder(XSBase64Binary.TYPE_NAME);
        XSBase64Binary xsb64b = xsb64bBuilder.buildObject(expectedXMLObjectQName, XSBase64Binary.TYPE_NAME);
        xsb64b.setValue(expectedValue);
        
        Marshaller marshaller = marshallerFactory.getMarshaller(xsb64b);
        marshaller.marshall(xsb64b);
        
        Document document = parserPool.parse(XSBase64BinaryTest.class.getResourceAsStream(testDocumentLocation));
        assertXMLEquals("Marshalled XSBase64Binary does not match example document", document, xsb64b);
    }
    
    /**
     * Tests Marshalling a base64Binary type.
     * 
     * @throws XMLParserException 
     * @throws UnmarshallingException 
     */
    @Test
    public void testUnmarshall() throws XMLParserException, UnmarshallingException{
        Document document = parserPool.parse(XSBase64BinaryTest.class.getResourceAsStream(testDocumentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(document.getDocumentElement());
        XSBase64Binary xsb64b = (XSBase64Binary) unmarshaller.unmarshall(document.getDocumentElement());
        
        AssertJUnit.assertEquals("Unexpected XSBase64Binary QName", expectedXMLObjectQName, xsb64b.getElementQName());
        AssertJUnit.assertEquals("Unexpected XSBase64Binary schema type", XSBase64Binary.TYPE_NAME, xsb64b.getSchemaType());
        AssertJUnit.assertEquals("Unexpected value of XSBase64Binary", xsb64b.getValue(), expectedValue);
    }
}