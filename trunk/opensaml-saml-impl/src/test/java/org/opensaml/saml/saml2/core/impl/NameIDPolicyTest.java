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
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.core.NameIDPolicy;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.core.impl.NameIDPolicyImpl}.
 */
public class NameIDPolicyTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected Format*/
    private String expectedFormat;

    /** Expected SPNameQualifer */
    private String expectedSPNameQualifer;

    /** Expected AllowCreate */
    private XSBooleanValue expectedAllowCreate;

    /**
     * Constructor
     */
    public NameIDPolicyTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/core/impl/NameIDPolicy.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml2/core/impl/NameIDPolicyOptionalAttributes.xml";
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedFormat = "urn:string:format";
        expectedSPNameQualifer = "urn:string:spname";
        expectedAllowCreate = new XSBooleanValue(Boolean.TRUE, false);

    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        NameIDPolicy policy = (NameIDPolicy) buildXMLObject(NameIDPolicy.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, policy);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        NameIDPolicy policy = (NameIDPolicy) buildXMLObject(NameIDPolicy.DEFAULT_ELEMENT_NAME);
        
        policy.setFormat(expectedFormat);
        policy.setSPNameQualifier(expectedSPNameQualifer);
        policy.setAllowCreate(expectedAllowCreate);
        
        assertXMLEquals(expectedOptionalAttributesDOM, policy);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        NameIDPolicy policy = (NameIDPolicy) unmarshallElement(singleElementFile);
        
        AssertJUnit.assertNotNull(policy);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        NameIDPolicy policy = (NameIDPolicy) unmarshallElement(singleElementOptionalAttributesFile);
        
        AssertJUnit.assertEquals("Unmarshalled name Format URI attribute value was not the expected value", expectedFormat, policy.getFormat());
        AssertJUnit.assertEquals("Unmarshalled SPNameQualifier URI attribute value was not the expected value", expectedSPNameQualifer, policy.getSPNameQualifier());
        AssertJUnit.assertEquals("Unmarshalled AllowCreate attribute value was not the expected value", expectedAllowCreate, policy.getAllowCreateXSBoolean());
    }
    
    /**
     * Test the proper behavior of the XSBooleanValue attributes.
     */
    @Test
    public void testXSBooleanAttributes() {
        NameIDPolicy policy = (NameIDPolicy) buildXMLObject(NameIDPolicy.DEFAULT_ELEMENT_NAME);
        
        // AllowCreate attribute
        policy.setAllowCreate(Boolean.TRUE);
        AssertJUnit.assertEquals("Unexpected value for boolean attribute found", Boolean.TRUE, policy.getAllowCreate());
        AssertJUnit.assertNotNull("XSBooleanValue was null", policy.getAllowCreateXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue was unexpected value", new XSBooleanValue(Boolean.TRUE, false),
                policy.getAllowCreateXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue string was unexpected value", "true", policy.getAllowCreateXSBoolean().toString());
        
        policy.setAllowCreate(Boolean.FALSE);
        AssertJUnit.assertEquals("Unexpected value for boolean attribute found", Boolean.FALSE, policy.getAllowCreate());
        AssertJUnit.assertNotNull("XSBooleanValue was null", policy.getAllowCreateXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue was unexpected value", new XSBooleanValue(Boolean.FALSE, false),
                policy.getAllowCreateXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue string was unexpected value", "false", policy.getAllowCreateXSBoolean().toString());
        
        policy.setAllowCreate((Boolean) null);
        AssertJUnit.assertEquals("Unexpected default value for boolean attribute found", Boolean.FALSE, policy.getAllowCreate());
        AssertJUnit.assertNull("XSBooleanValue was not null", policy.getAllowCreateXSBoolean());
    }
}