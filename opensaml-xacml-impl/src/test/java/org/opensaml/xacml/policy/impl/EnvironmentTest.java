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
import org.opensaml.xacml.policy.EnvironmentMatchType;
import org.opensaml.xacml.policy.EnvironmentType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.xacml.policy.EnvironmentType}.
 */
public class EnvironmentTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedMatchId;
    private int expectedNumChildren;
    
    /**
     * Constructor
     */
    public EnvironmentTest(){
        singleElementFile = "/data/org/opensaml/xacml/policy/impl/Environment.xml";
        childElementsFile = "/data/org/opensaml/xacml/policy/impl/EnvironmentChildElements.xml";
        
        expectedMatchId = "https://example.org/Environment/Id";
        expectedNumChildren = 7;
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        EnvironmentType environment = (EnvironmentType) unmarshallElement(singleElementFile);

        Assert.assertTrue(environment.getEnvrionmentMatches().isEmpty());
   }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        EnvironmentType environment = new EnvironmentTypeImplBuilder().buildObject();
        
        assertXMLEquals(expectedDOM, environment);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        EnvironmentType environment = new EnvironmentTypeImplBuilder().buildObject();
        EnvironmentMatchTypeImplBuilder builder = new EnvironmentMatchTypeImplBuilder();
        
        for (int i = 0; i < expectedNumChildren; i++) {
            EnvironmentMatchType environmentMatch = builder.buildObject();
            environmentMatch.setMatchId(expectedMatchId);
            environment.getEnvrionmentMatches().add(environmentMatch);
        }
        
        assertXMLEquals(expectedChildElementsDOM, environment);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        EnvironmentType environment = (EnvironmentType) unmarshallElement(childElementsFile);
        
        Assert.assertEquals(environment.getEnvrionmentMatches().size(), expectedNumChildren);
        
        for (EnvironmentMatchType environmentMatch: environment.getEnvrionmentMatches()) {
        
            Assert.assertEquals(environmentMatch.getMatchId(), expectedMatchId);
        }
   }
}