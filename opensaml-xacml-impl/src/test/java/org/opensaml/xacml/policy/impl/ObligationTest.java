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
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.ObligationType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.xacml.policy.ObligationType}.
 */
public class ObligationTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedObligationId;
    private EffectType expectedFulFillOn;
    private int expectedAttributeAssignments = 2;
    private String expectedDataType;
    private String expectedAttributeId;
    
    /**
     * Constructor
     */
    public ObligationTest(){
        singleElementFile = "/org/opensaml/xacml/policy/impl/Obligation.xml";
        childElementsFile  = "/org/opensaml/xacml/policy/impl/ObligationChildElements.xml";
        
        expectedObligationId = "http://example.org/Obligation/Id";
        expectedFulFillOn = EffectType.Deny;
        expectedAttributeAssignments = 2;
        expectedDataType = "https://example.org/Obligation/Id/Data/Type";
        expectedAttributeId = "https://example.org/Obligation/Id/Attribute/Id";
    }
    

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall(){
        ObligationType obligation = (ObligationType) unmarshallElement(singleElementFile);
        
        Assert.assertEquals(obligation.getObligationId(), expectedObligationId);
        Assert.assertEquals(obligation.getFulfillOn(), expectedFulFillOn);

        Assert.assertTrue(obligation.getAttributeAssignments().isEmpty());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        ObligationType obligation = (new ObligationTypeImplBuilder()).buildObject();
        
        obligation.setObligationId(expectedObligationId);
        obligation.setFulfillOn(expectedFulFillOn);
        
        assertXMLEquals(expectedDOM, obligation);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall(){
        ObligationType obligation = (ObligationType) unmarshallElement(childElementsFile);
        
        Assert.assertEquals(obligation.getObligationId(), expectedObligationId);
        Assert.assertEquals(obligation.getFulfillOn(), expectedFulFillOn);
        Assert.assertEquals(obligation.getAttributeAssignments().size(), expectedAttributeAssignments);
        
        for (AttributeAssignmentType attributeAssignment: obligation.getAttributeAssignments()) {
            Assert.assertEquals(attributeAssignment.getAttributeId(), expectedAttributeId);
            Assert.assertEquals(attributeAssignment.getDataType(), expectedDataType);
        }
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall(){
        ObligationType obligation = (new ObligationTypeImplBuilder()).buildObject();
        AttributeAssignmentTypeImplBuilder builder = new AttributeAssignmentTypeImplBuilder();

        obligation.setObligationId(expectedObligationId);
        obligation.setFulfillOn(expectedFulFillOn);
        for (int i = 0; i < expectedAttributeAssignments; i++) {
            AttributeAssignmentType attributeAssignment = builder.buildObject();
            attributeAssignment.setAttributeId(expectedAttributeId);
            attributeAssignment.setDataType(expectedDataType);
            obligation.getAttributeAssignments().add(attributeAssignment);
        }

        assertXMLEquals(expectedChildElementsDOM, obligation);
}
}