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

import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.profile.ProfileException;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * Base class for conditional profile actions.
 * 
 * <p>A condition does not represent a situation in which an error should be raised, but that normal
 * processing should continue and the action simply doesn't apply, so a false condition does not
 * raise a non-proceed event.</p>
 * 
 * @param <InboundMessageType> type of in-bound message
 * @param <OutboundMessageType> type of out-bound message
 */
public abstract class AbstractConditionalProfileAction<InboundMessageType, OutboundMessageType> extends
        AbstractProfileAction<InboundMessageType, OutboundMessageType> {
    
    /** Condition dictating whether to run or not. */
    @Nonnull private Predicate<ProfileRequestContext> activationCondition;
    
    /** Constructor. */
    public AbstractConditionalProfileAction() {
        activationCondition = Predicates.alwaysTrue();
    }

    /**
     * Set activation condition indicating whether action should execute.
     * 
     * @param condition predicate to apply
     */
    public void setActivationCondition(@Nonnull final Predicate<ProfileRequestContext> condition) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        activationCondition = Constraint.isNotNull(condition, "Predicate cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(
            @Nonnull final ProfileRequestContext<InboundMessageType, OutboundMessageType> profileRequestContext)
            throws ProfileException {
        if (activationCondition.apply(profileRequestContext)) {
            return super.doPreExecute(profileRequestContext);
        } else {
            LoggerFactory.getLogger(AbstractConditionalProfileAction.class).debug(
                    "{} Activation condition for action returned false", getLogPrefix());
            return false;
        }
    }

}