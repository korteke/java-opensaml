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

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.joda.time.DateTime;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.messaging.context.SamlEndpointContext;
import org.opensaml.saml.common.messaging.context.SamlPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SamlProtocolContext;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test case for {@link HTTPPostEncoder}.
 */
public class HTTPPostEncoderTest extends XMLObjectBaseTestCase {

    /** Velocity template engine. */
    private VelocityEngine velocityEngine;

    /** {@inheritDoc} */
    @BeforeMethod
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.INPUT_ENCODING, "UTF-8");
        velocityEngine.setProperty(RuntimeConstants.OUTPUT_ENCODING, "UTF-8");
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine.init();
    }

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
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        HTTPPostEncoder encoder = new HTTPPostEncoder();
        encoder.setMessageContext(messageContext);
        encoder.setHttpServletResponse(response);
        
        encoder.setVelocityEngine(velocityEngine);
        encoder.setVelocityTemplateId("/templates/saml2-post-binding.vm");
        
        encoder.initialize();
        encoder.prepareContext();
        encoder.encode();

        Assert.assertEquals(response.getContentType(), "text/html", "Unexpected content type");
        Assert.assertEquals("UTF-8", response.getCharacterEncoding(), "Unexpected character encoding");
        Assert.assertEquals(response.getHeader("Cache-control"), "no-cache, no-store", "Unexpected cache controls");
        Assert.assertEquals(response.getContentAsString().hashCode(), 1080099645);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRequestEncoding() throws Exception {
        SAMLObjectBuilder<AuthnRequest> responseBuilder = (SAMLObjectBuilder<AuthnRequest>) builderFactory
                .getBuilder(AuthnRequest.DEFAULT_ELEMENT_NAME);
        AuthnRequest samlMessage = responseBuilder.buildObject();
        samlMessage.setID("foo");
        samlMessage.setVersion(SAMLVersion.VERSION_20);
        samlMessage.setIssueInstant(new DateTime(0));

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
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        HTTPPostEncoder encoder = new HTTPPostEncoder();
        encoder.setMessageContext(messageContext);
        encoder.setHttpServletResponse(response);
        
        encoder.setVelocityEngine(velocityEngine);
        encoder.setVelocityTemplateId("/templates/saml2-post-binding.vm");
        
        encoder.initialize();
        encoder.prepareContext();
        encoder.encode();

        Assert.assertEquals(response.getContentType(), "text/html", "Unexpected content type");
        Assert.assertEquals("UTF-8", response.getCharacterEncoding(), "Unexpected character encoding");
        Assert.assertEquals(response.getHeader("Cache-control"), "no-cache, no-store", "Unexpected cache controls");
        Assert.assertEquals(response.getContentAsString().hashCode(), 1039626476);
    }
}