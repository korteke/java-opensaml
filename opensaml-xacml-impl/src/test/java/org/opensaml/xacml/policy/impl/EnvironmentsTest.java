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
import org.opensaml.xacml.policy.EnvironmentType;
import org.opensaml.xacml.policy.EnvironmentsType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.xacml.policy.EnvironmentsType}.
 */
public class EnvironmentsTest extends XMLObjectProviderBaseTestCase {
    
    private int expectedNumChildren;
    
    /**
     * Constructor
     */
    public EnvironmentsTest(){
        singleElementFile = "/org/opensaml/xacml/policy/impl/Environments.xml";
        childElementsFile = "/org/opensaml/xacml/policy/impl/EnvironmentsChildElements.xml";
        
        expectedNumChildren = 4;
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        EnvironmentsType environments = (EnvironmentsType) unmarshallElement(singleElementFile);

        Assert.assertTrue(environments.getEnvironments().isEmpty());
   }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        EnvironmentsType environments = new EnvironmentsTypeImplBuilder().buildObject();
        
        assertXMLEquals(expectedDOM, environments);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        EnvironmentsType environments = new EnvironmentsTypeImplBuilder().buildObject();
        EnvironmentTypeImplBuilder builder = new EnvironmentTypeImplBuilder();
        
        for (int i = 0; i < expectedNumChildren; i++) {
            environments.getEnvironments().add(builder.buildObject());
        }
        
        assertXMLEquals(expectedChildElementsDOM, environments);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        EnvironmentsType environments = (EnvironmentsType) unmarshallElement(childElementsFile);
        
        Assert.assertEquals(environments.getEnvironments().size(), expectedNumChildren);
        
        for (EnvironmentType environment: environments.getEnvironments()) {
        
            Assert.assertTrue(environment.getEnvrionmentMatches().isEmpty());
        }
   }
}