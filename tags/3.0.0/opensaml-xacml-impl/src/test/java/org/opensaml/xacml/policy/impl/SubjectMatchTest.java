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
import org.opensaml.xacml.policy.AttributeDesignatorType;
import org.opensaml.xacml.policy.AttributeSelectorType;
import org.opensaml.xacml.policy.AttributeValueType;
import org.opensaml.xacml.policy.SubjectMatchType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.xacml.policy.ResourceMatchType}.
 */
public class SubjectMatchTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedMatchId;
    private String expectedDataType;
    private String expectedAttributeId;
    private String expectedRequestContextPath;
    
    /**
     * Constructor
     */
    public SubjectMatchTest(){
        singleElementFile = "/data/org/opensaml/xacml/policy/impl/SubjectMatch.xml";
        childElementsFile  = "/data/org/opensaml/xacml/policy/impl/SubjectMatchChildElements.xml";

        expectedMatchId = "http://example.org/Subject/Match/Match/Id";
        expectedDataType="https://example.org/Subject/Match/Data/Type";
        expectedAttributeId="https://example.org/Subject/Match/Attribute/Id";
        expectedRequestContextPath="SubjectMatchAttrSelectConextPath";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall(){
        SubjectMatchType subjectMatch = (SubjectMatchType) unmarshallElement(singleElementFile);
        
        Assert.assertEquals(subjectMatch.getMatchId(), expectedMatchId);
        Assert.assertNull(subjectMatch.getAttributeValue());
        Assert.assertNull(subjectMatch.getSubjectAttributeDesignator());
        Assert.assertNull(subjectMatch.getAttributeSelector());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        SubjectMatchType subjectMatch = (new SubjectMatchTypeImplBuilder()).buildObject();
        
        subjectMatch.setMatchId(expectedMatchId);
        
        assertXMLEquals(expectedDOM, subjectMatch);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall(){
        SubjectMatchType subjectMatch = (SubjectMatchType) unmarshallElement(childElementsFile);
        
        Assert.assertEquals(subjectMatch.getMatchId(), expectedMatchId);

        Assert.assertEquals(subjectMatch.getAttributeValue().getDataType(), expectedDataType);
        
        Assert.assertEquals(subjectMatch.getSubjectAttributeDesignator().getAttributeId(), expectedAttributeId);
        Assert.assertEquals(subjectMatch.getSubjectAttributeDesignator().getDataType(), expectedDataType);
        
        Assert.assertEquals(subjectMatch.getAttributeSelector().getDataType(), expectedDataType);
        Assert.assertEquals(subjectMatch.getAttributeSelector().getRequestContextPath(), expectedRequestContextPath);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall(){
        SubjectMatchType subjectMatch = (new SubjectMatchTypeImplBuilder()).buildObject();
        subjectMatch.setMatchId(expectedMatchId);
        
        AttributeValueType attributeValue = new AttributeValueTypeImplBuilder().buildObject();
        attributeValue.setDataType(expectedDataType);
        subjectMatch.setAttributeValue(attributeValue);
        
        AttributeDesignatorType attributeDesignator = buildXMLObject(AttributeDesignatorType.SUBJECT_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME);
        attributeDesignator.setAttributeId(expectedAttributeId+"*");
        attributeDesignator.setDataType(expectedDataType+"*");
        attributeDesignator.setMustBePresent(null);
        subjectMatch.setSubjectAttributeDesignator(attributeDesignator);
        attributeDesignator = buildXMLObject(AttributeDesignatorType.SUBJECT_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME);
        attributeDesignator.setAttributeId(expectedAttributeId);
        attributeDesignator.setDataType(expectedDataType);
        attributeDesignator.setMustBePresent(null);
        subjectMatch.setSubjectAttributeDesignator(attributeDesignator);
        
        AttributeSelectorType attributeSelector = new AttributeSelectorTypeImplBuilder().buildObject();
        attributeSelector.setDataType(expectedDataType+"*");
        attributeSelector.setRequestContextPath("*"+expectedRequestContextPath);
        attributeSelector.setMustBePresentXSBoolean(null);
        subjectMatch.setAttributeSelector(attributeSelector);
        attributeSelector = new AttributeSelectorTypeImplBuilder().buildObject();
        attributeSelector.setDataType(expectedDataType);
        attributeSelector.setRequestContextPath(expectedRequestContextPath);
        attributeSelector.setMustBePresentXSBoolean(null);
        subjectMatch.setAttributeSelector(attributeSelector);
                
        assertXMLEquals(expectedChildElementsDOM, subjectMatch);
    }
}