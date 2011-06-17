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
import org.opensaml.xml.security.credential.BasicCredential;
import org.opensaml.xml.security.criteria.KeyNameCriteria;

/**
 *
 */
public class EvaluableKeyNameCredentialCriteriaTest extends TestCase {
    
    private BasicCredential credential;
    private String keyName;
    private KeyNameCriteria criteria;
    
    public EvaluableKeyNameCredentialCriteriaTest() {
        keyName = "someKeyName";
    }

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        
        credential = new BasicCredential();
        credential.getKeyNames().add(keyName);
        credential.getKeyNames().add("foo");
        credential.getKeyNames().add("bar");
        
        criteria = new KeyNameCriteria(keyName);
    }
    
    public void testSatifsy() {
        EvaluableKeyNameCredentialCriteria evalCrit = new EvaluableKeyNameCredentialCriteria(criteria);
        assertTrue("Credential should have matched the evaluable criteria", evalCrit.evaluate(credential));
    }

    public void testNotSatisfy() {
        criteria.setKeyName(keyName + "OTHER");
        EvaluableKeyNameCredentialCriteria evalCrit = new EvaluableKeyNameCredentialCriteria(criteria);
        assertFalse("Credential should NOT have matched the evaluable criteria", evalCrit.evaluate(credential));
    }
    
    public void testCanNotEvaluate() {
        credential.getKeyNames().clear();
        EvaluableKeyNameCredentialCriteria evalCrit = new EvaluableKeyNameCredentialCriteria(criteria);
        assertNull("Credential should have been unevaluable against the criteria", evalCrit.evaluate(credential));
    }
    
    public void testRegistry() throws SecurityException {
        EvaluableCredentialCriteria evalCrit = EvaluableCredentialCriteriaRegistry.getEvaluator(criteria);
        assertNotNull("Evaluable criteria was unavailable from the registry", evalCrit);
        assertTrue("Credential should have matched the evaluable criteria", evalCrit.evaluate(credential));
    }
}
