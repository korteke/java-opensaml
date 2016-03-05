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
import org.opensaml.xacml.policy.CombinerParametersType;
import org.opensaml.xacml.policy.DefaultsType;
import org.opensaml.xacml.policy.DescriptionType;
import org.opensaml.xacml.policy.IdReferenceType;
import org.opensaml.xacml.policy.PolicyCombinerParametersType;
import org.opensaml.xacml.policy.PolicySetCombinerParametersType;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.policy.TargetType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.xacml.policy.PolicySetType}.
 */
public class PolicySetTest extends XMLObjectProviderBaseTestCase {

    private final String expectedVersion;

    private final String expectedPolicySetId;

    private final String expectedPolicyCombiningAlgId;

    private final int expectedNumPolicySet;

    private final int expectedNumPolicy;

    private final int expectedNumPolicySetIdReference;

    private final int expectedNumPolicyIdReference;

    private final int expectedNumCombinerParameters;

    private final int expectedNumPolicyCombinerParameters;

    private final int expectedNumPolicySetCombinerParameters;

    private final String expectedDescription;

    private final String expectedPolicyId;

    private final String expectedRuleCombiningAlgId;

    private final String expectedPolicySetIdReference;

    private final String expectedPolicyIdReference;

    /**
     * Constructor
     */
    public PolicySetTest() {
        singleElementFile = "/org/opensaml/xacml/policy/impl/PolicySet.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/xacml/policy/impl/PolicySetOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/xacml/policy/impl/PolicySetChildElements.xml";

        expectedVersion = "8.7.6";
        expectedPolicySetId = "https://example.org/Policy/Set/Policy/Set/Id";
        expectedPolicyCombiningAlgId = "https://example.org/Policy/Set/Policy/Combining/Alg/Id";

        expectedNumPolicySet = 2;
        expectedNumPolicy = 2;
        expectedNumPolicySetIdReference = 3;
        expectedNumPolicyIdReference = 4;
        expectedNumCombinerParameters = 1;
        expectedNumPolicyCombinerParameters = 3;
        expectedNumPolicySetCombinerParameters = 2;

        expectedDescription = "This is a Description";
        expectedPolicyId = "https://example.org/Policy/Policy/Id";
        expectedRuleCombiningAlgId = "https://example.org/Policy/Rule/Combining/Alg";
        expectedPolicySetIdReference = "https://example.org/Policy/Set/Policy/Set/Id/Ref";
        expectedPolicyIdReference = "https://example.org/Policy/Set/Policy/Id/Ref";

    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnmarshall() {
        PolicySetType policySet = (PolicySetType) unmarshallElement(singleElementFile);

        Assert.assertEquals(policySet.getVersion(), "1.0");
        Assert.assertEquals(policySet.getPolicySetId(), expectedPolicySetId);
        Assert.assertEquals(policySet.getPolicyCombiningAlgoId(), expectedPolicyCombiningAlgId);

        Assert.assertNull(policySet.getDescription());
        Assert.assertNull(policySet.getPolicySetDefaults());
        Assert.assertNull(policySet.getTarget());

        Assert.assertTrue(policySet.getPolicySets().isEmpty());
        Assert.assertTrue(policySet.getPolicies().isEmpty());
        Assert.assertTrue(policySet.getPolicySetIdReferences().isEmpty());
        Assert.assertTrue(policySet.getPolicyIdReferences().isEmpty());
        Assert.assertTrue(policySet.getCombinerParameters().isEmpty());
        Assert.assertTrue(policySet.getPolicyCombinerParameters().isEmpty());
        Assert.assertTrue(policySet.getPolicySetCombinerParameters().isEmpty());
        Assert.assertTrue(policySet.getPolicyChoiceGroup().isEmpty());

        Assert.assertNull(policySet.getObligations());
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementMarshall() {
        PolicySetType policySet = (new PolicySetTypeImplBuilder()).buildObject();
        Assert.assertEquals(policySet.getVersion(), "1.0");

        policySet.setVersion(null);
        policySet.setPolicySetId(expectedPolicySetId);
        policySet.setPolicyCombiningAlgoId(expectedPolicyCombiningAlgId);

        assertXMLEquals(expectedDOM, policySet);
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsUnmarshall() {
        PolicySetType policySet = (PolicySetType) unmarshallElement(childElementsFile);

        Assert.assertEquals(policySet.getPolicySetId(), expectedPolicySetId);
        Assert.assertEquals(policySet.getPolicyCombiningAlgoId(), expectedPolicyCombiningAlgId);
        Assert.assertEquals(policySet.getVersion(), expectedVersion);

        Assert.assertEquals(policySet.getDescription().getValue(), expectedDescription);
        Assert.assertNotNull(policySet.getPolicySetDefaults());
        Assert.assertNotNull(policySet.getTarget());

        Assert.assertEquals(policySet.getPolicySets().size(), expectedNumPolicySet);
        for (PolicySetType myPolicySet : policySet.getPolicySets()) {
            Assert.assertEquals(myPolicySet.getPolicySetId(), expectedPolicySetId);
            Assert.assertEquals(myPolicySet.getPolicyCombiningAlgoId(), expectedPolicyCombiningAlgId);
            Assert.assertEquals(myPolicySet.getVersion(), expectedVersion);
        }

        Assert.assertEquals(policySet.getPolicies().size(), expectedNumPolicy);
        for (PolicyType policy : policySet.getPolicies()) {
            Assert.assertEquals(policy.getPolicyId(), expectedPolicyId);
            Assert.assertEquals(policy.getRuleCombiningAlgoId(), expectedRuleCombiningAlgId);
            Assert.assertEquals(policy.getVersion(), expectedVersion);
        }

        Assert.assertEquals(policySet.getPolicySetIdReferences().size(), expectedNumPolicySetIdReference);
        for (IdReferenceType idRef : policySet.getPolicySetIdReferences()) {
            Assert.assertEquals(idRef.getValue(), expectedPolicySetIdReference);
        }

        Assert.assertEquals(policySet.getPolicyIdReferences().size(), expectedNumPolicyIdReference);
        for (IdReferenceType idRef : policySet.getPolicyIdReferences()) {
            Assert.assertEquals(idRef.getValue(), expectedPolicyIdReference);
        }

        Assert.assertEquals(policySet.getCombinerParameters().size(), expectedNumCombinerParameters);

        Assert.assertEquals(policySet.getPolicyCombinerParameters().size(), expectedNumPolicyCombinerParameters);
        for (PolicyCombinerParametersType pcp : policySet.getPolicyCombinerParameters()) {
            Assert.assertEquals(pcp.getPolicyIdRef(), expectedPolicyIdReference);
        }

        Assert.assertEquals(policySet.getPolicySetCombinerParameters().size(), expectedNumPolicySetCombinerParameters);
        for (PolicySetCombinerParametersType pscp : policySet.getPolicySetCombinerParameters()) {
            Assert.assertEquals(pscp.getPolicySetIdRef(), expectedPolicySetIdReference);
        }

        Assert.assertEquals(policySet.getPolicyChoiceGroup().size(), expectedNumCombinerParameters + expectedNumPolicy
                + expectedNumPolicyCombinerParameters + expectedNumPolicyIdReference
                + expectedNumPolicySetCombinerParameters + expectedNumPolicySetIdReference + expectedNumPolicySet);

        Assert.assertNotNull(policySet.getObligations());
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsMarshall() {
        PolicySetType policySet = (new PolicySetTypeImplBuilder()).buildObject();
        policySet.setVersion(expectedVersion);
        policySet.setPolicySetId(expectedPolicySetId);
        policySet.setPolicyCombiningAlgoId(expectedPolicyCombiningAlgId);

        DescriptionType description = new DescriptionTypeImplBuilder().buildObject();
        description.setValue(expectedDescription);
        policySet.setDescription(description);
        policySet.setPolicySetDefaults((DefaultsType) buildXMLObject(DefaultsType.POLICY_SET_DEFAULTS_ELEMENT_NAME));
        policySet.setTarget((TargetType) buildXMLObject(TargetType.DEFAULT_ELEMENT_NAME));

        for (int i = 0; i < expectedNumPolicySet; i++) {
            PolicySetType myPolicySet = buildXMLObject(PolicySetType.DEFAULT_ELEMENT_NAME);
            myPolicySet.setVersion(expectedVersion);
            myPolicySet.setPolicySetId(expectedPolicySetId);
            myPolicySet.setPolicyCombiningAlgoId(expectedPolicyCombiningAlgId);
            policySet.getPolicySets().add(myPolicySet);
        }

        for (int i = 0; i < expectedNumPolicy; i++) {
            PolicyType policy = buildXMLObject(PolicyType.DEFAULT_ELEMENT_NAME);
            policy.setVersion(expectedVersion);
            policy.setPolicyId(expectedPolicyId);
            policy.setRuleCombiningAlgoId(expectedRuleCombiningAlgId);
            policySet.getPolicies().add(policy);
        }

        for (int i = 0; i < expectedNumPolicySetIdReference; i++) {
            IdReferenceType ref = buildXMLObject(IdReferenceType.POLICY_SET_ID_REFERENCE_ELEMENT_NAME);
            ref.setValue(expectedPolicySetIdReference);
            policySet.getPolicySetIdReferences().add(ref);
        }

        for (int i = 0; i < expectedNumPolicyIdReference; i++) {
            IdReferenceType ref = buildXMLObject(IdReferenceType.POLICY_ID_REFERENCE_ELEMENT_NAME);
            ref.setValue(expectedPolicyIdReference);
            policySet.getPolicySetIdReferences().add(ref);
        }

        for (int i = 0; i < expectedNumCombinerParameters; i++) {
            policySet.getCombinerParameters().add(
                    (CombinerParametersType) buildXMLObject(CombinerParametersType.DEFAULT_ELEMENT_NAME));
        }

        for (int i = 0; i < expectedNumPolicyCombinerParameters; i++) {
            PolicyCombinerParametersType pcp = buildXMLObject(PolicyCombinerParametersType.DEFAULT_ELEMENT_NAME);
            pcp.setPolicyIdRef(expectedPolicyIdReference);
            policySet.getPolicyCombinerParameters().add(pcp);
        }

        for (int i = 0; i < expectedNumPolicySetCombinerParameters; i++) {
            PolicySetCombinerParametersType pscp = buildXMLObject(PolicySetCombinerParametersType.DEFAULT_ELEMENT_NAME);
            pscp.setPolicySetIdRef(expectedPolicySetIdReference);
            policySet.getPolicySetCombinerParameters().add(pscp);
        }

        policySet.setObligations(new ObligationsTypeImplBuilder().buildObject());

        assertXMLEquals(expectedChildElementsDOM, policySet);
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementOptionalAttributesUnmarshall() {
        PolicySetType policySet = (PolicySetType) unmarshallElement(singleElementOptionalAttributesFile);

        Assert.assertEquals(policySet.getVersion(), expectedVersion);
        Assert.assertEquals(policySet.getPolicySetId(), expectedPolicySetId);
        Assert.assertEquals(policySet.getPolicyCombiningAlgoId(), expectedPolicyCombiningAlgId);

    }

    /** {@inheritDoc} */
    @Test public void testSingleElementOptionalAttributesMarshall() {
        PolicySetType policySet = (new PolicySetTypeImplBuilder()).buildObject();

        policySet.setVersion(expectedVersion);
        policySet.setPolicySetId(expectedPolicySetId);
        policySet.setPolicyCombiningAlgoId(expectedPolicyCombiningAlgId);

        assertXMLEquals(expectedOptionalAttributesDOM, policySet);
    }

}