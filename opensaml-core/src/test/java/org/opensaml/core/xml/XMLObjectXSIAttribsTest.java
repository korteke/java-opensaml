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

package org.opensaml.core.xml;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.core.xml.mock.SimpleXMLObjectBuilder;
import org.w3c.dom.Document;

/**
 * Test support for attributes from the XML Schema Instance namespace.
 */
public class XMLObjectXSIAttribsTest extends XMLObjectBaseTestCase {
    
    /** QName for SimpleXMLObject. */
    private QName simpleXMLObjectQName;

    /** Constructor. */
    public XMLObjectXSIAttribsTest() {
        super();
        
        simpleXMLObjectQName = new QName(SimpleXMLObject.NAMESPACE, SimpleXMLObject.LOCAL_NAME);
    }
    
    @Test
    public void testUnmarshallNoNil() throws XMLParserException, UnmarshallingException {
        String documentLocation = "/data/org/opensaml/core/xml/SimpleXMLObjectWithAttribute.xml";
        Document document = parserPool.parse(XMLObjectXSIAttribsTest.class.getResourceAsStream(documentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(document.getDocumentElement());
        SimpleXMLObject sxObject = (SimpleXMLObject) unmarshaller.unmarshall(document.getDocumentElement());

        AssertJUnit.assertNull(sxObject.isNilXSBoolean());
        AssertJUnit.assertFalse("Expected isNil() false", sxObject.isNil());
    }
    
    @Test
    public void testUnmarshallNil() throws XMLParserException, UnmarshallingException {
        String documentLocation = "/data/org/opensaml/core/xml/SimpleXMLObjectNil.xml";
        Document document = parserPool.parse(XMLObjectXSIAttribsTest.class.getResourceAsStream(documentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(document.getDocumentElement());
        SimpleXMLObject sxObject = (SimpleXMLObject) unmarshaller.unmarshall(document.getDocumentElement());

        AssertJUnit.assertNotNull(sxObject.isNilXSBoolean());
        AssertJUnit.assertTrue("Expected isNil() true", sxObject.isNil());
    }
    
    @Test
    public void testMarshallNil() throws XMLParserException {
        String expectedDocumentLocation = "/data/org/opensaml/core/xml/SimpleXMLObjectNil.xml";
        Document expectedDocument = parserPool.parse(XMLObjectXSIAttribsTest.class
                .getResourceAsStream(expectedDocumentLocation));

        SimpleXMLObjectBuilder sxoBuilder = (SimpleXMLObjectBuilder) builderFactory.getBuilder(simpleXMLObjectQName);
        SimpleXMLObject sxObject = sxoBuilder.buildObject();
        sxObject.setNil(true);

        assertXMLEquals(expectedDocument, sxObject);
    }
    
    @Test
    public void testUnmarshallSchemaLocation() throws XMLParserException, UnmarshallingException {
        String expectedValue = "http://www.example.com/Test http://www.example.com/Test.xsd";
        String documentLocation = "/data/org/opensaml/core/xml/SimpleXMLObjectSchemaLocation.xml";
        Document document = parserPool.parse(XMLObjectXSIAttribsTest.class.getResourceAsStream(documentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(document.getDocumentElement());
        SimpleXMLObject sxObject = (SimpleXMLObject) unmarshaller.unmarshall(document.getDocumentElement());

        AssertJUnit.assertEquals("Incorrect xsi:schemaLocation value", expectedValue, sxObject.getSchemaLocation());
    }
    
    @Test
    public void testMarshallSchemaLocation() throws XMLParserException {
        String expectedValue = "http://www.example.com/Test http://www.example.com/Test.xsd";
        String expectedDocumentLocation = "/data/org/opensaml/core/xml/SimpleXMLObjectSchemaLocation.xml";
        Document expectedDocument = parserPool.parse(XMLObjectXSIAttribsTest.class
                .getResourceAsStream(expectedDocumentLocation));

        SimpleXMLObjectBuilder sxoBuilder = (SimpleXMLObjectBuilder) builderFactory.getBuilder(simpleXMLObjectQName);
        SimpleXMLObject sxObject = sxoBuilder.buildObject();
        sxObject.setSchemaLocation(expectedValue);

        assertXMLEquals(expectedDocument, sxObject);
    }
    
    @Test
    public void testUnmarshallNoNamespaceSchemaLocation() throws XMLParserException, UnmarshallingException {
        String expectedValue = "http://www.example.com/Test.xsd";
        String documentLocation = "/data/org/opensaml/core/xml/SimpleXMLObjectNoNamespaceSchemaLocation.xml";
        Document document = parserPool.parse(XMLObjectXSIAttribsTest.class.getResourceAsStream(documentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(document.getDocumentElement());
        SimpleXMLObject sxObject = (SimpleXMLObject) unmarshaller.unmarshall(document.getDocumentElement());

        AssertJUnit.assertEquals("Incorrect xsi:noNamespaceSchemaLocation value", expectedValue, sxObject.getNoNamespaceSchemaLocation());
    }
    
    @Test
    public void testMarshallNoNamespaceSchemaLocation() throws XMLParserException {
        String expectedValue = "http://www.example.com/Test.xsd";
        String expectedDocumentLocation = "/data/org/opensaml/core/xml/SimpleXMLObjectNoNamespaceSchemaLocation.xml";
        Document expectedDocument = parserPool.parse(XMLObjectXSIAttribsTest.class
                .getResourceAsStream(expectedDocumentLocation));

        SimpleXMLObjectBuilder sxoBuilder = (SimpleXMLObjectBuilder) builderFactory.getBuilder(simpleXMLObjectQName);
        SimpleXMLObject sxObject = sxoBuilder.buildObject();
        sxObject.setNoNamespaceSchemaLocation(expectedValue);

        assertXMLEquals(expectedDocument, sxObject);
    }
    

}
