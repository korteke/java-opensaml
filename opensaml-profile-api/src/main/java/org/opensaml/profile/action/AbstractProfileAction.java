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
import javax.annotation.concurrent.ThreadSafe;

import org.opensaml.profile.ProfileException;
import org.opensaml.profile.context.ProfileRequestContext;

import net.shibboleth.utilities.java.support.component.AbstractIdentifiableInitializableComponent;
import net.shibboleth.utilities.java.support.component.ComponentValidationException;
import net.shibboleth.utilities.java.support.component.ValidatableComponent;


//TODO perf metrics

/**
 * Base class for profile actions.
 * 
 * @param <InboundMessageType> type of in-bound message
 * @param <OutboundMessageType> type of out-bound message
 */
@ThreadSafe
public abstract class AbstractProfileAction<InboundMessageType, OutboundMessageType> extends
        AbstractIdentifiableInitializableComponent implements ValidatableComponent,
        ProfileAction<InboundMessageType, OutboundMessageType> {

    /**
     * Constructor.
     * 
     * Initializes the ID of this action to the class name.
     */
    public AbstractProfileAction() {
        super();

        setId(getClass().getName());
    }

    /** {@inheritDoc} */
    public synchronized void setId(String componentId) {
        super.setId(componentId);
    }
    
    /** {@inheritDoc} */
    public void validate() throws ComponentValidationException {
        
    }

    /** {@inheritDoc} */
    public void execute(
            @Nonnull final ProfileRequestContext<InboundMessageType, OutboundMessageType> profileRequestContext)
            throws ProfileException {
        doExecute(profileRequestContext);
    }
    
    /**
     * Performs this action. Actions must override this method to perform their work.
     * 
     * @param profileRequestContext the current IdP profile request context
     * 
     * @throws ProfileException thrown if there is a problem executing the profile action
     */
    public void doExecute(
            @Nonnull final ProfileRequestContext<InboundMessageType, OutboundMessageType> profileRequestContext)
            throws ProfileException {
        throw new ProfileException("This operation is not implemented.");
    }

}