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
import org.opensaml.xacml.policy.ConditionType;
import org.opensaml.xacml.policy.FunctionType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.xacml.policy.ConditionType}.
 */
public class ConditionTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedFunctionId;
    
    /**
     * Constructor
     */
    public ConditionTest(){
        singleElementFile = "/org/opensaml/xacml/policy/impl/Condition.xml";
        childElementsFile = "/org/opensaml/xacml/policy/impl/ConditionChildElements.xml";
        
        expectedFunctionId = "https://example.org/Condition/Function/Id";
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        ConditionType condition = (ConditionType) unmarshallElement(singleElementFile);

        Assert.assertNull(condition.getExpression());
   }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        ConditionType condition = new ConditionTypeImplBuilder().buildObject();
        
        assertXMLEquals(expectedDOM, condition);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        ConditionType condition = new ConditionTypeImplBuilder().buildObject();

        FunctionType func = new FunctionTypeImplBuilder().buildObject();
        func.setFunctionId(expectedFunctionId);
        condition.setExpression(func);
        
        
        assertXMLEquals(expectedChildElementsDOM, condition);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        ConditionType condition = (ConditionType) unmarshallElement(childElementsFile);
        
        FunctionType func = (FunctionType) condition.getExpression();
        
        Assert.assertEquals(func.getFunctionId(), expectedFunctionId);
   }
}