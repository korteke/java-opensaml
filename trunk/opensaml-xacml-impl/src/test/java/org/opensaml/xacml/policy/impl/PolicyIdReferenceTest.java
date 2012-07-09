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
 * PolicytIdReference.
 */
public class PolicyIdReferenceTest extends XMLObjectProviderBaseTestCase {

    private String expectedReference;

    private String expectedVersion;

    private String expectedEarliestVersion;

    private String expectedLatestVersion;

    /**
     * Constructor
     */
    public PolicyIdReferenceTest() {
        singleElementFile = "/data/org/opensaml/xacml/policy/impl/PolicyIdReference.xml";
        singleElementOptionalAttributesFile =
                "/data/org/opensaml/xacml/policy/impl/PolicyIdReferenceOptionalAttributes.xml";

        expectedReference = "https://example.org/Policy/Id/Reference";
        expectedVersion = "3.2.*";
        expectedEarliestVersion = "3.1.+";
        expectedLatestVersion = "3.3.99";
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnmarshall() {
        IdReferenceType policyIdReference = (IdReferenceType) unmarshallElement(singleElementFile);

        Assert.assertEquals(policyIdReference.getValue(), expectedReference);
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementMarshall() {
        IdReferenceType policyIdReference =
                (IdReferenceType) buildXMLObject(IdReferenceType.POLICY_ID_REFERENCE_ELEMENT_NAME);

        policyIdReference.setValue(expectedReference);
        assertXMLEquals(expectedDOM, policyIdReference);
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementOptionalAttributesUnmarshall() {
        IdReferenceType policyIdReference = (IdReferenceType) unmarshallElement(singleElementOptionalAttributesFile);

        Assert.assertEquals(policyIdReference.getValue(), expectedReference);
        Assert.assertEquals(policyIdReference.getVersion(), expectedVersion);
        Assert.assertEquals(policyIdReference.getEarliestVersion(), expectedEarliestVersion);
        Assert.assertEquals(policyIdReference.getLatestVersion(), expectedLatestVersion);
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementOptionalAttributesMarshall() {
        IdReferenceType policyIdReference =
                (IdReferenceType) buildXMLObject(IdReferenceType.POLICY_ID_REFERENCE_ELEMENT_NAME);

        policyIdReference.setValue(expectedReference);
        policyIdReference.setVersion(expectedVersion);
        policyIdReference.setEarliestVersion(expectedEarliestVersion);
        policyIdReference.setLatestVersion(expectedLatestVersion);

        assertXMLEquals(expectedOptionalAttributesDOM, policyIdReference);
    }
}