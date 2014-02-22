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

import org.opensaml.profile.ProfileException;
import org.opensaml.profile.context.ProfileRequestContext;

import net.shibboleth.utilities.java.support.component.IdentifiedComponent;
import net.shibboleth.utilities.java.support.component.InitializableComponent;


/**
 * Interface for actions that operate on a {@link ProfileRequestContext}.
 * 
 * <p>Actions are expected to interact with the environment, access data,
 * and produce results using the context tree provided at execution time.
 * They signal state transitions by attaching an {@link org.opensaml.profile.context.EventContext}
 * to the tree.</p>
 * 
 * <p>Actions may be stateful or stateless, and are therefore not inherently thread-safe.</p>
 * 
 * @param <InboundMessageType> type of in-bound message
 * @param <OutboundMessageType> type of out-bound message
 */
public interface ProfileAction<InboundMessageType, OutboundMessageType>
    extends IdentifiedComponent, InitializableComponent {

    /**
     * Performs this action.
     * 
     * @param profileRequestContext the current IdP profile request context
     * 
     * @throws ProfileException thrown if there is a problem executing the profile action
     */
    public void execute(
            @Nonnull final ProfileRequestContext<InboundMessageType, OutboundMessageType> profileRequestContext)
            throws ProfileException;
}