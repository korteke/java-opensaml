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

package org.opensaml.saml.saml2.assertion.impl;

import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.assertion.BaseAssertionValidationTest;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SenderVouchesSubjectConfirmationValidatorTest extends BaseAssertionValidationTest {

    private SenderVouchersSubjectConfirmationValidator validator;
    
    private SubjectConfirmation subjectConfirmation;
    
    @BeforeMethod(dependsOnMethods="setUpBasicAssertion")
    public void setUp() {
        validator = new SenderVouchersSubjectConfirmationValidator();
        subjectConfirmation = getAssertion().getSubject().getSubjectConfirmations().get(0);
    }
    
    @Test
    public void testSenderVouches() throws AssertionValidationException {
        subjectConfirmation.setMethod(SubjectConfirmation.METHOD_SENDER_VOUCHES);
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.VALID);
    }
    
    @Test
    public void testNonSenderVouches() throws AssertionValidationException {
        subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INDETERMINATE);
    }
 
}
