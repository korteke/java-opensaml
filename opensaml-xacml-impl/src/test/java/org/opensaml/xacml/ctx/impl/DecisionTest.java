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
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.xacml.ctx.DecisionType}.
 */
public class DecisionTest extends XMLObjectProviderBaseTestCase {

    private DecisionType.DECISION expectedDecision;


    /**
     * Constructor
     */
    public DecisionTest() {
        singleElementFile = "/data/org/opensaml/xacml/ctx/impl/Decision.xml";

        expectedDecision = DECISION.Indeterminate;
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementUnmarshall() {
        DecisionType decision = (DecisionType) unmarshallElement(singleElementFile);

        Assert.assertEquals(decision.getDecision(), expectedDecision);
    }

    /** {@inheritDoc} */
    @Test public void testSingleElementMarshall() {
        DecisionType decision = (new DecisionTypeImplBuilder()).buildObject();

        decision.setDecision(expectedDecision);
        assertXMLEquals(expectedDOM, decision);
    }

}