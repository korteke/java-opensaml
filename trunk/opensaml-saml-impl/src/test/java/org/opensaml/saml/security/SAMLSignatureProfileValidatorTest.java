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

package org.opensaml.saml.security;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.saml.security.SAMLSignatureProfileValidator;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.impl.SignatureImpl;
import org.opensaml.xmlsec.signature.support.SignatureException;

/**
 * Test the SAML XML Signature profile validator.
 */
public class SAMLSignatureProfileValidatorTest extends XMLObjectBaseTestCase {
    
    private SAMLSignatureProfileValidator validator;
    
    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        validator = new SAMLSignatureProfileValidator();
    }


    @Test
    public void testValid() {
        Signature sig = getSignature("/data/org/opensaml/saml/security/Signed-AuthnRequest-Valid.xml");
        assertValidationPass("Valid signature", sig);
    }
    
    @Test
    public void testInvalidNoXMLSignature() {
        Signature sig = getSignature("/data/org/opensaml/saml/security/Signed-AuthnRequest-Valid.xml");
        ((SignatureImpl)sig).setXMLSignature(null);
        assertValidationFail("Invalid signature - no XMLSignature", sig);
    }
    
    @Test
    public void testInvalidTooManyReferences() {
        Signature sig = getSignature("/data/org/opensaml/saml/security/Signed-AuthnRequest-TooManyReferences.xml");
        assertValidationFail("Invalid signature - too many References", sig);
    }
    
    @Test
    public void testInvalidNonLocalURI() {
        Signature sig = getSignature("/data/org/opensaml/saml/security/Signed-AuthnRequest-NonLocalURI.xml");
        assertValidationFail("Invalid signature - non-local Reference URI", sig);
    }
    
    @Test
    public void testInvalidMissingID() {
        Signature sig = getSignature("/data/org/opensaml/saml/security/Signed-AuthnRequest-MissingID.xml");
        assertValidationFail("Invalid signature - missing ID on parent object", sig);
    }
    
    @Test
    public void testInvalidBadURIValue() {
        Signature sig = getSignature("/data/org/opensaml/saml/security/Signed-AuthnRequest-BadURIValue.xml");
        assertValidationFail("Invalid signature - bad URI value", sig);
    }
    
    @Test
    public void testInvalidTooManyTransforms() {
        Signature sig = getSignature("/data/org/opensaml/saml/security/Signed-AuthnRequest-TooManyTransforms.xml");
        assertValidationFail("Invalid signature - too many Transforms", sig);
    }
    
    @Test
    public void testInvalidBadTransform() {
        Signature sig = getSignature("/data/org/opensaml/saml/security/Signed-AuthnRequest-BadTransform.xml");
        assertValidationFail("Invalid signature - bad Transform", sig);
    }
    
    @Test
    public void testInvalidMissingEnvelopedTransform() {
        Signature sig = getSignature("/data/org/opensaml/saml/security/Signed-AuthnRequest-MissingEnvelopedTransform.xml");
        assertValidationFail("Invalid signature - missing Enveloped Transform", sig);
    }
    
    /**
     * Get the signature to validated.  Assume the document element of the file is 
     * a SignableSAMLObject.
     * 
     * @param filename file containing a signed SignableSAMLObject as its document element.
     * @return the signature from the indicated element
     */
    protected Signature getSignature(String filename) {
        SignableSAMLObject signableObj = (SignableSAMLObject) unmarshallElement(filename);
        return signableObj.getSignature();
    }
    
    /**
     * Asserts that the validation of the specified Signature target 
     * was successful, as expected.
     * 
     * @param message failure message if the validation does not pass
     * @param validateTarget the XMLObject to validate
     */
    protected void assertValidationPass(String message, Signature validateTarget) {
       try {
           validator.validate(validateTarget);
       } catch (SignatureException e) {
           Assert.fail(message + " : Expected success, but validation failure raised ValidationException: " + e.getMessage());
       }
    }
    
    /**
     * Asserts that the validation of the specified Signature target 
     * failed, as expected.
     * 
     * @param message failure message if the validation does not fail
     * @param validateTarget XMLObject to validate
     */
    protected void assertValidationFail(String message, Signature validateTarget) {
       try {
           validator.validate(validateTarget);
           Assert.fail(message + " : Validation success, expected failure to raise ValidationException");
       } catch (SignatureException e) {
       }
    }
    

}
