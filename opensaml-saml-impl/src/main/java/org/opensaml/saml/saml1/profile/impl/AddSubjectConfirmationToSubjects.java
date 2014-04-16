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

package org.opensaml.saml.saml1.profile.impl;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.navigate.MessageLookup;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.ConfirmationMethod;
import org.opensaml.saml.saml1.core.Response;
import org.opensaml.saml.saml1.core.Statement;
import org.opensaml.saml.saml1.core.Subject;
import org.opensaml.saml.saml1.core.SubjectConfirmation;
import org.opensaml.saml.saml1.core.SubjectStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Objects;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 * Action that builds {@link SubjectConfirmation} and adds it to the {@link Subject} of all the statements
 * in all the assertions found in a {@link Response}. The message to update is returned by a lookup
 * strategy, by default the message returned by {@link ProfileRequestContext#getOutboundMessageContext()}.
 * 
 * <p>No assertions or statements will be created by this action, but if no {@link Subject} exists in
 * the statements found, it will be created.</p>
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

    /** Builder for ConfirmationMethod objects. */
    @Nonnull private final SAMLObjectBuilder<ConfirmationMethod> confirmationMethodBuilder;
    
    /** Flag controlling whether to overwrite an existing confirmation. */
    private boolean overwriteExisting;
    
    /** Strategy used to locate the {@link Response} to operate on. */
    @Nonnull private Function<ProfileRequestContext, Response> responseLookupStrategy;
    
    /** Methods to add. */
    @Nonnull @NonnullElements private Collection<String> confirmationMethods;
    
    /** Response to modify. */
    @Nullable private Response response;
    
    /** Flag indicating whether the outbound message is being issued via the Artifact profile. */
    private boolean artifactProfile;
    
    /** Constructor. */
    public AddSubjectConfirmationToSubjects() {
        subjectBuilder = (SAMLObjectBuilder<Subject>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Subject>getBuilderOrThrow(
                        Subject.DEFAULT_ELEMENT_NAME);
        confirmationBuilder = (SAMLObjectBuilder<SubjectConfirmation>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<SubjectConfirmation>getBuilderOrThrow(
                        SubjectConfirmation.DEFAULT_ELEMENT_NAME);
        confirmationMethodBuilder = (SAMLObjectBuilder<ConfirmationMethod>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<ConfirmationMethod>getBuilderOrThrow(
                        ConfirmationMethod.DEFAULT_ELEMENT_NAME);
        
        overwriteExisting = true;
        
        responseLookupStrategy =
                Functions.compose(new MessageLookup<>(Response.class), new OutboundMessageContextLookup());
        confirmationMethods = Collections.emptyList();
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
     * Set the confirmation methods to use.
     * 
     * @param methods   confirmation methods to use
     */
    public synchronized void setMethods(@Nonnull @NonnullElements final Collection<String> methods) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        Constraint.isNotEmpty(methods, "Confirmation method collection cannot be null or empty");
        
        confirmationMethods = Lists.newArrayList(Collections2.filter(methods, Predicates.notNull()));
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (confirmationMethods.isEmpty()) {
            throw new ComponentInitializationException("Confirmation method list cannot be empty");
        }
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
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
        
        final SAMLBindingContext bindingCtx = profileRequestContext.getOutboundMessageContext().getSubcontext(
                SAMLBindingContext.class, false);
        artifactProfile = bindingCtx != null
                && Objects.equal(bindingCtx.getBindingUri(), SAMLConstants.SAML1_ARTIFACT_BINDING_URI);
        
        return super.doPreExecute(profileRequestContext);
    }
    
// Checkstyle: CyclomaticComplexity OFF    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {

        final SubjectConfirmation confirmation = confirmationBuilder.buildObject();
        for (String method : confirmationMethods) {
            if (artifactProfile && ConfirmationMethod.METHOD_BEARER.equals(method)) {
// Checkstyle: ModifiedControlVariable OFF
                method = ConfirmationMethod.METHOD_ARTIFACT;
// Checkstyle: ModifiedControlVariable ON
            }
            final ConfirmationMethod newMethod = confirmationMethodBuilder.buildObject();
            newMethod.setConfirmationMethod(method);
            confirmation.getConfirmationMethods().add(newMethod);
        }
        
        int count = 0;
        
        for (final Assertion assertion : response.getAssertions()) {
            for (final Statement statement : assertion.getStatements()) {
                if (statement instanceof SubjectStatement) {
                    final Subject subject = getStatementSubject((SubjectStatement) statement);
                    final SubjectConfirmation existing = subject.getSubjectConfirmation();
                    if (existing == null || overwriteExisting) {
                        subject.setSubjectConfirmation(count > 0 ? cloneConfirmation(confirmation) : confirmation);
                    }
                    count ++;
                }
            }
        }
        
        if (count > 0) {
            log.debug("{} Added SubjectConfirmation with methods {} to {} statement subject(s)", getLogPrefix(),
                    confirmationMethods, count);
        }
    }
// Checkstyle: CyclomaticComplexity ON
    
    /**
     * Get the subject to which the confirmation will be added.
     * 
     * @param statement the statement being modified
     * 
     * @return the subject to which the confirmation will be added
     */
    @Nonnull private Subject getStatementSubject(@Nonnull final SubjectStatement statement) {
        if (statement.getSubject() != null) {
            return statement.getSubject();
        }
        
        final Subject subject = subjectBuilder.buildObject();
        statement.setSubject(subject);
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
        
        for (final ConfirmationMethod method : confirmation.getConfirmationMethods()) {
            final ConfirmationMethod newMethod = confirmationMethodBuilder.buildObject();
            newMethod.setConfirmationMethod(method.getConfirmationMethod());
            clone.getConfirmationMethods().add(newMethod);
        }
        
        return clone;
    }
    
}