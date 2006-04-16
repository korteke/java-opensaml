/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
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

package org.opensaml.xml;

import javax.xml.namespace.QName;

import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.mock.SimpleXMLObject;
import org.opensaml.xml.mock.SimpleXMLObjectBuilder;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Unit test for marshalling functions.
 */
public class MarshallingTest extends XMLObjectBaseTestCase {

    /** QName for SimpleXMLObject */
    private QName simpleXMLObjectQName;

    /**
     * Constructor
     */
    public MarshallingTest() {
        super();

        simpleXMLObjectQName = new QName(SimpleXMLObject.NAMESAPACE, SimpleXMLObject.LOCAL_NAME);
    }

    /**
     * Tests marshalling an object that has DOM Attrs.
     * 
     * @throws XMLParserException
     * @throws MarshallingException
     */
    public void testMarshallingWithAttributes() throws XMLParserException {
        String expectedId = "Firefly";
        String expectedDocumentLocation = "/data/org/opensaml/xml/SimpleXMLObjectWithAttribute.xml";
        Document expectedDocument = parserPool.parse(MarshallingTest.class
                .getResourceAsStream(expectedDocumentLocation));

        SimpleXMLObjectBuilder sxoBuilder = (SimpleXMLObjectBuilder) builderFactory.getBuilder(simpleXMLObjectQName);
        SimpleXMLObject sxObject = sxoBuilder.buildObject();
        sxObject.setId(expectedId);

        assertEquals(expectedDocument, sxObject);
        assertEquals(expectedDocument, sxObject);
    }

    /**
     * Tests marshalling an object that has DOM Element textual content.
     * 
     * @throws XMLParserException
     */
    public void testMarshallingWithElementContent() throws XMLParserException {
        String expectedDocumentLocation = "/data/org/opensaml/xml/SimpleXMLObjectWithContent.xml";
        Document expectedDocument = parserPool.parse(MarshallingTest.class
                .getResourceAsStream(expectedDocumentLocation));

        SimpleXMLObjectBuilder sxoBuilder = (SimpleXMLObjectBuilder) builderFactory.getBuilder(simpleXMLObjectQName);

        SimpleXMLObject sxObject = (SimpleXMLObject) sxoBuilder.buildObject();

        SimpleXMLObject child1 = (SimpleXMLObject) sxoBuilder.buildObject();
        child1.setValue("Content1");
        sxObject.getSimpleXMLObjects().add(child1);

        SimpleXMLObject child2 = (SimpleXMLObject) sxoBuilder.buildObject();
        child2.setValue("Content2");
        sxObject.getSimpleXMLObjects().add(child2);

        SimpleXMLObject child3 = (SimpleXMLObject) sxoBuilder.buildObject();
        sxObject.getSimpleXMLObjects().add(child3);

        SimpleXMLObject grandchild1 = (SimpleXMLObject) sxoBuilder.buildObject();
        grandchild1.setValue("Content3");
        child3.getSimpleXMLObjects().add(grandchild1);

        assertEquals(expectedDocument, sxObject);
    }

    /**
     * Tests marshalling an object that has DOM Element children
     * 
     * @throws XMLParserException
     * @throws MarshallingException
     */
    public void testMarshallingWithChildElements() throws XMLParserException, MarshallingException {
        String expectedDocumentLocation = "/data/org/opensaml/xml/SimpleXMLObjectWithChildren.xml";
        Document expectedDocument = parserPool.parse(MarshallingTest.class
                .getResourceAsStream(expectedDocumentLocation));

        SimpleXMLObjectBuilder sxoBuilder = (SimpleXMLObjectBuilder) builderFactory.getBuilder(simpleXMLObjectQName);
        SimpleXMLObject sxObject = sxoBuilder.buildObject();
        SimpleXMLObject sxObjectChild1 = sxoBuilder.buildObject();
        SimpleXMLObject sxObjectChild2 = sxoBuilder.buildObject();
        sxObject.getSimpleXMLObjects().add(sxObjectChild1);
        sxObject.getSimpleXMLObjects().add(sxObjectChild2);

        assertEquals(expectedDocument, sxObject);
    }

    /**
     * Tests marshalling a fragment of an already marshalled tree into an existing, but different, DOM tree.
     * 
     * @throws XMLParserException
     * @throws MarshallingException 
     */
    public void testMarshallingXMLFragment() throws XMLParserException, MarshallingException {
        String expectedDocumentLocation = "/data/org/opensaml/xml/SOAPMessageWithContent.xml";
        String soapDocLocation = "/data/org/opensaml/xml/SOAPMessage.xml";
        Document soapDoc = parserPool.parse(MarshallingTest.class.getResourceAsStream(soapDocLocation));
        Element soapBody = (Element) soapDoc.getDocumentElement().getElementsByTagName("Body").item(0);
        
        SimpleXMLObjectBuilder sxoBuilder = (SimpleXMLObjectBuilder) builderFactory.getBuilder(simpleXMLObjectQName);
        
        SimpleXMLObject response = sxoBuilder.buildObject(SimpleXMLObject.NAMESAPACE, "Response", SimpleXMLObject.NAMESPACE_PREFIX);
        SimpleXMLObject statement = sxoBuilder.buildObject(SimpleXMLObject.NAMESAPACE, "Statement", SimpleXMLObject.NAMESPACE_PREFIX);
        response.getSimpleXMLObjects().add(statement);
        
        // Marshall it once so the DOM is cached
        Marshaller marshaller = marshallerFactory.getMarshaller(simpleXMLObjectQName);
        marshaller.marshall(response);
        
        // Marshall statement (with cached DOM) into SOAP Body element child
        Document expectedDocument = parserPool.parse(MarshallingTest.class.getResourceAsStream(expectedDocumentLocation));
        Element statementElem = marshaller.marshall(statement, soapBody);
        assertXMLEqual(expectedDocument, statementElem.getOwnerDocument());
    }
}