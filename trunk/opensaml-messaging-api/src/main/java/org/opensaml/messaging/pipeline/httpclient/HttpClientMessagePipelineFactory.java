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

import javax.annotation.Nullable;


/**
 * Factory for instances of {@link HttpClientMessagePipeline}.
 * 
 * @param <OutboundMessageType> the outbound message type
 * @param <InboundMessageType> the inbound message type
 */
public interface HttpClientMessagePipelineFactory<InboundMessageType, OutboundMessageType> {
    
    /**
     * Return a new instance of {@link HttpClientMessagePipeline}.
     * 
     * @return a new pipeline instance
     */
    HttpClientMessagePipeline<InboundMessageType, OutboundMessageType> newInstance();
    
    /**
     * Return a new instance of {@link HttpClientMessagePipeline}.
     * 
     * @param pipelineName the name of the pipeline to return
     * 
     * @return a new pipeline instance
     */
    HttpClientMessagePipeline<InboundMessageType, OutboundMessageType> newInstance(@Nullable final String pipelineName);

}
