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
import org.opensaml.xacml.ctx.ActionType;
import org.opensaml.xacml.ctx.AttributeType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling {@link link org.opensaml.xacml.ctx.ActionType}.
 */
public class ActionTest extends XMLObjectProviderBaseTestCase {

    private int expectedNumAttributes;

    private String expectedAttributeId;

    private String expectedDataType;

    /**
     * Constructor
     */
    public ActionTest() {
        singleElementFile = "/data/org/opensaml/xacml/ctx/impl/Action.xml";
        childElementsFile = "/data/org/opensaml/xacml/ctx/impl/ActionChildElements.xml";

        expectedNumAttributes = 5;
        expectedAttributeId = "https://example.org/Action/Attribute/Attribute/Id";
        expectedDataType = "https://example.org/Action/Attribute/Data/Type";
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnmarshall() {
        ActionType action = (ActionType) unmarshallElement(singleElementFile);

        Assert.assertTrue(action.getAttributes().isEmpty());
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementMarshall() {
        ActionType action = new ActionTypeImplBuilder().buildObject();

        assertXMLEquals(expectedDOM, action);
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsUnmarshall() {
        ActionType action = (ActionType) unmarshallElement(childElementsFile);

        Assert.assertEquals(action.getAttributes().size(), expectedNumAttributes);
        for (AttributeType attribute: action.getAttributes()) {
            Assert.assertEquals(attribute.getAttributeId(), expectedAttributeId);
            Assert.assertEquals(attribute.getDataType(), expectedDataType);
        }
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsMarshall() {
        ActionType action = new ActionTypeImplBuilder().buildObject();

        for (int i = 0; i < expectedNumAttributes; i++) {
            AttributeType attribute = buildXMLObject(AttributeType.DEFAULT_ELEMENT_NAME);
            attribute.setAttributeID(expectedAttributeId);
            attribute.setDataType(expectedDataType);
            action.getAttributes().add(attribute);
        }

        assertXMLEquals(expectedChildElementsDOM, action);
    }

}