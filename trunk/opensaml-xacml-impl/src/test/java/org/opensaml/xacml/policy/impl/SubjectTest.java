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
import org.opensaml.xacml.policy.SubjectMatchType;
import org.opensaml.xacml.policy.SubjectType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link link org.opensaml.xacml.policy.SubjectType}.
 */
public class SubjectTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedMatchId;
    private int expectedResourceMatches;
    
    /**
     * Constructor
     */
    public SubjectTest(){
        singleElementFile = "/data/org/opensaml/xacml/policy/impl/Subject.xml";
        childElementsFile  = "/data/org/opensaml/xacml/policy/impl/SubjectChildElements.xml";

        expectedMatchId = "http://example.org/Subject/Match/Id";
        expectedResourceMatches = 2;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall(){
        SubjectType subject = (SubjectType) unmarshallElement(singleElementFile);
        
        Assert.assertTrue(subject.getSubjectMatches().isEmpty());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        SubjectType subject = (new SubjectTypeImplBuilder()).buildObject();
        
        assertXMLEquals(expectedDOM, subject);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall(){
        SubjectType subject = (SubjectType) unmarshallElement(childElementsFile);
        
        Assert.assertEquals(subject.getSubjectMatches().size(), expectedResourceMatches);
        
        for (SubjectMatchType subjectMatch : subject.getSubjectMatches()) {
            Assert.assertEquals(subjectMatch.getMatchId(),expectedMatchId);
        }
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall(){
        SubjectType subject = new SubjectTypeImplBuilder().buildObject();
        SubjectMatchTypeImplBuilder builder = new SubjectMatchTypeImplBuilder();

        for (int i = 0; i < expectedResourceMatches; i++) {
            SubjectMatchType subjectMatch = builder.buildObject();
            subjectMatch.setMatchId(expectedMatchId);
            subject.getSubjectMatches().add(subjectMatch);
        }
       
        assertXMLEquals(expectedChildElementsDOM, subject);
    }
}