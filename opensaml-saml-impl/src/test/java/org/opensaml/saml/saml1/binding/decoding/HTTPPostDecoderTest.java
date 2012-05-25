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

package org.opensaml.saml.saml1.binding.decoding;

import java.net.MalformedURLException;
import java.net.URL;

import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.context.SamlProtocolContext;
import org.opensaml.saml.saml1.core.Response;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test case for SAML 1 HTTP POST decoding.
 */
public class HTTPPostDecoderTest extends XMLObjectBaseTestCase {

    private String expectedRelayValue = "relay";

    private HTTPPostDecoder decoder;

    private MockHttpServletRequest httpRequest;

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        httpRequest = new MockHttpServletRequest();
        httpRequest.setMethod("POST");
        httpRequest.setParameter("TARGET", expectedRelayValue);

        decoder = new HTTPPostDecoder();
        decoder.setParserPool(parserPool);
        decoder.setHttpServletRequest(httpRequest);
        decoder.initialize();
    }

    /** Test decoding message. */
    @Test
    public void testDecode() throws Exception {
        Response samlResponse = (Response) unmarshallElement("/data/org/opensaml/saml/saml1/binding/Response.xml");

        String deliveredEndpointURL = samlResponse.getRecipient();

        httpRequest.setParameter("SAMLResponse", encodeMessage(samlResponse));

        populateRequestURL(httpRequest, deliveredEndpointURL);

        decoder.decode();
        MessageContext<SAMLObject> messageContext = decoder.getMessageContext();

        Assert.assertTrue(messageContext.getMessage() instanceof Response);
        Assert.assertEquals(messageContext.getSubcontext(SamlProtocolContext.class).getRelayState(), expectedRelayValue);
    }

    private void populateRequestURL(MockHttpServletRequest request, String requestURL) {
        URL url = null;
        try {
            url = new URL(requestURL);
        } catch (MalformedURLException e) {
            Assert.fail("Malformed URL: " + e.getMessage());
        }
        request.setScheme(url.getProtocol());
        request.setServerName(url.getHost());
        if (url.getPort() != -1) {
            request.setServerPort(url.getPort());
        } else {
            if ("https".equalsIgnoreCase(url.getProtocol())) {
                request.setServerPort(443);
            } else if ("http".equalsIgnoreCase(url.getProtocol())) {
                request.setServerPort(80);
            }
        }
        request.setRequestURI(url.getPath());
        request.setQueryString(url.getQuery());
    }

    protected String encodeMessage(SAMLObject message) throws Exception {
        marshallerFactory.getMarshaller(message).marshall(message);
        String messageStr = SerializeSupport.nodeToString(message.getDOM());

        return Base64Support.encode(messageStr.getBytes("UTF-8"), Base64Support.UNCHUNKED);
    }
}