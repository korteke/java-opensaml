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

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.AssertJUnit;
import org.opensaml.security.SecurityHelper;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.criteria.impl.EvaluableCredentialCriteriaRegistry;
import org.opensaml.security.credential.criteria.impl.EvaluableCredentialCriterion;
import org.opensaml.security.credential.criteria.impl.EvaluableKeyAlgorithmCredentialCriterion;
import org.opensaml.security.criteria.KeyAlgorithmCriterion;

/**
 *
 */
public class EvaluableKeyAlgorithmCredentialCriterionTest {
    
    private BasicCredential credential;
    private String keyAlgo;
    private KeyAlgorithmCriterion criteria;
    
    public EvaluableKeyAlgorithmCredentialCriterionTest() {
        keyAlgo = "RSA";
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        credential = new BasicCredential();
        credential.setPublicKey(SecurityHelper.generateKeyPair(keyAlgo, 1024, null).getPublic());
        
        criteria = new KeyAlgorithmCriterion(keyAlgo);
    }
    
    @Test
    public void testSatifsy() {
        EvaluableKeyAlgorithmCredentialCriterion evalCrit = new EvaluableKeyAlgorithmCredentialCriterion(criteria);
        AssertJUnit.assertTrue("Credential should have matched the evaluable criteria", evalCrit.evaluate(credential));
    }

    @Test
    public void testNotSatisfy() {
        criteria.setKeyAlgorithm("SomeOtherKeyAlgo");
        EvaluableKeyAlgorithmCredentialCriterion evalCrit = new EvaluableKeyAlgorithmCredentialCriterion(criteria);
        AssertJUnit.assertFalse("Credential should NOT have matched the evaluable criteria", evalCrit.evaluate(credential));
    }
    
    @Test
    public void testCanNotEvaluate() {
        credential.setPublicKey(null);
        EvaluableKeyAlgorithmCredentialCriterion evalCrit = new EvaluableKeyAlgorithmCredentialCriterion(criteria);
        AssertJUnit.assertNull("Credential should have been unevaluable against the criteria", evalCrit.evaluate(credential));
    }
    
    @Test
    public void testRegistry() throws Exception {
        EvaluableCredentialCriterion evalCrit = EvaluableCredentialCriteriaRegistry.getEvaluator(criteria);
        AssertJUnit.assertNotNull("Evaluable criteria was unavailable from the registry", evalCrit);
        AssertJUnit.assertTrue("Credential should have matched the evaluable criteria", evalCrit.evaluate(credential));
    }
}
