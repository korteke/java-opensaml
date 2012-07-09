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
import org.opensaml.xacml.policy.DefaultsType;
import org.opensaml.xacml.policy.DescriptionType;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.policy.RuleCombinerParametersType;
import org.opensaml.xacml.policy.RuleType;
import org.opensaml.xacml.policy.VariableDefinitionType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link link org.opensaml.xacml.policy.PolicyType}.
 */
public class PolicyTest extends XMLObjectProviderBaseTestCase {
    
    private final String expectedVersion;
    private final String expectedPolicyId;
    private final String expectedRuleCombiningAlgId;

    private final int expectedNumCombinerParameters;
    private final int expectedNumRuleCombinerParameters;
    private final int expectedNumVariableDefinitions;
    private final int expectedNumRules;
    
    private final String expectedDescription;
    private final String expectedRuleCombinerParametersRuleIdRef;
    private final String expectedVariableDefinitionVariableId;
    private final String expectedRuleRuleId;
    private final EffectType expectedRuleEffect;
    /**
     * Constructor
     */
    public PolicyTest(){
        singleElementFile = "/data/org/opensaml/xacml/policy/impl/Policy.xml";
        singleElementOptionalAttributesFile = "/data/org/opensaml/xacml/policy/impl/PolicyOptionalAttributes.xml";
        childElementsFile  = "/data/org/opensaml/xacml/policy/impl/PolicyChildElements.xml";

        
        expectedVersion="9.9.8";
        expectedPolicyId="https://example.org/Policy/Policy/Id";
        expectedRuleCombiningAlgId="https://example.org/Policy/Rule/Combining/Alg";

        expectedNumCombinerParameters = 3;
        expectedNumRuleCombinerParameters = 3;
        expectedNumVariableDefinitions = 1;
        expectedNumRules = 4;

        expectedDescription = "This is a Description";
        expectedRuleCombinerParametersRuleIdRef="https://example.org/Rule/Id/Ref";
        expectedVariableDefinitionVariableId="VariableDefinitionId";
        expectedRuleRuleId="RuleRuleId";
        expectedRuleEffect = EffectType.Permit;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall(){
        PolicyType policy = (PolicyType) unmarshallElement(singleElementFile);
        
        Assert.assertEquals(policy.getVersion(), "1.0");
        Assert.assertEquals(policy.getPolicyId(), expectedPolicyId);
        Assert.assertEquals(policy.getRuleCombiningAlgoId(), expectedRuleCombiningAlgId);
        
        Assert.assertNull(policy.getDescription());
        Assert.assertNull(policy.getPolicyDefaults());
        Assert.assertTrue(policy.getCombinerParameters().isEmpty());
        Assert.assertNull(policy.getTarget());
        Assert.assertTrue(policy.getRuleCombinerParameters().isEmpty());
        Assert.assertTrue(policy.getVariableDefinitions().isEmpty());
        Assert.assertTrue(policy.getRules().isEmpty());
        Assert.assertNull(policy.getObligations());
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall(){
        PolicyType policy = (new PolicyTypeImplBuilder()).buildObject();
        Assert.assertEquals(policy.getVersion(), "1.0");

        policy.setVersion(null);
        policy.setPolicyId(expectedPolicyId);
        policy.setRuleCombiningAlgoId(expectedRuleCombiningAlgId);
        
        assertXMLEquals(expectedDOM, policy);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall(){
        PolicyType policy = (PolicyType) unmarshallElement(childElementsFile);
        
        Assert.assertEquals(policy.getVersion(), expectedVersion);
        Assert.assertEquals(policy.getPolicyId(), expectedPolicyId);
        Assert.assertEquals(policy.getRuleCombiningAlgoId(), expectedRuleCombiningAlgId);

        Assert.assertEquals(policy.getDescription().getValue(), expectedDescription);
        Assert.assertNotNull(policy.getPolicyDefaults());
        Assert.assertEquals(policy.getCombinerParameters().size(), expectedNumCombinerParameters);
        Assert.assertNotNull(policy.getTarget());
        Assert.assertEquals(policy.getRuleCombinerParameters().size(), expectedNumRuleCombinerParameters);
        for (RuleCombinerParametersType rcp : policy.getRuleCombinerParameters()) {
            Assert.assertEquals(rcp.getRuleIdRef(), expectedRuleCombinerParametersRuleIdRef);            
        }

        Assert.assertEquals(policy.getVariableDefinitions().size(), expectedNumVariableDefinitions);
        for (VariableDefinitionType variableDefn : policy.getVariableDefinitions()) {
            Assert.assertEquals(variableDefn.getVariableId(), expectedVariableDefinitionVariableId);
        }

        Assert.assertEquals(policy.getRules().size(), expectedNumRules);
        for (RuleType rule : policy.getRules()) {
            Assert.assertEquals(rule.getRuleId(), expectedRuleRuleId);
            Assert.assertEquals(rule.getEffect(), expectedRuleEffect);
        }

        Assert.assertNotNull(policy.getObligations());
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall(){
        PolicyType policy = (new PolicyTypeImplBuilder()).buildObject();
        policy.setVersion(expectedVersion);
        policy.setPolicyId(expectedPolicyId);
        policy.setRuleCombiningAlgoId(expectedRuleCombiningAlgId);

        DescriptionType description = new DescriptionTypeImplBuilder().buildObject();
        description.setValue(expectedDescription);
        policy.setDescription(description);
        policy.setPolicyDefaults((DefaultsType) buildXMLObject(DefaultsType.POLICY_DEFAULTS_ELEMENT_NAME));

        CombinerParametersTypeImplBuilder combinerBuilder = new CombinerParametersTypeImplBuilder();
        for (int i = 0; i < expectedNumCombinerParameters; i++) {
            policy.getCombinerParameters().add(combinerBuilder.buildObject());
        }
        policy.setTarget(new TargetTypeImplBuilder().buildObject());

        RuleCombinerParametersTypeImplBuilder rcpBuilder = new RuleCombinerParametersTypeImplBuilder();
        for (int i = 0; i < expectedNumRuleCombinerParameters; i++) {
            RuleCombinerParametersType rcp = rcpBuilder.buildObject();
            rcp.setRuleIdRef(expectedRuleCombinerParametersRuleIdRef);
            policy.getRuleCombinerParameters().add(rcp);
        }
        
        VariableDefinitionTypeImplBuilder variableDefnBuilder = new VariableDefinitionTypeImplBuilder();
        for (int i = 0; i < expectedNumVariableDefinitions; i++) {
            VariableDefinitionType variableDefn = variableDefnBuilder.buildObject();
            variableDefn.setVariableId(expectedVariableDefinitionVariableId);
            policy.getVariableDefinitions().add(variableDefn);
        }
        
        RuleTypeImplBuilder ruleBuilder = new RuleTypeImplBuilder();
        for (int i = 0; i < expectedNumRules; i++) {
            RuleType rule = ruleBuilder.buildObject();
            rule.setRuleId(expectedRuleRuleId);
            rule.setEffect(expectedRuleEffect);
            policy.getRules().add(rule);
        }
        policy.setObligations(new ObligationsTypeImplBuilder().buildObject());

        assertXMLEquals(expectedChildElementsDOM, policy);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        PolicyType policy = (PolicyType) unmarshallElement(singleElementOptionalAttributesFile);
        
        Assert.assertEquals(policy.getVersion(), expectedVersion);
        Assert.assertEquals(policy.getPolicyId(), expectedPolicyId);
        Assert.assertEquals(policy.getRuleCombiningAlgoId(), expectedRuleCombiningAlgId);

    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        PolicyType policy = (new PolicyTypeImplBuilder()).buildObject();

        policy.setVersion(expectedVersion);
        policy.setPolicyId(expectedPolicyId);
        policy.setRuleCombiningAlgoId(expectedRuleCombiningAlgId);
        
        assertXMLEquals(expectedOptionalAttributesDOM, policy);
    }

}