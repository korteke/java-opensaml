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

package org.opensaml.messaging.decoder.httpclient;

import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;

import org.apache.http.HttpResponse;
import org.opensaml.messaging.decoder.AbstractMessageDecoder;
import org.opensaml.messaging.decoder.MessageDecodingException;

/**
 * Abstract implementation of {@link HttpClientResponseMessageDecoder}.
 * 
 * @param <MessageType> the message type of the message context on which to operate
 */
public abstract class AbstractHttpClientResponseMessageDecoder<MessageType> extends AbstractMessageDecoder<MessageType>
        implements HttpClientResponseMessageDecoder<MessageType> {

    /** The HTTP client response. */
    private HttpResponse response;

    /** {@inheritDoc} */
    @Nullable public HttpResponse getHttpResponse() {
        return response;
    }

    /** {@inheritDoc} */
    public synchronized void setHttpResponse(@Nullable final HttpResponse clientResponse) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);

        response = clientResponse;
    }
    
    /** {@inheritDoc} */
    public void decode() throws MessageDecodingException {
        super.decode();
    }

    /** {@inheritDoc} */
    protected void doDestroy() {
        response = null;
        
        super.doDestroy();
    }

    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();

        if (response == null) {
            throw new ComponentInitializationException("HTTP client response cannot be null");
        }
    }
}