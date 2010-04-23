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
package org.opensaml.messaging.decoder.servlet.impl;

import javax.servlet.http.HttpServletRequest;

import org.opensaml.messaging.decoder.impl.BaseMessageDecoder;
import org.opensaml.messaging.decoder.servlet.HttpServletRequestMessageDecoder;


/**
 * Abstract implementation of {@link HttpServletRequestMessageDecoder}.
 * 
 * @param <MessageType> the message type of the message context on which to operate
 */
public abstract class AbstractHttpServletRequestMessageDecoder<MessageType> extends BaseMessageDecoder<MessageType> 
        implements HttpServletRequestMessageDecoder<MessageType> {
    
    /** The HTTP servlet request. */
    private HttpServletRequest request;
    
    /** {@inheritDoc} */
    public HttpServletRequest getHttpServletRequest() {
        return request;
    }
    
    /** {@inheritDoc} */
    public void setHttpServletRequest(HttpServletRequest servletRequest) {
        request = servletRequest;
    }
    
}
