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

package org.opensaml.saml.saml2.binding.decoding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.RequestAbstractType;
import org.opensaml.saml.saml2.core.Response;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 */
public class HTTPRedirectDeflateDecoderTest extends XMLObjectBaseTestCase {

    private String expectedRelayValue = "relay";

    private HTTPRedirectDeflateDecoder decoder;

    private MockHttpServletRequest httpRequest;

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        httpRequest = new MockHttpServletRequest();
        httpRequest.setMethod("GET");
        httpRequest.setParameter("RelayState", expectedRelayValue);

        decoder = new HTTPRedirectDeflateDecoder();
        decoder.setParserPool(parserPool);
        decoder.setHttpServletRequest(httpRequest);
        decoder.initialize();
    }

    @Test
    public void testResponseDecoding() throws MessageDecodingException {
        // Note, Spring's Mock objects don't do URL encoding/decoding, so this is the URL decoded form
        httpRequest
                .setParameter(
                        "SAMLResponse",
                        "fZAxa8NADIX3/opDe3yXLG2F7VASCoF2qdMM3Y6LkhrOp8PSlfz8uqYdvBTeIMHT08ert7chmi8apefUwLpyYCgFPvfp2sD78Xn1ANv2rhY/xIxvJJmTkNmTaJ+8zkefqhmtpZsfcqSKxyuYw76BC/M0iBQ6JFGfdMp/vHcrt550dA5nVc65DzCnP4TND8IElQTnpw2UMSF76QWTH0hQA3ZPry84OTGPrBw4QvuL2KnXIsttx2cyJx8L/R8msxu7EgKJgG1ruwy1yxrabw==");

        populateRequestURL(httpRequest, "http://example.org");

        decoder.decode();
        MessageContext messageContext = decoder.getMessageContext();

        Assert.assertTrue(messageContext.getMessage() instanceof Response);
        Assert.assertEquals(SAMLBindingSupport.getRelayState(messageContext), expectedRelayValue);
    }

    @Test
    public void testRequestDecoding() throws MessageDecodingException, MessageEncodingException, MarshallingException {
        AuthnRequest samlRequest =
                (AuthnRequest) unmarshallElement("/data/org/opensaml/saml/saml2/binding/AuthnRequest.xml");
        samlRequest.setDestination(null);

        httpRequest.setParameter("SAMLRequest", encodeMessage(samlRequest));

        decoder.decode();
        MessageContext<SAMLObject> messageContext = decoder.getMessageContext();

        Assert.assertTrue(messageContext.getMessage() instanceof RequestAbstractType);
        Assert.assertEquals(SAMLBindingSupport.getRelayState(messageContext), expectedRelayValue);
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

    protected String encodeMessage(SAMLObject message) throws MessageEncodingException, MarshallingException {
        try {
            marshallerFactory.getMarshaller(message).marshall(message);
            String messageStr = SerializeSupport.nodeToString(message.getDOM());

            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            Deflater deflater = new Deflater(Deflater.DEFLATED, true);
            DeflaterOutputStream deflaterStream = new DeflaterOutputStream(bytesOut, deflater);
            deflaterStream.write(messageStr.getBytes("UTF-8"));
            deflaterStream.finish();

            return Base64Support.encode(bytesOut.toByteArray(), Base64Support.UNCHUNKED);
        } catch (IOException e) {
            throw new MessageEncodingException("Unable to DEFLATE and Base64 encode SAML message", e);
        }
    }
}