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

package org.opensaml.messaging.decoder.servlet;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;

import org.opensaml.messaging.decoder.AbstractMessageDecoder;
import org.opensaml.messaging.decoder.MessageDecodingException;

/**
 * Abstract implementation of {@link HttpServletRequestMessageDecoder}.
 * 
 * @param <MessageType> the message type of the message context on which to operate
 */
public abstract class AbstractHttpServletRequestMessageDecoder<MessageType> extends AbstractMessageDecoder<MessageType>
        implements HttpServletRequestMessageDecoder<MessageType> {

    /** The HTTP servlet request. */
    private HttpServletRequest request;

    /** {@inheritDoc} */
    @Nullable public HttpServletRequest getHttpServletRequest() {
        return request;
    }

    /** {@inheritDoc} */
    public synchronized void setHttpServletRequest(@Nullable final HttpServletRequest servletRequest) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);

        request = servletRequest;
    }
    
    /** {@inheritDoc} */
    public void decode() throws MessageDecodingException {
        super.decode();
        getMessageContext().getSubcontext(HttpServletRequestContext.class, true).setHttpServletRequest(getHttpServletRequest());
    }

    /** {@inheritDoc} */
    protected void doDestroy() {
        request = null;
        
        super.doDestroy();
    }

    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();

        if (request == null) {
            throw new ComponentInitializationException("HTTP Servlet request cannot be null");
        }
    }
}