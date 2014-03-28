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

import net.shibboleth.utilities.java.support.collection.Pair;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.messaging.context.navigate.MessageLookup;
import org.opensaml.profile.ProfileException;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.InboundMessageContextLookup;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.profile.SAMLEventIds;
import org.opensaml.saml.ext.saml2delrestrict.Delegate;
import org.opensaml.saml.ext.saml2delrestrict.DelegationRestrictionType;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Condition;
import org.opensaml.saml.saml2.core.EncryptedElementType;
import org.opensaml.saml.saml2.core.EncryptedID;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.ManageNameIDRequest;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDMappingRequest;
import org.opensaml.saml.saml2.core.NameIDMappingResponse;
import org.opensaml.saml.saml2.core.NewEncryptedID;
import org.opensaml.saml.saml2.core.NewID;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectQuery;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.xmlsec.DecryptionParameters;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.opensaml.xmlsec.encryption.support.DecryptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * Action to decrypt an {@link EncryptedID} element and replace it with the decrypted {@link NameID}
 * in situ.
 * 
 * <p>All of the built-in SAML message types that may include an {@link EncryptedID} are potentially
 * handled, but the actual message to handle is obtained via strategy function, by default the inbound
 * message.</p> 
 * 
 * <p>The {@link SecurityParametersContext} governing the decryption process is located by a lookup
 * strategy, by default a child of the inbound message context.</p>
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_MSG_CTX}
 * @event {@link EventIds#INVALID_PROFILE_CTX}
 * @event {@link SAMLEventIds#DECRYPT_NAMEID_FAILED}
 */
public class DecryptNameIDs extends AbstractProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(DecryptNameIDs.class);
    
    /** Are decryption failures a fatal condition? */
    private boolean errorFatal;

    /** Strategy used to locate the {@link SecurityParametersContext}. */
    @Nonnull private Function<ProfileRequestContext, SecurityParametersContext> securityParamsLookupStrategy;

    /** Strategy used to locate the SAML message to operate on. */
    @Nonnull private Function<ProfileRequestContext, Object> messageLookupStrategy;
    
    /** Predicate dertermining whether to attempt decryption. */
    @Nonnull private Predicate<Pair<ProfileRequestContext,EncryptedElementType>> decryptionPredicate;
    
    /** The decryption object. */
    @Nullable private Decrypter decrypter;
    
    /** Message to operate on. */
    @Nullable private SAMLObject message;
    
    /** Constructor. */
    public DecryptNameIDs() {
        errorFatal = true;
        securityParamsLookupStrategy = Functions.compose(new ChildContextLookup<>(SecurityParametersContext.class),
                new InboundMessageContextLookup());
        messageLookupStrategy = Functions.compose(new MessageLookup<>(Object.class), new InboundMessageContextLookup());
        decryptionPredicate = Predicates.alwaysTrue();
    }
    
    /**
     * Set whether decryption failure should be treated as an error or ignored.
     * 
     * @param flag  true iff decryption failure should be fatal
     */
    public synchronized void setErrorFatal(final boolean flag) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        errorFatal = flag;
    }
    
    /**
     * Set the strategy used to locate the {@link SecurityParametersContext} associated with a given
     * {@link ProfileRequestContext}.
     * 
     * @param strategy strategy used to locate the {@link SecurityParametersContext} associated with a given
     *            {@link ProfileRequestContext}
     */
    public synchronized void setSecurityParametersContextLookupStrategy(
            @Nonnull final Function<ProfileRequestContext, SecurityParametersContext> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        securityParamsLookupStrategy =
                Constraint.isNotNull(strategy, "SecurityParametersContext lookup strategy cannot be null");
    }
    
    /**
     * Set the strategy used to locate the {@link SAMLObect} to operate on.
     * 
     * @param strategy strategy used to locate the {@link SAMLObject} to operate on
     */
    public synchronized void setMessageLookupStrategy(
            @Nonnull final Function<ProfileRequestContext, Object> strategy) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        messageLookupStrategy = Constraint.isNotNull(strategy, "Message lookup strategy cannot be null");
    }
    
    /**
     * Set the predicate used to determine whether to attempt decryption.
     * 
     * @param predicate predicate to use
     */
    public synchronized void setDecryptionPredicate(
            @Nonnull final Predicate<Pair<ProfileRequestContext,EncryptedElementType>> predicate) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        decryptionPredicate = Constraint.isNotNull(predicate, "Decryption predicate cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) throws ProfileException {
        Object theMessage = messageLookupStrategy.apply(profileRequestContext);
        if (theMessage == null) {
            log.debug("{} No message was returned by lookup strategy", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        } else if (!(theMessage instanceof SAMLObject)) {
            log.debug("{} Message was not a SAML construct, nothing to do", getLogPrefix());
            return false;
        }
        
        message = (SAMLObject) theMessage;
        
        final SecurityParametersContext paramsCtx = securityParamsLookupStrategy.apply(profileRequestContext);
        if (paramsCtx == null || paramsCtx.getDecryptionParameters() == null) {
            log.debug("{} No security parameter context or decryption parameters", getLogPrefix());
        } else {
            final DecryptionParameters params = paramsCtx.getDecryptionParameters();
            decrypter = new Decrypter(params.getDataKeyInfoCredentialResolver(),
                    params.getKEKKeyInfoCredentialResolver(), params.getEncryptedKeyResolver());
        }
        
        return super.doPreExecute(profileRequestContext);
    }
    
// Checkstyle: CyclomaticComplexity OFF
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) throws ProfileException {
        
        try {
            if (message instanceof AuthnRequest) {
                processSubject(profileRequestContext, ((AuthnRequest) message).getSubject());
            } else if (message instanceof SubjectQuery) {
                processSubject(profileRequestContext, ((SubjectQuery) message).getSubject());
            } else if (message instanceof Response) {
                for (final Assertion a : ((Response) message).getAssertions()) {
                    processAssertion(profileRequestContext, a);
                }
            } else if (message instanceof LogoutRequest) {
                processLogoutRequest(profileRequestContext, (LogoutRequest) message);
            } else if (message instanceof ManageNameIDRequest) {
                processManageNameIDRequest(profileRequestContext, (ManageNameIDRequest) message);
            } else if (message instanceof NameIDMappingRequest) {
                processNameIDMappingRequest(profileRequestContext, (NameIDMappingRequest) message);
            } else if (message instanceof NameIDMappingResponse) {
                processNameIDMappingResponse(profileRequestContext, (NameIDMappingResponse) message);
            } else if (message instanceof Assertion) {
                processAssertion(profileRequestContext, (Assertion) message);
            } else {
                log.debug("{} Message was of unrecognized type {}, nothing to do", getLogPrefix(),
                        message.getClass().getName());
                return;
            }
        } catch (final DecryptionException e) {
            log.warn(getLogPrefix() + "Failure performing decryption", e);
            if (errorFatal) {
                ActionSupport.buildEvent(profileRequestContext, SAMLEventIds.DECRYPT_NAMEID_FAILED);
            }
        }
        
    }
