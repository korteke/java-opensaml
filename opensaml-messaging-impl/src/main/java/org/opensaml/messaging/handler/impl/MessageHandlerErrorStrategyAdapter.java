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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.error.TypedMessageErrorHandler;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;

/**
 * A {@link MessageHandler} which wraps and invokes another handler, catches any {@link Throwable} which is 
 * thrown by the wrapped handler's {@link MessageHandler#invoke(MessageContext)}, and applies
 * a list of configured {@link TypedMessageErrorHandler} strategies.
 * 
 * <p>
 * The configured error handlers are iterated in the supplied order, and each will be given an opportunity to 
 * handle the error if the thrown {@link Throwable} is an instance a type supported by the error handler,
 * as determined by {@link TypedMessageErrorHandler#handlesError(Throwable)}.  This iteration is essentially a 
 * "dynamic catch block".  The first handler to indicate it has handled the error via the return value of 
 * {@link TypedMessageErrorHandler#handleError(Throwable, MessageContext)} terminates the error handler iteration.
 * </p>
 * 
 * <p>
 * A configured error handler may simply adjust or decorate the {@link MessageContext} with additional
 * information (e.g. to register a SOAP fault on behalf of a non-SOAP aware handler), or it may itself directly emit
 * an error response in a protocol- or technology-specific manner.
 * </p>
 * 
 * <p>
 * Whether the thrown {@link Throwable} is rethrown by this handler is determined by the flags 
 * {@link #setRethrowIfHandled(boolean)} and {@link #setRethrowIfNotHandled(boolean)}.
 * </p>
 * 
 * @param <MessageType> the type of message handled by the handler
 */
public class MessageHandlerErrorStrategyAdapter<MessageType> extends AbstractMessageHandler<MessageType> {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(MessageHandlerErrorStrategyAdapter.class);
    
    /** The wrapped message handler. */
    private MessageHandler<MessageType> wrappedHandler;
    
    /** The list of typed error handlers. */
    private List<TypedMessageErrorHandler> errorHandlers;
    
    /** Flag indicating whether the wrapped handler's exception should be rethrown after being handled successfully. */
    private boolean rethrowIfHandled;
    
    /** Flag indicating whether the wrapped handler's exception should be rethrown if not handled successfully
     * by any configured error handler. */
    private boolean rethrowIfNotHandled;
    
    /**
     * Constructor.
     *
     * @param messageHandler the wrapped message handler
     * @param typedErrorHandlers the list of typed error handlers to apply
     */
    public MessageHandlerErrorStrategyAdapter(@Nonnull final MessageHandler<MessageType> messageHandler, 
            @Nonnull final List<TypedMessageErrorHandler> typedErrorHandlers) {
        wrappedHandler = Constraint.isNotNull(messageHandler, "Wrapped MessageHandler cannot be null");
        errorHandlers = new ArrayList<>(Collections2.filter(
                Constraint.isNotNull(typedErrorHandlers, "List of TypedMessageErroHandlers cannot be null"), 
                Predicates.notNull()));
        
        rethrowIfHandled = false;
        rethrowIfNotHandled = true;
    }
    
    /**
     * Set whether to rethrow the error if it is successfully handled by one of the
     * configured {@link TypedMessageErrorHandler}.
     * 
     * <p>Default is: false</p>
     * 
     * @param flag true if should rethrow, false if not
     */
    public void setRethrowIfHandled(boolean flag) {
        rethrowIfHandled = flag;
    }

    /**
     * Set whether to rethrow the error if it is NOT successfully handled by any of the
     * configured {@link TypedMessageErrorHandler}.
     * 
     * <p>Default is: true</p>
     * 
     * @param flag true if should rethrow, false if not
     */
    public void setRethrowIfNotHandled(boolean flag) {
        rethrowIfNotHandled = flag;
    }


    /** {@inheritDoc} */
    protected void doInvoke(MessageContext<MessageType> messageContext) throws MessageHandlerException {
        try {
            wrappedHandler.invoke(messageContext);
        } catch (Throwable t) {
            log.trace("Wrapped message handler threw error", t);
            for (TypedMessageErrorHandler errorHandler : errorHandlers) {
                if (errorHandler.handlesError(t)) {
                    log.trace("Handler indicates it can handle the error: {}", errorHandler.getClass().getName());
                    boolean handled = errorHandler.handleError(t, messageContext);
                    log.trace("Handler's indication whether it actually handled the error: {}", handled);
                    if (handled) {
                        if (rethrowIfHandled) {
                            log.trace("Based on config, rethrowing the handled error");
                            throw t;
                        } else {
                            log.trace("Based on config, swallowing the handled error");
                            return;
                        }
                    } else {
                        log.trace("Handler indicates it did NOT handle the error, continuing with remaining handlers");
                        continue;
                    }
                }
            }
            log.trace("No error handler handled the thrown error");
            if (rethrowIfNotHandled) {
                log.trace("Based on config, rethrowing the unhandled error");
                throw t;
            } else {
                log.trace("Based on config, swallowing the unhandled error");
            }
        }
    }

}
