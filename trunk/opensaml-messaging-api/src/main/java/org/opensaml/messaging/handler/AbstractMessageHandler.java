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

package org.opensaml.messaging.handler;

import java.util.UUID;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.component.AbstractIdentifiableInitializableComponent;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.messaging.context.MessageContext;
import org.slf4j.LoggerFactory;

/**
 * A base abstract implementation of {@link MessageHandler}.
 * 
 * @param <MessageType> the type of message being handled
 */
public abstract class AbstractMessageHandler<MessageType> extends AbstractIdentifiableInitializableComponent implements
        MessageHandler<MessageType> {

    /**
     * Constructor.
     * 
     */
    public AbstractMessageHandler() {
        super();
        setId(UUID.randomUUID().toString());
    }

    /** {@inheritDoc} */
    @Override public void invoke(@Nonnull final MessageContext<MessageType> messageContext)
            throws MessageHandlerException {
        Constraint.isNotNull(messageContext, "Message context cannot be null");

        // The try/catch logic is designed to suppress a checked exception raised by
        // the doInvoke step by any unchecked errors in the doPostInvoke method.
        // The original exception is logged, and can be accessed from the suppressing
        // error object using the Java 7 API.

        if (doPreInvoke(messageContext)) {
            try {
                doInvoke(messageContext);
            } catch (MessageHandlerException e) {
                try {
                    doPostInvoke(messageContext, e);
                } catch (Throwable t) {
                    LoggerFactory.getLogger(AbstractMessageHandler.class).warn(
                            getLogPrefix() + " Unchecked exception/error thrown by doPostInvoke, "
                                    + "superseding a MessageHandlerException ", e);
                    t.addSuppressed(e);
                    throw t;
                }
                throw e;
            } catch (Throwable t) {
                try {
                    doPostInvoke(messageContext);
                } catch (Throwable t2) {
                    LoggerFactory.getLogger(AbstractMessageHandler.class).warn(
                            getLogPrefix() + " Unchecked exception/error thrown by doPostInvoke, "
                                    + "superseding an unchecked exception/error ", t);
                    t2.addSuppressed(t);
                    throw t2;
                }
                throw t;
            }

            doPostInvoke(messageContext);
        }
    }

    /**
     * Called prior to execution, handlers may override this method to perform pre-processing for a request.
     * 
     * <p>
     * If false is returned, execution will not proceed.
     * </p>
     * 
     * <p>
     * If returning successfully, the last step should be to return the result of the superclass version of this method.
     * </p>
     * 
     * @param messageContext the message context on which to invoke the handler
     * @return true iff execution should proceed
     * 
     * @throws MessageHandlerException if there is a problem executing the handler pre-routine
     */
    protected boolean doPreInvoke(@Nonnull final MessageContext<MessageType> messageContext)
            throws MessageHandlerException {
        return true;
    }

    /**
     * Performs the handler logic.
     * 
     * @param messageContext the message context on which to invoke the handler
     * @throws MessageHandlerException if there is an error invoking the handler on the message context
     */
    protected abstract void doInvoke(@Nonnull final MessageContext<MessageType> messageContext)
            throws MessageHandlerException;

    /**
     * Called after execution, handlers may override this method to perform post-processing for a request.
     * 
     * <p>
     * Handlers must not "fail" during this step. This method will not be called if {@link #doPreInvoke} fails, but is
     * called if an exception is raised by {@link #doInvoke}.
     * </p>
     * 
     * @param messageContext the message context on which the handler was invoked
     */
    protected void doPostInvoke(@Nonnull final MessageContext<MessageType> messageContext) {
    }

    /**
     * Called after execution, handlers may override this method to perform post-processing for a request.
     * 
     * <p>
     * Handlers must not "fail" during this step. This method will not be called if {@link #doPreInvoke} fails, but is
     * called if an exception is raised by {@link #doInvoke}.
     * </p>
     * 
     * <p>
     * This version of the method will be called if an exception is raised during execution of the handler. The overall
     * handler result will be to raise this error, so any errors inadvertently raised by this method will be logged and
     * superseded.
     * </p>
     * 
     * <p>
     * The default implementation simply calls the error-less version of this method.
     * </p>
     * 
     * @param messageContext the message context on which the handler was invoked
     * @param e an exception raised by the {@link #doInvoke} method
     */
    protected void doPostInvoke(@Nonnull final MessageContext<MessageType> messageContext, @Nonnull final Exception e) {
        doPostInvoke(messageContext);
    }

    /**
     * Return a prefix for logging messages for this component.
     * 
     * @return a string for insertion at the beginning of any log messages
     */
    @Nonnull protected String getLogPrefix() {
        return "Message Handler " + getId() + ":";
    }

}