/*
 * Copyright [2007] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.ws.security.provider;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.opensaml.ws.security.BaseSecurityPolicyTest;
import org.opensaml.ws.security.SecurityPolicyRuleFactory;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Test the HTTP security policy rule.
 */
public class HTTPRuleTest extends BaseSecurityPolicyTest {
    
    private HTTPRuleFactory httpRuleFactory;
    
    private String contentType = "text/html";
    private String method = "POST";
    private String characterEncoding = "UTF-8";
    private String scheme = "http";
    private boolean requireSecured = true;

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        super.setUp();
        
        httpRuleFactory = new HTTPRuleFactory();
        httpRuleFactory.setContentType(contentType);
        httpRuleFactory.setRequestMethod(method);
        httpRuleFactory.setCharacterEncoding(characterEncoding);
        httpRuleFactory.setRequestScheme(scheme);
        httpRuleFactory.setRequireSecured(requireSecured);
        
        getPolicyRuleFactories().add((SecurityPolicyRuleFactory) httpRuleFactory);
    }
    
    /** {@inheritDoc} */
    protected MockHttpServletRequest buildServletRequest() {
        MockHttpServletRequest request =  super.buildServletRequest();
        request.setContentType(contentType);
        request.setMethod(method);
        request.setCharacterEncoding(characterEncoding);
        request.setScheme(scheme);
        request.setSecure(requireSecured);
        return request;
    }

    /**
     * Test all parameters valid.
     */
    public void testAllGood() {
        assertPolicySuccess("All request parameters are valid");
    }

    /**
     * Bad request content type.
     */
    public void testContentTypeBad() {
        httpRequest.setContentType("GARBAGE");
        assertPolicyFail("Invalid content type");
    }

    /**
     * Bad request method.
     */
    public void testRequestMethodBad() {
        httpRequest.setMethod("GARBAGE");
        assertPolicyFail("Invalid request method");
    }
    
    /**
     * Bad request character encoding.
     */
    public void testCharacterEncodingBad() {
        httpRequest.setCharacterEncoding("GARBAGE");
        assertPolicyFail("Invalid character encoding");
    }

    /**
     * Bad request scheme.
     */
    public void testRequestSchemeBad() {
        httpRequest.setScheme("GARBAGE");
        assertPolicyFail("Invalid request scheme");
    }
    
    /**
     * Bad request secure flag.
     */
    public void testRequireSecureBad() {
        httpRequest.setSecure(!requireSecured);
        assertPolicyFail("Invalid secure flag");
    }
}