// Checkstyle: CyclomaticComplexity ON
    
    /**
     * Decrypt an {@link EncryptedID} and return the result.
     * 
     * @param profileRequestContext current profile request context
     * @param encID the encrypted object
     * 
     * @return the decrypted name, or null if the object did not need decryption
     * @throws DecryptionException if an error occurs during decryption
     */
    @Nullable private NameID processEncryptedID(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final EncryptedID encID) throws DecryptionException {
        
        if (!decryptionPredicate.apply(
                new Pair<ProfileRequestContext,EncryptedElementType>(profileRequestContext, encID))) {
            return null;
        }
        
        if (decrypter == null) {
            throw new DecryptionException("No decryption parameters, unable to decrypt EncryptedID");
        }
        
        final SAMLObject object = decrypter.decrypt(encID);
        if (object instanceof NameID) {
            return (NameID) object;
        }
        throw new DecryptionException("Decrypted EncryptedID was not a NameID, was a "
                + object.getElementQName().toString());
    }

    /**
     * Decrypt a {@link NewEncryptedID} and return the result.
     * 
     * @param profileRequestContext current profile request context
     * @param encID the encrypted object
     * 
     * @return the decrypted name, or null if the object did not need decryption
     * @throws DecryptionException if an error occurs during decryption
     */
    @Nullable private NewID processNewEncryptedID(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final NewEncryptedID encID) throws DecryptionException {
        
        if (!decryptionPredicate.apply(
                new Pair<ProfileRequestContext,EncryptedElementType>(profileRequestContext, encID))) {
            return null;
        }

        if (decrypter == null) {
            throw new DecryptionException("No decryption parameters, unable to decrypt NewEncryptedID");
        }

        return decrypter.decrypt(encID);
    }
    
