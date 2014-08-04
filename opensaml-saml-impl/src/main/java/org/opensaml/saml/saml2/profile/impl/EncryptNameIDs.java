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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;

import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.context.navigate.MessageLookup;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.ext.saml2delrestrict.Delegate;
import org.opensaml.saml.ext.saml2delrestrict.DelegationRestrictionType;
import org.opensaml.saml.saml2.core.ArtifactResponse;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Condition;
import org.opensaml.saml.saml2.core.EncryptedID;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.ManageNameIDRequest;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDMappingRequest;
import org.opensaml.saml.saml2.core.NameIDMappingResponse;
import org.opensaml.saml.saml2.core.NewEncryptedID;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectQuery;
import org.opensaml.saml.saml2.profile.context.EncryptionContext;
import org.opensaml.xmlsec.EncryptionParameters;
import org.opensaml.xmlsec.encryption.support.EncryptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Sets;

/**
 * Action that encrypts all {@link NameID}s in a message obtained from a lookup strategy,
 * by default the outbound message context.
 * 
 * <p>Specific formats may be excluded from encryption, by default excluding the "entity" format.</p> 
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#UNABLE_TO_ENCRYPT}
 * 
 * @post All SAML {@link NameID}s in all locations have been replaced with encrypted versions.
 * It's possible for some to be replaced but others not if an error occurs.
 */
public class EncryptNameIDs extends AbstractEncryptAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(EncryptNameIDs.class);
    
    /** Strategy used to locate the message to operate on. */
    @Nonnull private Function<ProfileRequestContext,SAMLObject> messageLookupStrategy;
    
    /** Formats to exclude from encryption. */
    @Nonnull @NonnullElements private Set<String> excludedFormats;
    
    /** The message to operate on. */
    @Nullable private SAMLObject message;
    
    /** Constructor. */
    public EncryptNameIDs() {
        messageLookupStrategy =
                Functions.compose(new MessageLookup<>(SAMLObject.class), new OutboundMessageContextLookup());
        excludedFormats = Collections.singleton(NameID.ENTITY);
    }

    /**
     * Set the strategy used to locate the {@link Response} to operate on.
     * 
     * @param strategy strategy used to locate the {@link Response} to operate on
     */
    public void setMessageLookupStrategy(@Nonnull final Function<ProfileRequestContext,SAMLObject> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        messageLookupStrategy = Constraint.isNotNull(strategy, "Message lookup strategy cannot be null");
    }
    
    /**
     * Set the {@link NameID} formats to ignore and leave unencrypted.
     * 
     * @param formats   formats to exclude
     */
    public void setExcludedFormats(@Nonnull @NonnullElements final Collection<String> formats) {
        excludedFormats = Sets.newHashSetWithExpectedSize(formats.size());
        excludedFormats.addAll(StringSupport.normalizeStringCollection(formats));
    }

    /** {@inheritDoc} */
    @Override
    @Nullable protected EncryptionParameters getApplicableParameters(@Nullable final EncryptionContext ctx) {
        if (ctx != null) {
            return ctx.getIdentifierEncryptionParameters();
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        message = messageLookupStrategy.apply(profileRequestContext);

        if (message != null && message instanceof ArtifactResponse) {
            message = ((ArtifactResponse) message).getMessage();
        }
        
        if (message == null) {
            log.debug("{} Message was not present, nothing to do", getLogPrefix());
            return false;
        }
        
        return super.doPreExecute(profileRequestContext);
    }
    
// Checkstyle: CyclomaticComplexity OFF
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        try {
            
            if (message instanceof AuthnRequest) {
                processSubject(((AuthnRequest) message).getSubject());
            } else if (message instanceof SubjectQuery) {
                processSubject(((SubjectQuery) message).getSubject());
            } else if (message instanceof Response) {
                for (final Assertion a : ((Response) message).getAssertions()) {
                    processAssertion(a);
                }
            } else if (message instanceof LogoutRequest) {
                processLogoutRequest((LogoutRequest) message);
            } else if (message instanceof ManageNameIDRequest) {
                processManageNameIDRequest((ManageNameIDRequest) message);
            } else if (message instanceof NameIDMappingRequest) {
                processNameIDMappingRequest((NameIDMappingRequest) message);
            } else if (message instanceof NameIDMappingResponse) {
                processNameIDMappingResponse((NameIDMappingResponse) message);
            } else if (message instanceof Assertion) {
                processAssertion((Assertion) message);
            } else {
                log.debug("{} Message was of unrecognized type {}, nothing to do", getLogPrefix(),
                        message.getClass().getName());
                return;
            }
        } catch (final EncryptionException e) {
            log.warn("{} Error encrypting NameID", getLogPrefix(), e);
            ActionSupport.buildEvent(profileRequestContext, EventIds.UNABLE_TO_ENCRYPT);
        }
    }
