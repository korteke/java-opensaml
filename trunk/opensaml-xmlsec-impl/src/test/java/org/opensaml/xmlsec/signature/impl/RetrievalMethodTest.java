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


import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.xmlsec.signature.RetrievalMethod;
import org.opensaml.xmlsec.signature.Transforms;

/**
 *
 */
public class RetrievalMethodTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedURI;
    private String expectedType;
    
    /**
     * Constructor
     *
     */
    public RetrievalMethodTest() {
        singleElementFile = "/data/org/opensaml/xmlsec/signature/impl/RetrievalMethod.xml";
        childElementsFile = "/data/org/opensaml/xmlsec/signature/impl/RetrievalMethodChildElements.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/xmlsec/signature/impl/RetrievalMethodOptionalAttributes.xml"; 
    }

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        
        expectedURI = "urn:string:foo";
        expectedType = "someType";
    }

    /** {@inheritDoc} */
    public void testSingleElementUnmarshall() {
        RetrievalMethod rm = (RetrievalMethod) unmarshallElement(singleElementFile);
        
        assertNotNull("RetrievalMethod", rm);
        assertEquals("URI attribute", expectedURI, rm.getURI());
        assertNull("Transforms child element", rm.getTransforms());
    }
    
    

    /** {@inheritDoc} */
    public void testSingleElementOptionalAttributesUnmarshall() {
        RetrievalMethod rm = (RetrievalMethod) unmarshallElement(singleElementOptionalAttributesFile);
        
        assertNotNull("RetrievalMethod", rm);
        assertEquals("URI attribute", expectedURI, rm.getURI());
        assertEquals("Type attribute", expectedType, rm.getType());
        assertNull("Transforms child element", rm.getTransforms());
    }

    /** {@inheritDoc} */
    public void testChildElementsUnmarshall() {
        RetrievalMethod rm = (RetrievalMethod) unmarshallElement(childElementsFile);
        
        assertNotNull("RetrievalMethod", rm);
        assertEquals("URI attribute", expectedURI, rm.getURI());
        assertNotNull("Transforms child element", rm.getTransforms());
    }

    /** {@inheritDoc} */
    public void testSingleElementMarshall() {
        RetrievalMethod rm = (RetrievalMethod) buildXMLObject(RetrievalMethod.DEFAULT_ELEMENT_NAME);
        
        rm.setURI(expectedURI);
        
        assertXMLEquals(expectedDOM, rm);
    }
    
    

    /** {@inheritDoc} */
    public void testSingleElementOptionalAttributesMarshall() {
        RetrievalMethod rm = (RetrievalMethod) buildXMLObject(RetrievalMethod.DEFAULT_ELEMENT_NAME);
        
        rm.setURI(expectedURI);
        rm.setType(expectedType);
        
        assertXMLEquals(expectedOptionalAttributesDOM, rm);
    }

    /** {@inheritDoc} */
    public void testChildElementsMarshall() {
        RetrievalMethod rm = (RetrievalMethod) buildXMLObject(RetrievalMethod.DEFAULT_ELEMENT_NAME);
        
        rm.setURI(expectedURI);
        rm.setTransforms((Transforms) buildXMLObject(Transforms.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, rm);
    }

}
