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

import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.mock.SimpleXMLObject;
import org.opensaml.xml.parse.XMLParserException;
import org.w3c.dom.Document;

/**
 * Unit test for unmarshalling functions.
 */
public class UnmarshallingTest extends XMLObjectBaseTestCase {

    /**
     * Constructor
     */
    public UnmarshallingTest() {
        super();
    }

    /**
     * Tests unmarshalling an element that has attributes.
     * 
     * @throws XMLParserException
     * @throws UnmarshallingException
     */
    public void testUnmarshallingWithAttributes() throws XMLParserException, UnmarshallingException {
        String expectedId = "Firefly";
        String documentLocation = "/data/org/opensaml/xml/SimpleXMLObjectWithAttribute.xml";
        Document document = parserPool.parse(UnmarshallingTest.class.getResourceAsStream(documentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(document.getDocumentElement());
        SimpleXMLObject sxObject = (SimpleXMLObject) unmarshaller.unmarshall(document.getDocumentElement());

        assertEquals("ID was not expected value", expectedId, sxObject.getId());
    }

    /**
     * Tests unmarshalling an element with content.
     * 
     * @throws XMLParserException
     * @throws UnmarshallingException
     */
    public void testUnmarshallingWithElementContent() throws XMLParserException, UnmarshallingException {
        String expectedContent = "Sample Content";
        String documentLocation = "/data/org/opensaml/xml/SimpleXMLObjectWithContent.xml";
        Document document = parserPool.parse(UnmarshallingTest.class.getResourceAsStream(documentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(document.getDocumentElement());
        SimpleXMLObject sxObject = (SimpleXMLObject) unmarshaller.unmarshall(document.getDocumentElement());

        assertEquals("Element content was not expected value", expectedContent, sxObject.getValue());
    }

    /**
     * Tests unmarshalling an element with child elements.
     * 
     * @throws XMLParserException
     * @throws MarshallingException
     */
    public void testUnmarshallingWithChildElements() throws XMLParserException, UnmarshallingException {
        String documentLocation = "/data/org/opensaml/xml/SimpleXMLObjectWithChildren.xml";
        Document document = parserPool.parse(UnmarshallingTest.class.getResourceAsStream(documentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(document.getDocumentElement());
        SimpleXMLObject sxObject = (SimpleXMLObject) unmarshaller.unmarshall(document.getDocumentElement());

        assertEquals("Number of children elements was not expected value", 2, sxObject.getSimpleXMLObjects().size());
    }
}