// Checkstyle: CyclomaticComplexity ON
    
    /**
     * Return true iff the NameID should be encrypted.
     * 
     * @param name  NameID to check
     * 
     * @return  true iff encryption should happen
     */
    private boolean shouldEncrypt(@Nullable final NameID name) {
        if (name != null) {
            String format = name.getFormat();
            if (format == null) {
                format = NameID.UNSPECIFIED;
            }
            if (!excludedFormats.contains(format)) {
                if (log.isDebugEnabled()) {
                    try {
                        final Element dom = XMLObjectSupport.marshall(name);
                        log.debug("{} NameID before encryption:\n{}", getLogPrefix(),
                                SerializeSupport.prettyPrintXML(dom));
                    } catch (final MarshallingException e) {
                        log.error("{} Unable to marshall NameID for logging purposes", getLogPrefix(), e);
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Encrypt any {@link NameID}s found in a subject and replace them with the result.
     * 
     * @param subject   subject to operate on
     * 
     * @throws EncryptionException if an error occurs
     */
    private void processSubject(@Nullable final Subject subject) throws EncryptionException {
        
        if (subject != null) {
            if (shouldEncrypt(subject.getNameID())) {
                log.debug("{} Encrypt NameID in Subject", getLogPrefix());
                final EncryptedID encrypted = getEncrypter().encrypt(subject.getNameID());
                subject.setEncryptedID(encrypted);
                subject.setNameID(null);
            }
            
            for (final SubjectConfirmation sc : subject.getSubjectConfirmations()) {
                if (shouldEncrypt(sc.getNameID())) {
                    log.debug("{} Encrypt NameID in SubjectConfirmation", getLogPrefix());
                    final EncryptedID encrypted = getEncrypter().encrypt(sc.getNameID());
                    sc.setEncryptedID(encrypted);
                    sc.setNameID(null);
                }
            }
        }
    }
    
    /**
     * Encrypt a {@link NameID} found in a LogoutRequest and replace it with the result.
     * 
     * @param request   request to operate on
     * 
     * @throws EncryptionException if an error occurs
     */
    private void processLogoutRequest(@Nonnull final LogoutRequest request) throws EncryptionException {
        
        if (shouldEncrypt(request.getNameID())) {
            log.debug("{} Encrypting NameID in LogoutRequest", getLogPrefix());
            final EncryptedID encrypted = getEncrypter().encrypt(request.getNameID());
            request.setEncryptedID(encrypted);
            request.setNameID(null);
        }
    }
    
    /**
     * Encrypt a {@link NameID} found in a ManageNameIDRequest and replace it with the result.
     * 
     * @param request   request to operate on
     * 
     * @throws EncryptionException if an error occurs
     */
    private void processManageNameIDRequest(@Nonnull final ManageNameIDRequest request) throws EncryptionException {
        
        if (shouldEncrypt(request.getNameID())) {
            log.debug("{} Encrypting NameID in ManageNameIDRequest", getLogPrefix());
            final EncryptedID encrypted = getEncrypter().encrypt(request.getNameID());
            request.setEncryptedID(encrypted);
            request.setNameID(null);
        }
        
        if (request.getNewID() != null) {
            log.debug("{} Encrypting NewID in ManageNameIDRequest", getLogPrefix());
            final NewEncryptedID encrypted = getEncrypter().encrypt(request.getNewID());
            request.setNewEncryptedID(encrypted);
            request.setNewID(null);
        }
    }

    /**
     * Encrypt a {@link NameID} found in a NameIDMappingRequest and replace it with the result.
     * 
     * @param request   request to operate on
     * 
     * @throws EncryptionException if an error occurs
     */
    private void processNameIDMappingRequest(@Nonnull final NameIDMappingRequest request) throws EncryptionException {
        
        if (shouldEncrypt(request.getNameID())) {
            log.debug("{} Encrypting NameID in NameIDMappingRequest", getLogPrefix());
            final EncryptedID encrypted = getEncrypter().encrypt(request.getNameID());
            request.setEncryptedID(encrypted);
            request.setNameID(null);
        }
    }

    /**
     * Encrypt a {@link NameID} found in a NameIDMappingResponse and replace it with the result.
     * 
     * @param response   response to operate on
     * 
     * @throws EncryptionException if an error occurs
     */
    private void processNameIDMappingResponse(@Nonnull final NameIDMappingResponse response)
            throws EncryptionException {
        
        if (shouldEncrypt(response.getNameID())) {
            log.debug("{} Encrypting NameID in NameIDMappingResponse", getLogPrefix());
            final EncryptedID encrypted = getEncrypter().encrypt(response.getNameID());
            response.setEncryptedID(encrypted);
            response.setNameID(null);
        }
    }

    /**
     * Decrypt any {@link EncryptedID} found in an assertion and replace it with the result.
     * 
     * @param assertion   assertion to operate on
     * 
     * @throws EncryptionException if an error occurs
     */
    private void processAssertion(@Nonnull final Assertion assertion) throws EncryptionException {

        processSubject(assertion.getSubject());            
        
        if (assertion.getConditions() != null) {
            for (final Condition c : assertion.getConditions().getConditions()) {
                if (!(c instanceof DelegationRestrictionType)) {
                    continue;
                }
                for (final Delegate d : ((DelegationRestrictionType) c).getDelegates()) {
                    if (shouldEncrypt(d.getNameID())) {
                        log.debug("{} Encrypting NameID in Delegate", getLogPrefix());
                        final EncryptedID encrypted = getEncrypter().encrypt(d.getNameID());
                        d.setEncryptedID(encrypted);
                        d.setNameID(null);
                    }
                }
            }
        }
    }
    
}