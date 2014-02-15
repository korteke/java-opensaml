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

package org.opensaml.saml.saml2.profile.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.profile.ProfileException;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.navigate.MessageLookup;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Functions;

/**
 * Action that builds {@link SubjectConfirmation} and adds it to the {@link Subject} of all the assertions
 * found in a {@link Response}. The message to update is returned by a lookup strategy, by default the message
 * returned by {@link ProfileRequestContext#getOutboundMessageContext()}.
 * 
 * <p>No assertions will be created by this action, but if no {@link Subject} exists in
 * the assertions found, it will be cretaed.</p>
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_MSG_CTX}
 */
public class AddSubjectConfirmationToSubjects extends AbstractProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AddSubjectConfirmationToSubjects.class);
    
    /** Builder for Subject objects. */
    @Nonnull private final SAMLObjectBuilder<Subject> subjectBuilder;

    /** Builder for SubjectConfirmation objects. */
    @Nonnull private final SAMLObjectBuilder<SubjectConfirmation> confirmationBuilder;
    
    /** Flag controlling whether to overwrite existing confirmations. */
    private boolean overwriteExisting;
    
    /** Strategy used to locate the {@link Response} to operate on. */
    @Nonnull private Function<ProfileRequestContext, Response> responseLookupStrategy;
    
    /** Method to add. */
    @NonnullAfterInit private String confirmationMethod;
    
    /** Response to modify. */
    @Nullable private Response response;
    
    /** Constructor. */
    public AddSubjectConfirmationToSubjects() {
        subjectBuilder = (SAMLObjectBuilder<Subject>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Subject>getBuilderOrThrow(
                        Subject.DEFAULT_ELEMENT_NAME);
        confirmationBuilder = (SAMLObjectBuilder<SubjectConfirmation>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<SubjectConfirmation>getBuilderOrThrow(
                        SubjectConfirmation.DEFAULT_ELEMENT_NAME);
        
        overwriteExisting = true;
        
        responseLookupStrategy =
                Functions.compose(new MessageLookup<>(Response.class), new OutboundMessageContextLookup());
    }
    
    /**
     * Set whether to overwrite any existing {@link SubjectConfirmation} objects found.
     * 
     * @param flag  true iff the action should overwrite any existing objects
     */
    public synchronized void setOverwriteExisting(final boolean flag) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        overwriteExisting = flag;
    }
    
    /**
     * Set the strategy used to locate the {@link Response} to operate on.
     * 
     * @param strategy strategy used to locate the {@link Response} to operate on
     */
    public synchronized void setResponseLookupStrategy(
            @Nonnull final Function<ProfileRequestContext, Response> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        responseLookupStrategy = Constraint.isNotNull(strategy, "Response lookup strategy cannot be null");
    }
    
    /**
     * Set the confirmation method to use.
     * 
     * @param method   confirmation method to use
     */
    public synchronized void setMethod(@Nonnull @NotEmpty final String method) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        confirmationMethod = Constraint.isNotNull(StringSupport.trimOrNull(method),
                "Confirmation method cannot be null or empty"); 
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (confirmationMethod == null) {
            throw new ComponentInitializationException("Confirmation method cannot be null or empty");
        }
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) throws ProfileException {
        log.debug("{} Attempting to add SubjectConfirmation to assertions in outgoing Response", getLogPrefix());

        response = responseLookupStrategy.apply(profileRequestContext);
        if (response == null) {
            log.debug("{} No SAML response located in current profile request context", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        } else if (response.getAssertions().isEmpty()) {
            log.debug("{} No assertions in response message, nothing to do", getLogPrefix());
            return false;
        }
        
        return super.doPreExecute(profileRequestContext);
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) throws ProfileException {

        final SubjectConfirmation confirmation = confirmationBuilder.buildObject();
        confirmation.setMethod(confirmationMethod);
        
        int count = 0;
        
        for (final Assertion assertion : response.getAssertions()) {
            final Subject subject = getAssertionSubject(assertion);
            if (overwriteExisting) {
                subject.getSubjectConfirmations().clear();
            }
            subject.getSubjectConfirmations().add(count > 0 ? cloneConfirmation(confirmation) : confirmation);
            count ++;
        }
        
        if (count > 0) {
            log.debug("{} Added SubjectConfirmation with method {} to {} assertion(s)", getLogPrefix(),
                    confirmationMethod, count);
        }
    }
    
    /**
     * Get the subject to which the confirmation will be added.
     * 
     * @param assertion the assertion being modified
     * 
     * @return the subject to which the confirmation will be added
     */
    @Nonnull private Subject getAssertionSubject(@Nonnull final Assertion assertion) {
        if (assertion.getSubject() != null) {
            return assertion.getSubject();
        }
        
        final Subject subject = subjectBuilder.buildObject();
        assertion.setSubject(subject);
        return subject;
    }
    
    /**
     * Create an efficient field-wise copy of a {@link SubjectConfirmation}.
     * 
     * @param confirmation    the object to clone
     * 
     * @return the copy
     */
    @Nonnull private SubjectConfirmation cloneConfirmation(@Nonnull final SubjectConfirmation confirmation) {
        final SubjectConfirmation clone = confirmationBuilder.buildObject();
        clone.setMethod(confirmation.getMethod());
        return clone;
    }
    
}