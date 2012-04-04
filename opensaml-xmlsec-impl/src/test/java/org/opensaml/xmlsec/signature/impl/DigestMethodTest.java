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
import org.opensaml.xmlsec.signature.DigestMethod;

public class DigestMethodTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedAlgorithm;
    private int expectedTotalChildren;
    
    /**
     * Constructor.
     *
     */
    public DigestMethodTest() {
        singleElementFile = "/data/org/opensaml/xmlsec/signature/impl/DigestMethod.xml";
        childElementsFile = "/data/org/opensaml/xmlsec/signature/impl/DigestMethodChildElements.xml";
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedAlgorithm = "urn:string:foo";
        expectedTotalChildren = 3;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        DigestMethod digestMethod = (DigestMethod) unmarshallElement(singleElementFile);
        
        AssertJUnit.assertNotNull("DigestMethod", digestMethod);
        AssertJUnit.assertEquals("Algorithm attribute", expectedAlgorithm, digestMethod.getAlgorithm());
        AssertJUnit.assertEquals("Total children", 0, digestMethod.getUnknownXMLObjects().size());
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        DigestMethod digestMethod = (DigestMethod) unmarshallElement(childElementsFile);
        
        AssertJUnit.assertNotNull("DigestMethod", digestMethod);
        AssertJUnit.assertEquals("Algorithm attribute", expectedAlgorithm, digestMethod.getAlgorithm());
        AssertJUnit.assertEquals("Total children", expectedTotalChildren, digestMethod.getUnknownXMLObjects().size());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        DigestMethod digestMethod = (DigestMethod) buildXMLObject(DigestMethod.DEFAULT_ELEMENT_NAME);
        
        digestMethod.setAlgorithm(expectedAlgorithm);
        
        assertXMLEquals(expectedDOM, digestMethod);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        DigestMethod digestMethod = (DigestMethod) buildXMLObject(DigestMethod.DEFAULT_ELEMENT_NAME);
        
        digestMethod.setAlgorithm(expectedAlgorithm);
        digestMethod.getUnknownXMLObjects().add( buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        digestMethod.getUnknownXMLObjects().add( buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        digestMethod.getUnknownXMLObjects().add( buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, digestMethod);
    }

}
