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
import org.opensaml.xacml.policy.IdReferenceType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.xacml.policy.IdReferenceType} subtype
 * PolicySetIdReference.
 */
public class PolicySetIdReferenceTest extends XMLObjectProviderBaseTestCase {

    private String expectedReference;

    private String expectedVersion;

    private String expectedEarliestVersion;

    private String expectedLatestVersion;

    /**
     * Constructor
     */
    public PolicySetIdReferenceTest() {
        singleElementFile = "/data/org/opensaml/xacml/policy/impl/PolicySetIdReference.xml";
        singleElementOptionalAttributesFile =
                "/data/org/opensaml/xacml/policy/impl/PolicySetIdReferenceOptionalAttributes.xml";

        expectedReference = "https://example.org/Policy/Set/Id/Reference";
        expectedVersion = "1.2.*";
        expectedEarliestVersion = "1.1.+";
        expectedLatestVersion = "1.3.99";
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnmarshall() {
        IdReferenceType policySetIdReference = (IdReferenceType) unmarshallElement(singleElementFile);

        Assert.assertEquals(policySetIdReference.getValue(), expectedReference);
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementMarshall() {
        IdReferenceType policySetIdReference =
                (IdReferenceType) buildXMLObject(IdReferenceType.POLICY_SET_ID_REFERENCE_ELEMENT_NAME);

        policySetIdReference.setValue(expectedReference);
        assertXMLEquals(expectedDOM, policySetIdReference);
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementOptionalAttributesUnmarshall() {
        IdReferenceType policySetIdReference = (IdReferenceType) unmarshallElement(singleElementOptionalAttributesFile);

        Assert.assertEquals(policySetIdReference.getValue(), expectedReference);
        Assert.assertEquals(policySetIdReference.getVersion(), expectedVersion);
        Assert.assertEquals(policySetIdReference.getEarliestVersion(), expectedEarliestVersion);
        Assert.assertEquals(policySetIdReference.getLatestVersion(), expectedLatestVersion);
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementOptionalAttributesMarshall() {
        IdReferenceType policySetIdReference =
                (IdReferenceType) buildXMLObject(IdReferenceType.POLICY_SET_ID_REFERENCE_ELEMENT_NAME);

        policySetIdReference.setValue(expectedReference);
        policySetIdReference.setVersion(expectedVersion);
        policySetIdReference.setEarliestVersion(expectedEarliestVersion);
        policySetIdReference.setLatestVersion(expectedLatestVersion);

        assertXMLEquals(expectedOptionalAttributesDOM, policySetIdReference);
    }
}