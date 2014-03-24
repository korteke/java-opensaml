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

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.profile.ProfileException;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.InboundMessageContextLookup;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NullableElements;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.navigate.MessageLookup;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.profile.FormatSpecificNameIdentifierGenerator;
import org.opensaml.saml.common.profile.SAMLEventIds;
import org.opensaml.saml.common.profile.logic.MetadataNameIdentifierFormatStrategy;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.profile.SAML2NameIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

/**
 * Action that builds a {@link NameID} and adds it to the {@link Subject} of all the assertions
 * found in a {@link Response}. The message to update is returned by a lookup strategy, by default
 * the message returned by {@link ProfileRequestContext#getOutboundMessageContext()}.
 * 
 * <p>No assertions will be created by this action, but if no {@link Subject} exists in
 * the assertions found, it will be cretaed.</p>
 * 
 * <p>The source of the {@link NameID} is one of a set of candidate {@link SAML2NameIDGenerator}
 * plugins injected into the action. The plugin(s) to attempt to use are derived from the Format value,
 * which is established by a lookup strategy.</p>
 * 
 * <p>In addition, the generation process is influenced by the requested {@link NameIDPolicy}, which
 * is evaluated using a pluggable predicate.</p>
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_MSG_CTX}
 * @event {@link SAMLEventIds#INVALID_NAMEID_POLICY}
 */
