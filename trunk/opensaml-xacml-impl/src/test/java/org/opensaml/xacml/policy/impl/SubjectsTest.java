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
import org.opensaml.xacml.policy.SubjectsType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link link org.opensaml.xacml.policy.SubjectsType}.
 */
public class SubjectsTest extends XMLObjectProviderBaseTestCase {
    
    private int expectedSubjects;
    
    /**
     * Constructor
     */
    public SubjectsTest(){
        singleElementFile = "/data/org/opensaml/xacml/policy/impl/Subjects.xml";
        childElementsFile  = "/data/org/opensaml/xacml/policy/impl/SubjectsChildElements.xml";

        expectedSubjects = 12;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall(){
        SubjectsType subjects = (SubjectsType) unmarshallElement(singleElementFile);
        
        Assert.assertTrue(subjects.getSubjects().isEmpty());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        SubjectsType subjects = (new SubjectsTypeImplBuilder()).buildObject();
        
        assertXMLEquals(expectedDOM, subjects);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall(){
        SubjectsType subjects = (SubjectsType) unmarshallElement(childElementsFile);
        
        Assert.assertEquals(subjects.getSubjects().size(), expectedSubjects);
        
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall(){
        SubjectsType subjects = new SubjectsTypeImplBuilder().buildObject();
        SubjectTypeImplBuilder builder = new SubjectTypeImplBuilder();

        for (int i = 0; i < expectedSubjects; i++) {
            subjects.getSubjects().add(builder.buildObject());
        }
       
        assertXMLEquals(expectedChildElementsDOM, subjects);
    }
}