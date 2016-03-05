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
import org.opensaml.xacml.policy.AttributeSelectorType;
import org.opensaml.xacml.policy.AttributeValueType;
import org.opensaml.xacml.policy.ResourceMatchType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.xacml.policy.ResourceMatchType}.
 */
public class ResourceMatchTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedMatchId;
    private String expectedDataType;
    private String expectedAttributeId;
    private String expectedRequestContextPath;
    
    /**
     * Constructor
     */
    public ResourceMatchTest(){
        singleElementFile = "/org/opensaml/xacml/policy/impl/ResourceMatch.xml";
        childElementsFile  = "/org/opensaml/xacml/policy/impl/ResourceMatchChildElements.xml";

        expectedMatchId = "http://example.org/Resource/Match/Match/Id";
        expectedDataType="https://example.org/Resource/Match/Data/Type";
        expectedAttributeId="https://example.org/Resource/Match/Attribute/Id";
        expectedRequestContextPath="ResourceMatchAttrSelectConextPath";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall(){
        ResourceMatchType resourceMatch = (ResourceMatchType) unmarshallElement(singleElementFile);
        
        Assert.assertEquals(resourceMatch.getMatchId(), expectedMatchId);
        Assert.assertNull(resourceMatch.getAttributeValue());
        Assert.assertNull(resourceMatch.getResourceAttributeDesignator());
        Assert.assertNull(resourceMatch.getAttributeSelector());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        ResourceMatchType resourceMatch = (new ResourceMatchTypeImplBuilder()).buildObject();
        
        resourceMatch.setMatchId(expectedMatchId);
        
        assertXMLEquals(expectedDOM, resourceMatch);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall(){
        ResourceMatchType resourceMatch = (ResourceMatchType) unmarshallElement(childElementsFile);
        
        Assert.assertEquals(resourceMatch.getMatchId(), expectedMatchId);

        Assert.assertEquals(resourceMatch.getAttributeValue().getDataType(), expectedDataType);
        
        Assert.assertEquals(resourceMatch.getResourceAttributeDesignator().getAttributeId(), expectedAttributeId);
        Assert.assertEquals(resourceMatch.getResourceAttributeDesignator().getDataType(), expectedDataType);
        
        Assert.assertEquals(resourceMatch.getAttributeSelector().getDataType(), expectedDataType);
        Assert.assertEquals(resourceMatch.getAttributeSelector().getRequestContextPath(), expectedRequestContextPath);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall(){
        ResourceMatchType resourceMatch = (new ResourceMatchTypeImplBuilder()).buildObject();
        resourceMatch.setMatchId(expectedMatchId);
        
        AttributeValueType attributeValue = new AttributeValueTypeImplBuilder().buildObject();
        attributeValue.setDataType(expectedDataType);
        resourceMatch.setAttributeValue(attributeValue);
        
        AttributeDesignatorType attributeDesignator = buildXMLObject(AttributeDesignatorType.RESOURCE_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME);
        attributeDesignator.setAttributeId(expectedAttributeId+"p");
        attributeDesignator.setDataType(expectedDataType+"*");
        attributeDesignator.setMustBePresent(true);
        resourceMatch.setResourceAttributeDesignator(attributeDesignator);
        
        attributeDesignator = buildXMLObject(AttributeDesignatorType.RESOURCE_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME);
        attributeDesignator.setAttributeId(expectedAttributeId);
        attributeDesignator.setDataType(expectedDataType);
        attributeDesignator.setMustBePresent(null);
        resourceMatch.setResourceAttributeDesignator(attributeDesignator);
        
        AttributeSelectorType attributeSelector = new AttributeSelectorTypeImplBuilder().buildObject();
        attributeSelector.setDataType(expectedDataType+"7");
        attributeSelector.setRequestContextPath(expectedRequestContextPath+"7");
        attributeSelector.setMustBePresent(true);
        resourceMatch.setAttributeSelector(attributeSelector);
        
        attributeSelector = new AttributeSelectorTypeImplBuilder().buildObject();
        attributeSelector.setDataType(expectedDataType);
        attributeSelector.setRequestContextPath(expectedRequestContextPath);
        attributeSelector.setMustBePresentXSBoolean(null);
        resourceMatch.setAttributeSelector(attributeSelector);
        
        assertXMLEquals(expectedChildElementsDOM, resourceMatch);
    }
}