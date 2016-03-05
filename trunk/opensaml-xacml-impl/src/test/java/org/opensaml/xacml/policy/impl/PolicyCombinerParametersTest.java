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
import org.opensaml.xacml.policy.PolicyCombinerParametersType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.xacml.policy.PolicyCombinerParametersType}.
 */
public class PolicyCombinerParametersTest extends XMLObjectProviderBaseTestCase {
    
    static private String expectedPolicyIdRef; 
    /**
     * Constructor
     */
    public PolicyCombinerParametersTest(){
        singleElementFile = "/org/opensaml/xacml/policy/impl/PolicyCombinerParameters.xml";
        
        expectedPolicyIdRef = "https://example.org/Policy/Id/Ref";
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        PolicyCombinerParametersType policyCombiners = (PolicyCombinerParametersType) unmarshallElement(singleElementFile);

        Assert.assertEquals(policyCombiners.getPolicyIdRef(), expectedPolicyIdRef);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        PolicyCombinerParametersType policyCombiners = (new PolicyCombinerParametersTypeImplBuilder()).buildObject();
        policyCombiners.setPolicyIdRef(expectedPolicyIdRef);
        assertXMLEquals(expectedDOM, policyCombiners );
    }
    
}