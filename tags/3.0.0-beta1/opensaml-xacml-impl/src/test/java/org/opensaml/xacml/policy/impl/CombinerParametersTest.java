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
import org.opensaml.xacml.policy.CombinerParameterType;
import org.opensaml.xacml.policy.CombinerParametersType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.xacml.policy.CombinerParametersType}.
 */
public class CombinerParametersTest extends XMLObjectProviderBaseTestCase {
    
    static final private String[] expectedParameterNames = {"nameParameters-0", "nameParameters-1", "nameParameters-2"}; 
    /**
     * Constructor
     */
    public CombinerParametersTest(){
        singleElementFile = "/data/org/opensaml/xacml/policy/impl/CombinerParameters.xml";
        childElementsFile = "/data/org/opensaml/xacml/policy/impl/CombinerParametersChildElements.xml";
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        CombinerParametersType combiners = (CombinerParametersType) unmarshallElement(singleElementFile);

        Assert.assertTrue(combiners.getCombinerParameters().isEmpty());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        CombinerParametersType combiners = (new CombinerParametersTypeImplBuilder()).buildObject();

        assertXMLEquals(expectedDOM, combiners );
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        CombinerParametersType combiners = (new CombinerParametersTypeImplBuilder()).buildObject();
        
        for (String paramterName: expectedParameterNames) {
            CombinerParameterType combiner = (new CombinerParameterTypeImplBuilder()).buildObject();
            combiner.setParameterName(paramterName);
            combiners.getCombinerParameters().add(combiner);
        }

        assertXMLEquals(expectedChildElementsDOM, combiners );
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        CombinerParametersType combiners = (CombinerParametersType) unmarshallElement(childElementsFile);

        Assert.assertEquals(combiners.getCombinerParameters().size(), expectedParameterNames.length);
    }
}