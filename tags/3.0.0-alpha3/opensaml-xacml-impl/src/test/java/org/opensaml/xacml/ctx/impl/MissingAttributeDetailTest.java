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
import org.opensaml.xacml.ctx.AttributeValueType;
import org.opensaml.xacml.ctx.MissingAttributeDetailType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.xacml.ctx.MissingAttributeDetailType}.
 */
public class MissingAttributeDetailTest extends XMLObjectProviderBaseTestCase {

    private String expectedAttributeId;

    private String expectedDataType;

    private String expectedIssuer;
    
    private int  expectedNumAttributeValues;

    /**
     * Constructor
     */
    public MissingAttributeDetailTest() {
        singleElementFile = "/data/org/opensaml/xacml/ctx/impl/MissingAttributeDetail.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/xacml/ctx/impl/MissingAttributeDetailOptionalAttributes.xml";
        childElementsFile = "/data/org/opensaml/xacml/ctx/impl/MissingAttributeDetailChildElements.xml";

        expectedAttributeId = "https://example.org/Missing/Attribute/Detail/Attribute/Id";
        expectedDataType = "https://example.org/Missing/Attribute/Detail/Data/Type";
        expectedIssuer = "MissingAttributeDetailIssuer";
        expectedNumAttributeValues = 5;
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnmarshall() {
        MissingAttributeDetailType missingAttributeDetail = (MissingAttributeDetailType) unmarshallElement(singleElementFile);

        Assert.assertEquals(missingAttributeDetail.getAttributeId(), expectedAttributeId);
        Assert.assertEquals(missingAttributeDetail.getDataType(), expectedDataType);
        Assert.assertNull(missingAttributeDetail.getIssuer());
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementMarshall() {
        MissingAttributeDetailType missingAttributeDetail = new MissingAttributeDetailTypeImplBuilder().buildObject();
        missingAttributeDetail.setAttributeId(expectedAttributeId);
        missingAttributeDetail.setDataType(expectedDataType);

        assertXMLEquals(expectedDOM, missingAttributeDetail);
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsUnmarshall() {
        MissingAttributeDetailType missingAttributeDetail = (MissingAttributeDetailType) unmarshallElement(childElementsFile);

        Assert.assertEquals(missingAttributeDetail.getAttributeValues().size(), expectedNumAttributeValues);
        Assert.assertEquals(missingAttributeDetail.getAttributeId(), expectedAttributeId);
        Assert.assertEquals(missingAttributeDetail.getDataType(), expectedDataType);
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsMarshall() {
        MissingAttributeDetailType attribute = new MissingAttributeDetailTypeImplBuilder().buildObject();

        attribute.setAttributeId(expectedAttributeId);
        attribute.setDataType(expectedDataType);
        for (int i = 0; i < expectedNumAttributeValues; i++) {
            attribute.getAttributeValues().add((AttributeValueType) buildXMLObject(AttributeValueType.DEFAULT_ELEMENT_NAME));
        }

        assertXMLEquals(expectedChildElementsDOM, attribute);
    }

    @Test public void testSingleElementOptionalAttributesUnmarshall() {
        MissingAttributeDetailType missingAttributeDetail = (MissingAttributeDetailType) unmarshallElement(singleElementOptionalAttributesFile);

        Assert.assertEquals(missingAttributeDetail.getAttributeId(), expectedAttributeId);
        Assert.assertEquals(missingAttributeDetail.getDataType(), expectedDataType);
        Assert.assertEquals(missingAttributeDetail.getIssuer(), expectedIssuer);
    }

    @Test public void testSingleElementOptionalAttributesMarshall() {
        MissingAttributeDetailType attribute = new MissingAttributeDetailTypeImplBuilder().buildObject();

        attribute.setAttributeId(expectedAttributeId);
        attribute.setDataType(expectedDataType);
        attribute.setIssuer(expectedIssuer);

        assertXMLEquals(expectedOptionalAttributesDOM, attribute);
    }

}