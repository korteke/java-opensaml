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
import org.opensaml.core.xml.schema.XSQName;
import org.opensaml.core.xml.schema.impl.XSQNameBuilder;
import org.w3c.dom.Document;

/**
 * Unit test for {@link XSQName}
 */
public class XSQNameTest extends XMLObjectBaseTestCase {
    
    private String testDocumentLocation;
    private QName expectedXMLObjectQName;
    private QName expectedValue;
    
    @BeforeMethod
    protected void setUp() throws Exception{
        testDocumentLocation = "/data/org/opensaml/core/xml/schema/xsQName.xml";
        expectedXMLObjectQName = new QName("urn:example.org:foo", "bar", "foo");
        expectedValue = new QName("urn:example.org:baz", "SomeValue", "baz");
    }

    /**
     * Tests Marshalling a QName type.
     * @throws MarshallingException 
     * @throws XMLParserException 
     */
    @Test
    public void testMarshall() throws MarshallingException, XMLParserException{
        XSQNameBuilder xsQNameBuilder = (XSQNameBuilder) builderFactory.getBuilder(XSQName.TYPE_NAME);
        XSQName xsQName = xsQNameBuilder.buildObject(expectedXMLObjectQName, XSQName.TYPE_NAME);
        xsQName.setValue(expectedValue);
        
        Marshaller marshaller = marshallerFactory.getMarshaller(xsQName);
        marshaller.marshall(xsQName);
        
        Document document = parserPool.parse(XSQNameTest.class.getResourceAsStream(testDocumentLocation));
        assertXMLEquals("Marshalled XSQName does not match example document", document, xsQName);
    }
    
    /**
     * Tests Unmarshalling a QName type.
     * 
     * @throws XMLParserException 
     * @throws UnmarshallingException 
     */
    @Test
    public void testUnmarshall() throws XMLParserException, UnmarshallingException{
        Document document = parserPool.parse(XSQNameTest.class.getResourceAsStream(testDocumentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(document.getDocumentElement());
        XSQName xsQName = (XSQName) unmarshaller.unmarshall(document.getDocumentElement());
        
        Assert.assertEquals(xsQName.getElementQName(), expectedXMLObjectQName, "Unexpected XSQName QName");
        Assert.assertEquals(xsQName.getSchemaType(), XSQName.TYPE_NAME, "Unexpected XSQName schema type");
        Assert.assertEquals(xsQName.getValue(), expectedValue, "Unexpected value of XSQName");
    }
}