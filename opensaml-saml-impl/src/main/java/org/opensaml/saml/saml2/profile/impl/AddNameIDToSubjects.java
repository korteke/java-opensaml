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
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.profile.ProfileException;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.annotation.constraint.NullableElements;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.navigate.MessageLookup;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.profile.logic.MetadataNameIdentifierFormatStrategy;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.profile.SAML2NameIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Functions;
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
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_MSG_CTX}
 */
public class AddNameIDToSubjects extends AbstractProfileAction<Object, Response> {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AddNameIDToSubjects.class);
    
    /** Builder for Subject objects. */
    @Nonnull private SAMLObjectBuilder<Subject> subjectBuilder;

    /** Builder for NameID objects. */
    @Nonnull private SAMLObjectBuilder<NameID> nameIdBuilder;
    
    /** Flag controlling whether to overwrite an existing NameID. */
    private boolean overwriteExisting;
    
    /** Strategy used to locate the {@link Response} to operate on. */
    @Nonnull private Function<ProfileRequestContext<Object,Response>, Response> responseLookupStrategy;

    /** Strategy used to determine the formats to try. */
    @Nonnull private Function<ProfileRequestContext, List<String>> formatLookupStrategy;
    
    /** Map of formats to generators. */
    @Nonnull @NonnullElements private Map<String, List<SAML2NameIDGenerator>> nameIdGeneratorMap;

    /** Formats to try. */
    @Nonnull @NonnullElements private List<String> formats;
    
    /** Response to modify. */
    @Nullable private Response response;
    
    /** Constructor. */
    public AddNameIDToSubjects() {
        subjectBuilder = (SAMLObjectBuilder<Subject>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Subject>getBuilderOrThrow(
                        Subject.DEFAULT_ELEMENT_NAME);
        nameIdBuilder = (SAMLObjectBuilder<NameID>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<NameID>getBuilderOrThrow(
                        NameID.DEFAULT_ELEMENT_NAME);
        
        overwriteExisting = true;
        
        responseLookupStrategy =
                Functions.compose(new MessageLookup<Response>(), new OutboundMessageContextLookup<Response>());
        formatLookupStrategy = new MetadataNameIdentifierFormatStrategy();
        nameIdGeneratorMap = Collections.emptyMap();
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
     * Set the strategy used to locate the {@link Response} to operate on.
     * 
     * @param strategy strategy used to locate the {@link Response} to operate on
     */
    public synchronized void setResponseLookupStrategy(
            @Nonnull final Function<ProfileRequestContext<Object,Response>, Response> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        responseLookupStrategy = Constraint.isNotNull(strategy, "Response lookup strategy cannot be null");
    }

    /**
     * Set the strategy function to use to obtain the formats to try.
     * 
     * @param strategy  format lookup strategy
     */
    public synchronized void setFormatLookupStrategy(
            @Nonnull final Function<ProfileRequestContext, List<String>> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        formatLookupStrategy = Constraint.isNotNull(strategy, "Format lookup strategy cannot be null");
    }
    
    /**
     * Set the map of formats to name identifier generation plugins to use.
     * 
     * <p>This map is not copied, but assigned directly to avoid extra copying, and is not written to.</p>
     * 
     * @param generators map of formats to plugins to use
     */
    public synchronized void setNameIDGenerators(
            @Nonnull @NullableElements Map<String, List<SAML2NameIDGenerator>> generators) {
        nameIdGeneratorMap = Constraint.isNotNull(generators, "NameIDGenerator map cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext<Object, Response> profileRequestContext)
            throws ProfileException {
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
        
        formats = formatLookupStrategy.apply(profileRequestContext);
        if (formats == null || formats.isEmpty()) {
            log.debug("{} No candidate NameIdentifier formats, an arbitrary format will be chosen", getLogPrefix());
            formats = Lists.newArrayList(nameIdGeneratorMap.keySet());
        } else {
            log.debug("{} Candidate NameIdentifier formats: {}", getLogPrefix(), formats);
        }
        
        log.debug("{} Candidate NameID formats: {}", getLogPrefix(), formats);
        
        return super.doPreExecute(profileRequestContext);
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext<Object, Response> profileRequestContext)
            throws ProfileException {

        final NameID nameId = generateNameID(profileRequestContext);
        if (nameId == null) {
            log.debug("{} Unable to generate a NameID, leaving empty", getLogPrefix());
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
            final List<SAML2NameIDGenerator> generators = nameIdGeneratorMap.get(format);
            if (generators == null) {
                continue;
            }
            for (final SAML2NameIDGenerator generator : generators) {
                if (generator != null && generator.apply(profileRequestContext)) {
                    try {
                        final NameID nameId = generator.generate(profileRequestContext, null);
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
    private Subject getAssertionSubject(@Nonnull final Assertion assertion) {
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