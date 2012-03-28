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

package org.opensaml.security.credential.criteria.impl;

import junit.framework.TestCase;

import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.credential.criteria.impl.EvaluableCredentialCriteriaRegistry;
import org.opensaml.security.credential.criteria.impl.EvaluableCredentialCriterion;
import org.opensaml.security.credential.criteria.impl.EvaluableUsageCredentialCriterion;
import org.opensaml.security.criteria.UsageCriterion;

/**
 *
 */
public class EvaluableUsageCredentialCriterionTest extends TestCase {
    
    private BasicCredential credential;
    private UsageType usage;
    private UsageCriterion criteria;
    
    public EvaluableUsageCredentialCriterionTest() {
        usage = UsageType.SIGNING;
    }

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        
        credential = new BasicCredential();
        credential.setUsageType(usage);
        
        criteria = new UsageCriterion(usage);
    }
    
    public void testSatifsyExactMatch() {
        EvaluableUsageCredentialCriterion evalCrit = new EvaluableUsageCredentialCriterion(criteria);
        assertTrue("Credential should have matched the evaluable criteria", evalCrit.evaluate(credential));
    }
    
    public void testSatisfyWithUnspecifiedCriteria() {
        criteria.setUsage(UsageType.UNSPECIFIED);
        EvaluableUsageCredentialCriterion evalCrit = new EvaluableUsageCredentialCriterion(criteria);
        assertTrue("Credential should have matched the evaluable criteria", evalCrit.evaluate(credential));
    }
    
    public void testSatisfyWithUnspecifiedCredential() {
        credential.setUsageType(UsageType.UNSPECIFIED);
        EvaluableUsageCredentialCriterion evalCrit = new EvaluableUsageCredentialCriterion(criteria);
        assertTrue("Credential should have matched the evaluable criteria", evalCrit.evaluate(credential));
    }

    public void testNotSatisfy() {
        criteria.setUsage(UsageType.ENCRYPTION);
        EvaluableUsageCredentialCriterion evalCrit = new EvaluableUsageCredentialCriterion(criteria);
        assertFalse("Credential should NOT have matched the evaluable criteria", evalCrit.evaluate(credential));
    }
    
    /* With BasicCredential, can't set UsageType to null, so can't really test.
    public void testCanNotEvaluate() {
        credential.setUsageType(null);
        EvaluableUsageCredentialCriterion evalCrit = new EvaluableUsageCredentialCriterion(criteria);
        assertNull("Credential should have been unevaluable against the criteria", evalCrit.evaluate(credential));
    }
    */
    
    public void testRegistry() throws Exception {
        EvaluableCredentialCriterion evalCrit = EvaluableCredentialCriteriaRegistry.getEvaluator(criteria);
        assertNotNull("Evaluable criteria was unavailable from the registry", evalCrit);
        assertTrue("Credential should have matched the evaluable criteria", evalCrit.evaluate(credential));
    }
}
