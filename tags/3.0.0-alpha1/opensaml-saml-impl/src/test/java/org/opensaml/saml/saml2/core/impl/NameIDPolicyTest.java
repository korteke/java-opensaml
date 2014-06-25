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
        
        Assert.assertNotNull(policy);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        NameIDPolicy policy = (NameIDPolicy) unmarshallElement(singleElementOptionalAttributesFile);
        
        Assert.assertEquals(policy.getFormat(), expectedFormat, "Unmarshalled name Format URI attribute value was not the expected value");
        Assert.assertEquals(policy.getSPNameQualifier(), expectedSPNameQualifer, "Unmarshalled SPNameQualifier URI attribute value was not the expected value");
        Assert.assertEquals(policy.getAllowCreateXSBoolean(), expectedAllowCreate, "Unmarshalled AllowCreate attribute value was not the expected value");
    }
    
    /**
     * Test the proper behavior of the XSBooleanValue attributes.
     */
    @Test
    public void testXSBooleanAttributes() {
        NameIDPolicy policy = (NameIDPolicy) buildXMLObject(NameIDPolicy.DEFAULT_ELEMENT_NAME);
        
        // AllowCreate attribute
        policy.setAllowCreate(Boolean.TRUE);
        Assert.assertEquals(policy.getAllowCreate(), Boolean.TRUE, "Unexpected value for boolean attribute found");
        Assert.assertNotNull(policy.getAllowCreateXSBoolean(), "XSBooleanValue was null");
        Assert.assertEquals(policy.getAllowCreateXSBoolean(), new XSBooleanValue(Boolean.TRUE, false),
                "XSBooleanValue was unexpected value");
        Assert.assertEquals(policy.getAllowCreateXSBoolean().toString(), "true", "XSBooleanValue string was unexpected value");
        
        policy.setAllowCreate(Boolean.FALSE);
        Assert.assertEquals(policy.getAllowCreate(), Boolean.FALSE, "Unexpected value for boolean attribute found");
        Assert.assertNotNull(policy.getAllowCreateXSBoolean(), "XSBooleanValue was null");
        Assert.assertEquals(policy.getAllowCreateXSBoolean(), new XSBooleanValue(Boolean.FALSE, false),
                "XSBooleanValue was unexpected value");
        Assert.assertEquals(policy.getAllowCreateXSBoolean().toString(), "false", "XSBooleanValue string was unexpected value");
        
        policy.setAllowCreate((Boolean) null);
        Assert.assertEquals(policy.getAllowCreate(), Boolean.FALSE, "Unexpected default value for boolean attribute found");
        Assert.assertNull(policy.getAllowCreateXSBoolean(), "XSBooleanValue was not null");
    }
}