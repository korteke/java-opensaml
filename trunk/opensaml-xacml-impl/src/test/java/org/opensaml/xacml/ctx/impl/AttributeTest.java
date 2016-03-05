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

package org.opensaml.xacml.ctx.impl;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.ctx.AttributeValueType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.xacml.ctx.AttributeType}.
 */
public class AttributeTest extends XMLObjectProviderBaseTestCase {

    private String expectedAttributeId;

    private String expectedDataType;

    private String expectedIssuer;
    
    private int  expectedNumAttributeValues;

    /**
     * Constructor
     */
    public AttributeTest() {
        singleElementFile = "/org/opensaml/xacml/ctx/impl/Attribute.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/xacml/ctx/impl/AttributeOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/xacml/ctx/impl/AttributeChildElements.xml";

        expectedAttributeId = "https://example.org/Attribute/Attribute/Id";
        expectedDataType = "https://example.org/Attribute/Data/Type";
        expectedIssuer = "AttributeIssuer";
        expectedNumAttributeValues = 3;
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnmarshall() {
        AttributeType attribute = (AttributeType) unmarshallElement(singleElementFile);

        Assert.assertEquals(attribute.getAttributeId(), expectedAttributeId);
        Assert.assertEquals(attribute.getDataType(), expectedDataType);
        Assert.assertNull(attribute.getIssuer());
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementMarshall() {
        AttributeType attribute = (new AttributeTypeImplBuilder()).buildObject();
        attribute.setAttributeID(expectedAttributeId);
        attribute.setDataType(expectedDataType);

        assertXMLEquals(expectedDOM, attribute);
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsUnmarshall() {
        AttributeType attribute = (AttributeType) unmarshallElement(childElementsFile);

        Assert.assertEquals(attribute.getAttributeValues().size(), expectedNumAttributeValues);
        Assert.assertEquals(attribute.getAttributeId(), expectedAttributeId);
        Assert.assertEquals(attribute.getDataType(), expectedDataType);
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsMarshall() {
        AttributeType attribute = (new AttributeTypeImplBuilder()).buildObject();

        attribute.setAttributeID(expectedAttributeId);
        attribute.setDataType(expectedDataType);
        for (int i = 0; i < expectedNumAttributeValues; i++) {
            attribute.getAttributeValues().add((AttributeValueType) buildXMLObject(AttributeValueType.DEFAULT_ELEMENT_NAME));
        }

        assertXMLEquals(expectedChildElementsDOM, attribute);
    }

    @Test public void testSingleElementOptionalAttributesUnmarshall() {
        AttributeType attribute = (AttributeType) unmarshallElement(singleElementOptionalAttributesFile);

        Assert.assertEquals(attribute.getAttributeId(), expectedAttributeId);
        Assert.assertEquals(attribute.getDataType(), expectedDataType);
        Assert.assertEquals(attribute.getIssuer(), expectedIssuer);
    }

    @Test public void testSingleElementOptionalAttributesMarshall() {
        AttributeType attribute = (new AttributeTypeImplBuilder()).buildObject();

        attribute.setAttributeID(expectedAttributeId);
        attribute.setDataType(expectedDataType);
        attribute.setIssuer(expectedIssuer);

        assertXMLEquals(expectedOptionalAttributesDOM, attribute);
    }

}