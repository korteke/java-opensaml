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

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.Assert;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyPair;

import net.shibboleth.utilities.java.support.xml.SerializeSupport;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.saml.common.binding.BasicSAMLMessageContext;
import org.opensaml.saml.common.binding.decoding.SAMLMessageDecoder;
import org.opensaml.saml.saml2.binding.decoding.HTTPSOAP11Decoder;
import org.opensaml.saml.saml2.core.AttributeQuery;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.security.SecurityException;
import org.opensaml.security.SecurityHelper;
import org.opensaml.security.credential.Credential;
import org.opensaml.soap.soap11.Envelope;
import org.opensaml.ws.message.decoder.MessageDecodingException;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.ws.transport.http.HttpServletRequestAdapter;
import org.opensaml.xmlsec.XMLSecurityHelper;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.Signer;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Test case for HTTP SOAP 1.1 decoder.
 */
public class HTTPSOAP11DecoderTest extends XMLObjectBaseTestCase {
    
    private String attribQueryDestination = "https://idp.example.com/idp/aa";
    
    private SAMLMessageDecoder decoder;
    
    private BasicSAMLMessageContext messageContext;
    
    private MockHttpServletRequest httpRequest;
    
    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        httpRequest = new MockHttpServletRequest();
        httpRequest.setMethod("POST");
        
        messageContext = new BasicSAMLMessageContext();
        messageContext.setInboundMessageTransport(new HttpServletRequestAdapter(httpRequest));
        
