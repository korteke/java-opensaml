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
import org.opensaml.xacml.policy.AttributeValueType;
import org.opensaml.xacml.policy.CombinerParameterType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.xacml.policy.CombinerParameterType}.
 */
public class CombinerParameterTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedDataType;
    private String expectedParameterName;
    /**
     * Constructor
     */
    public CombinerParameterTest(){
        singleElementFile = "/data/org/opensaml/xacml/policy/impl/CombinerParameter.xml";
        childElementsFile = "/data/org/opensaml/xacml/policy/impl/CombinerParameterChildElements.xml";

        expectedDataType = "https://example.org/Combiner/Parameter/Type";
        expectedParameterName = "nameParameter";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        CombinerParameterType combiner = (CombinerParameterType) unmarshallElement(singleElementFile);

        Assert.assertEquals(combiner.getParameterName(), expectedParameterName);
        Assert.assertNull(combiner.getAttributeValue());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        CombinerParameterType combiner = (new CombinerParameterTypeImplBuilder()).buildObject();
        
        combiner.setParameterName(expectedParameterName);
        assertXMLEquals(expectedDOM, combiner );
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        CombinerParameterType combiner = (new CombinerParameterTypeImplBuilder()).buildObject();
        
        combiner.setParameterName(expectedParameterName);
        AttributeValueType attributeValue = (new AttributeValueTypeImplBuilder()).buildObject();
        attributeValue.setDataType(expectedDataType);
        combiner.setAttributeValue(attributeValue);
        assertXMLEquals(expectedChildElementsDOM, combiner );
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        CombinerParameterType combiner = (CombinerParameterType) unmarshallElement(childElementsFile);

        Assert.assertEquals(combiner.getParameterName(), expectedParameterName);
        Assert.assertEquals(combiner.getAttributeValue().getDataType(), expectedDataType);
    }
}