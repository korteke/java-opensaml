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

package org.opensaml.saml.saml2.wssecurity.messaging.impl;

import java.net.URISyntaxException;
import java.security.KeyException;
import java.security.cert.CertificateException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.collection.Pair;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.common.messaging.context.SAMLSelfEntityContext;
import org.opensaml.saml.saml2.assertion.SAML20AssertionValidator;
import org.opensaml.saml.saml2.assertion.SAML2AssertionValidationParameters;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.profile.SAML2ActionTestingSupport;
import org.opensaml.saml.saml2.wssecurity.SAML20AssertionToken;
import org.opensaml.security.SecurityException;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.Body;
import org.opensaml.soap.soap11.Envelope;
import org.opensaml.soap.soap11.Fault;
import org.opensaml.soap.soap11.FaultCode;
import org.opensaml.soap.soap11.Header;
import org.opensaml.soap.util.SOAPSupport;
import org.opensaml.soap.wssecurity.Security;
import org.opensaml.soap.wssecurity.WSSecurityConstants;
import org.opensaml.soap.wssecurity.messaging.Token.ValidationStatus;
import org.opensaml.soap.wssecurity.messaging.WSSecurityContext;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Function;

/**
 *
 */
public class WSSecuritySAML20AssertionTokenSecurityHandlerTest extends XMLObjectBaseTestCase {
    private WSSecuritySAML20AssertionTokenSecurityHandler handler;
    
    private MessageContext<XMLObject> messageContext;
    private MockHttpServletRequest httpServletRequest;
    private Assertion assertion;
    private SubjectConfirmation subjectConfirmation;
    
    private String issuerEntityID = "https://idp.example.org";
    private String rpEntityID = "https://rp.example.com";
    private String requestURL = "https://rp.example.com/wss/saml";
    private String remoteAddr = "10.1.2.3";
    
    @BeforeMethod
    protected void setUp() throws CertificateException, URISyntaxException, KeyException, SecurityException, MarshallingException, SignatureException {
        assertion = buildAssertion();
        messageContext = buildMessageContext();
        httpServletRequest = buildHttpServletRequest();
        
        handler = new WSSecuritySAML20AssertionTokenSecurityHandler();
        handler.setHttpServletRequest(httpServletRequest);
        // do init in the test methods
    }
    
    @Test
    public void testDefaultsValid() throws ComponentInitializationException, MessageHandlerException {
        handler.setAssertionValidator(new MockAssertionValidator(ValidationResult.VALID, subjectConfirmation, false));
        handler.initialize();
        
        handler.invoke(messageContext);
        
        WSSecurityContext securityContext = messageContext.getSubcontext(WSSecurityContext.class);
        Assert.assertNotNull(securityContext);
        Assert.assertEquals(securityContext.getTokens().size(), 1);
        Assert.assertTrue(securityContext.getTokens().get(0) instanceof SAML20AssertionToken);
        SAML20AssertionToken token = (SAML20AssertionToken) securityContext.getTokens().get(0);
        Assert.assertSame(token.getWrappedToken(), assertion);
        Assert.assertEquals(token.getValidationStatus(), ValidationStatus.VALID);
        Assert.assertSame(token.getSubjectConfirmation(), subjectConfirmation);
    }
    
    @Test
    public void testDefaultsInvalid() throws ComponentInitializationException {
        handler.setAssertionValidator(new MockAssertionValidator(ValidationResult.INVALID, null, false));
        handler.initialize();
        
        try {
            handler.invoke(messageContext);
            Assert.fail("Assertion validation should have failed");
        } catch (MessageHandlerException e) {
            Fault fault = messageContext.getSubcontext(SOAP11Context.class, true).getFault();
            Assert.assertNotNull(fault);
            Assert.assertEquals(fault.getCode().getValue(), WSSecurityConstants.SOAP_FAULT_INVALID_SECURITY_TOKEN);
        }
    }
    
    @Test
    public void testDefaultsIndeterminate() throws ComponentInitializationException {
        handler.setAssertionValidator(new MockAssertionValidator(ValidationResult.INDETERMINATE, null, false));
        handler.initialize();
        
        try {
            handler.invoke(messageContext);
            Assert.fail("Assertion validation should have failed");
        } catch (MessageHandlerException e) {
            Fault fault = messageContext.getSubcontext(SOAP11Context.class, true).getFault();
            Assert.assertNotNull(fault);
            Assert.assertEquals(fault.getCode().getValue(), WSSecurityConstants.SOAP_FAULT_INVALID_SECURITY_TOKEN);
        }
    }
    
