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
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.xml.schema.impl.XSStringBuilder;
import org.w3c.dom.Document;

/**
 * Unit test for {@link XSString}
 */
public class XSStringTest extends XMLObjectBaseTestCase {
    
    private String testDocumentLocation;
    private QName expectedXMLObjectQName;
    private String expectedValue;
    
    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception{
        testDocumentLocation = "/data/org/opensaml/core/xml/schema/xsString.xml";
        expectedXMLObjectQName = new QName("urn:example.org:foo", "bar", "foo");
        expectedValue = "test";
    }

    /**
     * Tests Marshalling a string type.
     * @throws MarshallingException 
     * @throws XMLParserException 
     */
    @Test
    public void testMarshall() throws MarshallingException, XMLParserException{
        XSStringBuilder xssBuilder = (XSStringBuilder) builderFactory.getBuilder(XSString.TYPE_NAME);
        XSString xsString = xssBuilder.buildObject(expectedXMLObjectQName, XSString.TYPE_NAME);
        xsString.setValue(expectedValue);
        
        Marshaller marshaller = marshallerFactory.getMarshaller(xsString);
        marshaller.marshall(xsString);
        
        Document document = parserPool.parse(XSStringTest.class.getResourceAsStream(testDocumentLocation));
        assertXMLEquals("Marshalled XSString does not match example document", document, xsString);
    }
    
    /**
     * Tests Marshalling a string type.
     * 
     * @throws XMLParserException 
     * @throws UnmarshallingException 
     */
    @Test
    public void testUnmarshall() throws XMLParserException, UnmarshallingException{
        Document document = parserPool.parse(XSStringTest.class.getResourceAsStream(testDocumentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(document.getDocumentElement());
        XSString xsString = (XSString) unmarshaller.unmarshall(document.getDocumentElement());
        
        Assert.assertEquals(xsString.getElementQName(), expectedXMLObjectQName, "Unexpected XSString QName");
        Assert.assertEquals(xsString.getSchemaType(), XSString.TYPE_NAME, "Unexpected XSString schema type");
        Assert.assertEquals(expectedValue, xsString.getValue(), "Unexpected value of XSString");
    }
}