// Checkstyle: CyclomaticComplexity OFF
    /**
     * Decrypt any {@link EncryptedID} found in a subject and replace it with the result.
     * 
     * @param profileRequestContext current profile request context
     * @param subject   subject to operate on
     * 
     * @throws DecryptionException if an error occurs
     */
    private void processSubject(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nullable final Subject subject) throws DecryptionException {
        
        if (subject != null) {
            if (subject.getEncryptedID() != null) {
                log.debug("{} Decrypting EncryptedID in Subject", getLogPrefix());
                try {
                    final NameID decrypted = processEncryptedID(profileRequestContext, subject.getEncryptedID());
                    if (decrypted != null) {
                        subject.setNameID(decrypted);
                        subject.setEncryptedID(null);
                    }
                } catch (final DecryptionException e) {
                    if (errorFatal) {
                        throw e;
                    }
                    log.warn(getLogPrefix() + "Trapped failure decrypting EncryptedID in Subject", e);
                }
            }
            
            for (final SubjectConfirmation sc : subject.getSubjectConfirmations()) {
                if (sc.getEncryptedID() != null) {
                    log.debug("{} Decrypting EncryptedID in SubjectConfirmation", getLogPrefix());
                    try {
                        final NameID decrypted = processEncryptedID(profileRequestContext, subject.getEncryptedID());
                        if (decrypted != null) {
                            sc.setNameID(decrypted);
                            sc.setEncryptedID(null);
                        }
                    } catch (final DecryptionException e) {
                        if (errorFatal) {
                            throw e;
                        }
                        log.warn(getLogPrefix() + "Trapped failure decrypting EncryptedID in SubjectConfirmation", e);
                    }
                }
            }
        }
    }
