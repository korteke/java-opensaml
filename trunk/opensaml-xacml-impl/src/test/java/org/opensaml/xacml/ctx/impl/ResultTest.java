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

package org.opensaml.xacml.ctx.impl;

import org.opensaml.core.xml.XMLObjectProviderBaseTestCase;
import org.opensaml.xacml.ctx.DecisionType;
import org.opensaml.xacml.ctx.DecisionType.DECISION;
import org.opensaml.xacml.ctx.ResultType;
import org.opensaml.xacml.ctx.StatusType;
import org.opensaml.xacml.policy.ObligationsType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling {@link link org.opensaml.xacml.ctx.ResultType}.
 */
public class ResultTest extends XMLObjectProviderBaseTestCase {

    private DecisionType.DECISION expectedDecision;

    /**
     * Constructor
     */
    public ResultTest() {
        singleElementFile = "/data/org/opensaml/xacml/ctx/impl/Result.xml";
        childElementsFile = "/data/org/opensaml/xacml/ctx/impl/ResultChildElements.xml";
        
        expectedDecision = DECISION.NotApplicable;
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnmarshall() {
        ResultType result = (ResultType) unmarshallElement(singleElementFile);

        Assert.assertNull(result.getDecision());
        Assert.assertNull(result.getStatus());
        Assert.assertNull(result.getObligations());
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementMarshall() {
        ResultType result = new ResultTypeImplBuilder().buildObject();

        assertXMLEquals(expectedDOM, result);
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsUnmarshall() {
        ResultType result = (ResultType) unmarshallElement(childElementsFile);

        Assert.assertEquals(result.getDecision().getDecision(), expectedDecision);
        Assert.assertNotNull(result.getStatus());
        Assert.assertNotNull(result.getObligations());
    }

    /** {@inheritDoc} */
    @Test public void testChildElementsMarshall() {
        ResultType result = new ResultTypeImplBuilder().buildObject();
        DecisionType decision = buildXMLObject(DecisionType.DEFAULT_ELEMENT_NAME);
        decision.setDecision(expectedDecision);

        result.setDecision(decision);
        
        result.setStatus((StatusType) buildXMLObject(StatusType.DEFAULT_ELEMENT_NAME));


        result.setObligations((ObligationsType) buildXMLObject(ObligationsType.DEFAULT_ELEMENT_QNAME));

        assertXMLEquals(expectedChildElementsDOM, result);
    }


}