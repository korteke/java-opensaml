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

package org.opensaml.profile.action;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.shibboleth.utilities.java.support.annotation.Prototype;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.component.AbstractInitializableComponent;
import net.shibboleth.utilities.java.support.component.ComponentSupport;

import org.opensaml.profile.context.EventContext;
import org.opensaml.profile.context.PreviousEventContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.LoggerFactory;

//TODO perf metrics

/**
 * Base class for profile actions.
 * 
 * This base class is annotated with {@link Prototype} to indicate that it is stateful.
 * 
 * @param <InboundMessageType> type of in-bound message
 * @param <OutboundMessageType> type of out-bound message
 */
@Prototype
public abstract class AbstractProfileAction<InboundMessageType, OutboundMessageType>
        extends AbstractInitializableComponent implements ProfileAction<InboundMessageType, OutboundMessageType> {

    /** Current HTTP request, if available. */
    @Nullable private HttpServletRequest httpServletRequest;

    /** Current HTTP response, if available. */
    @Nullable private HttpServletResponse httpServletResponse;

    /**
     * Get the current HTTP request if available.
     * 
     * @return current HTTP request
     */
    @Nullable public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    /**
     * Set the current HTTP request.
     * 
     * @param request current HTTP request
     */
    public void setHttpServletRequest(@Nullable final HttpServletRequest request) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        httpServletRequest = request;
    }

    /**
     * Get the current HTTP response.
     * 
     * @return current HTTP response
     */
    @Nullable public HttpServletResponse getHttpServletResponse() {
        return httpServletResponse;
    }

    /**
     * Set the current HTTP response.
     * 
     * @param response current HTTP response
     */
    public void setHttpServletResponse(@Nullable final HttpServletResponse response) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        httpServletResponse = response;
    }

    /** {@inheritDoc} */
    @Override public void execute(
            @Nonnull final ProfileRequestContext<InboundMessageType, OutboundMessageType> profileRequestContext) {

        // Clear any existing EventContext that might be hanging around, and if it exists,
        // copy the Event to a PreviousEventContext. Don't clear any existing PreviousEventContext
        // because it may be from an earlier error of interest to other actions.
        EventContext previousEvent = profileRequestContext.getSubcontext(EventContext.class, false);
        if (previousEvent != null) {
            profileRequestContext.getSubcontext(PreviousEventContext.class, true).setEvent(previousEvent.getEvent());
            profileRequestContext.removeSubcontext(EventContext.class);
        }

        // The try/catch logic is designed to suppress a checked exception raised by
        // the doInvoke step by any unchecked errors in the doPostInvoke method.
        // The original exception is logged, and can be accessed from the suppressing
        // error object using the Java 7 API.

        if (doPreExecute(profileRequestContext)) {
            try {
                doExecute(profileRequestContext);
            } catch (final Throwable t) {
                try {
                    if (t instanceof Exception) {
                        doPostExecute(profileRequestContext, (Exception) t);
                    } else {
                        doPostExecute(profileRequestContext);
                    }
                } catch (Throwable t2) {
                    LoggerFactory.getLogger(AbstractProfileAction.class).warn(
                            getLogPrefix() + " Unchecked exception/error thrown by doPostInvoke, "
                                    + "superseding earlier exception/error ", t);
                    t2.addSuppressed(t);
                    throw t2;
                }
                throw t;
            }

            doPostExecute(profileRequestContext);
        }
    }

    /**
     * Called prior to execution, actions may override this method to perform pre-processing for a request.
     * 
     * <p>
     * If false is returned, execution will not proceed, and the action should attach an
     * {@link org.opensaml.profile.context.EventContext} to the context tree to signal how to continue with overall
     * workflow processing.
     * </p>
     * 
     * <p>
     * If returning successfully, the last step should be to return the result of the superclass version of this method.
     * </p>
     * 
     * @param profileRequestContext the current IdP profile request context
     * @return true iff execution should proceed
     */
    protected boolean doPreExecute(
            @Nonnull final ProfileRequestContext<InboundMessageType, OutboundMessageType> profileRequestContext) {
        return true;
    }

    /**
     * Performs this action. Actions must override this method to perform their work.
     * 
     * @param profileRequestContext the current IdP profile request context
     */
    protected void doExecute(
            @Nonnull final ProfileRequestContext<InboundMessageType, OutboundMessageType> profileRequestContext) {
        
    }

    /**
     * Called after execution, actions may override this method to perform post-processing for a request.
     * 
     * <p>
     * Actions must not "fail" during this step and will not have the opportunity to signal events at this stage. This
     * method will not be called if {@link #doPreExecute} fails, but is called if an exception is raised by
     * {@link #doExecute}.
     * </p>
     * 
     * @param profileRequestContext the current IdP profile request context
     */
    protected void doPostExecute(
            @Nonnull final ProfileRequestContext<InboundMessageType, OutboundMessageType> profileRequestContext) {
    }

    /**
     * Called after execution, actions may override this method to perform post-processing for a request.
     * 
     * <p>
     * Actions must not "fail" during this step and will not have the opportunity to signal events at this stage. This
     * method will not be called if {@link #doPreExecute} fails, but is called if an exception is raised by
     * {@link #doExecute}.
     * </p>
     * 
     * <p>
     * This version of the method will be called if an exception is raised during execution of the action. The overall
     * action result will be to raise this error, so any errors inadvertently raised by this method will be logged and
     * superseded.
     * </p>
     * 
     * <p>
     * The default implementation simply calls the error-less version of this method.
     * </p>
     * 
     * @param profileRequestContext the current IdP profile request context
     * @param e an exception raised by the {@link #doExecute} method
     */
    protected void doPostExecute(
            @Nonnull final ProfileRequestContext<InboundMessageType, OutboundMessageType> profileRequestContext,
            @Nonnull final Exception e) {
        doPostExecute(profileRequestContext);
    }

    /**
     * Return a prefix for logging messages for this component.
     * 
     * @return a string for insertion at the beginning of any log messages
     */
    @Nonnull @NotEmpty protected String getLogPrefix() {
        return "Profile Action:";
    }

}