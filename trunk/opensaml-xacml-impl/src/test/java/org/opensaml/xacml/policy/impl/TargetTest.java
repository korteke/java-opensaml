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
import org.opensaml.xacml.policy.TargetType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link link org.opensaml.xacml.policy.TargetType}.
 */
public class TargetTest extends XMLObjectProviderBaseTestCase {
    
    /**
     * Constructor
     */
    public TargetTest(){
        singleElementFile = "/data/org/opensaml/xacml/policy/impl/Target.xml";
        childElementsFile  = "/data/org/opensaml/xacml/policy/impl/TargetChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall(){
        TargetType target = (TargetType) unmarshallElement(singleElementFile);
        
        Assert.assertNull(target.getSubjects());
        Assert.assertNull(target.getResources());
        Assert.assertNull(target.getActions());
        Assert.assertNull(target.getEnvironments());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        TargetType target = (new TargetTypeImplBuilder()).buildObject();
        
        assertXMLEquals(expectedDOM, target);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall(){
        TargetType target = (TargetType) unmarshallElement(childElementsFile);
        
        Assert.assertNotNull(target.getSubjects());
        Assert.assertNotNull(target.getResources());
        Assert.assertNotNull(target.getActions());
        Assert.assertNotNull(target.getEnvironments());
        
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall(){
        TargetType target = new TargetTypeImplBuilder().buildObject();

        target.setActions(new ActionsTypeImplBuilder().buildObject());
        target.setEnvironments(new EnvironmentsTypeImplBuilder().buildObject());
        target.setSubjects(new SubjectsTypeImplBuilder().buildObject());
        target.setResources(new ResourcesTypeImplBuilder().buildObject());

        assertXMLEquals(expectedChildElementsDOM, target);
    }
}