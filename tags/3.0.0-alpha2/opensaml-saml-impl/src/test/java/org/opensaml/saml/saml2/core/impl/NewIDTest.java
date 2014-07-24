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
import org.testng.Assert;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.core.NewID;

/**
 * 
 */
public class NewIDTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected element content */
    private String expectedNewID;

    /**
     * Constructor
     *
     */
    public NewIDTest() {
        super();
        singleElementFile = "/data/org/opensaml/saml/saml2/core/impl/NewID.xml";
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedNewID = "SomeSAMLNameID";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        NewID newID = (NewID) unmarshallElement(singleElementFile);
        
       Assert.assertEquals(newID.getNewID(), expectedNewID, "The unmarshalled NewID was not the expected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        NewID newID = (NewID) buildXMLObject(NewID.DEFAULT_ELEMENT_NAME);
        
        newID.setNewID(expectedNewID);
        
        assertXMLEquals(expectedDOM, newID);
    }
}