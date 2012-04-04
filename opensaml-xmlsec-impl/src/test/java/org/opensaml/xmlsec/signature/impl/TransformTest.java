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

package org.opensaml.xmlsec.signature.impl;


import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.AssertJUnit;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.xmlsec.signature.Transform;
import org.opensaml.xmlsec.signature.XPath;

/**
 *
 */
public class TransformTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedAlgorithm;
    private int expectedTotalChildren;
    private int expectedXPathChildren;
    
    /**
     * Constructor
     *
     */
    public TransformTest() {
        singleElementFile = "/data/org/opensaml/xmlsec/signature/impl/Transform.xml";
        childElementsFile = "/data/org/opensaml/xmlsec/signature/impl/TransformChildElements.xml";
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedAlgorithm = "urn:string:foo";
        expectedTotalChildren = 5;
        expectedXPathChildren = 2;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        Transform transform = (Transform) unmarshallElement(singleElementFile);
        
        AssertJUnit.assertNotNull("Transform", transform);
        AssertJUnit.assertEquals("Algorithm attribute", expectedAlgorithm, transform.getAlgorithm());
        AssertJUnit.assertEquals("Total children", 0, transform.getAllChildren().size());
        AssertJUnit.assertEquals("XPath children", 0, transform.getXPaths().size());
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        Transform transform = (Transform) unmarshallElement(childElementsFile);
        
        AssertJUnit.assertNotNull("Transform", transform);
        AssertJUnit.assertEquals("Algorithm attribute", expectedAlgorithm, transform.getAlgorithm());
        AssertJUnit.assertEquals("Total children", expectedTotalChildren, transform.getAllChildren().size());
        AssertJUnit.assertEquals("XPath children", expectedXPathChildren, transform.getXPaths().size());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        Transform transform = (Transform) buildXMLObject(Transform.DEFAULT_ELEMENT_NAME);
        
        transform.setAlgorithm(expectedAlgorithm);
        
        assertXMLEquals(expectedDOM, transform);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        Transform transform = (Transform) buildXMLObject(Transform.DEFAULT_ELEMENT_NAME);
        
        transform.setAlgorithm(expectedAlgorithm);
        transform.getAllChildren().add( buildXMLObject(XPath.DEFAULT_ELEMENT_NAME));
        transform.getAllChildren().add( buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        transform.getAllChildren().add( buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        transform.getAllChildren().add( buildXMLObject(XPath.DEFAULT_ELEMENT_NAME));
        transform.getAllChildren().add( buildXMLObject(SimpleXMLObject.ELEMENT_NAME));

        assertXMLEquals(expectedChildElementsDOM, transform);
    }

}
