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

package org.opensaml.messaging.pipeline.httpclient;

import org.opensaml.messaging.decoder.httpclient.HttpClientResponseMessageDecoder;
import org.opensaml.messaging.encoder.httpclient.HttpClientRequestMessageEncoder;
import org.opensaml.messaging.pipeline.MessagePipeline;

/**
 * Specialization of {@link MessagePipeline} which narrows the type of allowed encoders and decoders.
 * 
 * @param <OutboundMessageType> the outbound message type
 * @param <InboundMessageType> the inbound message type
 */
public interface HttpClientMessagePipeline<InboundMessageType, OutboundMessageType> 
        extends MessagePipeline<InboundMessageType, OutboundMessageType> {
    
    /**
     * {@inheritDoc} 
     * 
     * <p>Narrows the super-interface return type to {@link HttpClientRequestMessageEncoder}.</p>
     */
    public HttpClientRequestMessageEncoder<OutboundMessageType> getEncoder();
    
    /**
     * {@inheritDoc} 
     * 
     * <p>Narrows the super-interface return type to {@link HttpClientResponseMessageDecoder}.</p>
     */
    public HttpClientResponseMessageDecoder<InboundMessageType> getDecoder();

}
