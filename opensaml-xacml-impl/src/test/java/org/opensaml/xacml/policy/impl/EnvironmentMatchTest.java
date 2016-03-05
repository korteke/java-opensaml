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
import org.opensaml.xacml.policy.EnvironmentMatchType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.xacml.policy.EnvironmentMatchType}.
 */
public class EnvironmentMatchTest extends XMLObjectProviderBaseTestCase {

    private String expectedMatchId;

    private String expectedDataType;

    private String expectedAttributeId;

    private String expectedRequestContextPath;

    /**
     * Constructor
     */
    public EnvironmentMatchTest() {
        singleElementFile = "/org/opensaml/xacml/policy/impl/EnvironmentMatch.xml";
        childElementsFile = "/org/opensaml/xacml/policy/impl/EnvironmentMatchChildElements.xml";

        expectedMatchId = "https://example.org/Environment/Match/Id";
        expectedDataType = "https://example.org/Environment/Match/Data/Type";
        expectedAttributeId = "https://example.org/Environment/Match/Attribute/Id";
        expectedRequestContextPath = "ConextPathAttrSelect";
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnmarshall() {
        EnvironmentMatchType environmentMatch = (EnvironmentMatchType) unmarshallElement(singleElementFile);

        Assert.assertEquals(environmentMatch.getMatchId(), expectedMatchId);
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementMarshall() {
        EnvironmentMatchType environmentMatch = new EnvironmentMatchTypeImplBuilder().buildObject();

        environmentMatch.setMatchId(expectedMatchId);
        assertXMLEquals(expectedDOM, environmentMatch);
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsMarshall() {
        EnvironmentMatchType environmentMatch = new EnvironmentMatchTypeImplBuilder().buildObject();
        environmentMatch.setMatchId(expectedMatchId);

        AttributeValueType attrValue = new AttributeValueTypeImplBuilder().buildObject();
        attrValue.setDataType(expectedDataType);
        environmentMatch.setAttributeValue(attrValue);

        AttributeDesignatorType attrDesignator =
                (AttributeDesignatorType) buildXMLObject(AttributeDesignatorType.ENVIRONMENT_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME);
        attrDesignator.setAttributeId(expectedAttributeId+"g");
        attrDesignator.setDataType(expectedDataType+"t");
        attrDesignator.setMustBePresent(true);
        environmentMatch.setEnvironmentAttributeDesignator(attrDesignator);
        attrDesignator =
                (AttributeDesignatorType) buildXMLObject(AttributeDesignatorType.ENVIRONMENT_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME);
        attrDesignator.setAttributeId(expectedAttributeId);
        attrDesignator.setDataType(expectedDataType);
        attrDesignator.setMustBePresentXSBoolean(null);
        environmentMatch.setEnvironmentAttributeDesignator(attrDesignator);

        AttributeSelectorType attrSelector = new AttributeSelectorTypeImplBuilder().buildObject();
        attrSelector.setRequestContextPath(expectedRequestContextPath + "ds");
        attrSelector.setDataType(expectedDataType + "*");
        attrSelector.setMustBePresent(true);
        environmentMatch.setAttributeSelector(attrSelector);
        attrSelector = new AttributeSelectorTypeImplBuilder().buildObject();
        attrSelector.setRequestContextPath(expectedRequestContextPath);
        attrSelector.setDataType(expectedDataType);
        attrSelector.setMustBePresentXSBoolean(null);
        environmentMatch.setAttributeSelector(attrSelector);

        assertXMLEquals(expectedChildElementsDOM, environmentMatch);
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsUnmarshall() {
        EnvironmentMatchType environmentMatch = (EnvironmentMatchType) unmarshallElement(childElementsFile);

        Assert.assertEquals(environmentMatch.getMatchId(), expectedMatchId);

        AttributeValueType attrValue = environmentMatch.getAttributeValue();
        Assert.assertEquals(attrValue.getDataType(), expectedDataType);

        AttributeDesignatorType attrDesignator = environmentMatch.getEnvironmentAttributeDesignator();
        Assert.assertEquals(attrDesignator.getAttributeId(), expectedAttributeId);
        Assert.assertEquals(attrDesignator.getDataType(), expectedDataType);
        attrDesignator.setMustBePresent(null);

        AttributeSelectorType attrSelector = environmentMatch.getAttributeSelector();
        Assert.assertEquals(attrSelector.getRequestContextPath(), expectedRequestContextPath);
        Assert.assertEquals(attrSelector.getDataType(), expectedDataType);
    }
}