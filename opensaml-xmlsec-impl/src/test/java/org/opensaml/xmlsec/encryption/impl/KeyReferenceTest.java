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

package org.opensaml.xmlsec.encryption.impl;


import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.AssertJUnit;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.xmlsec.encryption.KeyReference;

/**
 *
 */
public class KeyReferenceTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedURI;
    private int expectedNumUnknownChildren;
    
    /**
     * Constructor
     *
     */
    public KeyReferenceTest() {
        singleElementFile = "/data/org/opensaml/xmlsec/encryption/impl/KeyReference.xml";
        childElementsFile = "/data/org/opensaml/xmlsec/encryption/impl/KeyReferenceChildElements.xml";
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedURI = "urn:string:foo";
        expectedNumUnknownChildren = 2;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        KeyReference ref = (KeyReference) unmarshallElement(singleElementFile);
        
        AssertJUnit.assertNotNull("KeyReference", ref);
        AssertJUnit.assertEquals("URI attribute", expectedURI, ref.getURI());
        AssertJUnit.assertEquals("Unknown children", 0, ref.getUnknownXMLObjects().size());
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        KeyReference ref = (KeyReference) unmarshallElement(childElementsFile);
        
        AssertJUnit.assertNotNull("KeyReference", ref);
        AssertJUnit.assertEquals("URI attribute", expectedURI, ref.getURI());
        AssertJUnit.assertEquals("Unknown children", expectedNumUnknownChildren, ref.getUnknownXMLObjects().size());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        KeyReference ref = (KeyReference) buildXMLObject(KeyReference.DEFAULT_ELEMENT_NAME);
        
        ref.setURI(expectedURI);
        
        assertXMLEquals(expectedDOM, ref);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        KeyReference ref = (KeyReference) buildXMLObject(KeyReference.DEFAULT_ELEMENT_NAME);
        
        ref.setURI(expectedURI);
        ref.getUnknownXMLObjects().add((SimpleXMLObject) buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        ref.getUnknownXMLObjects().add((SimpleXMLObject) buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, ref);
    }

}
