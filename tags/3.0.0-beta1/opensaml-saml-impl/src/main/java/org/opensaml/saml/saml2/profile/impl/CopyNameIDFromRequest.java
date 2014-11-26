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

import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.InboundMessageContextLookup;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;

import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.messaging.context.navigate.MessageLookup;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.messaging.context.SAMLSubjectNameIdentifierContext;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Functions;

/**
 * Action that builds a {@link NameID} and adds it to the {@link Subject} of all the statements
 * in all the assertions found in a {@link Response}. The message to update is returned by a lookup
 * strategy, by default the message returned by {@link ProfileRequestContext#getOutboundMessageContext()}.
 * 
 * <p>No assertions or statements will be created by this action, but if no {@link Subject} exists in
 * the statements found, it will be created.</p>
 * 
 * <p>The source of the {@link NameID} is a {@link SAMLSubjectNameIdentifierContext} returned by a lookup
 * strategy.</p>
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_MSG_CTX}
 */
public class CopyNameIDFromRequest extends AbstractProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(CopyNameIDFromRequest.class);
    
    /** Builder for Subject objects. */
    @Nonnull private final SAMLObjectBuilder<Subject> subjectBuilder;

    /** Builder for NameID objects. */
    @Nonnull private final SAMLObjectBuilder<NameID> nameIdBuilder;

    /** Flag controlling whether to overwrite an existing NameID. */
    private boolean overwriteExisting;
        
    /** Strategy used to locate the name identifier context to copy from. */
    @Nonnull
    private Function<ProfileRequestContext,SAMLSubjectNameIdentifierContext> nameIdentifierContextLookupStrategy;
    
    /** Strategy used to locate the {@link Response} to operate on. */
    @Nonnull private Function<ProfileRequestContext,Response> responseLookupStrategy;

    /** NameID to copy. */
    @Nullable private NameID nameId; 
    
    /** Response to modify. */
    @Nullable private Response response;
    
    /** Constructor. */
    public CopyNameIDFromRequest() {
        subjectBuilder = (SAMLObjectBuilder<Subject>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Subject>getBuilderOrThrow(
                        Subject.DEFAULT_ELEMENT_NAME);
        nameIdBuilder = (SAMLObjectBuilder<NameID>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<NameID>getBuilderOrThrow(
                        NameID.DEFAULT_ELEMENT_NAME);
        
        overwriteExisting = true;
        
        nameIdentifierContextLookupStrategy = Functions.compose(
                new ChildContextLookup<>(SAMLSubjectNameIdentifierContext.class, true),
                new InboundMessageContextLookup());
        
        responseLookupStrategy =
                Functions.compose(new MessageLookup<>(Response.class), new OutboundMessageContextLookup());
    }
    
    /**
     * Set whether to overwrite any existing {@link NameID} objects found.
     * 
     * @param flag  true iff the action should overwrite any existing objects
     */
    public void setOverwriteExisting(final boolean flag) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        overwriteExisting = flag;
    }
    
    /**
     * Set the strategy used to locate the {@link SAMLSubjectNameIdentifierContext} to copy from.
     * 
     * @param strategy lookup strategy
     */
    public void setNameIDContextLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,SAMLSubjectNameIdentifierContext> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        nameIdentifierContextLookupStrategy = Constraint.isNotNull(strategy,
                "SAMLSubjectNameIdentifierContext lookup strategy cannot be null");
    }
    
    /**
     * Set the strategy used to locate the {@link Response} to operate on.
     * 
     * @param strategy lookup strategy
     */
    public void setResponseLookupStrategy(@Nonnull final Function<ProfileRequestContext,Response> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        responseLookupStrategy = Constraint.isNotNull(strategy, "Response lookup strategy cannot be null");
    }

    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        log.debug("{} Attempting to add NameID to statements in outgoing Response", getLogPrefix());

        response = responseLookupStrategy.apply(profileRequestContext);
        if (response == null) {
            log.debug("{} No SAML response located", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        } else if (response.getAssertions().isEmpty()) {
            log.debug("{} No assertions in response message, nothing to do", getLogPrefix());
            return false;
        }
        
        final SAMLSubjectNameIdentifierContext idCtx = nameIdentifierContextLookupStrategy.apply(profileRequestContext);
        if (idCtx == null) {
            log.debug("{} No SAMLSubjectNameIdentifierContext located", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        }
        
        nameId = idCtx.getSAML2SubjectNameID();
        if (nameId == null) {
            log.debug("{} No SAMLSubjectNameIdentifierContext located", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        }
                
        return super.doPreExecute(profileRequestContext);
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {

        int count = 0;
        
        for (final Assertion assertion : response.getAssertions()) {
            final Subject subject = getAssertionSubject(assertion);
            final NameID existing = subject.getNameID();
            if (existing == null || overwriteExisting) {
                subject.setNameID(cloneNameID());
                count ++;
            }
        }
        
        if (count > 0) {
            log.debug("{} Added NameID to {} assertion(s)", getLogPrefix(), count);
        }
    }
    
    /**
     * Get the subject to which the name identifier will be added.
     * 
     * @param assertion the assertion being modified
     * 
     * @return the assertion to which the name identifier will be added
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
     * Create an efficient field-wise copy of a {@link NameID}.
     * 
     * @return the copy
     */
    @Nonnull private NameID cloneNameID() {
        final NameID clone = nameIdBuilder.buildObject();
        
        clone.setFormat(nameId.getFormat());
        clone.setNameQualifier(nameId.getNameQualifier());
        clone.setSPNameQualifier(nameId.getSPNameQualifier());
        clone.setSPProvidedID(nameId.getSPProvidedID());
        clone.setValue(nameId.getValue());
        
        return clone;
    }
    
}