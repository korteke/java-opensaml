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

package org.opensaml.saml.saml2.core.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.AssertJUnit;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Attribute;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.saml.saml2.core.impl.AttributeImpl}.
 */
public class AttributeTest extends XMLObjectProviderBaseTestCase {

    /** Expected Name attribute value */
    protected String expectedName;

    /** Expected NameFormat attribute value */
    protected String expectedNameFormat;

    /** Expected FriendlyName attribute value */
    protected String expectedFriendlyName;

    /**
     * Constructor
     */
    public AttributeTest() {
        singleElementFile = "/data/org/opensaml/saml/saml2/core/impl/Attribute.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/saml/saml2/core/impl/AttributeOptionalAttributes.xml";
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedName = "attribName";
        expectedNameFormat = "urn:string";
        expectedFriendlyName = "Attribute Name";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        Attribute attribute = (Attribute) unmarshallElement(singleElementFile);

        String name = attribute.getName();
        AssertJUnit.assertEquals("Name was " + name + ", expected " + expectedName, expectedName, name);

    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        Attribute attribute = (Attribute) unmarshallElement(singleElementOptionalAttributesFile);

        String name = attribute.getName();
        AssertJUnit.assertEquals("Name was " + name + ", expected " + expectedName, expectedName, name);

        String nameFormat = attribute.getNameFormat();
        AssertJUnit.assertEquals("NameFormat was " + nameFormat + ", expected " + expectedNameFormat, expectedNameFormat,
                nameFormat);

        String friendlyName = attribute.getFriendlyName();
        AssertJUnit.assertEquals("FriendlyName was " + friendlyName + ", expected " + expectedFriendlyName, expectedFriendlyName,
                friendlyName);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        QName qname = new QName(SAMLConstants.SAML20_NS, Attribute.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        Attribute attribute = (Attribute) buildXMLObject(qname);

        attribute.setName(expectedName);

        assertXMLEquals(expectedDOM, attribute);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        QName qname = new QName(SAMLConstants.SAML20_NS, Attribute.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        Attribute attribute = (Attribute) buildXMLObject(qname);

        attribute.setName(expectedName);
        attribute.setNameFormat(expectedNameFormat);
        attribute.setFriendlyName(expectedFriendlyName);

        assertXMLEquals(expectedOptionalAttributesDOM, attribute);
    }
}