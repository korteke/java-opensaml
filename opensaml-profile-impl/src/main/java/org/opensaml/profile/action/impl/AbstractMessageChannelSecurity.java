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

package org.opensaml.profile.action.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.messaging.context.BaseContext;
import org.opensaml.messaging.context.MessageChannelSecurityContext;
import org.opensaml.profile.ProfileException;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.context.ProfileRequestContext;

import com.google.common.base.Function;

/**
 * Abstract base class for profile actions which populate a {@link MessageChannelSecurityContext} on a {@link BaseContext},
 * where the latter is located using a lookup strategy.
 */
public class AbstractMessageChannelSecurity extends AbstractProfileAction {
    
    /**
     * Strategy used to look up the parent {@link BaseContext} on which the {@link MessageChannelSecurityContext} 
     * will be populated.
     */
    @Nonnull private Function<ProfileRequestContext, BaseContext> parentContextLookupStrategy;
    
    @Nullable private BaseContext parentContext;
    
    /** Constructor. */
    public AbstractMessageChannelSecurity() {
        //TODO this just returns the input PRC - need better default?
        parentContextLookupStrategy = new Function<ProfileRequestContext, BaseContext>() {
            @Nullable public BaseContext apply(@Nullable ProfileRequestContext input) {
                return input;
            }
        };
    }
    
    /**
     * Set the strategy used to look up the parent {@link BaseContext} on which the {@link MessageChannelSecurityContext}
     * will be populated.
     * 
     * @param strategy strategy used to look up the parent {@link BaseContext} on which to populate
     *          the {@link MessageChannelSecurityContext}
     */
    public void setParentContextLookupStrategy(
            @Nonnull final Function<ProfileRequestContext, BaseContext> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        parentContextLookupStrategy = Constraint.isNotNull(strategy, "Parent context lookup strategy cannot be null");
    }

    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) throws ProfileException {

        parentContext = parentContextLookupStrategy.apply(profileRequestContext);
        return parentContext != null;
    }
    
    /**
     * Get the parent context on which the {@link MessageChannelSecurityContext} will be populated.
     * 
     * @return the parent context
     */
    protected BaseContext getParentContext() {
        return parentContext;
    }

}
