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

package org.opensaml.xacml.policy.impl;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.xacml.policy.AttributeAssignmentType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.xacml.policy.AttributeAssignmentType}.
 */
public class AttributeAssignmentTest extends XMLObjectProviderBaseTestCase {

    private String expectedDataType;

    private String expectedAttributeId;

    private String expectedContent;

    /**
     * Constructor
     */
    public AttributeAssignmentTest() {
        singleElementFile = "/org/opensaml/xacml/policy/impl/AttributeAssignment.xml";

        expectedDataType = "https://example.org/Data/Type";
        expectedAttributeId = "https://example.org/Attribute/Id";
        expectedContent = "Sometext";
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnmarshall() {
        AttributeAssignmentType attributeAssignment = (AttributeAssignmentType) unmarshallElement(singleElementFile);

        Assert.assertEquals(attributeAssignment.getValue(), expectedContent);
        Assert.assertEquals(attributeAssignment.getDataType(), expectedDataType);
        Assert.assertEquals(attributeAssignment.getAttributeId(), expectedAttributeId);
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementMarshall() {
        AttributeAssignmentType attributeAssignment = (new AttributeAssignmentTypeImplBuilder()).buildObject();

        attributeAssignment.setValue(expectedContent);
        attributeAssignment.setDataType(expectedDataType);
        attributeAssignment.setAttributeId(expectedAttributeId);
        assertXMLEquals(expectedDOM, attributeAssignment);
    }

}