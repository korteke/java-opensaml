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

package org.opensaml.saml.common.profile.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.messaging.context.navigate.MessageLookup;
import org.opensaml.profile.action.AbstractConditionalProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml1.profile.SAML1ActionSupport;
import org.opensaml.saml.saml2.profile.SAML2ActionSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Functions;

/**
 * Action that adds the <code>NotBefore</code> attribute to every {@link Assertion} in a SAML 1/2
 * response message. If the containing Conditions is not present, it will be created.
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_MSG_CTX}
 */
public class AddNotBeforeConditionToAssertions extends AbstractConditionalProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AddNotBeforeConditionToAssertions.class);

    /** Strategy used to locate the response to operate on. */
    @Nonnull private Function<ProfileRequestContext,SAMLObject> responseLookupStrategy;
    
    /** Response to modify. */
    @Nullable private SAMLObject response;
    
    /** Constructor. */
    public AddNotBeforeConditionToAssertions() {
        responseLookupStrategy =
                Functions.compose(new MessageLookup<>(SAMLObject.class), new OutboundMessageContextLookup());
    }
    
    /**
     * Set the strategy used to locate the Response to operate on.
     * 
     * @param strategy lookup strategy
     */
    public synchronized void setResponseLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,SAMLObject> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        responseLookupStrategy = Constraint.isNotNull(strategy, "Response lookup strategy cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        log.debug("{} Attempting to add NotBefore condition to every Assertion in outgoing Response", getLogPrefix());

        response = responseLookupStrategy.apply(profileRequestContext);
        if (response == null) {
            log.debug("{} No SAML Response located in current profile request context", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        }
        
        if (response instanceof org.opensaml.saml.saml1.core.Response) {
            if (((org.opensaml.saml.saml1.core.Response) response).getAssertions().isEmpty()) {
                log.debug("{} No assertions available, nothing to do", getLogPrefix());
                return false;
            }
        } else if (response instanceof org.opensaml.saml.saml2.core.Response) {
            if (((org.opensaml.saml.saml2.core.Response) response).getAssertions().isEmpty()) {
                log.debug("{} No assertions available, nothing to do", getLogPrefix());
                return false;
            }
        } else {
            log.debug("{} Message returned by lookup strategy was not a SAML Response", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        }
        
        return super.doPreExecute(profileRequestContext);
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {

        if (response instanceof org.opensaml.saml.saml1.core.Response) {
            for (final org.opensaml.saml.saml1.core.Assertion assertion :
                    ((org.opensaml.saml.saml1.core.Response) response).getAssertions()) {
                log.debug("{} Added NotBefore condition to Assertion {}", getLogPrefix(), assertion.getID());
                SAML1ActionSupport.addConditionsToAssertion(this, assertion).setNotBefore(
                        ((org.opensaml.saml.saml1.core.Response) response).getIssueInstant());
            }
        } else if (response instanceof org.opensaml.saml.saml2.core.Response) {
            for (final org.opensaml.saml.saml2.core.Assertion assertion :
                    ((org.opensaml.saml.saml2.core.Response) response).getAssertions()) {
                log.debug("{} Added NotBefore condition to Assertion {}", getLogPrefix(), assertion.getID());
                SAML2ActionSupport.addConditionsToAssertion(this, assertion).setNotBefore(
                        ((org.opensaml.saml.saml2.core.Response) response).getIssueInstant());
            }
        }
    }
    
}