public class AddNameIDToSubjects extends AbstractProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AddNameIDToSubjects.class);
    
    /** Builder for Subject objects. */
    @Nonnull private SAMLObjectBuilder<Subject> subjectBuilder;

    /** Builder for NameID objects. */
    @Nonnull private SAMLObjectBuilder<NameID> nameIdBuilder;
    
    /** Flag controlling whether to overwrite an existing NameID. */
    private boolean overwriteExisting;

    /** Strategy used to locate the {@link AuthnRequest} to operate on, if any. */
    @Nonnull private Function<ProfileRequestContext,AuthnRequest> requestLookupStrategy;
    
    /** Strategy used to locate the {@link Response} to operate on. */
    @Nonnull private Function<ProfileRequestContext,Response> responseLookupStrategy;

    /** Predicate to validate {@link NameIDPolicy}. */
    @Nonnull private Predicate<ProfileRequestContext> nameIDPolicyPredicate;
    
    /** Strategy used to determine the formats to try. */
    @Nonnull private Function<ProfileRequestContext,List<String>> formatLookupStrategy;
    
    /** Map of formats to generators. */
    @Nonnull @NonnullElements private ListMultimap<String,SAML2NameIDGenerator> nameIdGeneratorMap;
    
    /** Fallback generator, generally for legacy support. */
    @Nullable private SAML2NameIDGenerator defaultNameIdGenerator;

    /** Formats to try. */
    @Nonnull @NonnullElements private List<String> formats;
    
    /** Format required by requested {@link NameIDPolicy}. */
    @Nullable private String requiredFormat;

    /** Request to examine. */
    @Nullable private AuthnRequest request;
    
    /** Response to modify. */
    @Nullable private Response response;
    
    /** Constructor.
     *  
     * @throws ComponentInitializationException if an error occurs initializing default predicate.
     */
    public AddNameIDToSubjects() throws ComponentInitializationException {
        subjectBuilder = (SAMLObjectBuilder<Subject>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Subject>getBuilderOrThrow(
                        Subject.DEFAULT_ELEMENT_NAME);
        nameIdBuilder = (SAMLObjectBuilder<NameID>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<NameID>getBuilderOrThrow(
                        NameID.DEFAULT_ELEMENT_NAME);
        
        overwriteExisting = true;
        
        requestLookupStrategy =
                Functions.compose(new MessageLookup<>(AuthnRequest.class), new InboundMessageContextLookup());
        responseLookupStrategy =
                Functions.compose(new MessageLookup<>(Response.class), new OutboundMessageContextLookup());
        
        // Default predicate pulls SPNameQualifier from NameIDPolicy and does a direct match
        // against issuer. Handles simple cases, overridden for complex ones.
        nameIDPolicyPredicate = new DefaultNameIDPolicyPredicate();
        ((DefaultNameIDPolicyPredicate) nameIDPolicyPredicate).initialize();
        
        formatLookupStrategy = new MetadataNameIdentifierFormatStrategy();
        nameIdGeneratorMap = ArrayListMultimap.create();
        formats = Collections.emptyList();
    }
    
    /**
     * Set whether to overwrite any existing {@link NameID} objects found.
     * 
     * @param flag  true iff the action should overwrite any existing objects
     */
    public synchronized void setOverwriteExisting(final boolean flag) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        overwriteExisting = flag;
    }

    /**
     * Set the strategy used to locate the {@link AuthnRequest} to examine, if any.
     * 
     * @param strategy strategy used to locate the {@link AuthnRequest}
     */
    public synchronized void setRequestLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,AuthnRequest> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        requestLookupStrategy = Constraint.isNotNull(strategy, "AuthnRequest lookup strategy cannot be null");
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
     * Set the predicate used to evaluate the {@link NameIDPolicy}.
     * 
     * @param predicate predicate used to evaluate the {@link NameIDPolicy}
     */
    public synchronized void setNameIDPolicyPredicate(@Nonnull final Predicate<ProfileRequestContext> predicate) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        nameIDPolicyPredicate = Constraint.isNotNull(predicate, "NameIDPolicy predicate cannot be null");
    }

    /**
     * Set the strategy function to use to obtain the formats to try.
     * 
     * @param strategy  format lookup strategy
     */
    public synchronized void setFormatLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,List<String>> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        formatLookupStrategy = Constraint.isNotNull(strategy, "Format lookup strategy cannot be null");
    }
    
    /**
     * Set the format-specific name identifier generators to use.
     * 
     * <p>Only generators that support the {@link FormatSpecificNameIdentifierGenerator} interface are
     * installed, and the generators are prioritized for a given format by the order they are supplied.</p> 
     * 
     * @param generators generators to use
     */
    public synchronized void setNameIDGenerators(
            @Nonnull @NullableElements final List<SAML2NameIDGenerator> generators) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        Constraint.isNotNull(generators, "NameIdentifierGenerator list cannot be null");
        
        nameIdGeneratorMap.clear();
        for (final SAML2NameIDGenerator generator : Collections2.filter(generators, Predicates.notNull())) {
            if (generator instanceof FormatSpecificNameIdentifierGenerator) {
                nameIdGeneratorMap.put(((FormatSpecificNameIdentifierGenerator) generator).getFormat(), generator);
            } else {
                log.warn("{} Unable to install NameIdentifierGenerator of type {}, not format-specific",
                        getLogPrefix(), generator.getClass().getName());
            }
        }
    }
    
    /**
     * Set the NameID generator to try if no generator(s) are mapped to a desired format.
     * 
     * <p>This is generally used for legacy support of the V2 attribute encoder approach,
     * which is format neutral and can't be mapped explicitly.</p>
     * 
     * @param generator a fallback default generator, if any
     */
    public synchronized void setDefaultNameIDGenerator(@Nullable final SAML2NameIDGenerator generator) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        defaultNameIdGenerator = generator;
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) throws ProfileException {
        log.debug("{} Attempting to add NameID to assertions in outgoing Response", getLogPrefix());
        
        response = responseLookupStrategy.apply(profileRequestContext);
        if (response == null) {
            log.debug("{} No SAML response located in current profile request context", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        } else if (response.getAssertions().isEmpty()) {
            log.debug("{} No assertions in response message, nothing to do", getLogPrefix());
            return false;
        }
        
        if (!nameIDPolicyPredicate.apply(profileRequestContext)) {
            log.debug("{} NameIDPolicy was unacceptable", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, SAMLEventIds.INVALID_NAMEID_POLICY);
            return false;
        }
        
        request = requestLookupStrategy.apply(profileRequestContext);
        
        requiredFormat = getRequiredFormat(profileRequestContext);
        if (requiredFormat != null) {
            formats = Collections.singletonList(requiredFormat);
            log.debug("{} Request specified NameID format: {}", getLogPrefix(), requiredFormat);
        } else {
            formats = formatLookupStrategy.apply(profileRequestContext);
            if (formats == null || formats.isEmpty()) {
                log.debug("{} No candidate NameIdentifier formats, an arbitrary format will be chosen", getLogPrefix());
                formats = Lists.newArrayList(nameIdGeneratorMap.keySet());
            } else {
                log.debug("{} Candidate NameIdentifier formats: {}", getLogPrefix(), formats);
            }
        }
        
        return super.doPreExecute(profileRequestContext);
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) throws ProfileException {

        final NameID nameId = generateNameID(profileRequestContext);
        if (nameId == null) {
            if (requiredFormat != null) {
                log.warn("{} Request specified use of an unsupportable identifier format: {}", getLogPrefix(),
                        requiredFormat);
                ActionSupport.buildEvent(profileRequestContext, SAMLEventIds.INVALID_NAMEID_POLICY);
            } else {
                log.debug("{} Unable to generate a NameID, leaving empty", getLogPrefix());
            }
            return;
        }
        
        int count = 0;
        
        for (final Assertion assertion : response.getAssertions()) {
            final Subject subject = getAssertionSubject(assertion);
            final NameID existing = subject.getNameID();
            if (existing == null || overwriteExisting) {
                subject.setNameID(count > 0 ? cloneNameID(nameId) : nameId);
            }
            count ++;
        }
        
        if (count > 0) {
            log.debug("{} Added NameID to {} assertion subject(s)", getLogPrefix(), count);
        }
    }

    /**
     * Extract a format required by the inbound request, if present.
     * 
     * @param profileRequestContext current profile request context
     * 
     * @return a format dictated by the request, or null 
     */
    @Nullable private String getRequiredFormat(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        if (request != null) {
            final NameIDPolicy policy = request.getNameIDPolicy();
            if (policy != null) {
                final String format = policy.getFormat();
                if (!Strings.isNullOrEmpty(format) && !NameID.UNSPECIFIED.equals(format)
                        && !NameID.ENCRYPTED.equals(format)) {
                    return format;
                }
            }
        }
        
        return null;
    }

    /**
     * Attempt to generate a {@link NameID} using each of the candidate Formats and plugins.
     * 
     * @param profileRequestContext current profile request context
     * 
     * @return a generated {@link NameID} or null
     */
    @Nullable private NameID generateNameID(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        // See if we can generate one.
        for (final String format : formats) {
            log.debug("{} Trying to generate NameID with Format {}", getLogPrefix(), format);
            List<SAML2NameIDGenerator> generators = nameIdGeneratorMap.get(format);
            if (generators.isEmpty()) {
                if (defaultNameIdGenerator != null) {
                    log.debug("{} No generators installed for Format {}, trying default/fallback method",
                            getLogPrefix(), format);
                    generators = Collections.singletonList(defaultNameIdGenerator);
                } else {
                    continue;
                }
            }
            for (final SAML2NameIDGenerator generator : generators) {
                if (generator.apply(profileRequestContext)) {
                    try {
                        final NameID nameId = generator.generate(profileRequestContext, format);
                        if (nameId != null) {
                            log.debug("{} Successfully generated NameID with Format {}", getLogPrefix(),
                                    format);
                            return nameId;
                        }
                    } catch (ProfileException e) {
                        log.error(getLogPrefix() + " Error while generating NameID", e);
                    }
                }
            }
        }
        
        return null;
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
     * @param nameId    the object to clone
     * 
     * @return the copy
     */
    @Nonnull private NameID cloneNameID(@Nonnull final NameID nameId) {
        final NameID clone = nameIdBuilder.buildObject();
        
        clone.setFormat(nameId.getFormat());
        clone.setNameQualifier(nameId.getNameQualifier());
        clone.setSPNameQualifier(nameId.getSPNameQualifier());
        clone.setSPProvidedID(nameId.getSPProvidedID());
        clone.setValue(nameId.getValue());
        
        return clone;
    }
    
}