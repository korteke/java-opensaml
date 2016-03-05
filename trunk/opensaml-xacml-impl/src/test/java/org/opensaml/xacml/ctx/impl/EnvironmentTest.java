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
import org.opensaml.xacml.ctx.EnvironmentType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.xacml.ctx.EnvironmentType}.
 */
public class EnvironmentTest extends XMLObjectProviderBaseTestCase {

    private int expectedNumResults;

    private String expectedAttributeId;

    private String expectedDataType;

    /**
     * Constructor
     */
    public EnvironmentTest() {
        singleElementFile = "/org/opensaml/xacml/ctx/impl/Environment.xml";
        childElementsFile = "/org/opensaml/xacml/ctx/impl/EnvironmentChildElements.xml";

        expectedNumResults = 4;
        expectedAttributeId = "https://example.org/Environment/Attribute/Attribute/Id";
        expectedDataType = "https://example.org/Environment/Attribute/Data/Type";
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnmarshall() {
        EnvironmentType environment = (EnvironmentType) unmarshallElement(singleElementFile);

        Assert.assertTrue(environment.getAttributes().isEmpty());
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementMarshall() {
        EnvironmentType environment = new EnvironmentTypeImplBuilder().buildObject();

        assertXMLEquals(expectedDOM, environment);
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsUnmarshall() {
        EnvironmentType environment = (EnvironmentType) unmarshallElement(childElementsFile);

        Assert.assertEquals(environment.getAttributes().size(), expectedNumResults);
        for (AttributeType attribute: environment.getAttributes()) {
            Assert.assertEquals(attribute.getAttributeId(), expectedAttributeId);
            Assert.assertEquals(attribute.getDataType(), expectedDataType);
        }
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsMarshall() {
        EnvironmentType environment = new EnvironmentTypeImplBuilder().buildObject();

        for (int i = 0; i < expectedNumResults; i++) {
            AttributeType attribute = buildXMLObject(AttributeType.DEFAULT_ELEMENT_NAME);
            attribute.setAttributeID(expectedAttributeId);
            attribute.setDataType(expectedDataType);
            environment.getAttributes().add(attribute);
        }

        assertXMLEquals(expectedChildElementsDOM, environment);
    }

}