    @Test
    public void testValidViaLookup() throws ComponentInitializationException, MessageHandlerException {
        handler.setAssertionValidator(null);
        handler.setAssertionValidatorLookup(
                new Function<Pair<MessageContext,Assertion>, SAML20AssertionValidator>() {
                    @Nullable public SAML20AssertionValidator apply(@Nullable Pair<MessageContext, Assertion> input) {
                        return new MockAssertionValidator(ValidationResult.VALID, subjectConfirmation, false);
                    }
                });
                
        handler.initialize();
        
        handler.invoke(messageContext);
        
        WSSecurityContext securityContext = messageContext.getSubcontext(WSSecurityContext.class);
        Assert.assertNotNull(securityContext);
        Assert.assertEquals(securityContext.getTokens().size(), 1);
        Assert.assertTrue(securityContext.getTokens().get(0) instanceof SAML20AssertionToken);
        SAML20AssertionToken token = (SAML20AssertionToken) securityContext.getTokens().get(0);
        Assert.assertSame(token.getWrappedToken(), assertion);
        Assert.assertEquals(token.getValidationStatus(), ValidationStatus.VALID);
        Assert.assertSame(token.getSubjectConfirmation(), subjectConfirmation);
    }
    
    @Test
    public void testInvalidNotFatal() throws ComponentInitializationException, MessageHandlerException {
        handler.setInvalidFatal(false);
        handler.setAssertionValidator(new MockAssertionValidator(ValidationResult.INVALID, null, false));
        handler.initialize();
        
        handler.invoke(messageContext);
        
        WSSecurityContext securityContext = messageContext.getSubcontext(WSSecurityContext.class);
        Assert.assertNotNull(securityContext);
        Assert.assertEquals(securityContext.getTokens().size(), 1);
        Assert.assertTrue(securityContext.getTokens().get(0) instanceof SAML20AssertionToken);
        SAML20AssertionToken token = (SAML20AssertionToken) securityContext.getTokens().get(0);
        Assert.assertSame(token.getWrappedToken(), assertion);
        Assert.assertEquals(token.getValidationStatus(), ValidationStatus.INVALID);
        Assert.assertSame(token.getSubjectConfirmation(), null);
    }
    
    @Test
    public void testIndeterminateNotFatal() throws ComponentInitializationException, MessageHandlerException {
        handler.setInvalidFatal(false);
        handler.setAssertionValidator(new MockAssertionValidator(ValidationResult.INDETERMINATE, null, false));
        handler.initialize();
        
        handler.invoke(messageContext);
        
        WSSecurityContext securityContext = messageContext.getSubcontext(WSSecurityContext.class);
        Assert.assertNotNull(securityContext);
        Assert.assertEquals(securityContext.getTokens().size(), 1);
        Assert.assertTrue(securityContext.getTokens().get(0) instanceof SAML20AssertionToken);
        SAML20AssertionToken token = (SAML20AssertionToken) securityContext.getTokens().get(0);
        Assert.assertSame(token.getWrappedToken(), assertion);
        Assert.assertEquals(token.getValidationStatus(), ValidationStatus.INDETERMINATE);
        Assert.assertSame(token.getSubjectConfirmation(), null);
    }
    
    @Test
    public void testException() throws ComponentInitializationException {
        handler.setAssertionValidator(new MockAssertionValidator(null, null, true));
        handler.initialize();
        
        try {
            handler.invoke(messageContext);
            Assert.fail("Assertion validation should have failed");
        } catch (MessageHandlerException e) {
            Fault fault = messageContext.getSubcontext(SOAP11Context.class, true).getFault();
            Assert.assertNotNull(fault);
            Assert.assertEquals(fault.getCode().getValue(), FaultCode.SERVER);
        }
    }
    
    @Test
    public void testNonSOAP() throws ComponentInitializationException, MessageHandlerException {
        messageContext.removeSubcontext(SOAP11Context.class);
        
        handler.setAssertionValidator(new MockAssertionValidator(ValidationResult.VALID, subjectConfirmation, false));
        handler.initialize();
        
        handler.invoke(messageContext);
        
        WSSecurityContext securityContext = messageContext.getSubcontext(WSSecurityContext.class);
        Assert.assertNull(securityContext);
    }
    
    @Test
    public void testNoAssertions() throws ComponentInitializationException, MessageHandlerException {
        messageContext.getSubcontext(SOAP11Context.class).getEnvelope().getHeader().getUnknownXMLObjects().clear();
        
        handler.setAssertionValidator(new MockAssertionValidator(ValidationResult.VALID, subjectConfirmation, false));
        handler.initialize();
        
        handler.invoke(messageContext);
        
        WSSecurityContext securityContext = messageContext.getSubcontext(WSSecurityContext.class);
        Assert.assertNull(securityContext);
    }
    
    @Test
    public void testNoHeader() throws ComponentInitializationException, MessageHandlerException {
        messageContext.getSubcontext(SOAP11Context.class).getEnvelope().setHeader(null);
        
        handler.setAssertionValidator(new MockAssertionValidator(ValidationResult.VALID, subjectConfirmation, false));
        handler.initialize();
        
        handler.invoke(messageContext);
        
        WSSecurityContext securityContext = messageContext.getSubcontext(WSSecurityContext.class);
        Assert.assertNull(securityContext);
    }
    
