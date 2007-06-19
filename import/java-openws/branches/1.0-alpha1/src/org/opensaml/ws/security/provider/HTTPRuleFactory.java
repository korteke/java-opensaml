/*
 * Copyright [2006] [University Corporation for Advanced Internet Development, Inc.]
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

import javax.servlet.http.HttpServletRequest;

import org.opensaml.ws.security.SecurityPolicyContext;
import org.opensaml.ws.security.SecurityPolicyException;
import org.opensaml.ws.security.SecurityPolicyRule;
import org.opensaml.ws.security.SecurityPolicyRuleFactory;
import org.opensaml.xml.XMLObject;

/**
 * A factory for a rule that checks that certain HTTP request characterisitics are met. Specifically the following items
 * are tested:
 * 
 * <ul>
 * <li>Request's content type contains the given content type</li>
 * <li>Request's character encoding matches the given character encoding</li>
 * <li>Request's scheme (http, https) match the given scheme</li>
 * <li>Request's method (POST, GET) match the given request method</li>
 * <li>Request used a secure transport (SSL, TLS) if it was required to be secure</li>
 * </ul>
 * 
 * Any option that is not set is not evaluated. No Issuer is determined or authenticated by this rule.
 */
public class HTTPRuleFactory implements SecurityPolicyRuleFactory<HttpServletRequest> {

    /** Expected content type of the request. */
    private String contentType;

    /** Expected method of the request. */
    private String requestMethod;

    /** Expected character encoding of the request. */
    private String characterEncoding;

    /** Expected scheme of the request. */
    private String requestScheme;

    /** Whether the request must be secure. */
    private boolean requireSecured;

    /**
     * Gets the expected content type of the request.
     * 
     * @return expected content type of the request
     */
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    /**
     * Sets the expected content type of the request.
     * 
     * @param encoding expected content type of the request
     */
    public void setCharacterEncoding(String encoding) {
        characterEncoding = encoding;
    }

    /**
     * Gets the expected content type of the request.
     * 
     * @return expected content type of the request
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the expected content type of the request.
     * 
     * @param type expected content type of the request
     */
    public void setContentType(String type) {
        contentType = type;
    }

    /**
     * Gets the expected method of the request.
     * 
     * @return expected method of the request
     */
    public String getRequestMethod() {
        return requestMethod;
    }

    /**
     * Sets expected method of the request.
     * 
     * @param method expected method of the request
     */
    public void setRequestMethod(String method) {
        requestMethod = method;
    }

    /**
     * Gets the expected scheme of the request.
     * 
     * @return expected scheme of the request
     */
    public String getRequestScheme() {
        return requestScheme;
    }

    /**
     * Sets the expected scheme of the request.
     * 
     * @param scheme expected scheme of the request
     */
    public void setRequestScheme(String scheme) {
        requestScheme = scheme;
    }

    /**
     * Gets whether a secure request is required.
     * 
     * @return whether a secure request is required
     */
    public boolean isRequireSecured() {
        return requireSecured;
    }

    /**
     * Sets whether a secure request is required.
     * 
     * @param secured whether a secure request is required
     */
    public void setRequireSecured(boolean secured) {
        requireSecured = secured;
    }

    /** {@inheritDoc} */
    public SecurityPolicyRule<HttpServletRequest> createRuleInstance() {
        return new HTTPRule(getContentType(), getCharacterEncoding(), getRequestScheme(), getRequestMethod(),
                isRequireSecured());
    }

    /**
     * Policy rule for checking basic HTTP request requirements.
     */
    protected class HTTPRule implements SecurityPolicyRule<HttpServletRequest> {

        /** Expected content type of the request. */
        private String contentType;

        /** Expected method of the request. */
        private String requestMethod;

        /** Expected character encoding of the request. */
        private String characterEncoding;

        /** Expected scheme of the request. */
        private String requestScheme;

        /** Whether the request must be secure. */
        private boolean requireSecured;

        /**
         * Constructor.
         * 
         * @param type expected content type
         * @param encoding expected character encoding
         * @param scheme expected scheme
         * @param method expected request method
         * @param secured whether the request must be secured
         */
        public HTTPRule(String type, String encoding, String scheme, String method, boolean secured) {
            contentType = type;
            characterEncoding = encoding;
            requestScheme = scheme;
            requestMethod = method;
            requireSecured = secured;
        }

        /** {@inheritDoc} */
        public void evaluate(HttpServletRequest request, XMLObject message, SecurityPolicyContext context)
                throws SecurityPolicyException {

            if (contentType != null && !request.getContentType().contains(contentType)) {
                throw new SecurityPolicyException("Invalid content type, expected " + contentType + " but was "
                        + request.getContentType());
            }

            if (characterEncoding != null && !request.getCharacterEncoding().equals(characterEncoding)) {
                throw new SecurityPolicyException("Invalid character encoding, expected " + characterEncoding
                        + " but was " + request.getCharacterEncoding());
            }

            if (requestScheme != null && !request.getScheme().equals(requestScheme)) {
                throw new SecurityPolicyException("Invalid request scheme, expected " + requestScheme + " but was "
                        + request.getScheme());
            }

            if (requestMethod != null && !request.getMethod().equals(requestMethod)) {
                throw new SecurityPolicyException("Invalid request method, expected " + requestMethod + " but was "
                        + request.getMethod());
            }

            if (requireSecured && !request.isSecure()) {
                throw new SecurityPolicyException("Request was required to be secured but was not");
            }
        }
    }
}
