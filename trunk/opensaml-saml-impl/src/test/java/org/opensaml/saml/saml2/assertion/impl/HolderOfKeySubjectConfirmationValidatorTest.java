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

import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.assertion.BaseAssertionValidationTest;
import org.opensaml.saml.saml2.core.KeyInfoConfirmationDataType;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 */
public class HolderOfKeySubjectConfirmationValidatorTest extends BaseAssertionValidationTest {
    
    private HolderOfKeySubjectConfirmationValidator validator;
    
    private SubjectConfirmation subjectConfirmation;
    
    private SubjectConfirmationData subjectConfirmationData;
    
    private KeyInfo keyInfo;
    
    private X509Certificate cert1, cert2;
    
    private PublicKey publicKey1, publicKey2;
    
    @BeforeClass
    protected void readCertsAndKeys() throws CertificateException, URISyntaxException {
        cert1 = getCertificate("subject1.crt");
        publicKey1 = cert1.getPublicKey();
        cert2 = getCertificate("subject2.crt");
        publicKey2 = cert2.getPublicKey();
    }
    
    @BeforeMethod(dependsOnMethods="setUpBasicAssertion")
    protected void setUp() throws NoSuchAlgorithmException, NoSuchProviderException {
        validator = new HolderOfKeySubjectConfirmationValidator();
        
        subjectConfirmation = getAssertion().getSubject().getSubjectConfirmations().get(0);
        subjectConfirmation.setMethod(SubjectConfirmation.METHOD_HOLDER_OF_KEY);
        subjectConfirmationData = buildBasicSubjectConfirmationData(KeyInfoConfirmationDataType.TYPE_NAME);
        subjectConfirmation.setSubjectConfirmationData(subjectConfirmationData);
        keyInfo = buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME);
        ((KeyInfoConfirmationDataType)subjectConfirmation.getSubjectConfirmationData()).getKeyInfos().add(keyInfo);
    }

    @Test
    public void testValidPublicKey() throws AssertionValidationException {
        KeyInfoSupport.addPublicKey(keyInfo, publicKey1);
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(HolderOfKeySubjectConfirmationValidator.PRESENTER_KEY_PARAM, publicKey1);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.VALID);
        
        Assert.assertSame(validationContext.getDynamicParameters().get(HolderOfKeySubjectConfirmationValidator.CONFIRMED_KEY_INFO_PARAM), keyInfo);
    }
    
    @Test
    public void testInvalidPublicKey() throws AssertionValidationException {
        KeyInfoSupport.addPublicKey(keyInfo, publicKey1);
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(HolderOfKeySubjectConfirmationValidator.PRESENTER_KEY_PARAM, publicKey2);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INVALID);
        
        Assert.assertNull(validationContext.getDynamicParameters().get(HolderOfKeySubjectConfirmationValidator.CONFIRMED_KEY_INFO_PARAM));
    }
    
    @Test
    public void testValidCert() throws AssertionValidationException, CertificateEncodingException {
        KeyInfoSupport.addCertificate(keyInfo, cert1);
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(HolderOfKeySubjectConfirmationValidator.PRESENTER_CERT_PARAM, cert1);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.VALID);
        
        Assert.assertSame(validationContext.getDynamicParameters().get(HolderOfKeySubjectConfirmationValidator.CONFIRMED_KEY_INFO_PARAM), keyInfo);
    }
    
    @Test
    public void testInvalidCert() throws AssertionValidationException, CertificateEncodingException {
        KeyInfoSupport.addCertificate(keyInfo, cert1);
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        staticParams.put(HolderOfKeySubjectConfirmationValidator.PRESENTER_CERT_PARAM, cert2);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INVALID);
        
        Assert.assertNull(validationContext.getDynamicParameters().get(HolderOfKeySubjectConfirmationValidator.CONFIRMED_KEY_INFO_PARAM));
    }
    
    @Test
    public void testMissingKeyInfos() throws AssertionValidationException {
        subjectConfirmationData.getUnknownXMLObjects().clear();
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(HolderOfKeySubjectConfirmationValidator.PRESENTER_KEY_PARAM, publicKey1);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INVALID);
        
        Assert.assertNull(validationContext.getDynamicParameters().get(HolderOfKeySubjectConfirmationValidator.CONFIRMED_KEY_INFO_PARAM));
    }
    
    @Test
    public void testMissingPresenterParams() throws AssertionValidationException {
        KeyInfoSupport.addPublicKey(keyInfo, publicKey1);
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INDETERMINATE);
        
        Assert.assertNull(validationContext.getDynamicParameters().get(HolderOfKeySubjectConfirmationValidator.CONFIRMED_KEY_INFO_PARAM));
    }
    
    @Test
    public void testInvalidPublicKeyParam() throws AssertionValidationException {
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(HolderOfKeySubjectConfirmationValidator.PRESENTER_KEY_PARAM, "foobar");
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INDETERMINATE);
        
        Assert.assertNull(validationContext.getDynamicParameters().get(HolderOfKeySubjectConfirmationValidator.CONFIRMED_KEY_INFO_PARAM));
    }
    
    @Test
    public void testInvalidCertParam() throws AssertionValidationException {
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(HolderOfKeySubjectConfirmationValidator.PRESENTER_CERT_PARAM, "foobar");
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INDETERMINATE);
        
        Assert.assertNull(validationContext.getDynamicParameters().get(HolderOfKeySubjectConfirmationValidator.CONFIRMED_KEY_INFO_PARAM));
    }
    
    @Test
    public void testCertAndKeyParamMismatch() throws AssertionValidationException {
        KeyInfoSupport.addPublicKey(keyInfo, publicKey1);
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(HolderOfKeySubjectConfirmationValidator.PRESENTER_KEY_PARAM, publicKey1);
        staticParams.put(HolderOfKeySubjectConfirmationValidator.PRESENTER_CERT_PARAM, cert2);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INDETERMINATE);
        
        Assert.assertNull(validationContext.getDynamicParameters().get(HolderOfKeySubjectConfirmationValidator.CONFIRMED_KEY_INFO_PARAM));
    }
    
    @Test
    public void testNonHOKMethod() throws AssertionValidationException {
        subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);
        subjectConfirmation.setSubjectConfirmationData(buildBasicSubjectConfirmationData());
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(HolderOfKeySubjectConfirmationValidator.PRESENTER_KEY_PARAM, publicKey1);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INDETERMINATE);
        
        Assert.assertNull(validationContext.getDynamicParameters().get(HolderOfKeySubjectConfirmationValidator.CONFIRMED_KEY_INFO_PARAM));
    }
    
    @Test
    public void testNonKeyInfoConfirmationData() throws AssertionValidationException {
        subjectConfirmation.setSubjectConfirmationData(buildBasicSubjectConfirmationData());
        
        Map<String,Object> staticParams = buildBasicStaticParameters();
        staticParams.put(HolderOfKeySubjectConfirmationValidator.PRESENTER_KEY_PARAM, publicKey1);
        
        ValidationContext validationContext = new ValidationContext(staticParams);
        
        Assert.assertEquals(validator.validate(subjectConfirmation, getAssertion(), validationContext), 
                ValidationResult.INVALID);
        
        Assert.assertNull(validationContext.getDynamicParameters().get(HolderOfKeySubjectConfirmationValidator.CONFIRMED_KEY_INFO_PARAM));
    }
    

}