    @Test(expectedExceptions=ComponentInitializationException.class)
    public void testNoValidatorOrLookup() throws ComponentInitializationException, MessageHandlerException {
        handler.setAssertionValidator(null);
        handler.setAssertionValidatorLookup(null);
        handler.initialize();
    }
    
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testNoValidatorAndFailedLookup() throws ComponentInitializationException, MessageHandlerException {
        handler.setAssertionValidator(null);
        handler.setAssertionValidatorLookup(new Function<Pair<MessageContext,Assertion>, SAML20AssertionValidator>() {
            @Nullable public SAML20AssertionValidator apply(@Nullable Pair<MessageContext, Assertion> input) {
                return null;
            }
        });
        
        handler.initialize();
        
        handler.invoke(messageContext);
    }
    
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testBadValidationContextBuilder() throws ComponentInitializationException, MessageHandlerException {
        handler.setValidationContextBuilder(
                new Function<SAML20AssertionTokenValidationInput, ValidationContext>() {
                    @Nullable public ValidationContext apply(@Nullable SAML20AssertionTokenValidationInput input) {
                        return null;
                    }
                });
        
        
        handler.setAssertionValidator(new MockAssertionValidator(ValidationResult.VALID, subjectConfirmation, false));
        handler.initialize();
        
        handler.invoke(messageContext);
    }
    

    
    
    //
    // Helper classes and methods
    //

    private MessageContext<XMLObject> buildMessageContext() {
        MessageContext<XMLObject> messageContext = new MessageContext<>();
        messageContext.getSubcontext(SAMLSelfEntityContext.class, true).setEntityId(rpEntityID);
        XMLObject payload = buildXMLObject(simpleXMLObjectQName);
        messageContext.setMessage(payload);
        if (assertion == null) {
            throw new RuntimeException("Assertion wasn't built");
        }
        Envelope envelope = buildXMLObject(Envelope.DEFAULT_ELEMENT_NAME);
        envelope.setBody((Body)buildXMLObject(Body.DEFAULT_ELEMENT_NAME));
        envelope.getBody().getUnknownXMLObjects().add(payload);
        envelope.setHeader((Header)buildXMLObject(Header.DEFAULT_ELEMENT_NAME));
        
        Security security = buildXMLObject(Security.ELEMENT_NAME);
        SOAPSupport.addSOAP11MustUnderstandAttribute(security, true);
        security.getUnknownXMLObjects().add(assertion);
        envelope.getHeader().getUnknownXMLObjects().add(security);
        
        messageContext.getSubcontext(SOAP11Context.class, true).setEnvelope(envelope);
        
        return messageContext;
    }

    private MockHttpServletRequest buildHttpServletRequest() throws URISyntaxException, CertificateException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("https");
        request.setServerName("rp.example.com");
        request.setServerPort(443);
        request.setRequestURI("/wss/saml");
        request.setRemoteAddr(remoteAddr);
        return request;
    }

    private Assertion buildAssertion() throws SecurityException, MarshallingException, SignatureException {
        Assertion assertion = SAML2ActionTestingSupport.buildAssertion();
        assertion.setIssuer(SAML2ActionTestingSupport.buildIssuer(issuerEntityID));
        assertion.setSubject(SAML2ActionTestingSupport.buildSubject("barney"));
        subjectConfirmation = buildXMLObject(SubjectConfirmation.DEFAULT_ELEMENT_NAME);
        subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);
        assertion.getSubject().getSubjectConfirmations().add(subjectConfirmation);
        assertion.getAuthnStatements().add(SAML2ActionTestingSupport.buildAuthnStatement());
        return assertion;
    }
    
    
    private static class MockAssertionValidator extends SAML20AssertionValidator {
        private ValidationResult  validationResult;
        private boolean isThrowException;
        private SubjectConfirmation confirmedSubjectConfirmation;
        
        public MockAssertionValidator(ValidationResult result, SubjectConfirmation confirmed, boolean throwException) {
            super(null, null, null, null, null);
            validationResult = result;
            confirmedSubjectConfirmation = confirmed;
            isThrowException = throwException;
        }
        
        @Nonnull public ValidationResult validate(@Nonnull Assertion assertion, @Nonnull ValidationContext context)
                throws AssertionValidationException {
            
            if (isThrowException) {
                throw new AssertionValidationException();
            } else {
                if (confirmedSubjectConfirmation != null) {
                    context.getDynamicParameters().put(SAML2AssertionValidationParameters.CONFIRMED_SUBJECT_CONFIRMATION, confirmedSubjectConfirmation);
                }
                return validationResult;
            }
        }
    }
    

}
