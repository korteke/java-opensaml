/*
 * Copyright 2010 University Corporation for Advanced Internet Development, Inc.
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
package org.opensaml.messaging.error.servlet.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.messaging.error.servlet.HttpServletMessageErrorHandler;

/**
 * Abstract implementation of {@link HttpServletMessageErrorHandler}.
 * 
 * @param <MessageType> the type of messge being handled
 */
public abstract class AbstractHttpServletMessageErrorHandler<MessageType> 
        implements HttpServletMessageErrorHandler<MessageType> {
    
    /** The HTTP servlet request. */
    private HttpServletRequest request;
    
    /** The HTTP servlet response. */
    private HttpServletResponse response;

    /** {@inheritDoc} */
    public HttpServletRequest getHttpServletRequest() {
        return request;
    }

    /** {@inheritDoc} */
    public HttpServletResponse getHttpServletResponse() {
        return response;
    }

    /** {@inheritDoc} */
    public void setHttpServletRequest(HttpServletRequest servletRequest) {
        request = servletRequest;
    }

    /** {@inheritDoc} */
    public void setHttpServletResponse(HttpServletResponse servletResponse) {
        response = servletResponse;
    }

}
