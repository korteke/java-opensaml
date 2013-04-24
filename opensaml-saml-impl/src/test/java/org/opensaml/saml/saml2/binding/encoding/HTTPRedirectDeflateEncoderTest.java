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

package org.opensaml.saml.saml2.binding.encoding;

import java.net.URI;
import java.security.KeyPair;

import net.shibboleth.utilities.java.support.net.UriSupport;

import org.joda.time.DateTime;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.binding.SAMLOutboundDestinationHandler;
import org.opensaml.saml.common.messaging.context.SamlEndpointContext;
import org.opensaml.saml.common.messaging.context.SamlPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SamlProtocolContext;
import org.opensaml.saml.common.messaging.context.SamlSigningContext;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.mock.MockSignatureSigningConfiguration;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit test for redirect encoding.
 */
public class HTTPRedirectDeflateEncoderTest extends XMLObjectBaseTestCase {
    
    /**
     * Tests encoding a SAML message to an servlet response.
     * 
     * @throws Exception
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testResponseEncoding() throws Exception {
        SAMLObjectBuilder<StatusCode> statusCodeBuilder = (SAMLObjectBuilder<StatusCode>) builderFactory
                .getBuilder(StatusCode.DEFAULT_ELEMENT_NAME);
        StatusCode statusCode = statusCodeBuilder.buildObject();
        statusCode.setValue(StatusCode.SUCCESS_URI);

        SAMLObjectBuilder<Status> statusBuilder = (SAMLObjectBuilder<Status>) builderFactory
                .getBuilder(Status.DEFAULT_ELEMENT_NAME);
        Status responseStatus = statusBuilder.buildObject();
        responseStatus.setStatusCode(statusCode);

        SAMLObjectBuilder<Response> responseBuilder = (SAMLObjectBuilder<Response>) builderFactory
                .getBuilder(Response.DEFAULT_ELEMENT_NAME);
        Response samlMessage = responseBuilder.buildObject();
        samlMessage.setID("foo");
        samlMessage.setVersion(SAMLVersion.VERSION_20);
        samlMessage.setIssueInstant(new DateTime(0));
        samlMessage.setStatus(responseStatus);

        SAMLObjectBuilder<Endpoint> endpointBuilder = (SAMLObjectBuilder<Endpoint>) builderFactory
                .getBuilder(AssertionConsumerService.DEFAULT_ELEMENT_NAME);
        Endpoint samlEndpoint = endpointBuilder.buildObject();
        samlEndpoint.setLocation("http://example.org");
        samlEndpoint.setResponseLocation("http://example.org/response");
        
        MessageContext<SAMLObject> messageContext = new MessageContext<SAMLObject>();
        messageContext.setMessage(samlMessage);
        messageContext.getSubcontext(SamlProtocolContext.class, true).setRelayState("relay");
        messageContext.getSubcontext(SamlPeerEntityContext.class, true)
            .getSubcontext(SamlEndpointContext.class, true).setEndpoint(samlEndpoint);
        
        SAMLOutboundDestinationHandler handler = new SAMLOutboundDestinationHandler();
        handler.invoke(messageContext);
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        HTTPRedirectDeflateEncoder encoder = new HTTPRedirectDeflateEncoder();
        encoder.setMessageContext(messageContext);
        encoder.setHttpServletResponse(response);
        
        encoder.initialize();
        encoder.prepareContext();
        encoder.encode();
        
        Assert.assertEquals("UTF-8", response.getCharacterEncoding(), "Unexpected character encoding");
        Assert.assertEquals(response.getHeader("Cache-control"), "no-cache, no-store", "Unexpected cache controls");
        Assert.assertEquals(response.getRedirectedUrl().hashCode(), 1442679212);
    }
    
    /**
     * Tests encoding a SAML message to an servlet response with simple sign.
     * 
     * @throws Exception
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testResponseEncodingWithSimpleSign() throws Exception {
        SAMLObjectBuilder<StatusCode> statusCodeBuilder = (SAMLObjectBuilder<StatusCode>) builderFactory
                .getBuilder(StatusCode.DEFAULT_ELEMENT_NAME);
        StatusCode statusCode = statusCodeBuilder.buildObject();
        statusCode.setValue(StatusCode.SUCCESS_URI);

        SAMLObjectBuilder<Status> statusBuilder = (SAMLObjectBuilder<Status>) builderFactory
                .getBuilder(Status.DEFAULT_ELEMENT_NAME);
        Status responseStatus = statusBuilder.buildObject();
        responseStatus.setStatusCode(statusCode);

        SAMLObjectBuilder<Response> responseBuilder = (SAMLObjectBuilder<Response>) builderFactory
                .getBuilder(Response.DEFAULT_ELEMENT_NAME);
        Response samlMessage = responseBuilder.buildObject();
        samlMessage.setID("foo");
        samlMessage.setVersion(SAMLVersion.VERSION_20);
        samlMessage.setIssueInstant(new DateTime(0));
        samlMessage.setStatus(responseStatus);

        SAMLObjectBuilder<Endpoint> endpointBuilder = (SAMLObjectBuilder<Endpoint>) builderFactory
                .getBuilder(AssertionConsumerService.DEFAULT_ELEMENT_NAME);
        Endpoint samlEndpoint = endpointBuilder.buildObject();
        samlEndpoint.setLocation("http://example.org");
        samlEndpoint.setResponseLocation("http://example.org/response");
        
        MessageContext<SAMLObject> messageContext = new MessageContext<SAMLObject>();
        messageContext.setMessage(samlMessage);
        messageContext.getSubcontext(SamlProtocolContext.class, true).setRelayState("relay");
        messageContext.getSubcontext(SamlPeerEntityContext.class, true)
            .getSubcontext(SamlEndpointContext.class, true).setEndpoint(samlEndpoint);
        KeyPair kp = KeySupport.generateKeyPair("RSA", 1024, null);
        
        MockSignatureSigningConfiguration mockSigningConfig = new MockSignatureSigningConfiguration();
        mockSigningConfig.setSigningCredential(CredentialSupport.getSimpleCredential(kp.getPublic(), kp.getPrivate()));
        messageContext.getSubcontext(SamlSigningContext.class, true).setSigningConfiguration(mockSigningConfig);
        
        SAMLOutboundDestinationHandler handler = new SAMLOutboundDestinationHandler();
        handler.invoke(messageContext);
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        HTTPRedirectDeflateEncoder encoder = new HTTPRedirectDeflateEncoder();
        encoder.setMessageContext(messageContext);
        encoder.setHttpServletResponse(response);
        
        encoder.initialize();
        encoder.prepareContext();
        encoder.encode();
        
        String queryString = new URI(response.getRedirectedUrl()).getRawQuery();
        
        Assert.assertNotNull(UriSupport.getRawQueryStringParameter(queryString, "Signature"), 
                "Signature parameter was not found");
        Assert.assertNotNull(UriSupport.getRawQueryStringParameter(queryString, "SigAlg"), 
                "SigAlg parameter was not found");
        
        // Note: to test that actual signature is cryptographically correct, really need a known good test vector.
        // Need to verify that we're signing over the right data in the right byte[] encoded form.
    }
}