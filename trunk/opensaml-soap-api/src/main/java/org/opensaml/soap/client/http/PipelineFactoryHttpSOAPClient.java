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

package org.opensaml.soap.client.http;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.pipeline.httpclient.HttpClientMessagePipeline;
import org.opensaml.messaging.pipeline.httpclient.HttpClientMessagePipelineFactory;
import org.opensaml.soap.common.SOAPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;

/**
 * SOAP client that is based on {@link HttpClientMessagePipeline}, produced at runtime from an instance of
 * {@link HttpClientMessagePipelineFactory}.
 * 
 * @param <OutboundMessageType> the outbound message type
 * @param <InboundMessageType> the inbound message type
 */
@ThreadSafe
public class PipelineFactoryHttpSOAPClient<OutboundMessageType, InboundMessageType> 
        extends AbstractPipelineHttpSOAPClient<OutboundMessageType, InboundMessageType> {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(PipelineFactoryHttpSOAPClient.class);
    
    /** Factory for the client message pipeline. */
    private HttpClientMessagePipelineFactory<InboundMessageType, OutboundMessageType> pipelineFactory;
    
    /** Strategy function used to resolve the pipeline name to execute. */
    private Function<InOutOperationContext<?, ?>, String> pipelineNameStrategy;
    
    /**
     * Set the message pipeline factory.
     * 
     * @param factory the message pipeline factory
     */
    public void setPipelineFactory(
            @Nonnull final HttpClientMessagePipelineFactory<InboundMessageType, OutboundMessageType> factory) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        
        pipelineFactory = Constraint.isNotNull(factory, "HttpClientPipelineFactory cannot be null"); 
    }
    
    /**
     * Set the strategy function used to resolve the name of the pipeline to use.  Null may be specified.
     * 
     * @param function the strategy function, or null
     */
    public void setPipelineNameStrategy(@Nullable final Function<InOutOperationContext<?, ?>, String> function) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        ComponentSupport.ifDestroyedThrowDestroyedComponentException(this);
        
        pipelineNameStrategy = function;
    }

    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (pipelineFactory == null) {
            throw new ComponentInitializationException("HttpClientPipelineFactory cannot be null");
        } 
    }

    /** {@inheritDoc} */
    protected void doDestroy() {
        pipelineFactory = null;
        pipelineNameStrategy = null;
        
        super.doDestroy();
    }

    /** {@inheritDoc} */
    @Nonnull protected HttpClientMessagePipeline<InboundMessageType, OutboundMessageType> 
            resolvePipeline(InOutOperationContext operationContext) throws SOAPException {
        
        String resolvedPipelineName = null;
        try {
            resolvedPipelineName = resolvePipelineName(operationContext);
            log.debug("Resolved pipeline name: {}", resolvedPipelineName);
            if (resolvedPipelineName != null) {
                return newPipeline(resolvedPipelineName);
            } else {
                return newPipeline();
            }
        } catch (SOAPException e) {
            log.warn("Problem resolving pipeline instance with name: {}", resolvedPipelineName, e);
            throw e;
        } catch (Exception e) {
            // This is to handle RuntimeExceptions, for example thrown by Spring dynamic factory approaches
            log.warn("Problem resolving pipeline instance with name: {}", resolvedPipelineName, e);
            throw new SOAPException("Could not resolve pipeline with name: " + resolvedPipelineName, e);
        }
    }

    /** {@inheritDoc} */
    @Nonnull protected HttpClientMessagePipeline<InboundMessageType, OutboundMessageType> newPipeline() 
            throws SOAPException {
        // Note: in a Spring environment, the actual factory impl might be a proxy via ServiceLocatorFactoryBean
        return pipelineFactory.newInstance();
    }
    
    /**
     * Get a new instance of the {@link HttpClientMessagePipeline} to be processed.
     * 
     * <p>
     * Each call to this (factory) method MUST produce a new instance of the pipeline.
     * </p>
     * 
     * @param name the name of pipeline to return
     * 
     * @return the new pipeline instance
     * 
     * @throws SOAPException if there is an error obtaining a new pipeline instance
     */
    @Nullable protected HttpClientMessagePipeline<InboundMessageType, OutboundMessageType> newPipeline(
            @Nullable final String name) throws SOAPException {
        // Note: in a Spring environment, the actual factory impl might be a proxy via ServiceLocatorFactoryBean
        return pipelineFactory.newInstance(name);
    }
    
    /**
     * Resolve the name of the pipeline to use.
     * 
     * @param operationContext the current operation context
     * @return the pipeline name, may be null
     */
    @Nullable protected String resolvePipelineName(@Nonnull final InOutOperationContext operationContext) {
        if (pipelineNameStrategy != null) {
            return pipelineNameStrategy.apply(operationContext);
        } else {
            return null;
        }
    }

}
