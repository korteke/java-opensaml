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
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.ObligationType;
import org.opensaml.xacml.policy.ObligationsType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link link org.opensaml.xacml.policy.ObligationsType}.
 */
public class ObligationsTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedObligationId;
    private EffectType expectedFulFillOn;
    private int expectedObligations;
    
    /**
     * Constructor
     */
    public ObligationsTest(){
        singleElementFile = "/data/org/opensaml/xacml/policy/impl/Obligations.xml";
        childElementsFile  = "/data/org/opensaml/xacml/policy/impl/ObligationsChildElements.xml";
        
        expectedObligationId = "http://example.org/Obligations/Id";
        expectedFulFillOn = EffectType.Permit;
        expectedObligations = 5;
    }
    

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall(){
        ObligationsType obligations = (ObligationsType) unmarshallElement(singleElementFile);

        Assert.assertTrue(obligations.getObligations().isEmpty());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        ObligationsType obligations = (new ObligationsTypeImplBuilder()).buildObject();
                
        assertXMLEquals(expectedDOM, obligations);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall(){
        ObligationsType obligations = (ObligationsType) unmarshallElement(childElementsFile);
        
        for (ObligationType obligation: obligations.getObligations()) {
            Assert.assertEquals(obligation.getObligationId(), expectedObligationId);
            Assert.assertEquals(obligation.getFulfillOn(), expectedFulFillOn);
            Assert.assertTrue(obligation.getAttributeAssignments().isEmpty());
        }
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall(){
        ObligationsType obligations = (new ObligationsTypeImplBuilder()).buildObject();
        ObligationTypeImplBuilder builder = new ObligationTypeImplBuilder();

        for (int i = 0; i < expectedObligations; i++) {
            ObligationType obligation = builder.buildObject();
            obligation.setObligationId(expectedObligationId);
            obligation.setFulfillOn(expectedFulFillOn);
            obligations.getObligations().add(obligation);
        }

        assertXMLEquals(expectedChildElementsDOM, obligations);
}
}