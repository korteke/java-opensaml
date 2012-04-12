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
import org.testng.Assert;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.criteria.impl.EvaluableCredentialCriteriaRegistry;
import org.opensaml.security.credential.criteria.impl.EvaluableCredentialCriterion;
import org.opensaml.security.credential.criteria.impl.EvaluableKeyNameCredentialCriterion;
import org.opensaml.security.criteria.KeyNameCriterion;
import org.opensaml.security.crypto.KeySupport;

/**
 *
 */
public class EvaluableKeyNameCredentialCriterionTest {
    
    private BasicCredential credential;
    private String keyName;
    private KeyNameCriterion criteria;
    
    public EvaluableKeyNameCredentialCriterionTest() {
        keyName = "someKeyName";
    }

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        credential = new BasicCredential(KeySupport.generateKey("AES", 128, null));
        credential.getKeyNames().add(keyName);
        credential.getKeyNames().add("foo");
        credential.getKeyNames().add("bar");
        
        criteria = new KeyNameCriterion(keyName);
    }
    
    @Test
    public void testSatifsy() {
        EvaluableKeyNameCredentialCriterion evalCrit = new EvaluableKeyNameCredentialCriterion(criteria);
        Assert.assertTrue(evalCrit.evaluate(credential), "Credential should have matched the evaluable criteria");
    }

    @Test
    public void testNotSatisfy() {
        criteria.setKeyName(keyName + "OTHER");
        EvaluableKeyNameCredentialCriterion evalCrit = new EvaluableKeyNameCredentialCriterion(criteria);
        Assert.assertFalse(evalCrit.evaluate(credential), "Credential should NOT have matched the evaluable criteria");
    }
    
    @Test
    public void testCanNotEvaluate() {
        credential.getKeyNames().clear();
        EvaluableKeyNameCredentialCriterion evalCrit = new EvaluableKeyNameCredentialCriterion(criteria);
        Assert.assertNull(evalCrit.evaluate(credential), "Credential should have been unevaluable against the criteria");
    }
    
    @Test
    public void testRegistry() throws Exception {
        EvaluableCredentialCriterion evalCrit = EvaluableCredentialCriteriaRegistry.getEvaluator(criteria);
        Assert.assertNotNull(evalCrit, "Evaluable criteria was unavailable from the registry");
        Assert.assertTrue(evalCrit.evaluate(credential), "Credential should have matched the evaluable criteria");
    }
}
