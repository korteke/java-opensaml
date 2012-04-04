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
import org.opensaml.xmlsec.signature.Transform;
import org.opensaml.xmlsec.signature.Transforms;

/**
 *
 */
public class TransformsTest extends XMLObjectProviderBaseTestCase {
    
    private int expectedNumTransforms;
    
    /**
     * Constructor
     *
     */
    public TransformsTest() {
        singleElementFile = "/data/org/opensaml/xmlsec/signature/impl/Transforms.xml";
        childElementsFile = "/data/org/opensaml/xmlsec/signature/impl/TransformsChildElements.xml";
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedNumTransforms = 2;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        Transforms em = (Transforms) unmarshallElement(singleElementFile);
        
        AssertJUnit.assertNotNull("Transforms", em);
        AssertJUnit.assertEquals("Transform children", 0, em.getTransforms().size());
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        Transforms em = (Transforms) unmarshallElement(childElementsFile);
        
        AssertJUnit.assertNotNull("Transforms", em);
        AssertJUnit.assertEquals("Transform children", expectedNumTransforms, em.getTransforms().size());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        Transforms em = (Transforms) buildXMLObject(Transforms.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, em);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        Transforms em = (Transforms) buildXMLObject(Transforms.DEFAULT_ELEMENT_NAME);
        
        em.getTransforms().add( (Transform) buildXMLObject(Transform.DEFAULT_ELEMENT_NAME));
        em.getTransforms().add( (Transform) buildXMLObject(Transform.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, em);
    }

}
