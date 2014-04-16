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

import java.net.URI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.joda.time.DateTime;
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
import org.opensaml.saml.common.binding.BindingException;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
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
 * <p>An associated {@link SubjectConfirmationData} will be built to spec based on a set of
 * lookup functions that optionally provide various attributes. They have appropriate defaults
 * for the simple use case of a bearer SSO assertion but need to be overridden for other cases.</p>
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

    /** Builder for SubjectConfirmation objects. */
    @Nonnull private final SAMLObjectBuilder<SubjectConfirmationData> confirmationDataBuilder;
    
    /** Flag controlling whether to overwrite existing confirmations. */
    private boolean overwriteExisting;
    
    /** Strategy used to locate the {@link Response} to operate on. */
    @Nonnull private Function<ProfileRequestContext,Response> responseLookupStrategy;
    
    /** Optional strategy to obtain value for {@link SubjectConfirmationData#getAddress()}. */
    @Nullable private Function<ProfileRequestContext,String> addressLookupStrategy;

    /** Optional strategy to obtain value for {@link SubjectConfirmationData#getInResponseTo()}. */
    @Nullable private Function<ProfileRequestContext,String> inResponseToLookupStrategy;

    /** Optional strategy to obtain value for {@link SubjectConfirmationData#getRecipient()}. */
    @Nullable private Function<ProfileRequestContext,String> recipientLookupStrategy;

    /** Optional strategy to obtain value for {@link SubjectConfirmationData#getNotOnOrAfter()}. */
    @Nullable private Function<ProfileRequestContext,Long> lifetimeLookupStrategy;
    
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
        confirmationDataBuilder = (SAMLObjectBuilder<SubjectConfirmationData>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<SubjectConfirmationData>getBuilderOrThrow(
                        SubjectConfirmationData.DEFAULT_ELEMENT_NAME);
        overwriteExisting = true;
        responseLookupStrategy =
                Functions.compose(new MessageLookup<>(Response.class), new OutboundMessageContextLookup());
        
        // Default pulls from servlet request.
        addressLookupStrategy = new Function<ProfileRequestContext,String>() {
            public String apply(ProfileRequestContext input) {
                final String address = getHttpServletRequest() != null ? getHttpServletRequest().getRemoteAddr() : null;
                log.debug("{} Setting confirmation data Address to {}", getLogPrefix(),
                        address != null ? address : "(none)");
                return address;
            }
        };
        
        // Default pulls from inbound message context and a SAMLMessageInfoContext child.
        inResponseToLookupStrategy = new Function<ProfileRequestContext,String>() {
            public String apply(ProfileRequestContext input) {
                if (response != null && response.getInResponseTo() != null) {
                    log.debug("{} Setting confirmation data InResponseTo to {}", getLogPrefix(),
                            response.getInResponseTo());
                    return response.getInResponseTo();
                }
                log.debug("{} Setting confirmation data InResponseTo to (none)", getLogPrefix());
                return null;
            }
        };
        
        // Default pulls from SAML endpoint on outbound message context.
        recipientLookupStrategy = new Function<ProfileRequestContext,String>() {
            public String apply(ProfileRequestContext input) {
                if (input.getOutboundMessageContext() != null) {
                    try {
                        final URI uri = SAMLBindingSupport.getEndpointURL(input.getOutboundMessageContext());
                        if (uri != null) {
                            final String url = uri.toString();
                            log.debug("{} Setting confirmation data Recipient to {}", getLogPrefix(), url);
                            return url;
                        }
                    } catch (BindingException e) {
                        log.debug(getLogPrefix() + " Error getting response endpoint", e);
                    }
                }
                log.debug("{} Setting confirmation data Recipient to (none)", getLogPrefix());
                return null;
            }
        };
        
        // Default is 5 minutes.
        lifetimeLookupStrategy = new Function<ProfileRequestContext,Long>() {
            public Long apply(ProfileRequestContext input) {
                log.debug("{} Setting confirmation data NotOnOrAfter to 5 minutes from now", getLogPrefix());
                return 5 * 60 * 1000L;
            }
        };
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
            @Nonnull final Function<ProfileRequestContext,Response> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        responseLookupStrategy = Constraint.isNotNull(strategy, "Response lookup strategy cannot be null");
    }

    /**
     * Set the strategy used to obtain value for {@link SubjectConfirmationData#getAddress()}.
     * 
     * @param strategy lookup strategy
     */
    public synchronized void setAddressLookupStrategy(@Nullable final Function<ProfileRequestContext,String> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        addressLookupStrategy = strategy;
    }

    /**
     * Set the strategy used to obtain value for {@link SubjectConfirmationData#getInResponseTo()}.
     * 
     * @param strategy lookup strategy
     */
    public synchronized void setInResponseToLookupStrategy(
            @Nullable final Function<ProfileRequestContext,String> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        inResponseToLookupStrategy = strategy;
    }

    /**
     * Set the strategy used to obtain value for {@link SubjectConfirmationData#getRecipient()}.
     * 
     * @param strategy lookup strategy
     */
    public synchronized void setRecipientLookupStrategy(
            @Nullable final Function<ProfileRequestContext,String> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        recipientLookupStrategy = strategy;
    }

    /**
     * Set the strategy used to obtain value for {@link SubjectConfirmationData#getNotOnOrAfter()}.
     * 
     * @param strategy lookup strategy
     */
    public synchronized void setLifetimeLookupStrategy(@Nullable final Function<ProfileRequestContext,Long> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        lifetimeLookupStrategy = strategy;
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
        
        return super.doPreExecute(profileRequestContext);
    }
    
 // Checkstyle: CyclomaticComplexity OFF
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {

        final SubjectConfirmation confirmation = confirmationBuilder.buildObject();
        confirmation.setMethod(confirmationMethod);
        
        SubjectConfirmationData confirmationData = null;
        
        final String address = addressLookupStrategy != null
                ? addressLookupStrategy.apply(profileRequestContext) : null;
        if (address != null) {
            confirmationData = confirmationData != null ? confirmationData : confirmationDataBuilder.buildObject();
            confirmationData.setAddress(address);
        }
        
        final String inResponseTo = inResponseToLookupStrategy != null
                ? inResponseToLookupStrategy.apply(profileRequestContext) : null;
        if (inResponseTo != null) {
            confirmationData = confirmationData != null ? confirmationData : confirmationDataBuilder.buildObject();
            confirmationData.setInResponseTo(inResponseTo);
        }

        final String recipient = recipientLookupStrategy != null
                ? recipientLookupStrategy.apply(profileRequestContext) : null;
        if (recipient != null) {
            confirmationData = confirmationData != null ? confirmationData : confirmationDataBuilder.buildObject();
            confirmationData.setRecipient(recipient);
        }
        
        final Long lifetime = lifetimeLookupStrategy != null
                ? lifetimeLookupStrategy.apply(profileRequestContext) : null;
        if (lifetime != null) {
            confirmationData = confirmationData != null ? confirmationData : confirmationDataBuilder.buildObject();
            confirmationData.setNotOnOrAfter(new DateTime().plus(lifetime));
        }
        
        if (confirmationData != null) {
            confirmation.setSubjectConfirmationData(confirmationData);
        }
        
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
 // Checkstyle: CyclomaticComplexity ON
    
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
        
        final SubjectConfirmationData data = confirmation.getSubjectConfirmationData();
        if (data != null) {
            final SubjectConfirmationData cloneData = confirmationDataBuilder.buildObject();
            cloneData.setAddress(data.getAddress());
            cloneData.setInResponseTo(data.getInResponseTo());
            cloneData.setRecipient(data.getRecipient());
            cloneData.setNotBefore(data.getNotBefore());
            cloneData.setNotOnOrAfter(data.getNotOnOrAfter());
            clone.setSubjectConfirmationData(cloneData);
        }
        
        return clone;
    }
    
}