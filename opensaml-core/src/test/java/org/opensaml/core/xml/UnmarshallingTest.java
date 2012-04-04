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
import org.testng.Assert;
import java.util.List;

import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.mock.SimpleXMLObject;
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
    @Test
    public void testUnmarshallingWithAttributes() throws XMLParserException, UnmarshallingException {
        String expectedId = "Firefly";
        String documentLocation = "/data/org/opensaml/core/xml/SimpleXMLObjectWithAttribute.xml";
        Document document = parserPool.parse(UnmarshallingTest.class.getResourceAsStream(documentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(document.getDocumentElement());
        SimpleXMLObject sxObject = (SimpleXMLObject) unmarshaller.unmarshall(document.getDocumentElement());

        Assert.assertNotNull(sxObject.getDOM(), "DOM was not cached after unmarshalling");
        Assert.assertEquals(sxObject.getId(), expectedId, "ID was not expected value");
    }

    /**
     * Tests unmarshalling an element with content.
     * 
     * @throws XMLParserException
     * @throws UnmarshallingException
     */
    @Test
    public void testUnmarshallingWithElementContent() throws XMLParserException, UnmarshallingException {
        String documentLocation = "/data/org/opensaml/core/xml/SimpleXMLObjectWithContent.xml";
        Document document = parserPool.parse(UnmarshallingTest.class.getResourceAsStream(documentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(document.getDocumentElement());
        SimpleXMLObject sxObject = (SimpleXMLObject) unmarshaller.unmarshall(document.getDocumentElement());
        
        Assert.assertNotNull(sxObject.getDOM(), "DOM was not cached after unmarshalling");
        
        List<SimpleXMLObject> children = sxObject.getSimpleXMLObjects();
        Assert.assertEquals(children.size(), 3, "Unexpected number of children");
        
        SimpleXMLObject child1 = children.get(0);
        Assert.assertEquals(child1.getValue(), "Content1", "Unexpected value (text content) for child 1");
        
        SimpleXMLObject child2 = children.get(1);
        Assert.assertEquals(child2.getValue(), "Content2", "Unexpected value (text content) for child 2");
        
        SimpleXMLObject child3 = children.get(2);
        Assert.assertNull(child3.getValue(), "Child had text content when it should not");
        
        List<SimpleXMLObject> grandChildren = child3.getSimpleXMLObjects();
        Assert.assertEquals(grandChildren.size(), 1, "Unexpected number of grandchildren (children for child 3)");
        
        SimpleXMLObject grandChild1 = grandChildren.get(0);
        Assert.assertEquals(grandChild1.getValue(), "Content3", "Unexpected value (text content) for grandchild 1");
    }

    /**
     * Tests unmarshalling an element with child elements.
     * 
     * @throws XMLParserException
     * @throws MarshallingException
     */
    @Test
    public void testUnmarshallingWithChildElements() throws XMLParserException, UnmarshallingException {
        String documentLocation = "/data/org/opensaml/core/xml/SimpleXMLObjectWithChildren.xml";
        Document document = parserPool.parse(UnmarshallingTest.class.getResourceAsStream(documentLocation));

        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(document.getDocumentElement());
        SimpleXMLObject sxObject = (SimpleXMLObject) unmarshaller.unmarshall(document.getDocumentElement());

        Assert.assertNotNull(sxObject.getDOM(), "DOM was not cached after unmarshalling");
        
        Assert.assertEquals(sxObject.getSimpleXMLObjects().size(), 2, "Number of children elements was not expected value");
    }
}