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

import java.util.Map;

import javax.annotation.Nonnull;

import org.joda.time.DateTime;
import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.assertion.BaseAssertionValidationTest;
import org.opensaml.saml.saml2.assertion.SAML2AssertionValidationParameters;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 */
public class AbstractSubjectConfirmationValidatorTest extends BaseAssertionValidationTest {
    
    private MockSubjectConfirmationValidator validator;
    
    private SubjectConfirmation subjectConfirmation;
    
    @BeforeMethod(dependsOnMethods="setUpBasicAssertion")
    public void setUp() {
        validator = new MockSubjectConfirmationValidator();
        subjectConfirmation = getAssertion().getSubject().getSubjectConfirmations().get(0);
        subjectConfirmation.setMethod(validator.getServicedMethod());
    }
    
    @Test
    public void testValidConfirmationData() throws AssertionValidationException {
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.VALID);        
    }
    
    @Test
    public void testNoConfirmationData() throws AssertionValidationException {
        subjectConfirmation.setSubjectConfirmationData(null);
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.VALID);        
    }
    
    @Test
    public void testInvalidAddress() throws AssertionValidationException {
        subjectConfirmation.getSubjectConfirmationData().setAddress("1.2.3.4");
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INVALID);        
    }
    
    @Test
    public void testInvalidAddressParamType() throws AssertionValidationException {
        Map<String,Object> staticParams = buildBasicStaticParameters();
        // It should be a Set<String>, not a String
        staticParams.put(SAML2AssertionValidationParameters.SC_VALID_ADDRESSES, SUBJECT_CONFIRMATION_ADDRESS);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INDETERMINATE);        
    }
    
    @Test
    public void testMissingAddressParam() throws AssertionValidationException {
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.remove(SAML2AssertionValidationParameters.SC_VALID_ADDRESSES);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INDETERMINATE);        
    }
    
    @Test
    public void testNoAddress() throws AssertionValidationException {
        subjectConfirmation.getSubjectConfirmationData().setAddress(null);
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.VALID);        
    }
    
    @Test
    public void testInvalidRecipient() throws AssertionValidationException {
        subjectConfirmation.getSubjectConfirmationData().setRecipient("https://bogussp.example.com");
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INVALID);        
    }
    
    @Test
    public void testInvalidRecipientParamType() throws AssertionValidationException {
        Map<String,Object> staticParams = buildBasicStaticParameters();
        // It should be a Set<String>, not a String
        staticParams.put(SAML2AssertionValidationParameters.SC_VALID_RECIPIENTS, SUBJECT_CONFIRMATION_RECIPIENT);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INDETERMINATE);        
    }
    
    @Test
    public void testMissingRecipientParam() throws AssertionValidationException {
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.remove(SAML2AssertionValidationParameters.SC_VALID_RECIPIENTS);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INDETERMINATE);        
    }
    
    @Test
    public void testNoRecipient() throws AssertionValidationException {
        subjectConfirmation.getSubjectConfirmationData().setRecipient(null);
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.VALID);        
    }
    
    @Test
    public void testInvalidNotBefore() throws AssertionValidationException {
        // Adjust them both just so they make sense
        subjectConfirmation.getSubjectConfirmationData().setNotBefore(new DateTime().plusMinutes(30));
        subjectConfirmation.getSubjectConfirmationData().setNotOnOrAfter(new DateTime().plusMinutes(60));
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INVALID);        
    }
    
    @Test
    public void testNoNotBefore() throws AssertionValidationException {
        subjectConfirmation.getSubjectConfirmationData().setNotBefore(null);
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.VALID);        
    }
    
    @Test
    public void testInvalidNotOnOrAfter() throws AssertionValidationException {
        // Adjust them both just so they make sense
        subjectConfirmation.getSubjectConfirmationData().setNotBefore(new DateTime().minusMinutes(60));
        subjectConfirmation.getSubjectConfirmationData().setNotOnOrAfter(new DateTime().minusMinutes(30));
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INVALID);        
    }
    
    @Test
    public void testNoNotOnOrAfter() throws AssertionValidationException {
        subjectConfirmation.getSubjectConfirmationData().setNotOnOrAfter(null);
        
        ValidationContext validationContext = new ValidationContext(buildBasicStaticParameters());
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.VALID);        
    }
    
    
    
    
    // Mock concrete class for testing
    
    public static class MockSubjectConfirmationValidator extends AbstractSubjectConfirmationValidator {

        /** {@inheritDoc} */
        public String getServicedMethod() {
            return "urn:test:foo";
        }

        /** {@inheritDoc} */
        @Nonnull protected ValidationResult doValidate(@Nonnull SubjectConfirmation confirmation,
                @Nonnull Assertion assertion, @Nonnull ValidationContext context) throws AssertionValidationException {
            return ValidationResult.VALID;
        }
        
    }

}
