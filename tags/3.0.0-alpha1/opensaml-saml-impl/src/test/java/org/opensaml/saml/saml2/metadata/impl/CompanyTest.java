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
package org.opensaml.saml.saml2.metadata.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.metadata.Company;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.impl.CompanyImpl}.
 */
public class CompanyTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected company name */
    protected String expectedName;
    
    /**
     * Constructor
     */
    public CompanyTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/metadata/impl/Company.xml";
    }
    
    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedName = "MyCompany";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        Company company = (Company) unmarshallElement(singleElementFile);
        
        Assert.assertEquals(company.getName(), expectedName, "Company name was not expected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        Company company = (new CompanyBuilder()).buildObject();
        
        company.setName(expectedName);

        assertXMLEquals(expectedDOM, company);
    }

}