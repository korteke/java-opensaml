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
import org.opensaml.xacml.policy.AttributeDesignatorType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.xacml.policy.AttributeDesignatorType} subtype Resource.
 */
public class ResourceAttributeDesignatorTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedDataType;
    private String expectedAttributeId;
    private String optionalIssuer;
    private Boolean optionalMustBePresent;
    /**
     * Constructor
     */
    public ResourceAttributeDesignatorTest(){
        singleElementFile = "/org/opensaml/xacml/policy/impl/ResourceAttributeDesignator.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/xacml/policy/impl/ResourceAttributeDesignatorOptionalAttributes.xml";

        expectedDataType = "https://example.org/Data/Type/Resource";
        expectedAttributeId = "https://example.org/Attribute/Id/Resource";
        optionalIssuer = "TheIssuerResource";
        optionalMustBePresent = true;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        AttributeDesignatorType attributeDesignator = (AttributeDesignatorType) unmarshallElement(singleElementFile);

        Assert.assertEquals(attributeDesignator.getDataType(), expectedDataType);
        Assert.assertEquals(attributeDesignator.getAttributeId(), expectedAttributeId);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        AttributeDesignatorType attributeDesignator = (AttributeDesignatorType) buildXMLObject(AttributeDesignatorType.RESOURCE_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME);
        
        attributeDesignator.setDataType(expectedDataType);
        attributeDesignator.setAttributeId(expectedAttributeId);
        attributeDesignator.setMustBePresent(null);
        assertXMLEquals(expectedDOM, attributeDesignator );
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        AttributeDesignatorType attributeDesignator = (AttributeDesignatorType) unmarshallElement(singleElementOptionalAttributesFile);

        Assert.assertEquals(attributeDesignator.getDataType(), expectedDataType);
        Assert.assertEquals(attributeDesignator.getAttributeId(), expectedAttributeId);
        Assert.assertEquals(attributeDesignator.getMustBePresent(), optionalMustBePresent);
        Assert.assertEquals(attributeDesignator.getIssuer(), optionalIssuer);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall(){
        AttributeDesignatorType attributeDesignator  = (AttributeDesignatorType) buildXMLObject(AttributeDesignatorType.RESOURCE_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME);
        
        attributeDesignator.setDataType(expectedDataType);
        attributeDesignator.setAttributeId(expectedAttributeId);
        attributeDesignator.setMustBePresent(optionalMustBePresent);
        attributeDesignator.setIssuer(optionalIssuer);
        assertXMLEquals(expectedOptionalAttributesDOM, attributeDesignator );
    }
}