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

import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.saml.common.binding.decoding.SAMLMessageDecoder;
import org.opensaml.saml.saml2.core.RequestAbstractType;
import org.opensaml.saml.saml2.core.Response;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test case for HTTP POST decoders.
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
        httpRequest.setParameter("RelayState", expectedRelayValue);
        
        decoder = new HTTPPostDecoder();
        decoder.setParserPool(parserPool);
        decoder.setHttpServletRequest(httpRequest);
        decoder.initialize();
    }

    /**
     * Test decoding a SAML httpRequest.
     * 
     * @throws MessageDecodingException 
     */
    @Test
    public void testRequestDecoding() throws MessageDecodingException {
        httpRequest.setParameter("SAMLRequest", "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPHNhbWxwOkF1dGhuUm"
                + "VxdWVzdCBJRD0iZm9vIiBJc3N1ZUluc3RhbnQ9IjE5NzAtMDEtMDFUMDA6MDA6MDAuMDAwWiIgVmVyc2lvbj0iMi4wIiB4bW"
                + "xuczpzYW1scD0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOnByb3RvY29sIi8+");
        
        decoder.decode();
        MessageContext messageContext = decoder.getMessageContext();

        Assert.assertTrue(messageContext.getMessage() instanceof RequestAbstractType);
        //TODO
        //Assert.assertEquals(messageContext.getRelayState(), expectedRelayValue);
    }

    /**
     * Test decoding a SAML response.
     * 
     * @throws MessageDecodingException 
     */
    @Test
    public void testResponseDecoding() throws MessageDecodingException {
        httpRequest.setParameter("SAMLResponse", "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPHNhbWxwOlJlc3Bvbn"
                + "NlIElEPSJmb28iIElzc3VlSW5zdGFudD0iMTk3MC0wMS0wMVQwMDowMDowMC4wMDBaIiBWZXJzaW9uPSIyLjAiIHhtbG5zOnN"
                + "hbWxwPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6cHJvdG9jb2wiPjxzYW1scDpTdGF0dXM+PHNhbWxwOlN0YXR1c0Nv"
                + "ZGUgVmFsdWU9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDpzdGF0dXM6U3VjY2VzcyIvPjwvc2FtbHA6U3RhdHVzPjwvc"
                + "2FtbHA6UmVzcG9uc2U+");

        decoder.decode();
        MessageContext messageContext = decoder.getMessageContext();

        Assert.assertTrue(messageContext.getMessage() instanceof Response);
        //TODO
        //Assert.assertEquals(messageContext.getRelayState(), expectedRelayValue);
    }
    
}