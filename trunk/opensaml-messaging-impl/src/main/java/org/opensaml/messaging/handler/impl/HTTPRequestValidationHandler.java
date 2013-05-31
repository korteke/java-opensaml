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

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A message handler that checks basic HTTP request properties.
 */
public class HTTPRequestValidationHandler extends AbstractMessageHandler {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(HTTPRequestValidationHandler.class);

    /** Expected content type of the request. */
    private String requiredContentType;

    /** Expected method of the request. */
    private String requiredRequestMethod;

    /** Whether the request must be secure. */
    private boolean requireSecured;
    
    /** The HTTP servlet request being evaluated. */
    private HttpServletRequest httpServletRequest;

    /**
     * Get the required content type.
     * 
     * @return the required content type
     */
    public String getRequiredContentType() {
        return requiredContentType;
    }

    /**
     * Set the required content type.
     * 
     * @param contentType the content type
     */
    public void setRequiredContentType(String contentType) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        requiredContentType = contentType;
        requiredRequestMethod = StringSupport.trimOrNull(contentType);
    }

    /**
     * Get the required request method.
     * 
     * @return the required request method
     */
    public String getRequiredRequestMethod() {
        return requiredRequestMethod;
    }

    /**
     * Set the required request method.
     * 
     * @param requestMethod the required request method
     */
    public void setRequiredRequestMethod(String requestMethod) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        requiredRequestMethod = StringSupport.trimOrNull(requestMethod);
    }

    /**
     * Get whether request is required to be secure.
     * 
     * @return true if required to be secure, false otherwise
     */
    public boolean isRequireSecured() {
        return requireSecured;
    }

    /**
     * Set whether request is required to be secure. 
     * 
     * @param secured true if required to be secure, false otherwise
     */
    public void setRequireSecured(boolean secured) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        requireSecured = secured;
    }

    /**
     * Get the HTTP servlet request instance being evaluated.
     * 
     * @return returns the request instance
     */
    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    /**
     * Set the HTTP servlet request instance being evaluated.
     * 
     * @param request the request instance
     */
    public void setHttpServletRequest(HttpServletRequest request) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        httpServletRequest = Constraint.isNotNull(request, "HttpServletRequest may not be null");
    }

    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        Constraint.isNotNull(getHttpServletRequest(), "HttpServletRequest may not be null");
    }

    /**
     * Evaluates whether the specified HTTP servlet request meets all requirements.
     * 
     * @param messageContext message context being evaluated
     * 
     * @throws MessageHandlerException thrown if the request does not meet the requirements of the handler
     */
    protected void doInvoke(MessageContext messageContext) throws MessageHandlerException {
        evaluateContentType(getHttpServletRequest());
        evaluateRequestMethod(getHttpServletRequest());
        evaluateSecured(getHttpServletRequest());
    }

    /**
     * Checks if the request is of the correct content type.
     * 
     * @param request the request being evaluated
     * 
     * @throws MessageHandlerException thrown if the content type was an unexpected value
     */
    protected void evaluateContentType(HttpServletRequest request) throws MessageHandlerException {
        String transportContentType = request.getHeader("Content-Type");
        if (getRequiredContentType() != null && !transportContentType.startsWith(getRequiredContentType())) {
            log.error("Invalid content type, expected '{}' but was '{}'", getRequiredContentType(), 
                    transportContentType);
            throw new MessageHandlerException("Invalid content type, expected " + getRequiredContentType() 
                    + " but was " + transportContentType);
        }
    }

    /**
     * Checks if the request contains the correct request method.
     * 
     * @param request the request being evaluated
     * 
     * @throws MessageHandlerException thrown if the request method was an unexpected value
     */
    protected void evaluateRequestMethod(HttpServletRequest request) throws MessageHandlerException {
        String transportMethod = request.getMethod();
        if (getRequiredRequestMethod() != null && !transportMethod.equalsIgnoreCase(getRequiredRequestMethod())) {
            log.error("Invalid request method, expected '{}' but was '{}'", getRequiredRequestMethod(), 
                    transportMethod);
            throw new MessageHandlerException("Invalid request method, expected " + getRequiredRequestMethod() 
                    + " but was " + transportMethod);
        }
    }

    /**
     * Checks if the request is secured.
     * 
     * @param request the request being evaluated
     * 
     * @throws MessageHandlerException thrown if the request is not secure and was required to be
     */
    protected void evaluateSecured(HttpServletRequest request) throws MessageHandlerException {
        if (isRequireSecured() && !request.isSecure()) {
            log.error("Request was required to be secured but was not");
            throw new MessageHandlerException("Request was required to be secured but was not");
        }
    }
}