        decoder = new HTTPSOAP11Decoder();
    }

    /**
     * Tests decoding a SOAP 1.1 message.
     */
    @Test
    public void testDecoding() throws Exception {
        String requestContent = "<soap11:Envelope xmlns:soap11=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                + "<soap11:Body><samlp:Response ID=\"foo\" IssueInstant=\"1970-01-01T00:00:00.000Z\" Version=\"2.0\" "
                + "xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\"><samlp:Status><samlp:StatusCode "
                + "Value=\"urn:oasis:names:tc:SAML:2.0:status:Success\"/></samlp:Status></samlp:Response>"
                + "</soap11:Body></soap11:Envelope>";
        httpRequest.setContent(requestContent.getBytes());

        decoder.decode(messageContext);

        Assert.assertTrue(messageContext.getInboundMessage() instanceof Envelope);
        Assert.assertTrue(messageContext.getInboundSAMLMessage() instanceof Response);
    }
    
    @Test
    public void testMessageEndpointGood() throws Exception {
        Envelope soapEnvelope = (Envelope) unmarshallElement("/data/org/opensaml/saml/saml2/binding/AttributeQuerySOAP.xml");
        
        AttributeQuery samlRequest = (AttributeQuery) soapEnvelope.getBody().getUnknownXMLObjects().get(0);
        String deliveredEndpointURL = samlRequest.getDestination();
        
        httpRequest.setContent(encodeMessage(soapEnvelope).getBytes());
        
        populateRequestURL(httpRequest, deliveredEndpointURL);
        
        try {
            decoder.decode(messageContext);
        } catch (SecurityException e) {
            Assert.fail("Caught SecurityException: " + e.getMessage());
        } catch (MessageDecodingException e) {
            Assert.fail("Caught MessageDecodingException: " + e.getMessage());
        }
    }
    
    @Test
    public void testMessageEndpointGoodWithQueryParams() throws Exception {
        Envelope soapEnvelope = (Envelope) unmarshallElement("/data/org/opensaml/saml/saml2/binding/AttributeQuerySOAP.xml");
        
        AttributeQuery samlRequest = (AttributeQuery) soapEnvelope.getBody().getUnknownXMLObjects().get(0);
        String deliveredEndpointURL = samlRequest.getDestination() + "?paramFoo=bar&paramBar=baz";
        
        httpRequest.setContent(encodeMessage(soapEnvelope).getBytes());
        
        populateRequestURL(httpRequest, deliveredEndpointURL);

        try {
            decoder.decode(messageContext);
        } catch (SecurityException e) {
            Assert.fail("Caught SecurityException: " + e.getMessage());
        } catch (MessageDecodingException e) {
            Assert.fail("Caught MessageDecodingException: " + e.getMessage());
        }
    }
    
    @Test
    public void testMessageEndpointInvalidURI() throws Exception {
        Envelope soapEnvelope = (Envelope) unmarshallElement("/data/org/opensaml/saml/saml2/binding/AttributeQuerySOAP.xml");
        
        AttributeQuery samlRequest = (AttributeQuery) soapEnvelope.getBody().getUnknownXMLObjects().get(0);
        String deliveredEndpointURL = samlRequest.getDestination() + "/some/other/endpointURI";
        
        httpRequest.setContent(encodeMessage(soapEnvelope).getBytes());
        
        populateRequestURL(httpRequest, deliveredEndpointURL);

        try {
            decoder.decode(messageContext);
            Assert.fail("Passed delivered endpoint check, should have failed");
        } catch (SecurityException e) {
            // do nothing, failure expected
        } catch (MessageDecodingException e) {
            Assert.fail("Caught MessageDecodingException: " + e.getMessage());
        }
    }
    
    @Test
    public void testMessageEndpointInvalidHost() throws Exception {
        Envelope soapEnvelope = (Envelope) unmarshallElement("/data/org/opensaml/saml/saml2/binding/AttributeQuerySOAP.xml");
        
        String deliveredEndpointURL = "https://bogusidp.example.com/idp/sso";
        
        httpRequest.setContent(encodeMessage(soapEnvelope).getBytes());
        
        populateRequestURL(httpRequest, deliveredEndpointURL);

        try {
            decoder.decode(messageContext);
            Assert.fail("Passed delivered endpoint check, should have failed");
        } catch (SecurityException e) {
            // do nothing, failure expected
        } catch (MessageDecodingException e) {
            Assert.fail("Caught MessageDecodingException: " + e.getMessage());
        }
    }
    
    @Test
    public void testMessageEndpointMissingDestinationNotSigned() throws Exception {
        Envelope soapEnvelope = (Envelope) unmarshallElement("/data/org/opensaml/saml/saml2/binding/AttributeQuerySOAP.xml");
        
        AttributeQuery samlRequest = (AttributeQuery) soapEnvelope.getBody().getUnknownXMLObjects().get(0);
        samlRequest.setDestination(null);
        
        String deliveredEndpointURL = attribQueryDestination;
        
        httpRequest.setContent(encodeMessage(soapEnvelope).getBytes());
        
        populateRequestURL(httpRequest, deliveredEndpointURL);

        try {
            decoder.decode(messageContext);
        } catch (SecurityException e) {
            Assert.fail("Caught SecurityException: " + e.getMessage());
        } catch (MessageDecodingException e) {
            Assert.fail("Caught MessageDecodingException: " + e.getMessage());
        }
    }
    
    @Test
    public void testMessageEndpointMissingDestinationSigned() throws Exception {
        Envelope soapEnvelope = (Envelope) unmarshallElement("/data/org/opensaml/saml/saml2/binding/AttributeQuerySOAP.xml");
        
        AttributeQuery samlRequest = (AttributeQuery) soapEnvelope.getBody().getUnknownXMLObjects().get(0);
        samlRequest.setDestination(null);
        
        Signature signature = (Signature) buildXMLObject(Signature.DEFAULT_ELEMENT_NAME);
        KeyPair kp = SecurityHelper.generateKeyPair("RSA", 1024, null);
        Credential signingCred = SecurityHelper.getSimpleCredential(kp.getPublic(), kp.getPrivate());
        signature.setSigningCredential(signingCred);
        samlRequest.setSignature(signature);
        XMLSecurityHelper.prepareSignatureParams(signature, signingCred, null, null);
        marshallerFactory.getMarshaller(soapEnvelope).marshall(soapEnvelope);
        Signer.signObject(signature);
        
        String deliveredEndpointURL = attribQueryDestination;
        
        httpRequest.setContent(encodeMessage(soapEnvelope).getBytes());
        
        populateRequestURL(httpRequest, deliveredEndpointURL);

        try {
            decoder.decode(messageContext);
            // SOAP binding doesn't require the Destination, even when signed
        } catch (SecurityException e) {
            Assert.fail("Caught SecurityException: " + e.getMessage());
        } catch (MessageDecodingException e) {
            Assert.fail("Caught MessageDecodingException: " + e.getMessage());
        }
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
    
    protected String encodeMessage(XMLObject message) throws MessageEncodingException, MarshallingException {
        marshallerFactory.getMarshaller(message).marshall(message);
        return SerializeSupport.nodeToString(message.getDOM());
    }
}