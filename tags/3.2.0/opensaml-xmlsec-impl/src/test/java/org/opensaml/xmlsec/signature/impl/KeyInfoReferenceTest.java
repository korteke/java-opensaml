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
import org.opensaml.xmlsec.signature.KeyInfoReference;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;

/**
 *
 */
public class KeyInfoReferenceTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedID;
    private String expectedURI;
    
    /**
     * Constructor
     *
     */
    public KeyInfoReferenceTest() {
        singleElementFile = "/data/org/opensaml/xmlsec/signature/impl/KeyInfoReference.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/xmlsec/signature/impl/KeyInfoReferenceOptionalAttributes.xml"; 
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedID = "bar";
        expectedURI = "#foo";
    }

    /** {@inheritDoc} */
    public void testSingleElementUnmarshall() {
        KeyInfoReference ref = (KeyInfoReference) unmarshallElement(singleElementFile);
        
        Assert.assertNotNull(ref, "KeyInfoReference");
        Assert.assertEquals(ref.getURI(), expectedURI, "URI attribute");
    }
    
    /** {@inheritDoc} */
    public void testSingleElementOptionalAttributesUnmarshall() {
        KeyInfoReference ref = (KeyInfoReference) unmarshallElement(singleElementOptionalAttributesFile);
        
        Assert.assertNotNull(ref, "KeyInfoReference");
        Assert.assertEquals(ref.getID(), expectedID, "Id attribute");
        Assert.assertEquals(ref.getURI(), expectedURI, "URI attribute");
        Assert.assertEquals(ref.resolveIDFromRoot(expectedID), ref);
    }

    /** {@inheritDoc} */
    public void testSingleElementMarshall() {
        KeyInfoReference ref = (KeyInfoReference) buildXMLObject(KeyInfoReference.DEFAULT_ELEMENT_NAME);
        
        ref.setURI(expectedURI);
        
        assertXMLEquals(expectedDOM, ref);
    }
    
    /** {@inheritDoc} */
    public void testSingleElementOptionalAttributesMarshall() {
        KeyInfoReference ref = (KeyInfoReference) buildXMLObject(KeyInfoReference.DEFAULT_ELEMENT_NAME);

        ref.setID(expectedID);
        ref.setURI(expectedURI);
        
        assertXMLEquals(expectedOptionalAttributesDOM, ref);
        Assert.assertEquals(ref.resolveIDFromRoot(expectedID), ref);
    }

}