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

package org.opensaml.xml.security.credential.criteria;

import junit.framework.TestCase;

import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.SecurityHelper;
import org.opensaml.xml.security.credential.BasicCredential;
import org.opensaml.xml.security.criteria.KeyAlgorithmCriterion;

/**
 *
 */
public class EvaluableKeyAlgorithmCredentialCriteriaTest extends TestCase {
    
    private BasicCredential credential;
    private String keyAlgo;
    private KeyAlgorithmCriterion criteria;
    
    public EvaluableKeyAlgorithmCredentialCriteriaTest() {
        keyAlgo = "RSA";
    }

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        
        credential = new BasicCredential();
        credential.setPublicKey(SecurityHelper.generateKeyPair(keyAlgo, 1024, null).getPublic());
        
        criteria = new KeyAlgorithmCriterion(keyAlgo);
    }
    
    public void testSatifsy() {
        EvaluableKeyAlgorithmCredentialCriterion evalCrit = new EvaluableKeyAlgorithmCredentialCriterion(criteria);
        assertTrue("Credential should have matched the evaluable criteria", evalCrit.evaluate(credential));
    }

    public void testNotSatisfy() {
        criteria.setKeyAlgorithm("SomeOtherKeyAlgo");
        EvaluableKeyAlgorithmCredentialCriterion evalCrit = new EvaluableKeyAlgorithmCredentialCriterion(criteria);
        assertFalse("Credential should NOT have matched the evaluable criteria", evalCrit.evaluate(credential));
    }
    
    public void testCanNotEvaluate() {
        credential.setPublicKey(null);
        EvaluableKeyAlgorithmCredentialCriterion evalCrit = new EvaluableKeyAlgorithmCredentialCriterion(criteria);
        assertNull("Credential should have been unevaluable against the criteria", evalCrit.evaluate(credential));
    }
    
    public void testRegistry() throws SecurityException {
        EvaluableCredentialCriterion evalCrit = EvaluableCredentialCriteriaRegistry.getEvaluator(criteria);
        assertNotNull("Evaluable criteria was unavailable from the registry", evalCrit);
        assertTrue("Credential should have matched the evaluable criteria", evalCrit.evaluate(credential));
    }
}
