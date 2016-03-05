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
import org.opensaml.xacml.policy.SubjectAttributeDesignatorType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.xacml.policy.SubjectAttributeDesignatorType}.
 */
public class SubjectAttributeDesignatorTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedDataType;
    private String expectedAttributeId;
    private String expectedSubjectCategory;
    private String optionalIssuer;
    private Boolean optionalMustBePresent;
    /**
     * Constructor
     */
    public SubjectAttributeDesignatorTest(){
        singleElementFile = "/org/opensaml/xacml/policy/impl/SubjectAttributeDesignator.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/xacml/policy/impl/SubjectAttributeDesignatorOptionalAttributes.xml";

        expectedDataType = "https://example.org/Data/Type/Subject";
        expectedAttributeId = "https://example.org/Attribute/Id/Subject";
        expectedSubjectCategory = "CategoryForSubject";
        optionalIssuer = "TheIssuerSubject";
        optionalMustBePresent = true;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        SubjectAttributeDesignatorType attributeDesignator = (SubjectAttributeDesignatorType) unmarshallElement(singleElementFile);

        Assert.assertEquals(attributeDesignator.getDataType(), expectedDataType);
        Assert.assertEquals(attributeDesignator.getAttributeId(), expectedAttributeId);
        Assert.assertEquals(attributeDesignator.getSubjectCategory(), expectedSubjectCategory);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        SubjectAttributeDesignatorType attributeDesignator = (new SubjectAttributeDesignatorTypeImplBuilder()).buildObject();
        
        Assert.assertFalse(attributeDesignator.getMustBePresent());
        attributeDesignator.setDataType(expectedDataType);
        attributeDesignator.setAttributeId(expectedAttributeId);
        attributeDesignator.setSubjectCategory(expectedSubjectCategory);
        attributeDesignator.setMustBePresent(null);
        assertXMLEquals(expectedDOM, attributeDesignator );
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        SubjectAttributeDesignatorType attributeDesignator = (SubjectAttributeDesignatorType) unmarshallElement(singleElementOptionalAttributesFile);

        Assert.assertEquals(attributeDesignator.getDataType(), expectedDataType);
        Assert.assertEquals(attributeDesignator.getAttributeId(), expectedAttributeId);
        Assert.assertEquals(attributeDesignator.getMustBePresent(), optionalMustBePresent);
        Assert.assertEquals(attributeDesignator.getSubjectCategory(), expectedSubjectCategory);
        Assert.assertEquals(attributeDesignator.getIssuer(), optionalIssuer);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall(){
        SubjectAttributeDesignatorType attributeDesignator = (new SubjectAttributeDesignatorTypeImplBuilder()).buildObject();
        
        attributeDesignator.setDataType(expectedDataType);
        attributeDesignator.setAttributeId(expectedAttributeId);
        attributeDesignator.setMustBePresent(optionalMustBePresent);
        attributeDesignator.setSubjectCategory(expectedSubjectCategory);
        attributeDesignator.setIssuer(optionalIssuer);
        assertXMLEquals(expectedOptionalAttributesDOM, attributeDesignator );
    }
}