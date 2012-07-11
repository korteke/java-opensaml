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
import org.opensaml.xacml.policy.DescriptionType;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.RuleType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link link org.opensaml.xacml.policy.RuleType}.
 */
public class RuleTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected ProfileURI */
    private String expectedRuleId;
    private String expectedDescription;
    
    /**
     * Constructor
     */
    public RuleTest(){
        singleElementFile = "/data/org/opensaml/xacml/policy/impl/Rule.xml";
        childElementsFile  = "/data/org/opensaml/xacml/policy/impl/RuleChildElements.xml";

        expectedRuleId = "RuleRuleId";
        expectedDescription = "This is a Description";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall(){
        RuleType rule = (RuleType) unmarshallElement(singleElementFile);
        
        Assert.assertEquals(rule.getRuleId(), expectedRuleId);
        Assert.assertEquals(rule.getEffect(), EffectType.Permit);
        Assert.assertNull(rule.getDescription());
        Assert.assertNull(rule.getTarget());
        Assert.assertNull(rule.getCondition());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        RuleType rule = (new RuleTypeImplBuilder()).buildObject();
        rule.setRuleId(expectedRuleId);
        rule.setEffect(EffectType.Permit);
        
        assertXMLEquals(expectedDOM, rule);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall(){
        RuleType rule = (RuleType) unmarshallElement(childElementsFile);
        
        Assert.assertEquals(rule.getRuleId(), expectedRuleId);
        Assert.assertEquals(rule.getEffect(), EffectType.Deny);
        Assert.assertEquals(rule.getDescription().getValue(), expectedDescription);
        Assert.assertNotNull(rule.getTarget());
        Assert.assertNotNull(rule.getCondition());
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall(){
        RuleType rule = (new RuleTypeImplBuilder()).buildObject();
        rule.setRuleId(expectedRuleId);
        rule.setEffect(EffectType.Deny);

        DescriptionType description = new DescriptionTypeImplBuilder().buildObject();
        description.setValue(expectedDescription);
        rule.setDescription(description);
        rule.setTarget(new TargetTypeImplBuilder().buildObject());
        rule.setCondition(new ConditionTypeImplBuilder().buildObject());

        assertXMLEquals(expectedChildElementsDOM, rule);
    }
}