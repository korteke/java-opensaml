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
import org.opensaml.xacml.policy.ApplyType;
import org.opensaml.xacml.policy.AttributeDesignatorType;
import org.opensaml.xacml.policy.AttributeSelectorType;
import org.opensaml.xacml.policy.AttributeValueType;
import org.opensaml.xacml.policy.ConditionType;
import org.opensaml.xacml.policy.ExpressionType;
import org.opensaml.xacml.policy.FunctionType;
import org.opensaml.xacml.policy.VariableReferenceType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.xacml.policy.ApplyType}.
 */
public class ApplyTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected ProfileURI */
    private String expectedFunctionId;
    
    /**
     * Constructor
     */
    public ApplyTest(){
        singleElementFile = "/data/org/opensaml/xacml/policy/impl/Apply.xml";
        childElementsFile  = "/data/org/opensaml/xacml/policy/impl/ApplyChildElements.xml";

        expectedFunctionId = "http://example.org";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall(){
        ApplyType apply = (ApplyType) unmarshallElement(singleElementFile);
        
        Assert.assertEquals(apply.getFunctionId(), expectedFunctionId, "MatchId URI has a value of " + apply.getFunctionId() + ", expected a value of " + expectedFunctionId);
        Assert.assertTrue(apply.getExpressions().isEmpty());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        ApplyType apply = (new ApplyTypeImplBuilder()).buildObject();
        
        apply.setFunctionId(expectedFunctionId);
        
        assertXMLEquals(expectedDOM, apply);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall(){
        ApplyType apply = (ApplyType) unmarshallElement(childElementsFile);
        
        Assert.assertEquals(apply.getFunctionId(), expectedFunctionId, "FunctionId URI has a value of " + apply.getFunctionId() + ", expected a value of " + expectedFunctionId);
        Assert.assertEquals(apply.getExpressions().size(), 9);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall(){
        ApplyType apply = (new ApplyTypeImplBuilder()).buildObject();
        apply.setFunctionId(expectedFunctionId);
        
        apply.getExpressions().add((ExpressionType)buildXMLObject(FunctionType.DEFAULT_ELEMENT_NAME));
        apply.getExpressions().add((ExpressionType)buildXMLObject(AttributeValueType.DEFAULT_ELEMENT_NAME));
        apply.getExpressions().add((ExpressionType)buildXMLObject(ConditionType.DEFAULT_ELEMENT_NAME));
        
        apply.getExpressions().add((ExpressionType)buildXMLObject(AttributeValueType.DEFAULT_ELEMENT_NAME));
        apply.getExpressions().add((ExpressionType)buildXMLObject(VariableReferenceType.DEFAULT_ELEMENT_NAME_XACML20));
        apply.getExpressions().add((ExpressionType)buildXMLObject(AttributeDesignatorType.ACTION_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME));
        
        apply.getExpressions().add((ExpressionType)buildXMLObject(AttributeSelectorType.DEFAULT_ELEMENT_NAME));
        apply.getExpressions().add((ExpressionType)buildXMLObject(FunctionType.DEFAULT_ELEMENT_NAME));
        
        ApplyType other = (ApplyType)buildXMLObject(ApplyType.DEFAULT_ELEMENT_NAME);
        other.setFunctionId(expectedFunctionId);
        apply.getExpressions().add(other);
        
        assertXMLEquals(expectedChildElementsDOM, apply);
}
}