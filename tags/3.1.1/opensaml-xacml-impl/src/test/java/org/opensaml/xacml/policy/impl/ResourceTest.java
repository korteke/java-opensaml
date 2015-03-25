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
import org.opensaml.xacml.policy.ResourceMatchType;
import org.opensaml.xacml.policy.ResourceType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.xacml.policy.ResourceType}.
 */
public class ResourceTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedMatchId;
    private int expectedResourceMatches;
    
    /**
     * Constructor
     */
    public ResourceTest(){
        singleElementFile = "/data/org/opensaml/xacml/policy/impl/Resource.xml";
        childElementsFile  = "/data/org/opensaml/xacml/policy/impl/ResourceChildElements.xml";

        expectedMatchId = "http://example.org/Resource/Match/Id";
        expectedResourceMatches = 4;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall(){
        ResourceType resource = (ResourceType) unmarshallElement(singleElementFile);
        
        Assert.assertTrue(resource.getResourceMatches().isEmpty());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        ResourceType resource = (new ResourceTypeImplBuilder()).buildObject();
        
        assertXMLEquals(expectedDOM, resource);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall(){
        ResourceType resource = (ResourceType) unmarshallElement(childElementsFile);
        
        Assert.assertEquals(resource.getResourceMatches().size(), expectedResourceMatches);
        
        for (ResourceMatchType resourceMatch : resource.getResourceMatches()) {
            Assert.assertEquals(resourceMatch.getMatchId(),expectedMatchId);
        }
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall(){
        ResourceType resource = new ResourceTypeImplBuilder().buildObject();
        ResourceMatchTypeImplBuilder builder = new ResourceMatchTypeImplBuilder();

        for (int i = 0; i < expectedResourceMatches; i++) {
            ResourceMatchType resourceMatch = builder.buildObject();
            resourceMatch.setMatchId(expectedMatchId);
            resource.getResourceMatches().add(resourceMatch);
        }
       
        assertXMLEquals(expectedChildElementsDOM, resource);
    }
}