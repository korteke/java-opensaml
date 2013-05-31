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

package org.opensaml.messaging.handler.impl;

import javax.servlet.http.HttpServletRequest;


import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test the HTTP request validation message handler.
 */
public class HTTPRequestValidationHandlerTest {
    
    private MockHttpServletRequest httpRequest;
    
    private String contentType = "text/html";
    private String method = "POST";
    private boolean requireSecured = true;
    
    private HTTPRequestValidationHandler handler;
    
    private MessageContext messageContext;

    @BeforeMethod
    protected void setUp() throws Exception {
        httpRequest = buildServletRequest();
        
        messageContext = new MessageContext();
        
        handler = new HTTPRequestValidationHandler();
        handler.setHttpServletRequest(httpRequest);
        handler.setRequiredContentType(contentType);
        handler.setRequiredRequestMethod(method);
        handler.setRequireSecured(true);
        handler.initialize();
    }
    
    /**
     * Builds a mock {@link HttpServletRequest}.
     * 
     * @return the mock request
     */
    protected MockHttpServletRequest buildServletRequest() {
        MockHttpServletRequest request =  new MockHttpServletRequest();
        request.setContentType(contentType);
        request.setMethod(method);
        request.setSecure(requireSecured);
        return request;
    }

    /**
     * Test all parameters valid.
     * @throws MessageHandlerException 
     */
    @Test
    public void testAllGood() throws MessageHandlerException {
        handler.invoke(messageContext);
    }

    /**
     * Bad request content type.
     * @throws MessageHandlerException 
     */
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testContentTypeBad() throws MessageHandlerException {
        httpRequest.setContentType("GARBAGE");
        handler.invoke(messageContext);
    }

    /**
     * Bad request method.
     * @throws MessageHandlerException 
     */
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testRequestMethodBad() throws MessageHandlerException {
        httpRequest.setMethod("GARBAGE");
        handler.invoke(messageContext);
    }
    
    /**
     * Bad request secure flag.
     * @throws MessageHandlerException 
     */
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testRequireSecureBad() throws MessageHandlerException {
        httpRequest.setSecure(!requireSecured);
        handler.invoke(messageContext);
    }
    
}
