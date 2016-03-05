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
import org.opensaml.xacml.policy.AttributeSelectorType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.xacml.policy.AttributeSelectorType}.
 */
public class AttributeSelectorTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedDataType;
    private String expectedRequestContextPath;
    private Boolean optionalMustBePresent;
    /**
     * Constructor
     */
    public AttributeSelectorTest(){
        singleElementFile = "/org/opensaml/xacml/policy/impl/AttributeSelector.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/xacml/policy/impl/AttributeSelectorOptionalAttributes.xml";

        expectedDataType = "https://example.org/Data/Attribute/Selector";
        expectedRequestContextPath = "ConextPathAttrSelect";
        optionalMustBePresent = true;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
       AttributeSelectorType attributeSelector = (AttributeSelectorType) unmarshallElement(singleElementFile);

        Assert.assertEquals(attributeSelector.getDataType(), expectedDataType);
        Assert.assertEquals(attributeSelector.getRequestContextPath(), expectedRequestContextPath);
        Assert.assertFalse(attributeSelector.getMustBePresent());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        AttributeSelectorType attributeSelector = (new AttributeSelectorTypeImplBuilder()).buildObject();
        
        Assert.assertFalse(attributeSelector.getMustBePresent());
        attributeSelector.setDataType(expectedDataType);
        attributeSelector.setRequestContextPath(expectedRequestContextPath);
        attributeSelector.setMustBePresentXSBoolean(null);
        Assert.assertFalse(attributeSelector.getMustBePresent());
        attributeSelector.setMustBePresent(true);
        Assert.assertTrue(attributeSelector.getMustBePresent());
        attributeSelector.setMustBePresent(null);
        assertXMLEquals(expectedDOM, attributeSelector );
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        AttributeSelectorType attributeSelector = (AttributeSelectorType) unmarshallElement(singleElementOptionalAttributesFile);

        Assert.assertEquals(attributeSelector.getDataType(), expectedDataType);
        Assert.assertEquals(attributeSelector.getRequestContextPath(), expectedRequestContextPath);
        Assert.assertEquals(attributeSelector.getMustBePresent(), optionalMustBePresent);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall(){
        AttributeSelectorType attributeSelector = (new AttributeSelectorTypeImplBuilder()).buildObject();
        
        attributeSelector.setDataType(expectedDataType);
        attributeSelector.setRequestContextPath(expectedRequestContextPath);
        attributeSelector.setMustBePresent(optionalMustBePresent);
        assertXMLEquals(expectedOptionalAttributesDOM, attributeSelector );
    }
}