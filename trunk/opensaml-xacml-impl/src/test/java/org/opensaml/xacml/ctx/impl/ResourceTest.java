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
import org.opensaml.xacml.ctx.ResourceContentType;
import org.opensaml.xacml.ctx.ResourceType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling {@link link org.opensaml.xacml.ctx.ResourceType}.
 */
public class ResourceTest extends XMLObjectProviderBaseTestCase {

    private int expectedNumAttributes;

    private String expectedAttributeId;

    private String expectedDataType;

    /**
     * Constructor
     */
    public ResourceTest() {
        singleElementFile = "/data/org/opensaml/xacml/ctx/impl/Resource.xml";
        childElementsFile = "/data/org/opensaml/xacml/ctx/impl/ResourceChildElements.xml";

        expectedNumAttributes = 3;
        expectedAttributeId = "https://example.org/Resource/Attribute/Attribute/Id";
        expectedDataType = "https://example.org/Resource/Attribute/Data/Type";
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnmarshall() {
        ResourceType resource = (ResourceType) unmarshallElement(singleElementFile);

        Assert.assertNull(resource.getResourceContent());
        Assert.assertTrue(resource.getAttributes().isEmpty());
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementMarshall() {
        ResourceType resource = new ResourceTypeImplBuilder().buildObject();

        assertXMLEquals(expectedDOM, resource);
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsUnmarshall() {
        ResourceType resource = (ResourceType) unmarshallElement(childElementsFile);

        Assert.assertNotNull(resource.getResourceContent());
        Assert.assertEquals(resource.getAttributes().size(), expectedNumAttributes);
        for (AttributeType attribute: resource.getAttributes()) {
            Assert.assertEquals(attribute.getAttributeId(), expectedAttributeId);
            Assert.assertEquals(attribute.getDataType(), expectedDataType);
        }
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsMarshall() {
        ResourceType resource = new ResourceTypeImplBuilder().buildObject();

        resource.setResourceContent((ResourceContentType) buildXMLObject(ResourceContentType.DEFAULT_ELEMENT_NAME));
        for (int i = 0; i < expectedNumAttributes; i++) {
            AttributeType attribute = buildXMLObject(AttributeType.DEFAULT_ELEMENT_NAME);
            attribute.setAttributeID(expectedAttributeId);
            attribute.setDataType(expectedDataType);
            resource.getAttributes().add(attribute);
        }

        assertXMLEquals(expectedChildElementsDOM, resource);
    }

}