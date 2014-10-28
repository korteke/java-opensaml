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

package org.opensaml.saml.common.binding.security.impl;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

import org.joda.time.DateTime;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests for {@link SAMLOutboundProtocolMessageSigningHandler}.
 */
public class SAMLOutboundProtocolMessageSigningHandlerTest extends XMLObjectBaseTestCase {
    
    private MessageContext<SAMLObject> messageContext;
    
    private SAMLOutboundProtocolMessageSigningHandler handler;
    
    @BeforeMethod
    public void setUp() throws NoSuchAlgorithmException, NoSuchProviderException, ComponentInitializationException {
        KeyPair kp = KeySupport.generateKeyPair("RSA", 2048, null);
        Credential cred = CredentialSupport.getSimpleCredential(kp.getPublic(), kp.getPrivate());
        
        SignatureSigningParameters signingParameters = new SignatureSigningParameters();
        signingParameters.setSigningCredential(cred);
        signingParameters.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        signingParameters.setSignatureReferenceDigestMethod(SignatureConstants.ALGO_ID_DIGEST_SHA256);
        signingParameters.setSignatureCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        
        messageContext = new MessageContext<>();
        messageContext.getSubcontext(SecurityParametersContext.class, true).setSignatureSigningParameters(signingParameters);
        
        handler = new SAMLOutboundProtocolMessageSigningHandler();
        handler.setSignErrorResponses(false);
        handler.initialize();
    }
    
    @Test
    public void testSAML2Response() throws MessageHandlerException {
        Response response = buildXMLObject(Response.DEFAULT_ELEMENT_NAME);
        response.setID("abc123");
        response.setIssueInstant(new DateTime());
        
        Issuer issuer = buildXMLObject(Issuer.DEFAULT_ELEMENT_NAME);
        issuer.setValue("http://idp.example.org");
        response.setIssuer(issuer);
        
        StatusCode sc = buildXMLObject(StatusCode.DEFAULT_ELEMENT_NAME);
        sc.setValue(StatusCode.SUCCESS);
        Status status = buildXMLObject(Status.DEFAULT_ELEMENT_NAME);
        status.setStatusCode(sc);
        response.setStatus(status);
        
        messageContext.setMessage(response);
        
        handler.invoke(messageContext);
        
        Assert.assertNotNull(response.getSignature(), "Signature was null");
    }

    @Test
    public void testSAML2ErrorResponse() throws MessageHandlerException {
        Response response = buildXMLObject(Response.DEFAULT_ELEMENT_NAME);
        response.setID("abc123");
        response.setIssueInstant(new DateTime());
        
        Issuer issuer = buildXMLObject(Issuer.DEFAULT_ELEMENT_NAME);
        issuer.setValue("http://idp.example.org");
        response.setIssuer(issuer);
        
        StatusCode sc = buildXMLObject(StatusCode.DEFAULT_ELEMENT_NAME);
        sc.setValue(StatusCode.RESPONDER);
        Status status = buildXMLObject(Status.DEFAULT_ELEMENT_NAME);
        status.setStatusCode(sc);
        response.setStatus(status);
        
        messageContext.setMessage(response);
        
        handler.invoke(messageContext);
        
        Assert.assertNull(response.getSignature(), "Signature was not null");
    }

}