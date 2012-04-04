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

package org.opensaml.saml.saml2.metadata.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.AssertJUnit;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.RequestedAttribute;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.impl.RequestedAttributeImpl}.
 */
public class RequestedAttributeTest extends XMLObjectProviderBaseTestCase {

    /** Expected Name attribute value */
    protected String expectedName;

    /** Expected NameFormat attribute value */
    protected String expectedNameFormat;

    /** Expected FriendlyName attribute value */
    protected String expectedFriendlyName;

    /** Excpected isRequired attribute value */
    protected XSBooleanValue expectedIsRequired;

    /**
     * Constructor
     */
    public RequestedAttributeTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/metadata/impl/RequestedAttribute.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml2/metadata/impl/RequestedAttributeOptionalAttributes.xml";
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedName = "attribName";
        expectedNameFormat = "urn:string";
        expectedFriendlyName = "Attribute Name";
        expectedIsRequired = new XSBooleanValue(Boolean.TRUE, false);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        RequestedAttribute attribute = (RequestedAttribute) unmarshallElement(singleElementFile);

        String name = attribute.getName();
        AssertJUnit.assertEquals("Name was " + name + ", expected " + expectedName, expectedName, name);

    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        RequestedAttribute requestedAttribute = (RequestedAttribute) unmarshallElement(singleElementOptionalAttributesFile);

        String name = requestedAttribute.getName();
        AssertJUnit.assertEquals("Name was " + name + ", expected " + expectedName, expectedName, name);

        String nameFormat = requestedAttribute.getNameFormat();
        AssertJUnit.assertEquals("NameFormat was " + nameFormat + ", expected " + expectedNameFormat, expectedNameFormat,
                nameFormat);

        String friendlyName = requestedAttribute.getFriendlyName();
        AssertJUnit.assertEquals("FriendlyName was " + friendlyName + ", expected " + expectedFriendlyName, expectedFriendlyName,
                friendlyName);

        boolean isRequired = requestedAttribute.isRequired().booleanValue();
        AssertJUnit.assertEquals("Is Required was " + isRequired + ", expected " + expectedIsRequired, expectedIsRequired,
                requestedAttribute.isRequiredXSBoolean());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20MD_NS, RequestedAttribute.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        RequestedAttribute requestedAttribute = (RequestedAttribute) buildXMLObject(qname);

        requestedAttribute.setName(expectedName);

        assertXMLEquals(expectedDOM, requestedAttribute);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        QName qname = new QName(SAMLConstants.SAML20MD_NS, RequestedAttribute.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);
        RequestedAttribute requestedAttribute = (RequestedAttribute) buildXMLObject(qname);

        requestedAttribute.setName(expectedName);
        requestedAttribute.setNameFormat(expectedNameFormat);
        requestedAttribute.setFriendlyName(expectedFriendlyName);
        requestedAttribute.setIsRequired(expectedIsRequired);

        assertXMLEquals(expectedOptionalAttributesDOM, requestedAttribute);
    }
    
    /**
     * Test the proper behavior of the XSBooleanValue attributes.
     */
    @Test
    public void testXSBooleanAttributes() {
        RequestedAttribute attrib = 
            (RequestedAttribute) buildXMLObject(RequestedAttribute.DEFAULT_ELEMENT_NAME);
        
        // isRequired attribute
        attrib.setIsRequired(Boolean.TRUE);
        AssertJUnit.assertEquals("Unexpected value for boolean attribute found", Boolean.TRUE, attrib.isRequired());
        AssertJUnit.assertNotNull("XSBooleanValue was null", attrib.isRequiredXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue was unexpected value", new XSBooleanValue(Boolean.TRUE, false),
                attrib.isRequiredXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue string was unexpected value", "true", attrib.isRequiredXSBoolean().toString());
        
        attrib.setIsRequired(Boolean.FALSE);
        AssertJUnit.assertEquals("Unexpected value for boolean attribute found", Boolean.FALSE, attrib.isRequired());
        AssertJUnit.assertNotNull("XSBooleanValue was null", attrib.isRequiredXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue was unexpected value", new XSBooleanValue(Boolean.FALSE, false),
                attrib.isRequiredXSBoolean());
        AssertJUnit.assertEquals("XSBooleanValue string was unexpected value", "false", attrib.isRequiredXSBoolean().toString());
        
        attrib.setIsRequired((Boolean) null);
        AssertJUnit.assertEquals("Unexpected default value for boolean attribute found", Boolean.FALSE, attrib.isRequired());
        AssertJUnit.assertNull("XSBooleanValue was not null", attrib.isRequiredXSBoolean());
    }
}