// Checkstyle: CyclomaticComplexity ON

    /**
     * Decrypt any {@link EncryptedID} found in a LogoutRequest and replace it with the result.
     * 
     * @param profileRequestContext current profile request context
     * @param request   request to operate on
     * 
     * @throws DecryptionException if an error occurs
     */
    private void processLogoutRequest(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final LogoutRequest request) throws DecryptionException {
        
        if (request.getEncryptedID() != null) {
            log.debug("{} Decrypting EncryptedID in LogoutRequest", getLogPrefix());
            final NameID decrypted = processEncryptedID(profileRequestContext, request.getEncryptedID());
            if (decrypted != null) {
                request.setNameID(decrypted);
                request.setEncryptedID(null);
            }
        }
    }

    /**
     * Decrypt any {@link EncryptedID} found in a ManageNameIDRequest and replace it with the result.
     * 
     * @param profileRequestContext current profile request context
     * @param request   request to operate on
     * 
     * @throws DecryptionException if an error occurs
     */
    private void processManageNameIDRequest(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final ManageNameIDRequest request) throws DecryptionException {
        
        if (request.getEncryptedID() != null) {
            log.debug("{} Decrypting EncryptedID in ManageNameIDRequest", getLogPrefix());
            final NameID decrypted = processEncryptedID(profileRequestContext, request.getEncryptedID());
            if (decrypted != null) {
                request.setNameID(decrypted);
                request.setEncryptedID(null);
            }
        }
        
        if (request.getNewEncryptedID() != null) {
            log.debug("{} Decrypting NewEncryptedID in ManageNameIDRequest", getLogPrefix());
            final NewID decrypted = processNewEncryptedID(profileRequestContext, request.getNewEncryptedID());
            if (decrypted != null) {
                request.setNewID(decrypted);
                request.setNewEncryptedID(null);
            }
        }
    }

    /**
     * Decrypt any {@link EncryptedID} found in a NameIDMappingRequest and replace it with the result.
     * 
     * @param profileRequestContext current profile request context
     * @param request   request to operate on
     * 
     * @throws DecryptionException if an error occurs
     */
    private void processNameIDMappingRequest(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final NameIDMappingRequest request) throws DecryptionException {
        
        if (request.getEncryptedID() != null) {
            log.debug("{} Decrypting EncryptedID in NameIDMappingRequest", getLogPrefix());
            final NameID decrypted = processEncryptedID(profileRequestContext, request.getEncryptedID());
            if (decrypted != null) {
                request.setNameID(decrypted);
                request.setEncryptedID(null);
            }
        }
    }

    /**
     * Decrypt any {@link EncryptedID} found in a NameIDMappingResponse and replace it with the result.
     * 
     * @param profileRequestContext current profile request context
     * @param response   response to operate on
     * 
     * @throws DecryptionException if an error occurs 
     */
    private void processNameIDMappingResponse(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final NameIDMappingResponse response) throws DecryptionException {
        
        if (response.getEncryptedID() != null) {
            log.debug("{} Decrypting EncryptedID in NameIDMappingRequest", getLogPrefix());
            final NameID decrypted = processEncryptedID(profileRequestContext, response.getEncryptedID());
            if (decrypted != null) {
                response.setNameID(decrypted);
                response.setEncryptedID(null);
            }
        }
    }
    
// Checkstyle: CyclomaticComplexity OFF
    /**
     * Decrypt any {@link EncryptedID} found in an assertion and replace it with the result.
     * 
     * @param profileRequestContext current profile request context
     * @param assertion   assertion to operate on
     * 
     * @throws DecryptionException if an error occurs
     */
    private void processAssertion(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final Assertion assertion) throws DecryptionException {

        try {
            processSubject(profileRequestContext, assertion.getSubject());
        } catch (final DecryptionException e) {
            if (errorFatal) {
                throw e;
            }
            log.warn(getLogPrefix() + "Trapped failure decrypting EncryptedIDs in Subjectn", e);
        }
            
        
        if (assertion.getConditions() != null) {
            for (final Condition c : assertion.getConditions().getConditions()) {
                if (!(c instanceof DelegationRestrictionType)) {
                    continue;
                }
                for (final Delegate d : ((DelegationRestrictionType) c).getDelegates()) {
                    if (d.getEncryptedID() != null) {
                        log.debug("{} Decrypting EncryptedID in Delegate", getLogPrefix());
                        try {
                            final NameID decrypted = processEncryptedID(profileRequestContext, d.getEncryptedID());
                            if (decrypted != null) {
                                d.setNameID(decrypted);
                                d.setEncryptedID(null);
                            }
                        } catch (final DecryptionException e) {
                            if (errorFatal) {
                                throw e;
                            }
                            log.warn(getLogPrefix() + "Trapped failure decrypting EncryptedID in Delegate", e);
                        }
                    }
                }
            }
        }
    }
// Checkstyle: CyclomaticComplexity OFF
    
}