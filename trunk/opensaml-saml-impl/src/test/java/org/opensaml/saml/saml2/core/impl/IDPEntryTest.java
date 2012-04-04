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

/**
 * 
 */
package org.opensaml.saml.saml2.core.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.AssertJUnit;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.core.IDPEntry;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.core.impl.IDPEntryImpl}.
 */
public class IDPEntryTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected ProviderID */
    private String expectedProviderID;

    /** Expected ProviderID */
    private String expectedName;
    
    /** Expected ProviderID */
    private String expectedLocation;
    
    /**
     * Constructor
     *
     */
    public IDPEntryTest() {
        super();
        singleElementFile = "/data/org/opensaml/saml/saml2/core/impl/IDPEntry.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml2/core/impl/IDPEntryOptionalAttributes.xml";
    }
    

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedProviderID = "urn:string:providerid";
        expectedName = "Example IdP";
        expectedLocation = "http://idp.example.org/endpoint";
    }




    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        IDPEntry entry = (IDPEntry) buildXMLObject(IDPEntry.DEFAULT_ELEMENT_NAME);
        
        entry.setProviderID(expectedProviderID);

        assertXMLEquals(expectedDOM, entry);
    }
    
    
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        IDPEntry entry = (IDPEntry) buildXMLObject(IDPEntry.DEFAULT_ELEMENT_NAME);
        
        entry.setProviderID(expectedProviderID);
        entry.setName(expectedName);
        entry.setLoc(expectedLocation);
        
        assertXMLEquals(expectedOptionalAttributesDOM, entry);
    }


    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        IDPEntry entry = (IDPEntry) unmarshallElement(singleElementFile);
        
        AssertJUnit.assertEquals("The unmarshalled ProviderID attribute was not the expected value", expectedProviderID, entry.getProviderID());

    }


    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        IDPEntry entry = (IDPEntry) unmarshallElement(singleElementOptionalAttributesFile);
        
        AssertJUnit.assertEquals("The unmarshalled Name attribute was not the expected value", expectedName, entry.getName());
        AssertJUnit.assertEquals("The unmarshalled Loc (location) attribute was not the expected value", expectedLocation, entry.getLoc());
    }
}