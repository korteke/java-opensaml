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
import org.testng.Assert;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.XSInteger;
import org.opensaml.core.xml.schema.impl.XSIntegerBuilder;
import org.w3c.dom.Document;

/**
 * Unit test for {@link XSInteger}
 */
public class XSIntegerTest extends XMLObjectBaseTestCase {
    
    private String testDocumentLocation;
    private QName expectedXMLObjectQName;
    private Integer expectedValue;
    
    @BeforeMethod
    protected void setUp() throws Exception{
        testDocumentLocation = "/org/opensaml/core/xml/schema/xsInteger.xml";
        expectedXMLObjectQName = new QName("urn:example.org:foo", "bar", "foo");
        expectedValue = 1967;
    }

    /**
     * Tests Marshalling a integer type.
     * @throws MarshallingException 
     * @throws XMLParserException 
     */
    @Test
    public void testMarshall() throws MarshallingException, XMLParserException{
        XSIntegerBuilder xsintBuilder = (XSIntegerBuilder) builderFactory.getBuilder(XSInteger.TYPE_NAME);
        XSInteger xsInteger = xsintBuilder.buildObject(expectedXMLObjectQName, XSInteger.TYPE_NAME);
        xsInteger.setValue(expectedValue);
        
        Marshaller marshaller = marshallerFactory.getMarshaller(xsInteger);
        marshaller.marshall(xsInteger);
        
        Document document = parserPool.parse(XSIntegerTest.class.getResourceAsStream(testDocumentLocation));
        assertXMLEquals("Marshalled XSInteger does not match example document", document, xsInteger);
    }
    
    /**
     * Tests Marshalling a integer type.
     * 
     * @throws XMLParserException 
     * @throws UnmarshallingException 
     */
    @Test
    public void testUnmarshall() throws XMLParserException, UnmarshallingException{
        Document document = parserPool.parse(XSIntegerTest.class.getResourceAsStream(testDocumentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(document.getDocumentElement());
        XSInteger xsInteger = (XSInteger) unmarshaller.unmarshall(document.getDocumentElement());
        
        Assert.assertEquals(xsInteger.getElementQName(), expectedXMLObjectQName, "Unexpected XSInteger QName");
        Assert.assertEquals(xsInteger.getSchemaType(), XSInteger.TYPE_NAME, "Unexpected XSInteger schema type");
        Assert.assertEquals(expectedValue, xsInteger.getValue(), "Unexpected value of XSInteger");
    }
}