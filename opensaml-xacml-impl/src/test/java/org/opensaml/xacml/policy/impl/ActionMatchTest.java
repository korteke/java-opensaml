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
import org.opensaml.xacml.policy.ActionMatchType;
import org.opensaml.xacml.policy.AttributeDesignatorType;
import org.opensaml.xacml.policy.AttributeSelectorType;
import org.opensaml.xacml.policy.AttributeValueType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link link org.opensaml.xacml.policy.ActionMatchType}.
 */
public class ActionMatchTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected ProfileURI */
    private String expectedMatchId;
    
    /**
     * Constructor
     */
    public ActionMatchTest(){
        singleElementFile = "/data/org/opensaml/xacml/policy/impl/ActionMatch.xml";
        childElementsFile  = "/data/org/opensaml/xacml/policy/impl/ActionMatchChildElements.xml";

        expectedMatchId = "http://example.org";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall(){
        ActionMatchType actionMatch = (ActionMatchType) unmarshallElement(singleElementFile);
        
        Assert.assertEquals(actionMatch.getMatchId(), expectedMatchId, "MatchId URI has a value of " + actionMatch.getMatchId() + ", expected a value of " + expectedMatchId);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        ActionMatchType actionMatch = (new ActionMatchTypeImplBuilder()).buildObject();
        
        actionMatch.setMatchId(expectedMatchId);
        
        assertXMLEquals(expectedDOM, actionMatch);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall(){
        ActionMatchType actionMatch = (ActionMatchType) unmarshallElement(childElementsFile);
        
        Assert.assertNotNull(actionMatch.getActionAttributeDesignator());
        Assert.assertNotNull(actionMatch.getAttributeSelector());
        Assert.assertNotNull(actionMatch.getAttributeValue());
        
        Assert.assertEquals(actionMatch.getMatchId(), expectedMatchId, "MatchId URI has a value of " + actionMatch.getMatchId() + ", expected a value of " + expectedMatchId);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall(){
        ActionMatchType actionMatch = (new ActionMatchTypeImplBuilder()).buildObject();
        actionMatch.setMatchId(expectedMatchId);
        
        AttributeDesignatorType attributeDesignatorType = (AttributeDesignatorType) buildXMLObject(AttributeDesignatorType.ACTION_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME);
        attributeDesignatorType.setMustBePresent(true);
        actionMatch.setActionAttributeDesignator(attributeDesignatorType);
        
        attributeDesignatorType = (AttributeDesignatorType) buildXMLObject(AttributeDesignatorType.ACTION_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME);
        attributeDesignatorType.setMustBePresent(false);
        actionMatch.setActionAttributeDesignator(attributeDesignatorType);

        actionMatch.setAttributeSelector((AttributeSelectorType) buildXMLObject(AttributeSelectorType.DEFAULT_ELEMENT_NAME));
        actionMatch.setAttributeSelector((AttributeSelectorType) buildXMLObject(AttributeSelectorType.DEFAULT_ELEMENT_NAME));
        actionMatch.setAttributeValue((AttributeValueType) buildXMLObject(AttributeValueType.DEFAULT_ELEMENT_NAME));

        assertXMLEquals(expectedChildElementsDOM, actionMatch);
}
}